package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry

/**
 * @param grafo la entrada del grafo de Nutrition: el ViewModel se pide ahí para compartirlo con 02,
 *  así las dos muestran la misma fecha.
 */
@Composable
fun NutritionDashboardRoute(
    grafo: NavBackStackEntry,
    onHistory: () -> Unit,
    onMeal: (mealId: Long) -> Unit,
    onAddMeal: () -> Unit,
    onVoice: () -> Unit,
    onPhoto: () -> Unit,
    onConfirmed: () -> Unit,
    viewModel: NutritionViewModel = hiltViewModel(grafo),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val date by viewModel.selectedDate.collectAsStateWithLifecycle()
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var mostrarBuscador by rememberSaveable { mutableStateOf(false) }

    NutritionDashboardScreen(
        state = state,
        date = date,
        onCalendar = { showDatePicker = true },
        onRetry = viewModel::onRetry,
        onHistory = onHistory,
        onMeal = onMeal,
        onAddMeal = onAddMeal,
        onVoice = onVoice,
        onPhoto = onPhoto,
        onSearch = { mostrarBuscador = true },
    )

    if (mostrarBuscador) {
        FoodSearchSheetHost(
            grafo = grafo,
            onDismiss = { mostrarBuscador = false },
            onConfirmed = { mostrarBuscador = false; onConfirmed() },
        )
    }

    if (showDatePicker) {
        NutritionDatePickerDialog(
            selected = date,
            onConfirm = {
                viewModel.onDateSelected(it)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }
}
