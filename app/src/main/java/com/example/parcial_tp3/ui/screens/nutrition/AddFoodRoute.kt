package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavBackStackEntry
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun AddFoodRoute(
    grafo: NavBackStackEntry,
    onBack: () -> Unit,
    onVoice: () -> Unit,
    onPhoto: () -> Unit,
    onManual: () -> Unit,
    onConfirmed: () -> Unit,
    @Suppress("UNUSED_PARAMETER") viewModel: AddFoodViewModel = hiltViewModel(),
) {
    var mostrarBuscador by rememberSaveable { mutableStateOf(false) }

    AddFoodScreen(
        onBack = onBack,
        onVoice = onVoice,
        onPhoto = onPhoto,
        onSearch = { mostrarBuscador = true },
        onManual = onManual,
    )

    if (mostrarBuscador) {
        FoodSearchSheetHost(
            grafo = grafo,
            onDismiss = { mostrarBuscador = false },
            onConfirmed = { mostrarBuscador = false; onConfirmed() },
        )
    }
}
