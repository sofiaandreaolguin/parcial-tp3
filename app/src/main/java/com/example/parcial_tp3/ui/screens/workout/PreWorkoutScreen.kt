package com.example.parcial_tp3.ui.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.Exercise
import com.example.parcial_tp3.domain.model.WorkoutDetail
import com.example.parcial_tp3.domain.model.WorkoutLevel
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCardOnDark
import com.example.parcial_tp3.ui.components.VoraCircleButton
import com.example.parcial_tp3.ui.components.VoraCircleButtonTone
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraTopBar
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Workout / 04 (nodo 1:510). Nombre, duracion y cantidad de ejercicios salen del detalle (RF01),
 * y "YOUR LAST SESSION" toma el nombre del primer ejercicio; las series, el foco y los consejos
 * son de demostracion. Va hasta el borde (RUTAS_A_SANGRE): el verde pasa por detras de la barra
 * de estado, como en el frame.
 *
 * Diferencias aceptadas con el Figma, por usar lo que ya tiene el tema: el titulo va en 26 Bold
 * (el nodo es SemiBold), "Controlled Repetitions" en 16 (el nodo es 15) y "Start Workout" es el
 * Large de VoraButton, 48 de alto y texto de 15 (el nodo 1:552 es 50 y 16). La X es la de
 * VoraTopBar, unos 8 mas cerca del borde. La fila "Need anything?" es VoraCardOnDark, con borde
 * y fondo al 5 % (el nodo 1:540 no tiene borde y es al 8 %), el orbe es el chico de 28 (el nodo
 * es 32) y el area tactil de 48 del microfono la hace 8 mas alta.
 */
@Composable
fun PreWorkoutScreen(
    state: WorkoutDetailUiState,
    onClose: () -> Unit,
    onAskVora: () -> Unit,
    onStartWorkout: (workoutId: String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding(),
    ) {
        Box(contentAlignment = Alignment.CenterStart) {
            Text(
                text = stringResource(R.string.pre_workout_get_ready),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(start = VoraTheme.spacing.screen),
            )
            VoraTopBar(title = null, onBack = null, onClose = onClose, contentColor = MaterialTheme.colorScheme.onPrimary)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(
                    start = VoraTheme.spacing.screen,
                    end = VoraTheme.spacing.screen,
                    bottom = VoraTheme.spacing.s24,
                ),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s20),
        ) {
            when (state) {
                WorkoutDetailUiState.Loading -> VoraCardOnDark(Modifier.fillMaxWidth()) {
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
                WorkoutDetailUiState.Error -> VoraCardOnDark(Modifier.fillMaxWidth()) {
                    Text(text = stringResource(R.string.details_error), style = MaterialTheme.typography.bodyMedium)
                }
                is WorkoutDetailUiState.Content -> PreWorkoutContent(
                    detail = state.detail,
                    onAskVora = onAskVora,
                    onStartWorkout = { onStartWorkout(state.detail.id) },
                )
            }
        }
    }
}

@Composable
private fun PreWorkoutContent(
    detail: WorkoutDetail,
    onAskVora: () -> Unit,
    onStartWorkout: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4)) {
        Text(
            text = detail.name,
            style = VoraTheme.text.heroTitle,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = pluralStringResource(
                R.plurals.workout_summary,
                detail.exerciseCount,
                detail.durationMinutes,
                detail.exerciseCount,
            ),
            style = VoraTheme.text.heroSubtitle,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
        InfoCard(label = stringResource(R.string.pre_workout_focus_label)) {
            Text(
                text = stringResource(R.string.pre_workout_focus_title_demo),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = stringResource(R.string.pre_workout_focus_body_demo),
                style = MaterialTheme.typography.bodyMedium,
                color = VoraTheme.colors.onDarkMuted,
            )
        }
        detail.exercises.firstOrNull()?.let { primero ->
            InfoCard(label = stringResource(R.string.pre_workout_last_session_label)) {
                Text(
                    text = stringResource(R.string.pre_workout_last_session_title, primero.name),
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = stringResource(R.string.pre_workout_last_session_demo),
                    style = MaterialTheme.typography.bodyMedium,
                    color = VoraTheme.colors.onDarkMuted,
                )
            }
        }
        InfoCard(label = stringResource(R.string.pre_workout_coach_tip_label)) {
            Text(
                text = stringResource(R.string.pre_workout_coach_tip_demo),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
    AskVoraRow(onAskVora)
    VoraButton(
        text = stringResource(R.string.workout_start),
        onClick = onStartWorkout,
        modifier = Modifier.fillMaxWidth(),
        style = VoraButtonStyle.Mint,
    )
}

/** Las tres tarjetas del medio: etiqueta lima de 12 Bold y su contenido debajo. */
@Composable
private fun InfoCard(label: String, content: @Composable () -> Unit) {
    VoraCardOnDark(Modifier.fillMaxWidth()) {
        Text(text = label, style = VoraTheme.text.panelLabel, color = MaterialTheme.colorScheme.tertiary)
        content()
    }
}

@Composable
private fun AskVoraRow(onAskVora: () -> Unit) {
    VoraCardOnDark(Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        ) {
            VoraOrb(size = VoraTheme.sizes.orbSmall, onDark = true)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2),
            ) {
                Text(
                    text = stringResource(R.string.pre_workout_ask_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text = stringResource(R.string.pre_workout_ask_example_demo),
                    style = VoraTheme.text.footnote,
                    color = VoraTheme.colors.onDarkMuted,
                )
            }
            VoraCircleButton(
                icon = painterResource(R.drawable.ic_mic),
                contentDescription = stringResource(R.string.pre_workout_ask_mic),
                onClick = onAskVora,
                tone = VoraCircleButtonTone.Cta,
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 844)
@Composable
private fun PreWorkoutScreenPreview() {
    fun ejercicio(id: String, name: String, muscleGroup: String) =
        Exercise(id, name, muscleGroup, sets = 3, reps = 10, restSeconds = 90, instructions = "", tip = "")
    Parcialtp3Theme {
        PreWorkoutScreen(
            state = WorkoutDetailUiState.Content(
                WorkoutDetail(
                    id = "w01",
                    name = "Upper Body Strength",
                    level = WorkoutLevel.INTERMEDIATE,
                    durationMinutes = 42,
                    exerciseCount = 5,
                    description = "",
                    exercises = listOf(
                        ejercicio("e01", "Bench Press", "Chest"),
                        ejercicio("e02", "Seated Cable Row", "Back"),
                    ),
                ),
            ),
            onClose = {},
            onAskVora = {},
            onStartWorkout = {},
        )
    }
}
