package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.entity.WorkoutEntity
import com.example.parcial_tp3.data.local.entity.WorkoutExerciseEntity
import com.example.parcial_tp3.di.NetworkModule
import com.example.parcial_tp3.domain.model.Workout
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * WorkoutRepositoryImpl con la VoraApi real (el Retrofit de NetworkModule) contra MockWebServer
 * (RF01, RF02, RF05, RF06, RF07). Spec: specs/funcionales/catalogo-de-entrenamientos.md, con la
 * cache vacia, y specs/funcionales/detalle-y-cache-de-entrenamientos.md (los "cache").
 */
class WorkoutRepositoryImplTest {

    private val server = MockWebServer()
    private lateinit var repository: WorkoutRepositoryImpl

    /** Cuerpo exacto de la seccion "Datos" de la spec (datos del mock, specs/API.md). */
    private val cuerpoDelMock = """
        {"data": [
          {"id": "w01", "name": "Fuerza inicial", "level": "beginner", "durationMinutes": 25, "exerciseCount": 3},
          {"id": "w02", "name": "Entrenamiento completo", "level": "intermediate", "durationMinutes": 35, "exerciseCount": 3},
          {"id": "w03", "name": "Fuerza y constancia", "level": "beginner", "durationMinutes": 30, "exerciseCount": 3}
        ]}
    """.trimIndent()

    @Before
    fun setUp() {
        server.start()
        val retrofit = NetworkModule.provideRetrofit(OkHttpClient(), server.url("/").toString())
        repository = WorkoutRepositoryImpl(NetworkModule.provideVoraApi(retrofit), FakeWorkoutDao())
    }

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `C6 catalogo con 200 devuelve los tres Workout en orden`() = runTest {
        server.enqueue(MockResponse().setBody(cuerpoDelMock))

        val resultado = repository.getWorkouts()

        assertEquals(
            listOf(
                Workout(id = "w01", name = "Fuerza inicial", level = WorkoutLevel.BEGINNER, durationMinutes = 25, exerciseCount = 3),
                Workout(id = "w02", name = "Entrenamiento completo", level = WorkoutLevel.INTERMEDIATE, durationMinutes = 35, exerciseCount = 3),
                Workout(id = "w03", name = "Fuerza y constancia", level = WorkoutLevel.BEGINNER, durationMinutes = 30, exerciseCount = 3),
            ),
            resultado.getOrNull(),
        )
    }

    @Test
    fun `C7 catalogo con data vacia devuelve exito con lista vacia`() = runTest {
        server.enqueue(MockResponse().setBody("""{"data": []}"""))

        val resultado = repository.getWorkouts()

        assertEquals(Result.success(emptyList<Workout>()), resultado)
    }

