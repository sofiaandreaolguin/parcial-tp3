# Coach por Retrofit con historial en Room (EXT01)

Flujo: AI Voice
Requisitos: EXT01 (ampliación) · se apoya en RF05, RF06, RF07, RF08 · respeta NAV05 y UI01
Pantallas: `Voice / 05 — Conversation` (nodo `1:1512`). **`Voice / 01 — Idle` (`1:1624`) no se toca.**
Responsable: Fiore

## Qué hace

Hoy `conversation` es de demostración: guarda lo que se escribe y VORA no contesta. Con esto, VORA **contesta consultando al servidor del docente**, y **la conversación queda guardada**: si cerrás y abrís la app, el hilo sigue ahí.

Cuando el hilo solo tiene la conversación de ejemplo, la pantalla ofrece además las preguntas sugeridas que manda el servidor; tocar una la envía.

## Qué contesta VORA, exactamente

El servidor **no genera texto**: tiene **cuatro respuestas fijas** y elige una por palabras clave del mensaje (`mock/server.py:164-171`). Verificado contra el mock local y el publicado:

| Si el mensaje contiene | `topic` | Respuesta |
|---|---|---|
| `entren`, `workout`, `cansad` | `workout` | "Abrí el detalle del entrenamiento y registrá las series que completes. Este consejo es una respuesta de demostración." |
| `cen`, `comid`, `vegetar`, `desayun` | `nutrition` | "Podés registrar tu comida por búsqueda o carga manual. Revisá las cantidades antes de guardar; la respuesta es de demostración." |
| `progres`, `objetiv`, `semana` | `progress` | "Consultá las sesiones completadas y las comidas de los últimos siete días. El resumen se calcula con tus registros locales." |
| nada de lo anterior | `general` | "Elegí un entrenamiento o registrá una comida para completar el recorrido de la app." |

Las **tres sugerencias del docente caen cada una en un tema distinto**, así que tocando los tres chips se ven las tres respuestas temáticas. Para la demo eso alcanza y es determinista.

Todas llegan con `isSimulated: true`, y dos lo dicen en el propio texto. Por eso C11 pide marcarlo en pantalla: la app no puede presentarlas como si fueran consejo real.

## Decisión de diseño: dónde van las sugerencias

El servidor devuelve **tres** sugerencias **en español** (las escribió el docente, están en `mock/fixtures.json` y el servidor publicado devuelve lo mismo). `Voice / 01 — Idle` tiene **cuatro** chips **en inglés** dibujados en el Figma.

**Las sugerencias del servidor van en `Voice / 05`, solo con el hilo vacío. `Voice / 01` queda intacta.**

Por qué:
- `Voice / 01` ya pasó dos vueltas del revisor visual. Cambiarle la cantidad y el idioma de los chips rompe UI01, que es donde están los 40 puntos.
- El Figma dibuja `Voice / 05` con una conversación **ya empezada**: nunca definió cómo se ve vacía. Es el mismo caso del estado `Error`, que tampoco tenía frame y se resolvió con una regla propia documentada (bitácora del 22/10).
- Las sugerencias son **datos del servidor**, no textos de la app: se muestran tal como llegan, igual que el nombre de un entrenamiento. No van a `strings.xml`.

## Decisión de diseño: la conversación de ejemplo se siembra en Room

Hoy `Voice / 05` arranca con `HILO_DEMO`: los cuatro mensajes del frame `1:1512`, con sus horas (`12:41`, `12:42`, `12:43`) y el pie "Listen to response (0:14)". Son los textos que están dibujados en el Figma, y los que reproducen `docs/evidencia/visual/voice-05-conversation-360dp.png` y `-411dp.png`.

Si el hilo pasara a leerse de Room sin más, en una instalación limpia la pantalla quedaría **vacía** y esas dos capturas dejarían de reproducirse: se rompe UI01/UI03 en una pantalla ya aprobada.

**Decisión (26/10, Fiore): el hilo de ejemplo se siembra en Room la primera vez que se abre la base.** La pantalla se ve igual que hoy, las capturas siguen valiendo, y a partir del primer mensaje propio todo es real y persistente.

