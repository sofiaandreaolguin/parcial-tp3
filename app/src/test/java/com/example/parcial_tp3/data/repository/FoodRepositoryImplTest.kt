package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.remote.VoraApi
import com.example.parcial_tp3.data.remote.dto.FoodsResponseDto
import com.example.parcial_tp3.di.NetworkModule
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * FoodRepositoryImpl con la VoraApi real contra MockWebServer (EXT02).
 * Spec: specs/funcionales/busqueda-de-alimentos.md, C1 a C7.
 *
 * Los cuerpos son los que devuelve el servidor del docente, comprobados el 26/10 contra
 * https://tp3.martinrivas.ar/v1/foods.
 */
class FoodRepositoryImplTest {

    private val server = MockWebServer()
    private lateinit var repository: FoodRepositoryImpl

    private val dosAlimentos = """
        {"data": [
          {"id": "f05", "name": "Pollo cocido",
           "nutritionPer100g": {"kcal": 165, "proteinG": 31, "carbsG": 0, "fatG": 3.6}},
          {"id": "f12", "name": "Aceite de oliva",
           "nutritionPer100g": {"kcal": 884, "proteinG": 0, "carbsG": 0, "fatG": 100}}
         ],
         "meta": {"page": 1, "pageSize": 50, "total": 12}}
    """.trimIndent()

    @Before
    fun setUp() {
        server.start()
        val retrofit = NetworkModule.provideRetrofit(OkHttpClient(), server.url("/").toString())
        repository = FoodRepositoryImpl(NetworkModule.provideVoraApi(retrofit))
    }

    @After
    fun tearDown() = server.shutdown()

    private fun error(appError: AppError, resultado: Result<*>) {
        val e = resultado.exceptionOrNull()
        assertTrue("esperaba un fallo y llego $resultado", resultado.isFailure)
        assertTrue("esperaba AppException y llego $e", e is AppException)
        assertEquals(appError, (e as AppException).error)
    }

    @Test
    fun `C1 buscar pide v1 foods con q page y pageSize`() = runTest {
        server.enqueue(MockResponse().setBody(dosAlimentos))

        repository.searchFoods("pollo", page = 1)

        val pedido = server.takeRequest(2, TimeUnit.SECONDS)
        assertEquals("/v1/foods?q=pollo&page=1&pageSize=50", pedido?.path)
    }

    @Test
    fun `C2 la respuesta se convierte en FoodPage con nutrientes y meta`() = runTest {
        server.enqueue(MockResponse().setBody(dosAlimentos))

        val pagina = repository.searchFoods("", page = 1).getOrNull()

        assertEquals(2, pagina?.items?.size)
        val pollo = pagina?.items?.first()
        assertEquals("f05", pollo?.id)
        assertEquals("Pollo cocido", pollo?.name)
        assertEquals(165, pollo?.per100g?.kcal)
        assertEquals(31.0, pollo?.per100g?.proteinG ?: 0.0, 0.0001)
        assertEquals(0.0, pollo?.per100g?.carbsG ?: -1.0, 0.0001)
        assertEquals(3.6, pollo?.per100g?.fatG ?: 0.0, 0.0001)
        assertEquals(1, pagina?.page)
        assertEquals(50, pagina?.pageSize)
        assertEquals(12, pagina?.total)
    }

