package com.example.parcial_tp3.ui.screens.home

import com.example.parcial_tp3.data.repository.ProfileRepository
import com.example.parcial_tp3.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** C42 en Home: el saludo usa el email cuando no hay nombre ni apellido. */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeNombreDeRespaldoTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private fun estado(first: String, last: String, email: String): HomeUiState {
        val perfil = ProfileRepository().apply { update { it.copy(firstName = first, lastName = last, email = email) } }
        return HomeViewModel(perfil, FakeMealRepository()).uiState.value
    }

    @Test
    fun `C42 sin nombre ni apellido Home saluda con el email y sin apellido`() = runTest {
        val e = estado("", "", "ana@mail.com")
        assertEquals("ana", e.firstName)
        assertEquals("", e.lastName)
    }

    @Test
    fun `C42 con nombre Home no cambia`() = runTest {
        val e = estado("Ana", "López", "ana@mail.com")
        assertEquals("Ana", e.firstName)
        assertEquals("López", e.lastName)
    }
}
