# Detalle de entrenamiento con Retrofit y caché en Room

Flujo: Workout
Requisitos: RF01 (parte 2: el detalle `GET /v1/workouts/{workoutId}`), RF02, RF05 (parcial: un fallo no borra la caché), RF06, RF07, RF08 (parcial: el `@Binds` del detalle real)
Pantallas: `Workout / 03 — Workout Details`, `Workout / 04 — Pre Workout` y `Workout / 06 — Exercise Details` (leen `WorkoutDetailViewModel`); `Workout / 01 — Dashboard` y `Workout / 02 — Weekly Plan` (leen `WorkoutsViewModel`, sin cambios visibles). `Workout / 05 — Active Workout` también usa `WorkoutDetailRepository` y pasa a leer el detalle real sin cambios.
Responsable: Hector

## Qué hace
Cuando se abre un entrenamiento, la app le pide el detalle al servidor (`GET /v1/workouts/{workoutId}`) y lo guarda en Room junto con sus ejercicios. El catálogo también se guarda cada vez que se descarga. **Siempre se intenta la red primero**: si la consulta falla por cualquier motivo (servidor apagado, 503, JSON roto) y lo pedido ya está en Room, se muestra lo guardado. Un detalle que nunca se descargó, con el servidor apagado, avisa que **todavía no está disponible sin conexión**. Ningún fallo borra lo guardado.

Reemplaza a `DemoWorkoutDetailRepository` (spec `detalle-de-entrenamiento-demo.md`), como esa spec preveía: cambia el `@Binds`, se borran la demo y su test, y el ViewModel, las pantallas y sus tests siguen igual (salvo lo nuevo de "no disponible sin conexión").

Al implementar, actualizar las specs vecinas sin tocar sus tests: en `detalle-de-entrenamiento-demo.md`, D1 a D3 pasan a retirados y C8 a reemplazado por C30 de esta spec; en `sesion-de-entrenamiento-demo.md`, lo mismo con su C25 (el grafo de Hilt ya no termina en la demo).

## Datos
- DTO (en `data/remote/dto/`), todo nulable, como `WorkoutDto` (Gson no respeta la nulabilidad de Kotlin):
  - `WorkoutDetailDto(id: String?, name: String?, level: String?, durationMinutes: Int?, exerciseCount: Int?, description: String?, exercises: List<ExerciseDto>?)`.
  - `ExerciseDto(id: String?, name: String?, muscleGroup: String?, sets: Int?, reps: Int?, restSeconds: Int?, instructions: String?, tip: String?)`.
- Modelo: `WorkoutDetail` y `Exercise` (`domain/model/WorkoutDetail.kt`) y `Workout` (`domain/model/Workout.kt`), **sin cambios**.
- Entity (en `data/local/entity/`):
  - `WorkoutEntity` suma `description: String? = null` al final (con valor por defecto, así `WorkoutDaoTest` sigue compilando). **`description == null` quiere decir "detalle nunca descargado"**: la fila llegó solo desde el catálogo.
  - `level` se guarda con el valor del contrato, en minúsculas (`"beginner"`), como ya hace `WorkoutDaoTest`, y se lee con el mismo `toWorkoutLevel()` del catálogo.
  - `WorkoutSummaryRow` (en `data/local/entity/`, no es tabla): solo las 5 columnas del resumen (`id`, `name`, `level`, `durationMinutes`, `exerciseCount`). Con `@Upsert(entity = WorkoutEntity::class)` actualiza esas columnas y deja `description` como estaba.
  - `WorkoutExerciseEntity` nueva, tabla `workout_exercises`: `workoutId: String`, `position: Int`, `exerciseId: String`, `name`, `muscleGroup`, `sets`, `reps`, `restSeconds`, `instructions`, `tip`. Clave primaria compuesta `(workoutId, position)`: el mismo ejercicio (e01) está en w01 y en w03, así que el id del ejercicio no sirve de clave.
- `VoraDatabase` suma `WorkoutExerciseEntity` y pasa de `version` 2 a 3. **Es zona de Sofi: avisarle antes del PR.** Con `fallbackToDestructiveMigration`, subir la versión borra en el emulador también las comidas guardadas (solo en desarrollo).
- Excepción propia (en `data/repository/WorkoutNotCachedException.kt`): `class WorkoutNotCachedException(val workoutId: String) : Exception(...)`. Es lo único de errores propios que suma esta spec (ver "Fuera de alcance").

Cuerpo exacto del detalle que usan los tests (datos del mock: `specs/API.md` y la tabla de `detalle-de-entrenamiento-demo.md`, copiada de `fixtures.json`):

