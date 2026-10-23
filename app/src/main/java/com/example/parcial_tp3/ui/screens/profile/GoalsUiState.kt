package com.example.parcial_tp3.ui.screens.profile

import com.example.parcial_tp3.domain.model.CoachingStyle
import com.example.parcial_tp3.domain.model.Goal
import com.example.parcial_tp3.domain.model.TrainingDays
import com.example.parcial_tp3.domain.model.WorkoutType

data class GoalsUiState(
    val goals: Set<Goal>,
    val trainingDays: TrainingDays,
    val workoutType: WorkoutType,
    val coachingStyle: CoachingStyle,
)
