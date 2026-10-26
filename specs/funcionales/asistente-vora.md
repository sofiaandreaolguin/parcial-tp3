# Asistente VORA (Voice y Conversation)

Flujo: Asistente VORA
Requisitos: NAV01, NAV02, NAV03, **NAV05**, UI01, UI02, UI03
Pantallas: `Voice / 01 — Idle`, `02 — Listening`, `03 — Processing`, `04 — Speaking`, `05 — Conversation`, `06 — Error`
Responsable: Fiore (con Zaki en el control visual de NAV05)

> Esta spec no es de carril C: no hay Retrofit ni Room. Se escribe igual porque la tarea
> agrega **seis componentes comunes** al sistema de diseño y conviene que las reglas estén
> escritas antes de programarlos. La sección "Datos" de la plantilla es acá el `UiState`.

## Qué hace

La persona abre el Asistente VORA desde Home, Workout o Progress y conversa con el coach.
El coach recorre cinco momentos —esperando, escuchando, pensando, respondiendo y error—
y además hay una pantalla de conversación escrita con el historial de mensajes.
**Todo es simulado**: no se graba audio, no se llama a ningún servidor y **no se pide
permiso de micrófono** (NAV05).

## Alcance

**Entra:** las 2 rutas (`voice` y `conversation`), los 6 componentes comunes nuevos, los
8 iconos, los textos en los dos idiomas y las transiciones de demostración.

**No entra:** EXT01 (coach real por Retrofit con historial en Room). Si algún día se hace,
cambia de dónde salen los mensajes de `conversation`, no esta spec.

---

# Parte A · Componentes comunes nuevos

Van todos en `ui/components/`, son **sin estado** (reciben datos y avisan toques) y sacan
todo del tema. Ninguno es "el de Voice": se diseñan para que los use cualquier pantalla.
Zona de Tema y componentes: los hace Fiore **avisándole a Martu**, y quedan a cargo de ella.

**Todas las medidas de esta sección salen del Figma leído con el conector**, no estimadas.
Archivo `IvCRfdJPXEkRcm6miaJHIG`, página `>UI Screens`. Los frames son de **390 × 844**.

| Frame | Nodo | Frame | Nodo |
|---|---|---|---|
| Voice / 01 — Idle | `1:1624` | Voice / 04 — Speaking | `1:1459` |
| Voice / 02 — Listening | `1:1372` | Voice / 05 — Conversation | `1:1512` |
| Voice / 03 — Processing | `1:1419` | Voice / 06 — Error | `1:1579` |

## A0 · Lo que hay que agregarle al tema antes que nada

**Colores.** De todo lo medido, el Figma usa **dos colores que no están en `Color.kt`**:

| Color | Dónde aparece | Qué es |
|---|---|---|
| `#4A5250` | esfera del orbe apagado | el gris oscuro que hace de "verde" en la versión apagada |
| `#FCDEDE` | círculo del icono del aviso de micrófono | rosa muy claro de atención |

Todo el resto ya está en el tema: `#E4E9E6` (`VoraBorder`), `#9AA39F` (`VoraTextMuted`),
`#123D35` (`VoraGreen`), `#F7F8F5` (`VoraBackground`), `#17211F` y `#68736F`.

**Tamaños.** Faltan estos en `VoraSizes` (`orb` 140 y `orbSmall` 28 ya están y son correctos):

| Token | Valor | Dónde |
|---|---|---|
| `orbMedium` | 120 | orbe del error |
| `micCircle` | 72 | botón de `TAP TO TALK` en Idle |
| `circleButton` | 40 | `+` y enviar del composer |
| `iconCircle` | 36 | círculo del icono del aviso |
| `playCircle` | 24 | botón de escuchar de una burbuja |

**Ojo con el margen lateral.** `VoraSpacing.screen` es 20, pero el Figma no usa un solo
margen en este flujo: el encabezado va a **20**, el contenido de Idle y Error a **24**, el
hilo de Conversation a **20** y la barra de escribir a **16**. No se fuerza `screen` donde
el frame dice otra cosa.

## A1 · `VoraOrb` — variante apagada

Cambio **aditivo** sobre el componente que ya existe: se agrega un parámetro con valor por
defecto, así ninguna llamada actual se rompe.

```kotlin
enum class VoraOrbTone { Brand, Muted }

fun VoraOrb(
    modifier: Modifier = Modifier,
    size: Dp = VoraTheme.sizes.orb,
    withRing: Boolean = true,
    onDark: Boolean = false,
    tone: VoraOrbTone = VoraOrbTone.Brand,   // nuevo
    content: @Composable BoxScope.() -> Unit = {},
)
```

