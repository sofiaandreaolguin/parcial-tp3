package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.remote.dto.FoodDto
import com.example.parcial_tp3.data.remote.dto.FoodsResponseDto
import com.example.parcial_tp3.data.remote.dto.NutrientsDto
import com.example.parcial_tp3.domain.model.FoodItem
import com.example.parcial_tp3.domain.model.FoodPage
import com.example.parcial_tp3.domain.model.Nutrients

/**
 * DTO -> modelo (RF06, C6).
 *
 * Si falta un campo lanza IllegalArgumentException, que `toAppError()` traduce a INVALID_DATA:
 * un alimento incompleto hace fallar **toda** la consulta, igual que en el catalogo de
 * entrenamientos. Es preferible a mostrar una tarjeta con ceros que parecen datos.
 */
fun FoodsResponseDto.toDomain(): FoodPage {
    val alimentos = requireNotNull(data) { "foods: la respuesta no trae data" }
    val meta = requireNotNull(meta) { "foods: la respuesta no trae meta" }
    return FoodPage(
        items = alimentos.map { it.toDomain() },
        page = requireNotNull(meta.page) { "meta: falta page" },
        pageSize = requireNotNull(meta.pageSize) { "meta: falta pageSize" },
        // Sin esto, un meta truncado deja total en 0 y la pantalla dice "no se encontro
        // ningun alimento" cuando en realidad la respuesta vino rota.
        total = requireNotNull(meta.total) { "meta: falta total" },
    )
}

private fun FoodDto.toDomain(): FoodItem = FoodItem(
    id = requireNotNull(id) { "food: falta id" },
    name = requireNotNull(name) { "food: falta name" },
    per100g = requireNotNull(nutritionPer100g) { "food: falta nutritionPer100g" }.toDomain(),
)

private fun NutrientsDto.toDomain(): Nutrients = Nutrients(
    kcal = requireNotNull(kcal) { "nutritionPer100g: falta kcal" },
    proteinG = requireNotNull(proteinG) { "nutritionPer100g: falta proteinG" },
    carbsG = requireNotNull(carbsG) { "nutritionPer100g: falta carbsG" },
    fatG = requireNotNull(fatG) { "nutritionPer100g: falta fatG" },
)
