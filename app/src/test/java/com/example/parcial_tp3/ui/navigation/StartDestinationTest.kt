package com.example.parcial_tp3.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class StartDestinationTest {

    @Test
    fun `C13 destino inicial con sesion es Home`() {
        assertEquals(Rutas.HOME_GRAPH, startDestination(hasSession = true))
    }

    @Test
    fun `C14 destino inicial sin sesion es SignIn`() {
        assertEquals(Rutas.ACCESS_GRAPH, startDestination(hasSession = false))
    }
}
