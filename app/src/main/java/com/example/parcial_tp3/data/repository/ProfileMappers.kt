package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.remote.dto.ProfileDto
import com.example.parcial_tp3.domain.model.Goal
import com.example.parcial_tp3.domain.model.Profile
import com.example.parcial_tp3.domain.model.ProfileEdit
import java.time.LocalDate
import java.time.format.DateTimeParseException

private inline fun <reified E : Enum<E>> enumOrDefault(name: String, default: E): E =
    enumValues<E>().firstOrNull { it.name == name } ?: default

private inline fun <reified E : Enum<E>> enumSet(names: List<String>): Set<E> =
    names.mapNotNull { name -> enumValues<E>().firstOrNull { it.name == name } }.toSet()

/** Un campo ausente (valor "vacio" del DTO) o invalido toma el valor de `Profile.default` (C38). */
fun ProfileDto.toDomain(): Profile {
    val base = Profile.default(firstName, lastName, email)
    val date = try {
        LocalDate.parse(birthDate)
    } catch (_: DateTimeParseException) {
        base.birthDate
    }
    return base.copy(
        birthDate = date,
        heightCm = if (heightCm > 0) heightCm else base.heightCm,
        weightKg = if (weightKg > 0) weightKg else base.weightKg,
        gender = enumOrDefault(gender, base.gender),
        experience = enumOrDefault(experience, base.experience),
        trainingDays = enumOrDefault(trainingDays, base.trainingDays),
        workoutType = enumOrDefault(workoutType, base.workoutType),
        coachingStyle = enumOrDefault(coachingStyle, base.coachingStyle),
        diet = enumOrDefault(diet, base.diet),
        responseStyle = enumOrDefault(responseStyle, base.responseStyle),
        goals = enumSet<Goal>(goals).ifEmpty { base.goals },
        foodsEnjoyed = enumSet(foodsEnjoyed),
        priorities = enumSet(priorities),
        foodsToAvoid = foodsToAvoid,
        speakAloud = speakAloud,
        autoPlay = autoPlay,
        allowInterruptions = allowInterruptions,
        voiceFeedback = voiceFeedback,
        hapticFeedback = hapticFeedback,
    )
}

fun Profile.toDto(): ProfileDto = ProfileDto(
    firstName = firstName,
    lastName = lastName,
    email = email,
    birthDate = birthDate.toString(),
    heightCm = heightCm,
    weightKg = weightKg,
    gender = gender.name,
    experience = experience.name,
    trainingDays = trainingDays.name,
    workoutType = workoutType.name,
    coachingStyle = coachingStyle.name,
    diet = diet.name,
    responseStyle = responseStyle.name,
    goals = goals.map { it.name },
    foodsEnjoyed = foodsEnjoyed.map { it.name },
    priorities = priorities.map { it.name },
    foodsToAvoid = foodsToAvoid,
    speakAloud = speakAloud,
    autoPlay = autoPlay,
    allowInterruptions = allowInterruptions,
    voiceFeedback = voiceFeedback,
    hapticFeedback = hapticFeedback,
)

/** Documento inexistente: perfil por defecto con el email de la cuenta y el nombre vacio (C23). */
fun profileOrDefault(dto: ProfileDto?, email: String): Profile =
    dto?.toDomain() ?: Profile.default("", "", email)

/** Campos de la actualizacion parcial de Edit Profile (C19, C36): `birthDate` ISO, `gender` por nombre. */
fun ProfileEdit.toFields(): Map<String, Any> = mapOf(
    "firstName" to firstName,
    "lastName" to lastName,
    "birthDate" to birthDate.toString(),
    "heightCm" to heightCm,
    "weightKg" to weightKg,
    "gender" to gender.name,
)
