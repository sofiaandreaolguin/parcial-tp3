package com.example.parcial_tp3.ui.screens.workout

import com.example.parcial_tp3.data.repository.WorkoutDetailRepository
import com.example.parcial_tp3.domain.model.WorkoutDetail
import kotlinx.coroutines.CompletableDeferred

/**
 * Repositorio falso: guarda los ids que le piden y retiene la respuesta hasta que el test la
 * completa, para poder ver el estado Loading. La primera llamada usa respuesta; las siguientes, la
 * proxima encolada y, si no hay ninguna, otra vez respuesta (asi varios ViewModels comparten una).
 */
class FakeWorkoutDetailRepository : WorkoutDetailRepository {

    val respuesta = CompletableDeferred<Result<WorkoutDetail>>()
    val idsPedidos = mutableListOf<String>()

    private val encoladas = ArrayDeque<CompletableDeferred<Result<WorkoutDetail>>>()

    /** Guarda una respuesta para la proxima llamada despues de la primera (el reintento). */
    fun encolarRespuesta(): CompletableDeferred<Result<WorkoutDetail>> =
        CompletableDeferred<Result<WorkoutDetail>>().also { encoladas.addLast(it) }

    override suspend fun getWorkoutDetail(id: String): Result<WorkoutDetail> {
        idsPedidos += id
        val siguiente = if (idsPedidos.size == 1) respuesta else encoladas.removeFirstOrNull() ?: respuesta
        return siguiente.await()
    }
}
