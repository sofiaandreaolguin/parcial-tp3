package com.example.parcial_tp3.ui.screens.workout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial_tp3.ui.components.IconosDelSistemaClaros

@Composable
fun ActiveWorkoutRoute(
    onFinish: (workoutId: String) -> Unit,
    viewModel: ActiveWorkoutViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    IconosDelSistemaClaros()

    ActiveWorkoutScreen(
        state = state,
        onCompleteSet = viewModel::onCompleteSet,
        onIncreaseWeight = viewModel::onIncreaseWeight,
        onDecreaseWeight = viewModel::onDecreaseWeight,
        onAddRest = viewModel::onAddRest,
        onSkipRest = viewModel::onSkipRest,
        onRetry = viewModel::onRetry,
        // El ViewModel no navega: primero corta el descanso y despues se abre 07 (spec, Contrato)
        onFinish = { workoutId ->
            viewModel.onFinish()
            onFinish(workoutId)
        },
    )
}
