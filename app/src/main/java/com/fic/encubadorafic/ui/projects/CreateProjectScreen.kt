package com.fic.encubadorafic.ui.projects

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.fic.encubadorafic.model.*

@Composable
fun CreateProjectRoute(
    authorId: Long, authorName: String, onBack: () -> Unit, onCreated: (Long) -> Unit,
    categories: List<ProjectOption>,
    categoriesLoading: Boolean,
    categoriesError: String?,
    onReloadCategories: () -> Unit,
    technologyOptions: List<ProjectOption> = emptyList(),
    vm: CreateProjectViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(state.savedId) {
        state.savedId?.let { id -> onCreated(id); vm.consumeSaved() }
    }
    CreateProjectScreen(
        state,
        onBack,
        categories = categories, categoriesLoading = categoriesLoading, technologyOptions = technologyOptions,
        categoriesError = categoriesError, onReloadCategories = onReloadCategories,
        onSave = { input, published -> vm.save(input, published, authorId, authorName) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    state: CreateProjectState,
    onBack: () -> Unit,
    onSave: (NewProject, Boolean) -> Unit,
    categories: List<ProjectOption> = emptyList(),
    categoriesLoading: Boolean = false,
    categoriesError: String? = null,
    onReloadCategories: () -> Unit = {},
    technologyOptions: List<ProjectOption> = emptyList(),
) {
    var title by rememberSaveable { mutableStateOf("") }
    var summary by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var categoryId by rememberSaveable { mutableStateOf<Long?>(null) }
    var technologies by rememberSaveable { mutableStateOf(arrayListOf<Long>()) }
    var submitted by rememberSaveable { mutableStateOf(false) }
    var discard by remember { mutableStateOf(false) }
    var publishConfirmation by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val selectedCategory = categories.firstOrNull { it.id == categoryId }
    val input = NewProject(title, summary, description, selectedCategory?.id, technologies.filter { id -> technologyOptions.any { it.id == id } }, selectedCategory?.name.orEmpty())
    val catalogReady = !categoriesLoading && categoriesError == null && categories.isNotEmpty()
    val errors = if (submitted) input.errors() else emptyMap()
    val dirty = title.isNotEmpty() || summary.isNotEmpty() || description.isNotEmpty() || categoryId != null || technologies.isNotEmpty()
    val navy = Color(0xFF142D4E)
    val blue = Color(0xFF246BCE)
    val leave = { if (dirty) discard = true else onBack() }

    BackHandler(enabled = dirty || state.busy) { if (!state.busy) discard = true }

    Scaffold(containerColor = Color(0xFFF3F6FA), topBar = {
        TopAppBar(
            title = { Text("Crear proyecto", color = navy, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(
                    onClick = leave,
                    enabled = !state.busy
                ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
            })
    }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Dale forma a tu idea",
                style = MaterialTheme.typography.headlineMedium,
                color = navy,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Cuéntale a la comunidad qué quieres construir y cómo puede participar.",
                color = Color(0xFF556579)
            )

            ProjectCover(
                categoryId ?: 1L, Modifier
                    .height(150.dp)
                    .clip(RoundedCornerShape(18.dp))
            )

            Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "Información general",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = navy
                    )
                    OutlinedTextField(
                        title,
                        { title = it },
                        label = { Text("Título") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.busy,
                        isError = "title" in errors,
                        supportingText = { Text(errors["title"] ?: "${title.length}/180") })

                    OutlinedTextField(
                        summary,
                        { summary = it },
                        label = { Text("Resumen") },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.busy,
                        isError = "summary" in errors,
                        supportingText = {
                            Text(
                                errors["summary"]
                                    ?: "${summary.length}/500 · Una explicación breve de tu idea"
                            )
                        })
                    if (categoriesLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
                    if (categoriesError != null) Text(categoriesError, color = MaterialTheme.colorScheme.error)
                    if (!categoriesLoading && categoriesError == null && categories.isEmpty()) Text("No hay categorías activas disponibles.")
                    TextButton(onClick = onReloadCategories, enabled = !state.busy && !categoriesLoading) {
                        Text(if (categoriesError != null) "Reintentar categorías" else "Actualizar categorías")
                    }
                    if (categoryId != null && selectedCategory == null && !categoriesLoading) Text("La categoría seleccionada ya no está disponible. Elige otra.", color = MaterialTheme.colorScheme.error)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { if (!state.busy && catalogReady) expanded = it }) {
                        OutlinedTextField(
                            value = categories.firstOrNull { it.id == categoryId }?.name.orEmpty(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            enabled = !state.busy,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            isError = "category" in errors,
                            supportingText = {
                                Text(
                                    errors["category"]
                                        ?: "Selecciona la categoría que mejor describe tu proyecto"
                                )
                            })
                        ExposedDropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                            categories.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.name) },
                                    onClick = { categoryId = option.id; expanded = false })
                            }
                        }
                    }
                }
            }
            Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "Acerca del proyecto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = navy
                    )

                    Text("Explica el problema, tu propuesta y el tipo de colaboración que buscas.")

                    OutlinedTextField(
                        description,
                        { description = it },
                        label = { Text("Descripción") },
                        minLines = 5,
                        maxLines = 10,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.busy,
                        isError = "description" in errors,
                        supportingText = {
                            Text(
                                errors["description"] ?: "${description.length}/20000"
                            )
                        })
                    Text("Tecnologías", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Opcional · Puedes elegir varias",
                        style = MaterialTheme.typography.bodySmall
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        technologyOptions.forEach { option ->
                            FilterChip(
                                selected = option.id in technologies,
                                onClick = {
                                    technologies =
                                        ArrayList(if (option.id in technologies) technologies - option.id else technologies + option.id)
                                },
                                enabled = !state.busy, label = { Text(option.name) })
                        }
                    }
                }
            }
            if (errors.isNotEmpty()) Text(
                "Revisa los campos indicados antes de continuar.",
                color = MaterialTheme.colorScheme.error
            )
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            if (state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
            Button(
                onClick = {
                    submitted = true; if (catalogReady && input.errors().isEmpty()) publishConfirmation = true
                },
                enabled = !state.busy, modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = blue)
            ) { Text("Publicar proyecto") }
            OutlinedButton(
                onClick = { submitted = true; if (catalogReady && input.errors().isEmpty()) onSave(input, false) },
                enabled = !state.busy, modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
            ) { Text("Guardar borrador") }
            Text(
                "El borrador necesita los datos básicos y no aparece en Explorar. Al publicar, el proyecto se agrega al catálogo de proyectos.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
    if (publishConfirmation) AlertDialog(
        onDismissRequest = { publishConfirmation = false },
        title = { Text("¿Publicar este proyecto?") },
        text = { Text("El proyecto se guardará en tu cuenta y aparecerá en Explorar.") },
        confirmButton = {
            TextButton(onClick = {
                publishConfirmation = false; onSave(
                input,
                true
            )
            }) { Text("Confirmar publicación") }
        },
        dismissButton = {
            TextButton(onClick = {
                publishConfirmation = false
            }) { Text("Seguir editando") }
        })
    if (discard) AlertDialog(
        onDismissRequest = { discard = false },
        title = { Text("¿Descartar este proyecto?") },
        text = { Text("Los datos que no guardaste se perderán.") },
        confirmButton = {
            TextButton(onClick = {
                discard = false; onBack()
            }) { Text("Descartar") }
        },
        dismissButton = { TextButton(onClick = { discard = false }) { Text("Seguir editando") } })
}
