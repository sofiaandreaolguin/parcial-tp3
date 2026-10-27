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

data class IngredientForm(val name: String = "", val grams: String = "", val kcal: String = "")

enum class IngredientField { NAME, GRAMS, KCAL }

sealed interface IngredientValidation {
    data class Valid(val ingredient: Ingredient) : IngredientValidation
    data class Invalid(val errors: Map<IngredientField, FieldError>) : IngredientValidation
}

/** Tope de gramos y kcal: frena errores de tipeo y que la suma de las kcal se pase de un Int (C75). */
private const val MAX_INGREDIENT_VALUE = 10_000

/** Ingrediente a mano (C75, C76). Fuera de rango es NEGATIVE, como en parseGrams de la búsqueda. */
fun validateIngredient(form: IngredientForm): IngredientValidation {
    val errors = mutableMapOf<IngredientField, FieldError>()
    val name = form.name.trim()
    if (name.isEmpty()) errors[IngredientField.NAME] = FieldError.REQUIRED
    val grams = parseWholeInRange(form.grams, 1..MAX_INGREDIENT_VALUE)
    grams.error?.let { errors[IngredientField.GRAMS] = it }
    val kcal = parseWholeInRange(form.kcal, 0..MAX_INGREDIENT_VALUE)
    kcal.error?.let { errors[IngredientField.KCAL] = it }

    if (errors.isNotEmpty()) return IngredientValidation.Invalid(errors)
    return IngredientValidation.Valid(Ingredient(name, grams.value!!.toInt(), kcal.value!!.toInt()))
}

/** Lo que pide el pop-up de carga: las reglas de [validateMeal] sin las kcal, que son la suma (C77). */
fun validateMealDetails(form: MealForm): Map<MealField, FieldError> =
    when (val result = validateMeal(form.copy(kcal = "0"))) {
        is MealValidation.Invalid -> result.errors
        is MealValidation.Valid -> emptyMap()
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

private fun parseWholeInRange(text: String, range: IntRange): Parsed {
    val t = text.trim()
    if (t.isEmpty()) return Parsed(error = FieldError.REQUIRED)
    val n = t.takeIf { WHOLE_NUMBER.matches(it) }?.toIntOrNull()
        ?: return Parsed(error = FieldError.NOT_A_WHOLE_NUMBER)
    return if (n in range) Parsed(n.toDouble()) else Parsed(error = FieldError.NEGATIVE)
}

private fun parseMacro(text: String): Parsed {
    val t = text.trim()
    if (t.isEmpty()) return Parsed(error = FieldError.REQUIRED)
    if (!DECIMAL_NUMBER.matches(t)) return Parsed(error = FieldError.NOT_A_NUMBER)
    val n = t.replace(',', '.').toDouble()
    return if (n < 0) Parsed(error = FieldError.NEGATIVE) else Parsed(n)
}
