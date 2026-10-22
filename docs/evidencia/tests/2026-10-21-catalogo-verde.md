# Tarea g · Catálogo de entrenamientos · Implementación en verde (21/10/2026)

Tarea: primera consulta Retrofit, `GET /v1/workouts` con DTO separado del modelo (RF01, RF06, RF07, RF08). Rama `feat/workout-catalogo-retrofit`. Spec: `specs/funcionales/catalogo-de-entrenamientos.md`. Rojo: `docs/evidencia/tests/2026-10-21-catalogo-rojo.md`.

## Código

- `data/repository/WorkoutMappers.kt`: `WorkoutDto.toDomain()` con `requireNotNull` por campo (lanza `IllegalArgumentException` con el nombre del campo) y un `when` exacto para `level` (`"beginner"`, `"intermediate"`, `"advanced"`; cualquier otro, `"Beginner"` incluido, lanza `IllegalArgumentException`). C3, C4, C5.
- `data/repository/WorkoutRepositoryImpl.kt`: `try` con `api.getWorkouts()`; código fuera de 2xx o `body()?.data` nulo dan `Result.failure`; si no, `Result.success(data.map { it.toDomain() })`, así un ítem inválido hace fallar toda la consulta. `catch (e: CancellationException) { throw e }` y después `catch (e: Exception) { Result.failure(e) }` (opción B, elegida por Hector: cubre el error "desconocido" de `CONTEXTO.md` y la promesa de la interfaz). C6 a C12.
- `ui/screens/workout/WorkoutsViewModel.kt`: `MutableStateFlow(Loading)` privado expuesto con `asStateFlow()`; la carga se lanza una sola vez en `init` con `viewModelScope.launch` y `fold` a `Content` o `Error`. Importa solo `data.repository`. C13, C14, C15.
- `di/RepositoryModule.kt`: sin cambios (el `@Binds` ya estaba desde el rojo). C16.

## Cambios en dos tests, autorizados por Hector

Los dos los autorizó Hector en la sesión. Ninguno debilita lo que prueba el test: el de C12 arregla el `tearDown` y el de C15 lo hace más estricto.

### C12 · demora de MockWebServer

`WorkoutRepositoryImplTest`, C12: `setHeadersDelay(10, TimeUnit.SECONDS)` pasó a `setHeadersDelay(3, TimeUnit.SECONDS)`. Lo autorizó Hector en la sesión ("Dale, cambiá setHeadersDelay(10, SECONDS) por setHeadersDelay(3, SECONDS) en C12. Lo autorizo yo").

Motivo: con la implementación, C12 cumplía sus tres aserciones, pero fallaba el `tearDown`:

```text
WorkoutRepositoryImplTest > C12 catalogo al cancelar relanza CancellationException FAILED
    java.io.IOException at WorkoutRepositoryImplTest.kt:48
java.io.IOException: Gave up waiting for queue to shut down
	at okhttp3.mockwebserver.MockWebServer.shutdown(MockWebServer.kt:454)
34 tests completed, 1 failed
```

El hilo de MockWebServer 4.12 duerme la demora completa aunque el cliente cancele, y `shutdown()` espera a lo sumo 5 s. En el rojo no se veía porque el stub no llamaba a la API. Con 3 s la demora sigue siendo mucho mayor que lo que tarda la cancelación y queda debajo del límite del `shutdown()`. Ahora C12 tarda 3,0 s. Lo que prueba no cambió.

### C15 · segunda observación

`WorkoutsViewModelTest`, C15, línea 73: después de lanzar la segunda observación, `advanceUntilIdle()` pasó a `runCurrent()`. Lo autorizó Hector en la sesión. El motivo y la verificación están en "C15 no detectaba `WhileSubscribed`", más abajo.

## Unitarios: 34 tests, 0 fallas, 0 errores

Comando: `./gradlew testDebugUnitTest assembleDebug --rerun-tasks`.

```text
> Task :app:hiltJavaCompileDebug
BUILD SUCCESSFUL in 1m
```

| Clase | Tests | Fallan | Errores |
|---|---|---|---|
| `WorkoutApiTest` | 2 | 0 | 0 |
| `WorkoutMappersTest` | 3 | 0 | 0 |
| `WorkoutRepositoryImplTest` | 9 | 0 | 0 |
| `WorkoutsViewModelTest` | 3 | 0 | 0 |
| Las 7 clases que ya estaban | 17 | 0 | 0 |

`scripts/reglas.sh`: sale con 0 (la UI no importa `retrofit2`, `data.remote` ni `data.local`).

