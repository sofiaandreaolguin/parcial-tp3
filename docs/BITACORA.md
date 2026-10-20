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

### 2026-10-19 · Hector · feat(di): proveer Retrofit y Room en módulos de Hilt
- Carril y requisitos: C (sin spec EARS: criterios C1 a C7 en el plan) · RF08, RF06, RF05 · tarea VC-f
- Plan aprobado: `NetworkModule` (URL de `local.properties`, logging, interceptor de escenarios solo en debug, OkHttp, Retrofit con Gson, `VoraApi` con `/health`) y `DatabaseModule` (`VoraDatabase` con solo `WorkoutEntity` y `WorkoutDao`). HTTP sin cifrar solo en debug. Tests primero.
- Qué propuso la IA y qué cambiamos: la IA propuso tres formas de armar la base Room. Hector pidió revisar si se pisaba con la tarea h de Sofi y eligió la base con solo Workout, sin tocar nada de comidas (decisión 003). La IA propuso la rama `feat/di-proyecto-base`; Hector pidió un nombre más claro y quedó `feat/di-hilt-retrofit-room`. La rama salió primero de un main local 13 commits atrás: la IA lo detectó antes de escribir código y Hector la recreó desde el main actualizado. La IA había puesto comentarios al final de las líneas de `local.properties` en el README, y los corrigió porque el `#` entra en el valor.
- Revisores: revisor-arquitectura y revisor-requisitos, sin bloqueos. ALTA: C6 y C7 (Room) sin correr; se corrieron después en el emulador, en verde y con dos controles negativos. Se corrigieron la ficha 003 que faltaba y una línea vieja de `CONTEXTO.md` §7. Quedan como sugerencias: validar `api.mockDelayMs`, URL publicada por defecto en release, `@Qualifier` en lugar de `@Named`, DTO con campos que admitan nulos (tarea g), `replaceAll` en el DAO (tarea g). /code-review no corrió.
- Evidencia: `docs/evidencia/tests/2026-10-19-vc-f-rojo.md` y `2026-10-19-vc-f-verde.md` (11 de 11 en verde, control negativo de C4 y C5). Commit del rojo: `3434d89`.
- Commit / PR:
