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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.parcial_tp3.data.repository.AuthRepository
import com.example.parcial_tp3.data.repository.SessionStarter
import com.example.parcial_tp3.ui.components.VoraBottomBar
import com.example.parcial_tp3.ui.navigation.RUTAS_A_SANGRE
import com.example.parcial_tp3.ui.navigation.RUTAS_CON_BARRA
import com.example.parcial_tp3.ui.navigation.Rutas
import com.example.parcial_tp3.ui.navigation.VoraTab
import com.example.parcial_tp3.ui.navigation.accessGraph
import com.example.parcial_tp3.ui.navigation.homeGraph
import com.example.parcial_tp3.ui.navigation.startDestination
import com.example.parcial_tp3.ui.navigation.nutritionGraph
import com.example.parcial_tp3.ui.navigation.profileGraph
import com.example.parcial_tp3.ui.navigation.progressGraph
import com.example.parcial_tp3.ui.navigation.voiceGraph
import com.example.parcial_tp3.ui.navigation.workoutGraph
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var auth: AuthRepository
    @Inject lateinit var sessionStarter: SessionStarter

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
                // la sesion se lee antes de componer el NavHost: no hay parpadeo de SignIn (C13, C14)
                VoraApp(
                    startDestination = startDestination(hasSession = auth.currentUid != null),
                    restoreSession = sessionStarter::restore,
                )
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
fun VoraApp(
    startDestination: String,
    restoreSession: suspend () -> Boolean,
) {
    val nav = rememberNavController()
    // arranque en frio con sesion: lee el perfil; si la sesion ya no vale, vuelve a SignIn (C18, C35)
    LaunchedEffect(Unit) {
        if (startDestination == Rutas.HOME_GRAPH && !restoreSession()) {
            nav.navigate(Rutas.ACCESS_GRAPH) { popUpTo(nav.graph.id) { inclusive = true } }
        }
    }
    val backStackEntry by nav.currentBackStackEntryAsState()
    val destino = backStackEntry?.destination
    val rutaActual = destino?.route
    val llevaBarra = rutaActual != null && rutaActual in RUTAS_CON_BARRA
    // Se pintan hasta el borde: en el Figma el verde de Listening, Processing y Speaking, el
    // blanco del chat y el resplandor menta del login van por detras de las barras del sistema.
    // Se encargan ellas de sus propios margenes. Las demas siguen igual que antes.
    val aSangre = rutaActual in RUTAS_A_SANGRE

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
            // El inicio es un GRAFO, no una pantalla: startDestination tiene que ser hijo directo
            // del grafo raiz. Sin sesion es access_graph (arranca en sign_in); con sesion, home_graph.
            startDestination = startDestination,
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
