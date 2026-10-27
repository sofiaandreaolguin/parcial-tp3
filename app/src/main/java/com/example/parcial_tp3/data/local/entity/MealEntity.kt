package com.example.parcial_tp3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla de comidas (RF04).
 *
 * El id es Long porque meal_details/{mealId} usa NavType.LongType (NAV04). La fecha va como
 * texto ISO (AAAA-MM-DD) y el tipo como el nombre del enum: la Entity no depende del dominio
 * y no hace falta un TypeConverter.
 */
@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val date: String,
    val type: String,
    val kcal: Int,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
    val ingredients: String = "[]",
)
