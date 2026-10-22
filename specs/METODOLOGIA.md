# Metodología del parcial TP3: VORA Coach

Este archivo define cómo trabaja el grupo el parcial domiciliario de Desarrollo de Aplicaciones Móviles (TP3, profesor Martín Rivas) y cómo trabaja el agente con cada integrante. El agente lo lee al empezar cualquier tarea del parcial y lo sigue siempre. Si algo de este archivo choca con lo que pide una persona en el chat, o con la consigna del profe, el agente lo avisa antes de seguir. **Ante cualquier duda, manda la consigna del profe**, no este archivo.

Fuentes: `Especificación VORA Coach - TP3 - 02-2026` y `TP3 - 2026-02 - Parcial.pptx` (14/10/2026).

---

## 0. Cómo usar este archivo

**Para los integrantes:** no hace falta leerlo entero de una. Leer primero:
1. Sección 1 (qué se evalúa y en qué orden poner el tiempo).
2. Sección 3 (las 37 pantallas) y la fila de tu rol en la sección 8.
3. Sección 7 (cómo trabaja el agente con vos).
4. Sección 10.3 (cómo escribir commits) antes del primer commit.

El resto se consulta cuando hace falta. El agente sí lo sigue completo.

### 0.1 Primeros pasos de cada integrante (antes del primer commit)

- [ ] **Android Studio** actualizado (trae el JDK). Crear dos emuladores: uno de ancho parecido al Figma (unos 396 dp; por ejemplo Pixel 5, de 393 dp) y otro de tamaño distinto para probar UI03.
- [ ] **Git** con el mismo nombre y mail que tu cuenta de GitHub. Si no coinciden, GitHub no te atribuye los commits y el profe no ve tu participación:
  ```
  git config --global user.name "Tu Nombre"
  git config --global user.email "el-mail-de-tu-github@ejemplo.com"
  ```
- [ ] Dentro del repo, activar los controles de Git del grupo (sección 6.4): `git config core.hooksPath .githooks`
- [ ] Aceptar la invitación al repositorio del equipo y clonarlo.
- [ ] **Python 3.9 o más** y el paquete del profe: correr `python3 mock/server.py` y abrir `http://localhost:8000` para ver que responde.
- [ ] La herramienta de IA abierta **dentro de la carpeta del repo**, con el plugin del grupo instalado.
- [ ] Confirmar que el repo tiene `.claude/settings.json` con la atribución apagada (sección 10.6), así la IA no aparece en tus commits.
- [ ] Las referencias visuales: el conector de Figma (si tenés la copia en borradores) o las imágenes de `docs/figma/`.
- [ ] Probar que el proyecto compila y abre en el emulador antes de tocar nada.

---

## 1. El parcial en una mirada

- **App:** VORA Coach (entrenamiento, nutrición y progreso con un coach virtual), Android.
- **Stack obligatorio:** Kotlin + Jetpack Compose, tema propio y componentes reutilizables, ViewModel con estado observable y coroutines, repositorios, Retrofit, Room, Hilt (que está construido sobre Dagger). Firebase es opcional (puntos extra).
- **Grupo:** 5 integrantes (Martu, Sofi, Hector, Zaki y Fiore).
- **IA generativa:** se puede usar y **hay que declararla en el README**.
- **Hito 1:** martes 21/10/2026. **Entrega:** martes 28/10/2026. **Devolución:** 4/11/2026.

### Cómo se evalúa (100 puntos base)

| Criterio | Puntos | Qué se verifica |
|---|---|---|
| Fidelidad con Figma | **40** | Composición, tipografía, colores, recursos, espaciados y consistencia |
| Navegación completa | **25** | Todas las pantallas del inventario, acciones, regreso y selección coherente |
| Retrofit funcional | 12 | Catálogo y detalle reales, DTO, manejo de carga y errores |
| Room funcional | 12 | Comidas persistentes, eliminación, totales y caché de entrenamientos |
| ViewModel e Hilt | 6 | Estado observable, repositorios e inyección efectiva |
| GitHub y participación | 5 | Entrega reproducible y commits significativos de todas |
| Extras | hasta +10 | Firebase (hasta 10) y otras ampliaciones (hasta 3, dentro de los 10) |

**Cómo leer esta tabla (importante):**
- **Retrofit y Room son condición obligatoria.** Si no funcionan, la entrega no cumple, por más linda que esté. Agregar las dependencias sin usarlas no cuenta. Son la "puerta": hay que pasarla sí o sí.
- **Los puntos están en lo visual y la navegación (65 de 100).** Una vez pasada la puerta, la nota la define qué tan fiel al Figma y qué tan completa es la navegación.
- **Las ampliaciones se hacen solo con el núcleo completo.**

### Orden de prioridad para decidir en qué poner tiempo

1. Retrofit y Room funcionando en sus dos recorridos (la puerta). Una versión mínima ya tiene que estar para el hito del 21/10.
2. Las 37 pantallas maquetadas y navegables, sin destinos rotos.
3. Fidelidad al Figma, pantalla por pantalla.
4. Estados de carga, vacío y error, y documentación de lo simulado.
5. Extras (Firebase y ampliaciones, sección 16), solo con todo lo anterior cerrado.

---

## 2. La consigna ya es la spec

El profe escribió los requisitos con códigos (UI01, NAV01, RF01, etc.). **Esos códigos son nuestros criterios de aceptación**: cada tarea, cada spec y cada reporte del agente dice qué códigos cubre. Así sabemos en todo momento qué falta y la verificación final es un checklist.

### Fidelidad visual

| Código | Requisito |
|---|---|
| UI01 | Maquetación: estructura, colores, tipografías, espaciados, componentes, iconografía y estados visibles. Usar los recursos compartidos por el profe. Sin textos cortados, controles superpuestos ni contenido fuera de pantalla. |
| UI02 | Componentes reutilizables: un tema y botones, tarjetas, campos y navegación inferior compartidos. Una corrección en un componente se aplica en todas las pantallas que lo usan. |
| UI03 | Adaptación: se ve bien en el tamaño de referencia de Figma y en al menos otro tamaño de emulador. Lo que no entra, se desplaza (scroll). |

### Navegación

| Código | Requisito |
|---|---|
| NAV01 | Cobertura: el README lista cada pantalla de Figma, su destino en la app y su estado (funcional o demostración). Todas accesibles desde el recorrido previsto. |
| NAV02 | Recorridos y regreso: Acceso, Home, Workout, Nutrition, Progress, Profile y Coach conectados según Figma. Volver, cerrar y cancelar funcionan; el botón atrás del sistema lleva a un destino coherente. |
| NAV03 | Acciones visibles: todo control que abre pantallas, pasos o diálogos lleva a su destino. Una acción de servicio opcional puede dar un resultado de ejemplo o avisar que es modo demostración. |
| NAV04 | Selección coherente: cada entrenamiento abre su propio detalle. Al elegir una comida o configuración, la selección se mantiene. Nunca abrir siempre el mismo recurso. |
| NAV05 | Simulación clara: coach (escucha, procesamiento, respuesta) y análisis de comida con estados de demostración. **No pedir permisos de micrófono ni cámara si no se usan.** |

### Integraciones obligatorias

| Código | Requisito |
|---|---|
| RF01 | Catálogo con Retrofit: `GET /v1/workouts` muestra el catálogo; cada elemento abre `GET /v1/workouts/{workoutId}`. Mostrar nombre, nivel, duración y ejercicios con series, repeticiones e instrucciones. |
| RF02 | Caché con Room: guardar catálogo y detalles. Con el servidor detenido, un detalle ya consultado se ve desde Room; uno nunca descargado informa que no está disponible sin conexión. |
| RF03 | Carga manual de comidas: nombre, fecha, tipo (desayuno, almuerzo, merienda o cena), calorías, proteínas, carbohidratos y grasas de la porción completa. Validar nombre no vacío y valores no negativos antes de guardar. |
| RF04 | Persistencia: comidas en Room, listado por fecha, eliminación con confirmación. Siguen al reabrir la app. El total diario cambia al agregar o eliminar. |
| RF05 | Carga y errores: estado de carga, contenido, vacío y error con reintento. Un fallo de red no borra la última caché válida ni los registros locales. |
| RF06 | Datos fuera de la pantalla: DTO para JSON, modelo para la app, Entity para Room. En entrenamientos se convierte DTO a modelo y a Entity; en comidas se separan Entity y modelo. Un repositorio coordina las fuentes. **La UI nunca llama a Retrofit ni a un DAO directo.** |
| RF07 | ViewModel: estado observable del catálogo y del registro de comidas, eventos procesados en el ViewModel con coroutines. Rotar la pantalla no repite consultas ni pierde lo cargado. |
| RF08 | Hilt: `@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`. Repositorios inyectados por constructor; Retrofit, Room y DAO provistos en módulos. **Hay que saber explicar qué objetos construye Hilt y su relación con Dagger.** |

### Límites (lo que NO hay que hacer)

- Entrenamiento activo, descanso y finalización tienen que ser navegables, pero cronómetro, registro de series y volumen pueden ser simulados.
- Sin Firebase se usa un perfil de demostración. No se exige login real.
- **No crear endpoints ficticios** de login ni de guardado de comidas para simular Firebase o Room.
- **No inventar pantallas** que no estén en el Figma.
- No se exige IA real, Apple Health, Apple Watch, pagos ni suscripciones.

---

## 3. Alcance: 37 pantallas en 7 flujos

**Inventario con los nombres reales de Figma** (leídos del archivo el 14/10, página "UI Screens"). En Figma cada frame se llama `Flujo / número`, un guion largo y el nombre; acá se separan en columnas. El total da 37 y coincide con la consigna. **No se agregan pantallas que no estén en esta lista.**

