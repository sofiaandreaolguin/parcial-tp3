package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.entity.MealEntity
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.MealType
import java.time.LocalDate

// Firmas vacias (tests en rojo): la logica real la escribe la sesion de implementar.

fun Meal.toEntity(): MealEntity =
    MealEntity(name = "", date = "", type = "", kcal = 0, proteinG = 0.0, carbsG = 0.0, fatG = 0.0)

fun MealEntity.toDomain(): Meal =
    Meal(
        name = "",
        date = LocalDate.of(2000, 1, 1),
        type = MealType.BREAKFAST,
        kcal = 0,
        proteinG = 0.0,
        carbsG = 0.0,
        fatG = 0.0,
    )
