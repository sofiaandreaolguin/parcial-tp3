package com.example.parcial_tp3.data.repository

/** Codigo de `FirebaseAuthException.errorCode` (String, sin tipos de Firebase) a error propio (C31). */
fun authErrorFromCode(code: String?): AuthError = AuthError.UNKNOWN

/** Codigo de `FirebaseFirestoreException.Code.name` a error propio (C31, C35). */
fun profileErrorFromCode(code: String?): ProfileError = ProfileError.UNKNOWN
