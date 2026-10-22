package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.domain.model.Meal
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

/**
 * Comidas guardadas, vistas desde la app (RF04, RF06). El ViewModel solo conoce esta interfaz:
 * nunca el DAO ni la Entity. Es interfaz para poder probar el ViewModel con un repositorio falso.
 */
interface MealRepository {

    /** Las comidas de un dia, ordenadas por id. Vuelve a emitir cuando se guarda una nueva. */
    fun observeMealsByDate(date: LocalDate): Flow<List<Meal>>

    /** Guarda la comida y devuelve su id nuevo. */
    suspend fun addMeal(meal: Meal): Long
}
