package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.remote.VoraApi
import com.example.parcial_tp3.domain.model.FoodPage
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

/**
 * El catalogo de alimentos del servidor (EXT02). Spec: busqueda-de-alimentos.md, C1 a C8.
 *
 * No toca Room: lo que se guarda es la comida, y de eso se encarga MealRepository.
 */
class FoodRepositoryImpl @Inject constructor(
    private val api: VoraApi,
) : FoodRepository {

    override suspend fun searchFoods(q: String, page: Int): Result<FoodPage> =
        try {
            val respuesta = api.getFoods(q, page, PAGE_SIZE)
            val cuerpo = respuesta.body()
            when {
                !respuesta.isSuccessful -> Result.failure(AppException(respuesta.aError()))
                cuerpo == null -> Result.failure(AppException(AppError.INVALID_DATA))
                else -> Result.success(cuerpo.toDomain())
            }
        } catch (e: CancellationException) {
            // Con el debounce se cancela una consulta por tecla: si se tradujera a AppError,
            // la pantalla parpadearia en error mientras se escribe (C7).
            throw e
        } catch (e: Exception) {
            Result.failure(AppException(e.toAppError(), e))
        }
}
