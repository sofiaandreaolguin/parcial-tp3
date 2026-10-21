package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.dao.MealDao
import com.example.parcial_tp3.domain.model.Meal
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

// Firmas vacias (tests en rojo): la logica real la escribe la sesion de implementar.
class MealRepositoryImpl @Inject constructor(
    private val dao: MealDao,
) : MealRepository {

    override fun observeMealsByDate(date: LocalDate): Flow<List<Meal>> = emptyFlow()

    override suspend fun addMeal(meal: Meal): Long = -1
}
