package com.example.parcial_tp3.ui.screens.workout

import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.AppError
import com.example.parcial_tp3.data.repository.AppException
import com.example.parcial_tp3.domain.model.Workout
import com.example.parcial_tp3.domain.model.WorkoutLevel
import com.example.parcial_tp3.util.MainDispatcherRule
import com.example.parcial_tp3.util.esTextoDeStrings
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
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

    // Estados: specs/funcionales/estados-de-carga-y-error.md

    /** Un ViewModel que ya llego a Error por un 503. */
    private fun TestScope.enError(repo: FakeWorkoutRepository = repository): WorkoutsViewModel {
        repo.respuesta.complete(Result.failure(AppException(AppError.SERVER)))
        val viewModel = WorkoutsViewModel(repo)
        advanceUntilIdle()
        val estado = viewModel.uiState.value
        assertTrue("tiene que arrancar en Error, fue $estado", estado is WorkoutsUiState.Error)
        return viewModel
    }

    @Test
    fun `C13 estados catalogo con lista vacia pasa a Empty`() = runTest {
        val viewModel = WorkoutsViewModel(repository)

        repository.respuesta.complete(Result.success(emptyList()))
        advanceUntilIdle()

        assertEquals(WorkoutsUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `C16 estados catalogo cada tipo de error tiene su mensaje`() = runTest {
        val casos = listOf(
            AppException(AppError.NO_CONNECTION) to R.string.workout_error_no_connection,
            AppException(AppError.SERVER) to R.string.workout_error_server,
            AppException(AppError.INVALID_DATA) to R.string.workout_error_invalid_data,
            AppException(AppError.NOT_FOUND) to R.string.workout_error,
            AppException(AppError.UNKNOWN) to R.string.workout_error,
            IOException("no es AppException") to R.string.workout_error,
        )

        val obtenidos = casos.map { (excepcion, _) ->
            val repo = FakeWorkoutRepository()
            repo.respuesta.complete(Result.failure(excepcion))
            val viewModel = WorkoutsViewModel(repo)
            advanceUntilIdle()
            viewModel.uiState.value
        }

        // Todos juntos, asi el fallo muestra cada mensaje equivocado
        assertEquals(casos.map { (_, mensaje) -> WorkoutsUiState.Error(mensaje) }, obtenidos)
        for (estado in obtenidos) {
            assertTrue("$estado tiene que tener un texto de strings.xml", esTextoDeStrings((estado as WorkoutsUiState.Error).message))
        }
    }

    @Test
    fun `C17 estados catalogo onRetry en Error pasa por Loading y vuelve a consultar`() = runTest {
        val casos = listOf(
            "con entrenamientos" to (Result.success(catalogo) to WorkoutsUiState.Content(catalogo)),
            "vacio" to (Result.success(emptyList<Workout>()) to WorkoutsUiState.Empty),
            "con falla" to (
                Result.failure<List<Workout>>(AppException(AppError.NO_CONNECTION)) to
                    WorkoutsUiState.Error(R.string.workout_error_no_connection)
                ),
        )

        for ((caso, respuestaYEsperado) in casos) {
            val (respuestaDelReintento, esperado) = respuestaYEsperado
            val repo = FakeWorkoutRepository()
            val viewModel = enError(repo)
            val reintento = repo.encolarRespuesta()

            viewModel.onRetry()

            assertEquals("$caso: justo despues de onRetry", WorkoutsUiState.Loading, viewModel.uiState.value)
            runCurrent()
            assertEquals("$caso: el repositorio tiene que llamarse otra vez", 2, repo.llamadas)

            reintento.complete(respuestaDelReintento)
            advanceUntilIdle()

            assertEquals(caso, esperado, viewModel.uiState.value)
        }
    }

    @Test
    fun `C18 estados catalogo onRetry fuera de Error no consulta`() = runTest {
        val viewModel = enError()
        val reintento = repository.encolarRespuesta()

        // Doble toque: sin runCurrent() entre los dos
        viewModel.onRetry()
        viewModel.onRetry()
        runCurrent()
        assertEquals("dos onRetry seguidos consultan una sola vez mas", 2, repository.llamadas)

        // En Loading
        viewModel.onRetry()
        runCurrent()
        assertEquals("en Loading no consulta", 2, repository.llamadas)

        // En Content
        reintento.complete(Result.success(catalogo))
        advanceUntilIdle()
        viewModel.onRetry()
        runCurrent()
        assertEquals("en Content no consulta", 2, repository.llamadas)
        assertEquals(WorkoutsUiState.Content(catalogo), viewModel.uiState.value)

        // En Empty
        val repoVacio = FakeWorkoutRepository()
        repoVacio.respuesta.complete(Result.success(emptyList()))
        val vacio = WorkoutsViewModel(repoVacio)
        advanceUntilIdle()
        vacio.onRetry()
        runCurrent()
        assertEquals("en Empty no consulta", 1, repoVacio.llamadas)
        assertEquals(WorkoutsUiState.Empty, vacio.uiState.value)

        // En el Loading de la primera carga
        val repoCargando = FakeWorkoutRepository()
        val cargando = WorkoutsViewModel(repoCargando)
        runCurrent()
        cargando.onRetry()
        runCurrent()
        assertEquals("en el Loading inicial no consulta", 1, repoCargando.llamadas)
    }

    @Test
    fun `C19 estados catalogo volver a observar despues del reintento no repite la consulta`() = runTest {
        val viewModel = enError()
        repository.encolarRespuesta().complete(Result.success(catalogo))

        val primeraObservacion = backgroundScope.launch { viewModel.uiState.collect {} }
        viewModel.onRetry()
        advanceUntilIdle()
        assertEquals(WorkoutsUiState.Content(catalogo), viewModel.uiState.value)

        // Rotacion: se deja de observar mas de 5 s y se vuelve
        primeraObservacion.cancel()
        advanceTimeBy(6_000)
        runCurrent()
        backgroundScope.launch { viewModel.uiState.collect {} }
        runCurrent()

        assertEquals("la carga y el reintento, nada mas", 2, repository.llamadas)
        assertEquals(WorkoutsUiState.Content(catalogo), viewModel.uiState.value)
    }

    @Test
    fun `C20 estados catalogo volver a observar en Error no reintenta solo`() = runTest {
        val viewModel = enError()
        val primeraObservacion = backgroundScope.launch { viewModel.uiState.collect {} }
        runCurrent()
        val error = viewModel.uiState.value

        primeraObservacion.cancel()
        advanceTimeBy(6_000)
        runCurrent()
        backgroundScope.launch { viewModel.uiState.collect {} }
        runCurrent()

        assertEquals("el repositorio tiene que llamarse una sola vez", 1, repository.llamadas)
        assertEquals(error, viewModel.uiState.value)
    }
}