```json
{"data": {
  "id": "w01", "name": "Fuerza inicial", "level": "beginner",
  "durationMinutes": 25, "exerciseCount": 3,
  "description": "Rutina de ejemplo para el trabajo práctico.",
  "exercises": [
    {"id": "e01", "name": "Sentadilla", "muscleGroup": "Piernas", "sets": 3, "reps": 10, "restSeconds": 60,
     "instructions": "Separá los pies y flexioná las rodillas con control.", "tip": "Usá una carga que puedas controlar."},
    {"id": "e02", "name": "Remo con mancuerna", "muscleGroup": "Espalda", "sets": 3, "reps": 10, "restSeconds": 60,
     "instructions": "Apoyá una mano y llevá la mancuerna hacia el torso.", "tip": "Usá una carga que puedas controlar."},
    {"id": "e03", "name": "Press con mancuernas", "muscleGroup": "Pecho", "sets": 3, "reps": 10, "restSeconds": 60,
     "instructions": "Empujá las mancuernas con un movimiento controlado.", "tip": "Usá una carga que puedas controlar."}
  ]
}}
```

El 404 del mock: `{"error": {"code": "WORKOUT_NOT_FOUND", "message": "Entrenamiento no encontrado."}}`.

## Contrato

| Pieza | Archivo | Firma |
|---|---|---|
| Endpoint | `data/remote/VoraApi.kt` | `@GET("v1/workouts/{workoutId}") suspend fun getWorkoutDetail(@Path("workoutId") workoutId: String): Response<ApiResponseDto<WorkoutDetailDto>>` |
| Mappers de red | `data/repository/WorkoutMappers.kt` | `fun WorkoutDetailDto.toDomain(): WorkoutDetail`, `fun ExerciseDto.toDomain(): Exercise` (reusan `toWorkoutLevel()`) |
| Mappers de Room | `data/repository/WorkoutMappers.kt` | `fun Workout.toEntity(): WorkoutEntity` (con `description` nulo), `fun WorkoutEntity.toDomain(): Workout`, `fun WorkoutDetail.toEntity(): WorkoutEntity`, `fun Exercise.toEntity(workoutId: String, position: Int): WorkoutExerciseEntity`, `fun WorkoutExerciseEntity.toDomain(): Exercise`, `fun WorkoutEntity.toDomain(exercises: List<WorkoutExerciseEntity>): WorkoutDetail` |
| DAO, operaciones básicas (abstractas) | `data/local/dao/WorkoutDao.kt` | Se conservan `observeAll()` y `upsertAll()` (los usa `WorkoutDaoTest`). Se suman: `@Query("SELECT * FROM workouts ORDER BY id") suspend fun getAll(): List<WorkoutEntity>`, `@Query("SELECT * FROM workouts WHERE id = :id") suspend fun getById(id: String): WorkoutEntity?`, `@Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY position") suspend fun getExercises(workoutId: String): List<WorkoutExerciseEntity>`, `@Upsert(entity = WorkoutEntity::class) suspend fun upsertSummaries(rows: List<WorkoutSummaryRow>)`, `@Upsert suspend fun upsertDetail(workout: WorkoutEntity)`, `@Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId") suspend fun deleteExercises(workoutId: String)` e `@Insert suspend fun insertExercises(exercises: List<WorkoutExerciseEntity>)` |
| DAO, operaciones compuestas (con cuerpo) | `data/local/dao/WorkoutDao.kt` | `@Transaction suspend fun saveSummaries(workouts: List<WorkoutEntity>)` llama a `upsertSummaries` (conserva el `description` que ya estaba). `@Transaction suspend fun saveDetail(workout: WorkoutEntity, exercises: List<WorkoutExerciseEntity>)` llama a `upsertDetail`, `deleteExercises` e `insertExercises` (reemplaza los ejercicios de ese entrenamiento). Son métodos con cuerpo en la interfaz: Room no acepta un `@Transaction` abstracto. **Sin `@ForeignKey` con `CASCADE`**: si alguna vez se usa `REPLACE`, la cascada borraría los ejercicios |
| Repositorio del catálogo | `data/repository/WorkoutRepositoryImpl.kt` | `class WorkoutRepositoryImpl @Inject constructor(private val api: VoraApi, private val dao: WorkoutDao) : WorkoutRepository`. La interfaz no cambia |
| Repositorio del detalle | `data/repository/WorkoutDetailRepositoryImpl.kt` | `class WorkoutDetailRepositoryImpl @Inject constructor(private val api: VoraApi, private val dao: WorkoutDao) : WorkoutDetailRepository`. La interfaz no cambia |
| Hilt | `di/RepositoryModule.kt` | `@Binds @Singleton abstract fun bindWorkoutDetailRepository(impl: WorkoutDetailRepositoryImpl): WorkoutDetailRepository`. Se borran `DemoWorkoutDetailRepository` y `DemoWorkoutDetailRepositoryTest` |
| Estado | `ui/screens/workout/WorkoutDetailUiState.kt` | Suma `data object NotAvailableOffline : WorkoutDetailUiState`. `Loading`, `Content` y `Error` no cambian |
| ViewModel | `ui/screens/workout/WorkoutDetailViewModel.kt` | Misma firma. Un `failure` con `WorkoutNotCachedException` pasa a `NotAvailableOffline`; cualquier otro, a `Error` |
| Pantallas | `WorkoutDetailsScreen`, `PreWorkoutScreen`, `ExerciseDetailsScreen` | Una rama más en el `when` del estado: `NotAvailableOffline` se dibuja **igual que el `Error` de cada pantalla, cambiando solo el texto** por `R.string.workout_not_available_offline`. 03 y 06 con `VoraInfoMessage(icon = ic_info)`; 04 (fondo oscuro) con `VoraCardOnDark` y `Text`. No se toca `ui/components/` |
| Textos | `res/values/strings.xml` y `res/values-es/strings.xml` | `workout_not_available_offline`: `This workout isn\'t available offline yet.` (con el apóstrofo escapado, si no el build falla) / `Este entrenamiento todavía no está disponible sin conexión.` |

