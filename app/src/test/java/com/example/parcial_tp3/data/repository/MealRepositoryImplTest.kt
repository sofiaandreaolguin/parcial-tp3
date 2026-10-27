package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.dao.MealDao
import com.example.parcial_tp3.data.local.entity.MealEntity
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.MealType
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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

    private val comidaGuardada = Meal(
        id = 4,
        name = "Avena con fruta",
        date = hoy,
        type = MealType.BREAKFAST,
        kcal = 320,
        proteinG = 12.5,
        carbsG = 54.0,
        fatG = 6.25,
    )

    /** El error que tira [bloque], o null si no tira nada. */
    private suspend fun errorDe(bloque: suspend () -> Unit): Throwable? =
        try {
            bloque()
            null
        } catch (e: Throwable) {
            e
        }

    private fun assertMealError(esperado: MealError, error: Throwable?) {
        assertEquals("tiro: $error", esperado, (error as? MealDataException)?.error)
    }

    @Test
    fun `C13 observeMeal devuelve Meal y null cuando no existe`() = runTest {
        dao.porId.value = entityGuardada

        assertEquals(comidaGuardada, repository.observeMeal(4).firstOrNull())
        assertEquals(4L, dao.idPedido)

        dao.porId.value = null
        assertNull(repository.observeMeal(4).first())
    }

    @Test
    fun `C14 updateMeal y deleteMeal llegan al DAO con el mismo id`() = runTest {
        repository.updateMeal(comidaGuardada.copy(kcal = 400))
        repository.deleteMeal(comidaGuardada)

        assertEquals(listOf(entityGuardada.copy(kcal = 400)), dao.actualizadas)
        assertEquals(listOf(entityGuardada), dao.borradas)
    }

    @Test
    fun `C73 deleteMeal no falla si no habia nada que borrar`() = runTest {
        dao.filasBorradas = 0

        val error = errorDe { repository.deleteMeal(comidaGuardada) }

        assertNull("no debería tirar nada: $error", error)
        assertEquals(listOf(entityGuardada), dao.borradas)
    }

    @Test
    fun `C16 una Entity invalida es MealDataException INVALID_DATA`() = runTest {
        dao.emitidas.value = listOf(entityGuardada.copy(type = "BRUNCH"))
        assertMealError(MealError.INVALID_DATA, errorDe { repository.observeMealsByDate(hoy).first() })

        dao.emitidas.value = listOf(entityGuardada.copy(date = "21/10/2026"))
        assertMealError(MealError.INVALID_DATA, errorDe { repository.observeMealsByDate(hoy).first() })

        dao.porId.value = entityGuardada.copy(type = "BRUNCH")
        assertMealError(MealError.INVALID_DATA, errorDe { repository.observeMeal(4).first() })
    }

    @Test
    fun `C17 update de una comida borrada es NOT_FOUND`() = runTest {
        dao.filasActualizadas = 0

        assertMealError(MealError.NOT_FOUND, errorDe { repository.updateMeal(comidaGuardada) })
    }

    @Test
    fun `C56 ingredientes invalidos son MealDataException INVALID_DATA`() = runTest {
        val invalidos = listOf(
            "hola",
            "{}",
            "",
            "null",
            "[null]",
            """[{"name":"x","grams":"abc","kcal":1}]""",
            """[{"grams":1,"kcal":1}]""",
            """[{"name":null,"grams":1,"kcal":1}]""",
        )

        for (texto in invalidos) {
            dao.emitidas.value = listOf(entityGuardada.copy(ingredients = texto))
            val porFecha = errorDe { repository.observeMealsByDate(hoy).first() }
            assertEquals("observeMealsByDate con '$texto' tiró: $porFecha", MealError.INVALID_DATA, (porFecha as? MealDataException)?.error)

            dao.porId.value = entityGuardada.copy(ingredients = texto)
            val porId = errorDe { repository.observeMeal(4).first() }
            assertEquals("observeMeal con '$texto' tiró: $porId", MealError.INVALID_DATA, (porId as? MealDataException)?.error)
        }
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

    val porId = MutableStateFlow<MealEntity?>(null)
    var idPedido: Long? = null
    val actualizadas = mutableListOf<MealEntity>()
    var filasActualizadas = 1
    var filasBorradas = 1

    override fun observeById(id: Long): Flow<MealEntity?> {
        idPedido = id
        return porId
    }

    override suspend fun update(meal: MealEntity): Int {
        actualizadas += meal
        return filasActualizadas
    }

    val borradas = mutableListOf<MealEntity>()

    override suspend fun delete(meal: MealEntity): Int {
        borradas += meal
        return filasBorradas
    }
}
