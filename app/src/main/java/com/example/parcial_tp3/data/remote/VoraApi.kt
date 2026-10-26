package com.example.parcial_tp3.data.remote

import com.example.parcial_tp3.data.remote.dto.ApiResponseDto
import com.example.parcial_tp3.data.remote.dto.CoachReplyDto
import com.example.parcial_tp3.data.remote.dto.CoachRequestDto
import com.example.parcial_tp3.data.remote.dto.HealthDto
import com.example.parcial_tp3.data.remote.dto.SuggestionDto
import com.example.parcial_tp3.data.remote.dto.WorkoutDetailDto
import com.example.parcial_tp3.data.remote.dto.WorkoutDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Los endpoints del mock del profe (specs/API.md).
 *
 * /health comprueba que la base del proyecto llega al servidor. /v1/workouts es el catalogo
 * (tarea g, specs/funcionales/catalogo-de-entrenamientos.md) y /v1/workouts/{workoutId} el
 * detalle (specs/funcionales/detalle-y-cache-de-entrenamientos.md).
 */
interface VoraApi {

    @GET("health")
    suspend fun getHealth(): Response<HealthDto>

    @GET("v1/workouts")
    suspend fun getWorkouts(): Response<ApiResponseDto<List<WorkoutDto>>>

    @GET("v1/workouts/{workoutId}")
    suspend fun getWorkoutDetail(@Path("workoutId") workoutId: String): Response<ApiResponseDto<WorkoutDetailDto>>

    @GET("v1/coach/suggestions")
    suspend fun getCoachSuggestions(): Response<ApiResponseDto<List<SuggestionDto>>>

    @POST("v1/coach/messages")
    suspend fun postCoachMessage(@Body body: CoachRequestDto): Response<ApiResponseDto<CoachReplyDto>>
}
