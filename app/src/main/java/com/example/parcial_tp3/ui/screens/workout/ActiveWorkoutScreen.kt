package com.example.parcial_tp3.ui.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.Exercise
import com.example.parcial_tp3.domain.model.WorkoutDetail
import com.example.parcial_tp3.domain.model.WorkoutLevel
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCardOnDark
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraStepper
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme
import java.text.NumberFormat

/**
 * Workout / 05 (nodo 1:554). Ejercicio, series, reps, peso y descanso salen de la sesion
 * (ActiveWorkoutViewModel, tabla "Lo que muestra cada pantalla" de la spec
 * sesion-de-entrenamiento-demo); "LAST SESSION", la frase de VORA COACH y "Voice On" son de
 * demostracion. Va hasta el borde (RUTAS_A_SANGRE), como 04.
 *
 * Diferencias aceptadas con el Figma, por usar lo que ya tiene el tema: "Finish Workout", "+30s" y
 * "Skip" son el Compact de VoraButton (13 Medium, 32 de alto; los nodos son 12 y 11, unos 24) y
 * "+30s" es OutlinedOnDark, blanco (el nodo 1:611 es menta). Los − y + son VoraStepper, al 12 %
 * (el nodo es 8 %), y su area tactil de 48 hace "LAST SESSION" unos 11 mas alta. Las tarjetas son VoraCardOnDark, al 5 % con borde al 10 % (los nodos dicen
 * 12 %, y "LAST SESSION" no tiene borde). El orbe es el de 16 (el nodo es 18), el divisor del
 * volumen es el de 38 (el nodo es 32) y "Complete Set" es el Large, texto de 15 (el nodo es 16).
 * Los chips de las series se reparten el ancho en vez de medir 110, asi entran en 360 dp; la
 * actual va al 12 % y las otras sin fondo (los nodos dicen 10 % y 2 %).
 * En el nodo 1:607 el reloj de "Resting" queda recortado por su marco y no se ve; se dibuja igual.
 */
@Composable
fun ActiveWorkoutScreen(
    state: ActiveWorkoutUiState,
    onCompleteSet: () -> Unit,
    onIncreaseWeight: () -> Unit,
    onDecreaseWeight: () -> Unit,
    onAddRest: () -> Unit,
    onSkipRest: () -> Unit,
    onFinish: (workoutId: String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(
                start = VoraTheme.spacing.screen,
                end = VoraTheme.spacing.screen,
                top = VoraTheme.spacing.s12,
                bottom = VoraTheme.spacing.s24,
            ),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
    ) {
        when (state) {
            ActiveWorkoutUiState.Loading -> VoraCardOnDark(Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(VoraTheme.sizes.icon),
                        color = MaterialTheme.colorScheme.secondary,
                        strokeWidth = VoraTheme.sizes.borderSelected,
                    )
                    Text(text = stringResource(R.string.details_loading), style = MaterialTheme.typography.bodyMedium)
                }
            }
            ActiveWorkoutUiState.Error -> VoraCardOnDark(Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.details_error), style = MaterialTheme.typography.bodyMedium)
            }
            is ActiveWorkoutUiState.Content -> ActiveWorkoutContent(
                state = state,
                onCompleteSet = onCompleteSet,
                onIncreaseWeight = onIncreaseWeight,
                onDecreaseWeight = onDecreaseWeight,
                onAddRest = onAddRest,
                onSkipRest = onSkipRest,
                onFinish = { onFinish(state.detail.id) },
            )
        }
    }
}

