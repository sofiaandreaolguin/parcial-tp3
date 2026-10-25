package com.example.parcial_tp3.ui.screens.nutrition

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

/** Las fechas del DatePicker de Material 3, que trabaja en millis UTC. */
class DatePickerDatesTest {

    // 2026-10-24T00:00:00Z
    private val medianocheUtc = 1_792_800_000_000L

    @Test
    fun `C27 los millis del DatePicker son la fecha en UTC`() {
        val fecha = LocalDate.of(2026, 10, 24)

        // En Argentina esos millis todavía son el 23 a las 21 h: igual tiene que dar el 24.
        assertEquals(fecha, selectedMillisToLocalDate(medianocheUtc))
        assertEquals(fecha, selectedMillisToLocalDate(medianocheUtc + 86_399_999L))
        assertEquals(medianocheUtc, fecha.toPickerMillis())
        assertEquals(fecha, selectedMillisToLocalDate(fecha.toPickerMillis()))
    }
}
