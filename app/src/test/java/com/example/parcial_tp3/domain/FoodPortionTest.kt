package com.example.parcial_tp3.domain

import com.example.parcial_tp3.domain.model.Nutrients
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * El calculo de la porcion (EXT02). Spec: specs/funcionales/busqueda-de-alimentos.md, C9 a C18.
 *
 * Los valores cada 100 g son los del servidor del docente, comprobados el 26/10 contra
 * https://tp3.martinrivas.ar/v1/foods. Los resultados esperados se calcularon con decimales
 * exactos, no con coma flotante: por eso C11 espera 25,4 y no 25,3.
 */
class FoodPortionTest {

    private val pollo = Nutrients(kcal = 165, proteinG = 31.0, carbsG = 0.0, fatG = 3.6)   // f05
    private val avena = Nutrients(kcal = 389, proteinG = 16.9, carbsG = 66.3, fatG = 6.9)  // f01
    private val banana = Nutrients(kcal = 89, proteinG = 1.1, carbsG = 22.8, fatG = 0.3)   // f03
    private val aceite = Nutrients(kcal = 884, proteinG = 0.0, carbsG = 0.0, fatG = 100.0) // f12
    private val leche = Nutrients(kcal = 61, proteinG = 3.2, carbsG = 4.8, fatG = 3.3)      // f02

    private fun assertNutrients(kcal: Int, prot: Double, carbs: Double, fat: Double, real: Nutrients) {
        assertEquals("kcal", kcal, real.kcal)
        assertEquals("proteina", prot, real.proteinG, 0.0001)
        assertEquals("carbohidratos", carbs, real.carbsG, 0.0001)
        assertEquals("grasa", fat, real.fatG, 0.0001)
    }

    @Test
    fun `C9 la porcion escala cada nutriente por gramos sobre cien`() {
        // 200 g = el doble de todo.
        assertNutrients(330, 62.0, 0.0, 7.2, nutrientsForPortion(pollo, 200.0))
    }

    @Test
    fun `C10 ciento cincuenta gramos de pollo dan 248 y 46 coma 5`() {
        // 165 x 1,5 = 247,5 y HALF_UP sube a 248; toInt() daria 247.
        assertNutrients(248, 46.5, 0.0, 5.4, nutrientsForPortion(pollo, 150.0))
    }

    @Test
    fun `C11 ciento cincuenta gramos de avena dan 25 coma 4 de proteina y no 25 coma 3`() {
        // 16,9 x 1,5 = 25,35 exacto -> HALF_UP 25,4.
        // En Double la cuenta da 25.349999999999998, que redondeado daria 25,3: el bug que evita BigDecimal.
        assertEquals(25.4, nutrientsForPortion(avena, 150.0).proteinG, 0.0001)
    }

    @Test
    fun `C12 ciento cincuenta gramos de banana dan 1 coma 7 de proteina y no 1 coma 6`() {
        // 1,1 x 1,5 = 1,65 exacto. HALF_UP da 1,7; HALF_EVEN daria 1,6.
        assertEquals(1.7, nutrientsForPortion(banana, 150.0).proteinG, 0.0001)
    }

    @Test
    fun `C9 las calorias tambien redondean con HALF_UP y no con HALF_EVEN`() {
        // 389 x 0,5 = 194,5 exacto. HALF_UP da 195; HALF_EVEN daria 194 (elige el par).
        // C10 no distingue las dos reglas: 247,5 da 248 con las dos. Este caso si.
        assertEquals(195, nutrientsForPortion(avena, 50.0).kcal)
        // 61 x 0,5 = 30,5 -> 31 con HALF_UP, 30 con HALF_EVEN.
        assertEquals(31, nutrientsForPortion(leche, 50.0).kcal)
    }

    @Test
    fun `C13 cien gramos devuelve los mismos valores del alimento`() {
        assertNutrients(165, 31.0, 0.0, 3.6, nutrientsForPortion(pollo, 100.0))
    }

    @Test
    fun `C14 un nutriente en cero sigue en cero en cualquier porcion`() {
        // f12 Aceite: proteina y carbohidratos en cero, el resto no.
        assertNutrients(442, 0.0, 0.0, 50.0, nutrientsForPortion(aceite, 50.0))
    }

    @Test
    fun `C15 los gramos con coma se leen como decimal`() {
        assertEquals(GramsParse.Ok(150.5), parseGrams("150,5"))
        assertEquals(GramsParse.Ok(150.5), parseGrams("150.5"))
        assertEquals(GramsParse.Ok(150.0), parseGrams(" 150 "))
    }

    @Test
    fun `C16 gramos vacios marcan REQUIRED`() {
        assertEquals(GramsParse.Err(FieldError.REQUIRED), parseGrams(""))
        assertEquals(GramsParse.Err(FieldError.REQUIRED), parseGrams("   "))
    }

    @Test
    fun `C17 gramos que no son numero marcan NOT_A_NUMBER`() {
        // Los mismos casos que descarta MealValidation: sin "+", exponente ni separador de miles.
        for (texto in listOf("abc", "1.2.3", "1e5", "+150", "15 0", "150g")) {
            assertEquals("con \"$texto\"", GramsParse.Err(FieldError.NOT_A_NUMBER), parseGrams(texto))
        }
    }

    @Test
    fun `C15 la coma siempre es decimal, nunca separador de miles`() {
        // Ojo: "1,000" es UN gramo, no mil. Es la regla que ya usa MealValidation para los macros
        // y se respeta para que los dos campos se comporten igual. Queda escrito porque sorprende.
        assertEquals(GramsParse.Ok(1.0), parseGrams("1,000"))
        assertEquals(GramsParse.Ok(1.0), parseGrams("1.000"))
    }

    @Test
    fun `C18 cero negativos y mas de diez mil marcan NEGATIVE`() {
        // parseMacro acepta el 0 porque una comida puede tener 0 g de grasa;
        // una porcion de 0 g no es una comida, asi que aca es error a proposito.
        for (texto in listOf("0", "0,0", "-1", "-150,5", "10001", "99999")) {
            assertEquals("con \"$texto\"", GramsParse.Err(FieldError.NEGATIVE), parseGrams(texto))
        }
        assertEquals(GramsParse.Ok(10_000.0), parseGrams("10000"))
    }
}
