package com.example.parcial_tp3.ui.navigation

import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.PantallaVacia
import com.example.parcial_tp3.ui.screens.nutrition.AddFoodRoute
import com.example.parcial_tp3.ui.screens.nutrition.FoodAnalysisRoute
import com.example.parcial_tp3.ui.screens.nutrition.FoodCaptureViewModel
import com.example.parcial_tp3.ui.screens.nutrition.FoodConfirmationRoute
import com.example.parcial_tp3.ui.screens.nutrition.NEW_MEAL_ID
import com.example.parcial_tp3.ui.screens.nutrition.NutritionDashboardRoute
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
            val captura = hiltViewModel<FoodCaptureViewModel>(remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) })
            AddFoodRoute(
                onBack = { nav.popBackStack() },
                onVoice = { nav.navigate(Rutas.VOICE_FOOD_LOGGING) },
                onPhoto = { abrirFoto(nav, captura) },
                onManual = { abrirCargaManual(nav, captura) },
            )
        }

        composable(Rutas.VOICE_FOOD_LOGGING) { entrada ->
            // remember: si "X" ya sacó la pantalla mientras anima su salida, buscar la entrada de
            // nuevo en cada composición tiraría IllegalArgumentException.
            val grafo = remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }
            val captura = hiltViewModel<FoodCaptureViewModel>(grafo)
            VoiceFoodLoggingRoute(
                grafo = grafo,
                onDone = { nav.navigate(Rutas.FOOD_ANALYSIS) { launchSingleTop = true } },
                onTypeInstead = { abrirCargaManual(nav, captura) },
                onClose = { nav.popBackStack() },
            )
        }

        composable(Rutas.FOOD_ANALYSIS) { entrada ->
            val grafo = remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }
            FoodAnalysisRoute(
                grafo = grafo,
                // 05 sale de la pila: si no, atrás desde 07 dejaría un análisis que ya no corre.
                onDone = {
                    nav.navigate(Rutas.foodConfirmation()) {
                        popUpTo(Rutas.FOOD_ANALYSIS) { inclusive = true }
                    }
                },
                // inclusive y por ruta: un segundo toque no saca también a 04
                onClose = { nav.popBackStack(Rutas.FOOD_ANALYSIS, inclusive = true) },
            )
        }

        composable(Rutas.PHOTO_ANALYSIS) { entrada ->
            val grafo = remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }
            PhotoAnalysisRoute(
                grafo = grafo,
                onBack = { nav.popBackStack() },
                onReview = { nav.navigate(Rutas.foodConfirmation()) { launchSingleTop = true } },
            )
        }

        composable(
            route = Rutas.FOOD_CONFIRMATION,
            // Sin mealId (-1) es una comida nueva; con id se edita una ya guardada (NAV04).
            arguments = listOf(
                navArgument(Rutas.ARG_MEAL_ID) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
            ),
        ) { entrada ->
            val grafo = remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }
            FoodConfirmationRoute(
                grafo = grafo,
                mealId = entrada.arguments?.getLong(Rutas.ARG_MEAL_ID) ?: NEW_MEAL_ID,
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
            PantallaVacia(nombre = stringResource(R.string.pant_nutrition_08))
        }
    }
}

// El borrador se carga antes de navegar y no al componer 06: rotar no lo pisa (CONTEXTO §2).
private fun abrirFoto(nav: NavController, captura: FoodCaptureViewModel) {
    captura.onPhotoCaptureStarted()
    nav.navigate(Rutas.PHOTO_ANALYSIS) { launchSingleTop = true }
}

// "Log Manually" y "Type instead": 07 tiene que abrir vacía aunque antes haya habido una foto.
private fun abrirCargaManual(nav: NavController, captura: FoodCaptureViewModel) {
    captura.onManualEntry()
    nav.navigate(Rutas.foodConfirmation())
}