- Los cuatro mensajes sembrados llevan `isSimulated = true` y `role` alternando `user` / `assistant`.
- Sus textos salen de `strings.xml` (son textos de la app, no del servidor), así que se siembran desde una capa que pueda resolverlos, **no desde el DAO**.
- `createdAt` de los cuatro se fija de modo que respeten el orden del frame y queden siempre **antes** que cualquier mensaje nuevo.
- Queda declarado en la tabla de funciones simuladas del README.

## Impacto en lo que ya está

Esta spec **reemplaza criterios de [`asistente-vora.md`](asistente-vora.md)** y obliga a tocar tests que hoy están en `main`. Se deja escrito porque `CLAUDE.md` prohíbe tocar un test para que pase sin que lo decida una persona.

| Qué | Hoy | Con EXT01 | Quién decidió |
|---|---|---|---|
| `asistente-vora.md` **C27** ("la app NO DEBE responder sola") | vigente | **reemplazado** por C6 de esta spec | Fiore, 26/10 |
| `asistente-vora.md` C24, C25, C28 | vigentes | siguen valiendo, ahora sobre Room (C5, C8, C15) | Fiore, 26/10 |
| `ConversationViewModelTest`: `enviar no genera una respuesta automatica` | pasa | **se borra** (afirma lo contrario de C6) | Fiore, 26/10 |
| `ConversationViewModelTest`: los otros 4 tests | pasan | **se reescriben**: hoy construyen `ConversationViewModel()` sin argumentos y el nuevo recibe repositorio y `Clock` | Fiore, 26/10 |

Al cerrar la tarea hay que dejar una línea en el Historial de `asistente-vora.md` apuntando acá.

## Zonas con responsable: hay que avisar

`CLAUDE.md` asigna la base Room y las comidas a **Sofi**, y la red a **Hector**: *"si una tarea necesita cambiarlas, avisá y no lo hagas por tu cuenta"*. Esta spec toca las dos.

| Qué se toca | Responsable | Estado del aviso |
|---|---|---|
| `VoraDatabase` (nueva Entity y DAO, `version` 3 → 4) y `DatabaseModule` | Sofi | **avisado el 26/10**, incluido que se borra `meals` |
| `VoraApi` (dos endpoints nuevos) | Hector | **avisado el 26/10** |

**Ojo con subir la versión de la base.** `DatabaseModule` construye con `fallbackToDestructiveMigration(dropAllTables = true)`: al pasar a `version = 4`, **se borran todas las tablas, incluida `meals`**. No es un bug —es lo decidido en [`003-base-room-compartida.md`](../../docs/decisiones/003-base-room-compartida.md)— pero significa que **quien tenga comidas cargadas las pierde** en el primer arranque después del cambio. Antes de grabar el video o rehacer evidencia de RF04 hay que volver a cargar una comida.

## Datos

**DTO** (`data/remote/dto/CoachDto.kt`) — envoltura `{"data": ...}`, la misma de workouts, así que se reusa `ApiResponseDto`:

- `SuggestionDto`: `id: String`, `text: String`
- `CoachRequestDto`: `text: String` (1 a 2000 caracteres, lo valida el servidor)
- `CoachReplyDto`: `role: String` (siempre `"assistant"`), `text: String`, `topic: String` (`general` | `workout` | `nutrition` | `progress`), `isSimulated: Boolean`

**Modelo** (`domain/model/`):

- `CoachSuggestion`: `id: String`, `text: String`
- `CoachMessage`: `id: Long`, `role: CoachRole` (enum `User` | `Assistant`), `text: String`, `topic: String?`, `isSimulated: Boolean`, `createdAt: Long`

**Entity** (`data/local/entity/CoachMessageEntity.kt`) — tabla `coach_messages`, con la convención de `MealEntity` (enum como texto, sin `TypeConverter`):

| Columna | Tipo | Por qué |
|---|---|---|
| `id` | `Long`, `autoGenerate` | igual que `meals` |
| `role` | `String` | `"user"` o `"assistant"` |
| `text` | `String` | |
| `topic` | `String?` | nulo en los mensajes de la persona |
| `isSimulated` | `Boolean` | el servidor lo manda; se muestra en pantalla |
| `createdAt` | `Long` | ordena el hilo; sale del `Clock` inyectado |

