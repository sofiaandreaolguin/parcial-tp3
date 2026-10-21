# Registro de comidas (persistencia)

Flujo: Nutrition
Requisitos: RF03, RF04, RF06, RF08
Pantallas: Nutrition 02 (Today's Meals), 07 (Food Confirmation) y 08 (Meal Details). Esta tarea (VC-h) no dibuja ninguna: deja los datos listos para que las use VC-l.
Responsable: Sofi

## Qué hace
Una comida se guarda en la base del teléfono y se puede volver a leer, filtrada por día. Todavía no hay formulario ni pantalla: esta parte es la base que después usan el listado, el detalle y el total diario.

## Datos
- DTO: ninguno. La consigna prohíbe crear endpoints de comidas (el guardado que se evalúa es el de Room).
- Modelo (`Meal`, en `domain/model/`):
  - `id: Long = 0` (0 = todavía no guardada), `name: String`, `date: LocalDate`, `type: MealType`.
  - `kcal: Int`, `proteinG: Double`, `carbsG: Double`, `fatG: Double`: valores de la porción completa (RF03).
  - `enum class MealType { BREAKFAST, LUNCH, SNACK, DINNER }`: desayuno, almuerzo, merienda y cena.
- Entity (`MealEntity`, tabla `meals`, en `data/local/entity/`):
  - `id: Long` (`@PrimaryKey(autoGenerate = true)`, `Long` porque `meal_details/{mealId}` usa `NavType.LongType`), `name: String`.
  - `date: String` en formato ISO `AAAA-MM-DD` y `type: String` con el nombre del enum, para que la Entity no dependa del dominio y no haga falta un `TypeConverter`.
  - `kcal: Int`, `proteinG`, `carbsG` y `fatG: Double`.
- Decisiones tomadas en la entrevista: `kcal` es `Int`; la fecha se guarda como texto ISO; un tipo desconocido en la base lanza excepción.

## Criterios de aceptación (EARS)

Cada línea es un test. Si no se te ocurre el test, la línea está mal escrita.

- C1. CUANDO se guarda una comida en Room, la app DEBE poder leerla por su fecha con los mismos datos que se guardaron (RF04).
- C2. La app DEBE listar solo las comidas de la fecha pedida y ninguna de otras fechas (RF04).
- C3. CUANDO se guardan dos comidas nuevas, cada una DEBE recibir un id distinto y mayor que cero (NAV04).
- C4. La conversión `Meal` → `MealEntity` → `Meal` DEBE conservar todos los campos (RF06).
- C5. La app DEBE guardar la fecha como texto ISO `AAAA-MM-DD` y el tipo con el nombre del enum (RF06).
- C6. CUANDO se piden las comidas de una fecha, el repositorio DEBE devolver modelos `Meal` de esa fecha, no Entity (RF06).
- C7. CUANDO se agrega una comida por el repositorio, DEBE llegar al DAO como Entity y el repositorio DEBE devolver el id nuevo (RF04, RF06).
- C8. SI la tabla tiene un tipo que no es ninguno de los cuatro del enum, ENTONCES la conversión a `Meal` DEBE fallar con `IllegalArgumentException` y no inventar un tipo (RF06).
- C9. SI no hay comidas guardadas para la fecha pedida, ENTONCES la consulta DEBE devolver una lista vacía (RF04).

Plantillas: **La app DEBE** (siempre) · **CUANDO** (ante un evento) · **MIENTRAS** (durante un estado) · **SI ... ENTONCES** (algo sale mal) · **DONDE** (opcional, para ampliaciones).

## Fuera de alcance
- Validación de nombre no vacío y de valores no negativos (RF03): va en VC-l, con el formulario.
- Eliminar con confirmación y total diario (RF04): VC-l.
- "Los registros siguen al cerrar y reabrir la app": se prueba a mano en VC-l, cuando haya formulario.
- ViewModel, pantallas y strings (RF07): VC-l. No se toca `ui/`, `MainActivity` ni la navegación.
- Editar una comida existente (`Edit Meal`) y buscar alimentos.
- Migración de la base: `version = 2` con el fallback destructivo ya existente (decisión 003). Solo se pierde la caché de entrenamientos del emulador.
- Que Hilt entregue de verdad el repositorio y el DAO: lo comprueba `./gradlew assembleDebug` (Hilt y Room validan el grafo al compilar), no un test de esta spec (RF08).

## Tests
| Criterio | Test | Dónde | Vi el rojo |
|---|---|---|---|
| C1 | `C1_insert_guarda_y_observeByDate_lee_la_comida` | `androidTest/` · `MealDaoTest` | no |
| C2 | `C2_observeByDate_lista_solo_las_comidas_de_esa_fecha` | `androidTest/` · `MealDaoTest` | no |
| C3 | `C3_dos_inserts_devuelven_ids_distintos_y_mayores_que_cero` | `androidTest/` · `MealDaoTest` | no |
| C4 | `` `C4 Meal pasa a Entity y vuelve igual` `` | `test/` · `MealMappersTest` | no |
| C5 | `` `C5 la Entity guarda la fecha ISO y el tipo con el nombre del enum` `` | `test/` · `MealMappersTest` | no |
| C6 | `` `C6 observeMealsByDate devuelve modelos Meal de esa fecha` `` | `test/` · `MealRepositoryImplTest` | no |
| C7 | `` `C7 addMeal manda una Entity al DAO y devuelve el id nuevo` `` | `test/` · `MealRepositoryImplTest` | no |
| C8 | `` `C8 un tipo desconocido en la Entity lanza IllegalArgumentException` `` | `test/` · `MealMappersTest` | no |
| C9 | `C9_observeByDate_sin_comidas_devuelve_lista_vacia` | `androidTest/` · `MealDaoTest` | no |

Los nombres de `androidTest/` van con guion bajo y sin espacios (con `minSdk` 29 el DEX no acepta espacios).

## Auditoría
- auditor-spec: (pendiente)

## Historial
- 2026-10-21. Spec creada para VC-h. Se sumaron C8 (tipo desconocido) y C9 (fecha sin comidas) a los siete criterios del plan.
