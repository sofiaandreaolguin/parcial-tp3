# Catálogo de entrenamientos (primera consulta Retrofit)

Flujo: Workout
Requisitos: RF01 (parcial: el catálogo, sin el detalle), RF06, RF07 (parcial: carga y rotación, sin reintento), RF08 (parcial: el repositorio del catálogo)
Pantallas: `Workout / 01 — Dashboard` y `Workout / 02 — Weekly Plan` van a usar `WorkoutsViewModel`. Esta tarea (g) no dibuja ninguna: deja los datos y el ViewModel listos para la tarea "Workout: las 7 pantallas fieles al Figma".
Responsable: Hector

## Qué hace
La app le pide al servidor la lista de entrenamientos (`GET /v1/workouts`) y la convierte del JSON a los modelos que usa la app. El ViewModel arranca cargando y después tiene la lista, o un error si la consulta falló. Todavía no se ve en pantalla: es la base que después dibujan Workout 01 y 02.

**Evidencia del hito 1:** salida de `./gradlew testDebugUnitTest` con la consulta (C1 a C11) en verde contra MockWebServer, y `assembleDebug` en verde (C16). "Mostrar el catálogo" en pantalla pasa a la tarea de las 7 pantallas (Workout 02): avisarlo en el tablero.

## Datos
- Envoltorio (`ApiResponseDto<T>`, en `data/remote/dto/ApiResponseDto.kt`): `data class` con `data: T?`. El mock siempre manda `{ "data": ... }`.
- DTO (`WorkoutDto`, en `data/remote/dto/WorkoutDto.kt`), la forma del JSON de `WorkoutSummary`:
  - `data class` con `id: String?`, `name: String?`, `level: String?`, `durationMinutes: Int?`, `exerciseCount: Int?`.
  - Todos admiten nulo: Gson no respeta la nulabilidad de Kotlin, y así un campo faltante se detecta en el mapper en vez de reventar más adelante.
- Modelo (`Workout`, en `domain/model/Workout.kt`):
  - `data class` con `id: String`, `name: String`, `level: WorkoutLevel`, `durationMinutes: Int`, `exerciseCount: Int`.
  - `enum class WorkoutLevel { BEGINNER, INTERMEDIATE, ADVANCED }`. En el JSON va exactamente en minúsculas (`"beginner"`), como el contrato de `specs/API.md`.
  - `WorkoutDto` y `Workout` son `data class` (igualdad por valor, como `HealthDto`), para poder compararlos con `assertEquals`.
- Nombres reservados: `Workout`/`WorkoutDto` es el **resumen** del catálogo. El detalle de `GET /v1/workouts/{id}` se va a llamar `WorkoutDetail`/`WorkoutDetailDto`, y sus ejercicios `Exercise`/`ExerciseDto` (avisar a la tarea del detalle).
- Entity: ninguna nueva. `WorkoutEntity` ya existe y la usa la caché (tarea j, RF02).
- Decisiones tomadas en la entrevista:
  - Un campo nulo o un nivel desconocido hace fallar **toda** la consulta, no solo ese ítem.
  - Sin filtro `?level=`: ninguna pantalla lo usa.
- Decisiones tomadas después de la auditoría (Hector las puede cambiar):
  - `level` distingue mayúsculas: `"Beginner"` es un nivel desconocido.

Cuerpo exacto que usan los tests (datos del mock, `specs/API.md`, "Comprobado corriendo el mock"):

```json
{"data": [
  {"id": "w01", "name": "Fuerza inicial", "level": "beginner", "durationMinutes": 25, "exerciseCount": 3},
  {"id": "w02", "name": "Entrenamiento completo", "level": "intermediate", "durationMinutes": 35, "exerciseCount": 3},
  {"id": "w03", "name": "Fuerza y constancia", "level": "beginner", "durationMinutes": 30, "exerciseCount": 3}
]}
```

## Contrato

| Pieza | Archivo | Firma |
|---|---|---|
| Endpoint | `data/remote/VoraApi.kt` | `@GET("v1/workouts") suspend fun getWorkouts(): Response<ApiResponseDto<List<WorkoutDto>>>` |
| Mapper | `data/repository/WorkoutMappers.kt` | `fun WorkoutDto.toDomain(): Workout` |
| Repositorio (interfaz) | `data/repository/WorkoutRepository.kt` | `suspend fun getWorkouts(): Result<List<Workout>>` |
| Repositorio (implementación) | `data/repository/WorkoutRepositoryImpl.kt` | `class WorkoutRepositoryImpl @Inject constructor(private val api: VoraApi) : WorkoutRepository` |
| Hilt | `di/RepositoryModule.kt` | `@Binds @Singleton abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository` (igual que `bindMealRepository`) |
| Estado | `ui/screens/workout/WorkoutsUiState.kt` | `sealed interface WorkoutsUiState` con `data object Loading`, `data class Content(val workouts: List<Workout>)` y `data object Error` |
| ViewModel | `ui/screens/workout/WorkoutsViewModel.kt` | `@HiltViewModel class WorkoutsViewModel @Inject constructor(private val repository: WorkoutRepository) : ViewModel()` con `val uiState: StateFlow<WorkoutsUiState>`. La carga se lanza en `init` con `viewModelScope` |

