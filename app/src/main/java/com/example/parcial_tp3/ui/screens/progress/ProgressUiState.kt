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
    val dashboard: ProgressDashboard,
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

/** Progress / 02. */
data class ProgressDashboard(
    val stats: List<DashboardStat>,
    @StringRes val insight: Int,
    val areas: List<DashboardArea>,
    @StringRes val goal: Int,
    @StringRes val goalBody: Int,
    val goalPercent: Int,
)

/** Un dato de "THIS WEEK OVERVIEW": "Workouts completed / 4 workouts / ↑ 1". */
data class DashboardStat(
    @StringRes val label: Int,
    @StringRes val value: Int,
    @StringRes val trend: Int,
)

/** Fila Workout, Nutrition o Activity: el tipo decide el ícono y a qué pantalla lleva. */
data class DashboardArea(
    val kind: CategoryKind,
    @StringRes val title: Int,
    @StringRes val detail: Int,
)