El orbe cambia de tamaño en cada frame, pero **las proporciones son siempre las mismas**:
aro al 90 % del diámetro y esfera al 75 %. O sea que el componente ya escala solo y alcanza
con pasarle `size`.

| Frame | Diámetro | Icono del centro |
|---|---|---|
| Voice / 02 — Listening | **170** | 32 |
| Voice / 03 — Processing | **150** | 32 |
| Voice / 01 — Idle | **140** | 32 |
| Voice / 04 — Speaking | **140** | 32 |
| Voice / 06 — Error | **120** | 34 |
| avatar de Conversation | **28** | — |

Lo único que cambia por tono son los degradados:

| Capa | `Brand` (lo que ya hace) | `Muted` (nuevo, leído del SVG del frame) |
|---|---|---|
| Halo | `orbHalo` plano | radial `#9AA39F → #123D35` **al 10 % de opacidad** |
| Aro | lima → menta → verde | `#E4E9E6 → #9AA39F (50 %) → #123D35` |
| Esfera | menta → verde | `#9AA39F → #4A5250` |

Es exactamente la misma estructura de degradados, con la paleta gris. Por eso `Muted` es un
parámetro y no un componente aparte.

**Dos correcciones a supuestos del componente actual.**

1. El KDoc de `VoraOrb` dice que el avatar chico de Conversation va sin aro
   (`withRing = false`). En el Figma **el avatar de 28 tiene las tres capas**
   (`orb-glow`, `orb-ring`, `orb-core`), igual que el grande. Se usa
   `VoraOrb(size = orbSmall)` con el aro puesto.
2. `Voice / 02 — Listening` y `Voice / 04 — Speaking` tienen **una capa más**, `orb-ripple`:
   un anillo exterior al **115 % del diámetro** que el componente hoy no dibuja. Es lo que
   da la sensación de que el orbe "late" en los dos estados activos. Hace falta un
   parámetro más, `withRipple: Boolean = false`.

**Dónde se reutiliza:** el orbe del error, y cualquier estado apagado del coach más
adelante (sin conexión, función desactivada en `Profile / 05 — Voice & AI Settings`).

## A2 · `VoraCircleButton` — botón redondo de icono

El mismo botón aparece **seis veces** en este flujo con distinto color y tamaño. Es un
componente, no seis cajas sueltas.

```kotlin
enum class VoraCircleButtonTone { Primary, Mint, Surface, OnDarkSubtle }

fun VoraCircleButton(
    icon: Painter,
    contentDescription: String?,   // de strings.xml, para TalkBack
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: VoraCircleButtonTone = VoraCircleButtonTone.Primary,
    size: Dp = VoraTheme.sizes.circleButton,
    iconSize: Dp = VoraTheme.sizes.icon,
    enabled: Boolean = true,
)
```

Medidos en los frames:

| Dónde | Círculo | Icono | Tono |
|---|---|---|---|
| micrófono de Listening | **80** | 28 | `Mint` |
| `TAP TO TALK` (Idle) | **72** | 28 | `Primary` |
| pausa (Speaking, el del medio) | **56** | 20 | `Surface` |
| repetir y stop (Speaking) | **40** | 18 | `OnDarkSubtle` |
| `+` del composer | **40** | 20 | `Surface` (fondo `#F7F8F5`) |
| enviar del composer | **40** | 18 | `Primary` |
| icono del aviso | **36** | 18 | fondo propio (`#FCDEDE`), lo pone `VoraNoticeCard` |
| escuchar, en la burbuja | **24** | 10 | `Surface` |

Los tres de Speaking van en una fila centrada de 184 de ancho, separados **24**, con el de
pausa más grande y alineado al centro vertical.

El área táctil nunca baja de `VoraTheme.sizes.touchTarget` (48) aunque el círculo dibujado
sea más chico: el de 24 y el de 40 quedan por debajo y necesitan el relleno invisible.

## A3 · `VoraChatBubble` — mensaje de una conversación

Dibuja **la fila entera**: avatar (si corresponde), burbuja y hora. Así la pantalla solo
pone la lista.

```kotlin
enum class VoraChatAuthor { Me, Vora }

fun VoraChatBubble(
    text: String,
    author: VoraChatAuthor,
    modifier: Modifier = Modifier,
    time: String? = null,
    footer: @Composable (() -> Unit)? = null,   // "Listen to response (0:14)"
)
```

