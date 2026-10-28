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
- Plan aprobado: llevar al repo la metodología del grupo (sección 6) para que la usen los cinco: `CLAUDE.md`, `specs/` (contexto, consigna, API, requisitos, plantilla), subagentes revisores, una skill por paso del ciclo, control automático, hook de commits, CI y plantilla de pull request.
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

### 2026-10-22 · Fiore · feat(voice): el asistente VORA con sus seis frames
- Carril y requisitos: B (pantalla) · UI01, UI02, UI03, NAV02, NAV03, **NAV05** · tarea VC-o
- Plan aprobado: spec funcional en `specs/funcionales/asistente-vora.md` con 38 criterios en EARS y un orden de seis pasos: iconos, tema, los seis componentes comunes, los cinco estados de `voice`, `conversation` y cierre. Dos rutas y ocho archivos de pantalla, todo simulado.
- Qué propuso la IA y qué cambiamos: las medidas salieron del Figma leído con el conector (archivo `IvCRfdJPXEkRcm6miaJHIG`), no estimadas, y los ocho iconos se convirtieron del SVG a vector XML con la convención que ya usaba Martu. Aparecieron tres cosas que el código daba por otra cosa: el orbe chico **sí lleva aro** (el KDoc decía lo contrario), Listening y Speaking tienen una capa más (`orb-ripple`, al 115 %) que el componente no dibujaba, y el margen lateral no es 20 en todos lados (20 en el encabezado, 24 en el contenido, 16 en la barra de escribir). Del Figma salieron solo dos colores nuevos: `#4A5250` y `#FCDEDE`. En la spec se había decidido reutilizar `VoraTextField` para la barra de escribir; **al implementarlo se cambió de idea** y el composer dibuja su propio campo, para no sumarle cuatro parámetros a un componente que usan todas las pantallas de formulario. El estado `Error` no tenía ninguna entrada en el Figma: se resolvió alternando en Processing (la primera vuelta termina en Speaking y la segunda en Error), determinista para que la demo no dependa de la suerte y para poder testearlo.
- Revisores: `revisor-visual` sobre las 12 capturas (los 6 frames en 411 y en 360 dp). Dio CON CAMBIOS en cinco pantallas y COINCIDE en el error. Antes de tocar nada se verificó cada hallazgo contra el Figma con el conector, y de ahí salieron **ocho correcciones**: "LIVE AUDIO" es un punto menta con texto al lado y no una pastilla; "LIVE TRANSCRIPT" va en menta; "THINKING..." va en menta y no en lima; los tres paneles verdes llevan borde y un fondo mucho más tenue (blanco al 5 % con borde al 10 %, no al 12 % sin borde), con su radio y su relleno por frame; los textos de los paneles son más grandes (18 en Listening, 16 en Speaking); el separador de fecha de Conversation va centrado y en gris tenue; y el anillo de carga pasó de 16 a 20 porque a 16 se leía como una astilla. Dos hallazgos del revisor **no** eran defectos: el recorte de la tarjeta de última conversación a 360 dp (la pantalla sí se desplaza, se comprobó deslizando: `voice-01-360dp-scroll.png`) y la falta de evidencia en modo oscuro (se capturó después de que arrancara la revisión). **Segunda vuelta del revisor** (el ciclo visual de METODOLOGIA 6.2 dice "iterar hasta que coincida", y la primera vez me quedé en corregir sin volver a revisar; mirar uno mismo las capturas propias es justo lo que descarta el fundamento 2 de la 6.4, "quien hace no se califica"). Con las capturas nuevas y sin saber qué se había tocado: **Conversation y Error pasaron a APROBADA**, y en Listening y Processing el contenido quedó fiel. De la segunda vuelta salió una corrección más, medida en los frames: cada pantalla deja su propio aire entre el encabezado y el orbe (20 en Idle, 40 en Listening, 50 en Processing, 30 en Speaking y Error) y estaba en cero en las cinco. Dos cosas se dejaron a propósito, por el fundamento 6 ("cuidado con el sobre-arreglo"): que la tarjeta de última conversación quede cortada en el primer viewport de 360 dp (es lo que hace cualquier lista que se desplaza) y la distancia entre los controles y "Tap to Interrupt" en Speaking (el frame lo deja contiguo, pero nuestras pantallas son más altas que los 844 del frame y el pie va anclado abajo en los seis). Queda abierto lo que el revisor llamó G1 y G2 y no es de esta tarjeta: las barras del sistema no se pintan del color de la pantalla y sus íconos quedan ilegibles sobre fondo claro. **Corrección sobre G1, tras traer `main`:** la rama estaba 24 commits atrás y el paso 0 del plan ("ponerse al día") no se había hecho. Con `main` adentro aparece que el **PR #8 de Sofi ya había arreglado G1** fijando `SystemBarStyle.light` en los dos bordes, así que los íconos de la barra se leen bien y la mitad de lo que el revisor marcó como prioridad uno venía de capturar sobre una rama vieja. **G2 sigue y queda mejor acotado:** el arreglo de Sofi da por sentado que la app es toda clara —era cierto cuando lo escribió—, y Voice es el primer flujo con tres pantallas verde oscuro, donde el verde no llega detrás de las barras. No es rehacer el armazón: es extender su arreglo para que el estilo del borde siga a la pantalla. Se habla con Sofi. Se verificó además que `Profile / 01` sigue igual con los cambios aditivos a `VoraOrb` y `VoraTopBar`, que era el riesgo que no se podía mirar sin `main`. Se le agregó a `scripts/reglas.sh` el control de que ningún `.kt` llame a `RequestPermission` ni a `checkSelfPermission`, con su control negativo: antes solo miraba el manifiesto.
- Evidencia: `./gradlew assembleDebug` y `testDebugUnitTest` en verde, 15 tests nuevos de los dos ViewModel (26 en total). Capturas de los seis frames en dos tamaños, en `docs/evidencia/visual/voice-0*-{411,360}dp.png`, sacadas recorriendo la app de verdad: el recorrido de la demo (Idle → Listening → Processing → Speaking → Idle → segunda vuelta → Error → Conversation) funciona de punta a punta. Para llegar a Voice sin pasar por Home, que todavía es pantalla de relleno, se apuntó el `NavHost` a `voice_graph` **solo para capturar**, y se revirtió. Se creó el AVD `Vora_360` (1080 x 2160 a 480 ppp) para el segundo tamaño de UI03. Con el modo oscuro del sistema activado la pantalla se ve igual (criterio C38): `voice-01-idle-411dp-oscuro.png`.
- Cierre (22/10): despues de la segunda vuelta del revisor se hizo la pasada que faltaba, nodo por nodo del Figma, sobre tipografias, colores y espaciados, que hasta ahi se habian mapeado "por funcion" a los roles del tema. De ahi salieron la flecha de volver dentro de un circulo a 20 del borde, SPEAKING en menta, TAP TO TALK en verde, "Type instead" subrayado, el encabezado de Conversation blanco de 56 debajo de la barra de estado, y el aire por frame (32 entre bloques en Idle, Speaking y Error; 40 en Listening y Processing). Las dos rutas del asistente pasaron a pintarse hasta el borde, que es lo unico que toca `MainActivity`: las otras 35 no cambian y `Profile / 01` quedo identica, verificado antes y despues. Dos errores propios quedaron en el camino y van como ejemplo en METODOLOGIA 6.2: el fondo del encabezado puesto antes del margen de la barra de estado, y el circulo de volver a 8 en vez de a 20 por el area tactil de 48. Los dos con el nodo bien leido y el resultado mal, y los dos pasaron varias revisiones a ojo antes de que un muestreo de color los mostrara en un segundo.
- Commit / PR:

### 2026-10-22 · Zaki · feat(profile): maquetar Profile / 02 Edit Profile según Figma
- Carril y requisitos: B · UI01, UI02, UI03, NAV02, NAV03, NAV05
- Plan aprobado: rama 2 de 6 del flujo Profile. Por ahora las pantallas de Profile solo se dibujan con los datos del `ProfileRepository` en memoria: los campos son de solo lectura y "Save changes" vuelve atrás sin guardar. Editar y guardar llega si se suma persistencia.
- Qué propuso la IA y qué cambiamos:
  - La IA propuso un menú para elegir Gender y campos con unidad fija para cm y kg. Zaki lo frenó: la pantalla es solo dibujo, y los datos se muestran estáticos como en el Figma.
  - La IA planteó que los campos abrirían el teclado sin dejar escribir. Se sumó `readOnly` a `VoraTextField`, con el visto bueno de Martu. Al hacerlo editable alcanza con quitarlo, y la edición va en el ViewModel y el repositorio.
  - Zaki pidió hacer componente solo lo que se repita en el flujo: `ProfileAvatar` (01 y 02) y el Toast de demostración (`rememberDemoToast`). "MY SNAPSHOT" pasó a `VoraSectionLabel`, que ya existía.
  - `birthDate` pasó de texto en inglés a `LocalDate`, con el formato en `strings.xml` según el idioma.
- Revisores:
  - revisor-visual, dos vueltas. La primera marcó la foto de 80 dp, la etiqueta de los campos y la nota al pie en 12, y dos espaciados; la IA confirmó las medidas en el Figma antes de cambiar el tema y Zaki eligió aplicar todo (`avatarLarge`, `fieldLabel`, `footnote`). La segunda: aprobada.
  - revisor-arquitectura: nada bloqueante. /code-review: se corrigieron dos comentarios que habían quedado mal, el Toast repetido y el `remember` del formato de fecha.
  - Quedan como sugerencias:
    - tres diferencias de 4 dp como mucho (GENDER → aviso, "Change photo" y botón → nota al pie);
    - el campo de solo lectura toma foco y TalkBack lo lee como editable;
    - "Save changes" vuelve sin avisar;
    - `ProfileRepository` sin interfaz;
    - nombres escritos en las previews.
- Evidencia: `docs/evidencia/visual/profile-02-{411dp,360dp,360dp-scrolled}.png` (teléfono real). Build y tests unitarios en verde, `control-rapido.sh` sin fallas.
- Commit / PR:

### 2026-10-22 · Zaki · feat(profile): maquetar Profile / 03 Your goals según Figma
- Carril y requisitos: B · UI01, UI02, UI03, NAV02, NAV03
- Plan aprobado: rama 3 de 6 del flujo Profile. La pantalla solo se dibuja con las opciones del perfil de demostración: tocar no cambia la selección y "Save preferences" vuelve atrás. Todo con componentes que ya existían.
- Qué propuso la IA y qué cambiamos:
  - La IA armó la pantalla con `VoraSectionLabel` y el revisor visual midió que las etiquetas del Figma son de 13 y no de 10. Se usó `labelMedium` en una función de la pantalla, sin cambiar el componente que también usa Voice.
  - El revisor marcó que el check de `VoraOptionCard` va a la altura del título y que la tarjeta es más alta. La IA propuso pasárselo a Martu y Zaki decidió hacerlo en esta rama (check arriba y padding 16), avisándole.
  - La IA se equivocó al sumar espacio con un `Spacer` dentro de un `spacedBy` (daba 32 y no 20). Lo detectó antes de la captura y lo pasó a `padding(top)`.
- Revisores:
  - revisor-visual, dos vueltas. La primera marcó las etiquetas de 13, tres espaciados y la tarjeta de opción. La segunda: aprobada, con una diferencia menor en el alto de los chips (zona de Martu).
  - revisor-arquitectura y /code-review: nada bloqueante. Se corrigieron dos comentarios, el rol de la tarjeta de coaching (`RadioButton`, elección única) y la traducción de "Supportive" ("De apoyo").
  - Quedan como sugerencias:
    - las opciones y los chips siguen siendo tocables sin cambiar nada, y los chips se anuncian como casilla (rol fijo en `VoraChip`);
    - los tres ViewModel de Profile repiten el mismo `map` + `stateIn`;
    - no hay tests de los ViewModel.
- Evidencia: `docs/evidencia/visual/profile-03-{411dp,360dp}{,-scrolled}.png` (teléfono real). Build y tests unitarios en verde, `control-rapido.sh` sin fallas.

### 2026-10-22 · Martu · feat(access): maquetar Login - 01 SignIn según Figma
- Carril y requisitos: B (pantalla) · UI01, UI02, UI03, NAV02, NAV03, RF07, RF08 · tarea VC-n (1 de 4)
- Plan aprobado: un PR por pantalla (SignIn, SignUp, Home Dashboard, Notifications). Paso 0 en la zona de tema y componentes: íconos que faltaban, aviso de demostración compartido y piezas de Acceso. `AuthViewModel` compartido por SignIn y SignUp, del grafo `access_graph`.
- Qué propuso la IA y qué cambiamos:
  - La IA midió el frame sobre la imagen exportada y propuso los textos un punto abajo de la lámina. Con el conector del Figma (archivo `LKv4KQzpss9FUJpCDPOVaF`, frame `2007:214`) se confirmó casi todo y se corrigió lo que no: el botón es 16 y no 17, los campos 55 y no 54, el selector 30, el escudo 16, y cuatro espacios de 2 a 4 dp.
  - Login no usa la lámina: paleta propia aislada en `VoraTheme.colors.access` y errores de formulario que el Figma no dibuja (ficha `docs/decisiones/006-login-paleta-propia-y-errores.md`).
  - La IA envolvió el ojo de la contraseña en una caja de 48 dp por el área táctil; al ver que agrandaba el campo a 72 se volvió atrás, porque Compose ya extiende el toque de un `clickable` chico a 48.
  - Martu pidió hacer en esta tarea lo que era de Fiore: SignIn se pinta hasta el borde (`RUTAS_A_SANGRE` en `Rutas.kt`, que ahora usa `MainActivity`) y `PANTALLAS.md` y el LEEME de access dicen `popUpTo(access_graph)`, que es lo que hace el código.
