package com.example.parcial_tp3.di

import com.example.parcial_tp3.BuildConfig
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * La URL del servidor sale de api.baseUrl en local.properties. Si alguien la escribe sin la
 * barra final, Retrofit tira IllegalArgumentException al arrancar (clase 9-sep).
 */
class BaseUrlTest {

    @Test
    fun `C5 la BASE_URL de BuildConfig termina en barra`() {
        assertTrue(
            "BASE_URL tiene que terminar en / y es ${BuildConfig.BASE_URL}",
            BuildConfig.BASE_URL.endsWith("/"),
        )
    }
}
