package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.domain.model.Profile
import com.example.parcial_tp3.domain.model.ProfileEdit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Firestore en memoria: un documento por uid. `profile` es lo que muestra la app (la cache local);
 * `docs` es el servidor. Una escritura se aplica a la cache y queda pendiente hasta confirmar o rechazar.
 */
class FakeProfileRepository : ProfileRepository() {
    val docs = mutableMapOf<String, Profile>()
    private val _perfil = MutableStateFlow(Profile.default("", "", ""))
    private val _error = MutableStateFlow<ProfileError?>(null)
    private var uidActual: String? = null

    override val profile: StateFlow<Profile> = _perfil
    override val loadError: StateFlow<ProfileError?> = _error

    var falloLoad: ProfileError? = null
    var falloCreate: ProfileError? = null

    val loads = mutableListOf<Pair<String, String>>()
    val creados = mutableListOf<Pair<String, Profile>>()
    val ediciones = mutableListOf<ProfileEdit>()
    var clearCalls = 0
    var reloadCalls = 0

    fun ponerError(error: ProfileError?) {
        _error.value = error
    }

    /** El servidor rechaza la ultima escritura: la cache vuelve a lo que tiene el servidor. */
    fun rechazarUltimaEscritura() {
        uidActual?.let { uid -> docs[uid]?.let { _perfil.value = it } }
    }

    override suspend fun load(uid: String, email: String) {
        loads += uid to email
        falloLoad?.let { throw ProfileDataException(it) }
        uidActual = uid
        _perfil.value = docs.getOrPut(uid) { Profile.default("", "", email) }
    }

    override suspend fun create(uid: String, profile: Profile) {
        creados += uid to profile
        falloCreate?.let { throw ProfileDataException(it) }
        docs[uid] = profile
        uidActual = uid
        _perfil.value = profile
    }

    override fun saveEdit(edit: ProfileEdit) {
        ediciones += edit
        _perfil.value = _perfil.value.copy(
            firstName = edit.firstName,
            lastName = edit.lastName,
            birthDate = edit.birthDate,
            heightCm = edit.heightCm,
            weightKg = edit.weightKg,
            gender = edit.gender,
        )
    }

    override suspend fun reload() {
        reloadCalls++
    }

    override fun clear() {
        clearCalls++
        uidActual = null
        _perfil.value = Profile.default("", "", "")
    }
}