    @Test
    fun `C3 sin servidor falla con NO_CONNECTION`() = runTest {
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))

        error(AppError.NO_CONNECTION, repository.searchFoods("pollo", page = 1))
    }

    @Test
    fun `C4 un 503 y un 400 fallan con SERVER`() = runTest {
        server.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAVAILABLE))
        error(AppError.SERVER, repository.searchFoods("pollo", page = 1))

        server.enqueue(
            MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .setBody("""{"error": {"code": "INVALID_REQUEST", "message": "pageSize debe estar entre 1 y 50."}}"""),
        )
        error(AppError.SERVER, repository.searchFoods("pollo", page = 1))
    }

    @Test
    fun `C5 json roto falla con INVALID_DATA y no con NO_CONNECTION`() = runTest {
        // Lo que devuelve el mock con X-Mock-Scenario: malformed. MalformedJsonException ES una
        // IOException, asi que si se atrapa en el orden equivocado sale NO_CONNECTION.
        server.enqueue(MockResponse().setBody("""{"data": INVALID_JSON"""))

        error(AppError.INVALID_DATA, repository.searchFoods("pollo", page = 1))
    }

    @Test
    fun `C6 si falta un campo toda la consulta falla con INVALID_DATA`() = runTest {
        val casos = listOf(
            """{"meta": {"page": 1, "pageSize": 50, "total": 12}}""",
            """{"data": [{"id": "f05", "name": "Pollo cocido", "nutritionPer100g": {"kcal": 165, "proteinG": 31, "carbsG": 0, "fatG": 3.6}}]}""",
            """{"data": [{"id": "f05", "name": "Pollo cocido"}], "meta": {"page": 1, "pageSize": 50, "total": 1}}""",
            """{"data": [{"name": "Pollo cocido", "nutritionPer100g": {"kcal": 165, "proteinG": 31, "carbsG": 0, "fatG": 3.6}}], "meta": {"page": 1, "pageSize": 50, "total": 1}}""",
            """{"data": [{"id": "f05", "nutritionPer100g": {"kcal": 165, "proteinG": 31, "carbsG": 0, "fatG": 3.6}}], "meta": {"page": 1, "pageSize": 50, "total": 1}}""",
            """{"data": [{"id": "f05", "name": "Pollo", "nutritionPer100g": {"proteinG": 31, "carbsG": 0, "fatG": 3.6}}], "meta": {"page": 1, "pageSize": 50, "total": 1}}""",
            // meta presente pero incompleto: sin esto, total queda en 0 y se ve como "no hay resultados".
            """{"data": [{"id": "f05", "name": "Pollo", "nutritionPer100g": {"kcal": 165, "proteinG": 31, "carbsG": 0, "fatG": 3.6}}], "meta": {"page": 1}}""",
        )
        for (cuerpo in casos) {
            server.enqueue(MockResponse().setBody(cuerpo))
            val resultado = repository.searchFoods("pollo", page = 1)
            assertTrue("deberia fallar con: $cuerpo", resultado.isFailure)
            assertEquals(
                "con: $cuerpo",
                AppError.INVALID_DATA,
                (resultado.exceptionOrNull() as? AppException)?.error,
            )
        }
    }

    @Test
    fun `C7 cancelar la busqueda no se traduce a AppError`() = runTest {
        // Con debounce se cancela una consulta por cada tecla. Si CancellationException se
        // atrapa como cualquier otra, cada letra deja un fallo UNKNOWN y la pantalla parpadea.
        val repo = FoodRepositoryImpl(ApiQueCancela)

        try {
            repo.searchFoods("pollo", page = 1)
            fail("esperaba que la CancellationException saliera, no que se convirtiera en Result")
        } catch (e: CancellationException) {
            assertEquals("cancelada a proposito", e.message)
        }
    }
}

/** Solo sirve para C7: todo lo demas de la interfaz no se usa en este test. */
private object ApiQueCancela : VoraApi {
    override suspend fun getFoods(q: String, page: Int, pageSize: Int): Response<FoodsResponseDto> =
        throw CancellationException("cancelada a proposito")

    override suspend fun getHealth() = error("no se usa")
    override suspend fun getWorkouts() = error("no se usa")
    override suspend fun getWorkoutDetail(workoutId: String) = error("no se usa")
    override suspend fun getCoachSuggestions() = error("no se usa")
    override suspend fun postCoachMessage(body: com.example.parcial_tp3.data.remote.dto.CoachRequestDto) =
        error("no se usa")
}