- El repositorio decide de dónde sale el dato. El ViewModel no sabe si vino de la red o de Room: el Figma no dibuja un aviso de "datos guardados" y no se inventan estados.
- `Error` sigue siendo `data object`: la tarea r lo cambia. Los tests siguen comparando con `is ...Error`.
- `WorkoutDao` es interfaz: los tests de los repositorios usan un `FakeWorkoutDao` en memoria (en `test/.../data/repository/`), así no necesitan emulador. El falso implementa **solo las operaciones básicas** y **hereda** `saveSummaries` y `saveDetail`, así la lógica real de esas dos corre también en `test/`. Contra Room de verdad se prueban en `androidTest/`.
- **"Sin conexión"** es una `IOException` que no es `MalformedJsonException` ni `EOFException`. Las dos últimas las tira Gson con un JSON roto (`EOFException` con un cuerpo cortado en medio de un objeto) aunque el servidor respondió. Se atrapan antes que `IOException` (`specs/CONTEXTO.md`, sección 2). Un timeout (`SocketTimeoutException`) cuenta como sin conexión.

## Criterios de aceptación (EARS)

Cada línea es un test. Si no se te ocurre el test, la línea está mal escrita.

Red y mappers:

- C1. CUANDO se llama a `VoraApi.getWorkoutDetail("w01")`, la petición DEBE ser `GET` con path exactamente `/v1/workouts/w01`, sin parámetros (RF01).
- C2. CUANDO el servidor responde el cuerpo del detalle de "Datos", Gson DEBE convertirlo en un `WorkoutDetailDto` igual al JSON, con 3 `ExerciseDto` en el mismo orden (RF01, RF06).
- C3. La conversión `WorkoutDetailDto.toDomain()` DEBE dar el `WorkoutDetail` de w01 igual campo por campo, con `level` `BEGINNER` y los 3 `Exercise` en orden con series, repeticiones e instrucciones (RF01, RF06).
- C4. SI cualquiera de los 7 campos de `WorkoutDetailDto` o de los 8 de un `ExerciseDto` es nulo, ENTONCES `toDomain()` DEBE fallar con `IllegalArgumentException` y no inventar un valor (RF06).
- C5. SI el `level` del detalle no es exactamente `"beginner"`, `"intermediate"` o `"advanced"`, ENTONCES `WorkoutDetailDto.toDomain()` DEBE fallar con `IllegalArgumentException` (RF06).
- C6. Pasar un `WorkoutDetail` a Entity (`toEntity()` y `Exercise.toEntity(workoutId, position)` con posiciones 0, 1, 2) y volver (`WorkoutEntity.toDomain(exercises)`) DEBE dar un `WorkoutDetail` igual al original, con `level` guardado como `"beginner"`; y `Workout.toEntity()` DEBE dejar `description` nulo y volver con `toDomain()` igual al `Workout` original (RF06).
- C7. SI `WorkoutEntity.toDomain(exercises)` recibe una fila con `description` nulo, ENTONCES DEBE fallar con `IllegalArgumentException` (RF06: una fila que solo vino del catálogo no es un detalle).

Room (`androidTest/`):

- C8. CUANDO se guarda un detalle con `saveDetail`, `getById` DEBE devolver la fila con su `description` y `getExercises` los 3 ejercicios ordenados por `position` (RF02).
- C9. CUANDO se vuelve a guardar el mismo detalle con otros ejercicios, `getExercises` DEBE devolver solo los nuevos, sin restos de los anteriores (RF02).
- C10. CUANDO se guarda el catálogo con `saveSummaries` después de un detalle ya guardado, la fila DEBE conservar su `description` y sus ejercicios, y tomar los datos nuevos del resumen (RF02: refrescar el catálogo no "olvida" un detalle descargado).
- C11. CUANDO se guarda con `saveSummaries` un entrenamiento que no estaba, su `description` DEBE quedar nulo (RF02: llegó solo desde el catálogo).

Repositorio del catálogo (`WorkoutRepositoryImpl`):

