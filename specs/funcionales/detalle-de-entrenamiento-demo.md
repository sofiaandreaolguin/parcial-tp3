# Detalle de entrenamiento con datos de demostración

Flujo: Workout
Requisitos: NAV04, RF01 (parcial: la forma del detalle, sin el `GET`), RF06 (parcial: interfaz del repositorio), RF07 (parcial: carga y rotación del detalle), RF08 (parcial: el `@Binds`)
Pantallas: `Workout / 03 — Workout Details`, `Workout / 04 — Pre Workout` y `Workout / 06 — Exercise Details` van a usar `WorkoutDetailViewModel`. Esta spec no dibuja ninguna: es el PR 3 de 10 de la tarea k y deja el estado listo para que esas tres pantallas lo lean.
Responsable: Hector

## Qué hace
Cuando se abre el detalle de un entrenamiento, el ViewModel lee el `workoutId` de la ruta (y el `exerciseId`, si la ruta lo trae) y le pide ese detalle al repositorio. Por ahora el repositorio es de demostración: devuelve w01, w02 y w03 copiados del mock del profe. Así cada entrenamiento abre **su propio** detalle (NAV04) aunque todavía no exista la consulta `GET /v1/workouts/{id}`.

**Lo que cambia con el detalle real** (otra tarea del carril C, puede ir con la j): DTO, `@GET`, mappers y una implementación real de `WorkoutDetailRepository`. Después cambia **solo** la línea del `@Binds` y se borra la demo, con su test. Ni las pantallas ni el ViewModel ni sus tests se tocan; por eso la interfaz y los modelos tienen desde ahora la forma de `specs/API.md`.

**Esta spec no cumple RF01 parte 2.** La consigna pide el detalle funcional con Retrofit, y `PANTALLAS.md` marca 03 y 06 como Funcional. La tarea del detalle real es obligatoria antes de la entrega del 28/10; hasta que se una, 03, 04 y 06 muestran datos de demostración.

## Datos
- DTO: ninguno. Lo suma la tarea del detalle real (`WorkoutDetailDto`, `ExerciseDto`, nombres reservados en `catalogo-de-entrenamientos.md`).
- Modelo (`domain/model/WorkoutDetail.kt`), `data class` como `Workout`, con todos los campos obligatorios, como el contrato:
  - `WorkoutDetail(id: String, name: String, level: WorkoutLevel, durationMinutes: Int, exerciseCount: Int, description: String, exercises: List<Exercise>)`.
  - `Exercise(id: String, name: String, muscleGroup: String, sets: Int, reps: Int, restSeconds: Int, instructions: String, tip: String)`.
  - `WorkoutLevel` es el del catálogo (`domain/model/Workout.kt`).
- Entity: ninguna. La caché del detalle es de la tarea j (RF02).
- Datos de la demo: los tres entrenamientos de `fixtures.json` del mock del profe, que es de donde `server.py` arma `GET /v1/workouts/{id}`. **Los tres vienen completos ahí** (`examples.json` solo muestra w01), así que w02 y w03 no se inventan:

| Id | Nombre | Nivel | Min | Ejercicios (en orden) |
|---|---|---|---|---|
| `w01` | Fuerza inicial | beginner | 25 | e01 Sentadilla (Piernas), e02 Remo con mancuerna (Espalda), e03 Press con mancuernas (Pecho) |
| `w02` | Entrenamiento completo | intermediate | 35 | e04 Puente de glúteos (Piernas), e05 Press de hombros (Hombros), e06 Peso muerto con mancuernas (Piernas) |
| `w03` | Fuerza y constancia | beginner | 30 | e01 Sentadilla, e04 Puente de glúteos, e02 Remo con mancuerna |

  Los tres tienen `exerciseCount` 3 y `description` "Rutina de ejemplo para el trabajo práctico.". Los seis ejercicios, copiados de `fixtures.json` el 23/10 (`mock/` no está en el repo):