Medidas del frame `Voice / 05 — Conversation`:

| | `Me` | `Vora` |
|---|---|---|
| Alineación de la fila | derecha | izquierda |
| Avatar | no lleva | `VoraOrb(size = orbSmall)`, separado **10** |
| Ancho de la burbuja | **280** | **270** |
| Fondo | `#123D35` | blanco con borde `#E4E9E6` |
| Relleno | **14** | **14** |
| Separación interna | **4** | **10** |
| Texto | Manrope Regular **14**, blanco | Manrope Regular **14**, `#17211F`, interlineado 1,4 |
| Hora | **10**, blanco al 60 %, a la derecha | **10**, debajo del texto |

**Los radios no son parejos**: 16 en tres esquinas y **4 en la que apunta al autor** —
abajo a la derecha en `Me`, abajo a la izquierda en `Vora`. Es lo que le da la "puntita".

La separación entre mensajes es **20** y la pone la lista, no la burbuja.

El `footer` es una ranura. Hoy la usa la fila de escuchar el audio, que lleva arriba un
separador de 1 y adentro: botón de 24 (radio 12, fondo `#F7F8F5`, icono de 10), separación
**8** y texto Manrope SemiBold **12** en `#68736F`.

**Dónde se reutiliza:** toda conversación futura, y EXT01 si se encara.

## A4 · `VoraComposer` — barra de escribir

```kotlin
fun VoraComposer(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    onAttach: (() -> Unit)? = null,   // null = no dibuja el +
    onMic: (() -> Unit)? = null,      // null = no dibuja el microfono
)
```

Medidas del frame: la fila va a **16** del borde, mide **44** de alto y separa sus tres
partes **12**. El `+` es un círculo de 40 con fondo `#F7F8F5` e icono de 20. El campo se
estira, tiene fondo `#F7F8F5`, **radio 22** (píldora), relleno horizontal **16**, texto
Manrope Regular **14** en `#68736F` y el micrófono de 18 al final, separado 8. El botón de
enviar es un círculo de 40 con fondo `#123D35` e icono de 18.

- El campo es **píldora y con fondo gris**, no el campo blanco con borde y radio 12 de
  `VoraTextField`. **Al implementarlo se cambió de idea:** el composer dibuja su propio
  campo y `VoraTextField` **no se toca**. Para reutilizarlo había que sumarle cuatro
  parámetros (fondo, borde, forma y relleno) a un componente que usan todas las pantallas
  de formulario, y eso sale más caro que repetir quince líneas de `BasicTextField`.
- **Con el campo vacío, enviar está deshabilitado.**
- El micrófono de la barra **no pide permiso** (NAV05): ver C22.

**Dónde se reutiliza:** cualquier pantalla que escriba y mande algo.

## A5 · `VoraNoticeCard` — tarjeta de aviso con dos acciones

El cartel `Microphone access needed` del error. Nutrition 04–06 tiene el mismo patrón con
la cámara, así que se hace genérico de entrada.

```kotlin
fun VoraNoticeCard(
    icon: Painter,
    iconBackground: Color,
    title: String,
    body: String,
    primaryText: String,
    onPrimary: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryText: String? = null,
    onSecondary: (() -> Unit)? = null,
)
```

Medidas del frame: tarjeta blanca con borde `#E4E9E6`, **radio 20**, relleno **20** y
separación interna **16**. El icono va en un círculo de **36** con fondo `#FCDEDE`, icono
de 18, separado **10** del título. Título Manrope SemiBold **15** en `#17211F`. Cuerpo
Manrope Regular **13** interlineado 1,4 en `#68736F`. Los dos botones ocupan **la mitad
cada uno**, separados **12**, con relleno 16/10 y radio de píldora: el primero con fondo
`#123D35` y texto Bold 13 blanco, el segundo solo con borde `#E4E9E6` y texto SemiBold 13
en `#68736F`.

El relleno vertical de esos botones es **10**, y `VoraButtonSize.Small` usa 8. Son 2 de
diferencia: o se le agrega un tamaño a `VoraButton`, o el componente los dibuja. Lo decide
Martu, que es la dueña de `VoraButton`.

## A6 · `VoraSectionLabel` — etiqueta de sección

La etiqueta chica aparece cuatro veces en este flujo solo (`Suggested Questions`,
`LIVE TRANSCRIPT`, `LAST CONVERSATION`, `TODAY AT 12:40 PM`) y en casi todas las demás
pantallas del Figma.

```kotlin
fun VoraSectionLabel(text: String, modifier: Modifier = Modifier, color: Color = ...)
```