- C12. CUANDO el servidor responde 200 con el catálogo, el repositorio DEBE guardarlo en Room (con `saveSummaries`) y devolver `Result.success` con los 3 `Workout` en el orden del servidor (RF01, RF02).
- C13. SI la consulta del catálogo devuelve cualquier fallo (servidor apagado, código fuera de 200-299, cuerpo o `data` nulo, JSON roto o de otro tipo, ítem inválido) y Room tiene entrenamientos, ENTONCES el repositorio DEBE devolver `Result.success` con los de Room, ordenados por `id` (RF02).
- C14. SI la consulta del catálogo falla y Room no tiene entrenamientos, ENTONCES el repositorio DEBE devolver `Result.failure` sin lanzar (RF05, RF06). Lo cubren los C8 a C11 del catálogo, que siguen con la caché vacía.
- C15. CUANDO el servidor responde 200 con `{"data": []}` y Room tiene entrenamientos, el repositorio DEBE devolver `Result.success` con lista vacía y Room DEBE seguir teniendo los que tenía (RF05: un vacío del servidor no borra la caché).
- C16. SI la consulta del catálogo falla, ENTONCES Room DEBE quedar igual que antes de la consulta (RF05).
- C17. SI guardar en Room lanza una excepción después de un 200 válido, ENTONCES el repositorio del catálogo DEBE devolver `Result.success` con lo que llegó de la red, sin lanzar (RF05: un problema de la caché no tapa un dato bueno).
- C18. SI leer o convertir lo de Room lanza durante el respaldo, ENTONCES el repositorio del catálogo DEBE devolver `Result.failure` sin lanzar (RF05).

Repositorio del detalle (`WorkoutDetailRepositoryImpl`):

- C19. CUANDO el servidor responde 200 con el detalle de w01, el repositorio DEBE guardarlo en Room (fila con `description` y sus 3 ejercicios) y devolver `Result.success` con ese `WorkoutDetail` (RF01, RF02).
- C20. SI la consulta del detalle devuelve cualquier fallo salvo 404 (servidor apagado, 503 u otro código fuera de 200-299, cuerpo o `data` nulo, JSON roto o de otro tipo, detalle inválido) y ese detalle ya está en Room (`description` no nulo), ENTONCES el repositorio DEBE devolver `Result.success` con el detalle de Room, igual al que se había descargado (RF02).
- C21. SI no hay conexión (una `IOException` que no es `MalformedJsonException` ni `EOFException`) y ese detalle nunca se descargó (no está en Room, o está solo con `description` nulo porque vino del catálogo), ENTONCES el repositorio DEBE devolver `Result.failure` con `WorkoutNotCachedException` para ese `workoutId` (RF02).
- C22. SI la consulta falla con 503, con JSON roto (`{"data": INVALID_JSON`, que da `MalformedJsonException`, o un cuerpo cortado como `{"data": {"id": "w01"`, que da `EOFException`), con JSON de otro tipo, con cuerpo o `data` nulo o con un detalle inválido (C4, C5), y ese detalle no está en Room, ENTONCES el repositorio DEBE devolver `Result.failure` **sin** `WorkoutNotCachedException` (RF02: "sin conexión" solo cuando de verdad no hubo conexión).
- C23. SI el servidor responde 404, ENTONCES el repositorio DEBE devolver `Result.failure` sin `WorkoutNotCachedException` y sin leer Room, aunque ese id esté guardado; y Room DEBE quedar igual (el entrenamiento no existe en el servidor; RF05: no se borra nada).
- C24. SI la consulta del detalle falla, ENTONCES Room DEBE quedar igual que antes de la consulta (RF05).
- C25. SI la coroutine que llama a `getWorkoutDetail` se cancela, ENTONCES el repositorio DEBE relanzar la `CancellationException` y no devolver un `Result` ni leer Room (RF07).
- C26. SI guardar en Room lanza una excepción después de un 200 válido, ENTONCES el repositorio del detalle DEBE devolver `Result.success` con lo que llegó de la red, sin lanzar (RF05).
- C27. SI leer o convertir lo de Room lanza durante el respaldo, ENTONCES el repositorio del detalle DEBE devolver `Result.failure` sin lanzar y sin `WorkoutNotCachedException` (RF05).

ViewModel, pantallas y build:

- C28. SI el repositorio devuelve `Result.failure` con `WorkoutNotCachedException`, ENTONCES el estado de `WorkoutDetailViewModel` DEBE pasar a `NotAvailableOffline`, no a `Error` ni quedar en `Loading` (RF02). Cualquier otro fallo sigue yendo a `Error` (C5 de `detalle-de-entrenamiento-demo.md`, sin cambios).
- C29. CUANDO el estado es `NotAvailableOffline`, las pantallas 03, 04 y 06 DEBEN mostrar el texto `workout_not_available_offline`, dibujado como el `Error` de cada pantalla (RF02). Se comprueba con captura en el emulador.
- C30. La app DEBE compilar con `./gradlew assembleDebug` con `WorkoutDetailRepository` atado a `WorkoutDetailRepositoryImpl` y sin `DemoWorkoutDetailRepository`: Hilt valida `WorkoutDetailViewModel` y `ActiveWorkoutViewModel` → `WorkoutDetailRepositoryImpl` → `VoraApi` y `WorkoutDao` (RF08).
- C31. CUANDO, con el catálogo y w01 ya descargados, se fuerza el cierre de la app, se apaga el servidor y se vuelve a abrir, 01 DEBE mostrar el primero del catálogo desde Room (y no `workout_error`; por diseño destaca solo ese), 02 DEBE mostrar los 3 entrenamientos y w01 su detalle desde Room; y w02, nunca abierto, DEBE mostrar `workout_not_available_offline` (RF02, prueba de la consigna). Se comprueba a mano con capturas, y va en la demostración final. Cerrar la app es lo que asegura que el catálogo sale de Room y no de un ViewModel que ya lo tenía en memoria.

