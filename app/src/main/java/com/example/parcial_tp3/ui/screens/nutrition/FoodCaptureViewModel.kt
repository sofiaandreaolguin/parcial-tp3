package com.example.parcial_tp3.ui.screens.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.MealDataException
import com.example.parcial_tp3.data.repository.MealRepository
import com.example.parcial_tp3.domain.MealField
import com.example.parcial_tp3.domain.MealForm
import com.example.parcial_tp3.domain.MealValidation
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.validateMeal
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
 * El borrador arranca en null (carga manual) y vuelve a null con [onManualEntry] y al guardar. El de
 * la foto se carga con [onPhotoCaptureStarted] al entrar a 06; el de la voz, al terminar el análisis,
 * y es el mismo (decisión de la spec carga-y-eliminacion-de-comidas).
 *
 * 07 vive acá (columna Controlador de specs/PANTALLAS.md): el formulario, la validación y el
 * guardado, con el evento [FoodCaptureEvent.Saved] de una sola vez.
 */
@HiltViewModel
class FoodCaptureViewModel @Inject constructor(
    private val repo: MealRepository,
) : ViewModel() {

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
            // La voz llega a 07 con la misma comida estimada que la foto.
            _uiState.update { it.copy(draft = PhotoMealDraft) }
            _events.send(FoodCaptureEvent.AnalysisDone)
        }
    }

    fun onCancelAnalysis() = stopAnalysis()

    fun onPhotoCaptureStarted() = _uiState.update { it.copy(draft = PhotoMealDraft) }

    fun onManualEntry() = _uiState.update { it.copy(draft = null) }

    /**
     * Abre 07. La llama el grafo antes de navegar, no la composición de 07, así que rotar no la
     * repite. Con el mismo [mealId] y 07 ya abierta no hace nada: si 07 se dejara sin [onDiscard],
     * la próxima apertura con ese id mostraría lo escrito antes. [draftName] lo resuelve el grafo
     * desde strings.xml: el ViewModel no tiene Context.
     */
    fun onOpenConfirmation(mealId: Long, defaultDate: LocalDate, draftName: String) {
        if (_uiState.value.confirmation?.mealId == mealId) return
        if (mealId == NEW_MEAL_ID) {
            val form = _uiState.value.draft?.toForm(draftName, defaultDate) ?: MealForm(date = defaultDate)
            _uiState.update { it.copy(confirmation = ConfirmationState(mealId, form)) }
            return
        }
        _uiState.update { it.copy(confirmation = ConfirmationState(mealId, MealForm(date = defaultDate))) }
        viewModelScope.launch {
            val meal = try {
                repo.observeMeal(mealId).first()
            } catch (e: MealDataException) {
                updateConfirmation(mealId) { it.copy(saveError = R.string.nutrition_error_load) }
                return@launch
            }
            updateConfirmation(mealId) { if (meal == null) it.copy(notFound = true) else it.copy(form = meal.toForm()) }
        }
    }

    /** Borra el error de los campos que cambiaron; los demás quedan hasta el próximo guardado. */
    fun onFormChange(form: MealForm) = updateConfirmation { c ->
        val changed = changedFields(c.form, form)
        c.copy(form = form, errors = c.errors - changed, saveError = null)
    }

    fun onSave() {
        val c = _uiState.value.confirmation ?: return
        if (c.isSaving) return
        val id = if (c.mealId == NEW_MEAL_ID) 0 else c.mealId
        when (val result = validateMeal(c.form, id)) {
            is MealValidation.Invalid -> updateConfirmation { it.copy(errors = result.errors) }
            is MealValidation.Valid -> save(c.mealId, result.meal)
        }
    }

    fun onDiscard() = _uiState.update { it.copy(confirmation = null) }

    private fun save(mealId: Long, meal: Meal) {
        updateConfirmation { it.copy(isSaving = true, errors = emptyMap(), saveError = null) }
        viewModelScope.launch {
            try {
                if (mealId == NEW_MEAL_ID) repo.addMeal(meal) else repo.updateMeal(meal)
            } catch (e: MealDataException) {
                updateConfirmation { it.copy(isSaving = false, saveError = R.string.nutrition_error_save) }
                return@launch
            }
            // El borrador se reinicia: la próxima captura arranca de cero.
            _uiState.update { it.copy(draft = null, confirmation = null) }
            _events.send(FoodCaptureEvent.Saved)
        }
    }

    private fun updateConfirmation(mealId: Long? = null, change: (ConfirmationState) -> ConfirmationState) =
        _uiState.update { state ->
            val c = state.confirmation
            if (c == null || (mealId != null && c.mealId != mealId)) state else state.copy(confirmation = change(c))
        }

    // Cancelar el Job no saca un AnalysisDone que ya está en el canal sin colector: se vacía.
    private fun stopAnalysis() {
        analysisJob?.cancel()
        while (_events.tryReceive().isSuccess) Unit
        _uiState.update { it.copy(analysisStep = AnalysisStep.Identifying) }
    }
}

private fun MealDraft.toForm(name: String, date: LocalDate) = MealForm(
    name = name,
    type = type,
    date = date,
    kcal = kcal.toString(),
    protein = proteinG.toFormText(),
    carbs = carbsG.toFormText(),
    fat = fatG.toFormText(),
)

private fun Meal.toForm() = MealForm(
    name = name,
    type = type,
    date = date,
    kcal = kcal.toString(),
    protein = proteinG.toFormText(),
    carbs = carbsG.toFormText(),
    fat = fatG.toFormText(),
)

// 38.0 se muestra "38", como se tipearía.
private fun Double.toFormText(): String = if (this % 1.0 == 0.0) toLong().toString() else toString()

private fun changedFields(old: MealForm, new: MealForm): Set<MealField> = buildSet {
    if (old.name != new.name) add(MealField.NAME)
    if (old.type != new.type) add(MealField.TYPE)
    if (old.kcal != new.kcal) add(MealField.KCAL)
    if (old.protein != new.protein) add(MealField.PROTEIN)
    if (old.carbs != new.carbs) add(MealField.CARBS)
    if (old.fat != new.fat) add(MealField.FAT)
}
