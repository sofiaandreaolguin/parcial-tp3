package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.remote.dto.ProfileDto
import com.example.parcial_tp3.domain.model.Profile
import com.example.parcial_tp3.domain.model.ProfileEdit

fun ProfileDto.toDomain(): Profile = Profile.default("", "", "")

fun Profile.toDto(): ProfileDto = ProfileDto()

/** Documento inexistente: perfil por defecto con el email de la cuenta y el nombre vacio (C23). */
fun profileOrDefault(dto: ProfileDto?, email: String): Profile = Profile.default("", "", "")

/** Campos de la actualizacion parcial de Edit Profile (C19, C36): `birthDate` ISO, `gender` por nombre. */
fun ProfileEdit.toFields(): Map<String, Any> = emptyMap()
