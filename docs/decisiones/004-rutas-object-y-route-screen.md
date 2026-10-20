# 004 · Rutas como `object` con constantes, y el ViewModel en `XxxRoute`

**Fecha:** 20/10/2026 · **Propuso:** Fiore (tarea VC-e, navegación) · **Estado:** propuesta, documenta una decisión ya tomada e integrada en `main` (PR #2)

## Contexto

Los proyectos del docente (`ar.edu.ort.*` en `ORT-Argentina/kotlin-compose`) resuelven dos cosas distinto a como quedaron en VORA. Las dos están en la zona de Navegación y hay que poder justificarlas en la devolución y en el cuestionario de entrega, que pregunta por arquitectura y por decisiones al escalar.

| Decisión | Ejemplos del docente | VORA |
|---|---|---|
| Declaración de rutas | `sealed class` con `data object` (`Quote`, `BootShop`, `Challenge1`) | `object Rutas` con `const val` |
| Dónde se pide el ViewModel | `hiltViewModel()` dentro del `composable` del NavHost (`Quote`) | dentro del `XxxRoute` (como `BootShop`) |

La consigna no prescribe ninguna de las dos formas: los requisitos de navegación son NAV01 a NAV05 (cobertura, recorridos y regreso, acciones visibles, selección coherente, permisos) y la tabla de evaluación mide "todas las pantallas del inventario, acciones, regreso y selección coherente", que es comportamiento. RF06, RF07 y RF08 hablan de DTO, modelo y Entity, ViewModel e Hilt, no de rutas.

## Opciones

1. Dejar `object Rutas` con constantes y documentar el porqué.
2. Migrar a `sealed class` con `data object`, como los ejemplos del docente.
3. Migrar solo las rutas sin argumentos y dejar las otras como constantes (mezcla).

## Decisión

La opción 1. Se deja como está y se documenta.

## Por qué (en criollo)

**Son 33 destinos y varios llevan argumentos** (`workout_details/{workoutId}`). Hacen falta constructores (`Rutas.workoutDetails(id)`) para armar la ruta con el id puesto.

**El `Scaffold` de `MainActivity` decide la barra comparando contra `destination.route`, que es un `String`.** Por eso existen `RUTAS_CON_BARRA` y `RUTAS_ENCIMA` como conjuntos de rutas. Con `sealed class` habría que sacarle `.route` a cada destino igual, y esos `Set` quedarían idénticos: la `sealed class` no ahorra ese paso, lo agrega.

**Está cubierto por tests.** `RutasTest.kt` prueba las rutas y los conjuntos.

**`XxxRoute` separado deja la `XxxScreen` stateless y previsualizable**, que es lo que permite la comparación con Figma de UI01 (40 puntos) y las cinco capturas del README. Esto no es una desviación: es lo que hace `BootShop`, del propio docente. La diferencia es solo con `Quote`.

## Consecuencias

- **`sealed class` es hoy lo más idiomático** y da chequeo del compilador en lugar de strings sueltos. Si el proyecto siguiera después de la entrega, es la migración natural.
- **El costo de migrar ahora:** 9 archivos la usan (`MainActivity` y los 7 grafos, más `Rutas.kt`), con unas 91 referencias en total; `Rutas.kt` son 159 líneas; habría que reescribir `RutasTest.kt` y los dos `Set`. Es la parte que vale 25 puntos y hoy funciona y está mergeada.
- **Para la devolución:** la respuesta no es "así salía en el ejemplo", es la de arriba. Que haya una decisión tomada con un motivo y un test que la cubre es lo que se evalúa en "arquitectura" y "mejoras al escalar".

## Por verificar

`specs/CONTEXTO.md` dice que "el enunciado dice que la estructura puede variar mientras se pueda justificar". Esa frase **no aparece en `specs/CONSIGNA.md`**. Puede ser algo que el docente dijo en clase y quedó como paráfrasis. Antes de usarla como argumento en la devolución, confirmar de dónde salió, o apoyarse solo en lo que sí está escrito: la consigna no prescribe la forma de las rutas, y la evaluación de navegación mide comportamiento.
