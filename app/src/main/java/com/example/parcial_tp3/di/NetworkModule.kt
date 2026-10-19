package com.example.parcial_tp3.di

import com.example.parcial_tp3.data.remote.VoraApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit

// FIRMAS VACIAS (tests en rojo): los @Provides de Hilt se agregan en el paso de implementar.
object NetworkModule {

    fun provideRetrofit(client: OkHttpClient, baseUrl: String): Retrofit =
        Retrofit.Builder().baseUrl("http://localhost/").build()

    fun provideVoraApi(retrofit: Retrofit): VoraApi = retrofit.create(VoraApi::class.java)
}
