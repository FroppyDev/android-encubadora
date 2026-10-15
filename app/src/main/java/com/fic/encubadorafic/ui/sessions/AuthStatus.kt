package com.fic.encubadorafic.ui.sessions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AuthFeedback(state: AuthUiState) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        state.fields.forEach { (field, message) ->
            val label = when (field) {
                "name" -> "Nombre"; "email" -> "Correo"; "password" -> "Contraseña"; "password_confirmation" -> "Confirmación"; else -> "Datos"
            }
            Text("$label: $message", color = MaterialTheme.colorScheme.error)
        }
        state.message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
    }
}

@Composable
fun VerificationScreen(
    state: AuthUiState,
    onRefresh: () -> Unit,
    onResend: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        Text("Verifica tu correo", style = MaterialTheme.typography.headlineMedium)
        Text("Hola, ${state.user?.name.orEmpty()}. Abre el enlace que enviamos a ${state.user?.email.orEmpty()} y vuelve aquí para continuar.")
        AuthFeedback(state)
        Button(
            onClick = onRefresh,
            enabled = !state.busy,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Ya verifiqué mi correo") }
        OutlinedButton(
            onClick = onResend,
            enabled = !state.busy,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Reenviar enlace") }
        TextButton(onClick = onLogout, enabled = !state.busy) { Text("Cerrar sesión") }
    }
}

@Composable
fun RestoreSessionScreen(state: AuthUiState, onRetry: () -> Unit, onLogout: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text("Comprobando tu sesión", style = MaterialTheme.typography.headlineSmall)
        AuthFeedback(state)
        if (!state.busy && state.error != null) {
            Button(onClick = onRetry) { Text("Reintentar conexión") }
            TextButton(onClick = onLogout) { Text("Cerrar sesión") }
        }
    }
}
