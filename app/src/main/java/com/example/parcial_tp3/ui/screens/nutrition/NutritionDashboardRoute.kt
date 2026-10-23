package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val state by viewModel.uiState.collectAsStateWithLifecycle()
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
