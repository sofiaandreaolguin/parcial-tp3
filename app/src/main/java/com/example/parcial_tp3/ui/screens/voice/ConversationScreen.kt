package com.example.parcial_tp3.ui.screens.voice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.CoachSuggestion
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraChatAudioFooter
import com.example.parcial_tp3.ui.components.VoraChatBubble
import com.example.parcial_tp3.ui.components.VoraChip
import com.example.parcial_tp3.ui.components.VoraComposer
import com.example.parcial_tp3.ui.components.VoraInfoMessage
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraSectionLabel
import com.example.parcial_tp3.ui.components.VoraStatusDot
import com.example.parcial_tp3.ui.components.VoraTag
import com.example.parcial_tp3.ui.components.VoraTagTone
import com.example.parcial_tp3.ui.components.VoraTopBar
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * El hilo escrito de Voice / 05. Solo dibuja.
 *
 * Debajo del hilo, sobre la barra de escribir, se apilan tres cosas que el frame no dibuja
 * porque no las conocia: el error del envio con reintento (C17 a C20), los chips de las
 * preguntas sugeridas (C5) y el aviso de que no vino ninguna (C7).
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
    onSugerencia: (CoachSuggestion) -> Unit,
    onReintentar: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borde = MaterialTheme.colorScheme.outline
    val grosor = with(LocalDensity.current) { VoraTheme.sizes.border.toPx() }
    val lista = rememberLazyListState()
    var yaSeAbrio by remember { mutableStateOf(false) }
    /*
     * Las dos cosas van por separado a proposito. Con una sola clave calculada
     * (`mensajes + 1 si envia`) el efecto se pierde el momento mas importante: cuando llega
     * la respuesta, el hilo crece en uno y "enviando" se apaga al mismo tiempo, los dos
     * cambios se compensan, la clave queda igual y la lista no baja a mostrarla.
     */
    LaunchedEffect(estado.messages.size, estado.enviando) {
        // el encabezado del dia ocupa el indice 0, asi que el ultimo mensaje cae en su mismo
        // numero; mientras se envia, el aviso queda una posicion mas abajo
        val ultimo = estado.messages.size + if (estado.enviando) 1 else 0
        if (ultimo == 0) return@LaunchedEffect
        if (yaSeAbrio) {
            lista.animateScrollToItem(ultimo)
        } else {
            // al abrir con un hilo largo ya guardado, animar el viaje desde arriba se ve
            // como un tiron: la primera vez se baja de una
            lista.scrollToItem(ultimo)
            yaSeAbrio = true
        }
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
            // aire abajo: sin esto la ultima burbuja queda pegada a la linea de la barra
            contentPadding = PaddingValues(bottom = VoraTheme.spacing.s20),
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
                    // cada burbuja entra sola en lugar de aparecer de golpe
                    modifier = Modifier.animateItem(),
                    time = mensaje.timeRes?.let { stringResource(it) },
                    footer = pieDe(mensaje),
                )
            }
            // "Enviando..." va al final del hilo, como en cualquier chat, y no arriba de la
            // barra de escribir: ahi crecia y encogia el bloque de abajo y el hilo entero
            // pegaba un salto cada vez (C25)
            if (estado.enviando) {
                // sin animateItem(): la respuesta ocupa este mismo lugar, y el desvanecido
                // de la fila se cruzaba por debajo de la burbuja nueva
                item(key = CLAVE_ENVIANDO) {
                    TextoAlPie(stringResource(R.string.conversation_sending))
                }
            }
        }
        Column(
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
                }
                .padding(VoraTheme.spacing.s16),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
        ) {
            val error = estado.error
            if (error != null) {
                ConversationStatusMessage(
                    text = stringResource(error),
                    onRetry = onReintentar.takeIf { estado.puedeReintentar },
                )
            }
            if (estado.sugerencias.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
                    verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
                ) {
                    // el texto llega del servidor, no de strings.xml: es dato, como el
                    // nombre de un entrenamiento
                    estado.sugerencias.forEach { sugerencia ->
                        VoraChip(
                            text = sugerencia.text,
                            selected = false,
                            onClick = { onSugerencia(sugerencia) },
                        )
                    }
                }
            } else if (estado.sinSugerencias) {
                TextoAlPie(stringResource(R.string.conversation_suggestions_empty))
            }
            VoraComposer(
                value = borrador,
                onValueChange = onBorradorChange,
                // C25: mientras espera la respuesta no se manda otro. El ViewModel tambien
                // lo corta (C26); aca se corta el toque para que no quede en la cola.
                onSend = { if (!estado.enviando) onEnviar() },
                placeholder = stringResource(R.string.conversation_placeholder),
                onAttach = {},
                // NAV05: no pide permiso, no graba
                onMic = {},
            )
        }
    }
}

/**
 * El pie de la burbuja: el audio del frame si lo trae, y si no la marca de respuesta
 * simulada (C11). Nunca los dos: en el frame la burbuja con audio no lleva etiqueta.
 */
@Composable
private fun pieDe(mensaje: ChatMessage): (@Composable () -> Unit)? {
    val audio = mensaje.audioRes
    return when {
        audio != null -> {
            { VoraChatAudioFooter(stringResource(audio), onPlay = {}) }
        }
        mensaje.isSimulated -> {
            { VoraTag(stringResource(R.string.conversation_simulated), tone = VoraTagTone.Green) }
        }
        else -> null
    }
}

/**
 * El error del envio con reintento (C17 a C20). Mismo armado que `WorkoutStatusMessage`.
 *
 * @param onRetry null cuando no hay nada que reintentar, como el texto demasiado largo
 *   (C13): ahi va el aviso solo, sin un boton que no haria nada.
 */
@Composable
private fun ConversationStatusMessage(
    text: String,
    onRetry: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
        VoraInfoMessage(text = text, icon = painterResource(R.drawable.ic_info))
        if (onRetry != null) {
            VoraButton(
                text = stringResource(R.string.conversation_retry),
                onClick = onRetry,
                style = VoraButtonStyle.Outlined,
                size = VoraButtonSize.Small,
            )
        }
    }
}

/** Una linea gris: "Enviando..." al final del hilo, o "no hay sugerencias" sobre la barra. */
@Composable
private fun TextoAlPie(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = VoraTheme.colors.textMuted,
        modifier = modifier,
    )
}

/** La fila de "Enviando..." entra y sale del hilo, asi que necesita una clave estable. */
private const val CLAVE_ENVIANDO = "enviando"

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ConversationPreview() {
    Parcialtp3Theme {
        ConversationScreen(
            estado = ConversationUiState(
                messages = HILO_DEMO,
                sugerencias = listOf(
                    CoachSuggestion("s01", "¿Cómo empiezo mi entrenamiento?"),
                    CoachSuggestion("s02", "¿Qué puedo cenar?"),
                ),
            ),
            borrador = "",
            onBorradorChange = {},
            onEnviar = {},
            onSugerencia = {},
            onReintentar = {},
            onVolver = {},
        )
    }
}
