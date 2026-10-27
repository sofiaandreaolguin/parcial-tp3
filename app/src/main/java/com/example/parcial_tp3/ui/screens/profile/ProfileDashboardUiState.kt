package com.example.parcial_tp3.ui.screens.profile

import androidx.annotation.StringRes
import com.example.parcial_tp3.domain.model.Experience
import com.example.parcial_tp3.domain.model.Goal
import com.example.parcial_tp3.domain.model.TrainingDays
import com.example.parcial_tp3.domain.model.WorkoutType

data class ProfileDashboardUiState(
    val fullName: String,
    val goals: List<Goal>,
    val experience: Experience,
    val trainingDays: TrainingDays,
    val workoutType: WorkoutType,
    /** La lectura fallo sin cache: mensaje de strings.xml con reintento (C24). */
    @StringRes val loadError: Int? = null,
)
