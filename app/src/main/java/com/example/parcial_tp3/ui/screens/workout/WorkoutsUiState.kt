package com.example.parcial_tp3.ui.screens.workout

import androidx.annotation.StringRes
import com.example.parcial_tp3.domain.model.Workout

/** Estado del catalogo. Content nunca lleva una lista vacia: eso es Empty (RF05). */
sealed interface WorkoutsUiState {
    data object Loading : WorkoutsUiState
    data class Content(val workouts: List<Workout>) : WorkoutsUiState
    data object Empty : WorkoutsUiState
    data class Error(@StringRes val message: Int) : WorkoutsUiState
}
