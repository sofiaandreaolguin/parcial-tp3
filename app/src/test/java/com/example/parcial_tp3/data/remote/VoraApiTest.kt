package com.example.parcial_tp3.data.remote

import com.example.parcial_tp3.data.remote.dto.HealthDto
import com.example.parcial_tp3.di.NetworkModule
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * El Retrofit que arma NetworkModule, contra un servidor falso (MockWebServer).
 * Comprueba la base del proyecto (RF08): la URL configurable y el conversor de JSON.
 */
class VoraApiTest {

    private val server = MockWebServer()

    @Before
    fun setUp() = server.start()

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `C1 Retrofit usa la BASE_URL que recibe`() {
        val baseUrl = server.url("/").toString()

        val retrofit = NetworkModule.provideRetrofit(OkHttpClient(), baseUrl)

        assertEquals(baseUrl, retrofit.baseUrl().toString())
    }

    @Test
    fun `C2 getHealth pide health y convierte el JSON del mock`() = runTest {
        server.enqueue(
            MockResponse().setBody("""{"status": "ok", "service": "vora-mock", "version": "1.0"}"""),
        )
        val retrofit = NetworkModule.provideRetrofit(OkHttpClient(), server.url("/").toString())
        val api = NetworkModule.provideVoraApi(retrofit)

        val response = api.getHealth()

        assertTrue(response.isSuccessful)
        assertEquals(HealthDto(status = "ok", service = "vora-mock", version = "1.0"), response.body())
        assertEquals("/health", server.takeRequest().path)
    }
}