| Id | `name` | `muscleGroup` | `instructions` |
|---|---|---|---|
| `e01` | Sentadilla | Piernas | Separá los pies y flexioná las rodillas con control. |
| `e02` | Remo con mancuerna | Espalda | Apoyá una mano y llevá la mancuerna hacia el torso. |
| `e03` | Press con mancuernas | Pecho | Empujá las mancuernas con un movimiento controlado. |
| `e04` | Puente de glúteos | Piernas | Elevá la cadera sin arquear la espalda. |
| `e05` | Press de hombros | Hombros | Empujá hacia arriba manteniendo una postura estable. |
| `e06` | Peso muerto con mancuernas | Piernas | Llevá la cadera hacia atrás con las mancuernas cerca del cuerpo. |

  Todos con `sets` 3, `reps` 10, `restSeconds` 60 y `tip` "Usá una carga que puedas controlar.". Los ids de ejercicio se repiten entre entrenamientos (e01 está en w01 y w03, con los mismos datos): el ejercicio se busca **dentro** del entrenamiento elegido.

## Contrato

| Pieza | Archivo | Firma |
|---|---|---|
| Repositorio (interfaz) | `data/repository/WorkoutDetailRepository.kt` | `interface WorkoutDetailRepository { suspend fun getWorkoutDetail(id: String): Result<WorkoutDetail> }` |
| Repositorio (demo) | `data/repository/DemoWorkoutDetailRepository.kt` | `@Singleton class DemoWorkoutDetailRepository @Inject constructor() : WorkoutDetailRepository`, con KDoc que la marca como demostración, como `ProfileRepository` |
| Hilt | `di/RepositoryModule.kt` | `@Binds @Singleton abstract fun bindWorkoutDetailRepository(impl: DemoWorkoutDetailRepository): WorkoutDetailRepository` |
| Estado | `ui/screens/workout/WorkoutDetailUiState.kt` | `sealed interface WorkoutDetailUiState` con `data object Loading`, `data class Content(val detail: WorkoutDetail, val selectedExercise: Exercise? = null)` y `data object Error` |
| ViewModel | `ui/screens/workout/WorkoutDetailViewModel.kt` | `@HiltViewModel class WorkoutDetailViewModel @Inject constructor(savedStateHandle: SavedStateHandle, private val repository: WorkoutDetailRepository) : ViewModel()` con `val uiState: StateFlow<WorkoutDetailUiState>`. Lee `Rutas.ARG_WORKOUT_ID` y `Rutas.ARG_EXERCISE_ID`. La carga se lanza en `init` con `viewModelScope` |

- `Error` va sin mensaje y sin reintento, igual que en el catálogo: la tarea de estados (RF05) lo pasa a `data class Error(val error: AppError)` y suma `onRetry()`. Por eso los tests comparan con `is WorkoutDetailUiState.Error`, nunca por igualdad.
- C4, C6 y D3 son "no encontrado" cuando llegue la tarea de estados.
- Un solo ViewModel para 03, 04 y 06: 03 y 04 no traen `exerciseId` y dejan `selectedExercise` en `null`; 06 lo trae. Cada pantalla de la pila tiene su propia instancia (`hiltViewModel()`), así que la consulta se repite al navegar de 03 a 04 o 06, pero no al rotar (RF07). Con la demo no cuesta nada; con el `GET` real lo evita la caché de la tarea j.

## Criterios de aceptación (EARS)

Cada línea es un test. Si no se te ocurre el test, la línea está mal escrita.

Repositorio de demostración (se borra junto con la demo):

- D1. CUANDO se pide `getWorkoutDetail` con `"w01"`, `"w02"` o `"w03"`, la demo DEBE devolver `Result.success` con el `WorkoutDetail` de ese id igual, campo por campo y con los ejercicios en orden, al de las tablas de "Datos" (NAV04, RF01: nombre, nivel, duración y ejercicios con series, repeticiones e instrucciones).
- D2. En los tres detalles de la demo, `exerciseCount` DEBE ser igual a `exercises.size` (RF01: la demo no puede contradecir su propio resumen).
- D3. SI se pide un id que no existe (por ejemplo `"no-existe"`), ENTONCES la demo DEBE devolver `Result.failure` con una `NoSuchElementException`, sin lanzarla, como el 404 `WORKOUT_NOT_FOUND` del mock.

ViewModel (sobreviven al cambio por el detalle real):

