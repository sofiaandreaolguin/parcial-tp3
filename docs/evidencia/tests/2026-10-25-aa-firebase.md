# aa · Firebase: tests en rojo y en verde (25/10/2026)

Tarea: cuenta, perfil en Firestore y reglas (extra aa). Rama `feat/firebase-extra`. Spec: `specs/funcionales/firebase-auth-perfil.md`.

## Rojo (commit `6c5b8e1`)
`./gradlew testDebugUnitTest` con el esqueleto vacío: 205 tests, 35 fallan, todos nuevos y por resultado. C4 pasó en rojo y se le hizo control negativo.

## Verde
`./gradlew testDebugUnitTest assembleDebug`: 206 tests, 0 fallas, verificado el 25/10 al cerrar la tarea. `bash scripts/reglas.sh`: sin fallas (cubre C30 y C32).
- Se borró `AuthViewModelTest` (acceso demo) y se agregó `LogOutNavigationTest` (C16). Los tests del commit en rojo no se modificaron; en la vuelta 2 se sumó un test a `EditProfileViewModelTest` y se agregaron `ProfileRepositoryTest` y `SessionViewModelTest`.

## Sin verificar
- Contra Firebase real: la lectura de `ProfileDto` con `toObject`, el registro y el inicio de sesión.
- C26 a C29 (reglas con dos cuentas): falta la evidencia en `docs/evidencia/firebase/`.
- Rotación real en SignIn, SignUp y Edit Profile (C25) y la CI sin `google-services.json` real.