`VoraDatabase` pasa de `version = 3` a `4`. **No hace falta migración a mano**: `DatabaseModule` ya construye con `fallbackToDestructiveMigration(dropAllTables = true)`.

## Criterios de aceptación (EARS)

**Siembra del hilo de ejemplo**

- C1. CUANDO se crea la base por primera vez, la app DEBE guardar los cuatro mensajes de `HILO_DEMO` con `isSimulated = true` y `createdAt` creciente en el orden del frame.
- C2. La app DEBE sembrar una sola vez: CUANDO se abre la app con la base ya creada, el hilo NO DEBE volver a traer los mensajes de ejemplo.

**Sugerencias**

- C3. CUANDO se abre `conversation` y el hilo solo tiene los mensajes sembrados, el ViewModel DEBE pedir `GET /v1/coach/suggestions` **una sola vez por ViewModel**.
- C4. CUANDO llegan sugerencias, el estado DEBE exponerlas en el mismo orden que las devolvió el servidor.
- C5. CUANDO se toca el chip número *n*, la app DEBE enviar el `text` de **esa** sugerencia como mensaje de la persona.
- C6. MIENTRAS el hilo tenga algún mensaje posterior a la siembra, el estado NO DEBE exponer sugerencias.
- C7. SI el `GET` devuelve `data` vacío (escenario `empty`), ENTONCES el estado DEBE quedar sin chips y DEBE exponer `conversation_suggestions_empty`.
- C8. SI el `GET` falla con cualquier `AppError`, ENTONCES el estado DEBE quedar sin chips, con el envío habilitado, y NO DEBE pasar a un estado de error de pantalla completa.

**Envío y respuesta**

- C9. CUANDO se envía un mensaje, la app DEBE guardarlo en Room con `role = user` **antes** de llamar al servidor.
- C10. CUANDO el servidor responde, la app DEBE guardar la respuesta con `role = assistant`, su `topic` y su `isSimulated`.
- C11. MIENTRAS un mensaje **traído del servidor** tenga `isSimulated = true`, la burbuja DEBE mostrar `conversation_simulated`.
- C11b. Los cuatro mensajes sembrados NO DEBEN mostrar esa marca, aunque C1 los guarde con `isSimulated = true`.
  **Por qué:** el frame `1:1512` no los dibuja marcados y son la evidencia de UI01
  (`docs/evidencia/visual/voice-05-conversation-411dp.png`); ponerles la etiqueta rompería una pantalla ya
  aprobada. Que ese hilo es de ejemplo queda declarado en la tabla de funciones simuladas del README, que es
  donde la consigna pide declararlo. Lo resuelve `conElDibujoDelFrame()` en `ConversationRoute`, junto con la
  hora y el pie de audio, que tampoco están en la Entity porque son parte del dibujo y no del mensaje.
  *(Decidido el 26/10 por Fiore, al implementar la pantalla.)*
- C12. SI el texto está vacío o es solo espacios, ENTONCES la app NO DEBE llamar al servidor.
- C13. SI el texto supera los 2000 caracteres, ENTONCES la app NO DEBE llamar al servidor y DEBE mostrar `conversation_error_too_long`. (El servidor responde 400 `INVALID_REQUEST`; se corta antes.)

**Historial (lo que demuestra Room)**

- C14. El DAO DEBE devolver el hilo ordenado por `ORDER BY createdAt ASC, id ASC`, para que dos mensajes con el mismo milisegundo queden en orden de inserción.
- C15. La app DEBE mostrar el hilo a partir de un `Flow` del DAO: CUANDO se inserta un mensaje, el estado DEBE emitirlo sin volver a consultar.
- C16. CUANDO se cierra por completo la app y se vuelve a abrir, la app DEBE mostrar el hilo completo guardado. (Se comprueba en el emulador: ver Tests.)

**Errores del envío (RF05)**

