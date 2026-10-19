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
- La pestaña Progress aterriza en **`02 — Dashboard`**, que es la que tiene los links a `03`, `04`, `05` y `06`.
- **Pendiente 2 de `specs/PANTALLAS.md`:** `01 — Today's Overview` puede quedar sin entrada. La única candidata es la sección "Today's Overview" de `Home / 01`. Coordinalo con quien maquete Home.
- Los datos de ejemplo usan **la misma forma que los modelos** `Workout` y `Meal`, para poder enchufarlos a Room si sobra tiempo.
- `Progress / 02` es más alta que 850 dp: lleva scroll.
