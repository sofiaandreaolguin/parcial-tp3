package com.example.parcial_tp3.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Fila de Progress: tarjeta blanca con título y subtítulo a la izquierda y [trailing] a la
 * derecha. No es `VoraListRow` porque ésa trae fijas la flecha gris, el toque y otras medidas.
 *
 * Por defecto mide como las filas Workout / Nutrition / Activity de 02 (radio 16, relleno 16,
 * título 15, nodo 1:1834). [compact] es la de "Exercise Progress" de 03 (radio 12, relleno 14,
 * título 14, nodo 1:1984).
 *
 * @param icon si viene, va en un círculo de 40 a la izquierda.
 */
@Composable
fun ProgressRow(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    compact: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    VoraCard(
        modifier = modifier.fillMaxWidth(),
        padding = if (compact) VoraTheme.spacing.s14 else VoraTheme.spacing.s16,
        radius = if (compact) VoraTheme.radius.r12 else VoraTheme.radius.r16,
        onClick = onClick,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(VoraTheme.sizes.areaIcon)
                        .background(colors.background, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painter = icon, contentDescription = null, tint = colors.primary, modifier = Modifier.size(VoraTheme.sizes.icon))
                }
                Spacer(Modifier.width(VoraTheme.spacing.s12))
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2),
            ) {
                Text(
                    text = title,
                    style = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface,
                )
                Text(text = subtitle, style = VoraTheme.text.footnote, color = colors.onSurfaceVariant)
            }
            Spacer(Modifier.width(VoraTheme.spacing.s8))
            trailing()
        }
    }
}
