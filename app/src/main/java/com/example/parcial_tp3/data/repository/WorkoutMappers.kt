package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.remote.dto.WorkoutDto
import com.example.parcial_tp3.domain.model.Workout
import com.example.parcial_tp3.domain.model.WorkoutLevel

// Firma vacia (tests en rojo): devuelve un valor fijo y no lee el DTO. La logica la escribe la
// skill implementar.
fun WorkoutDto.toDomain(): Workout =
    Workout(id = "", name = "", level = WorkoutLevel.BEGINNER, durationMinutes = 0, exerciseCount = 0)
