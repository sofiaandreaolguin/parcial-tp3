# Pantallas del flujo Entrenamiento

Son 7 pantallas: 01 y 02 con barra, 03 a 07 se abren encima.

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
| Workout / 01 — Dashboard | `workout_dashboard` | Con barra | **Funcional** |
| Workout / 02 — Weekly Plan | `weekly_plan` | Con barra | **Funcional** |
| Workout / 03 — Workout Details | `workout_details/{workoutId}` | Encima | **Funcional** |
| Workout / 04 — Pre Workout | `pre_workout/{workoutId}` | Encima | Demostración |
| Workout / 05 — Active Workout | `active_workout/{workoutId}` | Encima | Demostración |
| Workout / 06 — Exercise Details | `exercise_details/{workoutId}/{exerciseId}` | Encima | **Funcional** |
| Workout / 07 — Workout Complete | `workout_complete/{workoutId}` | Encima | Demostración |

## Antes de empezar

- La ruta y el nombre salen de `specs/PANTALLAS.md`. **No inventes rutas**: están todas en `ui/navigation/Rutas.kt`.
- **No dibujes la barra inferior.** Se arma una sola vez en el `Scaffold` de `MainActivity` y aparece sola en las pantallas que la llevan.
- **Ningún texto escrito en el código.** Todo va en `res/values/strings.xml` y `values-es/`, y se usa con `stringResource()`. El control `scripts/reglas.sh` lo rechaza.
- **Colores, tipografías y tamaños salen del tema** (`ui/theme/`). Ninguna pantalla escribe un color a mano.
- Las pantallas más altas que el frame (unos 850 dp) llevan scroll (UI03).
- Para reemplazar la pantalla de relleno: en el grafo de tu flujo (`ui/navigation/WorkoutNavGraph.kt`), cambiá `PantallaVacia(...)` por tu `XxxRoute(...)`.
- **Acá viven RF01 y RF02** (Retrofit + caché en Room) y **NAV04**.
- El `workoutId` entra en `03` y se arrastra por `04`, `05`, `06` y `07`. Se lee con `SavedStateHandle`. Si se pierde, todos los entrenamientos abren el mismo y se pierde NAV04.
- **Pendiente 5 de `specs/PANTALLAS.md`:** el catálogo de RF01 se muestra en `02 — Your Plan`, que lista tres con entrada por ítem, igual que devuelve el mock. `01` muestra solo el destacado. Confirmalo al maquetar.
