package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/** De que color sale el circulo. El tamano lo pide cada frame, por eso va aparte. */
enum class VoraCircleButtonTone {
    /** Verde lleno: "TAP TO TALK" de Idle y el enviar de Conversation. */
    Primary,
    /** Menta: el microfono grande de Listening. */
    Mint,
    /** Fondo de pantalla: el + del composer y el escuchar de una burbuja. */
    Surface,
    /** Blanco al 12 %, para los controles sobre el verde de Speaking. */
    OnDarkSubtle,
}

/**
 * Boton redondo de icono. Aparece seis veces en el asistente con distinto tamano y color:
 * microfono de Idle (72) y de Listening (80), los tres controles de audio de Speaking (56
 * la pausa, 40 repetir y stop) y el + y el enviar de Conversation (40).
 *
 * El circulo dibujado puede ser mas chico que el area tactil, que por omision no baja de
 * 48, igual que el selector de Active Workout. [touchSize] la achica donde agrandarla
 * rompe el ancho de la fila, como el "Listen to response" dentro de una burbuja.
 */
@Composable
fun VoraCircleButton(
    icon: Painter,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: VoraCircleButtonTone = VoraCircleButtonTone.Primary,
    size: Dp = VoraTheme.sizes.circleButton,
    iconSize: Dp = VoraTheme.sizes.icon,
    touchSize: Dp = VoraTheme.sizes.touchTarget,
    enabled: Boolean = true,
) {
    val colors = MaterialTheme.colorScheme
    val extra = VoraTheme.colors
    val (fondo, tinte) = when (tone) {
        VoraCircleButtonTone.Primary -> colors.primary to colors.onPrimary
        VoraCircleButtonTone.Mint -> colors.secondary to extra.onAccent
        VoraCircleButtonTone.Surface -> colors.background to colors.onSurface
        VoraCircleButtonTone.OnDarkSubtle -> extra.onDarkSubtle to colors.onPrimary
    }
    Box(
        modifier = modifier
            .size(maxOf(size, touchSize))
            .clip(CircleShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier.size(size).background(fondo, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = icon,
                contentDescription = contentDescription,
                tint = tinte,
                modifier = Modifier.size(iconSize),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraCircleButtonPreview() {
    Parcialtp3Theme {
        Row(
            Modifier.padding(VoraTheme.spacing.screen),
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VoraCircleButton(
                icon = painterResource(R.drawable.ic_mic),
                contentDescription = stringResource(R.string.voice_tap_to_talk),
                onClick = {},
                size = VoraTheme.sizes.micCircle,
                iconSize = VoraTheme.sizes.iconMic,
            )
            VoraCircleButton(
                icon = painterResource(R.drawable.ic_mic),
                contentDescription = stringResource(R.string.voice_tap_to_talk),
                onClick = {},
                tone = VoraCircleButtonTone.Mint,
                size = VoraTheme.sizes.micCircleActive,
                iconSize = VoraTheme.sizes.iconMic,
            )
            VoraCircleButton(
                icon = painterResource(R.drawable.ic_send),
                contentDescription = stringResource(R.string.conversation_send),
                onClick = {},
            )
            VoraCircleButton(
                icon = painterResource(R.drawable.ic_plus),
                contentDescription = null,
                onClick = {},
                tone = VoraCircleButtonTone.Surface,
            )
        }
    }
}
