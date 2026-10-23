package com.example.parcial_tp3.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** @param onDestination navegar a donde lleva la notificacion: lo resuelve el grafo. */
@Composable
fun NotificationsRoute(
    onDestination: (NotificationDestination) -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    NotificationsScreen(
        state = state,
        onMarkAllAsRead = viewModel::markAllAsRead,
        onOpen = { item ->
            viewModel.onOpen(item.id)
            onDestination(item.destination)
        },
    )
}