- C17. SI el envío falla sin conexión, ENTONCES la app DEBE mostrar `conversation_error_no_connection` con reintento.
- C18. SI el servidor devuelve 503, ENTONCES la app DEBE mostrar `conversation_error_server` con reintento.
- C19. SI el cuerpo es `malformed` o viene cortado, ENTONCES la app DEBE mostrar `conversation_error_invalid_data` con reintento.
- C20. SI el servidor devuelve 404, ENTONCES la app DEBE mostrar `conversation_error_server` con reintento.
- C21. SI el servidor demora **menos** que el timeout de OkHttp, ENTONCES la app NO DEBE tratarlo como error.
- C22. SI el envío falla por cualquier causa, ENTONCES el mensaje de la persona ya guardado NO DEBE borrarse y el historial DEBE seguir visible.
- C23. CUANDO se toca reintentar, la app DEBE reenviar **el texto del mensaje que falló** (el que guarda el ViewModel, no el último de Room) y NO DEBE insertarlo otra vez en Room.
- C24. La traducción de la excepción a error propio DEBE hacerse en el repositorio con `AppError` / `toAppError()`, que ya existen; la pantalla nunca ve una excepción.

**Estado y rotación (RF07)**

- C25. MIENTRAS se espera la respuesta, el estado del **ViewModel** DEBE exponer `enviando = true` y la pantalla DEBE deshabilitar el envío.
- C26. CUANDO se llama dos veces seguidas a `onEnviar` sin dejar correr la primera, el repositorio DEBE recibir **un solo** POST y Room **un solo** insert. (La marca se pone **antes** del `launch`.)
- C27. CUANDO se rota la pantalla, la app DEBE conservar el hilo y NO DEBE repetir el `GET` de sugerencias.
- C28. CUANDO se rota mientras se espera la respuesta, la app DEBE seguir mostrando que está enviando, NO DEBE repetir el POST y DEBE guardar la respuesta cuando llegue.
- C29. CUANDO se rota, lo escrito y sin mandar DEBE conservarse (sigue valiendo C28 de `asistente-vora.md`).

**Permisos y build**

- C30. La app NO DEBE pedir permiso de micrófono: el botón del frame sigue sin hacer nada (NAV05).
- C31. El proyecto DEBE compilar con `./gradlew assembleDebug` y `./scripts/reglas.sh` NO DEBE dar hallazgos.

## Contrato

Las firmas se acuerdan acá para que `tests-rojo` compile contra ellas.

| Pieza | Archivo | Firma |
|---|---|---|
| API sugerencias | `data/remote/VoraApi.kt` | `suspend fun getCoachSuggestions(): Response<ApiResponseDto<List<SuggestionDto>>>` |
| API mensajes | `data/remote/VoraApi.kt` | `suspend fun postCoachMessage(@Body body: CoachRequestDto): Response<ApiResponseDto<CoachReplyDto>>` |
| DAO | `data/local/dao/CoachDao.kt` | `fun observeAll(): Flow<List<CoachMessageEntity>>` · `suspend fun insert(m: CoachMessageEntity): Long` · `suspend fun count(): Int` |
| Repositorio | `data/repository/CoachRepository.kt` | `suspend fun getSuggestions(): Result<List<CoachSuggestion>>` · `fun observeHistory(): Flow<List<CoachMessage>>` · `suspend fun send(text: String): Result<CoachMessage>` |
| Siembra | `data/repository/CoachRepositoryImpl.kt` | `suspend fun seedIfEmpty(demo: List<CoachMessage>)` — la llama el ViewModel, que resuelve los `@StringRes` |
| Mapeo a UI | `ui/screens/voice/ConversationUiState.kt` | `fun CoachMessage.toChatMessage(): ChatMessage` — **fuera de `data/`**: `reglas.sh` prohíbe que `ui/` importe `data.*`, así que el ViewModel recibe el modelo de dominio |
| Estado | `ui/screens/voice/ConversationUiState.kt` | `data class ConversationUiState(messages, suggestions, enviando, error: Int?, sinSugerencias)` |
| ViewModel | `ui/screens/voice/ConversationViewModel.kt` | `onEnviar(texto: String)` · `onSugerencia(s: CoachSuggestion)` · `onReintentar()` |
| Hilt | `di/DatabaseModule.kt` | `@Provides fun provideCoachDao(db: VoraDatabase): CoachDao` |
| Hilt | `di/RepositoryModule.kt` | `@Binds @Singleton fun bindCoachRepository(impl: CoachRepositoryImpl): CoachRepository` |

