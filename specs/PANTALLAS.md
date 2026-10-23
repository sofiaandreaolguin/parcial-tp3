# Inventario de las 37 pantallas (NAV01)

**Tarea:** VC-c · **Responsable del archivo:** Fiore (rol Navegación e inventario) · **Fecha:** 18/10/2026

De dónde sale: los frames del Figma del grupo (`figma.com/design/1nvbbfYNYmKAevuaG28J85`, página "UI Screens"), la consigna (`specs/CONSIGNA.md`) y la sección 3.3 de la metodología (qué comparte cada pantalla, leído de las capas de cada frame el 14/10).

**El Figma usa dos convenciones de nombre y las dos son correctas:** el flujo de Acceso escribe `Login - 01 — SignIn` (con guion) y los otros seis `Flujo / NN — Nombre` (con barra). Está así en el panel de capas: no lo "arreglen".

Los **37 nombres se verificaron uno por uno contra el panel de capas del Figma el 18/10** y están escritos exactamente como figuran ahí (`Workout / 03 — Workout Details`), escritos igual que en el panel de capas.

**El Figma no tiene prototipo armado:** el botón de presentar pasa los 41 frames (37 pantallas + 4 láminas del sistema de diseño) como diapositivas, sin flechas entre ellos. El recorrido que define este archivo es el del grupo.

**Cómo verificar los nombres:** en `docs/figma/` hay **una imagen por flujo** (`Workout Flow.png`, `Nutrition Flow.png`, …), con las pantallas de ese flujo una al lado de la otra en orden, exportadas a 2x. No hay una imagen por frame.

Para qué sirve:
- Es la **base de NAV01**: la tabla del README sale de acá (sección 10).
- Es el **contrato de la tarea VC-e** (navegación con las 37): las rutas de la sección 9 son las que se crean en el `NavHost`.
- Es el **reparto**: cada rol sabe qué pantallas son suyas y cuáles toca con otra (sección 7).

**No se agregan pantallas que no estén en esta lista** (límite de la consigna: no inventar pantallas).

---

## 1. Resumen

| | Cuántas |
|---|---|
| Pantallas de Figma (frames) | **37** |
| Con barra inferior | **16** |
| Se abren encima (sin barra) | **19** |
| De acceso (antes de entrar a la app) | **2** |
| Destinos de navegación en el `NavHost` | **33** (los 5 estados de Voice son una sola ruta, sección 3) |
| Flujos (grafos) | **7** |

Por flujo:

| Flujo | Frames | Con barra | Encima | Acceso | Área (consigna) |
|---|---|---|---|---|---|
| Acceso (Login) | 2 | 0 | 0 | 2 | Acceso |
| Inicio (Home) | 2 | 2 | 0 | 0 | Home y Profile |
| Asistente VORA (Voice) | 6 | 0 | 6 | 0 | Coach y Progress |
| Entrenamiento (Workout) | 7 | 2 | 5 | 0 | Workout |
| Nutrición (Nutrition) | 8 | 6 | 2 | 0 | Nutrition |
| Progreso (Progress) | 6 | 5 | 1 | 0 | Coach y Progress |
| Perfil (Profile) | 6 | 1 | 5 | 0 | Home y Profile |
| **Total** | **37** | **16** | **19** | **2** | |

Por estado (NAV01):

| Estado | Cuántas | Cuáles |
|---|---|---|
| Funcional | 8 | Workout 01, 02, 03 y 06; Nutrition 01, 02, 07 y 08 |
| Demostración | 29 | Las demás |

Son los dos estados que nombra NAV01, y coinciden con la tabla de **cobertura mínima por área** de la consigna, donde el profe ya clasifica Acceso, Home y Profile como demostración, Workout como Retrofit + Room, Nutrition como Room, y Coach y Progress como simulado.

### Las 3 de demostración que igual muestran algún dato real

No cumplen un RF, así que siguen siendo demostración, pero conviene declararlo en el README: suma y evita que parezca que está de más.

| Pantalla | Qué dato es real |
|---|---|
| Home / 01 — Dashboard | El entrenamiento recomendado sale de la caché de Room y el total de calorías de la tabla de comidas |
| Workout / 04 — Pre Workout | Muestra el detalle real ya descargado; lo simulado es arrancar la sesión |
| Workout / 05 — Active Workout | Los ejercicios y series salen del detalle real; el cronómetro es simulado |

---

## 2. Cómo leer las tablas

**Tipo** (sale de las capas del frame en Figma):
- **Con barra:** el frame tiene la capa `bottom-navigation`. Va dentro del `Scaffold` de `MainActivity` y **no dibuja la barra**. Ojo: llevar barra **no quiere decir que no tenga flecha de volver**. `Nutrition / 03, 06, 07 y 08` tienen las dos cosas, así que `VoraTopBar` también se usa en pantallas con barra.
- **Encima:** sin barra inferior. Lleva su propio encabezado con volver o cerrar (`VoraTopBar`). Se llega con `navigate()` y se vuelve con `popBackStack()`.
- **Acceso:** antes de entrar a la app, sin barra.

**Ruta:** el `String` del destino en el `NavHost`. Es una **propuesta de la tarea VC-b**: una vez acordada, no se cambia (la usan los cinco roles).

**Controlador:** el ViewModel que le da el estado. Un mismo ViewModel puede servir a varias pantallas del mismo flujo si comparten datos. **Perfil es la excepción:** sus 6 pantallas tienen un ViewModel propio cada una y el perfil de demostración se comparte por un `ProfileRepository` (acordado el 21/10, sección 9).

**Estado** (lo que pide NAV01):
- **Funcional:** todo lo que muestra sale de Retrofit o de Room.
- **Demostración:** no cumple ningún RF. Si igual muestra algún dato que sale de Room, sigue siendo demostración y se aclara entre paréntesis en el README (punto 6: "funciones simuladas documentadas").

**La regla para decidir, y se aplica igual a las 37:** una pantalla es **Funcional cuando ella cumple un requisito RF**. Lo accesorio puede ser de ejemplo y no le cambia el estado. Por eso `Nutrition / 01` es Funcional (cumple el total diario de RF04) aunque su anillo de progreso sea de ejemplo, y `Home / 01` es Demostración (no cumple ningún RF) aunque lea dos datos reales de Room.

Son las dos palabras de la consigna. No se inventa una tercera categoría: el profe ya clasificó por área, y declarar "demostración" no resta puntos (*"las funciones simuladas bien documentadas no se penalizan fuera del núcleo obligatorio"*).

