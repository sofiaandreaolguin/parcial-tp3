package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.domain.model.Exercise
import com.example.parcial_tp3.domain.model.WorkoutDetail
import com.example.parcial_tp3.domain.model.WorkoutLevel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Detalle de demostracion, hasta que exista GET /v1/workouts/{workoutId}. Los datos son los de
 * fixtures.json del mock del profe (de ahi arma server.py el detalle). La tarea del detalle real
 * borra esta clase y su test, y cambia el @Binds de RepositoryModule.
 */
@Singleton
class DemoWorkoutDetailRepository @Inject constructor() : WorkoutDetailRepository {

    override suspend fun getWorkoutDetail(id: String): Result<WorkoutDetail> =
        DETALLES[id]?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("Entrenamiento no encontrado: $id"))

    private companion object {
        fun ejercicio(id: String, name: String, muscleGroup: String, instructions: String) = Exercise(
            id = id,
            name = name,
            muscleGroup = muscleGroup,
            sets = 3,
            reps = 10,
            restSeconds = 60,
            instructions = instructions,
            tip = "Usá una carga que puedas controlar.",
        )

        val E01 = ejercicio("e01", "Sentadilla", "Piernas", "Separá los pies y flexioná las rodillas con control.")
        val E02 = ejercicio("e02", "Remo con mancuerna", "Espalda", "Apoyá una mano y llevá la mancuerna hacia el torso.")
        val E03 = ejercicio("e03", "Press con mancuernas", "Pecho", "Empujá las mancuernas con un movimiento controlado.")
        val E04 = ejercicio("e04", "Puente de glúteos", "Piernas", "Elevá la cadera sin arquear la espalda.")
        val E05 = ejercicio("e05", "Press de hombros", "Hombros", "Empujá hacia arriba manteniendo una postura estable.")
        val E06 = ejercicio("e06", "Peso muerto con mancuernas", "Piernas", "Llevá la cadera hacia atrás con las mancuernas cerca del cuerpo.")

        fun detalle(id: String, name: String, level: WorkoutLevel, durationMinutes: Int, exercises: List<Exercise>) =
            WorkoutDetail(
                id = id,
                name = name,
                level = level,
                durationMinutes = durationMinutes,
                exerciseCount = 3,
                description = "Rutina de ejemplo para el trabajo práctico.",
                exercises = exercises,
            )

        val DETALLES = listOf(
            detalle("w01", "Fuerza inicial", WorkoutLevel.BEGINNER, 25, listOf(E01, E02, E03)),
            detalle("w02", "Entrenamiento completo", WorkoutLevel.INTERMEDIATE, 35, listOf(E04, E05, E06)),
            detalle("w03", "Fuerza y constancia", WorkoutLevel.BEGINNER, 30, listOf(E01, E04, E02)),
        ).associateBy { it.id }
    }
}
