package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavBackStackEntry
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

/**
 * "Log Manually" y la búsqueda abren el pop-up de carga encima de 03, sin navegar.
 *
 * @param onPhoto navega a 06; el borrador de la foto se carga antes, acá.
 * @param onConfirmed navega a 07, cuando "Continue" del pop-up de carga la abrió.
 */
@Composable
fun AddFoodRoute(
    grafo: NavBackStackEntry,
    onBack: () -> Unit,
    onVoice: () -> Unit,
    onPhoto: () -> Unit,
    onConfirmed: () -> Unit,
    @Suppress("UNUSED_PARAMETER") viewModel: AddFoodViewModel = hiltViewModel(),
    captura: FoodCaptureViewModel = hiltViewModel(grafo),
    nutricion: NutritionViewModel = hiltViewModel(grafo),
) {
    var mostrarBuscador by rememberSaveable { mutableStateOf(false) }

    AddFoodScreen(
        onBack = onBack,
        onVoice = onVoice,
        onPhoto = {
            captura.onPhotoCaptureStarted()
            onPhoto()
        },
        onSearch = { mostrarBuscador = true },
        onManual = { captura.onOpenManualEntry(nutricion.selectedDate.value) },
    )

    if (mostrarBuscador) {
        FoodSearchSheetHost(
            grafo = grafo,
            onDismiss = { mostrarBuscador = false },
            // La búsqueda dejó abierto el pop-up de carga: se cierra la hoja y queda ese.
            onConfirmed = { mostrarBuscador = false },
        )
    }

    ManualEntrySheetHost(grafo = grafo, onContinued = onConfirmed)
}
