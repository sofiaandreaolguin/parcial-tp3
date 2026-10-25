package com.example.parcial_tp3.data.local.entity

/**
 * Las columnas del resumen del catalogo, sin description. No es tabla: con
 * @Upsert(entity = WorkoutEntity::class) actualiza solo estas y deja description como estaba.
 */
data class WorkoutSummaryRow(
    val id: String,
    val name: String,
    val level: String,
    val durationMinutes: Int,
    val exerciseCount: Int,
)
