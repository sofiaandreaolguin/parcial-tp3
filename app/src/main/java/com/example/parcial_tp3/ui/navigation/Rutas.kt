package com.example.parcial_tp3.ui.navigation

import androidx.annotation.StringRes
import com.example.parcial_tp3.R

/**
 * Las rutas de las 37 pantallas del Figma.
 *
 * Son 33 destinos para 37 frames: los 5 estados de Voice (Idle, Listening, Processing,
 * Speaking y Error) comparten la ruta [VOICE] porque son la misma pantalla cambiando de
 * cara, no destinos distintos (ficha docs/decisiones/002).
 *
 * Fuente: specs/PANTALLAS.md, seccion 15. Ninguna pantalla escribe una ruta a mano.
 */
object Rutas {

    // Grafos (uno por flujo)
    const val ACCESS_GRAPH = "access_graph"
    const val HOME_GRAPH = "home_graph"
    const val WORKOUT_GRAPH = "workout_graph"
    const val NUTRITION_GRAPH = "nutrition_graph"
    const val PROGRESS_GRAPH = "progress_graph"
    const val PROFILE_GRAPH = "profile_graph"
    const val VOICE_GRAPH = "voice_graph"

    // Acceso
    const val SIGN_IN = "sign_in"
    const val SIGN_UP = "sign_up"

    // Inicio
    const val HOME_DASHBOARD = "home_dashboard"
    const val NOTIFICATIONS = "notifications"

    // Asistente VORA
    const val VOICE = "voice"
    const val CONVERSATION = "conversation"

    // Entrenamiento
    const val ARG_WORKOUT_ID = "workoutId"
    const val ARG_EXERCISE_ID = "exerciseId"
    const val WORKOUT_DASHBOARD = "workout_dashboard"
    const val WEEKLY_PLAN = "weekly_plan"
    const val WORKOUT_DETAILS = "workout_details/{$ARG_WORKOUT_ID}"
    const val PRE_WORKOUT = "pre_workout/{$ARG_WORKOUT_ID}"
    const val ACTIVE_WORKOUT = "active_workout/{$ARG_WORKOUT_ID}"
    const val EXERCISE_DETAILS = "exercise_details/{$ARG_WORKOUT_ID}/{$ARG_EXERCISE_ID}"
    const val WORKOUT_COMPLETE = "workout_complete/{$ARG_WORKOUT_ID}"

    fun workoutDetails(workoutId: String) = "workout_details/$workoutId"
    fun preWorkout(workoutId: String) = "pre_workout/$workoutId"
    fun activeWorkout(workoutId: String) = "active_workout/$workoutId"
    fun exerciseDetails(workoutId: String, exerciseId: String) =
        "exercise_details/$workoutId/$exerciseId"
    fun workoutComplete(workoutId: String) = "workout_complete/$workoutId"

    // Nutricion
    const val ARG_MEAL_ID = "mealId"
    const val NUTRITION_DASHBOARD = "nutrition_dashboard"
    const val TODAYS_MEALS = "todays_meals"
    const val ADD_FOOD = "add_food"
    const val VOICE_FOOD_LOGGING = "voice_food_logging"
    const val FOOD_ANALYSIS = "food_analysis"
    const val PHOTO_ANALYSIS = "photo_analysis"

    /** mealId = -1 significa "comida nueva"; con id, se edita una ya guardada (NAV04). */
    const val FOOD_CONFIRMATION = "food_confirmation?$ARG_MEAL_ID={$ARG_MEAL_ID}"
    const val MEAL_DETAILS = "meal_details/{$ARG_MEAL_ID}"

    fun foodConfirmation(mealId: Long = -1L) = "food_confirmation?$ARG_MEAL_ID=$mealId"
    fun mealDetails(mealId: Long) = "meal_details/$mealId"

    // Progreso
    const val TODAYS_OVERVIEW = "todays_overview"
    const val PROGRESS_DASHBOARD = "progress_dashboard"
    const val WORKOUT_PROGRESS = "workout_progress"
    const val NUTRITION_PROGRESS = "nutrition_progress"
    const val HEALTH_TRENDS = "health_trends"
    const val WEEKLY_REVIEW = "weekly_review"

