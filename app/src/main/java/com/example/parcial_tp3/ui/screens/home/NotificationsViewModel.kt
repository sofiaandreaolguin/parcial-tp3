package com.example.parcial_tp3.ui.screens.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Home / 02. Las notificaciones son de demostracion y viven en memoria: "Mark all as read" y
 * abrir una las marcan como leidas mientras la pantalla siga en la pila. Sobreviven a la
 * rotacion porque el estado es del ViewModel (RF07); si se sale con la pestana Home y se
 * vuelve con la campana, el ViewModel es nuevo y vuelven las no leidas.
 */
@HiltViewModel
class NotificationsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState(NotificationsDemoData.NOTIFICATIONS))
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    fun markAllAsRead() = _uiState.update { state ->
        state.copy(notifications = state.notifications.map { it.copy(unread = false) })
    }

    /** Tocar una notificacion la marca como leida; la Route navega a su destino. */
    fun onOpen(id: String) = _uiState.update { state ->
        state.copy(notifications = state.notifications.map { if (it.id == id) it.copy(unread = false) else it })
    }
}
