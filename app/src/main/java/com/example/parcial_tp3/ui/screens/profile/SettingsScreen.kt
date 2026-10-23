package com.example.parcial_tp3.ui.screens.profile

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraListRow
import com.example.parcial_tp3.ui.components.VoraTopBarLarge
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onLogOut: () -> Unit,
    onDemo: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        VoraTopBarLarge(title = stringResource(R.string.profile_settings), onBack = onBack)
        Column(
            modifier = Modifier.padding(
                start = VoraTheme.spacing.screen,
                end = VoraTheme.spacing.screen,
                bottom = VoraTheme.spacing.s20,
            ),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
        ) {
            ProfileSectionLabel(stringResource(R.string.settings_account))
            VoraListRow(
                title = state.fullName,
                subtitle = state.email,
                icon = painterResource(R.drawable.ic_person),
                onClick = onEditProfile,
            )
            SettingsRow(R.string.settings_premium, R.string.settings_premium_sub, R.drawable.ic_star, onDemo)
            SettingsRow(R.string.settings_manage_account, null, R.drawable.ic_edit, onDemo)

            ProfileSectionLabel(stringResource(R.string.settings_app_preferences))
            SettingsRow(R.string.settings_notifications, R.string.settings_notifications_sub, R.drawable.ic_bell, onDemo)
            SettingsRow(R.string.settings_units, R.string.settings_units_sub, R.drawable.ic_units, onDemo)
            SettingsRow(R.string.settings_appearance, R.string.settings_appearance_sub, R.drawable.ic_eye, onDemo)

            ProfileSectionLabel(stringResource(R.string.settings_connected_data))
            SettingsRow(R.string.settings_health, R.string.settings_health_sub, R.drawable.ic_activity, onDemo)

            ProfileSectionLabel(stringResource(R.string.settings_privacy_security))
            SettingsRow(R.string.settings_privacy_policy, null, R.drawable.ic_lock, onDemo)
            // NAV05: no pide permisos ni abre los ajustes del sistema, solo avisa
            SettingsRow(R.string.settings_permissions, null, R.drawable.ic_shield, onDemo)

            ProfileSectionLabel(stringResource(R.string.settings_support))
            SettingsRow(R.string.settings_help, null, R.drawable.ic_help, onDemo)
            SettingsRow(R.string.settings_contact, null, R.drawable.ic_mail, onDemo)

            VoraButton(
                text = stringResource(R.string.settings_log_out),
                onClick = onLogOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = VoraTheme.spacing.s24),
                style = VoraButtonStyle.Destructive,
            )
            Text(
                text = stringResource(R.string.settings_version),
                style = MaterialTheme.typography.bodySmall,
                color = VoraTheme.colors.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = VoraTheme.spacing.s8),
            )
        }
    }
}

@Composable
private fun SettingsRow(
    @StringRes title: Int,
    @StringRes subtitle: Int?,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
) {
    VoraListRow(
        title = stringResource(title),
        subtitle = subtitle?.let { stringResource(it) },
        icon = painterResource(icon),
        onClick = onClick,
    )
}

@Preview(showBackground = true, heightDp = 1350)
@Composable
private fun SettingsScreenPreview() {
    Parcialtp3Theme {
        SettingsScreen(
            state = SettingsUiState(fullName = "Alex Morgan", email = "alex@morgan.com"),
            onBack = {},
            onEditProfile = {},
            onLogOut = {},
            onDemo = {},
        )
    }
}
