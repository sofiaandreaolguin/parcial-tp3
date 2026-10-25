# Firebase: cuenta, perfil en Firestore y reglas

Flujo: Acceso y Profile
Requisitos: extra "aa" (Firebase, hasta 10 puntos: Auth 4, Firestore 4, reglas 2). Aplican RF06 (la UI no importa Firebase), RF08 (Hilt) y NAV02 (salida del login y logout)
Pantallas: Login - 01 — SignIn, Login - 02 — SignUp, Profile / 02 — Edit Profile, Profile / 06 — Settings ("Log out")
Responsable: Martu

## Qué hace
La persona crea una cuenta con email y contraseña, inicia y cierra sesión, y la sesión sigue abierta al reabrir la app. Su perfil se guarda en Firestore y se recupera al iniciar sesión; solo ella puede leer y escribir su propio documento. Reemplaza al acceso de demostración: sin cuenta no se entra a Home. Los botones de Apple, Google y biometría siguen siendo aviso de modo demostración.

## Datos
- Auth: usuario de Firebase Authentication (`uid`, email), con proveedor email y contraseña.
- DTO: `ProfileDto` (documento `users/{uid}`). Campos simples: `firstName`, `lastName`, `email`, `birthDate` (String ISO `yyyy-MM-dd`), `heightCm`, `weightKg`, `gender`, `experience`, `trainingDays`, `workoutType`, `coachingStyle`, `diet`, `responseStyle` (Strings con el nombre del enum), `goals`, `foodsEnjoyed`, `priorities` (listas de String), `foodsToAvoid` (lista de String), `speakAloud`, `autoPlay`, `allowInterruptions`, `voiceFeedback`, `hapticFeedback` (Boolean). Todos con valor por defecto, para que Firestore pueda deserializar un documento incompleto.
- Modelo: `Profile` (`domain/model/Profile.kt`), sin cambios, más `Profile.default(firstName, lastName, email)` con los valores de la tabla de abajo. Conversiones explícitas `ProfileDto.toDomain()` y `Profile.toDto()`.
- Entity: ninguna. El perfil no se guarda en Room.
- Errores propios: `AuthDataException` (credenciales inválidas, email ya registrado, contraseña débil, demasiados intentos, sin conexión, desconocido) y `ProfileDataException` (sin conexión, sin permiso, datos inválidos, desconocido). Se atrapan en el repositorio.
- Persistencia de Firestore: se deja la caché local por defecto del SDK. Una escritura sin red queda en cola y se envía al volver la red.

Perfil por defecto (`Profile.default`; valores confirmados por Martu el 2026-10-25):

| Campo | Valor |
|---|---|
| `firstName`, `lastName`, `email` | los de la cuenta |
| `birthDate` | 2000-01-01 |
| `heightCm`, `weightKg` | 170, 70 |
| `gender` | `PREFER_NOT_TO_SAY` |
| `experience` | `BEGINNER` |
| `goals` | `{ IMPROVE_FITNESS }` |
| `trainingDays` | `THREE` |
| `workoutType` | `MIXED` |
| `coachingStyle`, `responseStyle` | `BALANCED` |
| `diet` | `EVERYTHING` |
| `foodsEnjoyed`, `foodsToAvoid`, `priorities` | vacíos |
| `speakAloud`, `autoPlay`, `allowInterruptions`, `voiceFeedback`, `hapticFeedback` | `true` |

## Criterios de aceptación (EARS)

Registro
- C1. CUANDO la persona toca "Sign Up" con nombre no vacío, email válido y contraseña de 8 o más caracteres, la app DEBE crear la cuenta en Firebase Authentication y pasar a Home sacando todo el grafo de acceso de la pila.
- C2. CUANDO se crea la cuenta, la app DEBE crear el documento `users/{uid}` con `Profile.default` usando el nombre y el email del formulario.
- C3. CUANDO se separa el nombre completo, la app DEBE usar la primera palabra como `firstName` y el resto como `lastName`, separando por cualquier secuencia de espacios en blanco (`\s+`) e ignorando los de los extremos (por ejemplo, "Ana María López" da "Ana" y "María López"; "Ana" da "Ana" y apellido vacío).
- C4. SI el formulario de SignUp tiene nombre vacío, email inválido o contraseña de menos de 8 caracteres, ENTONCES la app NO DEBE llamar a Firebase y DEBE mostrar el error del campo (las reglas de forma de `AuthRules` siguen como primer filtro).
- C5. SI el email ya tiene cuenta, ENTONCES la app DEBE quedarse en SignUp y mostrar el error "email ya registrado".
- C6. SI Firebase rechaza la contraseña por débil, ENTONCES la app DEBE quedarse en SignUp y mostrar el error de contraseña débil.
- C7. SI la cuenta se crea pero falla la creación del documento del perfil, ENTONCES la app DEBE mostrar el error, NO DEBE entrar a Home y DEBE poder iniciar sesión después con esa cuenta (C23 crea el documento al iniciar sesión).

