package com.example.parcial_tp3.domain

import com.example.parcial_tp3.domain.model.Nutrients
import java.math.BigDecimal
import java.math.RoundingMode

/** Lo que sale de leer los gramos que se tipearon. */
sealed interface GramsParse {
    data class Ok(val grams: Double) : GramsParse
    data class Err(val error: FieldError) : GramsParse
}

/** EXT02: ninguna porcion razonable pasa de esto, y mas arriba la kcal no entra en un Int. */
const val MAX_GRAMS = 10_000.0

// La misma de MealValidation, que la tiene privada: sin "+", exponente ni NaN.
// Ojo: "1,000" coincide y vale UN gramo. La coma es decimal, nunca separador de miles.
private val DECIMAL_NUMBER = Regex("""^-?\d+([.,]\d+)?$""")

private val CIEN = BigDecimal(100)

/**
 * Los nutrientes de una porcion, a partir de los que el servidor da **cada 100 g** (EXT02, C9).
 *
 * La cuenta va en BigDecimal y no en Double a proposito. En Double, 16,9 x 1,5 da
 * 25.349999999999998, que redondeado a un decimal daria 25,3 cuando el valor exacto es 25,35 y
 * tiene que quedar en 25,4. Con HALF_UP y no HALF_EVEN porque 1,65 tiene que dar 1,7 (C12).
 *
 * Las calorias quedan enteras porque `Meal.kcal` es Int, y el valor redondeado es el que se
 * precarga y el que se guarda: es el que suma en el total diario (RF04).
 */
fun nutrientsForPortion(per100g: Nutrients, grams: Double): Nutrients {
    val factor = BigDecimal.valueOf(grams).divide(CIEN)
    fun escalar(valor: Double) = BigDecimal.valueOf(valor).multiply(factor)
    return Nutrients(
        kcal = escalar(per100g.kcal.toDouble()).setScale(0, RoundingMode.HALF_UP).toInt(),
        proteinG = escalar(per100g.proteinG).setScale(1, RoundingMode.HALF_UP).toDouble(),
        carbsG = escalar(per100g.carbsG).setScale(1, RoundingMode.HALF_UP).toDouble(),
        fatG = escalar(per100g.fatG).setScale(1, RoundingMode.HALF_UP).toDouble(),
    )
}

/**
 * Lee los gramos tipeados (C15 a C18).
 *
 * Mas estricto que `parseMacro` en un punto: ahi el 0 es valido porque una comida puede tener
 * 0 g de grasa; aca una porcion de 0 g no es una comida.
 */
fun parseGrams(text: String): GramsParse {
    val t = text.trim()
    if (t.isEmpty()) return GramsParse.Err(FieldError.REQUIRED)
    if (!DECIMAL_NUMBER.matches(t)) return GramsParse.Err(FieldError.NOT_A_NUMBER)
    val gramos = t.replace(',', '.').toDouble()
    return if (gramos <= 0.0 || gramos > MAX_GRAMS) {
        GramsParse.Err(FieldError.NEGATIVE)
    } else {
        GramsParse.Ok(gramos)
    }
}
