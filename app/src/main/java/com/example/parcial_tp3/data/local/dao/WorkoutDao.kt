package com.example.parcial_tp3.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.parcial_tp3.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

// FIRMAS VACIAS (tests en rojo): las consultas reales se escriben en el paso de implementar.
@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workouts WHERE 0")
    fun observeAll(): Flow<List<WorkoutEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsertAll(workouts: List<WorkoutEntity>)
}
