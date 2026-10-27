package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.remote.ProfileRemote
import com.example.parcial_tp3.data.remote.dto.ProfileDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/** Firestore falso: `docs` es el servidor, `cambios` lo que emite el listener y `rechazos` los errores de update. */
class FakeProfileRemote : ProfileRemote {
    val docs = mutableMapOf<String, ProfileDto>()
    val cambios = MutableSharedFlow<ProfileDto>(extraBufferCapacity = 8)
    var falloGet: ProfileError? = null
    var falloCreate: ProfileError? = null
    var falloObserve: Throwable? = null
    var observadores = 0
    var gets = 0
    val updates = mutableListOf<Map<String, Any>>()
    private var onError: (ProfileError) -> Unit = {}

    override suspend fun get(uid: String): ProfileDto? {
        gets++
        falloGet?.let { throw ProfileDataException(it) }
        return docs[uid]
    }

    override suspend fun create(uid: String, dto: ProfileDto) {
        falloCreate?.let { throw ProfileDataException(it) }
        docs[uid] = dto
    }

    override fun update(uid: String, fields: Map<String, Any>, onError: (ProfileError) -> Unit) {
        updates += fields
        this.onError = onError
    }

    fun rechazarUpdate(error: ProfileError) = onError(error)

    override fun observe(uid: String): Flow<ProfileDto> {
        observadores++
        return kotlinx.coroutines.flow.flow {
            falloObserve?.let { throw it }
            cambios.collect { emit(it) }
        }
    }
}
