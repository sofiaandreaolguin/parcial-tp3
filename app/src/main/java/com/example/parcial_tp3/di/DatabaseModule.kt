package com.example.parcial_tp3.di

import android.content.Context
import androidx.room.Room
import com.example.parcial_tp3.data.local.VoraDatabase
import com.example.parcial_tp3.data.local.dao.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Le dice a Hilt como construir la base Room y sus DAO (RF08).
 *
 * La base es @Singleton: una sola instancia para toda la app. Cada DAO sale de esa base, asi
 * que los repositorios piden el DAO por constructor y nunca ven la base ni el contexto.
 *
 * Para sumar una tabla (por ejemplo las comidas, tarea h): la Entity en @Database de
 * VoraDatabase, su DAO ahi mismo, un @Provides aca abajo y subir version.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VoraDatabase =
        Room.databaseBuilder(context, VoraDatabase::class.java, "vora.db")
            // Mientras desarrollamos: si cambia la version, se borra la base y se crea de
            // nuevo en vez de crashear (docs/decisiones/003-base-room-compartida.md).
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideWorkoutDao(db: VoraDatabase): WorkoutDao = db.workoutDao()
}
