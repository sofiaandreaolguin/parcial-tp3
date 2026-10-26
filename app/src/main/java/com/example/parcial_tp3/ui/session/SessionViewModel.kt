package com.example.parcial_tp3.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.data.repository.AuthRepository
import com.example.parcial_tp3.data.repository.ProfileRepository
import com.example.parcial_tp3.data.repository.SessionStarter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SessionState { Restoring, Active, Invalid }

/**
 * La sesion de Firebase durante la vida de la app. Lee el perfil una sola vez al arrancar con sesion
 * (rotar no lo repite, RF07) y cierra la sesion si el servidor deja de aceptarla (C35).
 */
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val starter: SessionStarter,
    private val profiles: ProfileRepository,
) : ViewModel() {

    /** Leido de forma sincrona para elegir el destino inicial antes de componer el NavHost (C13, C14). */
    val hasSession: Boolean = auth.currentUid != null

    private val _state = MutableStateFlow(if (hasSession) SessionState.Restoring else SessionState.Active)
    val state: StateFlow<SessionState> = _state.asStateFlow()

    init {
        if (hasSession) {
            viewModelScope.launch {
                _state.value = if (starter.restore()) SessionState.Active else SessionState.Invalid
            }
        }
        viewModelScope.launch {
            profiles.sessionExpired.collect {
                auth.signOut()
                profiles.clear()
                _state.value = SessionState.Invalid
            }
        }
    }

    /** La navegacion ya llevo a SignIn: se baja la marca para que no se repita al rotar. */
    fun onInvalidHandled() {
        _state.value = SessionState.Active
    }
}
