package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.dao.CoachDao
import com.example.parcial_tp3.data.remote.VoraApi
import com.example.parcial_tp3.data.remote.dto.CoachRequestDto
import com.example.parcial_tp3.domain.model.CoachMessage
import com.example.parcial_tp3.domain.model.CoachRole
import com.example.parcial_tp3.domain.model.CoachSuggestion
import java.net.HttpURLConnection
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * El coach (EXT01): Retrofit trae las sugerencias y la respuesta, Room guarda el historial.
 *
 * A diferencia del catalogo, aca Room **no** es respaldo de la red: son dos cosas distintas.
 * El mock no guarda ningun mensaje, asi que el hilo solo vive en Room y la red solo aporta la
 * respuesta nueva. Por eso un fallo de red nunca toca lo guardado (C22).
 */
class CoachRepositoryImpl @Inject constructor(
    private val api: VoraApi,
    private val dao: CoachDao,
) : CoachRepository {

    override suspend fun getSuggestions(): Result<List<CoachSuggestion>> =
        try {
            val response = api.getCoachSuggestions()
            val cuerpo = response.body()?.data
            when {
                !response.isSuccessful -> Result.failure(AppException(response.aError()))
                cuerpo == null -> Result.failure(AppException(AppError.INVALID_DATA))
                // una sugerencia invalida hace fallar toda la consulta, igual que el catalogo
                else -> Result.success(cuerpo.map { it.toDomain() })
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(AppException(e.toAppError(), e))
        }

    override fun observeHistory(): Flow<List<CoachMessage>> =
        dao.observeAll().map { filas -> filas.map { it.toDomain() } }

    /**
     * Guarda el mensaje propio **antes** de llamar al servidor (C9): si la llamada falla, lo
     * escrito ya esta en el hilo y no se pierde (C22).
     *
     * **Reintentar no lo duplica (C23):** si el ultimo del hilo ya es ese mismo texto de la
     * persona, es el envio que fallo y solo se repite la parte de red. Se mira el hilo y no
     * una marca en memoria, asi el reintento tampoco duplica despues de rotar.
     */
    override suspend fun send(text: String): Result<CoachMessage> {
        return try {
            if (!esElQueFallo(text)) {
                dao.insert(mensajePropio(text).toEntity())
            }
            val respuesta = api.postCoachMessage(CoachRequestDto(text))
            val cuerpo = respuesta.body()?.data
            when {
                !respuesta.isSuccessful -> Result.failure(AppException(respuesta.aError()))
                cuerpo == null -> Result.failure(AppException(AppError.INVALID_DATA))
                else -> {
                    val mensaje = cuerpo.toDomain(createdAt = siguienteMomento())
                    guardar(mensaje)
                    Result.success(mensaje)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(AppException(e.toAppError(), e))
        }
    }

    override suspend fun seedIfEmpty(demo: List<CoachMessage>) {
        if (dao.count() > 0) return
        demo.forEach { dao.insert(it.toEntity()) }
    }

    private suspend fun mensajePropio(text: String) =
        CoachMessage(role = CoachRole.User, text = text, createdAt = siguienteMomento())

    /** El ultimo del hilo es esta misma pregunta sin responder: estamos reintentando. */
    private suspend fun esElQueFallo(text: String): Boolean {
        val ultimo = dao.observeAll().first().lastOrNull() ?: return false
        return ultimo.role == CoachRole.User.name.lowercase() && ultimo.text == text
    }

    // Un problema al guardar no tapa una respuesta buena de la red, igual que en el catalogo.
    private suspend fun guardar(mensaje: CoachMessage) {
        try {
            dao.insert(mensaje.toEntity())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // se sigue con lo de la red
        }
    }

    /** El orden del hilo no depende del reloj del sistema: se apoya en lo que ya hay guardado. */
    private suspend fun siguienteMomento(): Long = dao.count().toLong() + 1
}

/** 404 es "no encontrado" y cualquier otro codigo de fallo es un problema del servidor (RF05). */
private fun retrofit2.Response<*>.aError(): AppError =
    if (code() == HttpURLConnection.HTTP_NOT_FOUND) AppError.NOT_FOUND else AppError.SERVER
