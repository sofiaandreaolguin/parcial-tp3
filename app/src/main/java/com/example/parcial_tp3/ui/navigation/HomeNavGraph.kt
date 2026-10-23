package com.example.parcial_tp3.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.parcial_tp3.irAPestana
import com.example.parcial_tp3.ui.screens.home.HomeRoute
import com.example.parcial_tp3.ui.screens.home.NotificationDestination
import com.example.parcial_tp3.ui.screens.home.NotificationsRoute

/** Inicio: Home / 01 y 02. Las dos con barra. */
fun NavGraphBuilder.homeGraph(nav: NavController) {
    navigation(startDestination = Rutas.HOME_DASHBOARD, route = Rutas.HOME_GRAPH) {
        composable(Rutas.HOME_DASHBOARD) {
            HomeRoute(
                onNotifications = { nav.navigate(Rutas.NOTIFICATIONS) { launchSingleTop = true } },
                onVoice = { nav.navigate(Rutas.VOICE) { launchSingleTop = true } },
                // las dos cruzan de flujo: "Start my workout" abre la pestana Workout como la barra
                onStartMyWorkout = { nav.irAPestana(VoraTab.WORKOUT) },
                onLogBreakfast = { nav.navigate(Rutas.ADD_FOOD) { launchSingleTop = true } },
                onOverview = { nav.irAPestana(VoraTab.PROGRESS) },
                onStartWorkout = { id -> nav.navigate(Rutas.workoutDetails(id)) { launchSingleTop = true } },
            )
        }
        composable(Rutas.NOTIFICATIONS) {
            NotificationsRoute(
                onDestination = { destino ->
                    when (destino) {
                        NotificationDestination.WeeklyReview -> nav.navigate(Rutas.WEEKLY_REVIEW) { launchSingleTop = true }
                        is NotificationDestination.WorkoutDetails ->
                            nav.navigate(Rutas.workoutDetails(destino.workoutId)) { launchSingleTop = true }
                        // navegacion comun y no de pestana: asi atras vuelve a Notifications
                        NotificationDestination.Nutrition -> nav.navigate(Rutas.NUTRITION_DASHBOARD) { launchSingleTop = true }
                        NotificationDestination.None -> Unit
                    }
                },
            )
        }
    }
}
