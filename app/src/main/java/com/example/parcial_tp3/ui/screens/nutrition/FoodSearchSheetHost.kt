package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry

/**
 * La parte con estado de la busqueda de alimentos (EXT02).
 *
 * **No es un destino del `NavHost`.** Lo muestra la pantalla que la abre, igual que
 * `NutritionDatePickerDialog`: la Route guarda un booleano y dibuja esto cuando esta en true.
 * Asi la pantalla de atras se sigue viendo y el inventario de rutas no cambia.
 *
 * Pide FoodCaptureViewModel y NutritionViewModel atados a la entrada del grafo, igual que
 * `rememberAbrirConfirmacion`: el primero recibe la comida y el alimento y el segundo da la
 * fecha que se esta mirando, para que el total diario que cambie sea el de ese dia (C32).
 */
@Composable
fun FoodSearchSheetHost(
    grafo: NavBackStackEntry,
    onDismiss: () -> Unit,
    onConfirmed: () -> Unit,
    viewModel: FoodSearchViewModel = hiltViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val captura = hiltViewModel<FoodCaptureViewModel>(grafo)
    val nutricion = hiltViewModel<NutritionViewModel>(grafo)

    FoodSearchSheet(
        estado = estado,
        onQueryChange = viewModel::onQueryChange,
        onFoodSelected = viewModel::onFoodSelected,
        onGramsChange = viewModel::onGramsChange,
        onBackFromPortion = viewModel::onBackFromPortion,
        onConfirm = {
            val seleccion = viewModel.onConfirm(nutricion.selectedDate.value)
            if (seleccion != null) {
                captura.onPrefill(seleccion)
                onConfirmed()
            }
        },
        onRetry = viewModel::onRetry,
        onDismiss = onDismiss,
    )
}