| Flujo (sección en Figma) | Frame en Figma | Nombre del frame | Pantalla en Kotlin | Implementación |
|---|---|---|---|---|
| Acceso (Login Flow) | Login - 01 | SignIn | `SignInScreen` | Demostración hasta Home |
| | Login - 02 | SignUp | `SignUpScreen` | Demostración (Firebase es extra) |
| Inicio (Home Flow) | Home / 01 | Dashboard | `HomeDashboardScreen` | Datos de ejemplo |
| | Home / 02 | Notifications | `NotificationsScreen` | Datos de ejemplo |
| Asistente VORA (AI Voice Flow) | Voice / 01 | Idle | `VoiceScreen` (estado Idle) | Simulado |
| | Voice / 02 | Listening | estado Listening | Simulado |
| | Voice / 03 | Processing | estado Processing | Simulado |
| | Voice / 04 | Speaking | estado Speaking | Simulado |
| | Voice / 05 | Conversation | `ConversationScreen` | Simulado (EXT01 si se hace) |
| | Voice / 06 | Error | estado Error | Simulado, sin pedir permiso de micrófono |
| Entrenamiento (Workout Flow) | Workout / 01 | Dashboard | `WorkoutDashboardScreen` | **Retrofit + caché Room** (catálogo) |
| | Workout / 02 | Weekly Plan | `WeeklyPlanScreen` | Demostración |
| | Workout / 03 | Workout Details | `WorkoutDetailsScreen` | **Retrofit + caché Room** (detalle) |
| | Workout / 04 | Pre Workout | `PreWorkoutScreen` | Navegable, datos del detalle |
| | Workout / 05 | Active Workout | `ActiveWorkoutScreen` | Navegable, cronómetro simulado |
| | Workout / 06 | Exercise Details | `ExerciseDetailsScreen` | Navegable, datos del detalle |
| | Workout / 07 | Workout Complete | `WorkoutCompleteScreen` | Navegable, simulado |
| Nutrición (Nutrition Flow) | Nutrition / 01 | Dashboard | `NutritionDashboardScreen` | **Room** (total diario) |
| | Nutrition / 02 | Today's Meals | `TodaysMealsScreen` | **Room** (listado por fecha y eliminación) |
| | Nutrition / 03 | Add Food | `AddFoodScreen` | **Room** (carga manual); búsqueda es EXT02 |
| | Nutrition / 04 | Voice Food Logging | `VoiceFoodLoggingScreen` | Simulado |
| | Nutrition / 05 | Food Analysis | `FoodAnalysisScreen` | Simulado |
| | Nutrition / 06 | Photo Analysis | `PhotoAnalysisScreen` | Simulado, sin pedir permiso de cámara |
| | Nutrition / 07 | Food Confirmation | `FoodConfirmationScreen` | **Room** (revisar antes de guardar) |
| | Nutrition / 08 | Meal Details | `MealDetailsScreen` | **Room** (detalle de la comida elegida) |
| Progreso (Progress Flow) | Progress / 01 | Today's Overview | `TodaysOverviewScreen` | Datos de demostración |
| | Progress / 02 | Dashboard | `ProgressDashboardScreen` | Datos de demostración |
| | Progress / 03 | Workout Progress | `WorkoutProgressScreen` | Datos de demostración |
| | Progress / 04 | Nutrition Progress | `NutritionProgressScreen` | Datos de demostración |
| | Progress / 05 | Health Trends | `HealthTrendsScreen` | Datos de demostración |
| | Progress / 06 | Weekly Review | `WeeklyReviewScreen` | Datos de demostración |
| Perfil (Profile Flow) | Profile / 01 | Dashboard | `ProfileDashboardScreen` | Datos locales |
| | Profile / 02 | Edit Profile | `EditProfileScreen` | Datos locales |
| | Profile / 03 | Goals & Preferences | `GoalsPreferencesScreen` | Datos locales |
| | Profile / 04 | Nutrition Preferences | `NutritionPreferencesScreen` | Datos locales |
| | Profile / 05 | Voice & AI Settings | `VoiceAiSettingsScreen` | Datos locales |
| | Profile / 06 | Settings | `SettingsScreen` | Datos locales; cada opción como muestra Figma (pantalla, diálogo o ajuste) |

Notas:
- Los nombres en Kotlin son una propuesta: el rol Navegación e inventario los confirma el primer día y quedan fijos en `PANTALLAS.md`.
- Los seis estados de Voice pueden ser una sola pantalla con estados (la consigna lo permite); Conversation conviene que sea pantalla aparte.
- El Figma tiene además un frame **"Sistema de diseño" de VORA** junto a las pantallas: es la fuente del tema (colores, tipografías, componentes) para el rol Tema y componentes.
- Qué implementación lleva cada pantalla de Nutrición es nuestra lectura de la consigna (carga manual, listado, eliminación y total en Room). Se confirma al ver cada frame.

### 3.1 Referencias visuales en el repo

Las pantallas exportadas del Figma van en `docs/figma/`, una imagen por frame, con el nombre del frame. Son la referencia del ciclo visual (sección 6.2) para quien no tenga el conector de Figma, y la base de las comparaciones del README.

- Exportadas al 14/10: **36 de 37**. Falta `Login - 02 SignUp`, y tampoco está el frame del sistema de diseño: hay que exportarlos.
- **Tamaño de referencia:** el ancho de los frames es de unos **396 dp** (Login, 390) y el alto base de unos **850 dp**. Las exportaciones vienen mezcladas a 1x y a 2x: se compara siempre a la misma escala.
- **Pantallas más altas que 850 dp** (por ejemplo Home / 01, Nutrition / 01, Progress / 02, Profile / 06): tienen contenido que se desplaza. Llevan scroll (UI03).
- **Los textos del Figma están en inglés** ("Sign In", "Workouts", "Today's Meals"). Como la fidelidad incluye el contenido visible, la propuesta es **respetarlos tal cual**. Es una decisión de grupo y lleva su ficha en `docs/decisiones/`.

### 3.2 Sistema de diseño

Valores exactos de la lámina **"Sistema de diseño — VORA"** del Figma (`docs/figma/Sistema de diseño — VORA.png`), pasados al código en `ui/theme/` por Tema y componentes (tarea VC-d, 20/10). Reemplazan a los aproximados que se habían medido sobre las capturas.

| Rol | Color | Dónde se ve | En el código |
|---|---|---|---|
| Verde principal | `#123D35` | Acciones, paneles, navegación activa | `colorScheme.primary` |
| Menta | `#65D6B0` | Coach, voz, carbohidratos | `colorScheme.secondary` |
| Lima | `#C9E86A` | CTA destacado ("Start Workout"), grasas | `colorScheme.tertiary`, `VoraTheme.colors.cta` |
| Superficie | `#FFFFFF` | Tarjetas y campos | `colorScheme.surface` |
| Fondo | `#F7F8F5` | Fondo de pantalla | `colorScheme.background` |
| Borde | `#E4E9E6` | Separadores | `colorScheme.outline` |
| Texto principal / secundario / tenue | `#17211F` / `#68736F` / `#9AA39F` | Títulos, apoyo, metadatos | `onSurface` / `onSurfaceVariant` / `VoraTheme.colors.textMuted` |
| Error | `#D95D5D` | Error y "NOT STARTED" | `colorScheme.error` |
| Éxito | `#27AE60` | Señal de éxito | `VoraTheme.colors.success` |
| Menta translúcida | `#65D6B0` al 12 % | Avisos y apoyo | `VoraTheme.colors.mintTranslucent` |

Tipografía, espaciado y radios: ver `specs/CONTEXTO.md` §6 y los KDoc de `ui/theme/Type.kt` y `ui/theme/Dimens.kt`.

**Componentes que se repiten** (UI02: se arman una vez y se reutilizan):
- Barra inferior de 5 pestañas: Home, Workout, Nutrition, Progress, Profile.
- Barra superior con flecha de volver y título.
- Botones: principal lima, oscuro, menta, de contorno y de texto.
- Tarjeta clara y tarjeta oscura "VORA" (con el orbe chico).
- Orbe de VORA (el círculo grande de las pantallas de voz).
- Chips y etiquetas (por ejemplo "MODERATE", "READY", alimentos elegidos).
- Control segmentado (Week, Month, 3 Months).
- Fila de lista con ícono, texto y flecha.
- Interruptor (toggle), campo de texto, selector con + y -.
- Anillo de progreso, barra de progreso y gráfico de barras.
- Tarjeta de estadística (número grande con etiqueta chica).

**Pantallas de referencia** que pide el profe para las comparaciones visuales: Home / 01 Dashboard, Workout / 01 Dashboard, Nutrition / 01 Dashboard, Progress / 01 Today's Overview, Profile / 01 Dashboard y Voice / 01 Idle (el "Listo para conversar"). El README lleva **cinco comparaciones visuales** (lado a lado, Figma contra la app).

**Figma que usamos:** `https://www.figma.com/design/1nvbbfYNYmKAevuaG28J85/Parcial-Domiciliario-7-Oct-26` (el de la presentación). El anexo de la especificación trae otro link (`owEBfWwacB12hsaruGixIg/Parcial-7-Oct---ORT`); si los frames no coinciden, se le consulta al profe.

---

## 4. La API (mock del profe)

- **URL publicada:** `https://tp3.martinrivas.ar/` (sin autenticación). Swagger: `https://tp3.martinrivas.ar/docs`.
- **Servidor local:** `python3 mock/server.py` desde la carpeta del paquete (Python 3.9 o más). Desde el emulador: `http://10.0.2.2:8000/`. HTTP sin cifrar se habilita **solo para desarrollo**.
- **Contrato:** `mock/openapi.json`; ejemplos en `mock/examples.json`.
- **Formato:** éxito `{ "data": ... }`, error `{ "error": { "code", "message" } }`.

| Endpoint | Uso |
|---|---|
| `GET /v1/workouts?level=` | **Obligatorio.** Catálogo, 3 entrenamientos |
| `GET /v1/workouts/{workoutId}` | **Obligatorio.** Detalle, 3 ejercicios; 404 si no existe |
| `GET /v1/foods`, `/v1/coach/...`, `/v1/nutrition/analyses`, `/v1/plans/weekly`, `/v1/progress/weekly-review`, `/v1/activity/daily` | Opcionales (ampliaciones) |
| `GET /health` | Estado del servidor |

