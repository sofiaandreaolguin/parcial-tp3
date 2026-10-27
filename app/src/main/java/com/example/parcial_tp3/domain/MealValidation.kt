package com.example.parcial_tp3.domain

import com.example.parcial_tp3.domain.model.Ingredient
import com.example.parcial_tp3.domain.model.Meal

/** RF03. Las reglas de cada campo estan en specs/funcionales/carga-y-eliminacion-de-comidas.md. */
sealed interface MealValidation {
    data class Valid(val meal: Meal) : MealValidation
    data class Invalid(val errors: Map<MealField, FieldError>) : MealValidation
}

enum class MealField { NAME, TYPE, KCAL, PROTEIN, CARBS, FAT }

enum class FieldError { REQUIRED, NOT_A_NUMBER, NOT_A_WHOLE_NUMBER, NEGATIVE }

// Sin "+", exponente, NaN ni separador de miles: toDouble() los aceptaria.
private val WHOLE_NUMBER = Regex("""^-?\d+$""")
private val DECIMAL_NUMBER = Regex("""^-?\d+([.,]\d+)?$""")

/** Revisa todos los campos, no solo el primero que falla. [id] es 0 para una comida nueva. */
fun validateMeal(form: MealForm, id: Long = 0): MealValidation {
    val errors = mutableMapOf<MealField, FieldError>()
    val name = form.name.trim()
    if (name.isEmpty()) errors[MealField.NAME] = FieldError.REQUIRED
    if (form.type == null) errors[MealField.TYPE] = FieldError.REQUIRED

    fun check(field: MealField, parsed: Parsed): Double? {
        parsed.error?.let { errors[field] = it }
        return parsed.value
    }
    val kcal = check(MealField.KCAL, parseKcal(form.kcal))
    val protein = check(MealField.PROTEIN, parseMacro(form.protein))
    val carbs = check(MealField.CARBS, parseMacro(form.carbs))
    val fat = check(MealField.FAT, parseMacro(form.fat))

    if (errors.isNotEmpty()) return MealValidation.Invalid(errors)
    return MealValidation.Valid(
        Meal(
            id = id,
            name = name,
            date = form.date,
            type = form.type!!,
            kcal = kcal!!.toInt(),
            proteinG = protein!!,
            carbsG = carbs!!,
            fatG = fat!!,
        ),
    )
}

fun isValidIngredient(ingredient: Ingredient): Boolean =
    ingredient.name.isNotBlank() && ingredient.grams > 0 && ingredient.kcal >= 0

private class Parsed(val value: Double? = null, val error: FieldError? = null)

private fun parseKcal(text: String): Parsed {
    val t = text.trim()
    if (t.isEmpty()) return Parsed(error = FieldError.REQUIRED)
    // "3000000000" pasa la regex pero no entra en un Int.
    val n = t.takeIf { WHOLE_NUMBER.matches(it) }?.toIntOrNull()
        ?: return Parsed(error = FieldError.NOT_A_WHOLE_NUMBER)
    return if (n < 0) Parsed(error = FieldError.NEGATIVE) else Parsed(n.toDouble())
}

private fun parseMacro(text: String): Parsed {
    val t = text.trim()
    if (t.isEmpty()) return Parsed(error = FieldError.REQUIRED)
    if (!DECIMAL_NUMBER.matches(t)) return Parsed(error = FieldError.NOT_A_NUMBER)
    val n = t.replace(',', '.').toDouble()
    return if (n < 0) Parsed(error = FieldError.NEGATIVE) else Parsed(n)
}
