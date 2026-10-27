package com.example.parcial_tp3.ui.screens.nutrition

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.parcial_tp3.data.local.VoraDatabase
import com.example.parcial_tp3.data.repository.FoodRepository
import com.example.parcial_tp3.data.repository.MealRepositoryImpl
import com.example.parcial_tp3.domain.model.FoodItem
import com.example.parcial_tp3.domain.model.FoodPage
import com.example.parcial_tp3.domain.model.MealType
import com.example.parcial_tp3.domain.model.Nutrients
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * C33: lo que elige la busqueda termina guardado en Room, de verdad (EXT02).
 * Spec: specs/funcionales/busqueda-de-alimentos.md.
 *
 * Va en androidTest porque usa Room de verdad, que es la regla de la casa. Recorre la cadena
 * entera: `onConfirm` arma el MealForm, `onPrefill` se lo deja a 07, `onSave` lo valida y lo
 * guarda, y despues se lee por fecha.
 *
 * **Es el test que atrapa el bug del formato.** `parseKcal` valida con `^-?\d+$`: si la busqueda
 * precargara "248.0" en vez de "248", la validacion de 07 marcaria NOT_A_WHOLE_NUMBER y no se
 * guardaria nada. Desde un test de ViewModel suelto eso no se ve, porque ahi nadie valida.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class FoodSearchGuardadoRoomTest {

    private lateinit var db: VoraDatabase
    private lateinit var comidas: MealRepositoryImpl

    private val hoy = LocalDate.of(2026, 10, 27)
    private val pollo = FoodItem("f05", "Pollo cocido", Nutrients(165, 31.0, 0.0, 3.6))

    /** Devuelve siempre el mismo alimento: este test no mira la red. */
    private class CatalogoQuieto(private val item: FoodItem) : FoodRepository {
        override suspend fun searchFoods(q: String, page: Int): Result<FoodPage> =
            Result.success(FoodPage(listOf(item), 1, 50, 1))
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            VoraDatabase::class.java,
        ).build()
        comidas = MealRepositoryImpl(db.mealDao())
    }

    @After
    fun tearDown() {
        db.close()
        Dispatchers.resetMain()
    }

    @Test
    fun c33_guardar_desde_la_busqueda_deja_la_comida_en_room() = runTest {
        val buscador = FoodSearchViewModel(CatalogoQuieto(pollo))
        val captura = FoodCaptureViewModel(comidas)
        advanceUntilIdle()

        buscador.onFoodSelected(pollo)
        buscador.onGramsChange("150")
        val form = buscador.onConfirm(hoy)
        requireNotNull(form) { "onConfirm tenia que devolver el formulario" }

        // El tipo lo elige la persona en 07: la busqueda lo deja en null a proposito (C38).
        assertEquals(null, form.type)
        captura.onPrefill(form.copy(type = MealType.LUNCH))
        captura.onSave()
        advanceUntilIdle()

        val delDia = comidas.observeMealsByDate(hoy).first()
        assertEquals("tiene que haber quedado una comida", 1, delDia.size)
        val comida = delDia.first()
        assertEquals("Pollo cocido", comida.name)
        assertEquals(MealType.LUNCH, comida.type)
        assertEquals(248, comida.kcal)
        assertEquals(46.5, comida.proteinG, 0.0001)
        assertEquals(0.0, comida.carbsG, 0.0001)
        assertEquals(5.4, comida.fatG, 0.0001)
    }

    @Test
    fun c33_el_total_del_dia_suma_lo_que_guardo_la_busqueda() = runTest {
        val buscador = FoodSearchViewModel(CatalogoQuieto(pollo))
        val captura = FoodCaptureViewModel(comidas)
        advanceUntilIdle()

        for (gramos in listOf("150", "100")) {
            buscador.onFoodSelected(pollo)
            buscador.onGramsChange(gramos)
            captura.onPrefill(buscador.onConfirm(hoy)!!.copy(type = MealType.LUNCH))
            captura.onSave()
            advanceUntilIdle()
        }

        val delDia = comidas.observeMealsByDate(hoy).first()
        assertEquals(2, delDia.size)
        // 248 de la porcion de 150 g mas 165 de la de 100 g: es lo que cambia el total de RF04.
        assertEquals(413, delDia.sumOf { it.kcal })
    }
}
