package com.example.parcial_tp3.data.repository

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
 * (RF01, RF06, RF07). Spec: specs/funcionales/catalogo-de-entrenamientos.md.
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
        repository = WorkoutRepositoryImpl(NetworkModule.provideVoraApi(retrofit))
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
}
