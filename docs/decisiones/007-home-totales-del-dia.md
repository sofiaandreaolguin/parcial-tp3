# 007 · Home: totales del día sumados en Kotlin y datos de demostración

**Fecha:** 22/10/2026 · **Decidió:** Martu · **Estado:** aplicado en Home / 01

## Contexto

Home / 01 muestra las calorías, proteínas y carbohidratos del día. La tarea pedía que fueran
reales (Room) y que el resto de la pantalla usara datos de demostración con la forma de los
modelos. `MealRepository` hoy expone `observeMealsByDate(fecha)`, que devuelve la lista de
comidas del día como `Flow`.

## Decisión

- `HomeViewModel` combina el perfil de demostración y `observeMealsByDate(hoy)`, y **suma los
  totales en Kotlin** (`sumOf`). Al guardar una comida en Nutrition, el anillo y la tarjeta se
  actualizan solos porque el DAO emite de nuevo.
- Objetivos diarios (2100 kcal, 140 g, 220 g), pasos, distancia, kcal activas y el
  entrenamiento recomendado viven en `HomeDemoData`. El recomendado es un `Workout` con id
  `w01`, que existe en la VORA Mock API, así "Start workout" abre un detalle de verdad.

## Por qué

- No hace falta tocar el DAO ni el repositorio de Sofi (zona de la base Room) para una
  pantalla de demostración: alcanza con lo que ya existe.
- Los datos de demostración con la forma del modelo permiten conectar el recomendado a la caché
  de entrenamientos (tarea j) sin cambiar la pantalla.

## Mejora posible (pregunta 5 del cuestionario)

- **Con muchas comidas por día**, traer la lista entera para sumarla es trabajo de más: conviene
  una consulta con `SUM(kcal)`, `SUM(proteinG)` y `SUM(carbsG)` en `MealDao`, filtrada por fecha
  e idealmente con un índice en la columna de fecha.
- La fecha de "hoy" se toma al crear el ViewModel: si la app queda abierta pasada la medianoche,
  sigue mostrando el día anterior hasta que se vuelve a crear.
