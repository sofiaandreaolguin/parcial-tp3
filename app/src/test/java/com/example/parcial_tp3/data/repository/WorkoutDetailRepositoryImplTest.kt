package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.entity.WorkoutEntity
import com.example.parcial_tp3.data.local.entity.WorkoutExerciseEntity
import com.example.parcial_tp3.di.NetworkModule
import com.example.parcial_tp3.domain.model.Exercise
import com.example.parcial_tp3.domain.model.WorkoutDetail
import com.example.parcial_tp3.domain.model.WorkoutLevel
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * WorkoutDetailRepositoryImpl con la VoraApi real (el Retrofit de NetworkModule) contra
 * MockWebServer y un FakeWorkoutDao (RF01, RF02, RF05, RF06, RF07).
 * Spec: specs/funcionales/detalle-y-cache-de-entrenamientos.md.
 */
class WorkoutDetailRepositoryImplTest {

    private val server = MockWebServer()
    private val dao = FakeWorkoutDao()
    private lateinit var repository: WorkoutDetailRepositoryImpl

    /** Cuerpo exacto del detalle de w01 de la seccion "Datos" de la spec. */
    private val cuerpoDelDetalle = """
        {"data": {
          "id": "w01", "name": "Fuerza inicial", "level": "beginner",
          "durationMinutes": 25, "exerciseCount": 3,
          "description": "Rutina de ejemplo para el trabajo práctico.",
          "exercises": [
            {"id": "e01", "name": "Sentadilla", "muscleGroup": "Piernas", "sets": 3, "reps": 10, "restSeconds": 60,
             "instructions": "Separá los pies y flexioná las rodillas con control.", "tip": "Usá una carga que puedas controlar."},
            {"id": "e02", "name": "Remo con mancuerna", "muscleGroup": "Espalda", "sets": 3, "reps": 10, "restSeconds": 60,
             "instructions": "Apoyá una mano y llevá la mancuerna hacia el torso.", "tip": "Usá una carga que puedas controlar."},
            {"id": "e03", "name": "Press con mancuernas", "muscleGroup": "Pecho", "sets": 3, "reps": 10, "restSeconds": 60,
             "instructions": "Empujá las mancuernas con un movimiento controlado.", "tip": "Usá una carga que puedas controlar."}
          ]
        }}
    """.trimIndent()

    private val sentadilla = Exercise(
        id = "e01", name = "Sentadilla", muscleGroup = "Piernas", sets = 3, reps = 10, restSeconds = 60,
        instructions = "Separá los pies y flexioná las rodillas con control.",
        tip = "Usá una carga que puedas controlar.",
    )
    private val remo = Exercise(
        id = "e02", name = "Remo con mancuerna", muscleGroup = "Espalda", sets = 3, reps = 10, restSeconds = 60,
        instructions = "Apoyá una mano y llevá la mancuerna hacia el torso.",
        tip = "Usá una carga que puedas controlar.",
    )
    private val press = Exercise(
        id = "e03", name = "Press con mancuernas", muscleGroup = "Pecho", sets = 3, reps = 10, restSeconds = 60,
        instructions = "Empujá las mancuernas con un movimiento controlado.",
        tip = "Usá una carga que puedas controlar.",
    )
    private val detalleW01 = WorkoutDetail(
        id = "w01",
        name = "Fuerza inicial",
        level = WorkoutLevel.BEGINNER,
        durationMinutes = 25,
        exerciseCount = 3,
        description = "Rutina de ejemplo para el trabajo práctico.",
        exercises = listOf(sentadilla, remo, press),
    )

    // w01 como queda en Room despues de descargarlo
    private val filaW01 = WorkoutEntity(
        "w01", "Fuerza inicial", "beginner", 25, 3, description = "Rutina de ejemplo para el trabajo práctico.",
    )
    private val ejerciciosW01 = listOf(sentadilla, remo, press).mapIndexed { posicion, ejercicio ->
        WorkoutExerciseEntity(
            workoutId = "w01",
            position = posicion,
            exerciseId = ejercicio.id,
            name = ejercicio.name,
            muscleGroup = ejercicio.muscleGroup,
            sets = ejercicio.sets,
            reps = ejercicio.reps,
            restSeconds = ejercicio.restSeconds,
            instructions = ejercicio.instructions,
            tip = ejercicio.tip,
        )
    }

    // w02 llego solo desde el catalogo: nunca se abrio su detalle
    private val filaW02DelCatalogo = WorkoutEntity("w02", "Entrenamiento completo", "intermediate", 35, 3, description = null)

