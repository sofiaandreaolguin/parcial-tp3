package com.example.parcial_tp3.ui.screens.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCardOnDark
import com.example.parcial_tp3.ui.components.VoraInfoMessage
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * El error con Retry de 01 a 06 (RF05). El Figma no lo dibuja: se arma con los componentes que ya
 * existen, como NutritionStatus. [onDark] es para los paneles verdes de 04 y 05.
 */
@Composable
internal fun WorkoutStatusMessage(
    text: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onDark: Boolean = false,
) {
    if (onDark) {
        VoraCardOnDark(modifier.fillMaxWidth()) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
            VoraButton(
                text = stringResource(R.string.workout_retry),
                onClick = onRetry,
                style = VoraButtonStyle.OutlinedOnDark,
                size = VoraButtonSize.Small,
            )
        }
    } else {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
            VoraInfoMessage(text = text, icon = painterResource(R.drawable.ic_info))
            VoraButton(
                text = stringResource(R.string.workout_retry),
                onClick = onRetry,
                style = VoraButtonStyle.Outlined,
                size = VoraButtonSize.Small,
            )
        }
    }
}
