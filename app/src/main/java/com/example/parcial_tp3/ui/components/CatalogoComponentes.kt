package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Catalogo de los componentes comunes, para compararlo con la lamina 04 "Elementos de
 * interfaz" del Figma. Es solo una preview: no es una ruta ni se ve en la app.
 *
 * Los textos son de relleno (claves que ya existen en strings.xml): lo que se compara es
 * la forma, el color y la medida, no el copy.
 */
@Composable
internal fun CatalogoComponentes() {
    val corto = stringResource(R.string.tab_workout)
    val largo = stringResource(R.string.pant_workout_03)
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
    ) {
        VoraTopBarLarge(title = stringResource(R.string.tab_profile), onBack = {})
        Column(
            Modifier.padding(horizontal = VoraTheme.spacing.screen),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
        ) {
            // Botones y acciones
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
            ) {
                VoraButton(corto, {}, size = VoraButtonSize.Small)
                VoraButton(corto, {}, style = VoraButtonStyle.Outlined, size = VoraButtonSize.Small)
                VoraButton(corto, {}, style = VoraButtonStyle.Text, size = VoraButtonSize.Small)
            }
            // Soft va sobre una tarjeta blanca, como "Edit profile" en Profile / 01
            VoraCard(Modifier.fillMaxWidth()) {
                VoraButton(corto, {}, style = VoraButtonStyle.Soft, size = VoraButtonSize.Small)
            }
            VoraButton(largo, {}, Modifier.fillMaxWidth())

            // Tarjetas y paneles
            VoraPanel(Modifier.fillMaxWidth()) {
                VoraTag(corto.uppercase(), tone = VoraTagTone.OnDark)
                Text(largo, style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                    VoraButton(corto, {}, style = VoraButtonStyle.Cta, size = VoraButtonSize.Small)
                    VoraButton(corto, {}, style = VoraButtonStyle.OutlinedOnDark, size = VoraButtonSize.Small)
                }
            }
            VoraCard(Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s12, radius = VoraTheme.radius.r12) {
                Text(corto, style = MaterialTheme.typography.bodyLarge)
                Text(largo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Campos
            VoraTextField(value = largo, onValueChange = {}, label = corto.uppercase())

            // Chips y etiquetas
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
            ) {
                VoraChip(corto, selected = true, onClick = {})
                VoraChip(corto, selected = false, onClick = {})
                VoraChip(corto, selected = true, onClick = {}, selectedStyle = VoraChipSelectedStyle.Dark)
                VoraChip(corto, selected = true, onClick = {}, selectedStyle = VoraChipSelectedStyle.Outline)
                VoraTag(corto.uppercase())
            }

            // Opciones y filas
            VoraOptionCard(corto, selected = true, onClick = {}, subtitle = largo)
            VoraOptionCard(corto, selected = false, onClick = {})
            VoraListRow(corto, {}, subtitle = largo, icon = painterResource(R.drawable.ic_tab_profile))

            // Mensajes y coaching
            VoraInfoMessage(largo, icon = painterResource(R.drawable.ic_info))
            VoraAdvice(corto.uppercase(), largo)

            CatalogoControles()
        }
    }
}


