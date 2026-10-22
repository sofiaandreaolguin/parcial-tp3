package com.example.parcial_tp3.data.remote

import com.example.parcial_tp3.data.remote.dto.ApiResponseDto
import com.example.parcial_tp3.data.remote.dto.HealthDto
import com.example.parcial_tp3.data.remote.dto.WorkoutDto
import retrofit2.Response
import retrofit2.http.GET

/**
 * Los endpoints del mock del profe (specs/API.md).
 *
 * /health comprueba que la base del proyecto llega al servidor. /v1/workouts es el catalogo
 * (tarea g, specs/funcionales/catalogo-de-entrenamientos.md).
 */
interface VoraApi {

    @GET("health")
    suspend fun getHealth(): Response<HealthDto>

    @GET("v1/workouts")
    suspend fun getWorkouts(): Response<ApiResponseDto<List<WorkoutDto>>>
}
