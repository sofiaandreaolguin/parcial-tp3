package com.example.parcial_tp3.ui.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.Exercise
import com.example.parcial_tp3.domain.model.WorkoutDetail
import com.example.parcial_tp3.domain.model.WorkoutLevel
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraCoachNote
import com.example.parcial_tp3.ui.components.VoraInfoMessage
import com.example.parcial_tp3.ui.components.VoraTopBar
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Workout / 03 (nodo 1:424). Nombre, nivel, duracion y ejercicios salen del detalle (RF01);
 * la nota del coach es de demostracion. Cada ejercicio abre su Exercise Details (NAV04).
 *
 * Diferencias aceptadas con el Figma, por usar lo que ya tiene el tema: el titulo va en 26
 * Bold (el nodo es SemiBold), el de VoraTopBar en 14 SemiBold (el nodo es 16) y los botones
 * son los Large de VoraButton, 48 de alto y texto Bold (el nodo 1:506 es 44 y SemiBold). "Edit
 * workout" es el Outlined del tema: borde gris, texto oscuro y fondo blanco, donde el nodo
 * 1:508 tiene borde y texto verdes sin relleno. En las filas, el numero va en 14 SemiBold (el
 * nodo es Bold) y el nombre en 16 SemiBold (el nodo es 15). Esos estilos quedan pedidos a Tema
 * y componentes.
 */
@Composable
fun WorkoutDetailsScreen(
    state: WorkoutDetailUiState,
    onBack: () -> Unit,
    onExercise: (workoutId: String, exerciseId: String) -> Unit,
    onStartWorkout: (workoutId: String) -> Unit,
    onEdit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        VoraTopBar(title = stringResource(R.string.details_title), onBack = onBack)
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
                is WorkoutDetailUiState.Error -> VoraInfoMessage(
                    text = stringResource(R.string.details_error),
                    icon = painterResource(R.drawable.ic_info),
                )
                WorkoutDetailUiState.NotAvailableOffline -> VoraInfoMessage(
                    text = stringResource(R.string.workout_not_available_offline),
                    icon = painterResource(R.drawable.ic_info),
                )
                is WorkoutDetailUiState.Content -> DetailContent(
                    detail = state.detail,
                    onExercise = { exerciseId -> onExercise(state.detail.id, exerciseId) },
                    onStartWorkout = { onStartWorkout(state.detail.id) },
                    onEdit = onEdit,
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    detail: WorkoutDetail,
    onExercise: (exerciseId: String) -> Unit,
    onStartWorkout: () -> Unit,
    onEdit: () -> Unit,
) {
    Header(detail)
    VoraCoachNote(
        label = stringResource(R.string.details_coach_note),
        text = stringResource(R.string.details_coach_note_demo),
        padding = VoraTheme.spacing.s12,
        radius = VoraTheme.radius.r12,
    )
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
        detail.exercises.forEachIndexed { index, exercise ->
            ExerciseRow(number = index + 1, exercise = exercise, onClick = { onExercise(exercise.id) })
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
        VoraButton(
            text = stringResource(R.string.workout_start),
            onClick = onStartWorkout,
            modifier = Modifier.fillMaxWidth(),
        )
        VoraButton(
            text = stringResource(R.string.details_edit),
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth(),
            style = VoraButtonStyle.Outlined,
        )
    }
}

@Composable
private fun Header(detail: WorkoutDetail) {
    val focus = detail.exercises
        .map { it.muscleGroup }
        .distinct()
        .joinToString(stringResource(R.string.details_focus_separator))

    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
        Text(
            text = detail.name,
            style = VoraTheme.text.heroTitle,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() },
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
            MetaText(stringResource(R.string.details_minutes, detail.durationMinutes))
            Separator()
            MetaText(stringResource(detail.level.label()))
            Separator()
            MetaText(pluralStringResource(R.plurals.details_exercises, detail.exerciseCount, detail.exerciseCount))
        }
        if (focus.isNotEmpty()) MetaText(stringResource(R.string.details_focus, focus))
    }
}

@Composable
private fun MetaText(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun Separator() {
    Text(
        text = stringResource(R.string.details_separator),
        style = MaterialTheme.typography.bodyMedium,
        color = VoraTheme.colors.textMuted,
        modifier = Modifier.clearAndSetSemantics {},
    )
}

/** Fila de ejercicio (nodo 1:460): numero en un cuadrado de 32 con radio 8. */
@Composable
private fun ExerciseRow(number: Int, exercise: Exercise, onClick: () -> Unit) {
    VoraCard(
        modifier = Modifier.fillMaxWidth(),
        padding = VoraTheme.spacing.s12,
        radius = VoraTheme.radius.r12,
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        ) {
            Box(
                modifier = Modifier
                    .size(VoraTheme.spacing.s32)
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(VoraTheme.radius.r8)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.details_exercise_number, number),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2)) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(
                        R.string.details_exercise_summary,
                        exercise.sets,
                        exercise.reps,
                        exercise.restSeconds,
                    ),
                    style = VoraTheme.text.footnote,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun WorkoutDetailsScreenPreview() {
    fun ejercicio(id: String, name: String, muscleGroup: String, reps: Int, rest: Int) =
        Exercise(id, name, muscleGroup, sets = 3, reps = reps, restSeconds = rest, instructions = "", tip = "")
    Parcialtp3Theme {
        WorkoutDetailsScreen(
            state = WorkoutDetailUiState.Content(
                WorkoutDetail(
                    id = "w01",
                    name = "Upper Body Strength",
                    level = WorkoutLevel.INTERMEDIATE,
                    durationMinutes = 42,
                    exerciseCount = 5,
                    description = "",
                    exercises = listOf(
                        ejercicio("e01", "Bench Press", "Chest", 10, 90),
                        ejercicio("e02", "Seated Cable Row", "Back", 12, 75),
                        ejercicio("e03", "Shoulder Press", "Shoulders", 10, 75),
                        ejercicio("e04", "Dumbbell Lateral Raise", "Shoulders", 12, 60),
                        ejercicio("e05", "Tricep Pushdown", "Arms", 12, 60),
                    ),
                ),
            ),
            onBack = {},
            onExercise = { _, _ -> },
            onStartWorkout = {},
            onEdit = {},
        )
    }
}
