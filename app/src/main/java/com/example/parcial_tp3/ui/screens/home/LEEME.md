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

**Ojo con el ViewModel:** el controlador de cada pantalla sale de la columna *Controlador* de `specs/PANTALLAS.md`; no se decide acá. **No todas las pantallas tienen uno propio:** hay flujos donde varias comparten uno a propósito, porque comparten datos (el borrador de una comida en Nutrition, los estados de Voice). Mirá la tabla de tu flujo antes de crear el archivo.

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
- La sección "Today's Overview" (tarjetas del anillo y de nutrición) abre `todays_overview` con la navegación de la pestaña Progress (`irAPestana`), igual que "Start my workout" abre la de Workout: así la barra queda marcada en la pestaña correcta y atrás vuelve a Home.
