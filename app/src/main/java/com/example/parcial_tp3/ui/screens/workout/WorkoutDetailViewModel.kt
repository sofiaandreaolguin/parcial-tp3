package com.example.parcial_tp3.ui.screens.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.data.repository.WorkoutDetailRepository
import com.example.parcial_tp3.domain.model.WorkoutDetail
import com.example.parcial_tp3.ui.navigation.Rutas
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Lo usan 03, 04 y 06. El workoutId (y en 06 el exerciseId) llega por la ruta (NAV04), asi cada
 * entrenamiento abre su propio detalle.
 */
@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WorkoutDetailRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkoutDetailUiState>(WorkoutDetailUiState.Loading)
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.asStateFlow()

    // Como en el catalogo, la carga arranca una sola vez en init: rotar no repite la consulta.
    init {
        val workoutId = savedStateHandle.get<String>(Rutas.ARG_WORKOUT_ID)
        val exerciseId = savedStateHandle.get<String>(Rutas.ARG_EXERCISE_ID)
        if (workoutId.isNullOrBlank()) {
            _uiState.value = WorkoutDetailUiState.Error
        } else {
            viewModelScope.launch {
                _uiState.value = repository.getWorkoutDetail(workoutId).fold(
                    onSuccess = { contenido(it, exerciseId) },
                    onFailure = { WorkoutDetailUiState.Error },
                )
            }
        }
    }

    private fun contenido(detail: WorkoutDetail, exerciseId: String?): WorkoutDetailUiState {
        if (exerciseId == null) return WorkoutDetailUiState.Content(detail)
        val exercise = detail.exercises.find { it.id == exerciseId } ?: return WorkoutDetailUiState.Error
        return WorkoutDetailUiState.Content(detail, exercise)
    }
}
