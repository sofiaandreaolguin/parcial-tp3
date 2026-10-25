package com.example.parcial_tp3.ui.screens.workout

import com.example.parcial_tp3.data.repository.WorkoutRepository
import com.example.parcial_tp3.domain.model.Workout
import kotlinx.coroutines.CompletableDeferred

/**
 * Repositorio falso: cuenta las llamadas y retiene la respuesta hasta que el test la completa,
 * para poder ver el estado Loading. La primera llamada usa respuesta; las siguientes, la proxima
 * encolada y, si no hay ninguna, otra vez respuesta.
 */
class FakeWorkoutRepository : WorkoutRepository {

    val respuesta = CompletableDeferred<Result<List<Workout>>>()
    var llamadas = 0
        private set

    private val encoladas = ArrayDeque<CompletableDeferred<Result<List<Workout>>>>()

    /** Guarda una respuesta para la proxima llamada despues de la primera (el reintento). */
    fun encolarRespuesta(): CompletableDeferred<Result<List<Workout>>> =
        CompletableDeferred<Result<List<Workout>>>().also { encoladas.addLast(it) }

    override suspend fun getWorkouts(): Result<List<Workout>> {
        llamadas++
        val siguiente = if (llamadas == 1) respuesta else encoladas.removeFirstOrNull() ?: respuesta
        return siguiente.await()
    }
}
