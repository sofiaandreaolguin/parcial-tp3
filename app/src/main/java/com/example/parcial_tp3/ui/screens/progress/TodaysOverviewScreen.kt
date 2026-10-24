package com.example.parcial_tp3.ui.screens.progress

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.WorkoutLevel
import com.example.parcial_tp3.ui.components.VoraAdvice
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraProgressRing
import com.example.parcial_tp3.ui.components.VoraStatusDot
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/** Progress / 01 — Today's Overview (frame 1:1682). Base de la pestaña Progress. */
@Composable
fun TodaysOverviewScreen(
    state: TodaysOverview,
    onStartWorkout: () -> Unit,
    onCategory: (CategoryKind) -> Unit,
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
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s20),
    ) {
        Header(stringResource(state.date))
        CompletionCard(state, onCategory)
        VoraAdvice(label = stringResource(R.string.progress_overview_advice_label), text = stringResource(state.advice))
        Breakdowns(state, onStartWorkout)
    }
}

@Composable
private fun Header(date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2)) {
        Text(text = date, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = stringResource(R.string.progress_overview_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics { heading() },
        )
    }
}

/** "DAILY COMPLETION 72%" con su anillo y las cuatro categorías. */
@Composable
private fun CompletionCard(state: TodaysOverview, onCategory: (CategoryKind) -> Unit) {
    val colors = MaterialTheme.colorScheme
    VoraCard(
        modifier = Modifier.fillMaxWidth(),
        padding = VoraTheme.spacing.s20,
        radius = VoraTheme.radius.r20,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4),
            ) {
                Text(
                    text = stringResource(R.string.progress_overview_completion),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.progress_percent, state.dailyPercent),
                    style = MaterialTheme.typography.headlineLarge,
                    color = colors.onSurface,
                )
            }
            VoraProgressRing(
                progress = state.dailyPercent / 100f,
                size = VoraTheme.sizes.ringOverview,
                strokeWidth = VoraTheme.sizes.ringStrokeOverview,
            )
        }
        Spacer(Modifier.height(VoraTheme.spacing.s16))
        HorizontalDivider(thickness = VoraTheme.sizes.border, color = colors.outline)
        Spacer(Modifier.height(VoraTheme.spacing.s16))
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
            state.categories.forEach { CategoryRow(it, onClick = { onCategory(it.kind) }) }
        }
    }
}

/** "● Nutrition Target   1,420 / 2,100 kcal 78%": el detalle tenue y el porcentaje fuerte. */
@Composable
private fun CategoryRow(category: OverviewCategory, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val percent = stringResource(R.string.progress_percent, category.percent)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VoraStatusDot(color = category.kind.color())
        Spacer(Modifier.width(VoraTheme.spacing.s8))
        Text(
            text = stringResource(category.label),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = buildAnnotatedString {
                append(stringResource(category.detail))
                append(" ")
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = colors.onSurface)) { append(percent) }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun Breakdowns(state: TodaysOverview, onStartWorkout: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
        Text(
            text = stringResource(R.string.progress_overview_breakdowns),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics { heading() },
        )
        WorkoutCard(state, onStartWorkout)
        GoalCard(state)
    }
}

@Composable
private fun WorkoutCard(state: TodaysOverview, onStartWorkout: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val workout = state.workout
    VoraCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.progress_overview_workout_label),
                style = VoraTheme.text.label,
                color = VoraTheme.colors.textMuted,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.progress_overview_not_started),
                style = VoraTheme.text.fieldLabel,
                color = colors.error,
            )
        }
        Spacer(Modifier.height(VoraTheme.spacing.s12))
        Text(text = workout.name, style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
        Spacer(Modifier.height(VoraTheme.spacing.s4))
        Text(
            text = stringResource(
                R.string.progress_workout_meta,
                pluralStringResource(R.plurals.workout_summary, workout.exerciseCount, workout.durationMinutes, workout.exerciseCount),
                stringResource(workout.level.label()),
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
        )
        Spacer(Modifier.height(VoraTheme.spacing.s12))
        VoraButton(
            text = stringResource(R.string.progress_overview_start_workout),
            onClick = onStartWorkout,
            size = VoraButtonSize.Small,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** "CURRENT GOAL | WEEKLY TARGET": dos columnas iguales, la segunda con borde a la izquierda. */
@Composable
private fun GoalCard(state: TodaysOverview) {
    VoraCard(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            GoalColumn(R.string.progress_overview_current_goal, stringResource(state.goal), Modifier.weight(1f))
            Spacer(Modifier.width(VoraTheme.spacing.s16))
            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                thickness = VoraTheme.sizes.border,
                color = MaterialTheme.colorScheme.outline,
            )
            Spacer(Modifier.width(VoraTheme.spacing.s16))
            GoalColumn(
                R.string.progress_overview_weekly_target,
                stringResource(R.string.progress_overview_weekly_value, state.weeklyDone, state.weeklyTarget),
                Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun GoalColumn(@StringRes label: Int, value: String, modifier: Modifier) {
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4),
    ) {
        Text(text = stringResource(label), style = MaterialTheme.typography.bodySmall, color = VoraTheme.colors.textMuted)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun CategoryKind.color(): Color = when (this) {
    CategoryKind.WORKOUT -> MaterialTheme.colorScheme.error
    CategoryKind.NUTRITION -> MaterialTheme.colorScheme.secondary
    CategoryKind.ACTIVITY -> MaterialTheme.colorScheme.primary
    CategoryKind.HYDRATION -> VoraTheme.colors.cta
}

@StringRes
private fun WorkoutLevel.label(): Int = when (this) {
    WorkoutLevel.BEGINNER -> R.string.workout_level_beginner
    WorkoutLevel.INTERMEDIATE -> R.string.workout_level_intermediate
    WorkoutLevel.ADVANCED -> R.string.workout_level_advanced
}

@Preview(showBackground = true, widthDp = 390, heightDp = 850)
@Composable
private fun TodaysOverviewScreenPreview() {
    Parcialtp3Theme {
        TodaysOverviewScreen(state = progressDemoState().overview, onStartWorkout = {}, onCategory = {})
    }
}
