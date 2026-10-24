package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import com.example.parcial_tp3.ui.components.rememberDemoToast

@Composable
fun AddFoodRoute(
    onBack: () -> Unit,
    onVoice: () -> Unit,
    onPhoto: () -> Unit,
    onManual: () -> Unit,
) {
    AddFoodScreen(
        onBack = onBack,
        onVoice = onVoice,
        onPhoto = onPhoto,
        onSearch = rememberDemoToast(),
        onManual = onManual,
    )
}
