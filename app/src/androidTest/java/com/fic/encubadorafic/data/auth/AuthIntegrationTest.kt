package com.fic.encubadorafic.data.auth

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.json.JSONObject
import java.net.ServerSocket
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class AuthIntegrationTest {
    private val account = """{"id":7,"name":"Student","email":"student@info.uas.edu.mx","email_verified_at":null}"""

    private fun exchange(status: Int, response: String, action: (HttpAuthService) -> Unit): String {
        val socket = ServerSocket(0)
        val executor = Executors.newSingleThreadExecutor()
        val request = executor.submit<String> {
            socket.accept().use { client ->
                client.soTimeout = 5000
                val input = client.getInputStream()
                val header = StringBuilder()
                while (!header.endsWith("\r\n\r\n")) {
                    val value = input.read()
                    check(value >= 0)
                    header.append(value.toChar())
                }
                val length = Regex("Content-Length: (\\d+)", RegexOption.IGNORE_CASE).find(header)?.groupValues?.get(1)?.toInt() ?: 0
                val body = ByteArray(length)
                var offset = 0
                while (offset < length) { val read = input.read(body, offset, length - offset); check(read > 0); offset += read }
                val bytes = response.toByteArray(Charsets.UTF_8)
                client.getOutputStream().apply {
                    write("HTTP/1.1 $status Test\r\nContent-Type: application/json\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n".toByteArray())
                    write(bytes); flush()
                }
                header.toString() + String(body, Charsets.UTF_8)
            }
        }
        try {
            action(HttpAuthService("http://127.0.0.1:${socket.localPort}/api/v1/", true))
            return request.get(10, TimeUnit.SECONDS)
        } finally { socket.close(); executor.shutdownNow() }
    }

    @Test fun loginUsesLaravelContractAndParsesTokenAndVerification() {
        val captured = exchange(200, """{"data":$account,"token":"test-token","token_type":"Bearer"}""") {
            val result = it.login("student@info.uas.edu.mx", "Password123!", "Test device")
            assertEquals("test-token", result.token)
            assertFalse(result.user.verified)
            assertEquals(7L, result.user.id)
        }
        assertTrue(captured.startsWith("POST /api/v1/login"))
        val body = JSONObject(captured.substringAfter("\r\n\r\n"))
        assertEquals("Test device", body.getString("device_name"))
        assertEquals("Password123!", body.getString("password"))
    }

    @Test fun registrationIncludesConfirmationAndAcceptsResponseWithoutToken() {
        val captured = exchange(201, """{"data":$account,"message":"Created"}""") {
            it.register("Student", "student@info.uas.edu.mx", "Password123!", "Password123!")
        }
        assertEquals("Password123!", JSONObject(captured.substringAfter("\r\n\r\n")).getString("password_confirmation"))
    }

    @Test fun validationErrorsRemainAssociatedWithFields() {
        exchange(422, """{"message":"Invalid","errors":{"email":["Correo duplicado"]}}""") {
            try { it.login("student@info.uas.edu.mx", "wrong", "Test"); fail() }
            catch (error: AuthException) { assertEquals(422, error.status); assertEquals("Correo duplicado", error.fields["email"]) }
        }
    }

    @Test fun authenticatedRequestsUseBearerAndLogoutAccepts204() {
        val captured = exchange(204, "") { it.logout("test-token") }
        assertTrue(captured.contains("Authorization: Bearer test-token", ignoreCase = true))
        assertFalse(captured.substringBefore("\r\n").contains("test-token"))
    }

    @Test fun serverErrorsAreNotShownVerbatim() {
        exchange(500, """{"message":"PRIVATE STACK TRACE"}""") {
            try { it.user("test-token"); fail() }
            catch (error: AuthException) { assertEquals(500, error.status); assertFalse(error.message.contains("PRIVATE")) }
        }
    }

    @Test fun productionDisallowsHttpBeforeSendingCredentials() {
        try { HttpAuthService("http://127.0.0.1:8000/api/v1/", false).login("student", "password", "test"); fail() }
        catch (error: AuthException) { assertEquals(0, error.status) }
    }

    @Test fun profilePatchSendsOnlyEditableFieldsAndParsesBiography() {
        val captured = exchange(200, """{"data":{"id":7,"name":"Nuevo nombre","email":"student@info.uas.edu.mx","bio":"Nueva bio","email_verified_at":"2026-10-14"}}""") {
            val user = it.updateProfile("test-token", "Nuevo nombre", "Nueva bio")
            assertEquals("Nueva bio", user.bio)
            assertTrue(user.verified)
        }
        assertTrue(captured.startsWith("PATCH /api/v1/user"))
        assertTrue(captured.contains("Authorization: Bearer test-token", ignoreCase = true))
        val body = JSONObject(captured.substringAfter("\r\n\r\n"))
        assertEquals(setOf("name", "bio"), body.keys().asSequence().toSet())
    }

    @Test fun biographyCanBeClearedWithJsonNull() {
        val captured = exchange(200, """{"data":$account}""") { it.updateProfile("token", "Student", null) }
        assertTrue(JSONObject(captured.substringAfter("\r\n\r\n")).isNull("bio"))
    }

    @Test fun categoryFlagsSupportLegacyAndActiveContract() {
        val captured = exchange(200, """{"data":[{"id":1,"name":"Legacy"},{"id":2,"name":"Hidden","is_active":false},{"id":3,"name":"Visible","is_active":1}],"last_page":2}""") {
            val page = it.categories("token", 2)
            assertEquals(listOf(true, false, true), page.items.map { category -> category.active })
            assertEquals(2, page.lastPage)
        }
        assertTrue(captured.startsWith("GET /api/v1/categories?page=2"))
    }

    @Test fun tokenIsEncryptedRestorableAndBoundToServer() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val namespace = "auth_test_" + java.util.UUID.randomUUID().toString()
        val store = KeystoreTokenStore(context, "https://first.example/api/v1/", namespace)
        try {
            store.write("test-secret-value")
            assertEquals("test-secret-value", KeystoreTokenStore(context, "https://first.example/api/v1/", namespace).read())
            val stored = context.getSharedPreferences(namespace, 0).getString("token", null)
            assertNotNull(stored)
            assertFalse(stored!!.contains("test-secret-value"))
            assertNull(KeystoreTokenStore(context, "https://other.example/api/v1/", namespace).read())
        } finally { store.clear(); context.deleteSharedPreferences(namespace) }
    }
}
