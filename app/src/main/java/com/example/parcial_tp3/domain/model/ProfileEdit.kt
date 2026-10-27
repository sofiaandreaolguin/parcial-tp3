package com.example.parcial_tp3.domain.model

import java.time.LocalDate

/** Los 6 campos que escribe Edit Profile (C19). Nada mas va a Firestore desde esa pantalla. */
data class ProfileEdit(
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val heightCm: Int,
    val weightKg: Int,
    val gender: Gender,
)
