package com.example.parcial_tp3.ui.screens.progress

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial_tp3.ui.components.rememberDemoToast

@Composable
fun WorkoutProgressRoute(
    onBack: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val showDemoNotice = rememberDemoToast()
    WorkoutProgressScreen(
        state = state.workout,
        onBack = onBack,
        onMore = showDemoNotice,
        onPeriod = showDemoNotice,
    )
}
