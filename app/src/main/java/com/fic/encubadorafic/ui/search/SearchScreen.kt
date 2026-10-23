package com.fic.encubadorafic.ui.search

import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.fic.encubadorafic.ui.main.MainViewModel
import com.fic.encubadorafic.ui.main.ProyectItem
import com.fic.encubadorafic.data.auth.Category

@Composable
fun SearchScren(
    onBack: () -> Unit = {},
    onOpen: (Long) -> Unit = {},
    categories: List<Category> = emptyList(),
) {
    val vm: MainViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf<Long?>(null) }
    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { vm.cargarRutas() }
    val results = state.proyectos.filter {
        (category == null || category == it.category_id) &&
                (it.title.contains(query.trim(), true) || it.summary.contains(query.trim(), true))
    }
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
            OutlinedTextField(
                query,
                { query = it },
                Modifier.weight(1f),
                label = { Text("Buscar proyectos") },
                singleLine = true
            )
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            if (state.cargando) LinearProgressIndicator(Modifier.fillMaxWidth())
            else if (state.error != null) {
                Text(state.error.orEmpty(), color = MaterialTheme.colorScheme.error)
                TextButton(onClick = vm::cargarRutas) { Text("Reintentar") }
            } else Text("${results.size} resultados encontrados")

            Spacer(Modifier.weight(1f))

            Box {
                TextButton(
                    modifier = Modifier.width(100.dp).height(40.dp),
                    border = BorderStroke(1.dp, Color.Black),
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                    expanded = true
                }) {
                    Text(
                        overflow = TextOverflow.Ellipsis,
                        text = categories.firstOrNull { it.id == category }?.name ?: "Todas"
                    )
                }
                DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Todas") },
                        onClick = { category = null; expanded = false })
                    categories.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.name) },
                            onClick = { category = item.id; expanded = false })
                    }
                }
            }

        }
        LazyColumn {
            items(results.size, key = { results[it].id }) { index ->
                val project = results[index]
                ProyectItem(
                    name = project.title,
                    description = project.summary,
                    category = categories.firstOrNull { it.id == project.category_id }?.name
                        ?: "Categoría no disponible",
                    categoryId = project.category_id,
                    coverUrl = project.cover_image,
                    onClick = { onOpen(project.id) })
            }
        }
    }
}