Plantillas: **La app DEBE** (siempre) · **CUANDO** (ante un evento) · **MIENTRAS** (durante un estado) · **SI ... ENTONCES** (algo sale mal) · **DONDE** (opcional, para ampliaciones).

Casos de la plantilla y cómo se cubren acá:
- Sin conexión: C13 y C20 (con caché), C14 y C21 (sin caché). Un timeout (`SocketTimeoutException`) cuenta como sin conexión: con el servidor vivo pero lento, un detalle nunca abierto muestra el aviso. Por eso la demora de prueba (`api.mockDelayMs`) se usa hasta 3000 ms, por debajo de los 10 s de OkHttp.
- 503 y JSON inválido: con caché, C13 y C20; sin caché, C14 y C22.
- 404: C23.
- Lista vacía: C15. El estado `Empty` es de la tarea r.
- Falla de Room: C17, C18, C26 y C27.
- Rotación: sin cambios, la cubren C15 del catálogo y C7 del detalle demo (la carga sigue en `init`).

## Fuera de alcance
- **Tarea r, "Estados de carga, vacío y error con reintento" (RF05):** `AppError` y errores por tipo, estado `Empty`, `onRetry()`, botón de reintento, mensajes de error distintos y cambios en cómo 01 y 02 dibujan carga y error. **Para quien haga la r:**
  - `NotAvailableOffline` y `WorkoutNotCachedException` se pueden absorber en `Error(AppError.NotAvailableOffline)`; C28 y C29 tendrían que seguir valiendo.
  - Esta spec ya distingue "sin conexión" de "JSON roto" en `WorkoutDetailRepositoryImpl` (lo que `catalogo-de-entrenamientos.md` le dejaba a la r). La r reemplaza esa clasificación por `AppError` sin cambiar el resultado de C20 a C23.
  - Las dos tareas tocan `WorkoutDetailUiState.kt`, los `when` de 03, 04 y 06 y los `catch` de los dos repositorios. Orden de merge: **esta antes que la r**, avisándole a quien la tenga.
- **Avisar que lo que se ve viene de la caché:** no está en el Figma.
- **Borrar de Room entrenamientos que el servidor ya no manda:** el mock siempre manda los mismos tres. Refrescar solo agrega o actualiza.
- **Filtro `?level=`:** ninguna pantalla lo usa.
- **Cambiar los timeouts de OkHttp** (`NetworkModule`).
- **Pantallas nuevas o cambios visuales** fuera del aviso de C29.

