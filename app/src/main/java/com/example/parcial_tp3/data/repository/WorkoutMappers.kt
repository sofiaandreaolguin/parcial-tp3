package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.entity.WorkoutEntity
import com.example.parcial_tp3.data.local.entity.WorkoutExerciseEntity
import com.example.parcial_tp3.data.remote.dto.ExerciseDto
import com.example.parcial_tp3.data.remote.dto.WorkoutDetailDto
import com.example.parcial_tp3.data.remote.dto.WorkoutDto
import com.example.parcial_tp3.domain.model.Exercise
import com.example.parcial_tp3.domain.model.Workout
import com.example.parcial_tp3.domain.model.WorkoutDetail
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

// Firmas vacias de la tarea del detalle y la cache: las completa la sesion de implementar. Devuelven
// un valor vacio y no lanzan, para que los tests fallen por el resultado.
private val EJERCICIO_VACIO = Exercise("", "", "", 0, 0, 0, "", "")
private val DETALLE_VACIO = WorkoutDetail("", "", WorkoutLevel.BEGINNER, 0, 0, "", emptyList())
private val ENTITY_VACIA = WorkoutEntity("", "", "", 0, 0)

fun WorkoutDetailDto.toDomain(): WorkoutDetail = DETALLE_VACIO

fun ExerciseDto.toDomain(): Exercise = EJERCICIO_VACIO

fun Workout.toEntity(): WorkoutEntity = ENTITY_VACIA

fun WorkoutEntity.toDomain(): Workout = Workout("", "", WorkoutLevel.BEGINNER, 0, 0)

fun WorkoutDetail.toEntity(): WorkoutEntity = ENTITY_VACIA

fun Exercise.toEntity(workoutId: String, position: Int): WorkoutExerciseEntity =
    WorkoutExerciseEntity("", 0, "", "", "", 0, 0, 0, "", "")

fun WorkoutExerciseEntity.toDomain(): Exercise = EJERCICIO_VACIO

fun WorkoutEntity.toDomain(exercises: List<WorkoutExerciseEntity>): WorkoutDetail = DETALLE_VACIO

// Exactamente en minusculas, como el contrato de specs/API.md: "Beginner" es desconocido.
private fun String.toWorkoutLevel(): WorkoutLevel =
    when (this) {
        "beginner" -> WorkoutLevel.BEGINNER
        "intermediate" -> WorkoutLevel.INTERMEDIATE
        "advanced" -> WorkoutLevel.ADVANCED
        else -> throw IllegalArgumentException("Nivel desconocido: $this")
    }