- `WorkoutRepository` es una interfaz para poder hacer un `FakeWorkoutRepository` en los tests del ViewModel.
- `WorkoutsUiState` es `sealed interface` para que la tarea de estados (RF05) lo extienda: `Empty` se suma sin tocar nada, y `Error` puede pasar a `data class Error(val error: AppError)` **sin romper los tests de esta spec**, porque comprueban `is WorkoutsUiState.Error` y nunca la igualdad con el objeto. Por la misma razón, ningún test de esta spec exige `Content(emptyList())`: la tarea de estados lo va a cambiar a `Empty`.

## Criterios de aceptación (EARS)

Cada línea es un test. Si no se te ocurre el test, la línea está mal escrita.

- C1. CUANDO se llama a `VoraApi.getWorkouts()`, la petición DEBE ser `GET` con path exactamente `/v1/workouts`, sin parámetros (RF01).
- C2. CUANDO el servidor responde el cuerpo de la sección "Datos", Gson DEBE convertirlo en 3 `WorkoutDto` iguales a los del JSON, en el mismo orden (RF01, RF06).
- C3. La conversión `WorkoutDto.toDomain()` DEBE conservar `id`, `name`, `durationMinutes` y `exerciseCount`, y traducir `"beginner"`, `"intermediate"` y `"advanced"` a su `WorkoutLevel` (RF06).
- C4. SI cualquiera de los 5 campos de `WorkoutDto` es nulo, ENTONCES `toDomain()` DEBE fallar con `IllegalArgumentException` y no inventar un valor (RF06).
- C5. SI `level` no es exactamente `"beginner"`, `"intermediate"` o `"advanced"` (por ejemplo `"pro"` o `"Beginner"`), ENTONCES `toDomain()` DEBE fallar con `IllegalArgumentException` (RF06).
- C6. CUANDO el servidor responde 200 con el cuerpo de "Datos", el repositorio DEBE devolver `Result.success` con 3 `Workout` en el mismo orden (w01, w02, w03) (RF01, RF06).
- C7. CUANDO el servidor responde 200 con `{"data": []}`, el repositorio DEBE devolver `Result.success` con una lista vacía, no un fallo (RF01).
- C8. SI el servidor responde un código fuera de 200-299 (por ejemplo 503), ENTONCES el repositorio DEBE devolver `Result.failure` sin lanzar la excepción (RF06).
- C9. SI la respuesta es 2xx y el cuerpo es nulo, o `data` es nulo, o algún entrenamiento no se puede convertir (C4, C5), ENTONCES el repositorio DEBE devolver `Result.failure` para **toda** la consulta, sin entrenamientos sueltos (RF06).
- C10. SI la consulta lanza una `IOException` (servidor apagado, o el JSON roto del escenario `malformed`, que es `MalformedJsonException`), ENTONCES el repositorio DEBE devolver `Result.failure` sin lanzarla (RF06).
- C11. SI el servidor responde 200 con un JSON bien formado pero de otro tipo (`{"data": {"id": "w01"}}`, que en Gson es `JsonSyntaxException` y no `IOException`), ENTONCES el repositorio DEBE devolver `Result.failure` sin lanzarla (RF06).
- C12. SI la coroutine que llama a `getWorkouts` se cancela, ENTONCES el repositorio DEBE relanzar la `CancellationException` y no devolver `Result.failure` (RF07: un ViewModel destruido no tiene que recibir un error).
- C13. CUANDO se crea el ViewModel, su estado DEBE empezar en `Loading` y pasar a `Content` con la lista que devolvió el repositorio (RF07).
- C14. SI el repositorio devuelve `Result.failure`, ENTONCES el estado del ViewModel DEBE pasar a `Error` y no quedar en `Loading` (RF07).
- C15. CUANDO el estado del mismo ViewModel se deja de observar y se vuelve a observar con más de 5 s de tiempo virtual entre una y otra (como al rotar), el repositorio DEBE haber sido llamado una sola vez y el estado DEBE seguir siendo `Content` con la misma lista (RF07).
- C16. La app DEBE compilar con `./gradlew assembleDebug` con `WorkoutsViewModel` inyectable: Hilt valida el grafo `WorkoutsViewModel` → `WorkoutRepository` → `VoraApi` al compilar (RF08).

