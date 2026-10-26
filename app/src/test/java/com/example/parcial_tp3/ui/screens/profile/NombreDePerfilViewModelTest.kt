package com.example.parcial_tp3.ui.screens.profile

import com.example.parcial_tp3.data.repository.FakeAuthRepository
import com.example.parcial_tp3.data.repository.ProfileRepository
import com.example.parcial_tp3.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** C42 en Settings y Profile / 01: el nombre mostrado sale de `displayName`. */
@OptIn(ExperimentalCoroutinesApi::class)
class NombreDePerfilViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private fun repo(first: String, last: String, email: String) = ProfileRepository().apply {
        update { it.copy(firstName = first, lastName = last, email = email) }
    }

    @Test
    fun `C42 settings sin apellido no deja espacio al final`() = runTest {
        val vm = SettingsViewModel(repo("Ana", "", "ana@mail.com"), FakeAuthRepository())
        assertEquals("Ana", vm.uiState.value.fullName)
    }

    @Test
    fun `C42 settings sin nombre usa el email como respaldo`() = runTest {
        val vm = SettingsViewModel(repo("", "", "ana@mail.com"), FakeAuthRepository())
        assertEquals("ana", vm.uiState.value.fullName)
    }

    @Test
    fun `C42 profile sin apellido no deja espacio al final`() = runTest {
        val vm = ProfileDashboardViewModel(repo("Ana", "", "ana@mail.com"))
        assertEquals("Ana", vm.uiState.value.fullName)
    }

    @Test
    fun `C42 profile sin nombre usa el email como respaldo`() = runTest {
        val vm = ProfileDashboardViewModel(repo("", "", "ana@mail.com"))
        assertEquals("ana", vm.uiState.value.fullName)
    }
}
