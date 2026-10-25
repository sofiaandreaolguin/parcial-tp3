package com.example.parcial_tp3.ui.screens.workout

import com.example.parcial_tp3.domain.model.Exercise
import com.example.parcial_tp3.domain.model.WorkoutDetail

/**
 * Estado del detalle (03, 04 y 06). selectedExercise solo lo usa 06. Es sealed interface para que
 * la tarea de estados (RF05) cambie Error por uno con mensaje.
 */
sealed interface WorkoutDetailUiState {
    data object Loading : WorkoutDetailUiState
    data class Content(
        val detail: WorkoutDetail,
        val selectedExercise: Exercise? = null,
    ) : WorkoutDetailUiState
    data object Error : WorkoutDetailUiState
    data object NotAvailableOffline : WorkoutDetailUiState
}
