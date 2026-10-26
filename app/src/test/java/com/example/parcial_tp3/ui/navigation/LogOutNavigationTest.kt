package com.example.parcial_tp3.ui.navigation

import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.createGraph
import com.example.parcial_tp3.irAPestana
import androidx.navigation.testing.TestNavHostController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/** C16: con las mismas rutas de la app, tras el log out el atras no vuelve a Home ni a Profile. */
@RunWith(RobolectricTestRunner::class)
class LogOutNavigationTest {

    private fun navController() = TestNavHostController(RuntimeEnvironment.getApplication()).apply {
        navigatorProvider.addNavigator(ComposeNavigator())
        graph = createGraph(startDestination = Rutas.HOME_GRAPH) {
            VoraTab.entries.forEach { tab ->
                navigation(startDestination = tab.inicio, route = tab.grafo) {
                    composable(tab.inicio) {}
                    if (tab == VoraTab.PROFILE) composable(Rutas.SETTINGS) {}
                }
            }
            navigation(startDestination = Rutas.SIGN_IN, route = Rutas.ACCESS_GRAPH) {
                composable(Rutas.SIGN_IN) {}
            }
        }
    }

    @Test
    fun `C16 despues del log out la pila no vuelve a Home`() {
        val nav = navController()
        nav.irAPestana(VoraTab.WORKOUT)
        nav.irAPestana(VoraTab.PROFILE)
        nav.navigate(Rutas.SETTINGS)
        assertEquals(Rutas.SETTINGS, nav.currentDestination?.route)

        nav.cerrarSesion()

        assertEquals(Rutas.SIGN_IN, nav.currentDestination?.route)
        assertNull("no hay pantalla anterior a la que volver", nav.previousBackStackEntry)
        val rutas = nav.currentBackStack.value.mapNotNull { it.destination.route }
        assertEquals(emptyList<String>(), rutas.filter { it == Rutas.HOME_DASHBOARD || it == Rutas.PROFILE_DASHBOARD || it == Rutas.SETTINGS })
    }
}
