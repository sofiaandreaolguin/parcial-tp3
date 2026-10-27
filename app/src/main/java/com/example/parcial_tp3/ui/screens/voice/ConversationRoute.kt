package com.example.parcial_tp3.ui.screens.voice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial_tp3.domain.model.CoachMessage
import com.example.parcial_tp3.domain.model.CoachRole
import com.example.parcial_tp3.ui.components.VoraChatAuthor

/**
 * La parte con estado de la ruta `conversation`.
 *
 * Lo que se esta tipeando no va al ViewModel: es estado pasajero de la pantalla y va con
 * `rememberSaveable`, como pide specs/CONTEXTO.md (seccion 3). Asi tampoco se pierde al
 * rotar.
 *
 * Aca se resuelve el hilo del frame antes de dárselo al ViewModel, porque sus textos estan
 * en strings.xml y el ViewModel no tiene Context.
 */
@Composable
fun ConversationRoute(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConversationViewModel = hiltViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var borrador by rememberSaveable { mutableStateOf("") }
    val demo = hiloDelFrame()
    LaunchedEffect(Unit) { viewModel.empezar(demo) }

    ConversationScreen(
        estado = estado.conElDibujoDelFrame(),
        borrador = borrador,
        onBorradorChange = { borrador = it },
        onEnviar = {
            viewModel.onEnviar(borrador)
            borrador = ""
        },
        onSugerencia = viewModel::onSugerencia,
        onReintentar = viewModel::onReintentar,
        onVolver = onVolver,
        modifier = modifier,
    )
}

/**
 * Lo unico que el Route aporta a la siembra: resolver los cuatro textos. Armar los mensajes es
 * [hiloSembrado], que es una funcion comun y tiene su test (C1).
 */
@Composable
private fun hiloDelFrame(): List<CoachMessage> =
    hiloSembrado(HILO_DEMO.map { mensaje -> mensaje.textRes?.let { stringResource(it) }.orEmpty() })

/**
 * Le devuelve a las burbujas sembradas lo que el frame `1:1512` dibuja y Room no guarda.
 *
 * - **El texto como clave y no como texto guardado.** La siembra resuelve los `@StringRes`
 *   con el idioma de ese momento y los guarda en Room; si la burbuja dibujara eso, cambiar
 *   el idioma del telefono dejaria estas cuatro en el idioma viejo y el resto de la pantalla
 *   en el nuevo. Devolviendo el `textRes`, el hilo del frame vuelve a seguir al sistema.
 * - **La hora y el pie "Listen to response (0:14)",** que son parte del dibujo y no del
 *   mensaje, asi que la Entity no los tiene.
 * - **Sin la marca de simulada.** Las cuatro se siembran con `isSimulated = true` (C1), pero
 *   el frame no las muestra marcadas y son la evidencia de UI01
 *   (`docs/evidencia/visual/voice-05-conversation-411dp.png`): la etiqueta de C11 es para las
 *   respuestas que trae el servidor, de la quinta burbuja en adelante. Que el hilo de ejemplo
 *   es simulado lo declara la tabla de funciones simuladas del README.
 *
 * Son siempre las primeras del hilo porque la siembra corre con la tabla vacia.
 */
private fun ConversationUiState.conElDibujoDelFrame(): ConversationUiState =
    copy(
        messages = messages.mapIndexed { posicion, mensaje ->
            val delFrame = HILO_DEMO.getOrNull(posicion) ?: return@mapIndexed mensaje
            mensaje.copy(
                textRes = delFrame.textRes,
                timeRes = delFrame.timeRes,
                audioRes = delFrame.audioRes,
                isSimulated = false,
            )
        },
    )
