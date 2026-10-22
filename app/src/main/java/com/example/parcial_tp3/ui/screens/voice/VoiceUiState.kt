package com.example.parcial_tp3.ui.screens.voice

import androidx.annotation.StringRes
import com.example.parcial_tp3.R

/**
 * Los cinco momentos del coach. **No son rutas**: son estados de la misma pantalla
 * (ficha docs/decisiones/002), porque cambia el centro y el texto, no la estructura.
 */
enum class VoiceStatus { Idle, Listening, Processing, Speaking, Error }

/** Los cuatro chips de Voice / 01, en el orden del frame. */
val SUGERENCIAS = listOf(
    R.string.voice_q_workout,
    R.string.voice_q_breakfast,
    R.string.voice_q_dinner,
    R.string.voice_q_progress,
)

/**
 * Lo que muestra la ruta `voice`.
 *
 * Los textos de demostracion se guardan como claves de strings.xml y no como texto suelto:
 * asi la pantalla no tiene ni un literal y el espanol sale solo de values-es.
 */
data class VoiceUiState(
    val status: VoiceStatus = VoiceStatus.Idle,
    @StringRes val transcript: Int = R.string.voice_transcript_demo,
    @StringRes val reply: Int = R.string.voice_reply_demo,
    val suggestions: List<Int> = SUGERENCIAS,
    @StringRes val lastQuestion: Int = R.string.voice_last_question,
)
