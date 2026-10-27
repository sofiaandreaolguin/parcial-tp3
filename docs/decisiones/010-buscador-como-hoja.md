# 010 · La búsqueda de alimentos es una hoja, no una pantalla

**Fecha:** 27/10/2026 · **Propuso:** Fiore (EXT02) · **Estado:** propuesta para el grupo

## Contexto

La consigna pone entre las ampliaciones "búsqueda de alimentos y cálculo nutricional"
(`CONSIGNA.md:126`). El botón **"Search Database"** de `Nutrition / 03` y el chip **"Search"** de
`Nutrition / 01` ya están dibujados en el Figma y avisaban modo demostración.

El problema: el Figma tiene **ocho** frames de Nutrition y **ninguno es una pantalla de resultados
de búsqueda**. Hay botón de entrada y hay pantalla de salida (`Nutrition / 07`), pero el paso del
medio no está dibujado. Y UI01 mide maquetación fiel al Figma.

## Opciones

1. Meter el buscador y los resultados **dentro de `Nutrition / 03`**, que sí tiene frame.
2. Una **pantalla nueva** declarada como pantalla sin frame.
3. Una **hoja inferior** sobre la pantalla que la abre.
4. No hacer EXT02.

## Decisión

La **opción 3**: un `ModalBottomSheet` que se abre sobre `Nutrition / 01` o `Nutrition / 03`, que se
siguen viendo detrás. **No es un destino del `NavHost`.**

El inventario **no cambia**: siguen siendo 37 pantallas y 33 destinos.

## Por qué (en criollo)

**Se probó primero como pantalla y se cambió después de verla.** La versión con ruta propia
funcionaba, pero sacaba de contexto: tocabas "Search" y desaparecía todo. Como hoja, no te vas de
donde estabas, y eso es lo que se siente bien para elegir un alimento y volver.

**Y sale más barato de lo que parece.** Siendo una ruta, había que tocar **once archivos** de
conteos —`CONTEXTO.md`, `PANTALLAS.md`, `REQUISITOS.md`, el README, `Rutas.kt`, `MainActivity.kt`,
`PantallaVacia.kt`, `RutasTest.kt`, las fichas 002 y 004 y el `LEEME.md` de Nutrition— porque todos
dicen "33 destinos para 37 pantallas". Eso es lo que se mira para NAV01 y NAV02, que valen 25 puntos
entre los dos, y un inventario a medias resta más de lo que suma una ampliación de ~1 punto. Como
hoja, ese riesgo desaparece entero.

**La 1 rompe lo que UI01 mide.** `Nutrition / 03` hoy coincide con su frame; meterle un buscador,
una lista y un campo de gramos la vuelve otra pantalla.

**La 4 deja el botón mintiendo.** "Search Database" ya está dibujado y la consigna pide la ampliación.

## Cómo se dibuja, ya que no hay frame

- Solo componentes de `ui/components/` y medidas de `VoraTheme`: `VoraTextField`, `VoraButton`,
  `VoraInfoMessage` y `MacroTotalsRow`. **Nada de valores sueltos.**
- **Altura fija al 82%** de la pantalla. Dejándola crecer con el contenido, la lista colapsaba y se
  veían dos alimentos de doce; con altura definida entran los doce y se sigue viendo lo de atrás.
- Los mismos estados que el resto (carga, contenido, vacío, error con reintento), con los componentes
  que ya usan Workout y Nutrition.
- Se controla en 394 dp y en 360 dp, igual que las 37 (UI03).

## Consecuencias

- **Es el primer `ModalBottomSheet` del proyecto.** El único otro elemento flotante es
  `NutritionDatePickerDialog`, que es el calendario de Material. El patrón de montaje es el mismo: la
  Route guarda un booleano y dibuja la hoja cuando está en true.
- **Dos momentos en la misma hoja**, no dos destinos: la lista y el panel de la porción. Con un
  alimento elegido, el atrás del sistema vuelve a la lista en vez de cerrar (`BackHandler`); sin
  alimento elegido, cierra (NAV02).
- `specs/PANTALLAS.md` suma una línea en la tabla de controles de Nutrition, **no una fila de
  pantalla**.
- Si el grupo prefiere la pantalla, lo que cambia es el envoltorio: la capa de datos, el cálculo y el
  ViewModel quedan igual. Lo que vuelve a aparecer es el trabajo en los once archivos.

## Precedente que NO aplica

La ficha 002 (Voice como una pantalla con estados) no sirve de precedente: ahí cinco frames que **sí
existen** comparten una ruta. Acá no hay dibujo que respetar.
