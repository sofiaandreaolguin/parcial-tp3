package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.remote.VoraApi
import com.example.parcial_tp3.domain.model.Workout
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val api: VoraApi,
) : WorkoutRepository {

    // Firma vacia (tests en rojo): la logica la escribe la skill implementar.
    override suspend fun getWorkouts(): Result<List<Workout>> = Result.success(emptyList())
}
