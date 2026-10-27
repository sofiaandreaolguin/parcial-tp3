package com.example.parcial_tp3.ui.screens.voice

import com.example.parcial_tp3.domain.model.CoachRole
import com.example.parcial_tp3.ui.components.VoraChatAuthor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * La siembra del hilo del frame (EXT01, C1).
 *
 * Antes esto no se podia probar: estaba adentro de una `@Composable` privada del Route. Se
 * saco a [hiloSembrado], que recibe los textos ya resueltos, y asi corre sin emulador.
 */
class HiloSembradoTest {

    private val textos = listOf("pregunta 1", "respuesta 1", "pregunta 2", "respuesta 2")

    @Test
    fun `C1 siembra los cuatro mensajes del frame`() {
        assertEquals(4, hiloSembrado(textos).size)
    }

    @Test
    fun `C1 respeta el orden del frame y alterna persona y VORA`() {
        val hilo = hiloSembrado(textos)

        assertEquals(textos, hilo.map { it.text })
        assertEquals(
            listOf(CoachRole.User, CoachRole.Assistant, CoachRole.User, CoachRole.Assistant),
            hilo.map { it.role },
        )
    }

    /** Si los roles salieran al reves, las burbujas propias y las de VORA cambiarian de lado. */
    @Test
    fun `C1 los roles salen de quien habla en el frame`() {
        val delFrame = HILO_DEMO.map { if (it.author == VoraChatAuthor.Me) CoachRole.User else CoachRole.Assistant }

        assertEquals(delFrame, hiloSembrado(textos).map { it.role })
    }

    @Test
    fun `C1 los cuatro quedan marcados como simulados`() {
        assertTrue(hiloSembrado(textos).all { it.isSimulated })
    }

    /** 1, 2, 3, 4: el repositorio sigue contando desde ahi, asi lo nuevo queda siempre despues. */
    @Test
    fun `C1 el orden es creciente y arranca en uno`() {
        assertEquals(listOf(1L, 2L, 3L, 4L), hiloSembrado(textos).map { it.createdAt })
    }

    @Test(expected = IllegalArgumentException::class)
    fun `C1 con una cantidad de textos que no es la del frame, corta`() {
        hiloSembrado(listOf("uno", "dos"))
    }
}
