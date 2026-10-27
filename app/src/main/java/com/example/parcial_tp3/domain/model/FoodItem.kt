package com.example.parcial_tp3.domain.model

/**
 * Un alimento del catalogo del servidor (EXT02). Los nutrientes son **cada 100 gramos**: el
 * servidor no sabe de porciones, la cuenta la hace la app (domain/FoodPortion.kt).
 *
 * No se llama `Food` porque ese nombre ya lo usa el enum de preferencias de `Profile`, en este
 * mismo paquete.
 */
data class FoodItem(
    val id: String,
    val name: String,
    val per100g: Nutrients,
)

/** Las cuatro cifras que maneja Nutrition, sin decir de que cantidad son. */
data class Nutrients(
    val kcal: Int,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
)

/** Una pagina de resultados con lo que dice `meta`. */
data class FoodPage(
    val items: List<FoodItem>,
    val page: Int,
    val pageSize: Int,
    val total: Int,
)
