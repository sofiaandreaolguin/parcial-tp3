package com.example.parcial_tp3.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import com.example.parcial_tp3.ui.theme.VoraTheme

/** Chips que saltan de línea cuando no entran, separados 8 (Your goals y Nutrition preferences). */
@Composable
fun ProfileChipRow(content: @Composable () -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
    ) { content() }
}
