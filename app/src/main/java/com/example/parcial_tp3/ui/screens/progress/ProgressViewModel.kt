package com.example.parcial_tp3.ui.screens.progress

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Controlador de las 6 pantallas de Progress. Datos de demostración, sin repositorio. */
@HiltViewModel
class ProgressViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(progressDemoState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()
}
