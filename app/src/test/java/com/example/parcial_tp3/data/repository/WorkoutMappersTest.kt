package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.remote.dto.WorkoutDto
import com.example.parcial_tp3.domain.model.Workout
import com.example.parcial_tp3.domain.model.WorkoutLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * Conversion del DTO del catalogo al modelo Workout (RF06). Es logica pura: corre sin emulador.
 * Spec: specs/funcionales/catalogo-de-entrenamientos.md.
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
}
