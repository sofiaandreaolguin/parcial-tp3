# Parcial TP3: VORA Coach (enunciado)

Resumen del grupo de la consigna del profe (`Especificación VORA Coach - TP3 - 02-2026.docx`) y de la presentación del 14/10. **Si hay diferencias, manda el documento original del profe.**

Docente: **Martín Rivas**. Cursos BE-TP3A y BE-TP3B. Modalidad: domiciliaria en grupo. Presentado el **14-oct-2026**.

## La app

**VORA Coach**: entrenamiento, nutrición y progreso con un coach virtual. App Android en **Kotlin + Jetpack Compose** a partir del diseño en Figma: **37 pantallas en 7 flujos**.

- **Prioridad 1:** reproducir las pantallas y poder recorrer todos los flujos.
- **Prioridad 2:** integrar **Retrofit y Room** en dos recorridos concretos: el catálogo de entrenamientos y el registro de comidas.
- Organizar la app con **ViewModel** e **inyección de dependencias con Hilt**.

**Figma:** `figma.com/design/1nvbbfYNYmKAevuaG28J85/Parcial-Domiciliario-7-Oct-26` (el de la presentación, que usa el grupo). El anexo de la consigna enlaza otro archivo, `owEBfWwacB12hsaruGixIg/Parcial-7-Oct---ORT`; si los frames no coinciden, consultar al profe.
Inventario de pantallas: [PANTALLAS.md](PANTALLAS.md). Mock y endpoints: [API.md](API.md).

## Cobertura mínima por área

| Área | Cobertura mínima | Implementación |
|---|---|---|
| Acceso | Pantallas del diseño y navegación hasta Home. Acceso de demostración válido sin Firebase | Demostración (Firebase es extra) |
| Home y Profile | Maquetación, accesos principales y ajustes navegables. Datos de ejemplo permitidos | Demostración |
| **Workout** | Flujo completo navegable. **Catálogo y detalle funcionales con Retrofit y caché en Room** | **Retrofit + Room** |
| **Nutrition** | Flujo completo navegable. **Carga manual, listado y eliminación de comidas en Room** | **Room** |
| Coach y Progress | Pantallas y estados del diseño navegables. Conversación y métricas pueden ser simuladas | Simulado |

Todas las pantallas del Figma tienen que estar **maquetadas y ser navegables**. Las que no tienen servicio real pueden usar datos de demostración, siempre que se pueda completar el recorrido sin bloqueos ni destinos inexistentes.

## Obligatorio y opcional

- **Retrofit y Room tienen que funcionar.** Agregar las dependencias sin usarlas no cumple. Son **condiciones obligatorias**: la fidelidad visual o los puntos extra no las reemplazan.
- **ViewModel y Hilt** organizan los flujos funcionales.
- **Hilt usa Dagger: no se exige un segundo grafo de Dagger puro dentro de la app.** Una práctica aislada de Dagger puro cuenta como ampliación (puntos extra).
- **Firebase es opcional** y da puntos extra.
- Voz real, análisis de fotos, seguimiento detallado de series y sincronización del historial son ampliaciones.
- **Primero la maquetación y la navegación de todo el diseño.**
- No se exige IA real, Apple Health, Apple Watch, pagos ni suscripciones.

## 1. Fidelidad visual y navegación

- **UI01 Maquetación.** Reproducir estructura, colores, tipografías, espaciados, componentes, iconografía y estados visibles, con los recursos compartidos por el docente. Sin textos cortados, controles superpuestos ni contenido fuera de pantalla.
- **UI02 Componentes reutilizables.** Un tema propio y botones, tarjetas, campos y navegación inferior reutilizados: corregir un componente tiene que corregirlo en todas las pantallas que lo usan.
- **UI03 Adaptación.** Apariencia consistente en el tamaño de referencia de Figma y en al menos otro tamaño de emulador. Lo que no entra tiene que poder desplazarse.
- **NAV01 Cobertura.** En el README, cada pantalla de Figma con su destino en la app y su estado (**funcional** o **demostración**). Todas accesibles desde el recorrido previsto.
- **NAV02 Recorridos y regreso.** Conectar Acceso, Home, Workout, Nutrition, Progress, Profile y Coach según Figma. Volver, cerrar y cancelar; el botón atrás del sistema lleva a un destino coherente.
- **NAV03 Acciones visibles.** Todo control que abre pantallas, pasos o diálogos lleva a su destino. Una acción de servicio opcional puede dar un resultado de ejemplo o avisar que es modo demostración.
- **NAV04 Selección coherente.** Cada entrenamiento del catálogo abre **su propio** detalle. Al elegir una comida o una configuración, la selección se mantiene durante el recorrido. No abrir siempre el mismo recurso.
- **NAV05 Simulación clara.** Poder recorrer escucha, procesamiento y respuesta del coach, y la revisión del análisis de comida, con estados de demostración. **No pedir permisos de micrófono o cámara si no se usan.**

