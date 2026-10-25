package com.example.parcial_tp3.ui.screens.workout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WorkoutDashboardRoute(
    onWeeklyPlan: () -> Unit,
    onStartWorkout: (workoutId: String) -> Unit,
    onWorkoutDetails: (workoutId: String) -> Unit,
    onViewProgress: () -> Unit,
    viewModel: WorkoutsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    WorkoutDashboardScreen(
        state = state,
        onWeeklyPlan = onWeeklyPlan,
        onStartWorkout = onStartWorkout,
        onWorkoutDetails = onWorkoutDetails,
        onViewProgress = onViewProgress,
        onRetry = viewModel::onRetry,
    )
}
