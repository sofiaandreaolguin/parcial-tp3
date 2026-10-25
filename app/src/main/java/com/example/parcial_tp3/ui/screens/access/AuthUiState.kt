package com.example.parcial_tp3.ui.screens.access

import androidx.annotation.StringRes

/**
 * El formulario de Login - 01 y 02. Lo comparten las dos pantallas (specs/PANTALLAS.md,
 * controlador AuthViewModel): el email que se escribe en SignIn sigue ahi al pasar a SignUp.
 *
 * Los errores son ids de strings.xml: el ViewModel decide cual, la pantalla lo traduce.
 * Quedan en null hasta que se toca "Sign In" o "Sign Up", para no marcar en rojo un campo
 * que todavia se esta escribiendo.
 */
data class AuthUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    @StringRes val nameError: Int? = null,
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    /** El formulario paso la validacion: la Route navega a Home y avisa con onNavigated(). */
    val signedIn: Boolean = false,
    /** Pedido a Firebase en curso: el boton muestra la carga y no acepta otro toque (C11). */
    val isLoading: Boolean = false,
    /** Error del pedido (credenciales, email repetido, sin conexion...), id de strings.xml (C5 a C12, C34). */
    @StringRes val formError: Int? = null,
) {
    /** "8+ secure characters" de SignUp: se prende cuando la contrasena ya alcanza. */
    val passwordLongEnough: Boolean get() = password.length >= AuthRules.MIN_PASSWORD
}

/** Las reglas del acceso de demostracion. Sin Firebase no hay cuentas: solo se valida la forma. */
object AuthRules {
    const val MIN_PASSWORD = 8
    private val EMAIL = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

    fun isValidEmail(email: String): Boolean = EMAIL.matches(email)
}
