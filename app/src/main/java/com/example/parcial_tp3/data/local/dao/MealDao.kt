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

    /** Las comidas de un dia (fecha ISO), por id. Room vuelve a emitir cada vez que cambia la tabla. */
    @Query("SELECT * FROM meals WHERE date = :date ORDER BY id")
    fun observeByDate(date: String): Flow<List<MealEntity>>
}
