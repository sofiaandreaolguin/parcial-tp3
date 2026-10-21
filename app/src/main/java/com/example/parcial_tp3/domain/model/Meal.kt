package com.example.parcial_tp3.domain.model

import java.time.LocalDate

/**
 * Comida registrada en Nutrition (RF03). Los nutrientes son los de la porcion completa.
 * id = 0 significa que todavia no se guardo: Room le asigna el definitivo.
 */
data class Meal(
    val id: Long = 0,
    val name: String,
    val date: LocalDate,
    val type: MealType,
    val kcal: Int,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
)

/** Desayuno, almuerzo, merienda y cena. */
enum class MealType { BREAKFAST, LUNCH, SNACK, DINNER }
