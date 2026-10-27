package com.example.parcial_tp3.di

import com.example.parcial_tp3.data.remote.FirestoreProfileRemote
import com.example.parcial_tp3.data.remote.ProfileRemote
import com.example.parcial_tp3.data.repository.AuthRepository
import com.example.parcial_tp3.data.repository.CoachRepository
import com.example.parcial_tp3.data.repository.CoachRepositoryImpl
import com.example.parcial_tp3.data.repository.FirebaseAuthRepository
import com.example.parcial_tp3.data.repository.MealRepository
import com.example.parcial_tp3.data.repository.MealRepositoryImpl
import com.example.parcial_tp3.data.repository.WorkoutDetailRepository
import com.example.parcial_tp3.data.repository.WorkoutDetailRepositoryImpl
import com.example.parcial_tp3.data.repository.WorkoutRepository
import com.example.parcial_tp3.data.repository.WorkoutRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Le dice a Hilt que implementacion entregar cuando alguien pide una interfaz de repositorio
 * (RF08). Asi el ViewModel pide MealRepository y en los tests se le pasa uno falso.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMealRepository(impl: MealRepositoryImpl): MealRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProfileRemote(impl: FirestoreProfileRemote): ProfileRemote

    @Binds
    @Singleton
    abstract fun bindWorkoutDetailRepository(impl: WorkoutDetailRepositoryImpl): WorkoutDetailRepository

    @Binds
    @Singleton
    abstract fun bindCoachRepository(impl: CoachRepositoryImpl): CoachRepository
}
