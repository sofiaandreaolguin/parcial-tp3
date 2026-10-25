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

@HiltViewModel
class ProfileDashboardViewModel @Inject constructor(
    repository: ProfileRepository,
) : ViewModel() {

    val uiState: StateFlow<ProfileDashboardUiState> = repository.profile
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = repository.profile.value.toUiState(),
        )

    fun onRetry() {}

    private fun Profile.toUiState() = ProfileDashboardUiState(
        fullName = "$firstName $lastName",
        // ordenados como en Goals & Preferences, no como se fueron eligiendo
        goals = goals.sorted(),
        experience = experience,
        trainingDays = trainingDays,
        workoutType = workoutType,
    )
}
