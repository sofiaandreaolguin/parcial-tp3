package com.example.parcial_tp3.ui.screens.voice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * La parte con estado de la ruta `conversation`.
 *
 * Lo que se esta tipeando no va al ViewModel: es estado pasajero de la pantalla y va con
 * `rememberSaveable`, como pide specs/CONTEXTO.md (seccion 3). Asi tampoco se pierde al
 * rotar.
 */
@Composable
fun ConversationRoute(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConversationViewModel = hiltViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var borrador by rememberSaveable { mutableStateOf("") }
    ConversationScreen(
        estado = estado,
        borrador = borrador,
        onBorradorChange = { borrador = it },
        onEnviar = {
            viewModel.onEnviar(borrador)
            borrador = ""
        },
        onVolver = onVolver,
        modifier = modifier,
    )
}
