# Requisitos de la consigna

Un código por fila, con su responsable y su estado. Se actualiza al cerrar cada pull request (lo propone la skill `cerrar-tarea`).

Estados: **pendiente** · **en curso** · **hecho** (está en `main`) · **verificado** (otra integrante lo probó y dejó evidencia en `docs/evidencia/`).

## Fidelidad visual y navegación

| Código | Qué pide | Responsable | Dónde se cubre | Estado | Verificó |
|---|---|---|---|---|---|
| UI01 | Maquetación fiel al Figma: estructura, colores, tipografías, espaciados, íconos; sin textos cortados ni superpuestos | Todas, cada una sus pantallas; control visual Zaki | `PANTALLAS.md` | pendiente | |
| UI02 | Tema propio y componentes reutilizados (corregir uno lo corrige en todas) | Martu | `ui/theme/`, `ui/components/` | pendiente | |
| UI03 | Consistente en el tamaño del Figma y en otro emulador; lo que no entra se desplaza | Todas; control visual Zaki | `PANTALLAS.md` | pendiente | |
| NAV01 | Cada pantalla de Figma con su destino y estado (funcional o demostración) en el README | Fiore | `PANTALLAS.md` y README | pendiente | |
| NAV02 | Recorridos entre áreas; volver, cerrar y cancelar; atrás del sistema coherente | Fiore | Grafos de navegación | pendiente | |
| NAV03 | Todo control que abre algo lleva a su destino | Todas, cada una sus pantallas | `PANTALLAS.md` | pendiente | |
| NAV04 | Cada entrenamiento abre su propio detalle; la selección se mantiene | Hector y Sofi | Rutas con id | pendiente | |
| NAV05 | Coach y análisis de comida simulados con estados claros; sin pedir micrófono ni cámara | Zaki y Sofi | Voice y Nutrition 04 a 06 | pendiente | |

## Integraciones obligatorias

| Código | Qué pide | Responsable | Spec | Estado | Verificó |
|---|---|---|---|---|---|
| RF01 | Catálogo `GET /v1/workouts` y detalle `GET /v1/workouts/{id}` con nombre, nivel, duración y ejercicios | Hector | `funcionales/catalogo-de-entrenamientos.md` (falta el detalle) | en curso | |
| RF02 | Caché en Room del catálogo y los detalles; sin servidor se ve lo descargado; lo nunca descargado avisa que no está disponible sin conexión | Hector | `funcionales/` (caché de entrenamientos) | pendiente | |
| RF03 | Carga manual de comidas con validación (nombre no vacío, números no negativos) | Sofi | `funcionales/` (registro de comidas) | pendiente | |
| RF04 | Comidas en Room, listado por fecha, eliminación con confirmación, total diario que cambia | Sofi | idem | pendiente | |
| RF05 | Carga, contenido, vacío y error con reintento; un fallo no borra caché ni registros | Hector (y Sofi en lo local) | las dos specs | pendiente | |
| RF06 | DTO, modelo y Entity separados; repositorio; la UI no toca Retrofit ni DAO | Hector y Sofi | `CONTEXTO.md` sección 2 | en curso | |
| RF07 | ViewModel con estado observable; rotar no repite consultas ni pierde estado | Hector y Sofi | las dos specs | en curso | |
| RF08 | Hilt: `@AndroidEntryPoint`, `@HiltViewModel`, repositorios por constructor, módulos; saber explicar qué construye Hilt y su relación con Dagger | Hector y Sofi | `di/` | en curso | |

## Hito 1 (miércoles 21/10)

- [ ] Compila
- [ ] Tema y componentes principales
- [ ] Inventario de pantallas
- [ ] Navegación entre áreas
- [x] Primera consulta Retrofit
- [ ] Primera inserción y lectura en Room
- [ ] Las cinco con al menos un commit significativo
