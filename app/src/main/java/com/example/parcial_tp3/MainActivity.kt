package com.example.parcial_tp3

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.parcial_tp3.ui.components.VoraBottomBar
import com.example.parcial_tp3.ui.navigation.RUTAS_CON_BARRA
import com.example.parcial_tp3.ui.navigation.Rutas
import com.example.parcial_tp3.ui.navigation.VoraTab
import com.example.parcial_tp3.ui.navigation.accessGraph
import com.example.parcial_tp3.ui.navigation.homeGraph
import com.example.parcial_tp3.ui.navigation.nutritionGraph
import com.example.parcial_tp3.ui.navigation.profileGraph
import com.example.parcial_tp3.ui.navigation.progressGraph
import com.example.parcial_tp3.ui.navigation.voiceGraph
import com.example.parcial_tp3.ui.navigation.workoutGraph
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // El tema de VORA es solo modo claro (el Figma no tiene oscuro, UI01), asi que la barra
        // de estado se fija a mano. Sin argumentos, enableEdgeToEdge elige el color de los iconos
        // segun el modo oscuro DEL CELULAR: en un celu en oscuro los pintaba blancos sobre nuestro
        // fondo claro y no se leian. SystemBarStyle.light = "el fondo es claro, pinta los iconos
        // oscuros" (se nombra por el fondo, no por el icono). Lo mismo abajo, que VoraBottomBar
        // tambien usa colorScheme.surface, que es claro.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            Parcialtp3Theme {
                VoraApp()
            }
        }
    }
}

/**
 * El armazon de la app: un Scaffold con la barra inferior y un NavHost con las 33 rutas.
 *
 * La barra se arma UNA sola vez aca y aparece sola en las 16 pantallas que la comparten
 * (RUTAS_CON_BARRA). Ninguna pantalla la dibuja por su cuenta: duplicarla queda igual a
 * la vista pero rompe UI02 (un componente, una correccion).
 *
 * Cada flujo vive en su propio archivo de grafo, asi cada rol trabaja en el suyo.
 */
@Composable
fun VoraApp() {
    val nav = rememberNavController()
    val backStackEntry by nav.currentBackStackEntryAsState()
    val destino = backStackEntry?.destination
    val rutaActual = destino?.route
    val llevaBarra = rutaActual != null && rutaActual in RUTAS_CON_BARRA
    // Las dos del asistente se pintan hasta el borde: en el Figma el verde de Listening,
    // Processing y Speaking y el blanco del chat van por detras de las barras del sistema.
    // Se encargan ellas de sus propios margenes. Las otras 35 siguen igual que antes.
    val aSangre = rutaActual == Rutas.VOICE || rutaActual == Rutas.CONVERSATION

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (llevaBarra) {
                VoraBottomBar(
                    selected = pestanaActual(destino),
                    onTab = { tab -> nav.irAPestana(tab) },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = nav,
            // El inicio es el GRAFO de acceso, no la pantalla: startDestination tiene que
            // ser hijo directo del grafo raiz, y sign_in vive adentro de access_graph.
            // El access_graph arranca en sign_in, asi que la primera pantalla es la misma.
            startDestination = Rutas.ACCESS_GRAPH,
            modifier = if (aSangre) Modifier else Modifier.padding(padding),
        ) {
            accessGraph(nav)
            homeGraph(nav)
            workoutGraph(nav)
            nutritionGraph(nav)
            progressGraph(nav)
            profileGraph(nav)
            voiceGraph(nav)
        }
    }
}

/**
 * Cual de las 5 pestanas esta activa.
 *
 * Se calcula con la jerarquia del destino, no comparando la ruta: las pantallas viven
 * dentro del grafo de su flujo, asi que preguntamos "esta adentro del grafo de Workout?".
 * Comparando solo la ruta, la pestana se apagaria en Weekly Plan o en cualquier pantalla
 * que no sea la primera del flujo.
 */
fun pestanaActual(destino: NavDestination?): VoraTab? =
    VoraTab.entries.firstOrNull { tab ->
        destino?.hierarchy?.any { it.route == tab.grafo } == true
    }

/**
 * Cambiar de pestana sin apilar pantallas, y recordando donde estaba cada una.
 *
 * Ojo con popUpTo: va a HOME_DASHBOARD, NO al startDestination del NavHost. El inicio
 * del NavHost es el login, que se saca de la pila al entrar, asi que popUpTo al
 * startDestination no encontraria nada, no sacaria nada, y cada toque de pestana
 * apilaria una pantalla mas.
 */
fun NavController.irAPestana(tab: VoraTab) {
    navigate(tab.inicio) {
        popUpTo(Rutas.HOME_DASHBOARD) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
