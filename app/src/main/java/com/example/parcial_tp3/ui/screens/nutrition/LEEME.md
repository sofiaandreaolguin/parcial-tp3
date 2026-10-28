# Pantallas del flujo Nutrición

Son 8 pantallas: 01, 02, 03, 06, 07 y 08 con barra; 04 y 05 se abren encima.
La búsqueda de alimentos (EXT02) no es una pantalla: es una hoja inferior que se abre sobre 01 y 03.

## Qué archivos van acá

Por cada pantalla, cuatro archivos (`specs/CONTEXTO.md`, secciones 3 y 5):

| Archivo | Qué es |
|---|---|
| `XxxRoute.kt` | **Stateful.** Pide el ViewModel con `hiltViewModel()`, lee el estado con `collectAsStateWithLifecycle()` y pasa los eventos |
| `XxxScreen.kt` | **Stateless.** Recibe el estado y las funciones por parámetro y solo dibuja. No sabe de dónde salen los datos |
| `XxxViewModel.kt` | `@HiltViewModel`, expone un `StateFlow` |
| `XxxUiState.kt` | El estado que la pantalla muestra |

**Ojo con el ViewModel:** el controlador de cada pantalla sale de la columna *Controlador* de `specs/PANTALLAS.md`; no se decide acá. **No todas las pantallas tienen uno propio:** hay flujos donde varias comparten uno a propósito, porque comparten datos (el borrador de una comida en Nutrition, los estados de Voice). Mirá la tabla de tu flujo antes de crear el archivo.

## Las pantallas de este flujo

| Frame de Figma | Ruta | Barra | Estado |
|---|---|---|---|
| Nutrition / 01 — Dashboard | `nutrition_dashboard` | Con barra | **Funcional** |
| Nutrition / 02 — Today's Meals | `todays_meals` | Con barra | **Funcional** |
| Nutrition / 03 — Add Food | `add_food` | Con barra | Demostración |
| Nutrition / 04 — Voice Food Logging | `voice_food_logging` | Encima | Demostración |
| Nutrition / 05 — Food Analysis | `food_analysis` | Encima | Demostración: tres pasos con `delay` (~2 s) y pasa sola a `07`; "Cancel analysis", la X y atrás cortan el análisis |
| Nutrition / 06 — Photo Analysis | `photo_analysis` | Con barra | Demostración: foto de ejemplo y estimación fija (`PhotoMealDraft`), sin cámara; "Review & Add" abre `07` y "Retake Photo" muestra el aviso |
| Nutrition / 07 — Food Confirmation | `food_confirmation` | Con barra | **Funcional**: tarjetas de ingredientes como el Figma; "+ Add Ingredient" (diálogo) y "Remove" cambian la lista, y las kcal son su suma. Llega con los de 05 o 06, o vacía desde el pop-up de carga. Guarda en Room con al menos un ingrediente. La Route que navega abre la confirmación antes. −/+ y "Save without logging" muestran el aviso |
| Nutrition / 08 — Meal Details | `meal_details/{mealId}` | Con barra | **Funcional**: la comida de Room por su id (NAV04). "Ingredients" muestra una tarjeta por ingrediente (las comidas guardadas antes de los ingredientes muestran una con el nombre y la proteína); "13:12" y el consejo son fijos (demostración). "Edit Meal" muestra el aviso de demostración (la edición quedó fuera de alcance), "Duplicate Meal" guarda una copia y vuelve, y "Delete Meal" (no está en el Figma: es el control de RF04) pide confirmación y vuelve |

## Antes de empezar

- La ruta y el nombre salen de `specs/PANTALLAS.md`. **No inventes rutas**: están todas en `ui/navigation/Rutas.kt`.
- **No dibujes la barra inferior.** Se arma una sola vez en el `Scaffold` de `MainActivity` y aparece sola en las pantallas que la llevan.
- **Ningún texto escrito en el código.** Todo va en `res/values/strings.xml` y `values-es/`, y se usa con `stringResource()`. El control `scripts/reglas.sh` lo rechaza.
- **Colores, tipografías y tamaños salen del tema** (`ui/theme/`). Ninguna pantalla escribe un color a mano.
- Las pantallas más altas que el frame (unos 850 dp) llevan scroll (UI03).
- Para reemplazar la pantalla de relleno: en el grafo de tu flujo (`ui/navigation/NutritionNavGraph.kt`), cambiá `PantallaVacia(...)` por tu `XxxRoute(...)`.
- **Acá viven RF03 y RF04** (carga manual con validación, listado por fecha, eliminación con confirmación y total diario en Room).
- Que `03`, `06`, `07` y `08` lleven barra sorprende, pero está así en el Figma. Y llevar barra **no quiere decir que no tengan flecha de volver**: la tienen.
- **Dos pendientes para cerrar antes de maquetar** (`specs/PANTALLAS.md`, sección 17):
  - **"Log Manually" (RF03)** no lleva a ningún lado en el Figma. Abre el pop-up de carga (`ManualEntrySheet`) encima de 03, igual que "Type instead" en 04 y la búsqueda; "Continue" lleva a 07.
  - **Eliminar una comida (RF04) no tiene control en ningún frame.** Resuelto en `08` con un botón "Delete Meal" y un diálogo de confirmación. Ojo: el "Remove" de `07` saca un ingrediente de la comida que se está armando, no borra una comida guardada.
- `food_confirmation` siempre crea una comida nueva: la edición quedó fuera de alcance (PR 2 de ingredientes).
- **NAV05:** `04`, `05` y `06` son simuladas y **no piden micrófono ni cámara**.
