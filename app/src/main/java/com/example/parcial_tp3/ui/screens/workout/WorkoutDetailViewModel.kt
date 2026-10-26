package com.example.parcial_tp3.ui.screens.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.WorkoutDetailRepository
import com.example.parcial_tp3.data.repository.WorkoutNotCachedException
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

    private val workoutId = savedStateHandle.get<String>(Rutas.ARG_WORKOUT_ID)
    private val exerciseId = savedStateHandle.get<String>(Rutas.ARG_EXERCISE_ID)

    private val _uiState = MutableStateFlow<WorkoutDetailUiState>(WorkoutDetailUiState.Loading)
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.asStateFlow()

    // Como en el catalogo, la carga arranca una sola vez en init: rotar no repite la consulta.
    init {
        if (workoutId.isNullOrBlank()) {
            _uiState.value = WorkoutDetailUiState.Error(R.string.details_error)
        } else {
            cargar(workoutId)
        }
    }

    // Loading antes del launch, como en el catalogo. Sin workoutId no hay nada que reintentar (C25).
    fun onRetry() {
        val estado = _uiState.value
        if (estado !is WorkoutDetailUiState.Error && estado != WorkoutDetailUiState.NotAvailableOffline) return
        if (workoutId.isNullOrBlank()) return
        _uiState.value = WorkoutDetailUiState.Loading
        cargar(workoutId)
    }

    private fun cargar(workoutId: String) {
        viewModelScope.launch {
            _uiState.value = repository.getWorkoutDetail(workoutId).fold(
                onSuccess = { contenido(it) },
                onFailure = { error ->
                    if (error is WorkoutNotCachedException) {
                        WorkoutDetailUiState.NotAvailableOffline
                    } else {
                        WorkoutDetailUiState.Error(detailErrorMessage(error))
                    }
                },
            )
        }
    }

    private fun contenido(detail: WorkoutDetail): WorkoutDetailUiState {
        if (exerciseId == null) return WorkoutDetailUiState.Content(detail)
        val exercise = detail.exercises.find { it.id == exerciseId }
            ?: return WorkoutDetailUiState.Error(R.string.details_error)
        return WorkoutDetailUiState.Content(detail, exercise)
    }
}
