package com.example.parcial_tp3.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Agrega a cada peticion los headers de prueba del mock (specs/API.md, escenarios para RF05):
 * X-Mock-Scenario (empty, error, malformed) y X-Mock-Delay-Ms (0 a 10000).
 *
 * Los valores salen de api.mockScenario y api.mockDelayMs en local.properties, y NetworkModule
 * lo suma solo en debug. Sin valores, la peticion sale como esta: el mock responde normal.
 */
class MockScenarioInterceptor(
    private val scenario: String,
    private val delayMs: Int,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            if (scenario.isNotBlank()) header("X-Mock-Scenario", scenario)
            if (delayMs > 0) header("X-Mock-Delay-Ms", delayMs.toString())
        }.build()
        return chain.proceed(request)
    }
}
