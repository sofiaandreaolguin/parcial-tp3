# <Nombre de la funcionalidad>

Flujo: <Workout | Nutrition | ...>
Requisitos: <RF01, RF02, ...>
Pantallas: <frames de Figma involucrados>
Responsable: <quién>

## Qué hace
(dos o tres líneas, desde lo que ve la persona que usa la app)

## Datos
- DTO: (campos del JSON, si viene del servidor)
- Modelo: (campos que usa la app)
- Entity: (tabla de Room, si se guarda)

## Criterios de aceptación (EARS)

Cada línea es un test. Si no se te ocurre el test, la línea está mal escrita.

- C1. La app DEBE ...
- C2. CUANDO ..., la app DEBE ...
- C3. MIENTRAS ..., la app DEBE ...
- C4. SI ..., ENTONCES la app DEBE ...

Plantillas: **La app DEBE** (siempre) · **CUANDO** (ante un evento) · **MIENTRAS** (durante un estado) · **SI ... ENTONCES** (algo sale mal) · **DONDE** (opcional, para ampliaciones).

Casos que casi siempre hay que cubrir: sin conexión, lista vacía, error del servidor (503), JSON inválido, no encontrado (404), qué pasa al rotar.

## Fuera de alcance
- (lo que esta funcionalidad NO hace)

## Tests
| Criterio | Test | Dónde | Vi el rojo |
|---|---|---|---|
| C1 | `nombre del test` | `test/` o `androidTest/` | sí / no (control negativo) |

## Auditoría
- auditor-spec: (fecha, hallazgos y qué se corrigió)

## Historial
- (fecha) Criterio agregado o cambiado, y por qué (por ejemplo, un bug que mostró un caso que faltaba)
