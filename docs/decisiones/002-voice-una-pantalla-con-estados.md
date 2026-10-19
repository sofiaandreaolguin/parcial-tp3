# 002 · Voice como una sola pantalla con estados

**Fecha:** 18/10/2026 · **Propuso:** Fiore (tarea VC-c, inventario) · **Estado:** propuesta para el grupo

## Contexto
El Figma tiene 6 frames de Asistente VORA: `Voice / 01 — Idle`, `02 — Listening`, `03 — Processing`, `04 — Speaking`, `05 — Conversation` y `06 — Error`. Hay que decidir cuántos destinos de navegación son, porque de eso dependen el inventario y el `NavHost` de la tarea VC-e.

## Opciones
1. 6 destinos en el `NavHost`, uno por frame.
2. 1 destino con 5 estados, y Conversation como destino aparte.

## Decisión
La opción 2: una ruta `voice` cuyo estado (`VoiceUiState`) vale Idle, Listening, Processing, Speaking o Error, más una ruta `conversation` aparte.

## Por qué (en criollo)
**Navegar es cambiar de pantalla; cambiar de estado no es navegar.** Los 5 frames son la misma pantalla en 5 momentos distintos: cambia el centro y el texto, no la estructura. Es lo mismo que una pantalla de carga: nadie hace una ruta "cargando" y otra "cargado".

**La prueba está en el botón de atrás.** Si fueran 6 rutas, cada paso del coach se apilaría, y al tocar atrás el usuario iría de "hablando" a "procesando" a "escuchando". Eso no es un recorrido que exista: el profe pide que el botón atrás lleve a un destino **coherente** (NAV02). Con una sola ruta, atrás cierra el asistente y vuelve a donde estabas, que es lo que uno espera.

Conversation sí es una ruta de verdad porque cambia el layout: es una lista de mensajes, no el círculo del coach.

## Consecuencias
- El inventario (`specs/PANTALLAS.md`, sección 5) marca los 5 frames apuntando a la misma ruta `voice`, con el estado entre paréntesis. **Las 37 pantallas de Figma siguen estando todas: son 33 destinos de navegación para 37 frames.**
- En la demo hay que **mostrar los 5 estados a mano** desde la propia pantalla, porque el profe los va a buscar en el recorrido (NAV05).
- En el README, las filas de los 5 frames de Voice llevan la misma ruta y aclaran el estado.
- Si se hace EXT01 (coach por Retrofit con historial en Room), cambia `conversation`, no esta decisión.
