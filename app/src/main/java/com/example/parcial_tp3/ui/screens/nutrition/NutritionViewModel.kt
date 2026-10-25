package com.example.parcial_tp3.ui.screens.nutrition

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.parcial_tp3.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Controlador de Nutrition / 01 y 02: las comidas de Room de la fecha elegida y su total.
 * Se pide atado al grafo de Nutrition, así 01 y 02 comparten la fecha.
 */
@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val repo: MealRepository,
    private val savedState: SavedStateHandle,
    private val clock: Clock,
) : ViewModel() {

    // Firmas vacías de la tarea VC-l: las completa la sesión de implementar.
    val uiState: StateFlow<NutritionUiState> = MutableStateFlow<NutritionUiState>(NutritionUiState.Loading).asStateFlow()

    val selectedDate: StateFlow<LocalDate> = MutableStateFlow(LocalDate.EPOCH).asStateFlow()

    @Suppress("UNUSED_PARAMETER")
    fun onDateSelected(date: LocalDate) = Unit

    fun onRetry() = Unit

    companion object {
        /** La fecha elegida, como texto ISO, para que sobreviva a la muerte del proceso. */
        const val KEY_SELECTED_DATE = "selectedDate"
    }
}