El estado previsto lo confirma el rol dueño del flujo al implementar la pantalla; si cambia, se corrige acá y en el README.

**Área:** la de la tabla de cobertura mínima de la consigna. La define el profe, así que no se discute ni cambia cuando el grupo reparta el trabajo.

---

## 3. Acceso (2 pantallas)

| # | Frame de Figma | Ruta | Tipo | Controlador | Estado | Área |
|---|---|---|---|---|---|---|
| 1 | Login - 01 — SignIn | `sign_in` | Acceso | `AuthViewModel` | Demostración | Acceso |
| 2 | Login - 02 — SignUp | `sign_up` | Acceso | `AuthViewModel` | Demostración | Acceso |

- **Entrada:** el `NavHost` arranca en `access_graph`, que a su vez arranca en `sign_in`. El `startDestination` del `NavHost` tiene que ser un **hijo directo** del grafo raíz: poner `sign_in` directo revienta en cuanto abrís la app.
- **Salida:** al entrar se navega a `home_dashboard` con `popUpTo(access_graph) { inclusive = true }`, así el botón atrás del sistema no vuelve al login (NAV02). Se saca **todo el grafo** y no solo `sign_in`: desde SignUp el login queda debajo, y además se libera el `AuthViewModel`, que es del grafo (acordado el 22/10).
- Sin Firebase es un acceso de demostración: valida el formulario y entra. Firebase es extra; si se hace, esta fila pasa a **Funcional**.
- Ojo visual: acá el botón es **lima sobre tarjeta clara**, distinto de la lámina de componentes.

### A dónde lleva cada control (NAV03)

Leído de los frames el 18/10. Lo que dice *a confirmar* es lectura mía del dibujo, no un dato del Figma: el archivo **no tiene prototipo armado** (el botón ▷ pasa los 41 frames como diapositivas), así que las flechas las definimos nosotros.

| Desde | Control | A dónde lleva |
|---|---|---|
| Login - 01 — SignIn | "Sign In →" | `home_dashboard`, **sacando el login de la pila** |
| | "Sign up here" | `sign_up` |
| | Flecha ‹ (arriba izq.) | **A ningún lado**: es la primera pantalla. Se dibuja sin acción |
| | Apple / Google / biometría | Aviso de modo demostración (salvo que se haga Firebase) |
| | "Forgot your password?" | Aviso de modo demostración |
| | Selector "EN ⌄" (las dos pantallas) | Aviso de modo demostración. **El idioma lo elige Android solo** según el teléfono (`values/` y `values-es/`), no este control |
| Login - 02 — SignUp | "Sign Up →" | `home_dashboard`, sacando el login de la pila |
| | "Sign In" (abajo) | `sign_in` |
| | "Recover" / "Password help?" | Aviso de modo demostración |

## 4. Inicio (2 pantallas)

| # | Frame de Figma | Ruta | Tipo | Controlador | Estado | Área |
|---|---|---|---|---|---|---|
| 3 | Home / 01 — Dashboard | `home_dashboard` | Con barra | `HomeViewModel` | Demostración | Home y Profile |
| 4 | Home / 02 — Notifications | `notifications` | Con barra | `NotificationsViewModel` | Demostración | Home y Profile |

- **Entrada:** `home_dashboard` es la pantalla base de la pestaña Home y el destino al que vuelven las demás pestañas (`popUpTo("home_dashboard") { saveState = true }`).
- **Qué es real en Home / 01:** las calorías, proteínas y carbohidratos de hoy salen de la tabla de comidas (Room) y el nombre, del perfil de demostración. El entrenamiento recomendado, los pasos, la distancia y los objetivos diarios son de ejemplo (`HomeDemoData`, con la forma del modelo `Workout`); el recomendado pasa a salir de la caché de Workout cuando cierre la tarea j (propuesto el 22/10 en el PR de Home; a confirmar por el grupo).
- Home / 01 es más alta que 850 dp: lleva scroll (UI03).
- Desde el panel del coach de Home / 01 se abre el Asistente VORA (`voice`). *A confirmar con el frame.*

### A dónde lleva cada control (NAV03)

Leído de los frames el 18/10. Lo que dice *a confirmar* es lectura mía del dibujo, no un dato del Figma: el archivo **no tiene prototipo armado** (el botón ▷ pasa los 41 frames como diapositivas), así que las flechas las definimos nosotros.

| Desde | Control | A dónde lleva |
|---|---|---|
| Home / 01 | Campana (arriba der.) | `notifications` |
| | **"Start voice session"** (lima) | `voice` · **cruza de flujo** |
| | "Start my workout" (chip) | `workout_dashboard` · cruza |
| | "Log breakfast" (chip) | `add_food` · cruza |
| | "Start workout" (tarjeta del recomendado) | `workout_details/{workoutId}` · cruza |
| | Sección "Today's Overview" (tarjetas del anillo y de nutrición) | `todays_overview` · cruza · *entrada secundaria*: abre la pestaña Progress como la barra |
| | Barra inferior | Las otras 4 pestañas |
| Home / 02 | "Mark all as read" | No navega |
| | "Upper Body Strength is ready" | `workout_details/{workoutId}` |
| | "Protein target milestone alert" | `nutrition_dashboard` |
| | "Your weekly health review is ready" | `weekly_review` |

## 5. Asistente VORA (6 pantallas)

| # | Frame de Figma | Ruta | Tipo | Controlador | Estado | Área |
|---|---|---|---|---|---|---|
| 5 | Voice / 01 — Idle | `voice` (estado `Idle`) | Encima | `VoiceViewModel` | Demostración | Coach y Progress |
| 6 | Voice / 02 — Listening | `voice` (estado `Listening`) | Encima | `VoiceViewModel` | Demostración | Coach y Progress |
| 7 | Voice / 03 — Processing | `voice` (estado `Processing`) | Encima | `VoiceViewModel` | Demostración | Coach y Progress |
| 8 | Voice / 04 — Speaking | `voice` (estado `Speaking`) | Encima | `VoiceViewModel` | Demostración | Coach y Progress |
| 9 | Voice / 05 — Conversation | `conversation` | Encima | `ConversationViewModel` | Demostración | Coach y Progress |
| 10 | Voice / 06 — Error | `voice` (estado `Error`) | Encima | `VoiceViewModel` | Demostración | Coach y Progress |

