package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.domain.model.CoachingStyle
import com.example.parcial_tp3.domain.model.Diet
import com.example.parcial_tp3.domain.model.Experience
import com.example.parcial_tp3.domain.model.Food
import com.example.parcial_tp3.domain.model.Gender
import com.example.parcial_tp3.domain.model.Goal
import com.example.parcial_tp3.domain.model.NutritionPriority
import com.example.parcial_tp3.domain.model.Profile
import com.example.parcial_tp3.domain.model.ProfileEdit
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
open class ProfileRepository @Inject constructor() {

    private val _profile = MutableStateFlow(DEMO)
    open val profile: StateFlow<Profile> = _profile.asStateFlow()

    open fun update(transform: (Profile) -> Profile) = _profile.update(transform)

    /** Error de la ultima lectura sin cache local; null si no hay (C24). */
    open val loadError: StateFlow<ProfileError?> = MutableStateFlow<ProfileError?>(null)

    /** Lee `users/{uid}`, lo crea con Profile.default si no existe y lo publica en `profile` (C18, C23). */
    open suspend fun load(uid: String, email: String) {}

    /** Crea `users/{uid}` (C2). */
    open suspend fun create(uid: String, profile: Profile) {}

    /** Actualizacion parcial de los 6 campos; no espera al servidor (C19, C36). */
    open fun saveEdit(edit: ProfileEdit) {}

    /** Reintento tras un error de lectura (C24). */
    open suspend fun reload() {}

    /** Cierre de sesion: no queda ningun dato de la cuenta anterior (C15, C17). */
    open fun clear() {}

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
