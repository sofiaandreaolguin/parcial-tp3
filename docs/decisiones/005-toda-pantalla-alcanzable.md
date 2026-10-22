# 005 · Toda pantalla del Figma tiene que ser alcanzable

**Fecha:** 22/10/2026 · **Trajo la respuesta:** Fiore (consulta al grupo) · **Estado:** acordado

## Contexto

NAV01 pide que cada pantalla del Figma tenga su destino y su estado. **Una pantalla sin
entrada rompe NAV01**, y el archivo de Figma **no tiene prototipo armado**: el botón ▷ pasa
los 41 frames como diapositivas, así que las flechas las definimos nosotros.

Al armar el inventario quedaron tres huecos: `Progress / 01 — Today's Overview` sin entrada,
el botón de registro manual de Nutrition sin destino, y la duda de si existía un botón para
eliminar una comida.

## Decisión

**Toda pantalla del Figma tiene que ser navegable, sí o sí.** El orden para resolverlo:

1. **Primero, el botón que ya existe en el frame** y cuyo destino se entiende solo. Es el
   caso normal y es lo que ya está cargado en la tabla NAV03 de `specs/PANTALLAS.md`.
2. **Si una pantalla queda huérfana**, se le asigna igual un control que ya exista en algún
   frame, con criterio, **aunque el recorrido no sea el más lógico del producto**. Vale más
   que el profe pueda llegar a la pantalla que que el recorrido sea perfecto.
3. **Nunca se inventa un control que no esté dibujado en el Figma** para resolver esto. Si
   no hay ningún botón razonable, se discute en el grupo antes de tocar nada.

## Las tres dudas del tablero, con la respuesta que vino

### #2 · `Progress / 01 — Today's Overview` podía quedar sin entrada

> *"Poder dirigir a la pantalla 1 y que desde Start Workout pase a la segunda. La idea es
> poder navegar a todas las pantallas posibles, sea como sea, aunque con algún criterio."*

**Resuelto así:**

- La pestaña Progress **aterriza en `todays_overview` (frame 01)**, no en el 02.
- El botón **"Start Workout" del frame 01 lleva a `progress_dashboard` (frame 02)**, que es
  el que tiene los links a 03, 04, 05 y 06.
- Así se llega a las seis y la 01 deja de depender de una entrada desde Home.

Es justo el caso del punto 2 de la decisión: **"Start Workout" no arranca un entrenamiento**,
y se acepta igual porque hace alcanzable todo el flujo. Antes ese botón apuntaba a
`pre_workout/{workoutId}`; se le saca, y `pre_workout` sigue teniendo dos entradas propias
desde Workout, así que no queda huérfano.

Ya está hecho: `ui/navigation/ProgressNavGraph.kt` y las filas de `specs/PANTALLAS.md`.

### #3 · "Log Manually" no llevaba a ningún lado (RF03)

> *"Podríamos conectarlo a la pantalla que decidamos, no es necesario."*

Queda aprobada la solución que ya estaba propuesta: **"Log Manually" abre
`Nutrition / 07 — Review Meal` vacío**, para completarlo a mano. Esa pantalla ya tiene los
campos que pide RF03 y no hay que inventar ninguna pantalla nueva, que la consigna lo
prohíbe. Es de Sofi (tareas `l` y `m`).

### #4 · No había botón para eliminar una comida (RF04)

> *"Eliminar el componente en la base, no en la API. Ya está el botón al editar el meal."*

Dos cosas quedan firmes: **eliminar borra de Room**, no llama a la API, y el control vive
**en el flujo de editar la comida**, al que se entra por "Edit Meal" de
`Nutrition / 08 — Meal Details`.

**Ojo, esto todavía no está cerrado del todo.** La duda original advertía que el "Remove"
que se ve en `Nutrition / 07` saca un **ingrediente** del borrador, no la comida guardada,
y que por eso no servía para RF04. Antes de implementarlo, Sofi tiene que decir **qué
control exacto** borra la comida. RF04 pide además **confirmación** antes de borrar, y eso
no está dibujado en ningún frame.

## Consecuencias

- La tabla NAV03 de `specs/PANTALLAS.md` deja de tener huecos: cada pantalla aparece como
  destino de al menos un control.
- Lo mismo se aplicó al estado `Error` del asistente VORA, que tenía cuatro salidas y
  ninguna entrada (ver `specs/funcionales/asistente-vora.md`).
- Si al maquetar una pantalla aparece que el botón elegido no existe en ese frame, se avisa
  y se busca otro: la regla no autoriza a dibujar un botón que el Figma no tiene.

## Lo único que falta precisar

**Qué control exacto borra una comida, y cómo se confirma** (duda #4). Lo define Sofi, que
es la dueña de comidas y de la base Room. Cuando lo diga, la fila va a la tabla NAV03 de
`specs/PANTALLAS.md`, que la carga Fiore.

## Relacionado

La duda **#5 del tablero sigue abierta**: si los iconos se sacan del Figma y van a `res/`, o
si se usa una librería aunque no queden idénticos. La cargó Sofi el 21/10. Afecta
directamente al asistente VORA, que necesita ocho iconos que hoy no existen en el proyecto
(ver `specs/funcionales/asistente-vora.md`, parte B). Conviene contestarla antes de que dos
personas resuelvan lo mismo de dos maneras.
