package com.example.parcial_tp3.data.repository

import javax.inject.Inject

/** Arranque en frio con sesion activa: lee el perfil de la cuenta (C18, C24, C35). */
class SessionStarter @Inject constructor(
    private val auth: AuthRepository,
    private val profiles: ProfileRepository,
) {
    /** false si la sesion ya no es valida y se cerro (C35): hay que llevar a SignIn. */
    suspend fun restore(): Boolean {
        val uid = auth.currentUid ?: return false
        try {
            profiles.load(uid, auth.currentEmail.orEmpty())
        } catch (e: ProfileDataException) {
            if (e.error == ProfileError.NO_PERMISSION) {
                auth.signOut()
                profiles.clear()
                return false
            }
        }
        return true
    }
}
