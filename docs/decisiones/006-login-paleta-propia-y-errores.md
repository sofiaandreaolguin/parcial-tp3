# 006 · Login: paleta propia y errores de formulario que el Figma no dibuja

**Fecha:** 22/10/2026 · **Decidió:** Martu (tema y componentes) · **Estado:** aplicado en Login - 01

## Contexto

Al medir `Login - 01 — SignIn` contra el Figma (nodos del frame `2007:214` y `scripts/color.py`
sobre `docs/figma/Login Flow.png`) aparecieron dos cosas que chocan con reglas del grupo:

1. **Los frames de Login no usan la lámina "Sistema de diseño".** El verde de los textos es
   `#0E382F` (no `#123D35`), el lima del botón es `#CCE65E` (no `#C9E86A`), el fondo es
   `#F3F9F5` con un resplandor menta, y los textos van un punto abajo de la lámina (título
   24/32, cuerpo 14/20, links 12/16).
2. **El Figma no dibuja el estado de error** de los campos, pero `specs/PANTALLAS.md` pide que
   el acceso de demostración "valide el formulario y entre". CONTEXTO §6 dice que no se
   inventan estados que no estén dibujados.

## Decisión

1. **Paleta propia, aislada en el tema.** Los 18 colores de Login viven en
   `VoraTheme.colors.access` (`VoraAccessColors`), con su origen anotado en `Color.kt`, y sus
   estilos y medidas en `VoraTextStyles.access*` y `VoraSizes.access*`. **Se usan solo en
   Acceso** (Login - 01 y 02): el resto de la app sigue con la lámina. Así se respeta "los
   colores salen del tema" sin ensuciar los roles de Material 3 que usan las otras 35 pantallas.
2. **El error se muestra, con lo mínimo.** Borde en el rojo del tema (`colorScheme.error`) y el
   mensaje abajo en 12 (`footnote`), solo después de tocar "Sign In" o "Sign Up" (no mientras se
   escribe). Validar sin decir por qué no entra no sirve, y el rojo y el tamaño ya existen en
   la lámina: no se agregan colores ni estilos para esto.

## Por qué

- Fidelidad (UI01) vale 40 puntos: copiar la lámina en Login dejaba diferencias visibles en
  todos los textos y el botón principal.
- Agrupar la paleta en `access` deja claro que es una excepción de un flujo y evita que alguien
  la use en otra pantalla por accidente.

## Qué se rompe si se cambia

- Pasar Login a los colores de la lámina: el botón y los textos dejan de coincidir con el frame
  (el revisor visual lo marca).
- Sacar el error: el formulario rechaza sin explicar por qué y la demo se traba.

## Mejora posible (pregunta 5 del cuestionario)

La validación de email es una expresión regular simple, suficiente para un acceso de
demostración. Con Firebase (tarea aa) la validación real la hace el servidor y esta queda solo
como primer filtro.
