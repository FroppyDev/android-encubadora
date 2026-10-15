package com.fic.encubadorafic.ui.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fic.encubadorafic.data.repository.ProjectsRepository
import com.fic.encubadorafic.model.NewProject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateProjectState(
    val busy: Boolean = false,
    val error: String? = null,
    val savedId: Long? = null,
)

@HiltViewModel
class CreateProjectViewModel @Inject constructor(private val repository: ProjectsRepository) :
    ViewModel() {
    private val mutable = MutableStateFlow(CreateProjectState())
    val state = mutable.asStateFlow()
    fun consumeSaved() {
        mutable.value = CreateProjectState()
    }

    fun save(input: NewProject, published: Boolean, authorId: Long, authorName: String) {
        if (mutable.value.busy || mutable.value.savedId != null) return
        mutable.value = CreateProjectState(busy = true)
        viewModelScope.launch {
            try {
                val result = repository.crearProyecto(input, published, authorId, authorName)
                mutable.value = CreateProjectState(savedId = result.project.id)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                mutable.value =
                    CreateProjectState(error = "No se pudo guardar. Conservamos tus datos para que puedas reintentar.")
            }
        }
    }
}
