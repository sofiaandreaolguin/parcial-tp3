package com.example.parcial_tp3.ui.screens.workout

import androidx.lifecycle.SavedStateHandle
import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.AppError
import com.example.parcial_tp3.data.repository.AppException
import com.example.parcial_tp3.data.repository.WorkoutNotCachedException
import com.example.parcial_tp3.domain.model.Exercise
import com.example.parcial_tp3.domain.model.WorkoutDetail
import com.example.parcial_tp3.domain.model.WorkoutLevel
import com.example.parcial_tp3.ui.navigation.Rutas
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

    // Estados: specs/funcionales/estados-de-carga-y-error.md

    /** Un ViewModel de w02 que ya llego a Error (o a NotAvailableOffline) por la falla dada. */
    private fun TestScope.conFalla(
        falla: Throwable = AppException(AppError.SERVER),
        repo: FakeWorkoutDetailRepository = repository,
        vararg extra: Pair<String, String>,
    ): WorkoutDetailViewModel {
        repo.respuesta.complete(Result.failure(falla))
        val viewModel = WorkoutDetailViewModel(SavedStateHandle(mapOf(Rutas.ARG_WORKOUT_ID to "w02", *extra)), repo)
        advanceUntilIdle()
        val estado = viewModel.uiState.value
        assertTrue(
            "tiene que arrancar en Error o NotAvailableOffline, fue $estado",
            estado is WorkoutDetailUiState.Error || estado == WorkoutDetailUiState.NotAvailableOffline,
        )
        return viewModel
    }

    @Test
    fun `C22 estados detalle cada tipo de error tiene su mensaje`() = runTest {
        val casos = listOf(
            AppException(AppError.NO_CONNECTION) to WorkoutDetailUiState.Error(R.string.workout_error_no_connection),
            AppException(AppError.SERVER) to WorkoutDetailUiState.Error(R.string.workout_error_server),
            AppException(AppError.INVALID_DATA) to WorkoutDetailUiState.Error(R.string.workout_error_invalid_data),
            AppException(AppError.NOT_FOUND) to WorkoutDetailUiState.Error(R.string.details_not_found),
            AppException(AppError.UNKNOWN) to WorkoutDetailUiState.Error(R.string.details_error),
            IOException("no es AppException") to WorkoutDetailUiState.Error(R.string.details_error),
            WorkoutNotCachedException("w02") to WorkoutDetailUiState.NotAvailableOffline,
        )

        val obtenidos = casos.map { (excepcion, _) -> conFalla(excepcion, FakeWorkoutDetailRepository()).uiState.value }

        // Sin workoutId y con un exerciseId que no esta en el detalle
        val sinWorkoutId = WorkoutDetailViewModel(SavedStateHandle(), FakeWorkoutDetailRepository())
        val repoConDetalle = FakeWorkoutDetailRepository().apply { respuesta.complete(Result.success(detalle)) }
        val sinEjercicio = WorkoutDetailViewModel(
            SavedStateHandle(mapOf(Rutas.ARG_WORKOUT_ID to "w02", Rutas.ARG_EXERCISE_ID to "e99")),
            repoConDetalle,
        )
        advanceUntilIdle()

        // Todos juntos, asi el fallo muestra cada mensaje equivocado
        assertEquals(
            casos.map { it.second } + List(2) { WorkoutDetailUiState.Error(R.string.details_error) },
            obtenidos + listOf(sinWorkoutId.uiState.value, sinEjercicio.uiState.value),
        )
        for (estado in obtenidos.filterIsInstance<WorkoutDetailUiState.Error>()) {
            assertTrue("$estado tiene que tener un texto de strings.xml", esTextoDeStrings(estado.message))
        }
    }

    @Test
    fun `C23 estados detalle onRetry en Error o NotAvailableOffline vuelve a consultar`() = runTest {
        val desde = listOf(
            "Error" to AppException(AppError.SERVER),
            "NotAvailableOffline" to WorkoutNotCachedException("w02"),
        )
        val resultados = listOf(
            "detalle" to (
                Result.success(detalle) to WorkoutDetailUiState.Content(detalle, selectedExercise = pressHombros)
                ),
            "falla" to (
                Result.failure<WorkoutDetail>(AppException(AppError.NO_CONNECTION)) to
                    WorkoutDetailUiState.Error(R.string.workout_error_no_connection)
                ),
            "sin conexion y sin guardar" to (
                Result.failure<WorkoutDetail>(WorkoutNotCachedException("w02")) to WorkoutDetailUiState.NotAvailableOffline
                ),
        )

        for ((estadoInicial, falla) in desde) {
            for ((caso, respuestaYEsperado) in resultados) {
                val (respuestaDelReintento, esperado) = respuestaYEsperado
                val repo = FakeWorkoutDetailRepository()
                val viewModel = conFalla(falla, repo, Rutas.ARG_EXERCISE_ID to "e05")
                val reintento = repo.encolarRespuesta()

                viewModel.onRetry()

                val donde = "desde $estadoInicial, con $caso"
                assertEquals("$donde: justo despues de onRetry", WorkoutDetailUiState.Loading, viewModel.uiState.value)
                runCurrent()
                assertEquals("$donde: el mismo workoutId otra vez", listOf("w02", "w02"), repo.idsPedidos)

                reintento.complete(respuestaDelReintento)
                advanceUntilIdle()

                assertEquals(donde, esperado, viewModel.uiState.value)
            }
        }
    }

    @Test
    fun `C24 estados detalle onRetry fuera de Error no consulta`() = runTest {
        val viewModel = conFalla()
        val reintento = repository.encolarRespuesta()

        // Doble toque: sin runCurrent() entre los dos
        viewModel.onRetry()
        viewModel.onRetry()
        runCurrent()
        assertEquals("dos onRetry seguidos consultan una sola vez mas", listOf("w02", "w02"), repository.idsPedidos)

        // En Loading
        viewModel.onRetry()
        runCurrent()
        assertEquals("en Loading no consulta", listOf("w02", "w02"), repository.idsPedidos)

        // En Content
        reintento.complete(Result.success(detalle))
        advanceUntilIdle()
        viewModel.onRetry()
        runCurrent()
        assertEquals("en Content no consulta", listOf("w02", "w02"), repository.idsPedidos)
        assertEquals(WorkoutDetailUiState.Content(detalle), viewModel.uiState.value)

        // En el Loading de la primera carga
        val repoCargando = FakeWorkoutDetailRepository()
        val cargando = WorkoutDetailViewModel(SavedStateHandle(mapOf(Rutas.ARG_WORKOUT_ID to "w02")), repoCargando)
        runCurrent()
        cargando.onRetry()
        runCurrent()
        assertEquals("en el Loading inicial no consulta", listOf("w02"), repoCargando.idsPedidos)
    }

    @Test
    fun `C25 estados detalle sin workoutId onRetry no consulta`() = runTest {
        repository.respuesta.complete(Result.success(detalle))
        val sinClave = viewModel()
        val vacio = viewModel(Rutas.ARG_WORKOUT_ID to "")
        val espacios = viewModel(Rutas.ARG_WORKOUT_ID to "  ")
        advanceUntilIdle()

        for ((caso, viewModel) in listOf("sin la clave" to sinClave, "vacio" to vacio, "con espacios" to espacios)) {
            viewModel.onRetry()
            advanceUntilIdle()
            val estado = viewModel.uiState.value
            assertTrue("$caso tiene que seguir en Error, fue $estado", estado is WorkoutDetailUiState.Error)
        }
        assertEquals("no tiene que llamar al repositorio", emptyList<String>(), repository.idsPedidos)
    }

    @Test
    fun `C26 estados detalle volver a observar despues del reintento no repite la consulta`() = runTest {
        // Despues de un reintento que termino en Content
        val reintentado = conFalla()
        repository.encolarRespuesta().complete(Result.success(detalle))
        val primeraObservacion = backgroundScope.launch { reintentado.uiState.collect {} }
        reintentado.onRetry()
        advanceUntilIdle()
        assertEquals(WorkoutDetailUiState.Content(detalle), reintentado.uiState.value)

        primeraObservacion.cancel()
        advanceTimeBy(6_000)
        runCurrent()
        backgroundScope.launch { reintentado.uiState.collect {} }
        runCurrent()

        assertEquals("la carga y el reintento, nada mas", listOf("w02", "w02"), repository.idsPedidos)
        assertEquals(WorkoutDetailUiState.Content(detalle), reintentado.uiState.value)

        // En Error: rotar no reintenta solo
        val repoConFalla = FakeWorkoutDetailRepository()
        val enError = conFalla(repo = repoConFalla)
        val observacionEnError = backgroundScope.launch { enError.uiState.collect {} }
        runCurrent()
        val error = enError.uiState.value

        observacionEnError.cancel()
        advanceTimeBy(6_000)
        runCurrent()
        backgroundScope.launch { enError.uiState.collect {} }
        runCurrent()

        assertEquals("en Error el repositorio tiene que llamarse una sola vez", listOf("w02"), repoConFalla.idsPedidos)
        assertEquals(error, enError.uiState.value)
    }
}
