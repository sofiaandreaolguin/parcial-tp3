package com.example.parcial_tp3.ui.screens.access

import com.example.parcial_tp3.data.repository.AuthDataException
import com.example.parcial_tp3.data.repository.AuthError
import com.example.parcial_tp3.data.repository.FakeAuthRepository
import com.example.parcial_tp3.data.repository.FakeProfileRepository
import com.example.parcial_tp3.data.repository.ProfileError
import com.example.parcial_tp3.domain.model.Profile
import com.example.parcial_tp3.util.MainDispatcherRule
import com.example.parcial_tp3.util.esTextoDeStrings
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** AuthViewModel con Firebase (specs/funcionales/firebase-auth-perfil.md), con repositorios falsos. */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthFirebaseViewModelTest {

    @get:Rule
    val main = MainDispatcherRule()

    private val auth = FakeAuthRepository()
    private val perfiles = FakeProfileRepository()
    private fun vm() = AuthViewModel(auth, perfiles)

    private fun AuthViewModel.escribir(
        nombre: String = "",
        email: String = "ana@mail.com",
        password: String = "secreto123",
    ) {
        onFullNameChange(nombre)
        onEmailChange(email)
        onPasswordChange(password)
    }

    private val AuthViewModel.estado get() = uiState.value

    private fun conCuenta(email: String = "ana@mail.com", password: String = "secreto123", uid: String = "uid-ana") {
        auth.cuentas[email] = password to uid
        perfiles.docs[uid] = Profile.default("Ana", "López", email)
    }

    // ---- Registro

    @Test
    fun `C1 registro valido crea la cuenta y entra a Home`() = runTest {
        val vm = vm().apply { escribir(nombre = "Ana López") }
        vm.signUp()
        advanceUntilIdle()
        assertEquals(1, auth.signUpCalls)
        assertEquals(listOf("ana@mail.com"), auth.emailsEnviados)
        assertTrue(vm.estado.signedIn)
        assertFalse(vm.estado.isLoading)
        assertNull(vm.estado.formError)
    }

    @Test
    fun `C2 registro crea el documento con Profile default y nombre y email del formulario`() = runTest {
        val vm = vm().apply { escribir(nombre = "  Ana María   López ", email = " ana@mail.com ") }
        vm.signUp()
        advanceUntilIdle()
        assertEquals(
            listOf("uid-1" to Profile.default("Ana", "María López", "ana@mail.com")),
            perfiles.creados,
        )
    }

    @Test
    fun `C4 formulario de registro invalido no llama al repositorio`() = runTest {
        val casos = listOf(
            Triple("", "ana@mail.com", "secreto123"),
            Triple("   ", "ana@mail.com", "secreto123"),
            Triple("Ana", "ana.mail.com", "secreto123"),
            Triple("Ana", "ana@mail.com", "1234567"),
        )
        casos.forEach { (nombre, email, clave) ->
            val vm = vm().apply { escribir(nombre, email, clave) }
            vm.signUp()
            advanceUntilIdle()
            assertFalse("$nombre/$email/$clave", vm.estado.signedIn)
            assertTrue(
                vm.estado.nameError != null || vm.estado.emailError != null || vm.estado.passwordError != null,
            )
        }
        assertEquals(0, auth.signUpCalls)
        assertTrue(perfiles.creados.isEmpty())
    }

    @Test
    fun `C5 email ya registrado muestra el error`() = runTest {
        auth.fallo = AuthDataException(AuthError.EMAIL_IN_USE)
        val vm = vm().apply { escribir(nombre = "Ana") }
        vm.signUp()
        advanceUntilIdle()
        assertFalse(vm.estado.signedIn)
        assertTrue(esTextoDeStrings(vm.estado.formError))
        assertTrue(perfiles.creados.isEmpty())
    }

    @Test
    fun `C6 contrasena debil muestra el error`() = runTest {
        auth.fallo = AuthDataException(AuthError.WEAK_PASSWORD)
        val vm = vm().apply { escribir(nombre = "Ana") }
        vm.signUp()
        advanceUntilIdle()
        assertFalse(vm.estado.signedIn)
        assertTrue(esTextoDeStrings(vm.estado.formError))

        auth.fallo = AuthDataException(AuthError.EMAIL_IN_USE)
        val otro = vm().apply { escribir(nombre = "Ana") }
        otro.signUp()
        advanceUntilIdle()
        assertNotEquals("debil y repetido no pueden decir lo mismo", otro.estado.formError, vm.estado.formError)
    }

    @Test
    fun `C7 falla el documento, no entra a Home y puede iniciar sesion despues`() = runTest {
        perfiles.falloCreate = ProfileError.NO_CONNECTION
        val vm = vm().apply { escribir(nombre = "Ana") }
        vm.signUp()
        advanceUntilIdle()
        assertEquals(1, auth.signUpCalls)
        assertTrue(esTextoDeStrings(vm.estado.formError))
        assertFalse(vm.estado.signedIn)

        perfiles.falloCreate = null
        vm.signIn()
        advanceUntilIdle()
        assertEquals(1, auth.signInCalls)
        assertTrue(vm.estado.signedIn)
        assertEquals("uid-1", perfiles.loads.single().first)
    }

    // ---- Inicio de sesion

    @Test
    fun `C8 inicio valido entra a Home`() = runTest {
        conCuenta()
        val vm = vm().apply { escribir() }
        vm.signIn()
        advanceUntilIdle()
        assertEquals(1, auth.signInCalls)
        assertTrue(vm.estado.signedIn)
        assertNull(vm.estado.formError)
    }

    @Test
    fun `C9 credenciales invalidas dan un unico error`() = runTest {
        conCuenta()
        val mal = vm().apply { escribir(password = "otra-clave-1") }
        mal.signIn()
        advanceUntilIdle()
        val inexistente = vm().apply { escribir(email = "nadie@mail.com") }
        inexistente.signIn()
        advanceUntilIdle()

        assertEquals(2, auth.signInCalls)
        assertFalse(mal.estado.signedIn)
        assertTrue(esTextoDeStrings(mal.estado.formError))
        assertEquals(mal.estado.formError, inexistente.estado.formError)
        assertNull(mal.estado.emailError)
        assertNull(mal.estado.passwordError)
    }

    @Test
    fun `C10 sin conexion o timeout muestra el error y conserva lo tipeado`() = runTest {
        auth.fallo = AuthDataException(AuthError.NO_CONNECTION)
        val sinRed = vm().apply { escribir(nombre = "Ana") }
        sinRed.signIn()
        advanceUntilIdle()
        assertTrue(esTextoDeStrings(sinRed.estado.formError))
        assertFalse(sinRed.estado.signedIn)
        assertEquals("ana@mail.com", sinRed.estado.email)
        assertEquals("secreto123", sinRed.estado.password)
        assertEquals("Ana", sinRed.estado.fullName)
        val errorSinRed = sinRed.estado.formError

        auth.fallo = null
        auth.cuelga = true
        val colgado = vm().apply { escribir(nombre = "Ana") }
        colgado.signUp()
        runCurrent()
        assertTrue(colgado.estado.isLoading)
        advanceTimeBy(9_000)
        assertTrue("antes de 10 s sigue cargando", colgado.estado.isLoading)
        advanceTimeBy(1_500)
        runCurrent()
        assertFalse(colgado.estado.isLoading)
        assertEquals(errorSinRed, colgado.estado.formError)
        assertEquals("ana@mail.com", colgado.estado.email)
        assertEquals("secreto123", colgado.estado.password)
    }

    @Test
    fun `C11 un segundo toque durante la carga no envia otro pedido`() = runTest {
        conCuenta()
        auth.compuerta = CompletableDeferred()
        val vm = vm().apply { escribir() }
        vm.signIn()
        runCurrent()
        assertTrue(vm.estado.isLoading)
        vm.signIn()
        vm.signUp()
        runCurrent()
        assertEquals(1, auth.signInCalls)
        assertEquals(0, auth.signUpCalls)

        auth.compuerta!!.complete(Unit)
        advanceUntilIdle()
        assertEquals(1, auth.signInCalls)
        assertFalse(vm.estado.isLoading)
        assertTrue(vm.estado.signedIn)
    }

    @Test
    fun `C12 error desconocido muestra mensaje generico`() = runTest {
        auth.fallo = AuthDataException(AuthError.UNKNOWN)
        val a = vm().apply { escribir() }
        a.signIn()
        advanceUntilIdle()
        assertTrue(esTextoDeStrings(a.estado.formError))
        assertFalse(a.estado.signedIn)

        auth.fallo = IllegalStateException("INTERNAL ASSERTION FAILED: detalle crudo")
        val b = vm().apply { escribir() }
        b.signIn()
        advanceUntilIdle()
        assertTrue(esTextoDeStrings(b.estado.formError))
        assertFalse(b.estado.isLoading)
        assertFalse(b.estado.signedIn)
    }

    @Test
    fun `C31 cada error se muestra con un mensaje de strings distinto`() = runTest {
        val mensajes = AuthError.entries.map { error ->
            auth.fallo = AuthDataException(error)
            val vm = vm().apply { escribir() }
            vm.signIn()
            advanceUntilIdle()
            assertTrue("$error", esTextoDeStrings(vm.estado.formError))
            vm.estado.formError
        }
        assertEquals(AuthError.entries.size, mensajes.toSet().size)
    }

    @Test
    fun `C33 signin con email invalido o contrasena vacia no llama a Firebase, contrasena corta si`() = runTest {
        conCuenta(password = "corta")
        val emailMalo = vm().apply { escribir(email = "ana.mail.com") }
        emailMalo.signIn()
        advanceUntilIdle()
        assertEquals(0, auth.signInCalls)
        assertTrue(emailMalo.estado.emailError != null)

        val sinClave = vm().apply { escribir(password = "") }
        sinClave.signIn()
        advanceUntilIdle()
        assertEquals(0, auth.signInCalls)
        assertTrue(sinClave.estado.passwordError != null)

        val corta = vm().apply { escribir(password = "corta") }
        corta.signIn()
        advanceUntilIdle()
        assertEquals(1, auth.signInCalls)
        assertNull(corta.estado.passwordError)
        assertTrue(corta.estado.signedIn)
    }

    @Test
    fun `C34 demasiados intentos muestra su error propio`() = runTest {
        auth.fallo = AuthDataException(AuthError.INVALID_CREDENTIALS)
        val credenciales = vm().apply { escribir() }
        credenciales.signIn()
        advanceUntilIdle()

        auth.fallo = AuthDataException(AuthError.TOO_MANY_REQUESTS)
        val muchos = vm().apply { escribir() }
        muchos.signIn()
        advanceUntilIdle()

        assertTrue(esTextoDeStrings(muchos.estado.formError))
        assertNotEquals(credenciales.estado.formError, muchos.estado.formError)
        assertNull(muchos.estado.passwordError)
        assertFalse(muchos.estado.signedIn)
    }

    // ---- Perfil al iniciar sesion

    @Test
    fun `C18 al iniciar sesion y en arranque en frio se lee el perfil de la cuenta`() = runTest {
        conCuenta()
        vm().apply { escribir() }.also {
            it.signIn()
            advanceUntilIdle()
        }
        assertEquals(listOf("uid-ana" to "ana@mail.com"), perfiles.loads)
        assertEquals("Ana", perfiles.profile.value.firstName)

        val frio = FakeProfileRepository().apply { docs["uid-ana"] = Profile.default("Ana", "López", "ana@mail.com") }
        auth.currentUid = "uid-ana"
        auth.currentEmail = "ana@mail.com"
        com.example.parcial_tp3.data.repository.SessionStarter(auth, frio).restore()
        assertEquals(listOf("uid-ana" to "ana@mail.com"), frio.loads)
        assertEquals("Ana", frio.profile.value.firstName)
    }

    @Test
    fun `C20 un perfil guardado se recupera al volver a iniciar sesion`() = runTest {
        conCuenta()
        val primera = vm().apply { escribir() }
        primera.signIn()
        advanceUntilIdle()
        perfiles.docs["uid-ana"] = perfiles.docs.getValue("uid-ana").copy(firstName = "Anita", heightCm = 181)
        auth.signOut()
        perfiles.clear()

        val segunda = vm().apply { escribir() }
        segunda.signIn()
        advanceUntilIdle()
        assertEquals("Anita", perfiles.profile.value.firstName)
        assertEquals(181, perfiles.profile.value.heightCm)
        assertEquals(2, perfiles.loads.size)
    }

    @Test
    fun `C35 sin permiso o sesion invalida cierra la sesion`() = runTest {
        conCuenta()
        perfiles.falloLoad = ProfileError.NO_PERMISSION
        val vm = vm().apply { escribir() }
        vm.signIn()
        advanceUntilIdle()
        assertEquals(1, auth.signOutCalls)
        assertFalse(vm.estado.signedIn)
        assertTrue(esTextoDeStrings(vm.estado.formError))

        auth.currentUid = "uid-ana"
        auth.currentEmail = "ana@mail.com"
        com.example.parcial_tp3.data.repository.SessionStarter(auth, perfiles).restore()
        assertEquals(2, auth.signOutCalls)
    }

    @Test
    fun `C17 otra cuenta no ve datos de perfil de la anterior`() = runTest {
        conCuenta("ana@mail.com", "secreto123", "uid-ana")
        conCuenta("beto@mail.com", "clave-beto-1", "uid-beto")
        perfiles.docs["uid-beto"] = Profile.default("Beto", "Pérez", "beto@mail.com")

        vm().apply { escribir() }.signIn()
        advanceUntilIdle()
        assertEquals("ana@mail.com", perfiles.profile.value.email)

        com.example.parcial_tp3.ui.screens.profile.SettingsViewModel(perfiles, auth).logOut()
        advanceUntilIdle()
        assertEquals("", perfiles.profile.value.email)
        assertEquals("", perfiles.profile.value.firstName)

        vm().apply { escribir(email = "beto@mail.com", password = "clave-beto-1") }.signIn()
        advanceUntilIdle()
        assertEquals("beto@mail.com", perfiles.profile.value.email)
        assertEquals("Beto", perfiles.profile.value.firstName)
        assertNotEquals("Ana", perfiles.profile.value.firstName)
    }

    // ---- Rotacion

    @Test
    fun `C25 el ViewModel no repite el pedido al recrearse`() = runTest {
        auth.fallo = AuthDataException(AuthError.NO_CONNECTION)
        val vm = vm().apply { escribir(nombre = "Ana") }
        vm.signUp()
        advanceUntilIdle()
        assertEquals(1, auth.signUpCalls)

        // la rotacion deja de colectar el estado y vuelve a colectarlo
        repeat(3) {
            val job = vm.uiState.onEach { }.launchIn(backgroundScope)
            advanceUntilIdle()
            job.cancel()
        }
        assertEquals(1, auth.signUpCalls)
        assertEquals(0, auth.signInCalls)
        assertEquals("Ana", vm.estado.fullName)
        assertEquals("ana@mail.com", vm.estado.email)
        assertEquals("secreto123", vm.estado.password)
        assertTrue(esTextoDeStrings(vm.estado.formError))
    }
}
