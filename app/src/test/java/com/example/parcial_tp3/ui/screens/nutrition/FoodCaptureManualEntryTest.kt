package com.example.parcial_tp3.ui.screens.nutrition

import com.example.parcial_tp3.domain.FieldError
import com.example.parcial_tp3.domain.MealField
import com.example.parcial_tp3.domain.MealForm
import com.example.parcial_tp3.domain.model.Ingredient
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.MealType
import com.example.parcial_tp3.ui.screens.home.FakeMealRepository
import com.example.parcial_tp3.util.MainDispatcherRule
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * El pop-up de carga (hoja inferior) de "Log Manually", "Type instead" y la búsqueda, que vive en
 * FoodCaptureViewModel. PR 2 de ingredientes, specs/funcionales/carga-y-eliminacion-de-comidas.md.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FoodCaptureManualEntryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val fecha = LocalDate.of(2026, 10, 27)

    // Sin kcal: el pop-up no las pide.
    private val formValido = MealForm(
        name = "Ensalada",
        type = MealType.DINNER,
        date = fecha,
        protein = "20",
        carbs = "30,5",
        fat = "12",
    )

    private val seleccion = FoodSelection(
        form = MealForm(
            name = "Pollo cocido",
            type = null,
            date = fecha,
            kcal = "248",
            protein = "46.5",
            carbs = "0",
            fat = "5.4",
        ),
        ingredient = Ingredient("Pollo cocido", grams = 150, kcal = 248),
    )

    private val repo = FakeMealRepository()
    private val vm = FoodCaptureViewModel(repo)

    private fun popUp(): ManualEntryState {
        val m = vm.uiState.value.manualEntry
        assertNotNull("el pop-up de carga no está abierto", m)
        return m!!
    }

    @Test
    fun `C29 abrir la carga manual la deja vacia y sin borrador`() = runTest {
        vm.onPhotoCaptureStarted()

        vm.onOpenManualEntry(fecha)

        assertEquals(ManualEntryState(MealForm(date = fecha)), popUp())
        assertNull("el borrador de la foto tiene que irse", vm.uiState.value.draft)
    }

    @Test
    fun `C35 corregir un campo del pop-up borra solo su error`() = runTest {
        vm.onOpenManualEntry(fecha)
        assertFalse(vm.onContinueManualEntry())

        vm.onManualEntryChange(popUp().form.copy(name = "Ensalada", protein = "20"))

        assertEquals(setOf(MealField.TYPE, MealField.CARBS, MealField.FAT), popUp().errors.keys)
    }

    @Test
    fun `C78 continuar invalido muestra errores y no abre 07`() = runTest {
        vm.onOpenManualEntry(fecha)
        val escrito = MealForm(name = " ", date = fecha, protein = "abc", carbs = "-1", fat = "")
        vm.onManualEntryChange(escrito)

        assertFalse(vm.onContinueManualEntry())

        val m = popUp()
        assertEquals(
            mapOf(
                MealField.NAME to FieldError.REQUIRED,
                MealField.TYPE to FieldError.REQUIRED,
                MealField.PROTEIN to FieldError.NOT_A_NUMBER,
                MealField.CARBS to FieldError.NEGATIVE,
                MealField.FAT to FieldError.REQUIRED,
            ),
            m.errors,
        )
        assertEquals(escrito, m.form)
        assertNull("no tiene que abrir 07", vm.uiState.value.confirmation)

        // Segundo toque en "Continue": el primero ya cerró el pop-up.
        vm.onManualEntryChange(formValido)
        assertTrue("con datos válidos tiene que seguir", vm.onContinueManualEntry())
        val abierta = vm.uiState.value.confirmation

        assertFalse("el segundo toque no tiene que seguir", vm.onContinueManualEntry())
        assertSame(abierta, vm.uiState.value.confirmation)
    }

    @Test
    fun `C79 continuar valido abre 07 con el form y los ingredientes del pop-up`() = runTest {
        vm.onOpenManualEntry(fecha)
        vm.onManualEntryChange(formValido)

        assertTrue(vm.onContinueManualEntry())

        assertNull("el pop-up tiene que cerrarse", vm.uiState.value.manualEntry)
        val c = vm.uiState.value.confirmation
        assertNotNull("07 no se abrió", c)
        assertEquals(formValido, c!!.form)
        assertEquals(emptyList<Ingredient>(), c.ingredients)
    }

    @Test
    fun `C80 cerrar el pop-up no abre 07 ni escribe`() = runTest {
        vm.onOpenManualEntry(fecha)
        popUp()
        vm.onManualEntryChange(formValido)

        vm.onDismissManualEntry()

        assertNull(vm.uiState.value.manualEntry)
        assertNull(vm.uiState.value.confirmation)
        assertEquals(emptyList<Meal>(), repo.agregadas)
    }

    @Test
    fun `C82 onPrefill abre el pop-up con el alimento`() = runTest {
        vm.onPhotoCaptureStarted()

        vm.onPrefill(seleccion)

        val m = popUp()
        assertEquals(seleccion.form, m.form)
        assertEquals(listOf(seleccion.ingredient), m.ingredients)
        assertNull("el borrador de la foto tiene que irse", vm.uiState.value.draft)
        assertNull("todavía no se abre 07", vm.uiState.value.confirmation)
    }

    @Test
    fun `C83 desde la busqueda 07 llega con el alimento como ingrediente`() = runTest {
        vm.onPrefill(seleccion)
        vm.onManualEntryChange(popUp().form.copy(type = MealType.LUNCH))

        assertTrue(vm.onContinueManualEntry())

        assertEquals(listOf(seleccion.ingredient), vm.uiState.value.confirmation?.ingredients)
    }
}
