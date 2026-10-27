package com.example.parcial_tp3.ui.screens.nutrition

import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.AppError
import com.example.parcial_tp3.domain.FieldError
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * C23 y C16 a C18: cada error tiene su aviso (EXT02).
 *
 * Son las ramas que un control negativo rompe sin que nadie se entere: NOT_FOUND y UNKNOWN caen
 * al `else` y, sin test, podrian quedar apuntando a cualquier texto o a ninguno.
 */
class FoodSearchMensajesTest {

    @Test
    fun `C23 cada AppError tiene su aviso y los que no tienen propio muestran el de servidor`() {
        assertEquals(R.string.food_search_error_no_connection, mensajeDe(AppError.NO_CONNECTION))
        assertEquals(R.string.food_search_error_invalid_data, mensajeDe(AppError.INVALID_DATA))
        assertEquals(R.string.food_search_error_server, mensajeDe(AppError.SERVER))
        // Los dos que no tienen aviso propio.
        assertEquals(R.string.food_search_error_server, mensajeDe(AppError.NOT_FOUND))
        assertEquals(R.string.food_search_error_server, mensajeDe(AppError.UNKNOWN))
    }

    @Test
    fun `C23 ningun AppError se queda sin aviso`() {
        for (error in AppError.entries) {
            assertEquals("$error no puede quedar en 0", true, mensajeDe(error) != 0)
        }
    }

    @Test
    fun `C16 a C18 cada problema de los gramos tiene su aviso`() {
        assertEquals(R.string.food_search_grams_required, mensajeDeGramos(FieldError.REQUIRED))
        assertEquals(R.string.food_search_grams_negative, mensajeDeGramos(FieldError.NEGATIVE))
        assertEquals(R.string.food_search_grams_not_a_number, mensajeDeGramos(FieldError.NOT_A_NUMBER))
        // El cuarto del enum no lo usa la busqueda, pero no puede quedar sin texto.
        assertEquals(R.string.food_search_grams_not_a_number, mensajeDeGramos(FieldError.NOT_A_WHOLE_NUMBER))
    }
}
