package com.example.parcial_tp3.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraSegmentedControl
import com.example.parcial_tp3.ui.components.VoraStatTile
import com.example.parcial_tp3.ui.components.VoraTag
import com.example.parcial_tp3.ui.components.VoraTagTone
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/** Progress / 02 — Dashboard (frame 1:1775). Se llega desde "Start Workout" de 01. */
@Composable
fun ProgressDashboardScreen(
    state: ProgressDashboard,
    onPeriod: () -> Unit,
    onArea: (CategoryKind) -> Unit,
    onWeeklyReview: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(
                start = VoraTheme.spacing.screen,
                end = VoraTheme.spacing.screen,
                top = VoraTheme.spacing.s12,
                bottom = VoraTheme.spacing.s24,
            ),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
    ) {
        Header()
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
        OverviewCard(state.stats)
        InsightPanel(stringResource(state.insight), onWeeklyReview)
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
            state.areas.forEach { area ->
                ProgressRow(
                    title = stringResource(area.title),
                    subtitle = stringResource(area.detail),
                    icon = painterResource(area.kind.icon()),
                    onClick = { onArea(area.kind) },
                    trailing = { ViewLink() },
                )
            }
        }
        GoalCard(state)
    }
}

@Composable
private fun Header() {
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4)) {
        Text(
            text = stringResource(R.string.progress_dash_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.progress_dash_subtitle),
            style = VoraTheme.text.advice,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** "THIS WEEK OVERVIEW": grilla de 2 × 2, separada 8; las dos de cada fila miden lo mismo. */
@Composable
private fun OverviewCard(stats: List<DashboardStat>) {
    VoraCard(modifier = Modifier.fillMaxWidth(), radius = VoraTheme.radius.r20) {
        Text(
            text = stringResource(R.string.progress_dash_overview),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = VoraTheme.colors.textMuted,
        )
        Spacer(Modifier.height(VoraTheme.spacing.s14))
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
            stats.chunked(2).forEach { fila ->
                EqualHeightRow(gap = VoraTheme.spacing.s8) {
                    fila.forEach { stat ->
                        VoraStatTile(
                            label = stringResource(stat.label),
                            value = stringResource(stat.value),
                            trend = stringResource(stat.trend),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fila de hijos del mismo ancho, todos con la altura del más alto. No usa IntrinsicSize.Min
 * porque el FlowRow de [VoraStatTile] informa mal su altura intrínseca y la tendencia se recortaba
 * a 360 dp: mide de verdad una vez y vuelve a componer con la altura fija.
 */
@Composable
private fun EqualHeightRow(gap: Dp, content: @Composable () -> Unit) {
    SubcomposeLayout { constraints ->
        val gapPx = gap.roundToPx()
        val medir = subcompose("medir", content)
        val w = (constraints.maxWidth - gapPx * (medir.size - 1)) / medir.size
        val h = medir.maxOf { it.measure(Constraints.fixedWidth(w)).height }
        val placeables = subcompose("poner", content).map { it.measure(Constraints.fixed(w, h)) }
        layout(constraints.maxWidth, h) {
            placeables.forEachIndexed { i, p -> p.place(i * (w + gapPx), 0) }
        }
    }
}

/** Panel verde "VORA Insight" con la cita y el link a Weekly Review. */
@Composable
private fun InsightPanel(insight: String, onWeeklyReview: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.primary, RoundedCornerShape(VoraTheme.radius.r20))
            .padding(VoraTheme.spacing.s16),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            VoraOrb(size = VoraTheme.sizes.orbInsight, onDark = true)
            Spacer(Modifier.width(VoraTheme.spacing.s8))
            Text(
                text = stringResource(R.string.progress_dash_insight_label),
                style = MaterialTheme.typography.labelLarge,
                color = colors.secondary,
                modifier = Modifier.weight(1f),
            )
            VoraTag(text = stringResource(R.string.progress_dash_consistent), tone = VoraTagTone.LimeOnDark)
        }
        Text(text = insight, style = VoraTheme.text.insight, color = colors.onPrimary)
        Row(
            modifier = Modifier
                .clickable(role = Role.Button, onClick = onWeeklyReview)
                .padding(top = VoraTheme.spacing.s4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.progress_dash_see_review),
                style = MaterialTheme.typography.labelMedium,
                color = VoraTheme.colors.cta,
            )
            Spacer(Modifier.width(VoraTheme.spacing.s6))
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = VoraTheme.colors.cta,
                modifier = Modifier.size(VoraTheme.sizes.chevronSmall),
            )
        }
    }
}

/** "View ›" en verde, a la derecha de cada fila. */
@Composable
private fun ViewLink() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.progress_dash_view),
            style = VoraTheme.text.fieldLabel,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.width(VoraTheme.spacing.s4))
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(VoraTheme.sizes.chevronSmall),
        )
    }
}

/** "ACTIVE GOAL 68% complete": menta translúcida con borde menta. */
@Composable
private fun GoalCard(state: ProgressDashboard) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(VoraTheme.radius.r16)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VoraTheme.colors.mintTranslucent, shape)
            .border(VoraTheme.sizes.border, colors.secondary, shape)
            .padding(VoraTheme.spacing.s16),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_target),
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(VoraTheme.sizes.iconXSmall),
            )
            Spacer(Modifier.width(VoraTheme.spacing.s8))
            Text(
                text = stringResource(R.string.progress_dash_active_goal),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = colors.primary,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.progress_dash_goal_percent, state.goalPercent),
                style = MaterialTheme.typography.labelMedium,
                color = colors.primary,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4)) {
            Text(text = stringResource(state.goal), style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
            Text(text = stringResource(state.goalBody), style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
        }
    }
}

private fun CategoryKind.icon(): Int = when (this) {
    CategoryKind.WORKOUT -> R.drawable.ic_tab_workout
    CategoryKind.NUTRITION -> R.drawable.ic_tab_nutrition
    CategoryKind.ACTIVITY -> R.drawable.ic_tab_progress
    CategoryKind.HYDRATION -> R.drawable.ic_activity
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1100)
@Composable
private fun ProgressDashboardScreenPreview() {
    Parcialtp3Theme {
        ProgressDashboardScreen(state = progressDemoState().dashboard, onPeriod = {}, onArea = {}, onWeeklyReview = {})
    }
}
