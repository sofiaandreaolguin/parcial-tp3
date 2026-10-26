package com.example.parcial_tp3.ui.screens.voice

import com.example.parcial_tp3.data.repository.CoachRepository
import com.example.parcial_tp3.domain.model.CoachMessage
import com.example.parcial_tp3.domain.model.CoachRole
import com.example.parcial_tp3.domain.model.CoachSuggestion
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Repositorio falso del coach. El historial es un StateFlow para poder comprobar que la
 * pantalla **observa** Room (C15): el test inserta y el estado tiene que emitir solo.
 *
 * Las respuestas se retienen con CompletableDeferred para poder mirar el estado "enviando"
 * antes de que lleguen, igual que FakeWorkoutRepository.
 */
class FakeCoachRepository : CoachRepository {

    val historial = MutableStateFlow<List<CoachMessage>>(emptyList())

    var sugerenciasResultado: Result<List<CoachSuggestion>> = Result.success(emptyList())
    var llamadasASugerencias = 0
        private set

    var envios = 0
        private set
    val textosEnviados = mutableListOf<String>()
    var sembrados: List<CoachMessage>? = null
        private set
    var vecesSembrado = 0
        private set

    /** Respuesta retenida del proximo send(); el test la completa cuando quiere. */
    var respuesta = CompletableDeferred<Result<CoachMessage>>()
        private set

    private val encoladas = ArrayDeque<CompletableDeferred<Result<CoachMessage>>>()

    fun encolarRespuesta(): CompletableDeferred<Result<CoachMessage>> =
        CompletableDeferred<Result<CoachMessage>>().also { encoladas.addLast(it) }

    /** Simula lo que hace Room al guardar: agrega al historial y emite. */
    fun agregarAlHistorial(mensaje: CoachMessage) {
        historial.value = historial.value + mensaje
    }

    override suspend fun getSuggestions(): Result<List<CoachSuggestion>> {
        llamadasASugerencias++
        return sugerenciasResultado
    }

    override fun observeHistory(): Flow<List<CoachMessage>> = historial

    override suspend fun send(text: String): Result<CoachMessage> {
        envios++
        textosEnviados += text
        agregarAlHistorial(
            CoachMessage(id = historial.value.size + 1L, role = CoachRole.User, text = text, createdAt = envios.toLong()),
        )
        val siguiente = if (envios == 1) respuesta else encoladas.removeFirstOrNull() ?: respuesta
        return siguiente.await()
    }

    override suspend fun seedIfEmpty(demo: List<CoachMessage>) {
        vecesSembrado++
        if (historial.value.isEmpty()) {
            sembrados = demo
            historial.value = demo
        }
    }
}
