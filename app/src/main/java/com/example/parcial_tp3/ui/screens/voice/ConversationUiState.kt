package com.example.parcial_tp3.ui.screens.voice

import androidx.annotation.StringRes
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraChatAuthor

/**
 * Un mensaje del hilo. Los de demostracion vienen de strings.xml ([textRes]) y los que
 * escribe la persona llegan como texto ([text]), que no se puede traducir.
 */
data class ChatMessage(
    val id: Int,
    val author: VoraChatAuthor,
    @StringRes val textRes: Int? = null,
    val text: String = "",
    @StringRes val timeRes: Int? = null,
    @StringRes val audioRes: Int? = null,
)

/** El hilo que muestra la ruta `conversation`, tal cual el frame Voice / 05. */
val HILO_DEMO = listOf(
    ChatMessage(1, VoraChatAuthor.Me, R.string.conversation_demo_q1, timeRes = R.string.conversation_demo_t1),
    ChatMessage(2, VoraChatAuthor.Vora, R.string.conversation_demo_a1, audioRes = R.string.conversation_listen),
    ChatMessage(3, VoraChatAuthor.Me, R.string.conversation_demo_q2, timeRes = R.string.conversation_demo_t2),
    ChatMessage(4, VoraChatAuthor.Vora, R.string.conversation_demo_a2, timeRes = R.string.conversation_demo_t3),
)

data class ConversationUiState(val messages: List<ChatMessage> = HILO_DEMO)
