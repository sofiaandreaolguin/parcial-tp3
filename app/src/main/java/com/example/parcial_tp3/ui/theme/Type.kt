package com.example.parcial_tp3.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.parcial_tp3.R

/** Manrope en toda la app, con los 4 pesos que usa el Figma (res/font, licencia OFL). */
val ManropeFamily = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
)

private fun manrope(size: Int, weight: FontWeight, lineHeight: Int? = null) = TextStyle(
    fontFamily = ManropeFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = (lineHeight ?: (size + 6)).sp,
    letterSpacing = 0.sp,
)

/**
 * La jerarquia de la lamina "02 Tipografia" repartida en los roles de Material 3.
 * Se elige el estilo por su funcion, no por su tamano.
 *
 * | Lamina                         | Rol de M3        | Valor          |
 * |--------------------------------|------------------|----------------|
 * | Cifra destacada (1,420)        | headlineSmall    | 24 SemiBold    |
 * | Titulo de pantalla             | titleLarge       | 20 Bold        |
 * | Titulo de tarjeta              | titleMedium      | 18 SemiBold    |
 * | Accion principal (Start)       | labelLarge       | 14 SemiBold    |
 * | Accion contextual (View...)    | labelMedium      | 13 SemiBold    |
 * | Cuerpo destacado               | bodyLarge        | 15 Regular     |
 * | Cuerpo                         | bodyMedium       | 13 Regular, 18 |
 * | Tiempo (2 hours ago)           | bodySmall        | 11 Regular     |
 * | Etiqueta (TODAY), en mayuscula | labelSmall       | 10 SemiBold    |
 *
 * La etiqueta va en mayusculas en el strings.xml, igual que en el Figma.
 */
val VoraTypography = Typography(
    headlineLarge = manrope(32, FontWeight.Bold),
    headlineMedium = manrope(28, FontWeight.SemiBold),
    headlineSmall = manrope(24, FontWeight.SemiBold),
    titleLarge = manrope(20, FontWeight.Bold),
    titleMedium = manrope(18, FontWeight.SemiBold),
    titleSmall = manrope(16, FontWeight.SemiBold),
    bodyLarge = manrope(15, FontWeight.Normal, lineHeight = 20),
    bodyMedium = manrope(13, FontWeight.Normal, lineHeight = 18),
    bodySmall = manrope(11, FontWeight.Normal, lineHeight = 14),
    labelLarge = manrope(14, FontWeight.SemiBold),
    labelMedium = manrope(13, FontWeight.SemiBold),
    labelSmall = manrope(10, FontWeight.SemiBold, lineHeight = 14),
)
