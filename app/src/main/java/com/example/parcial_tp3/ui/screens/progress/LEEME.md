# Pantallas del flujo Progreso

Son 6 pantallas: 01 a 05 con barra, 06 se abre encima.

## Qué archivos van acá

Por cada pantalla, cuatro archivos (`specs/CONTEXTO.md`, secciones 3 y 5):

| Archivo | Qué es |
|---|---|
| `XxxRoute.kt` | **Stateful.** Pide el ViewModel con `hiltViewModel()`, lee el estado con `collectAsStateWithLifecycle()` y pasa los eventos |
| `XxxScreen.kt` | **Stateless.** Recibe el estado y las funciones por parámetro y solo dibuja. No sabe de dónde salen los datos |
| `XxxViewModel.kt` | `@HiltViewModel`, expone un `StateFlow` |
| `XxxUiState.kt` | El estado que la pantalla muestra |

**Ojo con el ViewModel:** en Progress las 6 pantallas usan `ProgressViewModel` con datos fijos de `ProgressDemoData.kt`. En general, el controlador de cada pantalla sale de la columna *Controlador* de `specs/PANTALLAS.md`; no se decide acá. **No todas las pantallas tienen uno propio:** hay flujos donde varias comparten uno a propósito, porque comparten datos (el borrador de una comida en Nutrition, los estados de Voice). Mirá la tabla de tu flujo antes de crear el archivo.

## Las pantallas de este flujo

| Frame de Figma | Ruta | Barra | Estado |
|---|---|---|---|
| Progress / 01 — Today's Overview | `todays_overview` | Con barra | Demostración |
| Progress / 02 — Dashboard | `progress_dashboard` | Con barra | Demostración |
| Progress / 03 — Workout Progress | `workout_progress` | Con barra | Demostración |
| Progress / 04 — Nutrition Progress | `nutrition_progress` | Con barra | Demostración |
| Progress / 05 — Health Trends | `health_trends` | Con barra | Demostración |
| Progress / 06 — Weekly Review | `weekly_review` | Encima | Demostración |

## Antes de empezar

- La ruta y el nombre salen de `specs/PANTALLAS.md`. **No inventes rutas**: están todas en `ui/navigation/Rutas.kt`.
- **No dibujes la barra inferior.** Se arma una sola vez en el `Scaffold` de `MainActivity` y aparece sola en las pantallas que la llevan.
- **Ningún texto escrito en el código.** Todo va en `res/values/strings.xml` y `values-es/`, y se usa con `stringResource()`. El control `scripts/reglas.sh` lo rechaza.
- **Colores, tipografías y tamaños salen del tema** (`ui/theme/`). Ninguna pantalla escribe un color a mano.
- Las pantallas más altas que el frame (unos 850 dp) llevan scroll (UI03).
- Para reemplazar la pantalla de relleno: en el grafo de tu flujo (`ui/navigation/ProgressNavGraph.kt`), cambiá `PantallaVacia(...)` por tu `XxxRoute(...)`.
- La pestaña Progress aterriza en **`01 — Today's Overview`**; su "Start Workout" lleva a `02 — Dashboard`, que tiene los links a `03`, `04`, `05` y `06` (ficha `docs/decisiones/005`).
- Los datos de ejemplo usan **la misma forma que los modelos** `Workout` y `Meal`, para poder enchufarlos a Room si sobra tiempo.
- `Progress / 02` es más alta que 850 dp: lleva scroll.
