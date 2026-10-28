package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial_tp3.ui.components.rememberDemoToast

/**
 * El ViewModel es de esta pantalla (no del grafo): lee el mealId de la ruta con su SavedStateHandle
 * (NAV04). Al eliminar o duplicar, vuelve. "Edit Meal" es de demostración: la edición quedó fuera
 * de alcance (spec carga-y-eliminacion-de-comidas, PR 2 de ingredientes).
 */
@Composable
fun MealDetailRoute(
    onBack: () -> Unit,
    viewModel: MealDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnBack by rememberUpdatedState(onBack)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                MealDetailEvent.Deleted, MealDetailEvent.Duplicated -> currentOnBack()
            }
        }
    }

    MealDetailScreen(
        state = state,
        onBack = onBack,
        onEdit = rememberDemoToast(),
        onDuplicate = viewModel::onDuplicate,
        onDeleteClick = viewModel::onDeleteClick,
        onConfirmDelete = viewModel::onConfirmDelete,
        onDismissDelete = viewModel::onDismissDelete,
    )
}
