package com.example.parcial_tp3.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.parcial_tp3.data.local.dao.MealDao
import com.example.parcial_tp3.data.local.dao.WorkoutDao
import com.example.parcial_tp3.data.local.entity.MealEntity
import com.example.parcial_tp3.data.local.entity.WorkoutEntity

/**
 * La unica base Room de la app (specs/CONTEXTO.md, seccion 2): cache de entrenamientos y,
 * las comidas de Nutrition (tarea h).
 */
@Database(entities = [WorkoutEntity::class, MealEntity::class], version = 2, exportSchema = false)
abstract class VoraDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao

    abstract fun mealDao(): MealDao
}
