package com.example.parcial_tp3.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.parcial_tp3.data.local.entity.WorkoutEntity
import com.example.parcial_tp3.data.local.entity.WorkoutExerciseEntity
import com.example.parcial_tp3.data.local.entity.WorkoutSummaryRow
import kotlinx.coroutines.flow.Flow

/**
 * Cache de entrenamientos (RF02): el catalogo y los detalles descargados con sus ejercicios.
 *
 * Los repositorios usan saveSummaries y saveDetail. Tienen cuerpo porque Room no acepta un
 * @Transaction abstracto, y asi el FakeWorkoutDao de los tests hereda la misma logica.
 */
@Dao
interface WorkoutDao {

    /** Flow: Room vuelve a emitir cada vez que cambia la tabla. */
    @Query("SELECT * FROM workouts ORDER BY id")
    fun observeAll(): Flow<List<WorkoutEntity>>

    /** Upsert: si el id ya esta, lo reemplaza (refrescar la cache no duplica filas). */
    @Upsert
    suspend fun upsertAll(workouts: List<WorkoutEntity>)

    @Query("SELECT * FROM workouts ORDER BY id")
    suspend fun getAll(): List<WorkoutEntity>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getById(id: String): WorkoutEntity?

    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY position")
    suspend fun getExercises(workoutId: String): List<WorkoutExerciseEntity>

    /** Upsert parcial: escribe solo las columnas del resumen y deja description como estaba. */
    @Upsert(entity = WorkoutEntity::class)
    suspend fun upsertSummaries(rows: List<WorkoutSummaryRow>)

    @Upsert
    suspend fun upsertDetail(workout: WorkoutEntity)

    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteExercises(workoutId: String)

    @Insert
    suspend fun insertExercises(exercises: List<WorkoutExerciseEntity>)

    // Firmas vacias de la tarea del detalle y la cache: las completa la sesion de implementar.
    @Transaction
    suspend fun saveSummaries(workouts: List<WorkoutEntity>) = Unit

    @Transaction
    suspend fun saveDetail(workout: WorkoutEntity, exercises: List<WorkoutExerciseEntity>) = Unit
}
