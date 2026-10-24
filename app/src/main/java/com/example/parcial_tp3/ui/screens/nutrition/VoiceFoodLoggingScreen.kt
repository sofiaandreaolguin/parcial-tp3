package com.example.parcial_tp3.ui.screens.nutrition

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCardOnDark
import com.example.parcial_tp3.ui.components.VoraCircleButton
import com.example.parcial_tp3.ui.components.VoraCircleButtonTone
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraStatusDot
import com.example.parcial_tp3.ui.components.VoraTopBar
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Nutrition / 04 — Voice Food Logging. Va encima y hasta el borde (RUTAS_A_SANGRE),
 * sobre el verde. Simulada/mockeada.
 *
 */
@Composable
fun VoiceFoodLoggingScreen(
    state: FoodCaptureUiState,
    onDone: () -> Unit,
    onTypeInstead: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding(),
    ) {
        Box(contentAlignment = Alignment.CenterStart) {
            Row(
                modifier = Modifier.padding(start = VoraTheme.spacing.screen),
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                VoraStatusDot(MaterialTheme.colorScheme.secondary)
                Text(
                    text = stringResource(R.string.nutrition_voice_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            VoraTopBar(
                title = null,
                onBack = null,
                actions = {
                    VoraCircleButton(
                        icon = painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.nutrition_voice_close),
                        onClick = onClose,
                        modifier = Modifier.padding(end = VoraTheme.spacing.s16),
                        tone = VoraCircleButtonTone.OnDarkSubtle,
                        iconSize = VoraTheme.sizes.iconTiny,
                    )
                },
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = VoraTheme.spacing.s24)
                .padding(top = VoraTheme.spacing.s24, bottom = VoraTheme.spacing.s24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s20),
        ) {
            // La onda (15 %) entra en el layout del orbe y en el frame sobresale: por eso 24 arriba y
            // 20 abajo y no los 40 y 32 del nodo, que se miden hasta el halo.
            VoraOrb(size = VoraTheme.sizes.orbListening, onDark = true, withRipple = true) {
                Icon(
                    painter = painterResource(R.drawable.ic_waveform),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(VoraTheme.sizes.iconOrb),
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
            ) {
                Text(
                    text = stringResource(R.string.nutrition_voice_listening),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = stringResource(R.string.nutrition_voice_hint),
                    style = VoraTheme.text.heroSubtitle,
                    color = VoraTheme.colors.onDarkMuted,
                    textAlign = TextAlign.Center,
                )
            }
            // el frame deja 40 entre el texto y el panel (20 de la columna + 20 de este padding) y
            // 16 entre panel y botón; el enlace queda a 8 del botón porque el Small ya suma 8
            Column(
                modifier = Modifier.padding(top = VoraTheme.spacing.s20),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
            ) {
                VoraCardOnDark(Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(state.transcript),
                        style = VoraTheme.text.panelBodySmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
                ) {
                    VoraButton(
                        text = stringResource(R.string.nutrition_voice_done),
                        onClick = onDone,
                        modifier = Modifier.fillMaxWidth(),
                        style = VoraButtonStyle.Mint,
                    )
                    VoraButton(
                        text = stringResource(R.string.nutrition_voice_type_instead),
                        onClick = onTypeInstead,
                        style = VoraButtonStyle.TextOnDark,
                        size = VoraButtonSize.Small,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun VoiceFoodLoggingScreenPreview() {
    Parcialtp3Theme {
        VoiceFoodLoggingScreen(
            state = FoodCaptureUiState(),
            onDone = {},
            onTypeInstead = {},
            onClose = {},
        )
    }
}
