package com.example.parcial_tp3.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.compose.ui.res.stringResource
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.PantallaVacia

/** Inicio: Home / 01 y 02. Las dos con barra. */
fun NavGraphBuilder.homeGraph(nav: NavController) {
    navigation(startDestination = Rutas.HOME_DASHBOARD, route = Rutas.HOME_GRAPH) {
        composable(Rutas.HOME_DASHBOARD) {
            PantallaVacia(nombre = stringResource(R.string.pant_home_01))
        }
        composable(Rutas.NOTIFICATIONS) {
            PantallaVacia(nombre = stringResource(R.string.pant_home_02))
        }
    }
}