- C1. CUANDO se crea el ViewModel con `workoutId` en la ruta, su estado DEBE empezar en `Loading` y pasar a `Content` con el detalle que devolvió el repositorio y `selectedExercise` nulo (RF07).
- C2. CUANDO se crea el ViewModel con `workoutId` `"w02"`, el repositorio DEBE recibir exactamente `"w02"` (NAV04: cada entrenamiento abre su propio detalle, no siempre el mismo).
- C3. CUANDO la ruta trae también `exerciseId` y ese ejercicio está en el detalle, `Content.selectedExercise` DEBE ser ese ejercicio (NAV04: la selección se mantiene hasta 06).
- C4. SI la ruta trae un `exerciseId` que no está en el detalle (también vacío o con solo espacios), ENTONCES el estado DEBE pasar a `Error` y no a `Content` (06 no tendría qué mostrar).
- C5. SI el repositorio devuelve `Result.failure`, ENTONCES el estado DEBE pasar a `Error` y no quedar en `Loading` (RF07).
- C6. SI la ruta no trae `workoutId`, o lo trae vacío o con solo espacios, ENTONCES el estado DEBE pasar a `Error` sin llamar al repositorio.
- C7. CUANDO el estado del mismo ViewModel se deja de observar y se vuelve a observar con más de 5 s de tiempo virtual entre una y otra (como al rotar), el repositorio DEBE haber sido llamado una sola vez y el estado DEBE seguir siendo el mismo `Content` (RF07).
- C8. La app DEBE compilar con `./gradlew assembleDebug` con `WorkoutDetailViewModel` inyectable: Hilt valida al compilar el grafo `WorkoutDetailViewModel` → `WorkoutDetailRepository` → `DemoWorkoutDetailRepository` (RF08).

Plantillas: **La app DEBE** (siempre) · **CUANDO** (ante un evento) · **MIENTRAS** (durante un estado) · **SI ... ENTONCES** (algo sale mal) · **DONDE** (opcional, para ampliaciones).

Casos de la plantilla y cómo se cubren acá:
- Sin conexión, 503 y JSON inválido: no aplican a la demo, que no usa la red. Para el ViewModel son todos un `Result.failure` del repositorio (C5); cuál fue el error lo distingue la tarea de estados.
- No encontrado (404): D3 en la demo; en el ViewModel es C5.
- Lista vacía: no aplica (el mock no cambia el detalle con `empty`, `specs/API.md`).
- Rotación: C7 en tests. La rotación real en el emulador se verifica cuando 03 use el ViewModel.

## Fuera de alcance
- **El detalle real** (`GET /v1/workouts/{workoutId}`, DTO, mappers, errores de red): otra tarea del carril C, ver "Qué hace".
- **Caché del detalle en Room (RF02):** tarea j.
- **Errores propios, mensaje y reintento (RF05):** tarea de estados.
- **Pantallas 03, 04 y 06, sus textos y la navegación:** PR 4, 5 y 6 de la tarea k. Acá no se toca `strings.xml`, ninguna pantalla, `WorkoutNavGraph` ni `MainActivity`.
- **La sesión de entrenamiento (05 y 07):** `ActiveWorkoutViewModel`, PR 7 de la tarea k.

## Tests
| Criterio | Test | Dónde | Vi el rojo |
|---|---|---|---|
| D1 | `` `D1 detalle demo devuelve w01 w02 y w03 cada uno con lo suyo` `` (compara por igualdad con el `WorkoutDetail` esperado, armado en el test desde las tablas) | `test/` · `data/repository/DemoWorkoutDetailRepositoryTest` | Sí, 23/10: `w01 tiene que ser exito, fue Failure(NotImplementedError)` |
| D2 | `` `D2 detalle demo exerciseCount coincide con la cantidad de ejercicios` `` | `test/` · `data/repository/DemoWorkoutDetailRepositoryTest` | Sí, 23/10: `w01 tiene que ser exito, fue Failure(NotImplementedError)` |
| D3 | `` `D3 detalle demo con id desconocido devuelve failure sin lanzar` `` | `test/` · `data/repository/DemoWorkoutDetailRepositoryTest` | Sí, 23/10: `tiene que fallar con NoSuchElementException, fue Failure(NotImplementedError)` |
| C1 | `` `C1 detalle arranca en Loading y pasa a Content sin ejercicio elegido` `` | `test/` · `ui/screens/workout/WorkoutDetailViewModelTest` (`FakeWorkoutDetailRepository`) | Sí, 23/10: `expected Content(...) but was Loading` |
| C2 | `` `C2 detalle pide al repositorio el workoutId de la ruta` `` | `test/` · `WorkoutDetailViewModelTest` | Sí, 23/10: `expected:<[w02]> but was:<[]>` |
| C3 | `` `C3 detalle con exerciseId elige ese ejercicio` `` | `test/` · `WorkoutDetailViewModelTest` | Sí, 23/10: `expected Content(..., selectedExercise=e05) but was Loading` |
| C4 | `` `C4 detalle con exerciseId que no esta pasa a Error` `` (`"e99"`, `""` y `"  "`) | `test/` · `WorkoutDetailViewModelTest` | Sí, 23/10: `con exerciseId "e99" tiene que ser Error, fue Loading` |
| C5 | `` `C5 detalle si el repositorio falla pasa a Error` `` | `test/` · `WorkoutDetailViewModelTest` | Sí, 23/10: `tiene que ser Error, fue Loading` |
| C6 | `` `C6 detalle sin workoutId pasa a Error sin llamar al repositorio` `` (sin la clave, `""` y `"  "`) | `test/` · `WorkoutDetailViewModelTest` | Sí, 23/10, en el estado (`sin la clave tiene que ser Error, fue Loading`). Que no llame al repositorio pasa en rojo: lleva control negativo |
| C7 | `` `C7 detalle volver a observar no repite la consulta ni pierde el estado` `` | `test/` · `WorkoutDetailViewModelTest` | Sí, 23/10, en la primera observación (`but was Loading`). La consulta única lleva control negativo con `WhileSubscribed` |
| C8 | `./gradlew assembleDebug` (no es un test unitario: la salida va a la evidencia) | build | Pasa en rojo (el `@Binds` es contrato y la firma vacía ya lo trae): lleva control negativo |

