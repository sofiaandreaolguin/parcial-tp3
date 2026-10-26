# feat(voice): coach por Retrofit con historial en Room (EXT01)

> **Base: `feat/firebase-extra` (la rama de Martu), no `main`.** Los dos extras se entregan
> juntos desde esa rama. Abrirlo con:
> `gh pr create --base feat/firebase-extra --head feat/voice-coach-historial`

## Qué cambia

`Voice / 05 — Conversation` deja de ser maqueta: VORA contesta consultando el servidor del docente y
la conversación se guarda en Room, así que sobrevive a cerrar la app. Es un extra, no lo pide la
consigna; se encaró con los ocho requisitos obligatorios ya en `main`.

## Carril y requisitos

- Carril: **C datos** (spec → tests en rojo → implementar) y después **B pantalla**.
- Requisitos: **EXT01**. Se apoya en RF05, RF06, RF07 y RF08, y respeta NAV05 y UI01.
- Spec: `specs/funcionales/coach-con-historial.md`, 31 criterios más C11b.
- **Reemplaza el C27 de `specs/funcionales/asistente-vora.md`** ("la app NO DEBE responder sola"),
  que queda tachado y explicado ahí mismo, y borra su test.

## Cómo probarlo

1. En `local.properties`, `api.baseUrl=https://tp3.martinrivas.ar/` (el servidor publicado; no hay
   que levantar nada). Con el mock local hace falta además `adb reverse tcp:8000 tcp:8000`.
2. Entrar a Home → "Start voice session" → "Type instead".
3. Se ve la conversación del frame y, debajo, las tres preguntas sugeridas que manda el servidor.
4. Tocar un chip: VORA contesta y la respuesta llega con la etiqueta "Simulated response". Los chips
   se van y no vuelven.
5. Escribir algo con "entren", "cen" o "progres" y mandarlo: cada palabra trae una respuesta
   distinta.
6. Apagar el servidor y mandar un mensaje: el mensaje **queda en el hilo** y aparece el aviso con
   **Retry**. Prenderlo y tocar Retry: se manda y **no se duplica**.
7. Cerrar la app del todo (`adb shell am force-stop com.example.parcial_tp3`) y volver a abrirla: el
   hilo sigue completo y los chips **no** vuelven.
8. Rotar con algo escrito sin mandar: no se pierde.

## Evidencia

- **258 tests unitarios, 0 fallos** (`./gradlew testDebugUnitTest`). Tests en rojo verificados antes
  de implementar: 27 fallaban por el resultado, ninguno por excepción.
- **21 tests con emulador, 0 fallos** (`./gradlew connectedDebugAndroidTest`): los de Room más
  `ConversationRotacionTest`, el primer test de Compose del proyecto.
- `./gradlew assembleDebug` en verde y `./scripts/reglas.sh` sin hallazgos.
- **Control negativo de todos los tests nuevos**: se rompió el código a propósito, uno por uno, y se
  comprobó que cada test lo detecta. Está la medición en la spec.
- Capturas en `docs/evidencia/coach/`: sugerencias en 411 y 360 dp (UI03), respuesta con la marca de
  simulada, error con reintento, reintento sin duplicar, y el hilo intacto después de cerrar la app.
- Revisores: `auditor-spec` (15 hallazgos, corregidos), `revisor-arquitectura` y `revisor-requisitos`
  en contexto limpio, con la pantalla ya hecha.

## Simulado o pendiente

- **El contenido sigue siendo simulado, la pantalla no.** El servidor tiene cuatro respuestas fijas
  elegidas por palabra clave, y llegan marcadas `isSimulated`. La conversación de ejemplo se siembra
  en Room la primera vez. Las dos cosas están declaradas en la tabla de funciones simuladas del
  README.
- **Sube la base de Room a `version = 4`.** Con `fallbackToDestructiveMigration`, la primera vez que
  se abre después del cambio **se borran las tablas, incluidas las comidas**. En instalación nueva no
  se nota; sobre una app ya instalada hay que volver a cargar una comida para rehacer la evidencia de
  RF04. Avisado a Sofi.
