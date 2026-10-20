package com.example.parcial_tp3.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp

/**
 * Espaciados de la lamina "03 Espaciado y radios". Son los unicos valores que usa la app:
 * no se impone una grilla de 4, se reutiliza el valor del patron mas cercano.
 *
 * Se leen con VoraTheme.spacing (por ejemplo `VoraTheme.spacing.screen` para el margen).
 */
@Immutable
object VoraSpacing {
    /** Entre titulo y subtitulo de una fila de comida ("gap de texto 2" en la lamina). */
    val s2 = 2.dp
    val s4 = 4.dp
    val s6 = 6.dp
    val s8 = 8.dp
    val s10 = 10.dp
    val s12 = 12.dp
    val s14 = 14.dp
    val s16 = 16.dp
    val s20 = 20.dp
    val s24 = 24.dp
    val s32 = 32.dp
    val s40 = 40.dp

    /** Margen lateral de todas las pantallas. */
    val screen = s20
}

/**
 * Radios de la lamina: 12 en campos y filas, 16 en tarjetas, 20 a 24 en paneles
 * destacados y 100 (pildora) en botones, chips y etiquetas.
 */
@Immutable
object VoraRadius {
    val r8 = 8.dp
    val r12 = 12.dp
    val r16 = 16.dp
    val r18 = 18.dp
    val r20 = 20.dp
    val r24 = 24.dp
    val pill = 100.dp
}

/** Medidas fijas de los componentes que no son espaciado ni radio. */
@Immutable
object VoraSizes {
    /** Borde de campos y tarjetas, y separador superior de la barra inferior. */
    val border = 1.dp
    /** Iconos de navegacion (barra inferior y encabezado), trazo de 2. */
    val icon = 20.dp
    /** Area tactil minima de un boton de icono. */
    val touchTarget = 48.dp
    /** Borde de una tarjeta de opcion elegida (menta). */
    val borderSelected = 2.dp
    /** Circulo blanco de volver en el encabezado grande (Profile, Settings). */
    val backCircle = 32.dp
    /** Circulo del icono de una fila de Settings. */
    val rowIconCircle = 36.dp
    /** Circulo de check de una tarjeta de opcion. */
    val checkCircle = 20.dp
}

/** Las formas de Material 3 apuntan a los mismos radios, asi los componentes de M3 coinciden. */
val VoraShapes = Shapes(
    extraSmall = RoundedCornerShape(VoraRadius.r8),
    small = RoundedCornerShape(VoraRadius.r12),
    medium = RoundedCornerShape(VoraRadius.r16),
    large = RoundedCornerShape(VoraRadius.r20),
    extraLarge = RoundedCornerShape(VoraRadius.r24),
)
