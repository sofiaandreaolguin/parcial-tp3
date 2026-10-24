package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.StringRes
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
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
 * Nutrition / 05 — Food Analysis (nodo 1:1077). Va encima y hasta el borde (RUTAS_A_SANGRE),
 * sobre el verde. Simulada: no usa micrófono ni cámara (NAV05).
 *
 */
@Composable
fun FoodAnalysisScreen(
    state: FoodCaptureUiState,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding(),
    ) {
        Box(contentAlignment = Alignment.CenterStart) {
            Text(
                text = stringResource(R.string.nutrition_analysis_title),
                style = MaterialTheme.typography.labelLarge,
                color = VoraTheme.colors.onDarkMuted,
                modifier = Modifier.padding(start = VoraTheme.spacing.screen),
            )
            VoraTopBar(
                title = null,
                onBack = null,
                actions = {
                    VoraCircleButton(
                        icon = painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.nutrition_analysis_close),
                        onClick = onCancel,
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
                .padding(horizontal = VoraTheme.spacing.s24, vertical = VoraTheme.spacing.s24),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // El layout del orbe incluye la onda (15 %): en el frame el orbe queda a 40 del
            // encabezado y la onda a 29,5, y el título a 204 desde ahí; 32 y 12 dan lo mismo a
            // 2,5 dp.
            VoraOrb(
                modifier = Modifier.padding(top = VoraTheme.spacing.s8),
                onDark = true,
                withRipple = true,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_waveform),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(VoraTheme.sizes.iconOrb),
                )
            }
            Text(
                text = stringResource(R.string.nutrition_analysis_heading),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(top = VoraTheme.spacing.s12)
                    .semantics { heading() },
            )
            VoraCardOnDark(
                modifier = Modifier
                    .padding(top = VoraTheme.spacing.s40)
                    .fillMaxWidth(),
                padding = VoraTheme.spacing.s14,
                spacing = VoraTheme.spacing.s4,
            ) {
                Text(
                    text = stringResource(R.string.nutrition_analysis_input_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text = stringResource(R.string.nutrition_analysis_input_demo),
                    style = VoraTheme.text.heroSubtitle,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Column(
                modifier = Modifier
                    .padding(top = VoraTheme.spacing.s20)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
            ) {
                AnalysisStep.entries.forEach { step ->
                    AnalysisStepRow(
                        text = stringResource(step.labelRes()),
                        done = step.ordinal < state.analysisStep.ordinal,
                        active = step == state.analysisStep,
                    )
                }
            }
            // el Small ya suma 8 arriba: con 8 más quedan los 16 que hay entre los pasos y el enlace
            VoraButton(
                text = stringResource(R.string.nutrition_analysis_cancel),
                onClick = onCancel,
                modifier = Modifier.padding(top = VoraTheme.spacing.s8),
                style = VoraButtonStyle.TextOnDark,
                size = VoraButtonSize.Small,
            )
        }
    }
}

@Composable
private fun AnalysisStepRow(text: String, done: Boolean, active: Boolean) {
    val dot: Color = when {
        done -> MaterialTheme.colorScheme.secondary
        active -> MaterialTheme.colorScheme.tertiary
        else -> VoraTheme.colors.onDarkSubtle
    }
    val textColor = if (done || active) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        VoraTheme.colors.onDarkBorder
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // el punto del frame mide 16; VoraStatusDot fija 8 pero respeta el tamaño que le llegue
        VoraStatusDot(dot, Modifier.size(VoraTheme.sizes.iconXSmall))
        Text(
            text = text,
            style = VoraTheme.text.heroSubtitle.let {
                if (active) it.copy(fontWeight = FontWeight.SemiBold) else it
            },
            color = textColor,
        )
    }
}

@StringRes
private fun AnalysisStep.labelRes(): Int = when (this) {
    AnalysisStep.Identifying -> R.string.nutrition_analysis_step_identifying
    AnalysisStep.Estimating -> R.string.nutrition_analysis_step_estimating
    AnalysisStep.Calculating -> R.string.nutrition_analysis_step_calculating
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun FoodAnalysisIdentifyingPreview() {
    Parcialtp3Theme {
        FoodAnalysisScreen(FoodCaptureUiState(analysisStep = AnalysisStep.Identifying), onCancel = {})
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun FoodAnalysisEstimatingPreview() {
    Parcialtp3Theme {
        FoodAnalysisScreen(FoodCaptureUiState(analysisStep = AnalysisStep.Estimating), onCancel = {})
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun FoodAnalysisCalculatingPreview() {
    Parcialtp3Theme {
        FoodAnalysisScreen(FoodCaptureUiState(analysisStep = AnalysisStep.Calculating), onCancel = {})
    }
}
