package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.remote.VoraApi
import com.example.parcial_tp3.domain.model.Workout
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class WorkoutRepositoryImpl @Inject constructor(
    private val api: VoraApi,
) : WorkoutRepository {

    override suspend fun getWorkouts(): Result<List<Workout>> =
        try {
            val response = api.getWorkouts()
            val workouts = response.body()?.data
            when {
                !response.isSuccessful -> Result.failure(IllegalStateException("HTTP ${response.code()}"))
                workouts == null -> Result.failure(IllegalStateException("Respuesta sin data"))
                // un item invalido lanza en toDomain() y hace fallar toda la consulta
                else -> Result.success(workouts.map { it.toDomain() })
            }
        } catch (e: CancellationException) {
            // Se relanza antes del catch generico: un ViewModel destruido no tiene que recibir un error
            throw e
        } catch (e: Exception) {
            // Red, JSON roto o de otro tipo, datos invalidos. La tarea de estados (RF05) suma aca los
            // catch especificos para traducirlos a errores propios.
            Result.failure(e)
        }
}
