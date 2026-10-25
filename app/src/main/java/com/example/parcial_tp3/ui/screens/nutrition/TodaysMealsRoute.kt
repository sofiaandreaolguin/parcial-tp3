package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun TodaysMealsRoute(
    onMeal: (mealId: Long) -> Unit,
    onAddDinner: () -> Unit,
    viewModel: NutritionViewModel = hiltViewModel(),
) {
    // Hasta el paso implementar de VC-l las pantallas siguen con la demostración.
    @Suppress("UNUSED_VARIABLE") val vm = viewModel
    val state = nutritionDemoState()
    TodaysMealsScreen(state = state, onMeal = onMeal, onAddDinner = onAddDinner)
}
