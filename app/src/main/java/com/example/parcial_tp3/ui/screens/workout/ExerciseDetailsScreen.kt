package com.example.parcial_tp3.ui.screens.workout

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.Exercise
import com.example.parcial_tp3.domain.model.WorkoutDetail
import com.example.parcial_tp3.domain.model.WorkoutLevel
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraInfoMessage
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraTopBar
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/** La foto del nodo 1:649 mide 350 x 160 en el frame de 390. */
private const val PROPORCION_FOTO = 350f / 160f

/**
 * Workout / 06 (nodo 1:628). Nombre, grupo muscular, instruccion y tip salen del ejercicio
 * elegido (RF01, NAV04). Son de demostracion la foto, que es la del Figma para todos los
 * ejercicios, y el ejemplo de "Ask Vora".
 *
 * El frame tiene 5 pasos y 2 cues del remo; el modelo trae una instruccion y un tip, y se
 * muestran esos (decidido con Hector) para que el texto sea siempre del ejercicio elegido.
 *
 * Diferencias aceptadas con el Figma, por usar lo que ya tiene el tema: el titulo de
 * VoraTopBar va en 14 (el nodo 1:642 es 16) y la ayuda es ic_help de 18 (el nodo 1:643 es 24).
 * El nombre va en 22 Bold (el nodo 1:647 es SemiBold), "Performance Steps" en 14 SemiBold (el
 * nodo 1:651 es Bold) y "PRO FORM CUES" en 12 SemiBold (el nodo 1:659 es Bold). "Back to
 * Workout" es el Medium de VoraButton, con texto de 14 (el nodo 1:672 es 15). El chevron de
 * "Ask Vora" no se ve en el frame porque el nodo 1:669 mide 100 dentro de un marco de 16 que lo
 * recorta; se dibuja a 16, que es lo que el marco quiso mostrar.
 */
@Composable
fun ExerciseDetailsScreen(
    state: WorkoutDetailUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onAskVora: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        VoraTopBar(
            title = stringResource(R.string.exercise_title),
            onBack = onBack,
            actions = {
                IconButton(onClick = onHelp) {
                    Icon(
                        painter = painterResource(R.drawable.ic_help),
                        contentDescription = stringResource(R.string.exercise_help),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = VoraTheme.spacing.screen,
                    end = VoraTheme.spacing.screen,
                    bottom = VoraTheme.spacing.s24,
                ),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
        ) {
            when (state) {
                WorkoutDetailUiState.Loading -> LoadingCard(stringResource(R.string.details_loading))
                is WorkoutDetailUiState.Error -> ErrorMessage()
                WorkoutDetailUiState.NotAvailableOffline -> ErrorMessage(R.string.workout_not_available_offline)
                is WorkoutDetailUiState.Content -> state.selectedExercise
                    ?.let { ExerciseContent(exercise = it, onAskVora = onAskVora, onBack = onBack) }
                    ?: ErrorMessage()
            }
        }
    }
}

@Composable
private fun ErrorMessage(@StringRes text: Int = R.string.details_error) {
    VoraInfoMessage(text = stringResource(text), icon = painterResource(R.drawable.ic_info))
}

@Composable
private fun ExerciseContent(exercise: Exercise, onAskVora: () -> Unit, onBack: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4)) {
        Text(
            text = exercise.name,
            style = VoraTheme.text.alertTitle,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.exercise_target, exercise.muscleGroup),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    // Decorativa: es la misma foto para todos los ejercicios.
    Image(
        painter = painterResource(R.drawable.exercise_demo),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(PROPORCION_FOTO)
            .clip(RoundedCornerShape(VoraTheme.radius.r16)),
    )
    if (exercise.instructions.isNotBlank()) {
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
            Text(
                text = stringResource(R.string.exercise_steps),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = exercise.instructions,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    if (exercise.tip.isNotBlank()) {
        VoraCard(
            modifier = Modifier.fillMaxWidth(),
            padding = VoraTheme.spacing.s12,
            radius = VoraTheme.radius.r12,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                Text(
                    text = stringResource(R.string.exercise_cues),
                    style = VoraTheme.text.fieldLabel,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.exercise_cue_bullet, exercise.tip),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
    AskVoraRow(onAskVora)
    VoraButton(
        text = stringResource(R.string.exercise_back),
        onClick = onBack,
        modifier = Modifier.fillMaxWidth(),
        size = VoraButtonSize.Medium,
    )
}

/** "vora-ask-box" (nodo 1:662): menta al 12 % sin borde, radio 12, padding 12 y separacion 10. */
@Composable
private fun AskVoraRow(onAskVora: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(VoraTheme.radius.r12))
            .background(VoraTheme.colors.mintTranslucent)
            .clickable(role = Role.Button, onClick = onAskVora)
            .padding(VoraTheme.spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10),
    ) {
        VoraOrb(size = VoraTheme.sizes.orbCard)
        Text(
            text = stringResource(R.string.exercise_ask_demo),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(VoraTheme.sizes.iconXSmall),
        )
    }
}

@Preview(showBackground = true, heightDp = 844)
@Composable
private fun ExerciseDetailsScreenPreview() {
    val ejercicio = Exercise(
        id = "e02",
        name = "Seated Cable Row",
        muscleGroup = "Lats, Mid-Back, Rhomboids",
        sets = 3,
        reps = 12,
        restSeconds = 75,
        instructions = "Sit upright with your chest lifted and pull the handle toward your lower torso.",
        tip = "Keep your shoulders rolled down and back.",
    )
    Parcialtp3Theme {
        ExerciseDetailsScreen(
            state = WorkoutDetailUiState.Content(
                detail = WorkoutDetail(
                    id = "w01",
                    name = "Upper Body Strength",
                    level = WorkoutLevel.INTERMEDIATE,
                    durationMinutes = 42,
                    exerciseCount = 1,
                    description = "",
                    exercises = listOf(ejercicio),
                ),
                selectedExercise = ejercicio,
            ),
            onBack = {},
            onHelp = {},
            onAskVora = {},
        )
    }
}
