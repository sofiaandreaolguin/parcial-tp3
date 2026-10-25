# Estados de carga, vacío y error con reintento

Flujo: Workout
Requisitos: RF05, RF07 (y RF06: errores traducidos en el repositorio)
Pantallas: `Workout / 01 — Dashboard` y `Workout / 02 — Weekly Plan` (`WorkoutsViewModel`); `Workout / 03 — Workout Details`, `Workout / 04 — Pre Workout` y `Workout / 06 — Exercise Details` (`WorkoutDetailViewModel`); `Workout / 05 — Active Workout` (`ActiveWorkoutViewModel`). `Workout / 07 — Workout Complete` solo se adapta para compilar (ver "Fuera de alcance").
Responsable: Hector

## Qué hace
Mientras la app consulta el catálogo o un detalle, se ve "cargando". Si el servidor responde una lista vacía, 01 y 02 avisan que no hay entrenamientos. Si la consulta falla y no hay nada guardado en Room, la pantalla dice **por qué** falló (sin conexión, servicio caído, datos ilegibles, entrenamiento inexistente) y ofrece **Retry**, que vuelve a consultar. Con algo guardado, se sigue viendo lo de Room como hasta ahora. Ningún fallo borra la caché ni las comidas, y rotar no repite la consulta ni pierde lo cargado, tampoco después de un reintento.

El Figma no dibuja ni el error ni el vacío: los pide RF05, y ante cualquier duda manda la consigna (`specs/CONTEXTO.md`, sección 6). Se arman con lo que ya existe (`VoraInfoMessage` / `VoraCardOnDark` y `VoraButton`), sin componentes nuevos.