**`ChatMessage.id` pasa de `Int` a `Long`** para coincidir con el id de Room; es la `key` del `LazyColumn` en `ConversationScreen`.

## Textos nuevos

Van en `values/strings.xml` **y en `values-es/strings.xml`** con las mismas claves (lo controla `reglas.sh`).

| Clave | Inglés | Español |
|---|---|---|
| `conversation_simulated` | Simulated response | Respuesta simulada |
| `conversation_sending` | Sending… | Enviando… |
| `conversation_retry` | Retry | Reintentar |
| `conversation_error_no_connection` | No connection. Check your network. | Sin conexión. Revisá tu red. |
| `conversation_error_server` | The server is not responding. | El servidor no responde. |
| `conversation_error_invalid_data` | The response could not be read. | No se pudo leer la respuesta. |
| `conversation_error_too_long` | The message is too long. | El mensaje es demasiado largo. |
| `conversation_suggestions_empty` | No suggestions available. | No hay sugerencias disponibles. |

## Fuera de alcance

- Tocar `Voice / 01 — Idle`: sus cuatro chips siguen saliendo de `strings.xml`.
- Los estados de voz (`Listening`, `Processing`, `Speaking`): siguen simulados.
- Borrar o editar mensajes del hilo.
- Mandar `context.coachingStyle` en el `POST` (el servidor lo acepta, pero no hay pantalla donde elegirlo).
- Traducir las sugerencias ni las respuestas: llegan del servidor y se muestran como llegan.
- **Reintentar después de cerrar la app.** El texto que falló vive en el ViewModel, no en la Entity: si se cierra la app con un envío fallado, al volver queda el mensaje propio sin respuesta y sin cartel. Guardar un estado `pendiente` en la tabla queda para más adelante; acotarlo así es lo que mantiene la ampliación chica.
- Cambiar el pie de audio de las burbujas (`audioRes`): sigue como está, por fidelidad al frame.

## Tests

| Criterio | Test | Dónde | Vi el rojo |
|---|---|---|---|
| C2 | `c2_count_dice_si_hay_que_sembrar` | `androidTest/` |  sí (`CoachDaoTest`) |
| C1 | `HiloSembradoTest`: 6 tests (cantidad, orden, roles, marca de simulado, numeración, y que corte si no llegan cuatro textos) | `test/` | control negativo el 26/10 |
| C3, C27 | `pide_sugerencias_una_sola_vez_por_viewmodel` | `test/` |  sí |
| C4 | `sugerencias_conservan_el_orden_del_servidor` | `test/` |  sí |
| C5 | `tocar_el_chip_n_envia_ese_texto` | `test/` |  sí |
| C6 | tres tests: `con mensajes propios no quedan sugerencias` (camino de enviar), `un mensaje que llega al hilo sin enviarlo tambien saca los chips` (el otro camino) y **`al reabrir con la conversacion empezada no vuelven los chips`**, que es el único que detecta el bug del 26/10 | `test/` | control negativo el 26/10 |
| C7 | `sugerencias_vacias_muestran_el_aviso` | `test/` |  sí |
| C8 | `fallo_de_sugerencias_no_bloquea_el_envio` | `test/` |  **pasa en rojo** |
| C9 | `enviar_guarda_el_mensaje_propio_antes_de_llamar` | `test/` |  sí |
| C10 | `respuesta_guarda_topic_y_isSimulated` | `test/` |  sí |
| C11 | `burbuja_simulada_muestra_el_aviso` | `test/` |  sí |
| C12 | `texto_vacio_o_en_blanco_no_llama_al_servidor` | `test/` |  **pasa en rojo** |
| C13 | `texto_de_mas_de_2000_no_llama_al_servidor` | `test/` |  sí |
| C14 | `c14_ordena_por_createdAt` y `c14_con_el_mismo_createdAt_ordena_por_id` | `androidTest/` |  sí |
| C15 | `insertar_emite_el_hilo_sin_volver_a_consultar` | `test/` |  sí |
| C16 | Evidencia manual: `adb shell am force-stop` y reabrir, captura en `docs/evidencia/coach/` | emulador | — |
| C17 a C21 | `repositorio_traduce_sin_conexion_503_malformed_404_y_demora` (MockWebServer) | `test/` |  sí, salvo la demora (**pasa en rojo**) |
| C22 | `fallo_de_envio_no_borra_el_historial` | `test/` |  sí |
| C23 | tres tests: `reintentar reenvia el texto que fallo` (ViewModel), `reintentar el mismo texto no lo duplica en Room` y `preguntar dos veces lo mismo a proposito deja los dos mensajes` (repositorio) | `test/` | control negativo el 26/10 |
| C24 | `coach_repository_usa_toAppError` | `test/` | |
| C25 | `mientras_envia_el_estado_lo_expone` | `test/` |  sí |
| C26 | `dos_toques_seguidos_mandan_un_solo_post` | `test/` |  sí |
| C28 | `rotar_durante_el_envio_no_repite_el_post` | `test/` |  sí |
| C29 | `ConversationRotacionTest`, el **primer test de Compose del proyecto**: escribe en la barra, simula la restauración de estado y comprueba que el texto sigue ahí | `androidTest/` | control negativo el 26/10 |
| C30 | Lo cubre C22 de [`asistente-vora.md`](asistente-vora.md) y el control de `reglas.sh` | `test/` | — |
| C31 | `./gradlew assembleDebug` y `./scripts/reglas.sh` | build | — |
| C10, C14 | `coach_mappers_dto_modelo_entity`: `role` desconocido, `topic` nulo, `isSimulated` ausente | `test/` |  sí, salvo `isSimulated` ausente (**pasa en rojo**) |