Envuelve un `Text` con el estilo de etiqueta del tema (alto de línea 15 en los frames). Es
chico a propósito: el valor es que si mañana cambia el estilo, cambia en un solo lugar.

**Ojo con las mayúsculas.** En el Figma el texto está escrito en capitalización normal
(`Suggested Questions`, `Live Transcript`, `Thinking...`) y el frame lo muestra en mayúsculas
porque se las aplica un estilo de texto. En Compose eso es el estilo, no el string: en
`strings.xml` va el texto como está escrito y las mayúsculas las pone `VoraSectionLabel`.
Así el texto en español tampoco queda gritado a mano.

# Parte B · Iconos

Los trae Fiore. Van en `res/drawable/` como **vector XML**, 24 × 24, trazo 2, sin color
escrito adentro (el color lo pone quien lo usa). Nada de PNG.

| Archivo | Dónde se usa |
|---|---|
| `ic_mic.xml` | Idle, Listening, composer, tarjeta del error |
| `ic_waveform.xml` | centro del orbe en Idle, Listening, Processing y Speaking |
| `ic_play.xml` | "Listen to response" de Conversation |
| `ic_pause.xml` | control del medio en Speaking |
| `ic_repeat.xml` | control izquierdo en Speaking |
| `ic_stop.xml` | control derecho en Speaking |
| `ic_send.xml` | botón de enviar del composer |
| `ic_refresh.xml` | centro del orbe apagado en el error |

El `+` ya existe (`ic_plus.xml`).

---

# Parte C · Estado y lógica

## `VoiceUiState` (ruta `voice`, `VoiceViewModel`)

```kotlin
enum class VoiceStatus { Idle, Listening, Processing, Speaking, Error }

data class VoiceUiState(
    val status: VoiceStatus = VoiceStatus.Idle,
    val transcript: String = "",          // lo que "dijo" la persona
    val reply: String = "",               // la respuesta del coach
    val elapsedSeconds: Int = 0,
    val totalSeconds: Int = 0,
    val suggestions: List<Int> = emptyList(),   // @StringRes de los 4 chips
    val lastQuestion: Int? = null,              // @StringRes de LAST CONVERSATION
)
```

Los textos de demostración se guardan como **ids de `strings.xml`**, no como texto suelto:
así no hay ni un literal en el código y el control `scripts/reglas.sh` queda contento.

## `ConversationUiState` (ruta `conversation`, `ConversationViewModel`)

```kotlin
data class ChatMessage(
    val id: String,
    val author: VoraChatAuthor,
    val text: String,
    val time: String,
    val audioSeconds: Int? = null,   // solo los de VORA que se pueden escuchar
)

data class ConversationUiState(val messages: List<ChatMessage> = emptyList())
```

Lo que se está tipeando **no** va acá: va con `rememberSaveable` en el `Route`, como pide
`CONTEXTO.md` sección 3.

## Transiciones

| Desde | Qué pasa | A |
|---|---|---|
| Idle | `TAP TO TALK` | Listening |
| Idle | un chip de `SUGGESTED QUESTIONS` | Listening, con esa pregunta ya en el transcript |
| Idle | `Type instead` o la tarjeta `LAST CONVERSATION` | ruta `conversation` |
| Listening | botón de micrófono | Processing |
| Listening | `Cancel` | Idle |
| Processing | **solo, a los 2 segundos** | Speaking (vuelta impar) o Error (vuelta par) |
| Processing | `Cancel` | Idle |
| Speaking | `Tap to Interrupt` | Idle |
| Speaking | repetir / pausar / stop | no cambia de estado |
| Error | `Try again`, `Allow Access` o `Not Now` | Idle |
| Error | `Type instead` | ruta `conversation` |
| cualquiera | flecha ‹ del encabezado | `popBackStack()` |

**Cómo se llega al Error.** Hoy `PANTALLAS.md` le da cuatro salidas y **ninguna entrada**,
así que en la demo el estado era inalcanzable. Se resuelve alternando en Processing: la
primera pasada termina en Speaking y la segunda en Error. Es determinista (no al azar: una
demo no puede depender de la suerte, y así se puede testear), no inventa ningún control que
no esté en el Figma, y deja los cinco estados a mano en un recorrido de treinta segundos.
**Hay que agregar esta fila a la tabla NAV03 de `PANTALLAS.md`.**

---

# Parte D · Las pantallas

Archivos, siguiendo `CONTEXTO.md` sección 3:

