package com.example.parcial_tp3.ui.screens.nutrition

import com.example.parcial_tp3.data.repository.MealDataException
import com.example.parcial_tp3.data.repository.MealError
import com.example.parcial_tp3.domain.FieldError
import com.example.parcial_tp3.domain.MealField
import com.example.parcial_tp3.domain.MealForm
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.MealType
import com.example.parcial_tp3.ui.screens.home.FakeMealRepository
import com.example.parcial_tp3.util.MainDispatcherRule
import com.example.parcial_tp3.util.esTextoDeStrings
import java.time.LocalDate
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Nutrition / 07, que vive en FoodCaptureViewModel: el nombre separa estos tests de los de 04 a
 * 06. Con un MealRepository falso (RF03, RF04, RF07).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FoodConfirmationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val fecha = LocalDate.of(2026, 10, 24)
    private val nueva = -1L

    private val guardada = Meal(
        id = 7,
        name = "Avena con fruta",
        date = LocalDate.of(2026, 10, 20),
        type = MealType.BREAKFAST,
        kcal = 320,
        proteinG = 12.5,
        carbsG = 54.0,
        fatG = 6.25,
    )

    private val formValido = MealForm(
        name = "Ensalada",
        type = MealType.DINNER,
        date = fecha,
        kcal = "350",
        protein = "20",
        carbs = "30,5",
        fat = "12",
    )

    private val repo = FakeMealRepository(listOf(guardada))
    private val vm = FoodCaptureViewModel(repo)

    private fun TestScope.eventos(): List<FoodCaptureEvent> {
        val lista = mutableListOf<FoodCaptureEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.events.toList(lista) }
        return lista
    }

    private fun confirmacion(): ConfirmationState {
        val c = vm.uiState.value.confirmation
        assertNotNull("07 no está abierta", c)
        return c!!
    }

    /** El texto del formulario como número: no importa si dice "38" o "38.0". */
    private fun numero(texto: String): Double? = texto.trim().replace(',', '.').toDoubleOrNull()

    private fun assertNumeros(meal: Meal, form: MealForm) {
        assertEquals(
            listOf(meal.kcal.toDouble(), meal.proteinG, meal.carbsG, meal.fatG),
            listOf(numero(form.kcal), numero(form.protein), numero(form.carbs), numero(form.fat)),
        )
    }

    private fun abrirManualConFormValido() {
        vm.onManualEntry()
        vm.onOpenConfirmation(nueva, fecha, "Comida estimada")
        vm.onFormChange(formValido)
    }

    @Test
    fun `C28 con borrador el formulario viene precargado`() = runTest {
        vm.onPhotoCaptureStarted()

        vm.onOpenConfirmation(nueva, fecha, "Pollo con arroz")

        val c = confirmacion()
        assertEquals(nueva, c.mealId)
        assertEquals("Pollo con arroz", c.form.name)
        assertEquals(MealType.LUNCH, c.form.type)
        assertEquals(fecha, c.form.date)
        assertEquals(listOf(520.0, 38.0, 54.0, 14.0), listOf(c.form.kcal, c.form.protein, c.form.carbs, c.form.fat).map(::numero))
    }

    @Test
    fun `C29 la carga manual viene vacia aunque antes hubo foto`() = runTest {
        vm.onPhotoCaptureStarted()

        vm.onManualEntry()
        vm.onOpenConfirmation(nueva, fecha, "Pollo con arroz")

        assertEquals(MealForm(date = fecha), confirmacion().form)
    }

    @Test
    fun `C30 con id el formulario trae la comida guardada`() = runTest {
        vm.onOpenConfirmation(7, fecha, "Pollo con arroz")

        val c = confirmacion()
        assertEquals(7L, c.mealId)
        assertEquals(Triple("Avena con fruta", MealType.BREAKFAST, guardada.date), Triple(c.form.name, c.form.type, c.form.date))
        assertNumeros(guardada, c.form)
    }

    @Test
    fun `C31 reabrir con el mismo id conserva lo escrito y no recarga`() = runTest {
        vm.onOpenConfirmation(7, fecha, "Pollo con arroz")
        vm.onFormChange(confirmacion().form.copy(name = "Avena editada"))

        vm.onOpenConfirmation(7, fecha, "Pollo con arroz")

        assertEquals("Avena editada", confirmacion().form.name)
        assertEquals(listOf(7L), repo.idsPedidos)
    }

    @Test
    fun `C32 onDiscard cierra el formulario`() = runTest {
        vm.onManualEntry()
        vm.onOpenConfirmation(nueva, fecha, "Comida estimada")
        vm.onFormChange(formValido)
        assertNotNull("07 no se abrió", vm.uiState.value.confirmation)

        vm.onDiscard()
        assertNull(vm.uiState.value.confirmation)

        vm.onOpenConfirmation(nueva, fecha, "Comida estimada")
        assertEquals(MealForm(date = fecha), confirmacion().form)
    }

    @Test
    fun `C33 un id que no existe es notFound`() = runTest {
        vm.onOpenConfirmation(99, fecha, "Pollo con arroz")

        assertTrue(confirmacion().notFound)
    }

    @Test
    fun `C34 guardar invalido muestra errores y no escribe`() = runTest {
        val eventos = eventos()
        vm.onManualEntry()
        vm.onOpenConfirmation(nueva, fecha, "Comida estimada")

        vm.onSave()

        assertEquals(MealField.entries.associateWith { FieldError.REQUIRED }, confirmacion().errors)
        assertEquals(emptyList<Meal>(), repo.agregadas + repo.actualizadas)
        assertEquals(emptyList<FoodCaptureEvent>(), eventos)
    }

    @Test
    fun `C35 corregir un campo borra solo su error`() = runTest {
        vm.onManualEntry()
        vm.onOpenConfirmation(nueva, fecha, "Comida estimada")
        vm.onSave()

        vm.onFormChange(confirmacion().form.copy(name = "Ensalada", kcal = "350"))

        assertEquals(
            setOf(MealField.TYPE, MealField.PROTEIN, MealField.CARBS, MealField.FAT),
            confirmacion().errors.keys,
        )
    }

    @Test
    fun `C36 guardar nueva llama a addMeal emite Saved y limpia`() = runTest {
        val eventos = eventos()
        vm.onPhotoCaptureStarted()
        vm.onOpenConfirmation(nueva, fecha, "Pollo con arroz")

        vm.onSave()

        assertEquals(
            listOf(Meal(name = "Pollo con arroz", date = fecha, type = MealType.LUNCH, kcal = 520, proteinG = 38.0, carbsG = 54.0, fatG = 14.0)),
            repo.agregadas,
        )
        assertEquals(listOf<FoodCaptureEvent>(FoodCaptureEvent.Saved), eventos)
        assertNull(vm.uiState.value.draft)
        assertNull(vm.uiState.value.confirmation)
    }

    @Test
    fun `C37 guardar edicion llama a updateMeal con el mismo id`() = runTest {
        val eventos = eventos()
        vm.onOpenConfirmation(7, fecha, "Pollo con arroz")
        vm.onFormChange(confirmacion().form.copy(kcal = "400"))

        vm.onSave()

        assertEquals(listOf(guardada.copy(kcal = 400)), repo.actualizadas)
        assertEquals(emptyList<Meal>(), repo.agregadas)
        assertEquals(listOf<FoodCaptureEvent>(FoodCaptureEvent.Saved), eventos)
    }

    @Test
    fun `C38 dos toques en guardar escriben una sola vez`() = runTest {
        val eventos = eventos()
        val bloqueo = CompletableDeferred<Unit>()
        repo.bloqueo = bloqueo
        abrirManualConFormValido()

        vm.onSave()
        assertTrue("no está guardando", confirmacion().isSaving)
        vm.onSave()
        bloqueo.complete(Unit)

        assertEquals(1, repo.agregadas.size)
        assertEquals(listOf<FoodCaptureEvent>(FoodCaptureEvent.Saved), eventos)
    }

    @Test
    fun `C39 si guardar falla conserva lo escrito y no emite Saved`() = runTest {
        val eventos = eventos()
        repo.errorAlEscribir = MealDataException(MealError.STORAGE)
        abrirManualConFormValido()

        vm.onSave()

        val c = confirmacion()
        assertEquals(1, repo.agregadas.size)
        assertEquals(formValido, c.form)
        assertTrue("saveError no es un texto de strings.xml: ${c.saveError}", esTextoDeStrings(c.saveError))
        assertFalse(c.isSaving)
        assertEquals(emptyList<FoodCaptureEvent>(), eventos)
    }
}
