package com.example.parcial_tp3

import com.example.parcial_tp3.ui.navigation.RUTAS_ACCESO
import com.example.parcial_tp3.ui.navigation.RUTAS_CON_BARRA
import com.example.parcial_tp3.ui.navigation.RUTAS_ENCIMA
import com.example.parcial_tp3.ui.navigation.VoraTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Control del inventario (specs/PANTALLAS.md, seccion 16).
 *
 * Es barato y agarra el error mas probable del esqueleto: que la barra inferior
 * aparezca en una pantalla que se abre encima, o al reves.
 */
class RutasTest {

    @Test
    fun `la barra inferior aparece en 16 pantallas`() {
        assertEquals(16, RUTAS_CON_BARRA.size)
    }

    @Test
    fun `ninguna pantalla que se abre encima lleva barra`() {
        RUTAS_ENCIMA.forEach { ruta ->
            assertTrue("$ruta se abre encima: no deberia llevar barra", ruta !in RUTAS_CON_BARRA)
        }
    }

    @Test
    fun `ninguna pantalla de acceso lleva barra`() {
        RUTAS_ACCESO.forEach { ruta ->
            assertTrue("$ruta es de acceso: no deberia llevar barra", ruta !in RUTAS_CON_BARRA)
        }
    }

    @Test
    fun `las tres listas suman las 33 rutas del inventario sin repetir`() {
        val todas = RUTAS_CON_BARRA + RUTAS_ENCIMA + RUTAS_ACCESO
        assertEquals(33, todas.size)
        assertEquals(16 + 15 + 2, todas.size)
    }

    @Test
    fun `cada pestana arranca en una pantalla que lleva barra`() {
        VoraTab.entries.forEach { tab ->
            assertTrue(
                "la pestana ${tab.name} arranca en ${tab.inicio}, que no lleva barra: " +
                    "al tocarla la barra desapareceria",
                tab.inicio in RUTAS_CON_BARRA,
            )
        }
    }
}
