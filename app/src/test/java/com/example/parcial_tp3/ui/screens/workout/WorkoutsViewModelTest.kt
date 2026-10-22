package com.example.parcial_tp3.ui.screens.workout

import com.example.parcial_tp3.domain.model.Workout
import com.example.parcial_tp3.domain.model.WorkoutLevel
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
 * WorkoutsViewModel con un repositorio falso (RF07): carga, error y rotacion.
 * Spec: specs/funcionales/catalogo-de-entrenamientos.md.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeWorkoutRepository()

    private val catalogo = listOf(
        Workout(id = "w01", name = "Fuerza inicial", level = WorkoutLevel.BEGINNER, durationMinutes = 25, exerciseCount = 3),
        Workout(id = "w02", name = "Entrenamiento completo", level = WorkoutLevel.INTERMEDIATE, durationMinutes = 35, exerciseCount = 3),
    )

    @Test
    fun `C13 catalogo arranca en Loading y pasa a Content con la lista`() = runTest {
        val viewModel = WorkoutsViewModel(repository)

        assertEquals(WorkoutsUiState.Loading, viewModel.uiState.value)

        repository.respuesta.complete(Result.success(catalogo))
        advanceUntilIdle()

        assertEquals(WorkoutsUiState.Content(catalogo), viewModel.uiState.value)
    }

    @Test
    fun `C14 catalogo si el repositorio falla pasa a Error`() = runTest {
        val viewModel = WorkoutsViewModel(repository)

        repository.respuesta.complete(Result.failure(IOException("sin conexion")))
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue("tiene que ser Error, fue $estado", estado is WorkoutsUiState.Error)
    }

    @Test
    fun `C15 catalogo volver a observar no repite la consulta ni pierde la lista`() = runTest {
        repository.respuesta.complete(Result.success(catalogo))
        val viewModel = WorkoutsViewModel(repository)

        // Primera pantalla observando
        val primeraObservacion = backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()
        assertEquals(WorkoutsUiState.Content(catalogo), viewModel.uiState.value)

        // Rotacion: se deja de observar mas de 5 s (el WhileSubscribed tipico) y se vuelve
        primeraObservacion.cancel()
        advanceTimeBy(6_000)
        runCurrent()
        backgroundScope.launch { viewModel.uiState.collect {} }
        runCurrent()

        assertEquals("el repositorio tiene que llamarse una sola vez", 1, repository.llamadas)
        assertEquals(WorkoutsUiState.Content(catalogo), viewModel.uiState.value)
    }
}
