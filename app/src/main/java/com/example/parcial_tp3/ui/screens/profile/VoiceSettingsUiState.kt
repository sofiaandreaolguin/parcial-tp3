package com.example.parcial_tp3.ui.screens.profile

import com.example.parcial_tp3.domain.model.CoachingStyle
import com.example.parcial_tp3.domain.model.ResponseStyle

data class VoiceSettingsUiState(
    val coachingStyle: CoachingStyle,
    val responseStyle: ResponseStyle,
    val speakAloud: Boolean,
    val autoPlay: Boolean,
    val allowInterruptions: Boolean,
    val voiceFeedback: Boolean,
    val hapticFeedback: Boolean,
)
