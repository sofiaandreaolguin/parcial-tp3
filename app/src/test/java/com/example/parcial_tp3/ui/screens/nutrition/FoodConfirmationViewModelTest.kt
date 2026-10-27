package com.example.parcial_tp3.ui.screens.nutrition

import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.MealDataException
import com.example.parcial_tp3.data.repository.MealError
import com.example.parcial_tp3.domain.FieldError
import com.example.parcial_tp3.domain.IngredientField
import com.example.parcial_tp3.domain.IngredientForm
import com.example.parcial_tp3.domain.MealForm
import com.example.parcial_tp3.domain.model.Ingredient
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

    // Lo que llega del pop-up de carga: sin kcal, porque son la suma de los ingredientes.
    private val formValido = MealForm(
        name = "Ensalada",
        type = MealType.DINNER,
        date = fecha,
        protein = "20",
        carbs = "30,5",
        fat = "12",
    )

    // Lo que la Route resuelve con getString a partir de PhotoMealDraft.
    private val ingredientes = listOf(
        Ingredient("Pollo", grams = 180, kcal = 280),
        Ingredient("Arroz", grams = 150, kcal = 190),
        Ingredient("Verduras", grams = 100, kcal = 50),
    )

    private val repo = FakeMealRepository()
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

    private fun abrirConFoto() {
        vm.onPhotoCaptureStarted()
        vm.onOpenConfirmation(fecha, "Pollo con arroz", ingredientes)
    }

    private fun abrirManual(form: MealForm = formValido) {
        vm.onOpenManualEntry(fecha)
        vm.onManualEntryChange(form)
        assertTrue("el pop-up de carga no dejó seguir a 07", vm.onContinueManualEntry())
    }

    private fun agregarIngrediente(form: IngredientForm) {
        vm.onAddIngredientClick()
        vm.onIngredientEntryChange(form)
        vm.onConfirmIngredient()
    }

    private val lechuga = IngredientForm(name = "Lechuga", grams = "100", kcal = "20")

    @Test
    fun `C28 con borrador el formulario viene precargado`() = runTest {
        vm.onPhotoCaptureStarted()

        vm.onOpenConfirmation(fecha, "Pollo con arroz")

        val c = confirmacion()
        assertEquals("Pollo con arroz", c.form.name)
        assertEquals(MealType.LUNCH, c.form.type)
        assertEquals(fecha, c.form.date)
        assertEquals(listOf(520.0, 38.0, 54.0, 14.0), listOf(c.form.kcal, c.form.protein, c.form.carbs, c.form.fat).map(::numero))
    }

    @Test
    fun `C31 abrir 07 reemplaza la confirmacion anterior`() = runTest {
        // Se salió de 07 por la barra inferior, sin descartar, después de sacar un ingrediente.
        abrirConFoto()
        vm.onRemoveIngredient(0)

        val otros = listOf(Ingredient("Fideos", grams = 200, kcal = 300))
        vm.onOpenConfirmation(fecha, "Fideos con salsa", otros)

        assertEquals("Fideos con salsa", confirmacion().form.name)
        assertEquals(otros, confirmacion().ingredients)

        // También desde el pop-up de carga.
        abrirManual()

        assertEquals(formValido, confirmacion().form)
        assertEquals(emptyList<Ingredient>(), confirmacion().ingredients)
    }

    @Test
    fun `C32 onDiscard cierra el formulario`() = runTest {
        abrirConFoto()
        assertNotNull("07 no se abrió", vm.uiState.value.confirmation)

        vm.onDiscard()
        assertNull(vm.uiState.value.confirmation)

        vm.onOpenConfirmation(fecha, "Otra comida", ingredientes.take(1))
        assertEquals("Otra comida", confirmacion().form.name)
        assertEquals(ingredientes.take(1), confirmacion().ingredients)
    }

    @Test
    fun `C34 guardar invalido no escribe y muestra saveError`() = runTest {
        val eventos = eventos()
        vm.onPhotoCaptureStarted()
        vm.onOpenConfirmation(fecha, "", ingredientes)

        vm.onSave()

        assertEquals(emptyList<Meal>(), repo.agregadas)
        assertEquals(R.string.nutrition_error_save, confirmacion().saveError)
        assertEquals(emptyList<FoodCaptureEvent>(), eventos)
    }

    @Test
    fun `C36 guardar nueva llama a addMeal emite Saved y limpia`() = runTest {
        val eventos = eventos()
        abrirConFoto()

        vm.onSave()

        assertEquals(
            listOf(
                Meal(
                    name = "Pollo con arroz", date = fecha, type = MealType.LUNCH,
                    kcal = 520, proteinG = 38.0, carbsG = 54.0, fatG = 14.0, ingredients = ingredientes,
                ),
            ),
            repo.agregadas,
        )
        assertEquals(listOf<FoodCaptureEvent>(FoodCaptureEvent.Saved), eventos)
        assertNull(vm.uiState.value.draft)
        assertNull(vm.uiState.value.confirmation)
    }

    @Test
    fun `C38 dos toques en guardar escriben una sola vez`() = runTest {
        val eventos = eventos()
        val bloqueo = CompletableDeferred<Unit>()
        repo.bloqueo = bloqueo
        abrirManual()
        agregarIngrediente(lechuga)

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
        abrirManual()
        agregarIngrediente(lechuga)

        vm.onSave()

        val c = confirmacion()
        assertEquals(1, repo.agregadas.size)
        assertEquals(formValido, c.form)
        assertTrue("saveError no es un texto de strings.xml: ${c.saveError}", esTextoDeStrings(c.saveError))
        assertFalse(c.isSaving)
        assertEquals(emptyList<FoodCaptureEvent>(), eventos)
    }

    @Test
    fun `C60 con borrador 07 trae los ingredientes que paso el grafo`() = runTest {
        vm.onPhotoCaptureStarted()

        vm.onOpenConfirmation(fecha, "Pollo con arroz", ingredientes)

        assertEquals(ingredientes, confirmacion().ingredients)
    }

    @Test
    fun `C61 guardar con borrador hace un solo addMeal con sus ingredientes`() = runTest {
        abrirConFoto()

        vm.onSave()

        assertEquals("addMeal no se llamó una sola vez", 1, repo.agregadas.size)
        val meal = repo.agregadas.single()
        assertEquals("Pollo con arroz", meal.name)
        assertEquals(ingredientes, meal.ingredients)
    }

    @Test
    fun `C62 un ingrediente invalido no guarda y muestra el error`() = runTest {
        val ev = eventos()
        vm.onPhotoCaptureStarted()
        vm.onOpenConfirmation(fecha, "Pollo con arroz", listOf(ingredientes[0], Ingredient("Arroz", grams = 0, kcal = 190)))

        vm.onSave()

        assertTrue("no debería llamar a addMeal: ${repo.agregadas}", repo.agregadas.isEmpty())
        val c = confirmacion()
        assertEquals(R.string.nutrition_error_ingredients, c.saveError)
        assertFalse(c.isSaving)
        assertTrue("no debería emitir Saved: $ev", ev.isEmpty())
    }

    @Test
    fun `C63 sin borrador 07 no tiene ingredientes`() = runTest {
        // Sin captura, draft es null.
        vm.onOpenConfirmation(fecha, "Comida estimada", ingredientes)

        assertEquals(emptyList<Ingredient>(), confirmacion().ingredients)
    }

    @Test
    fun `C84 Add Ingredient abre el pop-up y cerrarlo no cambia la lista`() = runTest {
        abrirConFoto()

        vm.onAddIngredientClick()
        assertEquals(IngredientEntryState(), confirmacion().ingredientEntry)

        vm.onIngredientEntryChange(lechuga)
        vm.onDismissIngredientEntry()

        assertNull(confirmacion().ingredientEntry)
        assertEquals(ingredientes, confirmacion().ingredients)
    }

    @Test
    fun `C85 confirmar un ingrediente lo agrega al final o muestra los errores`() = runTest {
        abrirConFoto()

        agregarIngrediente(lechuga)

        assertEquals(ingredientes + Ingredient("Lechuga", grams = 100, kcal = 20), confirmacion().ingredients)
        assertNull("el pop-up tiene que cerrarse", confirmacion().ingredientEntry)

        val invalido = IngredientForm(name = " ", grams = "0", kcal = "abc")
        agregarIngrediente(invalido)

        val c = confirmacion()
        assertEquals("la lista no tiene que cambiar", ingredientes + Ingredient("Lechuga", grams = 100, kcal = 20), c.ingredients)
        assertEquals(
            IngredientEntryState(
                form = invalido,
                errors = mapOf(
                    IngredientField.NAME to FieldError.REQUIRED,
                    IngredientField.GRAMS to FieldError.NEGATIVE,
                    IngredientField.KCAL to FieldError.NOT_A_WHOLE_NUMBER,
                ),
            ),
            c.ingredientEntry,
        )
    }

    @Test
    fun `C86 corregir un campo del ingrediente borra solo su error`() = runTest {
        abrirConFoto()
        val invalido = IngredientForm(name = " ", grams = "0", kcal = "abc")
        agregarIngrediente(invalido)

        vm.onIngredientEntryChange(invalido.copy(name = "Sal"))

        assertEquals(setOf(IngredientField.GRAMS, IngredientField.KCAL), confirmacion().ingredientEntry?.errors?.keys)
    }

    @Test
    fun `C87 las kcal guardadas son la suma de los ingredientes`() = runTest {
        abrirConFoto()
        assertEquals(520, confirmacion().totalKcal)
        vm.onRemoveIngredient(2)
        assertEquals(470, confirmacion().totalKcal)
        agregarIngrediente(IngredientForm(name = "Palta", grams = "50", kcal = "80"))
        assertEquals(550, confirmacion().totalKcal)
        vm.onDiscard()

        // form.kcal no cuenta: lo que se guarda es la suma.
        abrirManual(formValido.copy(kcal = "999"))
        agregarIngrediente(IngredientForm(name = "Pollo", grams = "180", kcal = "280"))
        agregarIngrediente(IngredientForm(name = "Arroz", grams = "150", kcal = "190"))
        agregarIngrediente(IngredientForm(name = "Verduras", grams = "100", kcal = "50"))
        vm.onSave()

        assertEquals(listOf(520), repo.agregadas.map { it.kcal })
        assertEquals(ingredientes, repo.agregadas.single().ingredients)
    }

    @Test
    fun `C88 sin ingredientes no guarda`() = runTest {
        val eventos = eventos()
        abrirManual()

        vm.onSave()

        assertEquals(emptyList<Meal>(), repo.agregadas)
        val c = confirmacion()
        assertEquals(R.string.nutrition_error_no_ingredients, c.saveError)
        assertFalse(c.isSaving)
        assertEquals(emptyList<FoodCaptureEvent>(), eventos)
    }

    @Test
    fun `C89 un ingrediente de 0 kcal se guarda`() = runTest {
        val eventos = eventos()
        abrirManual()
        agregarIngrediente(IngredientForm(name = "Agua", grams = "250", kcal = "0"))

        vm.onSave()

        assertEquals(listOf(0), repo.agregadas.map { it.kcal })
        assertEquals(listOf<FoodCaptureEvent>(FoodCaptureEvent.Saved), eventos)
    }

    @Test
    fun `C90 Remove saca el ingrediente de esa posicion`() = runTest {
        abrirConFoto()

        vm.onRemoveIngredient(1)
        assertEquals(listOf(ingredientes[0], ingredientes[2]), confirmacion().ingredients)

        vm.onRemoveIngredient(-1)
        vm.onRemoveIngredient(2)
        assertEquals("fuera de la lista no cambia nada", listOf(ingredientes[0], ingredientes[2]), confirmacion().ingredients)
    }

    @Test
    fun `C91 mientras guarda no cambian los ingredientes`() = runTest {
        val bloqueo = CompletableDeferred<Unit>()
        repo.bloqueo = bloqueo
        abrirConFoto()
        vm.onSave()
        assertTrue("no está guardando", confirmacion().isSaving)

        vm.onAddIngredientClick()
        assertNull("no tiene que abrir el pop-up de ingrediente", confirmacion().ingredientEntry)
        vm.onRemoveIngredient(0)
        vm.onIngredientEntryChange(lechuga)
        vm.onConfirmIngredient()

        assertEquals(ingredientes, confirmacion().ingredients)
        bloqueo.complete(Unit)
    }

    @Test
    fun `C101 agregar o sacar un ingrediente borra el error de guardado`() = runTest {
        repo.errorAlEscribir = MealDataException(MealError.STORAGE)
        abrirConFoto()
        vm.onSave()
        assertNotNull("guardar tenía que fallar", confirmacion().saveError)

        vm.onRemoveIngredient(0)
        assertNull("sacar un ingrediente tiene que borrar el error", confirmacion().saveError)

        vm.onSave()
        assertNotNull("guardar tenía que fallar", confirmacion().saveError)

        agregarIngrediente(lechuga)
        assertNull("agregar un ingrediente tiene que borrar el error", confirmacion().saveError)
    }

    @Test
    fun `C102 mientras guarda abrir 07 no reemplaza la confirmacion`() = runTest {
        val bloqueo = CompletableDeferred<Unit>()
        repo.bloqueo = bloqueo
        abrirConFoto()
        vm.onSave()
        val guardando = confirmacion()
        assertTrue("no está guardando", guardando.isSaving)

        // Se salió de 07 por la barra mientras guardaba y se volvió a abrir.
        vm.onOpenConfirmation(fecha, "Otra comida", ingredientes.take(1))
        assertEquals(guardando, confirmacion())

        vm.onOpenManualEntry(fecha)
        vm.onManualEntryChange(formValido)
        assertNotNull("el pop-up de carga no se abrió", vm.uiState.value.manualEntry)
        assertFalse("no tiene que seguir a 07", vm.onContinueManualEntry())
        assertNotNull("el pop-up no tiene que cerrarse", vm.uiState.value.manualEntry)
        assertEquals(guardando, confirmacion())

        bloqueo.complete(Unit)
    }
}
