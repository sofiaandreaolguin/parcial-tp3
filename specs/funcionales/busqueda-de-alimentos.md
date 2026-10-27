# Búsqueda de alimentos y cálculo nutricional

Flujo: Nutrition
Requisitos: **EXT02** (ampliación; el núcleo no la pide). Se apoya en RF03, RF04, RF05, RF06, RF07 y RF08, y respeta NAV02, NAV03 y UI01.
Pantallas: `Nutrition / 01 — Dashboard` (chip "Search") y `Nutrition / 03 — Add Food` (botón "Search Database") abren **una hoja inferior, que no es un destino del `NavHost`**; `Nutrition / 07 — Food Confirmation` recibe el formulario cargado y guarda.
Responsable: Fiore

> **Dos cosas que contradicen a `METODOLOGIA.md` y se asumen a sabiendas, 26/10.** §16 sugiere que
> EXT02 "la toma Nutrition" (Sofi) y que las ampliaciones van "solo con el núcleo completo", y hoy UI01
> y UI03 figuran con "falta VC-s" en `REQUISITOS.md`. Fiore la toma igual y lo decide ella. Si se
> mantiene, hay que actualizar §16 al cerrar.

## Qué hace

Hoy "Search Database" avisa modo demostración. Con esto abre una pantalla donde se escribe el nombre de
un alimento, se consulta el catálogo del servidor del docente, se elige uno y se dice cuántos gramos se
comieron. La app calcula los nutrientes de esa porción y deja cargado el formulario de `Nutrition / 07`,
donde se revisa, se elige el tipo de comida y se guarda por el mismo camino de Room que la carga manual,
así el total diario se actualiza (RF04).

El servidor **no** calcula nada: devuelve los valores cada 100 g y la multiplicación la hace la app.
Comprobado el 26/10 contra `https://tp3.martinrivas.ar/`: pasarle `grams=150` devuelve la misma respuesta.

## Decisión de diseño: una sola página, sin paginación

`PAGE_SIZE = 50`. El catálogo tiene **12 alimentos** y el servidor acepta `pageSize` hasta 50, así que
**entra todo en una consulta** (comprobado el 26/10: `?pageSize=50` devuelve 12 items y
`meta: {page 1, pageSize 50, total 12}`).