- **5 frames, 1 ruta.** Idle, Listening, Processing, Speaking y Error comparten layout: son estados de `VoiceScreen`, no destinos distintos. Un cambio de estado no es navegación. Ficha: `docs/decisiones/002-voice-una-pantalla-con-estados.md`.
- Conversation sí es una ruta aparte: cambia el layout (lista de mensajes).
- **NAV05:** se recorre escucha, procesamiento, respuesta y error con botones de demostración. **No se pide permiso de micrófono.**
- Si se hace EXT01 (coach por Retrofit con historial en Room), `conversation` pasa a **Funcional**.

### A dónde lleva cada control (NAV03)

Leído de los frames el 18/10. Lo que dice *a confirmar* es lectura mía del dibujo, no un dato del Figma: el archivo **no tiene prototipo armado** (el botón ▷ pasa los 41 frames como diapositivas), así que las flechas las definimos nosotros.

Los 5 estados de `voice` no son navegación: son cambios de `VoiceUiState` en la misma pantalla (ficha 002).

| Desde | Control | A dónde lleva |
|---|---|---|
| Idle | "TAP TO TALK" | Estado `Listening` |
| | Chips "Start my workout", "Log my breakfast", "What should I eat tonight?", "How am I doing?" | Estado `Listening` con esa pregunta cargada (demostración) |
| | "Type instead" | `conversation` |
| | Tarjeta "LAST CONVERSATION" | `conversation` |
| | Flecha ‹ | `popBackStack()` |
| Listening | Botón de micrófono | Estado `Processing` |
| | "Cancel" | Estado `Idle` |
| Processing | Termina solo | Estado `Speaking` |
| | "Cancel" | Estado `Idle` |
| Speaking | "Tap to Interrupt" | Estado `Idle` |
| | Repetir / pausar | No navega |
| Error | "Try again" | Estado `Idle` |
| | "Type instead" | `conversation` |
| | **"Allow Access"** | Estado `Idle`. **NO pide el permiso real** (NAV05) |
| | "Not Now" | Estado `Idle` |
| Conversation | Enviar un mensaje | No navega |
| | Flecha ‹ | `popBackStack()` |

## 6. Entrenamiento (7 pantallas)

| # | Frame de Figma | Ruta | Tipo | Controlador | Estado | Área |
|---|---|---|---|---|---|---|
| 11 | Workout / 01 — Dashboard | `workout_dashboard` | Con barra | `WorkoutsViewModel` | Funcional | Workout |
| 12 | Workout / 02 — Weekly Plan | `weekly_plan` | Con barra | `WorkoutsViewModel` | Funcional | Workout |
| 13 | Workout / 03 — Workout Details | `workout_details/{workoutId}` | Encima | `WorkoutDetailViewModel` | Funcional | Workout |
| 14 | Workout / 04 — Pre Workout | `pre_workout/{workoutId}` | Encima | `WorkoutDetailViewModel` | Demostración | Workout |
| 15 | Workout / 05 — Active Workout | `active_workout/{workoutId}` | Encima | `ActiveWorkoutViewModel` | Demostración | Workout |
| 16 | Workout / 06 — Exercise Details | `exercise_details/{workoutId}/{exerciseId}` | Encima | `WorkoutDetailViewModel` | Funcional | Workout |
| 17 | Workout / 07 — Workout Complete | `workout_complete/{workoutId}` | Encima | `ActiveWorkoutViewModel` | Demostración | Workout |

- **RF01 y RF02:** 01 muestra `GET /v1/workouts` con caché en Room; 03 muestra `GET /v1/workouts/{workoutId}` con caché.
- **NAV04:** el `workoutId` viaja en la ruta y se lee con `SavedStateHandle`. Cada entrenamiento abre **su propio** detalle; el id se arrastra por 04, 05, 06 y 07, así la selección se mantiene en todo el recorrido.
- **Qué es real:** 02 lista entrenamientos del catálogo real, pero el reparto semanal es de ejemplo. 04 y 06 muestran el detalle ya cargado; lo simulado en 04 y 05 es la sesión (cronómetro, series). 07 es un resumen simulado.
- **RF05:** 01 y 03 tienen las cuatro caras (cargando, contenido, vacío, error con reintento). Las capturas van a `docs/evidencia/estados/`.

### A dónde lleva cada control (NAV03)

Leído de los frames el 18/10. Lo que dice *a confirmar* es lectura mía del dibujo, no un dato del Figma: el archivo **no tiene prototipo armado** (el botón ▷ pasa los 41 frames como diapositivas), así que las flechas las definimos nosotros.

| Desde | Control | A dónde lleva |
|---|---|---|
| 01 Dashboard | Calendario (arriba der.) | `weekly_plan` · el frame se llama "Your Plan" y es el calendario |
| | "Start Workout" (lima, panel verde) | `pre_workout/{workoutId}` |
| | "Details" | `workout_details/{workoutId}` |
| | "View progress" | `workout_progress` · cruza |
| 02 Weekly Plan | "TODAY'S WORKOUT · READY" | `workout_details/{workoutId}` |
| | Otro día de la semana | `workout_details/{workoutId}` de ese día |
| | "I'm tired today" / "Move Wed workout to Fri" | Aviso de modo demostración |
| 03 Workout Details | "Start Workout" | `pre_workout/{workoutId}` |
| | Un ejercicio de la lista (01 Bench Press…) | `exercise_details/{workoutId}/{exerciseId}` |
| | "Edit workout" | Aviso de modo demostración |
| | Flecha ‹ | `popBackStack()` |
| 04 Pre Workout | "Start Workout" | `active_workout/{workoutId}` |
| | X (cerrar) | `popBackStack()` |
| | Micrófono "Need anything?" | `voice` · cruza |
| 05 Active Workout | "Complete Set" | **No navega**: pasa a la serie siguiente |
| | "Finish Workout" (arriba der.) | `workout_complete/{workoutId}` |
| | "Skip" / "+30s" del descanso | No navega |
| 06 Exercise Details | "Back to Workout" | `popBackStack()` → `active_workout` |
| | "?" (ayuda) | Aviso de modo demostración |
| | "Ask Vora: Explain grip variations" | `voice` · cruza |
| 07 Workout Complete | "Done" | `workout_dashboard`, limpiando la sesión de la pila |
| | "Great / Good / Tough / Too Easy" | No navega |

## 7. Nutrición (8 pantallas)

