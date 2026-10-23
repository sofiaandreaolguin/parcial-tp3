package com.example.parcial_tp3.ui.screens.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * NotificationsViewModel (Home / 02): las notificaciones de demostracion, "Mark all as read" y
 * abrir una. El estado vive en un MutableStateFlow, asi que no hace falta avanzar corrutinas.
 */
class NotificationsViewModelTest {

    private fun sinLeer(vm: NotificationsViewModel) = vm.uiState.value.notifications.filter { it.unread }.map { it.id }

    @Test
    fun `arranca con las cuatro del frame y dos sin leer`() {
        val vm = NotificationsViewModel()
        assertEquals(listOf("coach", "workout", "nutrition", "streak"), vm.uiState.value.notifications.map { it.id })
        assertEquals(listOf("coach", "workout"), sinLeer(vm))
        assertTrue(vm.uiState.value.hasUnread)
    }

    @Test
    fun `hoy tiene tres y ayer una`() {
        val estado = NotificationsViewModel().uiState.value
        assertEquals(listOf("coach", "workout", "nutrition"), estado.today.map { it.id })
        assertEquals(listOf("streak"), estado.yesterday.map { it.id })
    }

    @Test
    fun `abrir una la marca como leida y no toca las demas`() {
        val vm = NotificationsViewModel()
        vm.onOpen("coach")
        assertEquals(listOf("workout"), sinLeer(vm))
    }

    @Test
    fun `abrir una que ya estaba leida no cambia nada`() {
        val vm = NotificationsViewModel()
        val antes = vm.uiState.value
        vm.onOpen("nutrition")
        assertEquals(antes, vm.uiState.value)
    }

    @Test
    fun `mark all as read deja todas leidas`() {
        val vm = NotificationsViewModel()
        vm.markAllAsRead()
        assertEquals(emptyList<String>(), sinLeer(vm))
        assertFalse(vm.uiState.value.hasUnread)
    }

    @Test
    fun `cada notificacion lleva al destino de PANTALLAS md`() {
        val destinos = NotificationsViewModel().uiState.value.notifications.associate { it.id to it.destination }
        assertEquals(NotificationDestination.WeeklyReview, destinos["coach"])
        assertEquals(NotificationDestination.WorkoutDetails("w01"), destinos["workout"])
        assertEquals(NotificationDestination.Nutrition, destinos["nutrition"])
        assertEquals(NotificationDestination.None, destinos["streak"])
    }
}
