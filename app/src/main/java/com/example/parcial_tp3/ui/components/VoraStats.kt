package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/** Un dato de una [VoraStatRow]: etiqueta chica arriba y valor abajo. */
data class VoraStat(val label: String, val value: String)

/**
 * Fila de datos en una tarjeta blanca, con divisores verticales entre columnas
 * ("SESSIONS 4 | VOLUME 8,420 kg | AVG TIME 41 min", "STEPS | DISTANCE | ACTIVE KCAL").
 * Todas las columnas tienen el mismo ancho.
 *
 * @param stats las etiquetas llegan de strings.xml ya en mayusculas.
 */
@Composable
fun VoraStatRow(
    stats: List<VoraStat>,
    modifier: Modifier = Modifier,
) {
    VoraCard(modifier = modifier.fillMaxWidth(), padding = VoraTheme.spacing.s12) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            stats.forEachIndexed { i, stat ->
                if (i > 0) {
                    VerticalDivider(
                        modifier = Modifier.height(VoraTheme.sizes.statDivider),
                        thickness = VoraTheme.sizes.border,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .semantics(mergeDescendants = true) {},
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stat.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = VoraTheme.colors.textMuted,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(VoraTheme.spacing.s2))
                    Text(
                        text = stat.value,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta gris de un dato destacado ("Workouts completed / 4 workouts ↑1"): etiqueta,
 * valor grande y, opcional, la tendencia en menta. Va dentro de una tarjeta blanca, de a
 * dos por fila.
 *
 * @param trend por ejemplo "↑ 6%", de strings.xml; null si no tiene.
 */
@Composable
fun VoraStatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trend: String? = null,
) {
    Column(
        modifier = modifier
            .semantics(mergeDescendants = true) {}
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(VoraTheme.radius.r12))
            .padding(VoraTheme.spacing.s12),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4),
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (trend != null) {
                Spacer(Modifier.width(VoraTheme.spacing.s8))
                Text(
                    text = trend,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = VoraTheme.spacing.s2),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraStatsPreview() {
    Parcialtp3Theme {
        val a = stringResource(R.string.tab_workout)
        val b = stringResource(R.string.tab_progress)
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(VoraTheme.spacing.screen),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        ) {
            VoraStatRow(listOf(VoraStat(a.uppercase(), b), VoraStat(b.uppercase(), a), VoraStat(a.uppercase(), b)))
            VoraCard(Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                    VoraStatTile(a, b, Modifier.weight(1f), trend = a)
                    VoraStatTile(b, a, Modifier.weight(1f))
                }
            }
        }
    }
}
