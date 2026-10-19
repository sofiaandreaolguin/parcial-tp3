# Pantallas del flujo Acceso

Son 2 pantallas, las dos de acceso: van antes de entrar a la app y no llevan barra inferior.

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
| Login - 01 — SignIn | `sign_in` | Acceso | Demostración |
| Login - 02 — SignUp | `sign_up` | Acceso | Demostración |

## Antes de empezar

- La ruta y el nombre salen de `specs/PANTALLAS.md`. **No inventes rutas**: están todas en `ui/navigation/Rutas.kt`.
- **No dibujes la barra inferior.** Se arma una sola vez en el `Scaffold` de `MainActivity` y aparece sola en las pantallas que la llevan.
- **Ningún texto escrito en el código.** Todo va en `res/values/strings.xml` y `values-es/`, y se usa con `stringResource()`. El control `scripts/reglas.sh` lo rechaza.
- **Colores, tipografías y tamaños salen del tema** (`ui/theme/`). Ninguna pantalla escribe un color a mano.
- Las pantallas más altas que el frame (unos 850 dp) llevan scroll (UI03).
- Para reemplazar la pantalla de relleno: en el grafo de tu flujo (`ui/navigation/AccessNavGraph.kt`), cambiá `PantallaVacia(...)` por tu `XxxRoute(...)`.
- Al entrar se navega a `home_dashboard` con `popUpTo(sign_in) { inclusive = true }`: el login sale de la pila y el botón atrás no vuelve ahí (NAV02).
- La flecha ‹ de arriba a la izquierda no tiene a dónde volver: se dibuja sin acción.
- Sin Firebase es un acceso de demostración: valida el formulario y entra.
