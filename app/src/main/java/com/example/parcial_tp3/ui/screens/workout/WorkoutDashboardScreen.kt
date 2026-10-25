package com.example.parcial_tp3.ui.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.Workout
import com.example.parcial_tp3.domain.model.WorkoutLevel
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraCoachNote
import com.example.parcial_tp3.ui.components.VoraInfoMessage
import com.example.parcial_tp3.ui.components.VoraPanel
import com.example.parcial_tp3.ui.components.VoraTag
import com.example.parcial_tp3.ui.components.VoraTagTone
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/** Cómo quedó cada día en "This Week's Activity". */
private enum class DayStatus { Done, Rest, Today }

/** Semana de demostración, la del frame: entrenó lunes, martes y jueves, y hoy es viernes. */
private val SEMANA_DEMO = listOf(
    DayStatus.Done, DayStatus.Done, DayStatus.Rest, DayStatus.Done,
    DayStatus.Today, DayStatus.Rest, DayStatus.Rest,
)

/**
 * Workout / 01. El panel verde muestra el primer entrenamiento del catálogo real; la nota del
 * coach, la semana y el progreso son de demostración.
 */
@Composable
fun WorkoutDashboardScreen(
    state: WorkoutsUiState,
    onWeeklyPlan: () -> Unit,
    onStartWorkout: (workoutId: String) -> Unit,
    onWorkoutDetails: (workoutId: String) -> Unit,
    onViewProgress: () -> Unit,
    onRetry: () -> Unit,
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
        Header(onWeeklyPlan)
        when (state) {
            WorkoutsUiState.Loading -> LoadingCard()
            is WorkoutsUiState.Error -> WorkoutStatusMessage(text = stringResource(state.message), onRetry = onRetry)
            WorkoutsUiState.Empty -> VoraInfoMessage(
                text = stringResource(R.string.workout_empty),
                icon = painterResource(R.drawable.ic_info),
            )
            is WorkoutsUiState.Content -> {
                val featured = state.workouts.firstOrNull()
                if (featured == null) {
                    VoraInfoMessage(
                        text = stringResource(R.string.workout_empty),
                        icon = painterResource(R.drawable.ic_info),
                    )
                } else {
                    TodayPanel(featured, onStartWorkout, onWorkoutDetails)
                }
            }
        }
        VoraCoachNote(
            label = stringResource(R.string.workout_recommends),
            text = stringResource(R.string.workout_recommends_demo),
            featured = true,
        )
        WeekActivityCard()
        ProgressCard(onViewProgress)
    }
}

@Composable
private fun Header(onWeeklyPlan: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2)) {
            Text(
                text = stringResource(R.string.workout_goal),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.workout_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )
        }
        Box(
            modifier = Modifier
                .size(VoraTheme.sizes.circleButton)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(VoraTheme.sizes.border, MaterialTheme.colorScheme.outline, CircleShape)
                .clickable(role = Role.Button, onClick = onWeeklyPlan),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_calendar),
                contentDescription = stringResource(R.string.workout_open_plan),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(VoraTheme.sizes.icon),
            )
        }
    }
}

@Composable
private fun TodayPanel(
    workout: Workout,
    onStartWorkout: (String) -> Unit,
    onWorkoutDetails: (String) -> Unit,
) {
    VoraPanel(
        modifier = Modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RoundedCornerShape(VoraTheme.radius.r20),
                shadow = Shadow(
                    radius = VoraTheme.sizes.panelShadow,
                    color = VoraTheme.colors.panelShadow,
                    offset = DpOffset(x = 0.dp, y = VoraTheme.sizes.panelShadow),
                ),
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.workout_ready),
                style = VoraTheme.text.panelLabel,
                color = MaterialTheme.colorScheme.secondary,
            )
            VoraTag(text = stringResource(workout.level.tagLabel()), tone = VoraTagTone.OnDark)
        }
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4)) {
            Text(text = workout.name, style = VoraTheme.text.panelTitle)
            Text(
                text = pluralStringResource(
                    R.plurals.workout_summary,
                    workout.exerciseCount,
                    workout.durationMinutes,
                    workout.exerciseCount,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = VoraTheme.colors.onDarkMuted,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
            VoraButton(
                text = stringResource(R.string.workout_start),
                onClick = { onStartWorkout(workout.id) },
                modifier = Modifier.weight(1f),
                style = VoraButtonStyle.Cta,
                size = VoraButtonSize.Medium,
            )
            VoraButton(
                text = stringResource(R.string.workout_details),
                onClick = { onWorkoutDetails(workout.id) },
                style = VoraButtonStyle.OutlinedOnDark,
                size = VoraButtonSize.Medium,
            )
        }
    }
}

