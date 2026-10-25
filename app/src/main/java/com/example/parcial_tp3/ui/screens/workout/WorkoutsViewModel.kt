package com.example.parcial_tp3.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val repository: WorkoutRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkoutsUiState>(WorkoutsUiState.Loading)
    val uiState: StateFlow<WorkoutsUiState> = _uiState.asStateFlow()

    // La carga arranca una sola vez, al crear el ViewModel: rotar o volver a observar no repite la
    // consulta (por eso no se usa stateIn con WhileSubscribed).
    init {
        cargar()
    }

    // Loading se pone antes del launch: un doble toque ve Loading en el segundo y no consulta dos veces.
    fun onRetry() {
        if (_uiState.value !is WorkoutsUiState.Error) return
        _uiState.value = WorkoutsUiState.Loading
        cargar()
    }

    private fun cargar() {
        viewModelScope.launch {
            _uiState.value = repository.getWorkouts().fold(
                onSuccess = { if (it.isEmpty()) WorkoutsUiState.Empty else WorkoutsUiState.Content(it) },
                onFailure = { WorkoutsUiState.Error(catalogErrorMessage(it)) },
            )
        }
    }
}
