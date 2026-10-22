package com.example.parcial_tp3.ui.navigation

import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.PantallaVacia
import com.example.parcial_tp3.ui.screens.profile.EditProfileRoute
import com.example.parcial_tp3.ui.screens.profile.GoalsRoute
import com.example.parcial_tp3.ui.screens.profile.ProfileDashboardRoute

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
            PantallaVacia(
                nombre = stringResource(R.string.pant_profile_04),
                onVolver = { nav.popBackStack() },
            )
        }
        composable(Rutas.VOICE_AI_SETTINGS) {
            PantallaVacia(
                nombre = stringResource(R.string.pant_profile_05),
                onVolver = { nav.popBackStack() },
            )
        }
        composable(Rutas.SETTINGS) {
            PantallaVacia(
                nombre = stringResource(R.string.pant_profile_06),
                onVolver = { nav.popBackStack() },
            )
        }
    }
}
