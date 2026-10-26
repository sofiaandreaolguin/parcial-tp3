package com.example.parcial_tp3.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.Experience
import com.example.parcial_tp3.domain.model.Goal
import com.example.parcial_tp3.domain.model.TrainingDays
import com.example.parcial_tp3.domain.model.WorkoutType
import com.example.parcial_tp3.ui.components.VoraAvatar
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraListRow
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraPanel
import com.example.parcial_tp3.ui.components.VoraSectionLabel
import com.example.parcial_tp3.ui.components.VoraTag
import com.example.parcial_tp3.ui.components.VoraTagTone
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

@Composable
fun ProfileDashboardScreen(
    state: ProfileDashboardUiState,
    onSettings: () -> Unit,
    onEditProfile: () -> Unit,
    onReviewPreferences: () -> Unit,
    onGoals: () -> Unit,
    onPreferences: () -> Unit,
    onDemo: () -> Unit,
    onRetry: () -> Unit = {},
) {
    val goals = goalsSummary(state.goals)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = VoraTheme.spacing.screen, vertical = VoraTheme.spacing.s12),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
    ) {
        Header(onSettings)
        state.loadError?.let { message ->
            VoraCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
                VoraButton(
                    text = stringResource(R.string.profile_retry),
                    onClick = onRetry,
                    style = VoraButtonStyle.Outlined,
                    size = VoraButtonSize.Small,
                )
            }
        }
        IdentityCard(state.fullName, onEditProfile)
        SnapshotCard(state, goals)
        PersonalisationPanel(onReviewPreferences)
        VoraListRow(
            title = stringResource(R.string.profile_goals),
            subtitle = goals,
            icon = painterResource(R.drawable.ic_target),
            onClick = onGoals,
        )
        VoraListRow(
            title = stringResource(R.string.profile_preferences),
            subtitle = stringResource(R.string.profile_preferences_sub),
            icon = painterResource(R.drawable.ic_sliders),
            onClick = onPreferences,
        )
        VoraListRow(
            title = stringResource(R.string.profile_connected),
            subtitle = stringResource(R.string.profile_connected_sub),
            icon = painterResource(R.drawable.ic_activity),
            onClick = onDemo,
            trailing = { VoraTag(stringResource(R.string.profile_synced), tone = VoraTagTone.Dark) },
        )
        VoraListRow(
            title = stringResource(R.string.profile_account),
            subtitle = stringResource(R.string.profile_account_sub),
            icon = painterResource(R.drawable.ic_lock),
            onClick = onDemo,
        )
    }
}

/** "Build strength & improve fitness": el primero con mayúscula y el resto en minúscula. */
@Composable
private fun goalsSummary(goals: List<Goal>): String =
    goals.map { stringResource(it.label()) }
        .reduceOrNull { acc, next ->
            stringResource(R.string.profile_goals_join, acc, next.replaceFirstChar { it.lowercase() })
        }
        .orEmpty()

@Composable
private fun Header(onSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.tab_profile),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .weight(1f)
                .semantics { heading() },
        )
        Box(
            modifier = Modifier
                .size(VoraTheme.sizes.backCircle)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(VoraTheme.sizes.border, MaterialTheme.colorScheme.outline, CircleShape)
                .clickable(role = Role.Button, onClick = onSettings),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_settings),
                contentDescription = stringResource(R.string.profile_settings),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun IdentityCard(fullName: String, onEditProfile: () -> Unit) {
    VoraCard(modifier = Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s20) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VoraAvatar()
            Spacer(Modifier.height(VoraTheme.spacing.s12))
            Text(
                text = fullName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(VoraTheme.spacing.s4))
            Text(
                text = stringResource(R.string.profile_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(VoraTheme.spacing.s12))
            VoraButton(
                text = stringResource(R.string.profile_edit),
                onClick = onEditProfile,
                style = VoraButtonStyle.Soft,
                size = VoraButtonSize.Small,
            )
        }
    }
}

@Composable
private fun SnapshotCard(state: ProfileDashboardUiState, goals: String) {
    VoraCard(modifier = Modifier.fillMaxWidth()) {
        VoraSectionLabel(stringResource(R.string.profile_snapshot), color = VoraTheme.colors.textMuted)
        Spacer(Modifier.height(VoraTheme.spacing.s8))
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
            ProfileInfoRow(stringResource(R.string.profile_snapshot_goal), goals)
            ProfileInfoRow(
                stringResource(R.string.profile_snapshot_experience),
                stringResource(state.experience.label()),
            )
            ProfileInfoRow(
                stringResource(R.string.profile_snapshot_training),
                stringResource(R.string.profile_per_week, stringResource(state.trainingDays.label())),
            )
            ProfileInfoRow(
                stringResource(R.string.profile_snapshot_workout),
                stringResource(R.string.profile_workout_long, stringResource(state.workoutType.label())),
            )
        }
    }
}

@Composable
private fun PersonalisationPanel(onReviewPreferences: () -> Unit) {
    VoraPanel(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
        ) {
            VoraOrb(size = VoraTheme.sizes.orbSmall, onDark = true)
            Text(
                text = stringResource(R.string.profile_vora_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4)) {
            Text(
                text = stringResource(R.string.profile_vora_title),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = stringResource(R.string.profile_vora_body),
                style = MaterialTheme.typography.bodyMedium,
                color = VoraTheme.colors.onDarkMuted,
            )
        }
        VoraButton(
            text = stringResource(R.string.profile_review),
            onClick = onReviewPreferences,
            style = VoraButtonStyle.Cta,
            size = VoraButtonSize.Small,
        )
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun ProfileDashboardScreenPreview() {
    Parcialtp3Theme {
        ProfileDashboardScreen(
            state = ProfileDashboardUiState(
                fullName = "Alex Morgan",
                goals = listOf(Goal.BUILD_STRENGTH, Goal.IMPROVE_FITNESS),
                experience = Experience.INTERMEDIATE,
                trainingDays = TrainingDays.FOUR,
                workoutType = WorkoutType.STRENGTH,
            ),
            onSettings = {},
            onEditProfile = {},
            onReviewPreferences = {},
            onGoals = {},
            onPreferences = {},
            onDemo = {},
        )
    }
}
