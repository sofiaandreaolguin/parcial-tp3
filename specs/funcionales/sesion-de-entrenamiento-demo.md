# Sesión de entrenamiento simulada

Flujo: Workout
Requisitos: NAV03 (los controles de 05 y 07 responden), NAV04 (el `workoutId` de la ruta), criterio de RF07 aplicado a la sesión (estado observable, eventos en el ViewModel con coroutines, rotación; la consigna lo exige para el catálogo y las comidas, acá se sigue igual), RF06 y RF08 (parciales: el ViewModel pasa por `WorkoutDetailRepository`, inyectado por Hilt). Lo encuadra el apartado "Límites del núcleo" de la consigna: el entrenamiento activo, el descanso y la finalización tienen que ser navegables; el cronómetro, el registro de cada serie y el volumen pueden simularse.
Pantallas: `Workout / 05 — Active Workout` (nodo `1:554`) y `Workout / 07 — Workout Complete` (nodo `1:673`) van a usar `ActiveWorkoutViewModel`. Esta spec no dibuja ninguna: es el PR 7 de 10 de la tarea k y deja el estado listo para que las dos pantallas lo lean.
Responsable: Hector

## Qué hace
Al tocar "Start Workout" en 04 se abre 05 con el entrenamiento elegido. La persona ve el ejercicio y la serie en la que está, ajusta el peso con − y +, y toca "Complete Set": la serie queda hecha, se pasa a la siguiente y arranca el descanso del ejercicio, que baja de a un segundo y se puede alargar (+30s) o cortar (Skip). Al tocar "Finish Workout", 07 muestra el resumen de lo que se hizo (ejercicios terminados, volumen y duración) y deja elegir la dificultad.

Todo es de demostración: los ejercicios, las series, las repeticiones y el descanso salen del detalle (`WorkoutDetailRepository`, hoy la demo con los datos de `fixtures.json`), pero la sesión no se guarda en ningún lado y no tiene endpoint. **No se inventan endpoints.**

**05 y 07 comparten la misma instancia.** 07 se abre encima de 05, que sigue en la pila, así que la ruta de 07 pide el ViewModel de la entrada de `active_workout` y no uno propio. Así el resumen muestra la sesión que se hizo y no el plan completo. Esto se arma en `WorkoutNavGraph` en el PR de 07; como es la zona de Fiore, se le avisa en ese PR. Nota para ese PR: la entrada se busca con `remember(backStackEntry) { nav.getBackStackEntry(Rutas.ACTIVE_WORKOUT) }` y se pasa a `hiltViewModel(...)`. Si se llama a `getBackStackEntry` directo en la composición, puede tirar `IllegalArgumentException` cuando "Done" saca 05 de la pila mientras 07 todavía se está componiendo (en la animación de salida).

## Datos
- DTO: ninguno. La sesión no viene del servidor.
- Modelo: `WorkoutDetail` y `Exercise` (`domain/model/WorkoutDetail.kt`), sin cambios. De cada ejercicio se usan `sets`, `reps` y `restSeconds`.
- Entity: ninguna. La sesión no se guarda: al cerrar la app se pierde.
- Valores de demostración (no están en el modelo; salen del Figma):

| Valor | Cuánto | De dónde |
|---|---|---|
| Peso inicial | 45,0 kg | "− 45 kg +" y "Last session: 45 kg × 12 reps" del frame 05 |
| Paso del peso | 2,5 kg | Lo eligió Hector (disco más chico habitual); el Figma no lo dice |
| Descanso extra | 30 s | Botón "+30s" del frame 05 |

- Lo que muestra cada pantalla y de dónde sale:

