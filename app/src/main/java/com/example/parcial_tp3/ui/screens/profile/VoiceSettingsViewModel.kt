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

/** Solo muestra los ajustes de voz: cambiarlos y guardarlos llega cuando Profile tenga persistencia. */
@HiltViewModel
class VoiceSettingsViewModel @Inject constructor(
    repository: ProfileRepository,
) : ViewModel() {

    val uiState: StateFlow<VoiceSettingsUiState> = repository.profile
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = repository.profile.value.toUiState(),
        )

    private fun Profile.toUiState() = VoiceSettingsUiState(
        coachingStyle = coachingStyle,
        responseStyle = responseStyle,
        speakAloud = speakAloud,
        autoPlay = autoPlay,
        allowInterruptions = allowInterruptions,
        voiceFeedback = voiceFeedback,
        hapticFeedback = hapticFeedback,
    )
}