## Tests
| Criterio | Test | Dónde | Vi el rojo |
|---|---|---|---|
| C1 | `` `C1 cache getWorkoutDetail pide GET v1 workouts w01` `` | `test/` · `WorkoutApiTest` (MockWebServer) | Pasó en rojo: la firma del endpoint ya es el contrato. Control negativo hecho el 25/10: con `@GET("v1/workout/{workoutId}")` falla con `expected:</v1/workout[s]/w01> but was:</v1/workout[]/w01>` |
| C2 | `` `C2 cache Gson convierte el detalle del mock en WorkoutDetailDto` `` | `test/` · `WorkoutApiTest` | Pasó en rojo: el DTO ya es el contrato. Control negativo hecho el 25/10: con `@SerializedName("musculo")` en `muscleGroup` falla con `expected:<WorkoutDetailDto(id=w01, …)> but was:<…>` |
| C3 | `` `C3 cache detalle toDomain conserva los campos y los ejercicios en orden` `` | `test/` · `WorkoutMappersTest` | Sí (25/10): `expected:<WorkoutDetail(id=w01, …)> but was:<WorkoutDetail(id=, …)>` |
| C4 | `` `C4 cache cada campo nulo del detalle o de un ejercicio lanza IllegalArgumentException` `` (15 casos) | `test/` · `WorkoutMappersTest` | Sí: `detalle con id nulo: expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown` |
| C5 | `` `C5 cache un nivel desconocido en el detalle lanza IllegalArgumentException` `` | `test/` · `WorkoutMappersTest` | Sí: `nivel pro: expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown` |
| C6 | `` `C6 cache detalle y resumen van a Entity y vuelven iguales` `` (con `level` `"beginner"` fijo en la Entity) | `test/` · `WorkoutMappersTest` | Sí: `expected:<WorkoutEntity(id=w01, …, level=beginner, …)> but was:<WorkoutEntity(id=, name=, level=, …)>` |
| C7 | `` `C7 cache una fila sin description no es un detalle` `` | `test/` · `WorkoutMappersTest` | Sí: `expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown` |
| C8 | `cache_C8_saveDetail_guarda_la_fila_y_los_ejercicios_en_orden` | `androidTest/` · `WorkoutDaoTest` | Sí (emulador, 25/10): `expected:<WorkoutEntity(id=w01, …)> but was:<null>` |
| C9 | `cache_C9_saveDetail_otra_vez_reemplaza_los_ejercicios` | `androidTest/` · `WorkoutDaoTest` | Sí: `expected:<[WorkoutExerciseEntity(…, exerciseId=e04, …), …]> but was:<[]>` |
| C10 | `cache_C10_saveSummaries_conserva_el_detalle_ya_guardado` | `androidTest/` · `WorkoutDaoTest` | Sí: `expected:<WorkoutEntity(id=w01, name=Fuerza inicial renovada, …, description=Rutina de ejemplo…)> but was:<null>`. Control (25/10): con `upsertAll(workouts)` en `saveSummaries` falla con `expected:<WorkoutEntity(…, description=Rutina de ejemplo…)> but was:<WorkoutEntity(…, description=null)>` |
| C11 | `cache_C11_saveSummaries_de_uno_nuevo_deja_description_nulo` | `androidTest/` · `WorkoutDaoTest` | Sí: `saveSummaries tiene que guardar la fila nueva` |
| C12 | `` `C12 cache catalogo con 200 guarda en Room y devuelve los tres` `` | `test/` · `WorkoutRepositoryImplTest` (MockWebServer y `FakeWorkoutDao`) | Sí: `expected:<[WorkoutEntity(id=w01, …, description=null), …]>`; Room queda vacía porque todavía no se guarda |
| C13 | `` `C13 cache catalogo que falla devuelve lo de Room` `` (servidor apagado, 503, cuerpo sin `data`, JSON roto, JSON de otro tipo, ítem inválido) | `test/` · `WorkoutRepositoryImplTest` | Sí: `con 503 expected:<Success([Workout(id=w01, …), Workout(id=w02, name=Entrenamiento guardado, …)])> but was:<Failure(…)>` |
| C14 | Los `C8` a `C11` del catálogo, con `FakeWorkoutDao` vacío | `test/` · `WorkoutRepositoryImplTest` | Ya en verde: lleva control negativo (devolver éxito vacío si Room está vacía). Control hecho el 25/10: devolver `Result.success(emptyList())` con Room vacía hace fallar los seis (`503 tiene que ser failure, fue Success([])`, y lo mismo con cuerpo nulo, ítem inválido, JSON roto, JSON de otro tipo y servidor apagado) |
| C15 | `` `C15 cache catalogo vacio del servidor no borra Room` `` | `test/` · `WorkoutRepositoryImplTest` | Pasó en rojo: el repositorio actual no toca Room. Control negativo en implementar: que el 200 vacío reemplace la tabla (por ejemplo, borrar las filas antes de `saveSummaries`). Control hecho el 25/10: borrar los ejercicios guardados antes de `saveSummaries` falla con `expected:<[WorkoutExerciseEntity(workoutId=w01, …)]> but was:<[]>` |
| C16 | `` `C16 cache catalogo que falla deja Room igual` `` | `test/` · `WorkoutRepositoryImplTest` | Pasó en rojo: el repositorio actual no toca Room. Control negativo en implementar: borrar la caché en el `catch` del fallo. Control hecho el 25/10: borrar los ejercicios en el `catch` falla con `ejercicios con JSON roto expected:<[WorkoutExerciseEntity(…)]> but was:<[]>` |
| C17 | `` `C17 cache catalogo si guardar en Room falla devuelve lo de la red` `` | `test/` · `WorkoutRepositoryImplTest` (`FakeWorkoutDao` que lanza al guardar) | Pasó en rojo: hoy no guarda en Room. Control negativo en implementar: guardar dentro del mismo `try` de la red, sin atrapar aparte el error de Room (devuelve `failure`). Control hecho el 25/10: guardar con `dao.saveSummaries` dentro del `try` de la red falla con `expected:<Success([Workout(id=w01, …)…])> but was:<Failure(…)>` |
| C18 | `` `C18 cache catalogo si leer Room falla devuelve failure sin lanzar` `` | `test/` · `WorkoutRepositoryImplTest` (`FakeWorkoutDao` que lanza al leer) | Pasó en rojo: hoy cualquier fallo de red ya es `failure`. Control negativo en implementar: leer Room fuera del `try` (el test lanza). Control hecho el 25/10: sin el `catch` al leer Room, el test lanza `IllegalStateException: Room falla al leer` |
| C19 | `` `C19 cache detalle con 200 guarda en Room y lo devuelve` `` | `test/` · `WorkoutDetailRepositoryImplTest` (MockWebServer y `FakeWorkoutDao`) | Sí: `expected:<Success(WorkoutDetail(id=w01, …))> but was:<Success(WorkoutDetail(id=, …))>` |
| C20 | `` `C20 cache detalle que falla devuelve lo de Room` `` (servidor apagado, 503, cuerpo sin `data`, JSON roto, JSON de otro tipo, detalle inválido) | `test/` · `WorkoutDetailRepositoryImplTest` | Sí: `con 503 expected:<Success(WorkoutDetail(id=w01, …))> but was:<Success(WorkoutDetail(id=, …))>` |
| C21 | `` `C21 cache detalle nunca descargado sin servidor falla con WorkoutNotCachedException` `` (sin fila, y con fila del catálogo con `description` nulo) | `test/` · `WorkoutDetailRepositoryImplTest` | Sí: `sin fila tiene que ser WorkoutNotCachedException, fue Success(WorkoutDetail(id=, …))` |
| C22 | `` `C22 cache detalle sin cache y sin falla de conexion no es WorkoutNotCachedException` `` (503, `INVALID_JSON`, cuerpo cortado, JSON de otro tipo, cuerpo sin `data`, detalle inválido) | `test/` · `WorkoutDetailRepositoryImplTest` | Sí: `con 503 tiene que ser failure, fue Success(WorkoutDetail(id=, …))`. Control hecho el 25/10: con el `catch (IOException)` antes que los de JSON roto falla con `con JSON roto no es falta de conexion, fue WorkoutNotCachedException`; sin el `catch (EOFException)`, con `con cuerpo cortado no es falta de conexion, fue WorkoutNotCachedException` |
| C23 | `` `C23 cache detalle con 404 falla sin leer Room y sin borrarlo` `` | `test/` · `WorkoutDetailRepositoryImplTest` | Sí: `404 tiene que ser failure, fue Success(WorkoutDetail(id=, …))` |
| C24 | `` `C24 cache detalle que falla deja Room igual` `` | `test/` · `WorkoutDetailRepositoryImplTest` | Pasó en rojo: la firma vacía no toca Room. Control negativo en implementar: borrar la fila o los ejercicios al fallar (por ejemplo, `deleteExercises` en el `catch`). Control hecho el 25/10: `deleteExercises(id)` al leer el respaldo falla con `ejercicios con 503 pidiendo w01 expected:<[WorkoutExerciseEntity(…)]> but was:<[]>` (y también C20) |
| C25 | `` `C25 cache detalle al cancelar relanza CancellationException` `` | `test/` · `WorkoutDetailRepositoryImplTest` (MockWebServer con demora) | Sí: `la consulta tiene que llegar al servidor antes de cancelarla` (la firma vacía no va a la red). Control negativo en implementar: `runCatching`, como prevé la spec. Control hecho el 25/10: sin el `catch (CancellationException)` falla con `al cancelar no tiene que devolver un Result, devolvio Success(WorkoutDetail(id=w01, …))` |
| C26 | `` `C26 cache detalle si guardar en Room falla devuelve lo de la red` `` | `test/` · `WorkoutDetailRepositoryImplTest` (`FakeWorkoutDao` que lanza al guardar) | Sí: `expected:<Success(WorkoutDetail(id=w01, …))> but was:<Success(WorkoutDetail(id=, …))>` |
| C27 | `` `C27 cache detalle si leer Room falla devuelve failure sin WorkoutNotCachedException` `` | `test/` · `WorkoutDetailRepositoryImplTest` (`FakeWorkoutDao` que lanza al leer) | Sí: `si falla leer Room con 503 tiene que ser failure, fue Success(WorkoutDetail(id=, …))` |
| C28 | `` `C28 cache detalle no descargado pasa a NotAvailableOffline` `` | `test/` · `WorkoutDetailViewModelTest` (`FakeWorkoutDetailRepository`) | Sí: `expected:<NotAvailableOffline> but was:<Error>` |
| C29 | Captura de 03, 04 y 06 en el emulador con el servidor apagado | `docs/evidencia/estados/` | No es test unitario. Hecho el 25/10: `workout-03-`, `workout-04-` y `workout-06-no-disponible-sin-conexion.png`. 06 solo se alcanza con un 03 con contenido: se restauró la pila tras matar el proceso y sacar w03 de la caché a mano |
| C30 | `./gradlew assembleDebug` | build | Control negativo: sacar el `@Binds` y ver que falla. Hecho el 25/10: sin el `@Binds` falla `hiltJavaCompileDebug` con `[Dagger/MissingBinding] com.example.parcial_tp3.data.repository.WorkoutDetailRepository cannot be provided without an @Provides-annotated method` |
| C31 | Prueba manual de la consigna con el mock local, con capturas | `docs/evidencia/estados/` | No es test unitario. Hecho el 25/10 con el mock local, cortando `adb reverse`: `workout-c31-*.png`: 01 con w01 y 02 con los tres, desde Room |

