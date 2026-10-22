package com.example.parcial_tp3.ui.screens.voice

import androidx.lifecycle.ViewModel
import com.example.parcial_tp3.ui.components.VoraChatAuthor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * El hilo de la ruta `conversation`. Es de demostracion: guarda lo que se escribe, pero
 * **VORA no contesta sola**, porque no hay servidor detras (eso seria EXT01).
 */
@HiltViewModel
class ConversationViewModel @Inject constructor() : ViewModel() {

    private val _estado = MutableStateFlow(ConversationUiState())
    val estado: StateFlow<ConversationUiState> = _estado.asStateFlow()

    private var siguienteId = HILO_DEMO.size + 1

    fun onEnviar(texto: String) {
        if (texto.isBlank()) return
        val mensaje = ChatMessage(siguienteId++, VoraChatAuthor.Me, text = texto.trim())
        _estado.value = _estado.value.copy(messages = _estado.value.messages + mensaje)
    }
}
