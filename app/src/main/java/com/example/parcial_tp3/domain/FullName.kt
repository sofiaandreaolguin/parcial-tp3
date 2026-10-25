package com.example.parcial_tp3.domain

/** Primera palabra como nombre y el resto como apellido (C3). */
fun splitFullName(fullName: String): Pair<String, String> {
    val words = fullName.trim().split(Regex("\\s+"))
    return words.first() to words.drop(1).joinToString(" ")
}
