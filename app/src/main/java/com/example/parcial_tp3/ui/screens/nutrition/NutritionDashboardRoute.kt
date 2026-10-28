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
 * @param grafo la entrada del grafo de Nutrition: los ViewModels se piden ahí para compartirlos con
 *  02 (la fecha) y con 04 a 07 (la captura).
 * @param onPhoto navega a 06; el borrador de la foto se carga antes, acá.
 * @param onConfirmed navega a 07, cuando "Continue" del pop-up de carga la abrió.
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
    captura: FoodCaptureViewModel = hiltViewModel(grafo),
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
        onPhoto = {
            captura.onPhotoCaptureStarted()
            onPhoto()
        },
        onSearch = { mostrarBuscador = true },
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
