package com.example.parcial_tp3.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.data.repository.ProfileRepository
import com.example.parcial_tp3.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Solo muestra las preferencias: elegir y guardar llega cuando Profile tenga persistencia. */
@HiltViewModel
class NutritionPreferencesViewModel @Inject constructor(
    repository: ProfileRepository,
) : ViewModel() {

    val uiState: StateFlow<NutritionPreferencesUiState> = repository.profile
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = repository.profile.value.toUiState(),
        )

    private fun Profile.toUiState() = NutritionPreferencesUiState(
        diet = diet,
        foodsEnjoyed = foodsEnjoyed,
        foodsToAvoid = foodsToAvoid,
        priorities = priorities,
    )
}
