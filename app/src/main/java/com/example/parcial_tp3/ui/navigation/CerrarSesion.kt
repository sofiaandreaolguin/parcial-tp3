package com.example.parcial_tp3.ui.navigation

import androidx.navigation.NavController

/**
 * Cerrar sesión (C15, C16): vacía toda la pila y lo que cada pestaña guardó (saveState de
 * irAPestana), así al volver a entrar ninguna abre una pantalla de la sesión anterior.
 */
fun NavController.cerrarSesion() {
    navigate(Rutas.ACCESS_GRAPH) {
        popUpTo(graph.id) { inclusive = true }
    }
    VoraTab.entries.forEach { clearBackStack(it.grafo) }
}
