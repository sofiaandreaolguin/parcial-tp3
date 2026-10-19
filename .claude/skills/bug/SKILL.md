---
name: bug
description: 'Carril D. Arregla un bug reproduciéndolo primero con un test (o una captura si es visual), sin tocar después ese test. Usar con "hay un bug", "algo no anda", "se rompió", "no funciona", "da error", "crashea" o /bug.'
---

# Bug (carril D)

Primero se reproduce, después se arregla. Nunca "arreglar" sin haber visto el error.

1. Pedí (o leé) **los pasos para reproducirlo**: qué se hizo, qué se esperaba, qué pasó. Si no está en la pestaña Bugs del tablero, proponé el texto para cargarlo. Si es de otra zona, avisá quién es la responsable.
2. Decidí qué hace de "rojo":
   - **Datos o lógica:** un test que reproduce exactamente el bug. Va en una sesión en modo rojo (`.tdd-rojo` o `TDD_ROJO=1`), sin tocar `app/src/main`. Mostrá que falla **por el resultado**. Commit sugerido: `test(<alcance>): reproducir <bug>`.
   - **Visual:** la captura del emulador donde se ve el problema, al lado del frame de `docs/figma/`.
   - **Navegación:** los pasos exactos y lo que dice `specs/PANTALLAS.md` sobre el destino correcto.
3. En una sesión nueva (o después de borrar `.tdd-rojo`): **explicá la causa en una oración antes de cambiar nada.** Arreglá la causa, no el síntoma. **Prohibido tocar el test del paso 2.**
4. Corré **todos** los tests, no solo el del bug. En visual: capturas en los dos tamaños y el subagente revisor-visual.
5. **La spec suma el caso:** si el bug mostró un comportamiento que la spec no decía, agregá el criterio en EARS en `specs/funcionales/` (y la línea en "Historial"), o corregí la fila de `PANTALLAS.md`.
6. Commit sugerido: `fix(<alcance>): <qué corrige>` con `Refs:`. Siguiente: skill cerrar-tarea.
