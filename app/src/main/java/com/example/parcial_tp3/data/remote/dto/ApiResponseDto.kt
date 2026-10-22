package com.example.parcial_tp3.data.remote.dto

/** Envoltorio de todas las respuestas del mock: {"data": ...} (specs/API.md). */
data class ApiResponseDto<T>(
    val data: T?,
)
