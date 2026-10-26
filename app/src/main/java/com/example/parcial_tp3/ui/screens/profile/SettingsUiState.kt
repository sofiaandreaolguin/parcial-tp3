package com.example.parcial_tp3.ui.screens.profile

data class SettingsUiState(
    val fullName: String,
    val email: String,
    val loggedOut: Boolean = false,
)
