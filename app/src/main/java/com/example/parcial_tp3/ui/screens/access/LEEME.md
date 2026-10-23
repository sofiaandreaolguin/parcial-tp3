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

**Ojo con el ViewModel:** el controlador de cada pantalla sale de la columna *Controlador* de `specs/PANTALLAS.md`; no se decide acá. **No todas las pantallas tienen uno propio:** hay flujos donde varias comparten uno a propósito, porque comparten datos (el borrador de una comida en Nutrition, los estados de Voice). Mirá la tabla de tu flujo antes de crear el archivo.

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
- Al entrar se navega a `home_dashboard` con `popUpTo(access_graph) { inclusive = true }` (`entrarAHome()` en `AccessNavGraph.kt`): todo el acceso sale de la pila y el botón atrás no vuelve ahí (NAV02). Sirve igual desde SignIn y desde SignUp.
- SignIn y SignUp comparten un `AuthViewModel` **del grafo**: la Route recibe la entrada de `access_graph` y hace `hiltViewModel(entrada)`. Con `hiltViewModel()` a secas cada pantalla tendría el suyo y se perdería lo escrito.
- Se pintan hasta el borde (`RUTAS_A_SANGRE` en `Rutas.kt`): el fondo pasa por detrás de la barra de estado y la pantalla pone `statusBarsPadding()` y `navigationBarsPadding()`.
- La flecha ‹ de arriba a la izquierda: en SignIn no tiene a dónde volver y se dibuja sin acción; en SignUp vuelve a SignIn (igual que "Sign In" de abajo y el atrás del sistema), con `popBackStack(sign_in)` para que un doble toque no saque también el login.
- Sin Firebase es un acceso de demostración: valida el formulario y entra.
