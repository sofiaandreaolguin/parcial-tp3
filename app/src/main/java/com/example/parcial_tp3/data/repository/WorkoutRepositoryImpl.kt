package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.dao.WorkoutDao
import com.example.parcial_tp3.data.remote.VoraApi
import com.example.parcial_tp3.domain.model.Workout
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

/**
 * El catalogo con cache (RF02): siempre se intenta la red primero y Room queda de respaldo ante
 * cualquier fallo. Ningun fallo borra lo guardado (RF05).
 */
class WorkoutRepositoryImpl @Inject constructor(
    private val api: VoraApi,
    private val dao: WorkoutDao,
) : WorkoutRepository {

    override suspend fun getWorkouts(): Result<List<Workout>> =
        try {
            val response = api.getWorkouts()
            val workouts = response.body()?.data
            when {
                !response.isSuccessful -> desdeRoom(IllegalStateException("HTTP ${response.code()}"))
                workouts == null -> desdeRoom(IllegalStateException("Respuesta sin data"))
                else -> {
                    // un item invalido lanza en toDomain() y hace fallar toda la consulta
                    val catalogo = workouts.map { it.toDomain() }
                    guardar(catalogo)
                    Result.success(catalogo)
                }
            }
        } catch (e: CancellationException) {
            // Se relanza antes del catch generico: un ViewModel destruido no tiene que recibir un error
            throw e
        } catch (e: Exception) {
            // Red, JSON roto o de otro tipo, datos invalidos. La tarea de estados (RF05) suma aca los
            // catch especificos para traducirlos a errores propios.
            desdeRoom(e)
        }

    // Un problema de la cache no tapa un dato bueno de la red: el error de Room se descarta.
    private suspend fun guardar(catalogo: List<Workout>) {
        try {
            dao.saveSummaries(catalogo.map { it.toEntity() })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // se sigue con lo de la red
        }
    }

    // Con la cache vacia se devuelve el error de la red, no una lista vacia.
    private suspend fun desdeRoom(error: Exception): Result<List<Workout>> =
        try {
            val guardados = dao.getAll()
            if (guardados.isEmpty()) Result.failure(error) else Result.success(guardados.map { it.toDomain() })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