- **Dos endpoints nuevos en `VoraApi`.** Avisado a Hector.
- **Sube dos dependencias de test**: `espresso-core` 3.5.1 → 3.7.0 y `androidx.test.ext:junit`
  1.1.5 → 1.3.0, en `gradle/libs.versions.toml`. Con las viejas, cualquier test de Compose revienta
  en el emulador API 37 (`NoSuchMethodException: InputManager.getInstance`). Son solo de
  `androidTest` y los 21 tests con emulador siguen en verde.
- **Dos cosas marcadas y no resueltas a propósito**, porque arreglarlas obliga a reescribir los tests
  de `tests-rojo` y eso lo decide el grupo: la carga arranca en la composición en vez del `init` del
  ViewModel, y la decoración del frame empareja por posición en vez de por id. Están en la sección
  Auditoría de la spec.
- Los criterios que se dibujan en la pantalla (C5, C7, C11, C25 y el botón de C17 a C20) no tienen
  test de Compose: quedan con evidencia manual, y está dicho en la spec.

## Uso de IA

- Propuso llamar a la rama `feat/coach-historial-room`; se corrigió a `feat/voice-coach-historial`,
  que es la convención de `CONTEXTO.md`.
- Dijo que subir la versión de Room era trivial. **No lo era**: borra la tabla de comidas. Lo
  encontró `auditor-spec`.
- Propuso leer el hilo de Room sin más, lo que dejaba `Voice / 05` vacía en instalación limpia y
  rompía dos capturas de evidencia de UI01. De ahí salió la decisión de sembrar el hilo del frame.
- **Metió dos bugs que se encontraron probando en el emulador**: los chips volvían al reabrir la app
  con la conversación empezada, y la lista no bajaba a mostrar la respuesta nueva. Los dos corregidos
  y explicados en la bitácora.
- No se modificó ningún test para que pasara. Al test flojo de C6 se le sumaron dos al lado, y se
  midió cuál detecta el bug: de los 23 del ViewModel, **uno solo**.

## Ojo: este PR trae además la puesta al día de la documentación

Se había pensado mandarla en un pull request aparte a `main`, y se decidió que viaje acá. Son los
cambios de la mañana del 26/10, que describen **los ocho requisitos obligatorios que ya están en
`main`**: la tabla de estado con su pull request y su fecha, el Hito 1 tildado, el `sdk.dir` que
faltaba en el README, cómo levantar el mock, las secciones de idiomas y de qué cambiaríamos al
escalar, y la sección 17 de `PANTALLAS.md`. Tienen su propia entrada en la bitácora y su propio
commit.

**Consecuencia a tener en cuenta:** esa puesta al día llega a `main` cuando se mergee esta rama. Si
los extras no se terminan integrando, `main` se queda con la documentación vieja.

## Antes de mergear: lo que va a chocar con la rama de Martu

Las dos ramas salieron de `main` por separado y tocan siete archivos en común. Ninguno es un
problema de fondo, pero hay que resolverlos a mano y conviene hacerlo entre las dos:

| Archivo | Qué pasa |
|---|---|
| `README.md` | las dos agregan secciones: la de Firebase y la del coach |
| `docs/BITACORA.md` | las dos agregan entradas al final |
| `specs/PANTALLAS.md` | Martu cambia las de Acceso y Profile; esta, `Voice / 05` |
| `app/src/main/res/values/strings.xml` y `values-es/` | las dos agregan claves al final |
| `di/RepositoryModule.kt` | Martu agrega los de Firebase; esta, `bindCoachRepository` |
| `gradle/libs.versions.toml` | Martu suma Firebase; esta sube `espresso-core` a 3.7.0 y `androidx.test.ext:junit` a 1.3.0 |

**Un choque de números que ya quedó resuelto de este lado:** la tarea de documentación había
renumerado la ficha de perfil de `004` a `008`, sin mirar que Martu ya tenía
`008-firebase-sin-acceso-demo.md` en su rama. Acá pasó a `009-perfil-un-viewmodel-por-pantalla.md`,
así que el `008` queda libre para la de Martu y no hay que tocar nada de su lado.
