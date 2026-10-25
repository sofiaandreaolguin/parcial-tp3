package com.example.parcial_tp3.domain.model

import java.time.LocalDate

/** Perfil del usuario. Las opciones son enums: la UI las traduce con stringResource. */
data class Profile(
    val firstName: String,
    val lastName: String,
    val email: String,
    val birthDate: LocalDate,
    val heightCm: Int,
    val weightKg: Int,
    val gender: Gender,
    val experience: Experience,
    val goals: Set<Goal>,
    val trainingDays: TrainingDays,
    val workoutType: WorkoutType,
    val coachingStyle: CoachingStyle,
    val diet: Diet,
    val foodsEnjoyed: Set<Food>,
    val foodsToAvoid: List<String>,
    val priorities: Set<NutritionPriority>,
    val responseStyle: ResponseStyle,
    val speakAloud: Boolean,
    val autoPlay: Boolean,
    val allowInterruptions: Boolean,
    val voiceFeedback: Boolean,
    val hapticFeedback: Boolean,
) {
    companion object {
        /** Perfil de una cuenta nueva (tabla de specs/funcionales/firebase-auth-perfil.md). */
        fun default(firstName: String, lastName: String, email: String): Profile = Profile(
            firstName = "",
            lastName = "",
            email = "",
            birthDate = LocalDate.of(1970, 1, 1),
            heightCm = 0,
            weightKg = 0,
            gender = Gender.FEMALE,
            experience = Experience.ADVANCED,
            goals = emptySet(),
            trainingDays = TrainingDays.SIX_PLUS,
            workoutType = WorkoutType.STRENGTH,
            coachingStyle = CoachingStyle.SUPPORTIVE,
            diet = Diet.OTHER,
            foodsEnjoyed = emptySet(),
            foodsToAvoid = emptyList(),
            priorities = emptySet(),
            responseStyle = ResponseStyle.CONCISE,
            speakAloud = false,
            autoPlay = false,
            allowInterruptions = false,
            voiceFeedback = false,
            hapticFeedback = false,
        )
    }
}

enum class Gender { FEMALE, MALE, NON_BINARY, PREFER_NOT_TO_SAY }

enum class Experience { BEGINNER, INTERMEDIATE, ADVANCED }

enum class Goal { BUILD_STRENGTH, IMPROVE_FITNESS, LOSE_BODY_FAT, BUILD_HEALTHY_HABITS }

enum class TrainingDays { THREE, FOUR, FIVE, SIX_PLUS }

enum class WorkoutType { STRENGTH, CARDIO, HIIT, MOBILITY, MIXED }

enum class CoachingStyle { SUPPORTIVE, BALANCED, DIRECT }

enum class Diet { EVERYTHING, VEGETARIAN, VEGAN, PESCATARIAN, OTHER }

enum class Food { CHICKEN, EGGS, FISH, RICE, PASTA, GREEK_YOGHURT, VEGETABLES, FRUIT }

enum class NutritionPriority { HIT_PROTEIN, EAT_CONSISTENTLY }

enum class ResponseStyle { CONCISE, BALANCED, DETAILED }
