package com.example.parcial_tp3.di

import com.example.parcial_tp3.BuildConfig
import com.example.parcial_tp3.data.remote.MockScenarioInterceptor
import com.example.parcial_tp3.data.remote.VoraApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Le dice a Hilt como construir lo de red, que no son clases nuestras (RF08).
 *
 * Esta en SingletonComponent y cada @Provides es @Singleton: toda la app comparte un solo
 * OkHttpClient, un solo Retrofit y una sola VoraApi. Quien necesite la API la pide por
 * constructor (por ejemplo WorkoutRepository) y Hilt resuelve la cadena entera:
 * VoraApi <- Retrofit <- OkHttpClient <- interceptores.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /** Sale de api.baseUrl en local.properties: local (10.0.2.2:8000) o publicado. */
    @Provides
    @Named("baseUrl")
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @Singleton
    fun provideMockScenarioInterceptor(): MockScenarioInterceptor =
        MockScenarioInterceptor(BuildConfig.MOCK_SCENARIO, BuildConfig.MOCK_DELAY_MS)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        mockScenario: MockScenarioInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder().apply {
            // Los escenarios de prueba solo en debug: la version de entrega pide al mock normal.
            if (BuildConfig.DEBUG) addInterceptor(mockScenario)
            addInterceptor(logging)
        }.build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, @Named("baseUrl") baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideVoraApi(retrofit: Retrofit): VoraApi = retrofit.create(VoraApi::class.java)
}