    private fun falla503() = MockResponse()
        .setResponseCode(503)
        .setBody("""{"error": {"code": "SERVICE_UNAVAILABLE", "message": "Servicio simulado no disponible."}}""")

    /** Los fallos que no apagan el servidor ni son 404: C20, C22 y C24. */
    private fun respuestasQueFallan() = listOf(
        "503" to falla503(),
        "cuerpo nulo" to MockResponse().setResponseCode(204),
        "data null" to MockResponse().setBody("""{"data": null}"""),
        "sin data" to MockResponse().setBody("{}"),
        // El escenario malformed del mock: Gson tira MalformedJsonException
        "JSON roto" to MockResponse().setBody("""{"data": INVALID_JSON"""),
        // Cortado en medio de un objeto: Gson tira EOFException
        "cuerpo cortado" to MockResponse().setBody("""{"data": {"id": "w01""""),
        "JSON de otro tipo" to MockResponse().setBody("""{"data": [1, 2]}"""),
        "detalle con nivel invalido" to MockResponse().setBody(cuerpoDelDetalle.replace("\"beginner\"", "\"pro\"")),
        "detalle con un campo nulo" to MockResponse().setBody(
            cuerpoDelDetalle.replace("\"description\": \"Rutina de ejemplo para el trabajo práctico.\"", "\"description\": null"),
        ),
    )

    @Before
    fun setUp() {
        server.start()
        val retrofit = NetworkModule.provideRetrofit(OkHttpClient(), server.url("/").toString())
        repository = WorkoutDetailRepositoryImpl(NetworkModule.provideVoraApi(retrofit), dao)
    }

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `C19 cache detalle con 200 guarda en Room y lo devuelve`() = runTest {
        server.enqueue(MockResponse().setBody(cuerpoDelDetalle))

        val resultado = repository.getWorkoutDetail("w01")

        assertEquals(Result.success(detalleW01), resultado)
        assertEquals(listOf(filaW01), dao.filasGuardadas())
        assertEquals(ejerciciosW01, dao.ejerciciosGuardados())
    }

    @Test
    fun `C20 cache detalle que falla devuelve lo de Room`() = runTest {
        dao.precargar(listOf(filaW01), ejerciciosW01)

        for ((caso, respuesta) in respuestasQueFallan()) {
            server.enqueue(respuesta)
            assertEquals("con $caso", Result.success(detalleW01), repository.getWorkoutDetail("w01"))
        }
        server.shutdown()
        assertEquals("con el servidor apagado", Result.success(detalleW01), repository.getWorkoutDetail("w01"))
    }

    @Test
    fun `C21 cache detalle nunca descargado sin servidor falla con WorkoutNotCachedException`() = runTest {
        dao.precargar(listOf(filaW02DelCatalogo))
        server.shutdown()

        val sinFila = repository.getWorkoutDetail("w01")
        val soloDelCatalogo = repository.getWorkoutDetail("w02")

        val errorSinFila = sinFila.exceptionOrNull()
        assertTrue("sin fila tiene que ser WorkoutNotCachedException, fue $sinFila", errorSinFila is WorkoutNotCachedException)
        assertEquals("w01", (errorSinFila as WorkoutNotCachedException).workoutId)

        val errorDelCatalogo = soloDelCatalogo.exceptionOrNull()
        assertTrue(
            "con la fila del catalogo tiene que ser WorkoutNotCachedException, fue $soloDelCatalogo",
            errorDelCatalogo is WorkoutNotCachedException,
        )
        assertEquals("w02", (errorDelCatalogo as WorkoutNotCachedException).workoutId)
    }

    @Test
    fun `C22 cache detalle sin cache y sin falla de conexion no es WorkoutNotCachedException`() = runTest {
        for ((caso, respuesta) in respuestasQueFallan()) {
            server.enqueue(respuesta)

            val resultado = repository.getWorkoutDetail("w01")

            assertTrue("con $caso tiene que ser failure, fue $resultado", resultado.isFailure)
            assertFalse(
                "con $caso no es falta de conexion, fue ${resultado.exceptionOrNull()}",
                resultado.exceptionOrNull() is WorkoutNotCachedException,
            )
        }
    }

    @Test
    fun `C23 cache detalle con 404 falla sin leer Room y sin borrarlo`() = runTest {
        dao.precargar(listOf(filaW01), ejerciciosW01)
        server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("""{"error": {"code": "WORKOUT_NOT_FOUND", "message": "Entrenamiento no encontrado."}}"""),
        )

        val resultado = repository.getWorkoutDetail("w01")

        assertTrue("404 tiene que ser failure, fue $resultado", resultado.isFailure)
        assertFalse(
            "404 no es falta de conexion, fue ${resultado.exceptionOrNull()}",
            resultado.exceptionOrNull() is WorkoutNotCachedException,
        )
        assertEquals("con 404 no tiene que leer Room", 0, dao.lecturas)
        assertEquals(listOf(filaW01), dao.filasGuardadas())
        assertEquals(ejerciciosW01, dao.ejerciciosGuardados())
    }

