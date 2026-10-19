package com.example.parcial_tp3.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.compose.ui.res.stringResource
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.PantallaVacia

/** Acceso: Login - 01 y 02. Sin barra, antes de entrar a la app. */
fun NavGraphBuilder.accessGraph(nav: NavController) {
    navigation(startDestination = Rutas.SIGN_IN, route = Rutas.ACCESS_GRAPH) {

        composable(Rutas.SIGN_IN) {
            PantallaVacia(
                nombre = stringResource(R.string.pant_login_01),
                // Provisorio: sin este boton el esqueleto queda encerrado en el login.
                // Lo reemplaza el "Sign In" de verdad cuando se maquete la pantalla.
                onContinuar = {
                    nav.navigate(Rutas.HOME_DASHBOARD) {
                        // El login sale de la pila: atras desde Home no vuelve al login (NAV02).
                        popUpTo(Rutas.SIGN_IN) { inclusive = true }
                    }
                },
            )
        }

        composable(Rutas.SIGN_UP) {
            PantallaVacia(nombre = stringResource(R.string.pant_login_02))
        }
    }
}
