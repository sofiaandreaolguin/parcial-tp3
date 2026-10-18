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