Inicio de sesión
- C8. CUANDO la persona toca "Sign In" con email y contraseña correctos, la app DEBE iniciar sesión y pasar a Home sacando todo el grafo de acceso de la pila.
- C9. SI el email o la contraseña no coinciden con una cuenta, ENTONCES la app DEBE quedarse en SignIn y mostrar un único error de credenciales inválidas, sin decir cuál de los dos falló.
- C10. SI no hay conexión al iniciar sesión o crear la cuenta, o Firebase no responde en 10 segundos, ENTONCES la app DEBE mostrar el error "sin conexión" y conservar lo tipeado.
- C11. MIENTRAS el pedido a Firebase está en curso, la app DEBE mostrar el estado de carga en el botón y NO DEBE enviar un segundo pedido si la persona vuelve a tocarlo.
- C12. SI ocurre un error de Firebase no previsto, ENTONCES la app DEBE mostrar un error genérico traducido, nunca el mensaje crudo de la excepción.
- C33. SI el email es inválido o la contraseña está vacía en SignIn, ENTONCES la app NO DEBE llamar a Firebase y DEBE mostrar el error del campo. El mínimo de 8 caracteres NO aplica al inicio de sesión (una cuenta creada fuera de la app puede tener una contraseña más corta).
- C34. SI Firebase responde "demasiados intentos", ENTONCES la app DEBE mostrar ese error traducido y NO DEBE dar a entender que la contraseña es incorrecta.

Sesión y cierre
- C13. CUANDO se abre la app con una sesión de Firebase activa, la app DEBE arrancar en Home, sin pasar por SignIn. El destino inicial se calcula leyendo la sesión de forma síncrona antes de componer el `NavHost`.
- C14. CUANDO se abre la app sin sesión activa, la app DEBE arrancar en SignIn.
- C15. CUANDO la persona toca "Log out" en Settings, la app DEBE cerrar la sesión de Firebase, vaciar el perfil que guarda `ProfileRepository` y llevar a SignIn vaciando toda la pila.
- C16. DESPUÉS de cerrar sesión, el botón atrás del sistema NO DEBE volver a una pantalla de Home ni de Profile.
- C17. SI otra cuenta inicia sesión en el mismo dispositivo después de un cierre de sesión, ENTONCES la app DEBE mostrar el perfil de esa cuenta y NINGÚN dato de perfil de la anterior. Las comidas y los entrenamientos descargados viven en Room y no se separan por cuenta (queda declarado en Fuera de alcance).
- C35. SI la lectura o escritura del perfil falla por "sin permiso" o la sesión ya no es válida (por ejemplo, la cuenta se borró desde la consola), ENTONCES la app DEBE cerrar la sesión y llevar a SignIn.

