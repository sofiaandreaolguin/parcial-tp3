package com.example.parcial_tp3.data.remote.dto

/**
 * Forma del JSON de GET /v1/workouts/{workoutId} (specs/API.md). Todo admite nulo, como en
 * WorkoutDto: Gson no respeta la nulabilidad de Kotlin y un campo faltante se detecta en el mapper.
 */
data class WorkoutDetailDto(
    val id: String?,
    val name: String?,
    val level: String?,
    val durationMinutes: Int?,
    val exerciseCount: Int?,
    val description: String?,
    val exercises: List<ExerciseDto>?,
)

data class ExerciseDto(
    val id: String?,
    val name: String?,
    val muscleGroup: String?,
    val sets: Int?,
    val reps: Int?,
    val restSeconds: Int?,
    val instructions: String?,
    val tip: String?,
)
