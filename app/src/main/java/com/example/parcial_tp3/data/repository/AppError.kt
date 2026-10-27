package com.example.parcial_tp3.data.repository

import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import java.io.EOFException
import java.io.IOException
import java.net.HttpURLConnection

/** Error de la red o de la cache de entrenamientos, traducido en el repositorio (RF05, RF06). */
class AppException(val error: AppError, cause: Throwable? = null) : Exception(cause)

enum class AppError { NO_CONNECTION, SERVER, NOT_FOUND, INVALID_DATA, UNKNOWN }

/**
 * Se clasifica por el tipo, nunca por la causa: OkHttp tira IOException("unexpected end of stream",
 * EOFException) cuando el servidor corta, y eso es sin conexion. JSON roto y cuerpo cortado van antes
 * que IOException porque heredan de ella.
 */
fun Throwable.toAppError(): AppError = when (this) {
    is AppException -> error
    is MalformedJsonException, is EOFException, is JsonParseException, is IllegalArgumentException -> AppError.INVALID_DATA
    is IOException -> AppError.NO_CONNECTION
    else -> AppError.UNKNOWN
}

/** El codigo HTTP traducido. 404 es "no existe"; el resto (503, 400, 500...) es el servidor. */
internal fun retrofit2.Response<*>.aError(): AppError =
    if (code() == HttpURLConnection.HTTP_NOT_FOUND) AppError.NOT_FOUND else AppError.SERVER
