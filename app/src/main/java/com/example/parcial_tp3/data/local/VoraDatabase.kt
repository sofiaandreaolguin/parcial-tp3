package com.example.parcial_tp3.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.parcial_tp3.data.local.dao.WorkoutDao
import com.example.parcial_tp3.data.local.entity.WorkoutEntity

/**
 * La unica base Room de la app (specs/CONTEXTO.md, seccion 2): cache de entrenamientos y,
 * cuando la sume Nutrition (tarea h), las comidas.
 */
@Database(entities = [WorkoutEntity::class], version = 1, exportSchema = false)
abstract class VoraDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
}
