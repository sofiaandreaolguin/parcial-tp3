package com.example.parcial_tp3.ui.screens.access

import androidx.lifecycle.ViewModel
import com.example.parcial_tp3.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Acceso de demostracion (CONSIGNA: Acceso es "Demostracion", Firebase es extra). Valida el
 * formulario y, si esta bien, deja entrar; no guarda nada.
 *
 * Vive en el grafo de acceso, no en cada pantalla: SignIn y SignUp comparten lo escrito.
 * El texto vive aca y no en un remember para que sobreviva a la rotacion (RF07).
 */
@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onFullNameChange(value: String) = _uiState.update { it.copy(fullName = value, nameError = null) }

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, emailError = null) }

    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, passwordError = null) }

    fun onTogglePasswordVisible() = _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }

    /** "Sign In →": email con forma valida y contrasena de 8 o mas. */
    fun signIn() = submit(requireName = false)

    /** "Sign Up →": lo mismo que SignIn, mas el nombre completo. */
    fun signUp() = submit(requireName = true)

    /** La Route ya navego a Home: se baja la marca para que volver a la pantalla no re-navegue. */
    fun onNavigated() = _uiState.update { it.copy(signedIn = false) }

    private fun submit(requireName: Boolean) = _uiState.update { typed ->
        // el autocompletado del teclado suele dejar un espacio al final: se guarda sin el
        val state = typed.copy(email = typed.email.trim())
        val nameError = if (requireName && state.fullName.isBlank()) R.string.access_error_name else null
        val emailError = if (AuthRules.isValidEmail(state.email)) null else R.string.access_error_email
        val passwordError = if (state.passwordLongEnough) null else R.string.access_error_password
        state.copy(
            nameError = nameError,
            emailError = emailError,
            passwordError = passwordError,
            signedIn = nameError == null && emailError == null && passwordError == null,
        )
    }
}
