package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.entity.MealEntity
import com.example.parcial_tp3.domain.model.Ingredient
import com.example.parcial_tp3.domain.model.Meal
import com.google.gson.JsonParser
import com.example.parcial_tp3.domain.model.MealType
import java.time.LocalDate
import java.time.format.DateTimeParseException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Mapeo entre el modelo Meal y su tabla MealEntity (RF06). Es logica pura: corre sin emulador.
 */
class MealMappersTest {

    private val almuerzo = Meal(
        id = 7,
        name = "Pollo con arroz",
        date = LocalDate.of(2026, 10, 21),
        type = MealType.LUNCH,
        kcal = 520,
        proteinG = 38.5,
        carbsG = 55.0,
        fatG = 12.25,
    )

    private val entityBase = MealEntity(
        id = 7,
        name = "Pollo con arroz",
        date = "2026-10-21",
        type = "LUNCH",
        kcal = 520,
        proteinG = 38.5,
        carbsG = 55.0,
        fatG = 12.25,
    )

    @Test
    fun `C4 Meal pasa a Entity y vuelve igual`() {
        assertEquals(almuerzo, almuerzo.toEntity().toDomain())
    }

    @Test
    fun `C5 la Entity guarda la fecha ISO y el tipo con el nombre del enum`() {
        val entity = almuerzo.toEntity()

        assertEquals("2026-10-21", entity.date)
        assertEquals("LUNCH", entity.type)
    }

    @Test
    fun `C8 un tipo desconocido en la Entity lanza IllegalArgumentException`() {
        assertThrows(IllegalArgumentException::class.java) {
            entityBase.copy(type = "BRUNCH").toDomain()
        }
    }

    @Test
    fun `C11 una fecha no ISO en la Entity lanza DateTimeParseException`() {
        assertThrows(DateTimeParseException::class.java) {
            entityBase.copy(date = "21/10/2026").toDomain()
        }
    }
    private val ingredientes = listOf(
        Ingredient("Pollo", grams = 180, kcal = 280),
        Ingredient("Arroz", grams = 150, kcal = 190),
        Ingredient("Verduras", grams = 100, kcal = 0),
    )

    @Test
    fun `C53 los ingredientes van y vuelven por los mappers en orden`() {
        val entity = almuerzo.copy(ingredients = ingredientes).toEntity()

        val json = JsonParser.parseString(entity.ingredients)
        assertTrue("no es un arreglo JSON: ${entity.ingredients}", json.isJsonArray)
        val leidos = json.asJsonArray.map { e ->
            val o = e.asJsonObject
            Ingredient(o.get("name").asString, o.get("grams").asInt, o.get("kcal").asInt)
        }
        assertEquals(ingredientes, leidos)
        assertEquals(ingredientes, entity.toDomain().ingredients)
    }

    @Test
    fun `C54 sin ingredientes se guarda corchetes vacios`() {
        val entity = almuerzo.toEntity()

        assertEquals("[]", entity.ingredients)
        assertEquals(emptyList<Ingredient>(), entity.toDomain().ingredients)
    }

    @Test
    fun `C55 un nombre con coma comillas y tildes vuelve igual`() {
        val raros = listOf(
            Ingredient("Arroz, integral", grams = 100, kcal = 120),
            Ingredient("Salsa \"casera\"", grams = 30, kcal = 40),
            Ingredient("Pan | tostado", grams = 50, kcal = 130),
            Ingredient("Pollo · papas", grams = 200, kcal = 350),
            Ingredient("Jamón y ñoquis con açaí", grams = 80, kcal = 210),
            Ingredient("  con espacios  ", grams = 10, kcal = 5),
        )

        assertEquals(raros, almuerzo.copy(ingredients = raros).toEntity().toDomain().ingredients)
    }

    @Test
    fun `C57 gramos o kcal que faltan se leen como 0`() {
        val entity = entityBase.copy(ingredients = """[{"name":"Arroz","kcal":190},{"name":"Pollo","grams":180}]""")

        assertEquals(
            listOf(Ingredient("Arroz", grams = 0, kcal = 190), Ingredient("Pollo", grams = 180, kcal = 0)),
            entity.toDomain().ingredients,
        )
    }
}
