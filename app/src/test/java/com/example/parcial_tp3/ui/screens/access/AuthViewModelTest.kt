package com.example.parcial_tp3.ui.screens.access

import com.example.parcial_tp3.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * AuthViewModel (Login - 01 y 02): la validacion del acceso de demostracion y el formulario
 * compartido entre SignIn y SignUp. El estado vive en un MutableStateFlow, asi que no hace falta
 * avanzar corrutinas.
 */
class AuthViewModelTest {

    private fun conDatos(
        nombre: String = "",
        email: String = "alex@morgan.com",
        password: String = "secreto123",
    ) = AuthViewModel().apply {
        onFullNameChange(nombre)
        onEmailChange(email)
        onPasswordChange(password)
    }

    private val AuthViewModel.estado get() = uiState.value

    @Test
    fun `sign in con email y contrasena validos entra`() {
        val vm = conDatos()
        vm.signIn()
        assertTrue(vm.estado.signedIn)
        assertNull(vm.estado.emailError)
        assertNull(vm.estado.passwordError)
    }

    @Test
    fun `sign in con email sin arroba no entra y marca el email`() {
        val vm = conDatos(email = "alex.morgan.com")
        vm.signIn()
        assertFalse(vm.estado.signedIn)
        assertEquals(R.string.access_error_email, vm.estado.emailError)
    }

    @Test
    fun `sign in con contrasena de 7 no entra y con 8 si`() {
        val corta = conDatos(password = "1234567").apply { signIn() }
        assertFalse(corta.estado.signedIn)
        assertEquals(R.string.access_error_password, corta.estado.passwordError)

        val justa = conDatos(password = "12345678").apply { signIn() }
        assertTrue(justa.estado.signedIn)
    }

    @Test
    fun `sign in vacio marca email y contrasena a la vez`() {
        val vm = AuthViewModel()
        vm.signIn()
        assertEquals(R.string.access_error_email, vm.estado.emailError)
        assertEquals(R.string.access_error_password, vm.estado.passwordError)
        assertFalse(vm.estado.signedIn)
    }

    @Test
    fun `sign in no pide nombre`() {
        val vm = conDatos(nombre = "")
        vm.signIn()
        assertNull(vm.estado.nameError)
        assertTrue(vm.estado.signedIn)
    }

    @Test
    fun `el email se guarda sin los espacios del autocompletado`() {
        val vm = conDatos(email = "  alex@morgan.com ")
        vm.signIn()
        assertTrue(vm.estado.signedIn)
        assertEquals("alex@morgan.com", vm.estado.email)
    }

    @Test
    fun `sign up con nombre vacio no entra y marca el nombre`() {
        val vm = conDatos(nombre = "   ")
        vm.signUp()
        assertFalse(vm.estado.signedIn)
        assertEquals(R.string.access_error_name, vm.estado.nameError)
    }

    @Test
    fun `sign up con nombre email y contrasena validos entra`() {
        val vm = conDatos(nombre = "Martina")
        vm.signUp()
        assertTrue(vm.estado.signedIn)
    }

    @Test
    fun `escribir en un campo borra solo su error`() {
        val vm = AuthViewModel()
        vm.signUp()
        vm.onEmailChange("a")
        assertNull(vm.estado.emailError)
        assertEquals(R.string.access_error_password, vm.estado.passwordError)
        assertEquals(R.string.access_error_name, vm.estado.nameError)
    }

    @Test
    fun `pasar de pantalla borra los errores y conserva lo escrito`() {
        val vm = conDatos(nombre = "", email = "mal", password = "123")
        vm.signUp()
        vm.onSwitchScreen()
        assertNull(vm.estado.nameError)
        assertNull(vm.estado.emailError)
        assertNull(vm.estado.passwordError)
        assertEquals("mal", vm.estado.email)
        assertEquals("123", vm.estado.password)
    }

    @Test
    fun `on navigated baja la marca de entrar`() {
        val vm = conDatos()
        vm.signIn()
        vm.onNavigated()
        assertFalse(vm.estado.signedIn)
    }

    @Test
    fun `el tilde de 8 caracteres se prende con 8`() {
        assertFalse(conDatos(password = "1234567").estado.passwordLongEnough)
        assertTrue(conDatos(password = "12345678").estado.passwordLongEnough)
    }

    @Test
    fun `mostrar contrasena alterna`() {
        val vm = AuthViewModel()
        vm.onTogglePasswordVisible()
        assertTrue(vm.estado.passwordVisible)
        vm.onTogglePasswordVisible()
        assertFalse(vm.estado.passwordVisible)
    }
}
