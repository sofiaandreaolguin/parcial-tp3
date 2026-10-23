package com.example.parcial_tp3.ui.navigation

import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.PantallaVacia
import com.example.parcial_tp3.ui.screens.workout.WorkoutDashboardRoute

/**
 * Entrenamiento: 7 pantallas. 01 y 02 con barra, 03 a 07 encima.
 *
 * NAV04: el workoutId viaja en la ruta desde 03 y se arrastra por 04, 05, 06 y 07,
 * asi cada entrenamiento abre su propio detalle y la seleccion se mantiene.
 */
fun NavGraphBuilder.workoutGraph(nav: NavController) {
    navigation(startDestination = Rutas.WORKOUT_DASHBOARD, route = Rutas.WORKOUT_GRAPH) {

        composable(Rutas.WORKOUT_DASHBOARD) {
            WorkoutDashboardRoute(
                onWeeklyPlan = { nav.navigate(Rutas.WEEKLY_PLAN) },
                onStartWorkout = { id -> nav.navigate(Rutas.preWorkout(id)) },
                onWorkoutDetails = { id -> nav.navigate(Rutas.workoutDetails(id)) },
                onViewProgress = { nav.navigate(Rutas.WORKOUT_PROGRESS) },
            )
        }

        composable(Rutas.WEEKLY_PLAN) {
            PantallaVacia(nombre = stringResource(R.string.pant_workout_02))
        }

        composable(
            route = Rutas.WORKOUT_DETAILS,
            arguments = listOf(navArgument(Rutas.ARG_WORKOUT_ID) { type = NavType.StringType }),
        ) {
            PantallaVacia(
                nombre = stringResource(R.string.pant_workout_03),
                onVolver = { nav.popBackStack() },
            )
        }

        composable(
            route = Rutas.PRE_WORKOUT,
            arguments = listOf(navArgument(Rutas.ARG_WORKOUT_ID) { type = NavType.StringType }),
        ) {
            PantallaVacia(
                nombre = stringResource(R.string.pant_workout_04),
                onVolver = { nav.popBackStack() },
            )
        }

        composable(
            route = Rutas.ACTIVE_WORKOUT,
            arguments = listOf(navArgument(Rutas.ARG_WORKOUT_ID) { type = NavType.StringType }),
        ) {
            PantallaVacia(
                nombre = stringResource(R.string.pant_workout_05),
                onVolver = { nav.popBackStack() },
            )
        }

        composable(
            route = Rutas.EXERCISE_DETAILS,
            arguments = listOf(
                navArgument(Rutas.ARG_WORKOUT_ID) { type = NavType.StringType },
                navArgument(Rutas.ARG_EXERCISE_ID) { type = NavType.StringType },
            ),
        ) {
            PantallaVacia(
                nombre = stringResource(R.string.pant_workout_06),
                onVolver = { nav.popBackStack() },
            )
        }

        composable(
            route = Rutas.WORKOUT_COMPLETE,
            arguments = listOf(navArgument(Rutas.ARG_WORKOUT_ID) { type = NavType.StringType }),
        ) {
            PantallaVacia(
                nombre = stringResource(R.string.pant_workout_07),
                onVolver = { nav.popBackStack() },
            )
        }
    }
}
