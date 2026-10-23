package com.example.parcial_tp3.ui.screens.nutrition

import com.example.parcial_tp3.R

/** Datos de demostración de Nutrition / 01 y 02. La tarea l los reemplaza por lo que sale de Room. */
fun nutritionDemoState() = NutritionUiState(
    date = R.string.nutrition_dash_date,
    caloriesConsumed = R.string.nutrition_dash_calories_value,
    caloriesGoal = R.string.nutrition_dash_calories_goal,
    caloriesPercent = R.string.nutrition_dash_calories_percent,
    caloriesProgress = CALORIES_PROGRESS,
    macros = listOf(
        MacroSummary(
            MacroKind.PROTEIN,
            R.string.nutrition_dash_protein,
            R.string.nutrition_dash_protein_now,
            R.string.nutrition_dash_protein_goal,
            PROTEIN_PROGRESS,
        ),
        MacroSummary(
            MacroKind.CARBS,
            R.string.nutrition_dash_carbs,
            R.string.nutrition_dash_carbs_now,
            R.string.nutrition_dash_carbs_goal,
            CARBS_PROGRESS,
        ),
        MacroSummary(
            MacroKind.FAT,
            R.string.nutrition_dash_fat,
            R.string.nutrition_dash_fat_now,
            R.string.nutrition_dash_fat_goal,
            FAT_PROGRESS,
        ),
    ),
    advice = R.string.nutrition_dash_advice,
    meals = listOf(
        MealSummary(
            1,
            R.string.nutrition_dash_breakfast,
            R.string.nutrition_dash_breakfast_items,
            R.string.nutrition_dash_breakfast_kcal,
        ),
        MealSummary(
            2,
            R.string.nutrition_dash_lunch,
            R.string.nutrition_dash_lunch_items,
            R.string.nutrition_dash_lunch_kcal,
        ),
        MealSummary(
            3,
            R.string.nutrition_dash_snack,
            R.string.nutrition_dash_snack_items,
            R.string.nutrition_dash_snack_kcal,
        ),
    ),
    pendingMeal = PendingMealSummary(
        name = R.string.nutrition_dash_dinner,
        hint = R.string.nutrition_dash_dinner_empty,
    ),
)

// 1.420 de 2.100 kcal.
private const val CALORIES_PROGRESS = 0.67f

// El Figma dibuja las barras con 77, 72 y 77 de 98 de ancho (nodos 1:765, 1:771 y 1:777),
// que no coincide con el cociente de 104/140, 152/220 y 48/65: se copia lo dibujado.
private const val PROTEIN_PROGRESS = 77f / 98f
private const val CARBS_PROGRESS = 72f / 98f
private const val FAT_PROGRESS = 77f / 98f
