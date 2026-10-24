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
)
