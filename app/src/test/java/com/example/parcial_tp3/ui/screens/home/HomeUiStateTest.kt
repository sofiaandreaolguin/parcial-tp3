package com.example.parcial_tp3.ui.screens.home

import org.junit.Assert.assertEquals
import org.junit.Test

/** Lo que calcula HomeUiState para el anillo y las barras, y el saludo segun la hora. */
class HomeUiStateTest {

    private fun estado(kcal: Int = 0, kcalGoal: Int = 2_100, proteinG: Int = 0, proteinGoal: Int = 140) = HomeUiState(
        firstName = "Alex",
        lastName = "Morgan",
        greeting = Greeting.MORNING,
        kcal = kcal,
        kcalGoal = kcalGoal,
        proteinG = proteinG,
        proteinGoal = proteinGoal,
        carbsG = 0,
        carbsGoal = 220,
        steps = 0,
        distanceKm = 0.0,
        activeKcal = 0,
        recommended = HomeDemoData.RECOMMENDED,
        estimatedVolumeKg = 0,
    )

    @Test
    fun `el anillo es calorias sobre objetivo`() {
        assertEquals(0.5f, estado(kcal = 1_050).dailyProgress, 0.0001f)
        assertEquals(50, estado(kcal = 1_050).dailyPercent)
    }

    @Test
    fun `pasarse del objetivo deja el anillo lleno y no mas`() {
        assertEquals(1f, estado(kcal = 3_000).dailyProgress, 0.0001f)
        assertEquals(100, estado(kcal = 3_000).dailyPercent)
    }

    @Test
    fun `sin objetivo el anillo queda en cero en vez de dividir por cero`() {
        assertEquals(0f, estado(kcal = 500, kcalGoal = 0).dailyProgress, 0.0001f)
        assertEquals(0f, estado(proteinG = 50, proteinGoal = 0).proteinProgress, 0.0001f)
    }

    @Test
    fun `el saludo cambia en las 5, las 12 y las 20`() {
        assertEquals(Greeting.EVENING, Greeting.at(4))
        assertEquals(Greeting.MORNING, Greeting.at(5))
        assertEquals(Greeting.MORNING, Greeting.at(11))
        assertEquals(Greeting.AFTERNOON, Greeting.at(12))
        assertEquals(Greeting.AFTERNOON, Greeting.at(19))
        assertEquals(Greeting.EVENING, Greeting.at(20))
        assertEquals(Greeting.EVENING, Greeting.at(0))
    }
}
