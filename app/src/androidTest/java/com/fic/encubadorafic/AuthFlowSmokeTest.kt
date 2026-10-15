package com.fic.encubadorafic

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection
import java.net.URI
import java.util.UUID

/** Opt-in local smoke test. Requires app logged out, adb reverse 8000/8025, and Mailpit. */
@RunWith(AndroidJUnit4::class)
class AuthFlowSmokeTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun waitFor(text: String, substring: Boolean = false) {
        compose.waitUntil(30_000) { compose.onAllNodesWithText(text, substring = substring).fetchSemanticsNodes().isNotEmpty() }
    }
    private fun get(url: String): String {
        val connection = URI(url).toURL().openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 10_000; connection.readTimeout = 10_000
            connection.setRequestProperty("Accept", "application/json")
            assertEquals(200, connection.responseCode)
            return connection.inputStream.bufferedReader().use { it.readText() }
        } finally { connection.disconnect() }
    }

    @Test fun registerVerifyLoginAndLogoutThroughScreens() {
        assumeTrue(InstrumentationRegistry.getArguments().getString("runAuthSmoke") == "true")
        val email = "android." + UUID.randomUUID().toString().take(8) + "@info.uas.edu.mx"
        val password = UUID.randomUUID().toString() + "Aa1!"
        compose.waitUntil(30_000) {
            compose.onAllNodesWithText("Regístrate").fetchSemanticsNodes().isNotEmpty() ||
                compose.onAllNodesWithText("Verifica tu correo").fetchSemanticsNodes().isNotEmpty()
        }
        if (compose.onAllNodesWithText("Verifica tu correo").fetchSemanticsNodes().isNotEmpty()) {
            // A previous interrupted smoke run may have left its synthetic session.
            // Do not clear or log out an unrelated user's account.
            compose.onNodeWithText("Hola, Prueba Android.", substring = true).assertExists()
            compose.onNodeWithText("Cerrar sesión").performScrollTo().performClick()
        }
        waitFor("Regístrate")
        compose.onNodeWithText("Regístrate").performScrollTo().performClick()
        val inputs = compose.onAllNodes(hasSetTextAction())
        inputs[0].performTextInput("Prueba Android")
        inputs[1].performTextInput(email)
        inputs[2].performTextInput(password)
        inputs[3].performTextInput(password)
        compose.onAllNodesWithText("Crear cuenta").filter(hasClickAction())[0].performScrollTo().performClick()
        waitFor("Cuenta creada.", substring = true)
        val login = compose.onAllNodes(hasSetTextAction())
        login[0].performTextInput(email)
        login[1].performTextInput(password)
        compose.onNodeWithText("Iniciar sesión").performScrollTo().performClick()
        waitFor("Verifica tu correo")

        var messageId: String? = null
        repeat(10) {
            if (messageId == null) {
                val messages = JSONObject(get("http://127.0.0.1:8025/api/v1/messages")).getJSONArray("messages")
                for (i in 0 until messages.length()) {
                    val message = messages.getJSONObject(i)
                    val recipients = message.getJSONArray("To")
                    for (j in 0 until recipients.length()) if (recipients.getJSONObject(j).getString("Address") == email) messageId = message.getString("ID")
                }
                if (messageId == null) Thread.sleep(300)
            }
        }
        val text = JSONObject(get("http://127.0.0.1:8025/api/v1/message/" + requireNotNull(messageId))).getString("Text")
        val verification = requireNotNull(Regex("http://localhost:8000/api/v1/email/verify/[^\\s]+").find(text)).value.trimEnd(')')
        get(verification)
        compose.onNodeWithText("Ya verifiqué mi correo").performScrollTo().performClick()
        waitFor("Explorar")
        compose.onAllNodesWithContentDescription("Localized description")[0].performClick()
        waitFor("Prueba Android")
        compose.onNodeWithText("Mi perfil").performClick()
        waitFor("Biografía")
        compose.waitUntil(30_000) { compose.onAllNodes(hasSetTextAction() and isEnabled()).fetchSemanticsNodes().size == 2 }
        compose.onAllNodes(hasSetTextAction())[0].performTextReplacement("Prueba Android Editada")
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("Biografía de prueba sprint 2")
        compose.onNodeWithText("Guardar cambios").performScrollTo().performClick()
        waitFor("Perfil guardado correctamente.")
        compose.onNodeWithText("Volver").performScrollTo().performClick()
        waitFor("Explorar")
        compose.onAllNodesWithContentDescription("Localized description")[0].performClick()
        waitFor("Prueba Android Editada")
        compose.onNodeWithText("Mi perfil").performClick()
        waitFor("Biografía de prueba sprint 2")
        compose.waitUntil(30_000) { compose.onAllNodes(hasText("Volver") and isEnabled()).fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Volver").performScrollTo().performClick()
        waitFor("Explorar")
        compose.onAllNodesWithContentDescription("Localized description")[0].performClick()
        compose.onNodeWithText("Categorías").performClick()
        waitFor("Actualizar categorías")
        compose.waitUntil(30_000) { compose.onAllNodes(hasText("Actualizar categorías") and isEnabled()).fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Volver").performClick()
        waitFor("Explorar")
        compose.onAllNodesWithContentDescription("Localized description")[0].performClick()
        compose.onNodeWithText("Cerrar sesión").performClick()
        waitFor("Regístrate")
        compose.onNodeWithText("Olvidé mi contraseña").performScrollTo().performClick()
        waitFor("Abrir formulario de recuperación")
        compose.onNodeWithText("Volver al login").performClick()
        waitFor("Regístrate")
        compose.onNodeWithText("Iniciar sesión").assertIsDisplayed()
    }
}
