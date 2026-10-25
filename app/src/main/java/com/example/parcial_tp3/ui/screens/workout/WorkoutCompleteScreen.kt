package com.example.parcial_tp3.ui.screens.workout

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.Exercise
import com.example.parcial_tp3.domain.model.WorkoutDetail
import com.example.parcial_tp3.domain.model.WorkoutLevel
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraChip
import com.example.parcial_tp3.ui.components.VoraChipSelectedStyle
import com.example.parcial_tp3.ui.components.VoraCoachNote
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme
import java.text.NumberFormat

/**
 * Workout / 07 (nodo 1:673). Duracion, ejercicios, volumen y dificultad salen de la sesion de 05
 * (tabla "Lo que muestra cada pantalla" de la spec sesion-de-entrenamiento-demo); la duracion es
 * la del plan, porque el cronometro es simulado. "You did well!", su frase y VORA INSIGHT son de
 * demostracion; el insight nombra el ultimo ejercicio en lugar de "Seated Cable Row".
 *
 * Diferencias aceptadas con el Figma, por usar lo que ya tiene el tema: los chips de dificultad
 * son VoraChip (14 Medium con padding 6/14; los nodos son 13 SemiBold con 8/14) y van en un
 * FlowRow, asi pasan abajo si no entran en 360 dp. "Done" es el Large de VoraButton, 15 Bold con
 * padding 14 (el nodo 1:727 es 15 SemiBold con 12). El resplandor del orbe es menta al 25 %
 * (mintBorderSoft; el nodo 1:686 es al 30 %). Los margenes laterales son 24, como en el nodo,
 * y no los 20 habituales.
 */
@Composable
fun WorkoutCompleteScreen(
    state: ActiveWorkoutUiState,
    onRate: (WorkoutRating) -> Unit,
    onDone: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(
                start = VoraTheme.spacing.s24,
                end = VoraTheme.spacing.s24,
                top = VoraTheme.spacing.s20,
                bottom = VoraTheme.spacing.s24,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s20),
    ) {
        when (state) {
            ActiveWorkoutUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.size(VoraTheme.sizes.icon),
                color = MaterialTheme.colorScheme.secondary,
                strokeWidth = VoraTheme.sizes.borderSelected,
            )
            is ActiveWorkoutUiState.Error -> Text(
                text = stringResource(R.string.details_error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            is ActiveWorkoutUiState.Content -> WorkoutCompleteContent(state, onRate, onDone)
        }
    }
}

@Composable
private fun WorkoutCompleteContent(
    state: ActiveWorkoutUiState.Content,
    onRate: (WorkoutRating) -> Unit,
    onDone: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val locale = LocalConfiguration.current.locales[0]
    val volumen = remember(locale) { NumberFormat.getNumberInstance(locale).apply { maximumFractionDigits = 1 } }

    Text(
        text = stringResource(R.string.complete_label),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = colors.secondary,
    )
    VictoryOrb()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6),
    ) {
        Text(
            text = stringResource(R.string.complete_title_demo),
            style = MaterialTheme.typography.headlineSmall,
            color = colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.complete_subtitle_demo),
            style = VoraTheme.text.heroSubtitle,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
    VoraCard(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SummaryStat(
                value = stringResource(R.string.details_minutes, state.detail.durationMinutes),
                label = stringResource(R.string.complete_duration),
            )
            SummaryStat(
                value = stringResource(R.string.complete_exercises_value, state.exercisesCompleted, state.detail.exercises.size),
                label = stringResource(R.string.complete_exercises),
            )
            SummaryStat(
                value = stringResource(R.string.active_weight, volumen.format(state.volumeKg)),
                label = stringResource(R.string.complete_volume),
            )
        }
    }
    VoraCoachNote(
        label = stringResource(R.string.complete_insight_label),
        text = stringResource(R.string.complete_insight_demo, state.currentExercise.name),
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10),
    ) {
        Text(
            text = stringResource(R.string.complete_rate),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
        ) {
            WorkoutRating.entries.forEach { rating ->
                VoraChip(
                    text = stringResource(rating.label()),
                    selected = state.rating == rating,
                    onClick = { onRate(rating) },
                    selectedStyle = VoraChipSelectedStyle.Dark,
                )
            }
        }
    }
    VoraButton(
        text = stringResource(R.string.complete_done),
        onClick = onDone,
        modifier = Modifier.fillMaxWidth(),
    )
}

/**
 * Orbe del nodo 1:685: un resplandor menta de 180 y adentro el orbe de 120 con el sello. Las
 * proporciones del orbe (aro y esfera) son las de VoraOrb.
 */
@Composable
private fun VictoryOrb() {
    Box(
        modifier = Modifier
            // 180 = 1,5 veces el orbe de 120, medido en el nodo
            .size(VoraTheme.sizes.orbMuted * 1.5f)
            .background(
                Brush.radialGradient(listOf(VoraTheme.colors.mintBorderSoft, Color.Transparent)),
                CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        VoraOrb(size = VoraTheme.sizes.orbMuted) {
            Image(painter = painterResource(R.drawable.ic_badge_check), contentDescription = null)
        }
    }
}

/** Un dato del resumen: cifra de 18 Bold arriba y etiqueta de 11 abajo (nodos 1:699 a 1:707). */
@Composable
private fun SummaryStat(value: String, label: String) {
    Column(
        modifier = Modifier.semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = VoraTheme.colors.textMuted)
    }
}

@Preview(showBackground = true, heightDp = 844)
@Composable
private fun WorkoutCompleteScreenPreview() {
    fun ejercicio(id: String, name: String) =
        Exercise(id, name, "Back", sets = 3, reps = 10, restSeconds = 90, instructions = "", tip = "")
    Parcialtp3Theme {
        WorkoutCompleteScreen(
            state = ActiveWorkoutUiState.Content(
                detail = WorkoutDetail(
                    id = "w01",
                    name = "Upper Body Strength",
                    level = WorkoutLevel.INTERMEDIATE,
                    durationMinutes = 42,
                    exerciseCount = 2,
                    description = "",
                    exercises = listOf(ejercicio("e01", "Bench Press"), ejercicio("e02", "Seated Cable Row")),
                ),
                exerciseIndex = 1,
                setIndex = 3,
                completedSets = List(6) { CompletedSet(if (it < 3) "e01" else "e02", 10, 45.0) },
                rating = WorkoutRating.GOOD,
            ),
            onRate = {},
            onDone = {},
        )
    }
}