**C16 no se prueba con `Room.inMemoryDatabaseBuilder`**: una base en memoria no puede demostrar que el hilo sobrevive a cerrar la app. Va como evidencia manual en el emulador, al estilo de C31 a C35 de [`estados-de-carga-y-error.md`](estados-de-carga-y-error.md). Hecha el 26/10: `docs/evidencia/coach/coach-historial-tras-cerrar-411dp.png`, después de `adb shell am force-stop`.

**Lo que los tests NO cubren, dicho en limpio.** Desde el 26/10 el proyecto tiene **un** test de
Compose, `ConversationRotacionTest`, que cubre C29. Los demás criterios de pantalla siguen sin test:
cada uno necesitaría su propio test de Compose con emulador, y se decidió no abrir ese frente a dos
días de la entrega. Se apoyan en el código y en la evidencia manual:

| Criterio | Cómo se comprueba hoy |
|---|---|
| C5 (el chip *n* manda ese texto) | el ViewModel sí tiene test; que el chip llame al ViewModel, no |
| C7 (aviso de sin sugerencias) y C11 (marca de simulada) | el estado tiene test; que la pantalla lo dibuje, no |
| C17 a C20 (botón de reintentar) | `docs/evidencia/coach/coach-error-reintento-411dp.png` y `coach-reintento-ok-411dp.png` |
| C25 (no se puede mandar mientras espera) | el estado tiene test; el corte en la pantalla, no |

**Rojo verificado el 26/10.** `./gradlew testDebugUnitTest`: **243 tests, 27 fallan**, los 27 por el resultado (`expected… but was…`), ninguno por compilación ni por una excepción suelta. Los `androidTest` compilan pero no se corrieron: necesitan el emulador abierto.

**Cinco pasan en rojo** y necesitan control negativo al implementar, porque hoy pasan por cómo son los valores neutros y no porque prueben algo:

| Test | Por qué pasa sin implementación |
|---|---|
| `con_mensajes_propios_no_hay_chips` | nunca hay sugerencias, así que la lista siempre está vacía |
| `fallo_de_sugerencias_no_bloquea_el_envio` | el estado neutro ya cumple lo que pide |
| `texto_vacio_o_en_blanco_no_llama_al_servidor` | el stub no llama al servidor nunca |
| la demora de `repositorio_traduce_…` | el stub devuelve éxito siempre |
| `isSimulated` ausente en los mappers | el valor neutro de `isSimulated` ya es `false` |

Al implementar, cada uno se rompe a propósito y se confirma que falla (`CONTEXTO.md` §7).

### Control negativo, hecho el 26/10

Se rompió el código a propósito, uno por uno, y se confirmó que el test lo detecta:

