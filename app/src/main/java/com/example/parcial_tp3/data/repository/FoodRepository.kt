package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.domain.model.FoodPage

/**
 * Cuantos alimentos se piden por consulta. El catalogo tiene 12 y el servidor acepta hasta 50,
 * asi que entran todos en una: por eso EXT02 no lleva paginacion (ver la spec).
 */
const val PAGE_SIZE = 50

/** El catalogo de alimentos del servidor (EXT02). No se cachea: sin conexion no se busca. */
interface FoodRepository {
    suspend fun searchFoods(q: String, page: Int): Result<FoodPage>
}