| # | Frame de Figma | Ruta | Tipo | Controlador | Estado | Área |
|---|---|---|---|---|---|---|
| 18 | Nutrition / 01 — Dashboard | `nutrition_dashboard` | Con barra | `NutritionViewModel` | Funcional | Nutrition |
| 19 | Nutrition / 02 — Today's Meals | `todays_meals` | Con barra | `NutritionViewModel` | Funcional | Nutrition |
| 20 | Nutrition / 03 — Add Food | `add_food` | Con barra | `AddFoodViewModel` | Demostración | Nutrition |
| 21 | Nutrition / 04 — Voice Food Logging | `voice_food_logging` | Encima | `FoodCaptureViewModel` | Demostración | Nutrition |
| 22 | Nutrition / 05 — Food Analysis | `food_analysis` | Encima | `FoodCaptureViewModel` | Demostración | Nutrition |
| 23 | Nutrition / 06 — Photo Analysis | `photo_analysis` | Con barra | `FoodCaptureViewModel` | Demostración | Nutrition |
| 24 | Nutrition / 07 — Food Confirmation | `food_confirmation?mealId={mealId}` | Con barra | `FoodCaptureViewModel` | Funcional | Nutrition |
| 25 | Nutrition / 08 — Meal Details | `meal_details/{mealId}` | Con barra | `MealDetailViewModel` | Funcional | Nutrition |

- **Las que sorprenden:** 03, 06, 07 y 08 **sí** tienen barra; 04 y 05 **no**. Es lo que muestra el Figma y se respeta.
- **RF03 y RF04:** 03 carga a mano con validación; 02 lista por fecha y elimina con confirmación; 01 muestra el total diario con una consulta (`SUM`); 08 es el detalle de la comida elegida.
- **Nutrition / 07:** los valores llegan del análisis simulado (04, 05 o 06), pero **el guardado es real** (Room).
- **NAV05:** 04, 05 y 06 son estados de demostración. **No se pide permiso de cámara ni de micrófono.**
- **NAV04:** el `mealId` viaja en la ruta (08).
- `FoodCaptureViewModel` lo comparten 04, 05, 06 y 07: se obtiene con el `NavBackStackEntry` del grafo (`hiltViewModel(navController.getBackStackEntry(Rutas.NUTRITION_GRAPH))`) para que el borrador de la comida sobreviva el paso de una pantalla a la siguiente. *A acordar en la tarea VC-b con la zona de Nutrition.* **Ojo:** colgarlo del grafo entero lo deja vivo mientras haya cualquier pantalla de Nutrition en la pila, así que una captura cancelada reaparece en la siguiente. Conviene acotarlo al subflujo de captura (04 a 07) o limpiar el borrador al salir de `food_confirmation`.

### A dónde lleva cada control (NAV03)

Leído de los frames el 18/10. Lo que dice *a confirmar* es lectura mía del dibujo, no un dato del Figma: el archivo **no tiene prototipo armado** (el botón ▷ pasa los 41 frames como diapositivas), así que las flechas las definimos nosotros.

| Desde | Control | A dónde lleva |
|---|---|---|
| 01 Dashboard | "Voice" (chip lima) | `voice_food_logging` |
| | "Photo" | `photo_analysis` |
| | "Search" | EXT02; si no se hace, aviso de modo demostración |
| | "View History" | `todays_meals` |
| | Una comida (Breakfast, Lunch, Snack) | `meal_details/{mealId}` |
| | "+ Add" en Dinner | `add_food` |
| 02 Today's Meals | "+ Add Dinner" | `add_food` |
| | Una comida | `meal_details/{mealId}` |
| 03 Add Food | "Start Voice Logging" | `voice_food_logging` |
| | "Snap a Photo" | `photo_analysis` |
| | "Search Database" | EXT02 |
| | **"Log Manually"** | `food_confirmation?mealId=-1` **vacío**, para completarlo a mano. El formulario de RF03 no tiene frame propio |
| | Flecha ‹ | `popBackStack()` |
| 04 Voice Food Logging | "Done Speaking" | `food_analysis` |
| | "Type instead" | El mismo formulario de carga manual |
| | X (cerrar) | `popBackStack()` |
| 05 Food Analysis | Termina solo | `food_confirmation?mealId=-1` (comida nueva) |
| | "Cancel analysis" | `popBackStack()` |
| 06 Photo Analysis | "Review & Add" | `food_confirmation?mealId=-1` (comida nueva) |
| | "Retake Photo" | Vuelve a tomar la foto (demostración) |
| 07 Food Confirmation | "Add to [comida]" | **Guarda en Room** y vuelve a `nutrition_dashboard` |
| | "Save without logging" | Aviso de modo demostración |
| | "−" / "+" de cantidad, "Remove" | No navega: cambia el borrador |
| | "+ Add Ingredient" | No navega |
| 08 Meal Details | "Edit Meal" | `food_confirmation?mealId={mealId}` · **edita**, no crea |
| | "Duplicate Meal" | Guarda una copia y vuelve |
| | Eliminar | **Diálogo de confirmación** (RF04) y después vuelve |

## 8. Progreso (6 pantallas)

| # | Frame de Figma | Ruta | Tipo | Controlador | Estado | Área |
|---|---|---|---|---|---|---|
| 26 | Progress / 01 — Today's Overview | `todays_overview` | Con barra | `ProgressViewModel` | Demostración | Coach y Progress |
| 27 | Progress / 02 — Dashboard | `progress_dashboard` | Con barra | `ProgressViewModel` | Demostración | Coach y Progress |
| 28 | Progress / 03 — Workout Progress | `workout_progress` | Con barra | `ProgressViewModel` | Demostración | Coach y Progress |
| 29 | Progress / 04 — Nutrition Progress | `nutrition_progress` | Con barra | `ProgressViewModel` | Demostración | Coach y Progress |
| 30 | Progress / 05 — Health Trends | `health_trends` | Con barra | `ProgressViewModel` | Demostración | Coach y Progress |
| 31 | Progress / 06 — Weekly Review | `weekly_review` | Encima | `ProgressViewModel` | Demostración | Coach y Progress |

- **Pantalla base de la pestaña Progress: `todays_overview`.** Confirmado el 22/10 con la respuesta a la duda #2: la pestaña aterriza en el frame 01 y desde ahí "Start Workout" lleva al 02, que es el que tiene los links a 03, 04, 05 y 06. Ficha: `docs/decisiones/005-toda-pantalla-alcanzable.md`.
- Los datos de demostración usan **la misma forma que los modelos** de Workout y Nutrition, para poder enchufarlos a Room si sobra tiempo.
- Progress / 02 es más alta que 850 dp: lleva scroll (UI03).

