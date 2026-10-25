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

fun WorkoutDetailDto.toDomain(): WorkoutDetail =
    WorkoutDetail(
        id = requireNotNull(id) { "WorkoutDetailDto sin id" },
        name = requireNotNull(name) { "WorkoutDetailDto sin name" },
        level = requireNotNull(level) { "WorkoutDetailDto sin level" }.toWorkoutLevel(),
        durationMinutes = requireNotNull(durationMinutes) { "WorkoutDetailDto sin durationMinutes" },
        exerciseCount = requireNotNull(exerciseCount) { "WorkoutDetailDto sin exerciseCount" },
        description = requireNotNull(description) { "WorkoutDetailDto sin description" },
        exercises = requireNotNull(exercises) { "WorkoutDetailDto sin exercises" }.map { it.toDomain() },
    )

fun ExerciseDto.toDomain(): Exercise =
    Exercise(
        id = requireNotNull(id) { "ExerciseDto sin id" },
        name = requireNotNull(name) { "ExerciseDto sin name" },
        muscleGroup = requireNotNull(muscleGroup) { "ExerciseDto sin muscleGroup" },
        sets = requireNotNull(sets) { "ExerciseDto sin sets" },
        reps = requireNotNull(reps) { "ExerciseDto sin reps" },
        restSeconds = requireNotNull(restSeconds) { "ExerciseDto sin restSeconds" },
        instructions = requireNotNull(instructions) { "ExerciseDto sin instructions" },
        tip = requireNotNull(tip) { "ExerciseDto sin tip" },
    )

// Desde el catalogo no llega description: queda nulo, que quiere decir "detalle nunca descargado".
fun Workout.toEntity(): WorkoutEntity =
    WorkoutEntity(
        id = id,
        name = name,
        level = level.toApiValue(),
        durationMinutes = durationMinutes,
        exerciseCount = exerciseCount,
        description = null,
    )

fun WorkoutEntity.toDomain(): Workout =
    Workout(
        id = id,
        name = name,
        level = level.toWorkoutLevel(),
        durationMinutes = durationMinutes,
        exerciseCount = exerciseCount,
    )

fun WorkoutDetail.toEntity(): WorkoutEntity =
    WorkoutEntity(
        id = id,
        name = name,
        level = level.toApiValue(),
        durationMinutes = durationMinutes,
        exerciseCount = exerciseCount,
        description = description,
    )

fun Exercise.toEntity(workoutId: String, position: Int): WorkoutExerciseEntity =
    WorkoutExerciseEntity(
        workoutId = workoutId,
        position = position,
        exerciseId = id,
        name = name,
        muscleGroup = muscleGroup,
        sets = sets,
        reps = reps,
        restSeconds = restSeconds,
        instructions = instructions,
        tip = tip,
    )

fun WorkoutExerciseEntity.toDomain(): Exercise =
    Exercise(
        id = exerciseId,
        name = name,
        muscleGroup = muscleGroup,
        sets = sets,
        reps = reps,
        restSeconds = restSeconds,
        instructions = instructions,
        tip = tip,
    )

// Una fila que solo vino del catalogo (description nulo) no es un detalle.
fun WorkoutEntity.toDomain(exercises: List<WorkoutExerciseEntity>): WorkoutDetail =
    WorkoutDetail(
        id = id,
        name = name,
        level = level.toWorkoutLevel(),
        durationMinutes = durationMinutes,
        exerciseCount = exerciseCount,
        description = requireNotNull(description) { "La fila $id no tiene el detalle descargado" },
        exercises = exercises.map { it.toDomain() },
    )

// La Entity guarda el nivel como el contrato, asi se lee con el mismo toWorkoutLevel().
private fun WorkoutLevel.toApiValue(): String =
    when (this) {
        WorkoutLevel.BEGINNER -> "beginner"
        WorkoutLevel.INTERMEDIATE -> "intermediate"
        WorkoutLevel.ADVANCED -> "advanced"
    }

// Exactamente en minusculas, como el contrato de specs/API.md: "Beginner" es desconocido.
private fun String.toWorkoutLevel(): WorkoutLevel =
    when (this) {
        "beginner" -> WorkoutLevel.BEGINNER
        "intermediate" -> WorkoutLevel.INTERMEDIATE
        "advanced" -> WorkoutLevel.ADVANCED
        else -> throw IllegalArgumentException("Nivel desconocido: $this")
    }
