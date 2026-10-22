package com.example.parcial_tp3.ui.screens.voice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import android.app.Activity
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraChip
import com.example.parcial_tp3.ui.components.VoraCircleButton
import com.example.parcial_tp3.ui.components.VoraCircleButtonTone
import com.example.parcial_tp3.ui.components.VoraNoticeCard
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraOrbTone
import com.example.parcial_tp3.ui.components.VoraProgressBar
import com.example.parcial_tp3.ui.components.VoraSectionLabel
import com.example.parcial_tp3.ui.components.VoraStatusDot
import com.example.parcial_tp3.ui.components.VoraTag
import com.example.parcial_tp3.ui.components.VoraTagTone
import com.example.parcial_tp3.ui.components.VoraTopBar
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/** Lo reproducido del audio de Speaking: 180 de los 342 de la barra, medido en el frame. */
private const val AVANCE_AUDIO = 0.53f

/**
 * Las cinco caras de la ruta `voice`. Solo dibuja: el estado y los eventos vienen de
 * [VoiceRoute].
 *
 * Listening, Processing y Speaking van sobre el verde y por eso pasan `onDark`; Idle y
 * Error van sobre el fondo claro. Ninguna de las cinco pide permiso de microfono (NAV05).
 */
@Composable
fun VoiceScreen(
    estado: VoiceUiState,
    onVolver: () -> Unit,
    onEscribir: () -> Unit,
    onHablar: () -> Unit,
    onSugerencia: (Int) -> Unit,
    onMicrofono: () -> Unit,
    onCancelar: () -> Unit,
    onInterrumpir: () -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val verde = estado.status == VoiceStatus.Listening ||
        estado.status == VoiceStatus.Processing ||
        estado.status == VoiceStatus.Speaking
    val contenido = if (verde) colors.onPrimary else colors.onBackground

    // Sobre el verde los iconos del sistema van claros. El resto de la app los deja
    // oscuros (PR #8), asi que se cambia solo mientras esta pantalla esta en verde y se
    // deja como estaba al salir.
    val vista = LocalView.current
    DisposableEffect(verde) {
        val ventana = (vista.context as Activity).window
        val control = WindowCompat.getInsetsController(ventana, vista)
        control.isAppearanceLightStatusBars = !verde
        control.isAppearanceLightNavigationBars = !verde
        onDispose {
            control.isAppearanceLightStatusBars = true
            control.isAppearanceLightNavigationBars = true
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .background(if (verde) colors.primary else colors.background),
    ) {
        VoraTopBar(
            modifier = Modifier.statusBarsPadding(),
            title = stringResource(
                when (estado.status) {
                    VoiceStatus.Idle -> R.string.voice_coach_title
                    VoiceStatus.Listening -> R.string.voice_listening_title
                    VoiceStatus.Processing -> R.string.voice_processing_title
                    VoiceStatus.Speaking -> R.string.voice_speaking_title
                    VoiceStatus.Error -> R.string.voice_error_title
                },
            ),
            onBack = onVolver,
            contentColor = contenido,
            backInCircle = true,
            titleAtStart = true,
            actions = {
                // el frame deja la etiqueta a 20 del borde; el encabezado no trae
                // margen lateral porque lo da el area tactil de los botones de icono
                val alBorde = Modifier.padding(end = VoraTheme.spacing.screen)
                when (estado.status) {
                    VoiceStatus.Idle -> VoraTag(stringResource(R.string.voice_online), alBorde)
                    // el frame no le pone pastilla: es un punto menta y el texto al lado
                    VoiceStatus.Listening -> Row(
                        alBorde,
                        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        VoraStatusDot(MaterialTheme.colorScheme.secondary)
                        Text(
                            text = stringResource(R.string.voice_live_audio),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                    VoiceStatus.Speaking -> VoraTag(
                        stringResource(R.string.voice_speaking_tag),
                        alBorde,
                        VoraTagTone.MintOnDark,
                    )
                    else -> Unit
                }
            },
        )
        // El cuerpo se desplaza si no entra (UI03) y el pie queda fijo abajo: en los seis
        // frames los controles son un bloque aparte, pegado al borde inferior.
        //
        // Cada frame deja su propio aire entre el encabezado y el orbe (20 en Idle, 40 en
        // Listening, 50 en Processing y 30 en Speaking y Error), asi que se toma el valor
        // mas cercano del tema, como pide la lamina de espaciados.
        // Medidos en los frames: el aire entre bloques del cuerpo y el del pie.
        val aire = when (estado.status) {
            VoiceStatus.Listening, VoiceStatus.Processing -> VoraTheme.spacing.s40
            else -> VoraTheme.spacing.s32
        }
        val airePie = when (estado.status) {
            VoiceStatus.Listening, VoiceStatus.Speaking -> VoraTheme.spacing.s20
            else -> VoraTheme.spacing.s16
        }
        val aireArriba = when (estado.status) {
            VoiceStatus.Idle -> VoraTheme.spacing.s20
            VoiceStatus.Listening, VoiceStatus.Processing -> VoraTheme.spacing.s40
            VoiceStatus.Speaking, VoiceStatus.Error -> VoraTheme.spacing.s32
        }
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = VoraTheme.spacing.s24)
                .padding(top = aireArriba),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(aire),
        ) {
            when (estado.status) {
                VoiceStatus.Idle -> Idle(estado, onSugerencia)
                VoiceStatus.Listening -> Listening(estado)
                VoiceStatus.Processing -> Processing(estado)
                VoiceStatus.Speaking -> Speaking(estado)
                VoiceStatus.Error -> Error(onReintentar)
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    start = VoraTheme.spacing.s24,
                    end = VoraTheme.spacing.s24,
                    top = VoraTheme.spacing.s16,
                    bottom = VoraTheme.spacing.s24,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(airePie),
        ) {
            when (estado.status) {
                VoiceStatus.Idle -> PieIdle(onHablar, onEscribir)
                VoiceStatus.Listening -> PieListening(onMicrofono, onCancelar)
                VoiceStatus.Processing -> PieCancelar(onCancelar)
                VoiceStatus.Speaking -> PieSpeaking(onInterrumpir)
                VoiceStatus.Error -> PieError(onReintentar, onEscribir)
            }
        }
    }
}

@Composable
private fun ColumnScope.Idle(estado: VoiceUiState, onSugerencia: (Int) -> Unit) {
    Orbe(VoraTheme.sizes.orb)
    Bloque(VoraTheme.spacing.s8) {
        Titulo(stringResource(R.string.voice_idle_title), VoraTheme.text.heroTitle)
        Bajada(stringResource(R.string.voice_idle_subtitle))
    }
    Bloque(VoraTheme.spacing.s12) {
    VoraSectionLabel(stringResource(R.string.voice_suggested))
    FlowRow(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
    ) {
        estado.suggestions.forEach { pregunta ->
            VoraChip(stringResource(pregunta), selected = false, onClick = { onSugerencia(pregunta) })
        }
    }
    }
    VoraCard(Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s14) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(R.string.voice_last_conversation),
                style = VoraTheme.text.label,
                color = VoraTheme.colors.textMuted,
            )
            Text(
                text = stringResource(R.string.voice_last_time),
                style = VoraTheme.text.labelPlain,
                color = VoraTheme.colors.textMuted,
            )
        }
        Text(
            text = stringResource(estado.lastQuestion),
            style = VoraTheme.text.quote,
            modifier = Modifier.padding(top = VoraTheme.spacing.s6),
        )
    }
}

