package com.fic.encubadorafic.data.auth

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class AuthRepositoryTest {
    private class MemoryStore : TokenStore {
        var token: String? = null
        override fun read() = token
        override fun write(token: String) { this.token = token }
        override fun clear() { token = null }
    }
    private class FakeService : AuthService {
        var failure: AuthException? = null
        var receivedEmail = ""
        var receivedConfirmation = ""
        var logoutToken: String? = null
        val account = AuthUser(1, "Estudiante", "test@info.uas.edu.mx", false)
        override fun register(name: String, email: String, password: String, confirmation: String) {
            failure?.let { throw it }; receivedEmail = email; receivedConfirmation = confirmation
        }
        override fun login(email: String, password: String, deviceName: String): LoginSession {
            failure?.let { throw it }; receivedEmail = email; return LoginSession(account, "test-token")
        }
        override fun user(token: String): AuthUser { failure?.let { throw it }; return account }
        override fun updateProfile(token: String, name: String, bio: String?) = account.copy(name = name, bio = bio)
        override fun categories(token: String, page: Int) = CategoryPage(emptyList(), 1)
        override fun resend(token: String) { failure?.let { throw it } }
        override fun logout(token: String) { failure?.let { throw it }; logoutToken = token }
    }

    @Test fun registrationNormalizesAndDoesNotCreateSession() = runBlocking {
        val api = FakeService(); val store = MemoryStore(); val repository = AuthRepository(api, store)
        repository.register(" Alumna ", " TEST@INFO.UAS.EDU.MX ", "Password123!", "Password123!")
        assertEquals("test@info.uas.edu.mx", api.receivedEmail)
        assertEquals("Password123!", api.receivedConfirmation)
        assertNull(store.token)
    }
    @Test fun rejectsExternalOrDeceptiveDomainAndMismatchedPassword() {
        for (email in listOf("a@gmail.com", "a@info.uas.edu.mx.otro.com", "a@sub.info.uas.edu.mx", "@info.uas.edu.mx")) {
            assertTrue(AuthValidation.registration("A", email, "Password123!", "Password123!").containsKey("email"))
        }
        val errors = AuthValidation.registration("", "a@info.uas.edu.mx", "short", "other")
        assertTrue(errors.keys.containsAll(listOf("name", "password", "password_confirmation")))
    }
    @Test fun loginStoresTokenButPreservesUnverifiedState() = runBlocking {
        val api = FakeService(); val store = MemoryStore(); val repository = AuthRepository(api, store)
        assertFalse(repository.login(" TEST@INFO.UAS.EDU.MX ", "password", "Android").verified)
        assertEquals("test-token", store.token)
        assertEquals("test@info.uas.edu.mx", api.receivedEmail)
        assertEquals(api.account, repository.restore())
    }
    @Test fun failedLoginDoesNotPersistToken() = runBlocking {
        val api = FakeService().apply { failure = AuthException(422, "Credenciales inválidas") }
        val store = MemoryStore()
        try { AuthRepository(api, store).login("test@info.uas.edu.mx", "wrong", "Android"); fail() } catch (_: AuthException) { }
        assertNull(store.token)
    }
    @Test fun expiredSessionIsRemovedButOfflineSessionIsKept() = runBlocking {
        val api = FakeService(); val store = MemoryStore(); val repository = AuthRepository(api, store)
        store.write("old")
        api.failure = AuthException(0, "Sin conexión")
        try { repository.restore(); fail() } catch (_: AuthException) { }
        assertEquals("old", store.token)
        api.failure = AuthException(401, "Expiró")
        try { repository.restore(); fail() } catch (_: AuthException) { }
        assertNull(store.token)
    }
    @Test fun logoutRevokesBeforeRemovingTokenAndAllowsRetryOnFailure() = runBlocking {
        val api = FakeService(); val store = MemoryStore(); val repository = AuthRepository(api, store)
        store.write("current")
        api.failure = AuthException(0, "Sin conexión")
        try { repository.logout(); fail() } catch (_: AuthException) { }
        assertEquals("current", store.token)
        api.failure = null
        repository.logout()
        assertEquals("current", api.logoutToken)
        assertNull(store.token)
    }
    @Test fun revokedTokenCanStillBeClearedOnLogout() = runBlocking {
        val api = FakeService().apply { failure = AuthException(401, "Expiró") }
        val store = MemoryStore().apply { write("revoked") }
        AuthRepository(api, store).logout()
        assertNull(store.token)
    }
}
