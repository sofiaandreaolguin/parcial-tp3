package com.example.parcial_tp3.data.remote

import okhttp3.Interceptor
import okhttp3.Response

// FIRMA VACIA (tests en rojo): la logica la agrega la tarea VC-f en el paso de implementar.
class MockScenarioInterceptor(
    private val scenario: String,
    private val delayMs: Int,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain.proceed(chain.request())
}
