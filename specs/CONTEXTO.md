# Contexto del proyecto (la constitución)

Lo que cualquier integrante, y cualquier IA, tiene que saber antes de tocar el código. Es corto a propósito: el detalle vive en los otros archivos de `specs/`.

| Archivo | Qué tiene |
|---|---|
| `specs/CONSIGNA.md` | Resumen de la consigna del profe: requisitos UI, NAV y RF, límites y evaluación. **Manda ante cualquier duda.** |
| `specs/API.md` | El mock del profe: endpoints, JSON, errores y escenarios de prueba |
| `specs/REQUISITOS.md` | Cada código de la consigna con su responsable y su estado |
| `specs/PANTALLAS.md` | Las 37 pantallas: barra o encima, desde dónde se llega, a dónde lleva cada control, estado. Lo arma Navegación e inventario (tarea c) |
| `specs/funcionales/` | Una spec en EARS por funcionalidad con datos. Plantilla en `specs/funcionales/PLANTILLA.md` |

## 1. Stack

- Kotlin, Jetpack Compose y Material 3. `minSdk` 29, `compileSdk` 37.
- Navigation Compose, ViewModel con `StateFlow` y coroutines, Retrofit con Gson, Room, Hilt (construido sobre Dagger).
- Versiones exactas en `gradle/libs.versions.toml` y en el README.
- Servidor: la `BASE_URL` sale de `api.baseUrl` en `local.properties` (no se sube). Por defecto el mock local desde el emulador, `http://10.0.2.2:8000/`. Para probar RF02 hay que poder apagar el servidor: conviene el local.

## 2. Arquitectura (RF06, RF07, RF08)

```
Pantalla (Compose)   ── observa el estado, manda eventos
   │
ViewModel (@HiltViewModel, StateFlow, coroutines)
   │
Repositorio (inyectado por constructor)
   ├── remoto: Retrofit → DTO → modelo
   └── local:  Room → DAO → Entity ↔ modelo
```

- **Tres clases de datos, cada una en su capa:** DTO (forma del JSON), modelo (lo que usa la app), Entity (tabla de Room). Las conversiones son funciones explícitas en la capa de datos: `WorkoutDto.toDomain()`, `Workout.toEntity()`, `WorkoutEntity.toDomain()`.
- **Una sola base Room** para toda la app (caché de entrenamientos y comidas).
- **La pantalla nunca llama a Retrofit ni a un DAO.** Solo muestra el estado del ViewModel.
- **Estados de cada consulta remota:** cargando, contenido, vacío y error con reintento. Si la red falla, se sigue mostrando lo que hay en Room; un fallo nunca borra la caché ni los registros locales.
- **Errores:** se atrapan en el repositorio y se traducen a errores propios (sin conexión, error del servidor, no encontrado, datos inválidos, desconocido). El JSON roto (`malformed`) se atrapa **antes** que `IOException`, porque `MalformedJsonException` es una `IOException` y se mostraría como "sin conexión". El ViewModel lo pasa a un estado `Error` con mensaje de `strings.xml`.
- **Hilt:** `@HiltAndroidApp` en `VoraApplication`, `@AndroidEntryPoint` en `MainActivity`, `@HiltViewModel` en cada ViewModel. Retrofit, la base y los DAO se proveen en módulos (`@Module`, `@InstallIn`, `@Provides`).
- **Rotación:** la carga arranca en el ViewModel (en `init` o por un evento), nunca desde la composición, así rotar no repite consultas.

## 3. Pantallas

- **Route y Screen:** cada pantalla en dos partes. `XxxRoute` es stateful: obtiene el ViewModel con `hiltViewModel()`, lee el estado con `collectAsStateWithLifecycle()` y pasa eventos. `XxxScreen` es stateless: recibe el estado y funciones (`onRetry`, `onDelete`) y solo dibuja.
- Estado solo visual y pasajero (un menú abierto, lo que se está tipeando) con `remember` o `rememberSaveable`. Lo que importa fuera de la pantalla o sobrevive a la rotación, en el ViewModel.
- **Textos:** ninguno escrito en el código. `res/values/strings.xml` en inglés (como el Figma) y `res/values-es/strings.xml` con las mismas claves. Siempre `stringResource(R.string.clave)`.
- **Barra inferior:** se arma **una sola vez** en el `Scaffold` de `MainActivity` y aparece en las pantallas de `RUTAS_CON_BARRA` (16). Las 19 que se abren encima no la tienen y llevan su encabezado con volver o cerrar. Las 2 de acceso tampoco. **Nunca se copia la barra en una pantalla.**
- **Un grafo de navegación por flujo**, cada uno en su archivo (`WorkoutNavGraph.kt`, `NutritionNavGraph.kt`, ...). `MainActivity`, la barra y `RUTAS_CON_BARRA` se tocan solo por pull request de Navegación e inventario.
- Cambio de pestaña: `popUpTo` a la pantalla base de Home con `saveState`, `launchSingleTop` y `restoreState`. **No** `popUpTo(startDestination)` ni `findStartDestination()`: el inicio del `NavHost` es el login y se saca de la pila al entrar.
- Pantallas más altas que el frame (unos 850 dp): llevan scroll (UI03).

## 4. Estructura de carpetas

> **Acordada el 19/10/2026** con el grupo. Antes era una propuesta; Hector marcó que el profe siempre lo maneja con una carpeta `screens`, así que se usa esa y adentro va una subcarpeta por flujo. Cierra la parte de carpetas de la tarea VC-b.