| Pantalla | Qué se ve | De dónde sale |
|---|---|---|
| 05 | "EXERCISE 2 OF 5" y el nombre | `exerciseIndex + 1`, `detail.exercises.size` y `currentExercise.name` |
| 05 | "10 REPS TARGET" y "3 × 12 SET MATRIX" | `currentExercise.reps` y `currentExercise.sets × currentExercise.reps` (con la demo, 10 y 3 × 10) |
| 05 | "− 45 kg +" | `weightKg` |
| 05 | Chips "Set 1 / Set 2 / Set 3" | Chip `n` contado desde 0 (dice "Set n+1"): hecho si `n < setIndex`; actual si `n == setIndex` y no `allSetsDone`; pendiente si no |
| 05 | "Resting • 01:18", "+30s" y "Skip" | `restRemainingSeconds`; la fila se ve solo si `isResting` |
| 07 | "42 min DURATION" | `detail.durationMinutes` (el cronómetro es simulado) |
| 07 | "5/5 EXERCISES" | `exercisesCompleted` / `detail.exercises.size` |
| 07 | "2,340 kg VOLUME" | `volumeKg` |
| 07 | "Great / Good / Tough / Too Easy" | `rating` |

  "Last session", la frase de VORA COACH, "Voice On" y la frase de VORA INSIGHT no salen del ViewModel: son textos de demostración de cada pantalla, en `strings.xml`. Lo decide el PR de cada pantalla (en 06 Hector eligió mostrar datos del modelo cuando el texto del Figma no corresponde al ejercicio).

## Contrato

| Pieza | Archivo | Firma |
|---|---|---|
| Estado | `ui/screens/workout/ActiveWorkoutUiState.kt` | `sealed interface ActiveWorkoutUiState` con `data object Loading`, `data class Content(...)` (abajo) y `data object Error`. En el mismo archivo, `data class CompletedSet(val exerciseId: String, val reps: Int, val weightKg: Double)` y `enum class WorkoutRating { GREAT, GOOD, TOUGH, TOO_EASY }` |
| ViewModel | `ui/screens/workout/ActiveWorkoutViewModel.kt` | `@HiltViewModel class ActiveWorkoutViewModel @Inject constructor(savedStateHandle: SavedStateHandle, private val repository: WorkoutDetailRepository) : ViewModel()` con `val uiState: StateFlow<ActiveWorkoutUiState>` y los eventos `onCompleteSet()`, `onAddRest()`, `onSkipRest()`, `onIncreaseWeight()`, `onDecreaseWeight()`, `onFinish()` y `onRate(rating: WorkoutRating)`. Lee `Rutas.ARG_WORKOUT_ID`. La carga se lanza en `init` con `viewModelScope` |
| Hilt | ninguno nuevo | Usa el `@Binds` de `WorkoutDetailRepository` que ya está en `di/RepositoryModule.kt` |

```kotlin
data class Content(
    val detail: WorkoutDetail,
    val exerciseIndex: Int = 0,          // ejercicio actual, desde 0
    val setIndex: Int = 0,               // serie actual del ejercicio, desde 0; == sets cuando terminó todo
    val weightKg: Double = 45.0,
    val restRemainingSeconds: Int = 0,   // 0 = no está descansando
    val completedSets: List<CompletedSet> = emptyList(),
    val rating: WorkoutRating? = null,
) : ActiveWorkoutUiState {
    val currentExercise: Exercise get() = detail.exercises[exerciseIndex]
    val isResting: Boolean get() = restRemainingSeconds > 0
    val allSetsDone: Boolean get() =
        exerciseIndex == detail.exercises.lastIndex && setIndex == currentExercise.sets
    val exercisesCompleted: Int get() = if (allSetsDone) detail.exercises.size else exerciseIndex
    val volumeKg: Double get() = completedSets.sumOf { it.reps * it.weightKg }
}
```