Perfil en Firestore
- C18. CUANDO se inicia sesión, o se abre la app con una sesión activa, la app DEBE leer `users/{uid}` y exponer ese perfil a Home, Profile, Goals, Nutrition Preferences y Voice & AI Settings.
- C19. CUANDO la persona toca "Save changes" en Edit Profile con datos válidos, la app DEBE escribir en `users/{uid}` SOLO nombre, apellido, fecha de nacimiento, altura, peso y género (actualización parcial, sin reemplazar el documento), NO DEBE esperar la confirmación del servidor y DEBE volver a Profile / 01 mostrando los datos nuevos desde la caché local.
- C36. CUANDO se guarda Edit Profile sobre un documento que ya tiene otros campos guardados (por ejemplo `goals`), la app NO DEBE modificar esos campos.
- C20. CUANDO se vuelve a iniciar sesión con la misma cuenta, la app DEBE mostrar los datos guardados en C19.
- C21. SI el nombre está vacío o es solo espacios, la altura o el peso no son un número entero de 1 en adelante, o la fecha de nacimiento es posterior a hoy, ENTONCES la app NO DEBE guardar y DEBE mostrar el error en el campo correspondiente del formulario.
- C22. SI el servidor rechaza una escritura ya aceptada en la caché (por ejemplo, sin permiso), ENTONCES la app DEBE volver a mostrar el perfil que está en el servidor, no el rechazado. Una escritura hecha sin red queda en cola y se envía al volver la red.
- C37. MIENTRAS se guarda, la app NO DEBE enviar un segundo pedido si la persona vuelve a tocar "Save changes".
- C23. SI el documento `users/{uid}` no existe al iniciar sesión (cuenta creada fuera de la app, o C7), ENTONCES la app DEBE crearlo con `Profile.default`, usando el email de la cuenta y el nombre vacío.
- C24. SI la lectura del perfil falla por falta de conexión y no hay caché local, ENTONCES la app DEBE mostrar un error con reintento y NO DEBE mostrar el perfil de otra cuenta ni uno inventado. SI hay caché local, ENTONCES DEBE mostrarla.
- C25. CUANDO se rota la pantalla en SignIn, SignUp o Edit Profile, la app NO DEBE repetir el pedido a Firebase ni perder lo tipeado.
- C38. SI el documento tiene un campo ausente, un enum desconocido o un tipo cambiado, ENTONCES `ProfileDto.toDomain()` DEBE usar el valor de `Profile.default` para ese campo y NO DEBE fallar.

Reglas de seguridad (`firebase/firestore.rules`)
- C26. Las reglas DEBEN permitir leer y escribir `users/{uid}` solo si `request.auth != null && request.auth.uid == uid`.
- C27. SI la cuenta B intenta leer o escribir `users/{uidA}`, ENTONCES las reglas DEBEN denegarlo.
- C28. SI no hay sesión, ENTONCES las reglas DEBEN denegar cualquier lectura o escritura.
- C29. Las reglas DEBEN denegar cualquier ruta distinta de `users/{uid}` (`allow read, write: if false`).

Arquitectura y entrega
- C30. La UI (`ui/`) NO DEBE importar `com.google.firebase`; solo usa `AuthRepository` y `ProfileRepository` por Hilt (RF06, RF08).
- C31. El repositorio DEBE traducir cada excepción de Firebase a un error propio; el ViewModel DEBE pasarlo a un estado de error con un mensaje de `strings.xml` en `values/` y `values-es/`.
- C32. Los repositorios NO DEBEN exponer tipos de Firebase (`FirebaseUser`, `DocumentSnapshot`) en su API pública; solo modelos de dominio.
- C39. Un módulo de Hilt (`@Module`, `@InstallIn(SingletonComponent)`) DEBE proveer `FirebaseAuth` y `FirebaseFirestore`.
- C40. La app DEBE compilar en un clon limpio copiando `app/google-services.json.example` a `app/google-services.json`. El repositorio NO DEBE contener el `google-services.json` real, contraseñas ni claves administrativas.
- C41. El README DEBE explicar cómo configurar Firebase (crear el proyecto, habilitar Email/Password y Firestore, poner `google-services.json`, publicar `firebase/firestore.rules`) y cómo probar las reglas con dos cuentas.

## Fuera de alcance
- Botones de Apple, Google y biometría: siguen como aviso de modo demostración.
- Recuperar contraseña, verificar el email, cambiar email o contraseña, borrar la cuenta.
- Guardar en Firestore los cambios de Goals, Nutrition Preferences y Voice & AI Settings: se leen del perfil, pero sus cambios siguen en memoria.
- Sincronizar comidas o entrenamientos con Firestore. Siguen en Room y en el mock, y no se separan por cuenta ni se borran al cerrar sesión.
- Modo demostración sin cuenta o sin `google-services.json` (decisión del grupo: Firebase siempre; riesgo anotado en el Historial).
- Emulator Suite y tests de reglas automatizados en CI: las reglas se prueban a mano con dos cuentas.
- Foto de perfil.

