package com.example.parcial_tp3.ui.screens.nutrition

import com.example.parcial_tp3.ui.screens.home.FakeMealRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/** El borrador de Nutrition / 06: lo que 06 muestra es lo que llega a 07. */
class FoodCapturePhotoDraftTest {

    @Test
    fun `al entrar a la foto el borrador es el de la foto`() {
        val vm = FoodCaptureViewModel(FakeMealRepository())
        assertNotEquals(PhotoMealDraft, vm.uiState.value.draft)

        vm.onPhotoCaptureStarted()

        assertEquals(PhotoMealDraft, vm.uiState.value.draft)
    }

    @Test
    fun `volver a entrar a la foto no deja otra captura en el borrador`() {
        val vm = FoodCaptureViewModel(FakeMealRepository())
        vm.onPhotoCaptureStarted()
        vm.onPhotoCaptureStarted()

        assertEquals(PhotoMealDraft, vm.uiState.value.draft)
    }

    @Test
    fun `los ingredientes de la foto suman las kcal del borrador`() {
        assertEquals(PhotoMealDraft.kcal, PhotoMealDraft.ingredients.sumOf { it.kcal })
    }

    @Test
    fun `los macros de la foto son P 38 C 54 y F 14`() {
        assertEquals(38.0, PhotoMealDraft.proteinG, 0.0)
        assertEquals(54.0, PhotoMealDraft.carbsG, 0.0)
        assertEquals(14.0, PhotoMealDraft.fatG, 0.0)
    }
}
