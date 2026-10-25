package com.example.parcial_tp3.ui.screens.nutrition

import java.time.LocalDate

// El DatePicker de Material 3 trabaja en millis UTC. Firmas vacias de la tarea VC-l: las
// completa la sesion de implementar.
@Suppress("UNUSED_PARAMETER")
fun selectedMillisToLocalDate(millis: Long): LocalDate = LocalDate.EPOCH

fun LocalDate.toPickerMillis(): Long = 0L
