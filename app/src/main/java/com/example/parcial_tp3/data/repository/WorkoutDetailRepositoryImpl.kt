package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.dao.WorkoutDao
import com.example.parcial_tp3.data.remote.VoraApi
import com.example.parcial_tp3.domain.model.WorkoutDetail
import com.example.parcial_tp3.domain.model.WorkoutLevel
import javax.inject.Inject

// Firma vacia de la tarea del detalle y la cache: la completa la sesion de implementar. Devuelve un
// detalle vacio, sin red ni Room, para que los tests fallen por el resultado.
class WorkoutDetailRepositoryImpl @Inject constructor(
    private val api: VoraApi,
    private val dao: WorkoutDao,
) : WorkoutDetailRepository {

    override suspend fun getWorkoutDetail(id: String): Result<WorkoutDetail> =
        Result.success(WorkoutDetail("", "", WorkoutLevel.BEGINNER, 0, 0, "", emptyList()))
}
