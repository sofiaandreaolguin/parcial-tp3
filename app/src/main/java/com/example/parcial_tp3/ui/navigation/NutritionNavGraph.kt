package com.example.parcial_tp3.ui.navigation

import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.PantallaVacia
import com.example.parcial_tp3.ui.screens.nutrition.AddFoodRoute
import com.example.parcial_tp3.ui.screens.nutrition.NutritionDashboardRoute
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

        composable(Rutas.NUTRITION_DASHBOARD) {
            NutritionDashboardRoute(
                onHistory = { nav.navigate(Rutas.TODAYS_MEALS) },
                onMeal = { mealId -> nav.navigate(Rutas.mealDetails(mealId)) },
                onAddDinner = { nav.navigate(Rutas.ADD_FOOD) },
                onVoice = { nav.navigate(Rutas.VOICE_FOOD_LOGGING) },
                onPhoto = { nav.navigate(Rutas.PHOTO_ANALYSIS) },
            )
        }

        composable(Rutas.TODAYS_MEALS) {
            TodaysMealsRoute(
                onMeal = { mealId -> nav.navigate(Rutas.mealDetails(mealId)) },
                onAddDinner = { nav.navigate(Rutas.ADD_FOOD) },
            )
        }

        composable(Rutas.ADD_FOOD) {
            AddFoodRoute(
                onBack = { nav.popBackStack() },
                onVoice = { nav.navigate(Rutas.VOICE_FOOD_LOGGING) },
                onPhoto = { nav.navigate(Rutas.PHOTO_ANALYSIS) },
                onManual = { nav.navigate(Rutas.foodConfirmation()) },
            )
        }

        composable(Rutas.VOICE_FOOD_LOGGING) { entrada ->
            // remember: si "X" ya sacó la pantalla mientras anima su salida, buscar la entrada de
            // nuevo en cada composición tiraría IllegalArgumentException.
            val grafo = remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }
            VoiceFoodLoggingRoute(
                grafo = grafo,
                onDone = { nav.navigate(Rutas.FOOD_ANALYSIS) },
                onTypeInstead = { nav.navigate(Rutas.foodConfirmation()) },
                onClose = { nav.popBackStack() },
            )
        }

        composable(Rutas.FOOD_ANALYSIS) {
            PantallaVacia(
                nombre = stringResource(R.string.pant_nutrition_05),
                onVolver = { nav.popBackStack() },
            )
        }

        composable(Rutas.PHOTO_ANALYSIS) {
            PantallaVacia(nombre = stringResource(R.string.pant_nutrition_06))
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
        ) {
            PantallaVacia(nombre = stringResource(R.string.pant_nutrition_07))
        }

        composable(
            route = Rutas.MEAL_DETAILS,
            arguments = listOf(navArgument(Rutas.ARG_MEAL_ID) { type = NavType.LongType }),
        ) {
            PantallaVacia(nombre = stringResource(R.string.pant_nutrition_08))
        }
    }
}
