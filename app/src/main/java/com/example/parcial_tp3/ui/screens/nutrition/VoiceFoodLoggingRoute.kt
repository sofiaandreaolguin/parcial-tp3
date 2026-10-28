package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.example.parcial_tp3.ui.components.IconosDelSistemaClaros

/**
 * @param grafo la entrada del grafo de Nutrition, de donde se pide el ViewModel que 05 a 07 comparten.
 * @param onConfirmed navega a 07, cuando "Continue" del pop-up de carga ("Type instead") la abrió.
 */
@Composable
fun VoiceFoodLoggingRoute(
    grafo: NavBackStackEntry,
    onDone: () -> Unit,
    onConfirmed: () -> Unit,
    onClose: () -> Unit,
    viewModel: FoodCaptureViewModel = hiltViewModel(grafo),
    nutricion: NutritionViewModel = hiltViewModel(grafo),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    IconosDelSistemaClaros()

    VoiceFoodLoggingScreen(
        state = state,
        onDone = {
            viewModel.onDoneSpeaking()
            onDone()
        },
        onTypeInstead = { viewModel.onOpenManualEntry(nutricion.selectedDate.value) },
        onClose = onClose,
    )

    ManualEntrySheetHost(grafo = grafo, onContinued = onConfirmed, viewModel = viewModel, onDarkScreen = true)
}