@Composable
private fun ActiveWorkoutContent(
    state: ActiveWorkoutUiState.Content,
    onCompleteSet: () -> Unit,
    onIncreaseWeight: () -> Unit,
    onDecreaseWeight: () -> Unit,
    onAddRest: () -> Unit,
    onSkipRest: () -> Unit,
    onFinish: () -> Unit,
) {
    val ejercicio = state.currentExercise
    val locale = LocalConfiguration.current.locales[0]
    val peso = remember(locale) { NumberFormat.getNumberInstance(locale).apply { maximumFractionDigits = 1 } }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2),
        ) {
            Text(
                text = stringResource(R.string.active_exercise_of, state.exerciseIndex + 1, state.detail.exercises.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = ejercicio.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.semantics { heading() },
            )
        }
        VoraButton(
            text = stringResource(R.string.active_finish),
            onClick = onFinish,
            style = VoraButtonStyle.SubtleOnDark,
            size = VoraButtonSize.Compact,
        )
    }
    VolumeCard(reps = ejercicio.reps, sets = ejercicio.sets)
    VoraCardOnDark(Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s12) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2),
            ) {
                Text(
                    text = stringResource(R.string.active_last_session_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = VoraTheme.colors.onDarkMuted,
                )
                Text(text = stringResource(R.string.active_last_session_demo), style = MaterialTheme.typography.labelLarge)
            }
            VoraStepper(
                value = stringResource(R.string.active_weight, peso.format(state.weightKg)),
                onDecrease = onDecreaseWeight,
                onIncrease = onIncreaseWeight,
            )
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
        repeat(ejercicio.sets) { n ->
            val estado = when {
                n < state.setIndex -> EstadoSerie.Hecha
                n == state.setIndex && !state.allSetsDone -> EstadoSerie.Actual
                else -> EstadoSerie.Pendiente
            }
            SetChip(numero = n + 1, estado = estado, modifier = Modifier.weight(1f))
        }
    }
    if (state.isResting) {
        RestRow(segundos = state.restRemainingSeconds, onAddRest = onAddRest, onSkipRest = onSkipRest)
    }
    CoachCard()
    VoraButton(
        text = stringResource(R.string.active_complete_set),
        onClick = onCompleteSet,
        modifier = Modifier.fillMaxWidth(),
        style = VoraButtonStyle.Cta,
    )
}

/** "CURRENT VOLUME WORK": reps arriba de "REPS TARGET" y series × reps arriba de "SET MATRIX". */
@Composable
private fun VolumeCard(reps: Int, sets: Int) {
    VoraCardOnDark(Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        ) {
            Text(
                text = stringResource(R.string.active_volume_label),
                style = VoraTheme.text.footnote,
                color = MaterialTheme.colorScheme.secondary,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s24),
            ) {
                VolumeStat(value = reps.toString(), label = stringResource(R.string.active_reps_target))
                VerticalDivider(
                    modifier = Modifier.height(VoraTheme.sizes.statDivider),
                    thickness = VoraTheme.sizes.border,
                    color = VoraTheme.colors.onDarkPanelBorder,
                )
                VolumeStat(
                    value = stringResource(R.string.active_set_matrix_value, sets, reps),
                    label = stringResource(R.string.active_set_matrix),
                )
            }
        }
    }
}

@Composable
private fun VolumeStat(value: String, label: String) {
    Column(
        modifier = Modifier.semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4),
    ) {
        Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = VoraTheme.colors.onDarkMuted)
    }
}

private enum class EstadoSerie { Hecha, Actual, Pendiente }

/**
 * Chip de una serie (nodos 1:592, 1:596 y 1:600): circulo de 20 con el numero y "Set n". Hecha,
 * circulo menta; actual, borde y circulo lima sobre blanco al 12 %; pendiente, circulo apagado.
 */
