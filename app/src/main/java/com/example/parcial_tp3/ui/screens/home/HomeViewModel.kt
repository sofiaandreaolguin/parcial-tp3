package com.example.parcial_tp3.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.data.repository.MealRepository
import com.example.parcial_tp3.data.repository.ProfileRepository
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Home / 01. El nombre sale del perfil de demostracion (si se edita en Profile, cambia aca) y
 * las calorias y macros de hoy, de las comidas guardadas en Room (RF04): guardar una comida en
 * Nutrition actualiza el anillo y la tarjeta solos, porque el DAO devuelve un Flow.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    profileRepository: ProfileRepository,
    mealRepository: MealRepository,
) : ViewModel() {

    private val greeting = Greeting.at(LocalTime.now().hour)

    val uiState: StateFlow<HomeUiState> = combine(
        profileRepository.profile,
        mealRepository.observeMealsByDate(LocalDate.now()),
    ) { profile, meals -> toUiState(profile, meals) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = toUiState(profileRepository.profile.value, emptyList()),
        )

    // El total del dia se suma aca y no en la consulta: con muchas comidas convendria un SUM
    // en el DAO (docs/decisiones/007-home-totales-del-dia.md).
    private fun toUiState(profile: Profile, meals: List<Meal>) = HomeUiState(
        firstName = profile.firstName,
        lastName = profile.lastName,
        greeting = greeting,
        kcal = meals.sumOf { it.kcal },
        kcalGoal = HomeDemoData.KCAL_GOAL,
        proteinG = meals.sumOf { it.proteinG }.roundToInt(),
        proteinGoal = HomeDemoData.PROTEIN_GOAL_G,
        carbsG = meals.sumOf { it.carbsG }.roundToInt(),
        carbsGoal = HomeDemoData.CARBS_GOAL_G,
        steps = HomeDemoData.STEPS,
        distanceKm = HomeDemoData.DISTANCE_KM,
        activeKcal = HomeDemoData.ACTIVE_KCAL,
        recommended = HomeDemoData.RECOMMENDED,
        estimatedVolumeKg = HomeDemoData.ESTIMATED_VOLUME_KG,
    )
}
