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
- Commit / PR:

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
- Commit / PR:
