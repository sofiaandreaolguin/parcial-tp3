package com.example.parcial_tp3.data.remote

import com.example.parcial_tp3.data.remote.dto.ExerciseDto
import com.example.parcial_tp3.data.remote.dto.WorkoutDetailDto
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
 * GET /v1/workouts y GET /v1/workouts/{workoutId} con el Retrofit que arma NetworkModule, contra
 * MockWebServer (RF01, RF06). Specs: specs/funcionales/catalogo-de-entrenamientos.md y
 * specs/funcionales/detalle-y-cache-de-entrenamientos.md (los "cache").
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

    /** Cuerpo exacto del detalle de w01 de la seccion "Datos" de la spec del detalle y la cache. */
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

    @Test
    fun `C1 cache getWorkoutDetail pide GET v1 workouts w01`() = runTest {
        server.enqueue(MockResponse().setBody(cuerpoDelDetalle))

        api.getWorkoutDetail("w01")

        val pedido = server.takeRequest()
        assertEquals("GET", pedido.method)
        // El path de MockWebServer incluye la query: compararlo entero comprueba que no hay parametros
        assertEquals("/v1/workouts/w01", pedido.path)
    }

    @Test
    fun `C2 cache Gson convierte el detalle del mock en WorkoutDetailDto`() = runTest {
        server.enqueue(MockResponse().setBody(cuerpoDelDetalle))

        val response = api.getWorkoutDetail("w01")

        assertTrue(response.isSuccessful)
        assertEquals(
            WorkoutDetailDto(
                id = "w01",
                name = "Fuerza inicial",
                level = "beginner",
                durationMinutes = 25,
                exerciseCount = 3,
                description = "Rutina de ejemplo para el trabajo práctico.",
                exercises = listOf(
                    ExerciseDto(
                        id = "e01", name = "Sentadilla", muscleGroup = "Piernas", sets = 3, reps = 10, restSeconds = 60,
                        instructions = "Separá los pies y flexioná las rodillas con control.",
                        tip = "Usá una carga que puedas controlar.",
                    ),
                    ExerciseDto(
                        id = "e02", name = "Remo con mancuerna", muscleGroup = "Espalda", sets = 3, reps = 10, restSeconds = 60,
                        instructions = "Apoyá una mano y llevá la mancuerna hacia el torso.",
                        tip = "Usá una carga que puedas controlar.",
                    ),
                    ExerciseDto(
                        id = "e03", name = "Press con mancuernas", muscleGroup = "Pecho", sets = 3, reps = 10, restSeconds = 60,
                        instructions = "Empujá las mancuernas con un movimiento controlado.",
                        tip = "Usá una carga que puedas controlar.",
                    ),
                ),
            ),
            response.body()?.data,
        )
    }
}
