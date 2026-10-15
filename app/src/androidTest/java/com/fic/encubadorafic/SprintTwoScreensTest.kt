package com.fic.encubadorafic

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.fic.encubadorafic.data.auth.AuthUser
import com.fic.encubadorafic.ui.perfil.PerfilScreen
import com.fic.encubadorafic.ui.categories.CategoriesScreen
import com.fic.encubadorafic.ui.sessions.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class SprintTwoScreensTest {
    @get:Rule val compose = createComposeRule()

    @Test fun profileSavesEditedFieldsAndShowsConfirmation() {
        val state = mutableStateOf(AuthUiState(phase = AuthPhase.SignedIn,
            user = AuthUser(1, "Ana", "ana@info.uas.edu.mx", true, "Anterior")))
        compose.setContent { MaterialTheme {
            PerfilScreen(state.value, onSave = { name, bio ->
                state.value = state.value.copy(user = state.value.user!!.copy(name = name, bio = bio), message = "Perfil guardado correctamente.")
            }, onRetry = {}, onBack = {})
        } }
        compose.onNodeWithText("Nombre").performScrollTo().performTextReplacement("Ana Nueva")
        compose.onNodeWithText("Biografía").performScrollTo().performTextReplacement("Biografía nueva")
        compose.onNodeWithText("Guardar cambios").performScrollTo().performClick()
        compose.onNodeWithText("Perfil guardado correctamente.").assertExists()
        compose.onNodeWithText("Guardar cambios").assertIsNotEnabled()
        assertEquals("Ana Nueva", state.value.user!!.name)
        assertEquals("Biografía nueva", state.value.user!!.bio)
    }

    @Test fun profileConfirmsDiscardingUnsavedChanges() {
        var left = false
        compose.setContent { MaterialTheme {
            PerfilScreen(AuthUiState(user = AuthUser(1, "Ana", "ana@info.uas.edu.mx", true)),
                onSave = { _, _ -> }, onRetry = {}, onBack = { left = true })
        } }
        compose.onNodeWithText("Nombre").performScrollTo().performTextReplacement("Otro nombre")
        compose.onNodeWithText("Volver").performScrollTo().performClick()
        compose.onNodeWithText("¿Descartar los cambios?").assertExists()
        assertFalse(left)
        compose.onNodeWithText("Seguir editando").performClick()
        compose.onNodeWithText("Otro nombre").assertExists()
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("Descartar").performClick()
        assertTrue(left)
    }

    @Test fun emptyCategoriesCanBeRefreshed() {
        var refreshed = false
        compose.setContent { MaterialTheme {
            CategoriesScreen(AuthUiState(categoriesLoaded = true), onRefresh = { refreshed = true }, onBack = {})
        } }
        compose.onNodeWithText("No hay categorías disponibles por ahora.").assertExists()
        compose.onNodeWithText("Actualizar categorías").performClick()
        assertTrue(refreshed)
    }

    @Test fun recoveryExplainsBrowserFlowAndAllowsReturn() {
        var returned = false
        compose.setContent { MaterialTheme { RecoveryScreen { returned = true } } }
        compose.onNodeWithText("Abrir formulario de recuperación").assertIsDisplayed()
        compose.onNodeWithText("Volver al login").performClick()
        assertTrue(returned)
    }
}
