# Tarea g · Catálogo de entrenamientos · Tests en rojo (21/10/2026)

Tarea: primera consulta Retrofit, `GET /v1/workouts` con DTO separado del modelo (RF01, RF06, RF07, RF08). Rama `feat/workout-catalogo-retrofit`. Spec: `specs/funcionales/catalogo-de-entrenamientos.md`.

Comando: `./gradlew testDebugUnitTest`.

Código en `main`: solo firmas vacías, según la sección "Contrato" de la spec:

- `ApiResponseDto<T>`, `WorkoutDto`, `Workout` y `WorkoutLevel`: las clases de datos, sin lógica.
- `VoraApi.getWorkouts()` con `@GET("v1/workouts")` (la anotación es la firma del endpoint, por eso C1 y C2 pasan en rojo).
- `WorkoutDto.toDomain()` devuelve un `Workout` fijo (`id = ""`, `BEGINNER`, ceros) y no lee el DTO.
- `WorkoutRepositoryImpl.getWorkouts()` devuelve `Result.success(emptyList())` sin llamar a la API.
- `WorkoutsUiState` (`Loading`, `Content`, `Error`) y `WorkoutsViewModel` con `uiState` fijo en `Loading`, sin carga en `init`.
- `@Binds @Singleton bindWorkoutRepository` en `RepositoryModule`.

Tests nuevos en `app/src/test/`: `WorkoutApiTest`, `WorkoutMappersTest`, `WorkoutRepositoryImplTest` (MockWebServer con el Retrofit de `NetworkModule.provideRetrofit`), `WorkoutsViewModelTest` con `FakeWorkoutRepository` y la regla nueva `util/MainDispatcherRule` (`StandardTestDispatcher`).

## Unitarios: 34 tests, 14 fallan, 0 errores

Todos los fallos son `AssertionError` (por el resultado). Ninguno falla por compilación, por configuración ni por una excepción inesperada. Los 15 tests que ya estaban siguen en verde.

| Criterio | Test | Resultado en rojo |
|---|---|---|
| C1 | `C1 catalogo getWorkouts pide GET v1 workouts sin parametros` | **Pasa en rojo:** el path lo fija la anotación `@GET`, que es parte de la firma. Lleva control negativo (cambiar el path o sumar un `@Query`). |
| C2 | `C2 catalogo Gson convierte el JSON del mock en tres WorkoutDto` | **Pasa en rojo:** lo resuelven Gson y el `data class` del DTO. Lleva control negativo (renombrar un campo del DTO, por ejemplo `durationMinutes` a `duration`). |
| C3 | `C3 catalogo toDomain conserva los campos y traduce los tres niveles` | **Falla por el resultado:** `expected:<Workout(id=w01, name=Fuerza inicial, level=BEGINNER, durationMinutes=25, exerciseCount=3)> but was:<Workout(id=, name=, level=BEGINNER, durationMinutes=0, exerciseCount=0)>` |
| C4 | `C4 catalogo cada campo nulo del DTO lanza IllegalArgumentException` | **Falla por el resultado:** `id nulo: expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown` |
| C5 | `C5 catalogo un nivel desconocido o con mayusculas lanza IllegalArgumentException` | **Falla por el resultado:** `nivel pro: expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown` |
| C6 | `C6 catalogo con 200 devuelve los tres Workout en orden` | **Falla por el resultado:** `expected:<[Workout(id=w01, ...), Workout(id=w02, ...), Workout(id=w03, ...)]> but was:<[]>` |
| C7 | `C7 catalogo con data vacia devuelve exito con lista vacia` | **Pasa en rojo:** el stub ya devuelve `Result.success(emptyList())`. Lleva control negativo (devolver `Result.failure` cuando la lista viene vacía). |
| C8 | `C8 catalogo con 503 devuelve failure sin lanzar` | **Falla por el resultado:** `503 tiene que ser failure, fue Success([])` |
| C9 | `C9 catalogo sin data falla toda la consulta` | **Falla por el resultado:** `cuerpo nulo tiene que ser failure, fue Success([])` (el test cubre 204 sin cuerpo, `{"data": null}` y `{}`) |
| C9 | `C9 catalogo con un item invalido falla toda la consulta` | **Falla por el resultado:** `un item invalido tiene que hacer fallar todo, fue Success([])` |
| C10 | `C10 catalogo con servidor apagado devuelve failure sin lanzar` | **Falla por el resultado:** `servidor apagado tiene que ser failure, fue Success([])` |
| C10 | `C10 catalogo con JSON roto devuelve failure sin lanzar` | **Falla por el resultado:** `JSON roto tiene que ser failure, fue Success([])` |
| C11 | `C11 catalogo con JSON de otro tipo devuelve failure sin lanzar` | **Falla por el resultado:** `JSON de otro tipo tiene que ser failure, fue Success([])` |
| C12 | `C12 catalogo al cancelar relanza CancellationException` | **Falla por el resultado, pero en la condición previa:** `la consulta tiene que llegar al servidor antes de cancelarla` (el stub no llama a la API). Las aserciones de la cancelación no se llegaron a ver en rojo: lleva control negativo con `runCatching` o `catch (e: Exception)`, como dice la spec. |
| C13 | `C13 catalogo arranca en Loading y pasa a Content con la lista` | **Falla por el resultado:** la primera aserción (`Loading`) pasa y la segunda da `expected:<Content(workouts=[...])> but was:<Loading>` |
| C14 | `C14 catalogo si el repositorio falla pasa a Error` | **Falla por el resultado:** `tiene que ser Error, fue Loading` |
| C15 | `C15 catalogo volver a observar no repite la consulta ni pierde la lista` | **Falla por el resultado:** `expected:<Content(workouts=[...])> but was:<Loading>` en la primera observación. La parte de "una sola llamada" no se llegó a ver en rojo: lleva control negativo con `flow { }.stateIn(viewModelScope, WhileSubscribed(5_000), Loading)`, como dice la spec. |

