# VORA Coach

App Android del parcial domiciliario de **Taller de Programación 3** (ORT), en Kotlin y Jetpack Compose, a partir del Figma de VORA Coach: **37 pantallas en 7 flujos**, con catálogo de entrenamientos por Retrofit y registro de comidas en Room.

## Integrantes

| Integrante | GitHub | Rol y zona |
|---|---|---|
| Martu | [@bullonm](https://github.com/bullonm) | Tema de Compose y componentes comunes · Acceso e Inicio |
| Sofi | [@sofiaandreaolguin](https://github.com/sofiaandreaolguin) | Nutrition: Room, carga y listado de comidas |
| Hector | [@delafuentehector](https://github.com/delafuentehector) | Workout: Retrofit, caché en Room y estados de error |
| Zaki | [@zakiel-go](https://github.com/zakiel-go) | Asistente VORA, Progress y Profile · control visual |
| Fiore | [@fiorelancissi](https://github.com/fiorelancissi) | Navegación, inventario de pantallas y `MainActivity` |

Lo que hizo cada uno se sigue por sus commits: `git shortlog -sn` los agrupa por persona (el `.mailmap` unifica las distintas grafías con que firmó cada quien).

## Figma

[`figma.com/design/1nvbbfYNYmKAevuaG28J85`](https://figma.com/design/1nvbbfYNYmKAevuaG28J85) — página **UI Screens** (los 37 frames) y la lámina **Sistema de diseño — VORA**.

En [`docs/figma/`](docs/figma/) hay una imagen por flujo, exportada a 2x, para comparar sin abrir Figma.

## Cómo ejecutar

1. Abrir el proyecto en Android Studio y hacer **Sync Project with Gradle Files**.
2. Crear `local.properties` con `api.baseUrl=https://tp3.martinrivas.ar/`, el servidor publicado por el docente. No hay que levantar nada.
3. Correr en un emulador. El tamaño de referencia es **394 dp**; se controla también en **360 dp** (UI03).

```bash
./gradlew assembleDebug        # compilar
./gradlew testDebugUnitTest    # tests unitarios (sin emulador)
./gradlew connectedDebugAndroidTest   # tests de Room (con el emulador abierto)
```

## Versiones usadas

| Herramienta | Versión |
|---|---|
| Android Gradle Plugin | 9.4.1 |
| Kotlin | 2.2.10 |
| KSP | 2.3.12 |
| Compose BOM | 2026.02.01 |
| Navigation Compose | 2.10.2 |
| Lifecycle (ViewModel / runtime Compose) | 2.11.0 |
| Retrofit + converter-gson | 3.0.0 |
| OkHttp logging-interceptor / mockwebserver (tests) | 4.12.0 |
| Coroutines (y coroutines-test) | 1.11.0 |
| Room | 2.8.5 |
| Hilt / hilt-navigation-compose | 2.60.1 / 1.4.0 |

`compileSdk` 37, `minSdk` 29.

## URL del servidor

`BASE_URL` sale de `api.baseUrl` en `local.properties` (no se sube a Git). Por defecto es `http://10.0.2.2:8000/` (mock local desde el emulador). Para el servidor publicado: `api.baseUrl=https://tp3.martinrivas.ar/`.

Después de cambiar `local.properties`, hacé **Sync Project with Gradle Files** para que se regenere `BuildConfig`. La URL siempre termina en `/`: si no, Retrofit no arranca (lo controla `BaseUrlTest`).

| Servidor | `api.baseUrl` | Cuándo |
|---|---|---|
| Mock local desde el emulador | `http://10.0.2.2:8000/` | Para probar la caché sin conexión (RF02) hace falta poder apagar el servidor. Usa el paquete del docente, que está en `mock/` tal como vino en `mock.zip` |
| Publicado por el docente | `https://tp3.martinrivas.ar/` | Sin levantar nada. Es el que hay que usar en la versión release |
| Teléfono físico | `http://<IP-de-la-compu>:8000/` | Con el mock del docente escuchando en `0.0.0.0` |

HTTP sin cifrar está habilitado **solo en debug** (`app/src/debug/res/xml/network_security_config.xml`). La release exige HTTPS, así que con el mock local no conecta.

### Escenarios de prueba del mock (RF05)

En debug, un interceptor de OkHttp (`MockScenarioInterceptor`) agrega los headers de prueba a cada petición. Se configuran en `local.properties` (y Sync):

```properties
# empty | error | malformed (sin la línea = respuesta normal)
api.mockScenario=empty
# demora en milisegundos, para ver el estado de carga
api.mockDelayMs=3000
```

Para volver a la respuesta normal, borrá las dos líneas. En release el interceptor no se agrega.

## Arquitectura

```
UI (Compose: XxxRoute stateful + XxxScreen stateless)
      ↕
ViewModel (@HiltViewModel, StateFlow, coroutines)
      ↕
Repositorio (inyectado por constructor)
      ↕
Retrofit (DTO)          Room (Entity)
```

- **La pantalla nunca llama a Retrofit ni a un DAO** (RF06): solo muestra el estado que le da el ViewModel.
- **DTO, modelo y Entity están separados**, con mappers explícitos entre ellos.
- **Los errores se atrapan en el repositorio** y se traducen a errores propios (sin conexión, error del servidor, no encontrado, datos inválidos). El ViewModel los convierte en un estado `Error` con mensaje de `strings.xml` y acción de reintentar. La pantalla nunca ve una excepción.
- **La carga arranca en el ViewModel**, nunca en la composición: rotar no repite consultas ni pierde lo cargado (RF07).

Detalle en [`specs/CONTEXTO.md`](specs/CONTEXTO.md).

### Paquetes

Los nombres de los paquetes son la arquitectura: se ve de dónde salen los datos y a dónde van sin abrir ningún archivo.

```
com.example.parcial_tp3
├── ui/
│   ├── screens/<flujo>/   una carpeta por flujo, los 7 del Figma
│   ├── components/        los comunes, se arman una sola vez
│   ├── navigation/        las rutas y un grafo por flujo
│   └── theme/             colores, tipografías, espaciados y formas
├── domain/model/          el modelo con el que trabaja la app
├── data/
│   ├── remote/            Retrofit: la API y los DTO que devuelve
│   ├── local/             Room: la base, los DAO y las Entity
│   └── repository/        coordina remoto y local, y traduce los errores
└── di/                    los módulos de Hilt que construyen todo eso
```

### Archivos

Los nombres de los archivos son el patrón:

- **Cada pantalla son cuatro archivos** con el mismo nombre y distinto sufijo: `XxxRoute` (stateful, pide el ViewModel), `XxxScreen` (stateless, solo dibuja), `XxxViewModel` y `XxxUiState`.
- **Cada dato tiene tres nombres**, uno por capa: `WorkoutDto` (lo que devuelve la API), `Workout` (el modelo de la app) y `WorkoutEntity` (lo que se guarda en Room). Están separados a propósito (RF06), con mappers explícitos entre ellos.

### Inyección de dependencias (RF08)

Hilt (construido sobre Dagger) arma el grafo al compilar:

- `VoraApplication` (`@HiltAndroidApp`) crea el contenedor de toda la app (`SingletonComponent`).
- `MainActivity` (`@AndroidEntryPoint`) puede recibir dependencias y da acceso a los `@HiltViewModel`.
- `di/NetworkModule`: provee la URL, los interceptores, `OkHttpClient`, `Retrofit` y `VoraApi`, una sola instancia cada uno (`@Singleton`).
- `di/DatabaseModule`: provee la única base Room (`VoraDatabase`, `@Singleton`) y sus DAO.
- Los repositorios piden por constructor (`@Inject constructor`) la API o el DAO que necesitan, y Hilt resuelve la cadena.

## Inventario de pantallas (NAV01)

Las 37 pantallas del Figma con su destino en la app y su estado. **8 funcionales** (cumplen un requisito con datos reales) y **29 de demostración**.

Los 5 estados del Asistente VORA son **una sola ruta** `voice` con estado distinto, no cinco destinos: ver [`docs/decisiones/002-voice-una-pantalla-con-estados.md`](docs/decisiones/002-voice-una-pantalla-con-estados.md). Por eso el `NavHost` tiene 33 destinos para 37 pantallas.

| # | Pantalla de Figma | Destino en la app | Estado |
|---|---|---|---|
| 1 | Login - 01 — SignIn | `sign_in` | Demostración |
| 2 | Login - 02 — SignUp | `sign_up` | Demostración |
| 3 | Home / 01 — Dashboard | `home_dashboard` | Demostración (el entrenamiento recomendado sale de la caché de Room y el total de calorías, de la tabla de comidas) |
| 4 | Home / 02 — Notifications | `notifications` | Demostración |
| 5 | Voice / 01 — Idle | `voice` (estado `Idle`) | Demostración |
| 6 | Voice / 02 — Listening | `voice` (estado `Listening`) | Demostración |
| 7 | Voice / 03 — Processing | `voice` (estado `Processing`) | Demostración |
| 8 | Voice / 04 — Speaking | `voice` (estado `Speaking`) | Demostración |
| 9 | Voice / 05 — Conversation | `conversation` | Demostración |
| 10 | Voice / 06 — Error | `voice` (estado `Error`) | Demostración |
| 11 | Workout / 01 — Dashboard | `workout_dashboard` | Funcional |
| 12 | Workout / 02 — Weekly Plan | `weekly_plan` | Funcional |
| 13 | Workout / 03 — Workout Details | `workout_details/{workoutId}` | Funcional |
| 14 | Workout / 04 — Pre Workout | `pre_workout/{workoutId}` | Demostración (muestra el detalle real ya descargado; lo simulado es arrancar la sesión) |
| 15 | Workout / 05 — Active Workout | `active_workout/{workoutId}` | Demostración (los ejercicios y series salen del detalle real; el cronómetro es simulado) |
| 16 | Workout / 06 — Exercise Details | `exercise_details/{workoutId}/{exerciseId}` | Funcional |
| 17 | Workout / 07 — Workout Complete | `workout_complete/{workoutId}` | Demostración |
| 18 | Nutrition / 01 — Dashboard | `nutrition_dashboard` | Funcional |
| 19 | Nutrition / 02 — Today's Meals | `todays_meals` | Funcional |
| 20 | Nutrition / 03 — Add Food | `add_food` | Demostración |
| 21 | Nutrition / 04 — Voice Food Logging | `voice_food_logging` | Demostración |
| 22 | Nutrition / 05 — Food Analysis | `food_analysis` | Demostración |
| 23 | Nutrition / 06 — Photo Analysis | `photo_analysis` | Demostración |
| 24 | Nutrition / 07 — Food Confirmation | `food_confirmation?mealId={mealId}` | Funcional |
| 25 | Nutrition / 08 — Meal Details | `meal_details/{mealId}` | Funcional |
| 26 | Progress / 01 — Today's Overview | `todays_overview` | Demostración |
| 27 | Progress / 02 — Dashboard | `progress_dashboard` | Demostración |
| 28 | Progress / 03 — Workout Progress | `workout_progress` | Demostración |
| 29 | Progress / 04 — Nutrition Progress | `nutrition_progress` | Demostración |
| 30 | Progress / 05 — Health Trends | `health_trends` | Demostración |
| 31 | Progress / 06 — Weekly Review | `weekly_review` | Demostración |
| 32 | Profile / 01 — Dashboard | `profile_dashboard` | Demostración |
| 33 | Profile / 02 — Edit Profile | `edit_profile` | Demostración |
| 34 | Profile / 03 — Goals & Preferences | `goals_preferences` | Demostración |
| 35 | Profile / 04 — Nutrition Preferences | `nutrition_preferences` | Demostración |
| 36 | Profile / 05 — Voice & AI Settings | `voice_ai_settings` | Demostración |
| 37 | Profile / 06 — Settings | `settings` | Demostración |

## Funciones simuladas

Están declaradas para que no parezcan faltantes. Ninguna es parte del núcleo obligatorio.

| Dónde | Qué es simulado |
|---|---|
| Asistente VORA | Los 6 estados (Idle, Listening, Processing, Speaking, Conversation, Error). **No se pide permiso de micrófono** (NAV05) |
| Nutrition | Carga por voz y por foto. **No se piden permisos de cámara ni micrófono** (NAV05) |
| Workout | Cronómetro, series y volumen de la sesión activa |
| Progress | Las métricas e indicadores, identificados como demostración |
| Profile | Perfil local, sin Firebase. "Change photo" y "Permissions & Data Controls" avisan modo demostración |
| Acceso | Ingreso de demostración hasta Home, sin Firebase |

Los controles del Figma que no tienen pantalla de destino ("Connected Data", "Account", Notifications, Units, Appearance, Help Centre, Contact Support, Privacy Policy) avisan que están en modo demostración: ninguno queda sin respuesta (NAV03).

## Estado de las funcionalidades

Las integraciones obligatorias y en qué anda cada una. Se actualiza al cerrar cada pull request; el detalle, con responsable y evidencia, está en [`specs/REQUISITOS.md`](specs/REQUISITOS.md).

| Código | Qué pide | Estado |
|---|---|---|
| RF01 | Catálogo `GET /v1/workouts` y detalle `GET /v1/workouts/{id}` con nombre, nivel, duración y ejercicios | pendiente |
| RF02 | Caché en Room del catálogo y los detalles; sin servidor se ve lo descargado; lo nunca descargado avisa que no está disponible sin conexión | pendiente |
| RF03 | Carga manual de comidas con validación (nombre no vacío, números no negativos) | pendiente |
| RF04 | Comidas en Room, listado por fecha, eliminación con confirmación, total diario que cambia | pendiente |
| RF05 | Carga, contenido, vacío y error con reintento; un fallo no borra caché ni registros | pendiente |
| RF06 | DTO, modelo y Entity separados; repositorio; la UI no toca Retrofit ni DAO | pendiente |
| RF07 | ViewModel con estado observable; rotar no repite consultas ni pierde estado | pendiente |
| RF08 | Hilt: `@AndroidEntryPoint`, `@HiltViewModel`, repositorios por constructor, módulos; saber explicar qué construye Hilt y su relación con Dagger | pendiente |

Los requisitos de maquetación y navegación (UI01 a UI03, NAV01 a NAV05) se ven en el inventario de arriba y en [`specs/REQUISITOS.md`](specs/REQUISITOS.md).

## Cómo trabajamos

Cada tarea entra por uno de **cuatro carriles**, según su tamaño: lo chico va directo; una pantalla va por plan, implementación y comparación contra el Figma; lo que toca datos va por spec, tests en rojo e implementación; y un bug arranca por el test que lo reproduce. Antes de cada pull request corren revisores automáticos en contexto limpio, y un control que no deja cerrar una tarea con tests rotos.

Cada tarea entra por una rama `tipo/flujo-tema` y vuelve a `main` por pull request, que une **quien lo aprobó**, nunca quien lo escribió.

| Dónde | Qué hay |
|---|---|
| [`specs/METODOLOGIA.md`](specs/METODOLOGIA.md) | La metodología completa del grupo |
| [`specs/CONTEXTO.md`](specs/CONTEXTO.md) | Arquitectura en capas, convenciones y Git |
| [`specs/PANTALLAS.md`](specs/PANTALLAS.md) | El inventario completo: rutas, controladores y a dónde lleva cada control |
| [`docs/decisiones/`](docs/decisiones/) | Las decisiones que tomamos y por qué, una por tema |
| [`docs/BITACORA.md`](docs/BITACORA.md) | Una entrada por tarea: qué se planificó, qué se corrigió y con qué evidencia |
| [`docs/evidencia/`](docs/evidencia/) | Capturas comparadas con el Figma, en dos tamaños |

### Uso de IA generativa

Usamos **Claude Code**, con las reglas del grupo en [`CLAUDE.md`](CLAUDE.md) y skills y subagentes propios del repo.

La IA propone plan, código y mensajes de commit. **No commitea, no hace push y no firma:** cada integrante revisa y commitea con su usuario, y un hook de Git rechaza cualquier commit que mencione a una IA como autora o coautora.

El detalle está en [`docs/ia/uso-ia.md`](docs/ia/uso-ia.md) y el porqué del método en [`docs/decisiones/001-metodologia-ia.md`](docs/decisiones/001-metodologia-ia.md).

## Video y comparaciones visuales

> **Pendiente (tarea u, 27/10):** link al video de demostración y las cinco comparaciones Figma contra app.