    @Test
    fun `C24 cache detalle que falla deja Room igual`() = runTest {
        dao.precargar(listOf(filaW01, filaW02DelCatalogo), ejerciciosW01)

        for (id in listOf("w01", "w02", "w03")) {
            for ((caso, respuesta) in respuestasQueFallan()) {
                server.enqueue(respuesta)
                repository.getWorkoutDetail(id)
                assertEquals("filas con $caso pidiendo $id", listOf(filaW01, filaW02DelCatalogo), dao.filasGuardadas())
                assertEquals("ejercicios con $caso pidiendo $id", ejerciciosW01, dao.ejerciciosGuardados())
            }
        }
        server.shutdown()
        for (id in listOf("w01", "w02", "w03")) {
            repository.getWorkoutDetail(id)
            assertEquals("filas con el servidor apagado pidiendo $id", listOf(filaW01, filaW02DelCatalogo), dao.filasGuardadas())
            assertEquals("ejercicios con el servidor apagado pidiendo $id", ejerciciosW01, dao.ejerciciosGuardados())
        }
    }

    @Test
    fun `C25 cache detalle al cancelar relanza CancellationException`() = runTest {
        dao.precargar(listOf(filaW01), ejerciciosW01)
        server.enqueue(MockResponse().setBody(cuerpoDelDetalle).setHeadersDelay(3, TimeUnit.SECONDS))
        var resultado: Result<WorkoutDetail>? = null

        // Red de verdad: la consulta corre en IO y el test espera a que llegue al servidor
        val consulta = launch(Dispatchers.IO) { resultado = repository.getWorkoutDetail("w01") }
        val pedido = server.takeRequest(5, TimeUnit.SECONDS)
        consulta.cancelAndJoin()

        assertNotNull("la consulta tiene que llegar al servidor antes de cancelarla", pedido)
        assertTrue("la coroutine tiene que terminar cancelada", consulta.isCancelled)
        assertNull("al cancelar no tiene que devolver un Result, devolvio $resultado", resultado)
        assertEquals("al cancelar no tiene que leer Room", 0, dao.lecturas)
    }

    @Test
    fun `C26 cache detalle si guardar en Room falla devuelve lo de la red`() = runTest {
        dao.lanzarAlGuardar = true
        server.enqueue(MockResponse().setBody(cuerpoDelDetalle))

        val resultado = repository.getWorkoutDetail("w01")

        assertEquals(Result.success(detalleW01), resultado)
    }

    @Test
    fun `C27 cache detalle si leer Room falla devuelve failure sin WorkoutNotCachedException`() = runTest {
        dao.precargar(listOf(filaW01), ejerciciosW01)
        dao.lanzarAlLeer = true

        // Leer lanza, con 503 y con el servidor apagado
        server.enqueue(falla503())
        val alLeerCon503 = repository.getWorkoutDetail("w01")

        // Convertir lanza: una fila con un nivel que toDomain() no conoce
        val daoConFilaInvalida = FakeWorkoutDao().apply {
            precargar(listOf(filaW01.copy(level = "pro")), ejerciciosW01)
        }
        val retrofit = NetworkModule.provideRetrofit(OkHttpClient(), server.url("/").toString())
        val conFilaInvalida = WorkoutDetailRepositoryImpl(NetworkModule.provideVoraApi(retrofit), daoConFilaInvalida)
        server.enqueue(falla503())
        val alConvertir = conFilaInvalida.getWorkoutDetail("w01")

        server.shutdown()
        val alLeerSinServidor = repository.getWorkoutDetail("w01")

        for ((caso, resultado) in listOf(
            "leer Room con 503" to alLeerCon503,
            "convertir lo de Room" to alConvertir,
            "leer Room con el servidor apagado" to alLeerSinServidor,
        )) {
            assertTrue("si falla $caso tiene que ser failure, fue $resultado", resultado.isFailure)
            assertFalse(
                "si falla $caso no es WorkoutNotCachedException, fue ${resultado.exceptionOrNull()}",
                resultado.exceptionOrNull() is WorkoutNotCachedException,
            )
        }
    }
}
