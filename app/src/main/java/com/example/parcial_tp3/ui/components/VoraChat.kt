package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/*
 * Anchos medidos en Voice / 05, sobre los 350 que mide el hilo: la burbuja propia ocupa
 * 280 y la fila de VORA 308 (avatar de 28 + 10 de separacion + burbuja de 270).
 */
private const val BURBUJA_PROPIA = 0.80f
private const val FILA_VORA = 0.88f

/** Quien dijo el mensaje: cambia el lado, el color y que esquina lleva la punta. */
enum class VoraChatAuthor { Me, Vora }

/**
 * Un mensaje de una conversacion, con su avatar y su hora. Dibuja la fila entera, asi la
 * pantalla solo arma la lista.
 *
 * Los radios no son parejos: 16 en tres esquinas y 4 en la que apunta al autor (abajo a la
 * derecha en los propios, abajo a la izquierda en los de VORA). Es lo que les da la punta.
 *
 * @param time null en los mensajes que muestran el pie en lugar de la hora.
 * @param footer ranura del pie de la burbuja: hoy, la fila de escuchar la respuesta.
 */
@Composable
fun VoraChatBubble(
    text: String,
    author: VoraChatAuthor,
    modifier: Modifier = Modifier,
    time: String? = null,
    footer: @Composable (() -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    val propio = author == VoraChatAuthor.Me
    val r16 = VoraTheme.radius.r16
    val punta = VoraTheme.radius.r4
    val forma = if (propio) {
        RoundedCornerShape(topStart = r16, topEnd = r16, bottomEnd = punta, bottomStart = r16)
    } else {
        RoundedCornerShape(topStart = r16, topEnd = r16, bottomEnd = r16, bottomStart = punta)
    }
    Box(
        modifier.fillMaxWidth(),
        contentAlignment = if (propio) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Row(
            Modifier.fillMaxWidth(if (propio) BURBUJA_PROPIA else FILA_VORA),
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10),
        ) {
            if (!propio) {
                VoraOrb(size = VoraTheme.sizes.orbSmall)
            }
            Column(
                Modifier
                    .weight(1f)
                    .clip(forma)
                    .background(if (propio) colors.primary else colors.surface)
                    .then(
                        if (propio) Modifier
                        else Modifier.border(VoraTheme.sizes.border, colors.outline, forma),
                    )
                    .padding(VoraTheme.spacing.s14),
                verticalArrangement = Arrangement.spacedBy(
                    if (propio) VoraTheme.spacing.s4 else VoraTheme.spacing.s10,
                ),
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (propio) colors.onPrimary else colors.onSurface,
                )
                if (time != null) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (propio) VoraTheme.colors.onDarkMuted else VoraTheme.colors.textMuted,
                        modifier = if (propio) Modifier.fillMaxWidth() else Modifier,
                        textAlign = if (propio) TextAlign.End else null,
                    )
                }
                if (footer != null) footer()
            }
        }
    }
}

/**
 * El pie de una burbuja de VORA que trae audio: "Listen to response (0:14)". En la demo no
 * reproduce nada, por eso [onPlay] puede no hacer nada.
 */
@Composable
fun VoraChatAudioFooter(label: String, onPlay: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(VoraTheme.sizes.border)
                .background(MaterialTheme.colorScheme.outline),
        )
        Row(
            Modifier.padding(top = VoraTheme.spacing.s10),
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VoraCircleButton(
                icon = painterResource(R.drawable.ic_play),
                contentDescription = label,
                onClick = onPlay,
                tone = VoraCircleButtonTone.Surface,
                size = VoraTheme.sizes.playCircle,
                iconSize = VoraTheme.sizes.iconTiny,
                touchSize = VoraTheme.sizes.playCircle,
            )
            Text(
                text = label,
                style = VoraTheme.text.labelAudio,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * La barra de escribir de Voice / 05: el +, el campo en forma de pildora con el microfono
 * adentro, y el boton de enviar. Medidas del frame: fila de 44 de alto separada 12, campo
 * con fondo de pantalla, radio de pildora y relleno 16.
 *
 * El campo no es [VoraTextField]: ese es blanco, con borde y radio 12, y este es gris, sin
 * borde y pildora. Se dibuja aca para no llenar de parametros un componente compartido.
 *
 * **NAV05:** [onMic] no pide permiso de microfono. En la demo no hace nada.
 *
 * @param onAttach null para no dibujar el +.
 * @param onMic null para no dibujar el microfono.
 */
@Composable
fun VoraComposer(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    onAttach: (() -> Unit)? = null,
    onMic: (() -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    val textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.onSurface)
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onAttach != null) {
            VoraCircleButton(
                icon = painterResource(R.drawable.ic_plus),
                contentDescription = stringResource(R.string.conversation_attach),
                onClick = onAttach,
                tone = VoraCircleButtonTone.Surface,
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .semantics { contentDescription = placeholder },
            textStyle = textStyle,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { if (value.isNotBlank()) onSend() }),
            cursorBrush = SolidColor(colors.primary),
            decorationBox = { campo ->
                Row(
                    Modifier
                        .height(VoraTheme.sizes.composerField)
                        .background(colors.background, RoundedCornerShape(VoraTheme.radius.pill))
                        .padding(horizontal = VoraTheme.spacing.s16),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
                ) {
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(text = placeholder, style = textStyle, color = VoraTheme.colors.textMuted)
                        }
                        campo()
                    }
                    if (onMic != null) {
                        Icon(
                            painter = painterResource(R.drawable.ic_mic),
                            contentDescription = stringResource(R.string.conversation_mic),
                            tint = VoraTheme.colors.textMuted,
                            modifier = Modifier.size(VoraTheme.sizes.iconSmall),
                        )
                    }
                }
            },
        )
        VoraCircleButton(
            icon = painterResource(R.drawable.ic_send),
            contentDescription = stringResource(R.string.conversation_send),
            onClick = onSend,
            iconSize = VoraTheme.sizes.iconSmall,
            enabled = value.isNotBlank(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraChatPreview() {
    Parcialtp3Theme {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(VoraTheme.spacing.screen),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s20),
        ) {
            VoraChatBubble(
                text = stringResource(R.string.conversation_demo_q1),
                author = VoraChatAuthor.Me,
                time = stringResource(R.string.conversation_demo_t1),
            )
            VoraChatBubble(
                text = stringResource(R.string.conversation_demo_a1),
                author = VoraChatAuthor.Vora,
                footer = {
                    VoraChatAudioFooter(stringResource(R.string.conversation_listen), {})
                },
            )
            VoraComposer(
                value = "",
                onValueChange = {},
                onSend = {},
                placeholder = stringResource(R.string.conversation_placeholder),
                onAttach = {},
                onMic = {},
            )
        }
    }
}
