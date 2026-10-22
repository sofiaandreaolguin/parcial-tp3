package com.example.parcial_tp3.data.remote

import com.example.parcial_tp3.data.remote.dto.WorkoutDto
import com.example.parcial_tp3.di.NetworkModule
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * GET /v1/workouts con el Retrofit que arma NetworkModule, contra MockWebServer (RF01, RF06).
 * Spec: specs/funcionales/catalogo-de-entrenamientos.md.
 */
class WorkoutApiTest {

    private val server = MockWebServer()
    private lateinit var api: VoraApi

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
        api = NetworkModule.provideVoraApi(retrofit)
    }

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `C1 catalogo getWorkouts pide GET v1 workouts sin parametros`() = runTest {
        server.enqueue(MockResponse().setBody(cuerpoDelMock))

        api.getWorkouts()

        val pedido = server.takeRequest()
        assertEquals("GET", pedido.method)
        assertEquals("/v1/workouts", pedido.path)
    }

    @Test
    fun `C2 catalogo Gson convierte el JSON del mock en tres WorkoutDto`() = runTest {
        server.enqueue(MockResponse().setBody(cuerpoDelMock))

        val response = api.getWorkouts()

        assertTrue(response.isSuccessful)
        assertEquals(
            listOf(
                WorkoutDto(id = "w01", name = "Fuerza inicial", level = "beginner", durationMinutes = 25, exerciseCount = 3),
                WorkoutDto(id = "w02", name = "Entrenamiento completo", level = "intermediate", durationMinutes = 35, exerciseCount = 3),
                WorkoutDto(id = "w03", name = "Fuerza y constancia", level = "beginner", durationMinutes = 30, exerciseCount = 3),
            ),
            response.body()?.data,
        )
    }
}
