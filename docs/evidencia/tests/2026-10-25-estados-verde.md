# Estados de carga, vacío y error con reintento · verde (25/10/2026)

Workout: estados de carga, vacío y error con reintento (RF05, RF07; RF06: errores traducidos en el repositorio). Rama `feat/workout-estados-carga-error`. Spec: `specs/funcionales/estados-de-carga-y-error.md` (C1 a C36). Los tests en rojo están en `5877b2e`.

## Qué se implementó (skill implementar)

- `AppError.kt`: `toAppError()` clasifica por el tipo de la excepción, nunca por su causa. `AppException` → su error; JSON roto, cuerpo cortado, `JsonParseException` e `IllegalArgumentException` → `INVALID_DATA`; otra `IOException` → `NO_CONNECTION`; el resto → `UNKNOWN`. C1.
- `WorkoutRepositoryImpl`: código fuera de 200-299 → `SERVER`; sin cuerpo o sin `data` → `INVALID_DATA`; excepción → `toAppError()`. Si falla Room → `AppException(UNKNOWN)`. C2 a C7.
- `WorkoutDetailRepositoryImpl`: 404 → `NOT_FOUND` sin leer Room; mismas reglas que el catálogo. Las ramas de `catch` se juntan en una, y `WorkoutNotCachedException` sale solo con `NO_CONNECTION`. C8 a C12.
- `WorkoutErrorMessages.kt`: `catalogErrorMessage` y `detailErrorMessage`. Los tres ViewModels cargan desde una `private fun cargar()` que llaman `init` y `onRetry()`. `onRetry()` actúa solo en `Error` (y en `NotAvailableOffline` del detalle) y pone `Loading` antes del `launch`. El catálogo vacío da `Empty`. C13 a C30.
- `WorkoutStatusMessage.kt`, en versión clara y `onDark`. 01 a 06 suman `onRetry` y sus Routes pasan `viewModel::onRetry`. 04, 05 y 06 suman `@Preview` en error (C33e). 07 sin cambios.
- Ningún test se modificó después del rojo.

## Verde

```text
./gradlew testDebugUnitTest
BUILD SUCCESSFUL in 22s
tests=216 failures=0 errors=0
AppErrorTest 1 · WorkoutRepositoryImplTest 21 · WorkoutDetailRepositoryImplTest 14 · WorkoutsViewModelTest 9 · WorkoutDetailViewModelTest 13 · ActiveWorkoutViewModelTest 29, todos sin fallas
scripts/reglas.sh: exit 0, sin hallazgos
./gradlew assembleDebug: exit 0
```

## Controles negativos

Se rompió el código a propósito, se corrió `testDebugUnitTest`, se vio fallar el test y se deshizo el cambio.

| Criterio | Rotura | Falla que se vio |
|---|---|---|
| C6 (pasaba en rojo) | `Result.failure(AppException(NO_CONNECTION))` con la respuesta buena | `C6 estados catalogo con demora menor al timeout devuelve los tres`: `expected:<Success([...])>`. La primera rotura (`desdeRoom` después de `guardar`) no falló: Room ya tenía los tres |
| C11 (pasaba en rojo) | `sinConexion = false` | `con timeout tiene que ser WorkoutNotCachedException, fue ...AppException` (y `C21 cache`) |
| C20 (pasaba en rojo) | reintentar cuando aparece un colector (`subscriptionCount`) | `el repositorio tiene que llamarse una sola vez expected:<1> but was:<3>`. Con `stateIn(WhileSubscribed)` falla antes, en la precondición del test |
| C25 (pasaba en rojo) | `onRetry()` sin el control de `workoutId` | `sin la clave tiene que seguir en Error, fue Content(...)` |
| C3, C9 (y C1) | `is IOException -> NO_CONNECTION` antes del JSON roto | `con JSON roto, fue ...AppException: ...MalformedJsonException` (C3) y `fue WorkoutNotCachedException` (C9) |
| C13 | `Content(it)` aunque la lista esté vacía | `expected:<Empty> but was:<Content(workouts=[])>` (y C17, C18) |
| C18 | `onRetry()` sin la guarda de `Error` | `dos onRetry seguidos consultan una sola vez mas expected:<2> but was:<3>` |
| C24 | `onRetry()` sin la guarda de estado | `expected:<[w02, w02]> but was:<[w02, w02, w02]>` |
| C29 | `onRetry()` sin la guarda de `Error` | `expected:<[w02, w02]> but was:<[w02, w02, w02]>` |
| C36 | `Text(text = "Try again later")` en `WorkoutStatusMessage` | `reglas.sh` sale con código 2: "hay texto escrito directo en una pantalla" |

## Emulador

Capturas `docs/evidencia/estados/estados-*.png` y log del mock en `docs/evidencia/estados/estados-log-del-mock.txt`. Se usó una copia del mock en el puerto 8001 con `adb reverse`; el "servidor apagado" se simuló sacando el `adb reverse`.

| Criterio | Resultado |
|---|---|
| C31 | Carga en 01 (Retry), 02 y 03 con `api.mockDelayMs=3000` |
| C32 | 01 y 02 con `empty`: "There are no workouts yet." sin Retry |
| C33a | 01 y 02 con `error`, `malformed` y sin servidor, con la app limpia: el texto de la tabla y Retry |
| C33b | 03 de w03 (nunca abierto) con `error`, `malformed` y sin servidor: el texto de la tabla y Retry |
| C33c | 01 y 03 sin servidor; se prende, Retry pasa por la carga y llega al contenido, con un `GET` |
| C33d | Retry con `error` suma un `GET` y vuelve al mismo mensaje (01 y 03) |
| C33e | `@Preview` de 04 (error y sin conexión), 05 (error) y 06 (error y sin conexión), abiertas con `PreviewActivity`. En las de 06 la barra superior queda encima de la barra de estado porque ese espacio lo pone el `Scaffold` de la app, que la preview no tiene; con la app real se ve bien (`workout-06-no-disponible-sin-conexion.png`) |
| C34 | Con `error`: 01, 02 y 03 de w01 muestran lo de Room. Nutrition se probó después de unir `main` con Nutrition / 07: se guardó "Prueba C34" (Lunch, 500 kcal) y con `malformed` y con `error` sigue en Nutrition / 01 (`estados-c34-nutrition-01-*.png`) |
| C35 | Rotar en `Error` (02) y durante la carga (01): sigue el mismo estado y no se suma ningún `GET` |

En la primera corrida el emulador dio ANR por "no focused window" en el launcher, en la app, en `system` y en System UI. El hilo principal de la app respondía (`dumpsys activity top` en 0,35 s). Después de `adb reboot` no se repitió.
