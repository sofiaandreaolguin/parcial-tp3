package com.example.parcial_tp3.domain

/** Primera palabra como nombre y el resto como apellido (C3). */
fun splitFullName(fullName: String): Pair<String, String> {
    val words = fullName.trim().split(Regex("\\s+"))
    return words.first() to words.drop(1).joinToString(" ")
}

/** Nombre para mostrar (C42): sin espacios sobrantes; sin nombre ni apellido, el email antes de la `@`. No se guarda. */
fun displayName(firstName: String, lastName: String, email: String): String =
    listOf(firstName.trim(), lastName.trim()).filter { it.isNotEmpty() }.joinToString(" ")
        .ifEmpty { email.substringBefore('@').trim() }