    // Perfil
    const val PROFILE_DASHBOARD = "profile_dashboard"
    const val EDIT_PROFILE = "edit_profile"
    const val GOALS_PREFERENCES = "goals_preferences"
    const val NUTRITION_PREFERENCES = "nutrition_preferences"
    const val VOICE_AI_SETTINGS = "voice_ai_settings"
    const val SETTINGS = "settings"
}

/**
 * Los 5 destinos de la barra inferior. Cada uno sabe en que grafo vive, para poder
 * calcular cual esta activa con la jerarquia del destino y no comparando la ruta.
 *
 * No lleva icono a proposito: los del Figma son dibujos propios, no iconos de Material.
 * Los suma Tema y componentes (tarea VC-d) como quiera, con drawables en res/.
 */
enum class VoraTab(
    val grafo: String,
    val inicio: String,
    @param:StringRes val etiqueta: Int,
) {
    HOME(Rutas.HOME_GRAPH, Rutas.HOME_DASHBOARD, R.string.tab_home),
    WORKOUT(Rutas.WORKOUT_GRAPH, Rutas.WORKOUT_DASHBOARD, R.string.tab_workout),
    NUTRITION(Rutas.NUTRITION_GRAPH, Rutas.NUTRITION_DASHBOARD, R.string.tab_nutrition),
    // La pestana aterriza en el frame 01, no en el 02: ficha docs/decisiones/005.
    PROGRESS(Rutas.PROGRESS_GRAPH, Rutas.TODAYS_OVERVIEW, R.string.tab_progress),
    PROFILE(Rutas.PROFILE_GRAPH, Rutas.PROFILE_DASHBOARD, R.string.tab_profile),
}

/**
 * Las 16 pantallas que comparten la barra inferior (specs/PANTALLAS.md, seccion 10).
 *
 * La barra se dibuja UNA sola vez, en el Scaffold de MainActivity, y aparece solo en
 * estas rutas. Ninguna pantalla la dibuja por su cuenta.
 *
 * Para las rutas con argumento va el patron, que es lo que devuelve destination.route.
 */
val RUTAS_CON_BARRA: Set<String> = setOf(
    Rutas.HOME_DASHBOARD,
    Rutas.NOTIFICATIONS,
    Rutas.WORKOUT_DASHBOARD,
    Rutas.WEEKLY_PLAN,
    Rutas.NUTRITION_DASHBOARD,
    Rutas.TODAYS_MEALS,
    Rutas.ADD_FOOD,
    Rutas.PHOTO_ANALYSIS,
    Rutas.FOOD_CONFIRMATION,
    Rutas.MEAL_DETAILS,
    Rutas.TODAYS_OVERVIEW,
    Rutas.PROGRESS_DASHBOARD,
    Rutas.WORKOUT_PROGRESS,
    Rutas.NUTRITION_PROGRESS,
    Rutas.HEALTH_TRENDS,
    Rutas.PROFILE_DASHBOARD,
)

/**
 * Las que se pintan por detras de las barras del sistema (sin el padding del Scaffold) y se
 * encargan de sus propios margenes con statusBarsPadding y navigationBarsPadding.
 */
val RUTAS_A_SANGRE: Set<String> = setOf(
    Rutas.SIGN_IN,
    Rutas.SIGN_UP,
    Rutas.VOICE,
    Rutas.CONVERSATION,
)

/**
 * Las que se abren encima: sin barra, con volver o cerrar propio.
 *
 * Son 15 rutas para 19 frames, porque los 5 estados de Voice comparten [Rutas.VOICE].
 */
val RUTAS_ENCIMA: Set<String> = setOf(
    Rutas.VOICE,
    Rutas.CONVERSATION,
    Rutas.WORKOUT_DETAILS,
    Rutas.PRE_WORKOUT,
    Rutas.ACTIVE_WORKOUT,
    Rutas.EXERCISE_DETAILS,
    Rutas.WORKOUT_COMPLETE,
    Rutas.VOICE_FOOD_LOGGING,
    Rutas.FOOD_ANALYSIS,
    Rutas.WEEKLY_REVIEW,
    Rutas.EDIT_PROFILE,
    Rutas.GOALS_PREFERENCES,
    Rutas.NUTRITION_PREFERENCES,
    Rutas.VOICE_AI_SETTINGS,
    Rutas.SETTINGS,
)

/** Las 2 de acceso: antes de entrar a la app, sin barra. */
val RUTAS_ACCESO: Set<String> = setOf(Rutas.SIGN_IN, Rutas.SIGN_UP)
