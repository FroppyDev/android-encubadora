package com.fic.encubadorafic.data.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

data class AuthUser(val id: Long, val name: String, val email: String, val verified: Boolean, val bio: String? = null, val avatarUrl: String? = null, val coverImageUrl: String? = null)

class LoginSession(val user: AuthUser, val token: String)
class AuthException(
    val status: Int,
    override val message: String,
    val fields: Map<String, String> = emptyMap(),
) : Exception(message)

interface TokenStore {
    fun read(): String?
    fun write(token: String)
    fun clear()
}

interface AuthService {
    fun register(name: String, email: String, password: String, confirmation: String)

    fun login(email: String, password: String, deviceName: String): LoginSession

    fun user(token: String): AuthUser

    fun updateProfile(token: String, name: String, bio: String?): AuthUser

    fun categories(token: String, page: Int): CategoryPage

    fun uploadImage(token: String, field: String, bytes: ByteArray, mime: String): AuthUser =
        throw UnsupportedOperationException()

    fun technologies(token: String): List<com.fic.encubadorafic.model.ProjectOption> = emptyList()

    fun resend(token: String)

    fun logout(token: String)
}

object AuthValidation {
    fun email(value: String) = value.trim().lowercase(Locale.ROOT)

    fun registration(
        name: String,
        email: String,
        password: String,
        confirmation: String,
    ): Map<String, String> = buildMap {
        if (name.trim().isEmpty() || name.trim().length > 100) put(
            "name",
            "Escribe tu nombre (máximo 100 caracteres)."
        )
        val normalized = email(email)
        if (!normalized.matches(Regex("^[^\\s@]+@info\\.uas\\.edu\\.mx$"))) put(
            "email",
            "Usa tu correo @info.uas.edu.mx."
        )
        if (password.length < 10) put(
            "password",
            "La contraseña debe tener al menos 10 caracteres."
        )
        if (password != confirmation) put("password_confirmation", "Las contraseñas no coinciden.")
    }
}

class AuthRepository(private val service: AuthService, private val tokens: TokenStore) {
    suspend fun register(name: String, email: String, password: String, confirmation: String) =
        withContext(Dispatchers.IO) {
            val errors = AuthValidation.registration(name, email, password, confirmation)
            if (errors.isNotEmpty()) throw AuthException(
                422,
                "Revisa los campos indicados.",
                errors
            )
            // Laravel registration does not return a token. Never simulate a session here.
            service.register(name.trim(), AuthValidation.email(email), password, confirmation)
        }

    suspend fun login(email: String, password: String, deviceName: String): AuthUser =
        withContext(Dispatchers.IO) {
            if (email.isBlank() || password.isBlank()) throw AuthException(
                422,
                "Escribe tu correo y contraseña."
            )
            val session = service.login(AuthValidation.email(email), password, deviceName)
            if (session.token.isBlank()) throw AuthException(
                502,
                "El servidor no devolvió una sesión válida."
            )
            try {
                tokens.write(session.token)
            } catch (e: Exception) {
                runCatching { service.logout(session.token) }
                throw AuthException(
                    0,
                    "No se pudo guardar la sesión de forma segura. Intenta de nuevo."
                )
            }
            session.user
        }

    suspend fun restore(): AuthUser? = withContext(Dispatchers.IO) {
        val token = tokens.read() ?: return@withContext null
        authenticated(token) { service.user(it) }
    }

    suspend fun refresh(): AuthUser = withContext(Dispatchers.IO) {
        authenticated(requireToken()) { service.user(it) }
    }

    suspend fun updateProfile(name: String, bio: String): AuthUser = withContext(Dispatchers.IO) {
        val errors = buildMap {
            if (name.trim().isEmpty() || name.trim().length > 100) put("name", "Escribe tu nombre (máximo 100 caracteres).")
            if (bio.length > 2000) put("bio", "La biografía admite hasta 2000 caracteres.")
        }
        if (errors.isNotEmpty()) throw AuthException(422, "Revisa los campos indicados.", errors)
        authenticated(requireToken()) { service.updateProfile(it, name.trim(), bio.trim().ifEmpty { null }) }
    }

    suspend fun categories(): List<Category> = withContext(Dispatchers.IO) {
        authenticated(requireToken()) { token ->
            val result = linkedMapOf<Long, Category>()
            var page = 1
            do {
                val response = service.categories(token, page)
                response.items.filter { it.active }.forEach { result[it.id] = it }
                val more = page < response.lastPage
                page++
            } while (more)
            result.values.toList()
        }
    }

    suspend fun uploadImage(field: String, bytes: ByteArray, mime: String): AuthUser = withContext(Dispatchers.IO) {
        authenticated(requireToken()) { service.uploadImage(it, field, bytes, mime) }
    }

    suspend fun technologies() = withContext(Dispatchers.IO) {
        authenticated(requireToken()) { service.technologies(it) }
    }

    suspend fun resend() = withContext(Dispatchers.IO) {
        authenticated(requireToken()) { service.resend(it) }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        val token = tokens.read()
        if (token != null) {
            try {
                service.logout(token)
            } catch (e: AuthException) {
                if (e.status != 401) throw e
            }
        }
        // On network failure keep the session so the user can retry server revocation.
        tokens.clear()
    }

    private fun requireToken(): String =
        tokens.read() ?: throw AuthException(401, "Tu sesión expiró. Inicia sesión nuevamente.")

    private fun <T> authenticated(token: String, block: (String) -> T): T = try {
        block(token)
    } catch (e: AuthException) {
        if (e.status == 401) tokens.clear()
        throw e
    }
}