**Escenarios de prueba (para RF05),** por header en la petición:
- `X-Mock-Scenario: normal | empty | error | malformed` (vacío, error 503, JSON inválido).
- `X-Mock-Delay-Ms: 3000` agrega 3 segundos de demora (sirve para ver el estado de carga).

Ningún POST del mock guarda nada: lo que se guarda se evalúa en Room.

**Interceptor de OkHttp (el profe pidió estudiarlos):** los headers de prueba se agregan con un interceptor, no a mano en cada llamada. Un `Interceptor` provisto por Hilt en el módulo de red agrega `X-Mock-Scenario` y `X-Mock-Delay-Ms` solo en la versión de desarrollo, y otro de logging muestra las peticiones en Logcat. Así se prueban todos los estados de RF05 cambiando un solo valor, y queda un buen ejemplo para explicar en la demo.

---

## 5. Arquitectura acordada

Es la que pide la consigna (RF06, RF07, RF08):

```
Pantalla (Compose)
   │  observa el estado / envía eventos
   ▼
ViewModel (@HiltViewModel, coroutines, StateFlow)
   │
   ▼
Repositorio (inyectado por constructor)
   ├── Fuente remota: Retrofit → DTO → modelo
   └── Fuente local:  Room → DAO → Entity ↔ modelo
```

Reglas:
- Tres tipos de clase de datos, cada uno en su capa: **DTO** (forma del JSON), **modelo** (lo que usa la app), **Entity** (tabla de Room). Las conversiones viven en funciones de mapeo, no en la pantalla.
- **Una sola base Room** para toda la app, con las tablas de entrenamientos (caché) y comidas.
- La pantalla solo muestra el estado que le da el ViewModel. Nunca llama a Retrofit ni a un DAO.
- El estado de cada consulta remota distingue: cargando, contenido, vacío y error (con reintento). Si falla la red, se sigue mostrando lo último que hay en Room.
- Hilt provee Retrofit, la base y los DAO en módulos (`@Module`, `@Provides`).
- Inicio y Progreso usan datos de demostración con la **misma forma que los modelos** de Entrenamiento y Nutrición, para poder conectarlos a Room si sobra tiempo.

### 5.1 Decisiones que salen del cuestionario de entrega

El cuestionario del aula (sección 13.1) pregunta por cosas que **no se pueden agregar al final**: se deciden ahora y se respetan desde el primer commit.

**Idiomas (pregunta 7): ningún texto escrito en el código.**
- Todos los textos van en `res/values/strings.xml`, en inglés, igual que el Figma (es el idioma por defecto).
- La versión en español va en `res/values-es/strings.xml`, con las mismas claves. Android elige solo según el idioma del teléfono.
- En Compose siempre `stringResource(R.string.clave)`; plurales con `pluralStringResource`; fechas y números formateados con el `Locale` del dispositivo, nunca armados a mano.
- Agregar otro idioma es sumar otra carpeta (`values-pt`, `values-fr`) y traducir el archivo, sin tocar código.
- El agente rechaza cualquier pantalla con texto escrito directo en un `Text("...")`.

**Stateful y stateless (pregunta 4): cada pantalla en dos partes.**
- `XxxRoute` es la parte **stateful**: obtiene su ViewModel con `hiltViewModel()`, lee el estado con `collectAsStateWithLifecycle()` y pasa eventos.
- `XxxScreen` es **stateless**: recibe el estado y funciones (`onRetry`, `onDelete`) por parámetro y solo dibuja. Esto se llama *state hoisting*: el estado sube a quien lo maneja.
- Los componentes comunes del tema (botones, tarjetas, campos) son stateless.
- Estado solo visual y pasajero (un menú abierto, el texto que se está tipeando) con `remember` o `rememberSaveable`; todo lo que sobrevive a una rotación o viene de datos, en el ViewModel.
- Criterio para elegir: si el dato importa fuera de la pantalla o tiene que sobrevivir a la rotación, es estado del ViewModel; si no, estado local del componente.

**Manejo de errores y mapeo (pregunta 6): se atrapan en el repositorio y se traducen una sola vez.**
- El repositorio envuelve las llamadas a Retrofit y Room y convierte cada excepción en un error propio de la app: sin conexión (`IOException`), error del servidor (`HttpException` 5xx, el escenario `error` del mock), no encontrado (404 del detalle), datos inválidos (JSON mal formado, el escenario `malformed`) y desconocido.
- El ViewModel recibe ese error y lo transforma en el estado `Error` de la pantalla, con un mensaje de `strings.xml` y la acción de reintentar. La pantalla nunca ve una excepción.
- **Estrategia de mapeo:** funciones de mapeo explícitas en la capa de datos (`WorkoutDto.toDomain()`, `Workout.toEntity()`, `WorkoutEntity.toDomain()`), una por cada paso entre capas. Así un cambio en el JSON del servidor se arregla en un solo lugar y la UI no se entera.

**Escalabilidad (pregunta 5): anotar los puntos débiles mientras se programa.**
- Cada vez que algo funcionaría mal con muchos datos (una lista que se carga entera, un total calculado en Kotlin en vez de en la consulta, una caché sin vencimiento), se anota en `docs/decisiones/` con la mejora posible. Eso arma la respuesta a la pregunta 5 con ejemplos reales del código.

---

## 6. Cómo trabajamos con la IA (la metodología)

Combinamos tres técnicas, cada una donde rinde más, y un sistema de autocontrol (6.4) que hace que la IA encuentre y corrija sus errores antes de que lleguen a nosotros.

### 6.1 Para todo el equipo: SDD liviano (spec driven development)

**Qué es:** escribir primero qué tiene que hacer el software y recién después pedirle a la IA que lo programe contra eso. La spec queda escrita en el repo y es la misma para los cinco.

Nuestra ventaja: **la consigna del profe ya es la spec**. En el repo hay una carpeta `specs/` con:

- `CONTEXTO.md` (o `CLAUDE.md`): la constitución del proyecto. Stack, arquitectura de la sección 5, estructura de carpetas, cómo se nombran las cosas, sistema de diseño y reglas para la IA.
- `PANTALLAS.md`: el inventario de las 37 pantallas. Por cada una: número, nombre del frame en Figma, flujo, responsable, desde dónde se llega, a dónde lleva cada control, estado (funcional o demostración) y códigos que cubre. Es también la base de la tabla NAV01 del README.
- `REQUISITOS.md`: la tabla de códigos de la sección 2, con una columna de estado (pendiente, hecho, verificado) y quién lo verificó.
- `funcionales/`: una spec corta para catálogo y detalle de entrenamientos (RF01, RF02, RF05) y otra para el registro de comidas (RF03, RF04, RF05), con la plantilla de la sección 17.

### 6.2 Para cada pantalla: ciclo visual (es donde están los 65 puntos)

**Qué es:** la IA trabaja mucho mejor cuando tiene contra qué compararse. Acá el control es el frame del Figma.

1. **Explorar.** La IA lee `CONTEXTO.md`, la fila de la pantalla en `PANTALLAS.md` y el frame del Figma (con el conector de Figma, si hay acceso al archivo; si no, con la captura exportada del frame).
2. **Planificar.** La IA propone cómo la va a armar: qué componentes comunes reutiliza, estructura y navegación. **No programa todavía.** La persona lee el plan, lo corrige y lo aprueba.
3. **Implementar.** Según el plan aprobado, usando solo componentes y valores del tema.
4. **Verificar.** Captura del emulador, la IA la compara con el frame y lista las diferencias (UI01). Se revisa también en un segundo tamaño de emulador (UI03).
5. **Medir, no mirar.** Mirar dos imágenes y decir "quedó igual" no alcanza: el ojo no distingue `#F7F8F5` de `#FFFFFF` ni 8 de 20 píxeles de margen. Cuando algo del color o de una medida está en duda, se comparan **los mismos puntos** en el frame y en la captura, y se mira el número.
6. **Iterar** hasta que coincida. Si el revisor y quien implementó no coinciden, **gana el nodo del Figma**: se va a buscar el valor, no se discute la captura.
7. **Commit** con mensaje descriptivo.

Captura del emulador:

```
adb shell screencap -p /sdcard/captura.png
adb pull /sdcard/captura.png
```

Color de un punto, en la captura o en el frame bajado del Figma:

```
python3 scripts/color.py captura.png 540,130 540,30
```

**Por qué el paso 5.** Leer bien el nodo no garantiza que salga bien. Dos ejemplos reales de la tarea del asistente (VC-o):

- El encabezado del chat se implementó con el fondo blanco **antes** del margen de la barra de estado, y el blanco se metió detrás de la hora. El nodo estaba bien leído; el orden de los modificadores de Compose, no.
- El círculo de volver quedó a 8 del borde en vez de a 20, porque el área táctil de 48 alrededor de un círculo de 32 ya aporta 8. El dato estaba bien leído y el resultado salió mal igual.

Las dos pasaron varias revisiones a ojo antes de que un muestreo de color las mostrara en un segundo.

### 6.3 Para Retrofit y Room: TDD (desarrollo guiado por tests)

**Qué es:** primero se escribe el test que describe lo que tiene que pasar, se ve que falla, y recién después se escribe el código que lo hace pasar.

Ejemplo (RF04): test "guardo una comida y la vuelvo a leer con los mismos datos, y el total del día suma sus calorías". Primero falla porque no existe el código; después se implementan Entity, DAO y repositorio hasta que pasa.

