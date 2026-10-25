package com.example.parcial_tp3.ui.screens.workout

import androidx.annotation.StringRes
import com.example.parcial_tp3.domain.model.Exercise
import com.example.parcial_tp3.domain.model.WorkoutDetail

/** Estado del detalle (03, 04 y 06). selectedExercise solo lo usa 06. */
sealed interface WorkoutDetailUiState {
    data object Loading : WorkoutDetailUiState
    data class Content(
        val detail: WorkoutDetail,
        val selectedExercise: Exercise? = null,
    ) : WorkoutDetailUiState
    data class Error(@StringRes val message: Int) : WorkoutDetailUiState
    data object NotAvailableOffline : WorkoutDetailUiState
}
