> **Base: `main`.** EXT01 ya entró por el PR #58, así que esta rama sale de `main` limpia.
> `gh pr create --base main --head feat/nutrition-busqueda-alimentos`

## Qué cambia

El botón **"Search Database"** de `Nutrition / 03` y el chip **"Search"** de `Nutrition / 01` dejan de
avisar modo demostración: abren una hoja donde se busca un alimento contra el servidor del docente, se
elige, se dicen los gramos, la app calcula los nutrientes de esa porción y deja cargado el formulario
de `Nutrition / 07`, que guarda en Room por el camino de siempre y actualiza el total del día.

## Carril y requisitos

- Carril: **C datos** (spec → tests en rojo → implementar) y después **B pantalla**.
- Requisitos: **EXT02**. Se apoya en RF03, RF04, RF05, RF06, RF07 y RF08, y respeta NAV02, NAV03 y UI01.
- Spec: `specs/funcionales/busqueda-de-alimentos.md`, 38 criterios.
- **El inventario no cambia.** La búsqueda es una hoja, no un destino: siguen siendo 37 pantallas y 33
  rutas, y `Rutas.kt`, `RutasTest`, `CONTEXTO.md`, `MainActivity.kt` y las fichas 002 y 004 **no se
  tocan**. Decisión y motivos: `docs/decisiones/010-buscador-como-hoja.md`.

## Cómo probarlo

1. En `local.properties`, `api.baseUrl=https://tp3.martinrivas.ar/` (el servidor publicado; no hay que
   levantar nada).
2. Hace falta poder iniciar sesión: con la plantilla `google-services.json.example` la app **compila
   pero no deja entrar**. Para probar esto hay que poner un `app/google-services.json` real.
3. Nutrition → chip **"Search"** (o "+ Add" → **"Search Database"**). Se abre la hoja con los 12
   alimentos y el Dashboard se sigue viendo atrás.
4. Escribir "pollo" de corrido: dispara **una sola** consulta, no una por letra.
5. "POLLO" en mayúsculas y "man" también encuentran: el servidor ignora mayúsculas y busca por parte
   del nombre. Borrar el texto vuelve a mostrar los doce.
6. "zzzz" → "No se encontró ningún alimento con ese nombre". Es el estado vacío, no el de error.
7. Tocar **Pollo cocido** y poner **150**: tienen que salir **248 kcal, 46,5 / 0 / 5,4**.
8. Poner 0, "abc" o 99999: cada uno con su aviso distinto.
9. Flecha atrás desde la porción: vuelve a los resultados sin perderlos. Atrás otra vez: cierra la hoja.
10. **"Use this food"** → `Nutrition / 07` con todo cargado y **editable**. Elegir el tipo de comida y
    guardar: el total del día cambia.
11. Guardar dos comidas del mismo tipo: son **dos filas**, no se pisan.
12. Apagar el wifi del emulador y buscar: aviso con **Reintentar**.

## Evidencia

- **351 tests unitarios, 0 fallos** (`./gradlew testDebugUnitTest`), 42 de EXT02. Los 37 se vieron en
  rojo antes de implementar, **todos por el resultado** (`expected ... but was ...`), ninguno por
  excepción ni por compilación, y **ninguno pasó en rojo**.
- **25 tests con emulador, 0 fallos** (`./gradlew connectedDebugAndroidTest`): los 21 que ya había más
  los 4 de EXT02. C33 recorre la cadena entera con Room en memoria.
- `./gradlew assembleDebug` en verde y `./scripts/reglas.sh` sin hallazgos.
- **Control negativo: 21 roturas a propósito, las 21 detectadas.** Está la tabla completa en la spec.
  **Tres tests se agregaron justamente porque no detectaban nada:** sacar el debounce no rompía ningún
  test (cancelar el job anterior ya dejaba una sola consulta: había que probar que **espera**), mirar
  `data.isEmpty()` en vez de `meta.total` tampoco, y faltaba la carrera de verdad (la respuesta vieja
  que llega tarde).
- Capturas en `docs/evidencia/busqueda/`: resultados y porción en **411 y 360 dp** (UI03), estado
  vacío, y la hoja en **modo oscuro**.
- Revisores: `auditor-spec` dos veces sobre la spec (15 + 15 hallazgos) y `revisor-arquitectura` sobre
  el código.

## Simulado o pendiente

- **Nada simulado.** Los datos son del servidor del docente y el guardado es Room de verdad.
- **Sin paginación, a propósito.** `METODOLOGIA.md` la pedía; el catálogo tiene 12 alimentos y el
  servidor acepta `pageSize` hasta 50, así que entran todos en una consulta y nunca se dispararía. La
  consigna no la pide. Queda tachado y explicado en METODOLOGIA.
- **Dos cambios chicos en zona de Sofi**, los dos en `FoodCaptureViewModel.kt`: el método nuevo
  `onPrefill(form)` —que además **limpia el borrador**, porque si no, después de probar la foto, 07
  mostraría Chicken / Rice / Vegetables debajo de "Pollo cocido"— y `toFormText` pasando a `internal`.
  Esto último **no es cosmético**: `parseKcal` valida con `^-?\d+$`, así que precargar "248.0" hacía
  que 07 marcara `NOT_A_WHOLE_NUMBER` y no dejara guardar.
- **Un endpoint más en `VoraApi`**, avisado a Hector. **No sube la versión de la base.**
- **No se puede cambiar la porción desde `Nutrition / 07`**: ahí se editan los valores, no los gramos.
  Si la porción estaba mal hay que volver a buscar el alimento. Agregarlo obliga a tocar `MealForm`.
- Sugerencias de la revisión de arquitectura **no aplicadas** (ninguna bloquea): usar `VoraTopBar` y
  `VoraListRow` en vez del encabezado y la fila propios; deshabilitar "Use this food" cuando no hay
  porción; mover `toFormText` a `domain/MealForm.kt` en vez de ampliarle la visibilidad; guardar en el
  estado un `@StringRes` ya resuelto en vez del `AppError`; y previews de los estados que faltan.

## Uso de IA

- **Propuso la pantalla, y estaba mal.** La primera versión era una ruta nueva declarada como "pantalla
  sin frame". Funcionaba, pero al probarla a mano sacaba de contexto, y además obligaba a tocar **once
  archivos** de conteos de inventario, que es lo que se mira para NAV01 y NAV02. Fiore pidió la hoja
  después de verla andando; con eso el inventario queda intacto.
- **Fijó un número equivocado como criterio.** El criterio del redondeo pedía 25,3 para 150 g de avena,
  que es el artefacto de coma flotante; el valor correcto es 25,4. Lo encontró la segunda auditoría.
- **Dejó una rotura del control negativo pegada en el código** (`HALF_EVEN` en las calorías) y **los
  347 tests pasaban igual**, porque el ejemplo elegido —247,5— da 248 con las dos reglas de redondeo.
  Hizo falta un caso que las separe: avena 50 g = 194,5, que da 195 con HALF_UP y 194 con HALF_EVEN.
- **Midió mal el control negativo dos veces:** leyendo solo stdout cuando Gradle escribe el resultado
  de los tests por stderr, y tomando "BUILD SUCCESSFUL" por bueno cuando la tarea quedaba UP-TO-DATE
  sin correr nada.
- **El color lila de la hoja lo encontró Fiore probando**, no la IA ni los tests.
- No se modificó ningún test para que pasara. Uno propio estaba mal y **se corrigió, no se borró**:
  esperaba que "1,000" fuera un error, cuando la regla que ya usa `MealValidation` lo lee como **un**
  gramo. Quedó como criterio con el comportamiento real, porque sorprende.
