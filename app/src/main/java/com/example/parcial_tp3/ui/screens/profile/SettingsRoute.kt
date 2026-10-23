package com.example.parcial_tp3.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial_tp3.ui.components.rememberDemoToast

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onLogOut: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val demo = rememberDemoToast()
    SettingsScreen(
        state = state,
        onBack = onBack,
        onEditProfile = onEditProfile,
        onLogOut = onLogOut,
        onDemo = demo,
    )
}
