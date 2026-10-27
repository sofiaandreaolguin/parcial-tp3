package com.example.parcial_tp3.domain.model

/** Quien escribio el mensaje. El servidor solo manda "assistant". */
enum class CoachRole { User, Assistant }

/**
 * Un mensaje del hilo del coach, como lo usa la app (EXT01).
 *
 * [topic] es nulo en los mensajes de la persona: lo clasifica el servidor.
 * [isSimulated] llega del servidor y se muestra en la burbuja (C11).
 */
data class CoachMessage(
    val id: Long = 0,
    val role: CoachRole,
    val text: String,
    val topic: String? = null,
    val isSimulated: Boolean = false,
    val createdAt: Long,
)

/** Una pregunta sugerida por el servidor. Es dato, no texto de la app: no va a strings.xml. */
data class CoachSuggestion(
    val id: String,
    val text: String,
)
