package com.example.parcial_tp3.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.parcial_tp3.data.local.dao.WorkoutDao
import com.example.parcial_tp3.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * DAO de la cache de entrenamientos con una base en memoria (specs/CONTEXTO.md, seccion 7).
 *
 * Los nombres van con guion bajo y no con espacios: con minSdk 29, el DEX de los tests
 * instrumentados no acepta espacios en los nombres de funcion.
 */
@RunWith(AndroidJUnit4::class)
class WorkoutDaoTest {

    private lateinit var db: VoraDatabase
    private lateinit var dao: WorkoutDao

    private val fuerzaInicial = WorkoutEntity("w01", "Fuerza inicial", "beginner", 25, 3)
    private val completo = WorkoutEntity("w02", "Entrenamiento completo", "intermediate", 35, 3)

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            VoraDatabase::class.java,
        ).build()
        dao = db.workoutDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun C6_upsertAll_guarda_y_observeAll_lee_lo_guardado() = runTest {
        dao.upsertAll(listOf(fuerzaInicial, completo))

        assertEquals(listOf(fuerzaInicial, completo), dao.observeAll().first())
    }

    @Test
    fun C7_upsert_con_el_mismo_id_reemplaza_sin_duplicar() = runTest {
        dao.upsertAll(listOf(fuerzaInicial))
        val actualizado = fuerzaInicial.copy(durationMinutes = 30)

        dao.upsertAll(listOf(actualizado))

        assertEquals(listOf(actualizado), dao.observeAll().first())
    }
}
