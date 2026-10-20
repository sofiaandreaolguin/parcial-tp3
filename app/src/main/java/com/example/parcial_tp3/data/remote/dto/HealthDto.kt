package com.example.parcial_tp3.data.remote.dto

/** Forma del JSON de GET /health: {"status": "ok", "service": "vora-mock", "version": "1.0"}. */
data class HealthDto(
    val status: String,
    val service: String,
    val version: String,
)
