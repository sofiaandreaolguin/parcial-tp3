package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.StringRes
import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.AppError
import com.example.parcial_tp3.domain.FieldError
import com.example.parcial_tp3.domain.model.FoodItem
import com.example.parcial_tp3.domain.model.Nutrients

/**
 * La hoja de busqueda de alimentos (EXT02). Dos momentos en la misma hoja: la lista y, con
 * [seleccionado] distinto de null, el panel de la porcion. Es un cambio de estado, no
 * navegacion, asi que el "atras" del panel vuelve a los resultados sin perderlos.
 */
data class FoodSearchUiState(
    val query: String = "",
    val items: List<FoodItem> = emptyList(),
    val cargando: Boolean = false,
    val error: AppError? = null,
    val vacio: Boolean = false,
    val seleccionado: FoodItem? = null,
    val gramos: String = GRAMOS_INICIALES,
    val gramosError: FieldError? = null,
    val porcion: Nutrients? = null,
)

/** La unidad en que viene el dato del servidor: el primer numero que se ve coincide con el suyo. */
const val GRAMOS_INICIALES = "100"

/** Cada AppError con su aviso. Los que no tienen uno propio muestran el de servidor (C23). */
@StringRes
fun mensajeDe(error: AppError): Int = when (error) {
    AppError.NO_CONNECTION -> R.string.food_search_error_no_connection
    AppError.INVALID_DATA -> R.string.food_search_error_invalid_data
    else -> R.string.food_search_error_server
}

/** Cada FieldError de los gramos con su aviso (C16 a C18). */
@StringRes
fun mensajeDeGramos(error: FieldError): Int = when (error) {
    FieldError.REQUIRED -> R.string.food_search_grams_required
    FieldError.NEGATIVE -> R.string.food_search_grams_negative
    else -> R.string.food_search_grams_not_a_number
}
