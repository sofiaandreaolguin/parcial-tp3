package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.example.parcial_tp3.ui.components.rememberDemoToast

/**
 * @param grafo la entrada del grafo de Nutrition, de donde se pide el ViewModel que 04 a 07 comparten.
 *  El borrador de la foto lo carga quien navega a 06, no esta pantalla (ver [FoodCaptureViewModel]).
 */
@Composable
fun PhotoAnalysisRoute(
    grafo: NavBackStackEntry,
    onBack: () -> Unit,
    onReview: () -> Unit,
    viewModel: FoodCaptureViewModel = hiltViewModel(grafo),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PhotoAnalysisScreen(
        draft = state.draft ?: PhotoMealDraft,
        onBack = onBack,
        onReview = onReview,
        onRetake = rememberDemoToast(),
    )
}