| Qué se rompió | ¿Falló el test? |
|---|---|
| Sacar el corte de texto vacío | **sí**, 1 test |
| Que los chips no se vayan nunca | **no** — ver abajo |
| Que un fallo de sugerencias bloquee la pantalla | **sí**, 1 test |
| `isSimulated` ausente tomado como `true` | **sí**, 1 test |
| Tratar cualquier respuesta como error | **sí**, 3 tests |

**`con_mensajes_propios_no_hay_chips` no detecta nada.** El test envía un mensaje y comprueba que no queden chips, pero las sugerencias se vacían **dos veces** por caminos distintos: al arrancar el envío (`enviar()`) y al crecer el hilo por encima de lo sembrado. Con el segundo roto, el primero sigue haciendo pasar el test.

### Cómo se cerró, el 26/10

Fiore decidió reforzar los tests en vez de dejarlo anotado. **El test viejo no se tocó** —cubre un camino real— y se agregaron dos al lado, así que ahora C6 tiene tres. La medición, rompiendo el código a propósito y mirando test por test cuál lo detecta:

| Test de C6 | ¿Detecta el bug del 26/10? |
|---|---|
| `con mensajes propios no quedan sugerencias` (el viejo) | **no** |
| `un mensaje que llega al hilo sin enviarlo tambien saca los chips` | **no** |
| `al reabrir con la conversacion empezada no vuelven los chips` | **sí** |

De los 23 tests del ViewModel, ese último es el **único** que falla cuando se rompe el código, y es el que reproduce lo que se vio en el emulador: la base contesta antes que el servidor, y el servidor repone los chips encima de una conversación ya empezada.

El de en medio tampoco es de relleno: cubre el camino de C6 que el viejo tapaba (que el hilo crezca **sin** pasar por `onEnviar`). Que no detecte *este* bug en particular es porque el bug está en el orden de llegada, no en ese camino.

## Al cerrar la tarea hay que actualizar

Hecho el 26/10:

- [x] `specs/PANTALLAS.md` y el inventario del README: `Voice / 05 — Conversation` pasó de **Demostración** a **Funcional**.
- [x] README, tabla de funciones simuladas: se separó la fila del asistente en dos, porque la pantalla ya no es simulada pero su contenido sí (el hilo sembrado y las cuatro respuestas fijas del servidor).
- [x] `specs/REQUISITOS.md`: sección **Extras** nueva, con EXT01 hecho y EXT02 sin empezar.
- [x] `specs/funcionales/asistente-vora.md`: C27 tachado y reemplazado por el C6 de acá, su test marcado como borrado, y línea en el Historial.
- [x] README: el conteo de tests pasó de 216 a 243, y se sumaron los 20 de Room y la sección del extra.
- [x] `docs/BITACORA.md`: entrada de la tarea.

## Auditoría

