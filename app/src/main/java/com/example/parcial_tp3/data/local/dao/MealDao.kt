package com.example.parcial_tp3.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.parcial_tp3.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow

/**
 * Comidas guardadas en Room (RF04). Lo minimo para guardar y leer por dia: eliminar y el total
 * diario los suma la tarea l.
 */
@Dao
interface MealDao {

    /** Devuelve el id nuevo. Si el id ya existe falla (ABORT): la capa de datos no pisa filas. */
    @Insert
    suspend fun insert(meal: MealEntity): Long

    // Firma vacia (tests en rojo): WHERE 0 siempre devuelve vacio. La query real la escribe implementar.
    @Query("SELECT * FROM meals WHERE 0 AND date = :date")
    fun observeByDate(date: String): Flow<List<MealEntity>>
}
