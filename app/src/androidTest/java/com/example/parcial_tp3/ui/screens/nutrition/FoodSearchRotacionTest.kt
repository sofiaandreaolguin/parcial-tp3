package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial_tp3.data.repository.FoodRepository
import com.example.parcial_tp3.domain.model.FoodItem
import com.example.parcial_tp3.domain.model.FoodPage
import com.example.parcial_tp3.domain.model.Nutrients
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import org.junit.Rule
import org.junit.Test

/**
 * C34: rotar no pierde lo que se escribio en la busqueda (EXT02).
 *
 * **Prueba menos que su equivalente del coach, y conviene decirlo.** En `Voice / 05` el borrador
 * vive en un `rememberSaveable` dentro del Route, asi que al rotar hay algo real que guardar y
 * restaurar. Aca `query` y `gramos` viven en el **ViewModel**, que sobrevive al giro por como
 * funciona Android, no por codigo nuestro: tienen que estar ahi porque son la entrada del
 * debounce y de la busqueda.
 *
 * Lo que si prueba, y por eso existe: que la pantalla **dibuja desde el estado** y no guarda el
 * texto por su cuenta. [StateRestorationTester] tira la composicion y la rehace con el mismo
 * ViewModel, que es exactamente lo que pasa al rotar. Si alguien cambiara el campo de busqueda
 * por un `remember { mutableStateOf }` local, el texto se perderia y este test lo agarra.
 *
 * No necesita Hilt: se arma el ViewModel a mano y se dibuja el contenido de la hoja, que no
 * tiene estado. Se dibuja el contenido y no `FoodSearchSheet` porque el ModalBottomSheet vive
 * en su propia ventana y StateRestorationTester no la alcanza.
 */
class FoodSearchRotacionTest {

    @get:Rule
    val compose = createComposeRule()

    private val pollo = FoodItem("f05", "Pollo cocido", Nutrients(165, 31.0, 0.0, 3.6))

    private class CatalogoQuieto(private val item: FoodItem) : FoodRepository {
        override suspend fun searchFoods(q: String, page: Int): Result<FoodPage> =
            Result.success(FoodPage(listOf(item), 1, 50, 1))
    }

    /** La pantalla con su ViewModel, como la arma el Route pero sin Hilt ni navegacion. */
    private fun StateRestorationTester.pantallaCon(modelo: FoodSearchViewModel) = setContent {
        Parcialtp3Theme {
            val estado by modelo.estado.collectAsStateWithLifecycle()
            FoodSearchScreen(
                estado = estado,
                onQueryChange = modelo::onQueryChange,
                onFoodSelected = modelo::onFoodSelected,
                onGramsChange = modelo::onGramsChange,
                onBackFromPortion = modelo::onBackFromPortion,
                onConfirm = {},
                onRetry = modelo::onRetry,
            )
        }
    }

    @Test
    fun c34_rotar_conserva_el_texto_buscado() {
        val restauracion = StateRestorationTester(compose)
        val modelo = FoodSearchViewModel(CatalogoQuieto(pollo))
        restauracion.pantallaCon(modelo)

        compose.onNodeWithText("Search a food by name").performTextInput("pollo")
        compose.onNodeWithText("pollo").assertIsDisplayed()

        restauracion.emulateSavedInstanceStateRestore()

        compose.onNodeWithText("pollo").assertIsDisplayed()
    }

    @Test
    fun c34_rotar_conserva_los_gramos_sin_confirmar() {
        val restauracion = StateRestorationTester(compose)
        val modelo = FoodSearchViewModel(CatalogoQuieto(pollo))
        restauracion.pantallaCon(modelo)

        modelo.onFoodSelected(pollo)
        modelo.onGramsChange("175")
        compose.onNodeWithText("175").assertIsDisplayed()

        restauracion.emulateSavedInstanceStateRestore()

        compose.onNodeWithText("175").assertIsDisplayed()
        // Y lo calculado sigue ahi: 165 x 1,75 = 288,75 -> 289 kcal.
        compose.onNodeWithText("289 kcal").assertIsDisplayed()
    }
}
