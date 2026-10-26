package com.example.parcial_tp3.data.repository

/** Cuenta de Firebase Authentication. Sin tipos de Firebase en la API (C32). */
interface AuthRepository {
    /** Sesion activa leida de forma sincrona (C13); null si no hay. */
    val currentUid: String?
    val currentEmail: String?

    /** Crea la cuenta y deja la sesion abierta. Devuelve el uid. */
    suspend fun signUp(email: String, password: String): String

    /** Devuelve el uid. */
    suspend fun signIn(email: String, password: String): String

    fun signOut()
}
