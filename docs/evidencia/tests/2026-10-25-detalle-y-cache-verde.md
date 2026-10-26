# Detalle y caché de entrenamientos · verde (25/10/2026)

Workout: catálogo y detalle con Retrofit y caché en Room (RF01, RF02, RF05 en parte, RF06, RF07, RF08 en parte). Rama `feat/workout-detalle-cache-room`. Spec: `specs/funcionales/detalle-y-cache-de-entrenamientos.md` (C1 a C31). Los tests en rojo están en `a77dc2b`.

## Qué se implementó (skill implementar)

- `WorkoutMappers.kt`: DTO del detalle → modelo con `requireNotNull` en cada campo; modelo ↔ Entity con el nivel en minúsculas, como el contrato. Una fila con `description` nulo no es un detalle. C3 a C7.
- `WorkoutDao`: `saveSummaries` usa el upsert parcial (`WorkoutSummaryRow`) y conserva el `description`; `saveDetail` hace upsert de la fila y reemplaza los ejercicios. C8 a C11.
- `WorkoutRepositoryImpl`: red primero; con 200 guarda en Room en un `try` aparte; ante cualquier fallo devuelve lo de Room ordenado por id, y `failure` si Room está vacía o falla. C12 a C18.
- `WorkoutDetailRepositoryImpl`: 404 → `failure` sin leer Room; 200 → guarda y devuelve; cualquier otro fallo → Room. `WorkoutNotCachedException` solo con una `IOException` que no es `MalformedJsonException` ni `EOFException` (se atrapan antes). C19 a C27.
- `WorkoutDetailViewModel`: `WorkoutNotCachedException` → `NotAvailableOffline`. C28.
- 03, 04 y 06: la rama `NotAvailableOffline` aparte, dibujada como el `Error` de cada pantalla con `workout_not_available_offline` (`values` y `values-es`). C29.
- `RepositoryModule`: el `@Binds` apunta a `WorkoutDetailRepositoryImpl`; se borraron `DemoWorkoutDetailRepository` y `DemoWorkoutDetailRepositoryTest`. C30.
- Ningún test se modificó después del rojo. `VoraDatabase` no se tocó (ya estaba en versión 3).

## Verde

```text
./gradlew testDebugUnitTest assembleDebug
BUILD SUCCESSFUL in 16s
tests=189 fallas=0 errores=0 salteados=0   (192 del rojo menos D1 a D3, que se borraron con la demo)
WorkoutApiTest 4 · WorkoutMappersTest 8 · WorkoutRepositoryImplTest 15 · WorkoutDetailRepositoryImplTest 9 · WorkoutDetailViewModelTest 8, todos sin fallas
scripts/reglas.sh: exit 0
```

```text
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.parcial_tp3.data.local.WorkoutDaoTest
Running tests on devices: Pixel_10_Pro(AVD) - 17
<testsuite name="com.example.parcial_tp3.data.local.WorkoutDaoTest" tests="6" failures="0" errors="0" skipped="0">
  cache_C8_saveDetail_guarda_la_fila_y_los_ejercicios_en_orden
  cache_C9_saveDetail_otra_vez_reemplaza_los_ejercicios
  cache_C10_saveSummaries_conserva_el_detalle_ya_guardado
  cache_C11_saveSummaries_de_uno_nuevo_deja_description_nulo
  C6_upsertAll_guarda_y_observeAll_lee_lo_guardado
  C7_upsert_con_el_mismo_id_reemplaza_sin_duplicar
BUILD SUCCESSFUL in 24s
```

## Controles negativos

Cada uno rompe el código con un script, corre el test y restaura el archivo desde una copia (comparada con `md5sum`: idéntico en todos).

