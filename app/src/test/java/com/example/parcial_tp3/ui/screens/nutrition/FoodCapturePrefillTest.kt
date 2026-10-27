package com.example.parcial_tp3.ui.screens.nutrition

import com.example.parcial_tp3.domain.MealForm
import com.example.parcial_tp3.ui.screens.home.FakeMealRepository
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * La puerta que usa la busqueda para dejar cargado Nutrition / 07 (EXT02, C31).
 * Spec: specs/funcionales/busqueda-de-alimentos.md.
 */
class FoodCapturePrefillTest {

    private val form = MealForm(
        name = "Pollo cocido",
        type = null,
        date = LocalDate.of(2026, 10, 26),
        kcal = "248",
        protein = "46.5",
        carbs = "0",
        fat = "5.4",
    )

    @Test
    fun `C31 onPrefill limpia el borrador del analisis`() {
        val vm = FoodCaptureViewModel(FakeMealRepository())
        // Venimos de probar la foto: el borrador tiene Chicken, Rice y Vegetables.
        vm.onPhotoCaptureStarted()
        assertEquals(PhotoMealDraft, vm.uiState.value.draft)

        vm.onPrefill(form)

        assertNull("si queda el borrador, 07 muestra los ingredientes de la foto", vm.uiState.value.draft)
    }

    @Test
    fun `C31 onPrefill deja la confirmacion con el formulario de la busqueda`() {
        val vm = FoodCaptureViewModel(FakeMealRepository())

        vm.onPrefill(form)

        val confirmacion = vm.uiState.value.confirmation
        assertEquals(NEW_MEAL_ID, confirmacion?.mealId)
        assertEquals(form, confirmacion?.form)
    }
}
