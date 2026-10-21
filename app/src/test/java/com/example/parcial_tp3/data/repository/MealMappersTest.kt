package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.entity.MealEntity
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.MealType
import java.time.LocalDate
import java.time.format.DateTimeParseException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
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
}
