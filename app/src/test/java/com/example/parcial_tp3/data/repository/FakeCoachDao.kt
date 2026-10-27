package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.dao.CoachDao
import com.example.parcial_tp3.data.local.entity.CoachMessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * CoachDao falso en memoria. Ordena como la consulta real (createdAt y despues id) para que
 * el test del repositorio no dependa del orden de insercion.
 */
class FakeCoachDao : CoachDao {

    private val filas = MutableStateFlow<List<CoachMessageEntity>>(emptyList())
    var falla = false

    override fun observeAll(): Flow<List<CoachMessageEntity>> = filas

    override suspend fun insert(message: CoachMessageEntity): Long {
        if (falla) throw IllegalStateException("Room roto a proposito")
        val id = (filas.value.maxOfOrNull { it.id } ?: 0L) + 1
        filas.value = (filas.value + message.copy(id = id)).sortedWith(
            compareBy({ it.createdAt }, { it.id }),
        )
        return id
    }

    override suspend fun count(): Int = filas.value.size
}
