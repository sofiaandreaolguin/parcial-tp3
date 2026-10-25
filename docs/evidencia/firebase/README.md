# Evidencia de las reglas de Firestore (C26 a C29)

Prueba del 25/10/2026 en el simulador de reglas de la consola (proyecto `vora-coach`, Firestore → Reglas → Zona de pruebas), con las reglas de `firebase/firestore.rules` ya publicadas. Cuenta A: `usuariopruebas@gmail.com` (UID `cdxpbZ0FUaUIzHYtpqXbUgB9YfE2`). Cuenta B: `usuariopruebas2@gmail.com` (UID `fsLaPjJJu7T3u1KdpY32Tig8moX2`).

| Criterio | Simulación | Resultado esperado | Resultado | Captura |
|---|---|---|---|---|
| C26 | `get users/{uidA}` autenticado como A | Permitido | Se autorizó | `C26.png` |
| C27 | `get users/{uidA}` autenticado como B | Denegado | Se rechazó | `C27.png` |
| C28 | `get users/{uidA}` sin autenticar | Denegado | Se rechazó | `C28.png` |
| C29 | `get otraColeccion/doc1` autenticado como A | Denegado | Se rechazó | `C29.png` |

Límite: el simulador prueba la lectura (`get`). La escritura usa la misma regla (`allow read, write`) y no se simuló por separado. Las dos cuentas existen en Authentication y tienen su documento `users/{uid}` en Firestore, creados desde la app.
