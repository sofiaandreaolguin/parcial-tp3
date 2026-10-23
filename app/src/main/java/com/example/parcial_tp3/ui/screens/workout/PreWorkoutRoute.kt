package com.example.parcial_tp3.ui.screens.workout

import android.app.Activity
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PreWorkoutRoute(
    onClose: () -> Unit,
    onAskVora: () -> Unit,
    onStartWorkout: (workoutId: String) -> Unit,
    viewModel: WorkoutDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // MainActivity deja los iconos del sistema oscuros, que sobre este verde no se leen. Se ponen
    // blancos al entrar (si no, se ven oscuros durante la animacion) y otra vez al reanudar: al
    // volver de Voice, Voice los devuelve a oscuros cuando sale, despues de que esta ya entro.
    val vista = LocalView.current
    DisposableEffect(Unit) {
        iconosClaros(vista, true)
        onDispose {}
    }
    LifecycleResumeEffect(Unit) {
        iconosClaros(vista, true)
        onPauseOrDispose { iconosClaros(vista, false) }
    }

    PreWorkoutScreen(
        state = state,
        onClose = onClose,
        onAskVora = onAskVora,
        onStartWorkout = onStartWorkout,
    )
}

private fun iconosClaros(vista: View, claros: Boolean) {
    val control = WindowCompat.getInsetsController((vista.context as Activity).window, vista)
    control.isAppearanceLightStatusBars = !claros
    control.isAppearanceLightNavigationBars = !claros
}
