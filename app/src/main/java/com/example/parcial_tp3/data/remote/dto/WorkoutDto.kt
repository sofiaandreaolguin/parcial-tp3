package com.example.parcial_tp3.data.remote.dto

/**
 * Forma del JSON de WorkoutSummary en GET /v1/workouts. Todo admite nulo: Gson no respeta la
 * nulabilidad de Kotlin, y asi un campo faltante se detecta en el mapper.
 */
data class WorkoutDto(
    val id: String?,
    val name: String?,
    val level: String?,
    val durationMinutes: Int?,
    val exerciseCount: Int?,
)