### A dónde lleva cada control (NAV03)

Leído de los frames el 18/10. Lo que dice *a confirmar* es lectura mía del dibujo, no un dato del Figma: el archivo **no tiene prototipo armado** (el botón ▷ pasa los 41 frames como diapositivas), así que las flechas las definimos nosotros.

| Desde | Control | A dónde lleva |
|---|---|---|
| 01 Today's Overview | "Start Workout" | `progress_dashboard` · **no es el destino "lógico"**, es el que hace alcanzables 02 a 06 (duda #2) |
| | Fila "Workout Progress" | `workout_progress` · *a confirmar* |
| | Fila "Nutrition Target" | `nutrition_progress` · *a confirmar* |
| | Fila "Activity Target" | `health_trends` · *a confirmar* |
| 02 Dashboard | "Workout · View >" | `workout_progress` |
| | "Nutrition · View >" | `nutrition_progress` |
| | "Activity · View >" | `health_trends` |
| | "See weekly review →" | `weekly_review` |
| | Week / Month / 3 Months | No navega: filtra en la misma pantalla |
| 03, 04 y 05 | Flecha ‹ | `popBackStack()` |
| | "···" (arriba der.) | Aviso de modo demostración |
| 06 Weekly Review | "Ask VORA" (lima) | `voice` · cruza |
| | "Done" | `popBackStack()` |
| | "Get dinner ideas →" | `voice` · cruza |
| | "View next week's plan →" | `weekly_plan` · cruza |

## 9. Perfil (6 pantallas)

| # | Frame de Figma | Ruta | Tipo | Controlador | Estado | Área |
|---|---|---|---|---|---|---|
| 32 | Profile / 01 — Dashboard | `profile_dashboard` | Con barra | `ProfileDashboardViewModel` | Demostración | Home y Profile |
| 33 | Profile / 02 — Edit Profile | `edit_profile` | Encima | `EditProfileViewModel` | Demostración | Home y Profile |
| 34 | Profile / 03 — Goals & Preferences | `goals_preferences` | Encima | `GoalsPreferencesViewModel` | Demostración | Home y Profile |
| 35 | Profile / 04 — Nutrition Preferences | `nutrition_preferences` | Encima | `NutritionPreferencesViewModel` | Demostración | Home y Profile |
| 36 | Profile / 05 — Voice & AI Settings | `voice_ai_settings` | Encima | `VoiceAiSettingsViewModel` | Demostración | Home y Profile |
| 37 | Profile / 06 — Settings | `settings` | Encima | `SettingsViewModel` | Demostración | Home y Profile |

- Perfil de demostración (sin Firebase no se exige login real).
- **Un ViewModel por pantalla** (acordado el 21/10 con Zaki, tarea VC-q): las 6 pantallas de Perfil no comparten controlador. Es el único flujo con este criterio; en los demás, varias pantallas comparten uno cuando comparten datos (sección 2).
- **El perfil de demostración se comparte por un `ProfileRepository`**, inyectado por constructor en los 6 ViewModels. Sin eso, lo que se edita en `02`, `03` o `04` no se ve al volver a `01`. Lo arma la zona de Perfil.
- Profile / 06 es más alta que 850 dp: lleva scroll (UI03).
- **NAV03:** cada opción de Settings hace lo que muestra el Figma (abre pantalla, abre diálogo o cambia un ajuste). Ninguna queda muerta. Lo que abre cada una se completa al maquetar el frame (sección 12).

---

### A dónde lleva cada control (NAV03)

Leído de los frames el 18/10. Lo que dice *a confirmar* es lectura mía del dibujo, no un dato del Figma: el archivo **no tiene prototipo armado** (el botón ▷ pasa los 41 frames como diapositivas), así que las flechas las definimos nosotros.

| Desde | Control | A dónde lleva |
|---|---|---|
| 01 Dashboard | Engranaje (arriba der.) | `settings` |
| | "Edit profile" | `edit_profile` |
| | "Review preferences" (panel VORA) | `voice_ai_settings` |
| | "Your Goals" | `goals_preferences` |
| | "Your Preferences" | `nutrition_preferences` |
| | "Connected Data · Synced" | **Sin frame** → aviso de modo demostración |
| | "Account" | **Sin frame** → aviso de modo demostración |
| 02 Edit Profile | "Save changes" | `popBackStack()` → 01 |
| | **"Change photo"** | Aviso de modo demostración. **No abre la cámara ni la galería** (NAV05) |
| 03 Goals & Preferences | "Save preferences" | `popBackStack()` |
| 04 Nutrition Preferences | "Save preferences" | `popBackStack()` |
| 05 Voice & AI Settings | "Preview" de la voz | Aviso de modo demostración |
| | "Privacy & permissions" | `settings` |
| 06 Settings | Fila "Alex Morgan" | `edit_profile` |
| | "VORA Premium" y "Manage account" | **Sin frame** → aviso de modo demostración |
| | Notifications, Units, Appearance | **Sin frame** → aviso de modo demostración |
| | "Health & Activity" | **Sin frame** → aviso de modo demostración |
| | **"Permissions & Data Controls"** | Aviso de modo demostración. **No abre los ajustes del sistema ni pide permisos** (NAV05) |
| | Help Centre, Contact Support, Privacy Policy | **Sin frame** → aviso de modo demostración |
| | **"Log out"** | `sign_in`, **vaciando toda la pila** |

**`05 Voice & AI Settings` ya tiene entrada confirmada.** Era la única pantalla a la que no le encontré una entrada clara en el Figma, y quedó colgada de "Review preferences" (panel VORA de `01 Dashboard`). Zaki la confirmó el 21/10 al maquetar `01 Dashboard` (tarea VC-q). **Una pantalla sin entrada rompe NAV01**, así que esta fila no se cambia sin darle antes otra entrada.

## 10. Las 16 con barra inferior

Van dentro del `Scaffold` y **no dibujan la barra**. La barra se arma **una sola vez** en `MainActivity`: es un componente compartido que las pantallas heredan, como un template. Duplicarla en cada pantalla es el error que marcó el profe el 14/10, aunque visualmente quede igual.

| Pestaña | Pantallas con barra |
|---|---|
| Home | `home_dashboard`, `notifications` |
| Workout | `workout_dashboard`, `weekly_plan` |
| Nutrition | `nutrition_dashboard`, `todays_meals`, `add_food`, `photo_analysis`, `food_confirmation`, `meal_details/{mealId}` |
| Progress | `todays_overview`, `progress_dashboard`, `workout_progress`, `nutrition_progress`, `health_trends` |
| Profile | `profile_dashboard` |

