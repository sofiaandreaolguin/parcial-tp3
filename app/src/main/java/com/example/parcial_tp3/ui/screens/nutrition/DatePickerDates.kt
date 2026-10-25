package com.example.parcial_tp3.ui.screens.nutrition

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

// El DatePicker de Material 3 trabaja en millis de la medianoche UTC. Con la zona del teléfono, en
// Argentina el 24 a las 00:00 UTC todavía es el 23 y se elegiría el día anterior.
fun selectedMillisToLocalDate(millis: Long): LocalDate =
    Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()

fun LocalDate.toPickerMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
