package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.dao.WorkoutDao
import com.example.parcial_tp3.data.remote.VoraApi
import com.example.parcial_tp3.domain.model.WorkoutDetail
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

/**
 * El detalle con cache (RF01, RF02): red primero, Room de respaldo ante cualquier fallo salvo el 404
 * (el entrenamiento no existe). Sin conexion y sin el detalle guardado, WorkoutNotCachedException.
 */
class WorkoutDetailRepositoryImpl @Inject constructor(
    private val api: VoraApi,
    private val dao: WorkoutDao,
) : WorkoutDetailRepository {

    override suspend fun getWorkoutDetail(id: String): Result<WorkoutDetail> =
        try {
            val response = api.getWorkoutDetail(id)
            val data = response.body()?.data
            when {
                response.code() == HTTP_NOT_FOUND -> Result.failure(AppException(AppError.NOT_FOUND))
                !response.isSuccessful -> desdeRoom(id, AppException(AppError.SERVER))
                data == null -> desdeRoom(id, AppException(AppError.INVALID_DATA))
                else -> {
                    val detail = data.toDomain()
                    guardar(detail)
                    Result.success(detail)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // toAppError() separa el JSON roto (que hereda de IOException) de la falta de conexion;
            // si no, un JSON roto se mostraria como "no disponible sin conexion".
            val error = e.toAppError()
            desdeRoom(id, AppException(error, e), sinConexion = error == AppError.NO_CONNECTION)
        }

    // Un problema de la cache no tapa un dato bueno de la red: el error de Room se descarta.
    private suspend fun guardar(detail: WorkoutDetail) {
        try {
            dao.saveDetail(
                detail.toEntity(),
                detail.exercises.mapIndexed { position, exercise -> exercise.toEntity(detail.id, position) },
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // se sigue con lo de la red
        }
    }

    // Si falla Room es UNKNOWN, sin toAppError() ni WorkoutNotCachedException: el problema es la cache.
    private suspend fun desdeRoom(id: String, error: AppException, sinConexion: Boolean = false): Result<WorkoutDetail> =
        try {
            val fila = dao.getById(id)
            when {
                fila?.description != null -> Result.success(fila.toDomain(dao.getExercises(id)))
                sinConexion -> Result.failure(WorkoutNotCachedException(id))
                else -> Result.failure(error)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(AppException(AppError.UNKNOWN, e))
        }

    private companion object {
        const val HTTP_NOT_FOUND = 404
    }
}
