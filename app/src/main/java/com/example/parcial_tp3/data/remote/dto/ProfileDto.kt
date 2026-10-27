package com.example.parcial_tp3.data.remote.dto

/**
 * Documento `users/{uid}`. Todo con valor por defecto para que Firestore deserialice un documento
 * incompleto. Los defaults son "campo ausente": 0 numeros, "" textos, listas vacias y true los
 * Boolean (igual que Profile.default); `toDomain()` los reemplaza por los de Profile.default (C38).
 */
data class ProfileDto(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val birthDate: String = "",
    val heightCm: Int = 0,
    val weightKg: Int = 0,
    val gender: String = "",
    val experience: String = "",
    val trainingDays: String = "",
    val workoutType: String = "",
    val coachingStyle: String = "",
    val diet: String = "",
    val responseStyle: String = "",
    val goals: List<String> = emptyList(),
    val foodsEnjoyed: List<String> = emptyList(),
    val priorities: List<String> = emptyList(),
    val foodsToAvoid: List<String> = emptyList(),
    val speakAloud: Boolean = true,
    val autoPlay: Boolean = true,
    val allowInterruptions: Boolean = true,
    val voiceFeedback: Boolean = true,
    val hapticFeedback: Boolean = true,
)
