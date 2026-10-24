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
    val workout: WorkoutProgress,
    val nutrition: NutritionProgress,
    val health: HealthTrends,
    val review: WeeklyReview,
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

/** Progress / 03. */
data class WorkoutProgress(
    val metrics: List<ProgressMetric>,
    /** Kilos levantados por día, de lunes a viernes; 0 es un día sin sesión. */
    val volumeKg: List<Float>,
    @StringRes val volumeChange: Int,
    val personalBest: PersonalBest,
    @StringRes val insight: Int,
    val exercises: List<ExerciseTrend>,
)

/**
 * Un dato con etiqueta y valor: la fila "SESSIONS 4 | VOLUME 8,420 kg" de 03 y 04, y cada
 * tarjeta de "FOOD HABITS & PATTERNS" de 04, la fila "AVG STEPS | AVG DISTANCE" de 05 y la de
 * "WORKOUTS | AVG STEPS | PROTEIN" de 06.
 */
data class ProgressMetric(
    @StringRes val label: Int,
    @StringRes val value: Int,
)

data class PersonalBest(
    @StringRes val exercise: Int,
    @StringRes val detail: Int,
    @StringRes val improvement: Int,
)

/** Fila de "EXERCISE PROGRESS": [improved] decide si la etiqueta va en menta o en gris. */
data class ExerciseTrend(
    @StringRes val name: Int,
    @StringRes val detail: Int,
    @StringRes val change: Int,
    val improved: Boolean,
)

/** Progress / 04. */
data class NutritionProgress(
    val metrics: List<ProgressMetric>,
    @StringRes val target: Int,
    /** De lunes a domingo: true si ese día llegó al objetivo de proteína. */
    val targetHit: List<Boolean>,
    @StringRes val insight: Int,
    val habits: List<ProgressMetric>,
)

/** Progress / 05. */
data class HealthTrends(
    val metrics: List<ProgressMetric>,
    /** Pasos por día, de lunes a domingo; los que llegan a [stepGoal] van en verde oscuro. */
    val steps: List<Int>,
    val stepGoal: Int,
    @StringRes val insight: Int,
    val signals: List<HabitSignal>,
)

/** El tipo decide el ícono de la fila de "HABIT SIGNALS". */
enum class SignalKind { SLEEP, MOVEMENT, WORKOUT, PROTEIN }

/** Fila de "HABIT SIGNALS": [improving] pone la tendencia en verde con la flecha, si no en gris. */
data class HabitSignal(
    val kind: SignalKind,
    @StringRes val title: Int,
    @StringRes val trend: Int,
    val improving: Boolean,
)

/** Progress / 06. */
data class WeeklyReview(
    @StringRes val headline: Int,
    val summary: List<ProgressMetric>,
    val wins: List<ReviewPoint>,
    val improvement: ReviewPoint,
    /** Las viñetas de "NEXT WEEK'S RECOMMENDATIONS", como recursos de strings.xml. */
    val recommendations: List<Int>,
)

/** Un punto de la revisión: un logro de "WHAT WENT WELL" o lo de "WHAT TO IMPROVE". */
data class ReviewPoint(
    @StringRes val title: Int,
    @StringRes val body: Int,
)
