package com.example.parcial_tp3.ui.screens.workout

import androidx.annotation.StringRes
import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.AppError
import com.example.parcial_tp3.data.repository.AppException
import com.example.parcial_tp3.data.repository.WorkoutNotCachedException

// Solo los llaman los ViewModels: una pantalla nunca ve la excepcion (RF06).

/** Mensaje de 01 y 02 (C16). Un 404 del catalogo no tiene texto propio. */
@StringRes
fun catalogErrorMessage(error: Throwable): Int = when ((error as? AppException)?.error) {
    AppError.NO_CONNECTION -> R.string.workout_error_no_connection
    AppError.SERVER -> R.string.workout_error_server
    AppError.INVALID_DATA -> R.string.workout_error_invalid_data
    else -> R.string.workout_error
}

/** Mensaje de 03 a 06 (C22, C27). */
@StringRes
fun detailErrorMessage(error: Throwable): Int = when {
    error is WorkoutNotCachedException -> R.string.workout_not_available_offline
    error !is AppException -> R.string.details_error
    else -> when (error.error) {
        AppError.NO_CONNECTION -> R.string.workout_error_no_connection
        AppError.SERVER -> R.string.workout_error_server
        AppError.INVALID_DATA -> R.string.workout_error_invalid_data
        AppError.NOT_FOUND -> R.string.details_not_found
        AppError.UNKNOWN -> R.string.details_error
    }
}