- Las propiedades calculadas no entran en la igualdad del `data class`: los tests comparan el `Content` entero con uno armado en el test y, aparte, leen las calculadas.
- `Error` va sin mensaje y sin reintento, como en `WorkoutDetailUiState`: la tarea de estados (RF05) lo cambia. Los tests comparan con `is ActiveWorkoutUiState.Error`.
- **Los eventos cambian `uiState.value` en el momento:** el test lo lee apenas vuelve la llamada, sin `runCurrent()`. Lo único que corre en una corrutina es la cuenta del descanso.
- El descanso es **una sola** corrutina en `viewModelScope` que hace `delay(1_000)` y resta 1 hasta llegar a 0. Arrancar uno nuevo, `onSkipRest()` y `onFinish()` cancelan el anterior. Como vive en el ViewModel, sigue contando al rotar (C24).
- Al completar una serie, los índices avanzan **en el momento** y el descanso corre después: por eso el Figma muestra "Set 2" como actual mientras dice "Resting".
- "Finish Workout" y "Done" navegan desde la pantalla (`onFinish` se llama antes de navegar); el ViewModel no emite eventos de navegación.

## Criterios de aceptación (EARS)

Cada línea es un test. Si no se te ocurre el test, la línea está mal escrita.

Carga:

- C1. CUANDO se crea el ViewModel con `workoutId` en la ruta, su estado DEBE empezar en `Loading` y pasar a `Content(detail)` con todos los valores por defecto: `exerciseIndex` 0, `setIndex` 0, `weightKg` 45,0, `restRemainingSeconds` 0, `completedSets` vacía y `rating` nulo (criterio de RF07).
- C2. CUANDO se crea el ViewModel con `workoutId` `"w02"`, el repositorio DEBE recibir exactamente `"w02"` (NAV04: la sesión es del entrenamiento elegido).
- C3. SI la ruta no trae `workoutId`, o lo trae vacío o con solo espacios, ENTONCES el estado DEBE pasar a `Error` sin llamar al repositorio.
- C4. SI el repositorio devuelve `Result.failure`, ENTONCES el estado DEBE pasar a `Error` y no quedar en `Loading` (criterio de RF07).
- C5. SI el detalle llega sin ejercicios, ENTONCES el estado DEBE pasar a `Error` y no a `Content` (05 no tendría qué mostrar).

Series ("Complete Set" no navega: pasa a la serie siguiente, NAV03):

- C6. CUANDO se llama a `onCompleteSet()` en una serie que no es la última del ejercicio, `setIndex` DEBE sumar 1, `exerciseIndex` DEBE quedar igual, `completedSets` DEBE sumar al final `CompletedSet(id del ejercicio, reps del ejercicio, weightKg actual)` y `restRemainingSeconds` DEBE pasar a `restSeconds` del ejercicio.
- C7. CUANDO se llama a `onCompleteSet()` en la última serie de un ejercicio que no es el último, `exerciseIndex` DEBE sumar 1, `setIndex` DEBE volver a 0, `completedSets` DEBE sumar al final `CompletedSet(id del ejercicio recién terminado, reps de ese ejercicio, weightKg actual)` y `restRemainingSeconds` DEBE pasar a `restSeconds` del ejercicio recién terminado, no del siguiente.
- C8. CUANDO se llama a `onCompleteSet()` en la última serie del último ejercicio, `allSetsDone` DEBE ser verdadero, `exercisesCompleted` DEBE ser la cantidad de ejercicios, `completedSets` DEBE sumar al final el `CompletedSet` de esa serie y `restRemainingSeconds` DEBE quedar en 0 y seguir en 0 después de 61 s de tiempo virtual, aunque se haya completado durante el descanso de la serie anterior (no hay descanso después de la última).
- C9. MIENTRAS `allSetsDone` es verdadero, `onCompleteSet()` NO DEBE cambiar el estado.
- C10. SI el ejercicio tiene `restSeconds` 0 o negativo, ENTONCES `onCompleteSet()` DEBE dejar `restRemainingSeconds` en 0.

Descanso ("+30s" y "Skip" no navegan, NAV03):

