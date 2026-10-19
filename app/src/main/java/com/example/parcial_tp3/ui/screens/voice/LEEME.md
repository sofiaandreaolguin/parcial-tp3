# Pantallas del flujo Asistente VORA

Son 6 frames de Figma en **2 rutas**: Idle, Listening, Processing, Speaking y Error son estados de la misma pantalla (ficha `docs/decisiones/002`). Las dos se abren encima, sin barra.

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
| Voice / 01 — Idle | `voice` (estado) | Encima | Demostración |
| Voice / 02 — Listening | `voice` (estado) | Encima | Demostración |
| Voice / 03 — Processing | `voice` (estado) | Encima | Demostración |
| Voice / 04 — Speaking | `voice` (estado) | Encima | Demostración |
| Voice / 05 — Conversation | `conversation` | Encima | Demostración |
| Voice / 06 — Error | `voice` (estado) | Encima | Demostración |

## Antes de empezar

- La ruta y el nombre salen de `specs/PANTALLAS.md`. **No inventes rutas**: están todas en `ui/navigation/Rutas.kt`.
- **No dibujes la barra inferior.** Se arma una sola vez en el `Scaffold` de `MainActivity` y aparece sola en las pantallas que la llevan.
- **Ningún texto escrito en el código.** Todo va en `res/values/strings.xml` y `values-es/`, y se usa con `stringResource()`. El control `scripts/reglas.sh` lo rechaza.
- **Colores, tipografías y tamaños salen del tema** (`ui/theme/`). Ninguna pantalla escribe un color a mano.
- Las pantallas más altas que el frame (unos 850 dp) llevan scroll (UI03).
- Para reemplazar la pantalla de relleno: en el grafo de tu flujo (`ui/navigation/VoiceNavGraph.kt`), cambiá `PantallaVacia(...)` por tu `XxxRoute(...)`.
- Los 5 estados van en un `VoiceUiState`, no en rutas distintas. En la demo hay que **poder mostrar los 5 a mano**, porque el profe los va a buscar.
- **`Voice / 06 — Error` dibuja un cartel de permiso de micrófono** con "Allow Access". Hay que dibujarlo por fidelidad visual, pero **el botón no puede pedir el permiso de verdad**: NAV05 lo prohíbe. Que vuelva a Idle.