- Todo va en `test/`: no hay Room. Los nombres empiezan con "detalle" para no confundirse con los `C1`… del catálogo.
- El ViewModel se arma en el test con `SavedStateHandle(mapOf(Rutas.ARG_WORKOUT_ID to "w02"))`, sin Android.
- `FakeWorkoutDetailRepository` (en `test/.../ui/screens/workout/`): guarda los ids que recibe y retiene la respuesta con un `CompletableDeferred`, como `FakeWorkoutRepository`, para poder ver `Loading` (C1) y contar llamadas (C6, C7).
- El detalle de prueba del ViewModel se arma en el test, no se lee de la demo: así los tests del ViewModel no se rompen cuando se borra la demo.
- `MainDispatcherRule` ya existe en `test/.../util/` (`StandardTestDispatcher`).
- C7 sigue el mismo método que C15 del catálogo: observar en `backgroundScope`, cancelar, avanzar 6 s y volver a observar con `runCurrent()`.
- Controles negativos de los que pueden pasar en rojo:
  - C6: con la firma vacía nunca se llama al repositorio. Control: llamarlo con el id vacío y ver que falla.
  - C7: control con `flow { }.stateIn(viewModelScope, WhileSubscribed(5_000), Loading)`, que vuelve a consultar.
  - C8: si la firma vacía ya trae el `@Binds`, control sacándolo y viendo que el build falla.

## Auditoría
- auditor-spec (2026-10-23): 8 hallazgos, ninguno bloquea.
  - Corregidos: #1 (tabla literal de los seis ejercicios), #2 (D1 compara el `WorkoutDetail` completo), #4 (aviso de que no cumple RF01 parte 2), #5 (controles negativos de C6, C7 y C8), #6 (D3 con `NoSuchElementException`; C4, C6 y D3 son "no encontrado" para la tarea de estados), #7 (una instancia por pantalla) y #8 (D2 pasa a RF01).
  - #3 lo decidió la IA y Hector lo confirmó el 23/10: un `exerciseId` vacío o con espacios va a `Error`, dentro de C4.

## Historial
- 2026-10-23. Spec creada para el PR 3 de la tarea k (base del detalle con datos de demostración), a partir del plan aprobado. Los datos de la demo salen de `fixtures.json` del mock, que trae los tres entrenamientos completos.
- 2026-10-23. Hector aprueba la spec, con C4 y C6 como estaban. Tests en rojo (skill tests-rojo): los 10 fallan por el resultado, no por compilación.
- 2026-10-23. Implementación (skill implementar), sin tocar los tests: 59 tests en verde y controles negativos de C6, C7 y C8. Tras `/code-review`, la demo copia `exerciseCount` 3 del mock en vez de calcularlo, así D2 controla algo. Evidencia en `docs/evidencia/tests/2026-10-23-detalle-demo.md`.
