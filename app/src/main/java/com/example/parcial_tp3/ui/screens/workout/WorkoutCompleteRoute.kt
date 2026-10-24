package com.example.parcial_tp3.ui.screens.workout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * @param sesion la entrada `active_workout` de la pila: 07 muestra la sesion de 05, asi que pide
 *   el ViewModel de esa entrada (spec sesion-de-entrenamiento-demo). Uno propio arrancaria el
 *   plan desde cero.
 */
@Composable
fun WorkoutCompleteRoute(
    sesion: ViewModelStoreOwner,
    onDone: () -> Unit,
    viewModel: ActiveWorkoutViewModel = hiltViewModel(sesion),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    WorkoutCompleteScreen(
        state = state,
        onRate = viewModel::onRate,
        onDone = onDone,
    )
}
