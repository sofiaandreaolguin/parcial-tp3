package com.example.parcial_tp3.data.remote

import com.example.parcial_tp3.data.remote.dto.HealthDto
import retrofit2.Response
import retrofit2.http.GET

/**
 * Los endpoints del mock del profe (specs/API.md).
 *
 * Por ahora solo /health, para comprobar que la base del proyecto llega al servidor.
 * Los de /v1/workouts los suma la tarea g (catalogo) con sus DTO.
 */
interface VoraApi {

    @GET("health")
    suspend fun getHealth(): Response<HealthDto>
}