## 2. Integraciones funcionales obligatorias

- **RF01 Catálogo con Retrofit.** `GET /v1/workouts` muestra el catálogo del mock; cada elemento abre `GET /v1/workouts/{workoutId}`. Mostrar al menos nombre, nivel, duración y ejercicios con series, repeticiones e instrucciones.
- **RF02 Caché de entrenamientos con Room.** Guardar catálogo y detalles descargados. Prueba: consultar un detalle, **detener el servidor** y volver a abrirlo: se tiene que ver desde Room. Un detalle nunca descargado informa que **todavía no está disponible sin conexión**.
- **RF03 Registro manual de comidas.** Nombre, fecha, tipo de comida (**desayuno, almuerzo, merienda, cena**) y calorías, proteínas, carbohidratos y grasas **de la porción completa**. Validar nombre no vacío y valores numéricos no negativos antes de guardar.
- **RF04 Persistencia de comidas.** Guardar en Room, listar **por fecha** y eliminar **con confirmación**. Los registros siguen al cerrar y reabrir la app. **El total diario cambia** al agregar o eliminar.
- **RF05 Carga y errores.** Estado de carga durante la consulta remota, contenido al completar, **estado vacío** sin resultados y **error con reintento** si falla. Un fallo de red **no borra la última caché válida ni los registros locales**.
- **RF06 Datos fuera de la pantalla.** **DTO** para el JSON, **modelos** para la app y **Entity** para Room. Entrenamientos: DTO → modelo y → Entity. Comidas: Entity y modelo separados. Un **repositorio** coordina las fuentes; **la UI no consulta Retrofit ni DAO directamente**.
- **RF07 ViewModel.** Estado observable del catálogo y del registro de comidas; eventos procesados en el ViewModel con coroutines. **La rotación no duplica consultas** ni pierde el estado cargado.
- **RF08 Hilt y Dagger.** Hilt con `@AndroidEntryPoint` y `@HiltViewModel`. Repositorios inyectados **por constructor**; **Retrofit, Room y DAO provistos en módulos**. **Explicar qué objetos construye Hilt y su relación con Dagger.**

### Límites del núcleo

- Entrenamiento activo, descanso y finalización **tienen que ser navegables**; el cronómetro, el registro de cada serie y el cálculo de volumen pueden simularse.
- Búsqueda de alimentos, coach remoto y métricas históricas son ampliaciones.
- Sin Firebase: **perfil de demostración**. No se exige autenticación real ni sincronización.
- **No crear endpoints ficticios** de login o guardado de comidas para simular Firebase o Room.

## 3. Servicios y datos de prueba

Ver [API.md](API.md). Resumen: servidor local `python3 mock/server.py` (Python 3.9+) en `http://localhost:8000`, desde el emulador `http://10.0.2.2:8000/`, o el publicado `https://tp3.martinrivas.ar/`. Ningún POST guarda nada: **el guardado que se evalúa es el de Room**.

## 4. Organización y entrega en GitHub

- **Un repositorio por equipo.** Todos con **commits propios, significativos y verificables**; tiene que poder verse quién hizo cada parte. No concentrar todo en una cuenta.
- Mensajes de commit que describan el cambio. Revisar las contribuciones antes de integrarlas.
- **Acordar primero rutas, modelos y componentes** para evitar versiones incompatibles.

### Reparto sugerido

