package com.example.parcial_tp3.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.parcial_tp3.data.local.entity.CoachMessageEntity
import kotlinx.coroutines.flow.Flow

/** El historial del coach (EXT01). Solo se agrega y se lee: no se edita ni se borra. */
@Dao
interface CoachDao {

    /**
     * El hilo completo. Se ordena tambien por id porque dos mensajes pueden caer en el mismo
     * milisegundo y ahi createdAt no alcanza para decidir el orden (C14).
     */
    @Query("SELECT * FROM coach_messages ORDER BY createdAt ASC, id ASC")
    fun observeAll(): Flow<List<CoachMessageEntity>>

    @Insert
    suspend fun insert(message: CoachMessageEntity): Long

    /** Para sembrar el hilo de ejemplo una sola vez (C2). */
    @Query("SELECT COUNT(*) FROM coach_messages")
    suspend fun count(): Int
}
