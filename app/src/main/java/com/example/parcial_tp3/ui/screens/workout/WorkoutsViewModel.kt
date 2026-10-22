package com.example.parcial_tp3.ui.screens.workout

import androidx.lifecycle.ViewModel
import com.example.parcial_tp3.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val repository: WorkoutRepository,
) : ViewModel() {

    // Firma vacia (tests en rojo): la carga en init la escribe la skill implementar.
    val uiState: StateFlow<WorkoutsUiState> = MutableStateFlow(WorkoutsUiState.Loading)
}
