package com.example.parcial_tp3.ui.screens.profile

import com.example.parcial_tp3.data.repository.AuthError
import com.example.parcial_tp3.data.repository.FakeAuthRepository
import com.example.parcial_tp3.data.repository.FakeProfileRepository
import com.example.parcial_tp3.data.repository.ProfileError
import com.example.parcial_tp3.domain.model.Gender
import com.example.parcial_tp3.domain.model.Goal
import com.example.parcial_tp3.domain.model.Profile
import com.example.parcial_tp3.domain.model.ProfileEdit
import com.example.parcial_tp3.util.MainDispatcherRule
import com.example.parcial_tp3.util.esTextoDeStrings
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfileViewModelTest {

    @get:Rule
    val main = MainDispatcherRule()

    private val perfiles = FakeProfileRepository().apply {
        docs["uid-ana"] = Profile.default("Ana", "López", "ana@mail.com").copy(goals = setOf(Goal.BUILD_STRENGTH))
    }
    private val auth = FakeAuthRepository()

    private suspend fun vmListo(): EditProfileViewModel {
        perfiles.load("uid-ana", "ana@mail.com")
        return EditProfileViewModel(perfiles)
    }

    private fun EditProfileViewModel.completar() {
        onFirstNameChange("Anita")
        onLastNameChange("Pérez")
        onBirthDateChange(LocalDate.of(1990, 5, 17))
        onHeightChange(181)
        onWeightChange(64)
        onGenderChange(Gender.FEMALE)
    }

    private val EditProfileViewModel.estado get() = uiState.value

    @Test
    fun `C19 guardar perfil escribe solo los 6 campos y vuelve`() = runTest {
        val vm = vmListo()
        vm.uiState.onEach { }.launchIn(backgroundScope)
        vm.completar()
        vm.save()
        advanceUntilIdle()

        val esperado = ProfileEdit("Anita", "Pérez", LocalDate.of(1990, 5, 17), 181, 64, Gender.FEMALE)
        assertEquals(listOf(esperado), perfiles.ediciones)
        assertTrue(vm.estado.saved)
        assertNull(vm.estado.firstNameError)
        // el perfil que se ve al volver ya trae los datos nuevos (cache local)
        assertEquals("Anita", perfiles.profile.value.firstName)
        // nada se pidio al servidor ni se reemplazo el documento
        assertTrue(perfiles.creados.isEmpty())
    }

    @Test
    fun `C21 datos invalidos no se guardan y muestran error en el campo`() = runTest {
        val vm = vmListo()
        vm.uiState.onEach { }.launchIn(backgroundScope)

        vm.completar(); vm.onFirstNameChange("   "); vm.save(); advanceUntilIdle()
        assertTrue(esTextoDeStrings(vm.estado.firstNameError))

        vm.completar(); vm.onHeightChange(0); vm.save(); advanceUntilIdle()
        assertTrue(esTextoDeStrings(vm.estado.heightError))
        vm.onHeightChange(-3); vm.save(); advanceUntilIdle()
        assertTrue(esTextoDeStrings(vm.estado.heightError))

        vm.completar(); vm.onWeightChange(0); vm.save(); advanceUntilIdle()
        assertTrue(esTextoDeStrings(vm.estado.weightError))

        vm.completar(); vm.onBirthDateChange(LocalDate.now().plusDays(1)); vm.save(); advanceUntilIdle()
        assertTrue(esTextoDeStrings(vm.estado.birthDateError))

        assertTrue("no se escribio nada", perfiles.ediciones.isEmpty())
        assertFalse(vm.estado.saved)

        // los limites validos: 1 cm, 1 kg y nacer hoy
        vm.completar(); vm.onHeightChange(1); vm.onWeightChange(1); vm.onBirthDateChange(LocalDate.now())
        vm.save(); advanceUntilIdle()
        assertEquals(1, perfiles.ediciones.size)
    }

    @Test
    fun `C22 una escritura rechazada vuelve a mostrar el perfil del servidor`() = runTest {
        val vm = vmListo()
        vm.uiState.onEach { }.launchIn(backgroundScope)
        val dashboard = ProfileDashboardViewModel(perfiles)
        dashboard.uiState.onEach { }.launchIn(backgroundScope)

        vm.completar()
        vm.save()
        advanceUntilIdle()
        assertEquals("Anita Pérez", dashboard.uiState.value.fullName)

        perfiles.rechazarUltimaEscritura()
        advanceUntilIdle()
        assertEquals("Ana López", dashboard.uiState.value.fullName)
        assertEquals(listOf(Goal.BUILD_STRENGTH), dashboard.uiState.value.goals)
    }

    @Test
    fun `C37 segundo toque en Save changes no envia otro pedido`() = runTest {
        val vm = vmListo()
        vm.uiState.onEach { }.launchIn(backgroundScope)
        vm.completar()
        vm.save()
        vm.save()
        advanceUntilIdle()
        vm.save()
        advanceUntilIdle()
        assertEquals(1, perfiles.ediciones.size)
    }

    @Test
    fun `C25 el ViewModel no repite el pedido al recrearse`() = runTest {
        val vm = vmListo()
        vm.completar()
        repeat(3) {
            val job = vm.uiState.onEach { }.launchIn(backgroundScope)
            advanceUntilIdle()
            job.cancel()
        }
        assertEquals("Anita", vm.estado.firstName)
        assertEquals(181, vm.estado.heightCm)
        assertEquals(Gender.FEMALE, vm.estado.gender)
        assertTrue(perfiles.ediciones.isEmpty())
        assertEquals(0, perfiles.reloadCalls)
    }

    // ---- Settings y Profile / 01

    @Test
    fun `C15 log out cierra la sesion, vacia el perfil y navega a SignIn`() = runTest {
        auth.currentUid = "uid-ana"
        perfiles.load("uid-ana", "ana@mail.com")
        val vm = SettingsViewModel(perfiles, auth)
        vm.uiState.onEach { }.launchIn(backgroundScope)

        vm.logOut()
        advanceUntilIdle()

        assertEquals(1, auth.signOutCalls)
        assertNull(auth.currentUid)
        assertEquals(1, perfiles.clearCalls)
        assertTrue(vm.uiState.value.loggedOut)
    }

    @Test
    fun `C24 lectura sin red y sin cache da error con reintento, con cache la muestra`() = runTest {
        val sinCache = FakeProfileRepository().apply { ponerError(ProfileError.NO_CONNECTION) }
        val vm = ProfileDashboardViewModel(sinCache)
        vm.uiState.onEach { }.launchIn(backgroundScope)
        advanceUntilIdle()
        assertTrue(esTextoDeStrings(vm.uiState.value.loadError))
        assertEquals("", vm.uiState.value.fullName.trim())
        vm.onRetry()
        advanceUntilIdle()
        assertEquals(1, sinCache.reloadCalls)

        val conCache = FakeProfileRepository().apply {
            docs["uid-ana"] = Profile.default("Ana", "López", "ana@mail.com")
            load("uid-ana", "ana@mail.com")
        }
        val conDatos = ProfileDashboardViewModel(conCache)
        conDatos.uiState.onEach { }.launchIn(backgroundScope)
        advanceUntilIdle()
        assertNull(conDatos.uiState.value.loadError)
        assertEquals("Ana López", conDatos.uiState.value.fullName)
    }

    @Test
    fun `sin perfil cargado no se da por guardado y muestra un error`() = runTest {
        val sinPerfil = FakeProfileRepository().apply { ponerError(ProfileError.NO_CONNECTION) }
        val vm = EditProfileViewModel(sinPerfil)
        vm.uiState.onEach { }.launchIn(backgroundScope)
        vm.completar()
        vm.save()
        advanceUntilIdle()

        assertFalse(vm.estado.saved)
        assertTrue(esTextoDeStrings(vm.estado.saveError))
        assertTrue(sinPerfil.ediciones.isEmpty())
    }

    @Test
    fun `C19 elegir solo fecha y genero los guarda y deja el resto del formulario`() = runTest {
        val vm = vmListo()
        vm.uiState.onEach { }.launchIn(backgroundScope)

        vm.onBirthDateChange(LocalDate.of(1988, 2, 29))
        vm.onGenderChange(Gender.NON_BINARY)
        assertEquals(LocalDate.of(1988, 2, 29), vm.estado.birthDate)
        assertEquals(Gender.NON_BINARY, vm.estado.gender)
        vm.save(); advanceUntilIdle()

        val guardado = perfiles.ediciones.single()
        assertEquals(LocalDate.of(1988, 2, 29), guardado.birthDate)
        assertEquals(Gender.NON_BINARY, guardado.gender)
        assertEquals("Ana", guardado.firstName)
        assertEquals(Gender.NON_BINARY, perfiles.profile.value.gender)
    }

    @Test
    fun `C21 una fecha valida elegida despues de una futura limpia el error`() = runTest {
        val vm = vmListo()
        vm.uiState.onEach { }.launchIn(backgroundScope)

        vm.onBirthDateChange(LocalDate.now().plusDays(1)); vm.save(); advanceUntilIdle()
        assertTrue(esTextoDeStrings(vm.estado.birthDateError))

        vm.onBirthDateChange(LocalDate.of(1995, 3, 14))
        assertNull(vm.estado.birthDateError)
        vm.save(); advanceUntilIdle()
        assertEquals(LocalDate.of(1995, 3, 14), perfiles.ediciones.single().birthDate)
    }
}
