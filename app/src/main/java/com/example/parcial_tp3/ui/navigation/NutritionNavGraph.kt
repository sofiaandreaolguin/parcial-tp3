package com.example.parcial_tp3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.parcial_tp3.domain.model.Ingredient
import com.example.parcial_tp3.ui.screens.nutrition.AddFoodRoute
import com.example.parcial_tp3.ui.screens.nutrition.FoodAnalysisRoute
import com.example.parcial_tp3.ui.screens.nutrition.FoodCaptureViewModel
import com.example.parcial_tp3.ui.screens.nutrition.FoodConfirmationRoute
import com.example.parcial_tp3.ui.screens.nutrition.MealDetailRoute
import com.example.parcial_tp3.ui.screens.nutrition.MealDraft
import com.example.parcial_tp3.ui.screens.nutrition.NutritionDashboardRoute
import com.example.parcial_tp3.ui.screens.nutrition.NutritionViewModel
import com.example.parcial_tp3.ui.screens.nutrition.PhotoAnalysisRoute
import com.example.parcial_tp3.ui.screens.nutrition.TodaysMealsRoute
import com.example.parcial_tp3.ui.screens.nutrition.VoiceFoodLoggingRoute

/**
 * Nutricion: 8 pantallas. 01, 02, 03, 06, 07 y 08 con barra; 04 y 05 encima.
 *
 * Los casos que sorprenden estan asi en el Figma y se respetan: 03, 06, 07 y 08
 * llevan barra aunque parezcan pasos de un flujo.
 *
 * NAV05: 04, 05 y 06 son simuladas y no piden microfono ni camara.
 */
