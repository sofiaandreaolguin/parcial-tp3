package com.example.parcial_tp3.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileDashboardRoute(
    onSettings: () -> Unit,
    onEditProfile: () -> Unit,
    onReviewPreferences: () -> Unit,
    onGoals: () -> Unit,
    onPreferences: () -> Unit,
    viewModel: ProfileDashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProfileDashboardScreen(
        state = state,
        onSettings = onSettings,
        onEditProfile = onEditProfile,
        onReviewPreferences = onReviewPreferences,
        onGoals = onGoals,
        onPreferences = onPreferences,
        onDemo = rememberDemoToast(),
    )
}
