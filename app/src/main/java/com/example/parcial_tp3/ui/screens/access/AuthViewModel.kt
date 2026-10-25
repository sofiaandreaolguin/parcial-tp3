package com.example.parcial_tp3.ui.screens.access

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.AuthDataException
import com.example.parcial_tp3.data.repository.AuthError
import com.example.parcial_tp3.data.repository.AuthRepository
import com.example.parcial_tp3.data.repository.ProfileDataException
import com.example.parcial_tp3.data.repository.ProfileError
import com.example.parcial_tp3.data.repository.ProfileRepository
import com.example.parcial_tp3.domain.model.Profile
import com.example.parcial_tp3.domain.splitFullName
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * Registro e inicio de sesion con Firebase (specs/funcionales/firebase-auth-perfil.md). Valida la
 * forma del formulario antes de llamar a los repositorios.
 *
 * Vive en el grafo de acceso, no en cada pantalla: SignIn y SignUp comparten lo escrito.
 * El texto vive aca y no en un remember para que sobreviva a la rotacion (RF07).
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val profiles: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onFullNameChange(value: String) =
        _uiState.update { it.copy(fullName = value, nameError = null, formError = null) }

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value, emailError = null, formError = null) }

    fun onPasswordChange(value: String) =
        _uiState.update { it.copy(password = value, passwordError = null, formError = null) }

    fun onTogglePasswordVisible() = _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }

    /** "Sign In →": email con forma valida y contrasena no vacia (C33). */
    fun signIn() = submit(signUp = false)

    /** "Sign Up →": nombre, email con forma valida y contrasena de 8 o mas (C4). */
    fun signUp() = submit(signUp = true)

    /**
     * Al pasar de SignIn a SignUp o al reves se borran los errores y queda lo escrito: si no,
     * el "Sign Up" fallido deja SignIn en rojo sin que ahi se haya tocado nada.
     */
    fun onSwitchScreen() =
        _uiState.update { it.copy(nameError = null, emailError = null, passwordError = null, formError = null) }

    /** La Route ya navego a Home: se baja la marca para que volver a la pantalla no re-navegue. */
    fun onNavigated() = _uiState.update { it.copy(signedIn = false) }

    private fun submit(signUp: Boolean) {
        val current = _uiState.value
        if (current.isLoading) return
        // el autocompletado del teclado suele dejar un espacio al final: se guarda sin el
        val email = current.email.trim()
        val nameError = if (signUp && current.fullName.isBlank()) R.string.access_error_name else null
        val emailError = if (AuthRules.isValidEmail(email)) null else R.string.access_error_email
        val passwordOk = if (signUp) current.passwordLongEnough else current.password.isNotEmpty()
        val passwordError = if (passwordOk) null else R.string.access_error_password
        if (nameError != null || emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    email = email,
                    nameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    formError = null,
                )
            }
            return
        }
        _uiState.update {
            it.copy(
                email = email,
                nameError = null,
                emailError = null,
                passwordError = null,
                formError = null,
                isLoading = true,
            )
        }
        viewModelScope.launch {
            try {
                withTimeout(REQUEST_TIMEOUT_MS) {
                    if (signUp) register(current.fullName, email, current.password) else login(email, current.password)
                }
                _uiState.update { it.copy(isLoading = false, signedIn = true) }
            } catch (e: TimeoutCancellationException) {
                fail(R.string.access_error_no_connection)
            } catch (e: CancellationException) {
                throw e
            } catch (e: AuthDataException) {
                fail(e.error.message())
            } catch (e: ProfileDataException) {
                fail(e.error.message())
            } catch (e: Exception) {
                fail(R.string.access_error_unknown)
            }
        }
    }

    private suspend fun register(fullName: String, email: String, password: String) {
        val uid = auth.signUp(email, password)
        val (firstName, lastName) = splitFullName(fullName)
        try {
            profiles.create(uid, Profile.default(firstName, lastName, email))
        } catch (e: Exception) {
            // sin documento no se entra: C23 lo crea la proxima vez que inicie sesion
            auth.signOut()
            throw e
        }
    }

    private suspend fun login(email: String, password: String) {
        val uid = auth.signIn(email, password)
        try {
            profiles.load(uid, email)
        } catch (e: Exception) {
            auth.signOut()
            profiles.clear()
            throw e
        }
    }

    private fun fail(@StringRes message: Int) =
        _uiState.update { it.copy(isLoading = false, signedIn = false, formError = message) }

    @StringRes
    private fun AuthError.message(): Int = when (this) {
        AuthError.INVALID_CREDENTIALS -> R.string.access_error_credentials
        AuthError.EMAIL_IN_USE -> R.string.access_error_email_in_use
        AuthError.WEAK_PASSWORD -> R.string.access_error_weak_password
        AuthError.TOO_MANY_REQUESTS -> R.string.access_error_too_many_requests
        AuthError.NO_CONNECTION -> R.string.access_error_no_connection
        AuthError.INVALID_EMAIL -> R.string.access_error_invalid_email
        AuthError.USER_DISABLED -> R.string.access_error_user_disabled
        AuthError.EMAIL_AUTH_DISABLED -> R.string.access_error_email_auth_disabled
        AuthError.UNKNOWN -> R.string.access_error_unknown
    }

    @StringRes
    private fun ProfileError.message(): Int = when (this) {
        ProfileError.NO_CONNECTION -> R.string.access_error_no_connection
        ProfileError.NO_PERMISSION -> R.string.access_error_no_permission
        ProfileError.INVALID_DATA, ProfileError.UNKNOWN -> R.string.access_error_unknown
    }

    private companion object {
        const val REQUEST_TIMEOUT_MS = 10_000L
    }
}
