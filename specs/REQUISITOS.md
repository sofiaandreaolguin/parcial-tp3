# Requisitos de la consigna

Un código por fila, con su responsable, **el pull request donde entró a `main`** y su estado.

Estados: **pendiente** · **en curso** · **hecho** (está en `main`) · **verificado** (otro integrante lo probó y dejó evidencia en `docs/evidencia/`).

La columna "En `main` desde" indica el pull request en el que cada requisito entró a `main`, con su fecha; `git log origin/main --merges` los lista. El código de los dieciséis entró entre el **18 y el 25/10**.

Ninguna fila figura todavía como **verificado**: ese estado pide que otro integrante lo pruebe y deje evidencia, y corresponde al control visual cruzado de las 37 (VC-s) y al ensayo del recorrido (VC-v). Donde ya hay evidencia de un tercero, está citada.

## Fidelidad visual y navegación

| Código | Qué pide | Responsable | Dónde se cubre · En `main` desde | Estado | Verificó |
|---|---|---|---|---|---|
| UI01 | Maquetación fiel al Figma: estructura, colores, tipografías, espaciados, íconos; sin textos cortados ni superpuestos | Todos, cada uno las suyas; control visual Zaki | Las 37 en `ui/screens/` · PR #16 a #54, 22 al 25/10 | hecho | Zaki, VC-s: 37/37 en `docs/evidencia/visual/control-final.md` |
| UI02 | Tema propio y componentes reutilizados (corregir uno lo corrige en todas) | Martu | `ui/theme/`, `ui/components/` · **PR #5 a #7, 20/10** | hecho | lo controla `reglas.sh` |
| UI03 | Consistente en el tamaño del Figma y en otro emulador; lo que no entra se desplaza | Todos; control visual Zaki | Capturas de las 37 en 411 dp y 360 dp, en `docs/evidencia/visual/` | hecho | Zaki, VC-s: 37/37 en `docs/evidencia/visual/control-final.md` |
| NAV01 | Cada pantalla de Figma con su destino y estado (funcional o demostración) en el README | Fiore | `PANTALLAS.md` y la tabla de 37 filas del README · **PR #2, 18/10** | hecho | |
| NAV02 | Recorridos entre áreas; volver, cerrar y cancelar; atrás del sistema coherente | Fiore | `ui/navigation/`: 7 grafos, 33 destinos · **PR #2, 18/10** | hecho | `RutasTest` |
| NAV03 | Todo control que abre algo lleva a su destino | Todos, cada uno las suyas | `PANTALLAS.md` §3 a §9; los controles sin frame avisan modo demostración · PR #27 y #49 | hecho | |
| NAV04 | Cada entrenamiento abre su propio detalle; la selección se mantiene | Hector y Sofi | Rutas con id (`workout_details/{workoutId}`) leídas con `SavedStateHandle` · PR #26 y **#51, 25/10** | hecho | |
| NAV05 | Coach y análisis de comida simulados con estados claros; sin pedir micrófono ni cámara | Zaki y Sofi | Voice y Nutrition 04 a 06 · PR #14, #44, #46 | hecho | lo controla `reglas.sh` |

## Integraciones obligatorias

