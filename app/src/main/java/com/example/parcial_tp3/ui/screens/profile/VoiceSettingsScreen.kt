package com.example.parcial_tp3.ui.screens.profile

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.CoachingStyle
import com.example.parcial_tp3.domain.model.ResponseStyle
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraOptionCard
import com.example.parcial_tp3.ui.components.VoraSegmentedControl
import com.example.parcial_tp3.ui.components.VoraSegmentedStyle
import com.example.parcial_tp3.ui.components.VoraToggle
import com.example.parcial_tp3.ui.components.VoraTopBarLarge
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

@Composable
fun VoiceSettingsScreen(
    state: VoiceSettingsUiState,
    onBack: () -> Unit,
    onPrivacy: () -> Unit,
    onDemo: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        VoraTopBarLarge(title = stringResource(R.string.coach_title), onBack = onBack)
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
                text = stringResource(R.string.coach_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            ProfileSectionLabel(stringResource(R.string.coach_style))
            VoraSegmentedControl(
                options = CoachingStyle.entries.map { stringResource(it.label()) },
                selectedIndex = state.coachingStyle.ordinal,
                onSelect = { onDemo() },
                style = VoraSegmentedStyle.Boxes,
            )
            ActiveVoiceCard(onPreview = onDemo)
            ProfileSectionLabel(stringResource(R.string.coach_speaking))
            VoraCard(modifier = Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s6) {
                ToggleRow(stringResource(R.string.coach_speak_aloud), state.speakAloud, onDemo)
                ToggleRow(stringResource(R.string.coach_autoplay), state.autoPlay, onDemo)
                ToggleRow(stringResource(R.string.coach_interruptions), state.allowInterruptions, onDemo)
                Text(
                    text = stringResource(R.string.coach_interruptions_hint),
                    style = VoraTheme.text.footnote,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = VoraTheme.spacing.s6, end = VoraTheme.spacing.s6, top = VoraTheme.spacing.s6),
                )
            }
            ProfileSectionLabel(stringResource(R.string.coach_response))
            Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                ResponseStyle.entries.forEach { style ->
                    VoraOptionCard(
                        title = stringResource(style.label()),
                        selected = style == state.responseStyle,
                        onClick = onDemo,
                        subtitle = stringResource(style.subtitle()),
                        role = Role.RadioButton,
                    )
                }
            }
            VoraCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = VoraTheme.spacing.s8),
                padding = VoraTheme.spacing.s6,
            ) {
                ToggleRow(stringResource(R.string.coach_voice_feedback), state.voiceFeedback, onDemo)
                ToggleRow(stringResource(R.string.coach_haptic), state.hapticFeedback, onDemo)
            }
            ControlCard(onPrivacy)
        }
    }
}

@Composable
private fun ActiveVoiceCard(onPreview: () -> Unit) {
    VoraCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = VoraTheme.spacing.s8),
    ) {
        ProfileSectionLabel(stringResource(R.string.coach_active_voice), modifier = Modifier)
        Row(
            modifier = Modifier.padding(top = VoraTheme.spacing.s12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(VoraTheme.sizes.iconCircle)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_volume),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Text(
                text = stringResource(R.string.coach_voice_name),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = VoraTheme.spacing.s12),
            )
            VoraButton(
                text = stringResource(R.string.coach_preview),
                onClick = onPreview,
                style = VoraButtonStyle.Outlined,
                size = VoraButtonSize.Small,
                icon = painterResource(R.drawable.ic_preview),
            )
        }
    }
}

/**
 * Fila con interruptor: se toca la fila entera, el interruptor solo dibuja (KDoc de VoraToggle).
 * 36 de alto y 12 al borde con el padding 6 de la tarjeta, como en el Figma (no llega a 48).
 */
@Composable
private fun ToggleRow(text: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = { onToggle() })
            .padding(VoraTheme.spacing.s6),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        VoraToggle(checked = checked, onCheckedChange = null)
    }
}

@Composable
private fun ControlCard(onPrivacy: () -> Unit) {
    val shape = RoundedCornerShape(VoraTheme.radius.r16)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = VoraTheme.spacing.s8)
            .background(VoraTheme.colors.mintTranslucent, shape)
            .border(VoraTheme.sizes.border, VoraTheme.colors.mintBorderSoft, shape)
            .padding(VoraTheme.spacing.s16),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
    ) {
        Text(
            text = stringResource(R.string.coach_control_title),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(R.string.coach_control_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.coach_privacy_link),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(role = Role.Button, onClick = onPrivacy),
        )
    }
}

@StringRes
private fun ResponseStyle.label(): Int = when (this) {
    ResponseStyle.CONCISE -> R.string.response_concise
    ResponseStyle.BALANCED -> R.string.response_balanced
    ResponseStyle.DETAILED -> R.string.response_detailed
}

@StringRes
private fun ResponseStyle.subtitle(): Int = when (this) {
    ResponseStyle.CONCISE -> R.string.response_concise_sub
    ResponseStyle.BALANCED -> R.string.response_balanced_sub
    ResponseStyle.DETAILED -> R.string.response_detailed_sub
}

@Preview(showBackground = true, heightDp = 1150)
@Composable
private fun VoiceSettingsScreenPreview() {
    Parcialtp3Theme {
        VoiceSettingsScreen(
            state = VoiceSettingsUiState(
                coachingStyle = CoachingStyle.BALANCED,
                responseStyle = ResponseStyle.BALANCED,
                speakAloud = true,
                autoPlay = true,
                allowInterruptions = true,
                voiceFeedback = true,
                hapticFeedback = true,
            ),
            onBack = {},
            onPrivacy = {},
            onDemo = {},
        )
    }
}
