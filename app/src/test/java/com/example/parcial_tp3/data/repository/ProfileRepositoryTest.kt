package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.remote.dto.ProfileDto
import com.example.parcial_tp3.domain.model.Gender
import com.example.parcial_tp3.domain.model.Goal
import com.example.parcial_tp3.domain.model.Profile
import com.example.parcial_tp3.domain.model.ProfileEdit
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

/** ProfileRepository real con un ProfileRemote falso (C22, C35 y el observador de load). */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileRepositoryTest {

    private val remote = FakeProfileRemote()
    private val ana = Profile.default("Ana", "López", "ana@mail.com").toDto()

    private val edicion = ProfileEdit("Anita", "Pérez", LocalDate.of(1990, 5, 17), 181, 64, Gender.FEMALE)

    @Test
    fun `C22 un rechazo del servidor vuelve a mostrar el perfil del servidor`() = runTest {
        remote.docs["uid-ana"] = ana
        val repo = ProfileRepository(remote, backgroundScope)
        repo.load("uid-ana", "ana@mail.com")
        runCurrent()

        repo.saveEdit(edicion)
        assertEquals("Anita", repo.profile.value.firstName)

        remote.cambios.emit(ana)
        runCurrent()
        assertEquals("Ana", repo.profile.value.firstName)
    }

    @Test
    fun `C35 un permiso denegado del listener cierra la sesion`() = runTest {
        remote.docs["uid-ana"] = ana
        remote.falloObserve = ProfileDataException(ProfileError.NO_PERMISSION)
        val repo = ProfileRepository(remote, backgroundScope)
        var vencidas = 0
        repo.sessionExpired.onEach { vencidas++ }.launchIn(backgroundScope)
        runCurrent()

        repo.load("uid-ana", "ana@mail.com")
        runCurrent()
        assertEquals(1, vencidas)
    }

    @Test
    fun `C35 un permiso denegado en la escritura cierra la sesion`() = runTest {
        remote.docs["uid-ana"] = ana
        val repo = ProfileRepository(remote, backgroundScope)
        var vencidas = 0
        repo.sessionExpired.onEach { vencidas++ }.launchIn(backgroundScope)
        repo.load("uid-ana", "ana@mail.com")
        runCurrent()

        repo.saveEdit(edicion)
        remote.rechazarUpdate(ProfileError.NO_PERMISSION)
        runCurrent()
        assertEquals(1, vencidas)

        remote.rechazarUpdate(ProfileError.NO_CONNECTION)
        runCurrent()
        assertEquals("sin red no es sesion vencida", 1, vencidas)
    }

    @Test
    fun `C35 un permiso denegado al reintentar cierra la sesion`() = runTest {
        remote.docs["uid-ana"] = ana
        val repo = ProfileRepository(remote, backgroundScope)
        var vencidas = 0
        repo.sessionExpired.onEach { vencidas++ }.launchIn(backgroundScope)
        repo.load("uid-ana", "ana@mail.com")
        runCurrent()

        remote.falloGet = ProfileError.NO_PERMISSION
        repo.reload()
        runCurrent()
        assertEquals(1, vencidas)
    }

    @Test
    fun `una lectura fallida no pierde el observador anterior`() = runTest {
        remote.docs["uid-ana"] = ana
        val repo = ProfileRepository(remote, backgroundScope)
        repo.load("uid-ana", "ana@mail.com")
        runCurrent()

        remote.falloGet = ProfileError.NO_CONNECTION
        repo.reload()
        runCurrent()

        remote.cambios.emit(ana.copy(firstName = "Anita"))
        runCurrent()
        assertEquals("Anita", repo.profile.value.firstName)
    }

    @Test
    fun `C22 tras registrarse el perfil tambien queda observado`() = runTest {
        val repo = ProfileRepository(remote, backgroundScope)
        repo.create("uid-ana", Profile.default("Ana", "López", "ana@mail.com"))
        runCurrent()
        assertEquals(1, remote.observadores)

        repo.saveEdit(edicion)
        remote.cambios.emit(ana)
        runCurrent()
        assertEquals("Ana", repo.profile.value.firstName)
    }

    @Test
    fun `un snapshot no pisa lo que Goals cambio en memoria pero si los campos de Edit Profile`() = runTest {
        remote.docs["uid-ana"] = ana
        val repo = ProfileRepository(remote, backgroundScope)
        repo.load("uid-ana", "ana@mail.com")
        runCurrent()
        repo.update { it.copy(goals = setOf(Goal.BUILD_STRENGTH), speakAloud = false) }

        remote.cambios.emit(ana.copy(firstName = "Anita", heightCm = 181))
        runCurrent()

        assertEquals("Anita", repo.profile.value.firstName)
        assertEquals(181, repo.profile.value.heightCm)
        assertEquals(setOf(Goal.BUILD_STRENGTH), repo.profile.value.goals)
        assertFalse(repo.profile.value.speakAloud)
    }

    @Test
    fun `un error del listener se refleja en loadError y el proximo reload vuelve a observar`() = runTest {
        remote.docs["uid-ana"] = ana
        remote.falloObserve = ProfileDataException(ProfileError.UNKNOWN)
        val repo = ProfileRepository(remote, backgroundScope)
        repo.load("uid-ana", "ana@mail.com")
        runCurrent()
        assertEquals(ProfileError.UNKNOWN, repo.loadError.value)

        remote.falloObserve = null
        repo.reload()
        runCurrent()
        assertNull(repo.loadError.value)
        assertEquals(2, remote.observadores)
        remote.cambios.emit(ana.copy(firstName = "Anita"))
        runCurrent()
        assertEquals("Anita", repo.profile.value.firstName)
    }
}
