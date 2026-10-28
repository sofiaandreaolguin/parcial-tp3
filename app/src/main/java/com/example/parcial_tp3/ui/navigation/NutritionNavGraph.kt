package com.example.parcial_tp3.ui.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.parcial_tp3.ui.screens.nutrition.AddFoodRoute
import com.example.parcial_tp3.ui.screens.nutrition.FoodAnalysisRoute
import com.example.parcial_tp3.ui.screens.nutrition.FoodConfirmationRoute
import com.example.parcial_tp3.ui.screens.nutrition.MealDetailRoute
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

        // Las Routes piden sus ViewModels a la entrada del grafo: 01 y 02 comparten la fecha, y 03 a
        // 07 la captura. El grafo solo navega.
        composable(Rutas.NUTRITION_DASHBOARD) { entrada ->
            val grafo = remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }
            NutritionDashboardRoute(
                grafo = grafo,
                onHistory = { nav.navigate(Rutas.TODAYS_MEALS) },
                onMeal = { mealId -> nav.navigate(Rutas.mealDetails(mealId)) },
                onAddMeal = { nav.navigate(Rutas.ADD_FOOD) },
                onVoice = { nav.navigate(Rutas.VOICE_FOOD_LOGGING) },
                onPhoto = { nav.navigate(Rutas.PHOTO_ANALYSIS) { launchSingleTop = true } },
                onConfirmed = { nav.navigate(Rutas.FOOD_CONFIRMATION) { launchSingleTop = true } },
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
            AddFoodRoute(
                grafo = grafo,
                onBack = { nav.popBackStack() },
                onVoice = { nav.navigate(Rutas.VOICE_FOOD_LOGGING) },
                onPhoto = { nav.navigate(Rutas.PHOTO_ANALYSIS) { launchSingleTop = true } },
                onConfirmed = { nav.navigate(Rutas.FOOD_CONFIRMATION) { launchSingleTop = true } },
            )
        }

        composable(Rutas.VOICE_FOOD_LOGGING) { entrada ->
            // remember: si "X" ya sacó la pantalla mientras anima su salida, buscar la entrada de
            // nuevo en cada composición tiraría IllegalArgumentException.
            val grafo = remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }
            VoiceFoodLoggingRoute(
                grafo = grafo,
                onDone = { nav.navigate(Rutas.FOOD_ANALYSIS) { launchSingleTop = true } },
                onConfirmed = { nav.navigate(Rutas.FOOD_CONFIRMATION) { launchSingleTop = true } },
                onClose = { nav.popBackStack() },
            )
        }

        composable(Rutas.FOOD_ANALYSIS) { entrada ->
            val grafo = remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }
            FoodAnalysisRoute(
                grafo = grafo,
                // 05 sale de la pila: si no, atrás desde 07 dejaría un análisis que ya no corre.
                onDone = {
                    nav.navigate(Rutas.FOOD_CONFIRMATION) { popUpTo(Rutas.FOOD_ANALYSIS) { inclusive = true } }
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
                onReview = { nav.navigate(Rutas.FOOD_CONFIRMATION) { launchSingleTop = true } },
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
