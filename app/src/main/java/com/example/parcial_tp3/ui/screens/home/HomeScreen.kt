package com.example.parcial_tp3.ui.screens.home

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.WorkoutLevel
import com.example.parcial_tp3.ui.components.VoraAvatar
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraInfoMessage
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraPanel
import com.example.parcial_tp3.ui.components.VoraProgressBar
import com.example.parcial_tp3.ui.components.VoraProgressRing
import com.example.parcial_tp3.ui.components.VoraStat
import com.example.parcial_tp3.ui.components.VoraStatRow
import com.example.parcial_tp3.ui.components.VoraTag
import com.example.parcial_tp3.ui.components.VoraTagTone
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme
import java.text.NumberFormat

/**
 * Home / 01 — Dashboard (nodo 2007:3). Con barra inferior: la pone el Scaffold de MainActivity.
 * Mas alta que 850 dp, asi que va con scroll (UI03). Es la pantalla que mas cruza de flujo:
 * cada control va a donde dice specs/PANTALLAS.md (NAV03).
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    onNotifications: () -> Unit,
    onVoice: () -> Unit,
    onStartMyWorkout: () -> Unit,
    onLogBreakfast: () -> Unit,
    onOverview: () -> Unit,
    onStartWorkout: (workoutId: String) -> Unit,
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
        Header(stringResource(R.string.home_full_name, state.firstName, state.lastName), state.greeting, onNotifications)
        CoachPanel(onVoice, onStartMyWorkout, onLogBreakfast)
        VoraInfoMessage(text = stringResource(R.string.home_on_track))
        Overview(state, onOverview, onStartWorkout)
    }
}

/** Foto, saludo y nombre a la izquierda; la campana con el punto menta a la derecha. */
@Composable
private fun Header(fullName: String, greeting: Greeting, onNotifications: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        VoraAvatar(size = VoraTheme.sizes.avatarSmall)
        Spacer(Modifier.width(VoraTheme.spacing.s12))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2)) {
            Text(text = stringResource(greeting.label()), style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
            Text(text = fullName, style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
        }
        Box(
            modifier = Modifier
                .size(VoraTheme.sizes.avatarSmall)
                .background(colors.surface, CircleShape)
                .border(VoraTheme.sizes.border, colors.outline, CircleShape)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onNotifications),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bell),
                contentDescription = stringResource(R.string.home_notifications),
                tint = colors.onSurface,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(VoraTheme.sizes.icon),
            )
            // el punto del nodo arranca en (20, 12) del circulo de 40
            Box(
                Modifier
                    .offset(VoraTheme.spacing.s20, VoraTheme.spacing.s12)
                    .size(VoraTheme.sizes.statusDot)
                    .background(colors.secondary, CircleShape),
            )
        }
    }
}

/** Panel verde del coach: orbe, "AI ONLINE", la pregunta, "Start voice session" y dos atajos. */
@Composable
private fun CoachPanel(onVoice: () -> Unit, onStartMyWorkout: () -> Unit, onLogBreakfast: () -> Unit) {
    VoraPanel(Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s16) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            VoraOrb(size = VoraTheme.sizes.orbHome, onDark = true)
            Spacer(Modifier.width(VoraTheme.spacing.s10))
            Text(
                text = stringResource(R.string.access_brand),
                style = VoraTheme.text.homeCoachName,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f),
            )
            VoraTag(text = stringResource(R.string.home_ai_online), tone = VoraTagTone.OnDark)
        }
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6)) {
            Text(text = stringResource(R.string.home_quote), style = VoraTheme.text.homeQuote)
            Text(
                text = stringResource(R.string.home_quote_sub),
                style = MaterialTheme.typography.bodyMedium,
                color = VoraTheme.colors.onDarkMuted,
            )
        }
        VoraButton(
            text = stringResource(R.string.home_voice_cta),
            onClick = onVoice,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = VoraTheme.sizes.homeVoiceCta),
            style = VoraButtonStyle.Cta,
            size = VoraButtonSize.Medium,
            icon = painterResource(R.drawable.ic_mic),
            alignStart = true,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
            VoraButton(
                text = stringResource(R.string.home_start_my_workout),
                onClick = onStartMyWorkout,
                style = VoraButtonStyle.SubtleOnDark,
                size = VoraButtonSize.Compact,
            )
            VoraButton(
                text = stringResource(R.string.home_log_breakfast),
                onClick = onLogBreakfast,
                style = VoraButtonStyle.SubtleOnDark,
                size = VoraButtonSize.Compact,
            )
        }
    }
}

