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
2. Completar `local.properties` (no se sube a Git) con **dos líneas**:

   ```properties
   # Dónde está el SDK de Android. Android Studio la escribe solo al abrir el proyecto;
   # hace falta ponerla a mano solo si se compila por consola sin haberlo abierto nunca.
   sdk.dir=/Users/TU-USUARIO/Library/Android/sdk
   # El servidor publicado por el docente. No hay que levantar nada.
   api.baseUrl=https://tp3.martinrivas.ar/
   ```

   Sin `sdk.dir`, Gradle corta con `SDK location not found`. En Windows la ruta suele ser
   `C:\\Users\\TU-USUARIO\\AppData\\Local\\Android\\Sdk`.
3. Configurar Firebase (ver [Firebase](#firebase-extra-aa)). Sin `app/google-services.json` no compila.
4. Correr en un emulador. El tamaño de referencia es **394 dp**; se controla también en **360 dp** (UI03).

```bash
./gradlew assembleDebug        # compilar
./gradlew testDebugUnitTest    # tests unitarios (sin emulador): 309, sin fallos
./gradlew connectedDebugAndroidTest   # tests de Room (con el emulador abierto)
```

### Levantar el mock local

Solo hace falta para probar la caché sin conexión (RF02), que necesita **poder apagar el servidor**. Con el servidor publicado del paso 2 no hay que levantar nada.

```bash
python3 mock/server.py          # queda escuchando en el puerto 8000; Ctrl+C lo apaga
python3 mock/server.py --port 9000 --host 0.0.0.0   # otro puerto, o para un teléfono físico
```

Necesita Python 3.9 o más, sin instalar dependencias. Después hay que apuntar `api.baseUrl` a `http://10.0.2.2:8000/` (ver **URL del servidor**) y hacer Sync.

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

**Hilt y Dagger:** Dagger es el que genera el código de inyección y comprueba el grafo **al compilar** (si falta una dependencia, no compila; no es un error en tiempo de ejecución). Hilt es una capa encima de Dagger que trae los componentes ya armados para Android —el de la aplicación, el de la actividad, el del ViewModel— con su ciclo de vida resuelto, así no hay que escribirlos a mano.

### Idiomas

Ningún texto está escrito en el código: los 734 van en `strings.xml` y se usan con `stringResource()`. Lo controla [`scripts/reglas.sh`](scripts/reglas.sh), que no deja cerrar una tarea con un `Text("...")` suelto.

| | Cuántos |
|---|---|
| Claves en `values/strings.xml` (inglés, el idioma del Figma) | 734 |
| Claves en `values-es/strings.xml` (español) | 732 |
| Marcadas `translatable="false"` | 2 |

Las dos que no se traducen son `access_password_placeholder` (`••••••••••••`) y `sign_up_name_placeholder` (`Alex Morgan`, el nombre de ejemplo del Figma): no son texto, son máscara y nombre propio. Marcarlas `translatable="false"` es lo que evita que Android Studio las reclame como traducciones faltantes.

La app sigue el idioma del sistema: con el emulador en español se ve en español, sin reiniciar nada.

### Qué cambiaríamos al escalar

Lo que hoy está bien resuelto para 37 pantallas y dejaría de estarlo con más:

- **Un módulo Gradle por flujo.** Hoy es un solo módulo `app`: cualquier cambio recompila todo. Con más pantallas conviene `:core:ui`, `:core:data` y un módulo por flujo, que además dejaría que Gradle compile en paralelo.
- **Paginación en el catálogo.** `GET /v1/workouts` trae la lista entera y entra en memoria. Con miles de entrenamientos haría falta Paging 3 leyendo de Room como única fuente de verdad.
- **Room como única fuente de verdad, con sincronización aparte.** Hoy el repositorio consulta la API y después guarda en Room. Al crecer conviene que la UI lea **siempre** de Room y que la descarga sea un trabajo de fondo (WorkManager), así la app anda igual sin conexión y no hay dos caminos para el mismo dato.
- **Casos de uso entre ViewModel y repositorio.** Con esta cantidad de lógica, el ViewModel llamando al repositorio alcanza y agregar una capa sería ruido. Cuando una misma regla la necesiten varias pantallas, ahí sí conviene.
- **Un solo `strings.xml` por idioma se vuelve inmanejable.** Convendría partirlo por flujo.
- **Las claves de Firebase y la URL** ya salen de `local.properties`, fuera de Git. Con más entornos (desarrollo, pruebas, producción) convendría un *build flavor* por entorno en vez de editar un archivo a mano.

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
| 9 | Voice / 05 — Conversation | `conversation` | **Funcional** (EXT01) |
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

## Firebase (extra aa)

Cuenta con email y contraseña (Firebase Authentication), perfil en Firestore y reglas de seguridad. Spec: [`specs/funcionales/firebase-auth-perfil.md`](specs/funcionales/firebase-auth-perfil.md). La app no tiene camino sin cuenta: sin sesión no se entra a Home.

### Configurarlo

1. En la [consola de Firebase](https://console.firebase.google.com) crear un proyecto y agregar una app Android con el paquete `com.example.parcial_tp3`.
2. En **Authentication > Sign-in method**, habilitar **Email/Password**.
3. En **Firestore Database**, crear la base en modo producción.
4. Descargar `google-services.json` y ponerlo en `app/google-services.json`. Está en `.gitignore`: no se sube. Para compilar sin proyecto propio (por ejemplo en la CI) alcanza con copiar la plantilla: `cp app/google-services.json.example app/google-services.json` (compila, pero no se puede iniciar sesión).
5. Publicar las reglas de [`firebase/firestore.rules`](firebase/firestore.rules): en **Firestore > Rules** pegar el contenido y publicar, o `firebase deploy --only firestore:rules`.

### Probar las reglas con dos cuentas

Se prueban a mano, con el simulador de reglas de la consola (**Firestore > Rules > Rules playground**) o desde la app:

1. Crear las cuentas A y B desde la app y anotar el `uid` de cada una (**Authentication > Users**).
2. Cuenta A lee y escribe `users/{uidA}`: permitido (C26).
3. Cuenta B lee y escribe `users/{uidA}`: denegado (C27).
4. Sin sesión (*Unauthenticated*), cualquier lectura o escritura: denegada (C28).
5. Cualquier otra ruta, por ejemplo `/otra/x`: denegada (C29).

Las capturas van en `docs/evidencia/firebase/` como `C26.png` a `C29.png`, indicando el `uid` usado.

## Funciones simuladas

Están declaradas para que no parezcan faltantes. Ninguna es parte del núcleo obligatorio.

| Dónde | Qué es simulado |
|---|---|
| Asistente VORA | Los 5 estados de voz (Idle, Listening, Processing, Speaking, Error). **No se pide permiso de micrófono** (NAV05) |
| Asistente VORA · Conversation | La pantalla **no** es simulada: habla con el servidor y guarda el hilo en Room (EXT01). Lo simulado es el contenido: la **conversación de ejemplo** que se siembra la primera vez son los cuatro mensajes dibujados en el frame `Voice / 05`, y el servidor del docente devuelve **cuatro respuestas fijas** elegidas por palabra clave, que llegan marcadas `isSimulated` y la burbuja muestra como "Simulated response" |
| Nutrition | Carga por voz y por foto. **No se piden permisos de cámara ni micrófono** (NAV05) |
| Workout | Cronómetro, series y volumen de la sesión activa |
| Progress | Las métricas e indicadores, identificados como demostración |
| Profile | Perfil en Firestore (`users/{uid}`). "Change photo" y "Permissions & Data Controls" avisan modo demostración |
| Acceso | Email y contraseña con Firebase Authentication. Apple, Google y biometría avisan modo demostración |

Los controles del Figma que no tienen pantalla de destino ("Connected Data", "Account", Notifications, Units, Appearance, Help Centre, Contact Support, Privacy Policy) avisan que están en modo demostración: ninguno queda sin respuesta (NAV03).

## Estado de las funcionalidades

Las ocho integraciones obligatorias **están implementadas y en `main`**. Cada fila indica el archivo donde mirarla y el pull request en el que entró, con su fecha.

| Código | Qué pide | Estado | Dónde mirarlo | En `main` desde |
|---|---|---|---|---|
| RF01 | Catálogo `GET /v1/workouts` y detalle `GET /v1/workouts/{id}` con nombre, nivel, duración y ejercicios | hecho | `WorkoutRepositoryImpl`, `WorkoutDetailRepositoryImpl` | PR #11 (22/10) y #51 (25/10) |
| RF02 | Caché en Room del catálogo y los detalles; sin servidor se ve lo descargado; lo nunca descargado avisa que no está disponible sin conexión | hecho | `WorkoutDao`, `WorkoutNotCachedException` | PR #51 (25/10) |
| RF03 | Carga manual de comidas con validación (nombre no vacío, números no negativos) | hecho | `domain/MealValidation.kt` | PR #50 (25/10) |
| RF04 | Comidas en Room, listado por fecha, eliminación con confirmación, total diario que cambia | hecho | `MealDao`, `MealDetailScreen` (diálogo de borrado) | PR #9 (21/10), #50 y #54 (25/10) |
| RF05 | Carga, contenido, vacío y error con reintento; un fallo no borra caché ni registros | hecho | `WorkoutsUiState`, `WorkoutStatusMessage` | PR #53 (25/10) |
| RF06 | DTO, modelo y Entity separados; repositorio; la UI no toca Retrofit ni DAO | hecho | `WorkoutDto` / `Workout` / `WorkoutEntity` + mappers | PR #4 (20/10), sostenido desde entonces |
| RF07 | ViewModel con estado observable; rotar no repite consultas ni pierde estado | hecho | `StateFlow` + `SavedStateHandle` en los ViewModel | PR #11 (22/10) y #50 (25/10) |
| RF08 | Hilt: `@AndroidEntryPoint`, `@HiltViewModel`, repositorios por constructor, módulos; saber explicar qué construye Hilt y su relación con Dagger | hecho | `di/`: 4 módulos, 26 `@HiltViewModel` | PR #4 (20/10) |

Los requisitos de maquetación y navegación (UI01 a UI03, NAV01 a NAV05) se ven en el inventario de arriba y en [`specs/REQUISITOS.md`](specs/REQUISITOS.md), también con su pull request; los ocho están en `main`.

### Un extra que no pide la consigna: el coach con historial (EXT01)

Con los ocho obligatorios ya en `main`, se encaró el extra que el docente había mencionado. `Voice / 05 — Conversation` dejó de ser maqueta:

| Qué hace | Dónde mirarlo |
|---|---|
| VORA contesta consultando al servidor | `CoachRepositoryImpl`, `VoraApi.postCoachMessage` |
| Preguntas sugeridas que manda el servidor, como chips | `GET /v1/coach/suggestions`, `ConversationScreen` |
| El hilo se guarda y **sobrevive a cerrar la app** | `CoachDao`, tabla `coach_messages` |
| Error con reintento que no duplica el mensaje | `CoachRepositoryImpl.send`, `ConversationViewModel.onReintentar` |

Reutiliza lo mismo que los ocho obligatorios: las tres capas separadas con sus mappers (RF06), los errores traducidos en el repositorio (RF05), el estado observable que aguanta la rotación (RF07) y Hilt (RF08). La spec, con sus 31 criterios, está en [`specs/funcionales/coach-con-historial.md`](specs/funcionales/coach-con-historial.md), y las capturas de cada estado en [`docs/evidencia/coach/`](docs/evidencia/coach/), incluida la de cerrar la app y volver a abrirla con el hilo intacto.

**Sube la base de Room a `version = 4`.** Como el proyecto usa `fallbackToDestructiveMigration`, la primera vez que se abre después del cambio **se borran las tablas, incluidas las comidas** (decisión [`003`](docs/decisiones/003-base-room-compartida.md)). En una instalación nueva no se nota; sobre una app ya instalada hay que volver a cargar una comida para rehacer la evidencia de RF04.

### Cómo comprobarlo sin instalar nada

**309 tests unitarios, sin fallos**, que no necesitan emulador:

```bash
./gradlew testDebugUnitTest
```

Cubren los mappers de las tres capas, los repositorios (con `MockWebServer` y DAO falsos), la validación de comidas, los errores y 19 ViewModels.

Con el emulador abierto corren **21 más, sin fallos**:

```bash
./gradlew connectedDebugAndroidTest
```

Son los de Room (`MealDaoTest`, `WorkoutDaoTest`, `MealRepositoryRoomTest`, `CoachDaoTest`) y uno de
Compose, `ConversationRotacionTest`, que comprueba que rotar no borra lo que estabas escribiendo.

### Evidencia de los estados (RF02, RF05 y RF07)

En [`docs/evidencia/estados/`](docs/evidencia/estados/) está capturado, pantalla por pantalla, lo que es difícil de ver en una demo en vivo:

| Código | Qué muestra |
|---|---|
| C31 | Estado de carga (con `api.mockDelayMs`) |
| C32 | Lista vacía (escenario `empty`) |
| C33 | Error del servidor, respuesta `malformed`, servidor apagado y **reintento** que vuelve al contenido |
| C34 | Un detalle ya visto **leído desde Room con el servidor apagado**, y que un fallo de red no borra las comidas |
| C35 | **Rotar la pantalla**: no repite la consulta ni pierde lo cargado |

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
