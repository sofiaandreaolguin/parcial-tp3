package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.StringRes
import com.example.parcial_tp3.domain.model.MealType
import java.time.LocalDate

/** Lo que muestran Nutrition / 01 y 02 con las comidas de Room de la fecha elegida (RF04, RF07). */
sealed interface NutritionUiState {
    data object Loading : NutritionUiState
    data class Content(
        val date: LocalDate,
        val kcal: Int,
        val kcalGoal: Int,
        val kcalProgress: Float,
        val macros: List<MacroTotal>,
        val meals: List<MealRow>,
        val pending: MealType?,
    ) : NutritionUiState
    data class Error(@StringRes val message: Int) : NutritionUiState
}

data class MacroTotal(val kind: MacroKind, val grams: Int, val goal: Int, val progress: Float)

data class MealRow(val id: Long, val type: MealType, val name: String, val kcal: Int)

/**
 * La versión de demostración de 01 y 02: cada texto es un recurso de strings.xml. Queda mientras
 * las pantallas no dibujen [NutritionUiState] (paso implementar de VC-l).
 */
data class NutritionDemoUiState(
    @StringRes val date: Int,
    @StringRes val caloriesConsumed: Int,
    @StringRes val caloriesGoal: Int,
    @StringRes val caloriesPercent: Int,
    val caloriesProgress: Float,
    val macros: List<MacroSummary>,
    @StringRes val advice: Int,
    val meals: List<MealSummary>,
    val pendingMeal: PendingMealSummary,
    val loggedMeals: List<LoggedMeal>,
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

/** Una comida registrada de Nutrition / 02: mismo id y mismos kcal que su fila de [MealSummary]. */
data class LoggedMeal(
    val id: Long,
    @StringRes val name: Int,
    @StringRes val kcal: Int,
    val ingredients: List<IngredientSummary>,
)

data class IngredientSummary(
    @StringRes val name: Int,
    @StringRes val quantity: Int,
    @StringRes val kcal: Int,
)