@Composable
private fun SetChip(numero: Int, estado: EstadoSerie, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(VoraTheme.radius.r12)
    val colors = MaterialTheme.colorScheme
    val extra = VoraTheme.colors
    val (fondo, borde, circulo) = when (estado) {
        EstadoSerie.Hecha -> Triple(Color.Transparent, extra.onDarkPanelBorder, colors.secondary)
        EstadoSerie.Actual -> Triple(extra.onDarkSubtle, extra.cta, extra.cta)
        EstadoSerie.Pendiente -> Triple(Color.Transparent, extra.onDarkPanelBorder, extra.onDarkChip)
    }
    val descripcion = stringResource(
        when (estado) {
            EstadoSerie.Hecha -> R.string.active_set_done
            EstadoSerie.Actual -> R.string.active_set_current
            EstadoSerie.Pendiente -> R.string.active_set_pending
        },
    )
    Row(
        modifier = modifier
            .background(fondo, shape)
            .border(VoraTheme.sizes.border, borde, shape)
            .padding(VoraTheme.spacing.s12)
            .semantics(mergeDescendants = true) { stateDescription = descripcion },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
    ) {
        Box(
            modifier = Modifier
                .size(VoraTheme.sizes.checkCircle)
                .background(circulo, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = numero.toString(),
                style = VoraTheme.text.label,
                fontWeight = FontWeight.Bold,
                color = extra.onAccent,
            )
        }
        Text(
            text = stringResource(R.string.active_set, numero),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** "Resting • 01:18" con "+30s" y "Skip" (nodo 1:604): menta al 12 % con borde menta al 25 %. */
@Composable
private fun RestRow(segundos: Int, onAddRest: () -> Unit, onSkipRest: () -> Unit) {
    val shape = RoundedCornerShape(VoraTheme.radius.r16)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VoraTheme.colors.mintTranslucent, shape)
            .border(VoraTheme.sizes.border, VoraTheme.colors.mintBorderSoft, shape)
            .padding(VoraTheme.spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_clock),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(VoraTheme.sizes.iconXSmall),
        )
        Text(
            text = stringResource(
                R.string.active_resting,
                stringResource(R.string.active_rest_time, segundos / 60, segundos % 60),
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f),
        )
        VoraButton(
            text = stringResource(R.string.active_add_rest),
            onClick = onAddRest,
            style = VoraButtonStyle.OutlinedOnDark,
            size = VoraButtonSize.Compact,
        )
        VoraButton(
            text = stringResource(R.string.active_skip_rest),
            onClick = onSkipRest,
            style = VoraButtonStyle.Mint,
            size = VoraButtonSize.Compact,
        )
    }
}

@Composable
private fun CoachCard() {
    VoraCardOnDark(Modifier.fillMaxWidth(), spacing = VoraTheme.spacing.s12) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6),
            ) {
                VoraOrb(size = VoraTheme.sizes.orbNote, withRing = false, onDark = true)
                Text(
                    text = stringResource(R.string.active_coach_label),
                    style = VoraTheme.text.label,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            Text(
                text = stringResource(R.string.active_voice_on_demo),
                style = VoraTheme.text.labelPlain,
                color = VoraTheme.colors.onDarkMuted,
            )
        }
        Text(text = stringResource(R.string.active_coach_quote_demo), style = VoraTheme.text.heroSubtitle)
    }
}

@Preview(showBackground = true, heightDp = 844)
@Composable
private fun ActiveWorkoutScreenPreview() {
    fun ejercicio(id: String, name: String) =
        Exercise(id, name, "Back", sets = 3, reps = 10, restSeconds = 90, instructions = "", tip = "")
    Parcialtp3Theme {
        ActiveWorkoutScreen(
            state = ActiveWorkoutUiState.Content(
                detail = WorkoutDetail(
                    id = "w01",
                    name = "Upper Body Strength",
                    level = WorkoutLevel.INTERMEDIATE,
                    durationMinutes = 42,
                    exerciseCount = 5,
                    description = "",
                    exercises = listOf(
                        ejercicio("e01", "Bench Press"),
                        ejercicio("e02", "Seated Cable Row"),
                        ejercicio("e03", "Shoulder Press"),
                        ejercicio("e04", "Lat Pulldown"),
                        ejercicio("e05", "Bicep Curl"),
                    ),
                ),
                exerciseIndex = 1,
                setIndex = 1,
                restRemainingSeconds = 78,
            ),
            onCompleteSet = {},
            onIncreaseWeight = {},
            onDecreaseWeight = {},
            onAddRest = {},
            onSkipRest = {},
            onFinish = {},
        )
    }
}
