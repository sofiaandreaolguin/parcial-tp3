package com.example.parcial_tp3.ui.screens.workout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial_tp3.ui.components.IconosDelSistemaClaros

@Composable
fun PreWorkoutRoute(
    onClose: () -> Unit,
    onAskVora: () -> Unit,
    onStartWorkout: (workoutId: String) -> Unit,
    viewModel: WorkoutDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    IconosDelSistemaClaros()

    PreWorkoutScreen(
        state = state,
        onClose = onClose,
        onAskVora = onAskVora,
        onStartWorkout = onStartWorkout,
    )
}