Son 2 + 2 + 6 + 5 + 1 = **16**.

```kotlin
// ui/navigation/Rutas.kt  (lo escribe Fiore en la tarea VC-e)
val RUTAS_CON_BARRA = setOf(
    Rutas.HOME_DASHBOARD, Rutas.NOTIFICATIONS,
    Rutas.WORKOUT_DASHBOARD, Rutas.WEEKLY_PLAN,
    Rutas.NUTRITION_DASHBOARD, Rutas.TODAYS_MEALS, Rutas.ADD_FOOD,
    Rutas.PHOTO_ANALYSIS, Rutas.FOOD_CONFIRMATION, Rutas.MEAL_DETAILS,
    Rutas.TODAYS_OVERVIEW, Rutas.PROGRESS_DASHBOARD, Rutas.WORKOUT_PROGRESS,
    Rutas.NUTRITION_PROGRESS, Rutas.HEALTH_TRENDS,
    Rutas.PROFILE_DASHBOARD,
)
```

Ojo: `MEAL_DETAILS` y `FOOD_CONFIRMATION` son rutas con argumento. En `RUTAS_CON_BARRA` va el **patrón** de la ruta (`meal_details/{mealId}`, `food_confirmation?mealId={mealId}`), que es lo que devuelve `destination.route`, no la ruta ya armada con el id.

## 11. Las 19 que se abren encima

Sin barra. Llevan `VoraTopBar` con volver o cerrar, se abren con `navigate()` y vuelven con `popBackStack()`. El botón atrás del sistema hace lo mismo que el de la pantalla (NAV02).

| Flujo | Pantallas |
|---|---|
| Voice (6) | `voice` (Idle, Listening, Processing, Speaking, Error) y `conversation` |
| Workout (5) | `workout_details`, `pre_workout`, `active_workout`, `exercise_details`, `workout_complete` |
| Nutrition (2) | `voice_food_logging`, `food_analysis` |
| Progress (1) | `weekly_review` |
| Profile (5) | `edit_profile`, `goals_preferences`, `nutrition_preferences`, `voice_ai_settings`, `settings` |

Son 6 + 5 + 2 + 1 + 5 = **19**.

## 12. Las 2 de acceso

`sign_in` y `sign_up`. Antes de entrar a la app, sin barra. El `NavHost` arranca en `access_graph` (que arranca en `sign_in`), y el login se saca de la pila al entrar.

---

## 13. Pantallas que se tocan entre roles

Son las que **no puede cerrar una sola persona**: necesitan algo de otra zona. Antes de empezarlas hay que acordar la firma o el modelo, si no se choca al unir. Van por zona, no por nombre, porque el reparto todavía no está cerrado.

| Pantalla | Zona que la maqueta | Zona que aporta | Qué hay que acordar |
|---|---|---|---|
| Home / 01 — Dashboard | Home y Profile | Workout (caché de entrenamientos) y Nutrition (total de calorías) | Qué expone cada repositorio para el resumen del día: el entrenamiento recomendado y el total de hoy. `HomeViewModel` se lo pide a los dos repositorios, nunca a los DAO |
| Las 16 con barra | Navegación e inventario (la enchufa) | Tema y componentes (la dibuja) | Firma de `VoraBottomBar(selected: VoraTab?, onTab: (VoraTab) -> Unit)` y el enum `VoraTab` con su ruta de grafo. Se entrega sin estado y se enchufa en el `Scaffold` |
| Todas (encabezado) | Cada zona la suya | Tema y componentes | Firma de `VoraTopBar` (título, acción de volver o cerrar). Se usa con datos propios; **no se copia su código** |
| Nutrition / 04, 05, 06 y 07 | Nutrition | Coach y Progress (simulación de voz y análisis) | Los estados de demostración del análisis (NAV05 cruza las dos zonas) y qué borrador le deja el análisis a `food_confirmation` |
| Voice / 05 — Conversation | Coach y Progress | Workout (solo si se hace EXT01: Retrofit y Room) | Únicamente si se encara el extra: endpoint del coach y tabla del historial |
| Progress / 03 y 04 | Coach y Progress | Workout y Nutrition (forma de los modelos) | Que los datos de ejemplo usen los modelos `Workout` y `Meal`, para poder conectarlos después si sobra tiempo |
| Profile / 06 — Settings | Home y Profile | Navegación e inventario | Qué opción abre pantalla, cuál abre diálogo y cuál solo cambia un ajuste (NAV03) |
| De Login 01 a Home | Acceso | Navegación e inventario | El `popUpTo(access_graph) { inclusive = true }` lo pone el grafo de acceso (`entrarAHome()`) |

---

## 14. Los 7 grafos y las 5 pestañas

Cada flujo en su archivo, así cada rol trabaja en el suyo y casi no hay conflictos al unir:

| Grafo | Archivo | Pantalla base | Pestaña |
|---|---|---|---|
| `access_graph` | `AccessNavGraph.kt` | `sign_in` | (ninguna) |
| `home_graph` | `HomeNavGraph.kt` | `home_dashboard` | Home |
| `workout_graph` | `WorkoutNavGraph.kt` | `workout_dashboard` | Workout |
| `nutrition_graph` | `NutritionNavGraph.kt` | `nutrition_dashboard` | Nutrition |
| `progress_graph` | `ProgressNavGraph.kt` | `progress_dashboard` | Progress |
| `profile_graph` | `ProfileNavGraph.kt` | `profile_dashboard` | Profile |
| `voice_graph` | `VoiceNavGraph.kt` | `voice` | (ninguna) |

Reglas de la zona (son de Navegación e inventario; se tocan solo por pull request de Fiore):
- `MainActivity.kt`, `VoraBottomBar` enchufado en el `Scaffold`, `Rutas.kt` y `RUTAS_CON_BARRA`.
- **Cambio de pestaña:** `popUpTo("home_dashboard") { saveState = true }`, `launchSingleTop = true`, `restoreState = true`. **No** `popUpTo(startDestination)` ni `findStartDestination()`: el inicio del `NavHost` es el login y se saca de la pila al entrar, así que ese `popUpTo` no encontraría nada y cada toque de pestaña apilaría pantallas.
- **Pestaña activa:** se calcula con la jerarquía del destino, no comparando la ruta: `VoraTab.entries.firstOrNull { tab -> destino?.hierarchy?.any { it.route == tab.graph } == true }`. Comparando solo la ruta, la pestaña se apagaría en Weekly Plan o en cualquier pantalla que no sea la primera del flujo.

