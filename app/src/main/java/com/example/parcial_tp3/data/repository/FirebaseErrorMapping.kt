package com.example.parcial_tp3.data.repository

/** Codigo de `FirebaseAuthException.errorCode` (String, sin tipos de Firebase) a error propio (C31). */
fun authErrorFromCode(code: String?): AuthError = when (code) {
    "ERROR_WRONG_PASSWORD", "ERROR_USER_NOT_FOUND", "ERROR_INVALID_CREDENTIAL" -> AuthError.INVALID_CREDENTIALS
    "ERROR_EMAIL_ALREADY_IN_USE" -> AuthError.EMAIL_IN_USE
    "ERROR_WEAK_PASSWORD" -> AuthError.WEAK_PASSWORD
    "ERROR_TOO_MANY_REQUESTS" -> AuthError.TOO_MANY_REQUESTS
    "ERROR_NETWORK_REQUEST_FAILED" -> AuthError.NO_CONNECTION
    else -> AuthError.UNKNOWN
}

/** Codigo de `FirebaseFirestoreException.Code.name` a error propio (C31, C35). */
fun profileErrorFromCode(code: String?): ProfileError = when (code) {
    "PERMISSION_DENIED", "UNAUTHENTICATED" -> ProfileError.NO_PERMISSION
    "UNAVAILABLE", "DEADLINE_EXCEEDED" -> ProfileError.NO_CONNECTION
    else -> ProfileError.UNKNOWN
}
