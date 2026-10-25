package com.example.parcial_tp3.ui.screens.workout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial_tp3.ui.components.rememberDemoToast

@Composable
fun WeeklyPlanRoute(
    onWorkout: (workoutId: String) -> Unit,
    viewModel: WorkoutsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    WeeklyPlanScreen(
        state = state,
        onWorkout = onWorkout,
        onAdjust = rememberDemoToast(),
        onRetry = viewModel::onRetry,
    )
}
