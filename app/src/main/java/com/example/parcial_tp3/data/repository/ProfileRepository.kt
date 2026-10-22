package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.domain.model.CoachingStyle
import com.example.parcial_tp3.domain.model.Diet
import com.example.parcial_tp3.domain.model.Experience
import com.example.parcial_tp3.domain.model.Food
import com.example.parcial_tp3.domain.model.Gender
import com.example.parcial_tp3.domain.model.Goal
import com.example.parcial_tp3.domain.model.NutritionPriority
import com.example.parcial_tp3.domain.model.Profile
import com.example.parcial_tp3.domain.model.ResponseStyle
import com.example.parcial_tp3.domain.model.TrainingDays
import com.example.parcial_tp3.domain.model.WorkoutType
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Perfil de demostración (CONSIGNA: Profile es "Demostración", sin Firebase).
 * Vive en memoria: lo editado se comparte entre pantallas y se pierde al cerrar la app.
 */
@Singleton
class ProfileRepository @Inject constructor() {

    private val _profile = MutableStateFlow(DEMO)
    val profile: StateFlow<Profile> = _profile.asStateFlow()

    fun update(transform: (Profile) -> Profile) = _profile.update(transform)

    private companion object {
        val DEMO = Profile(
            firstName = "Alex",
            lastName = "Morgan",
            email = "alex@morgan.com",
            birthDate = LocalDate.of(1995, 3, 14),
            heightCm = 178,
            weightKg = 76,
            gender = Gender.PREFER_NOT_TO_SAY,
            experience = Experience.INTERMEDIATE,
            goals = setOf(Goal.BUILD_STRENGTH, Goal.IMPROVE_FITNESS),
            trainingDays = TrainingDays.FOUR,
            workoutType = WorkoutType.STRENGTH,
            coachingStyle = CoachingStyle.BALANCED,
            diet = Diet.EVERYTHING,
            foodsEnjoyed = Food.entries.toSet(),
            foodsToAvoid = listOf("Peanuts", "Shellfish", "Mushrooms"),
            priorities = setOf(NutritionPriority.HIT_PROTEIN, NutritionPriority.EAT_CONSISTENTLY),
            responseStyle = ResponseStyle.BALANCED,
            speakAloud = true,
            autoPlay = true,
            allowInterruptions = true,
            voiceFeedback = true,
            hapticFeedback = true,
        )
    }
}
