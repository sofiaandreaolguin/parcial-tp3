package com.example.parcial_tp3.ui.screens.nutrition

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Nutrition / 03  no muestra datos que cambien por ahora: solo navega. estado se pone por si en alguna pantalla a futuro se llega a necesitar */
data object AddFoodUiState

/** Controlador de Nutrition / 03. El estado está vacío a propósito; por ahora no hay uno y se llena si la tarea l o alguna a futuro lo llegara a necesitar. */
@HiltViewModel
class AddFoodViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AddFoodUiState)
    val uiState: StateFlow<AddFoodUiState> = _uiState.asStateFlow()
}
