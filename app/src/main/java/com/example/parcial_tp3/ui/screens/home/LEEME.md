# Pantallas del flujo Inicio

Son 2 pantallas, las dos con barra inferior.

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
| Home / 01 — Dashboard | `home_dashboard` | Con barra | Demostración |
| Home / 02 — Notifications | `notifications` | Con barra | Demostración |

## Antes de empezar

- La ruta y el nombre salen de `specs/PANTALLAS.md`. **No inventes rutas**: están todas en `ui/navigation/Rutas.kt`.
- **No dibujes la barra inferior.** Se arma una sola vez en el `Scaffold` de `MainActivity` y aparece sola en las pantallas que la llevan.
- **Ningún texto escrito en el código.** Todo va en `res/values/strings.xml` y `values-es/`, y se usa con `stringResource()`. El control `scripts/reglas.sh` lo rechaza.
- **Colores, tipografías y tamaños salen del tema** (`ui/theme/`). Ninguna pantalla escribe un color a mano.
- Las pantallas más altas que el frame (unos 850 dp) llevan scroll (UI03).
- Para reemplazar la pantalla de relleno: en el grafo de tu flujo (`ui/navigation/HomeNavGraph.kt`), cambiá `PantallaVacia(...)` por tu `XxxRoute(...)`.
- `Home / 01` es más alta que 850 dp: lleva scroll.
- Es la pantalla que más cruza de flujo: el botón lima abre el coach (`voice`), y los chips llevan a Workout y a Nutrition.
- **Mirá si la sección "Today's Overview" abre algo.** Si abre, va a `todays_overview`; si no, esa pantalla de Progress queda sin entrada (pendiente 2 de `specs/PANTALLAS.md`).
