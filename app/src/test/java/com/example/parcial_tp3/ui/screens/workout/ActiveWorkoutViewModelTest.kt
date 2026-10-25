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
import com.example.parcial_tp3.ui.screens.workout.ActiveWorkoutUiState.Content
import com.example.parcial_tp3.util.MainDispatcherRule
import com.example.parcial_tp3.util.esTextoDeStrings
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * ActiveWorkoutViewModel con un repositorio falso (NAV03, NAV04, RF07): carga, series, descanso,
 * peso, fin y rotacion. El detalle se arma aca y no se lee de la demo, que se borra con el detalle
 * real. Spec: specs/funcionales/sesion-de-entrenamiento-demo.md.
 *
 * Despues de completar una serie nunca se usa advanceUntilIdle(): correria el descanso entero.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ActiveWorkoutViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeWorkoutDetailRepository()

    // Distintos entre si para que C7 y C18 distingan el ejercicio recien terminado del siguiente.
    private val puente = Exercise(
        id = "e04", name = "Puente de glúteos", muscleGroup = "Piernas", sets = 3, reps = 10,
        restSeconds = 60, instructions = "Elevá la cadera sin arquear la espalda.",
        tip = "Usá una carga que puedas controlar.",
    )
    private val pressHombros = Exercise(
        id = "e05", name = "Press de hombros", muscleGroup = "Hombros", sets = 3, reps = 12,
        restSeconds = 90, instructions = "Empujá hacia arriba manteniendo una postura estable.",
        tip = "Usá una carga que puedas controlar.",
    )
    private val detalle = WorkoutDetail(
        id = "w02", name = "Entrenamiento completo", level = WorkoutLevel.INTERMEDIATE,
        durationMinutes = 35, exerciseCount = 2,
        description = "Rutina de ejemplo para el trabajo práctico.",
        exercises = listOf(puente, pressHombros),
    )
    private val inicial = Content(
        detalle, exerciseIndex = 0, setIndex = 0, weightKg = 45.0, restRemainingSeconds = 0,
        completedSets = emptyList(), rating = null,
    )

    private fun viewModel(
        vararg argumentos: Pair<String, String>,
        repo: FakeWorkoutDetailRepository = repository,
    ) = ActiveWorkoutViewModel(SavedStateHandle(mapOf(*argumentos)), repo)

    /** El ViewModel con el detalle ya cargado, antes de cualquier evento. */
    private fun TestScope.sesion(
        detail: WorkoutDetail = detalle,
        repo: FakeWorkoutDetailRepository = repository,
    ): ActiveWorkoutViewModel {
        repo.respuesta.complete(Result.success(detail))
        val viewModel = viewModel(Rutas.ARG_WORKOUT_ID to detail.id, repo = repo)
        runCurrent()
        viewModel.contenido()
        return viewModel
    }

    private fun ActiveWorkoutViewModel.contenido(): Content {
        val estado = uiState.value
        assertTrue("la sesion tiene que estar en Content, fue $estado", estado is Content)
        return estado as Content
    }

    private fun TestScope.avanzar(milisegundos: Long) {
        advanceTimeBy(milisegundos)
        runCurrent()
    }

    private fun serie(ejercicio: Exercise, pesoKg: Double = 45.0) =
        CompletedSet(ejercicio.id, ejercicio.reps, pesoKg)

    private fun ActiveWorkoutViewModel.todosLosEventos() {
        onCompleteSet()
        onAddRest()
        onSkipRest()
        onIncreaseWeight()
        onDecreaseWeight()
        onFinish()
        onRate(WorkoutRating.GREAT)
    }

    // Carga

    @Test
    fun `C1 sesion arranca en Loading y pasa a Content con los valores iniciales`() = runTest {
        val viewModel = viewModel(Rutas.ARG_WORKOUT_ID to "w02")

        assertEquals(ActiveWorkoutUiState.Loading, viewModel.uiState.value)

        repository.respuesta.complete(Result.success(detalle))
        runCurrent()

        assertEquals(inicial, viewModel.uiState.value)
    }

    @Test
    fun `C2 sesion pide al repositorio el workoutId de la ruta`() = runTest {
        viewModel(Rutas.ARG_WORKOUT_ID to "w02")
        runCurrent()

        assertEquals(listOf("w02"), repository.idsPedidos)
    }

    @Test
    fun `C3 sesion sin workoutId pasa a Error sin llamar al repositorio`() = runTest {
        repository.respuesta.complete(Result.success(detalle))
        val sinClave = viewModel()
        val vacio = viewModel(Rutas.ARG_WORKOUT_ID to "")
        val espacios = viewModel(Rutas.ARG_WORKOUT_ID to "  ")
        runCurrent()

        for ((caso, viewModel) in listOf("sin la clave" to sinClave, "vacio" to vacio, "con espacios" to espacios)) {
            val estado = viewModel.uiState.value
            assertTrue("$caso tiene que ser Error, fue $estado", estado is ActiveWorkoutUiState.Error)
        }
        assertEquals("no tiene que llamar al repositorio", emptyList<String>(), repository.idsPedidos)
    }

    @Test
    fun `C4 sesion si el repositorio falla pasa a Error`() = runTest {
        val viewModel = viewModel(Rutas.ARG_WORKOUT_ID to "w02")

        repository.respuesta.complete(Result.failure(IOException("sin conexion")))
        runCurrent()

        val estado = viewModel.uiState.value
        assertTrue("tiene que ser Error, fue $estado", estado is ActiveWorkoutUiState.Error)
    }

    @Test
    fun `C5 sesion con detalle sin ejercicios pasa a Error`() = runTest {
        val viewModel = viewModel(Rutas.ARG_WORKOUT_ID to "w02")

        repository.respuesta.complete(Result.success(detalle.copy(exerciseCount = 0, exercises = emptyList())))
        runCurrent()

        val estado = viewModel.uiState.value
        assertTrue("tiene que ser Error, fue $estado", estado is ActiveWorkoutUiState.Error)
    }

    // Series

    @Test
    fun `C6 sesion completar serie pasa a la siguiente y arranca el descanso`() = runTest {
        val viewModel = sesion()

        viewModel.onCompleteSet()

        assertEquals(
            Content(detalle, exerciseIndex = 0, setIndex = 1, restRemainingSeconds = 60, completedSets = listOf(serie(puente))),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `C7 sesion completar la ultima serie de un ejercicio pasa al siguiente`() = runTest {
        val viewModel = sesion()

        repeat(3) { viewModel.onCompleteSet() }

        // El descanso y la serie son de e04 (60 s, 10 reps), no de e05 (90 s, 12 reps)
        assertEquals(
            Content(detalle, exerciseIndex = 1, setIndex = 0, restRemainingSeconds = 60, completedSets = List(3) { serie(puente) }),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `C8 sesion completar la ultima serie de todo termina sin descanso`() = runTest {
        val viewModel = sesion()
        repeat(5) { viewModel.onCompleteSet() }
        avanzar(1_000)

        // La ultima se completa durante el descanso de la anterior
        viewModel.onCompleteSet()

        val esperado = Content(
            detalle, exerciseIndex = 1, setIndex = 3, restRemainingSeconds = 0,
            completedSets = List(3) { serie(puente) } + List(3) { serie(pressHombros) },
        )
        assertEquals(esperado, viewModel.uiState.value)
        val estado = viewModel.contenido()
        assertTrue("allSetsDone tiene que ser verdadero", estado.allSetsDone)
        assertEquals(2, estado.exercisesCompleted)

        avanzar(61_000)

        assertEquals("despues de la ultima serie no hay descanso", esperado, viewModel.uiState.value)
    }

    @Test
    fun `C9 sesion terminada ignora completar serie`() = runTest {
        val viewModel = sesion()
        repeat(6) { viewModel.onCompleteSet() }
        val terminada = viewModel.contenido()
        assertTrue("allSetsDone tiene que ser verdadero", terminada.allSetsDone)

        viewModel.onCompleteSet()

        assertEquals(terminada, viewModel.uiState.value)
    }

    @Test
    fun `C10 sesion ejercicio sin descanso no arranca descanso`() = runTest {
        for (descanso in listOf(0, -5)) {
            val sinDescanso = puente.copy(id = "e06", reps = 8, restSeconds = descanso)
            val detail = detalle.copy(exerciseCount = 1, exercises = listOf(sinDescanso))
            val viewModel = sesion(detail, FakeWorkoutDetailRepository())

            viewModel.onCompleteSet()

            assertEquals(
                "con restSeconds $descanso",
                Content(detail, setIndex = 1, restRemainingSeconds = 0, completedSets = listOf(serie(sinDescanso))),
                viewModel.uiState.value,
            )
        }
    }

    // Descanso

    @Test
    fun `C11 sesion el descanso baja de a un segundo y se queda en cero`() = runTest {
        val viewModel = sesion()

        viewModel.onCompleteSet()
        assertEquals("al completar", 60, viewModel.contenido().restRemainingSeconds)

        avanzar(1_000)
        assertEquals("a 1 s", 59, viewModel.contenido().restRemainingSeconds)

        avanzar(59_000)
        assertEquals("a 60 s", 0, viewModel.contenido().restRemainingSeconds)

        avanzar(1_000)
        assertEquals("a 61 s", 0, viewModel.contenido().restRemainingSeconds)
    }

    @Test
    fun `C12 sesion mas 30 suma al descanso y sigue contando`() = runTest {
        val viewModel = sesion()
        viewModel.onCompleteSet()
        avanzar(1_000)
        assertEquals(59, viewModel.contenido().restRemainingSeconds)

        viewModel.onAddRest()
        assertEquals("al tocar +30s", 89, viewModel.contenido().restRemainingSeconds)

        avanzar(1_000)
        assertEquals("1 s despues", 88, viewModel.contenido().restRemainingSeconds)
    }

    @Test
    fun `C13 sesion mas 30 sin descanso no hace nada`() = runTest {
        val viewModel = sesion()

        viewModel.onAddRest()

        assertEquals(inicial, viewModel.uiState.value)
    }

    @Test
    fun `C14 sesion skip corta el descanso`() = runTest {
        val viewModel = sesion()
        viewModel.onCompleteSet()
        avanzar(1_000)

        viewModel.onSkipRest()

        val cortado = viewModel.contenido()
        assertEquals(0, cortado.restRemainingSeconds)
        avanzar(5_000)
        assertEquals("5 s despues", cortado, viewModel.uiState.value)
    }

    @Test
    fun `C15 sesion completar durante el descanso arranca uno nuevo y cuenta uno solo`() = runTest {
        val viewModel = sesion()
        viewModel.onCompleteSet()
        avanzar(30_000)

        viewModel.onCompleteSet()
        assertEquals("al completar con 30 s de descanso", 60, viewModel.contenido().restRemainingSeconds)
        avanzar(1_000)
        assertEquals("tiene que contar un solo descanso", 59, viewModel.contenido().restRemainingSeconds)

        // Ultima serie de e04: el descanso nuevo es el de e04 (60), no el de e05 (90)
        avanzar(29_000)
        viewModel.onCompleteSet()
        assertEquals("al completar la ultima serie de e04", 60, viewModel.contenido().restRemainingSeconds)
        avanzar(1_000)
        assertEquals("tiene que contar un solo descanso", 59, viewModel.contenido().restRemainingSeconds)
    }

    // Peso

    @Test
    fun `C16 sesion mas y menos cambian el peso de a 2 y medio`() = runTest {
        val viewModel = sesion()

        viewModel.onIncreaseWeight()
        assertEquals(47.5, viewModel.contenido().weightKg, 0.0)

        viewModel.onDecreaseWeight()
        viewModel.onDecreaseWeight()
        assertEquals(42.5, viewModel.contenido().weightKg, 0.0)
    }

    @Test
    fun `C17 sesion el peso no baja de cero`() = runTest {
        val viewModel = sesion()

        repeat(18) { viewModel.onDecreaseWeight() }
        assertEquals("18 veces desde 45", 0.0, viewModel.contenido().weightKg, 0.0)

        viewModel.onDecreaseWeight()
        assertEquals("la 19.a no baja de 0", 0.0, viewModel.contenido().weightKg, 0.0)
    }

    @Test
    fun `C18 sesion cada serie guarda su peso y suma el volumen`() = runTest {
        val viewModel = sesion()

        viewModel.onCompleteSet()
        viewModel.onIncreaseWeight()
        viewModel.onIncreaseWeight()
        viewModel.onCompleteSet()

        assertEquals(listOf(serie(puente, 45.0), serie(puente, 50.0)), viewModel.contenido().completedSets)
        assertEquals(950.0, viewModel.contenido().volumeKg, 0.0)

        viewModel.onIncreaseWeight()

        assertEquals("cambiar el peso despues no cambia el volumen", 950.0, viewModel.contenido().volumeKg, 0.0)
    }

    // Fin (07)

    @Test
    fun `C19 sesion terminar corta el descanso y conserva el avance`() = runTest {
        val conDescanso = sesion()
        conDescanso.onRate(WorkoutRating.GOOD)
        conDescanso.onIncreaseWeight()
        conDescanso.onCompleteSet()
        avanzar(1_000)
        val esperado = conDescanso.contenido().copy(restRemainingSeconds = 0)

        conDescanso.onFinish()

        assertEquals("con descanso", esperado, conDescanso.uiState.value)
        avanzar(61_000)
        assertEquals("con descanso, 61 s despues", esperado, conDescanso.uiState.value)

        val sinDescanso = sesion(repo = FakeWorkoutDetailRepository())
        sinDescanso.onCompleteSet()
        sinDescanso.onSkipRest()
        val antes = sinDescanso.contenido()

        sinDescanso.onFinish()

        assertEquals("sin descanso", antes, sinDescanso.uiState.value)
        avanzar(61_000)
        assertEquals("sin descanso, 61 s despues", antes, sinDescanso.uiState.value)
    }

    @Test
    fun `C20 sesion terminar antes cuenta solo los ejercicios completos`() = runTest {
        val viewModel = sesion()
        repeat(4) { viewModel.onCompleteSet() }

        viewModel.onFinish()

        val estado = viewModel.contenido()
        assertFalse("allSetsDone tiene que ser falso", estado.allSetsDone)
        assertEquals(1, estado.exercisesCompleted)
    }

    @Test
    fun `C26 sesion despues de terminar se puede seguir`() = runTest {
        val viewModel = sesion()
        viewModel.onCompleteSet()
        viewModel.onFinish()

        viewModel.onCompleteSet()

        assertEquals(
            Content(detalle, exerciseIndex = 0, setIndex = 2, restRemainingSeconds = 60, completedSets = List(2) { serie(puente) }),
            viewModel.uiState.value,
        )
        avanzar(1_000)
        assertEquals("el descanso cuenta despues de terminar", 59, viewModel.contenido().restRemainingSeconds)
    }

    @Test
    fun `C21 sesion elegir dificultad la guarda y la cambia`() = runTest {
        val viewModel = sesion()
        assertNull(viewModel.contenido().rating)

        viewModel.onRate(WorkoutRating.GOOD)
        assertEquals(WorkoutRating.GOOD, viewModel.contenido().rating)

        viewModel.onRate(WorkoutRating.TOUGH)
        assertEquals(WorkoutRating.TOUGH, viewModel.contenido().rating)

        viewModel.onRate(WorkoutRating.TOUGH)
        assertEquals(WorkoutRating.TOUGH, viewModel.contenido().rating)
    }

    // Robustez y rotacion

    @Test
    fun `C22 sesion sin Content los eventos no cambian nada`() = runTest {
        val cargando = viewModel(Rutas.ARG_WORKOUT_ID to "w02")
        cargando.todosLosEventos()
        assertEquals("en Loading", ActiveWorkoutUiState.Loading, cargando.uiState.value)

        repository.respuesta.complete(Result.success(detalle))
        runCurrent()
        assertEquals("los eventos en Loading no se guardan para despues", inicial, cargando.uiState.value)
        avanzar(61_000)
        assertEquals("61 s despues", inicial, cargando.uiState.value)

        val conFalla = FakeWorkoutDetailRepository()
        conFalla.respuesta.complete(Result.failure(IOException("sin conexion")))
        val conError = viewModel(Rutas.ARG_WORKOUT_ID to "w02", repo = conFalla)
        runCurrent()
        assertTrue("tiene que ser Error, fue ${conError.uiState.value}", conError.uiState.value is ActiveWorkoutUiState.Error)

        conError.todosLosEventos()

        assertTrue("en Error, fue ${conError.uiState.value}", conError.uiState.value is ActiveWorkoutUiState.Error)
    }

    @Test
    fun `C23 sesion volver a observar no repite la consulta ni pierde el avance`() = runTest {
        repository.respuesta.complete(Result.success(detalle))
        val viewModel = viewModel(Rutas.ARG_WORKOUT_ID to "w02")

        // Primera pantalla observando
        val primeraObservacion = backgroundScope.launch { viewModel.uiState.collect {} }
        runCurrent()
        viewModel.onCompleteSet()
        // Sin descanso, asi el estado no cambia mientras pasan los 6 s
        viewModel.onSkipRest()
        val avance = viewModel.contenido()
        assertEquals(Content(detalle, setIndex = 1, completedSets = listOf(serie(puente))), avance)

        // Rotacion: se deja de observar mas de 5 s (el WhileSubscribed tipico) y se vuelve
        primeraObservacion.cancel()
        avanzar(6_000)
        backgroundScope.launch { viewModel.uiState.collect {} }
        runCurrent()

        assertEquals("el repositorio tiene que llamarse una sola vez", listOf("w02"), repository.idsPedidos)
        assertEquals(avance, viewModel.uiState.value)
    }

    @Test
    fun `C24 sesion el descanso sigue contando sin observar`() = runTest {
        repository.respuesta.complete(Result.success(detalle))
        val viewModel = viewModel(Rutas.ARG_WORKOUT_ID to "w02")

        val primeraObservacion = backgroundScope.launch { viewModel.uiState.collect {} }
        runCurrent()
        viewModel.onCompleteSet()
        assertEquals(60, viewModel.contenido().restRemainingSeconds)

        primeraObservacion.cancel()
        avanzar(6_000)
        backgroundScope.launch { viewModel.uiState.collect {} }
        runCurrent()

        assertEquals(54, viewModel.contenido().restRemainingSeconds)
        assertEquals("el repositorio tiene que llamarse una sola vez", listOf("w02"), repository.idsPedidos)
    }

    // Estados: specs/funcionales/estados-de-carga-y-error.md

    /** La sesion de w02 que ya llego a Error por la falla dada. */
    private fun TestScope.conFalla(
        falla: Throwable = AppException(AppError.SERVER),
        repo: FakeWorkoutDetailRepository = repository,
    ): ActiveWorkoutViewModel {
        repo.respuesta.complete(Result.failure(falla))
        val viewModel = viewModel(Rutas.ARG_WORKOUT_ID to "w02", repo = repo)
        runCurrent()
        val estado = viewModel.uiState.value
        assertTrue("tiene que arrancar en Error, fue $estado", estado is ActiveWorkoutUiState.Error)
        return viewModel
    }

    @Test
    fun `C27 estados sesion cada tipo de error tiene su mensaje`() = runTest {
        val casos = listOf(
            AppException(AppError.NO_CONNECTION) to R.string.workout_error_no_connection,
            AppException(AppError.SERVER) to R.string.workout_error_server,
            AppException(AppError.INVALID_DATA) to R.string.workout_error_invalid_data,
            AppException(AppError.NOT_FOUND) to R.string.details_not_found,
            AppException(AppError.UNKNOWN) to R.string.details_error,
            IOException("no es AppException") to R.string.details_error,
            // 05 no tiene un estado propio para "no disponible sin conexion"
            WorkoutNotCachedException("w02") to R.string.workout_not_available_offline,
        )

        val obtenidos = casos.map { (excepcion, _) -> conFalla(excepcion, FakeWorkoutDetailRepository()).uiState.value }

        // Un detalle sin ejercicios y una ruta sin workoutId
        val repoSinEjercicios = FakeWorkoutDetailRepository().apply {
            respuesta.complete(Result.success(detalle.copy(exerciseCount = 0, exercises = emptyList())))
        }
        val sinEjercicios = viewModel(Rutas.ARG_WORKOUT_ID to "w02", repo = repoSinEjercicios)
        val sinWorkoutId = viewModel(repo = FakeWorkoutDetailRepository())
        runCurrent()

        // Todos juntos, asi el fallo muestra cada mensaje equivocado
        assertEquals(
            (casos.map { it.second } + List(2) { R.string.details_error }).map { ActiveWorkoutUiState.Error(it) },
            obtenidos + listOf(sinEjercicios.uiState.value, sinWorkoutId.uiState.value),
        )
        for (estado in obtenidos) {
            assertTrue("$estado tiene que tener un texto de strings.xml", esTextoDeStrings((estado as ActiveWorkoutUiState.Error).message))
        }
    }

    @Test
    fun `C28 estados sesion onRetry en Error vuelve a consultar y arranca de cero`() = runTest {
        val viewModel = conFalla()
        val reintento = repository.encolarRespuesta()

        viewModel.onRetry()

        assertEquals("justo despues de onRetry", ActiveWorkoutUiState.Loading, viewModel.uiState.value)
        runCurrent()
        assertEquals("el mismo workoutId otra vez", listOf("w02", "w02"), repository.idsPedidos)

        reintento.complete(Result.success(detalle))
        runCurrent()

        assertEquals(inicial, viewModel.uiState.value)
    }

    @Test
    fun `C29 estados sesion onRetry en Content no reinicia la sesion`() = runTest {
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

        // En Content, con una serie completada
        reintento.complete(Result.success(detalle))
        runCurrent()
        viewModel.onCompleteSet()
        viewModel.onSkipRest()
        val avance = viewModel.contenido()
        assertEquals(Content(detalle, setIndex = 1, completedSets = listOf(serie(puente))), avance)

        viewModel.onRetry()
        runCurrent()

        assertEquals("en Content no consulta", listOf("w02", "w02"), repository.idsPedidos)
        assertEquals("el Content no cambia", avance, viewModel.uiState.value)

        // En el Loading de la primera carga
        val repoCargando = FakeWorkoutDetailRepository()
        val cargando = ActiveWorkoutViewModel(SavedStateHandle(mapOf(Rutas.ARG_WORKOUT_ID to "w02")), repoCargando)
        runCurrent()
        cargando.onRetry()
        runCurrent()
        assertEquals("en el Loading inicial no consulta", listOf("w02"), repoCargando.idsPedidos)
    }

    @Test
    fun `C30 estados sesion volver a observar despues del reintento no repite la consulta`() = runTest {
        // Despues de un reintento que termino en Content
        val reintentado = conFalla()
        repository.encolarRespuesta().complete(Result.success(detalle))
        val primeraObservacion = backgroundScope.launch { reintentado.uiState.collect {} }
        reintentado.onRetry()
        runCurrent()
        assertEquals(inicial, reintentado.uiState.value)

        primeraObservacion.cancel()
        avanzar(6_000)
        backgroundScope.launch { reintentado.uiState.collect {} }
        runCurrent()

        assertEquals("la carga y el reintento, nada mas", listOf("w02", "w02"), repository.idsPedidos)
        assertEquals(inicial, reintentado.uiState.value)

        // En Error: rotar no reintenta solo
        val repoConFalla = FakeWorkoutDetailRepository()
        val enError = conFalla(repo = repoConFalla)
        val observacionEnError = backgroundScope.launch { enError.uiState.collect {} }
        runCurrent()
        val error = enError.uiState.value

        observacionEnError.cancel()
        avanzar(6_000)
        backgroundScope.launch { enError.uiState.collect {} }
        runCurrent()

        assertEquals("en Error el repositorio tiene que llamarse una sola vez", listOf("w02"), repoConFalla.idsPedidos)
        assertEquals(error, enError.uiState.value)
    }
}
