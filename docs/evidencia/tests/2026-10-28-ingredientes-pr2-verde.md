# PR 2 de ingredientes: tests en verde (2026-10-28)

Rama `feat/nutrition-ingredientes-carga`. Spec `specs/funcionales/carga-y-eliminacion-de-comidas.md` (PR 2: C75 a C102 y los reescritos).

## Rojo
`05848ba` (tests en rojo): fallaban 25 de 380 en `test/`, todos por `AssertionError`: C75 a C90, C101, C102 y los reescritos C29, C31, C34, C35, C38, C39 y C31 de la búsqueda.

## Verde
- `./gradlew testDebugUnitTest`: **380 tests, 0 fallas, 0 errores**. Solo se sacaron de los tests los `override` provisorios de `FakeMealDao.update` y `FakeMealRepository.updateMeal`, que dejaron de existir en el contrato.
- `./gradlew connectedDebugAndroidTest` (emulador Pixel 9, API 36): **26 tests, 0 fallas**.
  - La primera corrida falló en `FoodSearchGuardadoRoomTest.c33_el_total_del_dia_suma_lo_que_guardo_la_busqueda` con `IllegalStateException: el pop-up no dejo seguir a 07`. Causa: el test pasaba a la segunda comida sin esperar a que Room terminara de guardar la primera, y C102 no deja abrir 07 mientras se guarda.
  - Se confirmó con una espera temporal (los dos tests pasaron) y se deshizo. Con permiso de Sofi, el test espera `FoodCaptureEvent.Saved` después de `onSave()`. Lo que comprueba no cambia.
- `assembleDebug`, `compileDebugAndroidTestKotlin`, `lintDebug` y `scripts/reglas.sh`: en verde.
- C95: `grep -nE "hiltViewModel|import .*ViewModel" NutritionNavGraph.kt` sin resultados.

## Control negativo (tests que pasaban con las firmas vacías)
Se rompió el código a propósito, se vio fallar el test con `AssertionError` y se deshizo:
- C63: usar `draftIngredients` sin borrador.
- C91: dejar editar los ingredientes mientras se guarda.
- C102 (la mitad de `onOpenConfirmation`): reemplazar la confirmación mientras se guarda.
