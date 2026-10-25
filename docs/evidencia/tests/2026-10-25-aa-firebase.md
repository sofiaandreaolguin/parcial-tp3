# aa · Firebase: tests en rojo y en verde (25/10/2026)

Tarea: cuenta, perfil en Firestore y reglas (extra aa). Rama `feat/firebase-extra`. Spec: `specs/funcionales/firebase-auth-perfil.md`.

## Rojo (commit `6c5b8e1`)
`./gradlew testDebugUnitTest` con el esqueleto vacío: 205 tests, 35 fallan, todos nuevos y por resultado. C4 pasó en rojo y se le hizo control negativo.

## Verde
`./gradlew testDebugUnitTest assembleDebug`: 206 tests, 0 fallas, verificado el 25/10 al cerrar la tarea. `bash scripts/reglas.sh`: sin fallas (cubre C30 y C32).
- Se borró `AuthViewModelTest` (acceso demo) y se agregó `LogOutNavigationTest` (C16). Los tests del commit en rojo no se modificaron; en la vuelta 2 se sumó un test a `EditProfileViewModelTest` y se agregaron `ProfileRepositoryTest` y `SessionViewModelTest`.

## Probado con Firebase real (25/10, emulador `Pixel_9_Pro`)
- La app arranca en SignIn sin sesión (C14) y Firebase inicializa sin errores en el logcat.
- Con las reglas todavía sin publicar, el registro creó la cuenta en Authentication y Firestore rechazó el documento (`PERMISSION_DENIED`); la app mostró el error y cerró la sesión (C7).
- Con las reglas publicadas, el inicio de sesión de esa cuenta entró a Home y creó `users/{uid}` con el perfil por defecto (C23, C18). Una segunda cuenta se registró y tiene su documento.
- Reglas (C26 a C29): ver `docs/evidencia/firebase/README.md`.

- Pruebas a mano de Martu en el emulador (25/10, resultado informado por ella, sin captura): editar el perfil y que se guarde en Firestore (C19, C20), cerrar sesión y volver a entrar (C15, C17) y rotar la pantalla en SignIn, SignUp y Edit Profile (C25). Todas salieron bien.

## Sin verificar
- La CI sin `google-services.json` real (corre al abrir el pull request).
