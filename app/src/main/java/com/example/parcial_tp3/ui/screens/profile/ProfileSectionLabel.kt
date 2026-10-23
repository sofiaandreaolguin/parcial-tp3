package com.example.parcial_tp3.ui.screens.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Etiqueta de sección de Profile / 03 a 06: 13 SemiBold (labelMedium), medida en el Figma;
 * VoraSectionLabel es de 10. Los 8 de arriba más el spacedBy(12) de la columna dan los 20 del frame;
 * dentro de una tarjeta ("ACTIVE VOICE") se pasa Modifier sin ese aire.
 */
@Composable
fun ProfileSectionLabel(
    text: String,
    modifier: Modifier = Modifier.padding(top = VoraTheme.spacing.s8),
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = VoraTheme.colors.textMuted,
        modifier = modifier,
    )
}
