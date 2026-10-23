package com.example.parcial_tp3.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeRoute(
    onNotifications: () -> Unit,
    onVoice: () -> Unit,
    onStartMyWorkout: () -> Unit,
    onLogBreakfast: () -> Unit,
    onOverview: () -> Unit,
    onStartWorkout: (workoutId: String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onNotifications = onNotifications,
        onVoice = onVoice,
        onStartMyWorkout = onStartMyWorkout,
        onLogBreakfast = onLogBreakfast,
        onOverview = onOverview,
        onStartWorkout = onStartWorkout,
    )
}
