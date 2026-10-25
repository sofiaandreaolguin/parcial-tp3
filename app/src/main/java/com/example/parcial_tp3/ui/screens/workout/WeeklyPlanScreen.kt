package com.example.parcial_tp3.ui.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.Workout
import com.example.parcial_tp3.domain.model.WorkoutLevel
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraInfoMessage
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraPanel
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/*
 * Semana de demostración, la del frame: hoy es martes 8 y el miércoles es de recuperación.
 * Los entrenamientos del catálogo real se reparten en los días con entrenamiento, en orden.
 * Entran cuatro: el mock trae tres; si el catálogo crece, los que sobran no se muestran acá.
 */
private const val HOY = 1
private const val RECUPERACION = 2
private val DIAS_CON_ENTRENAMIENTO = listOf(HOY, 3, 5, 6)
private val FECHAS_DEMO = (7..13).toList()

/**
 * Workout / 02. Las tarjetas son el catálogo real (RF01) y cada una abre su propio
 * `workoutId` (NAV04); el reparto en la semana y la recuperación del miércoles son de
 * demostración, como dice `PANTALLAS.md`.
 */
@Composable
fun WeeklyPlanScreen(
    state: WorkoutsUiState,
    onWorkout: (workoutId: String) -> Unit,
    onAdjust: () -> Unit,
) {
    val plan: Map<Int, Workout> = (state as? WorkoutsUiState.Content)
        ?.workouts
        ?.zip(DIAS_CON_ENTRENAMIENTO) { workout, dia -> dia to workout }
        ?.toMap()
        .orEmpty()
    val diasLargos = stringArrayResource(R.array.weekly_days_long)

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
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4)) {
            Text(
                text = stringResource(R.string.weekly_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = stringResource(R.string.weekly_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Calendar(plan, diasLargos, onWorkout)
        when (state) {
            WorkoutsUiState.Loading -> LoadingCard()
            is WorkoutsUiState.Error -> VoraInfoMessage(
                text = stringResource(R.string.workout_error),
                icon = painterResource(R.drawable.ic_info),
            )
            WorkoutsUiState.Empty -> VoraInfoMessage(
                text = stringResource(R.string.workout_empty),
                icon = painterResource(R.drawable.ic_info),
            )
            is WorkoutsUiState.Content -> if (plan.isEmpty()) {
                VoraInfoMessage(
                    text = stringResource(R.string.workout_empty),
                    icon = painterResource(R.drawable.ic_info),
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
                    plan.forEach { (dia, workout) ->
                        if (dia == HOY) {
                            TodayCard(workout) { onWorkout(workout.id) }
                            DayCard(
                                day = diasLargos[RECUPERACION],
                                title = stringResource(R.string.weekly_recovery_title),
                                detail = stringResource(R.string.weekly_recovery_detail),
                                recovery = true,
                            )
                        } else {
                            DayCard(
                                day = diasLargos[dia],
                                title = workout.name,
                                detail = workoutSummary(workout),
                                onClick = { onWorkout(workout.id) },
                            )
                        }
                    }
                }
            }
        }
        AdjustPanel(onAdjust)
    }
}

@Composable
private fun workoutSummary(workout: Workout): String = pluralStringResource(
    R.plurals.workout_summary,
    workout.exerciseCount,
    workout.durationMinutes,
    workout.exerciseCount,
)

/** Los días con entrenamiento se pueden tocar y abren el suyo, igual que su tarjeta. */
@Composable
private fun Calendar(plan: Map<Int, Workout>, diasLargos: Array<String>, onWorkout: (String) -> Unit) {
    val diasCortos = stringArrayResource(R.array.weekly_days_short)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        diasCortos.forEachIndexed { dia, corto ->
            val hoy = dia == HOY
            val workout = plan[dia]
            val descripcion = stringResource(R.string.weekly_day_desc, diasLargos[dia], FECHAS_DEMO[dia])
            Column(
                modifier = Modifier
                    .width(VoraTheme.sizes.calendarDay)
                    .clip(RoundedCornerShape(VoraTheme.radius.pill))
                    .then(if (hoy) Modifier.background(MaterialTheme.colorScheme.primary) else Modifier)
                    .then(
                        if (workout != null) {
                            Modifier.clickable(role = Role.Button) { onWorkout(workout.id) }
                        } else {
                            Modifier
                        },
                    )
                    .semantics(mergeDescendants = true) {
                        contentDescription = descripcion
                        selected = hoy
                    }
                    .padding(horizontal = VoraTheme.spacing.s4, vertical = VoraTheme.spacing.s8),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6),
            ) {
                Text(
                    text = corto,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (hoy) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = FECHAS_DEMO[dia].toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (hoy) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}

/** La de hoy no es una [VoraCard]: lleva el borde verde y la etiqueta "READY" menta llena. */
@Composable
private fun TodayCard(workout: Workout, onClick: () -> Unit) {
    val shape = RoundedCornerShape(VoraTheme.radius.r16)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(VoraTheme.sizes.border, MaterialTheme.colorScheme.primary, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(VoraTheme.spacing.s16),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.weekly_today),
                style = VoraTheme.text.label,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.weekly_ready),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(VoraTheme.radius.r4))
                    .padding(horizontal = VoraTheme.spacing.s8, vertical = VoraTheme.spacing.s2),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2)) {
            Text(
                text = workout.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(
                    R.string.weekly_summary_level,
                    workoutSummary(workout),
                    stringResource(workout.level.label()),
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** El día de recuperación no navega: no es un entrenamiento del catálogo. */
@Composable
private fun DayCard(
    day: String,
    title: String,
    detail: String,
    onClick: (() -> Unit)? = null,
    recovery: Boolean = false,
) {
    VoraCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4),
            ) {
                Text(text = day, style = VoraTheme.text.label, color = VoraTheme.colors.textMuted)
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (recovery) VoraOrb(size = VoraTheme.sizes.orbCard)
        }
    }
}

/**
 * Usa [VoraPanel] y [VoraButton] aunque el nodo no coincide del todo: el panel del Figma es
 * de radio 16 con separacion 12 y las pildoras son de 12 Regular, padding 6/12 y blanco al
 * 8 %. A la vista es igual, y asi un cambio en los componentes llega tambien aca.
 */
@Composable
private fun AdjustPanel(onAdjust: () -> Unit) {
    VoraPanel(modifier = Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s16) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
        ) {
            VoraOrb(size = VoraTheme.sizes.orbNoteLarge, onDark = true)
            Text(
                text = stringResource(R.string.weekly_adjust_title),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        Text(
            text = stringResource(R.string.weekly_adjust_body),
            style = MaterialTheme.typography.bodyMedium,
            color = VoraTheme.colors.onDarkMuted,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
        ) {
            listOf(R.string.weekly_adjust_tired, R.string.weekly_adjust_move).forEach { texto ->
                VoraButton(
                    text = stringResource(texto),
                    onClick = onAdjust,
                    style = VoraButtonStyle.SubtleOnDark,
                    size = VoraButtonSize.Small,
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun WeeklyPlanScreenPreview() {
    Parcialtp3Theme {
        WeeklyPlanScreen(
            state = WorkoutsUiState.Content(
                listOf(
                    Workout("w01", "Upper Body Strength", WorkoutLevel.INTERMEDIATE, 42, 5),
                    Workout("w02", "Lower Body Strength", WorkoutLevel.INTERMEDIATE, 45, 6),
                    Workout("w03", "Full Body", WorkoutLevel.BEGINNER, 30, 3),
                ),
            ),
            onWorkout = {},
            onAdjust = {},
        )
    }
}