- Revisores: `revisor-visual` APROBADA (sugirió el placeholder de puntos y el color del escudo, aplicados); `revisor-requisitos` y `revisor-arquitectura` sin nada que bloquee; `/code-review` con 7 hallazgos: 3 corregidos (email sin espacios, degradé con `drawWithCache`, medidas del resplandor con nombre), 1 sin cambio necesario (área táctil) y 3 que quedan (test de `AuthViewModel`, reusar `VoraCircleButton` para volver, y la barra de estado, que después se resolvió). Se aplicaron también: la Route pide el ViewModel del grafo sin valor por defecto, tokens en vez de cuentas para sombras y flecha, y el estilo de "Sign up here" en el tema.
- Pendiente: ~~`AuthViewModelTest`~~ hecho el 23/10 (entrada "test(access, home)"). SignUp entró a `RUTAS_A_SANGRE` con su PR.
- Evidencia: `assembleDebug` y `testDebugUnitTest` en verde (49 tests), `control-rapido.sh` sin fallas. Capturas en `docs/evidencia/visual/login-01-{394dp,360dp,360dp-scroll}.png` (Pixel 9 Pro con densidad ajustada). Recorrido en el emulador: Sign In vacío muestra los errores; con datos válidos entra a Home y atrás cierra la app; "Listo" del teclado entra; "Sign up here" va a SignUp y al volver se conserva el email; el texto sobrevive a la rotación.
- Commit / PR: `dd35857`, `854f637`, `c6adf0f`, `4bdfea2` · [#18](https://github.com/sofiaandreaolguin/parcial-tp3/pull/18)

### 2026-10-22 · Martu · feat(access): maquetar Login - 02 SignUp según Figma
- Carril y requisitos: B (pantalla) · UI01, UI02, UI03, NAV02, NAV03, RF07, RF08 · tarea VC-n (2 de 4)
- Plan aprobado: PR 2 de 4 de la tarea n, en una rama que sale de `feat/access-sign-in` (PR #18 todavía abierto). Reusa las piezas de Acceso y el `AuthViewModel` del grafo.
- Qué propuso la IA y qué cambiamos:
  - Las medidas salieron de los nodos del Figma (frame `4002:2`) y los colores de la imagen exportada. SignUp no es SignIn con otro formulario: tiene su propia escala (título 28, etiquetas 16, campos blancos de 52 con borde gris, botón de 48, orbe de 56 con degradé lima a verde). Se resolvió con variantes de los mismos componentes (`compact`, `style`, `AccessOutlined`, `lines`, `height`), sin copiarlos.
  - La IA comparó mal el botón contra el Figma (se olvidó un margen) y dio por buena una tarjeta que terminaba 11 dp antes. El revisor visual lo notó, aunque ubicó mal el hueco; midiendo campo por campo apareció la causa: Compose recorta el interlineado por omisión (`Trim.Both`) y cada etiqueta de 16/24 ocupaba 22. Se sacó el recorte en los estilos de SignUp y la tarjeta quedó a 1 dp del frame. En el título de 28 eso no alcanzó (Compose ubica la letra 5 dp más abajo con o sin recorte) y se compensó en los espacios del encabezado, con el porqué en el comentario.
  - Al probar apareció un bug: como el formulario es compartido, un "Sign Up" fallido dejaba SignIn en rojo. Ahora `onSwitchScreen()` borra los errores (no lo escrito) al pasar de una pantalla a la otra, también con el atrás del sistema (`BackHandler`).
  - `/code-review` encontró dos bugs de doble toque: "Sign up here" apilaba dos SignUp y la flecha podía sacar también el login y dejar la pila vacía. Se corrigieron con `launchSingleTop` y `popBackStack(sign_in)`.
  - El tilde de "8+ secure characters" se prende con 8 caracteres; en el Figma está siempre prendido porque el frame es estático. El tercer botón social del nodo mide 46 (copia de SignIn): en la app los tres miden 48.
- Revisores: `revisor-visual` CON CAMBIOS (espaciado de la tarjeta, corregido como se cuenta arriba; quedan como sugerencia los íconos que no son los del Figma, la flecha de "Sign Up" más fina y que al hacer scroll el contenido pasa por debajo de la barra de estado); `revisor-requisitos` y `revisor-arquitectura` sin nada que bloquee; se aplicaron el divisor sin líneas transparentes, la medida del brillo del orbe comentada y la tabla NAV03 y el LEEME al día. `/code-review`: 2 corregidos (doble toque), 3 sugerencias (armazón y pie compartidos entre SignIn y SignUp, divisor, ya corregido).
- Pendiente: ~~`AuthViewModelTest`~~ hecho el 23/10 (entrada "test(access, home)"), con los casos de `signUp` y `onSwitchScreen`.
- Evidencia: `assembleDebug` y `testDebugUnitTest` en verde, `control-rapido.sh` sin fallas. Capturas en `docs/evidencia/visual/login-02-{394dp,360dp,360dp-scroll}.png`. Posiciones medidas contra los nodos a ±1 dp (título, frase, tarjeta, tres campos, botón y fin de tarjeta). Recorrido: "Sign Up" vacío muestra tres errores; flecha, atrás del sistema y "Sign In" vuelven a SignIn sin errores viejos; doble toque en "Sign up here" y en la flecha no rompe la pila; con datos entra a Home y atrás cierra la app.
- Commit / PR: `5a102fb`, `a1dd26d`, `50a107c` · [#20](https://github.com/sofiaandreaolguin/parcial-tp3/pull/20)

### 2026-10-22 · Hector · feat(workout): maquetar Workout / 01 Dashboard según Figma
- Carril y requisitos: B · UI01, UI03, NAV03 · tarea k (PR 1 de 10 del flujo Workout)
- Plan aprobado: las 7 pantallas de Workout en un PR por pantalla. 01 y 02 usan el catálogo real (`WorkoutsViewModel`); de 03 a 07 van con datos de demostración detrás de una interfaz `WorkoutDetailRepository`, para que la tarea del detalle (`GET /v1/workouts/{id}`, carril C aparte) solo cambie la implementación. Lo que no es dibujo (mock, base del detalle, sesión simulada) va en PR propios.
- Qué propuso la IA y qué cambiamos:
  - El conector de Figma no tenía acceso al archivo del grupo. Hector pasó una copia a su nombre (`SfJjENBxckjdZUbLllD6fJ`) y las medidas salieron de los nodos, no de la exportación.
  - La tarjeta "VORA RECOMMENDS" se repite en 01, 03 y 07 y no existía. La IA preguntó antes de tocar la zona de Martu. Hector eligió crear `VoraCoachNote` y avisarle en el PR, igual que con el ícono `ic_calendar` (sacado del SVG del nodo), el tamaño `Medium` de `VoraButton` (14 SemiBold, 12/16, el del panel) y los tokens nuevos del tema.
  - El panel muestra el primer entrenamiento del catálogo del mock: la etiqueta dice el nivel real (BEGINNER) en vez de "MODERATE", y no va "Focus: Chest & Back" porque la API no trae ese dato.
  - Para probar en el emulador: el servidor publicado del profe estaba caído, y en el `Pixel_10_Pro` (Android 17) la app no llega a `10.0.2.2` aunque la consola sí. Se levantó el mock local del paquete del profe con `py` y se usó `adb reverse tcp:8000 tcp:8000` con `api.baseUrl=http://127.0.0.1:8000/`. El emulador `Medium_Phone` (Android 37.1 de 16 KB) se colgaba y se dejó de usar.
- Revisores:
  - revisor-visual, dos vueltas. La primera dio CON CAMBIOS: faltaba la sombra del panel (0 8 8, verde al 6 %) y los divisores de "Your Progress" quedaban 5 a 9 dp corridos porque el `weight` incluía el borde y el padding. Se corrigieron las dos. Segunda vuelta: APROBADA, con los divisores a menos de 1 dp del Figma.
  - revisor-arquitectura: nada bloquea. El ALTA es que el error no tiene reintento, que necesita cambiar el ViewModel y queda para la tarea de estados (RF05).
  - /code-review: se corrigió TalkBack, que leía la inicial ("T: done") y ahora lee el día completo. También se arreglaron dos cosas del revisor de arquitectura: un sangrado antes de `package` y el recorrido de los días con `zip` en vez de índice.
  - Quedan como sugerencias, para Martu: el área táctil del calendario (40 dp) y de "View progress", un tono con borde en `VoraCircleButton`, mover la sombra adentro de `VoraPanel`, tokens para "FIT" y los valores de progreso en Bold, el borde de "Details" al 30 % contra 25 % del Figma, y actualizar la tabla de componentes de CONTEXTO §9. Para la tarea de estados: el error con reintento. Desvío visual aceptado: los dos divisores de "Your Progress" llegan hasta abajo.
- Evidencia: `docs/evidencia/visual/workout-01-{394dp,360dp,360dp-scrolled}.png` (Pixel 10 Pro a 394 y 360 dp). `installDebug` y `testDebugUnitTest` en verde (49 tests), `reglas.sh` sin fallas. Los cuatro controles llevan a donde dice `PANTALLAS.md` y rotar no repite la consulta (0 `GET` en el log).
- Commit / PR:

### 2026-10-22 · Hector · feat(workout): maquetar Workout / 02 Weekly Plan según Figma
- Carril y requisitos: B · UI01, UI03, NAV03, NAV04, RF01 (el catálogo en pantalla) · tarea k (PR 2 de 10 del flujo Workout)
- Plan aprobado: 02 reutiliza el `WorkoutsViewModel` de 01 y reparte el catálogo real en una semana de ejemplo. Hoy es el martes 8, el miércoles es de recuperación y los entrenamientos van en martes, jueves y sábado. Cada tarjeta y cada día con entrenamiento del calendario abre `workout_details/{workoutId}` con su propio id. "I'm tired today" y "Move Wed workout to Fri" muestran el aviso de demostración.
- Qué propuso la IA y qué cambiamos:
  - El frame dibuja solo dos días con entrenamiento. La IA propuso sumar una tarjeta (SATURDAY) para que se vean los tres del catálogo del mock y cada uno abra el suyo. Hector lo aprobó. La tarjeta de recuperación del miércoles queda como en el Figma y no navega, porque no es un entrenamiento del catálogo (en el mock es `type: recovery`).
  - Faltaban tres medidas en el tema: la píldora del calendario (42), el orbe de la tarjeta (24) y el fondo de las píldoras del panel (blanco al 8 %). La IA preguntó antes de tocar la zona de Martu. Hector aprobó sumarlos con el nodo del Figma en el KDoc y avisarle en el PR.
  - La tarjeta de hoy, la etiqueta "READY", el panel de 16 y sus píldoras quedan `private` en la Screen, porque los componentes comunes no los cubren (`VoraCard` tiene el borde fijo, `VoraTag` Mint es translúcida y `VoraPanel` es de 20).
  - La IA quiso dejar un test de Compose para NAV04 (cada tarjeta manda su id), pero no corre en el `Pixel_10_Pro`: Espresso 3.5.1 llama a `InputManager.getInstance`, que no existe en Android 17. Se sacó el test y queda como pendiente subir Espresso. Además, `connectedDebugAndroidTest` desinstala la app, así que hay que reinstalarla antes de las capturas.
- Revisores:
  - revisor-visual: APROBADA en la primera vuelta. Diferencias menores sin corregir: la píldora del día y las tarjetas miden unos 3 dp más de alto por el interlineado de 20 del tema, y el orbe chico de `VoraOrb` muestra un aro claro.
  - revisor-arquitectura: nada bloquea. La ALTA es que los tokens tocan la zona de Martu, y se resuelve avisándole en el PR. Quedan como sugerencias: el error sin reintento (RF05, tarea de estados), sacar el reparto a una función pura, un tono de `VoraTag` menta lleno, un estilo `OnDark` en `VoraChip`, radio y padding en `VoraPanel`, compartir el `WorkoutsViewModel` entre 01 y 02 por la entrada del grafo, y mover `LoadingCard` a un archivo común del flujo.
  - /code-review: se sacó un `toSortedMap()` que sobraba y se comentó que la semana de ejemplo muestra hasta cuatro entrenamientos.
- Evidencia: `docs/evidencia/visual/workout-02-{394dp,394dp-scrolled,360dp,360dp-scrolled}.png` (Pixel 10 Pro a 394 y 360 dp, con el mock local). `installDebug` y `testDebugUnitTest` en verde (49 tests), `reglas.sh` sin fallas. En el emulador: la tarjeta del jueves y la píldora del sábado abren 03, y las píldoras del panel muestran el aviso.
- Commit / PR:

### 2026-10-23 · Zaki · feat(profile): maquetar Profile / 04 Nutrition preferences según Figma
- Carril y requisitos: B · UI01, UI02, UI03, NAV02, NAV03
- Plan aprobado: rama 4 de 6 del flujo Profile. Antes de la pantalla, un commit que comparte las piezas que se repiten en el flujo: `ProfileSectionLabel` (de 03) y `ProfileInfoRow` (de 01). La pantalla solo se dibuja con los datos del perfil de demostración.
- Qué propuso la IA y qué cambiamos:
  - Siguiendo la regla de Zaki (componente solo si se repite en el flujo), la IA esperó al segundo uso para extraer la etiqueta de sección y la fila etiqueta-valor, y midió en el Figma de 04 que la etiqueta es la misma de 03 antes de compartirla.
  - Los alimentos a evitar se muestran como texto del usuario, sin traducir; los valores de Meal preferences, que no se editan en el Figma, van en `strings.xml`.
  - El revisor visual marcó un campo de ingrediente más compacto en el Figma, el texto de los chips en 13 y la bajada algo más grande. Zaki decidió aceptarlas: son diferencias menores y dos de ellas cambian componentes de toda la app.
- Revisores:
  - revisor-visual: una vuelta, con las diferencias menores aceptadas.
  - revisor-arquitectura y /code-review: nada bloqueante. Las dos MEDIA son de accesibilidad en `VoraChip` (rol de casilla en elección única y la ⊗ sin descripción) y quedan como pedido para Martu.
  - /code-review marcó la fila de chips repetida entre 03 y 04; pasó a `ProfileChipRow`, compartida en el flujo.
  - Queda como sugerencia:
    - los ViewModel de Profile repiten el mismo `map` + `stateIn`. Se dejó así a propósito: son seis líneas sin lógica, muestran el patrón de la cursada en cada ViewModel y siguen la forma del resto del proyecto. Si se suma persistencia, el repositorio expone el `Flow` de Room (como `MealRepository`) y en cada ViewModel solo cambia el valor inicial del `stateIn`.
- Evidencia: `docs/evidencia/visual/profile-04-{411dp,360dp}{,-scrolled}.png` (teléfono real). Build y tests unitarios en verde, `control-rapido.sh` sin fallas.

### 2026-10-22 · Martu · feat(home): maquetar Home / 01 Dashboard según Figma
- Carril y requisitos: B (pantalla) · UI01, UI02, UI03, NAV02, NAV03, NAV04, RF04, RF07, RF08 · tarea VC-n (3 de 4)
- Plan aprobado: PR 3 de 4 de la tarea n, en una rama que sale de `main` (no depende de SignUp). Calorías y macros de hoy desde Room; el resto de demostración con la forma de `Workout`.
- Qué propuso la IA y qué cambiamos:
  - Las medidas salieron de los nodos del Figma (frame `2007:3`) y los colores de la imagen exportada: Home usa la lámina, así que no hizo falta paleta propia. Se reusaron `VoraPanel`, `VoraButton`, `VoraInfoMessage`, `VoraProgressRing`, `VoraProgressBar`, `VoraStatRow`, `VoraCard`, `VoraOrb`, `VoraTag` y `ProfileAvatar`; lo que el frame dibuja distinto se agregó como parámetro con su valor de antes por defecto (padding del panel, alto de barra, columnas a la izquierda, botón con ícono, estilo sobre verde, tamaño compacto, etiqueta neutra).
  - Las comillas de la pregunta desaparecían: Android las usa como delimitador en `strings.xml` y hay que escaparlas. La pregunta cortaba en otro lugar que el Figma ("fitness" arriba); medida la primera línea (241 contra 247.5 dp), se le dio 0.3 de espaciado y corta igual.
  - "Start my workout" y la sección "Today's Overview" navegan como la barra (`irAPestana`), así la pestaña queda marcada y atrás vuelve a Home.
  - Los totales del día se suman en Kotlin (ficha `docs/decisiones/007-home-totales-del-dia.md`, con la mejora posible para la pregunta 5).
- Revisores: `revisor-visual` APROBADA (marcó el fondo de "TODAY", que en el nodo no tiene relleno: corregido; el apóstrofo de "Today's" es recto en el nodo, así que se dejó); `revisor-requisitos` y `revisor-arquitectura` sin nada que bloquee. Martu pidió mover `ProfileAvatar` (estaba en `ui/screens/profile`) a `ui/components` como `VoraAvatar`, porque ahora lo usan Profile y Home; en Profile solo cambian el import y el nombre. Se aplicaron también: fechas de la documentación (la IA puso 23/10 tomando la hora UTC de GitHub), estilos en negrita como tokens del tema (`homeValue`), alto mínimo y no fijo en el botón de voz, el porcentaje calculado en el estado, el nombre armado con un string (el orden depende del idioma) y el estilo del botón compacto con nombre genérico (`buttonCompact`). `/code-review`: 2 corregidos (etiquetas de la fila de datos alineadas a la izquierda al cortar; alto de línea del valor grande), 3 que quedan.
- Pendiente: ~~`HomeViewModelTest`~~ hecho el 23/10 (entrada "test(access, home)"), sin `Clock` inyectado: el saludo se prueba con `Greeting.at` y el día con la fecha que pide el ViewModel. Sigue pendiente que la fecha de "hoy" se fija al crear el ViewModel (ficha 007).
- Evidencia: `assembleDebug` y `testDebugUnitTest` en verde, `control-rapido.sh` sin fallas. Capturas en `docs/evidencia/visual/home-01-{394dp,394dp-scroll,360dp,360dp-scroll}.png`. Posiciones contra los nodos a ±2 dp (panel, botón de voz, tarjetas, fila de datos y recomendado). Recorrido: campana, "Start voice session", "Start my workout", "Log breakfast", "Start workout" y las tarjetas del anillo y de nutrición van a su destino y atrás vuelve a Home.
- Commit / PR: `ce77b9a`, `523223c`, `0a94dbc`, `85d5ace`, `5eb7b38` · [#23](https://github.com/sofiaandreaolguin/parcial-tp3/pull/23). Los commits `523223c`, `0a94dbc` y `85d5ace` no compilan solos (en el del avatar quedaron afuera las dos pantallas de Profile); `5eb7b38` lo completa.

### 2026-10-23 · Hector · feat(workout): base del detalle de entrenamiento con datos de demostración
- Carril y requisitos: C · NAV04, RF01 (parcial), RF06, RF07, RF08 · tarea k, PR 3 de 10
- Plan aprobado: modelos `WorkoutDetail` y `Exercise` con la forma de `API.md`, interfaz `WorkoutDetailRepository`, `DemoWorkoutDetailRepository` con `@Binds`, y `WorkoutDetailViewModel` que lee `workoutId` y `exerciseId` de la ruta para 03, 04 y 06. Sin pantallas. El detalle real cambia solo el `@Binds` y borra la demo.
- Qué propuso la IA y qué cambiamos:
  - El plan decía que el mock solo traía w01 completo. La IA encontró los tres entrenamientos completos en `fixtures.json` (de ahí arma `server.py` el detalle), así que w02 y w03 se copiaron y no se inventaron.
  - La IA propuso que un `exerciseId` que no está en el entrenamiento (o vacío) y un `workoutId` que falta den `Error`; Hector lo aprobó.
  - Las tres skills del carril C (spec, rojo, implementar) se hicieron en la misma sesión, con el acuerdo de Hector, porque la IA no podía abrir sesiones nuevas.
  - `/code-review` y `revisor-arquitectura` marcaron que la demo calculaba `exerciseCount` desde la lista, así que D2 no podía fallar: ahora copia el 3 del mock.
- Revisores: auditor-spec (8 hallazgos, ninguno bloqueaba; 7 corregidos en la spec y 1 confirmado por Hector), revisor-requisitos (los 11 criterios se cumplen), revisor-arquitectura (nada bloquea), `/code-review` (2 BAJA: uno corregido; el fake con `idsPedidos` mutable queda como sugerencia).
- Pendiente: el detalle real `GET /v1/workouts/{workoutId}` (RF01 parte 2) es obligatorio antes del 28/10. Errores propios y reintento, con la tarea de estados (RF05).
- Evidencia: `docs/evidencia/tests/2026-10-23-detalle-demo.md` (rojo de los 10 tests, verde con 59 tests y controles negativos de C6, C7 y C8). `assembleDebug` en verde y `reglas.sh` sin fallas.
- Commit / PR:

### 2026-10-23 · Zaki · feat(profile): maquetar Profile / 05 VORA Coach según Figma
- Carril y requisitos: B · UI01, UI03, NAV01, NAV02, NAV03, NAV05
- Plan aprobado: rama 5 de 6 del flujo Profile. Incluye, en un commit aparte, los avisos de modo demostración en los controles sin función de 02, 03 y 04: era muy chico para su propio PR y es del mismo flujo. La pantalla 05 solo se dibuja con los ajustes del perfil de demostración. "Privacy & permissions" navega a Settings, y así se cierra la entrada a 05 desde "Review preferences" (NAV01).
- Qué propuso la IA y qué cambiamos:
  - Zaki pidió que todo control sin función del flujo avise modo demostración en vez de no responder. La IA lo cableó en los Route con `rememberDemoToast()`, y los botones Save avisan y vuelven.
  - `ProfileSectionLabel` sumó un `modifier` para usarlo dentro de la tarjeta de voz activa sin el aire de arriba.
- Revisores:
  - revisor-visual, dos vueltas. La primera marcó las tarjetas de interruptores más altas, los espacios entre bloques y dos tamaños de texto, y Zaki eligió corregirlos (filas de 36 con 12 al borde, 20 entre bloques, nota en 12, título en 15). La segunda: aprobada. Quedaron aceptadas las diferencias de componentes de Martu (botón Preview y texto del selector) y los textos de 14.
  - revisor-arquitectura y /code-review: nada bloqueante.
  - Quedan como sugerencias:
    - el campo "Add an ingredient…" no avisa al tocarlo (`VoraTextField` no tiene `onClick`);
    - el enlace "Privacy & permissions" tiene un área táctil chica y no usa `launchSingleTop`;
    - `ProfileSectionLabel` lleva el padding en el `modifier` por defecto;
    - en español, "VORA Coach" convive con "Coach VORA" de Voice.
- Evidencia: `docs/evidencia/visual/profile-05-{411dp,360dp}{,-scrolled}.png` (teléfono real). Build y tests unitarios en verde, `control-rapido.sh` sin fallas.

### 2026-10-23 · Hector · feat(workout): maquetar Workout / 03 Workout Details según Figma
- Carril y requisitos: B · UI01, UI03, NAV03, NAV04, RF01 (muestra el detalle de demostración) · tarea k, PR 4 de 10
- Plan aprobado: `WorkoutDetailsScreen` y `WorkoutDetailsRoute` sobre el `WorkoutDetailViewModel` del PR #24, textos en los dos idiomas y la Route en lugar de `PantallaVacia` en `WorkoutNavGraph`. Fila → `exercise_details`, Start Workout → `pre_workout`, Edit workout → aviso demo.
- Qué propuso la IA y qué cambiamos:
  - El conector no tenía acceso al Figma del grupo; Hector pasó una copia y las medidas salieron del nodo 1:424.
  - La IA propuso sumar al tema un estilo de 26 SemiBold y un botón de borde verde de 44, como en 02. Hector decidió no calcar al 100 % y usar lo que ya hay: título 26 Bold, botones Large de 48 y "Edit workout" con el Outlined gris. Las diferencias quedan anotadas en el KDoc y pedidas a Martu.
  - Los datos de la pantalla son los de la demo (copiados del mock), no los del dibujo: con el detalle real no cambia lo que se ve.
  - `LoadingCard` de 01 ahora recibe el texto, para decir "Loading workout".
- Revisores: revisor-visual CON CAMBIOS (texto y fondo de "Edit workout", KDoc incompleto: se anotaron) y APROBADA en la segunda vuelta; revisor-arquitectura sin nada que bloquee (las dos ALTA: el reintento es de RF05 y quedó pendiente; la tipografía armada con `.copy` se cambió por estilos del tema); `/code-review` con 3 arreglos (línea de datos con `FlowRow`, separadores sin TalkBack, Focus oculto si no hay ejercicios) y el doble toque como sugerencia.
- Pendiente para Martu: botón con borde y texto verdes sin relleno, botón de 44 con 15 SemiBold, título 26 SemiBold, número 14 Bold y nombre de fila 15 SemiBold, y un tamaño `exerciseBadge` de 32 (nodos 1:443, 1:460, 1:506 y 1:508).
- Evidencia: `docs/evidencia/visual/workout-03-{394dp,360dp}.png` (Pixel 10 Pro, con la captura del emulador porque `adb screencap` sale negro en Android 17). `testDebugUnitTest` y `assembleDebug` en verde, `reglas.sh` sin fallas. En el emulador: Workout → Details abre 03 con "Fuerza inicial".
- Commit / PR:

### 2026-10-23 · Zaki · feat(profile): maquetar Profile / 06 Settings según Figma
- Carril y requisitos: B · UI01, UI03, NAV02, NAV03, NAV05
- Plan aprobado: rama 6 de 6, la última pantalla del flujo Profile. Se arma con componentes que ya existían (`VoraListRow`, `ProfileSectionLabel`, `VoraButton` Destructive) y los íconos de la rama 1. Alex Morgan abre Edit profile, las demás filas avisan modo demostración y Log out vuelve al login vaciando la pila.
- Qué propuso la IA y qué cambiamos:
  - La IA dejó "VORA v1.0.0" como texto del Figma y no lo tomó del build (`versionName` es "1.0" y es zona de otra integrante).
  - /code-review encontró que Log out no borraba lo que cada pestaña guardaba al cambiar de pestaña. Al volver a entrar, una pestaña podía abrir una pantalla de la sesión anterior. La IA lo confirmó leyendo `irAPestana` y Zaki eligió arreglarlo con `clearBackStack` de cada pestaña antes de ir al login (NAV02).
  - Con esta pantalla, las 6 rutas de Profile dejan de ser `PantallaVacia`.
- Revisores:
  - revisor-visual: aprobada en la primera vuelta. Quedan menores en componentes compartidos (rojo de `VoraButton` Destructive, margen del título de `VoraTopBarLarge`) y unos 3 dp antes de cada etiqueta de sección.
  - revisor-arquitectura: nada bloqueante.
  - /code-review: el Log out, corregido.
  - Quedan como sugerencias:
    - `launchSingleTop` en las filas que navegan;
    - el nombre completo armado en dos ViewModel;
    - el repositorio en memoria no se reinicia al cerrar sesión;
    - los textos del Figma que pueden leerse raros en la demo ("Renews 14 June 2026", "Apple Watch").
- Evidencia: `docs/evidencia/visual/profile-06-{411dp,360dp}{,-scrolled}.png` (teléfono real). Build y tests unitarios en verde, `control-rapido.sh` sin fallas. Zaki probó en el teléfono que, después de Log out, atrás cierra la app.
- Commit / PR:

### 2026-10-23 · Sofi · feat(nutrition): maquetar Nutrition / 01 Dashboard según Figma
- Carril y requisitos: B · UI01, UI03, NAV03 · tarea m, PR 1 de 8
- Plan aprobado: pantalla 01 con `NutritionViewModel` (compartido con 02) y datos de demostración. Todo texto, incluidos los números de ejemplo, en `strings.xml` y `values-es` como `@StringRes`; solo el avance del anillo y de las barras queda como número en el ViewModel. Voice, Photo, View History, las comidas y "+ Add" de Dinner navegan según NAV03; calendario y Search muestran el aviso demo. Sin Room ni búsqueda de alimentos (tarea l y VC-y).
- Qué propuso la IA y qué cambiamos:
  - La IA propuso un componente `VoraScreenHeader` (etiqueta, título de 24 y acción). El grupo prefirió no crearlo todavía y esperar al revisor; no se hizo y el encabezado quedó privado en la pantalla. Se midieron los encabezados de las 37 pantallas: el patrón sirve para Nutrition 01 y 02 y Workout 01 (con otra etiqueta de 13); Nutrition 03, 06, 07 y 08 piden `VoraTopBar` con un parámetro de estilo de título, no un componente nuevo.
  - Los íconos de cámara y lupa faltaban: Martu no los iba a hacer y se exportaron del Figma como vectores a `res/drawable`.
  - El revisor visual marcó que el círculo del calendario no se veía (`VoraCircleButton` no tiene el tono blanco con borde). Sofi eligió dibujarlo en la pantalla.
  - El revisor marcó "Search" cortado a 360 dp. Se pasó el panel y "+ Add" a `VoraButtonSize.Compact`, que no alcanzó, y después se repartió el ancho con pesos distintos (0,93 / 0,98 / 1,09). Los tres botones ya no miden lo mismo como en el Figma.
  - Se aceptó el aire de más de unos 11 dp en la cabecera de "Today's Meals" (padding del `VoraButton` de texto) y el texto del panel en el 70 % del tema en vez del 60 % del Figma.
  - Las barras de macros copian lo que dibuja el Figma (77, 72 y 77 de 98), que no es el cociente de los gramos.
- Revisores: revisor-visual, tres vueltas (la 2.ª quedó CON CAMBIOS por "Search" y la 3.ª APROBADA); revisor-arquitectura CON CAMBIOS con 2 MEDIA; `/code-review` con 8 observaciones sin bloqueo. Sin aplicar, quedan como sugerencias:
  - `FontWeight.Bold` a mano en las kcal;
  - el círculo del calendario mide 40 y el área táctil mínima es 48;
  - los pesos de los botones están ajustados al inglés a 360 dp y no se probaron en español ni con letra grande;
  - las barras no coinciden con los gramos que muestran;
  - `MealRow` y `PendingMealRow` repiten el diseño;
  - el nombre privado `VoraPanelCard`;
  - `NutritionViewModel` no se comparte entre 01 y 02 si cada Route lo pide con `hiltViewModel()` (para la tarea l).
- La CI (`lintDebug`) falló en el PR por la preview, que construía el `NutritionViewModel` (`ViewModelConstructorInComposable` y `StateFlowValueCalledInComposition`). Local solo corríamos build y tests, no lint. Se pasó el estado de demo a `NutritionDemoData.kt`, que usan el ViewModel y la preview, y ya pasa `lintDebug` en local.
- Pendiente: la lógica de Room (guardar, listar por fecha y total) es la tarea l; ahí los datos de la pantalla pasan de `@StringRes` a valores reales y el estado necesita cargando, vacío y error (RF07). Si el grupo quiere un encabezado general, se decide en este PR.
- Evidencia: `docs/evidencia/visual/nutrition-01-{411dp,360dp}{,-scroll}.png` (emulador de 1080x2424 a 420 dpi y el mismo con `wm size` a 360 dp). `assembleDebug` y `testDebugUnitTest` en verde, `reglas.sh` exit 0.
- Commit / PR:
### 2026-10-23 · Sofi · feat(nutrition): maquetar Nutrition / 02 Today's Meals según Figma
- Carril y requisitos: B · UI01, UI03, NAV03 · tarea m, PR 2 de 8
- Plan aprobado: pantalla 02 con el mismo `NutritionViewModel` que el Dashboard; el estado suma solo las comidas con sus ingredientes (gramos y kcal), con los mismos ids (1, 2 y 3) y las mismas kcal que las filas del Dashboard. Tocar una comida abre `meal_details/{id}` y "+ Add Dinner" abre `add_food`. Con scroll (UI03); la barra la pone el Scaffold.
- Qué propuso la IA y qué cambiamos:
    - La tarjeta punteada "Dinner is not logged yet" no tiene componente (`VoraCard` dibuja su borde sólido por dentro y no se le puede cambiar desde afuera). La IA frenó y planteó pedírselo a Martu o dibujarla en la pantalla. Sofi eligió dibujarla en la pantalla, como `dashedBorder` privado, porque la tarea VC-r (estados de carga, vacío y error de las consultas remotas) no la cubre: es un elemento del estado normal del frame, no el vacío de una consulta.
    - Se midió el borde sobre el nodo 1:925: 4 px de trazo y 4 de hueco, 1 px, color `outline`. Las tres tarjetas con comida son sólidas; solo la de la cena vacía es discontinua.
    - La píldora "1,420 kcal" se rellena en el Figma con verde al 8 %; sobre el fondo da `#E5E9E6`, que es el `outline` del tema, así que se usó ese token en vez de inventar un alfa.
    - El mock del Figma no cierra: Breakfast 420 + Lunch 520 + Snack 240 suman 1.180, y el total que dibuja el Figma (Dashboard y 02) es 1,420. Se dejó 1,420 como pidió el grupo; con Room el total será la suma real.
    - `lintDebug` (que el PR 1 nos mostró que hay que correr) marcó un error propio: "Yogur griego (0% grasa)" se leía como un formato `% g`. Se marcó esa string con `formatted="false"`.
- Revisores: revisor-visual APROBADO en la primera vuelta; revisor-arquitectura sin BLOQUEA ni CRÍTICA; `/code-review` sin bloqueos. Sin aplicar, quedan como sugerencias:
    - `HorizontalDivider` sin `thickness = VoraTheme.sizes.border`;
    - la tarjeta de la cena se dibuja siempre: con Room tiene que mostrarse solo si no hay una cena registrada;
    - `caloriesConsumed` es un `@StringRes` de demostración: con Room pasa a ser un número y el total sale de sumar las comidas;
    - `dashedBorder` es privado de esta pantalla: si otra pantalla necesita un estado vacío igual, conviene subirlo a `ui/components` (Martu);
    - el comentario que explicaba por qué la píldora usa `outline` se sacó a pedido de Sofi, aunque el revisor y `/code-review` lo marcaron: el motivo (verde al 8 % sobre el fondo da `#E5E9E6`) queda en esta entrada;
    - el texto del Figma dice "Today's Meals" con apóstrofo recto en el nodo 1:869; el render lo muestra curvo, no es una diferencia;
    - el listado por fecha de RF04 no tiene frame: hoy la pantalla muestra solo hoy (tarea l).
- Evidencia: `docs/evidencia/visual/nutrition-02-{411dp,360dp}{,-scroll}.png` (emulador de 1080x2424 a 420 dpi y el mismo con `wm size` a 360 dp; al final se devolvió el tamaño). `assembleDebug`, `testDebugUnitTest` y `lintDebug` (0 errores) en verde, `reglas.sh` exit 0. A mano: una comida abre Meal Details, "+ Add Dinner" abre Add Food y atrás vuelve a 02.
- Commit / PR:


### 2026-10-23 · Martu · refactor(components): armazón, pie y botón de volver compartidos en Acceso
- Carril y requisitos: B (componentes) · UI02 · tarea VC-n
- Plan aprobado: aplicar las tres sugerencias que quedaron de `/code-review` en SignIn y SignUp, sin cambiar nada visible, en un PR chico después de que se unieran #20 y #23.
- Qué propuso la IA y qué cambiamos:
  - `VoraAccessScaffold` (fondo hasta el borde, márgenes de las barras y del teclado, scroll y pie abajo) y `VoraAccessFooterLink` ("Don't have an account? Sign up here" y "Already have an account? Sign In") reemplazan la estructura que SignIn y SignUp repetían.
  - El círculo de volver pasa a ser `VoraCircleButton` con un tono nuevo, `Elevated` (blanco con sombra del color del ícono), `tint` opcional y `onClick` que puede ser `null` (la flecha de SignIn se dibuja sin acción y TalkBack no la anuncia como botón).
  - En la primera versión la sombra desaparecía: `VoraCircleButton` recorta el área táctil en círculo para el efecto del toque, y ese recorte se comía la sombra. Se vio comparando las capturas píxel a píxel; para el tono `Elevated` el toque usa un `ripple` redondo sin recortar y los demás tonos siguen igual.
- Revisores: `revisor-arquitectura` sin nada que bloquee; confirmó que los usos de `VoraCircleButton` en Voice y en el chat no cambian (pasan `onClick` y no usan `Elevated`). Se aplicaron sus sugerencias: un solo token para la sombra del volver (`buttonShadow`, había quedado repetido con `accessShadow`), se sacó el ancho de flecha que quedó sin uso, el tono `Elevated` en la preview y en el catálogo (con y sin acción) y nombres claros en vez de `(f, t)`. Quedó como estaba el área táctil de 40 del volver (igual que antes; agrandarla corre el círculo). `/code-review`: se actualizó el KDoc de `VoraCircleButton` (uso en Login y diferencia entre `onClick = null` y `enabled = false`); quedan como sugerencia que `iconSize` sea cuadrado (de ahí el medio píxel de la flecha). Las capturas se repitieron después de estos cambios y siguen idénticas.
- Evidencia: `assembleDebug` y `testDebugUnitTest` en verde, `control-rapido.sh` sin fallas. Capturas de SignIn y SignUp en 394 y 360 dp, con y sin scroll, antes (en `main`) y después: idénticas debajo de la barra de estado, salvo 229 píxeles de antialiasing en la flecha de volver a 394 dp (el ícono pasó de una caja de 10 × 14 a una de 14 × 14 con la misma escala; a 360 dp sale idéntico). Recorrido: la flecha de SignIn no hace nada, la de SignUp vuelve a SignIn y se sigue entrando a Home.
- Commit / PR: `990205e`, `85d62f6` · [#30](https://github.com/sofiaandreaolguin/parcial-tp3/pull/30)

### 2026-10-23 · Martu · feat(home): maquetar Home / 02 Notifications según Figma
- Carril y requisitos: B (pantalla) · UI01, UI02, UI03, NAV02, NAV03, NAV04, RF07, RF08 · tarea VC-n (4 de 4, cierra la tarea)
- Plan aprobado: PR 4 de 4 de la tarea n, en una rama que sale de `main`. Cuatro notificaciones de demostración agrupadas en hoy y ayer, "Mark all as read" y cada una a su destino de `PANTALLAS.md`.
- Qué propuso la IA y qué cambiamos:
  - Las medidas salieron de los nodos del frame Home / 02 y los colores de la imagen exportada (lámina). Se reusó `VoraCard`; se sumaron dos estilos, dos medidas y el ícono de la llama (la mancuerna y la manzana son los de la barra).
  - Las tarjetas salían 1 a 2.5 dp más altas que en el Figma y la última quedaba 8 dp más abajo. Medido línea por línea, la fila del remitente ocupa más en Compose: con 2 (no 4) hasta el título, las cuatro caen a ±3 dp.
  - El título de cada tarjeta salía más liviano y más ancho que en el frame (234 contra 227 dp): pasó a SemiBold con −0.35 de espaciado y mide 229.
  - "Protein target…" usaba la navegación de pestaña y atrás volvía a Home; ahora usa navegación común y vuelve a Notifications, como "Log breakfast" de Home (anotado en `PANTALLAS.md`).
  - Abrir una notificación la marca como leída; la de la racha no tiene destino y no se puede tocar.
- Revisores: `revisor-visual` CON CAMBIOS (peso y ancho del título: corregido; la sección de ayer queda 3 dp abajo y el destello del coach es más fino que el del frame: quedan); `revisor-requisitos` y `revisor-arquitectura` sin nada que bloquee. Se aplicaron: datos de demostración en `NotificationsDemoData` (la preview ya no crea el ViewModel), "Unread" como estado de la tarjeta para TalkBack, minutos para lo de menos de una hora, `MINUTES_PER_HOUR` y el KDoc del ViewModel con lo que de verdad dura el estado de leída. `/code-review`: 3 corregidos, 1 que queda (la notificación dice "Upper Body Strength" y `w01` en la API es "Fuerza inicial", igual que en Home).
- Tests: `NotificationsViewModelTest`, 6 casos (estado inicial con dos sin leer, partición hoy / ayer, `onOpen` marca solo esa, abrir una ya leída no cambia nada, `markAllAsRead` y el destino de cada una). Control negativo: con `onOpen` marcando todas y `markAllAsRead` sin hacer nada fallan 3; restaurado, pasan los 6. Se escribieron en la misma sesión que implementó, a pedido de Martu (la metodología pide otra sesión).
- Evidencia: `assembleDebug` y `testDebugUnitTest` en verde, `control-rapido.sh` sin fallas. Capturas en `docs/evidencia/visual/home-02-{394dp,360dp}.png`. Tarjetas contra los nodos a ±3 dp. Recorrido: dos sin leer al entrar; la del coach va a `weekly_review` y queda leída; "Upper Body Strength" abre `workout_details/w01`; "Protein target" abre Nutrition / 01 y atrás vuelve; la racha no navega; "Mark all as read" deja cero sin leer y sobrevive a la rotación; atrás vuelve a Home.
- Commit / PR:

### 2026-10-23 · Hector · feat(workout): maquetar Workout / 04 Pre Workout según Figma
- Carril y requisitos: B · UI01, UI03, NAV03, NAV04, RF01 (muestra el detalle de demostración) · tarea k, PR 5 de 10
- Plan aprobado: `PreWorkoutScreen` y `PreWorkoutRoute` sobre `WorkoutDetailViewModel`, textos en los dos idiomas y la Route en lugar de `PantallaVacia` en `WorkoutNavGraph`. X → `popBackStack()`, micrófono → `voice`, Start Workout → `active_workout/{workoutId}`. Medidas del nodo 1:510.
- Qué propuso la IA y qué cambiamos:
  - La IA propuso reutilizar lo que había y anotar las diferencias. No había ninguna tarjeta para fondo verde (Voice tiene una copia privada) ni un botón redondo lima. Hector eligió crearlos en `ui/components/` en vez de dejarlos en la pantalla: `VoraCardOnDark` y el tono `Cta` de `VoraCircleButton`, en un commit aparte para que los revise Martu.
  - Para que el verde pase por detrás de la barra de estado, como en el Figma, Hector aprobó agregar `Rutas.PRE_WORKOUT` a `RUTAS_A_SANGRE` (zona de Fiore; se le avisa en el PR).
  - Antes de programar, Hector pidió revisar lo nuevo de `main` (#30, componentes de Acceso): no traía nada para esta pantalla y la rama se adelantó hasta ahí.
  - El nivel no se muestra porque el frame no lo tiene. "YOUR LAST SESSION" usa el nombre del primer ejercicio; las series, el foco y los consejos son de demostración (`pre_workout_*_demo`).
  - El revisor de arquitectura avisó que los íconos de la barra de estado podían quedar oscuros al volver de Voice. Se reprodujo en el emulador (micrófono → atrás: hora y batería en negro sobre verde) y se cambió el efecto para que también se apliquen al reanudar la pantalla.
- Revisores: revisor-visual APROBADA en la primera vuelta (observaciones menores dentro de lo aceptado: la X unos 7 dp corrida, el encabezado 5 dp más bajo por el alto de `VoraTopBar`, el orbe chico con aro). revisor-arquitectura sin nada que bloquee: se corrigieron los íconos de la barra y el KDoc de `VoraCardOnDark`. `/code-review`: además, los íconos se ponen blancos al entrar para que no se vean oscuros durante la animación. Quedan como sugerencia: pasar el `PanelOscuro` de Voice a `VoraCardOnDark`, el reintento del error (RF05, también falta en 03), compartir los datos de la preview con 03 y agregar los componentes nuevos a CONTEXTO §9.
- Pendiente para Fiore: revisar la línea de `RUTAS_A_SANGRE` y ajustar la fila 64 de `PANTALLAS.md` (además de arrancar la sesión, son de demostración el foco, la última sesión y el consejo).
- Evidencia: `docs/evidencia/visual/workout-04-{394dp,360dp}.png` (emulador, densidad 520 y 569). `testDebugUnitTest` y `assembleDebug` en verde, `reglas.sh` sin fallas. Recorrido en el emulador después del arreglo: Pre Workout con íconos blancos → micrófono abre Voice (íconos oscuros sobre claro) → atrás vuelve con íconos blancos → la X vuelve a Workout con íconos oscuros.
- Commit / PR:

- Commit / PR: `ba80834`, `5397a6b`, `a37706b` · [#31](https://github.com/sofiaandreaolguin/parcial-tp3/pull/31). El test de su ViewModel entró después, con los de la tarea (entrada "test(access, home)").

### 2026-10-23 · Martu · test(access, home): tests de los ViewModels de la tarea n
- Carril y requisitos: C (datos y estado) · RF04, RF07, NAV02 · tarea VC-n (cierre)
- Plan aprobado: cubrir con tests unitarios los tres ViewModels de la tarea n, que los revisores marcaron sin test en los cuatro PR: `AuthViewModel`, `HomeViewModel` y `NotificationsViewModel`. Iban a ir en el PR #31, pero se unió antes: van en un PR propio, `test/access-home-viewmodels`.
- Qué propuso la IA y qué cambiamos:
  - **Se escribieron en la misma sesión que implementó**, a pedido de Martu para cerrar la tarea; la metodología pide otra sesión. Para compensar, cada archivo tiene su control negativo: se rompió el código a propósito, se vio fallar el test y se restauró desde git.
  - `AuthViewModelTest` (13): email inválido, contraseña de 7 y de 8, todo vacío, SignIn no pide nombre, email sin espacios, SignUp con nombre en blanco y válido, escribir borra solo su error, `onSwitchScreen` borra errores y conserva lo escrito, `onNavigated`, el tilde de 8 y mostrar contraseña. Control negativo: mínimo en 7, sin `trim`, `isEmpty` en vez de `isBlank` y `onSwitchScreen` sin hacer nada → fallan 5.
  - `HomeViewModelTest` (5) con un `FakeMealRepository` que emite como el Flow de Room: suma del día con redondeo, solo las comidas de hoy, guardar una comida actualiza el total, el nombre sale del perfil y cambia al editarlo, objetivos y recomendado de demostración. `HomeUiStateTest` (4): el anillo, el tope en 1, objetivo en 0 sin dividir por cero y los cortes del saludo. Control negativo: proteínas sumando carbohidratos, pedir las comidas de ayer, sin tope y el corte de las 20 corrido → fallan 5.
  - `NotificationsViewModelTest` (6): ver la entrada de Home / 02.
  - La primera versión de `HomeViewModelTest` fallaba por el test y no por el código: la corrutina que observaba el estado no arrancaba (dispatcher normal de `runTest`) y `WhileSubscribed` nunca se suscribía. Se observa con `UnconfinedTestDispatcher(testScheduler)`, con el porqué en el KDoc.
- Revisores: no se corrieron revisores en contexto limpio sobre los tests.
- Evidencia: `testDebugUnitTest` con 87 tests en verde (28 nuevos), `assembleDebug` y `control-rapido.sh` sin fallas. Los controles negativos se corrieron sobre `HEAD` limpio y se restauró con `git checkout`.
- Commit / PR:

### 2026-10-23 · Hector · feat(workout): maquetar Workout / 06 Exercise Details según Figma
- Carril y requisitos: B · UI01, UI03, NAV03, NAV04, RF01 (muestra el ejercicio elegido del detalle de demostración) · tarea k, PR 6 de 10
- Plan aprobado: `ExerciseDetailsScreen` y `ExerciseDetailsRoute` sobre `WorkoutDetailViewModel` (con `exerciseId` en la ruta ya devuelve el ejercicio elegido), textos en los dos idiomas y la Route en lugar de `PantallaVacia` en `WorkoutNavGraph`. ‹ y "Back to Workout" → `popBackStack()`, "Ask Vora" → `voice`, ayuda → aviso de demostración. Medidas del nodo 1:628.
- Qué propuso la IA y qué cambiamos:
  - El frame trae 5 pasos y 2 cues del remo, y el modelo trae una instrucción y un tip por ejercicio. La IA propuso mostrar los datos reales en lugar del texto del Figma, y Hector lo eligió: así lo que se lee siempre es del ejercicio elegido. No hay textos `_demo` de pasos ni de cues.
  - En el repo no había foto. Hector eligió exportar la del Figma (nodo 1:649) como `drawable-nodpi/exercise_demo.jpg`, igual que `avatar.png`: 1200 × 549 y 90 KB, recortada como en el frame. Es decorativa y la misma para todos los ejercicios.
  - "Rate this workout difficulty" no está en este frame: es de 07 ("Great / Good / Tough"). La ayuda no tiene destino en `PANTALLAS.md`, así que muestra el aviso de demostración.
  - La fila "Ask Vora" quedó como función privada de la pantalla, hecha con colores y medidas del tema. `VoraInfoMessage` es la más parecida, pero tiene borde, no tiene flecha y no se puede tocar.
  - El chevron de "Ask Vora" no se ve en el Figma: el nodo 1:669 mide 100 dentro de un marco de 16 que lo recorta. Se dibuja a 16 y queda anotado en el KDoc.
- Revisores: revisor-visual APROBADA en la primera vuelta; las medidas coinciden a ±1 dp. Avisó que la flecha y la ayuda quedan unos 8 dp más cerca del borde, que viene de `VoraTopBar` y pasa lo mismo en 03 y 04. revisor-arquitectura sin nada que bloquee: se aplicó el `when (state)` que cubre todos los estados. Quedan: el reintento del error (RF05, igual que 03 y 04) y que el detalle de demostración está en castellano. `/code-review`: 6 sugerencias, ninguna aplicada. La más importante es que dos toques seguidos en volver hacen dos `popBackStack()`; es el mismo patrón de 03 y 04, y la navegación es de Fiore. Las otras: la proporción de la foto como token del tema, la foto a 146 dp de alto en 360, el texto del error que habla del entrenamiento, las comillas tipográficas y la fila Ask Vora como componente.
- Evidencia: `docs/evidencia/visual/workout-06-{394dp,360dp}.png` (emulador, densidad 520 y 569). `testDebugUnitTest` (59 tests, 0 fallas) y `assembleDebug` en verde, `reglas.sh` sin fallas. Recorrido en el emulador: Workout → Details → Sentadilla abre 06; la ayuda muestra el aviso; "Ask Vora" abre Voice y atrás vuelve a 06; "Back to Workout" vuelve a 03.
- Commit / PR:

### 2026-10-23 · Hector · feat(workout): implementar la sesión de entrenamiento simulada
- Carril y requisitos: C, paso 3 · NAV03, NAV04, RF07, y RF06 y RF08 en parte · tarea k, PR 7 de 10. Spec: `specs/funcionales/sesion-de-entrenamiento-demo.md` (C1 a C26).
- Plan aprobado: un solo archivo, `ActiveWorkoutViewModel.kt`. La carga en `init` copia a `WorkoutDetailViewModel` (id vacío, falla o detalle sin ejercicios pasan a `Error`). Los siete eventos cambian el estado en el momento y solo en `Content`. El descanso es una sola corrutina en `viewModelScope`. Primero la carga, después ver el rojo de C6 a C26, después los eventos.
- Qué propuso la IA y qué cambiamos:
  - La IA propuso que el descanso lea lo que queda del estado en cada vuelta, en lugar de llevar un contador propio. Así "+30s" suma sin abrir otra corrutina y una corrutina vieja no puede revivir el descanso.
  - Con la carga hecha pasaron con los eventos vacíos C13, C14, C19 y C22, y no C9, C13, C17 y C22 como se esperaba. Los cuatro llevan su control negativo, y C9 uno aparte, porque falló en su preparación.
  - Dos controles de la spec no rompían lo que tenían que romper:
    - C8 y C19 "sin cancelar": con esta implementación no fallan. C19 tampoco falla con un contador propio, porque mira recién a los 61 s. Se controló con un Finish que no corta el descanso. El test no se tocó: queda anotado en la spec para que decida Hector.
    - C23 con `stateIn(WhileSubscribed)`: falla antes de su criterio. Se usó una carga que se repite en cada nueva suscripción, que da `[w02, w02]`.
  - Por los revisores se arreglaron dos comentarios y la cancelación del descanso quedó dentro de `arrancarDescanso()`. Se borraron dos archivos sueltos de los controles que habían quedado en la raíz.
- Revisores: revisor-requisitos, sin nada que bloquee; los 26 criterios se cumplen y no se tocó ningún test. revisor-arquitectura, sin nada que bloquee. `/code-review`: 3 hallazgos, 2 aplicados. Quedan como sugerencia: `Error` sin reintento ni estado vacío (RF05, igual que el detalle), `_uiState.update { }`, una constante para el `1_000` y la forma mezclada de nombrar `Content` y `Loading`.
- Evidencia: `testDebugUnitTest` con 112 tests y 0 fallas (25 de la sesión), `assembleDebug` OK y `reglas.sh` sin fallas. Los controles negativos de C3, C7, C8, C9, C13, C14, C15, C19, C22, C23 y C25 están en la columna "Vi el rojo" de la spec. El de C25: sin `@Inject` falla KSP con `[Hilt] @HiltViewModel annotated class should contain exactly one @Inject ...`.
- Commit / PR:

### 2026-10-23 · Hector · feat(workout): maquetar Active Workout según Figma
- Carril y requisitos: B · UI01, UI03, NAV03, NAV04 · tarea k, PR 8 de 10. Frame: `Workout / 05 — Active Workout`, nodo 1:554. Depende de #36 (el ViewModel): la rama sale de `feat/workout-active-workout`.
- Plan aprobado: `ActiveWorkoutRoute` (Hilt y `collectAsStateWithLifecycle`) y `ActiveWorkoutScreen` sin estado, con lo que dice la tabla "Lo que muestra cada pantalla" de la spec. Componentes del tema aunque no calquen el Figma. En `WorkoutNavGraph` se cambia `PantallaVacia` por la Route, y "Finish Workout" llama a `onFinish()` y después navega a 07. Hector aprobó también sumar `ACTIVE_WORKOUT` a `RUTAS_A_SANGRE` (zona de Fiore, se avisa en el PR), agregar `ic_clock` y usar `OutlinedOnDark` para "+30s".
- Qué propuso la IA y qué cambiamos:
  - El archivo original del Figma no le daba acceso al conector. Hector pasó una copia y con ella se midió el nodo 1:554. Las diferencias con el tema quedaron en el KDoc de `ActiveWorkoutScreen`.
  - En el Figma, el reloj de "Resting" (nodo 1:607) mide 100 dentro de un marco de 16 que lo recorta, así que no se ve. Se dibuja a 16, como el chevron de 06.
  - Los chips de las series son una pieza privada de la pantalla: no hay componente igual (`VoraChip` es una píldora blanca). Se reparten el ancho en vez de medir 110, así entran en 360 dp.
  - Los efectos que ponen blancos los íconos del sistema estaban dentro de `PreWorkoutRoute`. Pasaron a `IconosDelSistema.kt`, que usan 04 y 05. Moverlos a un archivo propio lo sugirió el revisor de arquitectura.
  - El revisor de requisitos pidió confirmar que "Serie n" entra en español a 360 dp. Entra (captura `workout-05-360dp-es.png`). Igual se le sumó `TextOverflow.Ellipsis`, por si la letra es más grande.
- Revisores: revisor-visual APROBADA en la primera vuelta (bloques a ±1 dp). Avisó que "LAST SESSION" queda unos 11 dp más alta por el área táctil de 48 del `VoraStepper`, y quedó en el KDoc. revisor-requisitos: los cuatro requisitos se cumplen y no se tocó ningún test. revisor-arquitectura: nada bloquea; la ALTA es el aviso de zona de `Rutas.kt`, ya aprobado por Hector. `/code-review`: 6 hallazgos, 1 aplicado (el corte de los chips). Quedan como sugerencia: el doble toque en "Finish Workout" apila 07 dos veces (mismo patrón que 03, 04 y 06; es la navegación de Fiore), "Complete Set" sigue activo después de la última serie (el Figma no dibuja ese estado), Loading y Error sin salida propia ni reintento (RF05), las tarjetas de Loading y Error copiadas de 04, "+30s" escrito en el texto y también como constante del ViewModel, y el orden de los efectos de los íconos entre 04 y 05 (en el emulador quedaron bien).
- Evidencia: `docs/evidencia/visual/workout-05-{394dp,360dp,360dp-es}.png` (emulador con densidad 520 y 569; la de 360 dp ya tiene el peso en 47,5 kg). `testDebugUnitTest` (112 tests, 0 fallas) y `assembleDebug` en verde, `reglas.sh` sin fallas. Recorrido en el emulador: 04 → "Start Workout" abre 05 con "Sentadilla"; "Complete Set" pasa a la serie 2 y arranca el descanso; − y + cambian el peso (45 → 47,5 → 45); "+30s" suma; "Skip" oculta la fila; "Finish Workout" abre 07, y al volver con atrás la sesión sigue. Cambiar la densidad recrea la pantalla y el descanso sigue contando.
- Commit / PR:

### 2026-10-24 · Zaki · feat(progress): maquetar Today's Overview según Figma
- Carril y requisitos: B · UI01, UI02, UI03, NAV01, NAV03 · Progress, rama 1 de 6. Frame: `Progress / 01 — Today's Overview`, nodo 1:1682.
- Plan aprobado: un solo `ProgressViewModel` para las 6 pantallas (PANTALLAS §8), con datos fijos en `progressDemoState()`, como `NutritionViewModel`, sin repositorio ni red (Progress es "Simulado"). Todo control sin destino en PANTALLAS muestra el aviso de demostración; los selectores de 02 a 04 quedan fijos en lo que dibuja el Figma.
- Qué propuso la IA y qué cambiamos:
  - El conector de Figma no lee el archivo del grupo ni las copias de otros: pide permiso de edición. Zaki pasó su copia (`RVM3VKG4SlRkNtW9vweQwu`) y las medidas salieron del nodo 1:1682.
  - `VoraAdvice` no coincidía con el frame (etiqueta de 10, texto de 13, destello de 24, separación 8). Ninguna pantalla lo usaba todavía, así que se ajustó al nodo 1:1726: etiqueta 13, texto 14 en 20 (`VoraTheme.text.advice`), destello 16, separación 12. Se sumaron `ringOverview` (60) y `ringStrokeOverview` (4,5) para el anillo: los anillos que ya existían miden 72 y 64.
  - La IA planeaba exportar todos los íconos del flujo en esta rama. La 01 no necesita ninguno nuevo, así que cada rama exporta los suyos.
  - Zaki preguntó por qué el nombre del entrenamiento está en el código y no en `strings.xml`: es un dato del modelo `Workout` (en la app real viene de la API), igual que en `HomeDemoData`. Se dejó así. Queda anotado que en el mock `w01` es "Fuerza inicial" y no "Upper Body Strength", lo mismo que ya pasaba en Home.
  - La IA probó la navegación tocando el teléfono por adb. Zaki pidió que no: las pruebas las hace la persona y al teléfono se entra solo para las capturas.
- Revisores: revisor-visual CON CAMBIOS por una sola diferencia: "Start Workout" mide 33 dp contra 37 del Figma (`VoraButtonSize.Small` tiene relleno 8 y el frame pide 10). Zaki la aceptó como tolerable, sin sumar un tamaño nuevo al botón. revisor-arquitectura sin nada que bloquee; la MEDIA es que se tocó la zona de tema y componentes (se avisa en el PR). Por ese revisor se corrigió en PANTALLAS que `progress_graph` arranca en `todays_overview`. `/code-review`: un hallazgo bajo, sin aplicar: "NOT STARTED" está fijo y no sale del porcentaje. Quedan como sugerencia: atar `ProgressViewModel` al grafo si algún día hace falta estado compartido (hoy cada pantalla tiene su instancia y da lo mismo), el `Workout` repetido con Home, el `fontWeight` sobre estilos del tema y el `modifier` en la Screen. Las filas de categorías miden 18 dp de alto, menos que los 48 recomendados para tocar.
- Evidencia: `docs/evidencia/visual/progress-01-{411dp,360dp}{,-scrolled}.png` (teléfono, 411 dp y 360 dp con `wm density 480`). `testDebugUnitTest` y `assembleDebug` en verde, `reglas.sh` sin fallas.
- Commit / PR:

### 2026-10-24 · Zaki · feat(progress): maquetar Dashboard según Figma
- Carril y requisitos: B · UI01, UI02, UI03, NAV01, NAV03 · Progress, rama 2 de 6. Frame: `Progress / 02 — Dashboard`, nodo 1:1775.
- Plan aprobado: `ProgressDashboardRoute` y `ProgressDashboardScreen` con el mismo `ProgressViewModel` y datos fijos de `progressDemoState()`. `VoraStatTile` ajustado al nodo 1:1797 (12/20/11), tono `VoraTagTone.LimeOnDark` para "CONSISTENT" y tokens `orbInsight`, `areaIcon`, `chevronSmall` y `text.insight`. Las filas Workout, Nutrition y Activity son `ProgressRow`, un componente del flujo (no se estiró `VoraListRow`, que trae fija la flecha gris y el toque); su variante `compact` es la de 03. El selector queda fijo en Week.
- Qué propuso la IA y qué cambiamos:
  - A 360 dp, "4 workouts ↑ 1" no entra en una línea de `VoraStatTile` (116 dp útiles, necesita unos 124). Zaki decidió que la tendencia baje entera a la línea de abajo, sin cortar el valor, y que la otra tarjeta de la fila iguale la altura.
  - La IA probó tres caminos que fallaron: un `Row` simple (el "1" bajaba solo), `weight` con "…" (cortaba "4 worko…") y un `FlowRow` dentro de una fila con `IntrinsicSize.Min` (la tendencia desaparecía, porque el `FlowRow` informa la altura intrínseca de una sola línea). Después de tres intentos se hizo `/clear` y se arrancó con un pedido nuevo que traía lo aprendido.
  - En la sesión nueva, la tarjeta se quedó con el `FlowRow` y la fila dejó de usar alturas intrínsecas. `EqualHeightRow`, privado de la pantalla, mide cada tarjeta con su ancho, toma la más alta y vuelve a componer las dos con esa altura (`SubcomposeLayout`). Queda privado porque se usa en un solo lugar.
  - La IA redactó los commits con cuerpo y separó mal las capturas. Zaki pidió releer `specs/` y la convención real del grupo: título más `Refs:`, el componente en un commit aparte y las capturas en el commit `docs`.
- Revisores: revisor-visual APROBADA, sin diferencias confirmadas; comparó contra `docs/figma/Progress Flow.png` porque en su sesión no tenía el conector. De sus dos dudas, el orbe se descartó (el token sale del nodo 1:1820) y el espacio de 16 entre el selector y la tarjeta quedó como está. revisor-arquitectura sin nada que bloquee: avisa las zonas de Martu (`VoraStats`, `VoraChip`, `Dimens`, `Type`) y de Fiore (`ProgressNavGraph`, `PANTALLAS.md`). `/code-review`: 10 hallazgos, ninguno bloquea, sin aplicar. Quedan como sugerencia: `launchSingleTop` en los cuatro `navigate` (doble toque apila), el área táctil chica de "See weekly review", `InsightPanel` en vez de `VoraPanel`, arreglar las intrínsecas en `VoraStatTile` en vez de en la pantalla, `compact` de `ProgressRow` todavía sin uso (es para 03), un `ProgressViewModel` por pantalla (igual que en 01) y tokens con el mismo valor que otros.
- Evidencia: `docs/evidencia/visual/progress-02-{411dp,360dp}{,-scrolled}.png` (teléfono, 411 dp y 360 dp con `wm density 480`). `assembleDebug` y `testDebugUnitTest` en verde (112 tests, 0 fallas), `reglas.sh` sin fallas.
- Commit / PR:

### 2026-10-24 · Zaki · feat(progress): maquetar Workout Progress según Figma
- Carril y requisitos: B · UI01, UI02, UI03, NAV01, NAV02, NAV03 · Progress, rama 3 de 6. Frame: `Progress / 03 — Workout Progress`, nodo 1:1904.
- Plan aprobado: `WorkoutProgressRoute` y `WorkoutProgressScreen` con el mismo `ProgressViewModel` y datos fijos (`WorkoutProgress` en `progressDemoState()`). Se reusan `VoraTopBarLarge`, `VoraSegmentedControl` (fijo en Week), `VoraStatRow`, `VoraCard`, `VoraBarChart`, `VoraOrb` y `ProgressRow(compact = true)`. Son privados el "···", la tarjeta del récord, la cita con el orbe y la etiqueta de cada ejercicio, porque no se repiten. Se exportaron del Figma `ic_more_horizontal` e `ic_trophy`, y se sumaron dos colores del frame (`highlight` e `highlightIcon`).
- Qué propuso la IA y qué cambiamos:
  - Para el encabezado (‹ en círculo, título a la izquierda y "···", igual en 03, 04 y 05), la IA dio tres opciones. Zaki eligió sumarle `actions` a `VoraTopBarLarge`. Tiene valor por defecto, así que Profile no cambia, y quedan 4 dp de diferencia con el frame (círculo de 32 contra 36, título de 20 contra 18).
  - La IA iba a sumar dos tokens nuevos de 40 (el trofeo y el orbe). Se reusaron `areaIcon` y `orbInsight`, que ya medían 40, para no tener dos nombres para el mismo tamaño (lo había marcado `/code-review` en la rama 2).
  - El volumen por día de la demo (1765, 1970, 0, 2240 y 2445 kg) sale de las alturas de las barras del frame y suma los 8,420 kg de VOLUME.
  - En la primera vuelta el revisor visual marcó la tarjeta Training Volume 18 dp más alta. La IA había tomado como alto de la barra los 80 de la columna del nodo 1:1946, y la barra más alta (1:1965) mide 72; además sumaba 12 de aire. Se volvió al alto por defecto de `VoraBarChart` (72) con 20 de aire arriba y se borró el token `chartHeightVolume`.
- Revisores: revisor-visual CON CAMBIOS en la primera vuelta (la tarjeta del gráfico) y APROBADA en la segunda. Quedan como dudas de pocos dp, toleradas: el interlineado de la cita (19 contra 17), 1,5 dp por fila de ejercicio y el alto del selector. revisor-arquitectura sin nada que bloquee: avisa las zonas de Martu (`VoraTopBarLarge`, colores, íconos) y de Fiore (`ProgressNavGraph`, `PANTALLAS.md`), y por él se actualizó la firma de `VoraTopBarLarge` en `CONTEXTO.md` §9. `/code-review`: 9 hallazgos, ninguno bloquea, sin aplicar. Quedan como sugerencia: los números de VOLUME y la descripción del gráfico son textos fijos y no salen de `volumeKg`, el subtítulo "PER SESSION" (es el texto del Figma), "PROMEDIO" en español pierde la palabra tiempo, el "···" copia el círculo de la flecha (pasarlo a un componente cuando lo use 04), la frase del récord se arma en el código y un `ProgressViewModel` por pantalla (igual que en 01 y 02).
- Evidencia: `docs/evidencia/visual/progress-03-{411dp,360dp}{,-scrolled}.png` (teléfono, 411 dp y 360 dp con `wm density 480`). `assembleDebug`, `testDebugUnitTest` y `lintDebug` en verde (lo mismo que corre la CI), `reglas.sh` sin fallas.
- Commit / PR:

### 2026-10-23 · Sofi · feat(nutrition): maquetar Nutrition / 03 Add Food según Figma
- Carril y requisitos: B · UI01, UI03, NAV02, NAV03, NAV05 · tarea m, PR 3 de 8
- Plan aprobado: `AddFoodViewModel` con un estado vacío y la pantalla con un `VoraPanel` con orbe, `VoraButton` Cta y tres filas. ‹ vuelve, "Start Voice Logging" abre `voice_food_logging`, "Snap a Photo" abre `photo_analysis`, "Log Manually" abre `food_confirmation` sin `mealId` (-1) y "Search Database" muestra el aviso de modo demostración. La barra la pone el Scaffold; con scroll como respaldo (UI03).
- Qué propuso la IA y qué cambiamos:
    - La IA armó `AddFoodViewModel` con un `AddFoodUiState` (un `data object`). El estado quedó vacío a propósito, dentro del mismo archivo del ViewModel (sin `AddFoodUiState.kt` aparte): la pantalla no tiene datos que cambien y el formulario de RF03 probablemente vive en 07. Se dejó por si se llega a usar y porque la columna Controlador de `PANTALLAS.md` asigna el ViewModel a 03. Si la tarea L necesita llevar "qué comida se está cargando" (desayuno, almuerzo, cena) desde las entradas hasta 07, puede ir por un argumento de ruta de `add_food` (`Rutas.kt`, de Fiore) y ahí el estado deja de estar vacío. Queda para la spec de la tarea L.
    - El frame dibuja el orbe a 44 dp y el tema no tiene ese tamaño (`orbSmall` 28, `orbHome` 36). Sofi eligió usar `orbHome` por ahora y ver cómo queda; el revisor-visual lo midió y el orbe queda a 20 dp de los bordes del panel igual que en el Figma. Un token de 44 es pedido a Martu si hace falta.
    - Las filas son `VoraCard` con radio 20 y flecha →, no `VoraListRow` (radio 16, chevron y círculo sin teñir). Sofi aprobó usar el componente que más se parece.
    - El encabezado es una flecha plana con `titleLarge`, como el frame, y no `VoraTopBar` ni `VoraTopBarLarge`. La primera versión dejaba la flecha 20 dp lejos del título y el encabezado 10 dp más abajo (el botón de 48 dp ocupaba lugar); se corrigió centrando el botón en una caja de 20 dp, sin perder el área táctil.
    - Los drawables `ic_camera` e `ic_search` vienen a 12 dp con trazo 1,5; a los 24 dp del frame (nodo 1:986) el trazo sale de 3 dp contra los 2 del Figma. Sofi pidió que se vean menos gruesos y los íconos existentes no se tocan (son de Martu): se dibujan a 18 dp (`iconSmall`, trazo de 2,25), un poco más chicos que los 20 del frame.
    - "Log Manually" usaba `ic_edit` (cuadrado con lápiz) y el frame dibuja solo un lápiz (nodo 1:1007). Con el OK de Sofi se exportó el SVG del Figma (24x24, trazo 2) como `ic_pen.xml`, ícono nuevo que no pisa ninguno existente; se dibuja a 20 dp.
    - Las comillas de la cita van escapadas (`\"`) en los dos `strings.xml`: sin escapar, aapt las descarta.
- Revisores: revisor-visual CON CAMBIOS en la primera vuelta (el encabezado de arriba) y APROBADA en la segunda, con las medidas a ±1 dp. revisor-arquitectura sin BLOQUEA ni CRÍTICA; marcó como ALTA que el orbe no mide 44, pero es la decisión de arriba y el comentario del código lo dice. Se aplicó la anotación `@DrawableRes` y `@StringRes`. `/code-review` (nivel low) volvió sin hallazgos en 6 segundos. Sin aplicar, quedan como sugerencias:
    - `FontWeight.SemiBold` suelto sobre `bodyLarge` en el título de las filas;
    - `QUOTE_WIDTH` de 220 dp es fijo (medido en el nodo 1:972; a 360 dp la cita no se corta);
    - el KDoc de `rememberDemoToast` cita solo Profile y Acceso (Martu);
    - la flecha de las filas queda unos 6 dp más a la derecha y unos 3 dp más grande que en el Figma, y la fila de dos líneas a 360 dp mide 88 dp contra 84: vienen del drawable y del interlineado del tema.
- Evidencia: `docs/evidencia/visual/nutrition-03-{411dp,360dp}.png` (emulador de 1080x2424 a 420 dpi y el mismo a 480 dpi; la densidad se devolvió). `assembleDebug`, `testDebugUnitTest` y `lintDebug` en verde, `reglas.sh` exit 0. A mano: voz abre 04, foto abre 06, manual abre 07, ‹ vuelve al Dashboard y Search muestra el aviso.
- Commit / PR:

### 2026-10-24 · Hector · feat(workout): maquetar Workout Complete según Figma
- Carril y requisitos: B · UI01, UI03, NAV03, NAV04 · tarea k, PR 9 de 10. Frame: `Workout / 07 — Workout Complete`, nodo 1:673. Rama `feat/workout-complete`, sacada de `main` (ya con 05 y el ViewModel).
- Plan aprobado: `WorkoutCompleteRoute` y `WorkoutCompleteScreen` sobre la misma instancia de `ActiveWorkoutViewModel` que 05, como dice la spec (`remember(entrada) { nav.getBackStackEntry(Rutas.ACTIVE_WORKOUT) }`). Reutiliza `VoraOrb`, `VoraCard`, `VoraCoachNote`, `VoraChip` (`Dark`) y `VoraButton`. Son piezas privadas el orbe de victoria (orbe de 120, resplandor menta de 180 y el sello) y la columna de cada dato del resumen: `VoraStatRow` pone la etiqueta arriba y divisores, y el Figma no. "Done" vuelve a Workout / 01 sacando la sesión de la pila.
- Qué propuso la IA y qué cambiamos:
  - Hector eligió que VORA INSIGHT siga siendo de demostración pero nombre el último ejercicio de la sesión, en lugar del "Seated Cable Row" fijo del Figma.
  - El sello del orbe no existía: se exportó del Figma como `ic_badge_check.xml`, una ilustración de tres colores que no se tiñe.
  - "Done" hace `popUpTo(HOME_DASHBOARD)` y no `popUpTo(WORKOUT_DASHBOARD)`: a 03 también se llega desde Home sin pasar por 01, y ahí 01 no está en la pila. Home siempre está en la base.
  - Se sumó `launchSingleTop` a "Finish Workout" de 05, que en el PR 8 había quedado como sugerencia de `/code-review`.
  - Por los revisores: la Route recibe la entrada de 05 y pide el ViewModel con `hiltViewModel(sesion)` como valor por defecto, para respetar Route/Screen de CONTEXTO §3. La traducción de la dificultad pasó a `WorkoutLabels.kt`. El KDoc anota que los márgenes son 24, como en el nodo.
  - `complete_insight_demo` lleva `%%`: con un argumento en el texto, el "8% c" se leía como formato y rompía la app.
- Revisores: revisor-visual APROBADA en la primera vuelta (separaciones y tamaños a ±1 dp; lo más visible es "Done", unos 5 dp más alto por el Large de `VoraButton`). revisor-requisitos: todo cumple, sin tests tocados. revisor-arquitectura: nada bloquea; se aplicó lo de la Route. `/code-review`: 6 hallazgos, 1 aplicado. Quedan como sugerencia: `VoraChip` anuncia los chips como casillas aunque se elige uno solo (componente de Martu), el insight dice "8 %" aunque no se haya hecho ninguna serie, Error sin reintento ni salida (RF05), y el tamaño del orbe sale de `orbMuted * 1.5` (pedirle a Martu un token propio). También la posible excepción de `hiltViewModel(sesion)` durante la animación de salida: no apareció ni con doble toque en "Done" mirando logcat.
- Evidencia: `docs/evidencia/visual/workout-07-{394dp,360dp,360dp-es}.png` (emulador con densidad 520 y 569). `testDebugUnitTest` (112 tests, 0 fallas) y `assembleDebug` en verde, `reglas.sh` sin fallas. Recorrido en el emulador: con el peso en 50 kg y las 9 series hechas, 07 mostró 25 min, 3/3 y 4,500 kg; "Good" quedó elegido y seguía elegido al volver de 05; atrás desde 07 volvió a 05; doble toque en "Done" abrió Workout / 01 una sola vez, sin errores en logcat; atrás desde 01 fue a Home.
- Commit / PR:

### 2026-10-24 · Sofi · feat(nutrition): maquetar Nutrition / 04 Voice Food Logging según Figma
- Carril y requisitos: B · UI01, UI03, NAV02, NAV03, NAV05 · tarea m, PR 4 de 8. Frame: `Nutrition / 04 — Voice Food Logging`, nodo 1:1038 (copia `GRk3uXr8…`). Rama `feat/nutrition-voice-logging`, sacada de `main` con el PR 3 ya unido.
- Plan aprobado: `FoodCaptureViewModel` y `FoodCaptureUiState` (compartidos por 04 a 07, con el borrador de demostración con la forma de `Meal` y los nombres como `@StringRes`), `VoiceFoodLoggingRoute` y `VoiceFoodLoggingScreen` sobre `VoraOrb`, `VoraCardOnDark`, `VoraCircleButton` y `VoraButton`. "Done Speaking" → `food_analysis`, "Type instead" → `food_confirmation` (-1), X → `popBackStack()`. El ViewModel se pide con la entrada del grafo de Nutrition (`remember(entrada) { nav.getBackStackEntry(Rutas.NUTRITION_GRAPH) }`).
- Qué propuso la IA y qué cambiamos:
    - Para que el verde pase por detrás de la barra de estado hizo falta sumar `Rutas.VOICE_FOOD_LOGGING` a `RUTAS_A_SANGRE` (zona de Fiore). Sofi lo aprobó, igual que en Workout 04 y 05; `MainActivity` no se tocó. Se avisa en el PR.
    - El frame dibuja el orbe a 160 y el tema no tiene ese tamaño. Sofi eligió `orbListening` (170) en vez de pedir un token a Martu.
    - Los íconos blancos de la barra de estado se importan de `IconosDelSistemaClaros` (en `ui/screens/workout/`, `internal`). Sofi decidió importarlo ahora y reevaluar antes del push si se mueve a `ui/components/`.
    - El encabezado se arma como el de Workout 04: punto y título propios y `VoraTopBar` solo para la X, que es un `VoraCircleButton` `OnDarkSubtle` de 40 (el frame dibuja 36, y 40 es el token).
    - "Type instead" necesitaba un texto subrayado claro sobre el verde y `TextMuted` se lee apagado. `Enlace` de `VoiceScreen` es privado y no se copió. Como en Workout 04 con `VoraCardOnDark`, Sofi eligió sumar el estilo `VoraButtonStyle.TextOnDark` (color `onDarkMuted`, el mismo del "Cancel" de Voice, y subrayado) en `VoraButton.kt`, en un commit aparte para que lo revise Martu, con una muestra en la preview del botón. Una tercera vuelta del revisor visual, solo sobre este cambio, dio APROBADA; el color lo comparó a ojo y no se midió en píxeles contra el frame.
    - Primera versión: "Done Speaking" fijo al pie, como en Voice. El frame lo pega debajo del panel; se corrigió.
    - Por el revisor visual: la onda del orbe (15 %) entra en el layout de `VoraOrb`, así que el aire sobre y bajo el orbe pasó de 40 y 32 a 24 y 20 (el nodo mide hasta el halo), y "Type instead" quedó a 8 del botón.
- Revisores: revisor-visual CON CAMBIOS en la primera vuelta y APROBADA en la segunda (y en una tercera, sobre `TextOnDark`), con las distancias entre bloques a menos de 2 dp en 411 y 360 dp. Quedan como justificadas: unos 10 dp de la barra de estado del emulador (54 contra 44 del mock de iOS), 8 dp del alto de `VoraTopBar` (56 contra 48), el grosor del anillo del orbe (`VoraOrb`, de Martu) y el radio del panel. revisor-arquitectura sin nada que bloquee. Sugerencias sin aplicar: mover `IconosDelSistemaClaros` a un lugar común (pendiente de la decisión de arriba), pasar `MealDraft` a `Meal` con `String` cuando 07 guarde en Room (no copiar el patrón `@StringRes`), y que la carga o el guardado arranquen en el ViewModel cuando 05 a 07 lo usen de verdad.
- Pendiente para el grupo: el borrador vive mientras haya una pantalla de Nutrition en la pila (`PANTALLAS.md` §7); hay que limpiarlo al salir de 07. Los macros del borrador están en 0 hasta que 07 los dibuje.
- Evidencia: `docs/evidencia/visual/nutrition-04-{411dp,360dp}.png` (emulador de 1080x2424 a 420 y 480 dpi; la densidad se devolvió). `assembleDebug` y `testDebugUnitTest` en verde, `reglas.sh` exit 0. A mano: "Done Speaking" abre 05, "Type instead" abre 07 y la X vuelve a Nutrition / 01; desde Dashboard, el chip Voice abre 04.
- Commit / PR:

### 2026-10-24 · Zaki · feat(progress): maquetar Nutrition Progress según Figma
- Carril y requisitos: B · UI01, UI02, UI03, NAV01, NAV02, NAV03 · Progress, rama 4 de 6. Frame: `Progress / 04 — Nutrition Progress`, nodo 1:2032.
- Plan aprobado: `NutritionProgressRoute` y `NutritionProgressScreen` con el mismo `ProgressViewModel` y datos fijos (`NutritionProgress`, que reusa `ProgressMetric` para la fila de datos y para los hábitos). Se reusan `VoraTopBarLarge`, `VoraSegmentedControl` (Pill fijo en Week y Tabs fijo en Protein), `VoraStatRow`, `VoraCard`, `VoraPanel` y `VoraOrb`. Son privados el círculo de cada día y la tarjeta de hábito. Token nuevo `dayTarget` (32, nodo 1:2082).
- Qué propuso la IA y qué cambiamos:
  - El "···" ya se repetía en 03 y 04. Zaki eligió pasarlo a un componente: `VoraTopBarAction`, que también usa la flecha de `VoraTopBarLarge`, así el círculo queda en un solo lugar.
  - A 360 dp la solapa "Calories" se cortaba ("Calori…"). La causa estaba en el componente: el estilo `Tabs` usaba texto de 14 y el nodo 1:2070 pide 11 SemiBold. Se corrigió solo para `Tabs` (lo usan esta pantalla y el catálogo). Zaki recordó que otra salida es que la celda se adapte al texto, como en el Dashboard de Nutrition; acá no hizo falta.
  - El revisor visual marcó unos 9 dp menos de aire entre el encabezado y el selector. Como `VoraTopBarLarge` lo usan cinco pantallas de Profile, Zaki pidió cambiarlo solo si no tocaba otras pantallas: se sumó el aire en la columna de contenido de 03 y 04, no en el componente. Las etiquetas de `VoraStatRow` un 10 % más angostas se dejaron, porque el componente lo usa Home.
  - La IA sumó primero 8 de aire; revisor-arquitectura vio que el comentario decía 22 y la cuenta daba 20. Se pasó a 10. También se sacó un `fontWeight` repetido en las solapas.
  - En español, "Carbohidratos" no entra en la solapa a 360 dp: se usó "Carbohid.". La IA cambió la densidad del teléfono a 360 dp a pedido de Zaki para que lo revise en español.
- Revisores: revisor-visual APROBADA en la primera vuelta (después del arreglo de las solapas). revisor-arquitectura sin nada que bloquee: avisa las zonas de Martu (`VoraTopBarAction`, `VoraSegmentedControl`, `dayTarget`, KDoc de `text.label`) y de Fiore (`ProgressNavGraph`, `PANTALLAS.md`); por él se actualizó `CONTEXTO.md` §9 y se corrigieron el aire y el peso repetido. `/code-review`: 9 hallazgos, ninguno bloquea; se aplicó el peso repetido. Quedan como sugerencia: el área táctil de 32 del círculo del encabezado (ya era así en `main`), los días repiten la inicial para TalkBack, las iniciales salen de un arreglo de Workout, el aire de 03 y 04 repetido en cada pantalla y una cantidad impar de hábitos dejaría la última tarjeta el doble de ancha.
- Evidencia: `docs/evidencia/visual/progress-04-{411dp,360dp}{,-scrolled}.png` (teléfono, 411 dp y 360 dp con `wm density 480`; tomadas con 8 de aire, el final es 10). `assembleDebug`, `testDebugUnitTest` y `lintDebug` en verde, `reglas.sh` sin fallas.
- Commit / PR:

### 2026-10-24 · Sofi · feat(nutrition): maquetar Nutrition / 05 Food Analysis según Figma
- Carril y requisitos: B · UI01, UI03, NAV02, NAV03, NAV05 · tarea m, PR 5 de 8. Frame: `Nutrition / 05 — Food Analysis`, nodo 1:1077 (copia `GRk3uXr8…`). Rama `feat/nutrition-food-analysis`, sacada de `main` con el PR 4 ya unido.
- Plan aprobado: `FoodAnalysisRoute` y `FoodAnalysisScreen` sobre `VoraOrb`, `VoraCardOnDark`, `VoraStatusDot`, `VoraCircleButton` y `VoraButton` (`TextOnDark`); no se reusó nada de `VoiceScreen`. "Done Speaking" de 04 llama a `FoodCaptureViewModel.onDoneSpeaking()`, que lanza un `Job` con tres `delay` de 700 ms (`ANALYSIS_STEP_MS`) y al final emite `AnalysisDone` por un `Channel`; la Route lo recoge y navega a `food_confirmation` (-1). "Cancel analysis", la X y atrás cancelan el `Job` y vuelven.
- Qué propuso la IA y qué cambiamos:
    - Sofi aprobó el plan tal cual y pidió que la línea de `RUTAS_A_SANGRE` (zona de Fiore) fuera en un commit aparte. Se avisa en el PR.
    - Al terminar, 05 sale de la pila (`popUpTo(FOOD_ANALYSIS) { inclusive = true }`): sin eso, atrás desde 07 dejaba un análisis que ya no corre. Atrás desde 07 vuelve a 04. Queda para Fiore confirmar si prefiere el dashboard.
    - El `BackHandler` de la Route cancela el análisis: sin eso, `AnalysisDone` quedaba en el canal y mandaba a 07 en la captura siguiente.
    - El conector de Figma se quedó sin cupo a mitad del trabajo (`get_design_context`). Las medidas salen de `get_metadata` del nodo (geometría) y los colores se midieron sobre la captura del frame con `scripts/color.py`: punto hecho `#65D6B0`, activo `#C9E86A`, pendiente `#2F554E`, todos iguales en la app.
    - El texto del panel es fijo (`nutrition_analysis_input_demo`) y no sale de `FoodCaptureUiState.transcript`: el frame de 05 trae una frase más corta que la de 04, y se priorizó la fidelidad al Figma (UI01). Cuando 07 use el borrador real, hay que decidirlo de nuevo.
    - El punto de los pasos mide 16 en el frame y `VoraStatusDot` fija 8. Respeta el tamaño que le llega, así que se le pasa `sizes.iconXSmall` sin tocar el componente de Martu.
    - Por los revisores: se pasó `popBackStack` a ser por ruta e inclusivo (un segundo toque no saca también a 04), `launchSingleTop` en el "Done Speaking" de 04, el vaciado del canal al cancelar, `update {}` atómico en el estado y la guarda de `onDoneSpeaking()` contra el doble toque.
- Revisores: revisor-visual APROBADA en la primera vuelta, con todo a 2 dp o menos (la X mide 40 y el frame 36, por `VoraCircleButton`; el panel 64 contra 66; la barra de estado y `VoraTopBar` suman unos 18 dp, como en 04). revisor-arquitectura: nada bloquea; se aplicó lo de los dobles toques y el canal. revisor-requisitos: todo cumple, sin tests tocados; los cinco requisitos pasan a "en curso" y UI01 espera el control visual de Zaki. `/code-review` (nivel medium): 8 hallazgos, ninguno bloquea, 1 aplicado (la guarda del doble toque). Sin aplicar: recoger el evento con `repeatOnLifecycle(STARTED)`, reemplazar el `Channel` por un indicador en el estado, recorrer `AnalysisStep.entries` en vez de repetir el `delay`, un hueco para el título en `VoraTopBar` (zona de Martu), `IconosDelSistemaClaros` importado desde `workout` y el peso SemiBold del paso activo armado con `copy` en la pantalla.
- Tests: `FoodCaptureViewModelTest`, 7 casos con `runTest` y tiempo virtual (no arranca solo, avanza cada 700 ms, navega una sola vez, cancelar a mitad corta, cancelar con el evento ya en el canal, doble "Done Speaking" no duplica y no reinicia). Se escribieron junto con el ViewModel y no antes (el commit en rojo no compilaría). Control negativo: sin el `cancel()` falla 1, con 900 ms en vez de 700 fallan 2, sin cancelar el `Job` anterior falla 1, sin vaciar el canal falla 1 y sin la guarda de `isActive` falla 1.
- Pendiente para el grupo: el borrador sigue sin limpiarse al salir de 07 (ver la entrada de 04). La fila de PANTALLAS.md ya dice Demostración y no cambia. Falta una captura con scroll forzado para UI03 (en horizontal el scroll anda a mano).
- Evidencia: `docs/evidencia/visual/nutrition-05-{411dp,360dp}.png` (emulador de 1080x2424 a 420 y 480 dpi, en el paso "Estimating portions" que dibuja el Figma; la densidad se devolvió). `assembleDebug` y `testDebugUnitTest` en verde, `reglas.sh` exit 0. A mano: 03 → 04 → Done Speaking → 05 → 07 solo, con la barra de 07; Cancel vuelve a 04 y 3 s después sigue en 04; rotar en 05 no reinicia el análisis ni navega dos veces (atrás desde 07 va a 04); atrás desde 07 vuelve a 04.
- Commit / PR:

### 2026-10-24 · Sofi · feat(nutrition): maquetar Nutrition / 06 Photo Analysis según Figma
- Carril y requisitos: B · UI01, UI03, NAV02, NAV03, NAV05 · tarea m, PR 6 de 8. Frame: `Nutrition / 06 — Photo Analysis` (copia `GRk3uXr8…`). Rama `feat/nutrition-photo-analysis`, sacada de `main` con el PR 5 ya unido.
- Plan aprobado: `PhotoAnalysisRoute` y `PhotoAnalysisScreen` sobre `VoraCard`, `VoraTag` y `VoraButton` (Primary y Outlined), con `FoodCaptureViewModel` ampliado con el borrador de la foto. ‹ → `popBackStack()`, "Review & Add" → `food_confirmation` (-1), "Retake Photo" → aviso de demostración. Todo dentro de Nutrition: ningún control va a `voice`. La barra la pone el `Scaffold`.
- Qué propuso la IA y qué cambiamos:
    - El conector de Figma se quedó sin cupo en la primera consulta (plan Starter). Las medidas salen de la captura del frame que pasó Sofi y se midieron con PIL contra las capturas del emulador (frame a 2,015 px/dp). No hubo lectura del nodo.
    - La foto la exportó Sofi del nodo como `meal-photo-container.jpg` (1050 x 660), con las esquinas redondeadas ya dibujadas en blanco. Se recortó 22 px a los lados y 14 arriba y abajo para sacarlas, y quedó en `drawable-nodpi/meal_photo_demo.jpg`: 1006 x 632 y 98 KB, con la proporción del frame (1,59). Es decorativa (`contentDescription = null`): las etiquetas y la tarjeta ya dicen qué hay en el plato. No pisa `avatar.png` ni `exercise_demo.jpg`.
    - Las dos etiquetas sobre la foto van como `VoraTag` `Dark` y se ubican en proporción a la foto (pollo 0,114 de ancho y 0,323 de alto, arroz 0,514 y 0,55), no en dp, así que a 360 dp no se corren ni se cortan (tampoco en español).
    - El borrador de la foto es `PhotoMealDraft` en `FoodCaptureUiState.kt`: misma forma que `Meal` y con ingredientes (pollo 180 g, arroz 150 g, verduras 100 g; 520 kcal, P 38, C 54, F 14). `MealDraft` ganó `ingredients`, vacío por defecto, así el de voz no cambia. Lo carga `FoodCaptureViewModel.onPhotoCaptureStarted()`, y lo llama el grafo antes de navegar desde "Photo" (01) y "Snap a Photo" (03), no la composición de 06. Eso toca `NutritionNavGraph.kt` (zona de Fiore), igual que cada reemplazo de `PantallaVacia`: se avisa en el PR.
    - El encabezado de 03 (flecha plana y título) se movió a `NutritionHeader.kt` para usarlo en 06. El diff de `AddFoodScreen.kt` es solo ese cambio.
    - Medidas que no cayeron en el rol esperado del tema: "520 kcal" mide 106 dp de ancho en el frame, o sea 28 SemiBold (`headlineMedium`) y no los 24 de la cifra destacada; la cita es de 12 con 16 de interlineado (`footnote` con `lineHeight` de 16); "VORA ESTIMATE" es de 13 y "Portions are estimated" de 11. En español la nota se acortó a "Porciones estimadas": con la versión larga quedaba pegada al título a 360 dp.
    - `VoraTag` no tenía los fondos de P (verde al 8 %, `#ECF0EF`) ni de F (lima al 12 %, `#F8FCED`). Sofi eligió sumarlos ella en un commit aparte para que lo revise Martu, como se hizo con `TextOnDark` en el PR 4: `VoraTagTone.Green` y `VoraTagTone.Lime`, con los colores del tema y su alpha, texto verde, y una muestra en la preview de `VoraChip.kt`. P usa `Green`, C sigue en `Mint` y F usa `Lime`; los fondos medidos en la captura del emulador coinciden con el frame. Sigue pendiente para Martu la forma: en el frame las etiquetas son rectángulos de esquina chica con ~8 dp de padding, y `VoraTag` las dibuja como píldora con 10.
- Revisores: revisor-visual, primera pasada incompleta (no pudo abrir el frame por el espacio raro del nombre del archivo); segunda CON CAMBIOS (tamaño de "520 kcal", texto de la cita y separación de la tarjeta; más la forma y el padding de `VoraTag`), todo medido y aplicado menos lo de `VoraTag`; tercera APROBADA, con la tarjeta a 98,3 dp contra 98,3 del frame y el resto a 1,5 dp o menos. revisor-arquitectura sin BLOQUEA ni CRÍTICA. Sugerencias sin aplicar: la etiqueta del arroz no tiene tope de ancho, y el segundo test del borrador (llamarlo dos veces) es casi redundante. No se corrió revisor-requisitos ni `/code-review`.
- Tests: `FoodCapturePhotoDraftTest`, 4 casos (el borrador cambia a `PhotoMealDraft`, volver a entrar lo deja igual, las kcal de los ingredientes suman las del borrador y los macros son 38, 54 y 14). Los 7 de `FoodCaptureViewModelTest` no se tocaron. Control negativo: sin el cuerpo de `onPhotoCaptureStarted()` fallan 2 de 4.
- Pendiente para el grupo:
    - 07 sigue siendo `PantallaVacia`: "los mismos números que 06" se comprueba en el PR 7, que va a leer `draft` y los ingredientes.
    - El borrador de voz no se reinicia: una captura de voz posterior a una foto conserva el de la foto. Se resuelve en 07, junto con la limpieza al salir (ver entradas de 04 y 05).
    - Martu: revisar los dos tonos nuevos de `VoraTag` y decidir la forma y el padding (arriba). Fiore: en español a 360 dp la barra inferior corta "Entrenamiento" como "Entrenamie…".
- Evidencia: `docs/evidencia/visual/nutrition-06-{411dp,360dp,360dp-es}.png` (emulador de 1080x2424 a 420 y 480 dpi; la densidad se devolvió). `assembleDebug` y `testDebugUnitTest` (123 tests, 0 fallas) en verde, `reglas.sh` exit 0. A mano: "Photo" de 01 y "Snap a Photo" de 03 abren 06; "Retake Photo" muestra el aviso; "Review & Add" abre 07; atrás desde 07 vuelve a 06 y ‹ vuelve al origen; la barra marca Nutrition; el paquete instalado no declara permisos de cámara ni micrófono.
- Commit / PR:

### 2026-10-24 · Zaki · feat(progress): maquetar Health Trends según Figma
- Carril y requisitos: B · UI01, UI02, UI03, NAV01, NAV02, NAV03 · Progress, rama 5 de 6. Frame: `Progress / 05 — Health Trends`, nodo 1:2156.
- Plan aprobado: `HealthTrendsRoute` y `HealthTrendsScreen` con el mismo `ProgressViewModel` y datos fijos (`HealthTrends`, `SignalKind`, `HabitSignal`). Se reusan `VoraTopBarLarge` (con el subtítulo nuevo), `VoraTopBarAction`, `VoraStatRow(alignStart = true)`, `VoraCard` y `VoraBarChart` con barras angostas. La cita con el orbe y la etiqueta de sección, que ya se repetían en 03 y 04, pasaron a `ProgressRow.kt` como `ProgressInsight` y `ProgressSectionLabel`. Es privada solo la fila de señal.
- Qué propuso la IA y qué cambiamos:
  - El encabezado del frame trae un subtítulo. Zaki propuso sumar `subtitle` a `VoraTopBarLarge`: viene en `null` y sin él el encabezado arma lo mismo que antes. Para confirmarlo hay que mirar Settings y la 03.
  - Del frame salen seis íconos. La IA comparó cada SVG con los que ya había: el pulso, la mancuerna y la manzana son `ic_activity`, `ic_tab_workout` e `ic_tab_nutrition` a otra escala, así que se exportaron solo `ic_bed`, `ic_navigation` e `ic_trending_up`.
  - Los pasos de la demo salen de las alturas de las barras: promedian los 7,842 de la fila y solo miércoles y viernes llegan a 8000, las dos barras oscuras. El color de cada barra se calcula contra la meta y "Goal: %1$,d" toma el mismo número, con separador de miles según el idioma.
  - El revisor visual midió 4 dp de más entre el subtítulo y la fila de datos: los 10 de aire de 03 y 04 están pensados para un encabezado sin subtítulo. Zaki pidió corregirlo sin capturas nuevas: en la 05 son 6.
  - Por los revisores se actualizaron el KDoc de `VoraTopBarLarge` (ahora nombra Progress y el subtítulo) y la fila de §9 de `CONTEXTO.md`.
- Revisores: revisor-visual APROBADA en la primera vuelta; las barras dejan más margen a los costados por el reparto de `VoraBarChart` (ya decidido). revisor-arquitectura sin nada que bloquee: avisa las zonas de Martu (`VoraTopBarLarge`, los tres íconos) y de Fiore (`ProgressNavGraph`, `PANTALLAS.md`). `/code-review`: 8 hallazgos, ninguno bloquea; se aplicó el KDoc. Queda como sugerencia: "7,842" y la descripción del gráfico para TalkBack son textos fijos que no salen de `steps`, la regla de "llegó a la meta" se calcula en la pantalla y no en el estado, el subtítulo no tiene tope de líneas y "Consistent" y "Stable" quedan los dos como "Estable" en español. Se descartó que el ícono de "Consistent" no fuera el del Figma: el SVG del nodo 1:2243 es el mismo pulso que `ic_activity`.
- Evidencia: `docs/evidencia/visual/progress-05-{411dp,360dp}{,-scrolled}.png` (teléfono, 411 dp y 360 dp con `wm density 480`; tomadas con 10 de aire bajo el encabezado, el final es 6). `assembleDebug`, `testDebugUnitTest` y `lintDebug` en verde, `reglas.sh` sin fallas.
- Commit / PR:

### 2026-10-24 · Zaki · feat(progress): maquetar Weekly Review según Figma
- Carril y requisitos: B · UI01, UI02, UI03, NAV01, NAV02, NAV03 · Progress, rama 6 de 6. Frame: `Progress / 06 — Weekly Review`, nodo 1:2287.
- Plan aprobado: `WeeklyReviewRoute` y `WeeklyReviewScreen` con el mismo `ProgressViewModel` y datos fijos (`WeeklyReview`, `ReviewPoint`). Se abre encima, sin barra y verde hasta el borde: `WEEKLY_REVIEW` entra en `RUTAS_A_SANGRE` y la Route pone blancos los íconos del sistema. Se reusan `VoraTopBarLarge`, `VoraCardOnDark`, `VoraCard`, `VoraStatusDot`, `VoraOrb` y `VoraButton` (`Cta` y `OutlinedOnDark`). Navega a `voice` ("Ask VORA", "Get dinner ideas") y a `weekly_plan` con `launchSingleTop`; "Done" y ‹ vuelven.
- Qué propuso la IA y qué cambiamos:
  - La IA propuso dejar `IconosDelSistemaClaros` en `ui/screens/workout/` (Nutrition ya lo importaba de ahí). Zaki eligió moverlo a `ui/components/` pidiendo comprobar que todo siguiera igual: solo cambió el paquete y cuatro imports (Pre Workout, Active Workout, Voice Food Logging y Food Analysis), y Zaki lo probó en el teléfono con el mock levantado.
  - Para el encabezado oscuro la IA recomendó uno privado; Zaki eligió una variante del componente: `onDark` en `VoraTopBarLarge` y `VoraTopBarAction`, con `false` por defecto para que Profile y Progress 03 a 05 no cambien. El ícono de alerta del frame no era `ic_info` (círculo relleno con "i"), así que se exportó `ic_alert_circle`.
  - El token del círculo del check se iba a llamar `checkCircle`, pero ya existía uno de 20 que usan otras pantallas: quedó `winCheck` (24).
  - Por `/code-review`, la etiqueta en español pasó de "ENTRENAMIENTOS" a "ENTRENOS" (la palabra larga se podía partir a 360 dp) y las viñetas de las recomendaciones se alinearon a la primera línea.
- Revisores: revisor-visual CON CAMBIOS por "7,842 steps" a 360 dp (aceptado por Zaki); el resto coincide con el frame dentro de unos 2 dp. revisor-arquitectura sin nada que bloquee: avisa las zonas de Martu (`VoraTopBarLarge`, `VoraTopBarAction`, `IconosDelSistema`, `winCheck`), de Fiore (`Rutas.kt`, `ProgressNavGraph`), de Hector y de Sofi (imports); por él se actualizó `CONTEXTO.md` §9 y se acomodó el KDoc de `IconosDelSistema`. `/code-review`: 10 hallazgos, ninguno bloquea; se aplicaron la etiqueta en español y las viñetas. Quedan como sugerencia: el área táctil chica de los links con flecha, el link con flecha repetido entre 02 y 06, un segundo estilo de título de sección, el aire bajo el subtítulo puesto a mano, los textos `pant_progress_0x` sin uso, `recommendations` sin `@StringRes`, la letra de `VoraButton` 1 a 2 sp más grande que el frame y que "View next week's plan" deja una pantalla de Workout en la pila de Progress (lo pide `PANTALLAS.md`; conviene hablarlo con Fiore).
- Evidencia: `docs/evidencia/visual/progress-06-{411dp,360dp}{,-scrolled}.png` (teléfono, 411 dp y 360 dp con `wm density 480`). `assembleDebug`, `testDebugUnitTest` y `lintDebug` en verde, `reglas.sh` sin fallas. Sin verificar en pantalla: la 06 en español a 360 dp ("ENTRENOS" entra en la cuenta, unos 50 dp en una columna de 90).
- Commit / PR:

### 2026-10-24 · Sofi · feat(nutrition): guardar, editar, eliminar y listar por fecha las comidas de Room
- Carril y requisitos: C, paso 3 (implementar) · RF03, RF04, RF06, RF07, RF08 · tarea VC-l. Spec: `specs/funcionales/carga-y-eliminacion-de-comidas.md`.
- Plan aprobado: repositorio con `observeMeal`, `updateMeal` y `deleteMeal` y la traducción de errores a `MealDataException`; `validateMeal` en `domain/`; 07 en `FoodCaptureViewModel`, 08 en `MealDetailViewModel` nuevo y 01/02 en `NutritionViewModel` atado al grafo, leyendo Room por fecha con un `DatePickerDialog` de Material 3. `catch` en `HomeViewModel` (C44). 07 y 08 siguen como `PantallaVacia` hasta los PR 7 y 8 de VC-m. Sin subir la versión de la base.
- Qué propuso la IA y qué cambiamos:
  - Antes de implementar, la IA avisó que C10 a C12 no se iban a poder ver en rojo: el DAO ya tenía las anotaciones de Room desde la sesión de tests, y Room genera esa implementación. Se corrieron igual con el emulador (pasaron, y C15 falló por la razón correcta) y se les hizo control negativo rompiendo las queries. En la spec quedó "no" en "Vi el rojo", con el motivo.
  - Para el caso que la spec no cubre (falla la lectura de la comida al abrir 07 para editar), la IA propuso mostrar `saveError` con el texto de carga, y Sofi lo aprobó con el plan.
  - 02 sin ingredientes guardados: la tarjeta del Figma pasa a ser un grupo por tipo, con la suma de kcal en el encabezado y una línea por comida. La tarjeta punteada "not logged yet" se reusa con el primer tipo pendiente ("Breakfast is not logged yet" en un día vacío), sin estados nuevos.
  - Se borraron `NutritionDemoUiState` y sus clases, y los textos de demostración que quedaron sin uso, en los dos idiomas. Los números de 01 pasaron a textos con formato (`%1$,d`), así el separador de miles cambia con el idioma.
  - Por revisor-arquitectura (BLOQUEA): la IA había hecho que el grafo pidiera `NutritionViewModel` y se lo pasara a las Routes. Se corrigió para que cada Route lo pida con `hiltViewModel(grafo)`, como 04 a 06 (CONTEXTO §3, decisión 004). Por la ALTA, el repositorio traduce también un `SQLiteException` al leer.
- Revisores: revisor-requisitos sin nada que bloquee (C1 a C50 cumplen con su test, C52 a mano, C51 parcial como se acordó). revisor-arquitectura: 1 BLOQUEA y 1 ALTA, corregidas en la segunda vuelta. `/code-review`: 10 hallazgos, ninguno rompe la spec. Sofi pidió aplicar uno: después de borrar, "Duplicate" ya no vuelve a crear la comida (guarda en `MealDetailViewModel`). Quedan como sugerencia:
  - la carga de la comida al editar pisa lo que se tipee antes de que llegue;
  - si 07 se deja sin `onDiscard`, reabrirla con -1 devuelve el formulario viejo (el PR 7 tiene que llamar a `onDiscard` desde ‹ y atrás);
  - el porcentaje del anillo se topa en 100 %;
  - `toFormText` puede dar notación científica;
  - "hoy" se fija al crear el ViewModel;
  - `LoadingCard` se importa de Workout (moverlo a `ui/components` es zona de Martu);
  - 02 dice "Today's Meals" aunque se elija otra fecha.
- Evidencia: `docs/evidencia/tests/2026-10-24-vc-l-verde.md` (169 unitarios y 12 instrumentados en verde, controles negativos de la validación, de C44 y de C10 a C12) y `docs/evidencia/estados/nutrition-0{1,2}-*.png`. Tests en rojo en el commit `910f83e`.
- Commit / PR:

### 2026-10-25 · Martu · feat(firebase): cuenta, perfil en Firestore y reglas por usuario (extra aa)
- Carril y requisitos: C · extra aa (Firebase: Auth 4, Firestore 4, reglas 2), RF06, RF08, NAV02 · Acceso y Profile. Spec: `specs/funcionales/firebase-auth-perfil.md` (C1 a C41).
- Plan aprobado: una sola rama larga `feat/firebase-extra` y un único pull request general al final (no uno por etapa). Spec en EARS entrevistada y auditada, tests en rojo en una sesión, implementación en otra, `google-services.json` ignorado con plantilla `.example` y copia en la CI.
- Qué propuso la IA y qué cambiamos:
  - Decisiones de la entrevista: perfil nuevo con nombre y email reales y el resto con valores por defecto (confirmados por Martu); el nombre completo se divide en la primera palabra y el resto; el acceso de demostración se reemplaza por Firebase. Martu pidió dejar los valores por defecto en lugar de vacíos, porque `Profile` no admite campos vacíos sin tocar seis pantallas ajenas.
  - El auditor de la spec marcó como CRÍTICA que reemplazar el acceso demo arriesga Navegación y Figma sin `google-services.json`. Martu eligió igual Firebase siempre y la IA lo dejó anotado como riesgo en el Historial de la spec; se mitiga con `app/google-services.json.example` y el paso de la CI que lo copia (C40).
  - Martu eligió dejar la caché de Firestore y las escrituras en cola, en lugar de desactivar la persistencia y esperar al servidor. Por eso C19 no espera la confirmación y C22 cubre el rechazo posterior.
  - Se borró `AuthViewModelTest` (13 tests del acceso demo) porque chocaba con la spec; lo reemplaza `AuthFirebaseViewModelTest`. Se agregó C16 en `test/` (`LogOutNavigationTest`) y se descartó la versión de `androidTest/`.
  - Los commits no se hicieron uno por paso: la implementación quedó junta y se agrupó en commits por pieza independiente (build, reglas, código, tests, docs).
- Revisores: revisor-requisitos sin BLOQUEA ni CRÍTICA (41 criterios cumplen; C26 a C29 con evidencia manual pendiente). revisor-arquitectura: 1 BLOQUEA (`MainActivity` recargaba el perfil desde la composición en cada rotación, RF07) y 2 ALTA (C35 incompleto; observador perdido si `load` falla), corregidos en la vuelta 1 con un `SessionViewModel`. `/code-review`: 10 hallazgos; se corrigieron los cuatro principales en la vuelta 2 (observador tras registrarse, guardado perdido sin perfil cargado, listener que pisaba Goals y observador que moría ante un error). Quedan como sugerencia: sesión abierta tras el timeout de 10 s, fecha de nacimiento y género de solo lectura en Edit Profile, Home con datos por defecto mientras restaura, errores de Auth sin mapear, `ProfileRepository` como `open class` con perfil DEMO, `navigation-testing` sin uso en el catálogo, y el espacio al final del nombre completo cuando falta el apellido.
- Evidencia: `docs/evidencia/tests/2026-10-25-aa-firebase.md` (tests en rojo y en verde, 206 en verde). Commits de la spec y de los tests en rojo: `15b934f` y `6c5b8e1`. Reglas con dos cuentas (C26 a C29): `docs/evidencia/firebase/` (simulador de la consola, las cuatro pruebas dieron lo esperado). Probado en el emulador con Firebase real: arranque en SignIn, registro, inicio de sesión y creación de `users/{uid}` (C23). Martu también probó a mano en el emulador editar el perfil, cerrar sesión y volver a entrar, y rotar la pantalla: todo salió bien.
- Commit / PR:

### 2026-10-25 · Martu · feat(profile): elegir fecha de nacimiento y género en Edit Profile (extra aa, etapa 1)
- Carril y requisitos: B · UI01, UI03, RF06, RF08 y C19, C21 de `specs/funcionales/firebase-auth-perfil.md` · frame `Profile / 02 — Edit Profile`.
- Plan aprobado: conectar `onBirthDateChange` y `onGenderChange`, que el ViewModel ya tenía, a `EditProfileScreen` y `EditProfileRoute`. La fecha abre el `DatePickerDialog` de Material 3 y el género un menú desplegable con las 4 opciones de `Gender`. Los campos mantienen el aspecto del Figma: solo se agrega la interacción.
- Qué propuso la IA y qué cambiamos:
  - El `code-review` de la tarea aa había dejado como sugerencia que fecha y género eran de solo lectura. Martu eligió diálogo de fecha más menú desplegable, en vez de un diálogo de opciones para el género, para quedar más cerca del campo del Figma.
  - Para no duplicar el calendario se generalizó el de Nutrition (`NutritionDatePickerDialog`) con tres parámetros que tienen valores por defecto iguales a los de antes (fechas seleccionables y textos de los botones), y se usa sin fechas futuras. Es zona de Sofi: Nutrition se comporta igual, pero conviene avisarle.
  - El revisor visual pidió cambios en la primera vuelta por el menú de género (ancho del campo, fondo blanco y radio 12). Se corrigió y lo aprobó en la segunda.
- Revisores: revisor-visual, con cambios en la primera vuelta y aprobada en la segunda.
- Evidencia: `docs/evidencia/visual/edit-profile-*.png` (campo, calendario y menú de género, en dos tamaños). 208 tests en verde y `reglas.sh` sin fallas. Tests nuevos en `EditProfileViewModelTest` (32 líneas, con su rojo); los existentes no se modificaron.
- Commit / PR: `752494e` y `5eaad13` en `feat/firebase-extra` (el pull request es el general de la tarea aa).

### 2026-10-25 · Martu · fix(firebase): nombre de respaldo, errores de Auth y dependencia sin uso (extra aa, etapa 2)
- Carril y requisitos: A/C · C42 y C43 de `specs/funcionales/firebase-auth-perfil.md` (criterios nuevos).
- Plan aprobado: tres arreglos que dejó el `code-review` de la tarea aa como sugerencia: nombre para mostrar, mapeo de errores de Auth y una dependencia de test sin uso.
- Qué propuso la IA y qué cambiamos:
  - Home, Profile y Settings armaban el nombre con `"$firstName $lastName"`, que dejaba un espacio sobrante o `" "` si el nombre estaba vacío (cuenta creada fuera de la app, C23). Un solo helper recorta y, si no hay nombre ni apellido, muestra la parte del email antes de la `@`. Es solo para mostrar: el documento sigue con el nombre vacío y C23 no cambia.
  - `ERROR_INVALID_EMAIL`, `ERROR_USER_DISABLED` y `ERROR_OPERATION_NOT_ALLOWED` caían en el error genérico. Ahora tienen error propio (`INVALID_EMAIL`, `USER_DISABLED`, `EMAIL_AUTH_DISABLED`) y mensaje en `values/` y `values-es/`.
  - Se sacó `androidx.navigation:navigation-testing` de `androidTest`: ningún test de esa carpeta lo usa (la prueba de C16 vive en `test/`).
  - La etapa 3 (Home y Profile mientras restaura la sesión) y la 4 (`ProfileRepository` como interfaz) quedaron fuera por tiempo y por tocar zonas ajenas.
- Revisores: revisor-requisitos, revisor-arquitectura y `/code-review` corrieron al final sobre toda la rama (etapas 1 y 2 incluidas): sin BLOQUEA ni CRÍTICA, 43 criterios cubiertos. Se corrigieron tres hallazgos baratos: la carrera de `edited` en `EditProfileViewModel` (guardar sin editar con un perfil que se recorta dejaba la pantalla abierta), `ProfileRepository.saveEdit` pasó a `_profile.update { }` y se sacó código muerto (`isSaving` de Edit Profile y dos strings; 220 tests en verde, con `EditProfileGuardarSinEditarTest` nuevo). Quedan como sugerencia: documento con un campo de otro tipo (C38), `SessionState.Restoring` sin usar en la UI, mover el diálogo de fecha de Nutrition a `ui/components/` y el scope de `ProfileRepository` sin inyectar.
- Evidencia: 219 tests en verde (eran 208), `assembleDebug` y `scripts/reglas.sh` sin fallas. Tests nuevos: `AuthErrorMappingTest`, `DisplayNameTest`, `HomeNombreDeRespaldoTest` y `NombreDePerfilViewModelTest`, con su rojo (no compilaban antes). Ningún test existente se modificó.
- Commit / PR: sin commitear al escribir esta entrada (el pull request es el general de la tarea aa).

### 2026-10-25 · Hector · feat(workout): leer el detalle real y guardar catálogo y detalle en Room
- Carril y requisitos: C, paso 3 (implementar) · RF01 (parte 2), RF02, RF05 (parcial), RF06, RF07, RF08 (parcial). Spec: `specs/funcionales/detalle-y-cache-de-entrenamientos.md` (C1 a C31). Rama `feat/workout-detalle-cache-room`.
- Plan aprobado: mappers DTO ↔ modelo ↔ Entity en `WorkoutMappers.kt`; cuerpos de `saveSummaries` (upsert parcial que conserva `description`) y `saveDetail` (reemplaza los ejercicios) en `WorkoutDao`; los dos repositorios con red primero y Room de respaldo; el 404 no cae a la caché; `WorkoutNotCachedException` solo con una `IOException` que no es `MalformedJsonException` ni `EOFException` (se atrapan antes). `NotAvailableOffline` en el ViewModel y en 03, 04 y 06, dibujado como el `Error` de cada pantalla. El `@Binds` pasa a `WorkoutDetailRepositoryImpl` y se borra la demo con su test. `VoraDatabase` no se tocó (ya estaba en versión 3 desde el rojo).
- Qué propuso la IA y qué cambiamos:
  - Hector pidió sumar las capturas de C29 y C31 en esta etapa, con el emulador y el mock prendidos. Para "apagar el servidor" la IA cortó `adb reverse` en lugar de matar el proceso del mock. A 06 sin conexión no se llega por el flujo normal: la IA restauró la pila después de matar el proceso y sacó w03 de la caché editando `vora.db` (documentado en la evidencia).
  - En el emulador la IA vio que C31 pedía algo que 01 no hace: 01 destaca solo el primero del catálogo (`TodayPanel` con `firstOrNull()`). Hector eligió corregir el texto del criterio (01 muestra el primero desde Room y 02 los tres) en vez de cambiar la pantalla.
  - Al probarlo, Hector preguntó por los "25 min" de 07: es la duración planificada del servidor, como decidió la spec de la sesión. La IA propuso una tarea aparte para el tiempo real; Hector la descartó.
  - Ningún test se modificó. Se actualizaron las specs vecinas: D1 a D3 retirados y C8 y C25 reemplazados por C30.
- Revisores: revisor-requisitos sin nada que bloquee (los 31 criterios cumplen; volvió a correr los 189 tests). revisor-arquitectura sin BLOQUEA ni ALTA: las dos MEDIA ya estaban previstas por la spec (errores propios para la tarea r, avisarle a Sofi). `/code-review`: 6 hallazgos, ninguno bloquea. Por los revisores se ordenó el Historial de `detalle-de-entrenamiento-demo.md`. Quedan como sugerencia:
  - una `IOException` que no sea de conexión (por ejemplo `ProtocolException`) también cuenta como "sin conexión";
  - `desdeRoom` lee la fila y los ejercicios en dos consultas sin transacción;
  - se guarda con el id que manda el servidor y se lee con el de la ruta;
  - el `try`/`catch` con `CancellationException` se repite cuatro veces (un helper le serviría a la tarea r);
  - `HTTP_NOT_FOUND` propio en vez de `HttpURLConnection.HTTP_NOT_FOUND`;
  - comentarios que repiten o quedaron viejos (`// se sigue con lo de la red`, "hace fallar toda la consulta" en `WorkoutMappers.kt` y `WorkoutRepositoryImpl.kt`);
  - `ErrorMessage(text: Int)` se leería mejor como `textRes`;
  - 05 con `WorkoutNotCachedException` va al `Error` común (sin cambios, como dice la spec);
  - el timeout como "sin conexión" no tiene test propio.
- Evidencia: `docs/evidencia/tests/2026-10-25-detalle-y-cache-verde.md` (189 unitarios, `WorkoutDaoTest` 6/6 en el emulador, `assembleDebug`, `reglas.sh` y once controles negativos) y `docs/evidencia/estados/workout-*.png` (C29 y C31). Tests en rojo en el commit `a77dc2b`.
- Commit / PR:

### 2026-10-25 · Sofi · feat(nutrition): maquetar Nutrition / 07 Food Confirmation según Figma
- Carril y requisitos: B · UI01, UI03, NAV02, NAV03, NAV04, RF03, RF04 · tarea m, PR 7 de 8. Frame: `Nutrition / 07 — Food Confirmation` (nodo 1:1185 de la copia `GRk3uXr8…`). Rama `feat/nutrition-food-confirmation`, sacada de `main` con el PR 6 y VC-l (comidas en Room) ya unidos.
- Plan aprobado: `FoodConfirmationRoute` y `FoodConfirmationScreen` sobre `FoodCaptureViewModel`, que ya tenía la lógica de 07 desde VC-l (formulario, validación, guardado en Room y evento `Saved`). Con borrador: tarjetas `VoraCard` con `VoraStepper(onDark = false)` y "Remove", "+ Add Ingredient", panel MEAL TOTALS y botones Primary y Outlined. Sin borrador: los campos de RF03. "Add to …" guarda y vuelve al Dashboard; ‹ y atrás descartan; −/+, "Remove", "+ Add Ingredient" y "Save without logging" muestran el aviso. La barra la pone el Scaffold; con scroll (UI03).
- Qué propuso la IA y qué cambiamos:
    - El pedido de Sofi era hacer solo la maqueta (sin guardar), pensando que VC-l venía después. VC-l ya estaba en `main`, así que la IA propuso conectar el guardado real y Sofi aceptó. La carga manual y la edición muestran campos (`VoraTextField` y chips de tipo) en lugar de las tarjetas, porque el frame no los dibuja. La edición no lleva lógica nueva: es la misma del ViewModel y llega desde 08 en el PR 8.
    - El tipo de la carga manual se elige con chips. La regla "tipo = comida pendiente del día" quedó para un PR aparte, con su criterio y su test. El botón dice "Add to [tipo]", o "Save Meal" sin tipo o al editar. En español van cuatro textos ("Agregar al almuerzo") en lugar de un `%1$s`, para que el artículo concuerde.
    - Los totales muestran el formulario que se va a guardar, precargado del mismo `PhotoMealDraft` que 06. No hay una suma aparte: sumar macros por ingrediente obligaba a inventar valores que el Figma no tiene.
    - Componentes (zona de Martu), los tres con valor por defecto y sin cambiar otras pantallas:
        - `VoraButtonStyle.TextDestructive`, para "Remove" (rojo sin borde);
        - `valueStyle` en `VoraStepper`, porque los gramos son de 14 en el frame (medidos: "180g" ocupa 32 dp) y el selector los dibujaba en 18 Bold;
        - `square` en `VoraStepper` con el token `stepperButtonSquare` (32), para los botones cuadrados. El radio es 8: en el nodo mide unos 6 y el tema no tiene ese valor. El área táctil sigue en 48, así que la tarjeta mide 101 y no 85.
    - Por revisor-arquitectura (BLOQUEA): la Route abría la confirmación en un `LaunchedEffect`, es decir, desde la composición (CONTEXTO §2). Sofi eligió que la abra el grafo antes de navegar, como `abrirFoto`. Costo aceptado: si muere el proceso con 07 abierta, solo se ve el encabezado con ‹. Por la MEDIA, ‹ y atrás no hacen nada mientras se guarda.
    - Al guardar se navega con `popUpTo(NUTRITION_GRAPH)` y no hasta el Dashboard. Desde "Log breakfast" de Home el Dashboard no está en la pila. Atrás no vuelve a 07.
    - El nombre y las kcal de las tarjetas son de 14 (`labelLarge`): "Grilled Chicken Breast" mide 145 dp en el frame. El revisor estimó 16 y medido daba 15.
- Revisores:
    - revisor-visual: CON CAMBIOS en la vuelta 1 ("+ Add Ingredient" con ~13 dp de más arriba y abajo, y el tamaño del texto de las tarjetas) y APROBADA en la vuelta 2, con las medidas a 2 dp o menos.
    - revisor-arquitectura: 1 BLOQUEA y 1 MEDIA en la vuelta 1, corregidas; en la vuelta 2, nada bloquea.
    - revisor-requisitos: nada bloquea; C28, C29, C31, C32, C34 a C36, C38 y C39 tienen camino desde la UI. C30, C33 y C37 (edición) esperan al PR 8.
    - `/code-review`: 7 hallazgos. Se corrigieron solo los comentarios desactualizados (KDoc de `onOpenConfirmation` y de la Route).
- Sugerencias sin aplicar:
    - Para Fiore, como sugerencia y no como arreglo necesario: tocar Home después de entrar a Nutrition desde Home ("Log breakfast", "Start workout" o la notificación de Nutrition) vuelve a mostrar Nutrition. `irAPestana` hace `popUpTo(HOME_DASHBOARD) { saveState = true }` con `restoreState = true`, y Navigation guarda lo que se apiló arriba de Home como si fuera de Home y lo restaura. Opciones: que tocar Home vuelva siempre limpio (sin guardar ni restaurar), o que "Log breakfast" cambie primero a la pestaña Nutrition. Ya pasaba antes de 07.
    - Para Fiore: `PANTALLAS.md` §7 dice que −/+ y "Remove" cambian el borrador, y la §17 (punto 4) que "Remove" saca un ingrediente. Acá muestran el aviso de demostración (decisión 4 del plan de VC-m: los ingredientes no se guardan). `Rutas.foodConfirmation` repite el `-1` en vez de usar `NEW_MEAL_ID`.
    - Para Martu: revisar `TextDestructive`, `valueStyle`, `square` y `stepperButtonSquare`, y sumar la firma nueva de `VoraStepper` a CONTEXTO §9. "Remove" sale en 13 SemiBold y en el frame parece Regular.
    - Para el PR 8: "Edit Meal" tiene que llamar a `onOpenConfirmation(id, …)` antes de navegar a 07; si no, 07 abre vacía. Si falla la carga al editar, el formulario queda vacío y se puede guardar.
    - Bajas:
        - doble toque en "Log Manually" o "Type instead" sin `launchSingleTop`: Sofi lo probó y no se reproduce;
        - tocar una pestaña mientras se guarda deja `Saved` en el canal y la próxima 07 navega al Dashboard;
        - con borrador, los errores de validación no tienen dónde verse (hoy el borrador siempre es válido);
        - una confirmación sin descartar se reusaría en la próxima captura (no se encontró un camino);
        - los tres "Remove" se leen igual con TalkBack;
        - los cinco campos repetidos de `MealFields`;
        - el objeto `Shown` que la Route escribe al componer.
- Tests: no se sumaron ni se cambiaron tests: la lógica es la de VC-l (C28 a C39). 189 unitarios en verde.
- Evidencia: `docs/evidencia/visual/nutrition-07-{411dp,360dp,360dp-abajo}.png`, de un emulador de 1080x2424 a 420 y 480 dpi (la densidad se devolvió). A 411 dp entra sin scroll; a 360 dp "Save without logging" se alcanza con scroll. `assembleDebug`, `testDebugUnitTest` y `lintDebug` en verde, y `reglas.sh` exit 0.
    - A mano, Sofi probó el recorrido después de la maqueta y anduvo: 06 y la voz abren 07 con el borrador, los controles sin destino muestran el aviso, rotar no pierde nada, guardar vuelve al Dashboard y atrás no vuelve a 07, la carga manual valida y "Type instead" abre los campos.
    - Después de las correcciones de los revisores se probó solo el doble toque.
    - Sin verificar: C51 (la comida sigue al cerrar la app) sin evidencia guardada, y `connectedDebugAndroidTest` (la rama no toca Room).
- Commit / PR:

### 2026-10-25 · Hector · feat(workout): mostrar carga, vacío y error por tipo con reintento
- Carril y requisitos: C, paso 3 (implementar) · RF05, RF07 (y RF06: errores traducidos en el repositorio) · tarea r. Spec: `specs/funcionales/estados-de-carga-y-error.md` (C1 a C36). Rama `feat/workout-estados-carga-error`.
- Plan aprobado:
  - `toAppError()` clasifica por tipo, sin mirar la causa, y los dos repositorios devuelven `AppException` según el fallo. Si falla Room, `UNKNOWN`.
  - `WorkoutErrorMessages.kt` traduce cada error a su mensaje. Los tres ViewModels tienen `cargar()` y `onRetry()`, que solo actúa en `Error` (y en `NotAvailableOffline` del detalle) y pone `Loading` antes del `launch`. El catálogo vacío pasa a `Empty`.
  - `WorkoutStatusMessage.kt` va en versión clara y oscura. 01 a 06 suman `onRetry`; 07 queda sin cambios.
  - No se tocó `ui/components/`, `ui/theme/`, la navegación, Room ni `NetworkModule`.
- Qué propuso la IA y qué cambiamos:
  - En el detalle, la IA juntó las cuatro ramas de `catch` en una sola que usa `toAppError()`. Así el JSON roto se separa de la falta de conexión en un solo lugar, en vez de depender del orden de los `catch`.
  - Dos controles negativos salieron mal a la primera y se rehicieron:
    - C6: la rotura no hizo fallar el test porque Room ya tenía los tres entrenamientos.
    - C20: con `stateIn` el test fallaba en su precondición y no en lo que prueba.
  - Hector pidió que la IA probara la app en el emulador. Para no tocar su mock, la IA levantó una copia en el puerto 8001 con `adb reverse` y simuló el servidor apagado sacando el `adb reverse`. Antes de borrar los datos de la app con `pm clear` pidió permiso.
  - En la primera corrida el emulador dio ANR por "no focused window" en el launcher, en la app, en `system` y en System UI. La IA comprobó que el hilo principal de la app respondía y, con el OK de Hector, reinició el emulador; después no se repitió.
  - Para C33e la IA sumó `@Preview` en error para 04, 05 y 06 y las abrió en el emulador con `PreviewActivity`.
  - C34 quedó parcial: Nutrition / 07 todavía es `PantallaVacia`, así que desde la app no se puede guardar una comida.
  - Ningún test se modificó.
- Revisores: ninguno marcó nada BLOQUEA, CRÍTICA ni ALTA, así que hubo una sola vuelta.
  - revisor-requisitos: C1 a C36 cumplen, salvo la parte de Nutrition de C34. Se corrigieron en la spec la tabla de Tests y el Historial (C34 parcial; C33d probado solo con `error`).
  - revisor-arquitectura: una MEDIA, que `ui/` importe `data.repository` en `WorkoutErrorMessages.kt`. La pide la spec, la usan solo los ViewModels y queda como sugerencia.
  - revisor-visual: aprobada. Una MEDIA en la evidencia: en las `@Preview` de 06 la barra superior queda encima de la de estado porque falta el `Scaffold`. Quedó anotado en la evidencia.
  - `/code-review`: 5 hallazgos bajos.
- Sugerencias que quedan:
  - `catalogErrorMessage` y `detailErrorMessage` como `internal`, y a futuro `AppError` en `domain/`;
  - `AppException` sin mensaje: se pierde el código HTTP y el id para los logs;
  - cualquier `IllegalArgumentException` cuenta como `INVALID_DATA`;
  - las dos tablas de mensajes repiten tres ramas;
  - la rama de `Content` vacío en 01 y 02 ya no se alcanza;
  - falta `liveRegion` para TalkBack en el mensaje de error;
  - Retry sin `workoutId` no hace nada (C25);
  - 05 en error no tiene cómo salir salvo el atrás del sistema;
  - la tarjeta de carga sobre verde está repetida en 04 y 05;
  - en los `@Preview` viejos `onRetry` va primero;
  - Retry Small mide menos de 48 dp (zona de Martu);
  - faltan capturas en español, de 02 con `malformed` y sin servidor, y de un Retry con `malformed`.
- Evidencia: `docs/evidencia/tests/2026-10-25-estados-verde.md` (216 unitarios en verde, `reglas.sh`, `assembleDebug` y los controles negativos de C3, C6, C9, C11, C13, C18, C20, C24, C25, C29 y C36) y `docs/evidencia/estados/estados-*.png` con `estados-log-del-mock.txt`. Tests en rojo en el commit `5877b2e`.
- Commit / PR:

### 2026-10-25 · Sofi · feat(nutrition): maquetar Nutrition / 08 Meal Details según Figma
- Carril y requisitos: B · UI01, UI03, NAV02, NAV03, NAV04, RF04 · tarea m, PR 8 de 8. Frame: `Nutrition / 08 — Meal Details` (nodo 1:1284 de la copia `GRk3uXr8…`). Rama `feat/nutrition-meal-details`, sacada de `main` con el PR 7 ya unido.
- Plan aprobado: `MealDetailRoute` y `MealDetailScreen` sobre `MealDetailViewModel`, que ya tenía la lógica desde VC-l (C41 a C50). Encabezado con el tipo de comida, tarjeta de totales con `VoraProgressRing` y P/C/F, consejo con `VoraInfoMessage`, la comida en "Ingredients" y tres botones. "Edit Meal" abre 07 con el id (el grafo arma la confirmación antes de navegar, como en el PR 7); "Duplicate Meal" guarda una copia y vuelve; "Delete Meal" pide confirmación y vuelve. La barra la pone el Scaffold; con scroll (UI03).
- Qué propuso la IA y qué cambiamos:
    - Room guarda la comida con sus totales, sin ingredientes, peso ni hora. Sofi eligió dejar el título "Ingredients" con una sola tarjeta: el nombre de la comida, la proteína en el lugar de los gramos ("38g protein") y las kcal. Cada 08 muestra una sola comida: la que se tocó en 01 o 02.
    - La IA propuso mostrar la fecha de la comida en el lugar del "13:12"; Sofi prefirió el "13:12" del Figma fijo. El consejo ("36g away…") también es fijo, como el de 01. Los dos son datos de demostración.
    - Eliminar (RF04) no tiene control en el Figma. Sofi preguntó si "Edit Meal" se podía reemplazar por "Delete Meal", pensando que editar era de demostración. La IA aclaró que la edición es real (C30, C37) y quedó un tercer botón "Delete Meal" con el estilo `Destructive` y un `AlertDialog` de Material 3.
    - El anillo es kcal de la comida / 2100 (`HomeDemoData.KCAL_GOAL`, la meta de demostración de 01), topado en 1. Por revisor-arquitectura (MEDIA), la cuenta salió de la pantalla: es la propiedad `kcalProgress` de `MealDetailUiState.Content`, como `kcalProgress` en 01. No cambia el ViewModel ni los tests.
    - La fila de P/C/F pasó a `NutritionCommon.kt` (`MacroTotalsRow`), compartida con 07 en un commit `refactor` aparte. Sofi confirmó en el emulador que 03, 06 y 07 se ven igual. `NutritionHeader` suma un texto opcional a la derecha y una versión con el título como texto.
    - Las tipografías se midieron contra Manrope, porque el conector de Figma no tenía cupo: "520 kcal" 32 SemiBold, "TOTAL CALORIES" 12, macros e "Ingredients" 16 SemiBold, nombre 13 SemiBold y kcal de la fila 14 Regular.
- Revisores:
    - revisor-visual: APROBADA en la primera vuelta, con todo a menos de 3 dp del frame.
    - revisor-arquitectura: nada bloquea. La MEDIA del anillo se corrigió, igual que el comentario viejo de la Route de 07.
    - revisor-requisitos: nada bloquea. C41 a C50 y la edición (C30, C33 y C37) tienen camino desde la UI.
    - `/code-review`: 5 hallazgos, ninguno aplicado.
- Sugerencias sin aplicar:
    - Guardar una edición lleva al Dashboard y no vuelve a 08: Sofi lo dejó así a propósito.
    - Mientras se elimina, los botones siguen activos. El ViewModel corta el doble toque, pero "Edit Meal" abriría 07 con "esta comida ya no existe".
    - "Duplicate Meal" vuelve sin avisar que se creó la copia.
    - El error de carga no tiene "Reintentar" (es Room, no una consulta remota).
    - El encabezado vacío mientras carga se anuncia como título en TalkBack.
    - Al editar, el grafo pasa `meal.name` como nombre del borrador, y ese dato no se usa.
    - Para Martu y Hector: `LoadingCard` vive en `WorkoutDashboardScreen.kt` y Nutrition lo importa de Workout. Moverlo a `ui/components/` toca 7 archivos de 3 zonas: queda para ellos.
    - Para Fiore: `PANTALLAS.md` línea 256 (dice que 03 carga, que 02 elimina y que el total sale de un `SUM`) y el punto 4 de la §17: eliminar ya tiene control, en 08.
- Tests: no se sumaron ni se cambiaron tests: la lógica es la de VC-l. 189 unitarios en verde.
- Evidencia: `docs/evidencia/visual/nutrition-08-{411dp,360dp}.png`, de un emulador de 1080x2424 a 420 y 480 dpi (la densidad se devolvió). Entra sin scroll en los dos tamaños. `assembleDebug`, `testDebugUnitTest` y `lintDebug` en verde, y `reglas.sh` exit 0.
    - A mano, Sofi probó en el emulador abrir una comida desde 01 y 02, editar, duplicar, eliminar con confirmación y rotar, y anduvo todo. Después de mover el anillo al estado no se volvió a probar.
    - Sin verificar: capturas del diálogo y de los estados de error, y que el total de Home baje al eliminar (es la misma consulta a Room que ya estaba).
- Commit / PR:

### 2026-10-25 · Hector · docs(workout): completar la evidencia de C34 con una comida de Nutrition / 07
- Carril y requisitos: C, cierre de la tarea r · RF05 ("un fallo no borra caché ni registros locales"). Spec: `specs/funcionales/estados-de-carga-y-error.md`, C34.
- Qué faltaba: en el PR #53 la parte de Nutrition de C34 quedó sin probar, porque Nutrition / 07 todavía era `PantallaVacia`. Con el PR #52 de Sofi en `main` ya se puede guardar una comida.
- Qué propuso la IA y qué cambiamos:
  - La IA guardó "Prueba C34" (Lunch, 500 kcal) desde 07 y reinstaló con `malformed` y con `error`, sin `pm clear`. Usó la misma copia del mock en el puerto 8001 con `adb reverse`.
  - La primera captura con `malformed` salió en 03, que no tiene barra inferior, y la de `error` se quedó en el login. Las dos se borraron y se repitieron.
  - Como el PR #53 ya estaba unido, Hector lo sube en un PR nuevo y chico, solo de documentos. No hay cambios de código.
- Evidencia: `docs/evidencia/estados/estados-c34-nutrition-01-antes.png`, `-con-error.png` y `-con-malformed.png`, y la tercera corrida en `estados-log-del-mock.txt`. Actualizados `docs/evidencia/tests/2026-10-25-estados-verde.md` y el Historial de la spec.
- Commit / PR:

### 2026-10-26 · Fiore · docs: poner al día el estado del repo para la entrega
- Carril y requisitos: A (documentación) · es lo primero que abre el profe. Toca `README.md`, `specs/REQUISITOS.md`, `specs/PANTALLAS.md` y `docs/decisiones/`.
- Qué pasaba: `REQUISITOS.md` y la tabla "Estado de las funcionalidades" del README venían del 22/10 y no reflejaban los veinte pull requests que entraron entre el 22 y el 25 (las 37 pantallas, el detalle con caché, las comidas y los estados de error). El Hito 1 figuraba sin marcar aunque se cumplió el 21/10.
- Cómo se revisó: fila por fila **contra el código de `main`**, no contra la memoria. RF01 y RF02 en `WorkoutRepositoryImpl` / `WorkoutDetailRepositoryImpl` y `WorkoutNotCachedException`; RF03 en `domain/MealValidation.kt`; RF04 en `MealDao.deleteById` y el diálogo de `MealDetailScreen`; RF05 en `WorkoutsUiState`; RF07 en `SavedStateHandle`; RF08 contando las anotaciones (1 `@HiltAndroidApp`, 1 `@AndroidEntryPoint`, 26 `@HiltViewModel`, 4 módulos). Nadie quedó en "verificado": ese estado pide que **otro** integrante lo pruebe, y eso es la tarea VC-s.
- **Cada fila lleva ahora el pull request y la fecha en que ese requisito entró a `main`** (#2 del 18/10, #4 y #5 a #7 del 20/10, #11 del 22/10, #50 a #54 del 25/10), tanto en `REQUISITOS.md` como en la tabla del README: el estado de cada requisito queda contrastable contra `git log origin/main --merges`. Donde el requisito se cumple de a poco en varios PR —NAV03 y NAV04— se citaron los que se pudieron verificar, sin inventar uno exacto.
- Qué se corrigió, y por qué cada cosa:
  - **El README no alcanzaba para compilar desde cero.** Decía "crear `local.properties` con `api.baseUrl`" y no nombraba `sdk.dir`: siguiéndolo al pie de la letra, Gradle corta con `SDK location not found`. Pasó de verdad al clonar en limpio. Ahora están las dos líneas, con la ruta de Windows.
  - **No decía cómo levantar el mock.** Decía dónde está, no el comando. Para probar RF02 hay que poder apagarlo: se agregó `python3 mock/server.py`, con `--port` y `--host`.
  - **Se agregaron las dos preguntas del cuestionario que no estaban escritas en ningún lado:** "mejoras al escalar" (módulos por flujo, Paging 3, Room como única fuente de verdad, casos de uso, flavors) e **idiomas**.
  - **Los 216 tests y `docs/evidencia/estados/` no se mencionaban.** Esa carpeta demuestra justo lo que es difícil de mostrar en vivo —carga, vacío, error, `malformed`, servidor apagado, reintento, leer desde Room y rotar— y estaba invisible. Ahora el README la indexa por código (C31 a C35).
  - **`PANTALLAS.md` sección 17 "Pendientes"** daba por abiertos cuatro puntos ya cerrados, tres de ellos **por respuesta del profe** (dudas #3 y #4). Se reescribió como registro de cómo cerró cada uno, con la respuesta textual.
  - **Dos fichas numeradas 004.** `004-perfil-un-viewmodel-por-pantalla.md` (21/10) pasó a **009**; se quedó con el 004 la de rutas (20/10, citada desde `CONTEXTO.md`). De paso, decía "propuesta para el grupo" y hace días que está aplicada: seis ViewModels de Profile y `ProfileRepository`. *(Primero se le había puesto 008, pero ese número ya lo usaba `008-firebase-sin-acceso-demo.md` en la rama de Martu; se corrigió el 26/10 antes de abrir el pull request.)*
- **G2 estaba cerrado y la bitácora no se había enterado.** La entrada del 22/10 lo dejó como pendiente ("el estilo del borde no sigue a la pantalla"). Hoy está resuelto y mejor de lo propuesto: `ui/components/IconosDelSistema.kt` lo resuelve para las cinco pantallas verdes a sangre (`PreWorkout`, `ActiveWorkout`, `FoodAnalysis`, `VoiceFoodLogging`, `WeeklyReview`) y `VoiceScreen` lo hace por su cuenta, porque ahí el fondo depende del **estado** y no de la ruta. Queda confirmarlo a ojo en el ensayo de la demo (VC-v): acá se verificó leyendo el código, no en el emulador.
- **Un falso positivo que conviene dejar anotado:** comparar las claves de `values` y `values-es` da 734 contra 732 y parece que faltan traducciones. No faltan: `access_password_placeholder` (`••••••••••••`) y `sign_up_name_placeholder` (`Alex Morgan`) están marcadas `translatable="false"` a propósito. **Si alguien agrega a `reglas.sh` el control de "mismas claves en los dos idiomas", tiene que descontar las `translatable="false"`** o va a fallar siempre.
- Revisores: ninguno; es documentación. Lo que afirma cada fila se comprobó contra el código, no contra la bitácora.
- Evidencia: `./gradlew assembleDebug testDebugUnitTest` en verde (216 tests, 0 fallos) y `./scripts/reglas.sh` sin fallas.
- Commit / PR:

### 2026-10-26 · Fiore · feat(voice): coach por Retrofit con historial en Room (EXT01)
- Carril y requisitos: C (datos) y después B (pantalla) · **EXT01**, se apoya en RF05, RF06, RF07 y RF08 · respeta NAV05 y UI01. Spec: [`specs/funcionales/coach-con-historial.md`](../specs/funcionales/coach-con-historial.md), 31 criterios + C11b.
- Qué cambia para quien usa la app: `Voice / 05 — Conversation` dejó de ser maqueta. VORA contesta consultando el servidor del docente, la pantalla ofrece las tres preguntas sugeridas que manda el servidor, y **la conversación se guarda**: se cierra la app, se abre, y el hilo sigue ahí.
- Por qué este extra y no el otro: `Voice / 05` ya tenía frame aprobado y evidencia visual, así que la pantalla no había que inventarla. La búsqueda de alimentos (EXT02) no tiene frame en el Figma.
- Orden de trabajo: `spec-nueva` → `auditor-spec` (15 hallazgos, corregidos) → `tests-rojo` (243 tests, 27 en rojo **por resultado**, ninguno por excepción) → `implementar` → control negativo → `pantalla` → revisores → `cerrar-tarea`.
- **Tres decisiones de diseño, todas escritas en la spec:**
  - Las sugerencias del servidor vienen **en español** (las escribió el docente) y van en `Voice / 05`. `Voice / 01 — Idle`, que tiene cuatro chips en inglés dibujados en el Figma, **no se tocó**: ahí están los puntos de UI01.
  - El hilo de ejemplo del frame **se siembra en Room** la primera vez. Sin eso, en instalación limpia la pantalla quedaba vacía y se rompían las dos capturas de evidencia de UI01/UI03.
  - **C11b:** las cuatro burbujas sembradas no llevan la marca "Simulated response", aunque se guarden con `isSimulated = true`. El frame no las dibuja marcadas. Que ese hilo es de ejemplo se declara en la tabla de funciones simuladas del README, que es donde la consigna pide declararlo.
- **Qué propuso la IA y qué cambiamos:**
  - Propuso llamar a la rama `feat/coach-historial-room`. Se corrigió a `feat/voice-coach-historial`: la convención de `CONTEXTO.md` pone el flujo primero, y las 39 ramas existentes la siguen.
  - Dijo que subir la versión de Room era trivial. **No lo era:** `DatabaseModule` usa `fallbackToDestructiveMigration(dropAllTables = true)`, así que pasar a `version = 4` **borra la tabla de comidas**. Lo encontró `auditor-spec`. Quedó avisado a Sofi y escrito en la spec y en el README.
  - Propuso leer el hilo de Room sin más. Habría dejado `Voice / 05` vacía en instalación limpia. De ahí salió la decisión de sembrar.
  - En la pantalla, la IA introdujo **dos bugs propios** que se encontraron probando en el emulador y están abajo.
  - No se tocó ningún test para que pasara. Los dos que están flojos quedan anotados, no arreglados a escondidas.
- **Dos bugs encontrados y corregidos, los dos visibles en la demo:**
  - **Los chips volvían.** Al reabrir la app con la conversación ya empezada reaparecían las tres preguntas sugeridas, incluida la que acababas de hacer. Las sugerencias se escribían por dos caminos que no se miraban entre sí: al reabrir, Room emite primero y el `GET` volvía después a reponerlas. Lo marcaron los dos revisores, se reprodujo en el emulador y ahora **una sola función decide** si se ven. Dato importante para la metodología: **lo tapaba el test flojo de C6 que el control negativo ya había señalado**. No era solo un test feo; escondía un defecto real.
  - **La lista no bajaba a mostrar la respuesta.** El efecto del scroll estaba atado a `mensajes + (1 si envía)`; cuando llega la respuesta el hilo crece en uno y "enviando" se apaga, los dos cambios se compensan, el número queda igual y Compose no vuelve a ejecutarlo. Ahora mira las dos cosas por separado. Lo encontró Fiore probando, no un test.
- Revisores: `revisor-arquitectura` y `revisor-requisitos` en contexto limpio, con la pantalla ya hecha. Los dos marcaron el mismo bug de C6. Se corrigieron además: el aviso de "sin sugerencias" que no se apagaba nunca, el botón de reintentar que aparecía en el error de texto demasiado largo sin hacer nada, el idioma congelado del hilo sembrado, y dos comentarios que afirmaban que `createdAt` sale del `Clock` cuando sale de contar filas.
- **Marcado y no resuelto, a propósito:** la carga arranca en la composición y no en el `init` del ViewModel, como pide `CONTEXTO.md` §2, y la decoración del frame empareja por posición en vez de por id. Las dos se arreglan inyectando un `StringProvider`, lo que cambia el constructor del ViewModel y obliga a reescribir los tests de `tests-rojo`. Eso lo decide una persona, no la IA.
- **Una trampa del emulador que conviene saber antes del video:** en el emulador de prueba, `10.0.2.2:8000` **no llegaba a la computadora** (timeout, con el mock andando y el firewall apagado). Para la demo conviene apuntar `api.baseUrl` al servidor publicado `https://tp3.martinrivas.ar/`, que no necesita levantar nada. Con mock local hace falta `adb reverse tcp:8000 tcp:8000` y apuntar a `localhost`.
- Evidencia: `assembleDebug` en verde; **243 tests unitarios, 0 fallos**; **20 tests de Room en el emulador, 0 fallos** (incluidos los 4 de `CoachDaoTest`, que nunca se habían corrido); `./scripts/reglas.sh` sin hallazgos. Control negativo de los 5 tests que pasaban en rojo: 4 detectaron la rotura, 1 no (anotado). Capturas en `docs/evidencia/coach/`: sugerencias en 411 y 360 dp (UI03), respuesta con la marca de simulada, error con reintento, reintento sin duplicar, y el hilo intacto después de `am force-stop` (C16). Con el sistema en modo oscuro la pantalla se ve igual, porque la app tiene un solo tema claro: no hay `values-night` ni `darkColorScheme`.
- Commit / PR:

### 2026-10-26 · Fiore · test: reforzar los tests de EXT01
- Carril y requisitos: C (datos) · **EXT01**, criterios C1, C6, C13, C18, C19, C20, C23 y C24.
- Por qué: al cerrar EXT01 quedaron anotados varios huecos —un test que no detectaba nada, dos que la spec prometía y no existían, y tres errores sin cubrir—. Fiore decidió taparlos en vez de dejarlos escritos.
- **15 tests nuevos, de 243 a 258.** Ninguno se escribió para que pasara: a cada uno se le hizo control negativo, rompiendo el código a propósito y comprobando que falla.
- **Un cambio de código para poder probar:** el armado de la siembra estaba dentro de una `@Composable` privada del Route, así que desde un test unitario era inalcanzable. Se sacó a `hiloSembrado()`, una función común que recibe los cuatro textos ya resueltos; el Route se quedó solo con resolver los `@StringRes`, que es lo único que necesita Compose. Con eso C1 pasó de no tener nada a tener seis tests.
- **Lo que midió el control negativo, y que vale la pena mirar:** se rompió el bug de C6 a propósito y se corrió test por test. De los 23 del ViewModel, **falla uno solo**: `al reabrir con la conversacion empezada no vuelven los chips`. El test viejo (`con mensajes propios no quedan sugerencias`) **sigue pasando**, igual que cuando el bug estaba vivo. Es la medición que confirma lo que se sospechaba al cerrar la tarea.
- **No se modificó ningún test para que pasara** (`CLAUDE.md`). El flojo quedó tal cual: cubre un camino real. Se le sumaron dos al lado, así C6 pasó de uno a tres.
- Qué se cubrió: C1 (siembra: cantidad, orden, roles, marca y numeración), C6 (los dos caminos más el de reapertura), C13 (el texto largo no ofrece reintentar), C18 a C20 y C24 (el salto de `AppError` a la clave de texto, que solo estaba probado para "sin conexión"), y las dos mitades de C23 (reintentar no duplica, pero preguntar lo mismo dos veces a propósito sí deja los dos).
- **Qué propuso la IA y qué cambiamos:** la IA había dejado los huecos anotados en la spec y propuesto arreglar solo el de C1, por ser el más barato. Fiore pidió arreglarlos todos. También se buscó en los otros 243 tests del proyecto si había alguno que no pudiera fallar (sin aserciones o con aserciones triviales): no hay ninguno.
- Lo que sigue sin test, y por qué: C29 (lo escrito sin mandar vive en `rememberSaveable`, así que hace falta un test de Compose con emulador, y el proyecto no tiene ninguno) y los criterios que se dibujan en la pantalla (C5, C7, C11, C25 y el botón de C17 a C20), que quedan con evidencia manual en `docs/evidencia/coach/`. Está escrito en la spec, no escondido.
- Revisores: ninguno nuevo; son los huecos que ya habían marcado `revisor-arquitectura` y `revisor-requisitos` al cerrar EXT01.
- Evidencia: `assembleDebug` en verde, **258 tests unitarios y 0 fallos**, `./scripts/reglas.sh` sin hallazgos, y los 7 controles negativos detectando la rotura.
- Commit / PR:

### 2026-10-26 · Fiore · test(voice): primer test de Compose, para C29
- Carril y requisitos: C (datos) · **EXT01**, criterio C29 (y cubre tambien C28 de `asistente-vora.md`).
- Qué faltaba: C29 pide que rotar no borre lo que escribiste y todavía no mandaste. Lo escrito vive en un `rememberSaveable` dentro de `ConversationRoute`, y eso no se puede comprobar desde `test/`: no hay composición ni estado que restaurar. Era el último hueco de los que había dejado anotados `revisor-requisitos`.
- Cómo se resolvió: `ConversationRotacionTest` en `androidTest/`, con `StateRestorationTester`. **No rota el emulador de verdad**: destruye y rearma la composición restaurando el estado guardado, que es exactamente lo que le pasa a `rememberSaveable` al girar la pantalla. Rotar de verdad además recrearía la Activity, y eso prueba el framework, no nuestro código.
- **No hizo falta Hilt.** `ConversationRoute` recibe el ViewModel por parámetro y solo usa `hiltViewModel()` como valor por omisión, así que el test le pasa uno construido a mano con un repositorio quieto. Eso ahorró montar toda la infraestructura de Hilt para tests.
- **Hubo que subir dos dependencias de test, y conviene que el grupo lo sepa:** `espresso-core` de 3.5.1 (de 2022) a 3.7.0, y `androidx.test.ext:junit` de 1.1.5 a 1.3.0. Con las viejas, cualquier test de Compose revienta en el emulador API 37 con `NoSuchMethodException: InputManager.getInstance`, porque Espresso llama por reflexión a un método que Android ya sacó. **No es código nuestro.** Cambia `gradle/libs.versions.toml`, que es de todos; las dos son solo de `androidTest`, y los 21 tests con emulador siguen en verde después del cambio.
- **Una trampa que casi me come:** la primera corrida con `-Pandroid.testInstrumentationRunnerArguments.class=...` dijo **BUILD SUCCESSFUL** y habían corrido **cero** tests: el filtro no enganchaba y Gradle da verde igual. Si alguien filtra tests por clase, que mire la cuenta en `app/build/outputs/androidTest-results/`, no el "SUCCESSFUL".
- Control negativo: se cambió `rememberSaveable` por `remember` en el Route y el test falló. Con el código bien, pasa.
- **Qué propuso la IA y qué cambiamos:** la IA había dejado C29 como "no se puede probar sin montar tests de Compose" y lo daba por cerrado con evidencia manual. Fiore pidió hacerlo igual. Resultó más barato de lo que la IA estimaba, porque el Route ya aceptaba el ViewModel por parámetro.
- Evidencia: **258 tests unitarios y 21 con emulador, 0 fallos**; `assembleDebug` en verde y `./scripts/reglas.sh` sin hallazgos.
- Commit / PR:

### 2026-10-26 · Zaki · fix(access): control visual final de las 37 pantallas y barra de estado en Login
- Carril y requisitos: B, tarea s del tablero · UI01 y UI03. Plan: comparar las 37 pantallas con el Figma sobre `main` (394fc28), en dos tamaños.
- Qué se hizo: Zaki fue navegando por la app en el teléfono (moto g(8) plus, 411 dp) y la IA sacó las capturas por adb. Después se repitió todo a 360 dp con `wm density 480` y se devolvió la densidad. Siete `revisor-visual`, uno por flujo y en contexto limpio, compararon contra `docs/figma/<Flujo> Flow.png`. En la primera vuelta salieron 34 APROBADA y 3 CON CAMBIOS. Después de corregir Login, quedaron las 37 APROBADA. La tabla está en `docs/evidencia/visual/control-final.md`.
- Qué propuso la IA y qué cambiamos:
  - La IA propuso 5 correcciones: barra de estado en Login, etiquetas de Photo Analysis, columna del medio de Workout Complete, botón "Preview" y focus en Workout Dashboard. Zaki eligió primero solo la del focus. Al revisarla, la IA vio que el catálogo de la API no trae los grupos musculares, así que es carril C y no un arreglo visual: quedó anotada como diferencia, sin código. Después Zaki pidió corregir Login. La causa estaba en `VoraAccessScaffold` (zona de Martu): ponía `statusBarsPadding` y `navigationBarsPadding` adentro del `verticalScroll`, y ahora van afuera. Las etiquetas de Photo Analysis las comparó Zaki en Figma y son iguales al diseño. La columna de Workout Complete y el botón "Preview" quedaron como diferencias menores aceptadas.
  - Varias de las sospechas de la IA las descartaron los revisores, porque el Figma es igual: los campos de SignUp, "3/5 complete" en dos renglones, el anillo sin número de Meal Details y el título "VORA Coach" de la 36.
  - Se borraron 53 capturas viejas (`-394dp`, `-scrolled`, los nombres viejos de Voice, `nutrition-07-360dp-abajo` y `nutrition-02-*-scroll`). Las que cita esta bitácora como evidencia de otras condiciones (modo oscuro, castellano, tarea del tema) se dejaron. Solo se cambió la referencia de `voice-01-idle-360dp-scroll.png` por `voice-01-360dp-scroll.png`, que muestra lo mismo.
- Bug encontrado y no corregido: al tocar la pestaña en la que ya se está (por ejemplo Home desde Notifications), `irAPestana` (`MainActivity.kt`) restaura la subpantalla en lugar de volver al inicio de la pestaña. Es de navegación (zona de Fiore) y va por el carril D en otra rama.
- Revisores: `revisor-visual` en los 7 flujos y una segunda vuelta en Login y en Edit Profile después de las correcciones, las dos APROBADA. Quedó un detalle menor sin aplicar: el tilde de "8+ secure characters" es gris y en el Figma es verde oscuro. `revisor-requisitos` no corrió porque no hay spec.
- Tests: no se cambiaron. `assembleDebug`, `testDebugUnitTest` y `lintDebug` en verde.
- Evidencia: `docs/evidencia/visual/<flujo>-NN-{411,360}dp[-scroll].png` (74 capturas base y 33 con scroll) y `control-final.md`.
- Evidencia de la corrección: `login-0{1,2}-*`, sacadas con el arreglo. Las capturas de antes no se guardaron: el problema queda descrito en la nota 1 de `control-final.md`.
- Sin verificar: las medidas se tomaron en proporción contra los PNG exportados, no en dp. No se probó la corrección de Login con el teclado abierto, que usa `imePadding`. El `revisor-arquitectura` del cambio de unidades de Edit Profile se cortó antes de terminar, y escribir y borrar en Height y Weight con el sufijo no se probó a mano.

### 2026-10-26 · Fiore · feat(nutrition): búsqueda de alimentos y cálculo por porción (EXT02)
- Carril y requisitos: C (datos) y después B (pantalla) · **EXT02**. Se apoya en RF03, RF04, RF05, RF06, RF07 y RF08, y respeta NAV02, NAV03 y UI01.
- Qué cambia: "Search Database" de `Nutrition / 03` y el chip "Search" de `Nutrition / 01` dejaban un aviso de modo demostración. Ahora abren `food_search`: se busca contra `GET /v1/foods`, se elige un alimento, se dicen los gramos, la app calcula los nutrientes de esa porción y deja cargado el formulario de `Nutrition / 07`, que guarda en Room por el camino de siempre (RF04).
- **El servidor no calcula nada.** Devuelve los valores cada 100 g y punto: le pasé `?grams=150` y devolvió exactamente lo mismo. La regla de tres la hace la app, y es la única lógica propia de toda la ampliación.
- **La cuenta va en `BigDecimal`, no en `Double`, y no es manía.** 16,9 × 1,5 en coma flotante da 25.349999999999998, que redondeado a un decimal da 25,3 cuando el valor correcto es 25,4. Con HALF_UP y no HALF_EVEN, porque 1,65 tiene que dar 1,7. Las dos cosas tienen su test con números concretos (C11 y C12).
- **No es una pantalla: es una hoja inferior.** Ninguno de los ocho frames de Nutrition es una pantalla de resultados, así que primero se hizo como ruta nueva declarada "sin frame". Al probarla a mano sacaba de contexto, y además obligaba a tocar once archivos de conteos de inventario. Se pasó a `ModalBottomSheet`: **no es un destino del `NavHost`, así que el inventario queda intacto en 37 pantallas y 33 rutas, y `Rutas.kt`, `RutasTest`, `CONTEXTO.md` y `MainActivity.kt` no se tocan.** Ficha: `docs/decisiones/010-buscador-como-hoja.md`.
- **Sin paginación, a propósito.** `METODOLOGIA.md` la pedía, pero el catálogo tiene 12 alimentos y el servidor acepta `pageSize` hasta 50: entran todos en una consulta. Se ahorran seis criterios y el 30% del trabajo, y la consigna no la pide (dice solo "búsqueda de alimentos y cálculo nutricional"). Hay que anotarlo en METODOLOGIA.
- **Dos cosas chicas en zona de Sofi, avisadas:** `FoodCaptureViewModel` gana `onPrefill(form)`, que además **limpia el borrador** (si no, después de probar la foto, 07 mostraría Chicken / Rice / Vegetables debajo de "Pollo cocido"), y `toFormText` pasa a `internal`. Esto último tampoco es cosmético: `parseKcal` valida con `^-?\d+$`, así que precargar "248.0" hacía que 07 marcara `NOT_A_WHOLE_NUMBER` y no dejara guardar. Un endpoint más en `VoraApi`, avisado a Hector. **No sube la versión de la base.**
- **Dos auditorías antes de escribir una línea de código.** La primera encontró que `Food` ya existía como enum de `Profile` (el modelo pasó a `FoodItem`) y que la spec decía "no se toca 07" cuando no había forma de precargarla. La segunda encontró lo de `parseKcal`, que `MealValidation` tiene su regla privada (así que `FoodPortion` escribe la suya), y **que un criterio mío estaba mal**: pedía 25,3 para la avena, que es el artefacto de coma flotante, no el número correcto.
- **Control negativo: 21 roturas a propósito, las 21 detectadas**, pero tres tests hubo que agregarlos porque no detectaban nada: sacar el debounce no rompía ningún test (cancelar el job anterior ya dejaba una sola consulta, así que había que probar que **espera**), y mirar `data.isEmpty()` en vez de `meta.total` tampoco (en el caso de prueba las dos cosas coincidían). Están cubiertos.
- **Una trampa de medición, otra vez:** el primer control negativo dio "nadie la detecta" en cinco roturas. Era mentira: Gradle escribe el resultado de los tests por **stderr**, y yo leía solo stdout. Y "BUILD SUCCESSFUL" también sale cuando la tarea queda UP-TO-DATE sin correr nada. Hay que borrar los XML de resultados y comprobar que se regeneren.
- **Un test mío estaba mal y se corrigió, no se borró:** esperaba que "1,000" fuera un error, cuando la regla que ya usa `MealValidation` lo lee como **un** gramo (la coma es decimal, nunca separador de miles). Quedó como criterio con el comportamiento real, porque sorprende.
- **Los dos con emulador, hechos el 27/10.** C33 recorre la cadena entera con Room en memoria: `onConfirm` arma el formulario, `onPrefill` se lo deja a 07, `onSave` lo valida y lo guarda, y se lee por fecha. Es el que atrapa el bug del formato: precargar "248.0" hace que `parseKcal` lo rechace y no se guarde nada, y eso desde un test de ViewModel suelto no se ve.
- **C34 prueba menos de lo que parece y conviene decirlo.** En el coach el borrador vive en un `rememberSaveable` y rotar lo pone a prueba de verdad; acá `query` y `gramos` viven en el ViewModel, que sobrevive al giro por cómo funciona Android, no por código nuestro. Lo que el test agarra es que la pantalla dibuje **desde el estado**: con un `remember` local que arranca vacío el texto se pierde y falla; con uno sembrado desde el estado no falla, y también se comprobó.
- **Pasó de pantalla a hoja inferior después de probarla a mano.** Como ruta funcionaba, pero sacaba de contexto. Siendo hoja no es un destino del `NavHost`, así que **se revirtieron los once archivos de conteos** y el inventario queda intacto en 37 pantallas y 33 rutas. Ficha: `docs/decisiones/010-buscador-como-hoja.md`.
- **El color lo encontró Fiore probando:** la hoja salía lila. El tema no define `surfaceContainerLow`, que es lo que `ModalBottomSheet` usa por defecto, así que Material caía a su paleta base. Se le pasa el fondo del tema.
- **El revisor de arquitectura encontró lo que el método no había agarrado:** `PageMetaDto` tenía los campos no anulables, contra el KDoc de su propio archivo. Gson no corre el constructor de Kotlin, así que un `meta` truncado dejaba `total` en 0 y la pantalla decía "no se encontró ningún alimento" cuando la respuesta vino rota. Corregido, con el caso sumado a C6 y su control negativo. También faltaba el test de la carrera del debounce (la respuesta vieja que llega tarde y pisa a la nueva): agregado, y sin la cancelación caen C20 y C24.
- Evidencia: **351 tests unitarios y 25 con emulador, 0 fallos**; `./gradlew assembleDebug` en verde y `./scripts/reglas.sh` sin hallazgos. Capturas en `docs/evidencia/busqueda/`, en 411 y 360 dp y en modo oscuro.
- Commit / PR:

### 2026-10-27 · Sofi · feat(nutrition): guardar los ingredientes de foto y voz y mostrarlos en 01, 02, 07 y 08
- Carril y requisitos: C (datos y ViewModels) y después B (pantallas) · RF03, RF04, RF06, RF07 · UI01, UI03, NAV03, NAV04. Spec `carga-y-eliminacion-de-comidas.md`, C53 a C71 (PR 1 de 2). Tests en rojo en `928d9d2`.
- Plan aprobado: los ingredientes van en una columna JSON de `meals`, convertida con Gson en `MealMappers.kt` (sin tabla, Entity ni DAO nuevos), con la base en versión 5 y fallback destructivo. `isValidIngredient` frena el guardado en 07 y el Duplicate de 08. Las pantallas dibujan lo guardado: bajada en 01, una línea por ingrediente en 02, tarjetas en 07 y 08.
- Qué propuso la IA y qué cambiamos:
  - La IA propuso dos commits (datos y lógica) y dejar las pantallas para otra sesión. Sofi recordó que las pantallas van en el mismo PR: se hicieron acá, y el PR se abre con todo junto.
  - La IA propuso la bajada de 01 en una línea con `…`, como el Figma. Sofi preguntó por un scroll horizontal o dos líneas. Quedó en hasta dos líneas: con el scroll, sin `…`, no se nota que hay más. Se corrigió C67 en la spec.
  - Sofi propuso dejar `NutritionNavGraph` como los otros grafos (solo navegación). Revisando git, la lógica era de Sofi y estaba decidida en la spec (25/10). Como el PR 2 cambia esos caminos (saca la edición y pasa la carga manual a pop-up), se decidió revisarlo en el plan del PR 2.
  - Las capturas en 360 dp mostraron la bajada de 01 pegada a las kcal: se separó con 12 dp, también en 02.
- Trampas que conviene saber:
  - Sin `app/google-services.json` no compila; con la plantilla compila, pero el login falla con "API key not valid". Para probar en el emulador hace falta el archivo real del proyecto `vora-coach`.
  - Gson no corre el constructor de Kotlin: un `name` que falta llega como `null` aunque el tipo diga `String`, y por eso se revisa a mano después de leer.
- Revisores:
  - `revisor-requisitos`: los 15 criterios cumplen; tests sin tocar desde el rojo.
  - `revisor-arquitectura`: una ALTA (07 dibujaba las tarjetas con el borrador y no con lo que se guarda), corregida en las pantallas. MEDIA: la versión 5 también borra la caché de entrenamientos (avisar a Hector).
  - Code review: nada crítico ni alto.
  - `revisor-visual`: las cinco pantallas APROBADA en la primera vuelta.
  - Sugerencias sin aplicar (BAJA y MEDIA): validar el Duplicate después del control de "ocupado o borrada"; al editar, una carga que falla puede guardar sin ingredientes (la edición se va en el PR 2); declarar Gson directo en `libs.versions.toml`; una regla `-keep` para `Ingredient` si se activa R8; las kcal de 02 un poco más chicas; los ViewModels de `ui/` importan `data.repository` y `reglas.sh` no lo controla.
- Evidencia: **365 tests unitarios y 26 con emulador, 0 fallos** (C58 incluido); `assembleDebug`, `lintDebug` y `reglas.sh` en verde. Control negativo de 16 roturas, las 16 detectadas (`docs/evidencia/tests/2026-10-27-ingredientes-verde.md`). Capturas `docs/evidencia/visual/nutrition-{01,02,07,08}-ingredientes-{411,360}dp.png` y `nutrition-08-sin-ingredientes-*`.
- Sin verificar: C70 a mano (instalar la versión 5 sobre una base vieja); el texto en castellano de las pantallas nuevas.
- Commit / PR:

### 2026-10-27 · Sofi · refactor(nutrition): borrar comidas con @Delete
- Carril y requisitos: C · RF04 y RF06. Spec `carga-y-eliminacion-de-comidas.md`: cambian C11, C12, C14 y C45, y se suman C72, C73 y C74. Tests en rojo en `f7a7200`.
- Plan aprobado: `MealDao.deleteById` (`@Query`) pasa a `@Delete suspend fun delete(meal: MealEntity): Int`, como en la clase. El repositorio pasa a `deleteMeal(meal: Meal)` y el ViewModel le pasa la comida que muestra `Content`. Lo que ve el usuario no cambia.
- Qué propuso la IA y qué cambiamos:
  - La IA propuso dos opciones: que el repositorio siga recibiendo el id o que reciba la `Meal`. Con el id, la implementación tendría que leer la fila o armar una Entity de mentira. Sofi eligió la `Meal`.
  - Si `delete` borra 0 filas, no es error (C73): para quien borra, que la comida ya no esté es lo que buscaba. `updateMeal` sí avisa (C17), porque ahí se pierde lo escrito.
  - Sofi propuso sumar el refactor al PR 1 de ingredientes. Se dejó aparte, porque ese PR ya estaba revisado y es de otro tipo (`feat` contra `refactor`).
  - Sofi pidió que quedara aclarado en la spec por qué la comida tiene id y el ingrediente no: la comida lo usa en algunas consultas (abrir su detalle, editarla, borrarla), y el ingrediente no tiene id porque no tiene tabla propia (vive en una columna de `meals`), y en esta versión de la app no se consulta, edita ni borra por separado. Quedó en el párrafo "Por qué la comida tiene id y el ingrediente no".
- Revisores:
  - `auditor-spec`, dos ALTA corregidas antes del rojo:
    - `FakeMealRepository.eliminadas` lo usaban C43, C46, C47 y C48. Se conservó y se sumó `comidasEliminadas`, así esos cuatro tests no se tocaron.
    - Faltaba qué hace confirmar sin `Content`: se sumó C74.
  - `revisor-requisitos`: los 7 criterios cumplen. El único cambio en los tests después del rojo es sacar el `override` de `deleteById` del `FakeMealDao`, que dejó de existir en el DAO.
  - `revisor-arquitectura`: un import sobrante y un comentario del ViewModel que nombraba `@Delete`; los dos se corrigieron.
- Trampa: dos compilaciones a la vez en la misma carpeta (un revisor y el control automático) fallan con `NoSuchFileException` o `EOFException`, sin que el código tenga nada mal. Corrido solo, da verde.
- Evidencia: **367 tests unitarios y 27 con emulador, 0 fallos**; `lintDebug` y `reglas.sh` en verde. Rojo, control negativo (`@Update` en lugar de `@Delete` para C11, C12 y C72, más cuatro roturas para C14, C45, C73 y C74) y verde en `docs/evidencia/tests/2026-10-27-borrar-con-delete.md`.
- Pendiente de otra zona: `specs/PANTALLAS.md` §17 todavía nombra `MealDao.deleteById` (Fiore). Va como aviso en el PR.
- Commit / PR:

### 2026-10-28 · Sofi · feat(nutrition): carga manual en pop-up, ingredientes a mano y kcal como suma (PR 2 de ingredientes)
- Carril y requisitos: C (datos y ViewModels) y después B (pantallas) · RF03, RF04, RF06, RF07, EXT02 · UI01, UI02, UI03, NAV01, NAV02, NAV03. Spec `carga-y-eliminacion-de-comidas.md`, C75 a C102 y los reescritos (PR 2 de 2). Tests en rojo en `05848ba`.
- Plan aprobado:
  - Carril C: `validateIngredient` y `validateMealDetails` en el dominio; los dos pop-ups viven en `FoodCaptureViewModel`; `onSave` controla primero que haya ingredientes, después `validateMeal` con las kcal sumadas y después `isValidIngredient`; la búsqueda devuelve el alimento como ingrediente. Se va la edición entera (`updateMeal`, `MealDao.update`, `NOT_FOUND` y el `mealId` de 07).
  - Carril B: hoja de carga (`ManualEntrySheet`), diálogo de ingrediente (`IngredientEntryDialog`), 07 con "+ Add Ingredient" y "Remove" reales y el total como suma, y las Routes que preparan el ViewModel, así `NutritionNavGraph` solo navega (C95).
- Qué propuso la IA y qué cambiamos:
  - La IA propuso sacar el texto "Save Meal", como decía la spec. Al planear vio que, si Android cierra la app en 06, 07 puede abrir sin tipo. Sofi decidió dejarlo como respaldo para ese caso y se corrigió la spec.
  - La IA agregó `nutrition_ingredient_cancel` y dos etiquetas de campo que la spec no nombraba, porque cada diálogo de la app tiene las suyas.
  - El grafo pasó a solo navegar después de revisar en el código cómo lo hacen los otros grafos.
  - La primera corrida de `connectedDebugAndroidTest` falló en `FoodSearchGuardadoRoomTest`: el test no esperaba a que Room terminara de guardar y chocaba con C102. La IA lo confirmó con una espera temporal que deshizo; Sofi decidió arreglar el test (espera `Saved`) y no aflojar C102.
  - `revisor-visual` pidió `VoraButton` en el diálogo de ingrediente y que la hoja no oscurezca la hora sobre 04: se corrigieron.
- Trampas que conviene saber:
  - Room escribe en su propio hilo y `advanceUntilIdle` no lo espera: en `androidTest`, esperar el evento del ViewModel.
  - La hoja inferior es otra ventana: no hereda los íconos claros de 04 y hay que ponerlos en la ventana de la hoja.
  - Deslizar la hoja hacia abajo la cierra (como debe ser): para scrollearla hacia arriba en una prueba automática, no arrastrar desde el borde de arriba.
  - Dos procesos sobre el mismo emulador (Android Studio y una prueba) se pisan: el "Run" de Android Studio cierra la app y la vuelve a abrir.
- Revisores:
  - `revisor-requisitos`: todos los criterios cumplen, ningún test debilitado. MEDIA: faltaban la evidencia de tests y en el Historial C96, C67, C68 y la pasada visual; se agregaron.
  - `revisor-arquitectura`: nada BLOQUEA ni ALTA. C95, capas, textos y tema en regla.
  - `revisor-visual`: CON CAMBIOS en la primera vuelta y APROBADO en la segunda.
  - Code review: nada crítico ni alto.
  - Sugerencias sin aplicar (MEDIA y BAJA): en `FoodSearchViewModel.onConfirm`, parsear los gramos antes de limpiar la selección y actualizar su KDoc; un doble toque muy rápido en "Remove" puede sacar también el ingrediente siguiente; kcal y macros pueden no coincidir (los macros se cargan para toda la comida, decisión de la spec); 01 ordena las comidas por cómo se guardaron y 02 por momento del día (viene de antes); el aviso de demostración es un Toast del sistema (de toda la app); `FieldError.OUT_OF_RANGE` en lugar de `NEGATIVE`; un canal de eventos por pantalla.
- Evidencia: **380 tests unitarios y 26 con emulador, 0 fallos**; `assembleDebug`, `lintDebug` y `reglas.sh` en verde. Rojo, control negativo (C63, C91, C102) y verde en `docs/evidencia/tests/2026-10-28-ingredientes-pr2-verde.md`. Recorrido a mano de C92 a C100 en 411 y 360 dp y capturas de C99 en `docs/evidencia/visual/`.
- Sin verificar: el texto en castellano de los pop-ups en 360 dp; C96 medido en píxeles; C51 (cerrar y reabrir la app con una comida del PR 2).
- Pendiente de otra zona: avisar a Fiore por `NutritionNavGraph`, `Rutas.kt`, `FoodSearchViewModel`, `FoodSearchSheetHost`, `PANTALLAS.md`, el README y el `LEEME.md` de Nutrition. En `PANTALLAS.md` siguen dos líneas de antes de este PR: la 260 (habla de `SUM` y de que "03 carga") y la §17 (nombra `MealDao.deleteById`).
- Commit / PR:
