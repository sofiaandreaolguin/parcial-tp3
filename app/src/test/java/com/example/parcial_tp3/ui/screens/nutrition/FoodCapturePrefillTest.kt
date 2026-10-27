package com.example.parcial_tp3.ui.screens.nutrition

import com.example.parcial_tp3.domain.MealForm
import com.example.parcial_tp3.domain.model.Ingredient
import com.example.parcial_tp3.ui.screens.home.FakeMealRepository
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * La puerta que usa la busqueda para abrir el pop-up de carga (EXT02, C31; desde el PR 2 de
 * ingredientes). Que el pop-up se abra con el alimento es C82, en FoodCaptureManualEntryTest.
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
    private val seleccion = FoodSelection(form, Ingredient("Pollo cocido", grams = 150, kcal = 248))

    @Test
    fun `C31 onPrefill limpia el borrador del analisis`() {
        val vm = FoodCaptureViewModel(FakeMealRepository())
        // Venimos de probar la foto: el borrador tiene Chicken, Rice y Vegetables.
        vm.onPhotoCaptureStarted()
        assertEquals(PhotoMealDraft, vm.uiState.value.draft)

        vm.onPrefill(seleccion)

        assertNull("si queda el borrador, 07 muestra los ingredientes de la foto", vm.uiState.value.draft)
    }

}
