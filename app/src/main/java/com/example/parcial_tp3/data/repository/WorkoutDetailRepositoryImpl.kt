package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.dao.WorkoutDao
import com.example.parcial_tp3.data.remote.VoraApi
import com.example.parcial_tp3.domain.model.WorkoutDetail
import com.google.gson.stream.MalformedJsonException
import java.io.EOFException
import java.io.IOException
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
                response.code() == HTTP_NOT_FOUND -> Result.failure(NoSuchElementException("Entrenamiento no encontrado: $id"))
                !response.isSuccessful -> desdeRoom(id, IllegalStateException("HTTP ${response.code()}"))
                data == null -> desdeRoom(id, IllegalStateException("Respuesta sin data"))
                else -> {
                    val detail = data.toDomain()
                    guardar(detail)
                    Result.success(detail)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: MalformedJsonException) {
            // JSON roto: el servidor respondio. Estas dos heredan de IOException, por eso van antes;
            // si no, un JSON roto se mostraria como "no disponible sin conexion".
            desdeRoom(id, e)
        } catch (e: EOFException) {
            desdeRoom(id, e)
        } catch (e: IOException) {
            desdeRoom(id, e, sinConexion = true)
        } catch (e: Exception) {
            desdeRoom(id, e)
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

    private suspend fun desdeRoom(id: String, error: Exception, sinConexion: Boolean = false): Result<WorkoutDetail> =
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
            Result.failure(e)
        }

    private companion object {
        const val HTTP_NOT_FOUND = 404
    }
}