- **Room:** tests con base en memoria (`Room.inMemoryDatabaseBuilder`): insertar, listar por fecha, eliminar, total diario, caché de entrenamientos.
- **Retrofit:** tests con servidor simulado (MockWebServer) para éxito, lista vacía, error 503, JSON inválido y 404.
- **Repositorio:** test de RF05: si la red falla, se devuelve lo que hay en caché y no se borra.
- **Validaciones de RF03:** nombre vacío y valores negativos no se guardan.
- **Control negativo:** si un test pasa a la primera, se rompe el código a propósito y se confirma que el test lo detecta. Un test que nunca falla no prueba nada.
- **Prueba manual con el mock real:** los headers `X-Mock-Scenario` y `X-Mock-Delay-Ms` para ver en pantalla cada estado de RF05.

### 6.4 Sistema de autocontrol: que la IA encuentre y corrija sus propios errores

#### Los fundamentos (por qué está armado así)

1. **La IA no se corrige sola sin una señal externa.** Un estudio de Google DeepMind (ICLR 2024) midió qué pasa cuando un modelo "revisa su respuesta" sin datos nuevos: en promedio **empeora**, porque cambia respuestas correctas por incorrectas (GPT-4 bajó de 95,5% a 89% en problemas de matemática). Lo que sí mejora el resultado es el **feedback externo**: ejecutar código, tests, herramientas. Traducido: decirle "revisá lo que hiciste" sirve poco; darle un test que falla sirve mucho.
2. **Quien hace no se califica.** El que escribió el código está sesgado a favor de su propio razonamiento. Un revisor en **contexto limpio** (otra sesión o un subagente) ve solo el resultado y los criterios, y lo evalúa por sí mismo. Es el patrón "escritor y revisor" que recomienda Anthropic.
3. **La spec es el juez.** Si un requisito no se puede convertir en una prueba, nadie (ni la IA ni ustedes) puede decir si está bien. Por eso cada criterio se escribe de forma que salga un test o una comparación con el Figma.
4. **Lo que tiene que pasar siempre, no se pide: se automatiza.** Una instrucción en `CLAUDE.md` es un consejo que la IA puede saltear; un *hook* (un script que corre solo en cierto momento) se ejecuta siempre.
5. **Evidencia, no afirmaciones.** La IA muestra la salida de los tests o la captura; no alcanza con que diga "listo".
6. **Cuidado con el sobre-arreglo.** Un revisor al que le piden encontrar problemas casi siempre encuentra alguno, aunque el trabajo esté bien. Si se corrige todo, el código se llena de capas y validaciones innecesarias. Los revisores marcan como obligatorio solo lo que afecta la correctitud o un requisito; el resto es opcional.

#### Capa 1: especificaciones verificables (EARS)

**Qué es EARS:** una forma de escribir requisitos creada en Rolls-Royce (2009) para reglas de motores de avión. Cada requisito sigue una de cinco plantillas, y eso elimina la ambigüedad. Adaptada al español:

| Tipo | Plantilla | Ejemplo del parcial |
|---|---|---|
| Siempre | La app DEBE ... | La app DEBE guardar las comidas en Room. (RF04) |
| Ante un evento | CUANDO ..., la app DEBE ... | CUANDO la persona toca un entrenamiento del catálogo, la app DEBE abrir el detalle de ese entrenamiento. (NAV04) |
| Mientras dura un estado | MIENTRAS ..., la app DEBE ... | MIENTRAS la consulta del catálogo está en curso, la pantalla DEBE mostrar el estado de carga. (RF05) |
| Algo que sale mal | SI ..., ENTONCES la app DEBE ... | SI no hay conexión y el detalle ya se descargó, ENTONCES la app DEBE mostrarlo desde Room. (RF02) |
| Opcional | DONDE ..., la app DEBE ... | DONDE esté la búsqueda de alimentos, la app DEBE precargar el formulario con los nutrientes de la porción. (EXT02) |

**Regla de oro:** cada línea EARS es **un test**. Si no se te ocurre el test, la línea está mal escrita.

Para armar una spec nueva, en vez de escribirla de cero, se le pide a la IA que **las entreviste** (lo recomienda Anthropic):

```
Quiero especificar <funcionalidad> del parcial VORA Coach (requisitos <RF..>).
Leé specs/CONTEXTO.md y la consigna. Entrevistame con preguntas, una por vez,
sobre casos borde, errores y lo que no está claro. No preguntes lo obvio.
Cuando terminemos, escribí la spec en formato EARS en specs/funcionales/<nombre>.md.
```

#### Capa 2: auditar la spec antes de programar

Un agente revisor de solo lectura (`auditor-spec`) lee la spec y busca, como hace el comando `analyze` de Spec Kit:
- **Ambigüedad:** palabras sin criterio medible ("rápido", "lindo", "correcto") o "TODO".
- **No testeable:** criterios que no se pueden probar.
- **Huecos:** requisitos de la consigna que la spec no cubre; errores sin definir (sin conexión, lista vacía, 404, JSON inválido).
- **Contradicciones** con `CONTEXTO.md` o con la consigna.
- **Fuera de alcance:** pantallas o endpoints que la consigna no pide.

Devuelve los hallazgos con severidad. La persona decide qué corregir y aprueba la spec. Recién ahí se programa.

#### Capa 3: los tests los escribe otro

El test se escribe **a partir de la spec, en una sesión distinta** de la que implementa. Si el mismo agente escribe el test y el código, tiende a escribir tests que su código ya pasa. Flujo: una sesión escribe los tests (fallan), otra escribe el código hasta que pasen. Se mantiene el **control negativo**: romper el código a propósito y ver que el test lo detecta.

#### Capa 4: controles automáticos que no se negocian

Son la "señal externa" del fundamento 1. Los arma el rol Tema y componentes junto con el proyecto base (tarea VC-06).

**a) Hook de Claude Code al terminar cada turno.** En `.claude/settings.json` (el mismo archivo de la sección 10.6):

```json
{
  "attribution": { "commit": "", "pr": "", "sessionUrl": false },
  "hooks": {
    "Stop": [
      { "hooks": [ { "type": "command", "command": "./scripts/control-rapido.sh" } ] }
    ]
  }
}
```

Cuando el agente quiere dar la tarea por terminada, corre el script. Si el script sale con código 2, Claude Code **no deja terminar** y le pasa el error para que lo corrija. Así el agente no puede decir "listo" con tests rotos.

`scripts/control-rapido.sh`:

```bash
#!/usr/bin/env bash
# Corre al final de cada turno del agente. Si algo falla, devuelve 2 y el agente sigue corrigiendo.
./gradlew -q ktlintCheck testDebugUnitTest >&2 || { echo "Fallan el formato o los tests unitarios: corregilos antes de terminar." >&2; exit 2; }
./scripts/reglas.sh || exit 2
```

`scripts/reglas.sh` (reglas del grupo que el compilador no ve; las rutas `data/remote` y `data/local` son las que fije `CONTEXTO.md`):

```bash
#!/usr/bin/env bash
fallas=0
UI=$(find app/src/main -path '*/ui/*' -name '*.kt')
if [ -n "$UI" ]; then
  if grep -nE '^import (retrofit2|.*\.data\.(remote|local)\.)' $UI >&2; then
    echo "RF06: una pantalla importa Retrofit o la capa de datos. Tiene que pasar por el ViewModel." >&2; fallas=1
  fi
  if grep -nE 'Text\(\s*(text\s*=\s*)?"' $UI >&2; then
    echo "Idiomas: hay texto escrito directo en una pantalla. Va en strings.xml con stringResource()." >&2; fallas=1
  fi
fi
[ $fallas -eq 0 ] || exit 2
```

Quien use Windows corre lo mismo desde Git Bash (viene con Git), o el rol Esqueleto arma la versión con `gradlew.bat`.

**b) Hook de Git que frena commits con la IA como coautora.** Archivo `.githooks/commit-msg`:

```bash
#!/usr/bin/env bash
if grep -qiE '^co-authored-by:.*(claude|anthropic|copilot|openai)|generated with' "$1"; then
  echo "El commit menciona a una IA como autora o coautora. Sacá esa línea: los commits van solo con tu usuario." >&2
  exit 1
fi
```

Se activa una vez por persona con `git config core.hooksPath .githooks` (sección 0.1).

**c) Integración continua en GitHub.** Cada pull request corre solo, en GitHub Actions, la compilación, los tests, el lint de Android y los tests de capturas. Si algo falla, el pull request no se puede juntar con `main` (se configura en la protección de `main`).

```yaml
# .github/workflows/ci.yml
name: CI
on: [pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: '17' }
      - uses: gradle/actions/setup-gradle@v4
      - run: ./gradlew assembleDebug testDebugUnitTest lintDebug verifyRoborazziDebug
```

**d) Tests de capturas con Roborazzi.** Roborazzi dibuja cada pantalla de Compose en la computadora (sin emulador) y la compara contra una imagen de referencia guardada. Si un cambio en un componente compartido rompe otra pantalla, el test falla y genera una imagen con las diferencias marcadas. Ojo: **no compara contra el Figma**, compara contra la última versión aprobada. El flujo es: el revisor visual aprueba la pantalla contra el Figma, se graba la referencia (`recordRoborazziDebug`), y desde ahí cualquier cambio visual no buscado salta solo. Protege los 40 puntos de fidelidad y el requisito UI02.

#### Capa 5: agentes revisores con contexto limpio

Son subagentes de Claude Code: archivos en `.claude/agents/` del repo, así los tienen los cinco. Cada uno corre **en su propio contexto**, sin ver la conversación ni el razonamiento de quien programó, y con herramientas de **solo lectura** (no pueden cambiar el código; solo reportan). Se invocan con "usá el subagente revisor-requisitos sobre este cambio".

`.claude/agents/auditor-spec.md`

