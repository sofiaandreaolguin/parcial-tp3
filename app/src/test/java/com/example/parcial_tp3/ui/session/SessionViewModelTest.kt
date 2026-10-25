package com.example.parcial_tp3.ui.session

import com.example.parcial_tp3.data.repository.FakeAuthRepository
import com.example.parcial_tp3.data.repository.FakeProfileRemote
import com.example.parcial_tp3.data.repository.FakeProfileRepository
import com.example.parcial_tp3.data.repository.ProfileError
import com.example.parcial_tp3.data.repository.ProfileRepository
import com.example.parcial_tp3.data.repository.SessionStarter
import com.example.parcial_tp3.data.repository.toDto
import com.example.parcial_tp3.domain.model.Gender
import com.example.parcial_tp3.domain.model.Profile
import com.example.parcial_tp3.domain.model.ProfileEdit
import java.time.LocalDate
import com.example.parcial_tp3.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** El arranque con sesion vive en un ViewModel: rotar no repite la lectura del perfil (RF07, C13, C35). */
@OptIn(ExperimentalCoroutinesApi::class)
class SessionViewModelTest {

    @get:Rule
    val main = MainDispatcherRule()

    private val auth = FakeAuthRepository()
    private val perfiles = FakeProfileRepository().apply {
        docs["uid-ana"] = Profile.default("Ana", "López", "ana@mail.com")
    }

    private fun conSesion() {
        auth.currentUid = "uid-ana"
        auth.currentEmail = "ana@mail.com"
    }

    private fun vm(profiles: ProfileRepository = perfiles) =
        SessionViewModel(auth, SessionStarter(auth, profiles), profiles)

    @Test
    fun `sin sesion no hay nada que restaurar`() = runTest {
        val vm = vm()
        advanceUntilIdle()
        assertFalse(vm.hasSession)
        assertEquals(SessionState.Active, vm.state.value)
        assertTrue(perfiles.loads.isEmpty())
    }

    @Test
    fun `con sesion lee el perfil una sola vez aunque se vuelva a colectar`() = runTest {
        conSesion()
        val vm = vm()
        assertTrue(vm.hasSession)
        repeat(3) {
            val job = vm.state.onEach { }.launchIn(backgroundScope)
            advanceUntilIdle()
            job.cancel()
        }
        assertEquals(listOf("uid-ana" to "ana@mail.com"), perfiles.loads)
        assertEquals(SessionState.Active, vm.state.value)
    }

    @Test
    fun `C35 una sesion que ya no vale se cierra y pide ir a SignIn`() = runTest {
        conSesion()
        perfiles.falloLoad = ProfileError.NO_PERMISSION
        val vm = vm()
        advanceUntilIdle()
        assertEquals(SessionState.Invalid, vm.state.value)
        assertEquals(1, auth.signOutCalls)

        vm.onInvalidHandled()
        assertEquals(SessionState.Active, vm.state.value)
    }

    @Test
    fun `C35 un permiso denegado despues del arranque cierra la sesion`() = runTest {
        conSesion()
        val remote = FakeProfileRemote().apply { docs["uid-ana"] = Profile.default("Ana", "López", "ana@mail.com").toDto() }
        val repo = ProfileRepository(remote, backgroundScope)
        val vm = vm(repo)
        advanceUntilIdle()
        assertEquals(SessionState.Active, vm.state.value)

        repo.saveEdit(ProfileEdit("Anita", "Pérez", LocalDate.of(1990, 5, 17), 181, 64, Gender.FEMALE))
        remote.rechazarUpdate(ProfileError.NO_PERMISSION)
        advanceUntilIdle()
        assertEquals(SessionState.Invalid, vm.state.value)
        assertEquals(1, auth.signOutCalls)
        assertEquals("", repo.profile.value.email)
    }
}
