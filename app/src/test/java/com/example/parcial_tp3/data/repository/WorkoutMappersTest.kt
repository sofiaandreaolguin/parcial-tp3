package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.entity.WorkoutEntity
import com.example.parcial_tp3.data.remote.dto.ExerciseDto
import com.example.parcial_tp3.data.remote.dto.WorkoutDetailDto
import com.example.parcial_tp3.data.remote.dto.WorkoutDto
import com.example.parcial_tp3.domain.model.Exercise
import com.example.parcial_tp3.domain.model.Workout
import com.example.parcial_tp3.domain.model.WorkoutDetail
import com.example.parcial_tp3.domain.model.WorkoutLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * Conversion de los DTO y de las Entity de entrenamientos a los modelos (RF06). Es logica pura:
 * corre sin emulador. Specs: specs/funcionales/catalogo-de-entrenamientos.md y
 * specs/funcionales/detalle-y-cache-de-entrenamientos.md (los "cache").
 */
class WorkoutMappersTest {

    private val dtoBase = WorkoutDto(
        id = "w01",
        name = "Fuerza inicial",
        level = "beginner",
        durationMinutes = 25,
        exerciseCount = 3,
    )

    @Test
    fun `C3 catalogo toDomain conserva los campos y traduce los tres niveles`() {
        assertEquals(
            Workout(
                id = "w01",
                name = "Fuerza inicial",
                level = WorkoutLevel.BEGINNER,
                durationMinutes = 25,
                exerciseCount = 3,
            ),
            dtoBase.toDomain(),
        )
        assertEquals(
            Workout(
                id = "w02",
                name = "Entrenamiento completo",
                level = WorkoutLevel.INTERMEDIATE,
                durationMinutes = 35,
                exerciseCount = 4,
            ),
            WorkoutDto(
                id = "w02",
                name = "Entrenamiento completo",
                level = "intermediate",
                durationMinutes = 35,
                exerciseCount = 4,
            ).toDomain(),
        )
        assertEquals(WorkoutLevel.ADVANCED, dtoBase.copy(level = "advanced").toDomain().level)
    }

    @Test
    fun `C4 catalogo cada campo nulo del DTO lanza IllegalArgumentException`() {
        assertThrows("id nulo", IllegalArgumentException::class.java) {
            dtoBase.copy(id = null).toDomain()
        }
        assertThrows("name nulo", IllegalArgumentException::class.java) {
            dtoBase.copy(name = null).toDomain()
        }
        assertThrows("level nulo", IllegalArgumentException::class.java) {
            dtoBase.copy(level = null).toDomain()
        }
        assertThrows("durationMinutes nulo", IllegalArgumentException::class.java) {
            dtoBase.copy(durationMinutes = null).toDomain()
        }
        assertThrows("exerciseCount nulo", IllegalArgumentException::class.java) {
            dtoBase.copy(exerciseCount = null).toDomain()
        }
    }

    @Test
    fun `C5 catalogo un nivel desconocido o con mayusculas lanza IllegalArgumentException`() {
        assertThrows("nivel pro", IllegalArgumentException::class.java) {
            dtoBase.copy(level = "pro").toDomain()
        }
        assertThrows("nivel Beginner con mayuscula", IllegalArgumentException::class.java) {
            dtoBase.copy(level = "Beginner").toDomain()
        }
    }

    // Detalle de w01 de la seccion "Datos" de specs/funcionales/detalle-y-cache-de-entrenamientos.md
    private val sentadillaDto = ExerciseDto(
        id = "e01", name = "Sentadilla", muscleGroup = "Piernas", sets = 3, reps = 10, restSeconds = 60,
        instructions = "Separá los pies y flexioná las rodillas con control.",
        tip = "Usá una carga que puedas controlar.",
    )
    private val remoDto = ExerciseDto(
        id = "e02", name = "Remo con mancuerna", muscleGroup = "Espalda", sets = 3, reps = 10, restSeconds = 60,
        instructions = "Apoyá una mano y llevá la mancuerna hacia el torso.",
        tip = "Usá una carga que puedas controlar.",
    )
    private val pressDto = ExerciseDto(
        id = "e03", name = "Press con mancuernas", muscleGroup = "Pecho", sets = 3, reps = 10, restSeconds = 60,
        instructions = "Empujá las mancuernas con un movimiento controlado.",
        tip = "Usá una carga que puedas controlar.",
    )
    private val detalleDto = WorkoutDetailDto(
        id = "w01",
        name = "Fuerza inicial",
        level = "beginner",
        durationMinutes = 25,
        exerciseCount = 3,
        description = "Rutina de ejemplo para el trabajo práctico.",
        exercises = listOf(sentadillaDto, remoDto, pressDto),
    )

