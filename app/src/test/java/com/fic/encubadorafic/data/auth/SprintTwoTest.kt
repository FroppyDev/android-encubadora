package com.fic.encubadorafic.data.auth

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class SprintTwoTest {
    private class Store : TokenStore {
        var token: String? = "test-token"
        override fun read() = token
        override fun write(token: String) { this.token = token }
        override fun clear() { token = null }
    }
    private class Service : AuthService {
        var account = AuthUser(1, "Ana", "ana@info.uas.edu.mx", true, "Original")
        var failure: AuthException? = null
        var updates = 0
        val pages = mutableListOf<Int>()
        override fun register(name: String, email: String, password: String, confirmation: String) = Unit
        override fun login(email: String, password: String, deviceName: String) = LoginSession(account, "test-token")
        override fun user(token: String) = account
        override fun logout(token: String) = Unit
        override fun resend(token: String) = Unit
        override fun updateProfile(token: String, name: String, bio: String?): AuthUser {
            failure?.let { throw it }
            updates++
            account = account.copy(name = name, bio = bio)
            return account
        }
        override fun categories(token: String, page: Int): CategoryPage {
            failure?.let { throw it }
            pages.add(page)
            return if (page == 1) CategoryPage(listOf(Category(1, "Activa", null, true), Category(2, "Inactiva", null, false)), 2)
            else CategoryPage(listOf(Category(1, "Activa", null, true), Category(3, "Otra", null, true)), 2)
        }
    }

    @Test fun profilePersistsAndBlankBiographyIsCleared() = runBlocking {
        val api = Service()
        val repository = AuthRepository(api, Store())
        val result = repository.updateProfile(" Ana Nueva ", " Nueva biografía ")
        assertEquals("Ana Nueva", result.name)
        assertEquals("Nueva biografía", repository.refresh().bio)
        assertEquals("ana@info.uas.edu.mx", result.email)
        assertTrue(result.verified)
        repository.updateProfile("Ana Nueva", "   ")
        assertNull(repository.refresh().bio)
    }

    @Test fun invalidProfileDoesNotReachServer() = runBlocking {
        val api = Service()
        try { AuthRepository(api, Store()).updateProfile(" ", "x".repeat(2001)); fail() }
        catch (e: AuthException) { assertEquals(setOf("name", "bio"), e.fields.keys) }
        assertEquals(0, api.updates)
    }

    @Test fun profile401ClearsSessionButNetworkFailureDoesNot() = runBlocking {
        val api = Service()
        val store = Store()
        val repository = AuthRepository(api, store)
        for (status in listOf(0, 401)) {
            api.failure = AuthException(status, "Error")
            try { repository.updateProfile("Ana", "Texto"); fail() } catch (_: AuthException) { }
            if (status == 0) assertNotNull(store.token) else assertNull(store.token)
        }
        assertEquals("Original", api.account.bio)
    }

    @Test fun categoriesReadEveryPageExcludeInactiveAndAvoidDuplicates() = runBlocking {
        val api = Service()
        val result = AuthRepository(api, Store()).categories()
        assertEquals(listOf(1, 2), api.pages)
        assertEquals(listOf(1L, 3L), result.map { it.id })
    }

    @Test fun categoriesRequireSession() = runBlocking {
        val api = Service()
        val store = Store().apply { clear() }
        try { AuthRepository(api, store).categories(); fail() }
        catch (e: AuthException) { assertEquals(401, e.status) }
        assertTrue(api.pages.isEmpty())
    }

    @Test fun recoveryLinkPreservesServerAndDeploymentPrefix() {
        assertEquals("http://127.0.0.1:8000/forgot-password", BackendLinks.passwordRecovery("http://127.0.0.1:8000/api/v1/", true))
        assertEquals("https://example.org/platform/forgot-password", BackendLinks.passwordRecovery("https://example.org/platform/api/v1", false))
    }

    @Test fun recoveryRejectsInsecureOrMalformedConfiguration() {
        for (url in listOf("http://example.org/api/v1", "https://user:secret@example.org/api/v1", "https://example.org/api/v1?token=x", "https://example.org/other", "")) {
            try { BackendLinks.passwordRecovery(url, false); fail(url) } catch (_: IllegalArgumentException) { }
        }
    }
}
