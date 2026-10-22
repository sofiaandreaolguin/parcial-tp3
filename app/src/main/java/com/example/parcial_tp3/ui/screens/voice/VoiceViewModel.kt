package com.example.parcial_tp3.ui.screens.voice

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Lo que tarda el coach en "pensar" antes de contestar. */
private const val DEMORA_PROCESSING_MS = 2_000L

/**
 * El coach simulado de la ruta `voice`. No graba, no llama a ningun servidor y **no pide
 * permiso de microfono** (NAV05): todo es de demostracion.
 *
 * Processing alterna a proposito: la primera vuelta termina en Speaking y la segunda en
 * Error. El Figma no le da ninguna entrada al estado de error, y el profe tiene que poder
 * recorrer los cinco a mano (ficha docs/decisiones/005). Alterna en vez de sortear para
 * que la demo no dependa de la suerte y para poder testearlo.
 */
@HiltViewModel
class VoiceViewModel @Inject constructor() : ViewModel() {

    private val _estado = MutableStateFlow(VoiceUiState())
    val estado: StateFlow<VoiceUiState> = _estado.asStateFlow()

    private var vueltas = 0
    private var pensando: Job? = null

    fun onHablar() = aEscuchar(_estado.value.transcript)

    fun onSugerencia(@StringRes pregunta: Int) = aEscuchar(pregunta)

    fun onMicrofono() {
        _estado.value = _estado.value.copy(status = VoiceStatus.Processing)
        pensando?.cancel()
        pensando = viewModelScope.launch {
            delay(DEMORA_PROCESSING_MS)
            vueltas++
            _estado.value = _estado.value.copy(
                status = if (vueltas % 2 == 1) VoiceStatus.Speaking else VoiceStatus.Error,
            )
        }
    }

    fun onCancelar() = aIdle()

    fun onInterrumpir() = aIdle()

    fun onReintentar() = aIdle()

    private fun aEscuchar(@StringRes pregunta: Int) {
        pensando?.cancel()
        _estado.value = _estado.value.copy(status = VoiceStatus.Listening, transcript = pregunta)
    }

    private fun aIdle() {
        pensando?.cancel()
        _estado.value = _estado.value.copy(status = VoiceStatus.Idle)
    }
}
