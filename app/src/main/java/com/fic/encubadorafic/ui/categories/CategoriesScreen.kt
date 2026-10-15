package com.fic.encubadorafic.ui.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fic.encubadorafic.ui.sessions.AuthUiState

@Composable
fun CategoriesScreen(state: AuthUiState, onRefresh: () -> Unit, onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack, enabled = !state.busy) { Text("Volver") }
        Text("Categorías", style = MaterialTheme.typography.headlineMedium)
        Text("Consulta las categorías disponibles para los proyectos de la comunidad.")
        Button(
            onClick = onRefresh,
            enabled = !state.busy
        ) { Text(if (state.error != null) "Reintentar" else "Actualizar categorías") }
        if (state.categoriesLoaded && state.categories.isEmpty() && !state.busy) Text("No hay categorías disponibles por ahora.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(state.categories, key = { it.id }) { category ->
                Card(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(category.name, style = MaterialTheme.typography.titleMedium)
                        category.description?.takeIf { it.isNotBlank() }?.let { Text(it) }
                    }
                }
            }
        }
    }
}
