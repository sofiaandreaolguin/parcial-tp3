package com.example.parcial_tp3.ui.navigation

import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.PantallaVacia

/**
 * Progreso: 6 pantallas. 01 a 05 con barra, 06 encima.
 *
 * La pestana aterriza en PROGRESS_DASHBOARD (Progress / 02), que es la que tiene los
 * links a 03, 04, 05 y 06. Si aterrizara en 01, esas cuatro quedaban sin camino.
 */
fun NavGraphBuilder.progressGraph(nav: NavController) {
    navigation(startDestination = Rutas.PROGRESS_DASHBOARD, route = Rutas.PROGRESS_GRAPH) {

        composable(Rutas.TODAYS_OVERVIEW) {
            PantallaVacia(nombre = stringResource(R.string.pant_progress_01))
        }
        composable(Rutas.PROGRESS_DASHBOARD) {
            PantallaVacia(nombre = stringResource(R.string.pant_progress_02))
        }
        composable(Rutas.WORKOUT_PROGRESS) {
            PantallaVacia(nombre = stringResource(R.string.pant_progress_03))
        }
        composable(Rutas.NUTRITION_PROGRESS) {
            PantallaVacia(nombre = stringResource(R.string.pant_progress_04))
        }
        composable(Rutas.HEALTH_TRENDS) {
            PantallaVacia(nombre = stringResource(R.string.pant_progress_05))
        }
        composable(Rutas.WEEKLY_REVIEW) {
            PantallaVacia(
                nombre = stringResource(R.string.pant_progress_06),
                onVolver = { nav.popBackStack() },
            )
        }
    }
}
