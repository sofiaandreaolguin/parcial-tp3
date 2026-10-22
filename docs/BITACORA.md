# Bitácora

Una entrada por tarea, en orden de fecha. La agrega la IA al cerrar cada tarea (skill `cerrar-tarea`) y la revisa quien hizo la tarea. Es la "data de cómo llegamos al resultado" que pidió el profe.

Formato:

```
### AAAA-MM-DD · Quién · tipo(alcance): descripción
- Carril y requisitos: (A/B/C/D) · (UI, NAV, RF)
- Plan aprobado: (resumen en dos líneas)
- Qué propuso la IA y qué cambiamos: (lo más importante de cada entrada)
- Revisores: (cuáles corrieron, qué bloqueaba y cómo se resolvió)
- Evidencia: (archivos en docs/evidencia/, commit del test en rojo)
- Commit / PR: (#número)
```

---

### 2026-10-18 · Fiore · chore: armar la metodología de trabajo con IA en el repo
- Carril y requisitos: A (configuración, sin código de la app) · organización y entrega en GitHub
- Plan aprobado: llevar al repo la metodología del grupo (sección 6) para que la usen las cinco: `CLAUDE.md`, `specs/` (contexto, consigna, API, requisitos, plantilla), subagentes revisores, una skill por paso del ciclo, control automático, hook de commits, CI y plantilla de pull request.
- Qué propuso la IA y qué cambiamos: la IA propuso cuatro carriles según el tamaño del cambio (chico, pantalla, datos, bug) y siete ajustes a la metodología (decisión 001). Fiore pidió que la IA armara solo la metodología y no el código de la app, y que la IA nunca commitee: se agregó además un bloqueo de `git commit` y `git push` en `.claude/settings.json`.
- Revisores: controles negativos de los scripts (ver decisión 001).
- Evidencia: guía "Cómo trabajamos VORA" (artifact compartido con el grupo).
- Commit / PR: [#1](https://github.com/sofiaandreaolguin/parcial-tp3/pull/1)

### 2026-10-19 · Hector · feat(di): proveer Retrofit y Room en módulos de Hilt
- Carril y requisitos: C (sin spec EARS: criterios C1 a C7 en el plan) · RF08, RF06, RF05 · tarea VC-f
- Plan aprobado: `NetworkModule` (URL de `local.properties`, logging, interceptor de escenarios solo en debug, OkHttp, Retrofit con Gson, `VoraApi` con `/health`) y `DatabaseModule` (`VoraDatabase` con solo `WorkoutEntity` y `WorkoutDao`). HTTP sin cifrar solo en debug. Tests primero.
- Qué propuso la IA y qué cambiamos: la IA propuso tres formas de armar la base Room. Hector pidió revisar si se pisaba con la tarea h de Sofi y eligió la base con solo Workout, sin tocar nada de comidas (decisión 003). La IA propuso la rama `feat/di-proyecto-base`; Hector pidió un nombre más claro y quedó `feat/di-hilt-retrofit-room`. La rama salió primero de un main local 13 commits atrás: la IA lo detectó antes de escribir código y Hector la recreó desde el main actualizado. La IA había puesto comentarios al final de las líneas de `local.properties` en el README, y los corrigió porque el `#` entra en el valor.
- Revisores: revisor-arquitectura y revisor-requisitos, sin bloqueos. ALTA: C6 y C7 (Room) sin correr; se corrieron después en el emulador, en verde y con dos controles negativos. Se corrigieron la ficha 003 que faltaba y una línea vieja de `CONTEXTO.md` §7. Quedan como sugerencias: validar `api.mockDelayMs`, URL publicada por defecto en release, `@Qualifier` en lugar de `@Named`, DTO con campos que admitan nulos (tarea g), `replaceAll` en el DAO (tarea g). /code-review no corrió.
- Evidencia: `docs/evidencia/tests/2026-10-19-vc-f-rojo.md` y `2026-10-19-vc-f-verde.md` (11 de 11 en verde, control negativo de C4 y C5). Commit del rojo: `3434d89`.

### 2026-10-20 · Martu · feat(tema): armar el tema de VORA, la barra inferior y el encabezado
- Carril y requisitos: B · UI01, UI02 (y apoya NAV02 con volver y cerrar)
- Plan aprobado: VC-d partida en tres PR. Este es el primero, para el hito 1: tema (colores, Manrope, espaciado, radios), `VoraBottomBar` definitiva con la misma firma y `VoraTopBar` nuevo. Después vienen botones, tarjetas, campos y chips (PR 2), y los indicadores y controles (PR 3).
- Qué propuso la IA y qué cambiamos:
  - Para los íconos, la IA recomendó exportar SVG del Figma. Martu primero eligió íconos de Material parecidos y después volvió a los SVG para que sean más fieles.
  - La IA dejó el tema solo en modo claro y sin color dinámico, mantuvo el nombre `Parcialtp3Theme` para no tocar `MainActivity` y sumó `VoraTheme.colors`, `spacing`, `radius` y `sizes` para lo que Material 3 no tiene.
  - Al mirar los frames, la IA cambió dos cosas del plan: el título del encabezado va chico y centrado, y la barra no lleva línea indicadora (la raya del Figma es la del sistema).
  - Al convertir los SVG, agregó 1 dp de margen para que no se corte el trazo.
- Revisores:
  - revisor-visual, dos vueltas: en la primera marcó que las pestañas estaban pegadas al borde (se sumaron 12 dp a los costados) y una captura mal nombrada; la segunda dio OK.
  - revisor-arquitectura: nada bloqueante. Se aplicó la corrección de radios en CONTEXTO §6 (8 y 18 sí están en la lámina) y la preview pasó a usar el fondo del tema.
  - /code-review: se corrigieron el área táctil de las pestañas, la flecha de volver a 20 dp del borde y las etiquetas en una sola línea. Queda para Fiore el color de los íconos de la barra de estado en modo oscuro (`enableEdgeToEdge` en `MainActivity`).
- Evidencia: `docs/evidencia/visual/tema-barra-home-394dp.png`, `tema-barra-workout-394dp.png`, `tema-barra-nutrition-360dp.png`. Build y tests unitarios en verde (6 tests), `reglas.sh` sin fallas.
- Commit / PR: [#5](https://github.com/sofiaandreaolguin/parcial-tp3/pull/5)

### 2026-10-20 · Martu · feat(componentes): agregar botones, tarjetas, campos, chips y mensajes
- Carril y requisitos: B · UI01, UI02
- Plan aprobado: PR 2 de 3 de la tarea VC-d. Componentes comunes de la lámina 04 (botones, tarjeta y panel, campo, chips y etiquetas, fila de lista, mensajes de coaching), sin estado y con todo del tema, más un catálogo en preview para compararlos con el Figma.
- Qué propuso la IA y qué cambiamos:
  - Al mirar Profile, la IA vio dos componentes que el plan no tenía: el encabezado grande, con la flecha en un círculo, y la tarjeta de opción con check. Los sumó a este PR. También sumó el estilo de botón `Soft` ("Edit profile") y movió los botones "3 days / 4 days" al PR 3, porque son un control segmentado y no chips.
  - Martu exportó los 6 íconos del Figma. `ic_info` e `ic_sparkle` venían con máscara, que Android no soporta, y la IA los rearmó con `clip-path`.
  - Para capturar el catálogo, la IA armó una pantalla temporal solo de debug y la borró antes del commit.
- Revisores:
  - revisor-visual, dos vueltas. En la primera midió el Figma con una escala equivocada (los PNG están a 2x). La IA midió a tamaño real y corrigió solo lo confirmado: chips de 14 y 33 dp, check de 20, botones chicos en los paneles, fila de comida con radio 12 y consejo de 13. En la segunda quedaban un botón partido en dos líneas a 360 dp, `Soft` invisible sobre el fondo y la separación del campo; se corrigieron y se verificaron con capturas.
  - /code-review: se corrigieron el nombre del campo para TalkBack, la elipsis de los botones y el rol de la tarjeta de opción (`RadioButton` para elección única).
  - revisor-arquitectura: nada bloqueante. Se aplicaron el rol de botón en las tarjetas que se tocan, el título como encabezado, sacar `0.dp` y la preview de la etiqueta oscura.
  - Quedan como sugerencias: áreas táctiles de 32 a 34 dp (el Figma las dibuja así), el rol de los chips que se pueden quitar y el nombre del color de fondo de `VoraTag`.
- Evidencia: `docs/evidencia/visual/componentes-catalogo-{1,2}-{394dp,360dp}.png`. Build y 11 tests unitarios en verde, `reglas.sh` sin fallas.
- Commit / PR: [#6](https://github.com/sofiaandreaolguin/parcial-tp3/pull/6)

### 2026-10-20 · Martu · feat(componentes): agregar controles e indicadores
- Carril y requisitos: B · UI01, UI02
- Plan aprobado: PR 3 de 3 de la tarea VC-d. Control segmentado, estadísticas, anillo y barras de avance, gráfico de barras, interruptor, selector − y + y el orbe de VORA, sin estado y con todo del tema, más su parte del catálogo.
- Qué propuso la IA y qué cambiamos:
  - Antes de programar, la IA midió todo en el Figma a tamaño real, recortando los PNG a 2x.
  - Encontró tres formas de control segmentado y armó un solo componente con tres estilos.
  - Armó un interruptor propio, porque el `Switch` de Material mide 52 × 32 y el del Figma 44 × 24.
  - Sacó el "−" del mismo trazo del "+", así no hizo falta exportar íconos nuevos.
  - Separó el catálogo en dos partes para capturar solo los controles, con una pantalla temporal de debug que se borró antes del commit.
- Revisores:
  - revisor-visual, dos vueltas. En la primera, con el Figma reducido, se confirmaron a tamaño real y se corrigieron: interruptor de 44 × 24, círculos del selector de 36, gráfico de 72 de alto con barras de 18 o 24 y punta redondeada solo arriba, varias barras resaltadas y orbe oscuro sin la línea gris. Para la segunda, la IA le pasó recortes a tamaño real; confirmó todo y quedaron la separación del selector y el halo del orbe claro, que se corrigieron y se verificaron con capturas. No se cambió el degradado de la esfera, que en el Figma es horizontal.
  - /code-review: el gráfico arma barra y etiqueta en la misma columna, y TalkBack lee solo el resumen.
  - revisor-arquitectura, sin nada bloqueante. Se corrigieron los dos ALTA: el interruptor se puede usar con la fila entera como zona táctil (`onCheckedChange = null`), y el selector tiene área táctil de 48 sin cambiar cómo se ve. También: las cajas se anuncian como opción única, el anillo y las estadísticas se leen como un solo elemento, el orbe chico tiene su propio tamaño y CONTEXTO §6 y §9 quedaron al día.
- Evidencia: `docs/evidencia/visual/controles-catalogo-{1,2}-{394dp,360dp}.png`. Build y 11 tests unitarios en verde, `reglas.sh` sin fallas.
- Commit / PR: [#7](https://github.com/sofiaandreaolguin/parcial-tp3/pull/7)

### 2026-10-20 · Fiore · fix(nav): fijar el estilo de la barra de estado para el tema claro
- Carril y requisitos: D (bug) · UI01 · zona de Navegación (`MainActivity`)
- Plan aprobado: `enableEdgeToEdge()` venía sin argumentos desde el commit inicial de la plantilla. Elegía el color de los íconos de las barras del sistema según el modo oscuro **del celular**, no según los colores de la app. Mientras el tema de la plantilla tenía modo oscuro no se notaba; al pasar el tema a solo claro (PR #5), en un celular en modo oscuro los íconos salían blancos sobre el fondo claro de VORA y no se leían. Se fija `SystemBarStyle.light` en las dos barras.
- Qué propuso la IA y qué cambiamos: la IA encontró que la barra de abajo tenía el mismo problema y no solo la de estado (`VoraBottomBar` usa `colorScheme.surface`, que también es claro), así que se fijaron las dos y no solo `statusBarStyle`. Con `minSdk = 29` las dos barras admiten íconos oscuros, así que el segundo argumento (`darkScrim`) nunca se usa y queda transparente. Se agregó un comentario que aclara que `SystemBarStyle.light` se nombra por el **fondo**, no por el color del ícono.
- También en este cambio: la ficha `docs/decisiones/004-rutas-object-y-route-screen.md`, que documenta dos decisiones de navegación que estaban tomadas pero sin escribir (`object Rutas` y `XxxRoute`), y la sección de `specs/CONTEXTO.md` que las compara con los proyectos de la cátedra.
- Revisores: no corrieron los subagentes revisores (cambio de carril D, de tres líneas). Corrió el control automático: `reglas.sh` sin fallas y `control-rapido.sh` en verde.
- Evidencia: `./gradlew testDebugUnitTest` en verde, 11 tests, 0 fallas. Comprobado en el emulador Pixel_8a (411 dp) con `adb shell cmd uimode night yes`: `docs/evidencia/visual/barra-estado-modo-oscuro-411dp.png`. La hora, el wifi y la batería se leen oscuros sobre el fondo claro de VORA, y la barra de gestos también.
- Commit / PR:

### 2026-10-21 · Sofi · feat(nutrition): guardar y leer comidas por fecha en Room
- Carril y requisitos: C · RF03 (parcial), RF04 (parcial), RF06, RF08 (parcial)
- Plan aprobado: VC-h, sesión 4 del plan (Commit 8 y pull request). Solo persistencia de comidas: `Meal`, `MealEntity`, `MealDao`, mapeo, `MealRepository` con su implementación y el cableado de Hilt. Sin ViewModel ni pantallas (VC-l).
- Qué propuso la IA y qué cambiamos:
  - La IA adelantó el repositorio (commit 6) al DAO (commit 7) para que `control-rapido.sh` quedara en verde: C6 y C7 usan un `FakeMealDao`.
  - Los controles negativos mostraron que C3 y C9 habían pasado en rojo. Se les hizo el control negativo recién con el código real.
  - Al cerrar, el revisor de requisitos marcó que la columna "Vi el rojo" de la spec decía "no" en todas las filas. Se corrigió a "sí", con nota en C3 y C9.
  - Los revisores señalaron el `fallbackToDestructiveMigration` (borra las comidas al subir la versión) y las excepciones de los mappers dentro del `Flow`. No se tocaron: la spec los deja para VC-l y la decisión 003 ya documenta el fallback.
- Revisores: revisor-requisitos, revisor-arquitectura y /code-review. Nada BLOQUEA ni CRÍTICA. Una ALTA (fallback destructivo con `VoraDatabase` en la versión 2), que queda como aviso y no se aplicó. Sugerencias sin aplicar: índice en `date`, `addMeal` con id repetido, test C10 con sincronización débil, `@Singleton` innecesario en el `@Binds`.
- Evidencia: `docs/evidencia/tests/2026-10-21-vc-h-{rojo,verde}.md`. Build y 17 tests unitarios en verde; 8 tests instrumentados en verde en el emulador (`MealDaoTest` 5, `WorkoutDaoTest` 2, `ExampleInstrumentedTest` 1).
- Commit / PR:

### 2026-10-22 · Zaki · feat(profile): maquetar Profile / 01 Dashboard según Figma
- Carril y requisitos: B · UI01, UI02, UI03, NAV01, NAV03
- Plan aprobado: rama 1 de 6 del flujo Profile. Profile es "Demostración" en la consigna, así que no hay Room: un `ProfileRepository` en memoria (`@Singleton`, `StateFlow`) con el perfil de Alex Morgan, y un ViewModel por pantalla que lo lee. Aviso de modo demostración con `Toast` en las filas sin pantalla.
- Qué propuso la IA y qué cambiamos:
  - La IA propuso un solo `ProfileViewModel` compartido por las 6 pantallas. Zaki lo puso en duda con sus apuntes de clase ("un viewmodel por pantalla") y se eligió un ViewModel por pantalla con el repositorio compartido, que es la cadena de RF06 y la forma del proyecto `Quote` del docente.
  - La IA propuso una función privada para la etiqueta "Synced". Zaki propuso usar `VoraTag` que ya existe; se sumó para usarlo el tono `Dark` a `VoraTag` y un `trailing` opcional a `VoraListRow` en vez de la función privada.
  - Zaki notó que al orbe del panel le faltaba el aro del Figma; se pasó a `VoraOrb(onDark = true)`.
  - El tamaño del avatar empezó como constante local y pasó al tema (`VoraSizes.avatar`).
  - Los 16 íconos los exportó otro agente del Figma (cuenta Starter, sin Dev Mode) y la IA los convirtió a vector drawable.
- Revisores:
  - revisor-visual, dos vueltas. La primera marcó texto del panel VORA más grande y padding de 20 en la tarjeta de identidad: Zaki decidió no aplicarlas. La segunda, con capturas del teléfono en inglés a 411 y 360 dp: aprobada.
  - revisor-arquitectura: un BLOQUEA, el conector " & " de los objetivos escrito en el código; se pasó a `profile_goals_join` ("&" en inglés, "y" en español). Un ALTA de zona: los cambios en `Dimens.kt`, `VoraCard.kt` y `VoraChip.kt` son de Martu y quedan para su aprobación en el PR.
  - /code-review: además del " & ", el caso de objetivos vacíos (`reduceOrNull`).
  - Quedan como sugerencias: test del ViewModel, `birthDate` como texto en inglés y alimentos a evitar sin traducir (se ven recién en 02 y 04), la frase bajo el nombre fija aunque cambien los objetivos, área táctil de 32 del engranaje (igual que `VoraTopBarLarge`), `FontWeight.Medium` en la pantalla y confirmar con Fiore la entrada a 05 desde "Review preferences".
- Evidencia: `docs/evidencia/visual/profile-01-{411dp,360dp}{,-scrolled}.png` (teléfono real). Build y tests unitarios en verde, `control-rapido.sh` sin fallas.
- Commit / PR:

### 2026-10-21 · Hector · feat(workout): implementar catálogo de entrenamientos con Retrofit
- Carril y requisitos: C · RF01 (parcial: catálogo), RF06, RF07 (parcial: carga y rotación), RF08 (parcial: repositorio)
- Plan aprobado: tarea g del hito 1, primera consulta Retrofit. `GET /v1/workouts` con `WorkoutDto` separado de `Workout`, mapper que no inventa valores, `WorkoutRepository` con `Result` y `WorkoutsViewModel` que carga una sola vez en `init`. Sin `AppError`, `Empty` ni reintento (RF05), sin Room (tarea j) y sin pantallas.
- Qué propuso la IA y qué cambiamos:
  - La IA propuso atrapar solo excepciones específicas en el repositorio. Hector preguntó para qué servía, y al comparar se eligió la opción B: relanzar `CancellationException` y después atrapar `Exception`. Cubre el error "desconocido" de `CONTEXTO.md` y la promesa de la interfaz de que todo error llega como `Result.failure`.
  - Con la implementación, C12 cumplía sus aserciones pero fallaba en el `tearDown`: MockWebServer dormía los 10 s de la demora y `shutdown()` espera a lo sumo 5. La IA no tocó el test y lo consultó. Hector autorizó bajar la demora a 3 s.
  - El control negativo de C15 (`WhileSubscribed`) mostró que C15 no lo detectaba, porque `advanceUntilIdle()` no corre el colector de `backgroundScope`. La IA lo probó con una copia temporal del test, que después borró, y Hector autorizó cambiar a `runCurrent()`. Ahora C15 falla solo con `expected:<1> but was:<2>`.
  - Se alineó la spec (decía `setBodyDelay` y el test usa `setHeadersDelay`) y se sumaron al historial los dos cambios de test.
- Revisores: revisor-requisitos (C1 a C16 cumplen; los dos cambios de test no debilitan nada), revisor-arquitectura y /code-review. Nada BLOQUEA, CRÍTICA ni ALTA. Sugerencias sin aplicar, que quedan para RF05: conservar el código HTTP (`HttpException`), atrapar el JSON roto antes que `IOException`, sacar la carga a `load()` para el reintento, registrar el error en el ViewModel, que C12 con 3 s puede ser inestable en una CI muy lenta (alternativa `SocketPolicy.NO_RESPONSE`) y aclarar en `CLAUDE.md` que el ViewModel sí importa `data.repository`.
- Evidencia: `docs/evidencia/tests/2026-10-21-catalogo-{rojo,verde}.md`. Test en rojo en el commit `a22a882`. 34 tests unitarios en verde, `assembleDebug` en verde y `reglas.sh` sin fallas. Controles negativos de C1, C2, C7, C12, C15 y C16.
- Commit / PR:
