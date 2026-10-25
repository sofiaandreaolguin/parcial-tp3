package com.example.parcial_tp3.domain

import com.example.parcial_tp3.domain.model.MealType
import java.time.LocalDate

/** El formulario de Nutrition / 07. Los numeros van como texto porque es lo que se tipea. */
data class MealForm(
    val name: String = "",
    val type: MealType? = null,
    val date: LocalDate,
    val kcal: String = "",
    val protein: String = "",
    val carbs: String = "",
    val fat: String = "",
)
