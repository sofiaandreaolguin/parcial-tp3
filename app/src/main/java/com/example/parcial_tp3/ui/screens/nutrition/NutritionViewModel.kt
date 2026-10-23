package com.example.parcial_tp3.ui.screens.nutrition

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Controlador de Nutrition / 01 y 02. Datos de demostración, sin repositorio ni Room. */
@HiltViewModel
class NutritionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(nutritionDemoState())
    val uiState: StateFlow<NutritionUiState> = _uiState.asStateFlow()
}
