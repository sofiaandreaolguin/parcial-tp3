---
name: spec-nueva
description: 'Carril C, paso 1. Escribe la spec en EARS de una funcionalidad con datos (Retrofit, Room, validaciones, ViewModel) entrevistando a la persona. Usar con "quiero agregar una funcionalidad", "tengo una feature nueva", "escribamos la spec de...", "especificá..." o /spec-nueva.'
---

# Spec nueva (carril C, paso 1)

Vas a escribir la spec de una funcionalidad **entrevistando** a la persona. No programás nada en esta sesión.

1. Leé `specs/CONTEXTO.md`, `specs/CONSIGNA.md`, `specs/API.md` (si hay servidor) y la fila de las pantallas en `specs/PANTALLAS.md`.
2. Confirmá qué requisitos cubre (RF, NAV, UI). Si la funcionalidad no está en la consigna, decilo y preguntá antes de seguir.
3. **Entrevistá: una pregunta por vez**, sobre lo que no está claro. No preguntes lo obvio ni lo que ya dice la consigna. Buscá casos borde: valores límite (el cero, vacío, espacios, coma decimal), errores (sin conexión, 404, 503, JSON inválido, lista vacía), rotación, qué pasa con lo ya guardado si algo falla.
4. Escribí la spec en `specs/funcionales/<nombre-en-kebab>.md` con `specs/funcionales/PLANTILLA.md`. Cada criterio en EARS y numerado (C1, C2...). **Cada línea tiene que poder convertirse en un test**: si no se te ocurre el test, reescribila.
5. Completá la tabla "Tests" con el nombre del test que probaría cada criterio y si va en `test/` o en `androidTest/` (solo lo que necesita Room de verdad va en `androidTest/`).
6. Cerrá con: "Ahora corré el subagente auditor-spec sobre esta spec en una sesión limpia o como subagente. Cuando la apruebes, el paso siguiente es la skill tests-rojo en una sesión nueva."

Mensaje de commit sugerido (lo commitea la persona): `docs(specs): agregar spec de <funcionalidad>` con `Refs: RFxx`.
