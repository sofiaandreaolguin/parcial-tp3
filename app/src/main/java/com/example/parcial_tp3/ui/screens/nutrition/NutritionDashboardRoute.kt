package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.parcial_tp3.ui.components.rememberDemoToast

@Composable
fun NutritionDashboardRoute(
    onHistory: () -> Unit,
    onMeal: (mealId: Long) -> Unit,
    onAddDinner: () -> Unit,
    onVoice: () -> Unit,
    onPhoto: () -> Unit,
    viewModel: NutritionViewModel = hiltViewModel(),
) {
    // Hasta el paso implementar de VC-l las pantallas siguen con la demostración.
    @Suppress("UNUSED_VARIABLE") val vm = viewModel
    val state = nutritionDemoState()
    val showDemoNotice = rememberDemoToast()
    NutritionDashboardScreen(
        state = state,
        onCalendar = showDemoNotice,
        onHistory = onHistory,
        onMeal = onMeal,
        onAddDinner = onAddDinner,
        onVoice = onVoice,
        onPhoto = onPhoto,
        onSearch = showDemoNotice,
    )
}
