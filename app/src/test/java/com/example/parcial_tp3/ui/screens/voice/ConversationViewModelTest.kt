package com.example.parcial_tp3.ui.screens.voice

import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.AppError
import com.example.parcial_tp3.data.repository.AppException
import com.example.parcial_tp3.domain.model.CoachMessage
import com.example.parcial_tp3.domain.model.CoachRole
import com.example.parcial_tp3.domain.model.CoachSuggestion
import com.example.parcial_tp3.util.MainDispatcherRule
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * ConversationViewModel (Voice / 05) con un CoachRepository falso.
 *
 * **Reemplaza a los tests de la conversacion de demostracion.** El de la respuesta automatica
 * se borro a proposito: C27 de `asistente-vora.md` ("la app NO DEBE responder sola") queda
 * reemplazado por C10 de `coach-con-historial.md`. Lo decidio Fiore el 26/10 y esta en la
 * seccion "Impacto en lo que ya esta" de esa spec.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ConversationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val reloj = Clock.fixed(Instant.parse("2026-10-26T12:00:00Z"), ZoneOffset.UTC)

    private val demo = listOf(
        CoachMessage(1, CoachRole.User, "What should I eat for dinner?", isSimulated = true, createdAt = 1),
        CoachMessage(2, CoachRole.Assistant, "You're currently 36g short...", isSimulated = true, createdAt = 2),
    )

    private val sugerencias = listOf(
        CoachSuggestion("s01", "¿Cómo empiezo mi entrenamiento?"),
        CoachSuggestion("s02", "¿Qué puedo cenar?"),
        CoachSuggestion("s03", "¿Cómo reviso mi progreso?"),
    )

    private fun vm(repo: FakeCoachRepository) = ConversationViewModel(repo, reloj)

    private fun respuesta(texto: String, topic: String = "workout") =
        CoachMessage(99, CoachRole.Assistant, texto, topic, isSimulated = true, createdAt = 99)

    /** Manda un mensaje, lo hace fallar con [error] y devuelve el estado que quedo (C18 a C20). */
    private fun TestScope.estadoTrasFallar(error: AppError): ConversationUiState {
        val repo = FakeCoachRepository()
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }
        modelo.empezar(demo)
        runCurrent()

        modelo.onEnviar("que entreno hoy?")
        runCurrent()
        repo.respuesta.complete(Result.failure(AppException(error)))
        runCurrent()

        trabajo.cancel()
        return modelo.estado.value
    }

    @Test
    fun `C3 pide las sugerencias una sola vez por viewmodel`() = runTest {
        val repo = FakeCoachRepository().apply { sugerenciasResultado = Result.success(sugerencias) }
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }

        modelo.empezar(demo)
        runCurrent()
        modelo.empezar(demo)
        runCurrent()

        assertEquals(1, repo.llamadasASugerencias)
        trabajo.cancel()
    }

    @Test
    fun `C4 las sugerencias conservan el orden del servidor`() = runTest {
        val repo = FakeCoachRepository().apply { sugerenciasResultado = Result.success(sugerencias) }
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }

        modelo.empezar(demo)
        runCurrent()

        assertEquals(listOf("s01", "s02", "s03"), modelo.estado.value.sugerencias.map { it.id })
        trabajo.cancel()
    }

    @Test
    fun `C5 tocar el chip manda el texto de esa sugerencia`() = runTest {
        val repo = FakeCoachRepository().apply { sugerenciasResultado = Result.success(sugerencias) }
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }
        modelo.empezar(demo)
        runCurrent()

        modelo.onSugerencia(sugerencias[1])
        runCurrent()

        assertEquals(listOf("¿Qué puedo cenar?"), repo.textosEnviados)
        trabajo.cancel()
    }

    @Test
    fun `C6 con mensajes propios no quedan sugerencias`() = runTest {
        val repo = FakeCoachRepository().apply { sugerenciasResultado = Result.success(sugerencias) }
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }
        modelo.empezar(demo)
        runCurrent()

        modelo.onEnviar("hola")
        runCurrent()

        assertTrue(modelo.estado.value.sugerencias.isEmpty())
        trabajo.cancel()
    }

    /**
     * El de arriba manda un mensaje, y eso vacia los chips por el camino de `enviar()`. Este
     * hace crecer el hilo **sin pasar por `onEnviar`**, que es el otro camino y el que de
     * verdad pide C6. Con los dos, romper cualquiera de los dos se nota.
     */
    @Test
    fun `C6 un mensaje que llega al hilo sin enviarlo tambien saca los chips`() = runTest {
        val repo = FakeCoachRepository().apply { sugerenciasResultado = Result.success(sugerencias) }
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }
        modelo.empezar(demo)
        runCurrent()
        assertEquals(3, modelo.estado.value.sugerencias.size)

        repo.agregarAlHistorial(respuesta("llegó sola"))
        runCurrent()

        assertTrue(modelo.estado.value.sugerencias.isEmpty())
        trabajo.cancel()
    }

    /**
     * El caso que se vio en el emulador el 26/10 y que ningun test detectaba: se reabre la app
     * con la conversacion ya empezada, el historial llega primero (es local) y el GET despues,
     * y el GET reponia los chips encima de una conversacion con mensajes propios.
     */
    @Test
    fun `C6 al reabrir con la conversacion empezada no vuelven los chips`() = runTest {
        val repo = FakeCoachRepository().apply { sugerenciasResultado = Result.success(sugerencias) }
        // la base ya tiene lo sembrado y ademas un mensaje de la persona de la vez anterior
        repo.historial.value = demo + CoachMessage(
            id = 3, role = CoachRole.User, text = "lo de ayer", createdAt = 3,
        )
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }

        modelo.empezar(demo)
        runCurrent()

        assertTrue(
            "volvieron los chips sobre una conversacion ya empezada",
            modelo.estado.value.sugerencias.isEmpty(),
        )
        trabajo.cancel()
    }

    @Test
    fun `C7 sugerencias vacias muestran el aviso y no dejan chips`() = runTest {
        val repo = FakeCoachRepository().apply { sugerenciasResultado = Result.success(emptyList()) }
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }

        modelo.empezar(demo)
        runCurrent()

        assertTrue(modelo.estado.value.sugerencias.isEmpty())
        assertTrue(modelo.estado.value.sinSugerencias)
        trabajo.cancel()
    }

    @Test
    fun `C8 si fallan las sugerencias la pantalla sigue usable`() = runTest {
        val repo = FakeCoachRepository().apply {
            sugerenciasResultado = Result.failure(AppException(AppError.NO_CONNECTION))
        }
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }

        modelo.empezar(demo)
        runCurrent()

        assertTrue(modelo.estado.value.sugerencias.isEmpty())
        assertEquals(null, modelo.estado.value.error)
        assertFalse(modelo.estado.value.enviando)
        trabajo.cancel()
    }

    @Test
    fun `C9 enviar guarda el mensaje propio antes de la respuesta`() = runTest {
        val repo = FakeCoachRepository()
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }
        modelo.empezar(demo)
        runCurrent()

        modelo.onEnviar("que entreno hoy?")
        runCurrent()

        assertTrue(modelo.estado.value.messages.any { it.text == "que entreno hoy?" })
        trabajo.cancel()
    }

    @Test
    fun `C11 la respuesta simulada se marca en la burbuja`() = runTest {
        val repo = FakeCoachRepository()
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }
        modelo.empezar(demo)
        runCurrent()

        modelo.onEnviar("que entreno hoy?")
        runCurrent()
        val reply = respuesta("Abrí el detalle del entrenamiento...")
        repo.agregarAlHistorial(reply)
        repo.respuesta.complete(Result.success(reply))
        runCurrent()

        val ultimo = modelo.estado.value.messages.lastOrNull()
        assertTrue("el hilo quedo vacio: no hay burbuja que marcar", ultimo != null)
        assertTrue(ultimo!!.isSimulated)
        trabajo.cancel()
    }

    @Test
    fun `C12 texto vacio o en blanco no llama al servidor`() = runTest {
        val repo = FakeCoachRepository()
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }
        modelo.empezar(demo)
        runCurrent()

        modelo.onEnviar("")
        modelo.onEnviar("   ")
        runCurrent()

        assertEquals(0, repo.envios)
        trabajo.cancel()
    }

    @Test
    fun `C13 texto de mas de 2000 no llama al servidor y avisa`() = runTest {
        val repo = FakeCoachRepository()
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }
        modelo.empezar(demo)
        runCurrent()

        modelo.onEnviar("a".repeat(2001))
        runCurrent()

        assertEquals(0, repo.envios)
        assertEquals(R.string.conversation_error_too_long, modelo.estado.value.error)
        trabajo.cancel()
    }

    @Test
    fun `C15 insertar en el historial emite sin volver a consultar`() = runTest {
        val repo = FakeCoachRepository()
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }
        modelo.empezar(demo)
        runCurrent()
        val antes = modelo.estado.value.messages.size

        repo.agregarAlHistorial(respuesta("llegó sola"))
        runCurrent()

        assertEquals(antes + 1, modelo.estado.value.messages.size)
        trabajo.cancel()
    }

    @Test
    fun `C22 un fallo de envio no borra el historial`() = runTest {
        val repo = FakeCoachRepository()
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }
        modelo.empezar(demo)
        runCurrent()

        modelo.onEnviar("que entreno hoy?")
        runCurrent()
        repo.respuesta.complete(Result.failure(AppException(AppError.NO_CONNECTION)))
        runCurrent()

        assertTrue(modelo.estado.value.messages.any { it.text == "que entreno hoy?" })
        assertEquals(R.string.conversation_error_no_connection, modelo.estado.value.error)
        trabajo.cancel()
    }

    @Test
    fun `C23 reintentar reenvia el texto que fallo`() = runTest {
        val repo = FakeCoachRepository()
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }
        modelo.empezar(demo)
        runCurrent()
        modelo.onEnviar("que entreno hoy?")
        runCurrent()
        repo.respuesta.complete(Result.failure(AppException(AppError.SERVER)))
        runCurrent()

        val segunda = repo.encolarRespuesta()
        modelo.onReintentar()
        runCurrent()
        segunda.complete(Result.success(respuesta("ahora si")))
        runCurrent()

        assertEquals(listOf("que entreno hoy?", "que entreno hoy?"), repo.textosEnviados)
        assertEquals(null, modelo.estado.value.error)
        trabajo.cancel()
    }

    @Test
    fun `C25 mientras envia el estado lo expone`() = runTest {
        val repo = FakeCoachRepository()
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }
        modelo.empezar(demo)
        runCurrent()

        modelo.onEnviar("que entreno hoy?")
        runCurrent()

        assertTrue(modelo.estado.value.enviando)
        trabajo.cancel()
    }

    @Test
    fun `C26 dos toques seguidos mandan un solo post`() = runTest {
        val repo = FakeCoachRepository()
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }
        modelo.empezar(demo)
        runCurrent()

        modelo.onEnviar("que entreno hoy?")
        modelo.onEnviar("que entreno hoy?")
        runCurrent()

        assertEquals(1, repo.envios)
        trabajo.cancel()
    }

    @Test
    fun `C28 rotar durante el envio no repite el post`() = runTest {
        val repo = FakeCoachRepository()
        val modelo = vm(repo)
        val primera = launch { modelo.estado.collect {} }
        modelo.empezar(demo)
        runCurrent()
        modelo.onEnviar("que entreno hoy?")
        runCurrent()

        // rotar: se va el colector viejo y entra uno nuevo; el ViewModel sobrevive
        primera.cancel()
        val segunda = launch { modelo.estado.collect {} }
        runCurrent()

        assertEquals(1, repo.envios)
        assertTrue(modelo.estado.value.enviando)
        segunda.cancel()
    }

    /**
     * C18 a C20: el repositorio ya traduce la excepcion a AppError; lo que no estaba probado
     * es el salto siguiente, de AppError a la clave de strings.xml que ve la pantalla. Solo
     * NO_CONNECTION tenia test, y los otros tres caen en la rama `else`.
     */
    @Test
    fun `C18 un 503 muestra el texto de servidor y deja reintentar`() = runTest {
        val estado = estadoTrasFallar(AppError.SERVER)

        assertEquals(R.string.conversation_error_server, estado.error)
        assertTrue(estado.puedeReintentar)
    }

    @Test
    fun `C19 un cuerpo que no se puede leer muestra su propio texto`() = runTest {
        val estado = estadoTrasFallar(AppError.INVALID_DATA)

        assertEquals(R.string.conversation_error_invalid_data, estado.error)
        assertTrue(estado.puedeReintentar)
    }

    @Test
    fun `C20 un 404 tambien muestra el texto de servidor`() = runTest {
        val estado = estadoTrasFallar(AppError.NOT_FOUND)

        assertEquals(R.string.conversation_error_server, estado.error)
        assertTrue(estado.puedeReintentar)
    }

    /** Un error desconocido no puede dejar la pantalla sin explicacion. */
    @Test
    fun `C24 un error desconocido igual muestra un texto`() = runTest {
        val estado = estadoTrasFallar(AppError.UNKNOWN)

        assertEquals(R.string.conversation_error_server, estado.error)
    }

    /**
     * El texto demasiado largo no se puede reintentar: no fallo el servidor, el mensaje es
     * invalido. Si quedara como reintentable, la pantalla dibuja un boton que no hace nada.
     */
    @Test
    fun `C13 el texto demasiado largo no ofrece reintentar`() = runTest {
        val repo = FakeCoachRepository()
        val modelo = vm(repo)
        val trabajo = launch { modelo.estado.collect {} }
        modelo.empezar(demo)
        runCurrent()

        modelo.onEnviar("a".repeat(2001))
        runCurrent()

        assertEquals(R.string.conversation_error_too_long, modelo.estado.value.error)
        assertFalse(modelo.estado.value.puedeReintentar)
        trabajo.cancel()
    }
}
