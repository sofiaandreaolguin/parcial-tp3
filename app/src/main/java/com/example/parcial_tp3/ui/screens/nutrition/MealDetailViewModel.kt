package com.example.parcial_tp3.ui.screens.nutrition

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.parcial_tp3.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

/** Controlador de Nutrition / 08: la comida de Rutas.ARG_MEAL_ID, con eliminar y duplicar. */
@HiltViewModel
class MealDetailViewModel @Inject constructor(
    private val repo: MealRepository,
    private val savedState: SavedStateHandle,
) : ViewModel() {

    // Firmas vacías de la tarea VC-l: las completa la sesión de implementar.
    val uiState: StateFlow<MealDetailUiState> = MutableStateFlow<MealDetailUiState>(MealDetailUiState.Loading).asStateFlow()

    val events: Flow<MealDetailEvent> = emptyFlow()

    fun onDeleteClick() = Unit

    fun onDismissDelete() = Unit

    fun onConfirmDelete() = Unit

    fun onDuplicate() = Unit
}
