package com.example.parcial_tp3.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial_tp3.ui.components.rememberDemoToast

@Composable
fun VoiceSettingsRoute(
    onBack: () -> Unit,
    onPrivacy: () -> Unit,
    viewModel: VoiceSettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    VoiceSettingsScreen(
        state = state,
        onBack = onBack,
        onPrivacy = onPrivacy,
        onDemo = rememberDemoToast(),
    )
}