- Los nombres empiezan con "cache" para no confundirse con los `C1`… del catálogo, del detalle demo y de `WorkoutDaoTest`. En `androidTest/` van con guion bajo (con `minSdk` 29 el DEX no acepta espacios), como los que ya tiene `WorkoutDaoTest`.
- **Único cambio a un test existente, autorizado por Hector el 25/10:** en `WorkoutRepositoryImplTest.setUp()`, `WorkoutRepositoryImpl(api)` pasa a `WorkoutRepositoryImpl(api, FakeWorkoutDao())`. No se toca ninguna aserción; con la caché vacía, C6 a C12 del catálogo prueban lo mismo que antes. Se borra `DemoWorkoutDetailRepositoryTest` junto con la demo, como preveía su spec.
- `FakeWorkoutDao` (nuevo, `test/.../data/repository/`): implementa solo las operaciones básicas del DAO con mapas en memoria y **hereda** `saveSummaries` y `saveDetail`. `getAll()` devuelve ordenado por `id` y `getExercises()` por `position`, igual que las `@Query` reales. `upsertSummaries` conserva `description`, como el `@Upsert` parcial. `observeAll()` con un `MutableStateFlow`. Deja leer su contenido (C15, C16, C23, C24), cuenta las lecturas (C23, C25) y tiene dos interruptores para lanzar al guardar (C17, C26) o al leer (C18, C27).
- Servidor apagado = `server.shutdown()` antes de la consulta; JSON roto = cuerpo `{"data": INVALID_JSON` (el del escenario `malformed`) y cuerpo cortado `{"data": {"id": "w01"`; 503 = `MockResponse().setResponseCode(503)` con el cuerpo de error del mock; JSON de otro tipo = `{"data": [1, 2]}` en el detalle, como el C11 del catálogo.
- C25 sigue el método del C12 del catálogo: `setHeadersDelay(3, TimeUnit.SECONDS)`, cancelar el `Job` y comprobar que terminó cancelado. Control negativo: `runCatching`.
- C28: el detalle de prueba se arma en el test, como los demás de `WorkoutDetailViewModelTest`.
- Controles negativos a prever: C10 (usar `upsertAll` en `saveSummaries` borra el `description`), C21/C22 (atrapar `IOException` antes que `MalformedJsonException` y `EOFException` hace que el JSON roto dé `WorkoutNotCachedException`).

