# VORA Coach · parcial de Taller de Programación 3 (ORT)

App Android en Kotlin y Jetpack Compose, a partir de un Figma de 37 pantallas. Grupo: Martu, Sofi, Hector, Zaki y Fiore.

Antes de cualquier tarea leé @specs/CONTEXTO.md. Ante cualquier duda manda la consigna del profe (`specs/CONSIGNA.md`).

## Antes de empezar: elegí el carril

| Si el cambio... | Carril | Cómo se trabaja |
|---|---|---|
| se dice en una oración y toca uno o dos archivos | **A · chico** | Directo, sin spec ni plan |
| es una pantalla, un componente o la navegación | **B · pantalla** | Skill `pantalla` |
| trae, guarda, valida o calcula datos (Retrofit, Room, ViewModel) | **C · datos** | Skills `spec-nueva`, después `tests-rojo`, después `implementar`, cada una en su sesión |
| es algo que andaba y ya no anda | **D · bug** | Skill `bug` |

Al terminar cualquier tarea de los carriles B, C o D: skill `cerrar-tarea`. Si la tarea tiene datos y pantalla (por ejemplo Workout / 03), primero C y después B.

Si no está claro qué carril es, preguntá antes de empezar.

## Reglas que no se negocian

- **Plan antes de código** (salvo carril A). Todo plan y todo reporte nombra los códigos que cubre (UI, NAV, RF).
- **Evidencia, no "listo":** mostrar la salida de los tests o del build, o la captura comparada con el Figma.
- **Nunca modificar un test para que pase.** Si un test parece mal, se avisa y decide la persona.
- **Arquitectura (RF06):** la UI nunca importa Retrofit, un DAO ni nada de `data/`. Pasa por el ViewModel y el repositorio.
- **Textos** en `res/values/strings.xml` (y `values-es`), con `stringResource`. **Colores, tipografías y tamaños** salen del tema.
- **Comentarios: los mínimos.** Nada que repita lo que el código ya dice. Se comenta solo lo que no se deduce leyendo: de dónde salió una medida del Figma, por qué se tomó una decisión rara, qué se rompe si se cambia. Un comentario que no se entiende solo está mal escrito: se arregla o se borra, no se deja ahí. **Ojo:** los KDoc de `ui/theme/` y de los componentes guardan de dónde salió cada valor del Figma, y `specs/METODOLOGIA.md` los cita como fuente. Ésos no se recortan.
- **Zonas con responsable:** tema y componentes (Martu); `MainActivity`, navegación y `specs/PANTALLAS.md` (Fiore); red y caché de entrenamientos (Hector); base Room y comidas (Sofi). Si una tarea necesita cambiarlas, avisá y no lo hagas por tu cuenta.
- **La IA nunca commitea, no hace push y nunca firma.** Redacta el mensaje de commit y el pull request; la persona los revisa y commitea con su usuario. Nada de `Co-Authored-By` ni "Generated with".
- **Sin permisos de micrófono ni cámara** (NAV05). Sin endpoints inventados.
- Después de **dos correcciones fallidas** sobre lo mismo, proponé `/clear` y un pedido nuevo con lo aprendido.

## Comandos

- Tests unitarios (sin emulador): `./gradlew testDebugUnitTest`
- Tests de Room (con el emulador abierto): `./gradlew connectedDebugAndroidTest`
- Compilar: `./gradlew assembleDebug`
- Captura del emulador: `adb shell screencap -p /sdcard/captura.png && adb pull /sdcard/captura.png`
- Mock local: `python3 mock/server.py` (desde el emulador: `http://10.0.2.2:8000/`). El paquete del profe va descomprimido en `mock/`; no está en el repo
- Sesión que escribe tests en rojo: abrirla con `TDD_ROJO=1 claude`, o crear el archivo vacío `.tdd-rojo` en la raíz y borrarlo al terminar.

El control automático (`scripts/control-rapido.sh`) corre solo al final de cada turno: si fallan los tests o las reglas del grupo, no se puede dar la tarea por terminada.