/** "Today's Overview": anillo y nutricion, la fila de datos y el entrenamiento recomendado. */
@Composable
private fun Overview(state: HomeUiState, onOverview: () -> Unit, onStartWorkout: (String) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val locale = LocalConfiguration.current.locales[0]
    val numero = remember(locale) { NumberFormat.getIntegerInstance(locale) }
    val decimal = remember(locale) { NumberFormat.getNumberInstance(locale).apply { maximumFractionDigits = 1 } }
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
        Text(text = stringResource(R.string.home_overview), style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
        // el anillo y la nutricion miden 110 y 228 de los 350 del frame
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        ) {
            VoraCard(
                modifier = Modifier
                    .weight(110f)
                    .fillMaxHeight(),
                padding = VoraTheme.spacing.s12,
                onClick = onOverview,
            ) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    VoraProgressRing(
                        progress = state.dailyProgress,
                        size = VoraTheme.sizes.ringSmall,
                        strokeWidth = VoraTheme.sizes.ringStrokeSmall,
                    ) {
                        Text(
                            text = stringResource(R.string.home_percent, state.dailyPercent),
                            style = VoraTheme.text.homeValue,
                            color = colors.onSurface,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                    Spacer(Modifier.height(VoraTheme.spacing.s12))
                    Text(text = stringResource(R.string.home_daily_goal), style = VoraTheme.text.footnote, color = colors.onSurfaceVariant)
                }
            }
            VoraCard(
                modifier = Modifier
                    .weight(228f)
                    .fillMaxHeight(),
                padding = VoraTheme.spacing.s12,
                onClick = onOverview,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.home_nutrition),
                        style = VoraTheme.text.footnote,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    ValueOfGoal(numero.format(state.kcal), stringResource(R.string.home_kcal_goal, numero.format(state.kcalGoal)), large = true)
                }
                Spacer(Modifier.height(VoraTheme.spacing.s10))
                Macro(R.string.home_protein, state.proteinG, state.proteinGoal, state.proteinProgress, colors.primary)
                Spacer(Modifier.height(VoraTheme.spacing.s6))
                Macro(R.string.home_carbs, state.carbsG, state.carbsGoal, state.carbsProgress, colors.secondary)
            }
        }
        VoraStatRow(
            stats = listOf(
                VoraStat(stringResource(R.string.home_steps), numero.format(state.steps)),
                VoraStat(stringResource(R.string.home_distance), stringResource(R.string.home_distance_value, decimal.format(state.distanceKm))),
                VoraStat(stringResource(R.string.home_active_kcal), stringResource(R.string.home_kcal_value, numero.format(state.activeKcal))),
            ),
            alignStart = true,
        )
        RecommendedWorkout(state, numero, onStartWorkout)
    }
}

/** "Protein 104g / 140g" con su barra de 6. */
@Composable
private fun Macro(@StringRes label: Int, grams: Int, goal: Int, progress: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(label),
                style = VoraTheme.text.footnote,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            ValueOfGoal(stringResource(R.string.home_grams, grams), stringResource(R.string.home_grams_goal, goal), large = false)
        }
        VoraProgressBar(progress = progress, color = color, height = VoraTheme.sizes.progressBarThick)
    }
}

