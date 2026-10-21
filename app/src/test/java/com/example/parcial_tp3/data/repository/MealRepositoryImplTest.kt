package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.dao.MealDao
import com.example.parcial_tp3.data.local.entity.MealEntity
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.MealType
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * MealRepositoryImpl con un DAO falso (specs/CONTEXTO.md, seccion 7): sin Room ni emulador.
 */
class MealRepositoryImplTest {

    private val dao = FakeMealDao()
    private val repository = MealRepositoryImpl(dao)

    private val hoy = LocalDate.of(2026, 10, 21)

    private val entityGuardada = MealEntity(
        id = 4,
        name = "Avena con fruta",
        date = "2026-10-21",
        type = "BREAKFAST",
        kcal = 320,
        proteinG = 12.5,
        carbsG = 54.0,
        fatG = 6.25,
    )

    @Test
    fun `C6 observeMealsByDate pide la fecha ISO al DAO y devuelve modelos Meal`() = runTest {
        dao.emitidas.value = listOf(entityGuardada)

        val comidas = repository.observeMealsByDate(hoy).firstOrNull()

        assertEquals("2026-10-21", dao.fechaPedida)
        assertEquals(
            listOf(
                Meal(
                    id = 4,
                    name = "Avena con fruta",
                    date = hoy,
                    type = MealType.BREAKFAST,
                    kcal = 320,
                    proteinG = 12.5,
                    carbsG = 54.0,
                    fatG = 6.25,
                ),
            ),
            comidas,
        )
    }

    @Test
    fun `C7 addMeal manda una Entity al DAO y devuelve el id nuevo`() = runTest {
        dao.idAsignado = 9
        val almuerzo = Meal(
            name = "Pollo con arroz",
            date = hoy,
            type = MealType.LUNCH,
            kcal = 520,
            proteinG = 38.5,
            carbsG = 55.0,
            fatG = 12.25,
        )

        val id = repository.addMeal(almuerzo)

        assertEquals(9L, id)
        assertEquals(
            listOf(
                MealEntity(
                    id = 0,
                    name = "Pollo con arroz",
                    date = "2026-10-21",
                    type = "LUNCH",
                    kcal = 520,
                    proteinG = 38.5,
                    carbsG = 55.0,
                    fatG = 12.25,
                ),
            ),
            dao.recibidas,
        )
    }
}

/** DAO falso: anota lo que le piden y devuelve lo que el test le deja preparado. */
private class FakeMealDao : MealDao {

    val emitidas = MutableStateFlow<List<MealEntity>>(emptyList())
    val recibidas = mutableListOf<MealEntity>()
    var fechaPedida: String? = null
    var idAsignado = 1L

    override suspend fun insert(meal: MealEntity): Long {
        recibidas += meal
        return idAsignado
    }

    override fun observeByDate(date: String): Flow<List<MealEntity>> {
        fechaPedida = date
        return emitidas
    }
}
