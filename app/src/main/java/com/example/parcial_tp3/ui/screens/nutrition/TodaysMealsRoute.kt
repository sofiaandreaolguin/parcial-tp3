package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry

/** @param grafo la entrada del grafo de Nutrition: el ViewModel es el de 01 y lista la fecha elegida ahí. */
@Composable
fun TodaysMealsRoute(
    grafo: NavBackStackEntry,
    onMeal: (mealId: Long) -> Unit,
    onAddMeal: () -> Unit,
    viewModel: NutritionViewModel = hiltViewModel(grafo),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TodaysMealsScreen(state = state, onRetry = viewModel::onRetry, onMeal = onMeal, onAddMeal = onAddMeal)
}
