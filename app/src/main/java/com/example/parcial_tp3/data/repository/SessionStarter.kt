package com.example.parcial_tp3.data.repository

import javax.inject.Inject

/** Arranque en frio con sesion activa: lee el perfil de la cuenta (C18, C24, C35). */
class SessionStarter @Inject constructor(
    private val auth: AuthRepository,
    private val profiles: ProfileRepository,
) {
    suspend fun restore() {}
}
