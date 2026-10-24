package com.example.parcial_tp3.ui.screens.progress

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial_tp3.ui.components.rememberDemoToast

@Composable
fun TodaysOverviewRoute(
    onStartWorkout: () -> Unit,
    onWorkoutProgress: () -> Unit,
    onNutritionProgress: () -> Unit,
    onHealthTrends: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val showDemoNotice = rememberDemoToast()
    TodaysOverviewScreen(
        state = state.overview,
        onStartWorkout = onStartWorkout,
        onCategory = { kind ->
            when (kind) {
                CategoryKind.WORKOUT -> onWorkoutProgress()
                CategoryKind.NUTRITION -> onNutritionProgress()
                CategoryKind.ACTIVITY -> onHealthTrends()
                CategoryKind.HYDRATION -> showDemoNotice()
            }
        },
    )
}
