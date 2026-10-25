package com.example.parcial_tp3.domain

import com.example.parcial_tp3.domain.model.Meal

/** RF03. Las reglas de cada campo estan en specs/funcionales/carga-y-eliminacion-de-comidas.md. */
sealed interface MealValidation {
    data class Valid(val meal: Meal) : MealValidation
    data class Invalid(val errors: Map<MealField, FieldError>) : MealValidation
}

enum class MealField { NAME, TYPE, KCAL, PROTEIN, CARBS, FAT }

enum class FieldError { REQUIRED, NOT_A_NUMBER, NOT_A_WHOLE_NUMBER, NEGATIVE }

// Firma vacia de la tarea VC-l: la completa la sesion de implementar.
@Suppress("UNUSED_PARAMETER")
fun validateMeal(form: MealForm, id: Long = 0): MealValidation = MealValidation.Invalid(emptyMap())