`METODOLOGIA.md` §EXT02 pide paginación al llegar al final de la lista. Se saca a propósito: con 12
resultados no se dispararía nunca en uso real, y costaba seis criterios, dos campos de estado y el
detector de fin de lista. **La consigna no la pide** (`CONSIGNA.md:126` dice solo "búsqueda de alimentos
y cálculo nutricional"). Hay que anotarlo en METODOLOGIA al cerrar.

Igual el repositorio recibe `page` y lo manda, así que agregarla después es sumar criterios, no rehacer.

## Decisión de diseño: es una hoja, no una pantalla

Las ocho de Nutrition en el Figma son Dashboard, Today's Meals, Add Food, Voice Food Logging, Food
Analysis, Photo Analysis, Food Confirmation y Meal Details. **Ninguna es una pantalla de resultados.**

Se probó primero como ruta propia y **se cambió después de verla funcionando**: sacaba de contexto.
Queda como `ModalBottomSheet` sobre la pantalla que la abre, que se sigue viendo detrás.

**No es un destino del `NavHost`, así que el inventario no cambia:** siguen siendo 37 pantallas y 33
destinos. Siendo ruta había que tocar once archivos de conteos, que es lo que se mira para NAV01 y
NAV02 (25 puntos entre los dos). Ficha: `docs/decisiones/010-buscador-como-hoja.md`.

Se monta como el calendario (`NutritionDatePickerDialog`): la Route guarda un booleano y dibuja la
hoja cuando está en true. Altura fija al **82%**; dejándola crecer con el contenido, la lista
colapsaba y se veían dos alimentos de doce.

## Decisión de diseño: los gramos se piden en la misma hoja

`food_search` tiene **dos momentos, no dos rutas**: la lista de resultados y, con un alimento elegido, el
panel de la porción. Es un cambio de estado, no navegación, igual que los cinco estados de `VoiceScreen`.

Eso define el "atrás", que es NAV02: desde el panel vuelve a los resultados sin perderlos (C28),
y desde los resultados cierra la hoja. Vale para la flecha y para el botón del
sistema, que necesita un `BackHandler` como el de `FoodConfirmationRoute.kt:50`.

## Impacto en lo que ya está

**Ninguno en el inventario.** Al no ser un destino, `Rutas.kt`, `RutasTest`, `PANTALLAS.md`,
`CONTEXTO.md`, el README y las fichas 002 y 004 quedan **como estaban**: 37 pantallas, 33 destinos,
`RUTAS_ENCIMA` con 15.

Lo único que cambia de lo que ya existía:

| Archivo | Qué cambia |
|---|---|
| `AddFoodRoute.kt` y `NutritionDashboardRoute.kt` | `onSearch` deja de ser `rememberDemoToast()` y abre la hoja |
| `specs/PANTALLAS.md` §7 | el control "Search" de `01` pasa de "modo demostración" a abrir la hoja; una nota, no una fila nueva |
| `ui/navigation/NutritionNavGraph.kt` | los dos destinos pasan `grafo` y `onConfirmed`. **`Rutas.kt` no se toca**, pero el grafo sí |
| `data/remote/VoraApi.kt` | un endpoint más: `GET /v1/foods` (zona de Hector) |
| `data/repository/AppError.kt` y `CoachRepositoryImpl.kt` | `aError()` se mueve de uno a otro y pasa a `internal`, para no duplicarla (zona de Hector) |
| `di/RepositoryModule.kt` | `@Binds` de `FoodRepository` |
| `ui/screens/nutrition/FoodCaptureViewModel.kt` | `onPrefill` y `toFormText` a `internal` (zona de Sofi) |
| `res/values/strings.xml` y `values-es/` | las 15 claves nuevas |
| `ui/screens/nutrition/LEEME.md` | dice que la búsqueda es una hoja, no una novena pantalla |

## Zonas con responsable: hay que avisar

- **`VoraApi` (Hector).** Un endpoint más: `GET /v1/foods`. Es el tercero de esta rama.
- **`FoodCaptureViewModel` (Sofi).** `Nutrition / 07` no tiene ViewModel propio. Hacen falta dos cambios
  en ese archivo, los dos chicos:
  - `fun onPrefill(form: MealForm)`, que **limpia el borrador** además de dejar la confirmación:
    `_uiState.update { it.copy(draft = null, confirmation = ConfirmationState(NEW_MEAL_ID, form)) }`.
    Sin limpiar `draft`, si antes hubo una foto, 07 mostraría Chicken / Rice / Vegetables debajo de
    "Pollo cocido" (`FoodConfirmationRoute.kt:57` lee `state.draft?.ingredients`).
  - `private fun Double.toFormText()` pasa a `internal`, para precargar "248" y no "248.0".
    **Esto no es cosmético:** `parseKcal` valida con `WHOLE_NUMBER = ^-?\d+$`, así que "248.0" haría que
    07 marque `NOT_A_WHOLE_NUMBER` y no deje guardar.
- **No se toca `MealValidation.kt`** (también de Sofi): `Parsed` y `DECIMAL_NUMBER` son privados, así que
  `FoodPortion` escribe su propia regla, con un comentario que apunte a la de allá.
- **No sube la versión de la base** ni se tocan `MealEntity`, `MealDao` ni `MealRepository`.

## Datos

- **DTO** (`data/remote/dto/FoodDto.kt`), anulables a propósito: Gson los deja en null si faltan, y eso
  es `INVALID_DATA` (C7).
  - `FoodsResponseDto { data: List<FoodDto>?, meta: PageMetaDto? }`
  - `FoodDto { id: String?, name: String?, nutritionPer100g: NutrientsDto? }`
  - `NutrientsDto { kcal: Int?, proteinG: Double?, carbsG: Double?, fatG: Double? }`
  - `PageMetaDto { page: Int?, pageSize: Int?, total: Int? }` — **anulable como el resto**: Gson no corre el constructor de Kotlin, así que un `meta` truncado dejaría `total` en 0 y la pantalla diría "no se encontró nada" cuando la respuesta vino rota.
  - Los ids son **strings** (`f01` a `f12`).
- **Modelo** (`domain/model/FoodItem.kt`): `FoodItem { id, name, per100g: Nutrients }`,
  `Nutrients { kcal: Int, proteinG: Double, carbsG: Double, fatG: Double }`,
  `FoodPage { items, page, pageSize, total }`.
  - **No se llama `Food`**: `domain/model/Profile.kt:73` ya tiene `enum class Food` en el mismo paquete.
  - **No se llama `Nutrition`**: hay un `data object Nutrition` en `ui/screens/home/NotificationsUiState.kt`.
- **Entity: ninguna nueva.** El catálogo no se cachea. Lo que se guarda es la comida, con lo que ya existe.

Los 12 alimentos cada 100 g, comprobados el 26/10:

| id | Nombre | kcal | prot | carbs | grasa |
|---|---|---|---|---|---|
| f01 | Avena | 389 | 16.9 | 66.3 | 6.9 |
| f02 | Leche | 61 | 3.2 | 4.8 | 3.3 |
| f03 | Banana | 89 | 1.1 | 22.8 | 0.3 |
| f04 | Arroz cocido | 130 | 2.7 | 28.2 | 0.3 |
| f05 | Pollo cocido | 165 | 31 | 0 | 3.6 |
| f06 | Lentejas cocidas | 116 | 9 | 20.1 | 0.4 |
| f07 | Tomate | 18 | 0.9 | 3.9 | 0.2 |
| f08 | Huevo | 155 | 13 | 1.1 | 11 |
| f09 | Yogur natural | 63 | 5.3 | 7 | 1.6 |
| f10 | Manzana | 52 | 0.3 | 13.8 | 0.2 |
| f11 | Pan integral | 247 | 13 | 41 | 4.2 |
| f12 | Aceite de oliva | 884 | 0 | 0 | 100 |

## Cómo se porta el servidor (comprobado el 26/10)

| Lo que se pide | Lo que devuelve |
|---|---|
| `pageSize=50` | **los 12 en una página**, `meta.total = 12` |
| `q=POLLO` | "Pollo cocido": ignora mayúsculas |
| `q=man` | "Manzana": busca por parte del nombre |
| `q=` vacío | el catálogo entero |
| `q=zzzzz` | `data: []` con `meta.total = 0` |
| `pageSize=999` | **400** `INVALID_REQUEST` |
| `X-Mock-Scenario: empty` | `meta.total = 0`: con eso se demuestra C16 sin inventar una búsqueda |
| `X-Mock-Scenario: error` | **503** |

## Criterios de aceptación (EARS)

Cada línea es un test. Si no se te ocurre el test, la línea está mal escrita.

Los errores se nombran con el `AppError` que ya existe (`NO_CONNECTION, SERVER, NOT_FOUND, INVALID_DATA,
UNKNOWN`) y se traducen en el repositorio con `toAppError()`. **No se inventa un tipo paralelo.**

### Datos (Retrofit y repositorio)

- C1. CUANDO se busca, la app DEBE pedir `GET /v1/foods` con el texto en `q`, `1` en `page` y `PAGE_SIZE` en `pageSize` (EXT02).
- C2. La conversión `FoodsResponseDto` → `FoodPage` DEBE conservar el id, el nombre y los cuatro nutrientes de cada alimento, más `page`, `pageSize` y `total`, y el repositorio DEBE devolver modelos, nunca DTOs (RF06).
- C3. SI el servidor no responde (`IOException`), ENTONCES el repositorio DEBE fallar con `AppError.NO_CONNECTION` (RF05).
- C4. SI el servidor devuelve **503 o 400**, ENTONCES el repositorio DEBE fallar con `AppError.SERVER` —los dos igual, porque para quien usa la app es lo mismo— y nunca devolver una lista vacía (RF05).
- C5. SI el cuerpo no es JSON válido, ENTONCES el repositorio DEBE fallar con `AppError.INVALID_DATA`, **no** con `NO_CONNECTION` (RF05). `MalformedJsonException` hereda de `IOException`: hay que atraparla antes.
- C6. SI falta `data`, falta `meta`, o un alimento llega sin `id`, sin `name` o sin `nutritionPer100g`, ENTONCES toda la consulta DEBE fallar con `AppError.INVALID_DATA` (RF05, RF06).
- C7. El repositorio DEBE relanzar `CancellationException` sin traducirla a `AppError`, como ya hacen `CoachRepositoryImpl` y `WorkoutRepositoryImpl` (si no, cada letra tipeada deja un fallo `UNKNOWN`).
- C8. La pantalla no DEBE importar Retrofit ni el DAO (RF06; lo controla `scripts/reglas.sh`).

### Cálculo de la porción (funciones puras, `domain/FoodPortion.kt`)

- C9. La app DEBE calcular los nutrientes de una porción multiplicando cada valor cada 100 g por `gramos / 100`, haciendo la cuenta con `BigDecimal` y redondeando con **HALF_UP**: las calorías a entero (`setScale(0)`) y los macros a un decimal (`setScale(1)`).
- C10. CUANDO la porción es de **150 g de f05 Pollo** (165 / 31 / 0 / 3,6), el resultado DEBE ser **248 kcal, 46,5 prot, 0 carbs y 5,4 grasa**.
- C11. CUANDO la porción es de **150 g de f01 Avena**, la proteína DEBE ser **25,4** (el valor exacto es 25,35 y HALF_UP sube). No 25,3, que es lo que sale de redondear el artefacto `25.349999999999998` de la coma flotante: por eso la cuenta va en `BigDecimal`.
- C12. CUANDO la porción es de **150 g de f03 Banana**, la proteína DEBE ser **1,7** (exacto 1,65; HALF_EVEN daría 1,6).
- C13. CUANDO la porción es de **100 g**, los nutrientes DEBEN ser exactamente los del alimento.
- C14. CUANDO un nutriente vale 0 cada 100 g, DEBE valer 0 en cualquier porción (f05 tiene `carbs = 0`; f12 tiene `prot = 0` y `carbs = 0`).

### Los gramos: parseo y validación

> `FoodPortion` escribe su propia regla porque `Parsed` y `DECIMAL_NUMBER` son privados en
> `MealValidation.kt`, que es de Sofi. Misma regex, distinto trato del 0: **`parseMacro` acepta 0 porque
> una comida puede tener 0 g de grasa; una porción de 0 g no es una comida.**

- C15. CUANDO los gramos se escriben con coma ("150,5"), la app DEBE tomarla como separador decimal. Sin "+", exponente, NaN ni separador de miles.
- C16. SI los gramos están vacíos, ENTONCES la app NO DEBE dejar continuar y DEBE marcar `FieldError.REQUIRED`.
- C17. SI los gramos no son un número ("abc", "1.2.3", "1e5"), ENTONCES DEBE marcar `FieldError.NOT_A_NUMBER`.
- C18. SI los gramos son **0, negativos o mayores que 10000**, ENTONCES la app NO DEBE dejar continuar, DEBE marcar `FieldError.NEGATIVE` y **no DEBE llamar al cálculo**. El tope existe porque con muchos dígitos las calorías no entran en `Meal.kcal: Int` y el guardado quedaría trabado sin explicación (`MealValidation.kt:55` ya guarda ese caso).

### Buscar

- C19. CUANDO se abre la hoja **por primera vez sobre una pantalla**, la app DEBE consultar con `q = ""` y mostrar el catálogo, y la consulta DEBE arrancar en el ViewModel, no en la composición (RF07, `CONTEXTO.md` §2). **Al reabrirla sobre la misma pantalla no vuelve a consultar**, a propósito: el ViewModel está atado al destino y conserva texto y resultados.
- C20. MIENTRAS se escribe, la app DEBE esperar **`DEBOUNCE_MS` = 300 ms** sin tecleo antes de consultar: escribir "pollo" de corrido DEBE disparar **una sola** consulta.
- C21. MIENTRAS la consulta está en curso, la app DEBE mostrar el estado de carga (RF05).
- C22. SI `meta.total` es **0**, ENTONCES la app DEBE mostrar el estado **vacío**, que no es el de error (RF05).
- C23. SI la consulta falla, ENTONCES la app DEBE mostrar el aviso con **Reintentar**, y al tocarlo DEBE repetir la misma búsqueda. Cada `AppError` DEBE tener su mensaje, y los que no tienen uno propio DEBEN mostrar el de servidor, como hace `ConversationViewModel` (RF05).
- C24. CUANDO una búsqueda se cancela porque cambió el texto, el estado NO DEBE quedar en error y DEBE terminar mostrando el resultado de la última búsqueda, no el de una anterior.
- C25. CUANDO el texto queda vacío, la app DEBE cancelar la consulta pendiente y volver a mostrar el catálogo completo, como al abrir.

### Elegir, porción y precarga

- C26. CUANDO se elige un alimento, la app DEBE mostrar el panel de la porción con **100** en los gramos.
- C27. MIENTRAS se cambian los gramos, la app DEBE mostrar los cuatro nutrientes recalculados **sin volver a consultar el servidor**.
- C28. CUANDO se vuelve atrás desde el panel de la porción —con la flecha o con el botón del sistema—, la app DEBE volver a los resultados **sin perderlos** y sin alimento elegido; sin alimento elegido, el atrás DEBE cerrar la hoja (NAV02).
- C29. CUANDO se confirma la porción, `onConfirm(date)` DEBE devolver un `MealForm` con el nombre del alimento, los cuatro nutrientes redondeados, esa fecha y `type = null`.
- C30. Los números del formulario DEBEN entrar con el formato de `toFormText`: las calorías como entero sin decimales ("248") y los macros con punto y sin ".0" ("0" y no "0.0"). Si no, `parseKcal` marca `NOT_A_WHOLE_NUMBER` y 07 no deja guardar.
- C31. CUANDO se precarga desde la búsqueda, `onPrefill` DEBE dejar `draft = null`, así 07 NO muestra los ingredientes de un análisis anterior (una comida = un alimento).
- C32. El formulario precargado DEBE llevar la fecha seleccionada en `NutritionViewModel`, la misma que usa la carga manual, para que el total diario que cambie sea el del día que se está mirando (RF04).
- C33. CUANDO se guarda desde `Nutrition / 07`, la comida DEBE quedar en Room por el mismo camino que la carga manual y el total diario DEBE cambiar (RF04).
- C34. MIENTRAS hay una búsqueda escrita o unos gramos sin confirmar, CUANDO se rota la pantalla, lo escrito NO DEBE perderse (RF07).
- C35. CUANDO se confirma la porción, la hoja DEBE cerrarse y el estado DEBE quedar **sin alimento elegido**, así la próxima vez que se abra arranca en la lista y no en el panel del alimento anterior.

### Navegación y cierre

- C36. La búsqueda NO DEBE agregar ningún destino al `NavHost`: el inventario sigue en 33 rutas (lo controla `RutasTest`, que no cambia).
- C37. El botón "Search Database" de `Nutrition / 03` y el chip "Search" de `Nutrition / 01` DEBEN abrir la hoja, y ninguno DEBE seguir avisando modo demostración (NAV03). **Evidencia manual con captura:** montar la hoja pide Hilt y navegación, y no hay infraestructura de Compose en `test/`.
- C38. Ningún texto de la pantalla DEBE estar escrito dentro de un Composable, y las 15 claves nuevas DEBEN existir en `values/` y en `values-es/` (`CONTEXTO.md` §3).

Plantillas: **La app DEBE** (siempre) · **CUANDO** (ante un evento) · **MIENTRAS** (durante un estado) · **SI ... ENTONCES** (algo sale mal) · **DONDE** (opcional, para ampliaciones).

## Contrato

| Pieza | Archivo | Firma |
|---|---|---|
| API | `data/remote/VoraApi.kt` | `@GET("v1/foods") suspend fun getFoods(@Query("q") q: String, @Query("page") page: Int, @Query("pageSize") pageSize: Int): Response<FoodsResponseDto>` |
| DTO | `data/remote/dto/FoodDto.kt` | las cuatro clases de Datos, con campos anulables |
| Modelo | `domain/model/FoodItem.kt` | `FoodItem`, `Nutrients`, `FoodPage` |
| Mappers | `data/repository/FoodMappers.kt` | `fun FoodsResponseDto.toDomain(): FoodPage`; lanza si falta un campo (C6) |
| Repositorio | `data/repository/FoodRepository.kt` y `FoodRepositoryImpl.kt` | `suspend fun searchFoods(q: String, page: Int): Result<FoodPage>` |
| Cálculo | `domain/FoodPortion.kt` | `fun nutrientsForPortion(per100g: Nutrients, grams: Double): Nutrients` y `fun parseGrams(text: String): GramsParse`, con `sealed interface GramsParse { data class Ok(val grams: Double); data class Err(val error: FieldError) }` — **puras, sin Android** |
| Estado | `ui/screens/nutrition/FoodSearchUiState.kt` | `data class FoodSearchUiState(query: String = "", items: List<FoodItem> = emptyList(), cargando: Boolean = false, error: AppError? = null, vacio: Boolean = false, seleccionado: FoodItem? = null, gramos: String = "100", gramosError: FieldError? = null, porcion: Nutrients? = null)` |
| ViewModel | `ui/screens/nutrition/FoodSearchViewModel.kt` | `@HiltViewModel class FoodSearchViewModel @Inject constructor(private val repo: FoodRepository)` con `onQueryChange(String)`, `onRetry()`, `onFoodSelected(FoodItem)`, `onGramsChange(String)`, `onBackFromPortion()` y `onConfirm(date: LocalDate): MealForm?` |
| Constantes | `FoodSearchViewModel.kt` y `FoodRepository.kt` | `DEBOUNCE_MS = 300L` en el ViewModel y `PAGE_SIZE = 50` en el repositorio, que es quien lo manda |
| Hoja | `ui/screens/nutrition/FoodSearchScreen.kt` | `FoodSearchSheet(...)` envuelve en `ModalBottomSheet` a `FoodSearchScreen(...)`, que no tiene estado; reusa `VoraTextField` y `MacroTotalsRow`; `BackHandler` para el atrás del sistema (C28) |
| Anfitrión | `ui/screens/nutrition/FoodSearchSheetHost.kt` | `FoodSearchSheetHost(grafo: NavBackStackEntry, onDismiss: () -> Unit, onConfirmed: () -> Unit)` |
| Puerta a 07 | `ui/screens/nutrition/FoodCaptureViewModel.kt` | **nuevo** `fun onPrefill(form: MealForm)` que limpia `draft`; `toFormText` pasa a `internal` |
| Add Food y Dashboard | `AddFoodRoute.kt` y `NutritionDashboardRoute.kt` | `onSearch` sube como parámetro en vez de `rememberDemoToast()` |
| Montaje | `AddFoodRoute.kt` y `NutritionDashboardRoute.kt` | un `rememberSaveable` booleano y la hoja cuando está en true, igual que `NutritionDatePickerDialog`. **No se toca `Rutas.kt` ni el grafo.** |
| Hilt | `di/RepositoryModule.kt` | `@Binds @Singleton abstract fun bindFoodRepository(impl: FoodRepositoryImpl): FoodRepository` |

## Textos nuevos

| Clave | Inglés (`values/`) | Español (`values-es/`) |
|---|---|---|
| `food_search_title` | Search food | Buscar alimento |
| `food_search_hint` | Search a food by name | Buscá un alimento por su nombre |
| `food_search_loading` | Searching… | Buscando… |
| `food_search_empty` | No food found with that name | No se encontró ningún alimento con ese nombre |
| `food_search_retry` | Retry | Reintentar |
| `food_search_error_no_connection` | No connection. Check the server and try again. | Sin conexión. Revisá el servidor y reintentá. |
| `food_search_error_server` | The server is not responding right now. | El servidor no está respondiendo en este momento. |
| `food_search_error_invalid_data` | The answer could not be read. | No se pudo leer la respuesta. |
| `food_search_per_100g` | %1$d kcal per 100 g | %1$d kcal cada 100 g |
| `food_search_grams_label` | Portion (g) | Porción (g) |
| `food_search_grams_required` | Enter how many grams | Poné cuántos gramos |
| `food_search_grams_not_a_number` | Only numbers, with comma or dot | Solo números, con coma o punto |
| `food_search_grams_negative` | Between 1 and 10000 grams | Entre 1 y 10000 gramos |
| `food_search_confirm` | Use this food | Usar este alimento |
| `food_search_back_to_results` | Back to results | Volver a los resultados |

## Fuera de alcance

- **Sin paginación** (ver la decisión de arriba).
- **No se cachea el catálogo en Room.** Sin conexión no se busca: error con reintento.
- **Una comida = un alimento.** Sumar ingredientes es otra funcionalidad y la encara Sofi.
- **No se toca `MealDraft`, `MealValidation`, `MealEntity`, `MealDao` ni `MealRepository`.**
- **`Nutrition / 07` no se rediseña**: su controlador gana `onPrefill` y `toFormText` pasa a `internal`.
- No se modifican los frames de `Nutrition / 01` ni `03`: solo cambia a dónde llevan sus controles.
- No hay favoritos, historial de búsquedas ni alimentos propios.

## Tests

| Criterio | Test | Dónde | Vi el rojo |
|---|---|---|---|
| C1 | `C1 buscar pide v1 foods con q page y pageSize` | `test/` | sí |
| C2 | `C2 la respuesta se convierte en FoodPage con nutrientes y meta` | `test/` | sí |
| C3 | `C3 sin servidor falla con NO_CONNECTION` | `test/` | sí |
| C4 | `C4 un 503 y un 400 fallan con SERVER` | `test/` | sí |
| C5 | `C5 json roto falla con INVALID_DATA y no con NO_CONNECTION` | `test/` | sí |
| C6 | `C6 si falta un campo toda la consulta falla con INVALID_DATA` | `test/` | sí |
| C7 | `C7 cancelar la busqueda no se traduce a AppError` | `test/` | sí |
| C8 | — | `scripts/reglas.sh` | |
| C9 | `C9 la porcion escala con BigDecimal y redondea HALF_UP` | `test/` | sí |
| C10 | `C10 ciento cincuenta gramos de pollo dan 248 y 46 coma 5` | `test/` | sí |
| C11 | `C11 ciento cincuenta gramos de avena dan 25 coma 4 y no 25 coma 3` | `test/` | sí |
| C12 | `C12 ciento cincuenta gramos de banana dan 1 coma 7 y no 1 coma 6` | `test/` | sí |
| C13 | `C13 las calorias tambien redondean con HALF_UP y no con HALF_EVEN` | `test/` | **agregado el 27/10, ver abajo** |
| C13 | `C13 cien gramos devuelve los mismos valores` | `test/` | sí |
| C14 | `C14 un nutriente en cero sigue en cero en cualquier porcion` | `test/` | sí |
| C15 | `C15 los gramos con coma se leen como decimal` | `test/` | sí |
| C16 | `C16 gramos vacios marcan REQUIRED` | `test/` | sí |
| C17 | `C17 gramos que no son numero marcan NOT_A_NUMBER` | `test/` | sí |
| C18 | `C18 cero negativos y mas de diez mil marcan NEGATIVE y no calculan` | `test/` | sí |
| C19 | `C19 al abrir consulta con q vacio desde el init` | `test/` | sí |
| C20 | `C20 escribir pollo de corrido dispara una sola consulta` | `test/` | sí |
| C20 | `C20 antes de los 300 ms todavia no consulto` | `test/` | **agregado tras el control negativo** |
| C21 | `C21 mientras busca el estado es cargando` | `test/` | sí |
| C22 | `C22 total cero deja el estado vacio y no el de error` | `test/` | sí |
| C22 | `C22 data vacia con total mayor que cero no es el estado vacio` | `test/` | **agregado tras el control negativo** |
| C23 | `C23 al fallar hay reintento con su mensaje y repite la busqueda` | `test/` | sí |
| C24 | `C24 una busqueda cancelada no deja el estado en error` | `test/` | sí |
| C24 | `C24 una respuesta vieja que llega tarde no pisa a la nueva` | `test/` | **agregado tras la revisión de arquitectura** |
| C25 | `C25 borrar el texto vuelve a mostrar el catalogo` | `test/` | sí |
| C26 | `C26 elegir un alimento deja la porcion en cien gramos` | `test/` | sí |
| C27 | `C27 cambiar los gramos recalcula sin volver a consultar` | `test/` | sí |
| C28 | `C28 volver desde la porcion conserva los resultados` | `test/` | sí |
| C29 | `C29 onConfirm arma el MealForm con nombre nutrientes fecha y type null` | `test/` | sí |
| C30 | `C30 los numeros entran como 248 y no como 248 punto cero` | `test/` | sí |
| C31 | `C31 onPrefill limpia el borrador del analisis` | `test/` | sí |
| C32 | `C32 el formulario lleva la fecha seleccionada` | `test/` | sí |
| C33 | `c33_guardar_desde_la_busqueda_deja_la_comida_en_room` | `androidTest/` | sí |
| C33 | `c33_el_total_del_dia_suma_lo_que_guardo_la_busqueda` | `androidTest/` | sí |
| C34 | `c34_rotar_conserva_el_texto_buscado` | `androidTest/` | sí |
| C34 | `c34_rotar_conserva_los_gramos_sin_confirmar` | `androidTest/` | sí |
| C35 | `C35 volver de 07 sin guardar deja los resultados sin seleccion` | `test/` | sí |
| C36 | `las tres listas suman las 33 rutas del inventario sin repetir` (el de siempre, sin cambios) | `test/` | — |
| C37 | — | evidencia manual (captura) | — |
| C38 | — | `scripts/reglas.sh` y revisión de las dos `strings.xml` en el pull request | |

**Solo C33 y C34 necesitan emulador.** Los otros 36 corren en `test/` con MockWebServer, un
`FoodRepository` falso o Robolectric.

### Control negativo, hecho el 26/10

Se rompió el código a propósito, una cosa por vez, y se comprobó qué test lo detecta. **21 roturas,
las 21 detectadas**, pero cinco de ellas recién después de agregar los tests que faltaban.

| Rotura a propósito | Lo detecta |
|---|---|
| Redondear los macros con HALF_EVEN | C12 (banana: 1,65 daría 1,6 y tiene que dar 1,7) |
| Redondear **las calorías** con HALF_EVEN | C13 (avena 50 g: 194,5 daría 194 y tiene que dar 195) |
| Hacer la cuenta en `Double`, con el orden natural `valor × (gramos/100)` | C11 (avena: daría 25,3 y tiene que dar 25,4) |
| Aceptar una porción de 0 gramos | C18 |
| Sacar el tope de 10000 gramos | C18 |
| Tragarse la `CancellationException` | C7 |
| Dejar pasar un alimento sin nutrientes | C6 |
| Clasificar el 400 como `INVALID_DATA` | C4 |
| Precargar los números con ".0" | C30 |
| No limpiar el borrador al precargar 07 | C31 |
| Precargar las calorías como "248.0" | C33 (las dos: la validación de 07 las rechaza y no se guarda nada) |
| Guardar el texto buscado en un `remember` local que arranca vacío | C34 (`...texto_buscado`) |
| Lo mismo con el campo de gramos | C34 (`...gramos_sin_confirmar`) |
| Dejar `PageMetaDto` con campos no anulables | C6 (un `meta` truncado se veía como "no hay resultados") |
| Sacar la cancelación de la búsqueda anterior | C20 y C24 (la respuesta vieja pisaba a la nueva) |
| Mandar todos los `AppError` al mismo aviso | C23 |
| Mapear mal el aviso de los gramos | C16-C18 |
| Calcular igual con los gramos inválidos | C18 |
| **Buscar sin esperar el debounce** | **nadie, hasta agregar el segundo test de C20** |
| **Mirar `data.isEmpty()` en vez de `meta.total`** | **nadie, hasta agregar el segundo test de C22** |

Los dos últimos son el motivo por el que el control negativo vale la pena. `C20 escribir pollo de
corrido dispara una sola consulta` pasaba **sin debounce**, porque cancelar la consulta anterior ya
dejaba una sola: lo que había que probar era que **espera**. Y `C22 total cero` pasaba mirando la
lista vacía, porque en ese caso de prueba `total = 0` y la lista vacía coinciden; hace falta el caso
donde no coinciden.

**Y una rotura que quedó pegada en el código sin que nadie la notara.** El 27/10, al revisar el
árbol antes de cerrar, apareció `RoundingMode.HALF_EVEN` en la línea de las calorías: resto de un
control negativo mal revertido. **Los 347 tests pasaban igual.** El motivo es que C10 usa 247,5, y
247,5 da 248 con las dos reglas: HALF_EVEN elige el par, que ahí es el de arriba. Hacía falta un caso
donde el par sea el de abajo, y ese es **avena 50 g = 194,5** (HALF_UP 195, HALF_EVEN 194). Quedó
como C13. La moraleja es doble: revisar el árbol antes de cerrar, y que un ejemplo numérico sólo
distingue dos reglas si está elegido para distinguirlas.

**Dos trampas de medición que casi dan un falso verde:** Gradle escribe el resultado de los tests por
**stderr**, no por stdout, así que leer solo stdout hace parecer que nada falla; y "BUILD SUCCESSFUL"
también sale cuando la tarea queda **UP-TO-DATE** sin correr nada. Hay que borrar los XML de
`app/build/test-results/` y comprobar que se regeneren.

## Al cerrar la tarea hay que actualizar

Los dos archivos de la tabla de Impacto, más: `specs/REQUISITOS.md` (EXT02 pasa a "hecho"),
`specs/METODOLOGIA.md` (§EXT02 sin paginación, y §16 con la dueña real),
`docs/decisiones/010-buscador-como-hoja.md` (ficha nueva) y `docs/BITACORA.md`.

## Auditoría

- **auditor-spec, 26/10, primera pasada: 15 hallazgos**, los 4 críticos verificados contra el código. `Food` ya existía como enum (→ `FoodItem`); no había camino para precargar 07 (→ `onPrefill`); la ruta nueva rompe `RutasTest`; el Contrato no alcanzaba para escribir los tests. Más la tabla de textos, `AppError` en vez de prosa, `CancellationException`, el parseo de gramos y la contradicción entre escalar y redondear.
- **revisor-arquitectura, 27/10: nada bloquea.** 1 ALTA, corregida: `PageMetaDto` tenía los campos
  no anulables, contra lo que decía el KDoc de su propio archivo, así que un `meta` truncado se veía
  como estado vacío en vez de `INVALID_DATA`. Se sumó el caso a C6 y se comprobó que lo detecta.
  Además marcó que al debounce le faltaba el test de la carrera de verdad (la respuesta vieja que
  llega tarde): está agregado y tiene su control negativo. Las sugerencias que **no** se aplicaron,
  por tiempo y porque ninguna bloquea, están en el cuerpo del pull request.
- **auditor-spec, 26/10, segunda pasada: 15 hallazgos más.** Los que cambiaron el diseño:
  - `parseKcal` usa `WHOLE_NUMBER`, así que precargar "248.0" **impedía guardar**. De ahí C30 y que `toFormText` pase a `internal`.
  - `Parsed` y `DECIMAL_NUMBER` son privados: "reusar `MealValidation`" no compilaba. `FoodPortion` escribe su regla.
  - `parseMacro` acepta 0, así que no se podía reusar y exigir que 0 sea error. Queda declarado como diferencia a propósito.
  - **El C15 anterior estaba mal:** pedía 25,3 para 150 g de avena, que es el artefacto de coma flotante. El valor correcto con HALF_UP es **25,4**. De ahí el `BigDecimal` de C9 y el caso de la banana (C12), que separa HALF_UP de HALF_EVEN.
  - `onPrefill` tiene que limpiar `draft` o 07 muestra ingredientes de un análisis viejo (C31).
  - Sumar una ruta toca **once archivos**, no seis.
  - Se sacaron por costo/beneficio: la paginación entera (`PAGE_SIZE = 50` y entran los 12), y cuatro criterios que no eran comportamiento. De 52 a 38.

## Cómo se cerró, el 26/10

- **351 tests unitarios, 0 fallos** (`./gradlew testDebugUnitTest`), 42 de ellos de EXT02.
- Los 36 se vieron en rojo antes de implementar, **todos por el resultado** (`expected ... but was ...`),
  ninguno por excepción ni por compilación, y **ninguno pasó en rojo**.
- `./gradlew assembleDebug` en verde y `./scripts/reglas.sh` sin hallazgos.
- **25 tests con emulador, 0 fallos** (`./gradlew connectedDebugAndroidTest`): los 21 que ya había
  más los 4 de EXT02. Los cuatro pasaron a la primera, así que se les hizo control negativo después.
- **C34 prueba menos de lo que parece, y está dicho en el test.** `query` y `gramos` viven en el
  ViewModel, que sobrevive al giro por cómo funciona Android, no por código nuestro. Lo que el test
  agarra es que la pantalla dibuje **desde el estado**: con un `remember` local que arranca vacío,
  el texto se pierde al rotar y el test falla. Con un `remember` sembrado desde el estado **no**
  falla, y eso también se comprobó.
- **Un test propio se corrigió, no se borró:** el de los gramos esperaba que "1,000" fuera un error,
  cuando la regla que ya usa `MealValidation` lo lee como **un** gramo. Quedó como criterio con el
  comportamiento real, porque sorprende.

## Historial

- 26/10 — Spec escrita (32 criterios). El endpoint se probó contra `https://tp3.martinrivas.ar/` antes de escribirla.
- 26/10 — Reescrita tras la primera auditoría: 52 criterios.
- 26/10 — Recortada tras la segunda: **38 criterios**, sin paginación, con el redondeo en `BigDecimal` y el formato de precarga resuelto.

- 27/10 — **Pasa de pantalla a hoja inferior**, después de probarla a mano: sacaba de contexto. Se revirtieron los once archivos de conteos y se borraron los dos tests de navegación, cuyos criterios ya no existen. Quedan **36 criterios**.
