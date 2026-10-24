package com.example.parcial_tp3.ui.screens.progress

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial_tp3.ui.components.IconosDelSistemaClaros

@Composable
fun WeeklyReviewRoute(
    onBack: () -> Unit,
    onAskVora: () -> Unit,
    onWeeklyPlan: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    IconosDelSistemaClaros()
    WeeklyReviewScreen(
        state = state.review,
        onBack = onBack,
        onAskVora = onAskVora,
        onWeeklyPlan = onWeeklyPlan,
    )
}
