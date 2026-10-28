package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalResources
import com.example.parcial_tp3.domain.model.Ingredient

/**
 * Abre 07 con el borrador de la voz o la foto. Lo llaman 05 y 06 antes de navegar, no la
 * composición de 07, así que rotar no lo repite (CONTEXTO §2). Los nombres se resuelven acá porque
 * el ViewModel no tiene Context; la fecha es la elegida en 01.
 */
@Composable
internal fun rememberOpenConfirmation(captura: FoodCaptureViewModel, nutricion: NutritionViewModel): () -> Unit {
    val resources = LocalResources.current
    return remember(captura, nutricion, resources) {
        {
            val borrador = captura.uiState.value.draft
            val nombre = resources.getString(borrador?.name ?: MealDraft().name)
            val ingredientes = borrador?.ingredients.orEmpty().map {
                Ingredient(resources.getString(it.name), it.grams, it.kcal)
            }
            captura.onOpenConfirmation(nutricion.selectedDate.value, nombre, ingredientes)
        }
    }
}
