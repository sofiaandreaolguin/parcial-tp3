package com.example.parcial_tp3.ui.screens.nutrition

import com.example.parcial_tp3.ui.screens.home.FakeMealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/** El análisis simulado de Nutrition / 05. Corre sin emulador: el tiempo es virtual. */
@OptIn(ExperimentalCoroutinesApi::class)
class FoodCaptureViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun antes() = Dispatchers.setMain(dispatcher)

    @After
    fun despues() = Dispatchers.resetMain()

    @Test
    fun `sin Done Speaking el analisis no arranca ni navega`() = runTest(dispatcher) {
        val vm = FoodCaptureViewModel(FakeMealRepository())
        val eventos = mutableListOf<FoodCaptureEvent>()
        backgroundScope.launch { vm.events.toList(eventos) }

        advanceTimeBy(10_000)
        runCurrent()

        assertEquals(AnalysisStep.Identifying, vm.uiState.value.analysisStep)
        assertEquals(emptyList<FoodCaptureEvent>(), eventos)
    }

    @Test
    fun `avanza un paso cada 700 ms`() = runTest(dispatcher) {
        val vm = FoodCaptureViewModel(FakeMealRepository())
        vm.onDoneSpeaking()

        runCurrent()
        assertEquals(AnalysisStep.Identifying, vm.uiState.value.analysisStep)

        advanceTimeBy(701)
        assertEquals(AnalysisStep.Estimating, vm.uiState.value.analysisStep)

        advanceTimeBy(700)
        assertEquals(AnalysisStep.Calculating, vm.uiState.value.analysisStep)
    }

    @Test
    fun `navega una sola vez al terminar los tres pasos`() = runTest(dispatcher) {
        val vm = FoodCaptureViewModel(FakeMealRepository())
        val eventos = mutableListOf<FoodCaptureEvent>()
        backgroundScope.launch { vm.events.toList(eventos) }
        vm.onDoneSpeaking()

        advanceTimeBy(2_099)
        runCurrent()
        assertEquals(emptyList<FoodCaptureEvent>(), eventos)

        advanceTimeBy(2)
        runCurrent()
        assertEquals(listOf<FoodCaptureEvent>(FoodCaptureEvent.AnalysisDone), eventos)

        advanceTimeBy(10_000)
        runCurrent()
        assertEquals(1, eventos.size)
    }

    @Test
    fun `cancelar a mitad corta el analisis y no navega`() = runTest(dispatcher) {
        val vm = FoodCaptureViewModel(FakeMealRepository())
        val eventos = mutableListOf<FoodCaptureEvent>()
        backgroundScope.launch { vm.events.toList(eventos) }
        vm.onDoneSpeaking()
        advanceTimeBy(1_000)

        vm.onCancelAnalysis()
        advanceTimeBy(10_000)
        runCurrent()

        assertEquals(AnalysisStep.Identifying, vm.uiState.value.analysisStep)
        assertEquals(emptyList<FoodCaptureEvent>(), eventos)
    }

    @Test
    fun `cancelar con el evento ya en el canal no lo deja para la proxima captura`() = runTest(dispatcher) {
        val vm = FoodCaptureViewModel(FakeMealRepository())
        vm.onDoneSpeaking()
        advanceTimeBy(2_101)
        runCurrent()

        vm.onCancelAnalysis()
        val eventos = mutableListOf<FoodCaptureEvent>()
        backgroundScope.launch { vm.events.toList(eventos) }
        runCurrent()

        assertEquals(emptyList<FoodCaptureEvent>(), eventos)
    }

    @Test
    fun `Done Speaking dos veces seguidas no duplica la navegacion`() = runTest(dispatcher) {
        val vm = FoodCaptureViewModel(FakeMealRepository())
        val eventos = mutableListOf<FoodCaptureEvent>()
        backgroundScope.launch { vm.events.toList(eventos) }
        vm.onDoneSpeaking()
        advanceTimeBy(1_000)
        vm.onDoneSpeaking()

        advanceTimeBy(10_000)
        runCurrent()

        assertEquals(1, eventos.size)
    }

    @Test
    fun `un segundo Done Speaking no reinicia el avance`() = runTest(dispatcher) {
        val vm = FoodCaptureViewModel(FakeMealRepository())
        vm.onDoneSpeaking()
        advanceTimeBy(1_000)
        vm.onDoneSpeaking()

        assertEquals(AnalysisStep.Estimating, vm.uiState.value.analysisStep)
    }

    @Test
    fun `C40 al terminar el analisis de voz el borrador es el de la foto`() = runTest(dispatcher) {
        val vm = FoodCaptureViewModel(FakeMealRepository())
        assertNull(vm.uiState.value.draft)

        vm.onDoneSpeaking()
        advanceTimeBy(2_101)
        runCurrent()

        assertEquals(PhotoMealDraft, vm.uiState.value.draft)
    }
}
