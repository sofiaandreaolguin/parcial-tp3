package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.entity.CoachMessageEntity
import com.example.parcial_tp3.data.remote.dto.CoachReplyDto
import com.example.parcial_tp3.data.remote.dto.SuggestionDto
import com.example.parcial_tp3.domain.model.CoachMessage
import com.example.parcial_tp3.domain.model.CoachRole
import com.example.parcial_tp3.domain.model.CoachSuggestion

// DTO, modelo y Entity del coach estan separados (RF06); aca estan los pasajes entre ellos.
// Un campo nulo o un rol desconocido lanza IllegalArgumentException, igual que WorkoutMappers:
// no se inventa un valor y el repositorio hace fallar toda la consulta.

fun SuggestionDto.toDomain(): CoachSuggestion =
    CoachSuggestion(
        id = requireNotNull(id) { "SuggestionDto sin id" },
        text = requireNotNull(text) { "SuggestionDto sin text" },
    )

// isSimulated es la excepcion: si no viene se toma como false, porque su ausencia significa
// que el servidor no lo declara simulado (C10). Los demas campos si son obligatorios.
fun CoachReplyDto.toDomain(createdAt: Long): CoachMessage =
    CoachMessage(
        role = requireNotNull(role) { "CoachReplyDto sin role" }.toCoachRole(),
        text = requireNotNull(text) { "CoachReplyDto sin text" },
        topic = requireNotNull(topic) { "CoachReplyDto sin topic" },
        isSimulated = isSimulated ?: false,
        createdAt = createdAt,
    )

fun CoachMessageEntity.toDomain(): CoachMessage =
    CoachMessage(
        id = id,
        role = role.toCoachRole(),
        text = text,
        topic = topic,
        isSimulated = isSimulated,
        createdAt = createdAt,
    )

// El id no se copia: lo pone Room al insertar (autoGenerate).
fun CoachMessage.toEntity(): CoachMessageEntity =
    CoachMessageEntity(
        role = role.name.lowercase(),
        text = text,
        topic = topic,
        isSimulated = isSimulated,
        createdAt = createdAt,
    )

fun String.toCoachRole(): CoachRole = when (lowercase()) {
    "user" -> CoachRole.User
    "assistant" -> CoachRole.Assistant
    else -> throw IllegalArgumentException("Rol desconocido: $this")
}