@Composable
internal fun LoadingCard(text: String = stringResource(R.string.workout_loading)) {
    VoraCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(VoraTheme.sizes.icon),
                strokeWidth = VoraTheme.sizes.borderSelected,
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun WeekActivityCard() {
    val iniciales = stringArrayResource(R.array.workout_week_initials)
    val nombres = stringArrayResource(R.array.workout_week_names)
    VoraCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.workout_week_activity),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(VoraTheme.spacing.s12))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SEMANA_DEMO.zip(iniciales.zip(nombres)).forEach { (status, dia) ->
                DayColumn(initial = dia.first, name = dia.second, status = status)
            }
        }
    }
}

/** [name] es lo que lee TalkBack: con la inicial sola, "T" puede ser martes o jueves. */
@Composable
private fun DayColumn(initial: String, name: String, status: DayStatus) {
    val descripcion = when (status) {
        DayStatus.Done -> stringResource(R.string.workout_day_done_desc, name)
        DayStatus.Rest -> stringResource(R.string.workout_day_rest_desc, name)
        DayStatus.Today -> stringResource(R.string.workout_day_today_desc, name)
    }
    Column(
        modifier = Modifier
            .width(VoraTheme.sizes.dayColumn)
            .semantics(mergeDescendants = true) { contentDescription = descripcion },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6),
    ) {
        Text(text = initial, style = VoraTheme.text.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        val circulo = Modifier
            .size(VoraTheme.sizes.dayCircle)
            .clip(CircleShape)
        when (status) {
            DayStatus.Done -> Box(circulo.background(MaterialTheme.colorScheme.secondary))
            DayStatus.Rest -> Box(
                circulo
                    .background(MaterialTheme.colorScheme.background)
                    .border(VoraTheme.sizes.border, MaterialTheme.colorScheme.outline, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.workout_day_rest),
                    style = VoraTheme.text.micro,
                    color = VoraTheme.colors.textMuted,
                )
            }
            DayStatus.Today -> Box(
                circulo.background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.workout_day_today),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}

@Composable
private fun ProgressCard(onViewProgress: () -> Unit) {
    VoraCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.workout_progress),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.workout_view_progress),
                style = VoraTheme.text.fieldLabel,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(role = Role.Button, onClick = onViewProgress),
            )
        }
        Spacer(Modifier.height(VoraTheme.spacing.s12))
        // En el Figma el borde y el padding de 12 van por fuera del reparto: las tres columnas
        // tienen el mismo ancho de contenido, por eso el weight va solo en ProgressStat.
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            ProgressStat(stringResource(R.string.workout_streak), stringResource(R.string.workout_streak_demo))
            ProgressDivider()
            ProgressStat(stringResource(R.string.workout_completed), stringResource(R.string.workout_completed_demo))
            ProgressDivider()
            ProgressStat(stringResource(R.string.workout_volume), stringResource(R.string.workout_volume_demo))
        }
    }
}

@Composable
private fun ProgressDivider() {
    Spacer(Modifier.width(VoraTheme.spacing.s12))
    VerticalDivider(thickness = VoraTheme.sizes.border, color = MaterialTheme.colorScheme.outline)
    Spacer(Modifier.width(VoraTheme.spacing.s12))
}

@Composable
private fun RowScope.ProgressStat(label: String, value: String) {
    Column(
        modifier = Modifier
            .weight(1f)
            .semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4),
    ) {
        Text(text = label, style = VoraTheme.text.labelPlain, color = VoraTheme.colors.textMuted)
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun WorkoutDashboardScreenPreview() {
    Parcialtp3Theme {
        WorkoutDashboardScreen(
            onRetry = {},
            state = WorkoutsUiState.Content(
                listOf(Workout("w01", "Upper Body Strength", WorkoutLevel.INTERMEDIATE, 42, 5)),
            ),
            onWeeklyPlan = {},
            onStartWorkout = {},
            onWorkoutDetails = {},
            onViewProgress = {},
        )
    }
}
