# 003 · Una base Room compartida, armada por la base del proyecto

**Fecha:** 19/10/2026 · **Propuso:** Hector (tarea VC-f, proyecto base) · **Estado:** propuesta, a confirmar con Sofi (responsable de la base Room)

## Contexto
La tarea VC-f pide "módulos que proveen Retrofit, la base Room y los DAO". La base Room es zona de Sofi (`CLAUDE.md`), y la Entity y el DAO de comidas son su tarea VC-h. Además, Room no compila una `@Database` sin al menos una Entity. Había que decidir quién crea la base y con qué tablas, sin pisar el trabajo de Sofi.

## Opciones
1. VC-f crea `VoraDatabase` y `DatabaseModule` con solo la tabla de entrenamientos (`WorkoutEntity` y `WorkoutDao` mínimos, zona de Hector). Sofi suma las comidas en VC-h.
2. VC-f crea también `MealEntity` y `MealDao`.
3. VC-f solo arma la red, y la base queda entera para Sofi.

## Decisión
La opción 1. No se crea nada de comidas.

Para la tarea h, Sofi suma en archivos de esta tarea:
- `MealEntity::class` en `@Database(entities = …)` de `data/local/VoraDatabase.kt`, `abstract fun mealDao(): MealDao` y `version = 2`.
- Un `@Provides fun provideMealDao(db: VoraDatabase): MealDao = db.mealDao()` en `di/DatabaseModule.kt`.

## Por qué (en criollo)
**Una sola base para toda la app** (`CONTEXTO.md` §2). Si cada flujo armara la suya, habría dos archivos de base y dos `@Provides` que compiten. La opción 2 le quitaba a Sofi su tarea h, y la 3 dejaba la tarea f sin cumplir lo que pide el tablero.

**La tabla de entrenamientos es de Hector** (caché de RF02, tarea j), así que la base nace con ella sin invadir nada.

## Consecuencias
- **`fallbackToDestructiveMigration(dropAllTables = true)` es solo para el desarrollo.** Cuando Sofi suba la versión, los emuladores que ya tienen la base la borran y la crean de nuevo en vez de crashear. Esto choca con "un fallo nunca borra los registros locales" (`CONTEXTO.md` §2) cuando ya haya comidas cargadas. **Antes de la entrega** hay que congelar la versión del esquema o escribir una `Migration` real y sacar el fallback.
- `exportSchema = false` por ahora. Si se escriben migraciones, conviene activarlo con `room { schemaDirectory(...) }`.
- La explicación para la devolución (RF08): Hilt crea una sola `VoraDatabase` (`@Singleton` en `SingletonComponent`), y cada repositorio recibe por constructor el DAO que necesita, nunca la base entera.
