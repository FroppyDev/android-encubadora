package com.fic.encubadorafic
import com.fic.encubadorafic.data.repository.FakeProjectsRepository
import com.fic.encubadorafic.model.NewProject
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class CreateProjectRepositoryTest {
    private val input = NewProject(" Mi proyecto ", " Resumen ", " Descripción ", 1, listOf(1, 2))
    @Test fun publishedProjectIsAvailableToListAndDetailWithRealDraftFields() = runBlocking {
        val repo = FakeProjectsRepository()
        val result = repo.crearProyecto(input, true, 99, "Autora")
        assertEquals("Mi proyecto", result.project.title)
        assertEquals(99L, result.project.user_id)
        assertEquals("Autora", result.authorName)
        assertEquals(listOf("Kotlin", "Laravel"), result.technologies)
        assertEquals("published", result.project.status)
        assertEquals("public", result.project.visibility)
        assertEquals(result, repo.obtenerDetalle(result.project.id))
        assertTrue(repo.obtenerProjectos().contains(result.project))
        assertEquals(0, result.supports)
        assertTrue(result.updates.isEmpty())
    }
    @Test fun draftRemainsPrivateAndIdsAndSlugsDoNotCollide() = runBlocking {
        val repo = FakeProjectsRepository()
        val a = repo.crearProyecto(input, false, 99, "Autora").project
        val b = repo.crearProyecto(input, true, 99, "Autora").project
        assertNotEquals(a.id, b.id)
        assertNotEquals(a.slug, b.slug)
        assertEquals("draft", a.status)
        assertEquals("private", a.visibility)
        assertFalse(repo.obtenerProjectos().filter { it.status == "published" && it.visibility == "public" }.contains(a))
    }
    @Test fun invalidFormCannotCreateSampleRecords() = runBlocking {
        val repo = FakeProjectsRepository()
        val count = repo.obtenerProjectos().size
        val invalid = NewProject(" ", "x".repeat(501), "", null, listOf(999))
        assertEquals(setOf("title", "summary", "description", "category", "technologies"), invalid.errors().keys)
        try { repo.crearProyecto(invalid, false, 99, "Autora"); fail() } catch (_: IllegalArgumentException) { }
        assertEquals(count, repo.obtenerProjectos().size)
    }
}