- C11. MIENTRAS hay descanso, `restRemainingSeconds` DEBE bajar 1 por cada segundo de tiempo virtual y quedarse en 0 al llegar: con 60, contando desde la llamada a `onCompleteSet()`, a 1 s (`advanceTimeBy(1_000)` y `runCurrent()`) vale 59, a 60 s vale 0 y a 61 s sigue en 0.
- C12. CUANDO se llama a `onAddRest()` mientras hay descanso, `restRemainingSeconds` DEBE sumar 30 y seguir bajando (de 59 a 89, y 1 s después 88).
- C13. SI se llama a `onAddRest()` sin descanso, ENTONCES `restRemainingSeconds` DEBE seguir en 0 ("+30s" no arranca un descanso).
- C14. CUANDO se llama a `onSkipRest()`, `restRemainingSeconds` DEBE pasar a 0 y no volver a cambiar: 5 s después el estado es el mismo.
- C15. CUANDO se llama a `onCompleteSet()` mientras hay descanso, el descanso anterior DEBE cortarse y arrancar uno nuevo con `restSeconds` del ejercicio de la serie que se acaba de completar (si era su última serie, como en C7; si era la última de todo, sin descanso, como en C8), contando solo uno: si quedaban 30 s, pasa a 60 y 1 s después vale 59, no 58.

Peso:

- C16. CUANDO se llama a `onIncreaseWeight()`, `weightKg` DEBE sumar 2,5 (de 45,0 a 47,5); CUANDO se llama a `onDecreaseWeight()`, DEBE restar 2,5 (de 45,0 a 42,5).
- C17. SI se llama a `onDecreaseWeight()` con `weightKg` en 0, ENTONCES `weightKg` DEBE seguir en 0 (18 veces desde 45,0 llegan a 0,0 y la 19.ª no baja de ahí).
- C18. CUANDO se llama a `onCompleteSet()`, el `CompletedSet` agregado DEBE llevar el `weightKg` de ese momento, y `volumeKg` DEBE ser la suma de `reps × weightKg` de `completedSets`: con reps 10, una serie con 45,0 y otra después de dos `onIncreaseWeight()` dan 450,0 + 500,0 = 950,0, y cambiar el peso después no cambia ese total.

Fin (07):

- C19. CUANDO se llama a `onFinish()`, con descanso o sin él, `restRemainingSeconds` DEBE quedar en 0 y seguir en 0 después de 61 s de tiempo virtual; `exerciseIndex`, `setIndex`, `weightKg`, `completedSets` y `rating` DEBEN quedar iguales.
- C20. MIENTRAS `allSetsDone` es falso, `exercisesCompleted` DEBE ser la cantidad de ejercicios con todas sus series hechas: con dos ejercicios de 3 series, tras 4 `onCompleteSet()` vale 1.
- C26. CUANDO se llama a `onCompleteSet()` después de `onFinish()` (se volvió de 07 a 05 con el atrás del sistema), la sesión DEBE seguir como si no se hubiera terminado: avanza la serie y arranca el descanso, como en C6 a C8.
- C21. CUANDO se llama a `onRate(...)`, `rating` DEBE pasar a esa dificultad: arranca nulo, `onRate(GOOD)` da `GOOD`, después `onRate(TOUGH)` da `TOUGH` y otra vez `onRate(TOUGH)` sigue en `TOUGH`.

Robustez y rotación:

