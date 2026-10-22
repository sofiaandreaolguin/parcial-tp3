package com.example.parcial_tp3.ui.screens.voice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraChatAudioFooter
import com.example.parcial_tp3.ui.components.VoraChatBubble
import com.example.parcial_tp3.ui.components.VoraComposer
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraSectionLabel
import com.example.parcial_tp3.ui.components.VoraStatusDot
import com.example.parcial_tp3.ui.components.VoraTopBar
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * El hilo escrito de Voice / 05. Solo dibuja.
 *
 * **NAV05:** el microfono de la barra de escribir no pide permiso; esta por fidelidad al
 * frame y no hace nada.
 */
@Composable
fun ConversationScreen(
    estado: ConversationUiState,
    borrador: String,
    onBorradorChange: (String) -> Unit,
    onEnviar: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borde = MaterialTheme.colorScheme.outline
    val grosor = with(LocalDensity.current) { VoraTheme.sizes.border.toPx() }
    val lista = rememberLazyListState()
    LaunchedEffect(estado.messages.size) {
        if (estado.messages.isNotEmpty()) lista.animateScrollToItem(estado.messages.size)
    }
    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        VoraTopBar(
            modifier = Modifier
                // el orden importa: primero el margen de la barra de estado y recien
                // despues el fondo, si no el blanco se mete detras de la hora. En el
                // frame la franja de arriba es el fondo de pantalla y el encabezado
                // blanco son 56 debajo.
                .statusBarsPadding()
                .height(VoraTheme.sizes.topBar)
                .background(MaterialTheme.colorScheme.surface)
                .drawBehind {
                    drawLine(
                        color = borde,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = grosor,
                    )
                },
            title = stringResource(R.string.voice_coach_title),
            onBack = onVolver,
            backInCircle = true,
            titleAtStart = true,
            titleBadge = { VoraStatusDot(MaterialTheme.colorScheme.secondary) },
            // el frame lo deja a 20 del borde; el encabezado no trae margen lateral
            actions = {
                VoraOrb(
                    size = VoraTheme.sizes.orbSmall,
                    modifier = Modifier.padding(end = VoraTheme.spacing.screen),
                )
            },
        )
        LazyColumn(
            Modifier
                .weight(1f)
                .padding(horizontal = VoraTheme.spacing.screen),
            state = lista,
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s20),
        ) {
            item {
                VoraSectionLabel(
                    stringResource(R.string.conversation_day),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = VoraTheme.spacing.s20),
                    color = VoraTheme.colors.textMuted,
                    textAlign = TextAlign.Center,
                )
            }
            items(estado.messages, key = { it.id }) { mensaje ->
                VoraChatBubble(
                    text = mensaje.textRes?.let { stringResource(it) } ?: mensaje.text,
                    author = mensaje.author,
                    time = mensaje.timeRes?.let { stringResource(it) },
                    footer = mensaje.audioRes?.let { audio ->
                        { VoraChatAudioFooter(stringResource(audio), onPlay = {}) }
                    },
                )
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding()
                .drawBehind {
                    drawLine(
                        color = borde,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = grosor,
                    )
                },
        ) {
            VoraComposer(
                value = borrador,
                onValueChange = onBorradorChange,
                onSend = onEnviar,
                placeholder = stringResource(R.string.conversation_placeholder),
                modifier = Modifier.padding(VoraTheme.spacing.s16),
                onAttach = {},
                // NAV05: no pide permiso, no graba
                onMic = {},
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ConversationPreview() {
    Parcialtp3Theme {
        ConversationScreen(ConversationUiState(), "", {}, {}, {})
    }
}
