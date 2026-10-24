package com.example.parcial_tp3.ui.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.parcial_tp3.ui.screens.workout.ActiveWorkoutRoute
import com.example.parcial_tp3.ui.screens.workout.ExerciseDetailsRoute
import com.example.parcial_tp3.ui.screens.workout.PreWorkoutRoute
import com.example.parcial_tp3.ui.screens.workout.WeeklyPlanRoute
import com.example.parcial_tp3.ui.screens.workout.WorkoutDashboardRoute
import com.example.parcial_tp3.ui.screens.workout.WorkoutCompleteRoute
import com.example.parcial_tp3.ui.screens.workout.WorkoutDetailsRoute

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
            WeeklyPlanRoute(onWorkout = { id -> nav.navigate(Rutas.workoutDetails(id)) })
        }

        composable(
            route = Rutas.WORKOUT_DETAILS,
            arguments = listOf(navArgument(Rutas.ARG_WORKOUT_ID) { type = NavType.StringType }),
        ) {
            WorkoutDetailsRoute(
                onBack = { nav.popBackStack() },
                onExercise = { id, exerciseId -> nav.navigate(Rutas.exerciseDetails(id, exerciseId)) },
                onStartWorkout = { id -> nav.navigate(Rutas.preWorkout(id)) },
            )
        }

        composable(
            route = Rutas.PRE_WORKOUT,
            arguments = listOf(navArgument(Rutas.ARG_WORKOUT_ID) { type = NavType.StringType }),
        ) {
            PreWorkoutRoute(
                onClose = { nav.popBackStack() },
                onAskVora = { nav.navigate(Rutas.VOICE) },
                onStartWorkout = { id -> nav.navigate(Rutas.activeWorkout(id)) },
            )
        }

        composable(
            route = Rutas.ACTIVE_WORKOUT,
            arguments = listOf(navArgument(Rutas.ARG_WORKOUT_ID) { type = NavType.StringType }),
        ) {
            // launchSingleTop: dos toques seguidos en "Finish Workout" no apilan dos 07
            ActiveWorkoutRoute(onFinish = { id -> nav.navigate(Rutas.workoutComplete(id)) { launchSingleTop = true } })
        }

        composable(
            route = Rutas.EXERCISE_DETAILS,
            arguments = listOf(
                navArgument(Rutas.ARG_WORKOUT_ID) { type = NavType.StringType },
                navArgument(Rutas.ARG_EXERCISE_ID) { type = NavType.StringType },
            ),
        ) {
            ExerciseDetailsRoute(
                onBack = { nav.popBackStack() },
                onAskVora = { nav.navigate(Rutas.VOICE) },
            )
        }

        composable(
            route = Rutas.WORKOUT_COMPLETE,
            arguments = listOf(navArgument(Rutas.ARG_WORKOUT_ID) { type = NavType.StringType }),
        ) { entrada ->
            // 07 muestra la sesion de 05, que sigue debajo en la pila. El remember evita buscar la
            // entrada en cada composicion: si "Done" ya saco 05 mientras 07 anima su salida,
            // getBackStackEntry tiraria IllegalArgumentException (spec sesion-de-entrenamiento-demo).
            val sesion = remember(entrada) { nav.getBackStackEntry(Rutas.ACTIVE_WORKOUT) }
            WorkoutCompleteRoute(
                sesion = sesion,
                // Vuelve a Workout / 01 sacando toda la sesion. Se vuelve hasta Home, que siempre
                // esta en la base, porque a 03 tambien se llega desde Home sin pasar por 01.
                onDone = {
                    nav.navigate(Rutas.WORKOUT_DASHBOARD) {
                        popUpTo(Rutas.HOME_DASHBOARD)
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
