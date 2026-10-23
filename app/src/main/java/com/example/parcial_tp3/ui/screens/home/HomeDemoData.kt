package com.example.parcial_tp3.ui.screens.home

import com.example.parcial_tp3.domain.model.Workout
import com.example.parcial_tp3.domain.model.WorkoutLevel

/**
 * Datos de demostracion de Home / 01 (CONSIGNA: Home es "Demostracion"). Tienen la misma
 * forma que los modelos de Workout para conectarlos sin tocar la pantalla: el recomendado
 * pasa a salir de la cache de entrenamientos cuando cierre la tarea j.
 *
 * Los objetivos diarios van aca hasta que Profile o Nutrition los guarden.
 */
object HomeDemoData {
    const val KCAL_GOAL = 2_100
    const val PROTEIN_GOAL_G = 140
    const val CARBS_GOAL_G = 220

    const val STEPS = 7_842
    const val DISTANCE_KM = 5.6
    const val ACTIVE_KCAL = 312

    /** w01 existe en la VORA Mock API, asi "Start workout" abre un detalle de verdad. */
    val RECOMMENDED = Workout(
        id = "w01",
        name = "Upper Body Strength",
        level = WorkoutLevel.INTERMEDIATE,
        durationMinutes = 42,
        exerciseCount = 5,
    )
    const val ESTIMATED_VOLUME_KG = 2_340
}