## Tests
| Criterio | Test | Dónde | Vi el rojo |
|---|---|---|---|
| C1 | `C1 registro valido crea la cuenta y entra a Home` (ViewModel con repositorio falso) | `test/` | Sí: `expected:<1> but was:<0>` (no llama a `signUp`) |
| C2 | `C2 registro crea el documento con Profile default y nombre y email del formulario` | `test/` | Sí: no se crea el documento; `Profile.default` devuelve valores neutros (`expected:<[Ana]> but was:<[]>`) |
| C3 | `C3 nombre completo se separa en nombre y apellido` (una palabra, varias, espacios y tabuladores) | `test/` | Sí: `expected:<(Ana, )> but was:<(, )>` |
| C4 | `C4 formulario de registro invalido no llama al repositorio` | `test/` | **Pasó en rojo** (el stub ya valida la forma y no llama a nada). Control negativo: con `signedIn = true` forzado el test falla; restaurado |
| C5 | `C5 email ya registrado muestra el error` | `test/` | Sí: `formError` nulo (no es texto de `strings.xml`) |
| C6 | `C6 contrasena debil muestra el error` | `test/` | Sí: `formError` nulo |
| C7 | `C7 falla el documento, no entra a Home y puede iniciar sesion despues` | `test/` | Sí: `signUp` no se llama (`expected:<1> but was:<0>`) |
| C8 | `C8 inicio valido entra a Home` | `test/` | Sí: `expected:<1> but was:<0>` (no llama a `signIn`) |
| C9 | `C9 credenciales invalidas dan un unico error` | `test/` | Sí: `expected:<2> but was:<0>` |
| C10 | `C10 sin conexion o timeout muestra el error y conserva lo tipeado` | `test/` | Sí: sin `formError` ni `isLoading` tras el fallo; el timeout de 10 s se prueba con tiempo virtual |
| C11 | `C11 un segundo toque durante la carga no envia otro pedido` | `test/` | Sí: `isLoading` nunca se prende |
| C12 | `C12 error desconocido muestra mensaje generico` | `test/` | Sí: `formError` nulo con `UNKNOWN` y con una excepción cruda |
| C13 | `C13 destino inicial con sesion es Home` (función pura del `startDestination`) | `test/` | Sí: `expected:<[home_graph]> but was:<[]>` |
| C14 | `C14 destino inicial sin sesion es SignIn` | `test/` | Sí: `expected:<[access_graph]> but was:<[]>` |
| C15 | `C15 log out cierra la sesion, vacia el perfil y navega a SignIn` | `test/` | Sí: `expected:<1> but was:<0>` (no cierra la sesión) |
| C16 | `C16 despues del log out la pila no vuelve a Home` (`TestNavHostController`) | `test/` | **Pendiente**: no escrito. `TestNavHostController` no corre en `test/` sin `navigation-testing` y Robolectric (o va a `androidTest/`); falta decidir y coordinar `build.gradle.kts` |
| C17 | `C17 otra cuenta no ve datos de perfil de la anterior` | `test/` | Sí: `expected:<[ana@mail.com]> but was:<[]>` (no lee el perfil de la cuenta) |
| C18 | `C18 al iniciar sesion y en arranque en frio se lee el perfil de la cuenta` | `test/` | Sí: `expected:<[(uid-ana, ana@mail.com)]> but was:<[]>` |
| C19 | `C19 guardar perfil escribe solo los 6 campos y vuelve` | `test/` | Sí: no hay escritura (`ediciones` vacío); y `toFields()` devuelve `{}` en vez de los 6 campos |
| C20 | `C20 un perfil guardado se recupera al volver a iniciar sesion` | `test/` | Sí: `expected:<[Anita]> but was:<[]>` |
| C21 | `C21 datos invalidos no se guardan y muestran error en el campo` (nombre en blanco, 0, negativo, fecha futura) | `test/` | Sí: no marca el error del campo |
| C22 | `C22 una escritura rechazada vuelve a mostrar el perfil del servidor` | `test/` | Sí: `expected:<[Anita Pérez]> but was:<[ ]>`. Cubre el contrato del ViewModel con un repositorio falso; el rechazo real del servidor se verifica a mano |
| C23 | `C23 documento inexistente se crea con el perfil por defecto` | `test/` | Sí: `expected:<[ana@mail.com]> but was:<[]>` (`profileOrDefault`) |
| C24 | `C24 lectura sin red y sin cache da error con reintento; con cache la muestra` | `test/` | Sí: `loadError` nulo y `onRetry()` no reintenta |
| C25 | `C25 el ViewModel no repite el pedido al recrearse` (la rotación real se verifica a mano) | `test/` | Sí: `expected:<1> but was:<0>` en Auth y `expected:<[Anita]> but was:<[]>` en Edit Profile |
| C26 | Prueba manual: cuenta A lee y escribe `users/{uidA}`; evidencia `C26.png` | simulador de reglas o app | n/a (manual) |
| C27 | Prueba manual: cuenta B lee y escribe `users/{uidA}` y se deniega; evidencia `C27.png` | simulador de reglas o app | n/a (manual) |
| C28 | Prueba manual: sin sesión se deniega; evidencia `C28.png` | simulador de reglas | n/a (manual) |
| C29 | Prueba manual: otra ruta se deniega; evidencia `C29.png` | simulador de reglas | n/a (manual) |
| C30 | `scripts/reglas.sh` falla si `ui/` importa `com.google.firebase` | `scripts/` | Pendiente para la implementación (extender `scripts/reglas.sh`) |
| C31 | `C31 cada excepcion de Firebase se traduce a un error propio` | `test/` | Sí: `expected:<INVALID_CREDENTIALS> but was:<UNKNOWN>` y mensajes sin texto de `strings.xml` |
| C32 | `scripts/reglas.sh` falla si `FirebaseUser` o `DocumentSnapshot` aparecen en una firma pública de `data/repository` | `scripts/` | Pendiente para la implementación (extender `scripts/reglas.sh`) |
| C33 | `C33 signin con email invalido o contrasena vacia no llama a Firebase; contrasena corta si` | `test/` | Sí: `expected:<1> but was:<0>` (la contraseña corta no llega a Firebase) |
| C34 | `C34 demasiados intentos muestra su error propio` | `test/` | Sí: `formError` nulo |
| C35 | `C35 sin permiso o sesion invalida cierra la sesion` | `test/` | Sí: `expected:<1> but was:<0>` (no cierra la sesión) |
| C36 | `C36 editar el nombre no modifica goals guardados` | `test/` | Sí: `toFields()` devuelve `{}` (no hay campos que comparar) |
| C37 | `C37 segundo toque en Save changes no envia otro pedido` | `test/` | Sí: `expected:<1> but was:<0>` |
| C38 | `C38 enum desconocido o campo ausente usa el valor por defecto` | `test/` | Sí: `expected` perfil por defecto con `Profile.default` neutro; compara contra valores escritos a mano en el test |
| C39 | `./gradlew assembleDebug` compila el grafo de Hilt con el módulo | build | n/a |
| C40 | Revisión: clon limpio + `.example` compila; `git ls-files` no lista `google-services.json` | CI y revisión | n/a |
| C41 | Revisión del README (`revisor-requisitos`) | revisión | n/a |

