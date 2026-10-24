package com.example.parcial_tp3.ui.screens.progress

import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.Workout
import com.example.parcial_tp3.domain.model.WorkoutLevel

/** Datos de demostración de Progress. Los números son los mismos que muestra Home / 01. */
fun progressDemoState() = ProgressUiState(
    overview = TodaysOverview(
        date = R.string.progress_overview_date,
        dailyPercent = 72,
        categories = listOf(
            OverviewCategory(CategoryKind.WORKOUT, R.string.progress_overview_workout, R.string.progress_overview_workout_detail, 0),
            OverviewCategory(CategoryKind.NUTRITION, R.string.progress_overview_nutrition, R.string.progress_overview_nutrition_detail, 78),
            OverviewCategory(CategoryKind.ACTIVITY, R.string.progress_overview_activity, R.string.progress_overview_activity_detail, 84),
            OverviewCategory(CategoryKind.HYDRATION, R.string.progress_overview_hydration, R.string.progress_overview_hydration_detail, 65),
        ),
        advice = R.string.progress_overview_advice,
        // w01 existe en la VORA Mock API, igual que el recomendado de Home
        workout = Workout(
            id = "w01",
            name = "Upper Body Strength",
            level = WorkoutLevel.INTERMEDIATE,
            durationMinutes = 42,
            exerciseCount = 5,
        ),
        goal = R.string.goal_build_strength,
        weeklyDone = 4,
        weeklyTarget = 5,
    ),
    dashboard = ProgressDashboard(
        stats = listOf(
            DashboardStat(R.string.progress_dash_workouts, R.string.progress_dash_workouts_value, R.string.progress_dash_workouts_trend),
            DashboardStat(R.string.progress_dash_steps, R.string.progress_dash_steps_value, R.string.progress_dash_steps_trend),
            DashboardStat(R.string.progress_dash_protein, R.string.progress_dash_protein_value, R.string.progress_dash_protein_trend),
            DashboardStat(R.string.progress_dash_streak, R.string.progress_dash_streak_value, R.string.progress_dash_streak_trend),
        ),
        insight = R.string.progress_dash_insight,
        areas = listOf(
            DashboardArea(CategoryKind.WORKOUT, R.string.progress_dash_area_workout, R.string.progress_dash_area_workout_detail),
            DashboardArea(CategoryKind.NUTRITION, R.string.progress_dash_area_nutrition, R.string.progress_dash_area_nutrition_detail),
            DashboardArea(CategoryKind.ACTIVITY, R.string.progress_dash_area_activity, R.string.progress_dash_area_activity_detail),
        ),
        goal = R.string.progress_dash_goal,
        goalBody = R.string.progress_dash_goal_body,
        goalPercent = 68,
    ),
    workout = WorkoutProgress(
        metrics = listOf(
            ProgressMetric(R.string.progress_workout_sessions, R.string.progress_workout_sessions_value),
            ProgressMetric(R.string.progress_workout_volume, R.string.progress_workout_volume_value),
            ProgressMetric(R.string.progress_workout_avg_time, R.string.progress_workout_avg_time_value),
        ),
        // proporcionales a las barras del frame (52, 58, 0, 66 y 72) y suman los 8,420 kg de VOLUME
        volumeKg = listOf(1765f, 1970f, 0f, 2240f, 2445f),
        volumeChange = R.string.progress_workout_volume_change,
        personalBest = PersonalBest(
            exercise = R.string.progress_workout_best_exercise,
            detail = R.string.progress_workout_best_detail,
            improvement = R.string.progress_workout_best_improvement,
        ),
        insight = R.string.progress_workout_insight,
        exercises = listOf(
            ExerciseTrend(R.string.progress_workout_bench, R.string.progress_workout_bench_detail, R.string.progress_workout_bench_change, improved = true),
            ExerciseTrend(R.string.progress_workout_cable_row, R.string.progress_workout_cable_row_detail, R.string.progress_workout_cable_row_change, improved = true),
            ExerciseTrend(R.string.progress_workout_shoulder, R.string.progress_workout_shoulder_detail, R.string.progress_workout_stable, improved = false),
            ExerciseTrend(R.string.progress_workout_lateral, R.string.progress_workout_lateral_detail, R.string.progress_workout_lateral_change, improved = true),
        ),
    ),
    nutrition = NutritionProgress(
        metrics = listOf(
            ProgressMetric(R.string.progress_nutrition_calories, R.string.progress_nutrition_calories_value),
            ProgressMetric(R.string.progress_nutrition_protein, R.string.progress_nutrition_protein_value),
            ProgressMetric(R.string.progress_nutrition_target_hit, R.string.progress_nutrition_target_hit_value),
        ),
        target = R.string.progress_nutrition_target,
        targetHit = listOf(true, true, false, true, true, false, true),
        insight = R.string.progress_nutrition_insight,
        habits = listOf(
            ProgressMetric(R.string.progress_nutrition_lunch, R.string.progress_nutrition_lunch_value),
            ProgressMetric(R.string.progress_nutrition_source, R.string.progress_nutrition_source_value),
            ProgressMetric(R.string.progress_nutrition_breakfast, R.string.progress_nutrition_breakfast_value),
            ProgressMetric(R.string.progress_nutrition_dinner, R.string.progress_nutrition_dinner_value),
        ),
    ),
    health = HealthTrends(
        metrics = listOf(
            ProgressMetric(R.string.progress_health_steps, R.string.progress_health_steps_value),
            ProgressMetric(R.string.progress_health_distance, R.string.progress_health_distance_value),
            ProgressMetric(R.string.progress_health_kcal, R.string.progress_health_kcal_value),
        ),
        // proporcionales a las barras del frame (45, 59, 61, 56, 71, 59 y 59): promedian los
        // 7,842 de AVG STEPS y solo miércoles y viernes pasan la meta, como las barras oscuras
        steps = listOf(6025, 7900, 8167, 7498, 9506, 7899, 7899),
        stepGoal = 8000,
        insight = R.string.progress_health_insight,
        signals = listOf(
            HabitSignal(SignalKind.SLEEP, R.string.progress_health_sleep, R.string.progress_health_improving, improving = true),
            HabitSignal(SignalKind.MOVEMENT, R.string.progress_health_movement, R.string.progress_health_consistent, improving = false),
            HabitSignal(SignalKind.WORKOUT, R.string.progress_health_workouts, R.string.progress_health_improving, improving = true),
            HabitSignal(SignalKind.PROTEIN, R.string.progress_health_protein, R.string.progress_health_improving, improving = true),
        ),
    ),
)
