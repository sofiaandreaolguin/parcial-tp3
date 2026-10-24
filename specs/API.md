# VORA Mock API

Resumen del contrato del mock del profe (`mock/openapi.json`, `mock/server.py`). Si algo no coincide, manda `openapi.json`.

## Servidores

| Dónde | URL | Notas |
|---|---|---|
| Publicado por el docente | `https://tp3.martinrivas.ar/` | Swagger en `https://tp3.martinrivas.ar/docs`. HTTPS: no necesita habilitar HTTP |
| Local | `http://localhost:8000` | `python3 mock/server.py` (Python 3.9+, sin dependencias). Probador web en `/`, Swagger en `/docs/` |
| Desde el emulador | `http://10.0.2.2:8000/` | Habilitar HTTP (cleartext) **solo en debug** |
| Teléfono físico | `http://<IP-de-la-compu>:8000/` | El servidor escucha en `127.0.0.1` por defecto: correrlo con `python3 mock/server.py --host 0.0.0.0` (también hay `--port`, o las variables `HOST`/`PORT`). Con cable USB es más simple `adb reverse tcp:8000 tcp:8000` y `api.baseUrl=http://localhost:8000/`: no depende de la IP ni del firewall |
| Postman | `{{baseUrl}}` = `http://localhost:8000` | O un mock en Postman Cloud creado desde los ejemplos (respuestas fijas, sin filtros ni validación) |

**Para RF02 y RF05 conviene el servidor local**: hay que poder detenerlo para probar la caché. En Retrofit, la `baseUrl` termina en `/` (clase 9-sep) y conviene sacarla de `BuildConfig` o `local.properties` para cambiar entre local y publicado sin tocar código.

## Formato

- Éxito: `{ "data": ... }`. Siempre viene envuelto en `data`.
- Error: `{ "error": { "code": "...", "message": "..." } }`.
- Sin autenticación. **Ningún POST guarda nada.**

## Endpoints

| Método y ruta | Uso | Obligatorio |
|---|---|---|
| `GET /v1/workouts?level=` | Catálogo: 3 entrenamientos. `level` opcional: `beginner`, `intermediate`, `advanced` | **Sí (RF01)** |
| `GET /v1/workouts/{workoutId}` | Detalle con 3 ejercicios. **404 `WORKOUT_NOT_FOUND`** si no existe | **Sí (RF01)** |
| `GET /v1/foods?q=&page=&pageSize=` | Búsqueda: 12 alimentos, paginada (`pageSize` 1-50, por defecto 10) | No |
| `GET /v1/coach/suggestions` | Preguntas sugeridas | No |
| `POST /v1/coach/messages` | Respuesta de texto simulada, sin IA | No |
| `POST /v1/nutrition/analyses` | Análisis por preset; requiere confirmar antes de guardar | No |
| `GET /v1/plans/weekly?startDate=` | Plan semanal | No |
| `POST /v1/progress/weekly-review` | Comentario simulado sobre métricas que manda la app | No |
| `GET /v1/activity/daily?date=` | Pasos y actividad de ejemplo | No |
| `GET /health` | Estado del servidor | No |

## Modelos (JSON → DTO)

### Obligatorios

```json
// GET /v1/workouts  →  { "data": [WorkoutSummary] }
{ "id": "w01", "name": "Fuerza inicial", "level": "beginner",
  "durationMinutes": 25, "exerciseCount": 3 }

// GET /v1/workouts/{id}  →  { "data": Workout }
{ "id": "w01", "name": "Fuerza inicial", "level": "beginner",
  "durationMinutes": 25, "exerciseCount": 3,
  "description": "Rutina de ejemplo para el trabajo práctico.",
  "exercises": [ Exercise ] }

// Exercise
{ "id": "e01", "name": "Sentadilla", "muscleGroup": "Piernas",
  "sets": 3, "reps": 10, "restSeconds": 60,
  "instructions": "Separá los pies y flexioná las rodillas con control.",
  "tip": "Usá una carga que puedas controlar." }
```

Todos los campos son obligatorios. Los ids son **strings** (`w01`, `e01`), no números.

Datos del mock:

| Id | Nombre | Nivel |
|---|---|---|
| `w01` | Fuerza inicial | beginner |
| `w02` | Entrenamiento completo | intermediate |
| `w03` | Fuerza y constancia | beginner |

### Opcionales