## Auditoría
- auditor-spec (2026-10-25): 14 hallazgos; uno bloqueaba (#1).
  - Corregidos: #1 (DAO con operaciones básicas abstractas, `saveSummaries` y `saveDetail` con cuerpo, `WorkoutSummaryRow` para el upsert parcial, sin `CASCADE`, y el falso hereda la lógica), #2 (C13, C20 y C22 con todos los fallos), #3 (C17, C18, C26 y C27: falla de Room), #4 ("sin conexión" excluye también `EOFException`, y C22 suma el cuerpo cortado), #5 (`level` en minúsculas en la Entity y C7 nuevo), #6 (C31 con la app cerrada a la fuerza), #7 (el aviso se dibuja como el `Error` de cada pantalla), #8 (lo que hereda la r y el orden de merge), #10 (`cache_` en los nombres de `androidTest/`), #11 (actualizar las specs vecinas), #12 (apóstrofo escapado), #13 (orden del falso) y #14 (el timeout cuenta como sin conexión; demora de prueba hasta 3000 ms; sin tocar `NetworkModule`).
  - #9: Hector confirmó el 25/10 las tres decisiones de la IA (ver "Historial").

## Historial
- 2026-10-25. Spec creada (Hector, con la skill spec-nueva) a partir del plan aprobado. En la entrevista: red primero y Room de respaldo ante cualquier fallo (sin conexión, 503, JSON roto); "no disponible sin conexión" solo con un error de conexión real; tablas `workouts` (con `description` nulo = nunca descargado) y `workout_exercises` con clave `(workoutId, position)`; el constructor del repositorio del catálogo suma el DAO y se cambia solo el `setUp` de su test. Errores propios, vacío y reintento quedan para la tarea r.
- 2026-10-25. Correcciones de la auditoría: los criterios pasan de 26 a 31 (C7 y los cuatro de falla de Room). La numeración cambia desde C7.
- 2026-10-25. Hector confirma las tres decisiones que había tomado la IA: el 404 no cae a la caché (C23, el entrenamiento no existe); el catálogo desde Room va ordenado por `id` (C13); un entrenamiento guardado solo desde el detalle también aparece en el catálogo sin conexión. Spec aprobada para los tests en rojo.
- 2026-10-25. Implementado (skill implementar): mappers, `saveSummaries` y `saveDetail`, los dos repositorios con red primero y Room de respaldo, `NotAvailableOffline` en el ViewModel y en 03, 04 y 06, y el `@Binds` al detalle real. 189 tests unitarios y los 6 de `WorkoutDaoTest` en verde; controles negativos en la tabla. Ningún test se modificó.
- 2026-10-25. Hector corrige el texto de C31: decía "01 y 02 DEBEN mostrar los 3 entrenamientos", pero 01 por diseño muestra solo el primero del catálogo (`TodayPanel` con `firstOrNull()`). Ahora 01 muestra el primero desde Room y 02 los 3. No cambia código ni tests.
