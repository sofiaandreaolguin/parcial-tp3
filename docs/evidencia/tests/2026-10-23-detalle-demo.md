# Tarea k · PR 3 · Detalle de entrenamiento con datos de demostración (23/10/2026)

Base del detalle para Workout 03, 04 y 06 (NAV04, RF01, RF06, RF07, RF08). Rama `feat/workout-detalle-demo`. Spec: `specs/funcionales/detalle-de-entrenamiento-demo.md`.

## Rojo (skill tests-rojo)

Con firmas vacías: la demo devuelve `Result.failure(NotImplementedError())` y el ViewModel se queda en `Loading`. Los 10 fallan por el resultado, no por compilación:

```text
D1 detalle demo devuelve w01 w02 y w03 cada uno con lo suyo   w01 tiene que ser exito, fue Failure(kotlin.NotImplementedError: ...)
D2 detalle demo exerciseCount coincide con la cantidad ...      w01 tiene que ser exito, fue Failure(kotlin.NotImplementedError: ...)
D3 detalle demo con id desconocido devuelve failure ...         tiene que fallar con NoSuchElementException, fue Failure(kotlin.NotImplementedError: ...)
C1 detalle arranca en Loading y pasa a Content ...              expected:<Content(detail=WorkoutDetail(id=w02, ...> but was:<Loading>
C2 detalle pide al repositorio el workoutId de la ruta          expected:<[w02]> but was:<[]>
C3 detalle con exerciseId elige ese ejercicio                   expected:<Content(detail=WorkoutDetail(id=w02, ...> but was:<Loading>
C4 detalle con exerciseId que no esta pasa a Error              con exerciseId "e99" tiene que ser Error, fue Loading
C5 detalle si el repositorio falla pasa a Error                 tiene que ser Error, fue Loading
C6 detalle sin workoutId pasa a Error sin llamar ...            sin la clave tiene que ser Error, fue Loading
C7 detalle volver a observar no repite la consulta ...          expected:<Content(detail=WorkoutDetail(id=w02, ...> but was:<Loading>
10 tests completed, 10 failed
```

## Verde (skill implementar)

- `DemoWorkoutDetailRepository`: w01, w02 y w03 de `fixtures.json` en un mapa por id, con `exerciseCount` 3 copiado del mock (no calculado, así D2 controla algo); un id desconocido da `Result.failure(NoSuchElementException)`. D1 a D3.
- `WorkoutDetailViewModel`: en `init` lee `workoutId` y `exerciseId` del `SavedStateHandle`. Sin `workoutId` (o en blanco) pasa a `Error` sin llamar al repositorio; si no, carga una sola vez con `viewModelScope.launch` y busca el ejercicio dentro del detalle. C1 a C7.
- Ningún test se modificó después del rojo.

```text
./gradlew testDebugUnitTest assembleDebug
BUILD SUCCESSFUL in 34s
59 tests, 0 fallas (los 49 de antes y los 10 nuevos)
scripts/reglas.sh: sin fallas
```

## Controles negativos

Cada uno rompe el código a propósito, se ve fallar el test y se vuelve atrás (comparado con `cmp` contra la copia).

| Criterio | Qué se rompió | Resultado |
|---|---|---|
| C6 | Con `workoutId` en blanco, llamar igual al repositorio antes de pasar a `Error` | `no tiene que llamar al repositorio expected:<[]> but was:<[, ,   ]>` |
| C7 | `flow { }.stateIn(viewModelScope, WhileSubscribed(5_000), Loading)` en vez de cargar en `init` | `el repositorio tiene que llamarse una sola vez expected:<[w02]> but was:<[w02, w02]>` |
| C8 | Sacar el `@Binds` de `RepositoryModule` y correr `assembleDebug` | `[Dagger/MissingBinding] ...WorkoutDetailRepository cannot be provided without an @Provides-annotated method.` `BUILD FAILED` |
