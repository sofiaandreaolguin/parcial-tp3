package com.example.parcial_tp3.ui.screens.home

import com.example.parcial_tp3.data.repository.MealDataException
import com.example.parcial_tp3.data.repository.MealError
import com.example.parcial_tp3.data.repository.ProfileRepository
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.MealType
import com.example.parcial_tp3.util.MainDispatcherRule
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * HomeViewModel (Home / 01) con un MealRepository falso (RF04, RF07): los totales del dia salen
 * de las comidas guardadas y se actualizan solos; el nombre sale del perfil. El uiState usa
 * WhileSubscribed, asi que cada test lo observa mientras dura.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val hoy = LocalDate.now()

    private fun comida(kcal: Int, proteinG: Double, carbsG: Double, fecha: LocalDate = hoy) = Meal(
        name = "Comida",
        date = fecha,
        type = MealType.LUNCH,
        kcal = kcal,
        proteinG = proteinG,
        carbsG = carbsG,
        fatG = 0.0,
    )

    /**
     * Observa el estado durante el test, como la pantalla. Sin dispatcher propio la corrutina no
     * arranca hasta avanzar el reloj y WhileSubscribed nunca se suscribe.
     */
    private fun TestScope.observar(vm: HomeViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
    }

    @Test
    fun `suma calorias proteinas y carbohidratos de las comidas de hoy`() = runTest {
        val comidas = FakeMealRepository(listOf(comida(500, 30.4, 60.0), comida(920, 10.3, 92.5)))
        val vm = HomeViewModel(ProfileRepository(), comidas)
        observar(vm)

        val estado = vm.uiState.value
        assertEquals(1_420, estado.kcal)
        assertEquals(41, estado.proteinG)
        // 40.7 g y 152.5 g: roundToInt redondea al entero mas cercano y el .5 hacia arriba
        assertEquals(153, estado.carbsG)
    }

    @Test
    fun `lee las comidas de hoy y no cuenta las de otro dia`() = runTest {
        val comidas = FakeMealRepository(listOf(comida(500, 0.0, 0.0), comida(800, 0.0, 0.0, fecha = hoy.minusDays(1))))
        val vm = HomeViewModel(ProfileRepository(), comidas)
        observar(vm)

        assertEquals(hoy, comidas.fechaPedida)
        assertEquals(500, vm.uiState.value.kcal)
    }

    @Test
    fun `guardar una comida actualiza el total sin volver a pedir nada`() = runTest {
        val comidas = FakeMealRepository()
        val vm = HomeViewModel(ProfileRepository(), comidas)
        observar(vm)
        assertEquals(0, vm.uiState.value.kcal)

        comidas.addMeal(comida(350, 20.0, 40.0))

        assertEquals(350, vm.uiState.value.kcal)
        assertEquals(20, vm.uiState.value.proteinG)
    }

    @Test
    fun `el nombre sale del perfil y cambia si se edita en Profile`() = runTest {
        val perfil = ProfileRepository()
        val vm = HomeViewModel(perfil, FakeMealRepository())
        observar(vm)
        assertEquals("Alex" to "Morgan", vm.uiState.value.firstName to vm.uiState.value.lastName)

        perfil.update { it.copy(firstName = "Martina") }

        assertEquals("Martina", vm.uiState.value.firstName)
    }

    @Test
    fun `objetivos y recomendado son los de la demostracion`() = runTest {
        val vm = HomeViewModel(ProfileRepository(), FakeMealRepository())
        observar(vm)

        val estado = vm.uiState.value
        assertEquals(HomeDemoData.KCAL_GOAL, estado.kcalGoal)
        assertEquals("w01", estado.recommended.id)
    }

    @Test
    fun `C44 un error de datos en Home muestra 0 sin romper`() = runTest {
        val comidas = FakeMealRepository(listOf(comida(500, 30.0, 60.0)))
        comidas.errorAlLeer = MealDataException(MealError.INVALID_DATA)
        val vm = HomeViewModel(ProfileRepository(), comidas)
        observar(vm)

        val estado = vm.uiState.value
        assertEquals(listOf(0, 0, 0), listOf(estado.kcal, estado.proteinG, estado.carbsG))
        assertEquals("Alex", estado.firstName)
    }
}
