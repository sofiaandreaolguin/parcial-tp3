# Ensayo del recorrido de la demo (tarea v)

Fecha: 2026-10-27 · Commit probado: `0b47c64` (`main`) · Checklist: `specs/METODOLOGIA.md` §14

## Condiciones
- Quién conduce: Martu pidió el ensayo y Claude Code manejó el emulador con `adb` (toques, capturas y volcados de la jerarquía de la pantalla). **No lo condujo una persona del grupo**, así que los tiempos no son los de una presentación real y no se cronometraron.
- Emulador: Pixel_9_Pro (1280x2856, 480 dpi = 427 dp de ancho; 360 dp con `wm size 1080x2400`) · instalación limpia: sí (`uninstall` + `install` al empezar; `pm clear` antes de los escenarios con caché vacía)
- Servidor: mock local `python3 mock/server.py --host 0.0.0.0` con `api.baseUrl=http://127.0.0.1:8000/` en `local.properties` y `adb reverse tcp:8000 tcp:8000`
- Cuenta de Firebase de prueba creada de antemano: sí (`usuariopruebas@gmail.com`) · con internet: sí
- Antes de empezar: `./gradlew testDebugUnitTest assembleDebug` → 380 tests, 0 fallos, APK generado

## Resultados

| # | Paso | Req. | ✅/❌ | Tiempo | Observaciones |
|---|---|---|---|---|---|
| 1 | Sign Up/Sign In, Home y las 37 pantallas: atrás, barra inferior, modo demostración, Voice con sus 5 estados, sin pedir micrófono/cámara | NAV01–NAV05 | ✅ con observaciones | | Las 37 pantallas se alcanzaron desde el recorrido: Login 01/02, Home 01/02, Voice 01 a 06, Workout 01 a 07, Nutrition 01 a 08, Progress 01 a 06, Profile 01 a 06. Login con Firebase OK. **Primer intento de Sign In dio "No connection"** y no se reprodujo tras reiniciar el wifi del emulador. Voice: Idle → Listening → Processing → Speaking con un toque en cada micrófono; el **Error aparece en la segunda vuelta** (vuelta par) con el cartel de micrófono, "Allow Access" vuelve a Idle sin diálogo del sistema y el manifiesto solo declara `INTERNET` (NAV05). Conversation se alcanza con "Type instead". Food Analysis (Nutrition 05) dura ~2 s y pasa solo a 07. Photo Analysis no pide cámara. Settings: "Permissions & Data Controls" no abre ajustes del sistema y las filas sin frame muestran el toast "Demo mode: this option isn't available yet" |
| ⚠️ | | **Observaciones del paso 1:** (a) la tarjeta "LAST CONVERSATION" de Voice no navega (no tiene `onClick` en `VoiceScreen.kt`) pero `specs/PANTALLAS.md` línea 184 dice que lleva a `conversation`; (b) las sugerencias de Conversation salen en español ("¿Cómo empiezo mi entrenamiento?") con la app en inglés; (c) Progress muestra fijo "TUESDAY, 8 SEPTEMBER" y Workout "TUE 8", mientras Nutrition muestra la fecha real (28 de octubre) — datos de demostración |
| 2 | Catálogo remoto y dos entrenamientos distintos con su detalle | RF01, NAV04 | ✅ | | Plan semanal con los 3 del mock; `w01` Fuerza inicial y `w02` Entrenamiento completo abren cada uno su detalle |
| 3 | Log Manually → pop-up (validación) → 07 sin ingredientes (error) → con ingrediente → Save → total sube → cerrar y abrir la app (C51) → eliminar con confirmación → total baja | RF03, RF04 | ✅ | | Pop-up: "Required" / "Choose a meal type" / "Can't be negative". 07 sin ingredientes: "Add at least one ingredient to save the meal". Guardado 250 kcal, total 0 → 250 (12 %); tras `force-stop` y reabrir sigue (C51 cerrado); Delete con diálogo → total vuelve a 0. **Observación:** la fila de Nutrition y Meal Details muestran el nombre del ingrediente ("Pollo"), no el nombre del pop-up ("Pollo con arroz") |
| 4 | Mock apagado: `w01` desde Room, `w03` no disponible; error con reintento, lista vacía, malformed; relevantar y reintentar | RF02, RF05 | ✅ | | Mock apagado: `w01` carga desde Room; `w03` (nunca abierto): "This workout isn't available offline yet" + Retry; Retry sin servidor mantiene el aviso; con el servidor de vuelta carga `w03`. Escenarios (`api.mockScenario`, recompilando cada vez): **empty** → "There are no workouts yet."; **error** con caché de Room → la lista sigue visible (no se borra la caché), y con caché vacía → "The service isn't available right now." + Retry; **malformed** con caché vacía → "We received data we couldn't read." + Retry. `local.properties` restaurado a `api.baseUrl=http://127.0.0.1:8000/` (con `adb reverse`) |
| 5 | `git shortlog -sn` y explicación de ViewModel, repositorios, Hilt y Dagger, Firebase | RF06–RF08 | ⚠️ parcial | | Práctica con el agente `tp3:docente-examinador`: cinco preguntas (módulos Provides/Binds, Hilt vs Dagger y scopes, Route/Screen y rotación, DTO/Entity/modelo, Firebase y reglas). Las respuestas se prepararon leyendo el código con Claude, **no se dijeron de memoria**. Quedó sin contestar la sexta (commits de cada integrante e integración por pull request). Correcciones que salieron: son 21 ViewModels (no 24); el proyecto usa KSP (no kapt); no hay `@HiltAndroidTest` ni DataStore; `MealRepositoryImpl` sí captura `DateTimeParseException`. **Falta** que cada integrante lo diga en voz alta |
| + | Rotar en catálogo y comidas | RF07 | ✅ | | Catálogo: 20 pedidos al mock antes, durante y después de rotar a horizontal y volver (sin consultas nuevas). Comidas: el pop-up "Log Manually" con el nombre y el tipo cargados se reacomoda en horizontal y conserva el estado al volver; sin crash (la redibujada tarda 3 a 5 s en el emulador) |
| + | Repetir todo a 360 dp, con los pop-ups en castellano | UI03 | ✅ con observaciones | | Emulador a 1080x2400 con 480 dpi (= 360 dp). Home, Workout, Nutrition, Progress y Profile se ven completas. En castellano: el pop-up "Registrar a mano" y el diálogo "Agregar ingrediente" entran sin cortes (cierra lo "sin verificar" del PR #63). **Textos cortados:** chip "Registrar desayu…" en Home y pestaña "Entrenamie…" en la barra inferior. No se recorrieron a 360 dp las 37 pantallas, solo las cuatro pestañas más los pop-ups |

## Quién explica qué (commit propio de cada integrante)
| Integrante | Commit | Qué explica |
|---|---|---|
| Martu | | |
| Sofi | | |
| Hector | | |
| Zaki | | |
| Fiore | | |

## Fallas encontradas y qué se hizo
Ningún paso falló. Lo que apareció y queda **sin arreglar** (decisión del grupo):

| Paso | Qué pasó | Arreglo | Estado |
|---|---|---|---|
| 1 | Primer Sign In dio "No connection"; no se reprodujo tras reiniciar el wifi del emulador | Verificar la red antes de la demo | Sin causa confirmada |
| 1 | Tarjeta "LAST CONVERSATION" de Voice sin `onClick`, contra `PANTALLAS.md` línea 184 | Agregar el `onClick` o corregir la spec | Pendiente |
| 1 | Sugerencias de Conversation en español con la app en inglés | Decidir idioma | Pendiente |
| 1 | Progress muestra "TUESDAY, 8 SEPTEMBER" y Workout "TUE 8" fijos | Aceptado como demostración, o usar la fecha real | Pendiente |
| 3 | La fila de la comida muestra el ingrediente ("Pollo"), no el nombre del pop-up ("Pollo con arroz") | Confirmar con Sofi si es el diseño | Pendiente |
| 360 dp | Textos cortados: chip "Registrar desayu…" y pestaña "Entrenamie…" | Acortar los textos en castellano | Pendiente |

## Segundo ensayo (cronometrado, objetivo < 10 min)
**No se hizo.** Hace falta que lo conduzca una persona del grupo, de punta a punta y con cronómetro.

## Pendientes de documentación detectados
- [ ] README: "8 funcionales" y Login 01/02 y Edit Profile como "Demostración" (con Firebase son 11 funcionales)
- [ ] `specs/PANTALLAS.md` §17 punto 7: dice que Firebase no se integró a `main` (PR #57)
- [ ] `specs/PANTALLAS.md` ~línea 260 y §17 (`MealDao.deleteById`), README y `LEEME.md` de Nutrition: flujo de ingredientes (aviso a Fiore)
- [ ] README: link al video y cinco comparaciones (tarea u); ¿hay que regrabar el tramo de comidas anterior al #63?
- [ ] Etiqueta `entrega-tp3` (tarea w)
