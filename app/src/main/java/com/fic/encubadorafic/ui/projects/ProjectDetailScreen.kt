package com.fic.encubadorafic.ui.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ProjectDetailRoute(
    id: Long,
    userId: Long?,
    onBack: () -> Unit,
    viewModel: ProjectDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(id) { viewModel.load(id) }
    ProjectDetailScreen(
        state,
        userId,
        onBack,
        { viewModel.load(id) },
        viewModel::toggleSupport,
        viewModel::requestCollaboration
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    state: ProjectDetailState, userId: Long?, onBack: () -> Unit,
    onRetry: () -> Unit, onSupport: () -> Unit, onCollaborate: () -> Unit,
) {
    val detail = state.detail
    var tab by rememberSaveable(detail?.project?.id) { mutableIntStateOf(0) }
    var confirm by rememberSaveable(detail?.project?.id) { mutableStateOf(false) }
    val navy = Color(0xFF142D4E)
    val blue = Color(0xFF246BCE)
    Scaffold(containerColor = Color(0xFFF3F6FA), topBar = {
        TopAppBar(
            title = { Text("Proyecto", color = navy, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        "Volver"
                    )
                }
            })
    }, bottomBar = {

        if (detail != null && detail.project.status == "published") Surface(shadowElevation = 8.dp) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = onSupport,
                    enabled = detail.demo,
                    modifier = Modifier.heightIn(min = 52.dp)
                ) {
                    Icon(
                        if (detail.supported) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (detail.supported) "Quitar apoyo" else "Apoyar proyecto"
                    )
                    Spacer(Modifier.width(8.dp)); Text("${detail.supports}")
                }
                if (userId == detail.project.user_id) Text(
                    "Este es tu proyecto",
                    modifier = Modifier.weight(1f)
                )
                else Button(
                    onClick = { confirm = true },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp),
                    enabled = detail.demo && !detail.collaborationPending,
                    colors = ButtonDefaults.buttonColors(containerColor = blue)
                ) {
                    Text(if (detail.collaborationPending) "Solicitud pendiente" else "Quiero colaborar")
                }
            }
        }
    }) { padding ->
        when {
            state.loading -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            detail == null -> Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(state.error ?: "Proyecto no disponible")
                TextButton(onClick = onRetry) { Text("Reintentar") }
            }

            else -> Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                ProjectCover(detail.project.category_id, Modifier.height(210.dp))
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        detail.categoryName.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = blue
                    )
                    Text(
                        detail.project.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = navy
                    )
                    Text(
                        detail.project.summary,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF556579)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            Modifier
                                .size(44.dp)
                                .background(Color(0xFFE0E9F4), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                detail.authorName.split(" ").filter { it.isNotBlank() }.take(2)
                                    .joinToString("") { it.take(1) },
                                color = navy,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(
                                "Creado por",
                                style = MaterialTheme.typography.labelSmall
                            ); Text(detail.authorName, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    if (detail.project.status != "published") Text(
                        if (detail.project.status == "draft") "Borrador · Solo visible para su autor" else "Proyecto archivado",
                        color = navy
                    )
                    SecondaryTabRow(selectedTabIndex = tab) {
                        Tab(
                            selected = tab == 0,
                            onClick = { tab = 0 },
                            text = { Text("Información") })
                        Tab(
                            selected = tab == 1,
                            onClick = { tab = 1 },
                            text = { Text("Avances (${detail.updates.size})") })
                    }
                    if (tab == 0) {
                        Section("Acerca del proyecto") {
                            Text(
                                detail.project.description,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        if (detail.technologies.isNotEmpty()) Section("Tecnologías") {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                detail.technologies.forEach { technology ->
                                    Surface(
                                        color = Color(0xFFF0F4FA),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            technology,
                                            Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                        Section("Información del proyecto") {
                            Text("Publicado: ${detail.project.created_at.take(10)}")
                            Text("Última actualización: ${detail.project.updated_at.take(10)}")
                            Text("Categoría: ${detail.categoryName}")
                        }
                    } else {
                        if (detail.updates.isEmpty()) Section("Aún no hay avances") { Text("El creador compartirá aquí las novedades del proyecto.") }
                        detail.updates.forEach { update ->
                            Section(update.title) {
                                Text(
                                    update.date,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = blue
                                )
                                Text(update.body, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
    if (confirm) AlertDialog(
        onDismissRequest = { confirm = false },
        title = { Text("Colaborar en este proyecto") },
        text = { Text("Esta demostración marcará una solicitud como pendiente en la app. No se enviará al creador.") },
        confirmButton = {
            TextButton(onClick = {
                confirm = false; onCollaborate()
            }) { Text("Probar solicitud") }
        },
        dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancelar") } })
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF142D4E)
            )
            content()
        }
    }
}
