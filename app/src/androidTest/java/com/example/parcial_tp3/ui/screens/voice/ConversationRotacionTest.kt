package com.example.parcial_tp3.ui.screens.voice

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.CoachRepository
import com.example.parcial_tp3.domain.model.CoachMessage
import com.example.parcial_tp3.domain.model.CoachSuggestion
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import java.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

/**
 * C29: rotar no pierde lo que se escribio y todavia no se mando.
 *
 * **Es el unico test de Compose del proyecto, y tiene que serlo.** El borrador vive en un
 * `rememberSaveable` dentro de `ConversationRoute` (asi lo pide `CONTEXTO.md` seccion 3: lo
 * que se esta tipeando es estado pasajero de la pantalla, no del ViewModel). Eso no se puede
 * comprobar desde `test/`, porque ahi no hay composicion ni Activity que rotar.
 *
 * No rota el emulador de verdad: [StateRestorationTester] destruye y rearma la composicion
 * restaurando el estado guardado, que es exactamente lo que le pasa a `rememberSaveable`
 * cuando la pantalla gira. Rotar de verdad ademas recrearia la Activity, y eso prueba el
 * framework, no nuestro codigo.
 *
 * No necesita Hilt: `ConversationRoute` recibe el ViewModel por parametro y solo usa
 * `hiltViewModel()` como valor por omision.
 */
class ConversationRotacionTest {

    @get:Rule
    val compose = createComposeRule()

    private val contexto = InstrumentationRegistry.getInstrumentation().targetContext
    private val aviso = contexto.getString(R.string.conversation_placeholder)

    private val aMedioEscribir = "esto todavia no lo mande"

    /** Repositorio quieto: este test no mira el hilo, solo el borrador. */
    private class RepositorioQuieto : CoachRepository {
        override suspend fun getSuggestions(): Result<List<CoachSuggestion>> = Result.success(emptyList())
        override fun observeHistory(): Flow<List<CoachMessage>> = MutableStateFlow(emptyList())
        override suspend fun send(text: String): Result<CoachMessage> =
            Result.failure(IllegalStateException("este test no manda nada"))
        override suspend fun seedIfEmpty(demo: List<CoachMessage>) = Unit
    }

    @Test
    fun c29_rotar_conserva_lo_escrito_sin_mandar() {
        val restauracion = StateRestorationTester(compose)
        val modelo = ConversationViewModel(RepositorioQuieto(), Clock.systemUTC())

        restauracion.setContent {
            Parcialtp3Theme {
                ConversationRoute(onVolver = {}, viewModel = modelo)
            }
        }

        compose.onNodeWithContentDescription(aviso).performTextInput(aMedioEscribir)
        compose.onNodeWithText(aMedioEscribir).assertIsDisplayed()

        restauracion.emulateSavedInstanceStateRestore()

        compose.onNodeWithText(aMedioEscribir).assertIsDisplayed()
    }
}
