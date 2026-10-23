package com.example.parcial_tp3.ui.screens.home

import com.example.parcial_tp3.R

/**
 * Notificaciones de demostracion de Home / 02 (CONSIGNA: Home es "Demostracion"). Aparte del
 * ViewModel, como HomeDemoData, para que la preview las use sin crearlo.
 */
object NotificationsDemoData {
    /** Las cuatro del frame, en el mismo orden. w01 existe en la VORA Mock API. */
    val NOTIFICATIONS = listOf(
        NotificationItem(
            id = "coach",
            icon = R.drawable.ic_sparkle,
            sender = R.string.notification_coach_sender,
            title = R.string.notification_coach_title,
            body = R.string.notification_coach_body,
            ageMinutes = 2 * 60,
            unread = true,
            destination = NotificationDestination.WeeklyReview,
        ),
        NotificationItem(
            id = "workout",
            icon = R.drawable.ic_tab_workout,
            sender = R.string.notification_workout_sender,
            title = R.string.notification_workout_title,
            body = R.string.notification_workout_body,
            ageMinutes = 4 * 60,
            unread = true,
            destination = NotificationDestination.WorkoutDetails("w01"),
        ),
        NotificationItem(
            id = "nutrition",
            icon = R.drawable.ic_tab_nutrition,
            sender = R.string.notification_nutrition_sender,
            title = R.string.notification_nutrition_title,
            body = R.string.notification_nutrition_body,
            ageMinutes = 6 * 60,
            unread = false,
            destination = NotificationDestination.Nutrition,
        ),
        NotificationItem(
            id = "streak",
            icon = R.drawable.ic_flame,
            sender = R.string.notification_streak_sender,
            title = R.string.notification_streak_title,
            body = R.string.notification_streak_body,
            ageMinutes = NotificationsUiState.MINUTES_PER_DAY,
            unread = false,
            destination = NotificationDestination.None,
        ),
    )
}
