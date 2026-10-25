package com.example.parcial_tp3.data.repository

/** Error de la red o de la cache de entrenamientos, traducido en el repositorio (RF05, RF06). */
class AppException(val error: AppError, cause: Throwable? = null) : Exception(cause)

enum class AppError { NO_CONNECTION, SERVER, NOT_FOUND, INVALID_DATA, UNKNOWN }

// Firma vacia de los tests en rojo: la clasificacion (tabla de specs/funcionales/estados-de-carga-y-error.md)
// llega con el paso de implementar.
fun Throwable.toAppError(): AppError = AppError.UNKNOWN
