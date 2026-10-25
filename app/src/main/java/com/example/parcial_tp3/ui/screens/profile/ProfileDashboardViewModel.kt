package com.example.parcial_tp3.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.ProfileError
import com.example.parcial_tp3.data.repository.ProfileRepository
import com.example.parcial_tp3.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileDashboardViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : ViewModel() {

    val uiState: StateFlow<ProfileDashboardUiState> = combine(repository.profile, repository.loadError) { profile, error ->
        profile.toUiState(error)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = repository.profile.value.toUiState(repository.loadError.value),
    )

    fun onRetry() {
        viewModelScope.launch { repository.reload() }
    }

    private fun Profile.toUiState(error: ProfileError?) = ProfileDashboardUiState(
        fullName = "$firstName $lastName",
        // ordenados como en Goals & Preferences, no como se fueron eligiendo
        goals = goals.sorted(),
        experience = experience,
        trainingDays = trainingDays,
        workoutType = workoutType,
        loadError = error?.let { R.string.profile_error_load },
    )
}
