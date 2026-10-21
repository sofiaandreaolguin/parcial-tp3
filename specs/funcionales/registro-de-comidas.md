# Registro de comidas (persistencia)

Flujo: Nutrition
Requisitos: RF03 (parcial: el modelo, sin validación), RF04 (parcial: guardar y listar, sin eliminar ni total), RF06, RF08 (parcial: el cableado de Hilt, sin ViewModel)
Pantallas: Nutrition 02 (Today's Meals), 07 (Food Confirmation) y 08 (Meal Details). Esta tarea (VC-h) no dibuja ninguna: deja los datos listos para que las use VC-l.
Responsable: Sofi

## Qué hace
Una comida se guarda en la base del teléfono y se puede volver a leer, filtrada por día. Todavía no hay formulario ni pantalla: esta parte es la base que después usan el listado, el detalle y el total diario. Cada comida guardada recibe un id propio, que después viaja en `meal_details/{mealId}` (NAV04).

## Datos
- DTO: ninguno. La consigna prohíbe crear endpoints de comidas (el guardado que se evalúa es el de Room).
- Modelo (`Meal`, en `domain/model/Meal.kt`):
  - `id: Long = 0` (0 = todavía no guardada), `name: String`, `date: LocalDate`, `type: MealType`.
  - `kcal: Int`, `proteinG: Double`, `carbsG: Double`, `fatG: Double`: valores de la porción completa (RF03).
  - `enum class MealType { BREAKFAST, LUNCH, SNACK, DINNER }`: desayuno, almuerzo, merienda y cena.
- Entity (`MealEntity`, tabla `meals`, en `data/local/entity/MealEntity.kt`):
  - `id: Long` (`@PrimaryKey(autoGenerate = true)`, `Long` porque `meal_details/{mealId}` usa `NavType.LongType`), `name: String`.
  - `date: String` en formato ISO `AAAA-MM-DD` y `type: String` con el nombre del enum, para que la Entity no dependa del dominio y no haga falta un `TypeConverter`.
  - `kcal: Int`, `proteinG`, `carbsG` y `fatG: Double`.
- Decisiones tomadas en la entrevista: `kcal` es `Int`; la fecha se guarda como texto ISO; un tipo desconocido en la base lanza excepción.

## Contrato

| Pieza | Archivo | Firma |
|---|---|---|
| DAO | `data/local/dao/MealDao.kt` | `@Dao interface MealDao` con `@Insert suspend fun insert(meal: MealEntity): Long` (estrategia por defecto, ABORT) y `@Query("SELECT * FROM meals WHERE date = :date ORDER BY id") fun observeByDate(date: String): Flow<List<MealEntity>>` |
| Mappers | `data/repository/MealMappers.kt` | `fun Meal.toEntity(): MealEntity` y `fun MealEntity.toDomain(): Meal` |
| Repositorio (interfaz) | `data/repository/MealRepository.kt` | `fun observeMealsByDate(date: LocalDate): Flow<List<Meal>>` y `suspend fun addMeal(meal: Meal): Long` |
| Repositorio (implementación) | `data/repository/MealRepositoryImpl.kt` | `class MealRepositoryImpl @Inject constructor(private val dao: MealDao) : MealRepository` |
| Base | `data/local/VoraDatabase.kt` | suma `MealEntity::class`, `abstract fun mealDao(): MealDao` y `version = 2` |
| Hilt | `di/DatabaseModule.kt` y `di/RepositoryModule.kt` | `@Provides fun provideMealDao(...)` y `@Binds @Singleton abstract fun bindMealRepository(impl: MealRepositoryImpl): MealRepository` |

`MealDao` es una interfaz para poder hacer un `FakeMealDao` en los tests del repositorio.

## Criterios de aceptación (EARS)

Cada línea es un test. Si no se te ocurre el test, la línea está mal escrita.

- C1. CUANDO se guarda una comida en Room, la app DEBE poder leerla por su fecha con los mismos `name`, `date`, `type`, `kcal`, `proteinG`, `carbsG` y `fatG`, y con `id` igual al que devolvió el insert (RF04).
- C2. La app DEBE listar solo las comidas de la fecha pedida y ninguna de otras fechas, ordenadas por `id` ascendente (RF04).
- C3. CUANDO se guardan dos comidas nuevas, cada una DEBE recibir un id distinto y mayor que cero (RF04).
- C4. La conversión `Meal` → `MealEntity` → `Meal` DEBE conservar los 8 campos, incluido `id` (RF06).
- C5. La app DEBE guardar la fecha como texto ISO `AAAA-MM-DD` y el tipo con el nombre del enum (RF06).
- C6. CUANDO se piden las comidas de una fecha, el repositorio DEBE pedirle al DAO esa fecha como texto ISO y devolver modelos `Meal` con el contenido de las Entity recibidas, no Entity (RF06).
- C7. CUANDO se agrega una comida por el repositorio, DEBE llegar al DAO como Entity con los 8 campos mapeados y el repositorio DEBE devolver el id nuevo (RF04, RF06).
- C8. SI la tabla tiene un tipo que no es ninguno de los cuatro del enum, ENTONCES la conversión a `Meal` DEBE fallar con `IllegalArgumentException` y no inventar un tipo (RF06).
- C9. SI no hay comidas guardadas para la fecha pedida, ENTONCES la consulta DEBE devolver una lista vacía (RF04).
- C10. MIENTRAS se observa una fecha, CUANDO se guarda una comida de esa fecha, el DAO DEBE emitir una lista nueva que la incluye (RF04: es lo que hace cambiar el total diario en VC-l).
- C11. SI la tabla tiene una fecha que no es ISO, ENTONCES la conversión a `Meal` DEBE fallar con `DateTimeParseException` (RF06).

Plantillas: **La app DEBE** (siempre) · **CUANDO** (ante un evento) · **MIENTRAS** (durante un estado) · **SI ... ENTONCES** (algo sale mal) · **DONDE** (opcional, para ampliaciones).

Casos de la plantilla que no aplican: sin conexión, 404, 503 y JSON inválido (es todo local, sin servidor). La rotación va en VC-l, con el ViewModel.

## Fuera de alcance
- Validación de nombre no vacío y de valores no negativos (RF03): va en VC-l, con el formulario. La capa de datos **no valida**: guarda lo que recibe (por ejemplo `kcal = -1`).
- Eliminar con confirmación y total diario (RF04): VC-l.
- "Los registros siguen al cerrar y reabrir la app": se prueba a mano en VC-l, cuando haya formulario.
- ViewModel, pantallas y strings (RF07): VC-l. No se toca `ui/`, `MainActivity` ni la navegación.
- Traducir los errores de datos (tipo o fecha inválidos) a un error propio en el repositorio, como pide CONTEXTO §2: se decide en VC-l junto con el estado `Error` del ViewModel. En VC-h el mapper lanza la excepción (C8, C11).
- Errores de escritura de SQLite (disco lleno, base cerrada): sin criterio en VC-h; los trata VC-l junto con los demás errores.
- Insertar una `Meal` con un `id` que ya existe: el `@Insert` usa ABORT y falla. Editar una comida (`Edit Meal`) y buscar alimentos no están en esta tarea.
- Migración de la base: `version = 2` con el fallback destructivo ya existente (decisión 003). Hoy solo se pierde la caché de entrenamientos del emulador; **antes de la entrega no se vuelve a subir la versión sin avisar a Sofi**, porque borraría las comidas guardadas.
- Que Hilt entregue de verdad el DAO y el repositorio (RF08): `assembleDebug` solo confirma que los módulos compilan, porque Hilt valida únicamente lo que alguien pide y todavía ningún ViewModel pide `MealRepository`. La inyección efectiva se verifica en VC-l.

## Tests
| Criterio | Test | Dónde | Vi el rojo |
|---|---|---|---|
| C1 | `C1_insert_guarda_y_observeByDate_lee_la_comida` | `androidTest/` · `MealDaoTest` | no |
| C2 | `C2_observeByDate_lista_solo_las_comidas_de_esa_fecha_ordenadas_por_id` | `androidTest/` · `MealDaoTest` | no |
| C3 | `C3_dos_inserts_devuelven_ids_distintos_y_mayores_que_cero` | `androidTest/` · `MealDaoTest` | no |
| C4 | `` `C4 Meal pasa a Entity y vuelve igual` `` | `test/` · `MealMappersTest` | no |
| C5 | `` `C5 la Entity guarda la fecha ISO y el tipo con el nombre del enum` `` | `test/` · `MealMappersTest` | no |
| C6 | `` `C6 observeMealsByDate pide la fecha ISO al DAO y devuelve modelos Meal` `` | `test/` · `MealRepositoryImplTest` | no |
| C7 | `` `C7 addMeal manda una Entity al DAO y devuelve el id nuevo` `` | `test/` · `MealRepositoryImplTest` | no |
| C8 | `` `C8 un tipo desconocido en la Entity lanza IllegalArgumentException` `` | `test/` · `MealMappersTest` | no |
| C9 | `C9_observeByDate_sin_comidas_devuelve_lista_vacia` | `androidTest/` · `MealDaoTest` | no |
| C10 | `C10_observeByDate_emite_la_lista_nueva_cuando_se_inserta_una_comida` | `androidTest/` · `MealDaoTest` | no |
| C11 | `` `C11 una fecha no ISO en la Entity lanza DateTimeParseException` `` | `test/` · `MealMappersTest` | no |

- Los nombres de `androidTest/` van con guion bajo y sin espacios (con `minSdk` 29 el DEX no acepta espacios).
- `control-rapido.sh` solo corre `testDebugUnitTest`: los tests de `androidTest/` (C1, C2, C3, C9, C10) se corren a mano con `./gradlew connectedDebugAndroidTest` y el emulador abierto, y la salida se guarda en `docs/evidencia/tests/` y se pega en el PR.

## Auditoría
- auditor-spec (2026-10-21): 15 hallazgos, ninguno rompe una condición obligatoria de la consigna.
  - Corregidos en la spec: #1 (la afirmación sobre `assembleDebug` y Hilt era falsa; ahora el cableado es entregable y la inyección efectiva queda para VC-l), #3 (sección "Contrato"), #4 (C10, reactividad), #5 (C1 y C4 con `id`), #6 (orden en C2), #8 (C11), #9 (casos que no aplican y errores de escritura), #10 (ABORT), #11 (la capa no valida), #12 (cómo se corren y registran los `androidTest`), #13 (C6 y C7 más precisos), #14 (C3 sin NAV04), #15 (requisitos parciales en la cabecera) y #7 (aviso sobre la versión de la base).
  - Decidido por Sofi: #2, la traducción a error propio de datos inválidos se deja para VC-l.
  - Pendiente para `cerrar-tarea`: no pasar RF03 y RF04 a "hecho" en `specs/REQUISITOS.md` hasta VC-l.

## Historial
- 2026-10-21. Spec creada para VC-h. Se sumaron C8 (tipo desconocido) y C9 (fecha sin comidas) a los siete criterios del plan.
- 2026-10-21. Tras la auditoría: se agregan la sección "Contrato", C10 (el listado se actualiza al insertar) y C11 (fecha no ISO); se precisan C1, C2, C3, C4, C6 y C7, y se corrige lo que decía la spec sobre la verificación de Hilt.
