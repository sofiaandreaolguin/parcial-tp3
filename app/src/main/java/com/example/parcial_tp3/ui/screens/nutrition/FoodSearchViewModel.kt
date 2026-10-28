package com.example.parcial_tp3.ui.screens.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.data.repository.FoodRepository
import com.example.parcial_tp3.data.repository.toAppError
import com.example.parcial_tp3.domain.GramsParse
import com.example.parcial_tp3.domain.MealForm
import com.example.parcial_tp3.domain.model.FoodItem
import com.example.parcial_tp3.domain.model.Ingredient
import com.example.parcial_tp3.domain.nutrientsForPortion
import com.example.parcial_tp3.domain.parseGrams
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import kotlin.math.roundToInt
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Cuanto se espera sin tecleo antes de consultar, para no buscar una vez por letra. */
const val DEBOUNCE_MS = 300L

/**
 * `food_search` (EXT02). Spec: specs/funcionales/busqueda-de-alimentos.md, C19 a C32 y C35.
 *
 * La primera consulta sale del init y no de la composicion, asi rotar no la repite (RF07).
 */
@HiltViewModel
class FoodSearchViewModel @Inject constructor(
    private val repo: FoodRepository,
) : ViewModel() {

    private val _estado = MutableStateFlow(FoodSearchUiState())
    val estado: StateFlow<FoodSearchUiState> = _estado.asStateFlow()

    /** La consulta en curso. Cada tecla cancela la anterior: por eso C7 del repositorio. */
    private var busqueda: Job? = null

    init {
        buscar(_estado.value.query, conEspera = false)
    }

    fun onQueryChange(texto: String) {
        _estado.update { it.copy(query = texto) }
        buscar(texto, conEspera = true)
    }

    fun onRetry() = buscar(_estado.value.query, conEspera = false)

    fun onFoodSelected(item: FoodItem) = _estado.update {
        it.copy(
            seleccionado = item,
            gramos = GRAMOS_INICIALES,
            gramosError = null,
            porcion = nutrientsForPortion(item.per100g, GRAMOS_INICIALES.toDouble()),
        )
    }

    /** Recalcula con lo que ya esta en memoria: no vuelve al servidor (C27). */
    fun onGramsChange(texto: String) {
        val alimento = _estado.value.seleccionado ?: return
        when (val leido = parseGrams(texto)) {
            is GramsParse.Ok -> _estado.update {
                it.copy(
                    gramos = texto,
                    gramosError = null,
                    porcion = nutrientsForPortion(alimento.per100g, leido.grams),
                )
            }
            is GramsParse.Err -> _estado.update {
                it.copy(gramos = texto, gramosError = leido.error, porcion = null)
            }
        }
    }

    fun onBackFromPortion() = _estado.update { it.sinSeleccion() }

    /**
     * El formulario que recibe `Nutrition / 07`, con los numeros ya redondeados y con el formato
     * que espera su validacion: "248" y no "248.0" (C30).
     *
     * Deja la pantalla en los resultados, asi volver de 07 sin guardar no muestra el panel de la
     * porcion de un alimento que ya se uso (C35).
     */
    fun onConfirm(date: LocalDate): FoodSelection? {
        val actual = _estado.value
        val alimento = actual.seleccionado ?: return null
        val porcion = actual.porcion ?: return null
        if (actual.gramosError != null) return null

        _estado.update { it.sinSeleccion() }
        val form = MealForm(
            name = alimento.name,
            type = null,
            date = date,
            kcal = porcion.kcal.toString(),
            protein = porcion.proteinG.toFormText(),
            carbs = porcion.carbsG.toFormText(),
            fat = porcion.fatG.toFormText(),
        )
        // Ingredient guarda gramos enteros: "0,4" no puede quedar en 0, que isValidIngredient rechaza (C81).
        val gramos = (parseGrams(actual.gramos) as? GramsParse.Ok)?.grams ?: return null
        return FoodSelection(form, Ingredient(alimento.name, gramos.roundToInt().coerceAtLeast(1), porcion.kcal))
    }

    private fun FoodSearchUiState.sinSeleccion() =
        copy(seleccionado = null, gramos = GRAMOS_INICIALES, gramosError = null, porcion = null)

    private fun buscar(q: String, conEspera: Boolean) {
        busqueda?.cancel()
        busqueda = viewModelScope.launch {
            if (conEspera) delay(DEBOUNCE_MS)
            _estado.update { it.copy(cargando = true, error = null, vacio = false) }
            repo.searchFoods(q, page = 1)
                .onSuccess { pagina ->
                    _estado.update {
                        // vacio y error son distintos: con total 0 no existe ese alimento.
                        it.copy(cargando = false, items = pagina.items, vacio = pagina.total == 0)
                    }
                }
                .onFailure { e ->
                    _estado.update { it.copy(cargando = false, error = e.toAppError(), items = emptyList()) }
                }
        }
    }
}
