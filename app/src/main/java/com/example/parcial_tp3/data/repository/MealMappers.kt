package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.entity.MealEntity
import com.example.parcial_tp3.domain.model.Ingredient
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.MealType
import com.google.gson.Gson
import com.google.gson.JsonParseException
import java.time.LocalDate

// Gson guarda los ingredientes con los nombres de los campos de Ingredient: renombrar uno
// rompe lo que ya está en Room.
private val gson = Gson()

fun Meal.toEntity(): MealEntity =
    MealEntity(
        id = id,
        name = name,
        date = date.toString(),
        type = type.name,
        kcal = kcal,
        proteinG = proteinG,
        carbsG = carbsG,
        fatG = fatG,
        ingredients = gson.toJson(ingredients),
    )

fun MealEntity.toDomain(): Meal =
    Meal(
        id = id,
        name = name,
        date = LocalDate.parse(date),
        type = MealType.valueOf(type),
        kcal = kcal,
        proteinG = proteinG,
        carbsG = carbsG,
        fatG = fatG,
        ingredients = parseIngredients(ingredients),
    )

// El repositorio traduce IllegalArgumentException a INVALID_DATA, pero Gson no la tira:
// devuelve null con "" o "null", y deja en null el nombre que falta sin avisar. Los números
// que faltan quedan en 0, y eso lo frena isValidIngredient antes de guardar.
private fun parseIngredients(json: String): List<Ingredient> {
    val parsed: Array<Ingredient?> = try {
        gson.fromJson(json, Array<Ingredient?>::class.java)
    } catch (e: JsonParseException) {
        throw IllegalArgumentException("ingredientes que no son un JSON válido", e)
    } ?: throw IllegalArgumentException("ingredientes vacíos o null")
    return parsed.map { element ->
        val ingredient = requireNotNull(element) { "ingrediente null" }
        val name: String? = ingredient.name
        requireNotNull(name) { "ingrediente sin nombre" }
        ingredient
    }
}
