package com.fic.encubadorafic.ui.sessions

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fic.encubadorafic.BuildConfig
import com.fic.encubadorafic.data.auth.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AuthPhase { Restoring, Guest, SignedIn }
data class AuthUiState(
    val phase: AuthPhase = AuthPhase.Restoring,
    val user: AuthUser? = null,
    val busy: Boolean = false,
    val error: String? = null,
    val fields: Map<String, String> = emptyMap(),
    val message: String? = null,
    val registered: Boolean = false,
    val categories: List<Category> = emptyList(),
    val categoriesLoaded: Boolean = false,
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(
        HttpAuthService(BuildConfig.API_BASE_URL, BuildConfig.DEBUG),
        KeystoreTokenStore(application, BuildConfig.API_BASE_URL)
    )
    private val mutableState = MutableStateFlow(AuthUiState())
    val state = mutableState.asStateFlow()

    init {
        restore()
    }

    fun restore() = perform {
        val user = repository.restore()
        mutableState.value = AuthUiState(
            phase = if (user == null) AuthPhase.Guest else AuthPhase.SignedIn,
            user = user
        )
    }

    fun login(email: String, password: String) = perform {
        val user = repository.login(
            email,
            password,
            "Android ${Build.MANUFACTURER} ${Build.MODEL}".take(100)
        )
        mutableState.value = AuthUiState(phase = AuthPhase.SignedIn, user = user)
    }

    fun register(name: String, email: String, password: String, confirmation: String) = perform {
        repository.register(name, email, password, confirmation)
        mutableState.value = AuthUiState(
            phase = AuthPhase.Guest, registered = true,
            message = "Cuenta creada. Revisa tu correo para verificarla y después inicia sesión."
        )
    }

    fun refreshVerification() = perform {
        val user = repository.refresh()
        mutableState.value = mutableState.value.copy(
            user = user,
            message = if (!user.verified) "Tu correo sigue pendiente. Abre el enlace recibido y vuelve a comprobar." else null
        )
    }

    fun loadProfile() = perform {
        mutableState.value = mutableState.value.copy(user = repository.refresh())
    }

    fun saveProfile(name: String, bio: String) = perform {
        val user = repository.updateProfile(name, bio)
        mutableState.value =
            mutableState.value.copy(user = user, message = "Perfil guardado correctamente.")
    }

    fun loadCategories() = perform {
        val categories = repository.categories()
        mutableState.value =
            mutableState.value.copy(categories = categories, categoriesLoaded = true)
    }

    fun resend() = perform {
        repository.resend()
        mutableState.value =
            mutableState.value.copy(message = "Enlace enviado. Revisa tu bandeja de entrada y correo no deseado.")
    }

    fun logout() = perform {
        repository.logout()
        mutableState.value = AuthUiState(phase = AuthPhase.Guest)
    }

    fun clearMessages() {
        if (!mutableState.value.busy) mutableState.value = mutableState.value.copy(
            error = null,
            fields = emptyMap(),
            message = null,
            registered = false
        )
    }

    private fun perform(action: suspend () -> Unit) {
        if (mutableState.value.busy) return
        mutableState.value =
            mutableState.value.copy(busy = true, error = null, fields = emptyMap(), message = null)
        viewModelScope.launch {
            try {
                action()
            } catch (e: CancellationException) {
                throw e
            } catch (e: AuthException) {
                mutableState.value =
                    if (e.status == 401) AuthUiState(phase = AuthPhase.Guest, error = e.message)
                    else mutableState.value.copy(error = e.message, fields = e.fields)
            } catch (e: Exception) {
                mutableState.value =
                    mutableState.value.copy(error = "No se pudo completar la operación. Intenta de nuevo.")
            } finally {
                mutableState.value = mutableState.value.copy(busy = false)
            }
        }
    }
}
