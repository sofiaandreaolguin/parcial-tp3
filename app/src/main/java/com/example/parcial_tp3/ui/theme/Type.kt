package com.example.parcial_tp3.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
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

/**
 * Los estilos de los frames de Voice que no caen en ningun rol de M3. Se leen con
 * `VoraTheme.text`. Salen medidos de los nodos del Figma, no estimados.
 *
 * No se tocan los roles de la tabla de arriba: el resto de la app sigue eligiendo por
 * funcion, y estos son el caso puntual de un flujo con su propia escala.
 */
/** El texto ocupa toda su caja de interlineado, como en el Figma. */
private val SIN_RECORTE = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None)

@Immutable
object VoraTextStyles {
    /** "How can I help?" de Voice / 01. */
    val heroTitle = manrope(26, FontWeight.Bold)
    /** "Sorry, I didn't catch that" de Voice / 06. */
    val alertTitle = manrope(22, FontWeight.Bold)
    /** Las bajadas centradas de Voice / 01 y 06. */
    val heroSubtitle = manrope(14, FontWeight.Normal, lineHeight = 19)
    /** "TAP TO TALK" y los titulos del encabezado de Voice. */
    val action = manrope(14, FontWeight.SemiBold)
    /** La cita de "LAST CONVERSATION": mas pesada que el cuerpo. */
    val quote = manrope(13, FontWeight.Medium)
    /** Las etiquetas de Voice: 11, un punto mas que labelSmall. */
    val label = manrope(11, FontWeight.SemiBold)
    /** La hora y los datos sueltos de 11 sin peso. */
    val labelPlain = manrope(11, FontWeight.Normal)
    /** "Listen to response (0:14)" y los rotulos de 12 de Voice. */
    val labelAudio = manrope(12, FontWeight.SemiBold)

    /** El cuerpo de los paneles sobre verde. */
    val panelBody = manrope(18, FontWeight.Medium, lineHeight = 27)
    val panelBodySmall = manrope(16, FontWeight.Medium, lineHeight = 24)
    val panelQuote = manrope(15, FontWeight.Normal, lineHeight = 23)

    /** Etiqueta de los campos ("FIRST NAME"): 12, medida en Profile / 02 (67 dp de ancho). */
    val fieldLabel = manrope(12, FontWeight.SemiBold)
    /** Nota al pie de Edit profile ("Your information is used..."): 12 Regular. */
    val footnote = manrope(12, FontWeight.Normal)

    // Acceso (Login - 01 y 02), medidos en docs/figma/Login Flow.png contra una captura: los
    // textos de Login van un punto abajo de la lamina (24 en vez de 26, 14 en vez de 15...).
    /** "VORA Coach": 24 Bold apretado (138 dp de ancho en el frame). */
    val accessTitle = manrope(24, FontWeight.Bold).copy(letterSpacing = (-0.3).sp)
    /** Bajada, texto de los campos y "Don't have an account?": 14 Regular. */
    val accessBody = manrope(14, FontWeight.Normal, lineHeight = 20)
    /** "ADAPTIVE INTELLIGENCE": 12 SemiBold espaciado (166 dp de ancho en el frame). */
    val accessTagline = manrope(12, FontWeight.SemiBold).copy(letterSpacing = 1.2.sp)
    /** "Sign In →": 16 Bold en 24 (nodo "Sign In" del Figma, 53 x 24). */
    val accessCta = manrope(16, FontWeight.Bold, lineHeight = 24)
    /** "Sign up here": como la bajada, en negrita y subrayado. */
    val accessBodyLink = accessBody.copy(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
    /** Los 12 puntos del placeholder de la contrasena, separados como en el frame. */
    val accessPasswordDots = accessBody.copy(letterSpacing = 3.sp)
    // Login - 02 (SignUp): otra escala, leida de los nodos del frame 4002:2. Las etiquetas, los
    // campos y la nota van sin el recorte de interlineado que Compose aplica por omision
    // (Trim.Both): con el recorte, un 16 en 24 ocupa 22 y cada etiqueta quedaba 2 dp mas corta
    // que su caja del Figma (medido). El titulo y la frase lo conservan: ver VoraBrandHeader.
    /** "VORA Coach" de SignUp: 28 Bold en 28 (163 dp de ancho). */
    val accessTitleLarge = manrope(28, FontWeight.Bold, lineHeight = 28).copy(letterSpacing = (-0.3).sp)
    /** "ADAPTIVE INTELLIGENCE" de SignUp: un poco mas abierto (171 dp de ancho). */
    val accessTaglineWide = accessTagline.copy(letterSpacing = 1.4.sp, lineHeight = 17.sp)
    /** Etiquetas de SignUp ("Full Name") y los textos de abajo de la tarjeta: 16 Regular en 24. */
    val accessLabelLarge = manrope(16, FontWeight.Normal, lineHeight = 24).copy(lineHeightStyle = SIN_RECORTE)
    /** Texto de los campos de SignUp: 16 Regular en 22. */
    val accessInputLarge = manrope(16, FontWeight.Normal, lineHeight = 22).copy(lineHeightStyle = SIN_RECORTE)
    /** "Recover": como accessLabelLarge, en negrita y subrayado. */
    val accessLabelLargeLink = accessLabelLarge.copy(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
    /** "8+ secure characters": 12 en 17. */
    val accessNote = manrope(12, FontWeight.Normal, lineHeight = 17).copy(lineHeightStyle = SIN_RECORTE)
    /** "Password help?": accessNote subrayado. */
    val accessNoteLink = accessNote.copy(textDecoration = TextDecoration.Underline)
    /** "Forgot your password?": 12 Medium. */
    val accessLink = manrope(12, FontWeight.Medium)
    /** "AI" de la insignia del logo. */
    val accessBadge = manrope(10, FontWeight.Bold, lineHeight = 12)
}