    private val detalleW01 = WorkoutDetail(
        id = "w01",
        name = "Fuerza inicial",
        level = WorkoutLevel.BEGINNER,
        durationMinutes = 25,
        exerciseCount = 3,
        description = "Rutina de ejemplo para el trabajo práctico.",
        exercises = listOf(
            Exercise(
                id = "e01", name = "Sentadilla", muscleGroup = "Piernas", sets = 3, reps = 10, restSeconds = 60,
                instructions = "Separá los pies y flexioná las rodillas con control.",
                tip = "Usá una carga que puedas controlar.",
            ),
            Exercise(
                id = "e02", name = "Remo con mancuerna", muscleGroup = "Espalda", sets = 3, reps = 10, restSeconds = 60,
                instructions = "Apoyá una mano y llevá la mancuerna hacia el torso.",
                tip = "Usá una carga que puedas controlar.",
            ),
            Exercise(
                id = "e03", name = "Press con mancuernas", muscleGroup = "Pecho", sets = 3, reps = 10, restSeconds = 60,
                instructions = "Empujá las mancuernas con un movimiento controlado.",
                tip = "Usá una carga que puedas controlar.",
            ),
        ),
    )

    @Test
    fun `C3 cache detalle toDomain conserva los campos y los ejercicios en orden`() {
        assertEquals(detalleW01, detalleDto.toDomain())
    }

    @Test
    fun `C4 cache cada campo nulo del detalle o de un ejercicio lanza IllegalArgumentException`() {
        val detallesConNulo = mapOf(
            "id" to detalleDto.copy(id = null),
            "name" to detalleDto.copy(name = null),
            "level" to detalleDto.copy(level = null),
            "durationMinutes" to detalleDto.copy(durationMinutes = null),
            "exerciseCount" to detalleDto.copy(exerciseCount = null),
            "description" to detalleDto.copy(description = null),
            "exercises" to detalleDto.copy(exercises = null),
        )
        // El ejercicio con el nulo va en el medio: los otros dos son validos
        val ejerciciosConNulo = mapOf(
            "id" to remoDto.copy(id = null),
            "name" to remoDto.copy(name = null),
            "muscleGroup" to remoDto.copy(muscleGroup = null),
            "sets" to remoDto.copy(sets = null),
            "reps" to remoDto.copy(reps = null),
            "restSeconds" to remoDto.copy(restSeconds = null),
            "instructions" to remoDto.copy(instructions = null),
            "tip" to remoDto.copy(tip = null),
        )

        for ((campo, dto) in detallesConNulo) {
            assertThrows("detalle con $campo nulo", IllegalArgumentException::class.java) { dto.toDomain() }
        }
        for ((campo, ejercicio) in ejerciciosConNulo) {
            val dto = detalleDto.copy(exercises = listOf(sentadillaDto, ejercicio, pressDto))
            assertThrows("ejercicio con $campo nulo", IllegalArgumentException::class.java) { dto.toDomain() }
        }
    }

    @Test
    fun `C5 cache un nivel desconocido en el detalle lanza IllegalArgumentException`() {
        assertThrows("nivel pro", IllegalArgumentException::class.java) {
            detalleDto.copy(level = "pro").toDomain()
        }
        assertThrows("nivel Beginner con mayuscula", IllegalArgumentException::class.java) {
            detalleDto.copy(level = "Beginner").toDomain()
        }
    }

    @Test
    fun `C6 cache detalle y resumen van a Entity y vuelven iguales`() {
        val fila = detalleW01.toEntity()
        val ejercicios = detalleW01.exercises.mapIndexed { posicion, ejercicio -> ejercicio.toEntity("w01", posicion) }

        assertEquals(
            WorkoutEntity("w01", "Fuerza inicial", "beginner", 25, 3, "Rutina de ejemplo para el trabajo práctico."),
            fila,
        )
        assertEquals(listOf(0, 1, 2), ejercicios.map { it.position })
        assertEquals(listOf("w01", "w01", "w01"), ejercicios.map { it.workoutId })
        assertEquals(listOf("e01", "e02", "e03"), ejercicios.map { it.exerciseId })
        assertEquals(detalleW01, fila.toDomain(ejercicios))

        val resumen = Workout(
            id = "w02",
            name = "Entrenamiento completo",
            level = WorkoutLevel.INTERMEDIATE,
            durationMinutes = 35,
            exerciseCount = 3,
        )
        val filaDelResumen = resumen.toEntity()

        assertEquals(WorkoutEntity("w02", "Entrenamiento completo", "intermediate", 35, 3, description = null), filaDelResumen)
        assertEquals(resumen, filaDelResumen.toDomain())
    }

    @Test
    fun `C7 cache una fila sin description no es un detalle`() {
        val filaDelCatalogo = WorkoutEntity("w01", "Fuerza inicial", "beginner", 25, 3, description = null)
        val ejercicios = detalleW01.exercises.mapIndexed { posicion, ejercicio -> ejercicio.toEntity("w01", posicion) }

        assertThrows(IllegalArgumentException::class.java) { filaDelCatalogo.toDomain(ejercicios) }
    }
}