/** "1,420 / 2,100 kcal" y "104g / 140g": el valor fuerte y el objetivo tenue, en una linea. */
@Composable
private fun ValueOfGoal(value: String, goal: String, large: Boolean) {
    val colors = MaterialTheme.colorScheme
    // el parrafo toma el estilo del valor (el mas alto) y el objetivo achica solo su tramo:
    // asi el alto de linea alcanza aunque se agrande la letra del sistema
    val estiloValor = if (large) VoraTheme.text.homeValue else VoraTheme.text.footnote
    val estiloObjetivo = if (large) MaterialTheme.typography.bodySmall else VoraTheme.text.footnote
    Text(
        text = buildAnnotatedString {
            // en las macros el valor va en negrita sobre el 12 de la fila
            withStyle(SpanStyle(fontWeight = if (large) null else FontWeight.Bold, color = colors.onSurface)) { append(value) }
            withStyle(estiloObjetivo.toSpanStyle().copy(color = VoraTheme.colors.textMuted)) { append(goal) }
        },
        style = estiloValor,
        maxLines = 1,
    )
}

/** La tarjeta del entrenamiento recomendado, con "Start workout" al detalle (NAV04). */
@Composable
private fun RecommendedWorkout(state: HomeUiState, numero: NumberFormat, onStartWorkout: (String) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val workout = state.recommended
    VoraCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_calendar),
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(VoraTheme.sizes.iconXSmall),
            )
            Spacer(Modifier.width(VoraTheme.spacing.s6))
            Text(
                text = stringResource(R.string.home_recommended),
                style = VoraTheme.text.fieldLabel,
                color = colors.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            VoraTag(text = stringResource(R.string.home_today), tone = VoraTagTone.Neutral)
        }
        Spacer(Modifier.height(VoraTheme.spacing.s12))
        Text(text = workout.name, style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
        Spacer(Modifier.height(VoraTheme.spacing.s4))
        Text(
            text = stringResource(
                R.string.home_workout_meta,
                pluralStringResource(R.plurals.workout_summary, workout.exerciseCount, workout.durationMinutes, workout.exerciseCount),
                stringResource(workout.level.label()),
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
        )
        Spacer(Modifier.height(VoraTheme.spacing.s12))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.home_estimated_volume, numero.format(state.estimatedVolumeKg)),
                style = VoraTheme.text.footnote,
                color = VoraTheme.colors.textMuted,
                modifier = Modifier.weight(1f),
            )
            VoraButton(
                text = stringResource(R.string.home_start_workout),
                onClick = { onStartWorkout(workout.id) },
                size = VoraButtonSize.Small,
            )
        }
    }
}

@StringRes
private fun Greeting.label(): Int = when (this) {
    Greeting.MORNING -> R.string.home_greeting_morning
    Greeting.AFTERNOON -> R.string.home_greeting_afternoon
    Greeting.EVENING -> R.string.home_greeting_evening
}

@StringRes
private fun WorkoutLevel.label(): Int = when (this) {
    WorkoutLevel.BEGINNER -> R.string.home_level_beginner
    WorkoutLevel.INTERMEDIATE -> R.string.home_level_intermediate
    WorkoutLevel.ADVANCED -> R.string.home_level_advanced
}

@Preview(showBackground = true, widthDp = 393, heightDp = 1009)
@Composable
private fun HomeScreenPreview() {
    Parcialtp3Theme {
        HomeScreen(
            state = HomeUiState(
                firstName = "Alex",
                lastName = "Morgan",
                greeting = Greeting.MORNING,
                kcal = 1_420,
                kcalGoal = HomeDemoData.KCAL_GOAL,
                proteinG = 104,
                proteinGoal = HomeDemoData.PROTEIN_GOAL_G,
                carbsG = 152,
                carbsGoal = HomeDemoData.CARBS_GOAL_G,
                steps = HomeDemoData.STEPS,
                distanceKm = HomeDemoData.DISTANCE_KM,
                activeKcal = HomeDemoData.ACTIVE_KCAL,
                recommended = HomeDemoData.RECOMMENDED,
                estimatedVolumeKg = HomeDemoData.ESTIMATED_VOLUME_KG,
            ),
            onNotifications = {},
            onVoice = {},
            onStartMyWorkout = {},
            onLogBreakfast = {},
            onOverview = {},
            onStartWorkout = {},
        )
    }
}