```
ui/screens/voice/
  VoiceRoute.kt  VoiceScreen.kt  VoiceViewModel.kt  VoiceUiState.kt
  ConversationRoute.kt  ConversationScreen.kt  ConversationViewModel.kt  ConversationUiState.kt
```

En `VoiceNavGraph.kt` se cambian los dos `PantallaVacia(...)` por `VoiceRoute(...)` y
`ConversationRoute(...)`. Las dos se abren encima: **no llevan barra inferior**.

**Hay de dónde copiar el patrón.** `Profile / 01 — Dashboard` ya está en `main`
(`ui/screens/profile/`, tarea VC-q de Zaki) con los cuatro archivos y además un
`ProfileLabels.kt` para los textos. Antes de empezar conviene traer `main` a la rama y
mirar cómo quedó, para que las dos pantallas de Voice sigan la misma forma.

`VoiceScreen` es **una sola pantalla** que elige qué dibujar según `status`. Comparten
encabezado y orbe; cambian el centro, el pie y el fondo.

| Estado | Fondo | Encabezado | `onDark` |
|---|---|---|---|
| Idle | claro | `‹ VORA Coach` + etiqueta `ONLINE` | `false` |
| Listening | verde | `‹ Listening` + `● LIVE AUDIO` | **`true`** |
| Processing | verde | `‹ Processing` | **`true`** |
| Speaking | verde | `‹ VORA Speaking` + etiqueta `SPEAKING` | **`true`** |
| Error | claro | `‹ System Alert` | `false` (y orbe `Muted`) |
| Conversation | claro | `‹ VORA Coach` + punto verde | `false` |

En los tres verdes, `VoraTopBar` va con `contentColor = colorScheme.onPrimary`.

---

## Medidas de cada frame

Leídas del Figma con el conector. En los seis, la barra de estado mide 44 y el encabezado
56, así que **el contenido siempre arranca a y = 100**. Los botones anchos del pie
(`Try again`, `Tap to Interrupt`) son todos de **342 × 48** a 24 del borde.

| Frame | Orbe | Lo propio de ese estado |
|---|---|---|
| 01 Idle | 140, a 20 del inicio del contenido | Título 36 y bajada 38 a 24 del borde · etiqueta de sección centrada · 4 chips de 34 de alto, relleno 14/8, separados 8, filas centradas · tarjeta `LAST CONVERSATION` 342 × 67 con relleno 14 · círculo de micrófono 72 con el rótulo 80 más abajo · `Type instead` al pie |
| 02 Listening | 170, a 40 | Panel `LIVE TRANSCRIPT` 342 × 122, relleno 20, con el texto a 48 · "Speak naturally…" centrado · círculo de micrófono 80 · `Cancel` |
| 03 Processing | 150, a 50 | `Thinking...` centrado · burbuja de lo capturado 342 × 82 con relleno 18 · fila de carga de 215 centrada: anillo de 16, separación 8 y texto · `Cancel` |
| 04 Speaking | 140 + onda, a 30 | Panel de la respuesta 342 × 112, relleno 20 · barra de audio 342 × **4** (llena hasta 180) con los tiempos 20 más abajo · tres controles en una fila de 184 centrada · `Tap to Interrupt` |
| 05 Conversation | 28 en el encabezado y en cada mensaje | Hilo a 20 del borde, mensajes separados 20 · barra de escribir a 16 (ver A3 y A4) |
| 06 Error | 120 apagado, a 30 | Título 30 y bajada 38 · tarjeta de aviso 342 × 200 (ver A5) · `Try again` · `Type instead` |

**El margen lateral no es uno solo:** encabezado a 20, contenido de Idle, Processing,
Speaking y Error a 24, hilo de Conversation a 20 y barra de escribir a 16.

## Orden de trabajo

Seis pasos, cada uno un pull request. El orden no es caprichoso: cada paso destraba al
siguiente, y los dos primeros son los que bloquean todo lo demás.

### Paso 0 · Ponerse al día

Rama nueva sacada de `origin/main`, no de la de documentación. En `main` ya está
`Profile / 01 — Dashboard` (tarea VC-q), que es el patrón de `Route`/`Screen`/`UiState`/
`ViewModel` a seguir, más un `ProfileLabels.kt` para los textos.

El cambio de la pestaña Progress (ficha 005) **no es parte de esta tarea**: es navegación,
toca a Zaki y conviene que vaya en su propio pull request, chico y rápido.

### Paso 1 · Los 8 iconos

Bloquea a los seis componentes, así que va primero. Se siguen **las reglas que ya usó Martu**
en `res/drawable/` (tarea VC-d), que son:

