package com.example.parcial_tp3.data.remote.dto

/**
 * `GET /v1/foods?q=&page=&pageSize=` (EXT02). Forma comprobada el 26/10 contra el servidor.
 *
 * Todo anulable a proposito: Gson deja en null lo que falta en el JSON, y un cuerpo incompleto
 * tiene que terminar en AppError.INVALID_DATA (C6), no en un campo con basura.
 */
data class FoodsResponseDto(
    val data: List<FoodDto>?,
    val meta: PageMetaDto?,
)

data class FoodDto(
    val id: String?,
    val name: String?,
    val nutritionPer100g: NutrientsDto?,
)

data class NutrientsDto(
    val kcal: Int?,
    val proteinG: Double?,
    val carbsG: Double?,
    val fatG: Double?,
)

data class PageMetaDto(
    val page: Int?,
    val pageSize: Int?,
    val total: Int?,
)
