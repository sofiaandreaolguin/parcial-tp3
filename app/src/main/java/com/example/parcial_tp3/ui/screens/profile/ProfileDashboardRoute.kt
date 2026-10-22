package com.example.parcial_tp3.ui.screens.profile

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial_tp3.R

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
    val context = LocalContext.current
    ProfileDashboardScreen(
        state = state,
        onSettings = onSettings,
        onEditProfile = onEditProfile,
        onReviewPreferences = onReviewPreferences,
        onGoals = onGoals,
        onPreferences = onPreferences,
        onDemo = { Toast.makeText(context, R.string.aviso_demo, Toast.LENGTH_SHORT).show() },
    )
}