```markdown
---
name: auditor-spec
description: Audita una spec de specs/funcionales antes de programar. Usar antes de implementar una funcionalidad nueva o cuando cambia una spec.
tools: Read, Grep, Glob
---
Sos auditor de especificaciones del parcial VORA Coach. No modificás archivos.
Leé specs/CONTEXTO.md, specs/REQUISITOS.md y la spec indicada. Buscá ambigüedad
(palabras sin criterio medible, TODO), criterios no testeables, huecos (requisitos
RF, UI o NAV sin cubrir; errores sin definir: sin conexión, lista vacía, 404, JSON
inválido), contradicciones con CONTEXTO.md y cosas fuera del alcance de la consigna.
Devolvé una tabla: severidad (CRÍTICA, ALTA, MEDIA), dónde, problema, propuesta.
Máximo 15 hallazgos. Si la spec está bien, decilo: no inventes problemas.
```

`.claude/agents/revisor-requisitos.md`

```markdown
---
name: revisor-requisitos
description: Revisa un cambio contra su spec y los requisitos de la consigna. Usar después de implementar y antes de abrir el pull request.
tools: Read, Grep, Glob, Bash
---
No modificás archivos. Corré `git diff main...HEAD` y leé la spec indicada.
Para cada criterio EARS de la spec, buscá el código que lo cumple y el test que lo
prueba. Marcá cada uno: CUMPLE (con archivo:línea del test), NO CUMPLE o SIN TEST.
Listá además cambios fuera del alcance de la tarea.
Reportá solo lo que afecta la correctitud o un requisito; nada de gustos de estilo.
```

`.claude/agents/revisor-arquitectura.md`

```markdown
---
name: revisor-arquitectura
description: Revisa que el código respete la arquitectura del grupo (capas, Hilt, estado, idiomas, errores). Usar antes de abrir un pull request con código de datos o de pantallas.
tools: Read, Grep, Glob
---
No modificás archivos. Revisá el cambio contra la sección 5 y 5.1 de la metodología:
RF06 (DTO, modelo y Entity separados, mappers explícitos, la UI no toca Retrofit ni DAO),
RF07 (estado en el ViewModel, sin consultas repetidas al rotar o recomponer),
RF08 (Hilt: módulos, inyección por constructor), pantallas divididas en Route
(stateful) y Screen (stateless), textos en strings.xml, errores traducidos en el
repositorio. Cada hallazgo con archivo:línea, regla rota y cómo arreglarlo.
Severidad: BLOQUEA solo si rompe un requisito; si no, SUGERENCIA.
```

`.claude/agents/revisor-visual.md`

```markdown
---
name: revisor-visual
description: Compara una pantalla de la app con su frame de Figma. Usar cuando una pantalla está implementada y hay captura del emulador.
tools: Read, Glob
---
No modificás archivos. Recibís la captura del emulador y la imagen del frame
(docs/figma/). Listá las diferencias en orden de impacto: estructura y orden de
elementos, colores fuera del tema, tipografía (tamaño, peso), espaciados, íconos,
textos cortados o superpuestos, contenido que no entra sin scroll. Para cada una:
qué se ve en Figma, qué se ve en la app y dónde. Al final: APROBADA o CON CAMBIOS.
No marques diferencias de contenido de ejemplo (nombres, números) salvo que rompan el diseño.
```

Además, Claude Code trae el comando **`/code-review`**, que revisa el cambio en busca de bugs (casos borde, errores de corrutinas, nulos) en un subagente limpio. Se usa junto con los otros.

#### Capa 6: el ciclo completo de una tarea

```
 1. Spec en EARS (entrevista) ──► 2. auditor-spec ──► persona aprueba la spec
                                                          │
 3. Sesión A: tests desde la spec (fallan)  ◄─────────────┘
                                   │
 4. Sesión B: implementa ──► hook Stop: formato + tests + reglas (si falla, sigue)
                                   │
 5. Revisores en paralelo, contexto limpio:
    revisor-requisitos · revisor-arquitectura · /code-review · revisor-visual
                                   │
 6. Se corrigen los BLOQUEA / CRÍTICA / ALTA ──► vuelve a 5 (máximo 2 vueltas)
                                   │
 7. Pull request ──► CI en GitHub ──► revisión de otro integrante ──► main
                                   │
 8. Bitácora: qué encontraron los revisores y qué se corrigió
```

Reglas del ciclo:
- **Máximo dos vueltas** de revisión y corrección. Si después de dos correcciones sigue mal, se cierra la sesión (`/clear`) y se arranca una nueva con un pedido mejor escrito que incluya lo aprendido. Anthropic lo recomienda: una sesión limpia con mejor pedido rinde más que una larga llena de intentos fallidos.
- **La persona decide.** Los revisores proponen; quien hizo la tarea elige qué se corrige y lo explica en el pull request.
- **Todo queda escrito.** Los hallazgos de los revisores y cómo se resolvieron van a la bitácora: es exactamente la "data de cómo llegaron al resultado" que pidió el profe, y sirve para responder la pregunta 5 del cuestionario.

---

## 7. Reglas para el agente (obligatorias)

1. **Leer antes de tocar.** Antes de cualquier tarea, leer `specs/CONTEXTO.md`, la parte que corresponde de `PANTALLAS.md` y los códigos de requisito involucrados.
2. **Plan antes de código.** No escribir código sin que la persona haya aprobado el plan.
3. **Siempre devolver lo que hizo.** Al terminar cada tarea, responder con el reporte de la sección 7.1. Sin excepción, aunque la tarea haya sido chica.
4. **Evidencia, no promesas.** Nunca decir "listo" o "funciona" sin mostrar la prueba: salida del build, resultado de los tests o captura comparada con el Figma.
5. **Citar los códigos.** Todo plan y todo reporte dice qué códigos (UI, NAV, RF) cubre.
6. **Explicar para enseñar.** Cada decisión importante se explica en palabras simples, con el porqué. Cada integrante tiene que poder explicar su commit en la demo, y el grupo tiene que poder explicar ViewModel, repositorios e inyección con Hilt.
7. **No inventar.** Si algo no está en la consigna, en la spec ni en el Figma, preguntar. No crear pantallas ni endpoints que no existen.
8. **Sistema de diseño siempre (UI02).** Colores, tipografías, espaciados y componentes salen del tema común. Nada de valores escritos a mano en una pantalla.
9. **Respetar la arquitectura (RF06).** La UI nunca llama a Retrofit ni a un DAO directo. Si una tarea lo tienta, el agente lo frena.
10. **Respetar las zonas compartidas.** Tema y componentes, navegación y base Room tienen responsable (sección 8). Si una tarea necesita cambiarlas, el agente lo avisa y no lo hace por su cuenta.
11. **Nada de permisos innecesarios (NAV05).** No agregar permisos de micrófono ni cámara si no se usan de verdad.
12. **Los commits son de la persona.** Cada integrante commitea desde su propia cuenta; el agente no concentra trabajo de varias en un commit ni commitea por otra. El agente redacta mensajes de commit, títulos y descripciones de pull request con las reglas de la sección 10, y propone cómo partir el trabajo en commits atómicos. **Nunca se agrega como autor ni coautor**: nada de `Co-Authored-By`, "Generated with" ni links a la sesión en commits o pull requests (sección 10.6).
13. **Simulado se marca.** Toda pantalla o acción con datos de demostración queda indicada en `PANTALLAS.md` para documentarla en el README.
14. **Dejar rastro.** Al terminar cada tarea, además del reporte, el agente agrega la entrada en `docs/BITACORA.md`, guarda la evidencia en `docs/evidencia/` y, si se tomó una decisión de diseño o arquitectura, propone la ficha en `docs/decisiones/` (sección 11). Siempre registra qué propuso la IA y qué cambió la persona.
15. **Autocontrol antes de reportar.** El agente no da una tarea por terminada sin pasar el control automático (sección 6.4, capa 4) y sin haber pedido la revisión de los subagentes que correspondan (capa 5). Nunca se autoevalúa "revisando lo que hizo": usa tests, el script de reglas y revisores en contexto limpio. Corrige solo los hallazgos que bloquean y anota el resto como sugerencia.

### 7.1 Formato del reporte al terminar una tarea

```
## Qué hice
(una o dos líneas)

## Requisitos que cubre
(códigos UI, NAV, RF)

## Archivos
- creados: ...
- modificados: ...

## Evidencia
- build: (resultado)
- tests: (cuáles corrieron y resultado, incluido el control negativo)
- visual: (comparación con el frame de Figma y diferencias que quedan)
- control automático: (resultado del hook: formato, tests y reglas)

## Revisión
- revisores que corrieron: (auditor-spec, revisor-requisitos, revisor-arquitectura, /code-review, revisor-visual)
- hallazgos que bloqueaban y cómo se corrigieron
- sugerencias que quedaron sin aplicar y por qué

## Sin verificar
(lo que no se pudo probar y por qué; si no hay nada, decirlo)

## Para entender
(explicación corta de la decisión más importante de esta tarea)
```

---

## 8. Roles

Es el reparto que sugiere el profe para equipos de cinco. Cada responsable también participa de la integración. Quién toma cada rol lo decide el grupo.

| Rol | Qué hace | Zona de la que es responsable |
|---|---|---|
| Tema y componentes | Tema de Compose sacado del Figma (colores, tipografías, espaciados, formas) y componentes comunes: botones, tarjetas, campos, navegación inferior. Después: Acceso e Inicio | Tema y componentes (UI02) |
| Navegación e inventario | Inventario con nombres reales de Figma, grafo de navegación de las 37 pantallas (vacías al principio), regreso y botón atrás | Navegación (NAV01 a NAV04) y `PANTALLAS.md` |
| Workout | Flujo Entrenamiento: Retrofit, caché Room, estados de carga y error, pantallas del flujo | Capa remota y caché de entrenamientos (RF01, RF02, RF05) |
| Nutrition | Flujo Nutrición: carga manual, listado, eliminación y total en Room, pantallas del flujo | Base Room y comidas (RF03, RF04) |
| Coach, Progress y Profile + verificación visual | Pantallas de los tres flujos simulados y coordinación del control visual de las 37 contra Figma | Las cinco comparaciones del README |