| Responsable | Parte |
|---|---|
| 1 | Tema y componentes comunes |
| 2 | Navegación e inventario de pantallas |
| 3 | Workout con Retrofit y caché en Room |
| 4 | Nutrition con Room |
| 5 (si son cinco) | Coach, Progress y Profile, y coordinar la verificación visual |

Cada responsable también participa de la integración.

### Hitos

| Fecha | Hito |
|---|---|
| **21-oct** (semana 1) | Compila; tema y componentes principales; inventario de pantallas; navegación entre áreas; **primera consulta Retrofit y primera inserción y lectura en Room**; **todos con al menos un commit significativo** |
| **28-oct** (semana 2, entrega; 19:00 según la clase del 7-oct) | Todas las pantallas completas y conectadas; integraciones obligatorias verificadas; diferencias visuales corregidas; estados de error probados; funciones simuladas documentadas. Ampliaciones solo con el núcleo completo |

### Contenido de la entrega

- Proyecto Android **que compila y se ejecuta sin errores**.
- **README** con: instrucciones de ejecución, versiones usadas, link al Figma, integrantes, **inventario de pantallas** con el estado de cada una, estado de cada funcionalidad, **servidor y URL usados y cómo configurarla**, **link al video de demostración**, **las cinco comparaciones visuales** y **uso de IA generativa declarado**.
- Historial de commits y una **etiqueta de entrega** (por ejemplo `entrega-tp3`).
- Con Firebase: instrucciones y reglas de acceso. **No subir contraseñas ni claves administrativas.**

### Participación individual

Cada integrante tiene que poder **explicar al menos un cambio propio, ubicar su commit y mostrar cómo se integra**. Commits vacíos, cambios de formato sin aporte o reemplazar todo el trabajo al final **no cuentan**.

## 5. Evaluación

| Criterio | Puntos | Qué se verifica |
|---|---|---|
| **Fidelidad con Figma** | **40** | Composición, tipografía, colores, recursos, espaciados y consistencia |
| **Navegación completa** | **25** | Todas las pantallas del inventario, acciones, regreso y selección coherente |
| Retrofit funcional | 12 | Catálogo y detalle reales, DTO, carga y errores |
| Room funcional | 12 | Comidas persistentes, eliminación, totales y caché de entrenamientos |
| ViewModel e Hilt | 6 | Estado observable, repositorios e inyección efectiva |
| GitHub y participación | 5 | Entrega reproducible y commits significativos de todos |
| **Total base** | **100** | Figma + navegación = 65 puntos |

Las funciones simuladas bien documentadas no se penalizan fuera del núcleo obligatorio.

### Extras (máximo 10 puntos)

- **Firebase, hasta 10:** Authentication (registro, inicio y cierre de sesión) hasta 4; Firestore (guardar y recuperar el perfil) hasta 4; reglas que aíslen los datos entre dos cuentas, con evidencia de la prueba, hasta 2. El paquete trae perfil de ejemplo, reglas y configuración opcional de Emulator Suite (no vinieron en el zip recibido).
- **Otras, hasta 3 (dentro del máximo de 10):** coach por Retrofit con historial en Room; búsqueda de alimentos y cálculo nutricional; seguimiento de series en el entrenamiento activo; **práctica aislada de Dagger puro**. No se duplican puntos por la misma integración.

## Demostración final (recorrido mínimo)

1. Abrir la app y recorrer las pantallas del inventario, mostrando regreso y navegación inferior.
2. Consultar el catálogo remoto y abrir **dos entrenamientos distintos** con sus detalles.
3. Registrar una comida, **cerrar y abrir la app**, comprobarla y eliminarla, mostrando cómo cambia el total.
4. **Detener el servidor** y abrir un detalle ya descargado desde Room. Probar error, lista vacía y reintento.
5. Mostrar los commits de cada integrante y **explicar ViewModel, repositorios e inyección** (y Firebase si lo hay).

## Faltantes del paquete

La consigna menciona un README del mock (para teléfono físico y configuración de red), un entorno de Postman, un inventario funcional, un perfil de ejemplo, reglas de Firebase y configuración de Emulator Suite. **Nada de eso venía en el `mock.zip` recibido** (solo `server.py`, `test_server.py`, `fixtures.json`, `examples.json`, `openapi.json`, `index.html` y `docs/`). Pedirlo al docente si hace falta.