- Un **vector XML** por icono, exportado del SVG del Figma. Nada de PNG.
- **Tamaño real del frame**, no 24 × 24: `ic_back` es 7 × 10, `ic_tab_home` 19 × 20,
  `ic_sparkle` 18 × 18. `android:width`/`height` y `viewportWidth`/`Height` coinciden.
- El dibujo va dentro de un `<group android:translateX="1" android:translateY="1">`, para
  que el trazo no se corte contra el borde del lienzo.
- **Trazo de 2**, `android:strokeLineCap="round"` y `android:fillColor="#00000000"` en los
  iconos de línea.
- **Color negro** (`#FF000000`) en el archivo: el color real lo pone quien lo usa, con el
  `tint` del `Icon`. Por eso un mismo icono sirve en fondo claro y en fondo verde.
- Un **comentario arriba** diciendo de qué SVG del Figma salió.
- Un archivo por icono, aunque se use en varios tamaños: el vector escala solo.

Los ocho: `ic_mic`, `ic_waveform`, `ic_play`, `ic_pause`, `ic_repeat`, `ic_stop`, `ic_send`
y `ic_refresh`. El `+` ya existe (`ic_plus`).

> Esto contesta la **duda #5 del tablero** (Sofi, 21/10: si los iconos salen del Figma o de
> una librería). Conviene responderla ahí mismo con esta convención, así Nutrition no
> resuelve lo mismo de otra manera.

### Paso 2 · Tema: los dos colores y los tamaños (parte A0)

Pull request chico y aparte, porque lo usan los seis componentes: `#4A5250` y `#FCDEDE` en
`Color.kt` y `Theme.kt`, y los tamaños que faltan en `Dimens.kt`.

### Paso 3 · Los seis componentes (partes A1 a A6)

En este orden, que es el de dependencia:

1. `VoraSectionLabel` (A6), que no depende de nada.
2. `VoraCircleButton` (A2).
3. `VoraOrb` con `tone` y `withRipple` (A1).
4. `VoraNoticeCard` (A5).
5. `VoraChatBubble` (A3), que usa el orbe de avatar.
6. `VoraComposer` (A4), que usa el botón redondo y `VoraTextField`.

Cada uno con su `@Preview` y su entrada en `CatalogoComponentes.kt`. **Avisarle a Martu
antes de abrir el pull request:** es su zona, y dos de los cambios tocan componentes que
ya usa `Profile / 01`.

### Paso 4 · Los 5 estados (ruta `voice`)

Los tests del `VoiceViewModel` **primero**: las transiciones son Kotlin puro, corren sin
emulador y cubren C2 a C10, C19 y C20. Después `VoiceUiState`, `VoiceViewModel`,
`VoiceScreen` y `VoiceRoute`, los textos en los dos idiomas, y cambiar el `PantallaVacia`
de `VoiceNavGraph.kt`.

Cierra con capturas en 393 dp y en otro tamaño, **en modo claro y en oscuro**, y el
subagente `revisor-visual` contra los seis frames.

### Paso 5 · Conversation (ruta `conversation`)

Lo mismo con `ConversationUiState`, `ConversationViewModel`, `ConversationScreen` y
`ConversationRoute`, usando `VoraChatBubble` y `VoraComposer`.

### Paso 6 · Cerrar la tarea

Agregarle a `scripts/reglas.sh` el control de C18 (que ningún `.kt` llame a
`RequestPermission` ni a `checkSelfPermission`), actualizar el estado de las seis filas en
`specs/PANTALLAS.md` y la fila NAV05 de `specs/REQUISITOS.md`, y la bitácora. Skill
`cerrar-tarea`.

## Lo que puede frenar

1. **`VoraOrb` y `VoraTextField` son de Martu** y los toca el paso 3. Si ella está
   trabajando en los mismos archivos, hay conflicto. Por eso el aviso antes, no después.
2. **El `orb-ripple` cambia un componente que `Profile / 01` ya usa.** Los parámetros son
   aditivos y con valor por omisión, así que Profile no debería cambiar, pero hay que
   mirarlo en la captura antes de dar el paso por terminado.
3. **La tarjeta vence el 23/10** y la tarea `s` (control visual de las 37, de Zaki) depende
   de ésta. Si algo se cae, el paso 5 (Conversation) es lo primero que se puede mover.

# Criterios de aceptación (EARS)

### Navegación y estados

