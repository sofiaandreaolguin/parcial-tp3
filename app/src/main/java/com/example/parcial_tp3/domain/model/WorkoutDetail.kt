package com.example.parcial_tp3.domain.model

/** Un entrenamiento con sus ejercicios, con la forma de GET /v1/workouts/{workoutId}. */
data class WorkoutDetail(
    val id: String,
    val name: String,
    val level: WorkoutLevel,
    val durationMinutes: Int,
    val exerciseCount: Int,
    val description: String,
    val exercises: List<Exercise>,
)

data class Exercise(
    val id: String,
    val name: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: Int,
    val restSeconds: Int,
    val instructions: String,
    val tip: String,
)
