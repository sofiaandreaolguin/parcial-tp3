package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.domain.model.Exercise
import com.example.parcial_tp3.domain.model.WorkoutDetail
import com.example.parcial_tp3.domain.model.WorkoutLevel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Detalle de demostracion (NAV04, RF01): cada id devuelve lo suyo, copiado de fixtures.json del
 * mock. Se borra junto con DemoWorkoutDetailRepository cuando llegue el detalle real.
 * Spec: specs/funcionales/detalle-de-entrenamiento-demo.md.
 */
class DemoWorkoutDetailRepositoryTest {

    private val repository = DemoWorkoutDetailRepository()

    private fun ejercicio(id: String, name: String, muscleGroup: String, instructions: String) = Exercise(
        id = id, name = name, muscleGroup = muscleGroup, sets = 3, reps = 10, restSeconds = 60,
        instructions = instructions, tip = "Usá una carga que puedas controlar.",
    )

    private val e01 = ejercicio("e01", "Sentadilla", "Piernas", "Separá los pies y flexioná las rodillas con control.")
    private val e02 = ejercicio("e02", "Remo con mancuerna", "Espalda", "Apoyá una mano y llevá la mancuerna hacia el torso.")
    private val e03 = ejercicio("e03", "Press con mancuernas", "Pecho", "Empujá las mancuernas con un movimiento controlado.")
    private val e04 = ejercicio("e04", "Puente de glúteos", "Piernas", "Elevá la cadera sin arquear la espalda.")
    private val e05 = ejercicio("e05", "Press de hombros", "Hombros", "Empujá hacia arriba manteniendo una postura estable.")
    private val e06 = ejercicio("e06", "Peso muerto con mancuernas", "Piernas", "Llevá la cadera hacia atrás con las mancuernas cerca del cuerpo.")

    private fun detalle(id: String, name: String, level: WorkoutLevel, minutos: Int, ejercicios: List<Exercise>) =
        WorkoutDetail(
            id = id, name = name, level = level, durationMinutes = minutos, exerciseCount = 3,
            description = "Rutina de ejemplo para el trabajo práctico.", exercises = ejercicios,
        )

    private val esperados = listOf(
        detalle("w01", "Fuerza inicial", WorkoutLevel.BEGINNER, 25, listOf(e01, e02, e03)),
        detalle("w02", "Entrenamiento completo", WorkoutLevel.INTERMEDIATE, 35, listOf(e04, e05, e06)),
        detalle("w03", "Fuerza y constancia", WorkoutLevel.BEGINNER, 30, listOf(e01, e04, e02)),
    )

    @Test
    fun `D1 detalle demo devuelve w01 w02 y w03 cada uno con lo suyo`() = runTest {
        for (esperado in esperados) {
            val resultado = repository.getWorkoutDetail(esperado.id)

            assertTrue("${esperado.id} tiene que ser exito, fue $resultado", resultado.isSuccess)
            assertEquals(esperado, resultado.getOrNull())
        }
    }

    @Test
    fun `D2 detalle demo exerciseCount coincide con la cantidad de ejercicios`() = runTest {
        for (id in listOf("w01", "w02", "w03")) {
            val resultado = repository.getWorkoutDetail(id)
            assertTrue("$id tiene que ser exito, fue $resultado", resultado.isSuccess)

            val detalle = resultado.getOrThrow()
            assertEquals("exerciseCount de $id", detalle.exercises.size, detalle.exerciseCount)
        }
    }

    @Test
    fun `D3 detalle demo con id desconocido devuelve failure sin lanzar`() = runTest {
        val resultado = repository.getWorkoutDetail("no-existe")

        val error = resultado.exceptionOrNull()
        assertTrue("tiene que fallar con NoSuchElementException, fue $resultado", error is NoSuchElementException)
    }
}
