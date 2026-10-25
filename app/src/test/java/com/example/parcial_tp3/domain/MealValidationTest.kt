package com.example.parcial_tp3.domain

import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.MealType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** validateMeal (RF03): la tabla de valores límite de specs/funcionales/carga-y-eliminacion-de-comidas.md. */
class MealValidationTest {

    private val fecha = LocalDate.of(2026, 10, 24)

    private val valido = MealForm(
        name = "Pollo con arroz",
        type = MealType.LUNCH,
        date = fecha,
        kcal = "520",
        protein = "38",
        carbs = "54",
        fat = "14",
    )

    private fun errores(form: MealForm): Map<MealField, FieldError> =
        (validateMeal(form) as? MealValidation.Invalid)?.errors.orEmpty()

    private fun comida(form: MealForm, id: Long = 0): Meal? =
        (validateMeal(form, id) as? MealValidation.Valid)?.meal

    // "válido" se espera como el número; un error, como el FieldError.
    private val tablaKcal: List<Pair<String, Any>> = listOf(
        "" to FieldError.REQUIRED,
        "  " to FieldError.REQUIRED,
        "520" to 520,
        " 520 " to 520,
        "0" to 0,
        "-0" to 0,
        "-5" to FieldError.NEGATIVE,
        "520.5" to FieldError.NOT_A_WHOLE_NUMBER,
        "520,5" to FieldError.NOT_A_WHOLE_NUMBER,
        "-5.5" to FieldError.NOT_A_WHOLE_NUMBER,
        "abc" to FieldError.NOT_A_WHOLE_NUMBER,
        "+5" to FieldError.NOT_A_WHOLE_NUMBER,
        "1e3" to FieldError.NOT_A_WHOLE_NUMBER,
        "NaN" to FieldError.NOT_A_WHOLE_NUMBER,
        "Infinity" to FieldError.NOT_A_WHOLE_NUMBER,
        "12,5,3" to FieldError.NOT_A_WHOLE_NUMBER,
        "1.234,5" to FieldError.NOT_A_WHOLE_NUMBER,
        "3000000000" to FieldError.NOT_A_WHOLE_NUMBER,
    )

    private val tablaMacro: List<Pair<String, Any>> = listOf(
        "" to FieldError.REQUIRED,
        "  " to FieldError.REQUIRED,
        "520" to 520.0,
        " 520 " to 520.0,
        "0" to 0.0,
        "-0" to 0.0,
        "-5" to FieldError.NEGATIVE,
        "520.5" to 520.5,
        "520,5" to 520.5,
        "-5.5" to FieldError.NEGATIVE,
        "abc" to FieldError.NOT_A_NUMBER,
        "+5" to FieldError.NOT_A_NUMBER,
        "1e3" to FieldError.NOT_A_NUMBER,
        "NaN" to FieldError.NOT_A_NUMBER,
        "Infinity" to FieldError.NOT_A_NUMBER,
        "12,5,3" to FieldError.NOT_A_NUMBER,
        "1.234,5" to FieldError.NOT_A_NUMBER,
        "3000000000" to 3_000_000_000.0,
    )

    @Test
    fun `C1 nombre vacio o con solo espacios es REQUIRED`() {
        assertEquals(FieldError.REQUIRED, errores(valido.copy(name = ""))[MealField.NAME])
        assertEquals(FieldError.REQUIRED, errores(valido.copy(name = "   "))[MealField.NAME])
    }

    @Test
    fun `C2 el nombre se guarda sin espacios en los bordes`() {
        assertEquals("Pollo con arroz", comida(valido.copy(name = "  Pollo con arroz  "))?.name)
    }

    @Test
    fun `C3 sin tipo es TYPE REQUIRED`() {
        assertEquals(FieldError.REQUIRED, errores(valido.copy(type = null))[MealField.TYPE])
    }

    @Test
    fun `C4 kcal da el resultado de cada fila de la tabla`() {
        val fallas = tablaKcal.mapNotNull { (texto, esperado) ->
            val form = valido.copy(kcal = texto)
            val obtenido: Any? = comida(form)?.kcal ?: errores(form)[MealField.KCAL]
            if (obtenido == esperado) null else "\"$texto\": esperaba $esperado y dio $obtenido"
        }

        assertEquals(emptyList<String>(), fallas)
    }

    @Test
    fun `C5 los macros dan el resultado de cada fila de la tabla`() {
        val campos: List<Triple<MealField, (MealForm, String) -> MealForm, (Meal) -> Double>> = listOf(
            Triple(MealField.PROTEIN, { f, t -> f.copy(protein = t) }, { it.proteinG }),
            Triple(MealField.CARBS, { f, t -> f.copy(carbs = t) }, { it.carbsG }),
            Triple(MealField.FAT, { f, t -> f.copy(fat = t) }, { it.fatG }),
        )
        val fallas = campos.flatMap { (campo, conTexto, gramos) ->
            tablaMacro.mapNotNull { (texto, esperado) ->
                val form = conTexto(valido, texto)
                val obtenido: Any? = comida(form)?.let(gramos) ?: errores(form)[campo]
                // == entre Double y no equals: así -0.0 cuenta como 0.0
                val coincide = if (esperado is Double && obtenido is Double) esperado == obtenido else esperado == obtenido
                if (coincide) null else "$campo \"$texto\": esperaba $esperado y dio $obtenido"
            }
        }

        assertEquals(emptyList<String>(), fallas)
    }

    @Test
    fun `C6 los macros aceptan coma y punto decimal`() {
        assertEquals(12.5, comida(valido.copy(protein = "12,5"))?.proteinG)
        assertEquals(12.5, comida(valido.copy(protein = "12.5"))?.proteinG)
    }

    @Test
    fun `C7 el cero es valido en kcal y macros`() {
        val meal = comida(valido.copy(kcal = "0", protein = "0", carbs = "0", fat = "0"))

        assertEquals(listOf(0.0, 0.0, 0.0, 0.0), meal?.let { listOf(it.kcal.toDouble(), it.proteinG, it.carbsG, it.fatG) })
    }

    @Test
    fun `C8 trae el error de cada campo invalido`() {
        val form = valido.copy(name = " ", type = null, kcal = "abc", protein = "-1")

        assertEquals(
            mapOf(
                MealField.NAME to FieldError.REQUIRED,
                MealField.TYPE to FieldError.REQUIRED,
                MealField.KCAL to FieldError.NOT_A_WHOLE_NUMBER,
                MealField.PROTEIN to FieldError.NEGATIVE,
            ),
            errores(form),
        )
    }

    @Test
    fun `C9 un formulario valido da la Meal con el id pedido`() {
        val esperada = Meal(
            id = 7,
            name = "Pollo con arroz",
            date = fecha,
            type = MealType.LUNCH,
            kcal = 520,
            proteinG = 38.0,
            carbsG = 54.0,
            fatG = 14.0,
        )

        assertEquals(esperada, comida(valido, id = 7))
        val nueva = comida(valido)
        assertTrue("una nueva lleva id 0: $nueva", nueva == esperada.copy(id = 0))
    }
}
