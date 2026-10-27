package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.entity.CoachMessageEntity
import com.example.parcial_tp3.data.remote.dto.CoachReplyDto
import com.example.parcial_tp3.data.remote.dto.SuggestionDto
import com.example.parcial_tp3.domain.model.CoachRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test

/** DTO, modelo y Entity del coach estan separados (RF06). Spec: coach-con-historial.md. */
class CoachMappersTest {

    @Test
    fun `C4 una sugerencia completa pasa a modelo`() {
        val dto = SuggestionDto(id = "s01", text = "¿Qué puedo cenar?")

        val modelo = dto.toDomain()

        assertEquals("s01", modelo.id)
        assertEquals("¿Qué puedo cenar?", modelo.text)
    }

    @Test
    fun `C4 una sugerencia sin texto es dato invalido`() {
        assertThrows(Exception::class.java) { SuggestionDto(id = "s01", text = null).toDomain() }
    }

    @Test
    fun `C10 la respuesta pasa a modelo con su topic`() {
        val dto = CoachReplyDto("assistant", "Abrí el detalle...", "workout", isSimulated = true)

        val modelo = dto.toDomain(createdAt = 1000)

        assertEquals(CoachRole.Assistant, modelo.role)
        assertEquals("workout", modelo.topic)
        assertEquals(1000, modelo.createdAt)
    }

    @Test
    fun `C10 si isSimulated no viene se toma como false`() {
        val dto = CoachReplyDto("assistant", "texto", "general", isSimulated = null)

        assertFalse(dto.toDomain(createdAt = 1).isSimulated)
    }

    @Test
    fun `C14 un rol desconocido es dato invalido`() {
        assertThrows(Exception::class.java) { "pirata".toCoachRole() }
    }

    @Test
    fun `C14 la Entity vuelve a modelo con el topic nulo de la persona`() {
        val fila = CoachMessageEntity(5, "user", "hola", topic = null, isSimulated = false, createdAt = 7)

        val modelo = fila.toDomain()

        assertEquals(CoachRole.User, modelo.role)
        assertEquals(null, modelo.topic)
        assertEquals(5, modelo.id)
    }
}
