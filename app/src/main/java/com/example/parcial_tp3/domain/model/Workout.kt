package com.example.parcial_tp3.domain.model

/** Un entrenamiento del catalogo (el resumen, sin ejercicios). */
data class Workout(
    val id: String,
    val name: String,
    val level: WorkoutLevel,
    val durationMinutes: Int,
    val exerciseCount: Int,
)

enum class WorkoutLevel { BEGINNER, INTERMEDIATE, ADVANCED }
