package com.fic.encubadorafic
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.platform.app.InstrumentationRegistry
import com.fic.encubadorafic.model.NewProject
import com.fic.encubadorafic.ui.projects.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File

class CreateProjectScreenTest {
    @get:Rule val compose = createComposeRule()
    @Test fun emptyFormShowsErrorsWithoutSaving() {
        var saved = false
        compose.setContent { MaterialTheme { CreateProjectScreen(CreateProjectState(), {}, { _, _ -> saved = true }) } }
        compose.onNodeWithText("Guardar borrador").performScrollTo().performClick()
        compose.onNodeWithText("Revisa los campos indicados antes de continuar.").assertExists()
        assertFalse(saved)
    }
    @Test fun validFormConfirmsPublishAndDeliversSelectedFields() {
        var received: NewProject? = null
        var published = false
        compose.setContent { MaterialTheme { CreateProjectScreen(CreateProjectState(), {}, { data, status -> received = data; published = status }) } }
        compose.onNodeWithText("Título").performScrollTo().performTextInput("Huerto estudiantil")
        compose.onNodeWithText("Resumen").performScrollTo().performTextInput("Sensores para ahorrar agua")
        compose.onNodeWithText("Categoría").performScrollTo().performClick()
        compose.onNodeWithText("Sustentabilidad").performClick()
        compose.onNodeWithText("Descripción").performScrollTo().performTextInput("Mediremos la humedad del suelo para mejorar el riego del campus.")
        compose.onNodeWithText("Arduino").performScrollTo().performClick()
        compose.onNodeWithText("Publicar proyecto").performScrollTo().performClick()
        assertNull(received)
        compose.onNodeWithText("Confirmar publicación").performClick()
        assertTrue(published)
        assertEquals(2L, received!!.categoryId)
        assertEquals(listOf(4L), received!!.technologyIds)
        compose.onNodeWithText("Dale forma a tu idea").performScrollTo()
        val output = File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null), "create-project.png")
        output.outputStream().use { compose.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
    }
    @Test fun leavingDirtyFormRequiresConfirmation() {
        var left = false
        compose.setContent { MaterialTheme { CreateProjectScreen(CreateProjectState(), { left = true }, { _, _ -> }) } }
        compose.onNodeWithText("Título").performScrollTo().performTextInput("Idea nueva")
        compose.onNodeWithContentDescription("Volver").performClick()
        compose.onNodeWithText("¿Descartar este proyecto?").assertExists()
        assertFalse(left)
        compose.onNodeWithText("Descartar").performClick()
        assertTrue(left)
    }
}
