package com.example.parcial_tp3.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/** Lo que muestra Home / 02: las notificaciones de hoy y las de ayer. */
data class NotificationsUiState(
    val notifications: List<NotificationItem>,
) {
    val today: List<NotificationItem> get() = notifications.filter { it.ageMinutes < MINUTES_PER_DAY }
    val yesterday: List<NotificationItem> get() = notifications.filter { it.ageMinutes >= MINUTES_PER_DAY }
    val hasUnread: Boolean get() = notifications.any { it.unread }

    companion object {
        const val MINUTES_PER_HOUR = 60
        const val MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR
    }
}

/**
 * Una notificacion de demostracion (CONSIGNA: Home es "Demostracion"). Los textos son ids de
 * strings.xml, asi se traducen; [destination] dice a donde lleva tocarla (specs/PANTALLAS.md).
 */
data class NotificationItem(
    val id: String,
    @DrawableRes val icon: Int,
    @StringRes val sender: Int,
    @StringRes val title: Int,
    @StringRes val body: Int,
    val ageMinutes: Int,
    val unread: Boolean,
    val destination: NotificationDestination,
)

/** A donde lleva cada notificacion: la de la racha no tiene pantalla y no navega. */
sealed interface NotificationDestination {
    data object WeeklyReview : NotificationDestination
    data class WorkoutDetails(val workoutId: String) : NotificationDestination
    data object Nutrition : NotificationDestination
    data object None : NotificationDestination
}
