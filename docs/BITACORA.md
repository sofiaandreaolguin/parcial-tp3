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
- Commit / PR:

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
- Commit / PR:
