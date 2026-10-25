package com.example.parcial_tp3.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.parcial_tp3.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow

/**
 * Comidas guardadas en Room (RF04). El total del dia no sale de aca: lo suma el ViewModel
 * (docs/decisiones/007-home-totales-del-dia.md).
 */
@Dao
interface MealDao {

    /** Devuelve el id nuevo. Si el id ya existe falla (ABORT): la capa de datos no pisa filas. */
    @Insert
    suspend fun insert(meal: MealEntity): Long

    /** Las comidas de un dia (fecha ISO), por id. Room vuelve a emitir cada vez que cambia la tabla. */
    @Query("SELECT * FROM meals WHERE date = :date ORDER BY id")
    fun observeByDate(date: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE id = :id")
    fun observeById(id: Long): Flow<MealEntity?>

    /** Devuelve las filas cambiadas: 0 si la comida ya no existe. */
    @Update
    suspend fun update(meal: MealEntity): Int

    /** Devuelve las filas borradas: 0 si el id no existe. */
    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
