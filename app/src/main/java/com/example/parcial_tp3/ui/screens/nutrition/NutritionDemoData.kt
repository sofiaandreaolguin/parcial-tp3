package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.MealType
import java.time.LocalDate

/** Metas de demostración de Nutrition y el estado de las previews de 01 y 02. */
object NutritionDemoData {

    /** Solo de Nutrition, como dice el Figma (nodo 1:777). kcal, proteína y carbos son las de Home. */
    const val FAT_GOAL_G = 65

    /** Los números del frame 1:730; el total no es la suma de las filas porque el Figma no lo es. */
    @Composable
    fun previewState() = NutritionUiState.Content(
        date = LocalDate.of(2026, 9, 8),
        kcal = 1_420,
        kcalGoal = 2_100,
        kcalProgress = 1_420f / 2_100f,
        macros = listOf(
            MacroTotal(MacroKind.PROTEIN, grams = 104, goal = 140, progress = 104f / 140f),
            MacroTotal(MacroKind.CARBS, grams = 152, goal = 220, progress = 152f / 220f),
            MacroTotal(MacroKind.FAT, grams = 48, goal = FAT_GOAL_G, progress = 48f / FAT_GOAL_G),
        ),
        meals = listOf(
            MealRow(1, MealType.BREAKFAST, stringResource(R.string.nutrition_dash_breakfast_items), 420),
            MealRow(2, MealType.LUNCH, stringResource(R.string.nutrition_dash_lunch_items), 520),
            MealRow(3, MealType.SNACK, stringResource(R.string.nutrition_dash_snack_items), 240),
        ),
        pending = MealType.DINNER,
    )
}
