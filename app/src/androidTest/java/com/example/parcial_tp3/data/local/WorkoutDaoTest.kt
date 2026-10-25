package com.example.parcial_tp3.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.parcial_tp3.data.local.dao.WorkoutDao
import com.example.parcial_tp3.data.local.entity.WorkoutEntity
import com.example.parcial_tp3.data.local.entity.WorkoutExerciseEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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

    // Los "cache_" son de specs/funcionales/detalle-y-cache-de-entrenamientos.md
    private val detalleW01 = fuerzaInicial.copy(description = "Rutina de ejemplo para el trabajo práctico.")

    private fun ejercicio(workoutId: String, position: Int, exerciseId: String, name: String) = WorkoutExerciseEntity(
        workoutId = workoutId,
        position = position,
        exerciseId = exerciseId,
        name = name,
        muscleGroup = "Piernas",
        sets = 3,
        reps = 10,
        restSeconds = 60,
        instructions = "Instrucciones de $name.",
        tip = "Usá una carga que puedas controlar.",
    )

    private val sentadilla = ejercicio("w01", 0, "e01", "Sentadilla")
    private val remo = ejercicio("w01", 1, "e02", "Remo con mancuerna")
    private val press = ejercicio("w01", 2, "e03", "Press con mancuernas")

    @Test
    fun cache_C8_saveDetail_guarda_la_fila_y_los_ejercicios_en_orden() = runTest {
        // Fuera de orden a proposito: getExercises ordena por position
        dao.saveDetail(detalleW01, listOf(press, sentadilla, remo))

        assertEquals(detalleW01, dao.getById("w01"))
        assertEquals(listOf(sentadilla, remo, press), dao.getExercises("w01"))
    }

    @Test
    fun cache_C9_saveDetail_otra_vez_reemplaza_los_ejercicios() = runTest {
        dao.saveDetail(detalleW01, listOf(sentadilla, remo, press))
        val nuevos = listOf(
            ejercicio("w01", 0, "e04", "Puente de glúteos"),
            ejercicio("w01", 1, "e05", "Press de hombros"),
        )

        dao.saveDetail(detalleW01, nuevos)

        assertEquals(nuevos, dao.getExercises("w01"))
    }

    @Test
    fun cache_C10_saveSummaries_conserva_el_detalle_ya_guardado() = runTest {
        dao.saveDetail(detalleW01, listOf(sentadilla, remo, press))
        // Como llega del catalogo: sin description y con datos nuevos del resumen
        val resumenNuevo = fuerzaInicial.copy(name = "Fuerza inicial renovada", durationMinutes = 30)

        dao.saveSummaries(listOf(resumenNuevo))

        assertEquals(resumenNuevo.copy(description = detalleW01.description), dao.getById("w01"))
        assertEquals(listOf(sentadilla, remo, press), dao.getExercises("w01"))
    }

    @Test
    fun cache_C11_saveSummaries_de_uno_nuevo_deja_description_nulo() = runTest {
        dao.saveSummaries(listOf(completo))

        val fila = dao.getById("w02")

        assertNotNull("saveSummaries tiene que guardar la fila nueva", fila)
        assertEquals(completo, fila)
        assertNull(fila?.description)
    }
}
