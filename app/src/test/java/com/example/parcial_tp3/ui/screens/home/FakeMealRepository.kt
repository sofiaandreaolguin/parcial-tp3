package com.example.parcial_tp3.ui.screens.home

import com.example.parcial_tp3.data.repository.MealRepository
import com.example.parcial_tp3.domain.model.Meal
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * MealRepository en memoria: emite de nuevo cada vez que cambian las comidas, como el Flow del
 * DAO de Room. Guarda la ultima fecha pedida para comprobar que Home lee las de hoy.
 */
class FakeMealRepository(comidas: List<Meal> = emptyList()) : MealRepository {

    val meals = MutableStateFlow(comidas)
    var fechaPedida: LocalDate? = null
        private set

    override fun observeMealsByDate(date: LocalDate): Flow<List<Meal>> {
        fechaPedida = date
        return meals.map { lista -> lista.filter { it.date == date } }
    }

    override suspend fun addMeal(meal: Meal): Long {
        val id = (meals.value.maxOfOrNull { it.id } ?: 0) + 1
        meals.value = meals.value + meal.copy(id = id)
        return id
    }
}
