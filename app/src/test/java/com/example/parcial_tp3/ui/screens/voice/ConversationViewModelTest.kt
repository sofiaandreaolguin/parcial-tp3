package com.example.parcial_tp3.ui.screens.voice

import com.example.parcial_tp3.ui.components.VoraChatAuthor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** El hilo de Conversation. Cubre los criterios C24, C25 y C27. */
class ConversationViewModelTest {

    @Test
    fun `arranca con el hilo de demostracion del frame`() {
        assertEquals(HILO_DEMO, ConversationViewModel().estado.value.messages)
    }

    @Test
    fun `enviar agrega el mensaje al final y es propio`() {
        val vm = ConversationViewModel()
        val antes = vm.estado.value.messages.size
        vm.onEnviar("  una pregunta  ")
        val mensajes = vm.estado.value.messages
        assertEquals(antes + 1, mensajes.size)
        assertEquals(VoraChatAuthor.Me, mensajes.last().author)
        assertEquals("una pregunta", mensajes.last().text)
    }

    @Test
    fun `enviar vacio o en blanco no agrega nada`() {
        val vm = ConversationViewModel()
        val antes = vm.estado.value.messages.size
        vm.onEnviar("")
        vm.onEnviar("   ")
        assertEquals(antes, vm.estado.value.messages.size)
    }

    /** C27: la conversacion es de demostracion, VORA no contesta sola. */
    @Test
    fun `enviar no genera una respuesta automatica`() {
        val vm = ConversationViewModel()
        vm.onEnviar("hola")
        assertTrue(vm.estado.value.messages.none { it.author == VoraChatAuthor.Vora && it.textRes == null })
    }

    @Test
    fun `los identificadores no se repiten`() {
        val vm = ConversationViewModel()
        vm.onEnviar("una")
        vm.onEnviar("otra")
        val ids = vm.estado.value.messages.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }
}
