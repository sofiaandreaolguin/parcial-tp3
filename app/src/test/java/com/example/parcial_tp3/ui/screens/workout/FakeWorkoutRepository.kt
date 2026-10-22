package com.example.parcial_tp3.ui.screens.workout

import com.example.parcial_tp3.data.repository.WorkoutRepository
import com.example.parcial_tp3.domain.model.Workout
import kotlinx.coroutines.CompletableDeferred

/**
 * Repositorio falso: cuenta las llamadas y retiene la respuesta hasta que el test la completa,
 * para poder ver el estado Loading.
 */
class FakeWorkoutRepository : WorkoutRepository {

    val respuesta = CompletableDeferred<Result<List<Workout>>>()
    var llamadas = 0
        private set

    override suspend fun getWorkouts(): Result<List<Workout>> {
        llamadas++
        return respuesta.await()
    }
}
