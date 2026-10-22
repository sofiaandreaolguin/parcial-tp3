package com.example.parcial_tp3.ui.screens.voice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * La parte con estado de la ruta `voice`: pide el ViewModel, lee el estado y pasa los
 * eventos. Dibujar es cosa de [VoiceScreen].
 */
@Composable
fun VoiceRoute(
    onVolver: () -> Unit,
    onEscribir: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VoiceViewModel = hiltViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    VoiceScreen(
        estado = estado,
        onVolver = onVolver,
        onEscribir = onEscribir,
        onHablar = viewModel::onHablar,
        onSugerencia = viewModel::onSugerencia,
        onMicrofono = viewModel::onMicrofono,
        onCancelar = viewModel::onCancelar,
        onInterrumpir = viewModel::onInterrumpir,
        onReintentar = viewModel::onReintentar,
        modifier = modifier,
    )
}