- C1. La app DEBE tener **una sola ruta `voice`** para Idle, Listening, Processing, Speaking y Error, y una ruta `conversation` aparte.
- C2. CUANDO la persona toca `TAP TO TALK` en Idle, la app DEBE pasar al estado Listening.
- C3. CUANDO la persona toca uno de los cuatro chips de `SUGGESTED QUESTIONS`, la app DEBE pasar a Listening **con el texto de ese chip en el transcript**.
- C4. CUANDO la persona toca el botón de micrófono en Listening, la app DEBE pasar a Processing.
- C5. CUANDO la app está en Processing, la app DEBE pasar sola a otro estado a los 2 segundos, sin que la persona toque nada.
- C6. MIENTRAS es la primera pasada por Processing desde que se abrió la pantalla, la app DEBE terminar en Speaking.
- C7. MIENTRAS es la segunda pasada por Processing, la app DEBE terminar en Error.
- C8. CUANDO la persona toca `Cancel` en Listening o en Processing, la app DEBE volver a Idle.
- C9. CUANDO la persona toca `Tap to Interrupt` en Speaking, la app DEBE volver a Idle.
- C10. CUANDO la persona toca `Try again` en Error, la app DEBE volver a Idle.
- C11. CUANDO la persona toca `Type instead` en Idle o en Error, la app DEBE navegar a `conversation`.
- C12. CUANDO la persona toca la tarjeta `LAST CONVERSATION` en Idle, la app DEBE navegar a `conversation`.
- C13. CUANDO la persona toca la flecha ‹ en cualquiera de las dos pantallas, la app DEBE hacer `popBackStack()` y volver a la pantalla desde la que se abrió.
- C14. CUANDO la persona usa el botón atrás del sistema estando en Speaking, la app DEBE cerrar el asistente entero, NO volver a Processing.
- C15. CUANDO se rota la pantalla, la app DEBE conservar el estado, el transcript y la respuesta.

### NAV05 · nada de micrófono

- C16. La app DEBE llegar a los cinco estados de `voice` sin instalar la app de nuevo y sin tocar código.
- C17. La app NO DEBE declarar `RECORD_AUDIO` ni ningún permiso de micrófono en el manifest.
- C18. La app NO DEBE llamar a `ActivityResultContracts.RequestPermission` ni a `checkSelfPermission` en ningún archivo del flujo.
- C19. CUANDO la persona toca `Allow Access` en la tarjeta del error, la app DEBE volver a Idle **sin abrir ningún diálogo del sistema**.
- C20. CUANDO la persona toca `Not Now`, la app DEBE volver a Idle.
- C21. La app DEBE dibujar igual la tarjeta `Microphone access needed`, porque UI01 pide fidelidad al frame.
- C22. CUANDO la persona toca el micrófono de la barra de escribir en Conversation, la app DEBE no hacer nada visible y **no pedir permiso**.

### Conversation

- C23. La app DEBE mostrar los mensajes en orden, los de la persona a la derecha y los de VORA a la izquierda con su avatar.
- C24. CUANDO la persona escribe un texto y toca enviar, la app DEBE agregarlo al final de la lista como mensaje propio y vaciar el campo.
- C25. MIENTRAS el campo está vacío, el botón de enviar DEBE estar deshabilitado.
- C26. CUANDO se agrega un mensaje, la lista DEBE desplazarse hasta el último.
- C27. ~~La app NO DEBE responder sola a un mensaje enviado: la conversación es de demostración y queda como está.~~
  **Reemplazado el 26/10 por el C6 de [`coach-con-historial.md`](coach-con-historial.md) (EXT01):** VORA ahora
  contesta consultando al servidor del docente y el hilo se guarda en Room. Era un criterio de la etapa en que
  `conversation` era maqueta; se deja tachado y no borrado para que se vea de dónde viene el cambio.
- C28. CUANDO se rota la pantalla, la app DEBE conservar los mensajes y lo que estaba escrito sin mandar.

### Componentes y tema

- C29. La app DEBE dibujar el orbe con `onDark = true` en Listening, Processing y Speaking, y con el valor por omisión en Idle, Conversation y Error.
- C30. La app DEBE dibujar el orbe del error con `tone = Muted`.
- C31. Ningún archivo de `ui/screens/voice/` DEBE escribir un color, un tamaño, un radio ni una tipografía a mano: todo sale de `VoraTheme` o de `MaterialTheme`.
- C32. Ningún archivo de `ui/screens/voice/` DEBE tener un texto escrito en el código: todos salen de `stringResource`.
- C33. `res/values/strings.xml` y `res/values-es/strings.xml` DEBEN tener **las mismas claves**.
- C34. Los seis componentes nuevos DEBEN tener su `@Preview` y entrar en `CatalogoComponentes.kt`.
- C35. Los parámetros nuevos de `VoraOrb` y de `VoraTextField` DEBEN tener valor por omisión, de modo que ninguna llamada existente cambie.

