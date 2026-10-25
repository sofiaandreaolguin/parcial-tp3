package com.example.parcial_tp3.data.repository

import org.junit.Assert.assertEquals
import org.junit.Test

class AuthErrorMappingTest {

    @Test
    fun `C43 email invalido, cuenta deshabilitada y acceso por email no habilitado tienen error propio`() {
        assertEquals(AuthError.INVALID_EMAIL, authErrorFromCode("ERROR_INVALID_EMAIL"))
        assertEquals(AuthError.USER_DISABLED, authErrorFromCode("ERROR_USER_DISABLED"))
        assertEquals(AuthError.EMAIL_AUTH_DISABLED, authErrorFromCode("ERROR_OPERATION_NOT_ALLOWED"))
    }
}