---

## 15. Rutas (propuesta para la tarea VC-b)

```kotlin
// ui/navigation/Rutas.kt
object Rutas {
    // grafos
    const val ACCESS_GRAPH = "access_graph"
    const val HOME_GRAPH = "home_graph"
    const val WORKOUT_GRAPH = "workout_graph"
    const val NUTRITION_GRAPH = "nutrition_graph"
    const val PROGRESS_GRAPH = "progress_graph"
    const val PROFILE_GRAPH = "profile_graph"
    const val VOICE_GRAPH = "voice_graph"

    // acceso
    const val SIGN_IN = "sign_in"
    const val SIGN_UP = "sign_up"

    // inicio
    const val HOME_DASHBOARD = "home_dashboard"
    const val NOTIFICATIONS = "notifications"

    // asistente VORA
    const val VOICE = "voice"
    const val CONVERSATION = "conversation"

    // entrenamiento
    const val WORKOUT_DASHBOARD = "workout_dashboard"
    const val WEEKLY_PLAN = "weekly_plan"
    const val ARG_WORKOUT_ID = "workoutId"
    const val ARG_EXERCISE_ID = "exerciseId"
    const val WORKOUT_DETAILS = "workout_details/{$ARG_WORKOUT_ID}"
    const val PRE_WORKOUT = "pre_workout/{$ARG_WORKOUT_ID}"
    const val ACTIVE_WORKOUT = "active_workout/{$ARG_WORKOUT_ID}"
    const val EXERCISE_DETAILS = "exercise_details/{$ARG_WORKOUT_ID}/{$ARG_EXERCISE_ID}"
    const val WORKOUT_COMPLETE = "workout_complete/{$ARG_WORKOUT_ID}"
    fun workoutDetails(workoutId: String) = "workout_details/$workoutId"
    fun preWorkout(workoutId: String) = "pre_workout/$workoutId"
    fun activeWorkout(workoutId: String) = "active_workout/$workoutId"
    fun exerciseDetails(workoutId: String, exerciseId: String) = "exercise_details/$workoutId/$exerciseId"
    fun workoutComplete(workoutId: String) = "workout_complete/$workoutId"

    // nutrición
    const val NUTRITION_DASHBOARD = "nutrition_dashboard"
    const val TODAYS_MEALS = "todays_meals"
    const val ADD_FOOD = "add_food"
    const val VOICE_FOOD_LOGGING = "voice_food_logging"
    const val FOOD_ANALYSIS = "food_analysis"
    const val PHOTO_ANALYSIS = "photo_analysis"
    /** mealId = -1 significa "comida nueva"; con id, se edita una ya guardada (NAV04). */
    const val FOOD_CONFIRMATION = "food_confirmation?$ARG_MEAL_ID={$ARG_MEAL_ID}"
    fun foodConfirmation(mealId: Long = -1L) = "food_confirmation?$ARG_MEAL_ID=$mealId"
    const val ARG_MEAL_ID = "mealId"
    const val MEAL_DETAILS = "meal_details/{$ARG_MEAL_ID}"
    fun mealDetails(id: Long) = "meal_details/$id"

    // progreso
    const val TODAYS_OVERVIEW = "todays_overview"
    const val PROGRESS_DASHBOARD = "progress_dashboard"
    const val WORKOUT_PROGRESS = "workout_progress"
    const val NUTRITION_PROGRESS = "nutrition_progress"
    const val HEALTH_TRENDS = "health_trends"
    const val WEEKLY_REVIEW = "weekly_review"

    // perfil
    const val PROFILE_DASHBOARD = "profile_dashboard"
    const val EDIT_PROFILE = "edit_profile"
    const val GOALS_PREFERENCES = "goals_preferences"
    const val NUTRITION_PREFERENCES = "nutrition_preferences"
    const val VOICE_AI_SETTINGS = "voice_ai_settings"
    const val SETTINGS = "settings"
}
```

Criterios: todo en `snake_case`, el nombre sale del frame, el argumento va en la ruta (NAV04) y las rutas se usan **siempre** por constante, nunca escritas a mano en una pantalla.

---

## 16. Control del inventario

### Se llega desde: la tabla al revés

Las tablas "A dónde lleva cada control" dicen **hacia dónde sale** cada pantalla. Esta dice **por dónde entra**, que es lo que NAV01 manda comprobar: *"todas accesibles desde el recorrido previsto"*.

**Cómo se usa:** si una fila queda vacía, esa pantalla no se alcanza y NAV01 se rompe. Es el control más barato que tiene este archivo.

