package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.di.NetworkModule
import com.example.parcial_tp3.domain.model.CoachRole
import java.net.HttpURLConnection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * CoachRepositoryImpl con la VoraApi real contra MockWebServer (EXT01).
 * Spec: specs/funcionales/coach-con-historial.md, criterios C17 a C24.
 *
 * Los cuerpos son los que devuelve el mock del docente, verificados contra `mock/server.py`
 * y contra el servidor publicado el 26/10.
 */
class CoachRepositoryImplTest {

    private val server = MockWebServer()
    private lateinit var dao: FakeCoachDao
    private lateinit var repository: CoachRepositoryImpl

    private val sugerenciasOk = """
        {"data": [
          {"id": "s01", "text": "¿Cómo empiezo mi entrenamiento?"},
          {"id": "s02", "text": "¿Qué puedo cenar?"},
          {"id": "s03", "text": "¿Cómo reviso mi progreso?"}
        ]}
    """.trimIndent()

    private val respuestaOk = """
        {"data": {"role": "assistant",
                  "text": "Abrí el detalle del entrenamiento y registrá las series que completes.",
                  "topic": "workout", "isSimulated": true}}
    """.trimIndent()

    @Before
    fun setUp() {
        server.start()
        dao = FakeCoachDao()
        val retrofit = NetworkModule.provideRetrofit(OkHttpClient(), server.url("/").toString())
        repository = CoachRepositoryImpl(NetworkModule.provideVoraApi(retrofit), dao)
    }

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `C4 las sugerencias llegan en el orden del servidor`() = runTest {
        server.enqueue(MockResponse().setBody(sugerenciasOk))

        val resultado = repository.getSuggestions()

        assertEquals(listOf("s01", "s02", "s03"), resultado.getOrThrow().map { it.id })
    }

    @Test
    fun `C10 la respuesta guarda topic y isSimulated`() = runTest {
        server.enqueue(MockResponse().setBody(respuestaOk))

        val mensaje = repository.send("que entreno hoy?").getOrThrow()

        assertEquals(CoachRole.Assistant, mensaje.role)
        assertEquals("workout", mensaje.topic)
        assertTrue(mensaje.isSimulated)
    }

    @Test
    fun `C9 el mensaje propio se guarda antes de llamar al servidor`() = runTest {
        server.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAVAILABLE))

        repository.send("que entreno hoy?")

        val guardados = dao.observeAll().first()
        assertEquals(1, guardados.size)
        assertEquals("user", guardados.first().role)
    }

    @Test
    fun `C17 sin conexion da NO_CONNECTION`() = runTest {
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))

        val error = repository.send("hola").exceptionOrNull()

        assertTrue("se esperaba un AppException y llego: ${error}", error is AppException)
        assertEquals(AppError.NO_CONNECTION, (error as AppException).error)
    }

    @Test
    fun `C18 un 503 da SERVER`() = runTest {
        server.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAVAILABLE))

        val error = repository.send("hola").exceptionOrNull()

        assertTrue("se esperaba un AppException y llego: ${error}", error is AppException)
        assertEquals(AppError.SERVER, (error as AppException).error)
    }

    @Test
    fun `C19 un cuerpo malformed da INVALID_DATA`() = runTest {
        server.enqueue(MockResponse().setBody("""{"data": {"role":"assistant","text":"""))

        val error = repository.send("hola").exceptionOrNull()

        assertTrue("se esperaba un AppException y llego: ${error}", error is AppException)
        assertEquals(AppError.INVALID_DATA, (error as AppException).error)
    }

    @Test
    fun `C20 un 404 da NOT_FOUND`() = runTest {
        server.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND))

        val error = repository.send("hola").exceptionOrNull()

        assertTrue("se esperaba un AppException y llego: ${error}", error is AppException)
        assertEquals(AppError.NOT_FOUND, (error as AppException).error)
    }

    @Test
    fun `C21 una demora menor al timeout no es error`() = runTest {
        server.enqueue(
            MockResponse().setBody(respuestaOk).setBodyDelay(500, java.util.concurrent.TimeUnit.MILLISECONDS),
        )

        val resultado = repository.send("que entreno hoy?")

        assertTrue(resultado.isSuccess)
    }

    @Test
    fun `C22 un fallo de red no borra lo guardado`() = runTest {
        server.enqueue(MockResponse().setBody(respuestaOk))
        repository.send("primero")
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))

        repository.send("segundo")

        val guardados = dao.observeAll().first()
        assertTrue(guardados.any { it.text == "primero" })
        assertTrue(guardados.any { it.text == "segundo" })
    }

    @Test
    fun `C2 sembrar dos veces no duplica el hilo`() = runTest {
        val demo = listOf(
            com.example.parcial_tp3.domain.model.CoachMessage(
                role = CoachRole.User, text = "What should I eat for dinner?", isSimulated = true, createdAt = 1,
            ),
        )

        repository.seedIfEmpty(demo)
        repository.seedIfEmpty(demo)

        assertEquals(1, dao.observeAll().first().size)
    }

    /**
     * C23. El reintento manda otra vez el mismo texto. El repositorio mira si el ultimo del
     * hilo ya es esa misma pregunta sin responder: si lo es, es el envio que fallo y solo
     * repite la parte de red. Sin esto, cada reintento deja un mensaje propio repetido.
     */
    @Test
    fun `C23 reintentar el mismo texto no lo duplica en Room`() = runTest {
        server.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAVAILABLE))
        repository.send("que entreno hoy?")

        server.enqueue(MockResponse().setBody(respuestaOk))
        repository.send("que entreno hoy?")

        val propios = dao.observeAll().first().filter { it.role == "user" }
        assertEquals(1, propios.count { it.text == "que entreno hoy?" })
    }

    /**
     * La otra mitad del mismo criterio, para que el control de arriba no se pase de listo:
     * preguntar dos veces lo mismo **a proposito**, con la primera ya contestada, tiene que
     * dejar los dos mensajes. El ultimo del hilo es la respuesta de VORA, no la pregunta.
     */
    @Test
    fun `C23 preguntar dos veces lo mismo a proposito deja los dos mensajes`() = runTest {
        server.enqueue(MockResponse().setBody(respuestaOk))
        repository.send("que entreno hoy?")

        server.enqueue(MockResponse().setBody(respuestaOk))
        repository.send("que entreno hoy?")

        val propios = dao.observeAll().first().filter { it.role == "user" }
        assertEquals(2, propios.count { it.text == "que entreno hoy?" })
    }
}
