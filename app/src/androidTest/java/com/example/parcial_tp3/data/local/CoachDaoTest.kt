package com.example.parcial_tp3.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.parcial_tp3.data.local.dao.CoachDao
import com.example.parcial_tp3.data.local.entity.CoachMessageEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * CoachDao con una base en memoria (EXT01, C14 y C2).
 *
 * **C16 no se prueba aca:** que el hilo sobreviva a cerrar la app no se puede demostrar con
 * una base en memoria. Va como evidencia manual en el emulador (ver la spec, seccion Tests).
 */
class CoachDaoTest {

    private lateinit var db: VoraDatabase
    private lateinit var dao: CoachDao

    private fun mensaje(texto: String, createdAt: Long, role: String = "user") =
        CoachMessageEntity(role = role, text = texto, topic = null, isSimulated = false, createdAt = createdAt)

    @Before
    fun crearBase() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            VoraDatabase::class.java,
        ).build()
        dao = db.coachDao()
    }

    @After
    fun cerrarBase() = db.close()

    @Test
    fun c14_ordena_por_createdAt() = runTest {
        dao.insert(mensaje("segundo", createdAt = 20))
        dao.insert(mensaje("primero", createdAt = 10))

        val hilo = dao.observeAll().first()

        assertEquals(listOf("primero", "segundo"), hilo.map { it.text })
    }

    @Test
    fun c14_con_el_mismo_createdAt_ordena_por_id() = runTest {
        dao.insert(mensaje("pregunta", createdAt = 10))
        dao.insert(mensaje("respuesta", createdAt = 10, role = "assistant"))

        val hilo = dao.observeAll().first()

        assertEquals(listOf("pregunta", "respuesta"), hilo.map { it.text })
    }

    @Test
    fun c15_insertar_vuelve_a_emitir_el_hilo() = runTest {
        dao.insert(mensaje("primero", createdAt = 10))
        assertEquals(1, dao.observeAll().first().size)

        dao.insert(mensaje("segundo", createdAt = 20))

        assertEquals(2, dao.observeAll().first().size)
    }

    @Test
    fun c2_count_dice_si_hay_que_sembrar() = runTest {
        assertEquals(0, dao.count())

        dao.insert(mensaje("sembrado", createdAt = 1))

        assertEquals(1, dao.count())
    }
}