- C22. MIENTRAS el estado es `Loading` o `Error`, los siete eventos NO DEBEN cambiar el estado ni lanzar una excepción. Si se llamaron en `Loading`, CUANDO después llega el detalle, el `Content` DEBE tener los valores por defecto de C1 y seguir igual después de 61 s de tiempo virtual.
- C23. CUANDO el estado del mismo ViewModel se deja de observar y se vuelve a observar con más de 5 s de tiempo virtual entre una y otra (como al rotar), el repositorio DEBE haber sido llamado una sola vez y el estado DEBE seguir siendo el mismo `Content`, con el avance de la sesión (criterio de RF07).
- C24. CUANDO se deja de observar con el descanso en 60 y se vuelve a observar 6 s después, `restRemainingSeconds` DEBE valer 54 y el repositorio DEBE haber sido llamado una sola vez (el descanso sigue contando al rotar).
- C25. La app DEBE compilar con `./gradlew assembleDebug` con `ActiveWorkoutViewModel` inyectable: Hilt valida al compilar el grafo `ActiveWorkoutViewModel` → `WorkoutDetailRepository` → `DemoWorkoutDetailRepository` (RF08). **Reemplazado el 25/10 por C30 de `detalle-y-cache-de-entrenamientos.md`**: el grafo termina en `WorkoutDetailRepositoryImpl`.

Plantillas: **La app DEBE** (siempre) · **CUANDO** (ante un evento) · **MIENTRAS** (durante un estado) · **SI ... ENTONCES** (algo sale mal) · **DONDE** (opcional, para ampliaciones).

Casos de la plantilla y cómo se cubren acá:
- Sin conexión, 503 y JSON inválido: para este ViewModel son todos un `Result.failure` del repositorio (C4), como en el detalle.
- No encontrado (404): C4. Sin `workoutId`: C3.
- Lista vacía: un detalle sin ejercicios (C5).
- Valores límite: peso en 0 (C17), descanso en 0 (C11) o sin descanso (C10, C13), última serie (C8, C9).
- Rotación: C23 (el avance) y C24 (el descanso sigue contando) en tests.

## Fuera de alcance
- **Las pantallas 05 y 07**, sus textos y la navegación (incluido compartir la instancia entre 05 y 07, y que "Done" vaya a `workout_dashboard` limpiando la sesión de la pila): PR 8 y 9 de la tarea k (carril B). Acá no se toca `strings.xml`, ninguna pantalla, `WorkoutNavGraph` ni `MainActivity`.
- **Guardar la sesión** en Room o mandarla al servidor: no hay endpoint y la consigna permite simularla. Si se cierra la app o Android mata el proceso, la sesión se pierde (no va a `SavedStateHandle`).
- **El cronómetro de la sesión:** la duración de 07 es `durationMinutes` del modelo.
- **"Last session"**, las frases de VORA y el reparto del Figma ("EXERCISE 2 OF 5", "3 × 12"): no hay datos reales para eso.
- **Un tope de peso:** solo se controla que no baje de 0.
- **Ejercicios con `sets` 0 o negativo:** el mock siempre trae `sets` 3.
- **Errores propios, mensaje y reintento (RF05):** tarea de estados.
- **La ampliación "seguimiento de series en el entrenamiento activo"** (extras de la consigna): esto se le parece, pero no se reclama sin preguntarle al profe, porque no se guarda nada.

