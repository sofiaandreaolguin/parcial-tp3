package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.StringRes
import com.example.parcial_tp3.domain.model.Ingredient
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

data class MealRow(
    val id: Long,
    val type: MealType,
    val name: String,
    val kcal: Int,
    val ingredients: List<Ingredient> = emptyList(),
)

enum class MacroKind { PROTEIN, CARBS, FAT }