Plantillas: **La app DEBE** (siempre) · **CUANDO** (ante un evento) · **MIENTRAS** (durante un estado) · **SI ... ENTONCES** (algo sale mal) · **DONDE** (opcional, para ampliaciones).

Casos de la plantilla y cómo se cubren acá:
- Sin conexión, 503 y JSON inválido: acá solo "no se cae y devuelve fallo" (C8, C10, C11). Un servidor más lento que el `readTimeout` termina en `SocketTimeoutException`, que es `IOException`, y cae en C10. Distinguir cuál fue el error, con su mensaje y reintento, es de la tarea de estados.
- Lista vacía: el repositorio la devuelve como éxito (C7). El estado `Empty` es de la tarea de estados.
- 404: no aplica al catálogo, que no lleva id.
- Rotación: C15 en tests. La rotación real en el emulador se verifica en la tarea de pantallas.

## Fuera de alcance
- **Tarea de estados (RF05):** errores propios (`AppError`: sin conexión, servidor, no encontrado, datos inválidos, desconocido), atrapar el JSON roto antes que `IOException`, estado `Empty`, mensajes de error en `strings.xml` y `onRetry()`. Acá `Error` es uno solo y sin mensaje.
- **Caché en Room y "no disponible sin conexión" (RF02):** tarea j. Ese repositorio va a sumar `WorkoutDao`.
- **Detalle `GET /v1/workouts/{workoutId}` con ejercicios (RF01 parte 2, NAV04):** otra tarea, con los nombres reservados en "Datos".
- **Pantallas Workout 01 a 07 y sus textos (UI01):** tarea "Workout: las 7 pantallas fieles al Figma". Acá no se toca `strings.xml`, ninguna pantalla, la navegación ni `MainActivity`.
- **Filtro `?level=`:** ninguna pantalla lo usa.
- **Rotación real en el emulador (un solo `GET` en el log al girar):** se verifica en la tarea de pantallas, cuando una pantalla use el ViewModel.

## Tests
| Criterio | Test | Dónde | Vi el rojo |
|---|---|---|---|
| C1 | `` `C1 catalogo getWorkouts pide GET v1 workouts sin parametros` `` | `test/` · `WorkoutApiTest` (MockWebServer) | Pasa en rojo (la ruta la fija `@GET`): lleva control negativo |
| C2 | `` `C2 catalogo Gson convierte el JSON del mock en tres WorkoutDto` `` | `test/` · `WorkoutApiTest` (MockWebServer) | Pasa en rojo (Gson y el DTO): lleva control negativo |
| C3 | `` `C3 catalogo toDomain conserva los campos y traduce los tres niveles` `` | `test/` · `WorkoutMappersTest` | Sí, 21/10: `expected Workout(id=w01...) but was Workout(id=...)` |
| C4 | `` `C4 catalogo cada campo nulo del DTO lanza IllegalArgumentException` `` (5 casos, uno por campo, con mensaje por campo) | `test/` · `WorkoutMappersTest` | Sí, 21/10: `nothing was thrown` |
| C5 | `` `C5 catalogo un nivel desconocido o con mayusculas lanza IllegalArgumentException` `` (`"pro"` y `"Beginner"`) | `test/` · `WorkoutMappersTest` | Sí, 21/10: `nothing was thrown` |
| C6 | `` `C6 catalogo con 200 devuelve los tres Workout en orden` `` | `test/` · `WorkoutRepositoryImplTest` (MockWebServer) | Sí, 21/10: `but was []` |
| C7 | `` `C7 catalogo con data vacia devuelve exito con lista vacia` `` | `test/` · `WorkoutRepositoryImplTest` (MockWebServer) | Pasa en rojo (el stub ya devuelve lista vacía): lleva control negativo |
| C8 | `` `C8 catalogo con 503 devuelve failure sin lanzar` `` | `test/` · `WorkoutRepositoryImplTest` (MockWebServer) | Sí, 21/10: `fue Success([])` |
| C9 | `` `C9 catalogo sin data falla toda la consulta` `` y `` `C9 catalogo con un item invalido falla toda la consulta` `` | `test/` · `WorkoutRepositoryImplTest` (MockWebServer) | Sí, 21/10, los dos: `fue Success([])` |
| C10 | `` `C10 catalogo con servidor apagado devuelve failure sin lanzar` `` y `` `C10 catalogo con JSON roto devuelve failure sin lanzar` `` | `test/` · `WorkoutRepositoryImplTest` (MockWebServer) | Sí, 21/10, los dos: `fue Success([])` |
| C11 | `` `C11 catalogo con JSON de otro tipo devuelve failure sin lanzar` `` | `test/` · `WorkoutRepositoryImplTest` (MockWebServer) | Sí, 21/10: `fue Success([])` |
| C12 | `` `C12 catalogo al cancelar relanza CancellationException` `` | `test/` · `WorkoutRepositoryImplTest` (MockWebServer con demora) | Sí, 21/10, pero en la condición previa (el stub no llama a la API): lleva control negativo con `runCatching` |
| C13 | `` `C13 catalogo arranca en Loading y pasa a Content con la lista` `` | `test/` · `WorkoutsViewModelTest` (`FakeWorkoutRepository`) | Sí, 21/10: `but was Loading` |
| C14 | `` `C14 catalogo si el repositorio falla pasa a Error` `` | `test/` · `WorkoutsViewModelTest` (`FakeWorkoutRepository`) | Sí, 21/10: `fue Loading` |
| C15 | `` `C15 catalogo volver a observar no repite la consulta ni pierde la lista` `` | `test/` · `WorkoutsViewModelTest` (`FakeWorkoutRepository`) | Sí, 21/10, en la primera observación (`but was Loading`): la consulta única lleva control negativo con `WhileSubscribed` |
| C16 | `./gradlew assembleDebug` (no es un test unitario: la salida va a la evidencia) | build | Pasa en rojo (el `@Binds` es contrato): lleva control negativo |

