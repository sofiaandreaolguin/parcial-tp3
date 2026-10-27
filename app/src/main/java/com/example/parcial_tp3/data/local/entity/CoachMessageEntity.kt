package com.example.parcial_tp3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla del historial del coach (EXT01). El mock no guarda nada, asi que el hilo vive aca.
 *
 * Misma convencion que [MealEntity]: el rol va como texto (el nombre del enum) y no hace
 * falta TypeConverter.
 *
 * **[createdAt] no es una fecha: es un numero de orden.** Lo pone el repositorio contando las
 * filas que ya hay (`dao.count() + 1`), no el reloj del sistema, asi el hilo queda ordenado
 * igual en cualquier maquina y los tests no dependen de la hora. El dia que las burbujas
 * muestren la hora de verdad hay que sumar otra columna, no reinterpretar esta.
 */
@Entity(tableName = "coach_messages")
data class CoachMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String,
    val text: String,
    val topic: String?,
    val isSimulated: Boolean,
    val createdAt: Long,
)
