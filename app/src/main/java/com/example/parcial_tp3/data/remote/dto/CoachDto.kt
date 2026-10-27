package com.example.parcial_tp3.data.remote.dto

/**
 * Lo que devuelve el servidor del coach (EXT01). Las tres vienen envueltas en
 * [ApiResponseDto], igual que el catalogo.
 *
 * Campos tal cual los manda el mock del docente (specs/API.md, specs/funcionales/
 * coach-con-historial.md). Los nulables son los que el servidor podria no mandar: el
 * mapper decide que hacer con cada uno.
 */
data class SuggestionDto(
    val id: String?,
    val text: String?,
)

/** Lo que se manda en POST /v1/coach/messages. `context` no se usa (fuera de alcance). */
data class CoachRequestDto(
    val text: String,
)

data class CoachReplyDto(
    val role: String?,
    val text: String?,
    val topic: String?,
    val isSimulated: Boolean?,
)
