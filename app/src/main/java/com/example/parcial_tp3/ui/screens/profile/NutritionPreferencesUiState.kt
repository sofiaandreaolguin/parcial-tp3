package com.example.parcial_tp3.ui.screens.profile

import com.example.parcial_tp3.domain.model.Diet
import com.example.parcial_tp3.domain.model.Food
import com.example.parcial_tp3.domain.model.NutritionPriority

data class NutritionPreferencesUiState(
    val diet: Diet,
    val foodsEnjoyed: Set<Food>,
    val foodsToAvoid: List<String>,
    val priorities: Set<NutritionPriority>,
)
