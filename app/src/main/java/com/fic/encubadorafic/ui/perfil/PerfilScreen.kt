package com.fic.encubadorafic.ui.perfil

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fic.encubadorafic.ui.sessions.AuthUiState

private val ProfileNavy = Color(0xFF142D4E)
private val ProfileBlue = Color(0xFF246BCE)
private val ProfileMuted = Color(0xFF64748B)

@Composable
fun PerfilScreen(
    state: AuthUiState,
    onSave: (String, String) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onUploadImage: (android.net.Uri, String) -> Unit = { _, _ -> },
    projectsContent: @Composable () -> Unit = {},
) {
    val user = state.user ?: return
    val avatarPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { onUploadImage(it, "avatar") }
        }
    val coverPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { onUploadImage(it, "cover") }
        }
    var name by rememberSaveable(user.id, user.name) { mutableStateOf(user.name) }
    var bio by rememberSaveable(user.id, user.bio) { mutableStateOf(user.bio.orEmpty()) }
    var confirmExit by remember { mutableStateOf(false) }
    val changed = name != user.name || bio != user.bio.orEmpty()
    val leave = { if (changed) confirmExit = true else onBack() }

    BackHandler(enabled = changed && !state.busy) { confirmExit = true }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6FA))
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = leave, enabled = !state.busy) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = ProfileNavy
                )
                Spacer(Modifier.width(6.dp))
                Text("Volver", color = ProfileNavy)
            }
            Spacer(Modifier.weight(1f))
            Text(
                "Mi perfil", color = ProfileNavy, style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(end = 12.dp)
            )
        }

        Column(
            Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Surface(shape = RoundedCornerShape(24.dp), color = Color.White) {
                Column {

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(168.dp)
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        ProfileNavy,
                                        Color(0xFF367DC8),
                                        Color(0xFF7EAFD3)
                                    )
                                )
                            )
                    ) {
                        Box(
                            Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 30.dp, y = (-50).dp)
                                .size(200.dp)
                                .background(Color.White.copy(alpha = .09f), CircleShape)
                        )
                        Box(
                            Modifier
                                .align(Alignment.BottomStart)
                                .offset(x = (-35).dp, y = 65.dp)
                                .size(190.dp)
                                .background(Color.White.copy(alpha = .07f), CircleShape)
                        )

                        RemoteImage(
                            user.coverImageUrl,
                            "Portada",
                            enabled = false,
                            onClick = { },
                            modifier = Modifier.matchParentSize()
                        )
                        Surface(
                            Modifier
                                .clickable(enabled = !state.busy) {
                                    coverPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .align(Alignment.BottomEnd)
                                .padding(12.dp),
                            color = Color.White.copy(alpha = .16f), shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.PhotoCamera,
                                    null,
                                    Modifier.size(16.dp),
                                    tint = Color.White
                                )
                                Text(
                                    "Cambiar portada",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }

                    Column(
                        Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 22.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                Modifier
                                    .offset(y = (-20).dp)
                                    .size(88.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE6EFFA))
                                    .border(5.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                RemoteImage(
                                    user.avatarUrl,
                                    "Foto de perfil",
                                    Modifier.matchParentSize(),
                                    enabled = !state.busy,
                                    onClick = {
                                        avatarPicker.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                )
                            }
                        }

                        Text(
                            "Perfil: hasta 2 MB‚ Portada: hasta 5 MB",
                            style = MaterialTheme.typography.bodySmall
                        )

                        if (state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                        state.fields["avatar"]?.let {
                            Text(
                                it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        state.fields["cover"]?.let {
                            Text(
                                it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Text(
                            user.name,
                            color = ProfileNavy,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            user.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ProfileMuted
                        )
                        HorizontalDivider(color = Color(0xFFEDF1F6))
                        Text(user.bio?.takeIf { it.isNotBlank() }
                            ?: "Comparte tus intereses y lo que te gustaria construir con la comunidad.",
                            color = ProfileMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            projectsContent()

            Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "Editar informacion",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ProfileNavy
                    )
                    Text(
                        "Tu nombre y biografia ayudan a que otros estudiantes te conozcan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ProfileMuted
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !state.busy,
                        shape = RoundedCornerShape(12.dp),
                        isError = state.fields.containsKey("name"),
                        supportingText = { Text(state.fields["name"] ?: "${name.length}/100") })
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Biografia") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 8,
                        enabled = !state.busy,
                        shape = RoundedCornerShape(12.dp),
                        isError = state.fields.containsKey("bio"),
                        supportingText = { Text(state.fields["bio"] ?: "${bio.length}/2000") })
                    state.message?.let { Text(it, color = ProfileBlue) }

                    Button(
                        onClick = { onSave(name, bio) },
                        enabled = changed && !state.busy,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ProfileBlue)
                    ) { Text("Guardar cambios") }

                    if (state.error != null) TextButton(
                        onClick = onRetry,
                        enabled = !state.busy && !changed
                    ) { Text("Volver a cargar perfil") }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
    if (confirmExit) AlertDialog(
        onDismissRequest = { confirmExit = false },
        title = { Text("¿Quieres descartar los cambios?") },
        text = { Text("Los cambios que no guardaste se perderan.") },
        confirmButton = {
            TextButton(onClick = {
                confirmExit = false; onBack()
            }) { Text("Descartar") }
        },
        dismissButton = {
            TextButton(onClick = {
                confirmExit = false
            }) { Text("Seguir editando") }
        })
}