fun NavGraphBuilder.nutritionGraph(nav: NavController) {
    navigation(startDestination = Rutas.NUTRITION_DASHBOARD, route = Rutas.NUTRITION_GRAPH) {

        // 01 y 02 piden NutritionViewModel a la entrada del grafo: comparten la fecha elegida.
        composable(Rutas.NUTRITION_DASHBOARD) { entrada ->
            val grafo = remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }
            val captura = hiltViewModel<FoodCaptureViewModel>(grafo)
            NutritionDashboardRoute(
                grafo = grafo,
                onHistory = { nav.navigate(Rutas.TODAYS_MEALS) },
                onMeal = { mealId -> nav.navigate(Rutas.mealDetails(mealId)) },
                onAddMeal = { nav.navigate(Rutas.ADD_FOOD) },
                onVoice = { nav.navigate(Rutas.VOICE_FOOD_LOGGING) },
                onPhoto = { abrirFoto(nav, captura) },
                onConfirmed = { nav.navigate(Rutas.FOOD_CONFIRMATION) },
            )
        }

        composable(Rutas.TODAYS_MEALS) { entrada ->
            val grafo = remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }
            TodaysMealsRoute(
                grafo = grafo,
                onMeal = { mealId -> nav.navigate(Rutas.mealDetails(mealId)) },
                onAddMeal = { nav.navigate(Rutas.ADD_FOOD) },
            )
        }

        composable(Rutas.ADD_FOOD) { entrada ->
            val grafo = remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }
            val captura = hiltViewModel<FoodCaptureViewModel>(grafo)
            val nutricion = hiltViewModel<NutritionViewModel>(grafo)
            AddFoodRoute(
                grafo = grafo,
                onBack = { nav.popBackStack() },
                onVoice = { nav.navigate(Rutas.VOICE_FOOD_LOGGING) },
                onPhoto = { abrirFoto(nav, captura) },
                onManual = { abrirCargaManual(captura, nutricion) },
                onConfirmed = { nav.navigate(Rutas.FOOD_CONFIRMATION) },
            )
        }

        composable(Rutas.VOICE_FOOD_LOGGING) { entrada ->
            // remember: si "X" ya sacó la pantalla mientras anima su salida, buscar la entrada de
            // nuevo en cada composición tiraría IllegalArgumentException.
            val grafo = remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }
            val captura = hiltViewModel<FoodCaptureViewModel>(grafo)
            val nutricion = hiltViewModel<NutritionViewModel>(grafo)
            VoiceFoodLoggingRoute(
                grafo = grafo,
                onDone = { nav.navigate(Rutas.FOOD_ANALYSIS) { launchSingleTop = true } },
                onTypeInstead = { abrirCargaManual(captura, nutricion) },
                onClose = { nav.popBackStack() },
            )
        }

        composable(Rutas.FOOD_ANALYSIS) { entrada ->
            val grafo = remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }
            val abrirConfirmacion = rememberAbrirConfirmacion(nav, grafo)
            FoodAnalysisRoute(
                grafo = grafo,
                // 05 sale de la pila: si no, atrás desde 07 dejaría un análisis que ya no corre.
                onDone = { abrirConfirmacion { popUpTo(Rutas.FOOD_ANALYSIS) { inclusive = true } } },
                // inclusive y por ruta: un segundo toque no saca también a 04
                onClose = { nav.popBackStack(Rutas.FOOD_ANALYSIS, inclusive = true) },
            )
        }

        composable(Rutas.PHOTO_ANALYSIS) { entrada ->
            val grafo = remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }
            val abrirConfirmacion = rememberAbrirConfirmacion(nav, grafo)
            PhotoAnalysisRoute(
                grafo = grafo,
                onBack = { nav.popBackStack() },
                onReview = { abrirConfirmacion { launchSingleTop = true } },
            )
        }

        composable(Rutas.FOOD_CONFIRMATION) { entrada ->
            val grafo = remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }
            FoodConfirmationRoute(
                grafo = grafo,
                // por ruta e inclusive: un segundo toque no saca también la pantalla de abajo
                onBack = { nav.popBackStack(Rutas.FOOD_CONFIRMATION, inclusive = true) },
                // Hasta el grafo y no hasta el Dashboard: desde Home ("Log breakfast") el Dashboard no
                // está en la pila. Así atrás no vuelve a 07 ni a los pasos de la captura.
                onSaved = {
                    nav.navigate(Rutas.NUTRITION_DASHBOARD) {
                        popUpTo(Rutas.NUTRITION_GRAPH)
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(
            route = Rutas.MEAL_DETAILS,
            arguments = listOf(navArgument(Rutas.ARG_MEAL_ID) { type = NavType.LongType }),
        ) {
            MealDetailRoute(
                // por ruta e inclusive: después de eliminar, un toque de más en ‹ no saca también 01
                onBack = { nav.popBackStack(Rutas.MEAL_DETAILS, inclusive = true) },
            )
        }
    }
}

// El borrador se carga antes de navegar y no al componer 06: rotar no lo pisa (CONTEXTO §2).
private fun abrirFoto(nav: NavController, captura: FoodCaptureViewModel) {
    captura.onPhotoCaptureStarted()
    nav.navigate(Rutas.PHOTO_ANALYSIS) { launchSingleTop = true }
}

// "Log Manually" y "Type instead": abren el pop-up de carga con la fecha de 01. La hoja que lo
// dibuja (ManualEntrySheetHost) y el paso a 07 con "Continue" van en la pantalla del pop-up.
private fun abrirCargaManual(captura: FoodCaptureViewModel, nutricion: NutritionViewModel) {
    captura.onOpenManualEntry(nutricion.selectedDate.value)
}

private typealias AbrirConfirmacion = (NavOptionsBuilder.() -> Unit) -> Unit

/**
 * Arma la confirmación en el ViewModel y recién después navega a 07, como [abrirFoto]: la carga no
 * arranca al componer 07 (CONTEXTO §2), así que rotar no la repite. El nombre del borrador se
 * resuelve acá porque el ViewModel no tiene Context; la fecha es la elegida en 01.
 */
@Composable
private fun rememberAbrirConfirmacion(nav: NavController, grafo: NavBackStackEntry): AbrirConfirmacion {
    val captura = hiltViewModel<FoodCaptureViewModel>(grafo)
    val nutricion = hiltViewModel<NutritionViewModel>(grafo)
    val resources = LocalResources.current
    return remember(nav, captura, nutricion, resources) {
        { opciones ->
            val borrador = captura.uiState.value.draft
            val nombre = resources.getString(borrador?.name ?: MealDraft().name)
            val ingredientes = borrador?.ingredients.orEmpty().map {
                Ingredient(resources.getString(it.name), it.grams, it.kcal)
            }
            captura.onOpenConfirmation(nutricion.selectedDate.value, nombre, ingredientes)
            nav.navigate(Rutas.FOOD_CONFIRMATION, opciones)
        }
    }
}
