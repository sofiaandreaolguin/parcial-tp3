package com.example.parcial_tp3.ui.screens.nutrition

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.example.parcial_tp3.ui.components.rememberDemoToast

/**
 * @param grafo la entrada del grafo de Nutrition: de ahí salen el borrador (compartido con 04 a 06)
 *  y la fecha elegida en 01. Por eso el [mealId] no se lee del SavedStateHandle del ViewModel, que
 *  es el del grafo: lo pasa el NavGraph desde los argumentos de esta ruta.
 * @param mealId [NEW_MEAL_ID] crea una comida; otro id edita la guardada (NAV04).
 * @param onSaved se llama una sola vez, cuando Room confirmó el guardado.
 */
@Composable
fun FoodConfirmationRoute(
    grafo: NavBackStackEntry,
    mealId: Long,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: FoodCaptureViewModel = hiltViewModel(grafo),
    nutritionViewModel: NutritionViewModel = hiltViewModel(grafo),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnSaved by rememberUpdatedState(onSaved)
    val draftName = stringResource(state.draft?.name ?: MealDraft().name)

    // Con el mismo mealId y 07 ya abierta no hace nada (C31): rotar no vuelve a pedir la comida.
    LaunchedEffect(mealId) {
        viewModel.onOpenConfirmation(mealId, nutritionViewModel.selectedDate.value, draftName)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                FoodCaptureEvent.Saved -> currentOnSaved()
                // Lo escucha 05.
                FoodCaptureEvent.AnalysisDone -> Unit
            }
        }
    }

    // Sin onDiscard, volver a abrir 07 con el mismo mealId mostraría lo que se había escrito.
    val back = {
        viewModel.onDiscard()
        onBack()
    }
    BackHandler(onBack = back)

    // Al guardar o descartar, el ViewModel limpia la confirmación (y al guardar, el borrador) antes de
    // que termine la animación de salida: se sigue mostrando lo último para que 07 no cambie mientras se va.
    val shown = remember { Shown() }
    state.confirmation?.let { c ->
        shown.confirmation = c
        shown.ingredients = if (c.mealId == NEW_MEAL_ID) state.draft?.ingredients.orEmpty() else emptyList()
    }

    FoodConfirmationScreen(
        confirmation = shown.confirmation,
        ingredients = shown.ingredients,
        onBack = back,
        onFormChange = viewModel::onFormChange,
        onSave = viewModel::onSave,
        onDemo = rememberDemoToast(),
    )
}

private class Shown(
    var confirmation: ConfirmationState? = null,
    var ingredients: List<DraftIngredient> = emptyList(),
)
