package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.StringRes
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.MealType

/**
 *  04 a 07: lo que se escuchó y el borrador de la comida. Todo es de
 * demostración; en la tarea l el borrador se guarda en Room.
 */
data class FoodCaptureUiState(
    @StringRes val transcript: Int = R.string.nutrition_voice_transcript_demo,
    val draft: MealDraft = MealDraft(),
    val analysisStep: AnalysisStep = AnalysisStep.Identifying,
)

/** El paso de Nutrition / 05 que se está haciendo; los anteriores están hechos y los siguientes pendientes. */
enum class AnalysisStep { Identifying, Estimating, Calculating }

sealed interface FoodCaptureEvent {
    data object AnalysisDone : FoodCaptureEvent
}

/** Misma forma que `Meal`, con el nombre como recurso para no escribir texto en el código. */
data class MealDraft(
    @StringRes val name: Int = R.string.nutrition_voice_meal_demo,
    val type: MealType = MealType.LUNCH,
    val kcal: Int = 520,
    val proteinG: Double = 0.0,
    val carbsG: Double = 0.0,
    val fatG: Double = 0.0,
)
