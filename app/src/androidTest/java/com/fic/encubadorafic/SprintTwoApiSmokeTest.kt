package com.fic.encubadorafic

import androidx.test.platform.app.InstrumentationRegistry
import com.fic.encubadorafic.data.auth.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.net.HttpURLConnection
import java.net.URI
import java.util.UUID

/** Opt-in: local Laravel + Mailpit over adb reverse; never touches the app's token store. */
class SprintTwoApiSmokeTest {
    private fun get(url: String, json: Boolean = true): String {
        val connection = URI(url).toURL().openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000
            connection.setRequestProperty("Accept", if (json) "application/json" else "text/html")
            assertEquals(200, connection.responseCode)
            return connection.inputStream.bufferedReader().use { it.readText() }
        } finally { connection.disconnect() }
    }

    @Test fun profilePersistsInLaravelAndCatalogAndRecoveryAreAvailable() {
        assumeTrue(InstrumentationRegistry.getArguments().getString("runSprintSmoke") == "true")
        val service = HttpAuthService("http://127.0.0.1:8000/api/v1/", true)
        val email = "sprint2." + UUID.randomUUID().toString().take(8) + "@info.uas.edu.mx"
        val password = UUID.randomUUID().toString() + "Aa1!"
        service.register("Prueba Sprint 2", email, password, password)
        val session = service.login(email, password, "Sprint 2 test")
        try {
            var id: String? = null
            repeat(20) {
                if (id == null) {
                    val messages = JSONObject(get("http://127.0.0.1:8025/api/v1/messages")).getJSONArray("messages")
                    for (i in 0 until messages.length()) {
                        val message = messages.getJSONObject(i)
                        val to = message.getJSONArray("To")
                        for (j in 0 until to.length()) if (to.getJSONObject(j).getString("Address") == email) id = message.getString("ID")
                    }
                    if (id == null) Thread.sleep(300)
                }
            }
            val body = JSONObject(get("http://127.0.0.1:8025/api/v1/message/" + requireNotNull(id))).getString("Text")
            val link = requireNotNull(Regex("http://localhost:8000/api/v1/email/verify/[^\\s]+").find(body)).value.trimEnd(')')
            get(link)
            assertTrue(service.user(session.token).verified)
            val updated = service.updateProfile(session.token, "Nombre actualizado", "Biografía persistente")
            assertEquals("Nombre actualizado", updated.name)
            val loaded = service.user(session.token)
            assertEquals("Biografía persistente", loaded.bio)
            assertEquals(email, loaded.email)
            service.updateProfile(session.token, "Nombre actualizado", null)
            assertNull(service.user(session.token).bio)
            assertTrue(service.categories(session.token, 1).items.isNotEmpty())
            val recovery = get(BackendLinks.passwordRecovery("http://127.0.0.1:8000/api/v1/", true), false)
            assertTrue(recovery.contains("email"))
        } finally { service.logout(session.token) }
    }
}
