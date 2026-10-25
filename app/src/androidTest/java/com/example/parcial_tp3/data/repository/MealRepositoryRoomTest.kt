package com.example.parcial_tp3.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.parcial_tp3.data.local.VoraDatabase
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.MealType
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * MealRepositoryImpl con Room en memoria: SQLiteException no se puede crear en un test JVM sin
 * Robolectric, así que la traducción de errores de escritura se prueba acá.
 */
@RunWith(AndroidJUnit4::class)
class MealRepositoryRoomTest {

    private lateinit var db: VoraDatabase
    private lateinit var repository: MealRepositoryImpl

    private val almuerzo = Meal(
        name = "Pollo con arroz",
        date = LocalDate.of(2026, 10, 24),
        type = MealType.LUNCH,
        kcal = 520,
        proteinG = 38.0,
        carbsG = 54.0,
        fatG = 14.0,
    )

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            VoraDatabase::class.java,
        ).build()
        repository = MealRepositoryImpl(db.mealDao())
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun C15_un_id_repetido_es_MealDataException_STORAGE() = runTest {
        val id = repository.addMeal(almuerzo)

        // insert es ABORT: Room tira SQLiteConstraintException y el repositorio la traduce.
        val error = try {
            repository.addMeal(almuerzo.copy(id = id))
            null
        } catch (e: Throwable) {
            e
        }

        assertEquals("tiró: $error", MealError.STORAGE, (error as? MealDataException)?.error)
    }
}
