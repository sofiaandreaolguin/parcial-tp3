package com.example.parcial_tp3.ui.screens.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.MealDataException
import com.example.parcial_tp3.data.repository.MealRepository
import com.example.parcial_tp3.domain.IngredientField
import com.example.parcial_tp3.domain.IngredientForm
import com.example.parcial_tp3.domain.IngredientValidation
import com.example.parcial_tp3.domain.MealField
import com.example.parcial_tp3.domain.MealForm
import com.example.parcial_tp3.domain.MealValidation
import com.example.parcial_tp3.domain.isValidIngredient
import com.example.parcial_tp3.domain.model.Ingredient
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.validateIngredient
import com.example.parcial_tp3.domain.validateMeal
import com.example.parcial_tp3.domain.validateMealDetails
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
 * El borrador arranca en null (carga manual) y vuelve a null con [onOpenManualEntry], [onPrefill] y al guardar. El de
 * la foto se carga con [onPhotoCaptureStarted] al entrar a 06; el de la voz, al terminar el análisis,
 * y es el mismo (decisión de la spec carga-y-eliminacion-de-comidas).
 *
 * 07 vive acá (columna Controlador de specs/PANTALLAS.md), con sus dos pop-ups: el de carga
 * (nombre, tipo y macros) y el de ingrediente. Los pop-ups están en el estado y no en un remember,
 * así rotar no pierde lo escrito. El guardado emite [FoodCaptureEvent.Saved] una sola vez.
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

    /** "Log Manually" y "Type instead": abre el pop-up de carga vacío, sin el borrador de una captura (C29). */
    fun onOpenManualEntry(defaultDate: LocalDate) = _uiState.update {
        it.copy(draft = null, manualEntry = ManualEntryState(MealForm(date = defaultDate)))
    }

    /** La búsqueda de alimentos (EXT02) abre el pop-up con la comida y el alimento como ingrediente (C82). */
    fun onPrefill(selection: FoodSelection) = _uiState.update {
        it.copy(draft = null, manualEntry = ManualEntryState(selection.form, ingredients = listOf(selection.ingredient)))
    }

    /** Borra el error de los campos que cambiaron; los demás quedan hasta el próximo "Continue" (C35). */
    fun onManualEntryChange(form: MealForm) = _uiState.update { state ->
        val m = state.manualEntry ?: return@update state
        state.copy(manualEntry = m.copy(form = form, errors = m.errors - changedFields(m.form, form)))
    }

    /**
     * "Continue" del pop-up de carga. Devuelve true solo si abrió 07: la Route navega con eso. Con el
     * pop-up ya cerrado (doble toque) o mientras se guarda otra comida (C102) no hace nada.
     */
    fun onContinueManualEntry(): Boolean {
        val state = _uiState.value
        val m = state.manualEntry ?: return false
        if (state.confirmation?.isSaving == true) return false
        val errors = validateMealDetails(m.form)
        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(manualEntry = m.copy(errors = errors)) }
            return false
        }
        _uiState.update { it.copy(manualEntry = null, confirmation = ConfirmationState(m.form, ingredients = m.ingredients)) }
        return true
    }

    fun onDismissManualEntry() = _uiState.update { it.copy(manualEntry = null) }

    /**
     * Abre 07 después de una captura de voz o foto. La llama quien navega, no la composición de 07,
     * así que rotar no la repite. Reemplaza la confirmación anterior (C31): 07 tiene barra y salir
     * por ella no llama a [onDiscard]. Mientras se guarda no la toca (C102). [draftName] y los
     * nombres de [draftIngredients] vienen resueltos de strings.xml: el ViewModel no tiene Context.
     * Sin borrador se ignoran los [draftIngredients] (C63).
     */
    fun onOpenConfirmation(
        defaultDate: LocalDate,
        draftName: String,
        draftIngredients: List<Ingredient> = emptyList(),
    ) {
        val state = _uiState.value
        if (state.confirmation?.isSaving == true) return
        val draft = state.draft
        val confirmation = if (draft == null) {
            ConfirmationState(MealForm(date = defaultDate))
        } else {
            ConfirmationState(draft.toForm(draftName, defaultDate), ingredients = draftIngredients)
        }
        _uiState.update { it.copy(confirmation = confirmation) }
    }

    fun onAddIngredientClick() = updateEditable { it.copy(ingredientEntry = IngredientEntryState()) }

    /** Borra el error de los campos que cambiaron (C86). */
    fun onIngredientEntryChange(form: IngredientForm) = updateEditable { c ->
        val e = c.ingredientEntry ?: return@updateEditable c
        c.copy(ingredientEntry = e.copy(form = form, errors = e.errors - changedFields(e.form, form)))
    }

    /** Agrega el ingrediente al final (C85) y borra el error de guardado (C101). */
    fun onConfirmIngredient() = updateEditable { c ->
        val e = c.ingredientEntry ?: return@updateEditable c
        when (val result = validateIngredient(e.form)) {
            is IngredientValidation.Invalid -> c.copy(ingredientEntry = e.copy(errors = result.errors))
            is IngredientValidation.Valid ->
                c.copy(ingredients = c.ingredients + result.ingredient, ingredientEntry = null, saveError = null)
        }
    }

    fun onDismissIngredientEntry() = updateConfirmation { it.copy(ingredientEntry = null) }

    /** "Remove" de cada tarjeta. Fuera de la lista no hace nada (C90). */
    fun onRemoveIngredient(index: Int) = updateEditable { c ->
        if (index !in c.ingredients.indices) c
        else c.copy(ingredients = c.ingredients.filterIndexed { i, _ -> i != index }, saveError = null)
    }

    /**
     * Se corta en el primer control que falla: sin ingredientes (C88), [validateMeal] (C34) e
     * [isValidIngredient] (C62). Valida una copia con las kcal sumadas y no cambia lo que se ve (C87, C39).
     */
    fun onSave() {
        val c = _uiState.value.confirmation ?: return
        if (c.isSaving) return
        if (c.ingredients.isEmpty()) {
            updateConfirmation { it.copy(saveError = R.string.nutrition_error_no_ingredients) }
            return
        }
        when (val result = validateMeal(c.form.copy(kcal = c.totalKcal.toString()))) {
            is MealValidation.Invalid -> updateConfirmation { it.copy(saveError = R.string.nutrition_error_save) }
            is MealValidation.Valid ->
                if (c.ingredients.all(::isValidIngredient)) {
                    save(result.meal.copy(ingredients = c.ingredients))
                } else {
                    updateConfirmation { it.copy(saveError = R.string.nutrition_error_ingredients) }
                }
        }
    }

    fun onDiscard() = _uiState.update { it.copy(confirmation = null) }

    private fun save(meal: Meal) {
        updateConfirmation { it.copy(isSaving = true, saveError = null) }
        viewModelScope.launch {
            try {
                repo.addMeal(meal)
            } catch (e: MealDataException) {
                updateConfirmation { it.copy(isSaving = false, saveError = R.string.nutrition_error_save) }
                return@launch
            }
            // El borrador se reinicia: la próxima captura arranca de cero.
            _uiState.update { it.copy(draft = null, confirmation = null) }
            _events.send(FoodCaptureEvent.Saved)
        }
    }

    private fun updateConfirmation(change: (ConfirmationState) -> ConfirmationState) =
        _uiState.update { state -> state.confirmation?.let { state.copy(confirmation = change(it)) } ?: state }

    /** Mientras se guarda, los ingredientes no cambian: lo guardado sería distinto de lo que se ve (C91). */
    private fun updateEditable(change: (ConfirmationState) -> ConfirmationState) =
        updateConfirmation { if (it.isSaving) it else change(it) }

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

// 38.0 se muestra "38", como se tipearía.
// internal porque la busqueda de alimentos (EXT02) precarga los macros por el mismo camino.
internal fun Double.toFormText(): String = if (this % 1.0 == 0.0) toLong().toString() else toString()

private fun changedFields(old: IngredientForm, new: IngredientForm): Set<IngredientField> = buildSet {
    if (old.name != new.name) add(IngredientField.NAME)
    if (old.grams != new.grams) add(IngredientField.GRAMS)
    if (old.kcal != new.kcal) add(IngredientField.KCAL)
}

private fun changedFields(old: MealForm, new: MealForm): Set<MealField> = buildSet {
    if (old.name != new.name) add(MealField.NAME)
    if (old.type != new.type) add(MealField.TYPE)
    if (old.kcal != new.kcal) add(MealField.KCAL)
    if (old.protein != new.protein) add(MealField.PROTEIN)
    if (old.carbs != new.carbs) add(MealField.CARBS)
    if (old.fat != new.fat) add(MealField.FAT)
}
