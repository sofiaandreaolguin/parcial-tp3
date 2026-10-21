package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.dao.MealDao
import com.example.parcial_tp3.domain.model.Meal
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MealRepositoryImpl @Inject constructor(
    private val dao: MealDao,
) : MealRepository {

    override fun observeMealsByDate(date: LocalDate): Flow<List<Meal>> =
        dao.observeByDate(date.toString()).map { entities -> entities.map { it.toDomain() } }

    override suspend fun addMeal(meal: Meal): Long = dao.insert(meal.toEntity())
}
