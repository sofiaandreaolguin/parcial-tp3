package com.example.parcial_tp3.ui.screens.profile

import com.example.parcial_tp3.domain.model.Gender
import java.time.LocalDate

data class EditProfileUiState(
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val heightCm: Int,
    val weightKg: Int,
    val gender: Gender,
)
