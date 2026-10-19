# 001 · Cómo trabajamos con la IA: cuatro carriles

**Fecha:** 18/10/2026 · **Propuso:** Fiore (con la IA) · **Estado:** propuesta para el grupo

## Contexto
La metodología del grupo (sección 6) define SDD, ciclo visual, TDD y revisores en contexto limpio. Faltaba decir cuándo usar cada cosa, y había dos puntos que iban a trabar el trabajo.

## Decisión
Cada tarea va por uno de cuatro carriles (detalle en `CLAUDE.md`):

| Carril | Cuándo | Cómo |
|---|---|---|
| A · chico | Se dice en una oración, uno o dos archivos | Directo |
| B · pantalla | Pantalla, componente, navegación | Plan, implementación, capturas, revisor visual |
| C · datos | Retrofit, Room, validaciones, ViewModel | Spec EARS, auditoría, tests en rojo, implementación, revisores |
| D · bug | Algo que andaba y ya no anda | Reproducir (test o captura), commit del rojo, arreglar la causa sin tocar el test, la spec suma el caso |

## Ajustes a la metodología
1. El control automático no deja terminar a la sesión que escribe tests en rojo: se agrega el modo `TDD_ROJO=1` o el archivo `.tdd-rojo`.
2. Los tests del DAO de Room van en `androidTest` y se corren a mano con el emulador; la lógica se prueba en `test/` con fuentes falsas (documentación de Android).
3. Carriles según el tamaño del cambio, para no usar el proceso completo en todo (Martin Fowler sobre SDD; guía de Claude Code: "si el cambio entra en una oración, salteá el plan").
4. Flujo explícito para bugs (guía de Claude Code: "escribí un test que falle reproduciendo el problema y después arreglalo").
5. Firmas vacías antes de los tests, para que el rojo sea por el resultado y no por compilación. El test en rojo va en su propio commit.
6. ktlint y Roborazzi quedan para después del hito: si el control los llama y no están instalados, falla siempre.
7. Fechas: hito y entrega son miércoles (21 y 28/10), la entrega a las 19:00.

Además, `.claude/settings.json` bloquea `git commit` y `git push` para la IA, y el control automático no corre Gradle si no cambió nada en `app/`.

## Consecuencias
- Cada integrante tiene que abrir la IA dentro de la carpeta del repo para que lea `CLAUDE.md`, las skills y los subagentes.
- Una vez por persona: `git config core.hooksPath .githooks`.
- Hay que proteger `main` en GitHub y marcar la CI como obligatoria.
