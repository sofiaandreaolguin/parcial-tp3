# Control visual final de las 37 pantallas (UI01, UI03)

Pase sobre `main` en 394fc28 (26/10/2026), en la rama `fix/visual-control-final`. Las capturas se sacaron en un moto g(8) plus: 411 dp es el tamaño del teléfono y 360 dp se forzó con `wm density 480`. El teléfono estaba en inglés y en modo claro, con el mock local para Workout. Después de traer `main` (PR #57, Firebase, y PR #58, coach con historial) se repitieron Login 01 y 02, Home 01, Voice 05 y Profile 01, 02 y 06, con una sesión real de Firebase. Cada flujo lo revisó `revisor-visual` en contexto limpio, contra su frame de `docs/figma/<Flujo> Flow.png`.

Nombres: `<flujo>-NN-411dp.png` y `-360dp.png`. Cuando la pantalla no entra entera, hay además una `-scroll.png` con la pantalla bajada hasta el final. En la columna "Scroll" se indica en qué tamaño hay esa captura extra.

| # | Pantalla | 411 | 360 | Scroll | Veredicto | Corrección |
|---|---|---|---|---|---|---|
| 1 | Login / 01 SignIn | `login-01-411dp` | `login-01-360dp` | 360 | APROBADA | Corregida (ver nota 1) |
| 2 | Login / 02 SignUp | `login-02-411dp` | `login-02-360dp` | 411 y 360 | APROBADA | Corregida (ver nota 1) |
| 3 | Home / 01 Dashboard | `home-01-411dp` | `home-01-360dp` | 411 y 360 | APROBADA | — |
| 4 | Home / 02 Notifications | `home-02-411dp` | `home-02-360dp` | — | APROBADA | — |
| 5 | Voice / 01 Idle | `voice-01-411dp` | `voice-01-360dp` | 360 | APROBADA | — |
| 6 | Voice / 02 Listening | `voice-02-411dp` | `voice-02-360dp` | — | APROBADA | — |
| 7 | Voice / 03 Processing | `voice-03-411dp` | `voice-03-360dp` | — | APROBADA | — |
| 8 | Voice / 04 Speaking | `voice-04-411dp` | `voice-04-360dp` | — | APROBADA | — |
| 9 | Voice / 05 Conversation | `voice-05-411dp` | `voice-05-360dp` | — | APROBADA | — |
| 10 | Voice / 06 Error | `voice-06-411dp` | `voice-06-360dp` | — | APROBADA | — |
| 11 | Workout / 01 Dashboard | `workout-01-411dp` | `workout-01-360dp` | 360 | APROBADA | Diferencia anotada (nota 3) |
| 12 | Workout / 02 Weekly Plan | `workout-02-411dp` | `workout-02-360dp` | 411 y 360 | APROBADA | — |
| 13 | Workout / 03 Workout Details | `workout-03-411dp` | `workout-03-360dp` | — | APROBADA | — |
| 14 | Workout / 04 Pre Workout | `workout-04-411dp` | `workout-04-360dp` | — | APROBADA | — |
| 15 | Workout / 05 Active Workout | `workout-05-411dp` | `workout-05-360dp` | — | APROBADA | — |
| 16 | Workout / 06 Exercise Details | `workout-06-411dp` | `workout-06-360dp` | — | APROBADA | — |
| 17 | Workout / 07 Workout Complete | `workout-07-411dp` | `workout-07-360dp` | — | APROBADA | — |
| 18 | Nutrition / 01 Dashboard | `nutrition-01-411dp` | `nutrition-01-360dp` | 411 y 360 | APROBADA | — |
| 19 | Nutrition / 02 Today's Meals | `nutrition-02-411dp` | `nutrition-02-360dp` | — | APROBADA | Diferencia anotada (nota 4) |
| 20 | Nutrition / 03 Add Food | `nutrition-03-411dp` | `nutrition-03-360dp` | — | APROBADA | — |
| 21 | Nutrition / 04 Voice Food Logging | `nutrition-04-411dp` | `nutrition-04-360dp` | — | APROBADA | — |
| 22 | Nutrition / 05 Food Analysis | `nutrition-05-411dp` | `nutrition-05-360dp` | — | APROBADA | — |
| 23 | Nutrition / 06 Photo Analysis | `nutrition-06-411dp` | `nutrition-06-360dp` | — | APROBADA | — (ver nota 2) |
| 24 | Nutrition / 07 Food Confirmation | `nutrition-07-411dp` | `nutrition-07-360dp` | 360 | APROBADA | — |
| 25 | Nutrition / 08 Meal Details | `nutrition-08-411dp` | `nutrition-08-360dp` | — | APROBADA | Diferencias anotadas (nota 4) |
| 26 | Progress / 01 Today's Overview | `progress-01-411dp` | `progress-01-360dp` | 411 y 360 | APROBADA | — |
| 27 | Progress / 02 Dashboard | `progress-02-411dp` | `progress-02-360dp` | 411 y 360 | APROBADA | — |
| 28 | Progress / 03 Workout Progress | `progress-03-411dp` | `progress-03-360dp` | 411 y 360 | APROBADA | — |
| 29 | Progress / 04 Nutrition Progress | `progress-04-411dp` | `progress-04-360dp` | 360 | APROBADA | — |
| 30 | Progress / 05 Health Trends | `progress-05-411dp` | `progress-05-360dp` | 360 | APROBADA | — |
| 31 | Progress / 06 Weekly Review | `progress-06-411dp` | `progress-06-360dp` | 411 y 360 | APROBADA | — |
| 32 | Profile / 01 Dashboard | `profile-01-411dp` | `profile-01-360dp` | 411 y 360 | APROBADA | — |
| 33 | Profile / 02 Edit Profile | `profile-02-411dp` | `profile-02-360dp` | 360 | APROBADA | Corregida (ver nota 5) |
| 34 | Profile / 03 Goals & Preferences | `profile-03-411dp` | `profile-03-360dp` | 411 y 360 | APROBADA | — |
| 35 | Profile / 04 Nutrition Preferences | `profile-04-411dp` | `profile-04-360dp` | 411 y 360 | APROBADA | — |
| 36 | Profile / 05 Voice & AI Settings | `profile-05-411dp` | `profile-05-360dp` | 411 y 360 | APROBADA | — |
| 37 | Profile / 06 Settings | `profile-06-411dp` | `profile-06-360dp` | 411 y 360 | APROBADA | — |

Resultado: las 37 pantallas están APROBADA. Login 01 y 02 y Edit Profile pasaron en la segunda vuelta del revisor, después de sus correcciones. UI03 se cumple en las 37: a 360 dp no queda contenido inalcanzable, y lo que no entra se alcanza con scroll.

## Notas

1. **Login 01 y 02 (corregida):** en la primera vuelta, al bajar, el contenido pasaba por debajo de la barra de estado transparente. En SignUp, la flecha de volver quedaba sobre la hora y el selector "EN" sobre la batería. La causa estaba en `VoraAccessScaffold` (`ui/components/VoraAccess.kt`): ponía `statusBarsPadding()` y `navigationBarsPadding()` adentro del `verticalScroll`, así que el margen se iba con el scroll. Ahora esos márgenes van afuera del scroll. El fondo sigue pintado hasta el borde, y lo que se desplaza queda entre las dos barras.
2. **Photo Analysis:** el revisor marcó que las etiquetas "Grilled Chicken" y "Basmati Rice" parecen cruzadas sobre la foto. Zaki comparó el frame en Figma y las etiquetas están igual que en el diseño, así que no hay nada que corregir.
3. **Workout Dashboard:** el Figma muestra "Focus: Chest & Back" en la tarjeta del entrenamiento recomendado. El catálogo de la API (`/v1/workouts`) no trae los grupos musculares, que solo vienen en el detalle (`/v1/workouts/{id}`), así que el focus se muestra en Workout Details (13). Agregarlo en el Dashboard sería una tarea del carril C: otra consulta desde `WorkoutsViewModel`.
4. **Today's Meals y Meal Details:** el Figma lista cada alimento con sus gramos y sus kcal. La app muestra una sola fila por comida, porque la comida se guarda sin separar los alimentos. "Delete Meal" (25) no está en el Figma: es un agregado por RF04.
5. **Edit Profile (corregida):** después del PR #57, Height y Weight mostraban "178" y "76", sin unidad. En el Figma dicen "178 cm" y "76 kg". Ahora `NumberField` (`EditProfileScreen.kt`) dibuja la unidad como sufijo visual, con los textos `edit_unit_cm` y `edit_unit_kg`. El valor que se edita sigue siendo solo el número.

## Diferencias menores que se aceptan

- SignIn: desde el PR #57 hay unos 13 dp de más entre Password y "Sign In". Es el lugar que queda reservado para el error de Firebase.

- Exercise Details: hay un solo paso y la foto es fija, porque eso es lo que trae el mock.
- Los datos de ejemplo (calorías, fechas fijas de Progress, nombres en castellano del mock) no se cuentan como diferencias.