## Tests
| Criterio | Test | Dónde | Vi el rojo |
|---|---|---|---|
| C1 | `` `C1 sesion arranca en Loading y pasa a Content con los valores iniciales` `` | `test/` · `ui/screens/workout/ActiveWorkoutViewModelTest` | Sí: `expected Content(...) but was Loading` |
| C2 | `` `C2 sesion pide al repositorio el workoutId de la ruta` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí: `expected [w02] but was []` |
| C3 | `` `C3 sesion sin workoutId pasa a Error sin llamar al repositorio` `` (sin la clave, `""` y `"  "`) | `test/` · `ActiveWorkoutViewModelTest` | Sí: `sin la clave tiene que ser Error, fue Loading`. Control negativo: llamar al repositorio con el id vacío da `no tiene que llamar al repositorio expected [] but was [, ,   ]` |
| C4 | `` `C4 sesion si el repositorio falla pasa a Error` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí: `tiene que ser Error, fue Loading` |
| C5 | `` `C5 sesion con detalle sin ejercicios pasa a Error` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí: `tiene que ser Error, fue Loading` |
| C6 | `` `C6 sesion completar serie pasa a la siguiente y arranca el descanso` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí. Con la carga hecha, por el criterio: `expected Content(... setIndex=1, restRemainingSeconds=60 ...)` y fue el `Content` inicial |
| C7 | `` `C7 sesion completar la ultima serie de un ejercicio pasa al siguiente` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí. Por el criterio: `expected Content(... exerciseIndex=1 ...)` y fue el inicial. Control: tomar el descanso y la serie del ejercicio siguiente hace fallar C7 (y C15 con `expected 60 but was 90`) |
| C8 | `` `C8 sesion completar la ultima serie de todo termina sin descanso` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí. Por el criterio: `expected Content(... setIndex=3 ...)`. Control: un descanso que cuenta con una variable propia y no se cancela da `despues de la ultima serie no hay descanso`. Sacar solo el `cancel()` no lo rompe: el descanso lee lo que queda del estado y frena en 0 |
| C9 | `` `C9 sesion terminada ignora completar serie` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí, en su preparación: `allSetsDone tiene que ser verdadero` (sin eventos la sesión no llega a terminar). Control: sin la guarda de `allSetsDone`, la aserción propia da `expected Content(...)` con otra serie más |
| C10 | `` `C10 sesion ejercicio sin descanso no arranca descanso` `` (`restSeconds` 0 y -5) | `test/` · `ActiveWorkoutViewModelTest` | Sí. Por el criterio: `con restSeconds 0 expected Content(... setIndex=1 ...)` |
| C11 | `` `C11 sesion el descanso baja de a un segundo y se queda en cero` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí. Por el criterio: `al completar expected 60 but was 0` |
| C12 | `` `C12 sesion mas 30 suma al descanso y sigue contando` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí. Por el criterio: `expected 59 but was 0` |
| C13 | `` `C13 sesion mas 30 sin descanso no hace nada` `` | `test/` · `ActiveWorkoutViewModelTest` | Pasó con los eventos vacíos. Control: `onAddRest()` sumando 30 siempre da `expected Content(...)` con 30 de descanso |
| C14 | `` `C14 sesion skip corta el descanso` `` | `test/` · `ActiveWorkoutViewModelTest` | Pasó con los eventos vacíos (el descanso nunca arrancaba). Control: Skip que deja 1 da `expected 0 but was 1`; un descanso con variable propia sin cancelar da `5 s despues` |
| C15 | `` `C15 sesion completar durante el descanso arranca uno nuevo y cuenta uno solo` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí. Por el criterio: `al completar con 30 s de descanso expected 60 but was 0`. Control: sin cancelar el descanso anterior da `tiene que contar un solo descanso expected 59 but was 58` |
| C16 | `` `C16 sesion mas y menos cambian el peso de a 2 y medio` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí. Por el criterio: `expected 47.5 but was 45.0` |
| C17 | `` `C17 sesion el peso no baja de cero` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí. Por el criterio: `18 veces desde 45 expected 0.0 but was 45.0` |
| C18 | `` `C18 sesion cada serie guarda su peso y suma el volumen` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí. Por el criterio: `expected [CompletedSet(e04, 10, 45.0), CompletedSet(e04, 10, 50.0)] but was []` |
| C19 | `` `C19 sesion terminar corta el descanso y conserva el avance` `` | `test/` · `ActiveWorkoutViewModelTest` | Pasó con los eventos vacíos. Controles: Finish que sube el peso y Finish que no corta el descanso dan `con descanso expected Content(...)`. **Sin cancelar la corrutina no falla**, ni con un contador propio: a los 61 s un descanso de 60 ya terminó de contar y vuelve a dar 0 (ver Historial) |
| C20 | `` `C20 sesion terminar antes cuenta solo los ejercicios completos` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí. Por el criterio: `expected 1 but was 0` |
| C26 | `` `C26 sesion despues de terminar se puede seguir` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí. Por el criterio: `expected Content(... setIndex=2 ...)` |
| C21 | `` `C21 sesion elegir dificultad la guarda y la cambia` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí. Por el criterio: `expected GOOD but was null` |
| C22 | `` `C22 sesion sin Content los eventos no cambian nada` `` (en `Loading` y en `Error`) | `test/` · `ActiveWorkoutViewModelTest` | Pasó con los eventos vacíos. Control: `onRate()` que pasa a `Error` fuera de `Content` da `en Loading expected Loading but was Error` |
| C23 | `` `C23 sesion volver a observar no repite la consulta ni pierde el avance` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí. Por el criterio: `expected Content(... setIndex=1 ...)`. Control: volver a cargar en cada nueva suscripción da `el repositorio tiene que llamarse una sola vez expected [w02] but was [w02, w02]`. El control con `stateIn(WhileSubscribed)` falla antes, en el avance, porque con `stateIn` el valor no cambia en el momento |
| C24 | `` `C24 sesion el descanso sigue contando sin observar` `` | `test/` · `ActiveWorkoutViewModelTest` | Sí. Por el criterio: `expected 60 but was 0` |
| C25 | `./gradlew assembleDebug` (no es un test unitario: la salida va a la evidencia) | build | Pasó en rojo: `BUILD SUCCESSFUL` con la firma vacía. Control: sin `@Inject` falla KSP con `[Hilt] @HiltViewModel annotated class should contain exactly one @Inject or @AssistedInject annotated constructor`. **Reemplazado el 25/10 por C30** de `detalle-y-cache-de-entrenamientos.md` |