Notas:
- Hilt (RF08) y ViewModel (RF07) los arman Workout y Nutrition en sus flujos, con un módulo de Hilt común acordado entre las dos.
- Tema y componentes y Navegación son lo primero que se necesita: si se atrasan, frenan a las otras tres.
- Antes de entregar, el grupo revisa entre todos Workout y Nutrition: Retrofit y Room son la condición obligatoria.

---

## 9. Calendario

| Fechas | Qué | Hito del profe |
|---|---|---|
| 14 al 16/10 | Acordar rutas, modelos y componentes (lo pide el profe para evitar versiones incompatibles). `CONTEXTO.md`, inventario y tema base | |
| 17 al 21/10 | Navegación entre todas las áreas, componentes principales, **primera consulta Retrofit y primera inserción y lectura en Room**, todos con al menos un commit significativo | **Hito 1: martes 21/10** (el repo compila) |
| 22 al 26/10 | Todas las pantallas completas y conectadas; integraciones obligatorias verificadas; ciclo visual por pantalla | |
| 26 al 27/10 | Corregir diferencias visuales, probar estados de error, documentar lo simulado, README, video de demo, ensayo del recorrido | |
| 28/10 | Etiqueta `entrega-tp3` y entrega | **Hito 2: entrega** |

Ojo con la carga de esas semanas: el mismo grupo entrega el TP de Calidad de Software el 26/10 y los miércoles está la reunión con Nestlé.

---

## 10. Git: commits, pull requests y revisiones

### 10.1 Lo que pide el profe (y por qué cambia cómo usamos Git)

- **Un único repositorio** de GitHub para el equipo.
- **Cada integrante commitea desde su cuenta**, con cambios propios, significativos y verificables. Tiene que poder identificarse quién hizo cada parte.
- No acreditan participación: commits vacíos, cambios de formato sin aporte, ni reemplazar todo el trabajo al final.
- En la demo, cada integrante **ubica su commit** y explica cómo se integra con el resto.
- Mensajes que describan el cambio y revisión de las contribuciones antes de integrarlas.
- Al final: etiqueta `entrega-tp3`.

Por eso: **nada de "Squash and merge"** en GitHub. Esa opción junta todos los commits de un pull request en uno solo y se pierde el detalle de qué hizo cada integrante. Usamos **"Create a merge commit"**, que conserva cada commit con su autor.

### 10.2 Ramas

- `main` siempre compila. Nadie commitea directo en `main`.
- En GitHub, `main` protegida: solo entra por pull request con **una aprobación** de otra integrante.
- Una rama por tarea, corta y con nombre claro: `tipo/flujo-tema`. Ejemplos: `feat/nutrition-eliminar-comida`, `feat/workout-catalogo-retrofit`, `fix/home-texto-cortado`.
- Una rama vive pocos días: cuanto más tarda en entrar, más conflictos acumula. Antes de abrir el pull request, traer lo último de `main`.

### 10.3 Mensajes de commit

Usamos **Conventional Commits** (un estándar de la industria) con las reglas clásicas de buen mensaje.

**Formato:**

```
tipo(alcance): descripción corta

Cuerpo opcional: qué cambia y por qué (no cómo, eso lo dice el código).

Refs: RF04
```

**Tipos que usamos:**

| Tipo | Cuándo |
|---|---|
| `feat` | Algo nuevo que se ve o se usa (una pantalla, una funcionalidad) |
| `fix` | Corrige un error (también una diferencia visual con el Figma) |
| `refactor` | Reorganiza código sin cambiar lo que hace |
| `test` | Agrega o corrige tests |
| `docs` | README, `specs/`, comentarios |
| `style` | Formato del código sin cambio de comportamiento (no confundir con estilos visuales: eso es `feat` o `fix`) |
| `chore` | Configuración, dependencias, Gradle |

**Alcance:** el flujo o la zona tocada: `access`, `home`, `voice`, `workout`, `nutrition`, `progress`, `profile`, `theme`, `nav`, `data`, `di` (Hilt).

**Reglas de la descripción:**
1. En infinitivo y minúscula, como completando "este commit va a...": `agregar`, `corregir`, `mostrar`. No "agregué" ni "agregando".
2. Corta: idealmente hasta 50 caracteres, nunca más de 72.
3. Sin punto final.
4. Línea en blanco entre la descripción y el cuerpo.
5. El cuerpo explica **qué y por qué**, con líneas de hasta 72 caracteres.
6. Al pie, el código de requisito si aplica (`Refs: RF04`).

**Ejemplos buenos:**

```
feat(nutrition): eliminar comida con confirmación

Al eliminar se muestra un diálogo; el total diario se recalcula
desde Room porque el DAO expone un Flow.

Refs: RF04
```

```
fix(home): corregir espaciado de tarjetas según Figma
test(workout): cubrir caché cuando el servidor falla
feat(di): proveer Retrofit y Room en módulos de Hilt
```

**Ejemplos malos:** `cambios`, `fix`, `ahora sí anda`, `wip`, `subo todo`, `arreglos varios de home y nutrition`.

**Qué va en un commit (commits atómicos):**
- **Un commit, un cambio lógico.** Si la descripción necesita un "y" para dos cosas distintas, son dos commits.
- **Cada commit compila.** Así cualquier punto de la historia se puede probar y la demo no muestra commits rotos.
- No mezclar cambios de formato con cambios de lógica: el formato tapa lo importante en la revisión.
- No commitear archivos generados, claves ni configuración local (`local.properties`, `google-services.json` con claves privadas, carpetas `build/`).

### 10.4 Pull requests

**Tamaño:** chico. Lo recomendado en la industria es apuntar a menos de 200 a 250 líneas de cambio real (sin contar archivos generados), algo que se revise en 10 a 15 minutos. Las revisiones de Cisco que estudió SmartBear muestran que encontrar errores cae fuerte pasados los 200 a 400 líneas. En este parcial: **un pull request por pantalla o por requisito** (por ejemplo, uno para RF03 y otro para RF04), no uno por flujo entero.

**Cantidad de commits:** no hay un número fijo; lo que importa es que cada commit sea un paso lógico que compila. Como referencia, entre 1 y 5 commits por pull request. Si pasa de 10, seguramente hay que partirlo. Con merge commit, cada uno de esos commits queda en la historia, así que conviene que todos tengan buen mensaje.

**Título:** con el mismo formato que un commit (`feat(nutrition): eliminar comida con confirmación`).

**Descripción (plantilla en `.github/pull_request_template.md`):**

```
## Qué cambia
(una o dos líneas)

## Requisitos
(UI, NAV, RF que cubre)

## Cómo probarlo
1. ...
2. ...

## Evidencia
- Captura del Figma y de la app, lado a lado (si es visual)
- Tests que corren y su resultado

## Simulado o pendiente
(qué quedó con datos de demostración o sin terminar)

## Uso de IA
(qué hizo la IA y qué se revisó a mano)
```

El reporte del agente (sección 7.1) ya trae casi todo esto: se copia y se ajusta.

### 10.5 Revisión de pull requests

**Quién y cuándo:**
- Revisa **otra integrante**, idealmente de otra zona (así todas conocen el código, que sirve para la demo).
- Respuesta en **menos de 24 horas**, aunque sea para decir "lo miro mañana a la mañana". Con dos semanas de plazo, un pull request parado frena a las demás.

**Criterio para aprobar** (el de Google): se aprueba si **mejora el proyecto**, aunque no sea perfecto. No se traba un pull request por gustos personales.

**Qué mirar, en este orden:**
1. ¿Cumple los requisitos que dice cubrir? ¿Se parece al Figma?
2. ¿Respeta la arquitectura? (la UI no llama a Retrofit ni a un DAO; usa el tema y los componentes comunes)
3. ¿Maneja carga, vacío y error donde corresponde?
4. ¿Los tests prueban algo (y tienen su control negativo)?
5. ¿Se entiende? Nombres claros, sin código muerto.

**Cómo escribir comentarios: Conventional Comments.** Cada comentario arranca con una etiqueta que dice qué tipo de comentario es, así quien lo escribió sabe qué es obligatorio y qué es opcional:

```
etiqueta (decoración): asunto

discusión opcional: por qué y qué propongo
```

| Etiqueta | Para qué |
|---|---|
| `praise:` | Algo bien hecho. Al menos uno por revisión, y sincero |
| `issue:` | Un problema concreto. Va con una propuesta de solución |
| `suggestion:` | Una mejora; decir qué y por qué es mejor |
| `question:` | Una duda; si no estás segura de que sea un problema, preguntá |
| `todo:` | Un cambio chico pero necesario |
| `nitpick:` | Un detalle de gusto; nunca bloquea |
| `thought:` | Una idea que surgió; no bloquea |

Decoraciones: `(blocking)` si tiene que resolverse antes de aprobar, `(non-blocking)` si no.

**Ejemplos:**

```
issue (blocking): la pantalla llama al DAO directo

RF06 pide que la UI pase por el ViewModel y el repositorio.
¿Movemos la consulta a MealRepository?
```

```
praise: muy claro el mapeo DTO → modelo → Entity, se entiende de una
nitpick (non-blocking): en Figma este título tiene 4dp más de margen arriba
```

**Cómo responde quien escribió el cambio:**
- Contesta cada comentario: lo corrige (y dice en qué commit) o explica por qué no.
- Quien escribió el cambio es responsable de él, no quien lo revisa.
- Los cambios pedidos se agregan como **commits nuevos**; no se reescribe la historia (nada de `push --force`) una vez que la revisión empezó, porque la revisora pierde lo que ya vio.
- Quien abrió el comentario lo marca como resuelto.

### 10.6 La IA y Git

- La IA **redacta** el mensaje de commit, el título y la descripción del pull request siguiendo estas reglas; **la persona los lee, los corrige si hace falta y commitea desde su cuenta**.
- La IA puede hacer una **primera revisión** de un pull request con Conventional Comments, pero la aprobación la da siempre una integrante (el profe pide revisión del equipo).

