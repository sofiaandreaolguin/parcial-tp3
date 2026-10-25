package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial_tp3.domain.model.Meal

/**
 * El ViewModel es de esta pantalla (no del grafo): lee el mealId de la ruta con su SavedStateHandle
 * (NAV04). Al eliminar o duplicar, vuelve.
 *
 * @param onEdit recibe la comida para que el grafo abra 07 con ella antes de navegar (CONTEXTO §2).
 */
@Composable
fun MealDetailRoute(
    onBack: () -> Unit,
    onEdit: (Meal) -> Unit,
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
        onEdit = { (state as? MealDetailUiState.Content)?.let { onEdit(it.meal) } },
        onDuplicate = viewModel::onDuplicate,
        onDeleteClick = viewModel::onDeleteClick,
        onConfirmDelete = viewModel::onConfirmDelete,
        onDismissDelete = viewModel::onDismissDelete,
    )
}
