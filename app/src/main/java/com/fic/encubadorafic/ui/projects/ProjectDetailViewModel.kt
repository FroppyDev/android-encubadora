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
    val busy: Boolean = false,
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

    // Demo behavior remains available for previews/tests; real actions use the API.
    fun toggleSupport() {
        val detail = mutable.value.detail ?: return
        if (detail.project.status != "published" || mutable.value.busy) return
        if (!detail.demo) {
            val api = repository as? com.fic.encubadorafic.data.repository.ApiProjectsRepository ?: return
            mutate { detail.copy(supported = true, supports = api.support(detail.project.id, true)) }
            return
        }
        update(
            detail.copy(
                supported = !detail.supported,
                supports = (detail.supports + if (detail.supported) -1 else 1).coerceAtLeast(0)
            )
        )
    }

    fun requestCollaboration() {
        val detail = mutable.value.detail ?: return
        if (mutable.value.busy || detail.project.status != "published") return
        if (detail.demo) { update(detail.copy(collaborationPending = true)); return }
        val api = repository as? com.fic.encubadorafic.data.repository.ApiProjectsRepository ?: return
        mutate { api.collaborate(detail.project.id); detail.copy(collaborationPending = true) }
    }

    fun removeSupport() {
        val detail = mutable.value.detail ?: return
        if (mutable.value.busy) return
        val api = repository as? com.fic.encubadorafic.data.repository.ApiProjectsRepository ?: return
        mutate { detail.copy(supported = false, supports = api.support(detail.project.id, false)) }
    }

    private fun mutate(action: suspend () -> ProjectDetail) {
        mutable.value = mutable.value.copy(busy = true, error = null)
        viewModelScope.launch {
            try { mutable.value = ProjectDetailState(loading = false, detail = action()) }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { mutable.value = mutable.value.copy(busy = false, error = e.message ?: "No se pudo completar la operación.") }
        }
    }

    private fun update(detail: ProjectDetail) {
        demos[detail.project.id] = detail
        mutable.value = ProjectDetailState(loading = false, detail = detail)
    }
}
