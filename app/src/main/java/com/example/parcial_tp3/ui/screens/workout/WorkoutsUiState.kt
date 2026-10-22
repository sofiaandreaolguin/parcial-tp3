package com.example.parcial_tp3.ui.screens.workout

import com.example.parcial_tp3.domain.model.Workout

/** Estado del catalogo. Es sealed interface para que la tarea de estados (RF05) sume Empty. */
sealed interface WorkoutsUiState {
    data object Loading : WorkoutsUiState
    data class Content(val workouts: List<Workout>) : WorkoutsUiState
    data object Error : WorkoutsUiState
}
