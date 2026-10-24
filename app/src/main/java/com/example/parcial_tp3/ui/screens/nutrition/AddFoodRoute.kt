package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.parcial_tp3.ui.components.rememberDemoToast

@Composable
fun AddFoodRoute(
    onBack: () -> Unit,
    onVoice: () -> Unit,
    onPhoto: () -> Unit,
    onManual: () -> Unit,
    @Suppress("UNUSED_PARAMETER") viewModel: AddFoodViewModel = hiltViewModel(),
) {
    AddFoodScreen(
        onBack = onBack,
        onVoice = onVoice,
        onPhoto = onPhoto,
        onSearch = rememberDemoToast(),
        onManual = onManual,
    )
}
