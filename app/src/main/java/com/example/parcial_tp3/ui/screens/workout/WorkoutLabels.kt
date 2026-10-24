package com.example.parcial_tp3.ui.screens.workout

import androidx.annotation.StringRes
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.WorkoutLevel

/** Etiqueta del nivel en mayúsculas, la del panel verde de Workout / 01. */
@StringRes
fun WorkoutLevel.tagLabel(): Int = when (this) {
    WorkoutLevel.BEGINNER -> R.string.workout_level_beginner_tag
    WorkoutLevel.INTERMEDIATE -> R.string.workout_level_intermediate_tag
    WorkoutLevel.ADVANCED -> R.string.workout_level_advanced_tag
}

/** Nivel con mayúscula inicial, el del resumen de las tarjetas de Workout / 02. */
@StringRes
fun WorkoutLevel.label(): Int = when (this) {
    WorkoutLevel.BEGINNER -> R.string.workout_level_beginner
    WorkoutLevel.INTERMEDIATE -> R.string.workout_level_intermediate
    WorkoutLevel.ADVANCED -> R.string.workout_level_advanced
}

/** Las cuatro dificultades de Workout / 07 ("Great / Good / Tough / Too Easy"). */
@StringRes
fun WorkoutRating.label(): Int = when (this) {
    WorkoutRating.GREAT -> R.string.complete_rate_great
    WorkoutRating.GOOD -> R.string.complete_rate_good
    WorkoutRating.TOUGH -> R.string.complete_rate_tough
    WorkoutRating.TOO_EASY -> R.string.complete_rate_too_easy
}
