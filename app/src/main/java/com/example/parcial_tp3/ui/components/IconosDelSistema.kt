package com.example.parcial_tp3.ui.components

import android.app.Activity
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.LifecycleResumeEffect

/**
 * Para las pantallas verdes que van hasta el borde (Workout / 04 y 05, Nutrition / 04 y 05,
 * Progress / 06). MainActivity deja los iconos del sistema oscuros, que sobre el verde no se
 * leen. Se ponen blancos al entrar (si no, se ven oscuros durante la animacion) y otra vez
 * al reanudar: al volver de Voice, Voice los devuelve a oscuros cuando sale, despues de que
 * esta ya entro. Va en la Route: en la preview no hay Activity.
 */
@Composable
internal fun IconosDelSistemaClaros() {
    val vista = LocalView.current
    DisposableEffect(Unit) {
        iconosClaros(vista, true)
        onDispose {}
    }
    LifecycleResumeEffect(Unit) {
        iconosClaros(vista, true)
        onPauseOrDispose { iconosClaros(vista, false) }
    }
}

private fun iconosClaros(vista: View, claros: Boolean) {
    val control = WindowCompat.getInsetsController((vista.context as Activity).window, vista)
    control.isAppearanceLightStatusBars = !claros
    control.isAppearanceLightNavigationBars = !claros
}
