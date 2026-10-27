package com.example.parcial_tp3.ui.screens.nutrition

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.example.parcial_tp3.domain.model.Ingredient
import com.example.parcial_tp3.ui.components.rememberDemoToast

/**
 * La confirmación ya llega abierta: la arma quien navega (CONTEXTO §2), con la fecha de 01. Si
 * el proceso murió, `confirmation` vuelve en null y solo se ve el encabezado, con ‹ para salir.
 *
 * @param grafo la entrada del grafo de Nutrition, de donde se pide el ViewModel que 04 a 07 comparten.
 * @param onSaved se llama una sola vez, cuando Room confirmó el guardado.
 */
@Composable
fun FoodConfirmationRoute(
    grafo: NavBackStackEntry,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: FoodCaptureViewModel = hiltViewModel(grafo),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnSaved by rememberUpdatedState(onSaved)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                FoodCaptureEvent.Saved -> currentOnSaved()
                // Lo escucha 05.
                FoodCaptureEvent.AnalysisDone -> Unit
            }
        }
    }

    // Sin onDiscard, volver a abrir 07 mostraría lo que se había escrito. Mientras guarda no se sale:
    // el guardado seguiría y su Saved quedaría esperando a la próxima 07.
    val back = {
        if (state.confirmation?.isSaving != true) {
            viewModel.onDiscard()
            onBack()
        }
    }
    BackHandler(onBack = back)

    // Al guardar o descartar, el ViewModel limpia la confirmación (y al guardar, el borrador) antes de
    // que termine la animación de salida: se sigue mostrando lo último para que 07 no cambie mientras se va.
    val shown = remember { Shown() }
    state.confirmation?.let { c ->
        shown.confirmation = c
        // Los que se van a guardar, no los del borrador: lo que se ve es lo que queda en Room.
        shown.ingredients = c.ingredients
    }

    FoodConfirmationScreen(
        confirmation = shown.confirmation,
        ingredients = shown.ingredients,
        onBack = back,
        onSave = viewModel::onSave,
        onDemo = rememberDemoToast(),
    )
}

private class Shown(
    var confirmation: ConfirmationState? = null,
    var ingredients: List<Ingredient> = emptyList(),
)
