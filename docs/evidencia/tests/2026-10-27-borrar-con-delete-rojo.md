# Borrar comidas con @Delete · rojo (27/10/2026)

Refactor de RF04 y RF06 en la rama `refactor/nutrition-borrar-con-delete`. Spec: `specs/funcionales/carga-y-eliminacion-de-comidas.md`, criterios C11, C12, C14, C45, C72, C73 y C74.

## Unitarios

```text
./gradlew testDebugUnitTest
367 tests completed, 4 failed
MealRepositoryImplTest > C73 deleteMeal no falla si no habia nada que borrar FAILED
  expected:<[MealEntity(id=4, ...)]> but was:<[]>
MealRepositoryImplTest > C14 updateMeal y deleteMeal llegan al DAO con el mismo id FAILED
  expected:<[MealEntity(id=4, ...)]> but was:<[]>
MealDetailViewModelTest > C45 confirmar borra una vez y emite Deleted FAILED
  expected:<[Meal(id=7, name=Avena con fruta, ...)]> but was:<[Meal(id=7, name=, date=-999999999-01-01, ...)]>
MealDetailViewModelTest > C74 sin Content confirmar no borra FAILED
  no debería borrar: [7, 99]
```

C43, C46, C47 y C48 siguen en verde sin cambios: `FakeMealRepository.eliminadas` sigue anotando ids.

## Con emulador

```text
./gradlew connectedDebugAndroidTest   (emulator-5554)
BUILD SUCCESSFUL · MealDaoTest tests="9" failures="0"
```

C11, C12 y C72 pasan desde el principio porque Room genera `@Delete`.

Control negativo: `@Update` en lugar de `@Delete` en `MealDao.delete` (misma firma; devuelve 1 pero no borra), y se volvió atrás.

```text
./gradlew connectedDebugAndroidTest -P...class=com.example.parcial_tp3.data.local.MealDaoTest
MealDaoTest tests="9" failures="3"
C11_delete_saca_la_comida_y_devuelve_las_filas_borradas -> expected:<[MealEntity(id=2, name=Pollo con arroz, ...)]> but was: ...
C12_observeById_emite_la_comida_y_null_al_borrarla -> expected null, but was:<MealEntity(id=1, ...)>
C72_delete_borra_por_id_aunque_cambien_los_otros_valores -> AssertionError (la tabla no quedó vacía)
```
