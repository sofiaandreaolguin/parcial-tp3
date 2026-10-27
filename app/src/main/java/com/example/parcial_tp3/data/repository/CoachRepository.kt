package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.domain.model.CoachMessage
import com.example.parcial_tp3.domain.model.CoachSuggestion
import kotlinx.coroutines.flow.Flow

/**
 * El coach (EXT01): Retrofit trae las sugerencias y la respuesta, Room guarda el historial.
 *
 * El hilo se **observa**: insertar un mensaje lo hace aparecer solo, sin volver a consultar (C15).
 */
interface CoachRepository {

    suspend fun getSuggestions(): Result<List<CoachSuggestion>>

    fun observeHistory(): Flow<List<CoachMessage>>

    /** Guarda el mensaje propio, llama al servidor y guarda la respuesta (C9, C10). */
    suspend fun send(text: String): Result<CoachMessage>

    /** Siembra el hilo de ejemplo del frame si la tabla esta vacia (C1, C2). */
    suspend fun seedIfEmpty(demo: List<CoachMessage>)
}
