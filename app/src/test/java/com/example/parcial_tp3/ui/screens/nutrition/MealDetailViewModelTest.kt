package com.example.parcial_tp3.ui.screens.nutrition

import androidx.lifecycle.SavedStateHandle
import com.example.parcial_tp3.data.repository.MealDataException
import com.example.parcial_tp3.data.repository.MealError
import com.example.parcial_tp3.domain.model.Ingredient
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.MealType
import com.example.parcial_tp3.ui.navigation.Rutas
import com.example.parcial_tp3.ui.screens.home.FakeMealRepository
import com.example.parcial_tp3.util.MainDispatcherRule
import com.example.parcial_tp3.util.esTextoDeStrings
import java.time.LocalDate
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** MealDetailViewModel (Nutrition / 08) con un MealRepository falso (RF04, RF07). */
@OptIn(ExperimentalCoroutinesApi::class)
class MealDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val guardada = Meal(
        id = 7,
        name = "Avena con fruta",
        date = LocalDate.of(2026, 10, 24),
        type = MealType.BREAKFAST,
        kcal = 320,
        proteinG = 12.5,
        carbsG = 54.0,
        fatG = 6.25,
    )

    private val repo = FakeMealRepository(listOf(guardada))

    private fun viewModel(mealId: Long = 7) =
        MealDetailViewModel(repo, SavedStateHandle(mapOf(Rutas.ARG_MEAL_ID to mealId)))

    private fun TestScope.observar(vm: MealDetailViewModel): Job =
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

    private fun TestScope.eventos(vm: MealDetailViewModel): List<MealDetailEvent> {
        val lista = mutableListOf<MealDetailEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.events.toList(lista) }
        return lista
    }

    private fun MealDetailViewModel.contenido(): MealDetailUiState.Content {
        val estado = uiState.value
        assertTrue("esperaba Content y es $estado", estado is MealDetailUiState.Content)
        return estado as MealDetailUiState.Content
    }

    @Test
    fun `C41 carga la comida de la ruta`() = runTest {
        val vm = viewModel()
        assertEquals(MealDetailUiState.Loading, vm.uiState.value)

        observar(vm)

        assertEquals(guardada, vm.contenido().meal)
        assertEquals(listOf(7L), repo.idsPedidos)
    }

    @Test
    fun `C42 no existe es NotFound y un error de datos es Error`() = runTest {
        val inexistente = viewModel(mealId = 99)
        observar(inexistente)
        assertEquals(MealDetailUiState.NotFound, inexistente.uiState.value)

        repo.errorAlLeer = MealDataException(MealError.INVALID_DATA)
        val conError = viewModel()
        observar(conError)
        val estado = conError.uiState.value
        assertTrue("esperaba Error y es $estado", estado is MealDetailUiState.Error)
        assertTrue("no es un texto de strings.xml", esTextoDeStrings((estado as MealDetailUiState.Error).message))
    }

    @Test
    fun `C43 Delete pide confirmacion y cancelar no borra`() = runTest {
        val vm = viewModel()
        observar(vm)

        vm.onDeleteClick()
        assertTrue(vm.contenido().confirmingDelete)

        vm.onDismissDelete()
        assertFalse(vm.contenido().confirmingDelete)
        assertEquals(emptyList<Long>(), repo.eliminadas)
    }

    @Test
    fun `C45 confirmar borra una vez y emite Deleted`() = runTest {
        val vm = viewModel()
        observar(vm)
        val eventos = eventos(vm)

        vm.onDeleteClick()
        vm.onConfirmDelete()

        assertEquals(listOf(guardada), repo.comidasEliminadas)
        assertEquals(listOf<MealDetailEvent>(MealDetailEvent.Deleted), eventos)
    }

    @Test
    fun `C74 sin Content confirmar no borra`() = runTest {
        // Sin colectores uiState se queda en Loading.
        val enCarga = viewModel()
        val eventosEnCarga = eventos(enCarga)
        enCarga.onConfirmDelete()

        // Con un id que no existe, NotFound.
        val noEncontrada = viewModel(mealId = 99)
        observar(noEncontrada)
        assertEquals(MealDetailUiState.NotFound, noEncontrada.uiState.value)
        val eventosNoEncontrada = eventos(noEncontrada)
        noEncontrada.onConfirmDelete()

        assertTrue("no debería borrar: ${repo.eliminadas}", repo.eliminadas.isEmpty())
        assertTrue(eventosEnCarga.isEmpty())
        assertTrue(eventosNoEncontrada.isEmpty())
    }

    @Test
    fun `C46 mientras esta ocupado no repite borrar ni duplicar`() = runTest {
        val vm = viewModel()
        observar(vm)
        val eventos = eventos(vm)
        val bloqueo = CompletableDeferred<Unit>()
        repo.bloqueo = bloqueo

        vm.onDeleteClick()
        vm.onConfirmDelete()
        assertTrue("no está ocupado", vm.contenido().isBusy)
        vm.onConfirmDelete()
        vm.onDuplicate()
        bloqueo.complete(Unit)

        assertEquals(listOf(7L), repo.eliminadas)
        assertEquals(emptyList<Meal>(), repo.agregadas)
        assertEquals(listOf<MealDetailEvent>(MealDetailEvent.Deleted), eventos)
    }

    @Test
    fun `C47 el null despues de borrar no muestra NotFound`() = runTest {
        val vm = viewModel()
        observar(vm)
        val eventos = eventos(vm)
        val bloqueo = CompletableDeferred<Unit>()
        repo.bloqueo = bloqueo

        vm.onDeleteClick()
        vm.onConfirmDelete()
        // Room avisa que la fila ya no está antes de que deleteMeal vuelva.
        repo.meals.value = emptyList()
        assertFalse("pasó a NotFound mientras borraba", vm.uiState.value is MealDetailUiState.NotFound)

        bloqueo.complete(Unit)

        assertEquals(listOf(7L), repo.eliminadas)
        assertEquals(listOf<MealDetailEvent>(MealDetailEvent.Deleted), eventos)
        assertFalse("pasó a NotFound después de borrar", vm.uiState.value is MealDetailUiState.NotFound)
    }

    @Test
    fun `C48 si borrar o duplicar falla muestra el error y no emite`() = runTest {
        repo.errorAlEscribir = MealDataException(MealError.STORAGE)
        val vm = viewModel()
        observar(vm)
        val eventos = eventos(vm)

        vm.onDeleteClick()
        vm.onConfirmDelete()

        val trasBorrar = vm.contenido()
        assertEquals(listOf(7L), repo.eliminadas)
        assertTrue("actionError no es un texto de strings.xml", esTextoDeStrings(trasBorrar.actionError))
        assertFalse(trasBorrar.isBusy)
        assertEquals(guardada, trasBorrar.meal)

        vm.onDuplicate()

        val trasDuplicar = vm.contenido()
        assertEquals(1, repo.agregadas.size)
        assertTrue("actionError no es un texto de strings.xml", esTextoDeStrings(trasDuplicar.actionError))
        assertFalse(trasDuplicar.isBusy)
        assertEquals(guardada, trasDuplicar.meal)
        assertEquals(emptyList<MealDetailEvent>(), eventos)
    }

    @Test
    fun `C49 Duplicate guarda una copia con id 0`() = runTest {
        val vm = viewModel()
        observar(vm)
        val eventos = eventos(vm)

        vm.onDuplicate()

        assertEquals(listOf(guardada.copy(id = 0)), repo.agregadas)
        assertEquals(listOf<MealDetailEvent>(MealDetailEvent.Duplicated), eventos)
    }

    @Test
    fun `C50 recolectar en menos de 5 s no vuelve a pedir la comida`() = runTest {
        val vm = viewModel()
        observar(vm).cancel()

        advanceTimeBy(4_000)
        observar(vm)

        assertEquals(guardada, vm.contenido().meal)
        assertEquals(1, repo.suscripcionesPorId)
    }
    private val ingredientes = listOf(
        Ingredient("Avena", grams = 60, kcal = 230),
        Ingredient("Banana", grams = 100, kcal = 90),
    )

    @Test
    fun `C65 Duplicate copia los ingredientes`() = runTest {
        val repoConIngredientes = FakeMealRepository(listOf(guardada.copy(ingredients = ingredientes)))
        val vm = MealDetailViewModel(repoConIngredientes, SavedStateHandle(mapOf(Rutas.ARG_MEAL_ID to 7L)))
        observar(vm)

        vm.onDuplicate()

        assertEquals("addMeal no se llamó una sola vez", 1, repoConIngredientes.agregadas.size)
        assertEquals(ingredientes, repoConIngredientes.agregadas.single().ingredients)
    }

    @Test
    fun `C71 Duplicate con un ingrediente invalido no guarda y muestra el error`() = runTest {
        val invalida = guardada.copy(ingredients = listOf(ingredientes[0], Ingredient("Banana", grams = 0, kcal = 90)))
        val repoInvalido = FakeMealRepository(listOf(invalida))
        val vm = MealDetailViewModel(repoInvalido, SavedStateHandle(mapOf(Rutas.ARG_MEAL_ID to 7L)))
        observar(vm)
        val ev = eventos(vm)

        vm.onDuplicate()

        assertTrue("no debería llamar a addMeal: ${repoInvalido.agregadas}", repoInvalido.agregadas.isEmpty())
        val c = vm.contenido()
        assertTrue("actionError no es un texto de strings.xml: ${c.actionError}", esTextoDeStrings(c.actionError))
        assertFalse(c.isBusy)
        assertTrue("no debería emitir Duplicated: $ev", ev.isEmpty())
    }
}
