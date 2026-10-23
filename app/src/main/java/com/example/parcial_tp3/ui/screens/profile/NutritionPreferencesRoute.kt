package com.example.parcial_tp3.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial_tp3.ui.components.rememberDemoToast

@Composable
fun NutritionPreferencesRoute(
    onBack: () -> Unit,
    viewModel: NutritionPreferencesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val demo = rememberDemoToast()
    NutritionPreferencesScreen(
        state = state,
        onBack = onBack,
        onSave = { demo(); onBack() },
        onDemo = demo,
    )
}
