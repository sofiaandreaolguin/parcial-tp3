package com.example.parcial_tp3.ui.screens.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Lo que dura cada uno de los tres pasos de Nutrition / 05: en total unos 2 s. */
private const val ANALYSIS_STEP_MS = 700L

/**
 * Controlador de Nutrition / 04 a 07. Se pide con el back stack entry del grafo de Nutrition para
 * que el borrador sobreviva de una pantalla a la siguiente. No graba ni pide micrófono (NAV05).
 *
 * El análisis de 05 es simulado: arranca con "Done Speaking" de 04 y no con la composición, así
 * que rotar no lo reinicia. Si 05 se cierra sin cancelarlo, el evento final quedaría en el canal
 * y mandaría a 07 en la próxima captura: quien saque a 05 de la pila tiene que llamar a
 * [onCancelAnalysis].
 *
 * El borrador de voz deja los macros en 0 hasta que 07 los dibuje. El de la foto ya los trae: se
 * carga con [onPhotoCaptureStarted] al entrar a 06, para que no quede el de una captura anterior.
 * Al revés no pasa: una captura de voz posterior a una foto conserva el borrador de la foto.
 */
@HiltViewModel
class FoodCaptureViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(FoodCaptureUiState())
    val uiState: StateFlow<FoodCaptureUiState> = _uiState.asStateFlow()

    private val _events = Channel<FoodCaptureEvent>(Channel.BUFFERED)
    val events: Flow<FoodCaptureEvent> = _events.receiveAsFlow()

    private var analysisJob: Job? = null

    fun onDoneSpeaking() {
        if (analysisJob?.isActive == true) return
        stopAnalysis()
        analysisJob = viewModelScope.launch {
            delay(ANALYSIS_STEP_MS)
            _uiState.update { it.copy(analysisStep = AnalysisStep.Estimating) }
            delay(ANALYSIS_STEP_MS)
            _uiState.update { it.copy(analysisStep = AnalysisStep.Calculating) }
            delay(ANALYSIS_STEP_MS)
            _events.send(FoodCaptureEvent.AnalysisDone)
        }
    }

    fun onCancelAnalysis() = stopAnalysis()

    fun onPhotoCaptureStarted() = _uiState.update { it.copy(draft = PhotoMealDraft) }

    // Cancelar el Job no saca un AnalysisDone que ya está en el canal sin colector: se vacía.
    private fun stopAnalysis() {
        analysisJob?.cancel()
        while (_events.tryReceive().isSuccess) Unit
        _uiState.update { it.copy(analysisStep = AnalysisStep.Identifying) }
    }
}
