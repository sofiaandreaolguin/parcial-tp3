package com.example.parcial_tp3.ui.screens.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.WorkoutDetailRepository
import com.example.parcial_tp3.ui.navigation.Rutas
import com.example.parcial_tp3.ui.screens.workout.ActiveWorkoutUiState.Content
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Sesion simulada que comparten 05 y 07: no se guarda ni tiene endpoint. El workoutId llega por la
 * ruta (NAV04). Spec: specs/funcionales/sesion-de-entrenamiento-demo.md.
 */
@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WorkoutDetailRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ActiveWorkoutUiState>(ActiveWorkoutUiState.Loading)
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    // Un solo descanso a la vez: arrancar otro, Skip y Finish cancelan el anterior
    private var descanso: Job? = null

    // Como en el detalle, la carga arranca una sola vez en init: rotar no repite la consulta.
    init {
        val workoutId = savedStateHandle.get<String>(Rutas.ARG_WORKOUT_ID)
        if (workoutId.isNullOrBlank()) {
            _uiState.value = ActiveWorkoutUiState.Error(R.string.details_error)
        } else {
            viewModelScope.launch {
                _uiState.value = repository.getWorkoutDetail(workoutId).fold(
                    onSuccess = { detail ->
                        // Sin ejercicios, 05 no tendria nada para mostrar (C5)
                        if (detail.exercises.isEmpty()) ActiveWorkoutUiState.Error(R.string.details_error) else Content(detail)
                    },
                    onFailure = { ActiveWorkoutUiState.Error(R.string.details_error) },
                )
            }
        }
    }

    fun onRetry() {}

    fun onCompleteSet() {
        val actual = _uiState.value as? Content ?: return
        if (actual.allSetsDone) return
        val ejercicio = actual.currentExercise
        val ultimaDelEjercicio = actual.setIndex + 1 == ejercicio.sets
        val ultimaDeTodo = ultimaDelEjercicio && actual.exerciseIndex == actual.detail.exercises.lastIndex
        val pasaAlSiguiente = ultimaDelEjercicio && !ultimaDeTodo
        // El descanso es del ejercicio recien terminado; despues de la ultima serie no hay descanso
        _uiState.value = actual.copy(
            exerciseIndex = if (pasaAlSiguiente) actual.exerciseIndex + 1 else actual.exerciseIndex,
            setIndex = if (pasaAlSiguiente) 0 else actual.setIndex + 1,
            restRemainingSeconds = if (ultimaDeTodo) 0 else ejercicio.restSeconds.coerceAtLeast(0),
            completedSets = actual.completedSets + CompletedSet(ejercicio.id, ejercicio.reps, actual.weightKg),
        )
        if (enDescanso()) arrancarDescanso() else cortarDescanso()
    }

    fun onAddRest() {
        if (enDescanso()) actualizar { it.copy(restRemainingSeconds = it.restRemainingSeconds + DESCANSO_EXTRA_SEGUNDOS) }
    }

    fun onSkipRest() = cortarDescanso()

    fun onIncreaseWeight() = actualizar { it.copy(weightKg = it.weightKg + PASO_PESO_KG) }

    fun onDecreaseWeight() = actualizar { it.copy(weightKg = (it.weightKg - PASO_PESO_KG).coerceAtLeast(0.0)) }

    // 07 se abre encima de 05: si se vuelve con el atras del sistema, la sesion sigue (C26)
    fun onFinish() = cortarDescanso()

    fun onRate(rating: WorkoutRating) = actualizar { it.copy(rating = rating) }

    // Lee lo que queda del estado en cada vuelta, asi +30s suma sin reiniciar la cuenta
    private fun arrancarDescanso() {
        descanso?.cancel()
        descanso = viewModelScope.launch {
            while (enDescanso()) {
                delay(1_000)
                actualizar { it.copy(restRemainingSeconds = (it.restRemainingSeconds - 1).coerceAtLeast(0)) }
            }
        }
    }

    private fun cortarDescanso() {
        descanso?.cancel()
        descanso = null
        actualizar { it.copy(restRemainingSeconds = 0) }
    }

    private fun enDescanso() = (_uiState.value as? Content)?.isResting == true

    // En Loading y en Error los eventos no hacen nada (C22)
    private fun actualizar(cambio: (Content) -> Content) {
        val actual = _uiState.value as? Content ?: return
        _uiState.value = cambio(actual)
    }

    private companion object {
        // Lo eligio Hector (disco mas chico habitual); el Figma no lo dice
        const val PASO_PESO_KG = 2.5
        // Boton "+30s" del frame 05
        const val DESCANSO_EXTRA_SEGUNDOS = 30
    }
}
