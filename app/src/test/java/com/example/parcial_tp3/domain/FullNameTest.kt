package com.example.parcial_tp3.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class FullNameTest {

    @Test
    fun `C3 nombre completo se separa en nombre y apellido`() {
        assertEquals("Ana" to "", splitFullName("Ana"))
        assertEquals("Ana" to "María López", splitFullName("Ana María López"))
        assertEquals("Ana" to "López", splitFullName("  Ana \t  López  "))
        assertEquals("Ana" to "María López", splitFullName("Ana\tMaría López"))
        assertEquals("" to "", splitFullName("   "))
    }
}
