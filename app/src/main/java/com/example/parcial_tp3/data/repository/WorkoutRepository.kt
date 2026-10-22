package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.domain.model.Workout

/**
 * El catalogo de entrenamientos, visto desde la app (RF01, RF06). Es interfaz para poder probar
 * el ViewModel con un repositorio falso.
 */
interface WorkoutRepository {

    /** Pide GET /v1/workouts. Cualquier error de red o de datos llega como Result.failure. */
    suspend fun getWorkouts(): Result<List<Workout>>
}
