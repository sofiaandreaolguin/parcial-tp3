package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.entity.MealEntity
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.MealType
import java.time.LocalDate

// La fecha se guarda como texto ISO (AAAA-MM-DD) y el tipo con el nombre del enum (RF06).
// No se valida nada: un tipo o una fecha invalidos en la tabla lanzan excepcion al leerlos.

fun Meal.toEntity(): MealEntity =
    MealEntity(
        id = id,
        name = name,
        date = date.toString(),
        type = type.name,
        kcal = kcal,
        proteinG = proteinG,
        carbsG = carbsG,
        fatG = fatG,
    )

fun MealEntity.toDomain(): Meal =
    Meal(
        id = id,
        name = name,
        date = LocalDate.parse(date),
        type = MealType.valueOf(type),
        kcal = kcal,
        proteinG = proteinG,
        carbsG = carbsG,
        fatG = fatG,
    )
