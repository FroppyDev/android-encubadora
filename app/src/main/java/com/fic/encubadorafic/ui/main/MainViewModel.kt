package com.fic.encubadorafic.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fic.encubadorafic.data.repository.ProjectsRepository
import com.fic.encubadorafic.model.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ProjectsUiState(
    val proyectos: List<Project> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ProjectsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState.asStateFlow()


    private var loadingJob: kotlinx.coroutines.Job? = null

    fun cargarRutas() {
        loadingJob?.cancel()
        loadingJob = viewModelScope.launch {
            _uiState.value = ProjectsUiState(cargando = true)

            try {
                val proyectos = repository.obtenerProjectos()
                    .filter { it.status == "published" && it.visibility == "public" && it.deleted_at == null }

                _uiState.value = ProjectsUiState(
                    proyectos = proyectos,
                    cargando = false
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = ProjectsUiState(
                    cargando = false,
                    error = (e as? com.fic.encubadorafic.data.auth.AuthException)?.message
                        ?: "No se pudieron cargar los proyectos"
                )
            }
        }
    }

}