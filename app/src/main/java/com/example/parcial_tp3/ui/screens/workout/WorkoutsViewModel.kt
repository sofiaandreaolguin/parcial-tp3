package com.example.parcial_tp3.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.R
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
        viewModelScope.launch {
            _uiState.value = repository.getWorkouts().fold(
                onSuccess = { WorkoutsUiState.Content(it) },
                onFailure = { WorkoutsUiState.Error(R.string.workout_error) },
            )
        }
    }

    fun onRetry() {}
}