```
app/src/main/java/com/example/parcial_tp3/
├── MainActivity.kt, VoraApplication.kt
├── ui/
│   ├── theme/            # tema: colores, tipografía, espaciados, radios (Martu)
│   ├── components/       # botones, tarjetas, campos, VoraTopBar, VoraBottomBar (Martu)
│   ├── navigation/       # rutas, RUTAS_CON_BARRA, un NavGraph por flujo (Fiore)
│   ├── components/       # PantallaVacia y la barra, mientras no esté el tema
│   └── screens/          # las 37 pantallas, una subcarpeta por flujo
│       ├── access/       #   Login 01 y 02
│       ├── home/         #   Home 01 y 02
│       ├── voice/        #   los 6 frames de Voice (2 rutas)
│       ├── workout/      #   las 7 de Workout
│       ├── nutrition/    #   las 8 de Nutrition
│       ├── progress/     #   las 6 de Progress
│       └── profile/      #   las 6 de Profile
│                         # cada pantalla: XxxRoute + XxxScreen + XxxViewModel + XxxUiState
├── domain/model/         # modelos de la app (Workout, Exercise, Meal)
├── data/
│   ├── remote/           # Retrofit: API, DTO, interceptores
│   ├── local/            # Room: base, DAO, Entity
│   └── repository/       # repositorios y mappers
└── di/                   # módulos de Hilt
```

Cada subcarpeta de `screens/` tiene un `LEEME.md` con las pantallas de ese flujo, su ruta, si llevan barra, su estado y lo que hay que cuidar al maquetarlas.

`scripts/reglas.sh` controla que nada dentro de `ui/` importe `retrofit2` ni `data.remote` o `data.local`, y que no haya texto escrito en el código. Como busca en todo `ui/`, `screens/` ya queda cubierto sin tocar el script.

**Para reemplazar una pantalla de relleno:** en el grafo de tu flujo (`ui/navigation/XxxNavGraph.kt`), cambiá `PantallaVacia(...)` por tu `XxxRoute(...)`. No se toca `MainActivity` ni la barra.

## 5. Nombres

| Qué | Cómo | Ejemplo |
|---|---|---|
| Pantalla | `XxxRoute` y `XxxScreen` | `WorkoutDetailsRoute`, `WorkoutDetailsScreen` |
| ViewModel y estado | `XxxViewModel`, `XxxUiState` | `NutritionViewModel`, `NutritionUiState` |
| Datos | `XxxDto`, `Xxx`, `XxxEntity` | `WorkoutDto`, `Workout`, `WorkoutEntity` |
| Mappers | `toDomain()`, `toEntity()`, `toDto()` | `WorkoutDto.toDomain()` |
| Tests | El nombre dice el criterio que prueba | `` `C2 nombre con solo espacios no es valido` `` |

## 6. Sistema de diseño (resumen)

El tema lo arma Tema y componentes en `ui/theme/`. Detalle completo en la sección 3.2 de la metodología. Ninguna pantalla escribe un color, un tamaño o un radio a mano.

- **Colores:** verde principal `#123D35` (acciones, paneles, navegación activa), menta `#65D6B0` (coach, voz), lima `#C9E86A` (CTA destacado), superficie `#FFFFFF`, fondo `#F7F8F5`, borde `#E4E9E6`, texto `#17211F` / `#68736F` / `#9AA39F`, error `#D95D5D`, éxito `#27AE60`, menta translúcida `#65D6B01F`. Sobre lima o menta, el texto va en verde.
- **Tipografía:** Manrope en toda la app (400, 500, 600, 700). Título de pantalla 20 Bold, título de tarjeta 18 SemiBold, cuerpo 13 Regular, etiqueta 10 SemiBold en mayúsculas, cifra destacada 24 SemiBold.
- **Espaciado:** 4, 6, 8, 10, 12, 14, 16, 20, 24, 32 y 40. Márgenes de pantalla 20.
- **Radios:** 12 campos y filas, 16 tarjetas, 20 a 24 paneles, 100 botones y chips.
- No se inventan estados que no estén dibujados en el Figma.

## 7. Tests

| Dónde | Qué se prueba | Cómo corre |
|---|---|---|
| `app/src/test/` | Validaciones, mappers, repositorio con fuentes falsas, ViewModel (estados, rotación), Retrofit con MockWebServer | Solo, en cada turno de la IA (`control-rapido.sh`) y en la CI |
| `app/src/androidTest/` | DAO de Room con `Room.inMemoryDatabaseBuilder` | A mano, con el emulador abierto, antes del pull request |

- Para probar el ViewModel: repositorio falso (fake), `runTest`, una regla que reemplaza `Dispatchers.Main` y un colector en `backgroundScope`.
- **Un test se ve fallar antes de dar por bueno el código.** Si pasa a la primera, se rompe el código a propósito y se confirma que falla (control negativo).
- Dependencias de test que todavía no están en `app/build.gradle.kts`: `kotlinx-coroutines-test` y `okhttp mockwebserver` (misma versión que okhttp). Las suma quien escriba el primer test que las necesite, coordinando con Workout (base del proyecto).

## 8. Git

- Ramas: `main` y `feature/flujo-tema`. Nada directo en `main`.
- Commits: Conventional Commits en infinitivo (`feat(nutrition): eliminar comida con confirmación`), con `Refs: RF04` al pie. Cada commit compila.
- Pull request chico (una pantalla o un requisito), con la plantilla. Lo une a `main` **quien lo aprobó**, con merge commit.
- Cada integrante commitea con su usuario. La IA no figura en ningún commit.

## 9. Acuerdos de la tarea b (rutas, modelos y componentes)

> **Pendiente de completar.** Copiar acá lo acordado en la tarea VC-b del tablero (pestaña Desarrollo): nombres de las rutas, modelos de entrenamientos y comidas, lista de componentes comunes y la firma de `VoraBottomBar`. Mientras tanto valen las propuestas de las secciones 3, 4 y 5.
