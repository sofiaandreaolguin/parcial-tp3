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
- Revisores: `revisor-visual` sobre las 12 capturas (los 6 frames en 411 y en 360 dp). Dio CON CAMBIOS en cinco pantallas y COINCIDE en el error. Antes de tocar nada se verificó cada hallazgo contra el Figma con el conector, y de ahí salieron **ocho correcciones**: "LIVE AUDIO" es un punto menta con texto al lado y no una pastilla; "LIVE TRANSCRIPT" va en menta; "THINKING..." va en menta y no en lima; los tres paneles verdes llevan borde y un fondo mucho más tenue (blanco al 5 % con borde al 10 %, no al 12 % sin borde), con su radio y su relleno por frame; los textos de los paneles son más grandes (18 en Listening, 16 en Speaking); el separador de fecha de Conversation va centrado y en gris tenue; y el anillo de carga pasó de 16 a 20 porque a 16 se leía como una astilla. Dos hallazgos del revisor **no** eran defectos: el recorte de la tarjeta de última conversación a 360 dp (la pantalla sí se desplaza, se comprobó deslizando: `voice-01-idle-360dp-scroll.png`) y la falta de evidencia en modo oscuro (se capturó después de que arrancara la revisión). **Segunda vuelta del revisor** (el ciclo visual de METODOLOGIA 6.2 dice "iterar hasta que coincida", y la primera vez me quedé en corregir sin volver a revisar; mirar uno mismo las capturas propias es justo lo que descarta el fundamento 2 de la 6.4, "quien hace no se califica"). Con las capturas nuevas y sin saber qué se había tocado: **Conversation y Error pasaron a APROBADA**, y en Listening y Processing el contenido quedó fiel. De la segunda vuelta salió una corrección más, medida en los frames: cada pantalla deja su propio aire entre el encabezado y el orbe (20 en Idle, 40 en Listening, 50 en Processing, 30 en Speaking y Error) y estaba en cero en las cinco. Dos cosas se dejaron a propósito, por el fundamento 6 ("cuidado con el sobre-arreglo"): que la tarjeta de última conversación quede cortada en el primer viewport de 360 dp (es lo que hace cualquier lista que se desplaza) y la distancia entre los controles y "Tap to Interrupt" en Speaking (el frame lo deja contiguo, pero nuestras pantallas son más altas que los 844 del frame y el pie va anclado abajo en los seis). Queda abierto lo que el revisor llamó G1 y G2 y no es de esta tarjeta: las barras del sistema no se pintan del color de la pantalla y sus íconos quedan ilegibles sobre fondo claro. **Corrección sobre G1, tras traer `main`:** la rama estaba 24 commits atrás y el paso 0 del plan ("ponerse al día") no se había hecho. Con `main` adentro aparece que el **PR #8 de Sofi ya había arreglado G1** fijando `SystemBarStyle.light` en los dos bordes, así que los íconos de la barra se leen bien y la mitad de lo que el revisor marcó como prioridad uno venía de capturar sobre una rama vieja. **G2 sigue y queda mejor acotado:** el arreglo de Sofi da por sentado que la app es toda clara —era cierto cuando lo escribió—, y Voice es el primer flujo con tres pantallas verde oscuro, donde el verde no llega detrás de las barras. No es rehacer el armazón: es extender su arreglo para que el estilo del borde siga a la pantalla. Se habla con Sofi. Se verificó además que `Profile / 01` sigue igual con los cambios aditivos a `VoraOrb` y `VoraTopBar`, que era el riesgo que no se podía mirar sin `main`. Se le agregó a `scripts/reglas.sh` el control de que ningún `.kt` llame a `RequestPermission` ni a `checkSelfPermission`, con su control negativo: antes solo miraba el manifiesto.
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
- Pendiente: `AuthViewModelTest` (carril C, en otra sesión con `tests-rojo`). SignUp entra a `RUTAS_A_SANGRE` cuando se maquete.
- Evidencia: `assembleDebug` y `testDebugUnitTest` en verde (49 tests), `control-rapido.sh` sin fallas. Capturas en `docs/evidencia/visual/login-01-{394dp,360dp,360dp-scroll}.png` (Pixel 9 Pro con densidad ajustada). Recorrido en el emulador: Sign In vacío muestra los errores; con datos válidos entra a Home y atrás cierra la app; "Listo" del teclado entra; "Sign up here" va a SignUp y al volver se conserva el email; el texto sobrevive a la rotación.
- Commit / PR: `dd35857`, `854f637`, `c6adf0f`, `4bdfea2` · PR #18

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
- Pendiente: `AuthViewModelTest` (carril C): `signUp` con nombre vacío, `signUp` válido y `onSwitchScreen`.
- Evidencia: `assembleDebug` y `testDebugUnitTest` en verde, `control-rapido.sh` sin fallas. Capturas en `docs/evidencia/visual/login-02-{394dp,360dp,360dp-scroll}.png`. Posiciones medidas contra los nodos a ±1 dp (título, frase, tarjeta, tres campos, botón y fin de tarjeta). Recorrido: "Sign Up" vacío muestra tres errores; flecha, atrás del sistema y "Sign In" vuelven a SignIn sin errores viejos; doble toque en "Sign up here" y en la flecha no rompe la pila; con datos entra a Home y atrás cierra la app.
- Commit / PR:

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
- Pendiente: `HomeViewModelTest` (carril C) con un `Clock` inyectado, para probar el saludo, la suma del día y el tope del anillo; la fecha de "hoy" se fija al crear el ViewModel (ficha 007).
- Evidencia: `assembleDebug` y `testDebugUnitTest` en verde, `control-rapido.sh` sin fallas. Capturas en `docs/evidencia/visual/home-01-{394dp,394dp-scroll,360dp,360dp-scroll}.png`. Posiciones contra los nodos a ±2 dp (panel, botón de voz, tarjetas, fila de datos y recomendado). Recorrido: campana, "Start voice session", "Start my workout", "Log breakfast", "Start workout" y las tarjetas del anillo y de nutrición van a su destino y atrás vuelve a Home.
- Commit / PR:

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
