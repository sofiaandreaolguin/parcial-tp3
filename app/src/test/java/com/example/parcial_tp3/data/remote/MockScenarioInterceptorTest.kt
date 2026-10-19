package com.example.parcial_tp3.data.remote

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * El interceptor que agrega los headers de prueba del mock (specs/API.md, escenarios para RF05).
 */
class MockScenarioInterceptorTest {

    private val server = MockWebServer()

    @Before
    fun setUp() = server.start()

    @After
    fun tearDown() = server.shutdown()

    private fun pedirCon(interceptor: MockScenarioInterceptor): RecordedRequest {
        server.enqueue(MockResponse())
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        client.newCall(Request.Builder().url(server.url("/v1/workouts")).build()).execute().close()
        return server.takeRequest()
    }

    @Test
    fun `C3 con escenario y demora la peticion lleva X-Mock-Scenario y X-Mock-Delay-Ms`() {
        val pedido = pedirCon(MockScenarioInterceptor(scenario = "error", delayMs = 3000))

        assertEquals("error", pedido.getHeader("X-Mock-Scenario"))
        assertEquals("3000", pedido.getHeader("X-Mock-Delay-Ms"))
    }

    @Test
    fun `C4 sin escenario ni demora la peticion sale sin headers de prueba`() {
        val pedido = pedirCon(MockScenarioInterceptor(scenario = "", delayMs = 0))

        assertNull(pedido.getHeader("X-Mock-Scenario"))
        assertNull(pedido.getHeader("X-Mock-Delay-Ms"))
    }
}
