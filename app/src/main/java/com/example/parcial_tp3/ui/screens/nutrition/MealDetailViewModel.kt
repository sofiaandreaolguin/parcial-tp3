package com.example.parcial_tp3.ui.screens.nutrition

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.MealDataException
import com.example.parcial_tp3.data.repository.MealRepository
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.ui.navigation.Rutas
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Controlador de Nutrition / 08: la comida de Rutas.ARG_MEAL_ID, con eliminar y duplicar (RF04). */
@HiltViewModel
class MealDetailViewModel @Inject constructor(
    private val repo: MealRepository,
    savedState: SavedStateHandle,
) : ViewModel() {

    private val mealId: Long = checkNotNull(savedState[Rutas.ARG_MEAL_ID])

    private val actions = MutableStateFlow(Actions())

    private val _events = Channel<MealDetailEvent>(Channel.BUFFERED)
    val events: Flow<MealDetailEvent> = _events.receiveAsFlow()

    // Room avisa que la fila ya no está antes de que deleteMeal vuelva: ese null no es NotFound,
    // y la pantalla sigue mostrando la comida hasta que navega hacia atrás.
    val uiState: StateFlow<MealDetailUiState> = combine(
        repo.observeMeal(mealId).filter { it != null || !actions.value.deletingOrDeleted },
        actions,
    ) { meal, a ->
        if (meal == null) {
            MealDetailUiState.NotFound
        } else {
            MealDetailUiState.Content(meal, a.confirmingDelete, a.isBusy, a.actionError)
        }
    }
        .catch { e ->
            if (e !is MealDataException) throw e
            emit(MealDetailUiState.Error(R.string.nutrition_error_load))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MealDetailUiState.Loading)

    fun onDeleteClick() = actions.update { it.copy(confirmingDelete = true, actionError = null) }

    fun onDismissDelete() = actions.update { it.copy(confirmingDelete = false) }

    fun onConfirmDelete() = runAction(deleting = true, MealDetailEvent.Deleted) { repo.deleteMeal(mealId) }

    fun onDuplicate() {
        val meal = (uiState.value as? MealDetailUiState.Content)?.meal ?: return
        runAction(deleting = false, MealDetailEvent.Duplicated) { repo.addMeal(meal.copy(id = 0)) }
    }

    private fun runAction(deleting: Boolean, done: MealDetailEvent, action: suspend () -> Unit) {
        // Ya borrada: duplicar volvería a crear la comida antes de que la pantalla se cierre.
        if (actions.value.isBusy || actions.value.deleted) return
        actions.update {
            it.copy(confirmingDelete = false, isBusy = true, deleting = deleting, actionError = null)
        }
        viewModelScope.launch {
            try {
                action()
            } catch (e: MealDataException) {
                actions.update { it.copy(isBusy = false, deleting = false, actionError = R.string.nutrition_error_action) }
                return@launch
            }
            actions.update { it.copy(isBusy = false, deleted = it.deleted || deleting) }
            _events.send(done)
        }
    }

    private data class Actions(
        val confirmingDelete: Boolean = false,
        val isBusy: Boolean = false,
        val deleting: Boolean = false,
        val deleted: Boolean = false,
        val actionError: Int? = null,
    ) {
        val deletingOrDeleted get() = deleting || deleted
    }
}