| Código | Qué pide | Responsable | Spec · En `main` desde | Estado | Verificó |
|---|---|---|---|---|---|
| RF01 | Catálogo `GET /v1/workouts` y detalle `GET /v1/workouts/{id}` con nombre, nivel, duración y ejercicios | Hector | [`catalogo-de-entrenamientos.md`](funcionales/catalogo-de-entrenamientos.md) · **PR #11, 22/10** · detalle en [`detalle-y-cache-de-entrenamientos.md`](funcionales/detalle-y-cache-de-entrenamientos.md) · **PR #51, 25/10** | hecho | |
| RF02 | Caché en Room del catálogo y los detalles; sin servidor se ve lo descargado; lo nunca descargado avisa que no está disponible sin conexión | Hector | [`detalle-y-cache-de-entrenamientos.md`](funcionales/detalle-y-cache-de-entrenamientos.md) · **PR #51, 25/10** | hecho | `docs/evidencia/estados/` (C34) |
| RF03 | Carga manual de comidas con validación (nombre no vacío, números no negativos) | Sofi | [`registro-de-comidas.md`](funcionales/registro-de-comidas.md) · **PR #50, 25/10** | hecho | |
| RF04 | Comidas en Room, listado por fecha, eliminación con confirmación, total diario que cambia | Sofi | [`carga-y-eliminacion-de-comidas.md`](funcionales/carga-y-eliminacion-de-comidas.md) · **PR #9 (21/10), #50 y #54 (25/10)** | hecho | |
| RF05 | Carga, contenido, vacío y error con reintento; un fallo no borra caché ni registros | Hector (y Sofi en lo local) | [`estados-de-carga-y-error.md`](funcionales/estados-de-carga-y-error.md) · **PR #53, 25/10** | hecho | `docs/evidencia/estados/` (C31 a C33) |
| RF06 | DTO, modelo y Entity separados; repositorio; la UI no toca Retrofit ni DAO | Hector y Sofi | `CONTEXTO.md` §2 · **PR #4, 20/10**, y sostenido en cada PR desde entonces | hecho | lo controla `reglas.sh` |
| RF07 | ViewModel con estado observable; rotar no repite consultas ni pierde estado | Hector y Sofi | las dos specs · PR #11 (22/10) y #50 (25/10) | hecho | `docs/evidencia/estados/` (C35) |
| RF08 | Hilt: `@AndroidEntryPoint`, `@HiltViewModel`, repositorios por constructor, módulos; saber explicar qué construye Hilt y su relación con Dagger | Hector y Sofi | `di/`: 4 módulos, 1 `@HiltAndroidApp`, 1 `@AndroidEntryPoint`, 28 `@HiltViewModel` · **PR #4, 20/10** | hecho | |

## Extras

La consigna no los pide. El docente los mencionó como algo que sumaba si quedaba tiempo, y se
encararon recién con los ocho requisitos obligatorios ya en `main`.

| Código | Qué pide | Responsable | Spec · En `main` desde | Estado | Verificó |
|---|---|---|---|---|---|
| EXT01 | Coach con respuestas del servidor (`GET /v1/coach/suggestions`, `POST /v1/coach/messages`) e historial guardado en Room que sobrevive a cerrar la app | Fiore | [`coach-con-historial.md`](funcionales/coach-con-historial.md) · rama `feat/voice-coach-historial`, **todavía sin unir a `main`** | hecho | `docs/evidencia/coach/` |
| EXT02 | Búsqueda de alimentos en `GET /v1/foods`, cálculo de los nutrientes de la porción y precarga de la carga manual | Fiore | [`busqueda-de-alimentos.md`](funcionales/busqueda-de-alimentos.md) · rama `feat/nutrition-busqueda-alimentos`, **todavía sin unir a `main`** | hecho | |

Con EXT01, `Voice / 05 — Conversation` pasa de **Demostración** a **Funcional**: es la única de las
seis del asistente que usa datos reales. Con EXT02, la búsqueda de alimentos se abre como **hoja inferior** sobre la pantalla que la llama,
así que el inventario sigue en 37 pantallas y 33 destinos. Reemplaza el C27 de
[`asistente-vora.md`](funcionales/asistente-vora.md), que decía que la app no respondía sola.

## Hito 1 (miércoles 21/10)

Cumplido en fecha. Lo que respalda cada punto:

- [x] **Compila** — `./gradlew assembleDebug` en verde, con los tests unitarios de ese momento sin fallos (hoy son 351: el número de hoy es el del repo, no el del hito)
- [x] **Tema y componentes principales** — `ui/theme/` y `ui/components/`, PR #5 a #7 del 20/10 (UI02)
- [x] **Inventario de pantallas** — `specs/PANTALLAS.md`, PR #2 del 18/10; los 37 frames verificados contra el panel de capas del Figma
- [x] **Navegación entre áreas** — 7 grafos y 33 destinos en `ui/navigation/`, PR #2 del 18/10, con la barra inferior en las 16 que la comparten
- [x] **Primera consulta Retrofit** — `GET /v1/workouts` en `WorkoutRepositoryImpl`, PR #11
- [x] **Primera inserción y lectura en Room** — `MealDao.insert` y `MealDao.observeByDate`, PR #9 del 21/10
- [x] **Las cinco con al menos un commit significativo** — `git shortlog -sn` sobre `main`
