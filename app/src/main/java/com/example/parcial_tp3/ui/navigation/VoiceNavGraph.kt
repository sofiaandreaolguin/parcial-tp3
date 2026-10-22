package com.example.parcial_tp3.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.parcial_tp3.ui.screens.voice.ConversationRoute
import com.example.parcial_tp3.ui.screens.voice.VoiceRoute

/**
 * Asistente VORA: 6 frames en 2 rutas.
 *
 * Idle, Listening, Processing, Speaking y Error son estados de [Rutas.VOICE], no
 * destinos distintos (ficha docs/decisiones/002). Conversation si es una ruta aparte
 * porque cambia el layout.
 *
 * NAV05: ninguna de las dos pide permiso de microfono.
 */
fun NavGraphBuilder.voiceGraph(nav: NavController) {
    navigation(startDestination = Rutas.VOICE, route = Rutas.VOICE_GRAPH) {
        composable(Rutas.VOICE) {
            VoiceRoute(
                onVolver = { nav.popBackStack() },
                onEscribir = { nav.navigate(Rutas.CONVERSATION) },
            )
        }
        composable(Rutas.CONVERSATION) {
            ConversationRoute(onVolver = { nav.popBackStack() })
        }
    }
}
