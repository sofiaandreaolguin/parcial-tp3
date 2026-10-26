package com.example.parcial_tp3.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class DisplayNameTest {

    @Test
    fun `C42 nombre y apellido se unen sin espacios sobrantes`() {
        assertEquals("Ana López", displayName("Ana", "López", "ana@mail.com"))
        assertEquals("Ana López", displayName(" Ana ", " López ", "ana@mail.com"))
    }

    @Test
    fun `C42 solo nombre o solo apellido no deja espacio al final ni al principio`() {
        assertEquals("Ana", displayName("Ana", "", "ana@mail.com"))
        assertEquals("López", displayName("", "López", "ana@mail.com"))
    }

    @Test
    fun `C42 sin nombre ni apellido usa la parte del email antes de la arroba`() {
        assertEquals("ana.lopez", displayName("", "", "ana.lopez@mail.com"))
        assertEquals("ana", displayName("  ", " ", "ana@mail.com"))
    }

    @Test
    fun `C42 sin nombre, apellido ni email queda vacio`() {
        assertEquals("", displayName("", "", ""))
        assertEquals("", displayName("", "", "@mail.com"))
    }
}