Es la "tarea r" que dejaron prevista `catalogo-de-entrenamientos.md` y `detalle-y-cache-de-entrenamientos.md` (sección "Fuera de alcance" de las dos). Las dos están en `main` (PR #51); la VC-l de Sofi (comidas, PR #50) también, y de ahí sale el patrón `Error(@StringRes)` + `onRetry()` de `NutritionViewModel`.

## Datos
- DTO, modelo y Entity: **sin cambios**.
- Errores propios (en `data/repository/AppError.kt`, como `MealDataException` / `MealError`):
  - `enum class AppError { NO_CONNECTION, SERVER, NOT_FOUND, INVALID_DATA, UNKNOWN }`.
  - `class AppException(val error: AppError, cause: Throwable? = null) : Exception(cause)`.
  - `fun Throwable.toAppError(): AppError`, la única clasificación. El orden importa porque `MalformedJsonException` y `EOFException` son `IOException` (`specs/CONTEXTO.md`, sección 2):

    | Excepción | `AppError` |
    |---|---|
    | `AppException` | su propio `error` |
    | `MalformedJsonException`, `EOFException`, `JsonParseException` (incluye `JsonSyntaxException` y `JsonIOException`), `IllegalArgumentException` (la de `toDomain()`) | `INVALID_DATA` |
    | cualquier otra `IOException` (servidor apagado, `SocketTimeoutException`) | `NO_CONNECTION` |
    | cualquier otra | `UNKNOWN` |

  Se clasifica por el **tipo** de la excepción, nunca por su `cause`. OkHttp tira `IOException("unexpected end of stream", EOFException)` cuando el servidor corta la conexión: eso es `NO_CONNECTION`, y mirar la causa lo convertiría en `INVALID_DATA`.

- `WorkoutNotCachedException` **se conserva** (la comparan C21 y C28 de la spec de caché): sigue siendo "sin conexión y nunca descargado".
- Textos nuevos (`res/values/strings.xml` y `res/values-es/strings.xml`), en el bloque de Workout junto a `workout_error`, no al final del archivo (la rama `feat/firebase-extra` también suma textos):

  | Clave | Inglés | Español |
  |---|---|---|
  | `workout_error_no_connection` | `No connection. Check your internet and try again.` | `Sin conexión. Revisá tu internet y probá de nuevo.` |
  | `workout_error_server` | `The service isn\'t available right now.` | `El servicio no está disponible en este momento.` |
  | `workout_error_invalid_data` | `We received data we couldn\'t read.` | `Llegaron datos que no se pudieron leer.` |
  | `details_not_found` | `This workout doesn\'t exist.` | `Este entrenamiento no existe.` |
  | `workout_retry` | `Retry` | `Reintentar` |

  Los que ya existen quedan como mensaje genérico (`UNKNOWN`): `workout_error` ("We couldn't load your workouts.") en el catálogo y `details_error` ("We couldn't load this workout.") en el detalle y la sesión. `workout_empty` y `workout_not_available_offline` no cambian.

Cuerpos de prueba (de `specs/API.md`, "Comprobado corriendo el mock"):
- `empty`: HTTP 200 `{"data": []}` (solo el catálogo; el detalle no cambia con `empty`).
- `error`: HTTP 503 `{"error": {"code": "SERVICE_UNAVAILABLE", "message": "Servicio simulado no disponible."}}`.
- `malformed`: HTTP 200 `{"data": INVALID_JSON` (Gson tira `MalformedJsonException` tal cual, en el catálogo y en el detalle).
- Cuerpo cortado: en el catálogo `{"data": [{"id": "w01"` y en el detalle `{"data": {"id": "w01"` (los dos dan `EOFException`). En el catálogo `{"data": {"id": "w01"` **no** sirve como corte: el adaptador de la lista ve `{`, y eso da `JsonSyntaxException` (JSON de otro tipo).
- 404: `{"error": {"code": "WORKOUT_NOT_FOUND", "message": "Entrenamiento no encontrado."}}`.
- `X-Mock-Delay-Ms`: en los tests, `setHeadersDelay` de MockWebServer.

## Contrato

| Pieza | Archivo | Firma |
|---|---|---|
| Errores | `data/repository/AppError.kt` | `AppError`, `AppException` y `Throwable.toAppError()` (ver "Datos") |
| Repositorio del catálogo | `data/repository/WorkoutRepositoryImpl.kt` | Misma firma. Sin nada en Room, devuelve `Result.failure(AppException(...))`: código fuera de 200-299 → `SERVER`; cuerpo o `data` nulo → `INVALID_DATA`; excepción → `toAppError()`. Lo que lance el respaldo con Room (leer o convertir una fila) se envuelve en `AppException(UNKNOWN)` y **no** pasa por `toAppError()`. Con Room lleno, igual que antes (`success` con lo guardado) |
| Repositorio del detalle | `data/repository/WorkoutDetailRepositoryImpl.kt` | Misma firma. 404 → `AppException(NOT_FOUND)` sin leer Room; otro código fuera de 200-299 → `SERVER`; cuerpo o `data` nulo → `INVALID_DATA`; sin conexión y sin detalle guardado → `WorkoutNotCachedException` (sin cambios); otra excepción → `toAppError()`. Lo que lance el respaldo con Room se envuelve en `AppException(UNKNOWN)`, sin pasar por `toAppError()` y sin `WorkoutNotCachedException` |
| Estado del catálogo | `ui/screens/workout/WorkoutsUiState.kt` | `Loading`, `Content(workouts)` (nunca con lista vacía), **`data object Empty`** y **`data class Error(@StringRes val message: Int)`** |
| Estado del detalle | `ui/screens/workout/WorkoutDetailUiState.kt` | `Error` pasa a **`data class Error(@StringRes val message: Int)`**. `Loading`, `Content` y `NotAvailableOffline` sin cambios |
| Estado de la sesión | `ui/screens/workout/ActiveWorkoutUiState.kt` | `Error` pasa a **`data class Error(@StringRes val message: Int)`**. `Loading` y `Content` sin cambios |
| Mensajes | `ui/screens/workout/WorkoutErrorMessages.kt` | `@StringRes fun catalogErrorMessage(error: Throwable): Int` y `@StringRes fun detailErrorMessage(error: Throwable): Int` (tabla de C16 y C22). `detailErrorMessage(WorkoutNotCachedException)` da `workout_not_available_offline`; 03, 04 y 06 nunca lo llaman con esa excepción porque antes la pasan a `NotAvailableOffline`. Solo los llaman los ViewModels, nunca un `@Composable` (importan `AppException` de `data.repository`, como los ViewModels) |
| ViewModels | `WorkoutsViewModel`, `WorkoutDetailViewModel`, `ActiveWorkoutViewModel` | Misma firma de constructor. Se suma **`fun onRetry()`**. La carga sigue arrancando en `init` (por una `private fun` que también llama `onRetry()`), nunca desde la composición |
| Pantallas | 01 a 06 y sus `XxxRoute` | Cada `XxxScreen` suma `onRetry: () -> Unit` y su `XxxRoute` le pasa `viewModel::onRetry`. 07 solo cambia `ActiveWorkoutUiState.Error ->` por `is ActiveWorkoutUiState.Error ->` |
| Mensaje con reintento | `ui/screens/workout/WorkoutStatusMessage.kt` | `@Composable internal fun WorkoutStatusMessage(text: String, onRetry: () -> Unit, modifier: Modifier = Modifier, onDark: Boolean = false)`. Claro: `VoraInfoMessage(icon = ic_info)` + `VoraButton(workout_retry, style = Outlined, size = Small)`, como `NutritionStatus`. Oscuro (04, 05): `VoraCardOnDark` con `Text` + `VoraButton(style = OutlinedOnDark, size = Small)`. **No se toca `ui/components/`** (zona de Martu) |

- `onRetry()` solo actúa en `Error` (y en `NotAvailableOffline` del detalle): pone `Loading` **antes** de lanzar la coroutine y después vuelve a consultar. En `Loading`, `Content` o `Empty` no hace nada. Si el `Loading` se pusiera dentro del `launch`, dos `onRetry()` seguidos verían los dos el `Error` y lanzarían dos consultas. Así un toque doble no lanza dos consultas y, en 05, nunca reinicia una sesión empezada.
- El vacío **no** lleva reintento (la consigna pide reintento en el error). Si el servidor dice "no hay" y Room tiene entrenamientos, se muestra `Empty` y Room queda igual (C15 de la spec de caché, que sigue valiendo).
- Todos los `Error` llevan Retry, también el 404 (decidido por Hector: la consigna no hace excepciones y desde la app no se llega a un id que no esté en el catálogo).

## Criterios de aceptación (EARS)

Cada línea es un test. Si no se te ocurre el test, la línea está mal escrita.

Clasificación (`AppErrorTest`, sin red):

- C1. `toAppError()` DEBE dar `INVALID_DATA` para `MalformedJsonException`, `EOFException`, `JsonSyntaxException`, `JsonIOException`, `JsonParseException` e `IllegalArgumentException`; `NO_CONNECTION` para `IOException`, `ConnectException`, `SocketTimeoutException` e `IOException(EOFException())` (se mira el tipo, no la causa); `UNKNOWN` para `IllegalStateException` y `RuntimeException`; y el `error` propio para una `AppException` (RF05, RF06).

Repositorio del catálogo (MockWebServer y `FakeWorkoutDao` vacío):

- C2. SI el servidor responde 503 con el cuerpo de `error`, ENTONCES el repositorio DEBE devolver `Result.failure` con `AppException(SERVER)` (RF05). Lo mismo con cualquier otro código fuera de 200-299 (404 y 400).
- C3. SI el servidor responde 200 con el cuerpo de `malformed` (`{"data": INVALID_JSON`) o con el cuerpo cortado `{"data": [{"id": "w01"`, ENTONCES el repositorio DEBE devolver `Result.failure` con `AppException(INVALID_DATA)` y no `NO_CONNECTION` (RF05).
- C4. SI la respuesta es 204 sin cuerpo, o 200 con cuerpo sin `data`, `{"data": null}`, JSON de otro tipo (`{"data": {"id": "w01"}}`) o un ítem inválido, ENTONCES el repositorio DEBE devolver `Result.failure` con `AppException(INVALID_DATA)` (RF05, RF06).
- C5. SI el servidor está apagado, o tarda más que el `readTimeout` del `OkHttpClient` (en el test, un cliente con `readTimeout` de 1 s y `setHeadersDelay` de 2 s), ENTONCES el repositorio DEBE devolver `Result.failure` con `AppException(NO_CONNECTION)`, y con el timeout el servidor DEBE haber recibido un solo pedido (RF05).
- C6. CUANDO el servidor responde el catálogo con una demora menor que el `readTimeout` (el mismo cliente de C5, con `readTimeout` de 1 s, y `setHeadersDelay` de 500 ms), el repositorio DEBE devolver `Result.success` con los 3 entrenamientos: la demora no es un error (RF05, escenario `X-Mock-Delay-Ms`).
- C7. SI la consulta falla (503 o servidor apagado) y leer Room lanza, o convertir una fila de Room lanza (una fila con nivel `"pro"`, que da `IllegalArgumentException`), ENTONCES el repositorio DEBE devolver `Result.failure` con `AppException(UNKNOWN)` sin lanzar, y no `INVALID_DATA` (RF05: el problema es de la caché, no de lo que mandó el servidor).

Repositorio del detalle (MockWebServer y `FakeWorkoutDao` vacío, salvo C11):

- C8. SI el servidor responde 503 (con el cuerpo de `error`), 500 o 400, ENTONCES el repositorio DEBE devolver `Result.failure` con `AppException(SERVER)`, no `WorkoutNotCachedException` (RF05).
- C9. SI el servidor responde 200 con `malformed`, con el cuerpo cortado `{"data": {"id": "w01"`, con JSON de otro tipo (`{"data": [1, 2]}`), sin `data`, con `{"data": null}` o con un detalle inválido, o responde 204 sin cuerpo, ENTONCES el repositorio DEBE devolver `Result.failure` con `AppException(INVALID_DATA)` (RF05, RF06).
- C10. SI el servidor responde 404, ENTONCES el repositorio DEBE devolver `Result.failure` con `AppException(NOT_FOUND)` (RF05). Sin leer Room, como ya pide C23 de la spec de caché.
- C11. SI el servidor está apagado o tarda más que el `readTimeout` y el detalle nunca se descargó, ENTONCES el repositorio DEBE devolver `Result.failure` con `WorkoutNotCachedException` (RF02, RF05: el timeout cuenta como sin conexión).
- C12. SI la consulta falla (503 o servidor apagado) y leer Room lanza, o convertir la fila guardada lanza (nivel `"pro"`), ENTONCES el repositorio DEBE devolver `Result.failure` con `AppException(UNKNOWN)` sin lanzar, y no `INVALID_DATA` ni `WorkoutNotCachedException` (RF05).

Lo que no cambia y sigue cubierto por la spec de caché (sin tests nuevos): con Room lleno cada fallo devuelve lo guardado (C13 y C20), un fallo deja Room igual (C16 y C24), un vacío del servidor no borra Room (C15) y la cancelación se relanza (C12 del catálogo y C25 del detalle).

ViewModel del catálogo (`WorkoutsViewModelTest`, `FakeWorkoutRepository`):

- C13. SI el repositorio devuelve `Result.success` con lista vacía, ENTONCES el estado DEBE pasar a `Empty`, no a `Content` (RF05).
- C14. MIENTRAS la consulta no termina, el estado DEBE ser `Loading` (RF05). Lo cubre C13 del catálogo (`arranca en Loading`); acá se repite para el reintento en C17.
- C15. SI el repositorio devuelve `Result.failure`, ENTONCES el estado DEBE pasar a `Error` con un `message` de `strings.xml` (RF05).
- C16. El `message` de `Error` en el catálogo DEBE ser `workout_error_no_connection` para `NO_CONNECTION`, `workout_error_server` para `SERVER`, `workout_error_invalid_data` para `INVALID_DATA`, y `workout_error` para `NOT_FOUND`, `UNKNOWN` o una excepción que no es `AppException` (RF05).
- C17. CUANDO se llama a `onRetry()` en `Error`, el estado DEBE ser `Loading` justo después de la llamada y el repositorio DEBE llamarse otra vez. Con la respuesta del reintento: una lista con entrenamientos DEBE dar `Content(lista)`, una lista vacía DEBE dar `Empty` y un fallo DEBE dar `Error` con su mensaje (tres casos en el test) (RF05).
- C18. CUANDO se llama a `onRetry()` dos veces seguidas en `Error`, sin `runCurrent()` entre las dos, el repositorio DEBE llamarse una sola vez más. Y CUANDO se llama en `Loading`, `Content` o `Empty`, NO DEBE llamarse (RF07: sin consultas duplicadas).
- C19. CUANDO, después de un reintento que terminó en `Content`, el estado se deja de observar y se vuelve a observar con más de 5 s de tiempo virtual entre una y otra (como al rotar), el repositorio DEBE haberse llamado 2 veces en total, no 3, y el estado DEBE seguir siendo el mismo `Content` (RF07).
- C20. CUANDO en `Error` el estado se deja de observar y se vuelve a observar con más de 5 s entre una y otra, el repositorio DEBE haberse llamado una sola vez y el estado DEBE seguir siendo el mismo `Error` (RF07: rotar no reintenta solo).

ViewModel del detalle (`WorkoutDetailViewModelTest`, `FakeWorkoutDetailRepository`):

- C21. SI el repositorio devuelve `Result.failure`, ENTONCES el estado DEBE pasar a `Error` con un `message` de `strings.xml`, salvo `WorkoutNotCachedException`, que sigue yendo a `NotAvailableOffline` (C28 de la spec de caché) (RF05).
- C22. El `message` de `Error` en el detalle DEBE ser `workout_error_no_connection` para `NO_CONNECTION`, `workout_error_server` para `SERVER`, `workout_error_invalid_data` para `INVALID_DATA`, `details_not_found` para `NOT_FOUND`, y `details_error` para `UNKNOWN` o una excepción que no es `AppException`. Sin `workoutId` en la ruta, o con un `exerciseId` que no está en el detalle, DEBE ser `details_error` (RF05).
- C23. CUANDO se llama a `onRetry()` en `Error` o en `NotAvailableOffline`, el estado DEBE ser `Loading` justo después de la llamada y el repositorio DEBE recibir otra vez el mismo `workoutId`. Con la respuesta del reintento: un detalle DEBE dar `Content` (con el ejercicio del `exerciseId` de la ruta, si lo hay), un fallo DEBE dar `Error` con su mensaje y `WorkoutNotCachedException` DEBE dar `NotAvailableOffline` (tres casos en el test) (RF05).
- C24. CUANDO se llama a `onRetry()` dos veces seguidas en `Error`, sin `runCurrent()` entre las dos, el repositorio DEBE llamarse una sola vez más. Y CUANDO se llama en `Loading` o en `Content`, NO DEBE llamarse (RF07).
- C25. SI no hay `workoutId` en la ruta, ENTONCES `onRetry()` NO DEBE llamar al repositorio y el estado DEBE seguir en `Error` (RF05).
- C26. CUANDO, después de un reintento que terminó en `Content`, se deja de observar y se vuelve a observar con más de 5 s entre una y otra, el repositorio DEBE haberse llamado 2 veces en total y el estado DEBE seguir siendo el mismo `Content`. Y CUANDO se hace lo mismo en `Error`, el repositorio DEBE haberse llamado una sola vez y el estado DEBE seguir en el mismo `Error` (RF07).

ViewModel de la sesión (`ActiveWorkoutViewModelTest`, `FakeWorkoutDetailRepository`):

- C27. SI el repositorio devuelve `Result.failure`, ENTONCES el estado DEBE pasar a `Error` con el mismo `message` que C22, y con `workout_not_available_offline` para `WorkoutNotCachedException` (05 no tiene un estado propio para eso). Un detalle sin ejercicios, o sin `workoutId`, DEBE dar `details_error` (RF05).
- C28. CUANDO se llama a `onRetry()` en `Error`, el estado DEBE ser `Loading` justo después de la llamada, el repositorio DEBE recibir otra vez el mismo `workoutId`, y con un detalle válido el estado DEBE pasar a `Content` con los valores iniciales (primer ejercicio, primera serie, 45 kg, sin descanso) (RF05).
- C29. CUANDO se llama a `onRetry()` dos veces seguidas en `Error`, sin `runCurrent()` entre las dos, el repositorio DEBE llamarse una sola vez más. Y CUANDO se llama en `Loading` o en `Content` (con series ya completadas), NO DEBE llamarse y el `Content` NO DEBE cambiar (RF07: un reintento no reinicia una sesión empezada).
- C30. CUANDO, después de un reintento que terminó en `Content`, se deja de observar y se vuelve a observar con más de 5 s entre una y otra, el repositorio DEBE haberse llamado 2 veces en total y el estado DEBE seguir siendo el mismo `Content`. Y CUANDO se hace lo mismo en `Error`, el repositorio DEBE haberse llamado una sola vez y el estado DEBE seguir en el mismo `Error` (RF07).

Pantallas, emulador y build (a mano, con capturas en `docs/evidencia/estados/`, y `python3 mock/server.py` con `api.mockScenario` / `api.mockDelayMs` en `local.properties`):

- C31. MIENTRAS dura la consulta (`api.mockDelayMs=3000`), 01, 02 y 03 DEBEN mostrar su tarjeta de carga (`workout_loading` / `details_loading`) (RF05). La demora de prueba va hasta 5000 ms: con 10000 se llega al `readTimeout` de 10 s de OkHttp y sale "sin conexión".
- C32. CUANDO el catálogo llega vacío (`api.mockScenario=empty`), 01 y 02 DEBEN mostrar `workout_empty` sin botón Retry (RF05).

  Texto que se espera en C33 según el escenario:

  | Escenario | 01 y 02 | 03 a 06 |
  |---|---|---|
  | `error` | `workout_error_server` | `workout_error_server` |
  | `malformed` | `workout_error_invalid_data` | `workout_error_invalid_data` |
  | servidor apagado | `workout_error_no_connection` | `workout_not_available_offline` |

- C33a. SI el catálogo falla con la app limpia (`adb shell pm clear com.example.parcial_tp3`), con `error`, con `malformed` o con el servidor apagado, ENTONCES 01 y 02 DEBEN mostrar el texto de la tabla con el botón Retry (RF05).
- C33b. SI, con el catálogo ya en Room (se abre una vez en `normal` y se reinstala con `./gradlew installDebug`, sin `pm clear`), se abre el 03 de un entrenamiento nunca abierto con `error`, con `malformed` o con el servidor apagado, ENTONCES 03 DEBE mostrar el texto de la tabla con Retry (RF05).
- C33c. CUANDO, con el servidor apagado y 01 o 03 en error, se prende el servidor y se toca Retry, la pantalla DEBE mostrar la carga (`api.mockDelayMs=3000`) y después el contenido (RF05). Se prueba prendiendo el servidor y no cambiando el escenario: `api.mockScenario` sale de `BuildConfig`, y cambiarlo obliga a reinstalar, lo que crea un ViewModel nuevo.
- C33d. CUANDO se toca Retry con `error` o con `malformed`, el log de `mock/server.py` DEBE mostrar un `GET` más, y la pantalla DEBE pasar por la carga y volver al mismo mensaje (RF05).
- C33e. A 04, 05 y 06 solo se llega desde un 03 con contenido, que deja el detalle guardado en Room, así que con la app de verdad muestran lo guardado y no el error. Para estas tres: captura del `@Preview` de cada `XxxScreen` con `Error(R.string.workout_error_server)`, y también con `NotAvailableOffline` en 04 y 06, con el texto y el botón Retry a la vista (RF05).
- C34. SI la consulta falla con el catálogo, w01 y una comida ya guardados (`error` o `malformed`), ENTONCES 01, 02 y 03 de w01 DEBEN mostrar lo de Room, y Nutrition / 01 DEBE seguir mostrando la comida (RF05: un fallo de red no borra la caché ni los registros locales).
- C35. CUANDO se rota el emulador durante la carga (`api.mockDelayMs=3000`), con el contenido ya cargado y con la pantalla en `Error`, el log de `mock/server.py` DEBE mostrar un solo `GET /v1/workouts` (o `GET /v1/workouts/w01`) por cada vez que se abrió la pantalla o se tocó Retry, y la pantalla DEBE seguir en el mismo estado (RF07).
- C36. La app DEBE compilar con `./gradlew assembleDebug` y `scripts/reglas.sh` NO DEBE dar hallazgos: ningún texto escrito en el código y nada de `ui/` importa `data.remote` ni `data.local` (RF06).

Plantillas: **La app DEBE** (siempre) · **CUANDO** (ante un evento) · **MIENTRAS** (durante un estado) · **SI ... ENTONCES** (algo sale mal) · **DONDE** (opcional, para ampliaciones).

Casos de la plantilla y cómo se cubren acá:
- Sin conexión: C5 y C11 (repositorios), C16, C22 y C27 (mensaje), C33a a C33c (pantalla). Con caché: C13 y C20 de la spec de caché, y C34.
- Lista vacía: C13 (ViewModel) y C32 (pantalla). Con caché: C15 de la spec de caché.
- 503: C2 y C8. JSON inválido: C3, C4 y C9.
- 404: C2 (catálogo, como `SERVER`) y C10 (detalle, `NOT_FOUND`).
- Demora (`X-Mock-Delay-Ms`): C6 (no es error), C5 y C11 (más que el timeout), C31 (se ve la carga).
- Rotación: C19, C20, C26, C30 y C35.
- Qué pasa con lo guardado: C34, y C16 y C24 de la spec de caché.

## Fuera de alcance
- **Reintento en 07 (Workout Complete):** 07 no consulta; muestra el `ActiveWorkoutViewModel` de 05 (`WorkoutCompleteRoute` pide `hiltViewModel(sesion)`). Un reintento recargaría el detalle con la sesión en cero y mostraría un resumen falso. Solo llega a `Error` si Android mata el proceso estando en 07 y al volver no se puede leer el detalle, que ya quedó en Room al abrir 05. Solo se cambia el `when` para que compile y se sigue mostrando `details_error`.
- **Reintento en el vacío:** la consigna lo pide solo en el error.
- **Avisar que lo que se ve viene de la caché:** no está en el Figma (decidido en la spec de caché).
- **Mostrar Room cuando el servidor manda una lista vacía:** el servidor manda; C15 de la spec de caché sigue valiendo.
- **Estados nuevos de Nutrition:** las comidas no usan la red, y `NutritionViewModel` ya tiene `Error` con `onRetry()` (C25 de `carga-y-eliminacion-de-comidas.md`). Zona de Sofi; no se toca.
- **Cambiar los timeouts de OkHttp** (`NetworkModule`) y el `MockScenarioInterceptor`.
- **Pull-to-refresh o refrescar el contenido ya cargado:** `onRetry()` solo actúa en `Error` y `NotAvailableOffline`.
- **Mensajes del servidor (`error.message` del JSON):** se muestra el texto propio de `strings.xml` según el tipo, no lo que manda el mock.

## Tests
| Criterio | Test | Dónde | Vi el rojo |
|---|---|---|---|
| C1 | `` `C1 estados toAppError clasifica cada excepcion` `` | `test/` · `AppErrorTest` | Sí: la lista esperada no coincide (`toAppError()` da `UNKNOWN` fijo) |
| C2 | `` `C2 estados catalogo con 503 u otro codigo es SERVER` `` | `test/` · `WorkoutRepositoryImplTest` (MockWebServer, `FakeWorkoutDao` vacío) | Sí: `expected:<SERVER> but was:<null>` (503) |
| C3 | `` `C3 estados catalogo con JSON roto o cortado es INVALID_DATA` `` | `test/` · `WorkoutRepositoryImplTest` | Sí: `expected:<INVALID_DATA> but was:<null>` (JSON roto) |
| C4 | `` `C4 estados catalogo sin data o con datos invalidos es INVALID_DATA` `` | `test/` · `WorkoutRepositoryImplTest` | Sí: `expected:<INVALID_DATA> but was:<null>` (204) |
| C5 | `` `C5 estados catalogo sin servidor o con timeout es NO_CONNECTION` `` | `test/` · `WorkoutRepositoryImplTest` | Sí: `expected:<NO_CONNECTION> but was:<null>` (timeout) |
| C6 | `` `C6 estados catalogo con demora menor al timeout devuelve los tres` `` | `test/` · `WorkoutRepositoryImplTest` | **Pasa en rojo** (el repositorio ya devuelve los tres): control negativo en implementar |
| C7 | `` `C7 estados catalogo si leer Room falla es UNKNOWN` `` | `test/` · `WorkoutRepositoryImplTest` (`FakeWorkoutDao` que lanza al leer) | Sí: `expected:<UNKNOWN> but was:<null>` (leer Room con 503) |
| C8 | `` `C8 estados detalle con 503 es SERVER` `` | `test/` · `WorkoutDetailRepositoryImplTest` | Sí: `expected:<SERVER> but was:<null>` (503) |
| C9 | `` `C9 estados detalle con JSON roto o datos invalidos es INVALID_DATA` `` | `test/` · `WorkoutDetailRepositoryImplTest` | Sí: `expected:<INVALID_DATA> but was:<null>` (JSON roto) |
| C10 | `` `C10 estados detalle con 404 es NOT_FOUND` `` | `test/` · `WorkoutDetailRepositoryImplTest` | Sí: `expected:<NOT_FOUND> but was:<null>` |
| C11 | `` `C11 estados detalle con timeout y sin cache es WorkoutNotCachedException` `` | `test/` · `WorkoutDetailRepositoryImplTest` | **Pasa en rojo** (`SocketTimeoutException` ya es `IOException`): control negativo en implementar |
| C12 | `` `C12 estados detalle si leer Room falla es UNKNOWN` `` | `test/` · `WorkoutDetailRepositoryImplTest` (`FakeWorkoutDao` que lanza al leer) | Sí: `expected:<UNKNOWN> but was:<null>` (leer Room con 503) |
| C13 | `` `C13 estados catalogo con lista vacia pasa a Empty` `` | `test/` · `WorkoutsViewModelTest` | Sí: `expected:<Empty> but was:<Content(workouts=[])>` |
| C14 | Lo cubren `C13 catalogo arranca en Loading…` y C17 | `test/` · `WorkoutsViewModelTest` | C13 del catálogo sigue en verde; C17 en rojo |
| C15, C16 | `` `C16 estados catalogo cada tipo de error tiene su mensaje` `` (incluye que sea de `strings.xml`, con `esTextoDeStrings`) | `test/` · `WorkoutsViewModelTest` | Sí: los tres primeros mensajes son `workout_error` |
| C17 | `` `C17 estados catalogo onRetry en Error pasa por Loading y vuelve a consultar` `` | `test/` · `WorkoutsViewModelTest` | Sí: `expected:<Loading> but was:<Error(...)>` |
| C18 | `` `C18 estados catalogo onRetry fuera de Error no consulta` `` | `test/` · `WorkoutsViewModelTest` | Sí: dos `onRetry()` seguidos, `expected:<2> but was:<1>` |
| C19 | `` `C19 estados catalogo volver a observar despues del reintento no repite la consulta` `` | `test/` · `WorkoutsViewModelTest` | Sí: `expected:<Content(...)>`, sigue en `Error` |
| C20 | `` `C20 estados catalogo volver a observar en Error no reintenta solo` `` | `test/` · `WorkoutsViewModelTest` | **Pasa en rojo** (`onRetry()` vacío no reintenta): control negativo en implementar |
| C21, C22 | `` `C22 estados detalle cada tipo de error tiene su mensaje` `` | `test/` · `WorkoutDetailViewModelTest` | Sí: los mensajes por tipo son `details_error` |
| C23 | `` `C23 estados detalle onRetry en Error o NotAvailableOffline vuelve a consultar` `` | `test/` · `WorkoutDetailViewModelTest` | Sí: `expected:<Loading> but was:<Error(...)>` |
| C24 | `` `C24 estados detalle onRetry fuera de Error no consulta` `` | `test/` · `WorkoutDetailViewModelTest` | Sí: `expected:<[w02, w02]> but was:<[w02]>` |
| C25 | `` `C25 estados detalle sin workoutId onRetry no consulta` `` | `test/` · `WorkoutDetailViewModelTest` | **Pasa en rojo** (`onRetry()` vacío no consulta): control negativo en implementar |
| C26 | `` `C26 estados detalle volver a observar despues del reintento no repite la consulta` `` | `test/` · `WorkoutDetailViewModelTest` | Sí: `expected:<Content(...)>`, sigue en `Error` |
| C27 | `` `C27 estados sesion cada tipo de error tiene su mensaje` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí: los mensajes por tipo son `details_error` |
| C28 | `` `C28 estados sesion onRetry en Error vuelve a consultar y arranca de cero` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí: `expected:<Loading> but was:<Error(...)>` |
| C29 | `` `C29 estados sesion onRetry en Content no reinicia la sesion` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí: `expected:<[w02, w02]> but was:<[w02]>` |
| C30 | `` `C30 estados sesion volver a observar despues del reintento no repite la consulta` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí: `expected:<Content(...)>`, sigue en `Error` |
| C31 a C35 (con C33a a C33e) | Capturas del emulador y de los `@Preview` (C33e), y log del mock | `docs/evidencia/estados/` | No es test unitario. La parte de Nutrition de C34 se probó después, cuando entró Nutrition / 07 (ver Historial) |
| C36 | `./gradlew assembleDebug` y `scripts/reglas.sh` | build | Control negativo: un texto escrito a mano en `WorkoutStatusMessage` hace fallar `reglas.sh` |

- Los nombres empiezan con "estados" para no confundirse con los `C1`… del catálogo, del detalle demo, de la sesión y de la caché.
- **Los tests existentes no se modifican.** Los de `Error` comparan con `is`, así que siguen valiendo con `Error(message)`. `C28 cache` compara `NotAvailableOffline` por igualdad, y ese estado no cambia.
- **Fakes:** `FakeWorkoutRepository` y `FakeWorkoutDetailRepository` necesitan más de una respuesta para el reintento. Se les suma `fun encolarRespuesta(): CompletableDeferred<Result<…>>`, que guarda una respuesta para la próxima llamada, sin cambiar lo que ya hacen:
  - la primera llamada usa `respuesta`;
  - cada llamada siguiente toma la próxima respuesta encolada y, **si no hay ninguna, usa `respuesta`**, como hoy;
  - `llamadas` / `idsPedidos` siguen contando igual.

  Esto es lo que mantiene en verde `` `C4 detalle con exerciseId que no esta pasa a Error` ``, que crea tres ViewModels sobre el mismo fake con `respuesta` ya completada. C17, C23 y C28 encolan una respuesta sin completar antes de `onRetry()`, para poder ver el `Loading`.
- C5 y C11 (timeout): se arma un `OkHttpClient` con `readTimeout(1, SECONDS)` solo en ese test, con `NetworkModule.provideRetrofit(cliente, url)`. `NetworkModule` no se toca.
- C17, C23 y C28 comprueban `Loading` justo después de `onRetry()`, antes de completar la segunda respuesta, con `StandardTestDispatcher` (`MainDispatcherRule`). C18, C24 y C29 llaman dos veces a `onRetry()` sin `runCurrent()` entre las dos: así detectan un `Loading` puesto dentro del `launch`.
- C7 y C12 usan los interruptores de `FakeWorkoutDao` (lanzar al leer) y una fila con nivel `"pro"`, como C18 y C27 de la spec de caché.
- C19, C20, C26 y C30 siguen el método de `C15 catalogo` (colector en `backgroundScope`, cancelar, `advanceTimeBy(6_000)`, `runCurrent()`). Control negativo: lanzar la carga desde `stateIn(WhileSubscribed)` o reintentar en el colector.
- Controles negativos a prever: C3 y C9 (atrapar `IOException` antes que el JSON roto da `NO_CONNECTION`); C18, C24 y C29 (`onRetry()` sin la guarda del estado o del `Job` llama dos veces o reinicia la sesión); C13 (devolver `Content(emptyList())`).

## Auditoría
- auditor-spec (2026-10-25): 14 hallazgos; uno bloqueaba (#1). Todos corregidos en la spec:
  - #1: los fakes usan `respuesta` cuando no hay nada encolado, así sigue en verde el C4 del detalle demo.
  - #2 y #3: C33 se parte en C33a a C33e. El reintento hasta el contenido se prueba prendiendo el servidor, porque el escenario sale de `BuildConfig`. 04, 05 y 06 se muestran con capturas de `@Preview`.
  - #4: tabla con el texto que se espera en cada escenario.
  - #5: cuerpo cortado propio del catálogo, `{"data": [{"id": "w01"`.
  - #6: el fallo de Room se envuelve en `UNKNOWN` sin pasar por `toAppError()`, y C7 y C12 suman la fila con nivel `"pro"`.
  - #7: `Loading` antes del `launch`, y los tests llaman dos veces sin `runCurrent()` entre las dos.
  - #8: el 204, `{"data": null}` y otros códigos.
  - #9: se clasifica por el tipo, no por la causa.
  - #10: el cliente de C6.
  - #11: `WorkoutNotCachedException` en `detailErrorMessage`.
  - #12: los resultados del reintento se prueban por separado, y "rotar en `Error`" también se prueba en el detalle y en la sesión.
  - #13: los mensajes solo los llaman los ViewModels.
  - #14: por qué se dibuja un estado que no está en el Figma, y la demora de prueba hasta 5000 ms.

## Historial
- 2026-10-25. Spec creada (Hector, con la skill spec-nueva) a partir del plan aprobado. Una sesión por paso del carril C. Alcance: 01 a 06; 07 solo compila. En la entrevista:
  - textos por tipo de error (sin conexión, servidor, datos, 404) con Retry / Reintentar, y los genéricos que ya existían para el resto;
  - el 404 también lleva Retry;
  - con `empty` y la caché llena se muestra `Empty` (el servidor manda);
  - el vacío no lleva reintento.
- 2026-10-25. Correcciones de la auditoría (ver "Auditoría"): C33 pasa a C33a a C33e. Los demás números no cambian.
- 2026-10-25. Hector aprueba la spec para los tests en rojo.
- 2026-10-25. Tests en rojo (Hector, con la skill tests-rojo): `AppErrorTest` nuevo y C2 a C30 sumados a los cinco archivos de la tabla, sin tocar los existentes. `FakeWorkoutRepository` y `FakeWorkoutDetailRepository` suman `encolarRespuesta()`. En `main/` solo firmas: `AppError.kt` con `toAppError()` fijo en `UNKNOWN`, `Error(@StringRes)` y `Empty`, `onRetry()` vacío, los textos nuevos y los `when` de 01 a 07 con `is Error`. `./gradlew testDebugUnitTest`: 216 tests, 23 fallan, todos con `AssertionError` y todos "estados". Pasan en rojo C6, C11, C20 y C25 (control negativo en implementar).
- 2026-10-25. Implementación (Hector, con la skill implementar), con el plan aprobado. Sin tocar tests, `ui/components/`, `ui/theme/`, navegación, Room ni `NetworkModule`:
  - `toAppError()` clasifica por tipo, según la tabla. Los dos repositorios devuelven `AppException` por tipo de fallo; si falla Room, `AppException(UNKNOWN)`. El detalle junta sus `catch` en uno solo y usa `toAppError()` para separar el JSON roto de la falta de conexión (`WorkoutNotCachedException` sigue igual).
  - `WorkoutErrorMessages.kt` (`catalogErrorMessage` y `detailErrorMessage`). Los tres ViewModels cargan desde una `private fun cargar()` que llaman `init` y `onRetry()`; `onRetry()` actúa solo en `Error` (y en `NotAvailableOffline` del detalle) y pone `Loading` antes del `launch`. El catálogo vacío da `Empty`.
  - `WorkoutStatusMessage.kt` (claro y `onDark`). 01 a 06 suman `onRetry` y cada `XxxRoute` pasa `viewModel::onRetry`. En 06, el `Content` sin ejercicio sigue sin Retry. 07 sin cambios.
  - `./gradlew testDebugUnitTest`: 216 tests, 0 fallas. `scripts/reglas.sh` sin hallazgos y `./gradlew assembleDebug` compila.
  - Controles negativos (se rompió el código, se vio el rojo y se deshizo):
    - C6: devolver `failure` con la respuesta buena. La primera rotura (`desdeRoom` después de `guardar`) no falló, porque Room ya tenía los tres.
    - C11: `sinConexion = false`.
    - C20: reintentar cuando aparece un colector (`subscriptionCount`), que falla con `expected:<1> but was:<3>`. Con `stateIn(WhileSubscribed)` falla antes, en la precondición del test.
    - C25: `onRetry()` sin el control de `workoutId`.
    - C3, C9 y C1: `IOException` antes que el JSON roto.
    - C13: `Content(emptyList())`.
    - C18, C24 y C29: `onRetry()` sin la guarda de estado.
    - C36: un texto escrito a mano en `WorkoutStatusMessage`, que hace salir a `reglas.sh` con código 2.
- 2026-10-25. Prueba en el emulador (Hector la pidió a la IA). Capturas `docs/evidencia/estados/estados-*.png` y `estados-log-del-mock.txt`, con una copia del mock en otro puerto y `adb reverse`. El "servidor apagado" se simuló sacando el `adb reverse`.
  - C31, C32, C33a a C33e y C35 dan lo esperado. Cada apertura y cada Retry suman un solo `GET`, y rotar no suma ninguno. C33d se probó con `error` (en 01 y 03); con `malformed` solo se abrieron las pantallas, sin tocar Retry. En C33a, de 02 solo hay captura con `error`.
  - C34: 01, 02 y 03 de w01 muestran lo de Room con `error`. **La parte de Nutrition no se pudo probar**: en esta rama Nutrition / 07 (Food Confirmation) todavía es `PantallaVacia` en la navegación, así que no hay cómo guardar una comida desde la app.
  - C33e: se sumaron `@Preview` con `Error(R.string.workout_error_server)` en 04, 05 y 06, y con `NotAvailableOffline` en 04 y 06. Se abrieron en el emulador con `PreviewActivity`.
  - En la primera corrida el emulador dio ANR por "no focused window" en el launcher, en la app, en `system` y en System UI. El hilo principal de la app respondía, así que no venía del código. Después de `adb reboot` no se repitió.
  - Después de sumar las `@Preview`: 216 tests en verde, `reglas.sh` sin hallazgos y `assembleDebug` compila.
- 2026-10-25. Cierre (skill cerrar-tarea): revisor-requisitos, revisor-arquitectura, revisor-visual y `/code-review` sin nada BLOQUEA, CRÍTICA ni ALTA. Las sugerencias quedaron en `docs/BITACORA.md`.
- 2026-10-25. C34 completo, después de unir `main` con Nutrition / 07 (PR #52) y de que el PR #53 entrara a `main`. Se guardó una comida desde 07 ("Prueba C34", Lunch, 500 kcal). Con `malformed` y con `error`, sin `pm clear`, 01, 02 y 03 de w01 muestran lo de Room, y Nutrition / 01 sigue mostrando la comida. Capturas `estados-c34-nutrition-01-antes.png`, `-con-error.png` y `-con-malformed.png`.
