# Carga, eliminación, ingredientes y total diario de comidas

Flujo: Nutrition
Requisitos: RF03, RF04, RF06, RF07, RF08
Pantallas: Nutrition 01 (Dashboard), 02 (Today's Meals), 07 (Food Confirmation) y 08 (Meal Details). Los ingredientes (PR 1 de 2) tocan 01, 02, 07 y 08. En VC-l se hacen los datos, los ViewModels y la adaptación de 01 y 02; las pantallas 07 y 08 se maquetan después, en los PR 7 y 8 de VC-m. El PR 2 de ingredientes suma un pop-up de carga sobre 01, 03 y 04, y otro de ingrediente sobre 07.
Responsable: Sofi

## Qué hace
La persona carga una comida a mano (o confirma la que "estimó" la voz o la foto), la ve en el listado del día elegido y en el total diario, y la puede eliminar con confirmación. Todo queda en Room: sigue ahí al cerrar y reabrir la app, y el total de Nutrition y de Home cambia solo al agregar o eliminar. *(Hasta el PR 2 de ingredientes también se podía editar; ver "Edición: fuera de alcance".)*

Se apoya en lo que dejó VC-h (`specs/funcionales/registro-de-comidas.md`): `Meal`, `MealEntity`, `MealDao.insert` / `observeByDate`, mappers y `MealRepository.observeMealsByDate` / `addMeal`.

**Ingredientes (PR 1 de 2, 2026-10-27).** La comida guarda también sus ingredientes (nombre, gramos y kcal) dentro de su fila. Los de foto y voz se guardan al confirmar, y se ven en 01, 02 y 08 como en el Figma. La carga manual y la búsqueda (EXT02) siguen guardando sin ingredientes hasta el PR 2, que cambia la carga manual a pop-up y saca la edición.

**Ingredientes (PR 2 de 2, 2026-10-27).** Toda comida nueva se guarda con al menos un ingrediente, y sus kcal son la suma de las kcal de sus ingredientes.
- **Carga manual:** "Log Manually" (03) y "Type instead" (04) abren un pop-up (una hoja inferior, como la búsqueda) encima de la pantalla, que pide nombre, tipo, proteína, carbohidratos y grasa (no las kcal). La fecha no se pide: es la elegida en 01 con su calendario, como hasta ahora (RF03: la comida se guarda con fecha). "Continue" lo valida y abre 07, donde se agregan los ingredientes a mano con "+ Add Ingredient" (otro pop-up: nombre, gramos y kcal) y se guarda.
- **Búsqueda (EXT02):** "Usar este alimento" abre el mismo pop-up, con el nombre y los macros de la porción ya puestos. Se elige el tipo, se sigue a 07 y el alimento llega como primer ingrediente (nombre, gramos de la porción y sus kcal).
- **07** es el único lugar donde se guarda: voz, foto, búsqueda y manual llegan ahí, y siempre muestra tarjetas de ingrediente, como el Figma. "+ Add Ingredient" y "Remove" funcionan para todas; −/+ siguen de demostración.
- **Macros:** se tipean para toda la comida, en el pop-up. `Ingredient` sigue con nombre, gramos y kcal.
- **Edición:** se saca la lógica; "Edit Meal" queda en 08 como botón de demostración, como en el Figma. Motivo en "Edición: fuera de alcance".

## Datos
- DTO: ninguno (la consigna no tiene endpoint de comidas). El JSON de los ingredientes es el formato de una columna de la Entity, no de una API: RF06 pide DTO solo para lo que llega por Retrofit.
- Modelo: `Meal` y `MealType`. `Meal` suma `ingredients: List<Ingredient>`, vacía por defecto.
- Entity: `MealEntity` suma la columna `ingredients: String`, con la lista en JSON y `"[]"` por defecto.
  - **No es una tabla aparte:** la lista vive en la fila de la comida y se borra con ella.
  - Sin Entity ni DAO nuevos (en clase: un DAO por entidad).
  - `VoraDatabase` pasa de `version = 4` a `5`. Cambiar el esquema obliga a subirla. No se escribe `Migration`: el fallback destructivo de la decisión 003 (`dropAllTables = true`) borra **todas** las tablas una vez y las vuelve a crear vacías con el esquema nuevo. Se pierden los registros (comidas, caché de entrenamientos e historial del coach); la caché se vuelve a llenar desde el servidor.
  - Antes de unir el PR, confirmar que ningún otro PR abierto sube también a `version = 5`.
- **Por qué la comida tiene id y el ingrediente no.** Hace falta un id cuando hay que elegir **una** comida: para abrir su detalle (`meal_details/{mealId}`, NAV04), y para borrarla (`deleteMeal`; hasta el PR 2 también para editarla). Nace en la capa de datos: las comidas no vienen de una API, así que lo crea Room al insertar (`@PrimaryKey(autoGenerate = true)` en `MealEntity`), y `Meal` lo lleva porque el ViewModel y la ruta lo necesitan (`id = 0`: todavía no se guardó). `@Delete` recibe la Entity entera, pero Room busca la fila **solo por la clave primaria**: por eso el objeto tiene que traer su id. `Ingredient` no tiene id porque nunca se elige uno solo en la base: vive dentro de la fila de su comida y se lee y se guarda con ella.
- Formulario (nuevo, en `domain/`): `MealForm(name: String = "", type: MealType? = null, date: LocalDate, kcal: String = "", protein: String = "", carbs: String = "", fat: String = "")`. Los números van como texto porque es lo que se tipea. **No tiene ingredientes:** viajan aparte, en `ConfirmationState.ingredients`.

## Contrato

### Datos y dominio
| Pieza | Archivo | Firma |
|---|---|---|
| DAO | `data/local/dao/MealDao.kt` | suma `@Query("SELECT * FROM meals WHERE id = :id") fun observeById(id: Long): Flow<MealEntity?>`, ~~`@Update suspend fun update(meal: MealEntity): Int`~~ (se va en el PR 2) y `@Delete suspend fun delete(meal: MealEntity): Int` (devuelve las filas borradas; hasta el 27/10 era `@Query("DELETE FROM meals WHERE id = :id") deleteById(id: Long)`, ver Historial) |
| Repositorio | `data/repository/MealRepository.kt` e Impl | suma `fun observeMeal(id: Long): Flow<Meal?>`, ~~`suspend fun updateMeal(meal: Meal)`~~ (se va en el PR 2) y `suspend fun deleteMeal(meal: Meal)`, que llama a `dao.delete(meal.toEntity())`. Si borra 0 filas (la comida ya no estaba) no es un error: lo que se quería ya pasó. Hasta el PR 2, `updateMeal` sí avisaba (C17) |
| Errores | `data/repository/MealDataException.kt` | `class MealDataException(val error: MealError, cause: Throwable? = null) : Exception(cause)` y `enum class MealError { INVALID_DATA, STORAGE }` (`NOT_FOUND` se va en el PR 2: solo lo usaba `updateMeal`) |
| Validación | `domain/MealValidation.kt` | `fun validateMeal(form: MealForm, id: Long = 0): MealValidation`; `sealed interface MealValidation { data class Valid(val meal: Meal); data class Invalid(val errors: Map<MealField, FieldError>) }`; `enum class MealField { NAME, TYPE, KCAL, PROTEIN, CARBS, FAT }`; `enum class FieldError { REQUIRED, NOT_A_NUMBER, NOT_A_WHOLE_NUMBER, NEGATIVE }` |
| Reloj | `di/ClockModule.kt` | `@Provides fun provideClock(): Clock = Clock.systemDefaultZone()`. Los ViewModels piden "hoy" con `LocalDate.now(clock)`, así los tests fijan el día |
| Fecha del DatePicker | `ui/screens/nutrition/DatePickerDates.kt` | `fun selectedMillisToLocalDate(millis: Long): LocalDate` y `fun LocalDate.toPickerMillis(): Long`, las dos con `ZoneOffset.UTC` (el DatePicker de Material 3 trabaja en UTC) |
| Ingrediente | `domain/model/Meal.kt` | `data class Ingredient(val name: String, val grams: Int, val kcal: Int)`; `Meal` suma `val ingredients: List<Ingredient> = emptyList()` como último parámetro |
| Columna | `data/local/entity/MealEntity.kt` | suma `val ingredients: String = "[]"` como último parámetro |
| Base | `data/local/VoraDatabase.kt` | `version = 5` |
| Mappers | `data/repository/MealMappers.kt` | `toEntity()` escribe la lista con `Gson().toJson`: un arreglo con un objeto por ingrediente y las claves `name`, `grams` y `kcal`. `toDomain()` la lee con `Gson().fromJson(texto, Array<Ingredient>::class.java)`, directo a `Ingredient`, sin una clase aparte para el JSON. **Renombrar un campo de `Ingredient` rompe lo guardado**, porque Gson usa el nombre del campo. Gson no tira `IllegalArgumentException`, así que `toDomain()` la lanza a mano en estos casos, y el repositorio ya la traduce a `INVALID_DATA`: <br>• Gson tira `JsonParseException` (texto que no es JSON, un objeto en vez de un arreglo, un número con texto o decimales). <br>• `fromJson` devuelve `null` (`""` o `"null"`). <br>• La lista trae un elemento `null`. <br>• A un ingrediente le falta el nombre o viene `null`: Gson lo deja en `null` sin avisar, así que se revisa después de leer. <br>Un número que falta se lee como 0 y no es error al leer: lo frena la validación antes de guardar |
| Validación de ingredientes | `domain/MealValidation.kt` | `fun isValidIngredient(ingredient: Ingredient): Boolean`: nombre no vacío ni solo espacios, `grams > 0` y `kcal >= 0` |
| Ingrediente a mano (PR 2) | `domain/MealValidation.kt` | `data class IngredientForm(val name: String = "", val grams: String = "", val kcal: String = "")`; `enum class IngredientField { NAME, GRAMS, KCAL }`; `fun validateIngredient(form: IngredientForm): IngredientValidation`; `sealed interface IngredientValidation { data class Valid(val ingredient: Ingredient); data class Invalid(val errors: Map<IngredientField, FieldError>) }`. Reglas en la tabla de C75. `FieldError` no cambia: fuera de rango es `NEGATIVE`, como ya hace `parseGrams` de la búsqueda |
| Datos de la comida (PR 2) | `domain/MealValidation.kt` | `fun validateMealDetails(form: MealForm): Map<MealField, FieldError>`: las mismas reglas de `validateMeal` para nombre, tipo y los tres macros, **sin mirar las kcal**. Vacío = válido. La usa el pop-up de carga |
| Alimento elegido (PR 2) | `ui/screens/nutrition/FoodSearchUiState.kt` | `data class FoodSelection(val form: MealForm, val ingredient: Ingredient)`. `FoodSearchViewModel.onConfirm(date)` pasa de devolver `MealForm?` a `FoodSelection?` (C81) |

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
- **01:** la bajada son los nombres de los ingredientes separados por `" · "`, en hasta dos líneas; si no entra, se corta con `…` (`maxLines = 2`, `TextOverflow.Ellipsis`). El Figma la dibuja en una línea porque sus nombres son cortos; con nombres cortos queda igual.
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
  - ~~Comida guardada (edición): `ingredients` son los de esa comida y se conservan al guardar.~~ *(PR 2: sin edición.)*
  - **La comida se guarda armada, de una vez:** mientras 07 está abierta vive solo en memoria. Al guardar, la `Meal` de `validateMeal` se completa con `copy(ingredients = …)` y va en un solo `addMeal` (un INSERT de una fila); hasta el PR 2, `updateMeal` al editar. `validateMeal` no cambia.
  - Antes de escribir, cada ingrediente pasa por `isValidIngredient` (hasta el PR 2, también al editar). Si alguno no cumple, no se escribe nada y `saveError` toma `R.string.nutrition_error_ingredients`.
  - Hasta el PR 2, 07 mostraba tarjetas solo para una comida nueva con borrador, y el formulario en los demás casos. **Las tarjetas se dibujan con `confirmation.ingredients`**, no con `draft.ingredients`, así lo que se ve es lo que se guarda.
- **Eventos nuevos** *(del PR 1; el PR 2 reemplaza `onManualEntry` y `onFormChange` y le saca `mealId` a `onOpenConfirmation`, ver "Pop-ups y 07")*:
  - `onManualEntry()` deja `draft = null`. Lo llaman "Log Manually" (03) y "Type instead" (04) antes de navegar, como `abrirFoto`.
  - `onOpenConfirmation(mealId: Long, defaultDate: LocalDate, draftName: String, draftIngredients: List<Ingredient> = emptyList())`. `draftName` y los nombres de los ingredientes los resuelve el grafo (`rememberAbrirConfirmacion`) con `getString`, porque el ViewModel no tiene `Context`.
  - `onFormChange(form: MealForm)`, `onSave()` y `onDiscard()`. Este último cierra 07: lo llaman ‹ y el botón atrás.
- **Evento de una sola vez:** `FoodCaptureEvent.Saved`.
- `PhotoAnalysisRoute` usa `state.draft ?: PhotoMealDraft`.

**Pop-ups y 07 (PR 2 de ingredientes).** `FoodCaptureUiState` y `ConfirmationState` cambian así:

```kotlin
data class FoodCaptureUiState(
    @StringRes val transcript: Int = R.string.nutrition_voice_transcript_demo,
    val draft: MealDraft? = null,
    val analysisStep: AnalysisStep = AnalysisStep.Identifying,
    val manualEntry: ManualEntryState? = null,       // null = pop-up de carga cerrado
    val confirmation: ConfirmationState? = null,
)
data class ManualEntryState(
    val form: MealForm,                              // form.kcal no se usa
    val errors: Map<MealField, FieldError> = emptyMap(),
    val ingredients: List<Ingredient> = emptyList(), // vacía a mano; el alimento si viene de la búsqueda
)
data class ConfirmationState(
    val form: MealForm,
    val isSaving: Boolean = false,
    @StringRes val saveError: Int? = null,
    val ingredients: List<Ingredient> = emptyList(),
    val ingredientEntry: IngredientEntryState? = null, // null = pop-up de ingrediente cerrado
) {
    val totalKcal: Int get() = ingredients.sumOf { it.kcal }
}
data class IngredientEntryState(
    val form: IngredientForm = IngredientForm(),
    val errors: Map<IngredientField, FieldError> = emptyMap(),
)
```
- `ConfirmationState.errors` se va: 07 ya no tiene campos donde mostrarlos. Los errores de nombre, tipo y macros se muestran en el pop-up de carga.
- **`ConfirmationState.mealId` se va** (no el id de la comida: `Meal.id` sigue, porque lo usan `@Delete`, la ruta de 08 y Duplicate). Se va de `ConfirmationState` y de `onOpenConfirmation`, y con él `NEW_MEAL_ID`: sin edición, 07 solo abre comidas nuevas. `onOpenConfirmation(defaultDate: LocalDate, draftName: String, draftIngredients: List<Ingredient> = emptyList())`. También se van `notFound` y su uso en 07 (`FoodConfirmationScreen`); el texto `nutrition_confirm_not_found` **queda**, porque lo usa 08 en `NotFound` (C42).
- **Abrir 07 reemplaza** la `confirmation` anterior, salvo mientras se guarda (C102), venga de `onOpenConfirmation` o de `onContinueManualEntry` (C31). Hace falta porque 07 tiene barra inferior: salir por la barra no llama a `onDiscard`, y sin esto el próximo "Review" mostraría la comida vieja.
- **Los pop-ups viven en el ViewModel**, no en un `remember`: rotar no pierde lo escrito, y la pantalla los dibuja cuando `manualEntry` o `ingredientEntry` no son `null`.
- **Eventos del pop-up de carga** (reemplazan a `onManualEntry()` y a `onFormChange`):
  - `onOpenManualEntry(defaultDate: LocalDate)`: `draft = null` y abre el pop-up vacío con esa fecha.
  - `onManualEntryChange(form: MealForm)`: borra el error de los campos que cambiaron.
  - `onContinueManualEntry(): Boolean`: valida con `validateMealDetails`. Si es válido, cierra el pop-up, abre `confirmation` (reemplazando la anterior) y devuelve `true`. Con `manualEntry == null` (doble toque) devuelve `false` sin tocar nada; la Route navega a 07 solo con `true`, como `FoodSearchViewModel.onConfirm` devuelve `null` si no hay nada que usar.
  - `onDismissManualEntry()`: cierra el pop-up sin abrir 07.
  - Controles de la hoja: "Continue" (`nutrition_manual_continue`) → `onContinueManualEntry()`; "Cancel" (`nutrition_manual_cancel`), deslizarla hacia abajo o el botón atrás → `onDismissManualEntry()`. Título: `nutrition_manual_title`. El pop-up de ingrediente: "Add" (`nutrition_ingredient_add`) → `onConfirmIngredient()`; "Cancel" o tocar afuera → `onDismissIngredientEntry()`; título `nutrition_ingredient_title`.
  - `onPrefill(selection: FoodSelection)`: lo llama la hoja de búsqueda. Antes abría 07; ahora abre el pop-up con `selection.form` y `ingredients = listOf(selection.ingredient)`, y `draft = null`.
- **Eventos de 07:**
  - `onAddIngredientClick()`, `onIngredientEntryChange(form: IngredientForm)`, `onConfirmIngredient()` y `onDismissIngredientEntry()`: el pop-up de ingrediente. Confirmar valida con `validateIngredient` y agrega el ingrediente **al final**.
  - `onRemoveIngredient(index: Int)`: "Remove" de cada tarjeta.
  - Agregar o sacar un ingrediente borra `saveError` (C101): antes lo borraba `onFormChange`, que se va.
  - `onSave()`: los controles van en este orden y se corta en el primero que falla: sin ingredientes (C88), `validateMeal` (C34) e `isValidIngredient` (C62). Si hay ingredientes, valida `c.form.copy(kcal = c.totalKcal.toString())` **sin cambiar `confirmation.form`**, así las kcal guardadas son siempre la suma (C87) y lo que se ve no cambia si falla (C39). Siempre llama a `addMeal`. `validateMeal` y `isValidIngredient` no cambian.
  - −/+ siguen llamando a `onDemo`.
- **07 dibuja siempre tarjetas** con `confirmation.ingredients`; sin ingredientes, solo "+ Add Ingredient", el panel de totales con 0 kcal y los botones. `MealFields` (los campos y los chips de tipo) pasan del cuerpo de 07 al pop-up de carga. El panel de totales muestra `totalKcal` y los macros de `form`.
- **07 sigue con `Column` y `verticalScroll`, sin `LazyColumn`.** Una comida lleva 10 a 20 ingredientes livianos (texto y dos botones, sin imágenes), que se componen sin costo apreciable; la pantalla scrollea entera (UI03). Pasar a `LazyColumn` obligaría a rearmar con `Spacer` los espaciados medidos del Figma: 6 dp entre tarjetas, "+ Add Ingredient" y totales; 20 dp el resto.
- **Los pop-ups no están en el Figma**, como el `AlertDialog` de "Delete Meal" (decisión del 25/10). Usan los componentes del tema (`VoraTextField`, `VoraChip`, `VoraButton`) y textos de `strings.xml`:
  - **el de carga es una hoja inferior** (`ModalBottomSheet`), como la búsqueda: son seis campos y los chips de tipo, y la hoja scrollea y se acomoda al teclado mejor que un diálogo en 360 dp. Desde 01 y 03, búsqueda y carga se abren igual. Reusa lo que resolvió Fiore en `FoodSearchSheetHost`: no es un destino del `NavHost` (el inventario sigue en 37) y se le pasa el fondo del tema (sin eso sale lila, porque el tema no define `surfaceContainerLow`). **No reusa la hoja de búsqueda**: esa busca en el servidor y tiene su ViewModel; la de carga es un formulario;
  - **el de ingrediente es un `AlertDialog`** de Material 3: son tres campos.
- **Quién prepara el ViewModel: las Routes, no el grafo** (cambia la decisión del 25/10, ver Historial). `NutritionNavGraph` queda como los de Workout y Progress: solo navega y no pide ViewModels. Cada Route llama al ViewModel y después al `onX()` que navega, como ya hacen `FoodSearchSheetHost` y `VoiceFoodLoggingRoute`:
  - 01 y 03: "Photo" llama a `onPhotoCaptureStarted()` y después a `onPhoto()`.
  - 01 y 03 (búsqueda), 03 ("Log Manually") y 04 ("Type instead"): abren el pop-up de carga **sobre la misma pantalla**, sin navegar. "Continue" navega a 07 solo si `onContinueManualEntry()` devuelve `true`. El pop-up lo dibuja un `ManualEntrySheetHost(grafo, onContinued)` compartido, como `FoodSearchSheetHost`.
  - 05 ("listo") y 06 ("Review"): llaman a `onOpenConfirmation(fecha, nombre, ingredientes)` con los nombres resueltos con `getString` en la Route, y después navegan. Lo que hoy hace `rememberAbrirConfirmacion` pasa a un helper de `ui/screens/nutrition/`.
  - Al volver de 07 sin guardar (‹ o atrás) se llama a `onDiscard()` y se vuelve a la pantalla de abajo **sin** el pop-up: lo escrito en él se pierde, como hoy lo escrito en 07.

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

**Borrado con `@Delete` (refactor, entre el PR 1 y el 2 de ingredientes):** `MealDao` (`delete` en lugar de `deleteById`), `MealRepository` y `MealRepositoryImpl` (`deleteMeal(meal)`) y `MealDetailViewModel.onConfirmDelete`. En los tests: `MealDaoTest` (C11, C12, que hoy borra con `deleteById`, y C72), el `FakeMealDao` de `MealRepositoryImplTest` (C14 y C73), `FakeMealRepository` y `MealDetailViewModelTest` (C45 y C74).
  - `FakeMealRepository.deleteMeal(meal)` **sigue anotando el id** en `eliminadas: List<Long>` y suma `comidasEliminadas: List<Meal>`, que usa solo C45. Así C43, C46, C47 y C48, que comprueban `eliminadas` con `listOf(7L)`, no se tocan.
  - KDoc de `MealRepository.deleteMeal`: no avisa si la comida ya no estaba (C73). El de `MealDao.delete` dice que devuelve las filas borradas y que busca por la clave primaria (C72).
  - Lo que comprueban C11, C12, C14 y C45 no cambia: cambia solo qué se le pasa al DAO y al repositorio.

**PR 2 de ingredientes (2026-10-27):**
- Dominio: `domain/MealValidation.kt` (`IngredientForm`, `validateIngredient`, `validateMealDetails`).
- 07 y pop-ups: `FoodCaptureViewModel`, `FoodCaptureUiState` (su KDoc de "null = carga manual" cambia, y se van `NEW_MEAL_ID` y su KDoc del "argumento de la ruta"), `FoodConfirmationRoute` y `FoodConfirmationScreen` (sin `MealFields`, Add y Remove reales, totales con `totalKcal`), y nuevos `ManualEntrySheet` / `ManualEntrySheetHost` e `IngredientEntryDialog` en `ui/screens/nutrition/`.
- Routes que pasan a preparar el ViewModel: `NutritionDashboardRoute`, `AddFoodRoute`, `VoiceFoodLoggingRoute`, `FoodAnalysisRoute` y `PhotoAnalysisRoute`. El helper que reemplaza a `rememberAbrirConfirmacion`, en `ui/screens/nutrition/`.
- Sin edición ("Edición: fuera de alcance"): `MealDetailScreen` y `MealDetailRoute` ("Edit Meal" pasa a demostración), `MealRepository`, `MealRepositoryImpl`, `MealDao`, `MealDataException` y `Rutas.kt`; en los tests, `MealDaoTest`, `MealRepositoryImplTest` y `FakeMealRepository`.
- `res/values/strings.xml` y `res/values-es/strings.xml`: títulos y botones de los dos pop-ups, `nutrition_error_no_ingredients`, y los rangos de gramos y kcal del ingrediente.
- **Fiore** (se toca y se le avisa una sola vez, en el PR):
  - `ui/navigation/NutritionNavGraph.kt`: se van `abrirFoto`, `abrirCargaManual`, `AbrirConfirmacion`, `rememberAbrirConfirmacion` y los `hiltViewModel`. Queda solo navegación (C95).
  - Búsqueda (`specs/funcionales/busqueda-de-alimentos.md`, responsable Fiore): `FoodSearchViewModel.onConfirm` devuelve `FoodSelection`, y `FoodSearchSheetHost` llama al nuevo `onPrefill(selection)`. Cambian sus C29, C31 y C33 (anotado en esa spec).
  - `ui/navigation/Rutas.kt`: `FOOD_CONFIRMATION` pierde el argumento `mealId` y `foodConfirmation(mealId)` queda sin parámetro.
  - `specs/PANTALLAS.md`: 03 y 04 abren un pop-up de carga y la búsqueda abre ese pop-up, no 07; "Edit Meal" de 08 pasa a demostración; la ruta de 07 sin `mealId` (líneas 283, 286, 296 y 587, y la nota 3 de la §17).
  - `README.md` (inventario de NAV01) y `ui/screens/nutrition/LEEME.md` (filas de 07 y 08, que dicen "Edit Meal abre 07 con el id").
- **Tests que cambian porque cambia el contrato** (los reescribe la sesión de tests en rojo; no es "cambiar un test para que pase", porque cambia el criterio, y lo decidió Sofi):
  - `FoodConfirmationViewModelTest`:
    - **todos** los que llaman a `onOpenConfirmation(nueva, …)` o `(7, …)`: la firma pierde `mealId`;
    - C29, C34, C35 y C63 llaman directo a `onManualEntry` / `onFormChange`: se reescriben con su criterio nuevo (C35 pasa a `FoodCaptureManualEntryTest`). C63 además pierde sus dos `onSave` y la mitad que usa `onPrefill(MealForm)`: hoy guarda dos comidas sin ingredientes, que C88 no deja;
    - C31 y C32: C31 se reescribe entero (reemplazar, no conservar; ver C31); C32 cambia cómo se abre 07;
    - C36 entra por la foto sin `draftIngredients`: con C88 ya no guardaría. Se le pasan los tres ingredientes del borrador, y las kcal esperadas siguen siendo 520 porque son su suma;
    - C38 y C39 usan el helper `abrirManualConFormValido` (`onManualEntry` + `onFormChange`): se rearma con el pop-up y un ingrediente. Lo que comprueban no cambia;
    - C62: se borra su segunda mitad (la de edición, `repoEdicion`), y su texto deja de nombrar `updateMeal`.
  - `FakeMealRepository` (home) pierde `updateMeal` y `actualizadas`: se sacan sus usos en C34, C61 y C62.
  - `FoodCapturePrefillTest` (C31 de la búsqueda): el primer test (`C31 onPrefill limpia el borrador del analisis`) pasa a `onPrefill(selection)`; el segundo (`C31 onPrefill deja la confirmacion con el formulario de la busqueda`) **se borra**: pide `confirmation` no nula y lo contradice C82, que lo reemplaza en `FoodCaptureManualEntryTest`.
  - `FoodSearchViewModelTest`: C29, C30 y C32 de la búsqueda leen `onConfirm(...)?.form` (C35 y C18 descartan o comparan con `null` y compilan igual).
  - `FoodSearchGuardadoRoomTest` (C33 de la búsqueda, `androidTest/`): el camino pasa por el pop-up (elegir el tipo y `onContinueManualEntry`); el alimento ya llega como ingrediente, no hay que agregarlo. Su KDoc dice que "248.0" no deja guardar: deja de valer, porque las kcal ahora son la suma.
  - **Se borran** (decisión de Sofi: la edición sale del alcance): C10 y C17 con sus tests, C30, C33, C37 y C64 de `FoodConfirmationViewModelTest`, y la parte de `update` de C14. `FakeMealDao` pierde `update`.

**Home (Martu):** `HomeViewModel` suma un `catch` (C44). Es un cambio chico y se avisa en el PR.

**Tests:**
- `FakeMealRepository` (home) suma los métodos nuevos.
- `FakeMealDao`, privado de `MealRepositoryImplTest`, suma `observeById`, `update` y `delete` (desde el refactor del 27/10; antes `deleteById`), registra lo que recibe y puede tirar excepciones. Con `delete` anota las Entity que recibe y devuelve las filas que el test le prepara (C73 usa 0).
- Los tests de `FoodCaptureViewModel` pasan a crearlo con un repositorio falso.

Ninguno cambia lo que comprueba. *(Vale para VC-l y el PR 1; los cambios del PR 2 están en su lista.)*

**Fiore:** `specs/PANTALLAS.md` línea 256 (dice que 03 carga, que 02 elimina y que el total sale de un `SUM`) y punto 4 de la §17 (eliminar no tiene control; además nombra `MealDao.deleteById`, que con el refactor del 27/10 pasa a `MealDao.delete`). Se le avisa en el PR para que lo actualice: carga en 07, elimina en 08 y el total se suma en el ViewModel, como Home (decisión 007).

No se tocan `MainActivity` ni `RUTAS_*`. `Rutas.kt` lo toca solo el PR 2 (zona de Fiore, se le avisa): `FOOD_CONFIRMATION` pierde el argumento `mealId`, porque 07 ya no abre comidas guardadas.

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
- ~~C10~~. *Borrado en el PR 2 (sin edición, no hay `@Update`).* Decía: CUANDO se actualiza una comida guardada, `observeByDate` DEBE emitir la lista con los valores nuevos y el mismo `id`.
- C11. CUANDO se elimina una comida guardada con `delete`, `observeByDate` DEBE emitir la lista sin ella y `delete` DEBE devolver 1; con una Entity cuyo `id` no existe, DEBE devolver 0 y no cambiar la tabla.
- C12. `observeById` DEBE emitir la comida guardada y, después de eliminarla, `null`.
- C13. `observeMeal` DEBE devolver `Meal` (no Entity) con el contenido de la Entity del DAO, y `null` cuando el DAO emite `null`.
- C14. CUANDO se elimina por el repositorio, `delete` del DAO DEBE recibir la Entity de esa comida, con el mismo `id`. *(PR 2: sale la parte de `update`.)*
- C15. SI Room falla al escribir (por ejemplo, `addMeal` de una comida con un `id` que ya existe, que con ABORT tira `SQLiteConstraintException`), ENTONCES el repositorio DEBE lanzar `MealDataException(STORAGE)`. Va en `androidTest/` con Room en memoria, porque `SQLiteException` no se puede crear en un test JVM sin Robolectric.
- C16. SI una Entity leída tiene un tipo o una fecha inválidos, ENTONCES el Flow de `observeMealsByDate` y el de `observeMeal` DEBEN fallar con `MealDataException(INVALID_DATA)`.
- ~~C17~~. *Borrado en el PR 2 (sin `updateMeal`).* Decía: SI `update` devuelve 0 (la comida se borró mientras se editaba), ENTONCES `updateMeal` DEBE lanzar `MealDataException(NOT_FOUND)`.
- C72. CUANDO se llama a `delete` con una Entity que tiene el `id` de una comida guardada pero otros valores (nombre, kcal, ingredientes), Room DEBE borrar esa fila igual: busca solo por la clave primaria.
- C73. SI `delete` devuelve 0, ENTONCES `deleteMeal` NO DEBE lanzar una excepción.

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
- C28. CUANDO se abre 07 *(PR 2: ya sin `mealId`)* después de una captura de voz o foto, `confirmation.form` DEBE venir con `draftName`, el tipo del borrador (almuerzo), la `defaultDate` y las kcal y los macros del borrador como texto.
- C29. CUANDO se llama a `onOpenManualEntry(d)`, `manualEntry.form` DEBE venir vacío, sin tipo y con la fecha `d`, sin errores ni ingredientes, y `draft` DEBE ser `null`, aunque antes haya habido una captura de foto. *(PR 2: antes era `onManualEntry()` seguido de `onOpenConfirmation`.)*
- ~~C30~~. *Borrado en el PR 2 (sin edición).* Decía: CUANDO se abre 07 con un id guardado, el formulario DEBE venir con los valores de esa comida.
- C31. CUANDO se llama a `onOpenConfirmation` o a `onContinueManualEntry()` (válido) con una `confirmation` ya abierta (se salió de 07 por la barra inferior sin descartar), la nueva DEBE reemplazar a la anterior: los ingredientes y el formulario DEBEN ser los de la nueva apertura, no los que se habían agregado o sacado. *(PR 2: antes conservaba lo escrito al reabrir con el mismo id; esa regla servía para la edición y, sin ella, dejaba una comida vieja en 07.)*
- C32. CUANDO se llama a `onDiscard()`, `confirmation` DEBE ser `null`; una nueva apertura arranca de cero.
- ~~C33~~. *Borrado en el PR 2 (07 ya no abre por id; `notFound` se va).* Decía: SI se abre 07 con un id que no existe, ENTONCES `confirmation.notFound` DEBE ser `true`.
- C34. SI al guardar `validateMeal` da `Invalid`, ENTONCES no DEBE escribirse en el repositorio, `saveError` DEBE ser `R.string.nutrition_error_save` y no DEBE emitirse `Saved`. Desde la pantalla no se llega (el pop-up ya validó y las kcal son una suma); el test llega con `onPhotoCaptureStarted()` y `onOpenConfirmation(fecha, "", ingredientes)`, que deja el nombre vacío. *(PR 2: antes los errores iban a `confirmation.errors`, que se va.)*
- C35. CUANDO se llama a `onManualEntryChange` después de un "Continue" inválido, DEBE desaparecer el error de los campos que cambiaron, y los demás DEBEN quedar. *(PR 2: antes era `onFormChange` en 07.)*
- C36. CUANDO se guarda una comida nueva válida, DEBE llamarse una vez a `addMeal` con los valores validados (las kcal, la suma de los ingredientes: C87) y emitirse `Saved` una sola vez. Después, `draft` y `confirmation` DEBEN ser `null`.
- ~~C37~~. *Borrado en el PR 2 (sin edición).* Decía: CUANDO se guarda una edición válida, DEBE llamarse a `updateMeal` con el mismo `id` (no a `addMeal`) y emitirse `Saved`.
- C38. MIENTRAS `isSaving` es `true`, un segundo `onSave()` no DEBE escribir otra vez ni emitir otro `Saved`.
- C39. SI guardar falla con `MealDataException`, ENTONCES el formulario DEBE conservar lo escrito, `saveError` DEBE tener un texto de `strings.xml`, `isSaving` DEBE volver a `false` y no DEBE emitirse `Saved`.
- C40. CUANDO el análisis de voz emite `AnalysisDone`, el borrador DEBE pasar a ser `PhotoMealDraft`. El borrador inicial sigue siendo `null`, así voz y foto llegan a 07 con la misma comida estimada.

### Detalle y eliminación en 08 (RF04, RF07)
- C41. Al crearse, `MealDetailViewModel` DEBE pedir la comida del `mealId` de la ruta y pasar de `Loading` a `Content`.
- C42. SI la comida no existe, ENTONCES el estado DEBE ser `NotFound`. SI `observeMeal` falla con `MealDataException`, ENTONCES DEBE ser `Error` con un texto de `strings.xml`.
- C43. CUANDO se toca "Delete Meal", `confirmingDelete` DEBE ser `true`. CUANDO se cancela, DEBE volver a `false` y no DEBE eliminarse nada.
- C44. SI el Flow de comidas de `HomeViewModel` falla con `MealDataException`, ENTONCES Home DEBE mostrar 0 kcal y 0 g, sin cerrarse la app.
- C45. CUANDO se confirma, DEBE llamarse una vez a `deleteMeal` con la comida que muestra `Content` y emitirse `Deleted` una sola vez.
- C74. SI `uiState` no es `Content` (`Loading`, `NotFound` o `Error`), ENTONCES `onConfirmDelete()` NO DEBE llamar a `deleteMeal` ni emitir `Deleted`. Antes borraba con el `mealId` de la ruta; la pantalla solo muestra "Delete Meal" con `Content`, así que no cambia nada para quien la usa.
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
- C62. SI al guardar algún ingrediente no pasa `isValidIngredient`, ENTONCES no DEBE llamarse a `addMeal`, `saveError` DEBE tener un texto de `strings.xml`, `isSaving` DEBE quedar en `false` y no DEBE emitirse `Saved`.
- C63. CUANDO se llama a `onOpenConfirmation(d, nombre, ingredientes)` con `draft = null`, `confirmation.ingredients` DEBE estar vacía aunque se pasen `draftIngredients`. *(PR 2: la carga manual y la búsqueda ya no pasan por acá, y una comida sin ingredientes no se guarda, C88.)*
- ~~C64~~. *Borrado en el PR 2 (sin edición).* Decía: CUANDO se abre 07 con un id guardado, `confirmation.ingredients` DEBE traer los de esa comida, y guardar la edición DEBE llamar a `updateMeal` con esos mismos ingredientes aunque haya cambiado el formulario.
- C65. CUANDO se duplica una comida con ingredientes, `addMeal` DEBE recibir los mismos ingredientes.
- C66. Cada `MealRow` de `Content` DEBE traer los ingredientes de su comida, en el mismo orden.
- C71. SI al duplicar algún ingrediente no pasa `isValidIngredient`, ENTONCES no DEBE llamarse a `addMeal`, `actionError` DEBE tener un texto de `strings.xml`, `isBusy` DEBE volver a `false` y no DEBE emitirse `Duplicated`.

### Carga manual, búsqueda y kcal como suma (PR 2 de ingredientes; RF03, RF04, RF07, EXT02)

Reglas del ingrediente a mano (`validateIngredient`). El texto se evalúa sin espacios en los bordes; primero el formato (`^-?\d+$` y entrar en un `Int`, como las kcal de C4; si no, `NOT_A_WHOLE_NUMBER`) y después el rango (si no, `NEGATIVE`, con el texto `nutrition_ingredient_grams_range` "Entre 1 y 10000 g" o `nutrition_ingredient_kcal_range` "Entre 0 y 10000 kcal"). Gramos y kcal son enteros porque `Ingredient` los guarda como `Int`. Los topes, 10000 g como en la búsqueda y 10000 kcal, frenan errores de tipeo y evitan que la suma se pase de un `Int`.

| Texto | gramos | kcal |
|---|---|---|
| `""` o `"  "` | REQUIRED | REQUIRED |
| `"150"`, `" 150 "` | válido (150) | válido (150) |
| `"0"`, `"-0"` | NEGATIVE | válido (0) |
| `"0150"` | válido (150) | válido (150) |
| `"1"` | válido | válido |
| `"10000"` | válido | válido |
| `"10001"`, `"-5"` | NEGATIVE | NEGATIVE |
| `"150.5"`, `"150,5"`, `"abc"`, `"+5"`, `"1e3"`, `"3000000000"` | NOT_A_WHOLE_NUMBER | NOT_A_WHOLE_NUMBER |

- C75. `validateIngredient` DEBE dar para gramos y kcal el resultado de cada fila de la tabla, y `NAME → REQUIRED` con el nombre vacío o con solo espacios.
- C76. CUANDO el formulario es válido, `Valid` DEBE traer un `Ingredient` con el nombre sin espacios en los bordes y los gramos y las kcal como números. SI hay varios campos inválidos, ENTONCES `Invalid` DEBE traer el error de cada uno.
- C77. `validateMealDetails` DEBE dar, para nombre, tipo, proteína, carbohidratos y grasa, los mismos errores que `validateMeal` en los casos de C1, C3, C5 y C8, y NUNCA un error de `KCAL`, aunque `form.kcal` sea `""` o `"abc"`.
- C78. SI se llama a `onContinueManualEntry()` con datos inválidos, ENTONCES DEBE devolver `false`, `manualEntry.errors` DEBE traer el error de cada campo, `manualEntry.form` DEBE conservar lo escrito y `confirmation` DEBE seguir en `null`. SI `manualEntry` ya es `null` (segundo toque en "Continue"), ENTONCES DEBE devolver `false` sin cambiar `confirmation`.
- C79. CUANDO se llama a `onContinueManualEntry()` con datos válidos, DEBE devolver `true`, `manualEntry` DEBE pasar a `null` y `confirmation` DEBE abrirse con el `form` del pop-up y los `ingredients` del pop-up (vacía si fue a mano).
- C80. CUANDO se llama a `onDismissManualEntry()`, `manualEntry` DEBE ser `null`, `confirmation` DEBE seguir en `null` y no DEBE escribirse en el repositorio.
- C81. CUANDO se confirma una porción en la búsqueda, `onConfirm(date)` DEBE devolver una `FoodSelection` cuyo `ingredient` tiene el nombre del alimento, las kcal de la porción (las mismas de `form.kcal`) y los gramos redondeados con `roundToInt()` y nunca menos de 1: `"150"` da 150, `"150,5"` da 151 y `"0,4"` da 1.
- C82. CUANDO se llama a `onPrefill(selection)`, `manualEntry` DEBE abrirse con `selection.form` e `ingredients = listOf(selection.ingredient)`, `draft` DEBE ser `null` y `confirmation` DEBE seguir en `null`.
- C83. CUANDO después de `onPrefill(selection)` se elige el tipo y se llama a `onContinueManualEntry()`, `confirmation.ingredients` DEBE ser `listOf(selection.ingredient)`.
- C84. CUANDO se llama a `onAddIngredientClick()`, `confirmation.ingredientEntry` DEBE abrirse vacío. CUANDO se llama a `onDismissIngredientEntry()`, DEBE pasar a `null` sin cambiar `confirmation.ingredients`.
- C85. CUANDO se llama a `onConfirmIngredient()` con un ingrediente válido, DEBE agregarse **al final** de `confirmation.ingredients` y `ingredientEntry` DEBE pasar a `null`. SI es inválido, ENTONCES la lista NO DEBE cambiar, `ingredientEntry.errors` DEBE traer el error de cada campo y `ingredientEntry.form` DEBE conservar lo escrito.
- C86. CUANDO se llama a `onIngredientEntryChange` después de un intento inválido, DEBE desaparecer el error de los campos que cambiaron, y los demás DEBEN quedar.
- C87. `confirmation.totalKcal` DEBE ser la suma de las kcal de `confirmation.ingredients` y cambiar al agregar o sacar uno. CUANDO se guarda, `addMeal` DEBE recibir esa suma como kcal, sin importar `form.kcal`: con `form.kcal = "999"` e ingredientes de 280, 190 y 50 kcal, la comida se guarda con 520.
- C88. SI se llama a `onSave()` sin ingredientes, ENTONCES no DEBE llamarse a `addMeal`, `saveError` DEBE ser `R.string.nutrition_error_no_ingredients`, `isSaving` DEBE quedar en `false` y no DEBE emitirse `Saved`.
- C89. Una comida con un solo ingrediente de 0 kcal DEBE guardarse, con 0 kcal (RF03 acepta el cero).
- C90. CUANDO se llama a `onRemoveIngredient(i)`, DEBE sacarse el ingrediente de la posición `i` y los demás DEBEN quedar en el mismo orden; también con los que vinieron de la foto o la voz. Con `i` fuera de la lista (`-1` o el tamaño), la lista NO DEBE cambiar.
- C102. SI la `confirmation` abierta tiene `isSaving = true` (se salió de 07 por la barra mientras guardaba), ENTONCES `onOpenConfirmation` NO DEBE reemplazarla y `onContinueManualEntry()` DEBE devolver `false` sin cerrar el pop-up. Si no, el guardado viejo, al terminar, borraría la confirmación nueva y emitiría `Saved`.
- C101. CUANDO `onConfirmIngredient()` agrega un ingrediente o `onRemoveIngredient(i)` saca uno, `confirmation.saveError` DEBE pasar a `null` (el "sin ingredientes" de C88 o el error de C39 no quedan en pantalla).
- C91. MIENTRAS `isSaving` es `true`, `onAddIngredientClick()`, `onConfirmIngredient()` y `onRemoveIngredient(i)` NO DEBEN cambiar `confirmation.ingredients` ni abrir el pop-up de ingrediente.

### Edición: fuera de alcance (PR 2, decisión de Sofi, 27/10)
La edición se saca aunque ya estaba implementada (VC-l y PR 8 de VC-m). Con los ingredientes dejaba de servir: abría 07 con los campos del formulario, que 07 ya no tiene, y para que anduviera había que rehacerla (abrir el pop-up precargado, llevar a 07 los ingredientes guardados, recalcular las kcal y resolver qué pasa con las comidas guardadas sin ingredientes, que no se podrían volver a guardar por C88). Eran unos 8 criterios más y unos 5 archivos con lógica nueva. Tan cerca de la entrega, y como la consigna no la pide (RF04: guardar, listar por fecha y eliminar con confirmación), queda fuera de alcance.

- "Edit Meal" **queda en 08 como botón de demostración**: está en el frame del Figma (lo que no está es "Delete Meal") y sacarlo alejaría 08 del diseño (UI01). Muestra el mismo aviso de demostración que −/+ en 07 (`rememberDemoToast()`).
- Se va toda la lógica, así no queda código sin uso: `onEdit` de `MealDetailRoute`, `MealDetailScreen` y `NutritionNavGraph`; el argumento `mealId` de la ruta de 07 (`Rutas.FOOD_CONFIRMATION` y `foodConfirmation(mealId)`); la carga por id de `onOpenConfirmation` y `ConfirmationState.notFound` con su uso en `FoodConfirmationScreen` (el texto `nutrition_confirm_not_found` **queda**: lo usa 08 en `NotFound`, C42); la rama `updateMeal` de `onSave`; `MealRepository.updateMeal`, `MealDao.update` (`@Update`) y `MealError.NOT_FOUND`, que solo usaba `updateMeal`; `Meal.toForm()` y el parámetro `mealId` de `updateConfirmation` en `FoodCaptureViewModel`; en `FoodConfirmationScreen`, las ramas del rótulo del botón que dicen "Save Meal" (al editar o sin tipo) y el `mealId` de sus previews. El botón de 07 dice siempre "Add to [tipo]", porque el tipo siempre está elegido (el pop-up lo pide y la foto y la voz traen almuerzo); el texto `nutrition_confirm_save_meal` se va.
- Criterios que se borran: C10, C17, C30, C33, C37, C64 y la parte de `update` de C14.
- Avisos a Fiore: `Rutas.kt` y `specs/PANTALLAS.md` (la fila de "Edit Meal" de 08 pasa a "demostración").
- Si el proyecto siguiera, la forma de volver a editar está en el Historial del 27/10 (pop-up precargado y 07 con los ingredientes guardados).

### A mano (no tienen test automático)
- C51. CUANDO se guarda una comida y se cierra la app (`adb shell am force-stop`), al reabrirla la comida DEBE seguir en 02 y en el total (RF04).
- C52. El ícono de calendario de 01 DEBE abrir un `DatePickerDialog` de Material 3 con la fecha elegida, y elegir otra DEBE cambiar lo que listan 01 y 02.
- C67. CUANDO se guarda una comida desde la foto o la voz, 01 DEBE mostrar como bajada sus ingredientes separados por `" · "` en hasta dos líneas, cortada con `…` si no entra en 360 dp, y una comida sin ingredientes DEBE seguir mostrando su nombre. *(PR 2: decía "manual"; desde el PR 2 solo quedan sin ingredientes las guardadas antes. Lo mismo en C68 y C69.)*
- C68. En 02, una comida con ingredientes DEBE verse como una línea por ingrediente (nombre, gramos y kcal), y tocar cualquiera de esas líneas DEBE abrir la 08 de esa comida. Una sin ingredientes DEBE verse como dice "Qué muestran 01 y 02 sin ingredientes".
- C69. En 08, una comida con ingredientes DEBE mostrar una tarjeta por ingrediente con nombre, gramos y kcal. Una sin ingredientes DEBE mostrar una sola tarjeta con su nombre y la proteína en la línea gris.
- C70. CUANDO se instala la versión con `version = 5` sobre una base vieja, la app DEBE abrir sin cerrarse. Room borra una vez todas las tablas y las vuelve a crear vacías, así que se pierden los registros de comidas, caché de entrenamientos e historial del coach. Lo que se cargue después DEBE seguir al cerrar y reabrir (C51).

**PR 2 de ingredientes** (recorrido en el emulador, 411 y 360 dp):
- C92. CUANDO se toca "Log Manually" en 03, DEBE abrirse el pop-up de carga **encima** de 03. "Continue" con el nombre vacío o sin tipo DEBE mostrar el error en el pop-up sin cerrarlo. Con datos válidos, DEBE abrirse 07 sin tarjetas, con la fecha elegida en 01; después de agregar dos ingredientes y guardar, 01 DEBE mostrar la comida en esa fecha con la suma de sus kcal y sus ingredientes como bajada, y el total del día DEBE subir en esa suma (RF04).
- C93. CUANDO se toca "Type instead" en 04, DEBE abrirse el mismo pop-up encima de 04. "Cancel" DEBE cerrarlo y dejar 04; "Continue" DEBE llevar a 07.
- C94. CUANDO se toca "Usar este alimento" en la búsqueda, abierta desde 01 o desde 03, la hoja DEBE cerrarse y el pop-up DEBE abrirse sobre la misma pantalla, con el nombre y los macros de la porción y sin tipo. "Continue" DEBE llevar a 07 con una tarjeta: el alimento, sus gramos y sus kcal.
- C95. `NutritionNavGraph.kt` NO DEBE pedir ViewModels (`grep -nE "hiltViewModel|import .*ViewModel" NutritionNavGraph.kt` sin resultados; un comentario que diga "ViewModel" no cuenta). Los caminos no cambian: foto y voz siguen llegando a 07 con sus tres ingredientes, y atrás desde 07 sin guardar vuelve a la pantalla de abajo sin el pop-up y sin guardar nada.
- C96. En 02, las kcal de cada ingrediente DEBEN verse en 12 SemiBold (`VoraTheme.text.fieldLabel`) y las del encabezado de la comida en 14 Bold (`labelLarge` con `FontWeight.Bold`), como el frame (medido por Sofi en el Figma, 27/10). El código ya los usa: se comprueba en la captura, sin cambio de código.
- C97. CUANDO se rota con el pop-up de carga o el de ingrediente abiertos, lo escrito y los errores DEBEN seguir ahí.
- C98. Con 20 ingredientes en 07, la pantalla DEBE scrollear hasta los botones de guardar sin cortar nada, en 360 dp (UI03).
- C100. CUANDO se toca "Edit Meal" en 08, DEBE mostrarse el aviso de demostración y NO DEBE navegar ni cambiar la comida.
- C99. Las capturas de esta tabla DEBEN rehacerse y guardarse en `docs/evidencia/visual/`. Las de pantallas del Figma se comparan con su frame (UI01); las de los pop-ups, que no están en el Figma, DEBEN usar `VoraTextField`, `VoraChip` y `VoraButton`, sin textos cortados ni controles encimados en 360 dp. Cambian por el PR 2; nada de esto es opcional:

| Pantalla | Captura | Qué cambia | Archivo (411 y 360 dp) |
|---|---|---|---|
| 01 | Comida cargada a mano, con ingredientes | La bajada de una comida manual pasa a ser sus ingredientes; la versión sin ingredientes casi no se va a ver | `nutrition-01-ingredientes-*` (rehacer) |
| 01 | Pop-up de carga precargado desde la búsqueda | Nueva | `nutrition-01-popup-busqueda-*` |
| 02 | Comidas con ingredientes, manual y de la búsqueda | Las comidas manuales y de la búsqueda tienen ingredientes, y cambia el estilo de las kcal (C96) | `nutrition-02-ingredientes-*` (rehacer) |
| 03 | Pop-up de carga vacío y con errores | Nueva: "Log Manually" ya no navega | `nutrition-03-popup-*`, `nutrition-03-popup-errores-*` |
| 04 | Pop-up de carga sobre 04 | Nueva: "Type instead" ya no navega | `nutrition-04-popup-*` |
| 07 | Sin ingredientes, después del pop-up | Nueva; reemplaza a la de formulario | `nutrition-07-sin-ingredientes-*` |
| 07 | Con el pop-up de ingrediente abierto y con errores | Nueva | `nutrition-07-popup-ingrediente-*` |
| 07 | Con ingredientes y totales (foto) | "+ Add Ingredient" y "Remove" ya funcionan; el total es la suma | `nutrition-07-ingredientes-*` (rehacer) |
| 07 | Error "sin ingredientes" al guardar | Nueva | `nutrition-07-error-sin-ingredientes-*` |
| 07 | 20 ingredientes, con scroll (C98) | Nueva | `nutrition-07-muchos-ingredientes-360dp-scroll` |
| 08 | Aviso de demostración de "Edit Meal" | La 08 no cambia (el botón queda); solo es nueva la del aviso (C100) | `nutrition-08-edit-demo-*` |
| Búsqueda | La hoja | No cambia: lo que cambia es lo que abre al confirmar (fila de 01) | — |

Plantillas: **La app DEBE** (siempre) · **CUANDO** (ante un evento) · **MIENTRAS** (durante un estado) · **SI ... ENTONCES** (algo sale mal) · **DONDE** (opcional, para ampliaciones).

Casos de la plantilla que no aplican: sin conexión, 404, 503 y JSON inválido (todo es local).

## Fuera de alcance
- Una tabla aparte de ingredientes: la lista vive en la fila de la comida (ver Datos).
- ~~Va en el PR 2:~~ cargar ingredientes a mano, guardar como ingrediente el alimento de la búsqueda y calcular las kcal como suma: **hechos en el PR 2** (C75 a C102). La edición queda fuera de alcance (ver "Edición: fuera de alcance").
- −/+ de 07: siguen de demostración (decisión de Sofi, PR 2). RF04 no los necesita: el total del día (kcal y macros, C22) cambia al agregar o eliminar comidas, no al cambiar gramos dentro de 07.
- Macros por ingrediente: `Ingredient` sigue con nombre, gramos y kcal; los macros se tipean para toda la comida (PR 2). Por eso, desde la búsqueda, los macros son los de un solo alimento: si en 07 se agregan más ingredientes, las kcal suman pero los macros no se recalculan ni se editan en 07.
- Elegir la fecha en el pop-up de carga: es la elegida en 01 (decisión de Sofi, PR 2).
- Recuperar lo escrito en los pop-ups si Android mata el proceso: viven en el ViewModel, que sobrevive a la rotación (C97), no a la muerte del proceso.
- Traducir los nombres ya guardados si se cambia el idioma de la app.
- Corregir una comida guardada con un ingrediente inválido (por ejemplo, gramos en 0 leídos de un JSON viejo): la validación también frena Duplicate, así que solo se puede eliminar. No se da por la app, porque todo lo que se escribe pasa por `isValidIngredient`.
- Sugerencias del PR 1 que no entran (PR 2, decisión de Sofi):
  - Validar el Duplicate después del control de "ocupado o borrada": no hace falta, porque con la app no se puede guardar un ingrediente inválido.
  - Declarar Gson en `libs.versions.toml`: hoy llega con `retrofit-converter-gson` y no cambia lo que hace la app; toca el build y la red, que no son de este PR.
  - Regla `-keep` para `Ingredient`: R8 está activo en release solo para `androidx`, `kotlin` y `kotlinx` (`app/build.gradle.kts`, `optimization`), así que no renombra los campos de `Ingredient`. Hace falta solo si se amplía a todo el código.
  - Interfaces de los repositorios en `domain/`: hoy están en `data/repository/`, separadas de su implementación con `@Binds`, como en la clase. Moverlas es un cambio de toda la app, no de Nutrition.
- "Retake Photo".
- Eliminar desde 02 o con deslizar: se elimina solo desde 08.
- Maquetar 07 y 08: son los PR 7 y 8 de VC-m. En VC-l solo se tocan para que compile.
- Cambiar `MainActivity` o `RUTAS_*`. (`Rutas.kt` lo toca el PR 2: ver "Archivos que se tocan".)
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
| C10 | `C10_update_cambia_los_valores_y_conserva_el_id` | `androidTest/` · `MealDaoTest` | no: pasa desde el principio porque Room genera el DAO con las anotaciones; control negativo el 2026-10-24 (docs/evidencia/tests/2026-10-24-vc-l-verde.md); **borrado** el 2026-10-27 (PR 2, sin edición) |
| C11 | `C11_delete_saca_la_comida_y_devuelve_las_filas_borradas` (antes `C11_deleteById_…`) | `androidTest/` · `MealDaoTest` | no: pasa desde el principio porque Room genera `@Delete`. Control negativo del refactor, 2026-10-27: `@Update` en lugar de `@Delete` (misma firma, no borra) y fallan C11, C12 y C72 |
| C12 | `C12_observeById_emite_la_comida_y_null_al_borrarla` | `androidTest/` · `MealDaoTest` | no: pasa desde el principio porque Room genera el DAO con las anotaciones; control negativo el 2026-10-24 (docs/evidencia/tests/2026-10-24-vc-l-verde.md) |
| C13 | `` `C13 observeMeal devuelve Meal y null cuando no existe` `` | `test/` · `MealRepositoryImplTest` | sí, 2026-10-24 |
| C14 | `` `C14 deleteMeal llega al DAO con la Entity de la comida` `` (antes `C14 updateMeal y deleteMeal llegan…`) | `test/` · `MealRepositoryImplTest` | sí, 2026-10-24; de nuevo el 2026-10-27 con `deleteMeal(meal)`: `delete` no recibe la Entity; en el PR 2 se queda solo con `deleteMeal`: hecho el 2026-10-27, sigue en verde (la parte que queda ya estaba implementada) |
| C72 | `C72_delete_borra_por_id_aunque_cambien_los_otros_valores` | `androidTest/` · `MealDaoTest` | no: pasa desde el principio, como C11. Control negativo: el mismo de C11, falla |
| C73 | `` `C73 deleteMeal no falla si no habia nada que borrar` `` | `test/` · `MealRepositoryImplTest` | sí, 2026-10-27: con la firma provisoria `deleteMeal` llama a `deleteById` y `delete` no recibe la Entity |
| C15 | `C15_un_id_repetido_es_MealDataException_STORAGE` | `androidTest/` · `MealRepositoryRoomTest` | sí, 2026-10-24 (tiró `SQLiteConstraintException` sin traducir) |
| C16 | `` `C16 una Entity invalida es MealDataException INVALID_DATA` `` | `test/` · `MealRepositoryImplTest` | sí, 2026-10-24 |
| C17 | `` `C17 update de una comida borrada es NOT_FOUND` `` | `test/` · `MealRepositoryImplTest` | sí, 2026-10-24; **borrado** el 2026-10-27 (PR 2) |
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
| C29 | `` `C29 abrir la carga manual la deja vacia y sin borrador` `` (antes `C29 la carga manual viene vacia…`) | `test/` · `FoodCaptureManualEntryTest` | sí, 2026-10-27 (PR 2: el pop-up no se abre); el viejo vio el rojo el 2026-10-24 |
| C30 | `` `C30 con id el formulario trae la comida guardada` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24; **borrado** el 2026-10-27 (PR 2) |
| C31 | `` `C31 abrir 07 reemplaza la confirmacion anterior` `` (antes `C31 reabrir con el mismo id…`) | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27: con la misma `confirmation` abierta no la reemplaza (`Pollo con arroz` en vez de `Fideos con salsa`) |
| C32 | `` `C32 onDiscard cierra el formulario` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24; en el PR 2 solo cambia cómo se abre 07 y sigue en verde |
| C33 | `` `C33 un id que no existe es notFound` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24; **borrado** el 2026-10-27 (PR 2) |
| C34 | `` `C34 guardar invalido no escribe y muestra saveError` `` (antes `…muestra errores y no escribe`) | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27: `saveError` queda en `null` (los errores van a `errors`) |
| C35 | `` `C35 corregir un campo del pop-up borra solo su error` `` (antes en `FoodConfirmationViewModelTest`) | `test/` · `FoodCaptureManualEntryTest` | sí, 2026-10-27 (el pop-up no se abre) |
| C36 | `` `C36 guardar nueva llama a addMeal emite Saved y limpia` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24; en el PR 2 sigue en verde: la foto ya guarda 520, que es la suma (el caso con `form.kcal` distinto es C87) |
| C37 | `` `C37 guardar edicion llama a updateMeal con el mismo id` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24; **borrado** el 2026-10-27 (PR 2) |
| C38 | `` `C38 dos toques en guardar escriben una sola vez` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24; de nuevo el 2026-10-27, por el pop-up (no deja seguir a 07) |
| C39 | `` `C39 si guardar falla conserva lo escrito y no emite Saved` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-24; de nuevo el 2026-10-27, por el pop-up |
| C40 | `` `C40 al terminar el analisis de voz el borrador es el de la foto` `` | `test/` · `FoodCaptureViewModelTest` | sí, 2026-10-24 |
| C41 | `` `C41 carga la comida de la ruta` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-24 |
| C42 | `` `C42 no existe es NotFound y un error de datos es Error` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-24 |
| C43 | `` `C43 Delete pide confirmacion y cancelar no borra` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-24 |
| C44 | `` `C44 un error de datos en Home muestra 0 sin romper` `` | `test/` · `HomeViewModelTest` | sí, 2026-10-24 (se cae con `MealDataException` sin atrapar) |
| C45 | `` `C45 confirmar borra una vez y emite Deleted` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-24; de nuevo el 2026-10-27: recibe una comida armada con el id de la ruta y no la de `Content` |
| C74 | `` `C74 sin Content confirmar no borra` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-27: la firma provisoria borra con el id de la ruta aunque no haya `Content` |
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
| C58 | `C58_addMeal_guarda_y_devuelve_los_ingredientes` | `androidTest/` · `MealRepositoryRoomTest` | compila, 2026-10-27; verde en el emulador el mismo día |
| C59 | `` `C59 isValidIngredient pide nombre gramos mayores a 0 y kcal no negativas` `` | `test/` · `MealValidationTest` | sí, 2026-10-27 |
| C60 | `` `C60 con borrador 07 trae los ingredientes que paso el grafo` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27 |
| C61 | `` `C61 guardar con borrador hace un solo addMeal con sus ingredientes` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27 |
| C62 | `` `C62 un ingrediente invalido no guarda y muestra el error` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27; en el PR 2 pierde la mitad de edición y sigue en verde |
| C63 | `` `C63 sin borrador 07 no tiene ingredientes` `` | `test/` · `FoodConfirmationViewModelTest` | no: pasa (el código del PR 1 ya lo cumple); control negativo del PR 2, 2026-10-27: usar `draftIngredients` sin borrador, falla |
| C64 | `` `C64 editar conserva los ingredientes guardados` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27; **borrado** el 2026-10-27 (PR 2) |
| C65 | `` `C65 Duplicate copia los ingredientes` `` | `test/` · `MealDetailViewModelTest` | no: `meal.copy(id = 0)` ya los copia; control negativo con `copy(ingredients = emptyList())`: falla, 2026-10-27 |
| C66 | `` `C66 cada MealRow trae sus ingredientes` `` | `test/` · `NutritionViewModelTest` | sí, 2026-10-27 |
| C71 | `` `C71 Duplicate con un ingrediente invalido no guarda y muestra el error` `` | `test/` · `MealDetailViewModelTest` | sí, 2026-10-27 |
| C67 | recorrido a mano y captura | emulador (360 y 411 dp) · `docs/evidencia/visual/nutrition-01-ingredientes-*` | no aplica; hecho 2026-10-27 |
| C68 | recorrido a mano y captura | emulador (360 y 411 dp) · `nutrition-02-ingredientes-*` | no aplica; hecho 2026-10-27 |
| C69 | recorrido a mano y captura | emulador (360 y 411 dp) · `nutrition-08-ingredientes-*` y `nutrition-08-sin-ingredientes-*` | no aplica; hecho 2026-10-27 |
| C70 | recorrido a mano | emulador | no aplica |
| C75 | `` `C75 validateIngredient da el resultado de cada fila de la tabla` `` | `test/` · `MealValidationTest` | sí, 2026-10-27: `validateIngredient` provisoria da `Invalid` vacío |
| C76 | `` `C76 un ingrediente valido sale sin espacios y con todos los errores si no` `` | `test/` · `MealValidationTest` | sí, 2026-10-27: `validateIngredient` provisoria da `Invalid` vacío |
| C77 | `` `C77 validateMealDetails valida como validateMeal sin mirar las kcal` `` | `test/` · `MealValidationTest` | sí, 2026-10-27: `validateMealDetails` provisoria no da errores |
| C78 | `` `C78 continuar invalido muestra errores y no abre 07` `` | `test/` · `FoodCaptureManualEntryTest` | sí, 2026-10-27: el pop-up no se abre |
| C79 | `` `C79 continuar valido abre 07 con el form y los ingredientes del pop-up` `` | `test/` · `FoodCaptureManualEntryTest` | sí, 2026-10-27: `onContinueManualEntry` devuelve `false` |
| C80 | `` `C80 cerrar el pop-up no abre 07 ni escribe` `` | `test/` · `FoodCaptureManualEntryTest` | sí, 2026-10-27: el pop-up no se abre |
| C81 | `` `C81 onConfirm devuelve el alimento como ingrediente con gramos redondeados` `` | `test/` · `FoodSearchViewModelTest` | sí, 2026-10-27: el ingrediente provisorio sale vacío |
| C82 | `` `C82 onPrefill abre el pop-up con el alimento` `` | `test/` · `FoodCaptureManualEntryTest` | sí, 2026-10-27: el pop-up no se abre |
| C83 | `` `C83 desde la busqueda 07 llega con el alimento como ingrediente` `` | `test/` · `FoodCaptureManualEntryTest` | sí, 2026-10-27: el pop-up no se abre |
| C84 | `` `C84 Add Ingredient abre el pop-up y cerrarlo no cambia la lista` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27: `ingredientEntry` sigue en `null` |
| C85 | `` `C85 confirmar un ingrediente lo agrega al final o muestra los errores` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27: la lista no cambia |
| C86 | `` `C86 corregir un campo del ingrediente borra solo su error` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27: no hay errores del ingrediente |
| C87 | `` `C87 las kcal guardadas son la suma de los ingredientes` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27: `totalKcal` provisoria da 0 |
| C88 | `` `C88 sin ingredientes no guarda` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27: el pop-up no deja seguir a 07 |
| C89 | `` `C89 un ingrediente de 0 kcal se guarda` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27: el pop-up no deja seguir a 07 |
| C90 | `` `C90 Remove saca el ingrediente de esa posicion` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27: Remove no saca nada |
| C91 | `` `C91 mientras guarda no cambian los ingredientes` `` | `test/` · `FoodConfirmationViewModelTest` | no: pasa con las firmas vacías; control negativo (Remove que borra aunque esté guardando): falla, 2026-10-27 |
| C101 | `` `C101 agregar o sacar un ingrediente borra el error de guardado` `` | `test/` · `FoodConfirmationViewModelTest` | sí, 2026-10-27: `saveError` no se borra |
| C102 | `` `C102 mientras guarda abrir 07 no reemplaza la confirmacion` `` | `test/` · `FoodConfirmationViewModelTest` | sí, por el pop-up de carga; la mitad de `onOpenConfirmation` pasaba con el código viejo; control negativo (abrir 07 siempre reemplaza): falla, 2026-10-27 |
| C92 a C98 | recorrido a mano y capturas | emulador (411 y 360 dp) | no aplica |
| C99 | capturas de la tabla y `revisor-visual` | `docs/evidencia/visual/` | no aplica |
| C100 | recorrido a mano y captura | emulador | no aplica |

- Los tests de `androidTest/` van con guion bajo (con `minSdk` 29 el DEX no acepta espacios) y se corren a mano con `./gradlew connectedDebugAndroidTest`. La salida se guarda en `docs/evidencia/tests/`.
- Los de ViewModel usan un repositorio falso, `runTest`, la regla de `Dispatchers.Main` y `UnconfinedTestDispatcher` para observar, como `HomeViewModelTest`. El falso cuenta las suscripciones con `onStart` (C26 y C50). `deleteMeal(meal)` saca de la lista la comida con `meal.id`, como Room (C72). *(Hasta el PR 2, `updateMeal` la reemplazaba.)*
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
- 2026-10-27. Implementación del PR 1 de ingredientes (datos y ViewModels, commits `425e2d6` y `d0fd972`): C53 a C66 y C71 en verde, C58 en el emulador. Decisión con Sofi al maquetar: la bajada de 01 va en hasta dos líneas y no en una (C67), para que se vean más ingredientes sin un gesto escondido; se descartó el scroll horizontal porque, sin `…`, no se nota que hay más.
- 2026-10-27. Pantallas del PR 1 de ingredientes: 01, 02, 07 y 08 dibujan los ingredientes guardados (C67 a C69), y 07 dibuja sus tarjetas con `confirmation.ingredients` (lo marcó el revisor de arquitectura). Al capturar en 360 dp la bajada de 01 quedaba pegada a las kcal: se separó con 12 dp, también en las líneas de 02. `revisor-visual`: las cinco APROBADA en la primera vuelta. C70 (instalar la versión 5 sobre una base vieja) no se recorrió a mano.
- 2026-10-27. Borrado con `@Delete`, como en la clase (rama `refactor/nutrition-borrar-con-delete`, PR aparte entre el 1 y el 2). Decisiones con Sofi: `deleteMeal` recibe la `Meal` y no el id (con el id, la implementación tendría que leer la fila o armar una Entity de mentira); si `delete` devuelve 0 no es un error (C73), a diferencia de `updateMeal` (C17). Cambian C11, C14 y C45; se suman C72 (Room borra por la clave primaria), C73 y C74 (sin `Content` no se borra), y el párrafo "Por qué la comida tiene id y el ingrediente no" en Datos, por la pregunta del profe en la clase de Retrofit.
- 2026-10-27. Corregida según la auditoría del refactor: C74 nuevo; `FakeMealRepository` sigue anotando ids en `eliminadas` (C43, C46, C47 y C48 no cambian) y suma `comidasEliminadas`; controles negativos de C11, C72 y C73 escritos; aviso a Fiore por `PANTALLAS.md` §17; C72 y C73 movidos después de C17.
- 2026-10-27. Tests en rojo del refactor `@Delete`: fallan C14, C45, C73 y C74 por el resultado. C11, C12 y C72 pasan porque Room genera `@Delete`; su control negativo (`@Update` con la misma firma) los hace fallar. C43, C46, C47 y C48 no se tocaron y siguen en verde. Firmas provisorias: `MealDao.delete` convive con `deleteById`, `MealRepository.deleteMeal(meal)` y `MealDetailViewModel.onConfirmDelete`, que por ahora siguen borrando por id.
- 2026-10-27. Implementación del refactor `@Delete`: 367 tests unitarios y 27 con emulador en verde, sin modificar tests (solo se sacó el `override` de `deleteById` del `FakeMealDao`). Control negativo de C14, C45, C73 y C74 en `docs/evidencia/tests/2026-10-27-borrar-con-delete.md`.
- 2026-10-27. Spec del PR 2 de ingredientes: C75 a C101, más C14, C29, C31, C34, C35, C36, C62, C63 y C67 a C69 reescritos (cada uno dice qué cambió). Entrevista con Sofi:
  - la carga manual es un pop-up que pide todo menos los ingredientes y las kcal, y lleva a 07; ahí se agregan los ingredientes y se guarda. 07 es el único lugar donde se guarda;
  - la búsqueda abre ese mismo pop-up, precargado, y el alimento llega a 07 como primer ingrediente;
  - las kcal de la comida son la suma de sus ingredientes; los macros se tipean para toda la comida;
  - para guardar hace falta al menos un ingrediente, y cada uno puede tener 0 kcal o más;
  - −/+ siguen de demostración; "+ Add Ingredient" y "Remove" funcionan;
  - 07 sigue con `Column` y scroll, sin `LazyColumn` (motivo en "Pop-ups y 07");
  - **cambia la decisión del 25/10:** la preparación del ViewModel pasa del grafo a las Routes, y `NutritionNavGraph` queda solo con navegación, como Workout y Progress. Ya lo hacían `FoodSearchSheetHost` y `VoiceFoodLoggingRoute`, así que quedan con un solo estilo. Se toca la zona de Fiore y se le avisa en el PR;
  - de las sugerencias del PR 1 entra solo el estilo de las kcal de 02 (C96); las demás, en "Fuera de alcance" con el motivo;
  - la edición sale del alcance: se borra la lógica y "Edit Meal" queda como demostración (C100). Motivo en "Edición: fuera de alcance". Para retomarla: "Edit Meal" abriría el pop-up precargado con la comida y "Continue" llevaría a 07 con sus ingredientes, guardando con `updateMeal`.
  
  Decidido por la IA (Sofi puede cambiarlo): el ingrediente a mano lleva gramos de 1 a 10000 y kcal de 0 a 10000, enteros; fuera de rango es `NEGATIVE`, como `parseGrams`; los gramos de la búsqueda se redondean y nunca bajan de 1; volver de 07 sin guardar no reabre el pop-up.
- 2026-10-27. Corregida según el `auditor-spec` del PR 2 (15 hallazgos: 1 BLOQUEA, 6 ALTA, 6 MEDIA y 2 BAJA). Se corrigieron todos:
  - #1 (BLOQUEA): `nutrition_confirm_not_found` queda, porque 08 la usa en `NotFound` (C42); solo se va su uso en 07.
  - #2: la lista de tests que cambian, con los tests reales (C36 entra por la foto; C62 pierde su mitad de edición; `actualizadas` en C34, C61 y C62).
  - #3: la fecha no se pide en el pop-up: es la de 01 (decisión de Sofi).
  - #4: agregar o sacar un ingrediente borra `saveError` (C101).
  - #5: doble toque en "Continue" (C78) y abrir 07 siempre reemplaza la confirmación anterior (C31), porque salir de 07 por la barra no la descarta.
  - #6: C96 con los valores del frame que midió Sofi (12 SemiBold y 14 Bold): el código ya los usa, así que 02 no cambia.
  - #7: `Rutas.kt` sale de "Fuera de alcance" y se suman `PANTALLAS.md`, `README.md` y `LEEME.md` al aviso a Fiore.
  - #8: título, "Qué hace" y contrato sin edición (`update`, `updateMeal`, `NOT_FOUND`).
  - #9: `onSave` valida una copia con las kcal sumadas, sin cambiar `confirmation.form`.
  - #10: el camino de C34; se van `ConfirmationState.mealId` y `NEW_MEAL_ID` (el id de la comida sigue).
  - #11: regla de formato, `"-0"`, `"0150"` y los textos de los rangos.
  - #12: la spec de búsqueda (contrato, Qué hace, C30 y la tabla de roturas).
  - #13: los pop-ups no se comparan con el Figma (C99).
  - #14: nombres de los tests reescritos y C100 después de C98.
  - #15: los macros desde la búsqueda no se recalculan, en "Fuera de alcance".

  Además, por decisión de Sofi: el pop-up de carga es una hoja inferior, como la búsqueda, y el de ingrediente un `AlertDialog`.
- 2026-10-27. Corregida según la segunda vuelta del `auditor-spec` (13 hallazgos: 0 BLOQUEA, 2 ALTA, 4 MEDIA, 7 BAJA). Se corrigieron todos: `nutrition_confirm_not_found` queda también en "Edición: fuera de alcance"; C63 sin `mealId` y sin guardar dos veces; C102 nuevo (mientras se guarda, abrir 07 no reemplaza la confirmación); el segundo test de `FoodCapturePrefillTest` se borra (lo reemplaza C82); C34 con `nutrition_error_save` y el orden de los controles de `onSave`; la spec de búsqueda con la marca del PR 2 en las secciones que faltaban; C14 con nombre nuevo; la lista de código sin uso completa (el botón de 07 dice siempre "Add to [tipo]"); `ManualEntrySheet` en lugar de "Dialog"; los controles de la hoja y sus textos; el `grep` de C95. Decidido por la IA (Sofi puede cambiarlo): el texto de C34 y el orden de `onSave`.
- 2026-10-27. Tests en rojo del PR 2 de ingredientes. Fallan 25 de 380 en `test/`, todos por el resultado (`AssertionError`): C75 a C90, C101 y C102, y los reescritos C29, C31, C34, C35, C38, C39 y C31 de la búsqueda (`FoodCapturePrefillTest`). C63 y C91 pasan con las firmas vacías: su control negativo falla, igual que el de la mitad de C102 que el código viejo ya cumplía. Siguen en verde, porque su criterio no cambió y solo cambió la llamada: C14, C28, C32, C36, C60, C61, C62 y C29, C30 y C32 de la búsqueda. Borrados: C10, C17, C30, C33, C37, C64, la parte de `update` de C14 y el segundo test de `FoodCapturePrefillTest`. `FoodSearchGuardadoRoomTest` pasa por el pop-up; compila y queda para el emulador. Firmas provisorias sin lógica: `IngredientForm`, `IngredientField`, `IngredientValidation`, `validateIngredient`, `validateMealDetails`, `FoodSelection`, `ManualEntryState`, `IngredientEntryState`, `totalKcal` (da 0), los eventos nuevos de `FoodCaptureViewModel` (vacíos; `onContinueManualEntry` da `false`) y `nutrition_error_no_ingredients`. `onOpenConfirmation` sin `mealId` convive con la vieja, `onPrefill(selection)` con `onPrefill(form)`, y `FakeMealDao.update` y `FakeMealRepository.updateMeal` quedan como `override` vacíos: los saca implementar junto con la edición.
