# Ingredientes en las comidas, PR 1 · verde (27/10/2026)

Nutrition: los ingredientes se guardan en la fila de la comida (RF03, RF04, RF06, RF07). Rama `feat/nutrition-ingredientes-datos`. Spec: `specs/funcionales/carga-y-eliminacion-de-comidas.md` (C53 a C66 y C71). Los tests en rojo están en `928d9d2`.

## Qué se implementó (skill implementar)

- `MealMappers.kt`: `toEntity()` escribe los ingredientes con `Gson().toJson`; `toDomain()` los lee con `fromJson` a `Array<Ingredient?>` y tira `IllegalArgumentException` si Gson tira `JsonParseException`, si devuelve `null`, si hay un elemento `null` o si falta el nombre. Los números que faltan quedan en 0. C53 a C57.
- `VoraDatabase`: `version = 5` sin `Migration`, con el KDoc al día (borra todas las tablas una vez). C58, C70.
- `isValidIngredient`: nombre que no esté en blanco, `grams > 0` y `kcal >= 0`. C59.
- `FoodCaptureViewModel`: con borrador, `confirmation.ingredients` son los `draftIngredients`; sin borrador, vacía; al editar, los de la comida guardada. Al guardar, si un ingrediente no es válido, `saveError = nutrition_error_ingredients` y no se escribe nada; si no, la `Meal` va con sus ingredientes en un solo `addMeal` o `updateMeal`. C60 a C64.
- `MealDetailViewModel.onDuplicate`: valida los ingredientes antes de `addMeal`. C65, C71.
- `NutritionViewModel`: cada `MealRow` trae sus ingredientes. C66.
- `NutritionNavGraph.rememberAbrirConfirmacion` (Fiore): resuelve con `getString` los nombres de `draft.ingredients` y los pasa como cuarto argumento.
- Ningún test se modificó después del rojo. Las pantallas 01, 02, 07 y 08 van en el paso siguiente (skill `pantalla`).

## Verde

```text
./gradlew assembleDebug testDebugUnitTest lintDebug
BUILD SUCCESSFUL in 37s
365 tests, 0 fallas, 0 errores
scripts/reglas.sh: exit 0
```

```text
./gradlew connectedDebugAndroidTest   (emulator-5554, sdk_gphone64_arm64)
BUILD SUCCESSFUL in 21s
26 tests, 0 fallas, 0 errores
<testsuite name="com.example.parcial_tp3.data.repository.MealRepositoryRoomTest" tests="2" failures="0" errors="0"
  C58_addMeal_guarda_y_devuelve_los_ingredientes
```

## Control negativo

Cada cambio se deshizo después de ver fallar el test.

| Código roto a propósito | Falla |
|---|---|
| `toEntity()` escribe `""` | C53, C54, C55 |
| No se revisa el nombre | C56 |
| El `null` de Gson se toma como lista vacía | C56 |
| Se acepta un elemento `null` | C56 |
| Gramos o kcal en 0 son error al leer | C53, C57 |
| `grams >= 0` en `isValidIngredient` | C59, C62, C71 |
| `kcal > 0` en `isValidIngredient` | C59 |
| `isNotEmpty()` en vez de `isNotBlank()` | C59, C62 |
| 07 nueva ignora `draftIngredients` | C60, C61, C62 |
| Sin borrador se usan los `draftIngredients` | C63 |
| Se guarda sin `copy(ingredients = …)` | C61, C64 |
| 07 no valida los ingredientes | C62 |
| La edición no carga los ingredientes | C62, C64 |
| Duplicate con `ingredients = emptyList()` | C65 |
| Duplicate no valida | C71 |
| `MealRow` sin ingredientes | C66 |