**En los commits solo aparecen nuestros usuarios reales.** La IA no figura en ningún commit ni pull request: ni como autora, ni como coautora (`Co-Authored-By`), ni con líneas del tipo "Generated with Claude Code". Cada commit lo firma quien lo hizo, con su usuario de GitHub. El profe evalúa commits propios y significativos de cada integrante.

Cómo se asegura:

1. **Git con tu usuario real** (sección 0.1): `user.name` y `user.email` iguales a tu cuenta de GitHub.
2. **Claude Code configurado para no firmar.** En el repo va el archivo `.claude/settings.json` (se commitea, así vale para los cinco) con:

   ```json
   {
     "attribution": {
       "commit": "",
       "pr": "",
       "sessionUrl": false
     }
   }
   ```

   `commit` y `pr` vacíos sacan la línea de coautoría de los commits y la firma de los pull requests; `sessionUrl` en `false` saca el link a la sesión. Si alguien usa otra herramienta de IA, se aplica la misma regla.
3. **Control antes de cada push:** mirar el último commit con `git log -1` y confirmar que el autor sea tu usuario y que no haya ninguna línea `Co-Authored-By` ni "Generated with". Hay casos reportados en que la configuración no se respeta, así que este control no se saltea. Si aparece, se corrige con `git commit --amend` **antes** de hacer push.

**¿Y la declaración de IA?** La consigna pide declararla **en el README**, no en los commits. Ahí va completa: herramientas, método, qué hizo la IA y qué se corrigió a mano, con links a la bitácora y a las decisiones (secciones 11.5 y 13).

---

## 11. Trazabilidad: mostrar cómo llegamos a cada resultado

El profe pidió que le demos **toda la data posible** para entender cómo llegamos al resultado. Por eso cada decisión, cada pantalla y cada uso de la IA deja rastro en el repo. La regla es que **lo registra el agente al terminar cada tarea**, a partir del reporte de la sección 7.1. Así no es trabajo extra para nadie.

### 11.1 Qué se guarda y dónde

```
docs/
├── BITACORA.md          # una entrada por tarea, en orden de fecha
├── decisiones/          # una ficha por decisión importante
│   ├── 001-arquitectura-capas.md
│   ├── 002-voice-una-pantalla-con-estados.md
│   └── ...
├── evidencia/
│   ├── visual/          # Figma y app lado a lado, por pantalla (antes y después de corregir)
│   ├── tests/           # salida de los tests de Room y Retrofit, con su control negativo
│   └── estados/         # capturas de carga, vacío, error y sin conexión (RF02, RF05)
└── ia/
    └── uso-ia.md        # cómo usamos la IA: método, ejemplos de pedidos, qué corregimos
```

### 11.2 La bitácora (`docs/BITACORA.md`)

Una entrada corta por tarea, que el agente agrega al final de cada reporte:

```
### 2026-10-18 · Sofi · feat(nutrition): eliminar comida con confirmación
- Requisitos: RF04
- Plan aprobado: (resumen en 2 líneas)
- Qué propuso la IA y qué cambiamos: la IA borraba sin confirmar;
  pedimos el diálogo porque RF04 lo exige.
- Evidencia: docs/evidencia/tests/rf04-eliminar.txt, captura antes/después
- Commit/PR: #12
```

La línea **"qué propuso la IA y qué cambiamos"** es la más valiosa para el profe: muestra que la IA no decidió sola y que entendimos lo que hizo.

### 11.3 Fichas de decisión (`docs/decisiones/`)

Para las decisiones que alguien podría preguntar "¿por qué así?". Formato corto:

```
# 002 · Voice como una sola pantalla con estados
Fecha: 2026-10-16 · Quién decidió: grupo
Contexto: Figma tiene 6 frames de Voice; la consigna permite estados.
Opciones: 6 pantallas separadas / 1 pantalla con 6 estados.
Decisión: 1 pantalla con estados, Conversation aparte.
Por qué: comparten layout; un cambio de estado no es navegación.
Consecuencia: el inventario marca los 6 frames apuntando a VoiceScreen.
```

Decisiones que seguro llevan ficha: arquitectura de capas, cómo se arma la caché de entrenamientos, cómo se calcula el total diario, Voice con estados, servidor usado, ampliaciones elegidas.

### 11.4 Evidencia visual y de pruebas

- **Visual:** por cada pantalla, la captura del frame de Figma y la de la app, lado a lado. Si hubo correcciones, también el "antes". Las cinco de las pantallas de referencia van al README.
- **Tests:** la salida de los tests de Room y Retrofit, incluido el control negativo (el test que falló cuando se rompió el código a propósito).
- **Estados:** capturas de carga, vacío, error con reintento y detalle sin conexión, con el header `X-Mock-Scenario` usado en cada una.
- **Video de la demo:** sigue el checklist de la sección 14.

### 11.5 Uso de IA (`docs/ia/uso-ia.md`)

Es el detalle de la declaración que pide la consigna:
- Qué herramientas usamos y para qué.
- El método (secciones 6 y 7) explicado con ejemplos reales: 3 o 4 pedidos que funcionaron bien y 2 que no, con qué aprendimos.
- Qué hizo la IA y qué se hizo o se corrigió a mano.
- Dónde se ve cada cosa en el repo: el commit o el pull request de cada ejemplo, y la entrada de la bitácora que lo cuenta.

Ojo: no pegar conversaciones enteras (son larguísimas y no se leen). Se resume, y se linkea el commit o el pull request.

### 11.6 Qué no se guarda

- Claves, contraseñas, tokens ni archivos de configuración privada.
- Datos personales de los integrantes más allá de nombre y usuario de GitHub.

---

## 12. Que la estructura se explique sola

El docente mira **la arquitectura a nivel paquetes y la estructura a nivel archivos**: abre el repo y tiene que entender cómo está armado sin que nadie se lo cuente. Por eso la organización no es cosmética, es parte de lo que se evalúa.

**Los paquetes dicen la arquitectura.** Con solo leer los nombres se ve de dónde salen los datos y a dónde van:

```
ui/                    pantallas, componentes, navegación y tema (nunca toca data/)
  screens/<flujo>/     una carpeta por flujo, los 7 del Figma
  components/          los comunes, se arman una vez
  navigation/          las rutas y un grafo por flujo
  theme/               colores, tipografías, espaciados y formas
domain/model/          el modelo con el que trabaja la app
data/
  remote/              Retrofit: la API y los DTO que devuelve
  local/               Room: la base, los DAO y las Entity
  repository/          coordina remoto y local, y traduce los errores
di/                    los módulos de Hilt que construyen todo eso
```

Los paquetes que todavía no están (`domain/model`, `data/repository`) se crean con el primer repositorio: no se inventan antes de tener qué poner adentro.

**Los archivos dicen el patrón.** Cada pantalla son cuatro archivos con el mismo nombre y distinto sufijo (`XxxRoute`, `XxxScreen`, `XxxViewModel`, `XxxUiState`): se ve de un vistazo cuál es stateful y cuál stateless (sección 5.1). Y los tres nombres de un mismo dato (`WorkoutDto`, `Workout`, `WorkoutEntity`) muestran que DTO, modelo y Entity están separados, sin abrir ningún archivo (RF06).

**Los documentos dicen cómo trabajamos.** La misma idea aplicada al repositorio: `specs/` qué hay que hacer, `docs/decisiones/` por qué lo hicimos así, `docs/BITACORA.md` cómo fue cada tarea, `docs/evidencia/` con qué lo probamos. El README (sección 13) los enlaza para que se pueda entrar por ahí.

**Eso también es la declaración de uso de IA.** En lugar de medirla con números, se sigue el rastro: cada entrada de la bitácora dice qué propuso la IA y qué corregimos, y linkea el commit o el pull request donde se ve.

---

## 13. README de la entrega

Lo que exige la consigna, en este orden:

1. Integrantes y qué hizo cada uno.
2. Enlace al Figma.
3. Instrucciones de ejecución y versiones utilizadas (Android Studio, Kotlin, Compose, Retrofit, Room, Hilt).
4. Servidor y URL usados, y cómo configurar la URL (publicado o local con `10.0.2.2`).
5. Inventario de pantallas (NAV01): cada pantalla de Figma, su destino en la app y su estado (funcional o demostración).
6. Estado de cada funcionalidad y funciones simuladas documentadas.
7. Arquitectura: capas, DTO, modelo y Entity, repositorios, ViewModel, qué construye Hilt y su relación con Dagger.
8. Enlace al video de demostración.
9. Las cinco comparaciones visuales (Figma contra app).
10. Declaración del uso de IA generativa: metodología (secciones 6 y 7), con link a `docs/ia/uso-ia.md`.
11. **Cómo llegamos al resultado:** links a `docs/BITACORA.md`, `docs/decisiones/` y `docs/evidencia/` (sección 11), para que el profe pueda seguir el camino de cada parte.
12. Si hay Firebase: instrucciones y reglas, sin contraseñas ni claves administrativas.
13. **Preguntas de la entrega:** las respuestas al cuestionario del aula (sección 13.1), con ejemplos y links al código.

### 13.1 Cuestionario de entrega del aula

Al entregar hay un cuestionario en el aula. Las respuestas se pueden cargar en el aula, en el README o en los dos: **van en los dos**, iguales, y en el README con links a los archivos del código que las demuestran.

