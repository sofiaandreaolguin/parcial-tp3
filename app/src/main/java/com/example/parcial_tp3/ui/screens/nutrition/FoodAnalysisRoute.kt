package com.example.parcial_tp3.ui.screens.nutrition

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.example.parcial_tp3.ui.screens.workout.IconosDelSistemaClaros

/**
 * @param grafo la entrada del grafo de Nutrition, de donde se pide el ViewModel que 04 a 07 comparten.
 * @param onDone se llama una sola vez, cuando el análisis termina.
 * @param onClose saca a 05 de la pila; la Route corta el análisis antes de llamarlo.
 */
@Composable
fun FoodAnalysisRoute(
    grafo: NavBackStackEntry,
    onDone: () -> Unit,
    onClose: () -> Unit,
    viewModel: FoodCaptureViewModel = hiltViewModel(grafo),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnDone by rememberUpdatedState(onDone)
    IconosDelSistemaClaros()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                FoodCaptureEvent.AnalysisDone -> currentOnDone()
            }
        }
    }

    val cancel = {
        viewModel.onCancelAnalysis()
        onClose()
    }
    BackHandler(onBack = cancel)

    FoodAnalysisScreen(state = state, onCancel = cancel)
}
