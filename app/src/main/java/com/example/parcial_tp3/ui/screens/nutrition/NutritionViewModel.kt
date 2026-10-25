package com.example.parcial_tp3.ui.screens.nutrition

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.MealDataException
import com.example.parcial_tp3.data.repository.MealRepository
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.MealType
import com.example.parcial_tp3.ui.screens.home.HomeDemoData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Controlador de Nutrition / 01 y 02: las comidas de Room de la fecha elegida y su total (RF04, RF07).
 * Se pide atado al grafo de Nutrition, así 01 y 02 comparten la fecha.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val repo: MealRepository,
    private val savedState: SavedStateHandle,
    clock: Clock,
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(
        savedState.get<String>(KEY_SELECTED_DATE)?.let(LocalDate::parse) ?: LocalDate.now(clock),
    )
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Cada reintento cambia el número y vuelve a suscribirse al Flow de Room.
    private val retries = MutableStateFlow(0)

    val uiState: StateFlow<NutritionUiState> = combine(_selectedDate, retries) { date, _ -> date }
        .flatMapLatest { date ->
            repo.observeMealsByDate(date)
                .map<List<Meal>, NutritionUiState> { meals -> toContent(date, meals) }
                .catch { e ->
                    if (e !is MealDataException) throw e
                    emit(NutritionUiState.Error(R.string.nutrition_error_load))
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NutritionUiState.Loading)

    fun onDateSelected(date: LocalDate) {
        savedState[KEY_SELECTED_DATE] = date.toString()
        _selectedDate.value = date
    }

    fun onRetry() = retries.update { it + 1 }

    // El total se suma acá y no con un SUM del DAO, igual que Home (docs/decisiones/007).
    // Se redondea la suma y no cada comida, así da lo mismo que Home.
    private fun toContent(date: LocalDate, meals: List<Meal>): NutritionUiState.Content {
        val kcal = meals.sumOf { it.kcal }
        return NutritionUiState.Content(
            date = date,
            kcal = kcal,
            kcalGoal = HomeDemoData.KCAL_GOAL,
            kcalProgress = progress(kcal, HomeDemoData.KCAL_GOAL),
            macros = listOf(
                macro(MacroKind.PROTEIN, meals.sumOf { it.proteinG }, HomeDemoData.PROTEIN_GOAL_G),
                macro(MacroKind.CARBS, meals.sumOf { it.carbsG }, HomeDemoData.CARBS_GOAL_G),
                macro(MacroKind.FAT, meals.sumOf { it.fatG }, NutritionDemoData.FAT_GOAL_G),
            ),
            meals = meals.map { MealRow(it.id, it.type, it.name, it.kcal) },
            pending = MealType.entries.firstOrNull { type -> meals.none { it.type == type } },
        )
    }

    private fun macro(kind: MacroKind, total: Double, goal: Int): MacroTotal {
        val grams = total.roundToInt()
        return MacroTotal(kind, grams, goal, progress(grams, goal))
    }

    private fun progress(value: Int, goal: Int): Float = (value.toFloat() / goal).coerceAtMost(1f)

    companion object {
        /** La fecha elegida, como texto ISO, para que sobreviva a la muerte del proceso. */
        const val KEY_SELECTED_DATE = "selectedDate"
    }
}
