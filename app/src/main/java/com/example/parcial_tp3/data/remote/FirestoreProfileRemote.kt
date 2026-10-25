package com.example.parcial_tp3.data.remote

import com.example.parcial_tp3.data.remote.dto.ProfileDto
import com.example.parcial_tp3.data.repository.ProfileDataException
import com.example.parcial_tp3.data.repository.ProfileError
import com.example.parcial_tp3.data.repository.profileErrorFromCode
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** Firestore con la cache local por defecto del SDK: una escritura sin red queda en cola (C19, C22). */
class FirestoreProfileRemote @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ProfileRemote {

    private fun doc(uid: String): DocumentReference = firestore.collection(USERS).document(uid)

    override suspend fun get(uid: String): ProfileDto? = translate {
        val snapshot = doc(uid).get().await()
        if (snapshot.exists()) snapshot.toObject(ProfileDto::class.java) else null
    }

    override suspend fun create(uid: String, dto: ProfileDto) = translate {
        doc(uid).set(dto).await()
        Unit
    }

    override fun update(uid: String, fields: Map<String, Any>) {
        doc(uid).update(fields)
    }

    override fun observe(uid: String): Flow<ProfileDto> = callbackFlow {
        val registration = doc(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(ProfileDataException(profileErrorFromCode(error.code.name), error))
                return@addSnapshotListener
            }
            runCatching { snapshot?.takeIf { it.exists() }?.toObject(ProfileDto::class.java) }
                .getOrNull()
                ?.let { trySend(it) }
        }
        awaitClose { registration.remove() }
    }

    private inline fun <T> translate(block: () -> T): T = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: FirebaseFirestoreException) {
        throw ProfileDataException(profileErrorFromCode(e.code.name), e)
    } catch (e: RuntimeException) {
        // toObject falla con RuntimeException si un campo cambio de tipo
        throw ProfileDataException(ProfileError.INVALID_DATA, e)
    }

    private companion object {
        const val USERS = "users"
    }
}