- **Food:** `{ id, name, nutritionPer100g: { kcal, proteinG, carbsG, fatG } }` + `meta: { page, pageSize, total }`. Alimentos: Avena, Leche, Banana, Arroz cocido, Pollo cocido, Lentejas cocidas, Tomate, Huevo, Yogur natural, Manzana, Pan integral, Aceite de oliva. La búsqueda ignora mayúsculas y tildes.
- **Suggestion:** `{ id, text }`.
- **CoachRequest:** `{ text (1-2000), context?: { coachingStyle: supportive | balanced | direct } }` → **CoachReply:** `{ role: "assistant", text, topic: general | workout | nutrition | progress, isSimulated }`. Texto vacío → 400 `INVALID_REQUEST`.
- **AnalysisRequest:** `{ source: text | photo, text?, preset?: breakfast | lunch | vegetarian }` → **Analysis:** `{ source, preset, isSimulated, requiresConfirmation, items: [{ foodId, name, grams, nutritionPer100g }], totals: Nutrition }`.
- **WeeklyPlan:** `{ startDate, days: [{ date, workoutId (puede ser null), type: workout | recovery }] }`.
- **ReviewRequest:** `{ startDate, summary: { completedSessions, volumeKg, averageKcal } }` → **Review:** `{ startDate, isSimulated, summary, text, recommendations: [String] }`.
- **Activity:** `{ date, steps, distanceKm, activeKcal, isSimulated }`.

## Escenarios de prueba (para RF05)

Se mandan como headers; conviene un **interceptor de OkHttp** que los agregue en debug (clase 7-oct).

| Header | Valores | Efecto |
|---|---|---|
| `X-Mock-Scenario` | `normal` | Respuesta normal |
| | `empty` | Listas vacías (`data: []`); solo cambia listas |
| | `error` | **HTTP 503** `SERVICE_UNAVAILABLE` |
| | `malformed` | **JSON inválido** a propósito (el conversor tira excepción: hay que atraparla) |
| `X-Mock-Delay-Ms` | 0 a 10000 | Demora; `3000` = 3 segundos, para ver el estado de carga |

Otros errores: **404** ruta o detalle inexistente (`/v1/workouts/no-existe`), **405** método incorrecto, **415** POST sin `Content-Type: application/json`, **400** parámetros inválidos.

## Qué tiene que manejar la app

- `Response.isSuccessful` (200-299), no `== 200` (clase 23-sep).
- Sin conexión o servidor caído: `IOException` → mostrar caché de Room si hay; si no, error con reintento.
- 503 y 404 → error con reintento o "no disponible", **sin borrar la caché**.
- `malformed` → excepción del conversor → error, sin crash.
- `empty` → estado vacío (distinto del error).

## Comprobado corriendo el mock (14/10, `server.py` local)

Salidas reales, no supuestas:
- `GET /health` → `{"status": "ok", "service": "vora-mock", "version": "1.0"}`.
- `GET /v1/workouts` → los 3 (w01 Fuerza inicial, beginner, 25 min; w02 Entrenamiento completo, intermediate, 35; w03 Fuerza y constancia, beginner, 30), 3 ejercicios cada uno.
- `?level=intermediate` → solo w02. `?level=pro` → **HTTP 400** `{"error": {"code": "INVALID_REQUEST", "message": "level: beginner, intermediate o advanced."}}`.
- `/v1/workouts/no-existe` → **HTTP 404** `{"error": {"code": "WORKOUT_NOT_FOUND", "message": "Entrenamiento no encontrado."}}`.
- `X-Mock-Scenario: empty` → **HTTP 200** `{"data": []}` en el catálogo. **En el detalle no cambia nada**: `/v1/workouts/w01` con `empty` devuelve el detalle completo. El estado vacío se prueba en el catálogo.
- `X-Mock-Scenario: error` → **HTTP 503** `{"error": {"code": "SERVICE_UNAVAILABLE", "message": "Servicio simulado no disponible."}}`.
- `X-Mock-Scenario: malformed` → **HTTP 200** con el texto `{"data": INVALID_JSON` (cortado). `isSuccessful` da verdadero y la excepción la tira Gson al leer: `MalformedJsonException` (que **es una `IOException`**) o `JsonSyntaxException`. Atrapar el JSON roto **antes** que `IOException`, si no se muestra como "sin conexión".
- `X-Mock-Delay-Ms: 1500` → la misma respuesta en 1,5 s.
- Servidor apagado → conexión rechazada (`curl` exit 7): en la app, `IOException`.
