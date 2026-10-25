package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.StringRes
import com.example.parcial_tp3.domain.model.Meal

/** Nutrition / 08. */
sealed interface MealDetailUiState {
    data object Loading : MealDetailUiState
    data class Content(
        val meal: Meal,
        val confirmingDelete: Boolean = false,
        /** Eliminando o duplicando. */
        val isBusy: Boolean = false,
        @StringRes val actionError: Int? = null,
    ) : MealDetailUiState
    data object NotFound : MealDetailUiState
    data class Error(@StringRes val message: Int) : MealDetailUiState
}

sealed interface MealDetailEvent {
    data object Deleted : MealDetailEvent
    data object Duplicated : MealDetailEvent
}
