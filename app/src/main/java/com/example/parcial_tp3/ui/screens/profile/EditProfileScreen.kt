package com.example.parcial_tp3.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.Gender
import com.example.parcial_tp3.ui.components.VoraAvatar
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraInfoMessage
import com.example.parcial_tp3.ui.components.VoraTextField
import com.example.parcial_tp3.ui.components.VoraTopBarLarge
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EditProfileScreen(
    state: EditProfileUiState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDemo: () -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]
    val pattern = stringResource(R.string.edit_birth_date_pattern)
    val formatter = remember(pattern, locale) { DateTimeFormatter.ofPattern(pattern, locale) }
    val birthDate = state.birthDate.format(formatter)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        VoraTopBarLarge(title = stringResource(R.string.profile_edit), onBack = onBack)
        Column(
            modifier = Modifier.padding(
                start = VoraTheme.spacing.screen,
                end = VoraTheme.spacing.screen,
                bottom = VoraTheme.spacing.s20,
            ),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                VoraAvatar(
                    modifier = Modifier.padding(top = VoraTheme.spacing.s12),
                    size = VoraTheme.sizes.avatarLarge,
                )
                VoraButton(
                    text = stringResource(R.string.edit_change_photo),
                    onClick = onDemo,
                    style = VoraButtonStyle.Text,
                    size = VoraButtonSize.Small,
                )
            }
            ReadOnlyField(stringResource(R.string.edit_first_name), state.firstName)
            ReadOnlyField(stringResource(R.string.edit_last_name), state.lastName)
            ReadOnlyField(stringResource(R.string.edit_birth_date), birthDate)
            Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
                ReadOnlyField(
                    label = stringResource(R.string.edit_height),
                    value = stringResource(R.string.edit_height_value, state.heightCm),
                    modifier = Modifier.weight(1f),
                )
                ReadOnlyField(
                    label = stringResource(R.string.edit_weight),
                    value = stringResource(R.string.edit_weight_value, state.weightKg),
                    modifier = Modifier.weight(1f),
                )
            }
            ReadOnlyField(stringResource(R.string.edit_gender), stringResource(state.gender.label()))
            VoraInfoMessage(
                text = stringResource(R.string.edit_info),
                icon = painterResource(R.drawable.ic_info),
            )
            Spacer(Modifier.height(VoraTheme.spacing.s8))
            VoraButton(
                text = stringResource(R.string.edit_save),
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(R.string.edit_footer),
                style = VoraTheme.text.footnote,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ReadOnlyField(label: String, value: String, modifier: Modifier = Modifier) {
    VoraTextField(
        value = value,
        onValueChange = {},
        modifier = modifier,
        label = label,
        readOnly = true,
    )
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun EditProfileScreenPreview() {
    Parcialtp3Theme {
        EditProfileScreen(
            state = EditProfileUiState(
                firstName = "Alex",
                lastName = "Morgan",
                birthDate = LocalDate.of(1995, 3, 14),
                heightCm = 178,
                weightKg = 76,
                gender = Gender.PREFER_NOT_TO_SAY,
            ),
            onBack = {},
            onSave = {},
            onDemo = {},
        )
    }
}
