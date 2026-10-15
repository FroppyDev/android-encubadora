package com.fic.encubadorafic

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.platform.app.InstrumentationRegistry
import com.fic.encubadorafic.data.repository.FakeProjectsRepository
import com.fic.encubadorafic.ui.projects.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File

class ProjectDetailScreenTest {
    @get:Rule val compose = createComposeRule()
    private fun detail(id: Long = 1) = runBlocking { requireNotNull(FakeProjectsRepository().obtenerDetalle(id)) }

    @Test fun selectedProjectSupportsDemoInteractionAndShowsUpdates() {
        val state = mutableStateOf(ProjectDetailState(loading = false, detail = detail()))
        compose.setContent { MaterialTheme {
            ProjectDetailScreen(state.value, 999, {}, {}, onSupport = {
                val d = state.value.detail!!
                state.value = state.value.copy(detail = d.copy(supported = !d.supported, supports = d.supports + if (d.supported) -1 else 1))
            }, onCollaborate = {
                state.value = state.value.copy(detail = state.value.detail!!.copy(collaborationPending = true))
            })
        } }
        compose.onNodeWithText("FIC Conecta").assertExists()
        val output = File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null), "project-detail.png")
        output.outputStream().use { compose.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        compose.onNodeWithContentDescription("Apoyar proyecto").performClick()
        assertTrue(state.value.detail!!.supported)
        compose.onNodeWithContentDescription("Quitar apoyo").performClick()
        assertFalse(state.value.detail!!.supported)
        compose.onNodeWithText("Quiero colaborar").performClick()
        compose.onNodeWithText("Probar solicitud").performClick()
        compose.onNodeWithText("Solicitud pendiente").assertIsNotEnabled()
        compose.onNodeWithText("Avances (2)").performScrollTo().performClick()
        compose.onNodeWithText("Primer prototipo").performScrollTo().assertIsDisplayed()
    }

    @Test fun ownerCannotRequestCollaborationAndEmptyUpdatesAreExplained() {
        val d = detail(3)
        compose.setContent { MaterialTheme {
            ProjectDetailScreen(ProjectDetailState(false, d), d.project.user_id, {}, {}, {}, {})
        } }
        compose.onNodeWithText("Este es tu proyecto").assertExists()
        compose.onNodeWithText("Quiero colaborar").assertDoesNotExist()
        compose.onNodeWithText("Avances (0)").performScrollTo().performClick()
        compose.onNodeWithText("Aún no hay avances").performScrollTo().assertIsDisplayed()
    }

    @Test fun missingProjectOffersRetryWithoutInteractionButtons() {
        var retried = false
        compose.setContent { MaterialTheme {
            ProjectDetailScreen(ProjectDetailState(false, error = "Este proyecto ya no está disponible."), 999, {}, { retried = true }, {}, {})
        } }
        compose.onNodeWithText("Reintentar").performClick()
        assertTrue(retried)
        compose.onNodeWithText("Quiero colaborar").assertDoesNotExist()
    }
}
