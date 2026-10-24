package com.example.parcial_tp3.ui.screens.progress

import androidx.annotation.StringRes
import com.example.parcial_tp3.domain.model.Workout

/**
 * Lo que muestran las pantallas de Progress. Todo es de demostración (CONSIGNA: Progress es
 * "Simulado"): los textos son recursos de strings.xml y el entrenamiento usa el modelo
 * [Workout] para poder conectarlo a la caché si sobra tiempo.
 */
data class ProgressUiState(
    val overview: TodaysOverview,
)

/** Progress / 01. */
data class TodaysOverview(
    @StringRes val date: Int,
    val dailyPercent: Int,
    val categories: List<OverviewCategory>,
    @StringRes val advice: Int,
    val workout: Workout,
    @StringRes val goal: Int,
    val weeklyDone: Int,
    val weeklyTarget: Int,
)

/** El tipo decide el color del punto y a qué pantalla lleva la fila. */
enum class CategoryKind { WORKOUT, NUTRITION, ACTIVITY, HYDRATION }

data class OverviewCategory(
    val kind: CategoryKind,
    @StringRes val label: Int,
    @StringRes val detail: Int,
    val percent: Int,
)
