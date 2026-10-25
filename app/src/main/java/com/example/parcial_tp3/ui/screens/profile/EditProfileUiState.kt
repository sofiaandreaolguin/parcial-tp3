package com.example.parcial_tp3.ui.screens.profile

import androidx.annotation.StringRes
import com.example.parcial_tp3.domain.model.Gender
import java.time.LocalDate

data class EditProfileUiState(
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val heightCm: Int,
    val weightKg: Int,
    val gender: Gender,
    val isSaving: Boolean = false,
    /** Guardado pedido: la Route vuelve a Profile / 01 (C19). */
    val saved: Boolean = false,
    @StringRes val firstNameError: Int? = null,
    @StringRes val birthDateError: Int? = null,
    @StringRes val heightError: Int? = null,
    @StringRes val weightError: Int? = null,
)
