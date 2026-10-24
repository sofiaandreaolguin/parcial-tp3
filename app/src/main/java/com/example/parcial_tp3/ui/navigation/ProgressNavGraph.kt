package com.example.parcial_tp3.ui.navigation

import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.PantallaVacia
import com.example.parcial_tp3.ui.screens.progress.HealthTrendsRoute
import com.example.parcial_tp3.ui.screens.progress.NutritionProgressRoute
import com.example.parcial_tp3.ui.screens.progress.ProgressDashboardRoute
import com.example.parcial_tp3.ui.screens.progress.TodaysOverviewRoute
import com.example.parcial_tp3.ui.screens.progress.WorkoutProgressRoute

/**
 * Progreso: 6 pantallas. 01 a 05 con barra, 06 encima.
 *
 * La pestana aterriza en TODAYS_OVERVIEW (Progress / 01) y desde ahi "Start Workout"
 * lleva a PROGRESS_DASHBOARD (Progress / 02), que es la que tiene los links a 03, 04, 05
 * y 06. Asi se llega a las seis y la 01 deja de depender de una entrada desde Home.
 *
 * El recorrido no es el mas logico del producto ("Start Workout" no arranca un
 * entrenamiento), y es a proposito: ficha docs/decisiones/005.
 */
fun NavGraphBuilder.progressGraph(nav: NavController) {
    navigation(startDestination = Rutas.TODAYS_OVERVIEW, route = Rutas.PROGRESS_GRAPH) {

        composable(Rutas.TODAYS_OVERVIEW) {
            TodaysOverviewRoute(
                onStartWorkout = { nav.navigate(Rutas.PROGRESS_DASHBOARD) },
                onWorkoutProgress = { nav.navigate(Rutas.WORKOUT_PROGRESS) },
                onNutritionProgress = { nav.navigate(Rutas.NUTRITION_PROGRESS) },
                onHealthTrends = { nav.navigate(Rutas.HEALTH_TRENDS) },
            )
        }
        composable(Rutas.PROGRESS_DASHBOARD) {
            ProgressDashboardRoute(
                onWorkoutProgress = { nav.navigate(Rutas.WORKOUT_PROGRESS) },
                onNutritionProgress = { nav.navigate(Rutas.NUTRITION_PROGRESS) },
                onHealthTrends = { nav.navigate(Rutas.HEALTH_TRENDS) },
                onWeeklyReview = { nav.navigate(Rutas.WEEKLY_REVIEW) },
            )
        }
        composable(Rutas.WORKOUT_PROGRESS) {
            WorkoutProgressRoute(onBack = { nav.popBackStack() })
        }
        composable(Rutas.NUTRITION_PROGRESS) {
            NutritionProgressRoute(onBack = { nav.popBackStack() })
        }
        composable(Rutas.HEALTH_TRENDS) {
            HealthTrendsRoute(onBack = { nav.popBackStack() })
        }
        composable(Rutas.WEEKLY_REVIEW) {
            PantallaVacia(
                nombre = stringResource(R.string.pant_progress_06),
                onVolver = { nav.popBackStack() },
            )
        }
    }
}
