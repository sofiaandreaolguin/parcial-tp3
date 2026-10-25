# Carga, edición, eliminación y total diario de comidas

Flujo: Nutrition
Requisitos: RF03, RF04, RF06, RF07, RF08
Pantallas: Nutrition 01 (Dashboard), 02 (Today's Meals), 07 (Food Confirmation) y 08 (Meal Details). En VC-l se hacen los datos, los ViewModels y la adaptación de 01 y 02; las pantallas 07 y 08 se maquetan después, en los PR 7 y 8 de VC-m.
Responsable: Sofi

## Qué hace
La persona carga una comida a mano (o confirma la que "estimó" la voz o la foto), la ve en el listado del día elegido y en el total diario, la puede editar y la puede eliminar con confirmación. Todo queda en Room: sigue ahí al cerrar y reabrir la app, y el total de Nutrition y de Home cambia solo al agregar, editar o eliminar.

Se apoya en lo que dejó VC-h (`specs/funcionales/registro-de-comidas.md`): `Meal`, `MealEntity`, `MealDao.insert` / `observeByDate`, mappers y `MealRepository.observeMealsByDate` / `addMeal`.

## Datos
- DTO: ninguno (la consigna no tiene endpoint de comidas).
- Modelo: `Meal` y `MealType`, sin cambios. **Los ingredientes no se guardan:** el borrador de voz o foto los muestra en 07, pero en Room queda la comida con sus totales.
- Entity: `MealEntity`, sin cambios. **No se sube la versión de la base** (sigue `version = 2`).
- Formulario (nuevo, en `domain/`): `MealForm(name: String = "", type: MealType? = null, date: LocalDate, kcal: String = "", protein: String = "", carbs: String = "", fat: String = "")`. Los números van como texto porque es lo que se tipea.

## Contrato

### Datos y dominio
| Pieza | Archivo | Firma |
|---|---|---|
| DAO | `data/local/dao/MealDao.kt` | suma `@Query("SELECT * FROM meals WHERE id = :id") fun observeById(id: Long): Flow<MealEntity?>`, `@Update suspend fun update(meal: MealEntity): Int` y `@Query("DELETE FROM meals WHERE id = :id") suspend fun deleteById(id: Long): Int` |
| Repositorio | `data/repository/MealRepository.kt` e Impl | suma `fun observeMeal(id: Long): Flow<Meal?>`, `suspend fun updateMeal(meal: Meal)` y `suspend fun deleteMeal(id: Long)` |
| Errores | `data/repository/MealDataException.kt` | `class MealDataException(val error: MealError, cause: Throwable? = null) : Exception(cause)` y `enum class MealError { INVALID_DATA, STORAGE, NOT_FOUND }` |
| Validación | `domain/MealValidation.kt` | `fun validateMeal(form: MealForm, id: Long = 0): MealValidation`; `sealed interface MealValidation { data class Valid(val meal: Meal); data class Invalid(val errors: Map<MealField, FieldError>) }`; `enum class MealField { NAME, TYPE, KCAL, PROTEIN, CARBS, FAT }`; `enum class FieldError { REQUIRED, NOT_A_NUMBER, NOT_A_WHOLE_NUMBER, NEGATIVE }` |
| Reloj | `di/ClockModule.kt` | `@Provides fun provideClock(): Clock = Clock.systemDefaultZone()`. Los ViewModels piden "hoy" con `LocalDate.now(clock)`, así los tests fijan el día |
| Fecha del DatePicker | `ui/screens/nutrition/DatePickerDates.kt` | `fun selectedMillisToLocalDate(millis: Long): LocalDate` y `fun LocalDate.toPickerMillis(): Long`, las dos con `ZoneOffset.UTC` (el DatePicker de Material 3 trabaja en UTC) |

### Estados y ViewModels
**01 y 02.** `NutritionViewModel @Inject constructor(repo: MealRepository, savedState: SavedStateHandle, clock: Clock)`:
- Se pide **atado al grafo de Nutrition**, así 01 y 02 comparten la fecha y el grafo se la pasa a 07.
- Expone `uiState: StateFlow<NutritionUiState>` y `selectedDate: StateFlow<LocalDate>`, y tiene `onDateSelected(date)` y `onRetry()`.
- La fecha se guarda en `savedState[KEY_SELECTED_DATE]` (`const val KEY_SELECTED_DATE = "selectedDate"`) como texto ISO.

```kotlin
sealed interface NutritionUiState {
    data object Loading : NutritionUiState
    data class Content(
        val date: LocalDate,
        val kcal: Int, val kcalGoal: Int, val kcalProgress: Float,
        val macros: List<MacroTotal>,
        val meals: List<MealRow>,
        val pending: MealType?,
    ) : NutritionUiState
    data class Error(@StringRes val message: Int) : NutritionUiState
}
data class MacroTotal(val kind: MacroKind, val grams: Int, val goal: Int, val progress: Float)
data class MealRow(val id: Long, val type: MealType, val name: String, val kcal: Int)
```
- Las metas son de demostración. kcal, proteína y carbos son las de Home (`HomeDemoData`: 2100, 140 y 220). La grasa, 65 g, es solo de Nutrition, en `NutritionDemoData`, como dice el Figma. El progreso se topa en 1.
- El consejo y la fecha del encabezado los arma la pantalla.

**Qué muestran 01 y 02 sin ingredientes:**
- **01:** una fila por comida, con el tipo como título, el nombre como bajada y las kcal.
- **02:** las comidas agrupadas por tipo. El encabezado del grupo lleva el tipo y la suma de kcal; cada comida es una línea con su nombre y sus kcal, y tocarla abre su detalle.

**07.** Se amplía `FoodCaptureViewModel`: suma `repo: MealRepository` al constructor y `FoodCaptureUiState` cambia así.

```kotlin
data class FoodCaptureUiState(
    @StringRes val transcript: Int = R.string.nutrition_voice_transcript_demo,
    val draft: MealDraft? = null,          // null = no hubo captura (carga manual)
    val analysisStep: AnalysisStep = AnalysisStep.Identifying,
    val confirmation: ConfirmationState? = null,  // null = 07 cerrada
)
data class ConfirmationState(
    val mealId: Long,                      // -1 = nueva
    val form: MealForm,
    val errors: Map<MealField, FieldError> = emptyMap(),
    val isSaving: Boolean = false,
    val notFound: Boolean = false,
    @StringRes val saveError: Int? = null,
)
```
- **Eventos nuevos:**
  - `onManualEntry()` deja `draft = null`. Lo llaman "Log Manually" (03) y "Type instead" (04) antes de navegar, como `abrirFoto`.
  - `onOpenConfirmation(mealId: Long, defaultDate: LocalDate, draftName: String)`. `draftName` lo resuelve la Route con `stringResource`, porque el ViewModel no tiene `Context`.
  - `onFormChange(form: MealForm)`, `onSave()` y `onDiscard()`. Este último cierra 07: lo llaman ‹ y el botón atrás.
- **Evento de una sola vez:** `FoodCaptureEvent.Saved`.
- `PhotoAnalysisRoute` usa `state.draft ?: PhotoMealDraft`.

**08.** `MealDetailViewModel @Inject constructor(repo: MealRepository, savedState: SavedStateHandle)` (nuevo). Lee `Rutas.ARG_MEAL_ID` y tiene `onDeleteClick()`, `onDismissDelete()`, `onConfirmDelete()` y `onDuplicate()`.

```kotlin
sealed interface MealDetailUiState {
    data object Loading : MealDetailUiState
    data class Content(
        val meal: Meal,
        val confirmingDelete: Boolean = false,
        val isBusy: Boolean = false,               // eliminando o duplicando
        @StringRes val actionError: Int? = null,
    ) : MealDetailUiState
    data object NotFound : MealDetailUiState
    data class Error(@StringRes val message: Int) : MealDetailUiState
}
sealed interface MealDetailEvent { data object Deleted : MealDetailEvent; data object Duplicated : MealDetailEvent }
```

### Archivos que se tocan
**Datos:** los del contrato.

**Nutrition (Sofi):**
- `NutritionViewModel`, `NutritionUiState` y `NutritionDemoData` (las previews)
- `NutritionDashboardRoute` / `Screen` y `TodaysMealsRoute` / `Screen`
- `FoodCaptureViewModel` y `FoodCaptureUiState`
- `PhotoAnalysisRoute`, `MealDetailViewModel` y `NutritionGraph`:
  - `NutritionViewModel` atado al grafo;
  - `onManualEntry` antes de "Log Manually" y "Type instead";
  - la fecha para 07.
- 07 y 08 siguen siendo `PantallaVacia` hasta los PR 7 y 8 de VC-m.

**Home (Martu):** `HomeViewModel` suma un `catch` (C44). Es un cambio chico y se avisa en el PR.

**Tests:**
- `FakeMealRepository` (home) suma los métodos nuevos.
- `FakeMealDao`, privado de `MealRepositoryImplTest`, suma `observeById`, `update` y `deleteById`, registra lo que recibe y puede tirar excepciones.
- Los tests de `FoodCaptureViewModel` pasan a crearlo con un repositorio falso.

Ninguno cambia lo que comprueba.

**Fiore:** `specs/PANTALLAS.md` línea 256 (dice que 03 carga, que 02 elimina y que el total sale de un `SUM`) y punto 4 de la §17 (eliminar no tiene control). Se le avisa en el PR para que lo actualice: carga en 07, elimina en 08 y el total se suma en el ViewModel, como Home (decisión 007).

No se tocan `Rutas.kt`, `MainActivity` ni `RUTAS_*`.

## Criterios de aceptación (EARS)

Cada línea es un test. Si no se te ocurre el test, la línea está mal escrita.

### Validación del formulario (RF03)
Regla de formato: el texto se evalúa sin espacios en los bordes; **primero el formato y después el signo**.
- kcal: tiene que coincidir con `^-?\d+$` y entrar en un `Int`. Si no, es `NOT_A_WHOLE_NUMBER`.
- Macros: tienen que coincidir con `^-?\d+([.,]\d+)?$`. Si no, es `NOT_A_NUMBER`.
- Si el número es menor que 0, es `NEGATIVE`.

| Texto | kcal | macro |
|---|---|---|
| `""` o `"  "` | REQUIRED | REQUIRED |
| `"520"`, `" 520 "`, `"0"`, `"-0"` | válido (520 / 0) | válido |
| `"-5"` | NEGATIVE | NEGATIVE |
| `"520.5"`, `"520,5"` | NOT_A_WHOLE_NUMBER | válido (520.5) |
| `"-5.5"` | NOT_A_WHOLE_NUMBER | NEGATIVE |
| `"abc"`, `"+5"`, `"1e3"`, `"NaN"`, `"Infinity"`, `"12,5,3"`, `"1.234,5"` | NOT_A_WHOLE_NUMBER | NOT_A_NUMBER |
| `"3000000000"` | NOT_A_WHOLE_NUMBER | válido |

- C1. SI el nombre está vacío o tiene solo espacios, ENTONCES `validateMeal` DEBE devolver `Invalid` con `NAME → REQUIRED`.
- C2. CUANDO el formulario es válido, la `Meal` de `Valid` DEBE tener el nombre sin espacios al principio ni al final.
- C3. SI no se eligió un tipo de comida, ENTONCES `validateMeal` DEBE devolver `TYPE → REQUIRED`.
- C4. `validateMeal` DEBE dar para kcal el resultado de cada fila de la tabla.
- C5. `validateMeal` DEBE dar para proteína, carbohidratos y grasa el resultado de cada fila de la tabla.
- C6. Los macros DEBEN aceptar coma y punto decimal: `"12,5"` y `"12.5"` dan `12.5`.
- C7. El cero DEBE ser válido en kcal y en los tres macros.
- C8. SI hay varios campos inválidos, ENTONCES `Invalid` DEBE traer el error de cada uno, no solo el primero.
- C9. CUANDO el formulario es válido, `Valid` DEBE traer una `Meal` con el `id` pedido (0 para una nueva) y la fecha, el tipo y los números del formulario.

### Room y repositorio (RF04, RF06)
- C10. CUANDO se actualiza una comida guardada, `observeByDate` DEBE emitir la lista con los valores nuevos y el mismo `id`.
- C11. CUANDO se elimina una comida por id, `observeByDate` DEBE emitir la lista sin ella y `deleteById` DEBE devolver 1; con un id que no existe, DEBE devolver 0 y no cambiar la tabla.
- C12. `observeById` DEBE emitir la comida guardada y, después de eliminarla, `null`.
- C13. `observeMeal` DEBE devolver `Meal` (no Entity) con el contenido de la Entity del DAO, y `null` cuando el DAO emite `null`.
- C14. CUANDO se actualiza o elimina por el repositorio, el DAO DEBE recibir la Entity con el mismo `id` o ese `id`, respectivamente.
- C15. SI Room falla al escribir (por ejemplo, `addMeal` de una comida con un `id` que ya existe, que con ABORT tira `SQLiteConstraintException`), ENTONCES el repositorio DEBE lanzar `MealDataException(STORAGE)`. Va en `androidTest/` con Room en memoria, porque `SQLiteException` no se puede crear en un test JVM sin Robolectric.
- C16. SI una Entity leída tiene un tipo o una fecha inválidos, ENTONCES el Flow de `observeMealsByDate` y el de `observeMeal` DEBEN fallar con `MealDataException(INVALID_DATA)`.
- C17. SI `update` devuelve 0 (la comida se borró mientras se editaba), ENTONCES `updateMeal` DEBE lanzar `MealDataException(NOT_FOUND)`.

### Listado por fecha y total diario (RF04, RF07)
- C18. Con el `SavedStateHandle` vacío, `NutritionViewModel` DEBE pedirle al repositorio la fecha de hoy según el `Clock` inyectado.
- C19. CUANDO se llama a `onDateSelected(f)`, el ViewModel DEBE pedirle al repositorio la fecha `f`, mostrar solo sus comidas y guardar `f.toString()` en `savedState[KEY_SELECTED_DATE]`.
- C20. SI `savedState[KEY_SELECTED_DATE]` ya trae una fecha, ENTONCES el ViewModel DEBE pedir esa y no la de hoy.
- C21. `Content` DEBE traer las comidas del día en el orden del repositorio, cada una con `id`, tipo, nombre y kcal.
- C22. El total DEBE sumar las kcal y, para cada macro, redondear la suma con `roundToInt()` (no sumar redondeos). Con las mismas comidas DEBE dar lo mismo que `HomeViewModel`, y DEBE cambiar cuando el repositorio emite una lista nueva.
- C23. La comida pendiente DEBE ser el primer tipo, en el orden desayuno, almuerzo, merienda y cena, que no tenga ninguna comida ese día. Si están los cuatro, `pending` DEBE ser `null`.
- C24. SI la fecha no tiene comidas, ENTONCES el estado DEBE ser `Content` con la lista vacía, total 0 y `pending = BREAKFAST`.
- C25. SI el Flow del repositorio falla con `MealDataException`, ENTONCES el estado DEBE pasar a `Error` con un texto de `strings.xml`. CUANDO se llama a `onRetry()`, DEBE volver a pedir las comidas.
- C26. CUANDO `uiState` se deja de colectar y se vuelve a colectar en menos de 5 s (rotación), el repositorio falso DEBE registrar una sola suscripción.
- C27. `selectedMillisToLocalDate` DEBE devolver 2026-10-24 para la medianoche UTC de ese día, y `toPickerMillis` DEBE ser su inversa.

### Confirmar y guardar desde 07 (RF03, RF04, RF07)
- C28. CUANDO se abre 07 con `mealId = -1` después de una captura de voz o foto, `confirmation.form` DEBE venir con `draftName`, el tipo del borrador (almuerzo), la `defaultDate` y las kcal y los macros del borrador como texto.
- C29. CUANDO se llama a `onManualEntry()` y después a `onOpenConfirmation(-1, …)`, el formulario DEBE venir vacío, sin tipo y con la `defaultDate`, aunque antes haya habido una captura de foto.
- C30. CUANDO se abre 07 con un id guardado, el formulario DEBE venir con los valores de esa comida.
- C31. SI se llama a `onOpenConfirmation` con el mismo `mealId` mientras `confirmation` no es `null` (rotación), ENTONCES el formulario DEBE conservar lo escrito con `onFormChange` y no DEBE volver a pedir la comida al repositorio.
- C32. CUANDO se llama a `onDiscard()`, `confirmation` DEBE ser `null`; una nueva apertura arranca de cero.
- C33. SI se abre 07 con un id que no existe, ENTONCES `confirmation.notFound` DEBE ser `true`.
- C34. SI se toca guardar con el formulario inválido, ENTONCES `confirmation.errors` DEBE traer el error de cada campo y no DEBE escribirse en el repositorio.
- C35. CUANDO se llama a `onFormChange` después de un guardado inválido, DEBE desaparecer el error de los campos que cambiaron, y los demás DEBEN quedar.
- C36. CUANDO se guarda una comida nueva válida, DEBE llamarse una vez a `addMeal` con los valores validados y emitirse `Saved` una sola vez. Después, `draft` y `confirmation` DEBEN ser `null`.
- C37. CUANDO se guarda una edición válida, DEBE llamarse a `updateMeal` con el mismo `id` (no a `addMeal`) y emitirse `Saved`.
- C38. MIENTRAS `isSaving` es `true`, un segundo `onSave()` no DEBE escribir otra vez ni emitir otro `Saved`.
- C39. SI guardar falla con `MealDataException`, ENTONCES el formulario DEBE conservar lo escrito, `saveError` DEBE tener un texto de `strings.xml`, `isSaving` DEBE volver a `false` y no DEBE emitirse `Saved`.
- C40. CUANDO el análisis de voz emite `AnalysisDone`, el borrador DEBE pasar a ser `PhotoMealDraft`. El borrador inicial sigue siendo `null`, así voz y foto llegan a 07 con la misma comida estimada.

### Detalle y eliminación en 08 (RF04, RF07)
- C41. Al crearse, `MealDetailViewModel` DEBE pedir la comida del `mealId` de la ruta y pasar de `Loading` a `Content`.
- C42. SI la comida no existe, ENTONCES el estado DEBE ser `NotFound`. SI `observeMeal` falla con `MealDataException`, ENTONCES DEBE ser `Error` con un texto de `strings.xml`.
- C43. CUANDO se toca "Delete Meal", `confirmingDelete` DEBE ser `true`. CUANDO se cancela, DEBE volver a `false` y no DEBE eliminarse nada.
- C44. SI el Flow de comidas de `HomeViewModel` falla con `MealDataException`, ENTONCES Home DEBE mostrar 0 kcal y 0 g, sin cerrarse la app.
- C45. CUANDO se confirma, DEBE llamarse una vez a `deleteMeal(id)` y emitirse `Deleted` una sola vez.
- C46. MIENTRAS `isBusy` es `true`, otro `onConfirmDelete()` u `onDuplicate()` no DEBE llamar al repositorio ni emitir otro evento.
- C47. MIENTRAS se elimina, o después de eliminar con éxito, un `null` de `observeMeal` no DEBE pasar el estado a `NotFound`.
- C48. SI eliminar o duplicar falla con `MealDataException`, ENTONCES `actionError` DEBE tener un texto de `strings.xml`, `isBusy` DEBE volver a `false`, la comida DEBE seguir en pantalla y no DEBE emitirse ningún evento.
- C49. CUANDO se toca "Duplicate Meal", DEBE llamarse una vez a `addMeal` con la misma comida y `id = 0`, y emitirse `Duplicated`.
- C50. CUANDO `uiState` se deja de colectar y se vuelve a colectar en menos de 5 s, el repositorio falso DEBE registrar una sola suscripción a `observeMeal`.

### A mano (no tienen test automático)
- C51. CUANDO se guarda una comida y se cierra la app (`adb shell am force-stop`), al reabrirla la comida DEBE seguir en 02 y en el total (RF04).
- C52. El ícono de calendario de 01 DEBE abrir un `DatePickerDialog` de Material 3 con la fecha elegida, y elegir otra DEBE cambiar lo que listan 01 y 02.

Plantillas: **La app DEBE** (siempre) · **CUANDO** (ante un evento) · **MIENTRAS** (durante un estado) · **SI ... ENTONCES** (algo sale mal) · **DONDE** (opcional, para ampliaciones).

Casos de la plantilla que no aplican: sin conexión, 404, 503 y JSON inválido (todo es local).

## Fuera de alcance
- Guardar ingredientes en Room, y una tabla nueva o subir la versión de la base.
- Buscar alimentos (VC-y) y "Retake Photo".
- Eliminar desde 02 o con deslizar: se elimina solo desde 08.
- Maquetar 07 y 08: son los PR 7 y 8 de VC-m. En VC-l solo se tocan para que compile.
- Cambiar `Rutas.kt`, `MainActivity` o `RUTAS_*`.
- Que "Log breakfast" de Home elija el tipo desayuno.
- Fechas futuras: se pueden elegir y no se validan.
- Errores de Room al leer que no sean de datos inválidos (base corrupta): no tienen criterio.

## Tests
| Criterio | Test | Dónde | Vi el rojo |
|---|---|---|---|
| C1 | `` `C1 nombre vacio o con solo espacios es REQUIRED` `` | `test/` · `MealValidationTest` | sí, 2026-10-24 |
| C2 | `` `C2 el nombre se guarda sin espacios en los bordes` `` | `test/` · `MealValidationTest` | sí, 2026-10-24 |
| C3 | `` `C3 sin tipo es TYPE REQUIRED` `` | `test/` · `MealValidationTest` | sí, 2026-10-24 |
| C4 | `` `C4 kcal da el resultado de cada fila de la tabla` `` | `test/` · `MealValidationTest` | sí, 2026-10-24 |
| C5 | `` `C5 los macros dan el resultado de cada fila de la tabla` `` | `test/` · `MealValidationTest` | sí, 2026-10-24 |
| C6 | `` `C6 los macros aceptan coma y punto decimal` `` | `test/` · `MealValidationTest` | sí, 2026-10-24 |
| C7 | `` `C7 el cero es valido en kcal y macros` `` | `test/` · `MealValidationTest` | sí, 2026-10-24 |
| C8 | `` `C8 trae el error de cada campo invalido` `` | `test/` · `MealValidationTest` | sí, 2026-10-24 |
| C9 | `` `C9 un formulario valido da la Meal con el id pedido` `` | `test/` · `MealValidationTest` | sí, 2026-10-24 |
| C10 | `C10_update_cambia_los_valores_y_conserva_el_id` | `androidTest/` · `MealDaoTest` | pendiente: compila, falta correrlo con el emulador |
| C11 | `C11_deleteById_saca_la_comida_y_devuelve_las_filas_borradas` | `androidTest/` · `MealDaoTest` | pendiente: compila, falta correrlo con el emulador |
| C12 | `C12_observeById_emite_la_comida_y_null_al_borrarla` | `androidTest/` · `MealDaoTest` | pendiente: compila, falta correrlo con el emulador |
| C13 | `` `C13 observeMeal devuelve Meal y null cuando no existe` `` | `test/` · `MealRepositoryImplTest` | sí, 2026-10-24 |
| C14 | `` `C14 updateMeal y deleteMeal llegan al DAO con el mismo id` `` | `test/` · `MealRepositoryImplTest` | sí, 2026-10-24 |
| C15 | `C15_un_id_repetido_es_MealDataException_STORAGE` | `androidTest/` · `MealRepositoryRoomTest` | pendiente: compila, falta correrlo con el emulador |
| C16 | `` `C16 una Entity invalida es MealDataException INVALID_DATA` `` | `test/` · `MealRepositoryImplTest` | sí, 2026-10-24 |
| C17 | `` `C17 update de una comida borrada es NOT_FOUND` `` | `test/` · `MealRepositoryImplTest` | sí, 2026-10-24 |
| C18 | `` `C18 arranca pidiendo la fecha de hoy del Clock` `` | `test/` · `NutritionViewModelTest` | sí, 2026-10-24 |
| C19 | `` `C19 elegir una fecha la pide y la guarda en SavedStateHandle` `` | `test/` · `NutritionViewModelTest` | sí, 2026-10-24 |
| C20 | `` `C20 al recrearse usa la fecha guardada` `` | `test/` · `NutritionViewModelTest` | sí, 2026-10-24 |
| C21 | `` `C21 lista las comidas del dia en orden` `` | `test/` · `NutritionViewModelTest` | sí, 2026-10-24 |
| C22 | `` `C22 el total redondea la suma y cambia con cada emision` `` | `test/` · `NutritionViewModelTest` | sí, 2026-10-24 |
| C23 | `` `C23 la pendiente es el primer tipo sin comidas` `` | `test/` · `NutritionViewModelTest` | sí, 2026-10-24 |
| C24 | `` `C24 un dia sin comidas da total 0 y pendiente BREAKFAST` `` | `test/` · `NutritionViewModelTest` | sí, 2026-10-24 |
| C25 | `` `C25 un error de datos pasa a Error y onRetry vuelve a pedir` `` | `test/` · `NutritionViewModelTest` | sí, 2026-10-24 |
| C26 | `` `C26 recolectar en menos de 5 s no vuelve a suscribirse` `` | `test/` · `NutritionViewModelTest` | sí, 2026-10-24 |
| C27 | `` `C27 los millis del DatePicker son la fecha en UTC` `` | `test/` · `DatePickerDatesTest` | sí, 2026-10-24 |
| C28 | `` `C28 con borrador el formulario viene precargado` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24 |
| C29 | `` `C29 la carga manual viene vacia aunque antes hubo foto` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24 |
| C30 | `` `C30 con id el formulario trae la comida guardada` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24 |
| C31 | `` `C31 reabrir con el mismo id conserva lo escrito y no recarga` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24 |
| C32 | `` `C32 onDiscard cierra el formulario` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24 |
| C33 | `` `C33 un id que no existe es notFound` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24 |
| C34 | `` `C34 guardar invalido muestra errores y no escribe` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24 |
| C35 | `` `C35 corregir un campo borra solo su error` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24 |
| C36 | `` `C36 guardar nueva llama a addMeal emite Saved y limpia` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24 |
| C37 | `` `C37 guardar edicion llama a updateMeal con el mismo id` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24 |
| C38 | `` `C38 dos toques en guardar escriben una sola vez` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24 |
| C39 | `` `C39 si guardar falla conserva lo escrito y no emite Saved` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24 |
| C40 | `` `C40 al terminar el analisis de voz el borrador es el de la foto` `` | `test/` · `FoodCaptureViewModelTest` | sí, 2026-10-24 |
| C41 | `` `C41 carga la comida de la ruta` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-24 |
| C42 | `` `C42 no existe es NotFound y un error de datos es Error` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-24 |
| C43 | `` `C43 Delete pide confirmacion y cancelar no borra` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-24 |
| C44 | `` `C44 un error de datos en Home muestra 0 sin romper` `` | `test/` · `HomeViewModelTest` | sí, 2026-10-24 (se cae con `MealDataException` sin atrapar) |
| C45 | `` `C45 confirmar borra una vez y emite Deleted` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-24 |
| C46 | `` `C46 mientras esta ocupado no repite borrar ni duplicar` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-24 |
| C47 | `` `C47 el null despues de borrar no muestra NotFound` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-24 |
| C48 | `` `C48 si borrar o duplicar falla muestra el error y no emite` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-24 |
| C49 | `` `C49 Duplicate guarda una copia con id 0` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-24 |
| C50 | `` `C50 recolectar en menos de 5 s no vuelve a pedir la comida` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-24 |
| C51 | recorrido a mano | emulador | no aplica |
| C52 | recorrido a mano | emulador | no aplica |

- Los tests de `androidTest/` van con guion bajo (con `minSdk` 29 el DEX no acepta espacios) y se corren a mano con `./gradlew connectedDebugAndroidTest`. La salida se guarda en `docs/evidencia/tests/`.
- Los de ViewModel usan un repositorio falso, `runTest`, la regla de `Dispatchers.Main` y `UnconfinedTestDispatcher` para observar, como `HomeViewModelTest`. El falso cuenta las suscripciones con `onStart` (C26 y C50). `deleteMeal` lo saca de la lista que emite y `updateMeal` lo reemplaza.
- `FoodConfirmationViewModelTest` prueba `FoodCaptureViewModel`: el nombre separa lo de 07 de lo de 04 a 06.

## Auditoría
- auditor-spec (2026-10-24): 15 hallazgos (2 BLOQUEA, 7 ALTA, 5 MEDIA, 1 BAJA). Se corrigieron todos en la spec:
  - #1 (BLOQUEA): `draft` nulo y `onManualEntry`, C29.
  - #2 (BLOQUEA): el formulario abierto sobrevive a la rotación y `onDiscard`, C31 y C32.
  - #3: las jerarquías de estado y qué muestran 01 y 02.
  - #4: `Clock` inyectado y la clave de `SavedStateHandle`, C18 a C20.
  - #5: doble toque en 08, C46.
  - #6: la tabla de valores límite.
  - #7: C15 pasa a `androidTest/` con un id repetido.
  - #8: los errores que faltaban:
    - `NOT_FOUND`, C17;
    - `observeMeal`, C16 y C42;
    - duplicar, C48;
    - Home, C44.
  - #9: las fechas del DatePicker en UTC, C27.
  - #10: C47.
  - #11: los falsos y el borrador inicial `null`.
  - #12: el redondeo y el día vacío, C22 y C24.
  - #13: C35 y C36.
  - #14: los archivos que se tocan y el aviso a Fiore.
  - #15: suscripciones en vez de llamadas, C26 y C50.
- Decidido por la IA en la corrección (Sofi puede cambiarlo):
  - corregir un campo borra solo su error, sin revalidar todo;
  - Home muestra 0 ante un error de datos;
  - las metas de 01 son las de `HomeDemoData`, más la grasa de 65 g de Nutrition.

## Historial
- 2026-10-24. Spec creada para VC-l, a partir del plan aprobado y de la entrevista con Sofi. Decisiones de la entrevista:
  - kcal entero y macros con coma o punto;
  - los cuatro números son obligatorios;
  - en la carga manual la fecha arranca en la de 01 y el tipo es obligatorio;
  - el borrador de voz pasa a ser el de la foto;
  - `NutritionViewModel` se ata al grafo de Nutrition.
- 2026-10-24. Corregida según la auditoría: de 42 a 52 criterios.
- 2026-10-24. Tests en rojo (paso 2 de VC-l): fallan los 46 de `test/`, cada uno por el resultado; los 4 de `androidTest/` compilan y quedan para correr con el emulador.
