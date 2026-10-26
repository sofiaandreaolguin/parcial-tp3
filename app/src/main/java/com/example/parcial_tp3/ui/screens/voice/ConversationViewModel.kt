package com.example.parcial_tp3.ui.screens.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.AppError
import com.example.parcial_tp3.data.repository.CoachRepository
import com.example.parcial_tp3.data.repository.toAppError
import com.example.parcial_tp3.domain.model.CoachMessage
import com.example.parcial_tp3.domain.model.CoachSuggestion
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Clock
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * El hilo de la ruta `conversation` (EXT01).
 *
 * El hilo sale de Room y VORA contesta por Retrofit. El orden de los mensajes **no** depende
 * de [reloj]: lo decide el repositorio contando lo que ya hay guardado, asi los tests no
 * dependen del reloj del sistema. [reloj] queda inyectado para cuando las burbujas muestren
 * la hora de verdad; hoy no se usa.
 */
@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val repo: CoachRepository,
    private val reloj: Clock,
) : ViewModel() {

    private val _estado = MutableStateFlow(ConversationUiState())
    val estado: StateFlow<ConversationUiState> = _estado.asStateFlow()

    private var empezado = false

    /** Cuantos mensajes dejo la siembra: con mas que eso, la conversacion ya arranco (C6). */
    private var sembrados = 0

    /** El texto del envio que fallo, para que reintentar mande ese y no el ultimo del hilo (C23). */
    private var textoQueFallo: String? = null

    /** Lo ultimo que contesto el GET. Los chips que se ven salen de aca, nunca al reves. */
    private var sugerenciasDelServidor: List<CoachSuggestion> = emptyList()

    /**
     * Una vez que la conversacion arranco, los chips no vuelven (C6).
     *
     * Hace falta una marca y no alcanza con mirar el hilo: el GET y el historial llegan por
     * caminos distintos, y al reabrir la app con mensajes guardados el historial emite
     * primero y el GET volvia despues a reponer los chips sobre la conversacion.
     */
    private var conversacionEmpezada = false

    /**
     * Siembra el hilo del frame si la tabla esta vacia, lo observa y pide las sugerencias.
     * Se ignora si ya se llamo: rotar no repite el GET (C3, C27).
     */
    fun empezar(demo: List<CoachMessage>) {
        if (empezado) return
        empezado = true
        sembrados = demo.size

        viewModelScope.launch {
            repo.seedIfEmpty(demo)
            repo.observeHistory().collect { hilo ->
                if (hilo.size > sembrados) conversacionEmpezada = true
                _estado.value = _estado.value.copy(
                    messages = hilo.map { it.toChatMessage() },
                    sugerencias = chips(),
                    sinSugerencias = _estado.value.sinSugerencias && !conversacionEmpezada,
                )
            }
        }
        viewModelScope.launch { pedirSugerencias() }
    }

    fun onEnviar(texto: String) {
        val limpio = texto.trim()
        if (limpio.isEmpty()) return
        if (limpio.length > MAXIMO) {
            // no hay nada que reintentar: el texto es invalido, no fallo el servidor
            _estado.value = _estado.value.copy(
                error = R.string.conversation_error_too_long,
                puedeReintentar = false,
            )
            return
        }
        enviar(limpio)
    }

    fun onSugerencia(sugerencia: CoachSuggestion) = onEnviar(sugerencia.text)

    fun onReintentar() {
        textoQueFallo?.let { enviar(it) }
    }

    /**
     * La marca de "enviando" se pone **antes** del launch: si se pusiera adentro, dos toques
     * seguidos pasarian los dos el control y mandarian dos veces el mismo mensaje (C26).
     */
    private fun enviar(texto: String) {
        if (_estado.value.enviando) return
        conversacionEmpezada = true
        _estado.value = _estado.value.copy(
            enviando = true,
            error = null,
            sugerencias = chips(),
            sinSugerencias = false,
        )

        viewModelScope.launch {
            val resultado = repo.send(texto)
            textoQueFallo = if (resultado.isFailure) texto else null
            _estado.value = _estado.value.copy(
                enviando = false,
                error = resultado.exceptionOrNull()?.let { mensajeDe(it) },
                puedeReintentar = resultado.isFailure,
            )
        }
    }

    /** Si fallan, la pantalla queda sin chips pero usable: no es un error de pantalla entera (C8). */
    private suspend fun pedirSugerencias() {
        val resultado = repo.getSuggestions()
        sugerenciasDelServidor = resultado.getOrNull().orEmpty()
        _estado.value = _estado.value.copy(
            sugerencias = chips(),
            sinSugerencias = resultado.isSuccess &&
                sugerenciasDelServidor.isEmpty() &&
                !conversacionEmpezada,
        )
    }

    /** El unico lugar que decide si se ven los chips. */
    private fun chips(): List<CoachSuggestion> =
        if (conversacionEmpezada) emptyList() else sugerenciasDelServidor

    private fun mensajeDe(error: Throwable): Int = when (error.toAppError()) {
        AppError.NO_CONNECTION -> R.string.conversation_error_no_connection
        AppError.INVALID_DATA -> R.string.conversation_error_invalid_data
        else -> R.string.conversation_error_server
    }

    private companion object {
        /** El servidor acepta hasta 2000 (specs/API.md); se corta antes de llamarlo (C13). */
        const val MAXIMO = 2000
    }
}
