package com.example.parcial_tp3.ui.screens.workout

import androidx.lifecycle.SavedStateHandle
import com.example.parcial_tp3.data.repository.WorkoutNotCachedException
import com.example.parcial_tp3.domain.model.Exercise
import com.example.parcial_tp3.domain.model.WorkoutDetail
import com.example.parcial_tp3.domain.model.WorkoutLevel
import com.example.parcial_tp3.ui.navigation.Rutas
import com.example.parcial_tp3.util.MainDispatcherRule
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * WorkoutDetailViewModel con un repositorio falso (NAV04, RF07): lee la ruta, carga, error y
 * rotacion. El detalle se arma aca y no se lee de la demo, que se borra con el detalle real.
 * Specs: specs/funcionales/detalle-de-entrenamiento-demo.md y, el C28 "cache",
 * specs/funcionales/detalle-y-cache-de-entrenamientos.md.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeWorkoutDetailRepository()

    private val puente = Exercise(
        id = "e04", name = "Puente de glúteos", muscleGroup = "Piernas", sets = 3, reps = 10,
        restSeconds = 60, instructions = "Elevá la cadera sin arquear la espalda.",
        tip = "Usá una carga que puedas controlar.",
    )
    private val pressHombros = Exercise(
        id = "e05", name = "Press de hombros", muscleGroup = "Hombros", sets = 3, reps = 10,
        restSeconds = 60, instructions = "Empujá hacia arriba manteniendo una postura estable.",
        tip = "Usá una carga que puedas controlar.",
    )
    private val detalle = WorkoutDetail(
        id = "w02", name = "Entrenamiento completo", level = WorkoutLevel.INTERMEDIATE,
        durationMinutes = 35, exerciseCount = 2,
        description = "Rutina de ejemplo para el trabajo práctico.",
        exercises = listOf(puente, pressHombros),
    )

    private fun viewModel(vararg argumentos: Pair<String, String>) =
        WorkoutDetailViewModel(SavedStateHandle(mapOf(*argumentos)), repository)

    @Test
    fun `C1 detalle arranca en Loading y pasa a Content sin ejercicio elegido`() = runTest {
        val viewModel = viewModel(Rutas.ARG_WORKOUT_ID to "w02")

        assertEquals(WorkoutDetailUiState.Loading, viewModel.uiState.value)

        repository.respuesta.complete(Result.success(detalle))
        advanceUntilIdle()

        assertEquals(WorkoutDetailUiState.Content(detalle, selectedExercise = null), viewModel.uiState.value)
    }

    @Test
    fun `C2 detalle pide al repositorio el workoutId de la ruta`() = runTest {
        viewModel(Rutas.ARG_WORKOUT_ID to "w02")
        advanceUntilIdle()

        assertEquals(listOf("w02"), repository.idsPedidos)
    }

    @Test
    fun `C3 detalle con exerciseId elige ese ejercicio`() = runTest {
        repository.respuesta.complete(Result.success(detalle))
        val viewModel = viewModel(Rutas.ARG_WORKOUT_ID to "w02", Rutas.ARG_EXERCISE_ID to "e05")
        advanceUntilIdle()

        assertEquals(WorkoutDetailUiState.Content(detalle, selectedExercise = pressHombros), viewModel.uiState.value)
    }

    @Test
    fun `C4 detalle con exerciseId que no esta pasa a Error`() = runTest {
        repository.respuesta.complete(Result.success(detalle))

        for (exerciseId in listOf("e99", "", "  ")) {
            val viewModel = viewModel(Rutas.ARG_WORKOUT_ID to "w02", Rutas.ARG_EXERCISE_ID to exerciseId)
            advanceUntilIdle()

            val estado = viewModel.uiState.value
            assertTrue("con exerciseId \"$exerciseId\" tiene que ser Error, fue $estado", estado is WorkoutDetailUiState.Error)
        }
    }

    @Test
    fun `C5 detalle si el repositorio falla pasa a Error`() = runTest {
        val viewModel = viewModel(Rutas.ARG_WORKOUT_ID to "w02")

        repository.respuesta.complete(Result.failure(IOException("sin conexion")))
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue("tiene que ser Error, fue $estado", estado is WorkoutDetailUiState.Error)
    }

    @Test
    fun `C6 detalle sin workoutId pasa a Error sin llamar al repositorio`() = runTest {
        repository.respuesta.complete(Result.success(detalle))
        val sinClave = viewModel()
        val vacio = viewModel(Rutas.ARG_WORKOUT_ID to "")
        val espacios = viewModel(Rutas.ARG_WORKOUT_ID to "  ")
        advanceUntilIdle()

        for ((caso, viewModel) in listOf("sin la clave" to sinClave, "vacio" to vacio, "con espacios" to espacios)) {
            val estado = viewModel.uiState.value
            assertTrue("$caso tiene que ser Error, fue $estado", estado is WorkoutDetailUiState.Error)
        }
        assertEquals("no tiene que llamar al repositorio", emptyList<String>(), repository.idsPedidos)
    }

    @Test
    fun `C7 detalle volver a observar no repite la consulta ni pierde el estado`() = runTest {
        repository.respuesta.complete(Result.success(detalle))
        val viewModel = viewModel(Rutas.ARG_WORKOUT_ID to "w02")

        // Primera pantalla observando
        val primeraObservacion = backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()
        assertEquals(WorkoutDetailUiState.Content(detalle), viewModel.uiState.value)

        // Rotacion: se deja de observar mas de 5 s (el WhileSubscribed tipico) y se vuelve
        primeraObservacion.cancel()
        advanceTimeBy(6_000)
        runCurrent()
        backgroundScope.launch { viewModel.uiState.collect {} }
        runCurrent()

        assertEquals("el repositorio tiene que llamarse una sola vez", listOf("w02"), repository.idsPedidos)
        assertEquals(WorkoutDetailUiState.Content(detalle), viewModel.uiState.value)
    }

    @Test
    fun `C28 cache detalle no descargado pasa a NotAvailableOffline`() = runTest {
        val viewModel = viewModel(Rutas.ARG_WORKOUT_ID to "w02")

        repository.respuesta.complete(Result.failure(WorkoutNotCachedException("w02")))
        advanceUntilIdle()

        assertEquals(WorkoutDetailUiState.NotAvailableOffline, viewModel.uiState.value)
    }
}
