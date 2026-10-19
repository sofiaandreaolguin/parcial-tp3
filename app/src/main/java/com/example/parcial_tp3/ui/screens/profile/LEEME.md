# Pantallas del flujo Perfil

Son 6 pantallas: solo `01` con barra, `02` a `06` se abren encima.

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
| Profile / 01 — Dashboard | `profile_dashboard` | Con barra | Demostración |
| Profile / 02 — Edit Profile | `edit_profile` | Encima | Demostración |
| Profile / 03 — Goals & Preferences | `goals_preferences` | Encima | Demostración |
| Profile / 04 — Nutrition Preferences | `nutrition_preferences` | Encima | Demostración |
| Profile / 05 — Voice & AI Settings | `voice_ai_settings` | Encima | Demostración |
| Profile / 06 — Settings | `settings` | Encima | Demostración |

## Antes de empezar

- La ruta y el nombre salen de `specs/PANTALLAS.md`. **No inventes rutas**: están todas en `ui/navigation/Rutas.kt`.
- **No dibujes la barra inferior.** Se arma una sola vez en el `Scaffold` de `MainActivity` y aparece sola en las pantallas que la llevan.
- **Ningún texto escrito en el código.** Todo va en `res/values/strings.xml` y `values-es/`, y se usa con `stringResource()`. El control `scripts/reglas.sh` lo rechaza.
- **Colores, tipografías y tamaños salen del tema** (`ui/theme/`). Ninguna pantalla escribe un color a mano.
- Las pantallas más altas que el frame (unos 850 dp) llevan scroll (UI03).
- Para reemplazar la pantalla de relleno: en el grafo de tu flujo (`ui/navigation/ProfileNavGraph.kt`), cambiá `PantallaVacia(...)` por tu `XxxRoute(...)`.
- Perfil de demostración: sin Firebase no se exige login real.
- **`05 — Voice & AI Settings` no tiene entrada clara en el Figma.** Por defecto cuelga de "Review preferences" en `01`. Confirmalo al maquetar: una pantalla sin entrada rompe NAV01.
- **Varias filas no tienen frame de destino** ("Connected Data", "Account", Notifications, Units, Appearance, Help Centre, Contact Support, Privacy Policy). NAV03 permite que avisen "modo demostración". **Lo que no pueden es no hacer nada.**
- **"Change photo" (`02`) y "Permissions & Data Controls" (`06`) no pueden pedir permisos de verdad** (NAV05).
- "Log out" en `06` vuelve a `sign_in` vaciando toda la pila.
- `Profile / 06` es más alta que 850 dp: lleva scroll.
