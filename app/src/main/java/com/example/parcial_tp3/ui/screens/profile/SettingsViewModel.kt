package com.example.parcial_tp3.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.data.repository.AuthRepository
import com.example.parcial_tp3.data.repository.ProfileRepository
import com.example.parcial_tp3.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SettingsViewModel @Inject constructor(
    repository: ProfileRepository,
) : ViewModel() {

    constructor(repository: ProfileRepository, auth: AuthRepository) : this(repository)

    /** "Log out": cierra la sesion, vacia el perfil y pide navegar a SignIn (C15). */
    fun logOut() {}

    val uiState: StateFlow<SettingsUiState> = repository.profile
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = repository.profile.value.toUiState(),
        )

    private fun Profile.toUiState() = SettingsUiState(
        fullName = "$firstName $lastName",
        email = email,
    )
}