/** La parte del catalogo con los controles e indicadores: segmentado, estadisticas, anillo, barras, grafico, interruptor, selector y orbe. */
@Composable
internal fun CatalogoControles() {
    val corto = stringResource(R.string.tab_workout)
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16)) {
        val opciones = listOf(corto, corto, corto)
        VoraSegmentedControl(opciones, 0, {})
        VoraCard(Modifier.fillMaxWidth()) {
            VoraSegmentedControl(opciones + corto, 1, {}, style = VoraSegmentedStyle.Tabs)
        }
        VoraSegmentedControl(opciones, 1, {}, style = VoraSegmentedStyle.Boxes)
        VoraStatRow(List(3) { VoraStat(corto.uppercase(), corto) })
        VoraCard(Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                VoraStatTile(corto, corto, Modifier.weight(1f), trend = corto.take(2))
                VoraStatTile(corto, corto, Modifier.weight(1f))
            }
        }
        VoraCard(Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
            ) {
                VoraProgressRing(0.67f) { Text(corto.take(3), style = MaterialTheme.typography.labelMedium) }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
                    VoraProgressBar(0.74f)
                    VoraProgressBar(0.69f, color = MaterialTheme.colorScheme.secondary)
                    VoraProgressBar(0.74f, color = MaterialTheme.colorScheme.tertiary)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                VoraTag(corto.uppercase())
                VoraStatusDot(MaterialTheme.colorScheme.error)
                Text(corto, style = MaterialTheme.typography.bodyMedium)
            }
        }
        VoraCard(Modifier.fillMaxWidth()) {
            VoraBarChart(
                values = listOf(0.6f, 0.8f, 0.9f, 0.75f, 1f, 0.8f, 0.8f),
                labels = List(7) { corto.take(1) },
                description = corto,
                highlighted = setOf(2, 4),
                barWidth = VoraTheme.sizes.barWidthNarrow,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16)) {
            VoraToggle(checked = true, onCheckedChange = {})
            VoraToggle(checked = false, onCheckedChange = {})
            VoraStepper(corto, {}, {}, onDark = false)
        }
        VoraPanel(Modifier.fillMaxWidth()) {
            VoraStepper(corto, {}, {})
        }
        // Orbe: los dos tonos y los tamanos que usa el asistente
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16)) {
            VoraOrb()
            VoraOrb(size = VoraTheme.sizes.orbMuted, tone = VoraOrbTone.Muted)
            VoraOrb(size = VoraTheme.sizes.orbSmall)
            VoraOrb(size = VoraTheme.sizes.orbSmall, withRing = false)
        }
        VoraPanel(Modifier.fillMaxWidth()) {
            VoraOrb(onDark = true, withRipple = true)
        }

        // Botones redondos, en los tamanos de Voice
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
            VoraCircleButton(
                painterResource(R.drawable.ic_mic),
                corto,
                {},
                size = VoraTheme.sizes.micCircle,
                iconSize = VoraTheme.sizes.iconMic,
            )
            VoraCircleButton(
                painterResource(R.drawable.ic_pause),
                corto,
                {},
                tone = VoraCircleButtonTone.Mint,
                size = VoraTheme.sizes.pauseCircle,
            )
            VoraCircleButton(painterResource(R.drawable.ic_send), corto, {}, iconSize = VoraTheme.sizes.iconSmall)
            VoraCircleButton(painterResource(R.drawable.ic_plus), corto, {}, tone = VoraCircleButtonTone.Surface)
        }
        // El volver de Login: elevado, con accion (SignUp) y sin accion (SignIn)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
            VoraCircleButton(
                painterResource(R.drawable.ic_back),
                corto,
                {},
                tone = VoraCircleButtonTone.Elevated,
                size = VoraTheme.sizes.accessBack,
                iconSize = VoraTheme.sizes.accessBackIcon,
                touchSize = VoraTheme.sizes.accessBack,
            )
            VoraCircleButton(
                painterResource(R.drawable.ic_back),
                null,
                null,
                tone = VoraCircleButtonTone.Elevated,
                size = VoraTheme.sizes.accessBack,
                iconSize = VoraTheme.sizes.accessBackIcon,
                touchSize = VoraTheme.sizes.accessBack,
            )
        }

        // Etiqueta de seccion, aviso y conversacion
        VoraSectionLabel(stringResource(R.string.voice_suggested))
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
        VoraChatBubble(
            text = stringResource(R.string.conversation_demo_q1),
            author = VoraChatAuthor.Me,
            time = stringResource(R.string.conversation_demo_t1),
        )
        VoraChatBubble(
            text = stringResource(R.string.conversation_demo_a2),
            author = VoraChatAuthor.Vora,
            footer = { VoraChatAudioFooter(stringResource(R.string.conversation_listen), {}) },
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

@Preview(showBackground = true, widthDp = 394, heightDp = 2800)
@Composable
private fun CatalogoComponentesPreview() {
    Parcialtp3Theme { CatalogoComponentes() }
}
