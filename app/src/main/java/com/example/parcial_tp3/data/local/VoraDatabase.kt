package com.example.parcial_tp3.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.parcial_tp3.data.local.dao.CoachDao
import com.example.parcial_tp3.data.local.dao.MealDao
import com.example.parcial_tp3.data.local.dao.WorkoutDao
import com.example.parcial_tp3.data.local.entity.CoachMessageEntity
import com.example.parcial_tp3.data.local.entity.MealEntity
import com.example.parcial_tp3.data.local.entity.WorkoutEntity
import com.example.parcial_tp3.data.local.entity.WorkoutExerciseEntity

/**
 * La unica base Room de la app (specs/CONTEXTO.md, seccion 2): cache de entrenamientos,
 * las comidas de Nutrition (tarea h) y el historial del coach (EXT01).
 *
 * **Al subir la version se pierde lo guardado.** DatabaseModule construye con
 * fallbackToDestructiveMigration(dropAllTables = true), asi que la version 4 borra tambien
 * `meals`: hay que volver a cargar una comida antes de sacar evidencia de RF04
 * (docs/decisiones/003-base-room-compartida.md).
 */
@Database(
    entities = [
        WorkoutEntity::class,
        WorkoutExerciseEntity::class,
        MealEntity::class,
        CoachMessageEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class VoraDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao

    abstract fun mealDao(): MealDao

    abstract fun coachDao(): CoachDao
}