- Todo va en `test/`: no hay Room. Los nombres empiezan con "sesion" para no confundirse con los `C1`… del catálogo y del detalle.
- Se reusa `FakeWorkoutDetailRepository` (guarda los ids y retiene la respuesta con un `CompletableDeferred`) y `MainDispatcherRule` (`StandardTestDispatcher`, que comparte el reloj virtual con `runTest`).
- El detalle de prueba se arma en el test, no se lee de la demo: dos ejercicios de 3 series **distintos entre sí**, `e04` con 10 reps y `restSeconds` 60, y `e05` con 12 reps y `restSeconds` 90, y uno aparte para C10. Si fueran iguales, C7 y C18 no podrían distinguir el ejercicio recién terminado del siguiente. Así los tests no se rompen cuando se borra la demo.
- **Cuidado con `advanceUntilIdle()` después de completar una serie:** corre el descanso entero hasta 0. Para mirar el descanso se usa `advanceTimeBy(...)` seguido de `runCurrent()`.
- C23: completar una serie y llamar a `onSkipRest()` antes de dejar de observar, así el descanso no cambia el estado mientras pasan los 6 s. Mismo método que C7 del detalle: observar en `backgroundScope`, cancelar, avanzar 6 s y volver a observar con `runCurrent()`.
- Controles negativos de los que pueden pasar en rojo:
  - C3: con la firma vacía nunca se llama al repositorio. Control: llamarlo con el id vacío y ver que falla.
  - C9, C13, C17 y C22: con la firma vacía el estado no cambia. Control: hacer que el evento cambie algo (por ejemplo, que `onAddRest()` sume 30 siempre) y ver que falla.
  - C7: control tomando `restSeconds` y el `CompletedSet` del ejercicio nuevo, que da 90 en vez de 60 y `e05` con 12 reps en vez de `e04` con 10.
  - C8 y C19: control sin cancelar la corrutina del descanso, que vuelve a bajar. Al implementar (23/10) sacar solo el `cancel()` no rompe ninguno de los dos, porque la cuenta lee el estado y frena en 0. Con un contador propio C8 falla y C19 no (ver Historial); C19 se controló con un Finish que no corta el descanso.
  - C15: control con dos corrutinas de descanso a la vez (sin cancelar la anterior), que da 58.
  - C23: control con `flow { }.stateIn(viewModelScope, WhileSubscribed(5_000), Loading)`, que vuelve a consultar. Al implementar falló antes de su criterio (con `stateIn` el valor no cambia en el momento), así que se usó una carga que se repite en cada nueva suscripción.
  - C25: control sacando `@Inject` del constructor y viendo que el build falla.
