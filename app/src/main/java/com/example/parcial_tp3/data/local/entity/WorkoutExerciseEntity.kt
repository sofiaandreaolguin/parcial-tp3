package com.example.parcial_tp3.data.local.entity

import androidx.room.Entity

/**
 * Los ejercicios de un detalle descargado (RF02). La clave es (workoutId, position) y no el id del
 * ejercicio: e01 esta en w01 y en w03. Sin @ForeignKey con CASCADE: un REPLACE en workouts
 * borraria los ejercicios.
 */
@Entity(tableName = "workout_exercises", primaryKeys = ["workoutId", "position"])
data class WorkoutExerciseEntity(
    val workoutId: String,
    val position: Int,
    val exerciseId: String,
    val name: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: Int,
    val restSeconds: Int,
    val instructions: String,
    val tip: String,
)