Evidencia de las reglas (C26 a C29): cuatro capturas del simulador de reglas de la consola o de la app, nombradas `C26.png` a `C29.png`, en `docs/evidencia/firebase/`, indicando el `uid` usado.

## Auditoría
- auditor-spec (2026-10-25): 15 hallazgos (2 críticos, 8 altos, 5 medios). Corregidos en esta versión: C7 (rediseñado), C13 (destino inicial síncrono), C17 (acotado al perfil), C19 (actualización parcial) y C36, C21, C22/C37, C24, C38, C33, C34, C35, C39 a C41, perfil por defecto (valores propuestos), tipos de test, formato de evidencia y reglas. Pendiente: actualizar PANTALLAS §3, §12 y §14 y CONTEXTO (el `NavHost` ya no arranca siempre en `access_graph`) al implementar. Aceptado sin cambio: el acceso demo se reemplaza (ver Historial).

## Historial
- 2026-10-25: spec creada. Decisiones de la entrevista: perfil nuevo con nombre y email reales y el resto por defecto; nombre completo dividido en primera palabra y resto; el acceso de demostración se reemplaza.
- 2026-10-25: tras la auditoría. Decisión: **Firebase siempre, sin camino demo** (la CRÍTICA 1 del auditor se acepta como riesgo: sin `google-services.json` o sin cuenta no se entra a Home; se mitiga con la plantilla `.example` para que el clon compile, C40). Decisión: **se deja la caché de Firestore y las escrituras sin red quedan en cola** (cambia C19/C22: no se espera la confirmación del servidor).
