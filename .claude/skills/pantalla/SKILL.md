---
name: pantalla
description: 'Carril B. Implementa una pantalla o un componente contra su frame de Figma (ciclo visual): plan, implementación, captura en dos tamaños y revisor visual. Usar con "tengo que hacer la pantalla...", "maquetá...", "armá el frame...", "esta pantalla no se parece al Figma" o /pantalla.'
---

# Pantalla (carril B, ciclo visual)

El control acá no es un test de código: es el frame del Figma.

1. Leé `specs/CONTEXTO.md` (secciones 3 y 6), la fila de la pantalla en `specs/PANTALLAS.md` y el frame (`docs/figma/` o el conector de Figma si está). Si la fila no existe, pedile a la persona que la agregue Navegación e inventario.
2. **Plan, sin programar:** estructura de la pantalla, componentes del tema que reutiliza, los que faltarían, a dónde navega cada control, si lleva barra inferior (si la lleva, **no la dibuja**: la pone el `Scaffold`), y qué requisitos cubre (UI01, UI02, UI03, NAV02, NAV03...). Si falta un componente común, **no lo crees en la pantalla**: es zona de Tema y componentes, se le pide a Martu. Esperá la aprobación.
3. Implementá: `XxxRoute` (ViewModel y estado) y `XxxScreen` (solo dibuja), textos en `strings.xml`, todo del tema, scroll si no entra. Datos de demostración con la misma forma que los modelos, marcados como demostración.
4. Pedí las capturas en el emulador parecido al Figma (unos 393 dp de ancho) y en otro tamaño, guardadas en `docs/evidencia/visual/<pantalla>-<emulador>.png`:
   `adb shell screencap -p /sdcard/captura.png && adb pull /sdcard/captura.png docs/evidencia/visual/<nombre>.png`
5. Corré el subagente **revisor-visual** con las capturas y el frame. Corregí lo que diga y repetí, **máximo dos vueltas**. Si sigue CON CAMBIOS, proponé `/clear` y un pedido nuevo.
6. Proponé la actualización de la fila en `PANTALLAS.md` (estado funcional o demostración). Siguiente: skill cerrar-tarea.

Mensaje de commit sugerido: `feat(<flujo>): maquetar <pantalla> según Figma` con `Refs: UI01, NAV03`.
