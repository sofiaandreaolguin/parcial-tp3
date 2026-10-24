package com.example.parcial_tp3.ui.screens.nutrition

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Controlador de Nutrition / 04 a 07. Se pide con el back stack entry del grafo de Nutrition para
 * que el borrador sobreviva de una pantalla a la siguiente. No graba ni pide micrófono (NAV05).
 *
 * Los macros del borrador quedan en 0 hasta que 07 los dibuje; el PR de 05 suma el avance solo.
 */
@HiltViewModel
class FoodCaptureViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(FoodCaptureUiState())
    val uiState: StateFlow<FoodCaptureUiState> = _uiState.asStateFlow()
}