- Rojo (23/10), con la firma vacía: el estado queda siempre en `Loading`. C1 a C5 fallan por su criterio; C6 a C26 fallan antes, en la carga. En `implementar`, primero la carga (C1 a C5) y correr los tests: C6 a C26 tienen que fallar por su criterio. Los que pasen con los eventos vacíos (se espera C9, C13, C17 y C22) llevan su control negativo antes de programar el resto.
- Carga hecha (23/10): pasaron con los eventos vacíos C13, C14, C19 y C22, y no los que se esperaban. C14 y C19 pasaron porque el descanso nunca arrancaba; C9 y C17 fallaron (C9 en su preparación, así que su aserción se controló aparte). Los cuatro que pasaron llevan su control en la tabla.

## Auditoría
- auditor-spec (2026-10-23): 13 hallazgos, 3 bloqueaban.
  - Corregidos: #1 (los dos ejercicios de prueba son distintos: 10 reps y 60 s, 12 reps y 90 s), #2 (C7 y C8 piden el `CompletedSet`), #3 (los eventos cambian el estado en el momento; C11 dice desde cuándo se cuenta), #5 (C19 con plazo, sin descanso y con `rating`), #6 (C8 sigue en 0 aunque se complete durante un descanso), #7 (C22 mira el `Content` que llega después), #8 (C15 dice de qué ejercicio es el descanso), #9 (RF07 citado como criterio aplicado), #10 (C24 nuevo: el descanso sigue contando al rotar; el build pasa a C25), #11 (chips contados desde 0), #12 (nota de `remember` para el PR de 07) y #13 (C18 y C20 en EARS).
  - #4 lo decidió Hector el 23/10: si después de "Finish Workout" se vuelve a 05 con el atrás del sistema, la sesión sigue (C26, va junto a C19 y C20; el número es el último porque se sumó después).

## Historial
- 2026-10-23. Spec creada para el PR 7 de la tarea k, entrevistando a Hector. Decidió: el descanso cuenta de verdad en el ViewModel; el peso arranca en 45 kg y cambia de a 2,5 kg; al terminar la última serie se queda en 05 hasta tocar "Finish Workout"; 07 comparte la instancia de 05; la dificultad vive en el ViewModel; "Complete Set" durante el descanso lo corta y completa la serie.
- 2026-10-23. Auditoría corregida (ver arriba). Se suman C24 (el descanso sigue al rotar) y C26 (después de terminar se puede seguir); el build pasa de C24 a C25.
- 2026-10-23. Hector aprueba la spec.
- 2026-10-23. Tests en rojo: los 25 de `ActiveWorkoutViewModelTest` fallan por aserción (ver "Vi el rojo"); el build pasa con la firma vacía.
- 2026-10-23. Implementado `ActiveWorkoutViewModel`: los 25 tests en verde (112 en total) y `assembleDebug` OK. Hallazgo para Hector: C19 no detecta un descanso que no se canceló, porque mira recién a los 61 s y para entonces uno de 60 ya volvió a 0 (C14 sí lo detecta, porque mira a los 5 s). El código cancela igual. El test no se tocó: decide Hector si se le agrega un control a 1 s de terminar.
- 2026-10-25. Con el detalle real (spec `detalle-y-cache-de-entrenamientos.md`), `ActiveWorkoutViewModel` lee `WorkoutDetailRepositoryImpl` sin cambios y C25 pasa a reemplazado por C30.