| Ruta | Se llega desde |
|---|---|
| `sign_in` | Arranque de la app (`startDestination`) · `settings` "Log out" |
| `sign_up` | `sign_in` "Sign up here" |
| `home_dashboard` | `sign_in` "Sign In →" · `sign_up` "Sign Up →" · pestaña Home |
| `notifications` | `home_dashboard` campana |
| `voice` | `home_dashboard` "Start voice session" · `pre_workout` micrófono · `weekly_review` "Ask VORA" y "Get dinner ideas" · `exercise_details` "Ask Vora" |
| `conversation` | `voice` "Type instead" y tarjeta "LAST CONVERSATION" |
| `workout_dashboard` | Pestaña Workout · `home_dashboard` "Start my workout" · `workout_complete` "Done" |
| `weekly_plan` | `workout_dashboard` calendario · `weekly_review` "View next week's plan →" |
| `workout_details/{workoutId}` | `workout_dashboard` "Details" · `weekly_plan` cada día · `home_dashboard` "Start workout" · `notifications` "Upper Body Strength is ready" |
| `pre_workout/{workoutId}` | `workout_dashboard` "Start Workout" · `workout_details` "Start Workout" |
| `active_workout/{workoutId}` | `pre_workout` "Start Workout" |
| `exercise_details/{workoutId}/{exerciseId}` | `workout_details` un ejercicio de la lista |
| `workout_complete/{workoutId}` | `active_workout` "Finish Workout" |
| `nutrition_dashboard` | Pestaña Nutrition · `notifications` "Protein target milestone alert" · `food_confirmation` al guardar |
| `todays_meals` | `nutrition_dashboard` "View History" |
| `add_food` | `nutrition_dashboard` "+ Add" en Dinner · `todays_meals` "+ Add Dinner" · `home_dashboard` "Log breakfast" |
| `voice_food_logging` | `nutrition_dashboard` "Voice" · `add_food` "Start Voice Logging" |
| `food_analysis` | `voice_food_logging` "Done Speaking" |
| `photo_analysis` | `nutrition_dashboard` "Photo" · `add_food` "Snap a Photo" |
| `food_confirmation?mealId={mealId}` | `food_analysis` al terminar · `photo_analysis` "Review & Add" · `meal_details` "Edit Meal" · `add_food` "Log Manually" |
| `meal_details/{mealId}` | `nutrition_dashboard` una comida · `todays_meals` una comida |
| `todays_overview` | **pantalla base de la pestaña Progress** · además `home_dashboard`, sección "Today's Overview" |
| `progress_dashboard` | Pestaña Progress |
| `workout_progress` | `progress_dashboard` "Workout · View >" · `workout_dashboard` "View progress" |
| `nutrition_progress` | `progress_dashboard` "Nutrition · View >" |
| `health_trends` | `progress_dashboard` "Activity · View >" |
| `weekly_review` | `progress_dashboard` "See weekly review →" · `notifications` "Your weekly health review is ready" |
| `profile_dashboard` | Pestaña Profile |
| `edit_profile` | `profile_dashboard` "Edit profile" · `settings` fila "Alex Morgan" |
| `goals_preferences` | `profile_dashboard` "Your Goals" |
| `nutrition_preferences` | `profile_dashboard` "Your Preferences" |
| `voice_ai_settings` | `profile_dashboard` "Review preferences" · confirmado el 21/10 al maquetar `Profile / 01` |
| `settings` | `profile_dashboard` engranaje · `voice_ai_settings` "Privacy & permissions" |

> **22/10 · Regla del grupo:** toda pantalla del Figma tiene que ser navegable. Si una queda
> huérfana se le asigna un control que ya exista en algún frame, con criterio, aunque el
> recorrido no sea el más lógico del producto. Nunca se inventa un botón que el frame no
> tenga. Ficha: `docs/decisiones/005-toda-pantalla-alcanzable.md`.

**No queda ninguna ⚠️.** `todays_overview` se cerró el 22/10 con la respuesta a la duda #2:
pasa a ser la pantalla base de la pestaña Progress, y "Start Workout" del frame 01 lleva al
02. La de `voice_ai_settings` se cerró el 21/10 al maquetar `Profile / 01`. Las 37 pantallas
tienen al menos una entrada.

### La cuenta

Lo que tiene que dar para que NAV01 esté cubierto:

- [ ] 37 filas de frame en las secciones 3 a 9 (2 + 2 + 6 + 7 + 8 + 6 + 6 = 37)
- [ ] 16 + 19 + 2 = 37
- [ ] Por área: 2 (Acceso) + 8 (Home y Profile) + 7 (Workout) + 8 (Nutrition) + 12 (Coach y Progress) = 37
- [ ] 33 rutas en el `NavHost`, todas en `Rutas.kt`, cada una dentro de un grafo
- [ ] `RUTAS_CON_BARRA` tiene exactamente 16 rutas y ninguna de las 19 de encima
- [ ] Ninguna fila de "Se llega desde" está vacía
- [ ] Las 37 se alcanzan recorriendo la app desde `sign_in` (se prueba en la demo, checklist de la sección 14 de la metodología)

Test del inventario (tarea VC-e, `app/src/test/`): que `RUTAS_CON_BARRA.size == 16`, que todas sus rutas existan en `Rutas` y que ninguna ruta de encima esté adentro. Es barato y evita que la barra aparezca donde no va.

---

## 17. Pendientes

1. **Tarea VC-b sin asignar** (acordar rutas, modelos y componentes). Las rutas de la sección 15 y la firma de `VoraBottomBar` salen de ahí; mientras no se acuerden, esto es una propuesta.
2. **Ya no hay entradas sin confirmar.** `Progress / 01 — Today's Overview` se resolvió el 22/10 (duda #2): es la pantalla base de la pestaña Progress y su "Start Workout" lleva a `progress_dashboard`. La de `Profile / 05 — Voice & AI Settings` quedó confirmada el 21/10 al maquetar `Profile / 01` (Zaki, tarea VC-q). Una pantalla sin entrada rompe NAV01, así que si alguna fila cambia hay que revisar esto de nuevo.
3. **El formulario de carga manual de comidas (RF03) no tiene frame propio.** Por ahora "Log Manually" abre `Nutrition / 07 — Food Confirmation` vacío, que ya tiene los campos. La consigna prohíbe inventar pantallas.
4. **Eliminar una comida (RF04) no tiene control en ningún frame.** `Nutrition / 08` tiene solo ‹, "Edit Meal" y "Duplicate Meal"; `Nutrition / 02` no tiene papelera ni swipe. El "Remove" de `07` saca un ingrediente del borrador, no borra una comida guardada. Lo define la zona de Nutrition antes de maquetar.
5. **El catálogo de entrenamientos (RF01) se muestra en `Workout / 02 — Your Plan`**, que lista tres con entrada por ítem, igual que devuelve el mock. `Workout / 01` muestra solo el destacado. **NAV04 se demuestra en `02`.**
6. **Controles sin frame de destino:** "Connected Data" y "Account" en `Profile / 01`; Notifications, Units, Appearance, Help Centre, Contact Support y Privacy Policy en `Profile / 06`. NAV03 permite que "una acción de servicio opcional dé un resultado de ejemplo o avise que es modo demostración". Lo que no pueden hacer es quedarse sin respuesta.
7. **Firebase:** si se hace, Login 01 y 02 pasan de Demostración a Funcional acá y en el README.

---

## 18. Cómo se usa esto en el README (NAV01)

El punto 5 del README ("Inventario de pantallas") se arma con **una sola tabla** de 37 filas con estas columnas: **Pantalla de Figma · Destino en la app (ruta) · Estado**. Es un recorte de las secciones 3 a 9 de este archivo, sin las columnas internas (tipo, controlador, responsable), que al profe no le hacen falta ahí.

Las 5 filas de la sección 1 que muestran algún dato real se escriben así: *"Home / 01 — Dashboard · `home_dashboard` · Demostración (el entrenamiento recomendado y el total de calorías salen de Room)"*. Eso también cubre el punto 6 del README (funciones simuladas documentadas).

**Si cambia una fila acá, cambia en el README.** Este archivo manda.
