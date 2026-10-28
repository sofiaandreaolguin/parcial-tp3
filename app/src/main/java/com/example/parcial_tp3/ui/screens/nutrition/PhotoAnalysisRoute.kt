package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.example.parcial_tp3.ui.components.rememberDemoToast

/**
 * @param grafo la entrada del grafo de Nutrition, de donde se pide el ViewModel que 04 a 07 comparten.
 *  El borrador de la foto lo carga quien navega a 06 (01 o 03), no esta pantalla: rotar no lo pisa.
 * @param onReview navega a 07; la confirmación se abre antes, acá.
 */
@Composable
fun PhotoAnalysisRoute(
    grafo: NavBackStackEntry,
    onBack: () -> Unit,
    onReview: () -> Unit,
    viewModel: FoodCaptureViewModel = hiltViewModel(grafo),
    nutricion: NutritionViewModel = hiltViewModel(grafo),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val openConfirmation = rememberOpenConfirmation(viewModel, nutricion)
    PhotoAnalysisScreen(
        draft = state.draft ?: PhotoMealDraft,
        onBack = onBack,
        onReview = {
            openConfirmation()
            onReview()
        },
        onRetake = rememberDemoToast(),
    )
}
