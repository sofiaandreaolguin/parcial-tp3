package com.example.parcial_tp3.ui.screens.voice

import com.example.parcial_tp3.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Las transiciones del coach simulado. Son Kotlin puro, asi que corren sin emulador.
 * Cubren los criterios C2 a C10, C19 y C20 de specs/funcionales/asistente-vora.md.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VoiceViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun antes() = Dispatchers.setMain(dispatcher)

    @After
    fun despues() = Dispatchers.resetMain()

    @Test
    fun `empieza en Idle`() {
        assertEquals(VoiceStatus.Idle, VoiceViewModel().estado.value.status)
    }

    @Test
    fun `al toque de hablar pasa a Listening`() {
        val vm = VoiceViewModel()
        vm.onHablar()
        assertEquals(VoiceStatus.Listening, vm.estado.value.status)
    }

    @Test
    fun `el chip carga la pregunta y pasa a Listening`() {
        val vm = VoiceViewModel()
        vm.onSugerencia(R.string.voice_q_dinner)
        assertEquals(VoiceStatus.Listening, vm.estado.value.status)
        assertEquals(R.string.voice_q_dinner, vm.estado.value.transcript)
    }

    @Test
    fun `al soltar el microfono pasa a Processing`() {
        val vm = VoiceViewModel()
        vm.onHablar()
        vm.onMicrofono()
        assertEquals(VoiceStatus.Processing, vm.estado.value.status)
    }

    @Test
    fun `la primera vuelta de Processing termina en Speaking`() = runTest(dispatcher) {
        val vm = VoiceViewModel()
        vm.onHablar()
        vm.onMicrofono()
        advanceUntilIdle()
        assertEquals(VoiceStatus.Speaking, vm.estado.value.status)
    }

    @Test
    fun `la segunda vuelta de Processing termina en Error`() = runTest(dispatcher) {
        val vm = VoiceViewModel()
        vm.onHablar()
        vm.onMicrofono()
        advanceUntilIdle()
        vm.onInterrumpir()
        vm.onHablar()
        vm.onMicrofono()
        advanceUntilIdle()
        assertEquals(VoiceStatus.Error, vm.estado.value.status)
    }

    @Test
    fun `cancelar en Listening vuelve a Idle`() {
        val vm = VoiceViewModel()
        vm.onHablar()
        vm.onCancelar()
        assertEquals(VoiceStatus.Idle, vm.estado.value.status)
    }

    @Test
    fun `cancelar en Processing corta la espera y no llega a Speaking`() = runTest(dispatcher) {
        val vm = VoiceViewModel()
        vm.onHablar()
        vm.onMicrofono()
        vm.onCancelar()
        advanceUntilIdle()
        assertEquals(VoiceStatus.Idle, vm.estado.value.status)
    }

    @Test
    fun `interrumpir en Speaking vuelve a Idle`() = runTest(dispatcher) {
        val vm = VoiceViewModel()
        vm.onHablar()
        vm.onMicrofono()
        advanceUntilIdle()
        vm.onInterrumpir()
        assertEquals(VoiceStatus.Idle, vm.estado.value.status)
    }

    /**
     * C19 y C20: en el error, "Allow Access" y "Not Now" vuelven a Idle. La pantalla les
     * pasa este mismo evento justamente para que ninguno pueda pedir el permiso real.
     */
    @Test
    fun `reintentar desde el error vuelve a Idle`() = runTest(dispatcher) {
        val vm = VoiceViewModel()
        repeat(2) {
            vm.onHablar()
            vm.onMicrofono()
            advanceUntilIdle()
        }
        assertEquals(VoiceStatus.Error, vm.estado.value.status)
        vm.onReintentar()
        assertEquals(VoiceStatus.Idle, vm.estado.value.status)
    }
}
