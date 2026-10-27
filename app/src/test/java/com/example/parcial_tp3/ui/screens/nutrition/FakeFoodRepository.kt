package com.example.parcial_tp3.ui.screens.nutrition

import com.example.parcial_tp3.data.repository.FoodRepository
import com.example.parcial_tp3.domain.model.FoodPage
import kotlinx.coroutines.CompletableDeferred

/**
 * Catalogo falso. Guarda cada texto consultado para poder contar las consultas (el debounce de
 * C20 y el "no vuelve a consultar" de C27), y puede retener la respuesta para mirar el estado
 * de carga antes de que llegue, igual que FakeCoachRepository.
 */
class FakeFoodRepository : FoodRepository {

    val consultas = mutableListOf<String>()
    var resultado: Result<FoodPage> = Result.success(FoodPage(emptyList(), 1, 50, 0))

    /** Si no es null, la proxima consulta espera a que el test la complete. */
    var retenida: CompletableDeferred<Result<FoodPage>>? = null

    override suspend fun searchFoods(q: String, page: Int): Result<FoodPage> {
        consultas += q
        retenida?.let { return it.await() }
        return resultado
    }
}
