package com.example.parcial_tp3.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.parcial_tp3.ui.screens.profile.EditProfileRoute
import com.example.parcial_tp3.ui.screens.profile.GoalsRoute
import com.example.parcial_tp3.ui.screens.profile.NutritionPreferencesRoute
import com.example.parcial_tp3.ui.screens.profile.ProfileDashboardRoute
import com.example.parcial_tp3.ui.screens.profile.SettingsRoute
import com.example.parcial_tp3.ui.screens.profile.VoiceSettingsRoute

/** Perfil: 6 pantallas. Solo 01 con barra; 02 a 06 se abren encima. */
fun NavGraphBuilder.profileGraph(nav: NavController) {
    navigation(startDestination = Rutas.PROFILE_DASHBOARD, route = Rutas.PROFILE_GRAPH) {

        composable(Rutas.PROFILE_DASHBOARD) {
            ProfileDashboardRoute(
                onSettings = { nav.navigate(Rutas.SETTINGS) },
                onEditProfile = { nav.navigate(Rutas.EDIT_PROFILE) },
                onReviewPreferences = { nav.navigate(Rutas.VOICE_AI_SETTINGS) },
                onGoals = { nav.navigate(Rutas.GOALS_PREFERENCES) },
                onPreferences = { nav.navigate(Rutas.NUTRITION_PREFERENCES) },
            )
        }
        composable(Rutas.EDIT_PROFILE) {
            EditProfileRoute(onBack = { nav.popBackStack() })
        }
        composable(Rutas.GOALS_PREFERENCES) {
            GoalsRoute(onBack = { nav.popBackStack() })
        }
        composable(Rutas.NUTRITION_PREFERENCES) {
            NutritionPreferencesRoute(onBack = { nav.popBackStack() })
        }
        composable(Rutas.VOICE_AI_SETTINGS) {
            VoiceSettingsRoute(
                onBack = { nav.popBackStack() },
                onPrivacy = { nav.navigate(Rutas.SETTINGS) },
            )
        }
        composable(Rutas.SETTINGS) {
            SettingsRoute(
                onBack = { nav.popBackStack() },
                onEditProfile = { nav.navigate(Rutas.EDIT_PROFILE) },
                // cerrar sesión: vacía toda la pila y lo que cada pestaña guardó (saveState de
                // irAPestana), así al volver a entrar ninguna abre una pantalla de la sesión anterior
                onLogOut = {
                    VoraTab.entries.forEach { nav.clearBackStack(it.inicio) }
                    nav.navigate(Rutas.ACCESS_GRAPH) {
                        popUpTo(nav.graph.id) { inclusive = true }
                    }
                },
            )
        }
    }
}
