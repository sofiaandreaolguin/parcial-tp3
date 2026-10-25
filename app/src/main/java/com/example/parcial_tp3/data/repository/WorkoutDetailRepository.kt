package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.domain.model.WorkoutDetail

/**
 * El detalle de un entrenamiento, visto desde la app (RF01, RF06). Es interfaz para poder probar
 * el ViewModel con un repositorio falso.
 */
interface WorkoutDetailRepository {

    /**
     * Un id que no existe o cualquier error llega como Result.failure; sin conexion y sin el detalle
     * guardado, con WorkoutNotCachedException.
     */
    suspend fun getWorkoutDetail(id: String): Result<WorkoutDetail>
}
