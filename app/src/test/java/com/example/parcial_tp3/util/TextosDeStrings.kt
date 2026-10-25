package com.example.parcial_tp3.util

import com.example.parcial_tp3.R

/** true si el id es un texto de strings.xml de la app. La spec no fija cual: solo que salga de ahi. */
fun esTextoDeStrings(id: Int?): Boolean =
    id != null && R.string::class.java.fields.any { it.getInt(null) == id }
