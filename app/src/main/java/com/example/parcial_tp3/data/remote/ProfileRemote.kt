package com.example.parcial_tp3.data.remote

import com.example.parcial_tp3.data.remote.dto.ProfileDto
import com.example.parcial_tp3.data.repository.ProfileDataException
import com.example.parcial_tp3.data.repository.ProfileError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/** Documento `users/{uid}`. Los errores salen como [ProfileDataException]. */
interface ProfileRemote {
    /** Lee el documento (servidor, o cache si no hay red); null si no existe. */
    suspend fun get(uid: String): ProfileDto?

    /** Crea o reemplaza el documento y espera la confirmacion del servidor. */
    suspend fun create(uid: String, dto: ProfileDto)

    /**
     * Actualizacion parcial sin esperar al servidor: la cache local la aplica en el acto.
     * Si el servidor la rechaza despues, o falla antes de enviarla, avisa con [onError].
     */
    fun update(uid: String, fields: Map<String, Any>, onError: (ProfileError) -> Unit)

    /** Cambios del documento, incluidas las escrituras que el servidor rechaza y revierte. */
    fun observe(uid: String): Flow<ProfileDto>

    /** Para construir `ProfileRepository` sin Firebase (los repositorios falsos de los tests). */
    object Unavailable : ProfileRemote {
        override suspend fun get(uid: String): ProfileDto? = throw ProfileDataException(ProfileError.UNKNOWN)
        override suspend fun create(uid: String, dto: ProfileDto) = throw ProfileDataException(ProfileError.UNKNOWN)
        override fun update(uid: String, fields: Map<String, Any>, onError: (ProfileError) -> Unit) = Unit
        override fun observe(uid: String): Flow<ProfileDto> = emptyFlow()
    }
}
