---
name: cerrar-tarea
description: 'Cierra una tarea antes del pull request: revisores en contexto limpio, reporte con evidencia, bitácora, requisitos y mensajes de commit y pull request sin firma de IA. Usar con "terminé", "ya está", "cerremos la tarea", "armá el pull request", "¿está lista para pasar a Hecha?" o /cerrar-tarea.'
---

# Cerrar la tarea

1. **Revisores en paralelo, en contexto limpio** (subagentes): `revisor-requisitos` (si hay spec), `revisor-arquitectura` (si hay código de datos o pantallas), `revisor-visual` (si es visual) y `/code-review`. Corregí solo lo marcado BLOQUEA, CRÍTICA o ALTA; el resto queda como sugerencia. **Máximo dos vueltas.**
2. Chequeá "Cuándo está Hecha": compila; tests en verde (y los de Room en el emulador si aplica); capturas en dos tamaños si es visual; textos en `strings.xml`; Route y Screen; todo del tema.
3. **Reporte** con este formato:
   - Qué hice (una o dos líneas)
   - Requisitos que cubre (UI, NAV, RF)
   - Archivos creados y modificados
   - Evidencia: build, tests (incluido el rojo y el control negativo), visual, control automático
   - Revisión: revisores que corrieron, qué bloqueaba y cómo se corrigió, sugerencias sin aplicar
   - Sin verificar (si no hay nada, decirlo)
   - Para entender: la decisión más importante, explicada simple para poder defenderla en la devolución
4. **Bitácora:** agregá la entrada al final de `docs/BITACORA.md` con el formato de ese archivo. La línea "qué propuso la IA y qué cambiamos" es obligatoria.
5. Proponé los cambios de estado en `specs/REQUISITOS.md` y, si es una pantalla, en `specs/PANTALLAS.md`.
6. **Redactá, no ejecutes:** los mensajes de commit (Conventional Commits en infinitivo, `Refs:` al pie, commits atómicos) y el título y la descripción del pull request con `.github/pull_request_template.md`. **Sin `Co-Authored-By`, sin "Generated with", sin links a la sesión.** La persona commitea con su usuario.
7. Recordale: pegar el link del pull request en la tarea del tablero, pasarla a **En test**, y que la une a `main` quien la apruebe.