## C16 · `./gradlew assembleDebug`

`BUILD SUCCESSFUL`, con `hiltJavaCompileDebug` ejecutado (no `UP-TO-DATE`): Hilt valida el grafo `WorkoutsViewModel` → `WorkoutRepository` → `VoraApi`.

## Controles negativos

Los que pidió la evidencia del rojo (C1, C2, C7 y C16 pasaron en rojo; C12 y C15 fallaron antes de llegar a lo que prueban). En cada uno se rompió el código a propósito, se vio el resultado y se restauró la copia (comprobado con `diff`: idéntico).

| Criterio | Cambio a propósito | Resultado | Vuelta atrás |
|---|---|---|---|
| C1 | `VoraApi`: `@GET("v1/workouts")` por `@GET("v1/workout")` | `2 tests completed, 1 failed`: **C1 falla**, `expected:</v1/workout[s]> but was:</v1/workout[]>` | Restaurado |
| C2 | `WorkoutDto`: `@SerializedName("duration")` en `durationMinutes` (el campo se llama distinto en el JSON) | `2 tests completed, 1 failed`: **C2 falla**, `expected:<[WorkoutDto(... durationMinutes=25 ...` (Gson deja `null`) | Restaurado |
| C7 | `WorkoutRepositoryImpl`: `workouts.isEmpty() -> Result.failure(...)` | `9 tests completed, 1 failed`: **C7 falla**, `expected:<Success([])> but was:<Failure(java.lang.IllegalStateException: Lista vacia)>` | Restaurado |
| C12 | `WorkoutRepositoryImpl`: se borra el `catch (e: CancellationException) { throw e }` (queda solo `catch (e: Exception)`) | `9 tests completed, 1 failed`: **C12 falla**, `al cancelar no tiene que devolver un Result, devolvio Failure(kotlinx.coroutines.JobCancellationException: StandaloneCoroutine was cancelled ...` | Restaurado |
| C15 | `WorkoutsViewModel`: `flow { emit(...) }.stateIn(viewModelScope, WhileSubscribed(5_000), Loading)` sin `init` | Con el test original: `3 tests completed, 2 failed`, **fallan C13 y C14** (`tiene que ser Error, fue Loading`) y **C15 pasa** (ver abajo). Con el test corregido: `3 tests completed, 3 failed`, **C15 falla**, `el repositorio tiene que llamarse una sola vez expected:<1> but was:<2>` | Restaurado, 34 de 34 en verde |
| C16 | `RepositoryModule`: comentado el `@Binds @Singleton bindWorkoutRepository` | `hiltJavaCompileDebug FAILED`: `[Dagger/MissingBinding] com.example.parcial_tp3.data.repository.WorkoutRepository cannot be provided without an @Provides-annotated method.` | Restaurado, `assembleDebug` en verde |

### C15 no detectaba `WhileSubscribed` (corregido)

Con el test original, la suite atrapaba la variante `WhileSubscribed` (fallaban C13 y C14), pero C15 solo no la atrapaba. Con un `SharingStarted` instrumentado, la corrida mostró `START`, la consulta y `STOP`, y nunca un segundo `START`: la segunda observación no llega a suscribirse.

Causa: el test lanza la segunda observación en `backgroundScope` y después hace `advanceUntilIdle()`, que no espera tareas de `backgroundScope` cuando no quedan tareas en primer plano. La segunda suscripción no corre antes de las aserciones, así que "una sola llamada" se cumple siempre.

Experimento, sin tocar `WorkoutsViewModelTest`: una copia temporal del test (`TmpExperimentoC15Test`, ya borrada) con `runCurrent()` en lugar de ese último `advanceUntilIdle()`.
- Con la variante `WhileSubscribed`: **C15 falla**.
- Con la implementación: **C15 pasa** (`BUILD SUCCESSFUL`).

Hector decidió aplicarlo. En `WorkoutsViewModelTest.kt`, línea 73, `advanceUntilIdle()` pasó a `runCurrent()`.

Verificación con el test real:
- Control negativo con `WhileSubscribed`: **C15 falla**, `el repositorio tiene que llamarse una sola vez expected:<1> but was:<2>`. Se restauró el ViewModel.
- Suite completa con la implementación: `./gradlew testDebugUnitTest assembleDebug --rerun-tasks`, `BUILD SUCCESSFUL`, 34 tests, 0 fallas, 0 errores. `reglas.sh` sale con 0.

Así C15 controla la rotación por sí solo, sin depender de que C13 y C14 no observen el estado.
