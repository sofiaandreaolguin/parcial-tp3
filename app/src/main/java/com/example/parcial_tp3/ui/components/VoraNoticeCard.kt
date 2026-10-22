package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Tarjeta de aviso con icono, texto y una o dos acciones: el "Microphone access needed" de
 * Voice / 06. Medidas del frame: radio 20, relleno 20, separacion 16, circulo del icono de
 * 36 con el icono de 18 separado 10 del titulo, y los dos botones a la mitad cada uno.
 *
 * El color del circulo lo pasa quien la usa, porque cambia con el aviso (rosa en el del
 * microfono). Nutrition / 04 a 06 tiene el mismo patron con la camara.
 *
 * @param secondaryText si es null, la tarjeta queda con un solo boton.
 */
@Composable
fun VoraNoticeCard(
    icon: Painter,
    iconBackground: Color,
    title: String,
    body: String,
    primaryText: String,
    onPrimary: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryText: String? = null,
    onSecondary: (() -> Unit)? = null,
) {
    VoraCard(modifier, padding = VoraTheme.spacing.s20, radius = VoraTheme.radius.r20) {
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(VoraTheme.sizes.iconCircle).background(iconBackground, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(VoraTheme.sizes.iconSmall),
                    )
                }
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
            }
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
                VoraButton(
                    text = primaryText,
                    onClick = onPrimary,
                    modifier = Modifier.weight(1f),
                    size = VoraButtonSize.Small,
                )
                if (secondaryText != null && onSecondary != null) {
                    VoraButton(
                        text = secondaryText,
                        onClick = onSecondary,
                        modifier = Modifier.weight(1f),
                        style = VoraButtonStyle.Outlined,
                        size = VoraButtonSize.Small,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraNoticeCardPreview() {
    Parcialtp3Theme {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(VoraTheme.spacing.screen),
        ) {
            VoraNoticeCard(
                icon = painterResource(R.drawable.ic_mic),
                iconBackground = VoraTheme.colors.noticeIcon,
                title = stringResource(R.string.voice_mic_notice_title),
                body = stringResource(R.string.voice_mic_notice_body),
                primaryText = stringResource(R.string.voice_allow_access),
                onPrimary = {},
                modifier = Modifier.fillMaxWidth(),
                secondaryText = stringResource(R.string.voice_not_now),
                onSecondary = {},
            )
        }
    }
}
