package com.fic.encubadorafic.ui.sessions

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fic.encubadorafic.BuildConfig
import com.fic.encubadorafic.data.auth.BackendLinks

@Composable
fun RecoveryScreen(onBack: () -> Unit) {

    val context = LocalContext.current
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        TextButton(onClick = onBack) { Text("Volver al login") }

        Text("Recuperar contraseña", style = MaterialTheme.typography.headlineMedium)
        Text("Abre el formulario, escribe tu correo institucional y sigue el enlace que recibirás. Después vuelve a la app e inicia sesión con tu nueva contraseña.")

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(onClick = {
            error = null
            try {
                val url = BackendLinks.passwordRecovery(BuildConfig.API_BASE_URL, BuildConfig.DEBUG)
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: IllegalArgumentException) {
                error = e.message
            } catch (_: android.content.ActivityNotFoundException) {
                error = "No se encontró un navegador. Instala uno para abrir el formulario."
            }

        }) { Text("Abrir formulario de recuperación") }
    }
}
