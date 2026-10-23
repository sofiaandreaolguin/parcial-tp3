package com.example.parcial_tp3.ui.screens.profile

import androidx.annotation.StringRes
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.CoachingStyle
import com.example.parcial_tp3.domain.model.Experience
import com.example.parcial_tp3.domain.model.Gender
import com.example.parcial_tp3.domain.model.Goal
import com.example.parcial_tp3.domain.model.TrainingDays
import com.example.parcial_tp3.domain.model.WorkoutType

// Texto de cada opción del perfil, para usar con stringResource.
@StringRes
fun Gender.label(): Int = when (this) {
    Gender.FEMALE -> R.string.gender_female
    Gender.MALE -> R.string.gender_male
    Gender.NON_BINARY -> R.string.gender_non_binary
    Gender.PREFER_NOT_TO_SAY -> R.string.gender_prefer_not_to_say
}

@StringRes
fun Goal.label(): Int = when (this) {
    Goal.BUILD_STRENGTH -> R.string.goal_build_strength
    Goal.IMPROVE_FITNESS -> R.string.goal_improve_fitness
    Goal.LOSE_BODY_FAT -> R.string.goal_lose_body_fat
    Goal.BUILD_HEALTHY_HABITS -> R.string.goal_build_healthy_habits
}

@StringRes
fun Experience.label(): Int = when (this) {
    Experience.BEGINNER -> R.string.experience_beginner
    Experience.INTERMEDIATE -> R.string.experience_intermediate
    Experience.ADVANCED -> R.string.experience_advanced
}

@StringRes
fun TrainingDays.label(): Int = when (this) {
    TrainingDays.THREE -> R.string.training_days_three
    TrainingDays.FOUR -> R.string.training_days_four
    TrainingDays.FIVE -> R.string.training_days_five
    TrainingDays.SIX_PLUS -> R.string.training_days_six_plus
}

@StringRes
fun WorkoutType.label(): Int = when (this) {
    WorkoutType.STRENGTH -> R.string.workout_type_strength
    WorkoutType.CARDIO -> R.string.workout_type_cardio
    WorkoutType.HIIT -> R.string.workout_type_hiit
    WorkoutType.MOBILITY -> R.string.workout_type_mobility
    WorkoutType.MIXED -> R.string.workout_type_mixed
}

@StringRes
fun CoachingStyle.label(): Int = when (this) {
    CoachingStyle.SUPPORTIVE -> R.string.coaching_supportive
    CoachingStyle.BALANCED -> R.string.coaching_balanced
    CoachingStyle.DIRECT -> R.string.coaching_direct
}
