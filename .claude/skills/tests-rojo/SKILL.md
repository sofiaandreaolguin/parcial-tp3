---
name: tests-rojo
description: 'Carril C, paso 2, y carril D. Escribe los tests a partir de una spec aprobada (o de un bug) y muestra que fallan, sin implementar la lógica. Usar con "escribí los tests de la spec", "tests en rojo", "TDD", "primero los tests" o /tests-rojo.'
---

# Tests en rojo (carril C, paso 2)

Escribís los tests **desde la spec**, no desde el código. No implementás la lógica en esta sesión: eso lo hace otra sesión con la skill `implementar`.

0. Chequeá que la sesión esté en modo rojo: variable `TDD_ROJO=1` o archivo `.tdd-rojo` en la raíz. Si no, pedile a la persona que cree el archivo vacío `.tdd-rojo` (si no, el control automático no te va a dejar terminar con tests fallando). Recordale borrarlo al final.
1. Leé `specs/CONTEXTO.md` (sección 7, Tests) y la spec en `specs/funcionales/`.
2. Un test por criterio, con el criterio en el nombre: `` `C2 nombre con solo espacios no es valido` ``.
   - Lógica, mappers, repositorio y ViewModel: `app/src/test/`, con fuentes falsas (fakes) y `runTest`. Retrofit con MockWebServer.
   - DAO de Room: `app/src/androidTest/` con `Room.inMemoryDatabaseBuilder`.
   - Si falta una dependencia de test (`kotlinx-coroutines-test`, `mockwebserver`), decilo: tocar `build.gradle.kts` se coordina con Workout.
3. **Para que compile, creá solo las firmas vacías** (clases, funciones que devuelven un valor neutro). Ninguna lógica.
4. Corré `./gradlew testDebugUnitTest` (y `connectedDebugAndroidTest` si hay tests de Room y el emulador está abierto). Mostrá la salida.
5. Confirmá que **cada test falla por el resultado** (`expected ... but was ...`), no por compilación ni por la configuración. Si alguno **pasa en rojo**, marcalo: va a necesitar control negativo después.
6. Cerrá con: qué tests fallan, cuáles pasaron en rojo, y "Commit sugerido (lo hace la persona): `test(<alcance>): cubrir <criterios>` con `Refs: RFxx`. Borrá `.tdd-rojo`. El paso siguiente es la skill implementar, en una sesión nueva (`/clear`)."