| # | Pregunta | Qué tiene que estar hecho para responderla bien | Dónde se apoya |
|---|---|---|---|
| 1 | *(falta transcribirla: no se veía en las capturas)* | | |
| 2 | Hash del último commit entregado por el grupo | La etiqueta `entrega-tp3` creada y subida. El hash se saca con `git rev-list -n 1 entrega-tp3` y tiene que coincidir con el último commit de `main` en GitHub. Vale 8 puntos: se copia, no se tipea. | Sección 10 |
| 3 | ¿Qué tipo de arquitectura usaron y por qué? ¿Podría mejorarla? | MVVM con repositorio como única fuente de datos para la UI, inyección con Hilt. Por qué: separa pantalla, estado y datos, y deja probar cada capa. Mejoras posibles: capa de dominio con casos de uso y módulos por funcionalidad. | Sección 5 |
| 4 | ¿Tuvieron objetos stateful y stateless? ¿Cómo definen la elección? | Pantallas divididas en `Route` (stateful) y `Screen` (stateless), componentes comunes stateless, y el criterio escrito. | Sección 5.1 |
| 5 | ¿Qué mejoras detectan? ¿Tendrían side effects si escala el volumen de datos? Al menos 2 cuestiones a refactorizar. | Lista de puntos débiles anotados durante el desarrollo. Ejemplos típicos: listados sin paginar (Paging 3), filtros y totales hechos en memoria en vez de en la consulta de Room, falta de índice por fecha en comidas, caché de entrenamientos sin vencimiento. | Sección 5.1 y `docs/decisiones/` |
| 6 | ¿Qué manejo de errores harían? ¿Dónde a nivel código? Estrategia de mapeo. | Errores atrapados en el repositorio, traducidos a errores propios, mostrados por el ViewModel como estado `Error` con reintento; mappers explícitos entre DTO, modelo y Entity. | Sección 5.1, RF05 y RF06 |
| 7 | Si hubiera que pasarla a español y conservar el inglés, ¿qué estrategia usarían? ¿Y para más idiomas? | Todos los textos en `strings.xml` desde el día 1: `values/` en inglés y `values-es/` en español. Otro idioma es otra carpeta. | Sección 5.1 |

La pregunta 2 depende de que el grupo cierre bien la entrega; las 3 a 7 se responden mejor si el código ya las cumple, por eso las decisiones de la sección 5.1 se toman desde el primer día.

---

## 14. Checklist de la demo final

Es el recorrido mínimo que pide el profe. Se ensaya antes de entregar.

- [ ] 1. Abrir la app y recorrer las pantallas del inventario, mostrando regreso y navegación inferior.
- [ ] 2. Consultar el catálogo remoto y abrir **dos entrenamientos distintos** con sus detalles.
- [ ] 3. Registrar una comida, cerrar y abrir la app, comprobar que sigue, eliminarla y mostrar cómo cambia el total.
- [ ] 4. Detener el servidor, abrir un detalle ya descargado desde Room; probar error, lista vacía y reintento.
- [ ] 5. Mostrar los commits de cada integrante y explicar ViewModel, repositorios e inyección (y Firebase, si lo hay).

---

## 15. Pendientes por definir

- [ ] Confirmar que el Figma de la especificación (`owEBfWwacB12hsaruGixIg`) no tiene diferencias con el de la presentación, que es el que usamos.
- [ ] Acceso al Figma desde el conector: la copia en borradores (`Dv82OC9hLPQqUfdxYuYOmd`) se abre, pero el conector solo ve la página "Cover Image". Revisar si la página "UI Screens" quedó en la copia; si no, volver a duplicar el archivo completo. Hace falta para sacar valores exactos (colores, tamaños, tipografías) del frame "Sistema de diseño".
- [ ] Exportar a `docs/figma/` los dos frames que faltan: `Login - 02 SignUp` y "Sistema de diseño".
- [x] Reemplazar los colores aproximados de la sección 3.2 por los exactos, y sumar tipografías, tamaños y radios de borde (VC-d, 20/10).
- [ ] Decidir si los textos quedan en inglés como en el Figma (sección 3.1).
- [x] Inventario con los nombres reales de los frames (sección 3, leído del Figma el 14/10).
- [ ] Proteger `main` en GitHub (pull request obligatorio, una aprobación) y cargar la plantilla `.github/pull_request_template.md`.
- [ ] Asignación de roles.
- [ ] Decidir qué ampliaciones se hacen y quién (sección 16).
- [ ] Probar en el servidor elegido las respuestas reales de `/v1/foods` (que filtre por `q`), `/v1/coach/suggestions` y `/v1/coach/messages` (qué campos trae) antes de especificar EXT01 y EXT02.
- [ ] Servidor de trabajo: publicado (`tp3.martinrivas.ar`) o local. Conviene el local para probar RF02 (detener el servidor).

---

## 16. Ampliaciones (puntos extra)

**Regla del profe:** se hacen **solo con el núcleo completo** (todo lo de las secciones 2 y 3 verificado). Antes de eso, ninguna integrante toca una ampliación.

### Cuánto suman (leer antes de elegir)

| Ampliación | Tope |
|---|---|
| Firebase Authentication: registro, inicio y cierre de sesión | 4 |
| Firebase Firestore: guardar y recuperar el perfil | 4 |
| Firebase reglas: aislar datos entre dos cuentas, con evidencia de la prueba | 2 |
| **Otras ampliaciones, todas juntas** (coach, buscador, series, Dagger puro) | **3 en total** |
| **Máximo de extras** | **10** |

Consecuencias:
- Coach con historial y buscador **no suman 3 cada uno**: entre todas las "otras" el tope es 3.
- Si se hace Firebase completo (10), las otras ya no suman nada porque se llegó al máximo.
- El profe valora que funcione y no duplica puntos por la misma integración.
- Lo que más nota mueve sigue siendo el núcleo: un punto de fidelidad visual (de 40) vale lo mismo que un punto extra, y es más seguro.

### EXT01: Coach por Retrofit con historial en Room

Quién guarda qué: **Retrofit trae** las sugerencias y la respuesta del coach; **Room guarda el historial**. El mock no guarda ningún mensaje, así que el historial tiene que vivir en Room.

- Pantalla "Listo para conversar": muestra como chips las preguntas de `GET /v1/coach/suggestions`. Tocar una la envía.
- Envío: `POST /v1/coach/messages` con el texto. La respuesta es simulada y trae una marca de simulación: se muestra indicándolo.
- Historial: cada mensaje (del usuario y del coach) se guarda en una tabla de Room. La pantalla "Conversación" lo observa desde Room, así sigue ahí al cerrar y abrir la app.
- Sin conexión: se ve el historial guardado y el envío muestra error con reintento (RF05), sin perder lo escrito.
- Solo texto: **no pedir permiso de micrófono** (NAV05). Los estados de voz (Escuchando, Procesando) siguen simulados.
- Misma arquitectura: DTO, modelo y Entity separados, repositorio, ViewModel y Hilt.

Tests: guardar y leer el historial en orden; una falla de red no borra el historial; MockWebServer para sugerencias y respuesta.

### EXT02: Búsqueda de alimentos y cálculo nutricional

- En "Agregar alimento", la opción de búsqueda consulta `GET /v1/foods?q=&page=&pageSize=` (12 alimentos, paginada).
- El ViewModel espera a que la persona deje de tipear (debounce de unos 300 ms) antes de consultar, para no disparar una búsqueda por letra.
- Paginación: al llegar al final de la lista, pide la página siguiente.
- Cálculo nutricional: al elegir un alimento y una porción, se calculan calorías, proteínas, carbohidratos y grasas proporcionales a la porción, y se precarga el formulario de carga manual (RF03). Se guarda por el mismo camino de Room, así el total diario se actualiza (RF04).
- Estados: cargando, resultados, sin resultados y error con reintento (RF05).
- **Ojo:** según la consigna, el servidor local filtra de verdad por `q`; el mock de Postman Cloud devuelve respuestas fijas. Hay que verificar que el servidor que usemos filtre (buscar algo que no existe tiene que dar lista vacía).

Tests: cálculo proporcional (por ejemplo, 150 g de un alimento con valores cada 100 g da 1,5 veces cada valor); valores no negativos; búsqueda vacía da estado vacío.

### Otras que también cuentan (dentro de los mismos 3 puntos)

- Seguimiento de series en Entrenamiento activo (guardar series y repeticiones hechas en Room).
- Práctica aislada de Dagger puro (fuera de la app, para mostrar la diferencia con Hilt).

### Orden recomendado si sobra tiempo

1. Corregir diferencias visuales pendientes (cada punto de los 40 cuenta igual que un extra).
2. Una "otra" ampliación bien hecha: el buscador (EXT02) es el que menos riesgo tiene, porque reusa el Room de Nutrición.
3. El coach (EXT01), si queda tiempo después.
4. Firebase solo si el grupo quiere ir por más de 3 puntos extra: es la única vía para llegar a 10.

Dueñas sugeridas: EXT02 la toma Nutrition; EXT01, quien tenga Coach o quien termine primero; Firebase, Tema y componentes (que ya tiene Acceso).

---

## 17. Plantillas

### 17.1 Spec de una funcionalidad (`specs/funcionales/<nombre>.md`)

```
# <Nombre de la funcionalidad>
Flujo: <Workout | Nutrition>
Requisitos: <RF01, RF02, ...>
Pantallas: <frames de Figma involucrados>

## Qué hace
(dos o tres líneas)

## Datos
- DTO: (campos del JSON)
- Modelo: (campos que usa la app)
- Entity: (tabla de Room)

## Criterios de aceptación (EARS, sección 6.4)
- C1. La app DEBE ...
- C2. CUANDO ..., la app DEBE ...
- C3. MIENTRAS ..., la app DEBE ...
- C4. SI ..., ENTONCES la app DEBE ...

## Fuera de alcance
- (lo que esta funcionalidad NO hace)

## Tests
- C1 → (nombre del test), con su control negativo
- C2 → ...

## Auditoría
- auditor-spec: (fecha, hallazgos y cómo se resolvieron)
```

### 17.2 Pedido al agente para una pantalla

```
Pantalla: <nombre del frame> (flujo <X>), fila <N> de PANTALLAS.md.
Requisitos: <códigos>.
Leé CONTEXTO.md, la fila y el frame del Figma.
No programes todavía: proponé el plan.
```

Y después de aprobar el plan:

```
Implementalo. Después te paso la captura del emulador:
comparala con el frame, listá las diferencias y corregí hasta que coincida.
Al terminar, devolveme el reporte.
```
