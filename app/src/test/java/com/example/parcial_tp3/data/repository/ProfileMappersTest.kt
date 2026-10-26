package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.remote.dto.ProfileDto
import com.example.parcial_tp3.domain.model.CoachingStyle
import com.example.parcial_tp3.domain.model.Diet
import com.example.parcial_tp3.domain.model.Experience
import com.example.parcial_tp3.domain.model.Gender
import com.example.parcial_tp3.domain.model.Goal
import com.example.parcial_tp3.domain.model.Profile
import com.example.parcial_tp3.domain.model.ProfileEdit
import com.example.parcial_tp3.domain.model.ResponseStyle
import com.example.parcial_tp3.domain.model.TrainingDays
import com.example.parcial_tp3.domain.model.WorkoutType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileMappersTest {

    /** Los valores de la tabla de la spec escritos a mano, para no depender de Profile.default. */
    private fun porDefecto(nombre: String, apellido: String, email: String) = Profile(
        firstName = nombre, lastName = apellido, email = email,
        birthDate = LocalDate.of(2000, 1, 1), heightCm = 170, weightKg = 70,
        gender = Gender.PREFER_NOT_TO_SAY, experience = Experience.BEGINNER,
        goals = setOf(Goal.IMPROVE_FITNESS), trainingDays = TrainingDays.THREE,
        workoutType = WorkoutType.MIXED, coachingStyle = CoachingStyle.BALANCED, diet = Diet.EVERYTHING,
        foodsEnjoyed = emptySet(), foodsToAvoid = emptyList(), priorities = emptySet(),
        responseStyle = ResponseStyle.BALANCED,
        speakAloud = true, autoPlay = true, allowInterruptions = true, voiceFeedback = true, hapticFeedback = true,
    )

    @Test
    fun `C2 Profile default usa los valores confirmados`() {
        val p = Profile.default("Ana", "López", "ana@mail.com")
        assertEquals("Ana", p.firstName)
        assertEquals("López", p.lastName)
        assertEquals("ana@mail.com", p.email)
        assertEquals(LocalDate.of(2000, 1, 1), p.birthDate)
        assertEquals(170, p.heightCm)
        assertEquals(70, p.weightKg)
        assertEquals(Gender.PREFER_NOT_TO_SAY, p.gender)
        assertEquals(Experience.BEGINNER, p.experience)
        assertEquals(setOf(Goal.IMPROVE_FITNESS), p.goals)
        assertEquals(TrainingDays.THREE, p.trainingDays)
        assertEquals(WorkoutType.MIXED, p.workoutType)
        assertEquals(CoachingStyle.BALANCED, p.coachingStyle)
        assertEquals(ResponseStyle.BALANCED, p.responseStyle)
        assertEquals(Diet.EVERYTHING, p.diet)
        assertTrue(p.foodsEnjoyed.isEmpty() && p.foodsToAvoid.isEmpty() && p.priorities.isEmpty())
        assertTrue(p.speakAloud && p.autoPlay && p.allowInterruptions && p.voiceFeedback && p.hapticFeedback)
    }

    @Test
    fun `C23 documento inexistente se crea con el perfil por defecto`() {
        assertEquals(porDefecto("", "", "ana@mail.com"), profileOrDefault(null, "ana@mail.com"))
        assertEquals("ana@mail.com", profileOrDefault(null, "ana@mail.com").email)
        assertEquals("", profileOrDefault(null, "ana@mail.com").firstName)
    }

    @Test
    fun `C38 enum desconocido o campo ausente usa el valor por defecto`() {
        val esperado = porDefecto("Ana", "López", "ana@mail.com")

        val ausentes = ProfileDto(firstName = "Ana", lastName = "López", email = "ana@mail.com").toDomain()
        assertEquals(esperado, ausentes)

        val raros = ProfileDto(
            firstName = "Ana", lastName = "López", email = "ana@mail.com",
            birthDate = "no-es-fecha", gender = "ALIEN", experience = "EXPERT", trainingDays = "NUNCA",
            workoutType = "YOGA", coachingStyle = "", diet = "CARNIVORE", responseStyle = "LONG",
            heightCm = 170, weightKg = 70, goals = listOf("NO_EXISTE"),
        ).toDomain()
        assertEquals(esperado, raros)
    }

    @Test
    fun `C38 un documento completo se convierte sin perder datos`() {
        val perfil = Profile.default("Ana", "López", "ana@mail.com").copy(
            birthDate = LocalDate.of(1990, 5, 17),
            heightCm = 181,
            weightKg = 64,
            gender = Gender.FEMALE,
            goals = setOf(Goal.BUILD_STRENGTH, Goal.LOSE_BODY_FAT),
            speakAloud = false,
            foodsToAvoid = listOf("Peanuts"),
        )
        assertEquals(perfil, perfil.toDto().toDomain())
        assertEquals("1990-05-17", perfil.toDto().birthDate)
        assertFalse(perfil.toDto().toDomain().speakAloud)
    }

    @Test
    fun `C19 la edicion escribe solo los 6 campos`() {
        val campos = ProfileEdit(
            firstName = "Ana", lastName = "López", birthDate = LocalDate.of(1990, 5, 17),
            heightCm = 181, weightKg = 64, gender = Gender.FEMALE,
        ).toFields()
        assertEquals(
            mapOf(
                "firstName" to "Ana",
                "lastName" to "López",
                "birthDate" to "1990-05-17",
                "heightCm" to 181,
                "weightKg" to 64,
                "gender" to "FEMALE",
            ),
            campos,
        )
    }

    @Test
    fun `C36 editar el nombre no modifica goals guardados`() {
        val campos = ProfileEdit(
            firstName = "Ana", lastName = "López", birthDate = LocalDate.of(1990, 5, 17),
            heightCm = 181, weightKg = 64, gender = Gender.FEMALE,
        ).toFields()
        assertTrue(campos.isNotEmpty())
        listOf("goals", "email", "experience", "trainingDays", "workoutType", "coachingStyle", "diet",
            "responseStyle", "foodsEnjoyed", "foodsToAvoid", "priorities", "speakAloud", "autoPlay",
            "allowInterruptions", "voiceFeedback", "hapticFeedback").forEach {
            assertFalse("$it no debe escribirse", campos.containsKey(it))
        }
    }

    @Test
    fun `C31 cada excepcion de Firebase se traduce a un error propio`() {
        assertEquals(AuthError.INVALID_CREDENTIALS, authErrorFromCode("ERROR_WRONG_PASSWORD"))
        assertEquals(AuthError.INVALID_CREDENTIALS, authErrorFromCode("ERROR_USER_NOT_FOUND"))
        assertEquals(AuthError.INVALID_CREDENTIALS, authErrorFromCode("ERROR_INVALID_CREDENTIAL"))
        assertEquals(AuthError.EMAIL_IN_USE, authErrorFromCode("ERROR_EMAIL_ALREADY_IN_USE"))
        assertEquals(AuthError.WEAK_PASSWORD, authErrorFromCode("ERROR_WEAK_PASSWORD"))
        assertEquals(AuthError.TOO_MANY_REQUESTS, authErrorFromCode("ERROR_TOO_MANY_REQUESTS"))
        assertEquals(AuthError.NO_CONNECTION, authErrorFromCode("ERROR_NETWORK_REQUEST_FAILED"))
        assertEquals(AuthError.UNKNOWN, authErrorFromCode("ERROR_ALGO_NUEVO"))
        assertEquals(AuthError.UNKNOWN, authErrorFromCode(null))

        assertEquals(ProfileError.NO_PERMISSION, profileErrorFromCode("PERMISSION_DENIED"))
        assertEquals(ProfileError.NO_PERMISSION, profileErrorFromCode("UNAUTHENTICATED"))
        assertEquals(ProfileError.NO_CONNECTION, profileErrorFromCode("UNAVAILABLE"))
        assertEquals(ProfileError.NO_CONNECTION, profileErrorFromCode("DEADLINE_EXCEEDED"))
        assertEquals(ProfileError.UNKNOWN, profileErrorFromCode("DATA_LOSS"))
        assertEquals(ProfileError.UNKNOWN, profileErrorFromCode(null))
    }
}
