package com.example.parcial_tp3.ui.screens.home

import com.example.parcial_tp3.data.repository.MealDataException
import com.example.parcial_tp3.data.repository.MealRepository
import com.example.parcial_tp3.domain.model.Meal
import java.time.LocalDate
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * MealRepository en memoria: emite de nuevo cada vez que cambian las comidas, como el Flow del
 * DAO de Room. Guarda la ultima fecha pedida para comprobar que Home lee las de hoy.
 *
 * Para los tests de VC-l tambien anota cada llamada de escritura (antes de esperar o fallar),
 * cuenta las suscripciones con onStart y puede fallar al leer o al escribir.
 */
class FakeMealRepository(comidas: List<Meal> = emptyList()) : MealRepository {

    val meals = MutableStateFlow(comidas)
    var fechaPedida: LocalDate? = null
        private set
    val fechasPedidas = mutableListOf<LocalDate>()
    val idsPedidos = mutableListOf<Long>()
    var suscripcionesPorFecha = 0
        private set
    var suscripcionesPorId = 0
        private set

    val agregadas = mutableListOf<Meal>()
    val eliminadas = mutableListOf<Long>()
    val comidasEliminadas = mutableListOf<Meal>()

    /** Lo tira cada Flow al suscribirse. */
    var errorAlLeer: MealDataException? = null

    /** Lo tira cada escritura, despues de anotarla. */
    var errorAlEscribir: MealDataException? = null

    /** Si no es null, cada escritura espera a que el test lo complete: asi se ve "guardando". */
    var bloqueo: CompletableDeferred<Unit>? = null

    override fun observeMealsByDate(date: LocalDate): Flow<List<Meal>> {
        fechaPedida = date
        fechasPedidas += date
        return meals.map { lista -> lista.filter { it.date == date } }
            .onStart {
                suscripcionesPorFecha++
                errorAlLeer?.let { throw it }
            }
    }

    override fun observeMeal(id: Long): Flow<Meal?> {
        idsPedidos += id
        return meals.map { lista -> lista.firstOrNull { it.id == id } }
            .onStart {
                suscripcionesPorId++
                errorAlLeer?.let { throw it }
            }
    }

    override suspend fun addMeal(meal: Meal): Long {
        agregadas += meal
        antesDeEscribir()
        val id = (meals.value.maxOfOrNull { it.id } ?: 0) + 1
        meals.value = meals.value + meal.copy(id = id)
        return id
    }

    override suspend fun deleteMeal(meal: Meal) {
        eliminadas += meal.id
        comidasEliminadas += meal
        antesDeEscribir()
        meals.value = meals.value.filterNot { it.id == meal.id }
    }

    private suspend fun antesDeEscribir() {
        bloqueo?.await()
        errorAlEscribir?.let { throw it }
    }
}
