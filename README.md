# VORA Coach

App Android del parcial domiciliario de **Taller de Programación 3** (ORT), en Kotlin y Jetpack Compose, a partir del Figma de VORA Coach: **37 pantallas en 7 flujos**, con catálogo de entrenamientos por Retrofit y caché en Room, registro de comidas en Room y, como extras, Firebase, un coach con historial y búsqueda de alimentos.

## Integrantes

| Integrante | GitHub | Rol y zona |
|---|---|---|
| Martu | [@bullonm](https://github.com/bullonm) | Tema de Compose y componentes comunes · Acceso e Inicio · Firebase |
| Sofi | [@sofiaandreaolguin](https://github.com/sofiaandreaolguin) | Nutrition: Room, carga, ingredientes y listado de comidas |
| Hector | [@delafuentehector](https://github.com/delafuentehector) | Workout: Retrofit, caché en Room y estados de error |
| Zaki | [@zakiel-go](https://github.com/zakiel-go) | Asistente VORA, Progress y Profile · control visual |
| Fiore | [@fiorelancissi](https://github.com/fiorelancissi) | Navegación, inventario de pantallas y `MainActivity` · coach y búsqueda (EXT01, EXT02) |

Lo que hizo cada uno se sigue por sus commits: `git shortlog -sn` los agrupa por persona (el `.mailmap` unifica las distintas grafías con que firmó cada quien).

## Probar la app

Lo más rápido: **servidor publicado del docente** (no hay que levantar nada) y el **`google-services.json` del grupo**.

1. **Clonar y abrir** en Android Studio:

   ```bash
   git clone https://github.com/sofiaandreaolguin/parcial-tp3.git
   ```

   Abrir la carpeta y esperar a que termine el primer Sync. Android Studio escribe solo `local.properties` con el `sdk.dir`.

2. **Copiar el archivo de Firebase** que entregamos por Campus o mail a `app/google-services.json`. No está en Git y **sin él no compila** (ver [Firebase](#firebase-extra-aa) si se prefiere un proyecto propio).

3. **Apuntar al servidor publicado.** Agregar esta línea al final de `local.properties`, en la raíz del proyecto:

   ```properties
   api.baseUrl=https://tp3.martinrivas.ar/
   ```

   Sin esa línea, la app busca el mock local en `http://10.0.2.2:8000/` y Workout muestra "No connection".

4. **Sync Project with Gradle Files** (el ícono del elefante) y **Run** en un emulador con internet.

5. **Crear una cuenta** en *Sign Up*, con cualquier email y una contraseña de 6 caracteres o más (el mínimo de Firebase). Sin sesión no se entra a Home ([decisión 008](docs/decisiones/008-firebase-sin-acceso-demo.md)).

Listo. Para el recorrido de la consigna:

| Qué probar | Dónde | Requisito |
|---|---|---|
| Catálogo remoto y detalle de dos entrenamientos | Pestaña **Workout** → *Weekly Plan* → una tarjeta | RF01, NAV04 |
| Cargar una comida, verla en el total, borrarla con confirmación | **Nutrition** → *Add Food* → *Log Manually* → nombre y tipo → *+ Add Ingredient* → *Save*; después tocar la comida → *Delete* | RF03, RF04 |
| Rotar sin perder nada | Cualquier pantalla de Workout o Nutrition | RF07 |
| Asistente sin pedir micrófono | **Home** → *Start voice session*, y tocar el micrófono para pasar por los estados | NAV05 |
| Coach que contesta y guarda el hilo | Asistente → *Type instead* | Extra EXT01 |
| Buscar un alimento y calcular la porción | Nutrition → *Add Food* → *Search Database* | Extra EXT02 |

Para el modo sin conexión (RF02) y los escenarios de error (RF05) hace falta el mock local: ver [Mock local y escenarios](#mock-local-y-escenarios).

### Tests y build (no necesitan el emulador ni Firebase real)

```bash
./gradlew assembleDebug testDebugUnitTest lintDebug   # lo mismo que corre la CI: 380 tests unitarios, 0 fallos
./gradlew connectedDebugAndroidTest                   # con el emulador abierto: 26 tests de Room y Compose, 0 fallos
```

Para compilar sin el archivo del grupo alcanza con la plantilla: `cp app/google-services.json.example app/google-services.json`. Compila y corren los tests, pero no se puede iniciar sesión.

### Si algo no arranca

| Síntoma | Solución |
|---|---|
| `SDK location not found` | Falta `sdk.dir` en `local.properties` (pasa al compilar por consola sin haber abierto el proyecto). En macOS: `sdk.dir=/Users/TU-USUARIO/Library/Android/sdk`. En Windows: `sdk.dir=C:\\Users\\TU-USUARIO\\AppData\\Local\\Android\\Sdk` |
| `JAVA_HOME is not set` al usar `./gradlew` | Usar el Java de Android Studio. En Git Bash, para esa terminal: `export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"`. Para que quede fijo, crear la variable `JAVA_HOME` con esa ruta en las variables de entorno de Windows y abrir otra terminal |
| `File google-services.json is missing` | Paso 2 |
| "No connection" en Workout | Falta el paso 3, o se cambió `local.properties` sin hacer Sync |
| "No connection" en el primer Sign In | Al grupo le pasó una vez en el ensayo y no se repitió. Reiniciar el wifi del emulador |
| "API key not valid" al iniciar sesión | Se está usando la plantilla `.example` y no el archivo real |


## Figma

[`figma.com/design/1nvbbfYNYmKAevuaG28J85`](https://figma.com/design/1nvbbfYNYmKAevuaG28J85): página **UI Screens** (los 37 frames) y lámina **Sistema de diseño — VORA**.

En [`docs/figma/`](docs/figma/) hay una imagen por flujo, exportada a 2x, para comparar sin abrir Figma.

## Video y comparaciones visuales

> **Pendiente (tarea u):** link al video de demostración y las cinco comparaciones Figma contra app.

Mientras tanto, las 37 pantallas están capturadas en **411 dp** (tamaño del teléfono) y **360 dp** (el chico, UI03), cada una revisada contra su frame: [`docs/evidencia/visual/control-final.md`](docs/evidencia/visual/control-final.md).

## Estado de las funcionalidades

Las ocho integraciones obligatorias **están implementadas y en `main`**. Cada fila indica el archivo donde mirarla y los pull requests en los que entró.

| Código | Qué pide | Dónde mirarlo | PR |
|---|---|---|---|
| RF01 | Catálogo `GET /v1/workouts` y detalle `GET /v1/workouts/{id}` con nombre, nivel, duración y ejercicios | `WorkoutRepositoryImpl`, `WorkoutDetailRepositoryImpl` | #11 (22/10), #51 (25/10) |
| RF02 | Caché en Room del catálogo y los detalles; sin servidor se ve lo descargado; lo nunca descargado avisa que no está disponible sin conexión | `WorkoutDao`, `WorkoutNotCachedException` | #51 (25/10) |
| RF03 | Carga manual con validación: nombre no vacío, números no negativos y al menos un ingrediente | `domain/MealValidation.kt`, `ManualEntrySheet`, `FoodCaptureViewModel` | #50 (25/10), #63 (28/10) |
| RF04 | Comidas en Room, listado por fecha, eliminación con confirmación, total diario que cambia | `MealDao` (`@Delete`), `MealDetailScreen` (diálogo de borrado) | #9 (21/10), #50, #54 (25/10), #61, #62 (27/10), #63 (28/10) |
| RF05 | Carga, contenido, vacío y error con reintento; un fallo no borra caché ni registros | `WorkoutsUiState`, `WorkoutStatusMessage` | #53 (25/10) |
| RF06 | DTO, modelo y Entity separados; repositorio; la UI no toca Retrofit ni DAO | `WorkoutDto` / `Workout` / `WorkoutEntity` + `WorkoutMappers.kt` | #4 (20/10), sostenido desde entonces |
| RF07 | ViewModel con estado observable; rotar no repite consultas ni pierde estado | `StateFlow` + `SavedStateHandle` en los ViewModel | #11 (22/10), #50 (25/10) |
| RF08 | Hilt: `@AndroidEntryPoint`, `@HiltViewModel`, repositorios por constructor, módulos | `di/` (5 módulos), 21 `@HiltViewModel` | #4 (20/10) |

Los requisitos de maquetación y navegación (UI01 a UI03, NAV01 a NAV05) se ven en el [inventario](#inventario-de-pantallas-nav01) y en [`specs/REQUISITOS.md`](specs/REQUISITOS.md), también con su pull request; los ocho están en `main`.

### Cómo se guardan las comidas (RF03, RF04)

- *Log Manually* (en `Nutrition / 03 — Add Food`) abre una **hoja inferior** con nombre y tipo de comida. Después, `Nutrition / 07` pide **al menos un ingrediente** ("+ Add Ingredient"), y las kcal de la comida son **la suma de sus ingredientes**.
- La foto y la voz (04, 05 y 06) son simuladas, pero sus ingredientes **sí se guardan** y se ven en 01, 02, 07 y 08.
- Los ingredientes van en una columna JSON de la tabla `meals`, sin tabla propia: no se consultan ni se borran por separado.
- Borrar usa `@Delete` con la `Meal` que muestra la pantalla, siempre después del diálogo de confirmación.
- Editar una comida guardada quedó fuera de alcance: "Edit Meal" avisa modo demostración.

Spec: [`specs/funcionales/carga-y-eliminacion-de-comidas.md`](specs/funcionales/carga-y-eliminacion-de-comidas.md).

**La base de Room está en `version = 5`.** Como el proyecto usa `fallbackToDestructiveMigration`, al instalar sobre una versión vieja **se borran las tablas**: comidas, caché de entrenamientos e hilo del coach ([decisión 003](docs/decisiones/003-base-room-compartida.md)). En una instalación nueva no se nota.

### Extra: el coach con historial (EXT01)

Con los ocho obligatorios ya en `main`, se encaró el extra que el docente había mencionado. `Voice / 05 — Conversation` dejó de ser maqueta: habla con el servidor y guarda el hilo.

| Qué hace | Dónde mirarlo |
|---|---|
| VORA contesta consultando al servidor | `CoachRepositoryImpl`, `VoraApi.postCoachMessage` |
| Preguntas sugeridas que manda el servidor, como chips | `GET /v1/coach/suggestions`, `ConversationScreen` |
| El hilo se guarda y **sobrevive a cerrar la app** | `CoachDao`, tabla `coach_messages` |
| Error con reintento que no duplica el mensaje | `CoachRepositoryImpl.send`, `ConversationViewModel.onReintentar` |

Reutiliza las tres capas con sus mappers (RF06), los errores traducidos en el repositorio (RF05), el estado que aguanta la rotación (RF07) y Hilt (RF08). Spec, con 31 criterios: [`specs/funcionales/coach-con-historial.md`](specs/funcionales/coach-con-historial.md). Capturas: [`docs/evidencia/coach/`](docs/evidencia/coach/). PR #58.

### Extra: buscar alimentos y calcular la porción (EXT02)

El botón **"Search Database"** de `Nutrition / 03` y el chip **"Search"** de `Nutrition / 01` estaban dibujados en el Figma y avisaban modo demostración. Ahora abren una **hoja inferior** sobre la pantalla, que se sigue viendo detrás:

| Qué hace | Dónde mirarlo |
|---|---|
| Busca en el catálogo del servidor, esperando a que dejes de tipear | `FoodRepositoryImpl`, `VoraApi.getFoods`, `DEBOUNCE_MS` |
| Calcula los nutrientes de la porción a partir de los valores cada 100 g | `domain/FoodPortion.kt` |
| Suma el alimento como ingrediente en `Nutrition / 07` | `FoodSearchViewModel`, `FoodCaptureViewModel` |
| Guarda por el mismo camino de Room y cambia el total del día | `MealRepository.addMeal` |

**El servidor no calcula nada:** devuelve los valores cada 100 g y la regla de tres la hace la app, en `BigDecimal` con `HALF_UP` y no en `Double`. En coma flotante, 16,9 × 1,5 da 25.349999999999998 y redondeado quedaría 25,3, cuando el valor exacto es 25,35 y tiene que quedar en **25,4**.

**No agrega pantallas ni rutas**: al ser una hoja y no un destino del `NavHost`, el inventario sigue en 37 pantallas y 33 rutas ([decisión 010](docs/decisiones/010-buscador-como-hoja.md)). **Sin paginación, a propósito:** el catálogo tiene 12 alimentos y el servidor acepta `pageSize` hasta 50, así que entran todos en una consulta y nunca se dispararía (queda tachado y explicado en `specs/METODOLOGIA.md`). Spec, con 36 criterios: [`specs/funcionales/busqueda-de-alimentos.md`](specs/funcionales/busqueda-de-alimentos.md). Capturas en 411 y 360 dp y en modo oscuro: [`docs/evidencia/busqueda/`](docs/evidencia/busqueda/). PR #60.

### Extra: Firebase

Cuenta, perfil en Firestore y reglas por usuario: ver [Firebase](#firebase-extra-aa). PR #57.

### Qué cubren los tests

- **380 tests unitarios** (`./gradlew testDebugUnitTest`): mappers de las tres capas, repositorios (con `MockWebServer` y DAO falsos), validación de comidas e ingredientes, cálculo de porciones, errores y ViewModels.
- **26 con el emulador** (`./gradlew connectedDebugAndroidTest`):
  - Room: `MealDaoTest`, `WorkoutDaoTest`, `CoachDaoTest` y `MealRepositoryRoomTest`.
  - Compose: `ConversationRotacionTest` y `FoodSearchRotacionTest` comprueban que rotar no pierde lo escrito, y `FoodSearchGuardadoRoomTest` que lo buscado se guarda en Room.

### Evidencia

| Dónde | Qué muestra |
|---|---|
| [`docs/evidencia/estados/`](docs/evidencia/estados/) | C31 carga (`api.mockDelayMs`) · C32 lista vacía (`empty`) · C33 error del servidor, `malformed`, servidor apagado y **reintento** · C34 detalle **leído desde Room con el servidor apagado**, y comidas intactas tras un fallo de red · C35 **rotar** sin repetir la consulta |
| [`docs/evidencia/visual/control-final.md`](docs/evidencia/visual/control-final.md) | Las 37 pantallas en 411 y 360 dp, con el veredicto de la revisión contra el Figma |
| [`docs/evidencia/ensayo-demo-2026-10-27.md`](docs/evidencia/ensayo-demo-2026-10-27.md) | El recorrido de la demo, paso por paso, con lo que se observó |
| [`docs/evidencia/firebase/`](docs/evidencia/firebase/) | C26 a C29: las reglas probadas con dos cuentas |
| [`docs/evidencia/tests/`](docs/evidencia/tests/) | Tests en rojo, control negativo y verde de cada funcionalidad con datos |

## Mock local y escenarios

Hace falta solo para dos pruebas: el modo sin conexión (RF02), que necesita **poder apagar el servidor**, y los escenarios de error (RF05).

1. Levantar el mock del docente, que está en `mock/` tal como vino en `mock.zip`. Necesita Python 3.9 o más, sin dependencias, y queda escuchando en el puerto 8000 (Ctrl+C lo apaga):

   ```bash
   python3 mock/server.py     # en Windows: py mock/server.py
   python3 mock/server.py --port 9000 --host 0.0.0.0   # otro puerto, o para un teléfono físico
   ```

2. En `local.properties`, cambiar la URL y hacer **Sync**:

   ```properties
   api.baseUrl=http://10.0.2.2:8000/
   ```

3. **Si el emulador no llega a `10.0.2.2`** (al grupo le pasó en Windows y la app muestra "No connection"), usar `api.baseUrl=http://127.0.0.1:8000/` y redirigir el puerto con el emulador abierto. Si `adb` no está en el PATH, va con la ruta completa:

   ```bash
   adb reverse tcp:8000 tcp:8000
   # Git Bash:   /c/Users/TU-USUARIO/AppData/Local/Android/Sdk/platform-tools/adb.exe reverse tcp:8000 tcp:8000
   # PowerShell: & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" reverse tcp:8000 tcp:8000
   ```

   Si responde `8000`, quedó conectado. Se borra cada vez que se reinicia el emulador.

**Probar RF02:** abrir un entrenamiento con el mock andando, apagar el mock (Ctrl+C) y volver a abrirlo: se ve desde Room. Uno que nunca se abrió avisa "This workout isn't available offline yet" con *Retry*.

**Probar RF05:** en debug, un interceptor de OkHttp (`MockScenarioInterceptor`) agrega los headers de prueba a cada petición. Se configuran en `local.properties` (y Sync):

```properties
# empty | error | malformed (sin la línea = respuesta normal)
api.mockScenario=empty
# demora en milisegundos, para ver el estado de carga
api.mockDelayMs=3000
```

Para volver a la respuesta normal, borrar las dos líneas. En release el interceptor no se agrega.

### URL del servidor

`BASE_URL` sale de `api.baseUrl` en `local.properties` y se regenera en `BuildConfig` con cada Sync. La URL siempre termina en `/` (lo controla `BaseUrlTest`).

| Servidor | `api.baseUrl` | Cuándo |
|---|---|---|
| Publicado por el docente | `https://tp3.martinrivas.ar/` | Para usar la app sin levantar nada. Es el que va en release |
| Mock local desde el emulador | `http://10.0.2.2:8000/` | Valor por defecto si no hay línea. Para RF02 y RF05 |
| Mock local con `adb reverse` | `http://127.0.0.1:8000/` | Si el emulador no llega a `10.0.2.2` |
| Teléfono físico | `http://<IP-de-la-compu>:8000/` | Con `python3 mock/server.py --host 0.0.0.0` |

HTTP sin cifrar está habilitado **solo en debug** (`app/src/debug/res/xml/network_security_config.xml`). La release exige HTTPS, así que con el mock local no conecta.

## Firebase (extra aa)

Cuenta con email y contraseña (Firebase Authentication), perfil en Firestore (`users/{uid}`) y reglas de seguridad. Spec: [`specs/funcionales/firebase-auth-perfil.md`](specs/funcionales/firebase-auth-perfil.md). No hay acceso de demostración sin cuenta ([decisión 008](docs/decisiones/008-firebase-sin-acceso-demo.md)).

El archivo de configuración va en `app/google-services.json`, que está en `.gitignore`. Hay dos caminos:

- **Con el archivo del grupo** (el de [Probar la app](#probar-la-app-en-5-minutos)): el proyecto ya tiene Authentication, Firestore y las reglas configurados. No hay que tocar la consola.
- **Con un proyecto propio:**
  1. En la [consola de Firebase](https://console.firebase.google.com), crear un proyecto y agregar una app Android con el paquete `com.example.parcial_tp3`.
  2. En **Authentication > Sign-in method**, habilitar **Email/Password**.
  3. En **Firestore Database**, crear la base en modo producción.
  4. Descargar `google-services.json` a `app/google-services.json`.
  5. Publicar las reglas de [`firebase/firestore.rules`](firebase/firestore.rules): en **Firestore > Rules** pegar el contenido y publicar, o `firebase deploy --only firestore:rules`.

### Probar las reglas con dos cuentas

Con el simulador de la consola (**Firestore > Rules > Rules playground**) o desde la app:

1. Crear las cuentas A y B desde la app y anotar el `uid` de cada una (**Authentication > Users**).
2. Cuenta A lee y escribe `users/{uidA}`: permitido (C26).
3. Cuenta B lee y escribe `users/{uidA}`: denegado (C27).
4. Sin sesión (*Unauthenticated*), cualquier lectura o escritura: denegada (C28).
5. Cualquier otra ruta, por ejemplo `/otra/x`: denegada (C29).

Las capturas están en [`docs/evidencia/firebase/`](docs/evidencia/firebase/), como `C26.png` a `C29.png`, con el `uid` usado en cada una.

## Inventario de pantallas (NAV01)

Las 37 pantallas del Figma con su destino en la app y su estado: **12 funcionales** (cumplen un requisito o un extra con datos reales) y **25 de demostración**. Una pantalla es funcional cuando ella cumple un RF; lo accesorio puede ser de ejemplo.

Los 5 estados del Asistente VORA son **una sola ruta** `voice` con estado distinto, no cinco destinos ([decisión 002](docs/decisiones/002-voice-una-pantalla-con-estados.md)). Por eso el `NavHost` tiene 33 destinos para 37 pantallas. El detalle de cada control y a dónde lleva está en [`specs/PANTALLAS.md`](specs/PANTALLAS.md).

| # | Pantalla de Figma | Destino en la app | Estado |
|---|---|---|---|
| 1 | Login - 01 — SignIn | `sign_in` | **Funcional** (Firebase, extra aa) |
| 2 | Login - 02 — SignUp | `sign_up` | **Funcional** (Firebase, extra aa) |
| 3 | Home / 01 — Dashboard | `home_dashboard` | Demostración (el entrenamiento recomendado sale de la caché de Room y el total de calorías, de la tabla de comidas) |
| 4 | Home / 02 — Notifications | `notifications` | Demostración |
| 5 | Voice / 01 — Idle | `voice` (estado `Idle`) | Demostración |
| 6 | Voice / 02 — Listening | `voice` (estado `Listening`) | Demostración |
| 7 | Voice / 03 — Processing | `voice` (estado `Processing`) | Demostración |
| 8 | Voice / 04 — Speaking | `voice` (estado `Speaking`) | Demostración |
| 9 | Voice / 05 — Conversation | `conversation` | **Funcional** (EXT01) |
| 10 | Voice / 06 — Error | `voice` (estado `Error`) | Demostración |
| 11 | Workout / 01 — Dashboard | `workout_dashboard` | **Funcional** |
| 12 | Workout / 02 — Weekly Plan | `weekly_plan` | **Funcional** |
| 13 | Workout / 03 — Workout Details | `workout_details/{workoutId}` | **Funcional** |
| 14 | Workout / 04 — Pre Workout | `pre_workout/{workoutId}` | Demostración (muestra el detalle real ya descargado; lo simulado es arrancar la sesión) |
| 15 | Workout / 05 — Active Workout | `active_workout/{workoutId}` | Demostración (los ejercicios y series salen del detalle real; el cronómetro es simulado) |
| 16 | Workout / 06 — Exercise Details | `exercise_details/{workoutId}/{exerciseId}` | **Funcional** |
| 17 | Workout / 07 — Workout Complete | `workout_complete/{workoutId}` | Demostración |
| 18 | Nutrition / 01 — Dashboard | `nutrition_dashboard` | **Funcional** |
| 19 | Nutrition / 02 — Today's Meals | `todays_meals` | **Funcional** |
| 20 | Nutrition / 03 — Add Food | `add_food` | Demostración (abre la carga manual y la búsqueda, que son reales) |
| 21 | Nutrition / 04 — Voice Food Logging | `voice_food_logging` | Demostración |
| 22 | Nutrition / 05 — Food Analysis | `food_analysis` | Demostración |
| 23 | Nutrition / 06 — Photo Analysis | `photo_analysis` | Demostración |
| 24 | Nutrition / 07 — Food Confirmation | `food_confirmation` | **Funcional** |
| 25 | Nutrition / 08 — Meal Details | `meal_details/{mealId}` | **Funcional** |
| 26 | Progress / 01 — Today's Overview | `todays_overview` | Demostración |
| 27 | Progress / 02 — Dashboard | `progress_dashboard` | Demostración |
| 28 | Progress / 03 — Workout Progress | `workout_progress` | Demostración |
| 29 | Progress / 04 — Nutrition Progress | `nutrition_progress` | Demostración |
| 30 | Progress / 05 — Health Trends | `health_trends` | Demostración |
| 31 | Progress / 06 — Weekly Review | `weekly_review` | Demostración |
| 32 | Profile / 01 — Dashboard | `profile_dashboard` | Demostración |
| 33 | Profile / 02 — Edit Profile | `edit_profile` | **Funcional** (Firebase, extra aa) |
| 34 | Profile / 03 — Goals & Preferences | `goals_preferences` | Demostración |
| 35 | Profile / 04 — Nutrition Preferences | `nutrition_preferences` | Demostración |
| 36 | Profile / 05 — Voice & AI Settings | `voice_ai_settings` | Demostración |
| 37 | Profile / 06 — Settings | `settings` | Demostración |

## Funciones simuladas

Están declaradas para que no parezcan faltantes. Ninguna es parte del núcleo obligatorio.

| Dónde | Qué es simulado |
|---|---|
| Asistente VORA | Los 5 estados de voz (Idle, Listening, Processing, Speaking, Error). **No se pide permiso de micrófono** (NAV05) |
| Asistente VORA · Conversation | La pantalla es real (EXT01). Lo simulado es el contenido: la conversación de ejemplo que se siembra la primera vez son los cuatro mensajes del frame `Voice / 05`, y el servidor del docente devuelve **cuatro respuestas fijas** por palabra clave, que la burbuja marca como "Simulated response" |
| Nutrition | El reconocimiento por voz y por foto: los ingredientes que "detecta" son de ejemplo, pero se guardan de verdad. **No se piden permisos de cámara ni micrófono** (NAV05). "Edit Meal" avisa modo demostración |
| Workout | Cronómetro, series y volumen de la sesión activa |
| Progress | Las métricas e indicadores, identificados como demostración |
| Profile | El perfil está en Firestore. Goals, Nutrition Preferences y Voice & AI se leen del perfil pero sus cambios quedan en memoria. "Change photo" y "Permissions & Data Controls" avisan modo demostración |
| Acceso | Email y contraseña con Firebase Authentication. Apple, Google y biometría avisan modo demostración |

Los controles del Figma sin pantalla de destino ("Connected Data", "Account", Notifications, Units, Appearance, Help Centre, Contact Support, Privacy Policy) avisan que están en modo demostración: ninguno queda sin respuesta (NAV03).

## Arquitectura

```
UI (Compose: XxxRoute stateful + XxxScreen stateless)
      ↕
ViewModel (@HiltViewModel, StateFlow, coroutines)
      ↕
Repositorio (inyectado por constructor)
      ↕
Retrofit (DTO)     Room (Entity)     Firebase
```

- **La pantalla nunca llama a Retrofit ni a un DAO** (RF06): solo muestra el estado que le da el ViewModel. Lo controla [`scripts/reglas.sh`](scripts/reglas.sh) en cada tarea y en la CI.
- **DTO, modelo y Entity están separados**, con mappers explícitos entre ellos (`WorkoutMappers.kt`, `MealMappers.kt`, `CoachMappers.kt`, `FoodMappers.kt`).
- **Los errores se atrapan en el repositorio** y se traducen a errores propios: sin conexión, error del servidor, no encontrado, datos inválidos. El ViewModel los convierte en un estado `Error` con mensaje de `strings.xml` y acción de reintentar. La pantalla nunca ve una excepción.
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
│   ├── session/           la sesión de Firebase, compartida por toda la app
│   └── theme/             colores, tipografías, espaciados y formas
├── domain/                el modelo y las reglas puras (validación, porciones)
├── data/
│   ├── remote/            Retrofit (la API y sus DTO) y Firestore
│   ├── local/             Room: la base, los DAO y las Entity
│   └── repository/        coordina remoto y local, y traduce los errores
└── di/                    los módulos de Hilt que construyen todo eso
```

- **Cada pantalla son cuatro archivos** con el mismo nombre y distinto sufijo: `XxxRoute` (stateful, pide el ViewModel), `XxxScreen` (stateless, solo dibuja), `XxxViewModel` y `XxxUiState`.
- **Cada dato tiene tres nombres**, uno por capa: `WorkoutDto` (lo que devuelve la API), `Workout` (el modelo de la app) y `WorkoutEntity` (lo que se guarda en Room).

### Inyección de dependencias (RF08)

Hilt (construido sobre Dagger) arma el grafo al compilar:

- `VoraApplication` (`@HiltAndroidApp`) crea el contenedor de toda la app (`SingletonComponent`).
- `MainActivity` (`@AndroidEntryPoint`) puede recibir dependencias y da acceso a los 21 `@HiltViewModel`.
- `di/NetworkModule`: la URL, los interceptores, `OkHttpClient`, `Retrofit` y `VoraApi`, una sola instancia cada uno (`@Singleton`).
- `di/DatabaseModule`: la única base Room (`VoraDatabase`, `@Singleton`) y sus DAO.
- `di/FirebaseModule`: `FirebaseAuth` y `FirebaseFirestore`.
- `di/RepositoryModule`: une cada interfaz de repositorio con su implementación (`@Binds`).
- `di/ClockModule`: el reloj del sistema, del que sale la fecha de hoy de Nutrition y del coach.

Los repositorios piden por constructor (`@Inject constructor`) la API, el DAO o el cliente de Firebase que necesitan, y Hilt resuelve la cadena.

**Hilt y Dagger:** Dagger genera el código de inyección y comprueba el grafo **al compilar**: si falta una dependencia, no compila (no es un error en tiempo de ejecución). Hilt es una capa encima de Dagger que trae ya armados los componentes de Android (el de la aplicación, el de la actividad, el del ViewModel) con su ciclo de vida resuelto, así no hay que escribirlos a mano. El proyecto usa KSP, no kapt.

### Idiomas

Ningún texto está escrito en el código: van en `strings.xml` y se usan con `stringResource()`. `scripts/reglas.sh` no deja cerrar una tarea con un `Text("...")` suelto.

| | Cuántos |
|---|---|
| Textos en `values/strings.xml` (inglés, el idioma del Figma) | 783 |
| Textos en `values-es/strings.xml` (español) | 781 |
| Marcados `translatable="false"` | 2 |

Los dos que no se traducen son `access_password_placeholder` (`••••••••••••`) y `sign_up_name_placeholder` (`Alex Morgan`, el nombre de ejemplo del Figma): son máscara y nombre propio, no texto. Marcarlos `translatable="false"` evita que Android Studio los reclame como traducciones faltantes.

La app sigue el idioma del sistema: con el emulador en español se ve en español, sin reiniciar nada.

### Qué cambiaríamos al escalar

Lo que hoy está bien resuelto para 37 pantallas y dejaría de estarlo con más:

- **Un módulo Gradle por flujo.** Hoy es un solo módulo `app`: cualquier cambio recompila todo. Con más pantallas conviene `:core:ui`, `:core:data` y un módulo por flujo, que además deja compilar en paralelo.
- **Paginación en el catálogo.** `GET /v1/workouts` trae la lista entera a memoria. Con miles de entrenamientos haría falta Paging 3 leyendo de Room.
- **Room como única fuente de verdad, con sincronización aparte.** Hoy el repositorio consulta la API y después guarda en Room. Al crecer conviene que la UI lea **siempre** de Room y que la descarga sea un trabajo de fondo (WorkManager), así no hay dos caminos para el mismo dato.
- **Migraciones de Room en vez de `fallbackToDestructiveMigration`.** Hoy cada cambio de versión borra los datos locales; con usuarios reales cada versión necesitaría su `Migration`.
- **Casos de uso entre ViewModel y repositorio.** Con esta cantidad de lógica, agregar una capa sería ruido. Cuando una misma regla la necesiten varias pantallas, ahí sí conviene.
- **Un solo `strings.xml` por idioma se vuelve inmanejable.** Convendría partirlo por flujo.
- **La configuración por entorno.** La URL y Firebase salen de `local.properties` y de un archivo fuera de Git. Con más entornos (desarrollo, pruebas, producción) convendría un *build flavor* por entorno.

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
| Firebase BOM (Auth, Firestore) / plugin google-services | 34.4.0 / 4.4.3 |
| Robolectric (tests) | 4.16 |

`compileSdk` 37, `minSdk` 29.

## Cómo trabajamos

Cada tarea entra por uno de **cuatro carriles**, según su tamaño:

- **Lo chico** va directo.
- **Una pantalla** va por plan, implementación y comparación contra el Figma.
- **Lo que toca datos** va por spec, tests en rojo e implementación.
- **Un bug** arranca por el test que lo reproduce.

Antes de cada pull request corren revisores automáticos en contexto limpio, y un control que no deja cerrar una tarea con tests rotos. Cada tarea va en una rama `tipo/flujo-tema` y vuelve a `main` por pull request, que une **quien lo aprobó**, nunca quien lo escribió. La CI corre `assembleDebug testDebugUnitTest lintDebug` y `scripts/reglas.sh`.

| Dónde | Qué hay |
|---|---|
| [`specs/METODOLOGIA.md`](specs/METODOLOGIA.md) | La metodología completa del grupo |
| [`specs/CONTEXTO.md`](specs/CONTEXTO.md) | Arquitectura en capas, convenciones y Git |
| [`specs/PANTALLAS.md`](specs/PANTALLAS.md) | El inventario completo: rutas, ViewModels y a dónde lleva cada control |
| [`specs/REQUISITOS.md`](specs/REQUISITOS.md) | Cada requisito de la consigna con dónde se cumple y su pull request |
| [`specs/funcionales/`](specs/funcionales/) | Las specs de cada funcionalidad con datos, un criterio por test |
| [`docs/decisiones/`](docs/decisiones/) | Las decisiones que tomamos y por qué, una por tema |
| [`docs/BITACORA.md`](docs/BITACORA.md) | Una entrada por tarea: qué se planificó, qué propuso la IA y qué cambiamos, con qué evidencia |
| [`docs/evidencia/`](docs/evidencia/) | Capturas comparadas con el Figma en dos tamaños, estados y tests |

### Uso de IA generativa

Usamos **Claude Code**, con las reglas del grupo en [`CLAUDE.md`](CLAUDE.md) y skills y subagentes propios del repo.

La IA propone plan, código y mensajes de commit. **No commitea, no hace push y no firma:** cada integrante revisa y commitea con su usuario, y un hook de Git rechaza cualquier commit que mencione a una IA como autora o coautora.

El detalle está en [`docs/ia/uso-ia.md`](docs/ia/uso-ia.md) y el porqué del método, en [`docs/decisiones/001-metodologia-ia.md`](docs/decisiones/001-metodologia-ia.md).

## Demostración y comparativa visual

Recorrido VORA Coach Demo App: https://drive.google.com/file/d/1rSiohBXNG2MvHy1OLMNjtSx2o7AC4xdk/view?usp=drive_link
Figa vs App: https://drive.google.com/file/d/1DOziWAehigRR0K7TiFeD0YGGvcYZxSak/view?usp=drive_link
