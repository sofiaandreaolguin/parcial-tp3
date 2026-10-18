---
name: implementar
description: 'Carril C, paso 3. Implementa el código hasta que pasen los tests ya escritos, sin modificarlos, con plan aprobado antes. Usar con "implementá la spec", "hacé pasar los tests", "programá lo de la spec" o /implementar.'
---

# Implementar hasta el verde (carril C, paso 3)

Los tests ya existen y están en rojo. Tu trabajo es el código que los hace pasar. **Prohibido modificar, borrar o debilitar un test.** Si un test te parece mal, frená y explicá por qué: decide la persona.

1. Leé `specs/CONTEXTO.md`, la spec en `specs/funcionales/` y los tests.
2. **Plan primero, sin tocar código:** qué archivos creás o cambiás, en qué capa (`data/remote`, `data/local`, `data/repository`, `domain/model`, `ui/<flujo>`, `di/`), qué requisitos cubre. Si toca una zona con dueña (tema, `MainActivity`, navegación, base Room, red de entrenamientos) y no es de quien te habla, avisá. Esperá la aprobación.
3. Implementá lo mínimo para pasar los tests, respetando `CONTEXTO.md`: DTO, modelo y Entity separados; la UI no toca Retrofit ni DAO; errores traducidos en el repositorio; textos en `strings.xml`.
4. Corré `./gradlew testDebugUnitTest` (y `connectedDebugAndroidTest` si corresponde). Mostrá la salida.
5. **Control negativo** de cada test que pasó en rojo: rompé el código a propósito, mostrá que el test falla, y deshacé el cambio.
6. Ordená el código (nombres, repetición) y volvé a correr los tests.
7. Cerrá con la evidencia y: "Siguiente: skill cerrar-tarea."
