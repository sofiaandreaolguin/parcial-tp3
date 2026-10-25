package com.example.parcial_tp3.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.data.repository.AuthRepository
import com.example.parcial_tp3.data.repository.ProfileRepository
import com.example.parcial_tp3.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val auth: AuthRepository,
) : ViewModel() {

    private val loggedOut = MutableStateFlow(false)

    /** "Log out": cierra la sesion, vacia el perfil y pide navegar a SignIn (C15). */
    fun logOut() {
        auth.signOut()
        repository.clear()
        loggedOut.value = true
    }

    val uiState: StateFlow<SettingsUiState> = combine(repository.profile, loggedOut) { profile, out ->
        profile.toUiState(out)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = repository.profile.value.toUiState(loggedOut.value),
    )

    private fun Profile.toUiState(loggedOut: Boolean) = SettingsUiState(
        fullName = "$firstName $lastName",
        email = email,
        loggedOut = loggedOut,
    )
}
