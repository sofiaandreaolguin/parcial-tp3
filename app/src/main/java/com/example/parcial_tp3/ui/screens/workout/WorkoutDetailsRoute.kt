package com.example.parcial_tp3.ui.screens.workout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial_tp3.ui.components.rememberDemoToast

@Composable
fun WorkoutDetailsRoute(
    onBack: () -> Unit,
    onExercise: (workoutId: String, exerciseId: String) -> Unit,
    onStartWorkout: (workoutId: String) -> Unit,
    viewModel: WorkoutDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    WorkoutDetailsScreen(
        state = state,
        onBack = onBack,
        onExercise = onExercise,
        onStartWorkout = onStartWorkout,
        onEdit = rememberDemoToast(),
        onRetry = viewModel::onRetry,
    )
}
