package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.StringRes
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.FieldError
import com.example.parcial_tp3.domain.MealField
import com.example.parcial_tp3.domain.MealForm
import com.example.parcial_tp3.domain.model.Ingredient
import com.example.parcial_tp3.domain.model.MealType

/**
 * 04 a 07: lo que se escuchó, el borrador de la comida y el formulario de 07. El borrador es de
 * demostración y no se guarda: al confirmar, en Room queda la comida con sus totales.
 */
data class FoodCaptureUiState(
    @StringRes val transcript: Int = R.string.nutrition_voice_transcript_demo,
    /** null = no hubo captura (carga manual). */
    val draft: MealDraft? = null,
    val analysisStep: AnalysisStep = AnalysisStep.Identifying,
    /** null = 07 cerrada. */
    val confirmation: ConfirmationState? = null,
)

/** mealId de 07 para una comida nueva (el valor por defecto del argumento de la ruta). */
const val NEW_MEAL_ID = -1L

data class ConfirmationState(
    /** [NEW_MEAL_ID] = nueva. */
    val mealId: Long,
    val form: MealForm,
    val errors: Map<MealField, FieldError> = emptyMap(),
    val isSaving: Boolean = false,
    val notFound: Boolean = false,
    @StringRes val saveError: Int? = null,
    val ingredients: List<Ingredient> = emptyList(),
)

/** El paso de Nutrition / 05 que se está haciendo; los anteriores están hechos y los siguientes pendientes. */
enum class AnalysisStep { Identifying, Estimating, Calculating }

sealed interface FoodCaptureEvent {
    data object AnalysisDone : FoodCaptureEvent
    data object Saved : FoodCaptureEvent
}

/** Misma forma que `Meal`. */
data class MealDraft(
    @StringRes val name: Int = R.string.nutrition_voice_meal_demo,
    val type: MealType = MealType.LUNCH,
    val kcal: Int = 520,
    val proteinG: Double = 0.0,
    val carbsG: Double = 0.0,
    val fatG: Double = 0.0,
    val ingredients: List<DraftIngredient> = emptyList(),
)

data class DraftIngredient(@StringRes val name: Int, val grams: Int, val kcal: Int)

/** Lo que "estima"  / 06: los mismos números que muestra 07, que los lee de acá. */
val PhotoMealDraft = MealDraft(
    kcal = 520,
    proteinG = 38.0,
    carbsG = 54.0,
    fatG = 14.0,
    ingredients = listOf(
        DraftIngredient(R.string.nutrition_meals_chicken, grams = 180, kcal = 280),
        DraftIngredient(R.string.nutrition_meals_rice, grams = 150, kcal = 190),
        DraftIngredient(R.string.nutrition_meals_vegetables, grams = 100, kcal = 50),
    ),
)