    @Test
    fun `C8 catalogo con 503 devuelve failure sin lanzar`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(503)
                .setBody("""{"error": {"code": "SERVICE_UNAVAILABLE", "message": "Servicio simulado no disponible."}}"""),
        )

        val resultado = repository.getWorkouts()

        assertTrue("503 tiene que ser failure, fue $resultado", resultado.isFailure)
    }

    @Test
    fun `C9 catalogo sin data falla toda la consulta`() = runTest {
        // 2xx con cuerpo nulo: Retrofit no lee el cuerpo de un 204
        server.enqueue(MockResponse().setResponseCode(204))
        server.enqueue(MockResponse().setBody("""{"data": null}"""))
        server.enqueue(MockResponse().setBody("{}"))

        val conCuerpoNulo = repository.getWorkouts()
        val conDataNull = repository.getWorkouts()
        val sinData = repository.getWorkouts()

        assertTrue("cuerpo nulo tiene que ser failure, fue $conCuerpoNulo", conCuerpoNulo.isFailure)
        assertTrue("data null tiene que ser failure, fue $conDataNull", conDataNull.isFailure)
        assertTrue("sin data tiene que ser failure, fue $sinData", sinData.isFailure)
    }

    @Test
    fun `C9 catalogo con un item invalido falla toda la consulta`() = runTest {
        server.enqueue(
            MockResponse().setBody(
                """
                {"data": [
                  {"id": "w01", "name": "Fuerza inicial", "level": "beginner", "durationMinutes": 25, "exerciseCount": 3},
                  {"id": "w02", "name": "Entrenamiento completo", "level": "pro", "durationMinutes": 35, "exerciseCount": 3},
                  {"id": "w03", "name": "Fuerza y constancia", "level": "beginner", "durationMinutes": 30, "exerciseCount": 3}
                ]}
                """.trimIndent(),
            ),
        )

        val resultado = repository.getWorkouts()

        assertTrue("un item invalido tiene que hacer fallar todo, fue $resultado", resultado.isFailure)
    }

    @Test
    fun `C10 catalogo con servidor apagado devuelve failure sin lanzar`() = runTest {
        server.shutdown()

        val resultado = repository.getWorkouts()

        assertTrue("servidor apagado tiene que ser failure, fue $resultado", resultado.isFailure)
    }

    @Test
    fun `C10 catalogo con JSON roto devuelve failure sin lanzar`() = runTest {
        // Lo mismo que manda el escenario malformed del mock (specs/API.md)
        server.enqueue(MockResponse().setBody("""{"data": INVALID_JSON"""))

        val resultado = repository.getWorkouts()

        assertTrue("JSON roto tiene que ser failure, fue $resultado", resultado.isFailure)
    }

    @Test
    fun `C11 catalogo con JSON de otro tipo devuelve failure sin lanzar`() = runTest {
        server.enqueue(MockResponse().setBody("""{"data": {"id": "w01"}}"""))

        val resultado = repository.getWorkouts()

        assertTrue("JSON de otro tipo tiene que ser failure, fue $resultado", resultado.isFailure)
    }

    @Test
    fun `C12 catalogo al cancelar relanza CancellationException`() = runTest {
        server.enqueue(MockResponse().setBody(cuerpoDelMock).setHeadersDelay(3, TimeUnit.SECONDS))
        var resultado: Result<List<Workout>>? = null

        // Red de verdad: la consulta corre en IO y el test espera a que llegue al servidor
        val consulta = launch(Dispatchers.IO) { resultado = repository.getWorkouts() }
        val pedido = server.takeRequest(5, TimeUnit.SECONDS)
        consulta.cancelAndJoin()

        assertNotNull("la consulta tiene que llegar al servidor antes de cancelarla", pedido)
        assertTrue("la coroutine tiene que terminar cancelada", consulta.isCancelled)
        assertNull("al cancelar no tiene que devolver un Result, devolvio $resultado", resultado)
    }

    private val tresWorkouts = listOf(
        Workout(id = "w01", name = "Fuerza inicial", level = WorkoutLevel.BEGINNER, durationMinutes = 25, exerciseCount = 3),
        Workout(id = "w02", name = "Entrenamiento completo", level = WorkoutLevel.INTERMEDIATE, durationMinutes = 35, exerciseCount = 3),
        Workout(id = "w03", name = "Fuerza y constancia", level = WorkoutLevel.BEGINNER, durationMinutes = 30, exerciseCount = 3),
    )

    // Lo que ya estaba en Room. Distinto de lo que manda el servidor, para saber de donde salio.
    private val guardadoW02 = WorkoutEntity("w02", "Entrenamiento guardado", "advanced", 40, 4)
    private val guardadoW01 = WorkoutEntity(
        "w01", "Fuerza inicial", "beginner", 25, 3, description = "Rutina de ejemplo para el trabajo práctico.",
    )
    private val ejercicioGuardado = WorkoutExerciseEntity(
        workoutId = "w01", position = 0, exerciseId = "e01", name = "Sentadilla", muscleGroup = "Piernas",
        sets = 3, reps = 10, restSeconds = 60,
        instructions = "Separá los pies y flexioná las rodillas con control.",
        tip = "Usá una carga que puedas controlar.",
    )

    /** Los fallos de C13 y C16 que no apagan el servidor (C8 a C11 del catalogo). */
    private val respuestasQueFallan = listOf(
        "503" to MockResponse()
            .setResponseCode(503)
            .setBody("""{"error": {"code": "SERVICE_UNAVAILABLE", "message": "Servicio simulado no disponible."}}"""),
        "cuerpo nulo" to MockResponse().setResponseCode(204),
        "data null" to MockResponse().setBody("""{"data": null}"""),
        "sin data" to MockResponse().setBody("{}"),
        "JSON roto" to MockResponse().setBody("""{"data": INVALID_JSON"""),
        "JSON de otro tipo" to MockResponse().setBody("""{"data": {"id": "w01"}}"""),
        "item invalido" to MockResponse().setBody(
            """{"data": [{"id": "w01", "name": "Fuerza inicial", "level": "pro", "durationMinutes": 25, "exerciseCount": 3}]}""",
        ),
    )

    /** Un repositorio con su propio DAO falso: el de setUp queda con la cache vacia. */
    private fun repositorioCon(dao: FakeWorkoutDao): WorkoutRepositoryImpl {
        val retrofit = NetworkModule.provideRetrofit(OkHttpClient(), server.url("/").toString())
        return WorkoutRepositoryImpl(NetworkModule.provideVoraApi(retrofit), dao)
    }

    @Test
    fun `C12 cache catalogo con 200 guarda en Room y devuelve los tres`() = runTest {
        val dao = FakeWorkoutDao()
        server.enqueue(MockResponse().setBody(cuerpoDelMock))

        val resultado = repositorioCon(dao).getWorkouts()

        assertEquals(Result.success(tresWorkouts), resultado)
        assertEquals(
            listOf(
                WorkoutEntity("w01", "Fuerza inicial", "beginner", 25, 3, description = null),
                WorkoutEntity("w02", "Entrenamiento completo", "intermediate", 35, 3, description = null),
                WorkoutEntity("w03", "Fuerza y constancia", "beginner", 30, 3, description = null),
            ),
            dao.filasGuardadas(),
        )
    }

    @Test
    fun `C13 cache catalogo que falla devuelve lo de Room`() = runTest {
        val dao = FakeWorkoutDao()
        // Precargadas fuera de orden: lo de Room vuelve ordenado por id
        dao.precargar(listOf(guardadoW02, guardadoW01))
        val repositorio = repositorioCon(dao)
        val deRoom = listOf(
            Workout(id = "w01", name = "Fuerza inicial", level = WorkoutLevel.BEGINNER, durationMinutes = 25, exerciseCount = 3),
            Workout(id = "w02", name = "Entrenamiento guardado", level = WorkoutLevel.ADVANCED, durationMinutes = 40, exerciseCount = 4),
        )

        for ((caso, respuesta) in respuestasQueFallan) {
            server.enqueue(respuesta)
            assertEquals("con $caso", Result.success(deRoom), repositorio.getWorkouts())
        }
        server.shutdown()
        assertEquals("con el servidor apagado", Result.success(deRoom), repositorio.getWorkouts())
    }

    @Test
    fun `C15 cache catalogo vacio del servidor no borra Room`() = runTest {
        val dao = FakeWorkoutDao()
        dao.precargar(listOf(guardadoW01, guardadoW02), listOf(ejercicioGuardado))
        server.enqueue(MockResponse().setBody("""{"data": []}"""))

        val resultado = repositorioCon(dao).getWorkouts()

        assertEquals(Result.success(emptyList<Workout>()), resultado)
        assertEquals(listOf(guardadoW01, guardadoW02), dao.filasGuardadas())
        assertEquals(listOf(ejercicioGuardado), dao.ejerciciosGuardados())
    }

    @Test
    fun `C16 cache catalogo que falla deja Room igual`() = runTest {
        val dao = FakeWorkoutDao()
        dao.precargar(listOf(guardadoW01, guardadoW02), listOf(ejercicioGuardado))
        val repositorio = repositorioCon(dao)

        for ((caso, respuesta) in respuestasQueFallan) {
            server.enqueue(respuesta)
            repositorio.getWorkouts()
            assertEquals("filas con $caso", listOf(guardadoW01, guardadoW02), dao.filasGuardadas())
            assertEquals("ejercicios con $caso", listOf(ejercicioGuardado), dao.ejerciciosGuardados())
        }
        server.shutdown()
        repositorio.getWorkouts()
        assertEquals("filas con el servidor apagado", listOf(guardadoW01, guardadoW02), dao.filasGuardadas())
        assertEquals("ejercicios con el servidor apagado", listOf(ejercicioGuardado), dao.ejerciciosGuardados())
    }

    @Test
    fun `C17 cache catalogo si guardar en Room falla devuelve lo de la red`() = runTest {
        val dao = FakeWorkoutDao().apply { lanzarAlGuardar = true }
        server.enqueue(MockResponse().setBody(cuerpoDelMock))

        val resultado = repositorioCon(dao).getWorkouts()

        assertEquals(Result.success(tresWorkouts), resultado)
    }

    @Test
    fun `C18 cache catalogo si leer Room falla devuelve failure sin lanzar`() = runTest {
        val falla503 = MockResponse()
            .setResponseCode(503)
            .setBody("""{"error": {"code": "SERVICE_UNAVAILABLE", "message": "Servicio simulado no disponible."}}""")

        // Leer lanza
        val daoQueLanza = FakeWorkoutDao().apply {
            precargar(listOf(guardadoW01))
            lanzarAlLeer = true
        }
        server.enqueue(falla503)
        val alLeer = repositorioCon(daoQueLanza).getWorkouts()

        // Convertir lanza: una fila con un nivel que toDomain() no conoce
        val daoConFilaInvalida = FakeWorkoutDao().apply {
            precargar(listOf(WorkoutEntity("w01", "Fuerza inicial", "pro", 25, 3)))
        }
        server.enqueue(falla503)
        val alConvertir = repositorioCon(daoConFilaInvalida).getWorkouts()

        assertTrue("si leer Room lanza tiene que ser failure, fue $alLeer", alLeer.isFailure)
        assertTrue("si convertir lo de Room lanza tiene que ser failure, fue $alConvertir", alConvertir.isFailure)
    }
}
