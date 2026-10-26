package com.example.parcial_tp3.ui.navigation

/** Destino inicial del NavHost segun la sesion, calculado antes de componerlo (C13, C14). */
fun startDestination(hasSession: Boolean): String =
    if (hasSession) Rutas.HOME_GRAPH else Rutas.ACCESS_GRAPH
