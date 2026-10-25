package com.example.parcial_tp3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla de la cache de entrenamientos (RF02).
 *
 * Los campos del resumen del catalogo (GET /v1/workouts) mas description, que llega solo con el
 * detalle: description nulo quiere decir "detalle nunca descargado". Los ejercicios van en
 * WorkoutExerciseEntity. El id es String porque el mock usa "w01", "w02"...
 */
@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey val id: String,
    val name: String,
    val level: String,
    val durationMinutes: Int,
    val exerciseCount: Int,
    val description: String? = null,
)