- **auditor-spec, 26/10:** 15 hallazgos. Se corrigieron todos los críticos y altos. Lo que cambió: se agregó la siembra del hilo de ejemplo (sin eso, `Voice / 05` quedaba vacía en instalación limpia y se rompían dos capturas de evidencia de UI01/UI03); se agregó la sección "Impacto en lo que ya está" porque C6 reemplaza a C27 de `asistente-vora.md` y obliga a borrar un test que está en `main`; se agregó el aviso a Sofi y Hector por zonas con responsable, y la advertencia de que subir la base a `version = 4` borra la tabla `meals`; se partieron los criterios que mezclaban dos cosas (los viejos C4, C9, C11, C14, C15); se sumaron los casos que faltaban de la plantilla (sugerencias vacías, 503, `malformed`, 404, demora, texto de más de 2000, rotación durante el envío, doble toque); se agregaron la tabla de Contrato y la de textos, que sin ellas `tests-rojo` no compila; y C16 pasó a evidencia manual porque una base en memoria no demuestra persistencia.
- Hallazgo aceptado pero **no** resuelto: el reintento no sobrevive a cerrar la app. Queda en Fuera de alcance, a propósito.
- Hallazgo de contexto: `METODOLOGIA.md` §16 pide el núcleo **verificado** antes de una ampliación, y VC-s (control visual) sigue pendiente. El grupo decidió el 26/10 avanzar igual, porque VC-s es de Zaki y corre en paralelo. Si EXT01 no cierra, se abandona sin tocar `main`.
- **`revisor-arquitectura` y `revisor-requisitos`, 26/10, con la pantalla ya hecha.** Los dos marcaron el mismo bug de C6 (los chips volvían), que se reprodujo en el emulador y se corrigió. También se corrigieron, de lo que marcaron: el aviso de "sin sugerencias" que no se apagaba nunca, el botón de reintentar que aparecía en el error de texto demasiado largo sin hacer nada, el idioma congelado del hilo sembrado, y dos comentarios que decían que `createdAt` sale del `Clock` cuando sale de contar filas.
- **Marcado y no resuelto, porque cambiarlo obliga a reescribir tests** (y eso lo decide una persona, `CLAUDE.md`): la carga arranca en la composición con un `LaunchedEffect` en vez del `init` del ViewModel, como pide `CONTEXTO.md` §2, y la decoración del frame empareja por posición en lugar de por id. Las dos se arreglan inyectando un `StringProvider` al ViewModel, lo que cambia su constructor; los tests de `tests-rojo` lo llaman con la forma actual.
- **Resuelto el 26/10, por decisión de Fiore:** C6 pasó de un test que no detectaba nada a tres, y se cubrieron los huecos que marcó `revisor-requisitos` (C1, C18, C19, C20 y las dos mitades de C23). Son **15 tests nuevos, 243 → 258**, cada uno con su control negativo. El test flojo **no se modificó**: se le sumaron otros dos al lado.

## Historial

- 26/10/2026 · Spec inicial. Los tres endpoints se probaron contra el mock local **y contra el servidor publicado** (`https://tp3.martinrivas.ar/`): devuelven lo mismo, y las cuatro respuestas del coach son fijas y elegidas por palabra clave. Dos decisiones del grupo ese día: dónde van las sugerencias, y sembrar el hilo de ejemplo en Room.
- 26/10/2026 · Implementada y cerrada (Fiore). Tres cosas que aparecieron al escribir la pantalla y no estaban previstas en la spec:
  1. **Room guarda el mensaje, no el dibujo.** Las cuatro burbujas sembradas perdían la hora (`12:41`, `12:42`, `12:43`) y el pie "Listen to response (0:14)", que están en el frame y no en la Entity. Y el texto, al guardarse resuelto, quedaba congelado en el idioma de la siembra. Lo devuelve `conElDibujoDelFrame()` en el Route, junto con lo de C11b.
  2. **Bug real encontrado por `revisor-arquitectura` y `revisor-requisitos`, confirmado en el emulador:** al reabrir la app con la conversación empezada, volvían los tres chips. Las sugerencias se escribían por dos caminos que no se miraban entre sí, y al reabrir el historial emite primero y el GET volvía después a reponerlos. Ahora una sola función decide si se ven. **Lo tapaba el test flojo de C6** que el control negativo ya había marcado: no era solo un test feo, escondía un defecto.
  3. **Un segundo bug, también propio:** la lista no bajaba a mostrar la respuesta. El efecto del scroll estaba atado a `mensajes + (1 si envía)`, y al llegar la respuesta los dos cambios se compensan, el número queda igual y Compose no vuelve a ejecutarlo. Ahora mira las dos cosas por separado.
- 26/10/2026 · Tests reforzados (Fiore). Se cerraron los huecos que marcó `revisor-requisitos`: **15 tests nuevos, de 243 a 258**, todos con control negativo. Para poder probar C1 hubo que sacar el armado de la siembra del Route a `hiloSembrado()`, una función común que recibe los textos ya resueltos; el Route solo resuelve los `@StringRes`. **Ningún test se modificó para que pasara**: el flojo de C6 quedó como estaba y se le agregaron dos al lado. Los dos que siguen sin test son C29 (lo escrito vive en `rememberSaveable`, hace falta un test de Compose) y los criterios que se dibujan en la pantalla, listados arriba.
- 26/10/2026 · Decisiones que quedaron escritas acá y no en el código: C11b (las sembradas no llevan la marca de simulada) y la lista de criterios de pantalla sin test, arriba.
