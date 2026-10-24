package com.example.parcial_tp3.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraBarChart
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraStat
import com.example.parcial_tp3.ui.components.VoraStatRow
import com.example.parcial_tp3.ui.components.VoraTopBarAction
import com.example.parcial_tp3.ui.components.VoraTopBarLarge
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/** Progress / 05 — Health Trends (frame 1:2156). Se llega desde "Activity · View >" de 02. */
@Composable
fun HealthTrendsScreen(
    state: HealthTrends,
    onBack: () -> Unit,
    onMore: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        VoraTopBarLarge(
            title = stringResource(R.string.progress_health_title),
            subtitle = stringResource(R.string.progress_health_subtitle),
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
            // el frame deja 22 entre el circulo de volver y el contenido; VoraTopBarLarge deja 12
            modifier = Modifier.padding(
                start = VoraTheme.spacing.screen,
                top = VoraTheme.spacing.s10,
                end = VoraTheme.spacing.screen,
                bottom = VoraTheme.spacing.s24,
            ),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
        ) {
            VoraStatRow(
                stats = state.metrics.map { VoraStat(stringResource(it.label), stringResource(it.value)) },
                alignStart = true,
            )
            StepsCard(state)
            ProgressInsight(stringResource(state.insight))
            Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                ProgressSectionLabel(stringResource(R.string.progress_health_signals))
                state.signals.forEach { SignalRow(it) }
            }
        }
    }
}

/** "Weekly Steps Trend": los días que llegan a la meta en verde oscuro, el resto en menta. */
@Composable
private fun StepsCard(state: HealthTrends) {
    VoraCard(modifier = Modifier.fillMaxWidth(), radius = VoraTheme.radius.r20) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.progress_health_chart_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.progress_health_goal, state.stepGoal),
                style = VoraTheme.text.fieldLabel,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        VoraBarChart(
            values = state.steps.map { it.toFloat() },
            labels = stringArrayResource(R.array.workout_week_initials).toList(),
            description = stringResource(R.string.progress_health_chart_description),
            highlighted = state.steps.indices.filter { state.steps[it] >= state.stepGoal }.toSet(),
            barWidth = VoraTheme.sizes.barWidthNarrow,
            // la barra mas alta mide 71 (nodo 1:2209), casi el alto por defecto de 72
            modifier = Modifier.padding(top = VoraTheme.spacing.s20),
        )
    }
}

/** Fila de "HABIT SIGNALS": ícono y nombre a la izquierda, la tendencia a la derecha. */
@Composable
private fun SignalRow(signal: HabitSignal) {
    val colors = MaterialTheme.colorScheme
    val trendColor = if (signal.improving) VoraTheme.colors.success else colors.onSurfaceVariant
    VoraCard(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
        padding = VoraTheme.spacing.s14,
        radius = VoraTheme.radius.r12,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        ) {
            Icon(
                painter = painterResource(signal.kind.icon()),
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(VoraTheme.sizes.iconSmall),
            )
            Text(
                text = stringResource(signal.title),
                style = MaterialTheme.typography.labelLarge,
                color = colors.onSurface,
                modifier = Modifier.weight(1f),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6),
            ) {
                Text(text = stringResource(signal.trend), style = MaterialTheme.typography.labelMedium, color = trendColor)
                Icon(
                    painter = painterResource(if (signal.improving) R.drawable.ic_trending_up else R.drawable.ic_activity),
                    contentDescription = null,
                    tint = trendColor,
                    modifier = Modifier.size(VoraTheme.sizes.chevronSmall),
                )
            }
        }
    }
}

private fun SignalKind.icon(): Int = when (this) {
    SignalKind.SLEEP -> R.drawable.ic_bed
    SignalKind.MOVEMENT -> R.drawable.ic_navigation
    SignalKind.WORKOUT -> R.drawable.ic_tab_workout
    SignalKind.PROTEIN -> R.drawable.ic_tab_nutrition
}

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun HealthTrendsScreenPreview() {
    Parcialtp3Theme {
        HealthTrendsScreen(state = progressDemoState().health, onBack = {}, onMore = {})
    }
}
