package com.example.parcial_tp3.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.parcial_tp3.data.local.dao.MealDao
import com.example.parcial_tp3.data.local.entity.MealEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * DAO de comidas con una base en memoria (specs/CONTEXTO.md, seccion 7).
 *
 * Los nombres van con guion bajo y no con espacios: con minSdk 29, el DEX de los tests
 * instrumentados no acepta espacios en los nombres de funcion.
 */
@RunWith(AndroidJUnit4::class)
class MealDaoTest {

    private lateinit var db: VoraDatabase
    private lateinit var dao: MealDao

    private val hoy = "2026-10-21"
    private val ayer = "2026-10-20"

    private val desayuno = MealEntity(
        name = "Avena con fruta",
        date = "2026-10-21",
        type = "BREAKFAST",
        kcal = 320,
        proteinG = 12.5,
        carbsG = 54.0,
        fatG = 6.25,
    )
    private val almuerzo = desayuno.copy(name = "Pollo con arroz", type = "LUNCH", kcal = 520)

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            VoraDatabase::class.java,
        ).build()
        dao = db.mealDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun C1_insert_guarda_y_observeByDate_lee_la_comida() = runTest {
        val id = dao.insert(desayuno)

        assertEquals(listOf(desayuno.copy(id = id)), dao.observeByDate(hoy).first())
    }

    @Test
    fun C2_observeByDate_lista_solo_las_comidas_de_esa_fecha_ordenadas_por_id() = runTest {
        val idAlmuerzo = dao.insert(almuerzo)
        dao.insert(desayuno.copy(date = ayer))
        val idDesayuno = dao.insert(desayuno)

        assertEquals(
            listOf(almuerzo.copy(id = idAlmuerzo), desayuno.copy(id = idDesayuno)),
            dao.observeByDate(hoy).first(),
        )
    }

    @Test
    fun C3_dos_inserts_devuelven_ids_distintos_y_mayores_que_cero() = runTest {
        val primero = dao.insert(desayuno)
        val segundo = dao.insert(almuerzo)

        assertTrue("ids: $primero y $segundo", primero > 0 && segundo > 0 && primero != segundo)
    }

    @Test
    fun C9_observeByDate_sin_comidas_devuelve_lista_vacia() = runTest {
        dao.insert(desayuno.copy(date = ayer))

        assertEquals(emptyList<MealEntity>(), dao.observeByDate(hoy).first())
    }

    @Test
    fun C10_observeByDate_emite_la_lista_nueva_cuando_se_inserta_una_comida() = runTest {
        val emisiones = Channel<List<MealEntity>>(Channel.UNLIMITED)
        val job = launch(Dispatchers.Default) {
            dao.observeByDate(hoy).collect { emisiones.send(it) }
        }
        // Room consulta en su propio hilo: se usa tiempo real y no el virtual de runTest.
        var id = 0L
        val (inicial, nueva) = withContext(Dispatchers.Default) {
            withTimeout(5_000) {
                val primera = emisiones.receive()
                id = dao.insert(desayuno)
                primera to emisiones.receive()
            }
        }
        job.cancel()

        assertEquals(emptyList<MealEntity>(), inicial)
        assertEquals(listOf(desayuno.copy(id = id)), nueva)
    }
}
