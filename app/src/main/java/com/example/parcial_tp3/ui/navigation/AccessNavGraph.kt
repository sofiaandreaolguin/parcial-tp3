package com.example.parcial_tp3.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.compose.runtime.remember
import com.example.parcial_tp3.ui.screens.access.SignInRoute
import com.example.parcial_tp3.ui.screens.access.SignUpRoute

/** Acceso: Login - 01 y 02. Sin barra, antes de entrar a la app. */
fun NavGraphBuilder.accessGraph(nav: NavController) {
    navigation(startDestination = Rutas.SIGN_IN, route = Rutas.ACCESS_GRAPH) {

        composable(Rutas.SIGN_IN) { entry ->
            val acceso = remember(entry) { nav.getBackStackEntry(Rutas.ACCESS_GRAPH) }
            SignInRoute(
                accessGraph = acceso,
                onSignedIn = { nav.entrarAHome() },
                // launchSingleTop: un doble toque no apila dos SignUp
                onSignUp = { nav.navigate(Rutas.SIGN_UP) { launchSingleTop = true } },
            )
        }

        composable(Rutas.SIGN_UP) { entry ->
            val acceso = remember(entry) { nav.getBackStackEntry(Rutas.ACCESS_GRAPH) }
            SignUpRoute(
                accessGraph = acceso,
                onSignedIn = { nav.entrarAHome() },
                // hasta sign_in y no un pop a ciegas: un doble toque no saca tambien el login
                onBack = { nav.popBackStack(Rutas.SIGN_IN, inclusive = false) },
            )
        }
    }
}

/**
 * Entrar a la app: a Home sacando todo el grafo de acceso de la pila, asi el boton atras
 * desde Home cierra la app en vez de volver al login (NAV02). Se saca el grafo y no solo
 * sign_in porque desde SignUp el login queda debajo.
 */
private fun NavController.entrarAHome() {
    navigate(Rutas.HOME_DASHBOARD) {
        popUpTo(Rutas.ACCESS_GRAPH) { inclusive = true }
    }
}