| Criterio | Qué se rompió | Resultado |
|---|---|---|
| C10 | `upsertAll(workouts)` en `saveSummaries` | `cache_C10…` falla: `expected:<WorkoutEntity(…, description=Rutina de ejemplo…)> but was:<WorkoutEntity(…, description=null)>` |
| C14 | Con Room vacía, `Result.success(emptyList())` | Fallan C8, C9 (×2), C10 (×2) y C11 del catálogo: `503 tiene que ser failure, fue Success([])` |
| C15 | Borrar los ejercicios antes de `saveSummaries` | `C15…` falla: `expected:<[WorkoutExerciseEntity(workoutId=w01, …)]> but was:<[]>` |
| C16 | Borrar los ejercicios en el `catch` | `C16…` falla: `ejercicios con JSON roto expected:<[…]> but was:<[]>` |
| C17 | `dao.saveSummaries` dentro del `try` de la red | `C17…` falla: `expected:<Success([Workout(id=w01, …)…])> but was:<Failure(…)>` |
| C18 | Sin el `catch` al leer Room | `C18…` lanza `IllegalStateException: Room falla al leer` |
| C21/C22 | `catch (IOException)` antes que `MalformedJsonException` y `EOFException` | `C22…` falla: `con JSON roto no es falta de conexion, fue WorkoutNotCachedException` |
| C22 | Sin el `catch (EOFException)` | `C22…` falla: `con cuerpo cortado no es falta de conexion, fue WorkoutNotCachedException` |
| C24 | `deleteExercises(id)` al leer el respaldo | Fallan `C24…` (`ejercicios con 503 pidiendo w01 expected:<[…]> but was:<[]>`) y `C20…` |
| C25 | Sin el `catch (CancellationException)` | `C25…` falla: `al cancelar no tiene que devolver un Result, devolvio Success(WorkoutDetail(id=w01, …))` |
| C30 | Sin el `@Binds` del detalle | `hiltJavaCompileDebug` falla: `[Dagger/MissingBinding] com.example.parcial_tp3.data.repository.WorkoutDetailRepository cannot be provided without an @Provides-annotated method` |

C1 y C2 ya tenían su control del rojo (columna "Vi el rojo" de la spec).

## Capturas en el emulador (C29 y C31)

Pixel 10 Pro (AVD), mock local de `mock/server.py` en la PC y `adb reverse tcp:8000 tcp:8000`. "Servidor apagado" = `adb reverse --remove tcp:8000`: el emulador no llega al mock (conexión rechazada) y el proceso del mock no se toca. App instalada de cero (sin base).

C29, en `docs/evidencia/estados/`:

| Pantalla | Cómo se llegó | Captura |
|---|---|---|
| 04 | Catálogo descargado, mock cortado, "Start Workout" de 01 (w01 todavía sin detalle) | `workout-04-no-disponible-sin-conexion.png`: tarjeta oscura con el texto, como el `Error` de 04 |
| 03 | Mock cortado, 02 → "Entrenamiento completo" (w02) | `workout-03-no-disponible-sin-conexion.png`: `VoraInfoMessage` con `ic_info`, como el `Error` de 03 |
| 06 | A 06 solo se llega desde un 03 con contenido. Se abrió w03 → 06 con el mock prendido, se mandó la app al fondo, se cortó el mock, se mató el proceso (`am kill`), se sacó w03 de la caché editando una copia de `vora.db` (con el `sqlite3` del SDK) y se volvió desde recientes: Android restauró la pila y el ViewModel de 06 volvió a pedir el detalle | `workout-06-no-disponible-sin-conexion.png`: `VoraInfoMessage` con `ic_info`, como el `Error` de 06 |

C31 (prueba de la consigna):

1. Con el mock: 01 (catálogo), 02, w01 → 03 y 06. En Room quedaron w01, w02 y w03; w01 con `description` y 3 ejercicios, w02 y w03 con `description` nulo (leído de la base del emulador).
2. `am force-stop`, mock cortado, se abre la app de nuevo.
3. 01 muestra el primero del catálogo, "Fuerza inicial", desde Room y no `workout_error` (`workout-c31-01-desde-room.png`); 02 muestra los tres (`workout-c31-02-desde-room.png`). El texto de C31 se corrigió el 25/10: decía que 01 mostraba los 3, pero por diseño destaca solo el primero.
4. w01 → 03 con su detalle y sus 3 ejercicios desde Room (`workout-c31-03-w01-desde-room.png`).
5. w02, nunca abierto → `This workout isn't available offline yet.` (`workout-c31-03-w02-no-disponible.png`).

Al terminar se volvió a poner `adb reverse tcp:8000 tcp:8000`.

## Pendiente

- Avisarle a Sofi del cambio de `VoraDatabase` (versión 3, tabla `workout_exercises`) antes del PR: con `fallbackToDestructiveMigration` se borran las comidas del emulador.
- Siguiente: skill `cerrar-tarea`.
