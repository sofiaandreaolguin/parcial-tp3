package com.example.parcial_tp3.data.repository

import kotlinx.coroutines.CompletableDeferred

/** Cuentas en memoria. `fallo` hace fallar el proximo pedido; `compuerta` lo deja esperando. */
class FakeAuthRepository : AuthRepository {
    val cuentas = mutableMapOf<String, Pair<String, String>>() // email -> (password, uid)
    var fallo: Throwable? = null
    var compuerta: CompletableDeferred<Unit>? = null
    var cuelga = false

    var signUpCalls = 0
    var signInCalls = 0
    var signOutCalls = 0
    val emailsEnviados = mutableListOf<String>()

    override var currentUid: String? = null
    override var currentEmail: String? = null

    override suspend fun signUp(email: String, password: String): String {
        signUpCalls++
        emailsEnviados += email
        esperar()
        val uid = "uid-${cuentas.size + 1}"
        cuentas[email] = password to uid
        currentUid = uid
        currentEmail = email
        return uid
    }

    override suspend fun signIn(email: String, password: String): String {
        signInCalls++
        emailsEnviados += email
        esperar()
        val (clave, uid) = cuentas[email] ?: throw AuthDataException(AuthError.INVALID_CREDENTIALS)
        if (clave != password) throw AuthDataException(AuthError.INVALID_CREDENTIALS)
        currentUid = uid
        currentEmail = email
        return uid
    }

    override fun signOut() {
        signOutCalls++
        currentUid = null
        currentEmail = null
    }

    private suspend fun esperar() {
        if (cuelga) kotlinx.coroutines.awaitCancellation()
        compuerta?.await()
        fallo?.let { throw it }
    }
}
