package com.example.parcial_tp3.ui.screens.profile

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.CoachingStyle
import com.example.parcial_tp3.domain.model.Goal
import com.example.parcial_tp3.domain.model.TrainingDays
import com.example.parcial_tp3.domain.model.WorkoutType
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraChip
import com.example.parcial_tp3.ui.components.VoraOptionCard
import com.example.parcial_tp3.ui.components.VoraSegmentedControl
import com.example.parcial_tp3.ui.components.VoraSegmentedStyle
import com.example.parcial_tp3.ui.components.VoraTopBarLarge
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

@Composable
fun GoalsScreen(
    state: GoalsUiState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDemo: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        VoraTopBarLarge(title = stringResource(R.string.goals_title), onBack = onBack)
        Column(
            modifier = Modifier.padding(
                start = VoraTheme.spacing.screen,
                end = VoraTheme.spacing.screen,
                top = VoraTheme.spacing.s12,
                bottom = VoraTheme.spacing.s20,
            ),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        ) {
            Text(
                text = stringResource(R.string.goals_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            ProfileSectionLabel(stringResource(R.string.goals_primary))
            Goal.entries.forEach { goal ->
                VoraOptionCard(
                    title = stringResource(goal.label()),
                    selected = goal in state.goals,
                    onClick = onDemo,
                    subtitle = goal.subtitle()?.let { stringResource(it) },
                )
            }
            ProfileSectionLabel(stringResource(R.string.goals_frequency))
            VoraSegmentedControl(
                options = TrainingDays.entries.map { stringResource(it.label()) },
                selectedIndex = state.trainingDays.ordinal,
                onSelect = { onDemo() },
                style = VoraSegmentedStyle.Boxes,
            )
            ProfileSectionLabel(stringResource(R.string.goals_workout))
            ProfileChipRow {
                WorkoutType.entries.forEach { type ->
                    VoraChip(
                        text = stringResource(type.label()),
                        selected = type == state.workoutType,
                        onClick = onDemo,
                    )
                }
            }
            ProfileSectionLabel(stringResource(R.string.goals_coaching))
            VoraOptionCard(
                title = stringResource(state.coachingStyle.label()),
                selected = true,
                onClick = onDemo,
                subtitle = state.coachingStyle.subtitle()?.let { stringResource(it) },
                role = Role.RadioButton,
            )
            VoraButton(
                text = stringResource(R.string.profile_save_preferences),
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = VoraTheme.spacing.s8),
            )
        }
    }
}

// El Figma solo dibuja subtítulo en las opciones elegidas; las demás no lo tienen.
@StringRes
private fun Goal.subtitle(): Int? = when (this) {
    Goal.BUILD_STRENGTH -> R.string.goal_build_strength_sub
    Goal.IMPROVE_FITNESS -> R.string.goal_improve_fitness_sub
    else -> null
}

@StringRes
private fun CoachingStyle.subtitle(): Int? = when (this) {
    CoachingStyle.BALANCED -> R.string.coaching_balanced_sub
    else -> null
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun GoalsScreenPreview() {
    Parcialtp3Theme {
        GoalsScreen(
            state = GoalsUiState(
                goals = setOf(Goal.BUILD_STRENGTH, Goal.IMPROVE_FITNESS),
                trainingDays = TrainingDays.FOUR,
                workoutType = WorkoutType.STRENGTH,
                coachingStyle = CoachingStyle.BALANCED,
            ),
            onBack = {},
            onSave = {},
            onDemo = {},
        )
    }
}
