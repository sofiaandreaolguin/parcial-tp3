package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.remote.dto.WorkoutDto
import com.example.parcial_tp3.domain.model.Workout
import com.example.parcial_tp3.domain.model.WorkoutLevel

// Un campo nulo o un nivel desconocido lanza IllegalArgumentException: no se inventa un valor y el
// repositorio hace fallar toda la consulta.
fun WorkoutDto.toDomain(): Workout =
    Workout(
        id = requireNotNull(id) { "WorkoutDto sin id" },
        name = requireNotNull(name) { "WorkoutDto sin name" },
        level = requireNotNull(level) { "WorkoutDto sin level" }.toWorkoutLevel(),
        durationMinutes = requireNotNull(durationMinutes) { "WorkoutDto sin durationMinutes" },
        exerciseCount = requireNotNull(exerciseCount) { "WorkoutDto sin exerciseCount" },
    )

// Exactamente en minusculas, como el contrato de specs/API.md: "Beginner" es desconocido.
private fun String.toWorkoutLevel(): WorkoutLevel =
    when (this) {
        "beginner" -> WorkoutLevel.BEGINNER
        "intermediate" -> WorkoutLevel.INTERMEDIATE
        "advanced" -> WorkoutLevel.ADVANCED
        else -> throw IllegalArgumentException("Nivel desconocido: $this")
    }
