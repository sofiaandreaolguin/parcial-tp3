package com.example.parcial_tp3.ui.screens.home

import com.example.parcial_tp3.domain.model.Workout

/**
 * Lo que muestra Home / 01. Las calorias, proteinas y carbohidratos de hoy salen de las
 * comidas guardadas en Room; el resto es de demostracion ([HomeDemoData]).
 */
data class HomeUiState(
    val firstName: String,
    val lastName: String,
    val greeting: Greeting,
    val kcal: Int,
    val kcalGoal: Int,
    val proteinG: Int,
    val proteinGoal: Int,
    val carbsG: Int,
    val carbsGoal: Int,
    val steps: Int,
    val distanceKm: Double,
    val activeKcal: Int,
    val recommended: Workout,
    val estimatedVolumeKg: Int,
) {
    /** El anillo "Daily Goal": calorias de hoy sobre el objetivo, entre 0 y 1. */
    val dailyProgress: Float get() = fraction(kcal, kcalGoal)
    val dailyPercent: Int get() = (dailyProgress * 100).toInt()
    val proteinProgress: Float get() = fraction(proteinG, proteinGoal)
    val carbsProgress: Float get() = fraction(carbsG, carbsGoal)

    private fun fraction(value: Int, goal: Int) = if (goal <= 0) 0f else (value.toFloat() / goal).coerceIn(0f, 1f)
}

/** El saludo cambia con la hora del dia; el Figma muestra el de la manana. */
enum class Greeting {
    MORNING,
    AFTERNOON,
    EVENING,
    ;

    companion object {
        fun at(hour: Int): Greeting = when (hour) {
            in 5..11 -> MORNING
            in 12..19 -> AFTERNOON
            else -> EVENING
        }
    }
}