### Fidelidad visual

- C36. Las seis pantallas DEBEN verse como su frame a 393 dp de ancho, sin textos cortados ni superpuestos (UI01).
- C37. Las seis pantallas DEBEN verse bien en un segundo emulador de otro tamaño, con desplazamiento donde no entren (UI03).
- C38. CUANDO el sistema está en modo oscuro, las seis pantallas DEBEN verse **igual que en
  modo claro**. El tema es solo claro a propósito (`ui/theme/Theme.kt`: el Figma no tiene
  modo oscuro y UI01 pide los mismos colores en cualquier celular), así que acá no se
  agrega un modo oscuro: se comprueba que la app no se dé vuelta sola.

## Fuera de alcance

- Grabar, reproducir o reconocer audio de verdad.
- Que VORA conteste de verdad (eso es EXT01).
- La barra de audio de Speaking avanzando sola: se dibuja en su posición y no corre.
- Animaciones del orbe. El Figma no las dibuja y `CONTEXTO.md` dice que no se inventan estados.

## Tests

La lógica de estados es Kotlin puro, así que va a `test/` y corre sin emulador. Lo visual se
controla con el subagente `revisor-visual` contra el frame, no con tests.

| Criterio | Test | Dónde | Vi el rojo |
|---|---|---|---|
| C2, C3 | `alToqueDeHablarPasaAListening` | `test/` | |
| C4 | `alSoltarElMicrofonoPasaAProcessing` | `test/` | |
| C5, C6 | `laPrimeraVezProcessingTerminaEnSpeaking` | `test/` | |
| C7 | `laSegundaVezProcessingTerminaEnError` | `test/` | |
| C8, C9, C10 | `cancelarYReintentarVuelvenAIdle` | `test/` | |
| C19, C20 | `allowAccessYNotNowVuelvenAIdleSinPedirPermiso` | `test/` | |
| C24, C25 | `enviarAgregaElMensajeYVaciaElCampo` | `test/` | |
| ~~C27~~ | ~~`enviarNoGeneraRespuestaAutomatica`~~ — **borrado el 26/10**: afirmaba lo contrario del C6 de EXT01 | — | — |
| C17 | `scripts/reglas.sh`, que ya busca `RECORD_AUDIO` en el manifiesto | control | |
| C18 | **falta**: agregarle a `scripts/reglas.sh` la busqueda de `RequestPermission` y `checkSelfPermission` en los `.kt` | control | |
| C31, C32 | `scripts/reglas.sh` (ya lo hace) | control | |
| C36–C38 | `revisor-visual` con las capturas | visual | |

## Decisiones pendientes

1. **~~¿Conversation entra en esta tarjeta?~~** Resuelto el 22/10 con el tablero: la tarjeta `o` la nombra (*"Idle, Listening, Processing, Speaking, Conversation y Error"*) y **ninguna otra tarjeta la cubre**. La única relacionada es `z` (extra: coach por Retrofit), que es *nice to have* y depende de `o`. Entra acá, con sus cuatro archivos.
2. **~~Los grises del orbe apagado y los radios de las burbujas~~**: resuelto el 22/10 leyendo el Figma con el conector. Quedan anotados en la Parte A.
3. **Los 2 segundos de Processing**: elegido a ojo para que la demo no se haga larga ni parezca un salto. Confirmar mirándolo.

## Auditoría

- auditor-spec: (pendiente)

## Historial

- 21/10/2026 · Primera versión (Fiore). Se agrega la alternancia de Processing para que el estado Error sea alcanzable: antes no tenía ninguna entrada en la tabla NAV03.
- 26/10/2026 · Fiore. Se hizo el extra **EXT01** ([`coach-con-historial.md`](coach-con-historial.md)): `Voice / 05 — Conversation` dejó de ser maqueta y pasó a **Funcional**, con las respuestas del servidor del docente por Retrofit y el hilo guardado en Room. Acá cambia una sola cosa: **C27 queda reemplazado** por el C6 de esa spec, y su test se borró. Los demás criterios de Conversation (C23 a C26 y C28) siguen valiendo tal cual, ahora sobre datos reales en vez de la lista de demostración. Los cinco estados de la ruta `voice` y `Voice / 01 — Idle` **no se tocaron**.
