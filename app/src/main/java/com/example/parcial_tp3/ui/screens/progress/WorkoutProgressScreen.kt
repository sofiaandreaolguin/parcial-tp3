package com.example.parcial_tp3.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraBarChart
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraSegmentedControl
import com.example.parcial_tp3.ui.components.VoraStat
import com.example.parcial_tp3.ui.components.VoraStatRow
import com.example.parcial_tp3.ui.components.VoraTopBarAction
import com.example.parcial_tp3.ui.components.VoraTopBarLarge
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/** Progress / 03 — Workout Progress (frame 1:1904). Se llega desde "Workout · View >" de 02. */
@Composable
fun WorkoutProgressScreen(
    state: WorkoutProgress,
    onBack: () -> Unit,
    onMore: () -> Unit,
    onPeriod: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        VoraTopBarLarge(
            title = stringResource(R.string.progress_workout_title),
            onBack = onBack,
            actions = {
                VoraTopBarAction(
                    icon = painterResource(R.drawable.ic_more_horizontal),
                    contentDescription = stringResource(R.string.progress_more),
                    onClick = onMore,
                )
            },
        )
        Column(
            modifier = Modifier.padding(
                start = VoraTheme.spacing.screen,
                end = VoraTheme.spacing.screen,
                bottom = VoraTheme.spacing.s24,
            ),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
        ) {
            // fijo en Week: los otros períodos no tienen datos y avisan "modo demostración"
            VoraSegmentedControl(
                options = listOf(
                    stringResource(R.string.progress_period_week),
                    stringResource(R.string.progress_period_month),
                    stringResource(R.string.progress_period_3_months),
                ),
                selectedIndex = 0,
                onSelect = { if (it != 0) onPeriod() },
            )
            VoraStatRow(state.metrics.map { VoraStat(stringResource(it.label), stringResource(it.value)) })
            VolumeCard(state)
            PersonalBestCard(state.personalBest)
            InsightRow(stringResource(state.insight))
            Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                Text(
                    text = stringResource(R.string.progress_workout_exercises),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = VoraTheme.colors.textMuted,
                )
                state.exercises.forEach { exercise ->
                    ProgressRow(
                        title = stringResource(exercise.name),
                        subtitle = stringResource(exercise.detail),
                        compact = true,
                        trailing = { TrendBadge(stringResource(exercise.change), exercise.improved) },
                    )
                }
            }
        }
    }
}

/** "Training Volume": título, cambio contra la semana pasada y las barras de lunes a viernes. */
@Composable
private fun VolumeCard(state: WorkoutProgress) {
    VoraCard(modifier = Modifier.fillMaxWidth(), radius = VoraTheme.radius.r20) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2),
            ) {
                Text(
                    text = stringResource(R.string.progress_workout_chart_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.progress_workout_chart_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = stringResource(state.volumeChange),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        VoraBarChart(
            values = state.volumeKg,
            labels = stringArrayResource(R.array.workout_week_initials).take(state.volumeKg.size),
            description = stringResource(R.string.progress_workout_chart_description),
            // la barra mas alta mide 72 (nodo 1:1965), el alto por defecto; con 20 arriba la
            // tarjeta da los 180 del nodo 1:1940
            modifier = Modifier.padding(top = VoraTheme.spacing.s20),
        )
    }
}

/** "PERSONAL BEST": fondo casi blanco con borde menta y el trofeo en un círculo verde claro. */
@Composable
private fun PersonalBestCard(best: PersonalBest) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(VoraTheme.radius.r16)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {}
            .background(VoraTheme.colors.highlight, shape)
            .border(VoraTheme.sizes.border, colors.secondary, shape)
            .padding(VoraTheme.spacing.s16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
    ) {
        Box(
            modifier = Modifier
                .size(VoraTheme.sizes.areaIcon)
                .background(VoraTheme.colors.highlightIcon, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_trophy),
                contentDescription = null,
                tint = VoraTheme.colors.success,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2)) {
            Text(
                text = stringResource(R.string.progress_workout_best_label),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = VoraTheme.colors.success,
            )
            Text(
                text = stringResource(best.exercise),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface,
            )
            val detail = stringResource(best.detail)
            val improvement = stringResource(best.improvement)
            Text(
                text = buildAnnotatedString {
                    append("$detail ")
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = VoraTheme.colors.success)) {
                        append(improvement)
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
            )
        }
    }
}

/**
 * La cita de VORA con el orbe de 40 (nodo 1:1976). No es `VoraInfoMessage`: ese lleva un
 * ícono en vez del orbe y el borde menta al 40 %, y acá el borde es menta entera.
 */
@Composable
private fun InsightRow(text: String) {
    val shape = RoundedCornerShape(VoraTheme.radius.r12)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VoraTheme.colors.mintTranslucent, shape)
            .border(VoraTheme.sizes.border, MaterialTheme.colorScheme.secondary, shape)
            .padding(VoraTheme.spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
    ) {
        VoraOrb(size = VoraTheme.sizes.orbInsight)
        Text(text = text, style = VoraTheme.text.quote, color = MaterialTheme.colorScheme.primary)
    }
}

/** "↑ 2.5 kg" en menta si mejoró; "→ Stable" en gris si no. */
@Composable
private fun TrendBadge(text: String, improved: Boolean) {
    val colors = MaterialTheme.colorScheme
    Text(
        text = text,
        style = VoraTheme.text.fieldLabel,
        color = if (improved) colors.primary else colors.onSurfaceVariant,
        modifier = Modifier
            .background(
                if (improved) VoraTheme.colors.mintTranslucent else colors.background,
                RoundedCornerShape(VoraTheme.radius.pill),
            )
            .padding(horizontal = VoraTheme.spacing.s8, vertical = VoraTheme.spacing.s4),
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1000)
@Composable
private fun WorkoutProgressScreenPreview() {
    Parcialtp3Theme {
        WorkoutProgressScreen(state = progressDemoState().workout, onBack = {}, onMore = {}, onPeriod = {})
    }
}
