package com.example.parcial_tp3.ui.screens.voice

import androidx.annotation.StringRes
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.CoachMessage
import com.example.parcial_tp3.domain.model.CoachRole
import com.example.parcial_tp3.domain.model.CoachSuggestion
import com.example.parcial_tp3.ui.components.VoraChatAuthor

/**
 * Un mensaje del hilo. El id es Long porque sale de Room y es la key del LazyColumn.
 *
 * [textRes] lo usan la preview y el hilo sembrado antes de pasar por Room; [text] es lo que
 * llega de Room o del servidor, que no se traduce.
 */
data class ChatMessage(
    val id: Long,
    val author: VoraChatAuthor,
    @StringRes val textRes: Int? = null,
    val text: String = "",
    @StringRes val timeRes: Int? = null,
    @StringRes val audioRes: Int? = null,
    val isSimulated: Boolean = false,
)

/**
 * Los cuatro mensajes del frame Voice / 05. **Ya no es el estado inicial de la pantalla:**
 * se siembran en Room la primera vez (C1, C2) y desde ahi el hilo sale de la base. Se
 * conservan como claves porque son textos de la app, no del servidor.
 *
 * El Route los resuelve con stringResource y se los pasa al ViewModel: asi el ViewModel no
 * necesita Context y los textos siguen saliendo de strings.xml.
 */
val HILO_DEMO = listOf(
    ChatMessage(1, VoraChatAuthor.Me, R.string.conversation_demo_q1, timeRes = R.string.conversation_demo_t1),
    ChatMessage(2, VoraChatAuthor.Vora, R.string.conversation_demo_a1, audioRes = R.string.conversation_listen),
    ChatMessage(3, VoraChatAuthor.Me, R.string.conversation_demo_q2, timeRes = R.string.conversation_demo_t2),
    ChatMessage(4, VoraChatAuthor.Vora, R.string.conversation_demo_a2, timeRes = R.string.conversation_demo_t3),
)

/**
 * Lo que muestra la ruta `conversation`.
 *
 * [sugerencias] solo tiene algo mientras el hilo no pasó de lo sembrado (C6). [error] es una
 * clave de strings.xml, nunca una excepcion: la pantalla no ve errores de red (C24).
 *
 * [puedeReintentar] separa los dos errores: el que fallo en el servidor se puede reintentar
 * (C17 a C20), el texto demasiado largo no (C13), asi el boton no aparece sin hacer nada.
 */
data class ConversationUiState(
    val messages: List<ChatMessage> = emptyList(),
    val sugerencias: List<CoachSuggestion> = emptyList(),
    val enviando: Boolean = false,
    @StringRes val error: Int? = null,
    val sinSugerencias: Boolean = false,
    val puedeReintentar: Boolean = false,
)

/**
 * El hilo del frame listo para sembrarlo en Room (C1), con los textos ya resueltos.
 *
 * Es una función común y no vive dentro del Route **para poder probarla sin Compose**: el Route
 * resuelve los cuatro `@StringRes` (que sí necesita `stringResource`) y le pasa el texto hecho.
 *
 * @param textos los cuatro de [HILO_DEMO], en ese orden.
 */
fun hiloSembrado(textos: List<String>): List<CoachMessage> {
    require(textos.size == HILO_DEMO.size) {
        "El hilo del frame tiene ${HILO_DEMO.size} mensajes y llegaron ${textos.size}"
    }
    return HILO_DEMO.mapIndexed { posicion, mensaje ->
        CoachMessage(
            role = if (mensaje.author == VoraChatAuthor.Me) CoachRole.User else CoachRole.Assistant,
            text = textos[posicion],
            // los cuatro son texto inventado para el frame, no consejo real
            isSimulated = true,
            // 1, 2, 3, 4: la misma cuenta que sigue el repositorio, asi lo nuevo queda despues
            createdAt = (posicion + 1).toLong(),
        )
    }
}

/** De modelo de dominio a lo que dibuja la pantalla. Vive en ui/ y no toca data/ (reglas.sh). */
fun CoachMessage.toChatMessage(): ChatMessage = ChatMessage(
    id = id,
    author = if (role == CoachRole.User) VoraChatAuthor.Me else VoraChatAuthor.Vora,
    text = text,
    isSimulated = isSimulated,
)
