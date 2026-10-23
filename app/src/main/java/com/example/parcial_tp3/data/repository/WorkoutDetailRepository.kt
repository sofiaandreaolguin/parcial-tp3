package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.domain.model.WorkoutDetail

/**
 * El detalle de un entrenamiento, visto desde la app (RF01, RF06). Hoy lo implementa la demo; la
 * tarea del detalle real suma la implementacion con Retrofit y cambia solo el @Binds.
 */
interface WorkoutDetailRepository {

    /** Un id que no existe o cualquier error llega como Result.failure. */
    suspend fun getWorkoutDetail(id: String): Result<WorkoutDetail>
}
