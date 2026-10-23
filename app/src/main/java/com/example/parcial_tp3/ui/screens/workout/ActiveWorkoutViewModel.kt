package com.example.parcial_tp3.ui.screens.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.parcial_tp3.data.repository.WorkoutDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// TODO(implementar): firmas vacias de la sesion de tests en rojo; la logica va con la skill implementar.
@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WorkoutDetailRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ActiveWorkoutUiState>(ActiveWorkoutUiState.Loading)
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    fun onCompleteSet() {}

    fun onAddRest() {}

    fun onSkipRest() {}

    fun onIncreaseWeight() {}

    fun onDecreaseWeight() {}

    fun onFinish() {}

    fun onRate(rating: WorkoutRating) {}
}
