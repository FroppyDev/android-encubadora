package com.fic.encubadorafic.ui.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fic.encubadorafic.data.repository.ProjectsRepository
import com.fic.encubadorafic.model.ProjectDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProjectDetailState(
    val loading: Boolean = true,
    val detail: ProjectDetail? = null,
    val error: String? = null,
)

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(private val repository: ProjectsRepository) :
    ViewModel() {
    private val mutable = MutableStateFlow(ProjectDetailState())
    val state = mutable.asStateFlow()
    private var job: Job? = null
    private val demos = mutableMapOf<Long, ProjectDetail>()

    fun load(id: Long) {
        job?.cancel()
        mutable.value = ProjectDetailState()
        job = viewModelScope.launch {
            try {
                val detail = demos[id] ?: repository.obtenerDetalle(id)
                mutable.value = ProjectDetailState(
                    loading = false, detail = detail,
                    error = if (detail == null) "Este proyecto ya no está disponible." else null
                )
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                mutable.value = ProjectDetailState(
                    loading = false,
                    error = "No se pudo cargar el proyecto. Intenta de nuevo."
                )
            }
        }
    }

    // Only demo state is mutated. Replace these callbacks with repository operations when integrating the API.
    fun toggleSupport() {
        val detail = mutable.value.detail ?: return
        if (!detail.demo || detail.project.status != "published") return
        update(
            detail.copy(
                supported = !detail.supported,
                supports = (detail.supports + if (detail.supported) -1 else 1).coerceAtLeast(0)
            )
        )
    }

    fun requestCollaboration() {
        val detail = mutable.value.detail ?: return
        if (!detail.demo || detail.project.status != "published") return
        update(detail.copy(collaborationPending = true))
    }

    private fun update(detail: ProjectDetail) {
        demos[detail.project.id] = detail
        mutable.value = ProjectDetailState(loading = false, detail = detail)
    }
}
