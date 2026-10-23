package com.example.parcial_tp3.ui.screens.workout

import com.example.parcial_tp3.data.repository.WorkoutDetailRepository
import com.example.parcial_tp3.domain.model.WorkoutDetail
import kotlinx.coroutines.CompletableDeferred

/**
 * Repositorio falso: guarda los ids que le piden y retiene la respuesta hasta que el test la
 * completa, para poder ver el estado Loading.
 */
class FakeWorkoutDetailRepository : WorkoutDetailRepository {

    val respuesta = CompletableDeferred<Result<WorkoutDetail>>()
    val idsPedidos = mutableListOf<String>()

    override suspend fun getWorkoutDetail(id: String): Result<WorkoutDetail> {
        idsPedidos += id
        return respuesta.await()
    }
}
