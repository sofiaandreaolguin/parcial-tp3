# Carga, edición, eliminación, ingredientes y total diario de comidas

Flujo: Nutrition
Requisitos: RF03, RF04, RF06, RF07, RF08
Pantallas: Nutrition 01 (Dashboard), 02 (Today's Meals), 07 (Food Confirmation) y 08 (Meal Details). Los ingredientes (PR 1 de 2) tocan 01, 02, 07 y 08. En VC-l se hacen los datos, los ViewModels y la adaptación de 01 y 02; las pantallas 07 y 08 se maquetan después, en los PR 7 y 8 de VC-m.
Responsable: Sofi

## Qué hace
La persona carga una comida a mano (o confirma la que "estimó" la voz o la foto), la ve en el listado del día elegido y en el total diario, la puede editar y la puede eliminar con confirmación. Todo queda en Room: sigue ahí al cerrar y reabrir la app, y el total de Nutrition y de Home cambia solo al agregar, editar o eliminar.

Se apoya en lo que dejó VC-h (`specs/funcionales/registro-de-comidas.md`): `Meal`, `MealEntity`, `MealDao.insert` / `observeByDate`, mappers y `MealRepository.observeMealsByDate` / `addMeal`.

**Ingredientes (PR 1 de 2, 2026-10-27).** La comida guarda también sus ingredientes (nombre, gramos y kcal) dentro de su fila. Los de foto y voz se guardan al confirmar, y se ven en 01, 02 y 08 como en el Figma. La carga manual y la búsqueda (EXT02) siguen guardando sin ingredientes hasta el PR 2, que cambia la carga manual a pop-up y saca la edición.

## Datos
- DTO: ninguno (la consigna no tiene endpoint de comidas). El JSON de los ingredientes es el formato de una columna de la Entity, no de una API: RF06 pide DTO solo para lo que llega por Retrofit.
- Modelo: `Meal` y `MealType`. `Meal` suma `ingredients: List<Ingredient>`, vacía por defecto.
- Entity: `MealEntity` suma la columna `ingredients: String`, con la lista en JSON y `"[]"` por defecto.
  - **No es una tabla aparte:** la lista vive en la fila de la comida y se borra con ella.
  - Sin Entity ni DAO nuevos (en clase: un DAO por entidad).
  - `VoraDatabase` pasa de `version = 4` a `5`. Cambiar el esquema obliga a subirla. No se escribe `Migration`: el fallback destructivo de la decisión 003 (`dropAllTables = true`) borra **todas** las tablas una vez y las vuelve a crear vacías con el esquema nuevo. Se pierden los registros (comidas, caché de entrenamientos e historial del coach); la caché se vuelve a llenar desde el servidor.
  - Antes de unir el PR, confirmar que ningún otro PR abierto sube también a `version = 5`.
- Formulario (nuevo, en `domain/`): `MealForm(name: String = "", type: MealType? = null, date: LocalDate, kcal: String = "", protein: String = "", carbs: String = "", fat: String = "")`. Los números van como texto porque es lo que se tipea. **No tiene ingredientes:** viajan aparte, en `ConfirmationState.ingredients`.

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
| Ingrediente | `domain/model/Meal.kt` | `data class Ingredient(val name: String, val grams: Int, val kcal: Int)`; `Meal` suma `val ingredients: List<Ingredient> = emptyList()` como último parámetro |
| Columna | `data/local/entity/MealEntity.kt` | suma `val ingredients: String = "[]"` como último parámetro |
| Base | `data/local/VoraDatabase.kt` | `version = 5` |
| Mappers | `data/repository/MealMappers.kt` | `toEntity()` escribe la lista con `Gson().toJson`: un arreglo con un objeto por ingrediente y las claves `name`, `grams` y `kcal`. `toDomain()` la lee con `Gson().fromJson(texto, Array<Ingredient>::class.java)`, directo a `Ingredient`, sin una clase aparte para el JSON. **Renombrar un campo de `Ingredient` rompe lo guardado**, porque Gson usa el nombre del campo. Gson no tira `IllegalArgumentException`, así que `toDomain()` la lanza a mano en estos casos, y el repositorio ya la traduce a `INVALID_DATA`: <br>• Gson tira `JsonParseException` (texto que no es JSON, un objeto en vez de un arreglo, un número con texto o decimales). <br>• `fromJson` devuelve `null` (`""` o `"null"`). <br>• La lista trae un elemento `null`. <br>• A un ingrediente le falta el nombre o viene `null`: Gson lo deja en `null` sin avisar, así que se revisa después de leer. <br>Un número que falta se lee como 0 y no es error al leer: lo frena la validación antes de guardar |
| Validación de ingredientes | `domain/MealValidation.kt` | `fun isValidIngredient(ingredient: Ingredient): Boolean`: nombre no vacío ni solo espacios, `grams > 0` y `kcal >= 0` |

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
data class MealRow(
    val id: Long, val type: MealType, val name: String, val kcal: Int,
    val ingredients: List<Ingredient> = emptyList(),
)
```
- Las metas son de demostración. kcal, proteína y carbos son las de Home (`HomeDemoData`: 2100, 140 y 220). La grasa, 65 g, es solo de Nutrition, en `NutritionDemoData`, como dice el Figma. El progreso se topa en 1.
- El consejo y la fecha del encabezado los arma la pantalla.

**Qué muestran 01 y 02 sin ingredientes:**
- **01:** una fila por comida, con el tipo como título, el nombre como bajada y las kcal.
- **02:** las comidas agrupadas por tipo. El encabezado del grupo lleva el tipo y la suma de kcal; cada comida es una línea con su nombre y sus kcal, y tocarla abre su detalle.

**Con ingredientes, como el Figma:**
- **01:** la bajada son los nombres de los ingredientes separados por `" · "`, en una sola línea; si no entra, se corta con `…` (`maxLines = 1`, `TextOverflow.Ellipsis`), como el Figma.
- **02:** en vez de la línea de la comida, una línea por ingrediente (nombre, gramos en gris abajo y kcal), una comida debajo de la otra y sin el nombre de la comida. Tocar cualquier línea abre la 08 de su comida. El encabezado sigue sumando las kcal de las comidas.
- **08:** "Ingredients" muestra una tarjeta por ingrediente con nombre, gramos en la línea gris y kcal. Sin ingredientes, una sola tarjeta con el nombre de la comida y la proteína en la línea gris.

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
    val ingredients: List<Ingredient> = emptyList(),  // los que se guardan con la comida
)
```
- **Ingredientes en 07 (PR 1):** `onOpenConfirmation` suma un cuarto parámetro, `draftIngredients: List<Ingredient> = emptyList()`. Lo arma el grafo con los ingredientes del borrador y sus nombres resueltos con `getString`, igual que `draftName`, porque el ViewModel no tiene `Context`. Los nombres quedan guardados en el idioma que tenía la app al confirmar. `DraftIngredient` no cambia.
  - Comida nueva con borrador: `ingredients` son los `draftIngredients`.
  - Comida nueva sin borrador (carga manual o `onPrefill` de la búsqueda): `ingredients` queda vacía.
  - Comida guardada (edición): `ingredients` son los de esa comida y se conservan al guardar, aunque cambien nombre, tipo, kcal o macros.
  - **La comida se guarda armada, de una vez:** mientras 07 está abierta vive solo en memoria. Al guardar, la `Meal` de `validateMeal` se completa con `copy(ingredients = …)` y va en un solo `addMeal` (un INSERT de una fila) o `updateMeal`. `validateMeal` no cambia.
  - Antes de escribir, cada ingrediente pasa por `isValidIngredient`, también al editar. Si alguno no cumple, no se escribe nada y `saveError` toma `R.string.nutrition_error_ingredients`.
  - 07 muestra tarjetas solo para una comida nueva con borrador; al editar muestra el formulario. **Las tarjetas se dibujan con `confirmation.ingredients`**, no con `draft.ingredients`, así lo que se ve es lo que se guarda.
- **Eventos nuevos:**
  - `onManualEntry()` deja `draft = null`. Lo llaman "Log Manually" (03) y "Type instead" (04) antes de navegar, como `abrirFoto`.
  - `onOpenConfirmation(mealId: Long, defaultDate: LocalDate, draftName: String, draftIngredients: List<Ingredient> = emptyList())`. `draftName` y los nombres de los ingredientes los resuelve el grafo (`rememberAbrirConfirmacion`) con `getString`, porque el ViewModel no tiene `Context`.
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
- 07 y 08 se maquetaron en los PR 7 y 8 de VC-m.

**PR 1 de ingredientes (2026-10-27):**
- Datos y dominio: `domain/model/Meal.kt`, `data/local/entity/MealEntity.kt`, `data/local/VoraDatabase.kt` (versión 5 y su KDoc, que hoy dice "la version 4 borra"), `data/repository/MealMappers.kt` y `domain/MealValidation.kt`.
- 07: `FoodCaptureViewModel`, `FoodCaptureUiState` (su KDoc dice que el borrador "no se guarda": deja de ser cierto para los ingredientes), `FoodConfirmationRoute` y `FoodConfirmationScreen` (tarjetas con `confirmation.ingredients`).
- 08: `MealDetailViewModel` (validar al duplicar, C71) y `MealDetailScreen`.
- 01 y 02: `NutritionViewModel`, `NutritionUiState`, `NutritionDemoData`, `NutritionDashboardScreen` y `TodaysMealsScreen`.
- `res/values/strings.xml` y `res/values-es/strings.xml`: `nutrition_error_ingredients` (hoy no existe).
- **Fiore:** `ui/navigation/NutritionNavGraph.kt`. `rememberAbrirConfirmacion` resuelve con `getString` los nombres de `draft.ingredients` y los pasa como cuarto argumento de `onOpenConfirmation`. Se le avisa en el PR.

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

### Ingredientes guardados (RF03, RF04, RF06, RF07)
- C53. `Meal.toEntity().ingredients` DEBE ser un arreglo JSON con un objeto por ingrediente, en el mismo orden, con las claves `name`, `grams` y `kcal` (el test lo lee con `JsonParser`, no compara el texto exacto), y `toDomain()` DEBE devolver la misma lista.
- C54. Una comida sin ingredientes DEBE guardarse con `"[]"` y leerse con la lista vacía.
- C55. Un nombre con coma, comillas, `|`, `·`, tildes o espacios DEBE volver igual después de ir y volver por los mappers.
- C56. SI `ingredients` es `"hola"`, `"{}"`, `""`, `"null"`, `"[null]"`, `[{"name":"x","grams":"abc","kcal":1}]`, `[{"grams":1,"kcal":1}]` o `[{"name":null,"grams":1,"kcal":1}]`, ENTONCES `observeMealsByDate` y `observeMeal` DEBEN fallar con `MealDataException(INVALID_DATA)`.
- C57. SI a un ingrediente del JSON le faltan los gramos o las kcal, ENTONCES `toDomain()` DEBE leerlos como 0, sin error.
- C58. CUANDO se guarda con `addMeal` una comida con ingredientes en Room, `observeMeal` DEBE devolverla con los mismos ingredientes.
- C59. `isValidIngredient` DEBE ser `false` con el nombre vacío o con solo espacios, con `grams` en 0 o negativo, o con `kcal` negativo; y `true` con nombre, `grams > 0` y `kcal` en 0 o más.
- C60. CUANDO se abre 07 para una comida nueva con borrador, `confirmation.ingredients` DEBE ser la lista `draftIngredients` que pasó el grafo.
- C61. CUANDO se guarda una comida nueva con borrador, DEBE llamarse una sola vez a `addMeal`, con la `Meal` y sus ingredientes juntos.
- C62. SI al guardar algún ingrediente no pasa `isValidIngredient`, ENTONCES no DEBE llamarse a `addMeal` ni a `updateMeal`, `saveError` DEBE tener un texto de `strings.xml`, `isSaving` DEBE quedar en `false` y no DEBE emitirse `Saved`.
- C63. CUANDO se abre 07 sin borrador (después de `onManualEntry()` o con `onPrefill`), `confirmation.ingredients` DEBE estar vacía aunque se pasen `draftIngredients`, y la comida DEBE guardarse sin ingredientes.
- C64. CUANDO se abre 07 con un id guardado, `confirmation.ingredients` DEBE traer los de esa comida, y guardar la edición DEBE llamar a `updateMeal` con esos mismos ingredientes aunque haya cambiado el formulario.
- C65. CUANDO se duplica una comida con ingredientes, `addMeal` DEBE recibir los mismos ingredientes.
- C66. Cada `MealRow` de `Content` DEBE traer los ingredientes de su comida, en el mismo orden.
- C71. SI al duplicar algún ingrediente no pasa `isValidIngredient`, ENTONCES no DEBE llamarse a `addMeal`, `actionError` DEBE tener un texto de `strings.xml`, `isBusy` DEBE volver a `false` y no DEBE emitirse `Duplicated`.

### A mano (no tienen test automático)
- C51. CUANDO se guarda una comida y se cierra la app (`adb shell am force-stop`), al reabrirla la comida DEBE seguir en 02 y en el total (RF04).
- C52. El ícono de calendario de 01 DEBE abrir un `DatePickerDialog` de Material 3 con la fecha elegida, y elegir otra DEBE cambiar lo que listan 01 y 02.
- C67. CUANDO se guarda una comida desde la foto o la voz, 01 DEBE mostrar como bajada sus ingredientes separados por `" · "` en una sola línea, cortada con `…` si no entra en 360 dp, y una comida manual DEBE seguir mostrando su nombre.
- C68. En 02, una comida con ingredientes DEBE verse como una línea por ingrediente (nombre, gramos y kcal), y tocar cualquiera de esas líneas DEBE abrir la 08 de esa comida. Una manual DEBE verse como dice "Qué muestran 01 y 02 sin ingredientes".
- C69. En 08, una comida con ingredientes DEBE mostrar una tarjeta por ingrediente con nombre, gramos y kcal. Una manual DEBE mostrar una sola tarjeta con su nombre y la proteína en la línea gris.
- C70. CUANDO se instala la versión con `version = 5` sobre una base vieja, la app DEBE abrir sin cerrarse. Room borra una vez todas las tablas y las vuelve a crear vacías, así que se pierden los registros de comidas, caché de entrenamientos e historial del coach. Lo que se cargue después DEBE seguir al cerrar y reabrir (C51).

Plantillas: **La app DEBE** (siempre) · **CUANDO** (ante un evento) · **MIENTRAS** (durante un estado) · **SI ... ENTONCES** (algo sale mal) · **DONDE** (opcional, para ampliaciones).

Casos de la plantilla que no aplican: sin conexión, 404, 503 y JSON inválido (todo es local).

## Fuera de alcance
- Una tabla aparte de ingredientes: la lista vive en la fila de la comida (ver Datos).
- **Va en el PR 2, no en este:** cargar ingredientes a mano, guardar como ingrediente el alimento elegido en la búsqueda (EXT02) y calcular las kcal de la comida como la suma de sus ingredientes. Ese PR también saca la edición de comidas.
- Que −/+, "Remove" y "+ Add Ingredient" de 07 funcionen: siguen como demostración.
- Traducir los nombres ya guardados si se cambia el idioma de la app.
- Corregir una comida guardada con un ingrediente inválido (por ejemplo, gramos en 0 leídos de un JSON viejo): la validación también frena la edición y Duplicate, así que solo se puede eliminar. No se da por la app, porque todo lo que se escribe pasa por `isValidIngredient`, y la edición se va en el PR 2.
- Pasar `deleteById` de `@Query` a `@Delete`, como en la clase: va en un PR aparte, entre el 1 y el 2 (cambia la llamada de C11).
- "Retake Photo".
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
| C10 | `C10_update_cambia_los_valores_y_conserva_el_id` | `androidTest/` · `MealDaoTest` | no: pasa desde el principio porque Room genera el DAO con las anotaciones; control negativo el 2026-10-24 (docs/evidencia/tests/2026-10-24-vc-l-verde.md) |
| C11 | `C11_deleteById_saca_la_comida_y_devuelve_las_filas_borradas` | `androidTest/` · `MealDaoTest` | no: pasa desde el principio porque Room genera el DAO con las anotaciones; control negativo el 2026-10-24 (docs/evidencia/tests/2026-10-24-vc-l-verde.md) |
| C12 | `C12_observeById_emite_la_comida_y_null_al_borrarla` | `androidTest/` · `MealDaoTest` | no: pasa desde el principio porque Room genera el DAO con las anotaciones; control negativo el 2026-10-24 (docs/evidencia/tests/2026-10-24-vc-l-verde.md) |
| C13 | `` `C13 observeMeal devuelve Meal y null cuando no existe` `` | `test/` · `MealRepositoryImplTest` | sí, 2026-10-24 |
| C14 | `` `C14 updateMeal y deleteMeal llegan al DAO con el mismo id` `` | `test/` · `MealRepositoryImplTest` | sí, 2026-10-24 |
| C15 | `C15_un_id_repetido_es_MealDataException_STORAGE` | `androidTest/` · `MealRepositoryRoomTest` | sí, 2026-10-24 (tiró `SQLiteConstraintException` sin traducir) |
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
| C53 | `` `C53 los ingredientes van y vuelven por los mappers en orden` `` | `test/` · `MealMappersTest` | sí, 2026-10-27 |
| C54 | `` `C54 sin ingredientes se guarda corchetes vacios` `` | `test/` · `MealMappersTest` | no: pasa al agregar el campo (`"[]"` por defecto); control negativo rompiendo el mapper (`ingredients = ""`): falla, 2026-10-27 |
| C55 | `` `C55 un nombre con coma comillas y tildes vuelve igual` `` | `test/` · `MealMappersTest` | sí, 2026-10-27 |
| C56 | `` `C56 ingredientes invalidos son MealDataException INVALID_DATA` `` | `test/` · `MealRepositoryImplTest` | sí, 2026-10-27 |
| C57 | `` `C57 gramos o kcal que faltan se leen como 0` `` | `test/` · `MealMappersTest` | sí, 2026-10-27 |
| C58 | `C58_addMeal_guarda_y_devuelve_los_ingredientes` | `androidTest/` · `MealRepositoryRoomTest` | compila, 2026-10-27; falta correrlo con el emulador |
| C59 | `` `C59 isValidIngredient pide nombre gramos mayores a 0 y kcal no negativas` `` | `test/` · `MealValidationTest` | sí, 2026-10-27 |
| C60 | `` `C60 con borrador 07 trae los ingredientes que paso el grafo` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27 |
| C61 | `` `C61 guardar con borrador hace un solo addMeal con sus ingredientes` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27 |
| C62 | `` `C62 un ingrediente invalido no guarda y muestra el error` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27 |
| C63 | `` `C63 sin borrador 07 no tiene ingredientes` `` | `test/` · `FoodConfirmationViewModelTest` | no: pasa al agregar el campo (lista vacía por defecto); control negativo asignando `draftIngredients` sin mirar `draft`: falla, 2026-10-27 |
| C64 | `` `C64 editar conserva los ingredientes guardados` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27 |
| C65 | `` `C65 Duplicate copia los ingredientes` `` | `test/` · `MealDetailViewModelTest` | no: `meal.copy(id = 0)` ya los copia; control negativo con `copy(ingredients = emptyList())`: falla, 2026-10-27 |
| C66 | `` `C66 cada MealRow trae sus ingredientes` `` | `test/` · `NutritionViewModelTest` | sí, 2026-10-27 |
| C71 | `` `C71 Duplicate con un ingrediente invalido no guarda y muestra el error` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-27 |
| C67 | recorrido a mano y captura | emulador (360 y 411 dp) | no aplica |
| C68 | recorrido a mano y captura | emulador (360 y 411 dp) | no aplica |
| C69 | recorrido a mano y captura | emulador (360 y 411 dp) | no aplica |
| C70 | recorrido a mano | emulador | no aplica |

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
- auditor-spec (2026-10-27, ingredientes del PR 1): 13 hallazgos (0 BLOQUEA, 3 ALTA, 7 MEDIA, 3 BAJA). Se corrigieron todos en la spec:
  - #1 y #2 (ALTA): Gson no tira `IllegalArgumentException` y deja un nombre `null` sin avisar. Mappers y C56 con los casos concretos.
  - #3 (ALTA): aviso a Fiore por `NutritionNavGraph`.
  - #4: la firma de `onOpenConfirmation` quedaba vieja en "Eventos nuevos".
  - #5: lista de archivos del PR 1, `nutrition_error_ingredients` y KDoc que quedan falsos.
  - #6: la versión 5 borra todas las tablas (C70) y hay que coordinarla con otros PRs.
  - #7: C54, C63 y C65 pasan sin rojo; llevan control negativo.
  - #8: la validación cubre también la edición y Duplicate (C71), por decisión de Sofi. El caso sin salida queda en Fuera de alcance.
  - #9: las tarjetas de 07 salen de `confirmation.ingredients`.
  - #10: bajada de 01 en una línea con `…` (C67).
  - #11: C53 con las claves del JSON.
  - #12: se sacaron los "como hoy" de C68 y C69.
  - #13: por qué no hay DTO.
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
- 2026-10-24. Implementación (paso 3 de VC-l): 169 tests unitarios y 12 instrumentados en verde, sin modificar tests. C10 a C12 no pudieron verse en rojo (Room genera el DAO) y llevan control negativo.
- 2026-10-25. Pantalla 07 maquetada (PR 7 de VC-m), conectada a esta lógica sin cambiarla. Decisiones con Sofi:
  - con borrador se ven los ingredientes y los totales del formulario; sin borrador (carga manual) o al editar, los campos de `MealForm`;
  - el tipo de la carga manual se elige con chips; "tipo = comida pendiente del día" queda para un PR aparte, con su criterio y su test;
  - el botón dice "Add to [tipo]", o "Save Meal" sin tipo o al editar;
  - −/+, "Remove" y "+ Add Ingredient" muestran el aviso de demostración: los ingredientes no se guardan;
  - `onOpenConfirmation` la llama el grafo antes de navegar, no la Route (CONTEXTO §2). Para editar desde 08 hay que llamarla con el id antes de navegar.
- 2026-10-25. Pantalla 08 maquetada (PR 8 de VC-m), conectada a `MealDetailViewModel` sin cambiar su lógica. Decisiones con Sofi:
  - "Ingredients" muestra una tarjeta con la comida, porque no hay ingredientes guardados;
  - "13:12" y el consejo son fijos;
  - eliminar es un botón "Delete Meal" con un `AlertDialog` (el Figma no lo tiene);
  - "Edit Meal" abre la confirmación con el id antes de navegar.
- 2026-10-27. Ingredientes guardados (PR 1 de 2): C53 a C71 (C71, después de la auditoría). Antes no se guardaban (decisión de VC-l). Plan y entrevista con Sofi:
  - van dentro de la fila de la comida, en una columna JSON, y no en una tabla aparte (sin Entity ni DAO nuevos);
  - la comida se guarda armada, en un solo INSERT, nunca sin ingredientes para completarla después;
  - `Ingredient` es un modelo (`data class`), no una Entity; Gson lee directo a él, sin una clase aparte para el JSON, porque el repo no usa ese patrón en otro lado;
  - al leer, un número que falta es 0; antes de guardar se valida: gramos mayores a 0 y kcal desde 0, como "no negativos" de RF03 y C7;
  - foto y voz guardan los suyos; manual y búsqueda, en el PR 2;
  - en 02, una línea por ingrediente y sin el nombre de la comida, como el Figma;
  - en 01, los ingredientes como bajada, como el Figma;
  - editar conserva los ingredientes, hasta que el PR 2 saque la edición;
  - `DraftIngredient` no cambia: el grafo resuelve los nombres, como `draftName`;
  - el borrado pasa a `@Delete`, como en la clase, en un PR aparte entre el 1 y el 2.
  
  Los tests existentes no cambian: `Meal`, `MealEntity`, `MealRow` y `onOpenConfirmation` traen valores por defecto.
- 2026-10-27. Tests en rojo del PR 1 de ingredientes: fallan 11 de `test/` por el resultado (C53, C55 a C57, C59 a C62, C64, C66 y C71); C54, C63 y C65 pasan y su control negativo falla; C58 compila y queda para correr con el emulador. Firmas sin lógica: `Ingredient`, las columnas y campos nuevos con su valor por defecto, `isValidIngredient` (devuelve `false`), el cuarto parámetro de `onOpenConfirmation` y `nutrition_error_ingredients`.
