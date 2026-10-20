package com.example.parcial_tp3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla de la cache de entrenamientos (RF02).
 *
 * Por ahora solo los campos del resumen del catalogo (GET /v1/workouts). El detalle y los
 * ejercicios los suma la tarea j. El id es String porque el mock usa "w01", "w02"...
 */
@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey val id: String,
    val name: String,
    val level: String,
    val durationMinutes: Int,
    val exerciseCount: Int,
)
