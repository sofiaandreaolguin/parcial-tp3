package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.StringRes

/**
 * Lo que muestran Nutrition / 01 y 02. Todo es de demostración: cada texto es un recurso de
 * strings.xml. Cuando llegue Room (tarea l) las comidas y los totales salen de la misma consulta.
 */
data class NutritionUiState(
    @StringRes val date: Int,
    @StringRes val caloriesConsumed: Int,
    @StringRes val caloriesGoal: Int,
    @StringRes val caloriesPercent: Int,
    val caloriesProgress: Float,
    val macros: List<MacroSummary>,
    @StringRes val advice: Int,
    val meals: List<MealSummary>,
    val pendingMeal: PendingMealSummary,
)

enum class MacroKind { PROTEIN, CARBS, FAT }

data class MacroSummary(
    val kind: MacroKind,
    @StringRes val label: Int,
    @StringRes val consumed: Int,
    @StringRes val goal: Int,
    val progress: Float,
)

data class MealSummary(
    val id: Long,
    @StringRes val name: Int,
    @StringRes val items: Int,
    @StringRes val kcal: Int,
)

/** La comida del día que todavía no se registró ("Dinner"): lleva el botón "+ Add". */
data class PendingMealSummary(
    @StringRes val name: Int,
    @StringRes val hint: Int,
)
