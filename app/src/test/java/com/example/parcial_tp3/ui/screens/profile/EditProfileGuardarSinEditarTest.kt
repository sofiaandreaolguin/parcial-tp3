package com.example.parcial_tp3.ui.screens.profile

import com.example.parcial_tp3.data.repository.FakeProfileRepository
import com.example.parcial_tp3.domain.model.Profile
import com.example.parcial_tp3.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfileGuardarSinEditarTest {

    @get:Rule
    val main = MainDispatcherRule()

    @Test
    fun `C19 guardar sin editar un nombre con espacio final cierra la pantalla`() = runTest {
        val perfiles = FakeProfileRepository().apply {
            docs["uid-ana"] = Profile.default("Ana ", "López", "ana@mail.com")
        }
        perfiles.load("uid-ana", "ana@mail.com")
        val vm = EditProfileViewModel(perfiles)
        advanceUntilIdle()

        vm.save()
        advanceUntilIdle()

        assertEquals("Ana", perfiles.profile.value.firstName)
        assertTrue("el perfil recortado no debe reabrir el formulario", vm.uiState.value.saved)
    }
}
