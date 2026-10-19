package com.example.parcial_tp3.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.parcial_tp3.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

/**
 * Cache de entrenamientos (RF02). Lo minimo para guardar y leer: la tarea j suma el detalle.
 */
@Dao
interface WorkoutDao {

    /** Flow: Room vuelve a emitir cada vez que cambia la tabla. */
    @Query("SELECT * FROM workouts ORDER BY id")
    fun observeAll(): Flow<List<WorkoutEntity>>

    /** Upsert: si el id ya esta, lo reemplaza (refrescar la cache no duplica filas). */
    @Upsert
    suspend fun upsertAll(workouts: List<WorkoutEntity>)
}
