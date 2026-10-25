package com.example.parcial_tp3.ui.screens.nutrition

import androidx.lifecycle.SavedStateHandle
import com.example.parcial_tp3.data.repository.MealDataException
import com.example.parcial_tp3.data.repository.MealError
import com.example.parcial_tp3.data.repository.ProfileRepository
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.MealType
import com.example.parcial_tp3.ui.screens.home.FakeMealRepository
import com.example.parcial_tp3.ui.screens.home.HomeViewModel
import com.example.parcial_tp3.util.MainDispatcherRule
import com.example.parcial_tp3.util.esTextoDeStrings
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** NutritionViewModel (Nutrition / 01 y 02) con un MealRepository falso (RF04, RF07). */
@OptIn(ExperimentalCoroutinesApi::class)
class NutritionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val hoy = LocalDate.of(2026, 10, 24)
    private val reloj = Clock.fixed(hoy.atTime(12, 0).toInstant(ZoneOffset.UTC), ZoneOffset.UTC)

    private fun comida(
        id: Long,
        type: MealType,
        kcal: Int = 100,
        proteinG: Double = 0.0,
        carbsG: Double = 0.0,
        fatG: Double = 0.0,
        fecha: LocalDate = hoy,
        name: String = "Comida $id",
    ) = Meal(id = id, name = name, date = fecha, type = type, kcal = kcal, proteinG = proteinG, carbsG = carbsG, fatG = fatG)

    private fun viewModel(
        repo: FakeMealRepository,
        savedState: SavedStateHandle = SavedStateHandle(),
        clock: Clock = reloj,
    ) = NutritionViewModel(repo, savedState, clock)

    /** Observa el estado como la pantalla: WhileSubscribed solo se suscribe con un colector. */
    private fun TestScope.observar(vm: NutritionViewModel): Job =
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

    private fun NutritionViewModel.contenido(): NutritionUiState.Content {
        val estado = uiState.value
        assertTrue("esperaba Content y es $estado", estado is NutritionUiState.Content)
        return estado as NutritionUiState.Content
    }

    private fun NutritionUiState.Content.gramos(kind: MacroKind) = macros.first { it.kind == kind }.grams

    @Test
    fun `C18 arranca pidiendo la fecha de hoy del Clock`() = runTest {
        val repo = FakeMealRepository()
        val vm = viewModel(repo)
        observar(vm)

        assertEquals(hoy, repo.fechaPedida)
        assertEquals(hoy, vm.contenido().date)
    }

    @Test
    fun `C19 elegir una fecha la pide y la guarda en SavedStateHandle`() = runTest {
        val ayer = hoy.minusDays(1)
        val repo = FakeMealRepository(listOf(comida(1, MealType.LUNCH), comida(2, MealType.DINNER, fecha = ayer)))
        val savedState = SavedStateHandle()
        val vm = viewModel(repo, savedState)
        observar(vm)

        vm.onDateSelected(ayer)

        assertEquals(ayer, repo.fechaPedida)
        assertEquals(listOf(2L), vm.contenido().meals.map { it.id })
        assertEquals("2026-10-23", savedState.get<String>(NutritionViewModel.KEY_SELECTED_DATE))
    }

    @Test
    fun `C20 al recrearse usa la fecha guardada`() = runTest {
        val repo = FakeMealRepository()
        val vm = viewModel(repo, SavedStateHandle(mapOf(NutritionViewModel.KEY_SELECTED_DATE to "2026-10-20")))
        observar(vm)

        assertEquals(listOf(LocalDate.of(2026, 10, 20)), repo.fechasPedidas)
    }

    @Test
    fun `C21 lista las comidas del dia en orden`() = runTest {
        val repo = FakeMealRepository(
            listOf(
                comida(5, MealType.DINNER, kcal = 700, name = "Pasta"),
                comida(2, MealType.BREAKFAST, kcal = 320, name = "Avena"),
            ),
        )
        val vm = viewModel(repo)
        observar(vm)

        assertEquals(
            listOf(MealRow(5, MealType.DINNER, "Pasta", 700), MealRow(2, MealType.BREAKFAST, "Avena", 320)),
            vm.contenido().meals,
        )
    }

    @Test
    fun `C22 el total redondea la suma y cambia con cada emision`() = runTest {
        // Mismo día para los dos: Home pide LocalDate.now() y no tiene Clock.
        val zona = ZoneId.systemDefault()
        val hoyReal = LocalDate.now(zona)
        val relojReal = Clock.fixed(hoyReal.atTime(12, 0).atZone(zona).toInstant(), zona)
        // 10.4 + 10.4 = 20.8 → 21; redondear cada una daría 20
        val repo = FakeMealRepository(
            listOf(
                comida(1, MealType.BREAKFAST, kcal = 300, proteinG = 10.4, carbsG = 30.3, fatG = 5.4, fecha = hoyReal),
                comida(2, MealType.LUNCH, kcal = 520, proteinG = 10.4, carbsG = 30.3, fatG = 5.4, fecha = hoyReal),
            ),
        )
        val vm = viewModel(repo, clock = relojReal)
        val home = HomeViewModel(ProfileRepository(), repo)
        observar(vm)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { home.uiState.collect {} }

        val total = vm.contenido()
        assertEquals(820, total.kcal)
        assertEquals(21, total.gramos(MacroKind.PROTEIN))
        assertEquals(61, total.gramos(MacroKind.CARBS))
        assertEquals(11, total.gramos(MacroKind.FAT))
        assertEquals(
            Triple(home.uiState.value.kcal, home.uiState.value.proteinG, home.uiState.value.carbsG),
            Triple(total.kcal, total.gramos(MacroKind.PROTEIN), total.gramos(MacroKind.CARBS)),
        )

        repo.addMeal(comida(0, MealType.DINNER, kcal = 180, proteinG = 0.8, fecha = hoyReal))

        assertEquals(1_000, vm.contenido().kcal)
        assertEquals(22, vm.contenido().gramos(MacroKind.PROTEIN))
    }

    @Test
    fun `C23 la pendiente es el primer tipo sin comidas`() = runTest {
        val repo = FakeMealRepository(listOf(comida(1, MealType.LUNCH), comida(2, MealType.BREAKFAST)))
        val vm = viewModel(repo)
        observar(vm)
        assertEquals(MealType.SNACK, vm.contenido().pending)

        repo.addMeal(comida(0, MealType.DINNER))
        assertEquals(MealType.SNACK, vm.contenido().pending)

        repo.addMeal(comida(0, MealType.SNACK))
        assertEquals(null, vm.contenido().pending)
    }

    @Test
    fun `C24 un dia sin comidas da total 0 y pendiente BREAKFAST`() = runTest {
        val vm = viewModel(FakeMealRepository(listOf(comida(1, MealType.LUNCH, fecha = hoy.minusDays(1)))))
        observar(vm)

        val estado = vm.contenido()
        assertEquals(emptyList<MealRow>(), estado.meals)
        assertEquals(0, estado.kcal)
        assertEquals(listOf(0, 0, 0), estado.macros.map { it.grams })
        assertEquals(MealType.BREAKFAST, estado.pending)
    }

    @Test
    fun `C25 un error de datos pasa a Error y onRetry vuelve a pedir`() = runTest {
        val repo = FakeMealRepository(listOf(comida(1, MealType.LUNCH)))
        repo.errorAlLeer = MealDataException(MealError.INVALID_DATA)
        val vm = viewModel(repo)
        observar(vm)

        val estado = vm.uiState.value
        assertTrue("esperaba Error y es $estado", estado is NutritionUiState.Error)
        assertTrue("no es un texto de strings.xml", esTextoDeStrings((estado as NutritionUiState.Error).message))

        repo.errorAlLeer = null
        vm.onRetry()

        assertEquals(listOf(1L), vm.contenido().meals.map { it.id })
        assertEquals(2, repo.suscripcionesPorFecha)
    }

    @Test
    fun `C26 recolectar en menos de 5 s no vuelve a suscribirse`() = runTest {
        val repo = FakeMealRepository(listOf(comida(1, MealType.LUNCH)))
        val vm = viewModel(repo)
        observar(vm).cancel()

        advanceTimeBy(4_000)
        observar(vm)

        assertEquals(listOf(1L), vm.contenido().meals.map { it.id })
        assertEquals(1, repo.suscripcionesPorFecha)
    }
}
