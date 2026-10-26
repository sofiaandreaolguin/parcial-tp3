package com.example.parcial_tp3.data.repository

class AuthDataException(val error: AuthError, cause: Throwable? = null) : Exception(cause)

enum class AuthError { INVALID_CREDENTIALS, EMAIL_IN_USE, WEAK_PASSWORD, TOO_MANY_REQUESTS, NO_CONNECTION,
    INVALID_EMAIL, USER_DISABLED, EMAIL_AUTH_DISABLED, UNKNOWN }

class ProfileDataException(val error: ProfileError, cause: Throwable? = null) : Exception(cause)

enum class ProfileError { NO_CONNECTION, NO_PERMISSION, INVALID_DATA, UNKNOWN }
