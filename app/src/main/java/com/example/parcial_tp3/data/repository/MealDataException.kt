package com.example.parcial_tp3.data.repository

/** Error de la capa de comidas, traducido desde Room o desde una Entity que no se puede leer. */
class MealDataException(val error: MealError, cause: Throwable? = null) : Exception(cause)

enum class MealError { INVALID_DATA, STORAGE, NOT_FOUND }
