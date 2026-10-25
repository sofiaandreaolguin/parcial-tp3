package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.StringRes
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.ui.screens.home.HomeDemoData

/** Nutrition / 08. */
sealed interface MealDetailUiState {
    data object Loading : MealDetailUiState
    data class Content(
        val meal: Meal,
        val confirmingDelete: Boolean = false,
        /** Eliminando o duplicando. */
        val isBusy: Boolean = false,
        @StringRes val actionError: Int? = null,
    ) : MealDetailUiState {
        /** Anillo de 08: las kcal de la comida sobre la meta diaria de demostración, como el de 01. */
        val kcalProgress: Float get() = (meal.kcal.toFloat() / HomeDemoData.KCAL_GOAL).coerceIn(0f, 1f)
    }
    data object NotFound : MealDetailUiState
    data class Error(@StringRes val message: Int) : MealDetailUiState
}

sealed interface MealDetailEvent {
    data object Deleted : MealDetailEvent
    data object Duplicated : MealDetailEvent
}
