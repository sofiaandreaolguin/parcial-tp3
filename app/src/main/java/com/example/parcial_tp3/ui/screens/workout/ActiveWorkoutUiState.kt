package com.example.parcial_tp3.ui.screens.workout

import com.example.parcial_tp3.domain.model.Exercise
import com.example.parcial_tp3.domain.model.WorkoutDetail

/**
 * Estado de la sesion simulada que comparten 05 y 07. Los 45 kg salen del frame 05 ("− 45 kg +").
 * Spec: specs/funcionales/sesion-de-entrenamiento-demo.md.
 */
sealed interface ActiveWorkoutUiState {
    data object Loading : ActiveWorkoutUiState
    data class Content(
        val detail: WorkoutDetail,
        val exerciseIndex: Int = 0,
        // == sets del ultimo ejercicio cuando se termino todo
        val setIndex: Int = 0,
        val weightKg: Double = 45.0,
        // 0 = no esta descansando
        val restRemainingSeconds: Int = 0,
        val completedSets: List<CompletedSet> = emptyList(),
        val rating: WorkoutRating? = null,
    ) : ActiveWorkoutUiState {
        val currentExercise: Exercise get() = detail.exercises[exerciseIndex]
        val isResting: Boolean get() = restRemainingSeconds > 0
        val allSetsDone: Boolean get() =
            exerciseIndex == detail.exercises.lastIndex && setIndex == currentExercise.sets
        val exercisesCompleted: Int get() = if (allSetsDone) detail.exercises.size else exerciseIndex
        val volumeKg: Double get() = completedSets.sumOf { it.reps * it.weightKg }
    }
    data object Error : ActiveWorkoutUiState
}

data class CompletedSet(val exerciseId: String, val reps: Int, val weightKg: Double)

enum class WorkoutRating { GREAT, GOOD, TOUGH, TOO_EASY }