- Todo va en `test/`: no se usa Room, así que nada va en `androidTest/` y `control-rapido.sh` corre todos. Los nombres empiezan con "catalogo" para no confundirse con los `C1` y `C2` de `VoraApiTest`, que son de VC-f.
- Los tests de red arman Retrofit con `NetworkModule.provideRetrofit(OkHttpClient(), server.url("/").toString())`, igual que `VoraApiTest`, así se prueba el mismo conversor que usa la app.
- C10: servidor apagado = `server.shutdown()` antes de la consulta; JSON roto = cuerpo `{"data": INVALID_JSON`, como manda el escenario `malformed` (`specs/API.md`).
- C12: MockWebServer con `setBodyDelay` largo; el test lanza `getWorkouts` en un `Job`, lo cancela y comprueba que terminó cancelado y no con un `Result`. El control negativo es atrapar con `runCatching` o `catch (e: Exception)`, que se tragan la cancelación.
- `MainDispatcherRule` (nueva, en `app/src/test/.../util/`, la va a usar también la tarea de estados) usa `StandardTestDispatcher` por defecto.
- C13: el fake retiene la respuesta con un `CompletableDeferred`. El test crea el ViewModel, comprueba `uiState.value == Loading`, completa el deferred, hace `advanceUntilIdle()` y comprueba `Content(lista)`.
- C14 compara con `is WorkoutsUiState.Error`, nunca por igualdad (ver "Contrato").
- C15: el fake cuenta las llamadas. Se observa en `backgroundScope`, se cancela ese colector, se avanza más de 5 s de tiempo virtual y se vuelve a observar. El control negativo es mover la carga a `flow { }.stateIn(viewModelScope, WhileSubscribed(5_000), Loading)`, que vuelve a consultar.

## Auditoría
- auditor-spec (2026-10-21): 15 hallazgos, ninguno bloquea; el alcance parcial no rompe la consigna.
  - Corregidos en la spec: #1 (C11, JSON de otro tipo), #2 (`Error` se compara con `is`, ningún test exige `Content(emptyList())`), #3 (C15 reescrito: no repite la consulta ni pierde la lista, con control negativo), #4 (evidencia del hito 1), #5 (`StandardTestDispatcher` y pasos de C13), #6 (C4 con los 5 campos), #8 (`data class`), #9 (nombres reservados para el detalle), #10 (JSON literal y cita a `API.md`, porque `mock/` no está en el repo), #12 (C9 con cuerpo nulo), #13 (C12, cancelación), #14 (C1 nombra `VoraApi` y los tests empiezan con "catalogo") y #15 (nombres exactos de los frames).
  - Decididos por la IA, para que Hector confirme: #7 (`level` distingue mayúsculas, como el contrato) y #11 (`@Singleton` en el `@Binds`, igual que el de comidas, y C16 con `assembleDebug` como evidencia de RF08).

## Historial
- 2026-10-21. Spec creada para la tarea g (hito 1). En la entrevista: un dato inválido hace fallar toda la consulta y no va el filtro por nivel. Errores propios, estado vacío y reintento quedan para la tarea de estados, para no pisarse.
- 2026-10-21. Tras la auditoría: se suman C11 (JSON de otro tipo), C12 (cancelación) y C16 (Hilt compila); C13 a C15 pasan a ser los del ViewModel, con C15 reescrito para la rotación; se precisan C1, C4, C5 y C9.