Salida de Gradle (recortada a los fallos):

```text
> Task :app:testDebugUnitTest

WorkoutMappersTest > C5 catalogo un nivel desconocido o con mayusculas lanza IllegalArgumentException FAILED
    java.lang.AssertionError at WorkoutMappersTest.kt:76
WorkoutMappersTest > C4 catalogo cada campo nulo del DTO lanza IllegalArgumentException FAILED
    java.lang.AssertionError at WorkoutMappersTest.kt:57
WorkoutMappersTest > C3 catalogo toDomain conserva los campos y traduce los tres niveles FAILED
    java.lang.AssertionError at WorkoutMappersTest.kt:26
WorkoutRepositoryImplTest > C9 catalogo sin data falla toda la consulta FAILED
    java.lang.AssertionError at WorkoutRepositoryImplTest.kt:89
WorkoutRepositoryImplTest > C9 catalogo con un item invalido falla toda la consulta FAILED
    java.lang.AssertionError at WorkoutRepositoryImplTest.kt:105
WorkoutRepositoryImplTest > C8 catalogo con 503 devuelve failure sin lanzar FAILED
    java.lang.AssertionError at WorkoutRepositoryImplTest.kt:76
WorkoutRepositoryImplTest > C12 catalogo al cancelar relanza CancellationException FAILED
    java.lang.AssertionError at WorkoutRepositoryImplTest.kt:152
WorkoutRepositoryImplTest > C10 catalogo con JSON roto devuelve failure sin lanzar FAILED
    java.lang.AssertionError at WorkoutRepositoryImplTest.kt:133
WorkoutRepositoryImplTest > C6 catalogo con 200 devuelve los tres Workout en orden FAILED
    java.lang.AssertionError at WorkoutRepositoryImplTest.kt:51
WorkoutRepositoryImplTest > C11 catalogo con JSON de otro tipo devuelve failure sin lanzar FAILED
    java.lang.AssertionError at WorkoutRepositoryImplTest.kt:143
WorkoutRepositoryImplTest > C10 catalogo con servidor apagado devuelve failure sin lanzar FAILED
    java.lang.AssertionError at WorkoutRepositoryImplTest.kt:124
WorkoutsViewModelTest > C15 catalogo volver a observar no repite la consulta ni pierde la lista FAILED
    java.lang.AssertionError at WorkoutsViewModelTest.kt:59
WorkoutsViewModelTest > C14 catalogo si el repositorio falla pasa a Error FAILED
    java.lang.AssertionError at WorkoutsViewModelTest.kt:48
WorkoutsViewModelTest > C13 catalogo arranca en Loading y pasa a Content con la lista FAILED
    java.lang.AssertionError at WorkoutsViewModelTest.kt:36

34 tests completed, 14 failed
BUILD FAILED in 1m 44s
```

Resumen por clase (reportes XML de `app/build/test-results/testDebugUnitTest/`):

| Clase | Tests | Fallan | Errores |
|---|---|---|---|
| `WorkoutApiTest` | 2 | 0 | 0 |
| `WorkoutMappersTest` | 3 | 3 | 0 |
| `WorkoutRepositoryImplTest` | 9 | 8 | 0 |
| `WorkoutsViewModelTest` | 3 | 3 | 0 |
| Las 7 clases que ya estaban | 17 | 0 | 0 |

## C16 · `./gradlew assembleDebug`

No es un test unitario. Con las firmas vacías ya compila, incluido `hiltJavaCompileDebug`, que valida el grafo `WorkoutsViewModel` → `WorkoutRepository` → `VoraApi`:

```text
> Task :app:kspDebugKotlin UP-TO-DATE
> Task :app:hiltJavaCompileDebug UP-TO-DATE
BUILD SUCCESSFUL in 11s
42 actionable tasks: 5 executed, 37 up-to-date
```

(`UP-TO-DATE` porque `testDebugUnitTest` ya había compilado el código de `main` con el `@Binds` nuevo.) **Pasa en rojo:** el `@Binds` es parte del contrato. Lleva control negativo (comentar el `@Binds` y ver que Hilt no compila) y se vuelve a correr con la implementación.

## Para la skill implementar: controles negativos pendientes

- **C1, C2, C7 y C16** pasaron en rojo.
- **C12 y C15** fallaron antes de llegar a lo que prueban de verdad (cancelación y una sola consulta).

Cuando estén en verde, romper el código a propósito con el control negativo de cada uno, ver que el test falla y volver atrás.