@Composable
private fun ColumnScope.Listening(estado: VoiceUiState) {
    Orbe(VoraTheme.sizes.orbListening, onDark = true, withRipple = true)
    PanelOscuro(radio = VoraTheme.radius.r24) {
        Text(
            text = stringResource(R.string.voice_live_transcript),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = stringResource(estado.transcript),
            style = VoraTheme.text.panelBody,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
    Bajada(stringResource(R.string.voice_speak_naturally), VoraTheme.colors.onDarkMuted)
}

@Composable
private fun ColumnScope.Processing(estado: VoiceUiState) {
    Orbe(VoraTheme.sizes.orbProcessing, onDark = true)
    Bloque(VoraTheme.spacing.s16) {
    Text(
        text = stringResource(R.string.voice_thinking),
        style = VoraTheme.text.action,
        color = MaterialTheme.colorScheme.secondary,
    )
    PanelOscuro(radio = VoraTheme.radius.r16, relleno = VoraTheme.spacing.s20) {
        Text(
            text = stringResource(estado.transcript),
            style = VoraTheme.text.panelQuote,
            color = VoraTheme.colors.onDarkMuted,
        )
    }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(VoraTheme.sizes.icon),
            color = MaterialTheme.colorScheme.secondary,
            strokeWidth = VoraTheme.sizes.borderSelected,
        )
        Text(
            text = stringResource(R.string.voice_checking),
            style = MaterialTheme.typography.bodyMedium,
            color = VoraTheme.colors.onDarkMuted,
        )
    }
}

@Composable
private fun ColumnScope.Speaking(estado: VoiceUiState) {
    Orbe(VoraTheme.sizes.orb, onDark = true, withRipple = true)
    PanelOscuro {
        Text(
            text = stringResource(estado.reply),
            style = VoraTheme.text.panelBodySmall,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
    Bloque(VoraTheme.spacing.s16) {
    VoraProgressBar(
        progress = AVANCE_AUDIO,
        color = MaterialTheme.colorScheme.secondary,
        trackColor = VoraTheme.colors.onDarkSubtle,
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        VoraSectionLabel(stringResource(R.string.voice_elapsed), color = VoraTheme.colors.onDarkMuted)
        VoraSectionLabel(stringResource(R.string.voice_remaining), color = VoraTheme.colors.onDarkMuted)
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s24),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VoraCircleButton(
            icon = painterResource(R.drawable.ic_repeat),
            contentDescription = stringResource(R.string.voice_repeat),
            onClick = {},
            tone = VoraCircleButtonTone.OnDarkSubtle,
            iconSize = VoraTheme.sizes.iconSmall,
        )
        VoraCircleButton(
            icon = painterResource(R.drawable.ic_pause),
            contentDescription = stringResource(R.string.voice_pause),
            onClick = {},
            tone = VoraCircleButtonTone.Mint,
            size = VoraTheme.sizes.pauseCircle,
        )
        VoraCircleButton(
            icon = painterResource(R.drawable.ic_stop),
            contentDescription = stringResource(R.string.voice_stop),
            onClick = {},
            tone = VoraCircleButtonTone.OnDarkSubtle,
            iconSize = VoraTheme.sizes.iconSmall,
        )
    }
    }
}

@Composable
private fun ColumnScope.Error(onReintentar: () -> Unit) {
    Orbe(VoraTheme.sizes.orbMuted, tone = VoraOrbTone.Muted, icono = R.drawable.ic_refresh)
    Bloque(VoraTheme.spacing.s12) {
        Titulo(stringResource(R.string.voice_error_heading), VoraTheme.text.alertTitle)
        Bajada(stringResource(R.string.voice_error_body))
    }
    VoraNoticeCard(
        icon = painterResource(R.drawable.ic_mic),
        iconBackground = VoraTheme.colors.noticeIcon,
        title = stringResource(R.string.voice_mic_notice_title),
        body = stringResource(R.string.voice_mic_notice_body),
        primaryText = stringResource(R.string.voice_allow_access),
        // NAV05: vuelve a Idle, NO abre el dialogo de permisos del sistema
        onPrimary = onReintentar,
        modifier = Modifier.fillMaxWidth(),
        secondaryText = stringResource(R.string.voice_not_now),
        onSecondary = onReintentar,
    )
}

// Los pies: en los seis frames los controles son un bloque aparte, pegado abajo.

@Composable
private fun PieIdle(onHablar: () -> Unit, onEscribir: () -> Unit) {
    // el frame separa 8 el circulo de su rotulo, y 16 el rotulo de "Type instead"
    Bloque(VoraTheme.spacing.s8) {
        VoraCircleButton(
            icon = painterResource(R.drawable.ic_mic),
            contentDescription = stringResource(R.string.voice_tap_to_talk),
            onClick = onHablar,
            size = VoraTheme.sizes.micCircle,
            iconSize = VoraTheme.sizes.iconMic,
        )
        Text(
            text = stringResource(R.string.voice_tap_to_talk),
            style = VoraTheme.text.action,
            color = MaterialTheme.colorScheme.primary,
        )
    }
    Enlace(stringResource(R.string.voice_type_instead), onEscribir)
}

@Composable
private fun PieListening(onMicrofono: () -> Unit, onCancelar: () -> Unit) {
    VoraCircleButton(
        icon = painterResource(R.drawable.ic_mic),
        contentDescription = stringResource(R.string.voice_listening_title),
        onClick = onMicrofono,
        tone = VoraCircleButtonTone.Mint,
        size = VoraTheme.sizes.micCircleActive,
        iconSize = VoraTheme.sizes.iconMic,
    )
    Enlace(stringResource(R.string.voice_cancel), onCancelar, VoraTheme.colors.onDarkMuted)
}

@Composable
private fun PieCancelar(onCancelar: () -> Unit) {
    Enlace(stringResource(R.string.voice_cancel), onCancelar, VoraTheme.colors.onDarkMuted)
}

@Composable
private fun PieSpeaking(onInterrumpir: () -> Unit) {
    VoraButton(
        text = stringResource(R.string.voice_tap_to_interrupt),
        onClick = onInterrumpir,
        modifier = Modifier.fillMaxWidth(),
        style = VoraButtonStyle.Cta,
    )
}

@Composable
private fun PieError(onReintentar: () -> Unit, onEscribir: () -> Unit) {
    VoraButton(
        text = stringResource(R.string.voice_try_again),
        onClick = onReintentar,
        modifier = Modifier.fillMaxWidth(),
        style = VoraButtonStyle.Mint,
    )
    Enlace(stringResource(R.string.voice_type_instead), onEscribir)
}

@Composable
private fun Orbe(
    size: Dp,
    onDark: Boolean = false,
    withRipple: Boolean = false,
    tone: VoraOrbTone = VoraOrbTone.Brand,
    icono: Int = R.drawable.ic_waveform,
) {
    VoraOrb(size = size, onDark = onDark, tone = tone, withRipple = withRipple) {
        Icon(
            painter = painterResource(icono),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(
                if (icono == R.drawable.ic_refresh) VoraTheme.sizes.iconOrbMuted else VoraTheme.sizes.iconOrb,
            ),
        )
    }
}

@Composable
private fun Titulo(texto: String, estilo: TextStyle) {
    Text(text = texto, style = estilo, textAlign = TextAlign.Center)
}

@Composable
private fun Bajada(texto: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Text(
        text = texto,
        style = VoraTheme.text.heroSubtitle,
        color = color,
        textAlign = TextAlign.Center,
    )
}

/** Agrupa lo que en el frame va mas junto que el aire general de la pantalla. */
@Composable
private fun Bloque(aire: Dp, contenido: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(aire),
        content = contenido,
    )
}

/**
 * El panel translucido de Listening, Processing y Speaking, sobre el verde: blanco al 5 %
 * con borde al 10 %. Cada frame usa su radio y su relleno, por eso van por parametro.
 */
@Composable
private fun PanelOscuro(
    radio: Dp = VoraTheme.radius.r20,
    relleno: Dp = VoraTheme.spacing.s20,
    separacion: Dp = VoraTheme.spacing.s12,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    val forma = RoundedCornerShape(radio)
    Column(
        Modifier
            .fillMaxWidth()
            .background(VoraTheme.colors.onDarkPanel, forma)
            .border(VoraTheme.sizes.border, VoraTheme.colors.onDarkPanelBorder, forma)
            .padding(relleno),
        verticalArrangement = Arrangement.spacedBy(separacion),
        content = contenido,
    )
}

/**
 * "Type instead" y "Cancel": texto suelto que navega. Si vuelve a aparecer fuera de Voice,
 * conviene que sea un estilo mas de VoraButton, zona de Tema y componentes.
 */
@Composable
private fun Enlace(texto: String, onClick: () -> Unit, color: Color = VoraTheme.colors.textMuted) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(VoraTheme.spacing.s8),
    )
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun VoiceIdlePreview() {
    Parcialtp3Theme {
        VoiceScreen(VoiceUiState(), {}, {}, {}, {}, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun VoiceErrorPreview() {
    Parcialtp3Theme {
        VoiceScreen(VoiceUiState(status = VoiceStatus.Error), {}, {}, {}, {}, {}, {}, {}, {})
    }
}
