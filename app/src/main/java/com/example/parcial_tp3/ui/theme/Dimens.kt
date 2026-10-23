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
    /** Punta de las barras de los graficos (medido en Weekly Steps Trend: 8 px a 2x). */
    val r4 = 4.dp
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
    /** Microfono grande de Voice / 01 y 02. */
    val iconMic = 28.dp
    /** Enviar, repetir, stop y el microfono chico del aviso. */
    val iconSmall = 18.dp
    /** Escuchar la respuesta, dentro de una burbuja. */
    val iconTiny = 10.dp
    /** La onda de voz del centro del orbe. */
    val iconOrb = 32.dp
    /** El recargar del centro del orbe apagado (Voice / 06). */
    val iconOrbMuted = 34.dp
    /** Area tactil minima de un boton de icono. */
    val touchTarget = 48.dp
    /** Borde de una tarjeta de opcion elegida (menta). */
    val borderSelected = 2.dp
    /** Circulo blanco de volver en el encabezado grande (Profile, Settings). */
    val backCircle = 32.dp
    /** Alto del encabezado de Voice: 12 + el circulo de 32 + 12. */
    val topBar = 56.dp
    /** Circulo del icono de una fila de Settings. */
    val rowIconCircle = 36.dp
    /** Circulo de check de una tarjeta de opcion. */
    val checkCircle = 20.dp

    // Indicadores (lamina 04: "Anillo 72 px · barras 4 px · senal 8 px")
    val ring = 72.dp
    val ringStroke = 6.dp
    val progressBar = 4.dp
    val statusDot = 8.dp
    /** Divisor vertical entre columnas de una fila de estadisticas. */
    val statDivider = 38.dp
    /** Botones redondos de - y + del selector (Active Workout). */
    val stepperButton = 36.dp
    /*
     * Orbe de VORA. Cada frame de Voice lo dibuja de un tamano distinto, pero siempre con
     * las mismas proporciones (aro al 90 % del diametro y esfera al 75 %), asi que alcanza
     * con pasarle el tamano.
     */
    /** Idle y Speaking. */
    val orb = 140.dp
    /** Listening: es el mas grande de los seis. */
    val orbListening = 170.dp
    /** Processing. */
    val orbProcessing = 150.dp
    /** El apagado del error (Voice / 06). */
    val orbMuted = 120.dp
    /** Esfera chica de VORA: avatar de Conversation y de las tarjetas "VORA Insight". */
    val orbSmall = 28.dp
    /** Orbe de las notas del coach: 20 en "VORA RECOMMENDS" (Workout / 01), 16 en 03 y 07. */
    val orbNoteLarge = 20.dp
    val orbNote = 16.dp

    /** "This Week's Activity" de Workout / 01: una columna de 36 por dia con un circulo de 28. */
    val dayColumn = 36.dp
    val dayCircle = 28.dp
    /** Sombra del panel verde de Workout / 01: 0 8 8, desenfoque y corrimiento hacia abajo de 8. */
    val panelShadow = 8.dp

    // Botones redondos de icono, medidos en los frames de Voice
    /** Microfono de Listening, el mas grande. */
    val micCircleActive = 80.dp
    /** Microfono de "TAP TO TALK" en Idle. */
    val micCircle = 72.dp
    /** Pausa de Speaking: mas grande que repetir y stop, que van de circleButton. */
    val pauseCircle = 56.dp
    /** El + y el enviar de la barra de escribir, y los controles de audio. */
    val circleButton = 40.dp
    /** Circulo del icono de un aviso (el microfono de Voice / 06). */
    val iconCircle = 36.dp
    /** Escuchar la respuesta, dentro de una burbuja de Conversation. */
    val playCircle = 24.dp
    /** Alto del campo de la barra de escribir (Voice / 05). */
    val composerField = 44.dp
    /** Anillo de carga de Voice / 03 Processing. */
    val spinner = 16.dp
    /** Barras del grafico: 24 en Training Volume, 18 en Weekly Steps Trend. */
    val barWidth = 24.dp
    val barWidthNarrow = 18.dp
    val chartHeight = 72.dp
    /** Interruptor de Voice & AI Settings: pista de 44 x 24 y perilla de 20. */
    val toggleWidth = 44.dp
    val toggleHeight = 24.dp
    val toggleThumb = 20.dp
    /** Foto de perfil de Profile / 01 Dashboard. */
    val avatar = 72.dp
    /** Foto de Profile / 02 Edit profile, mas grande que en el Dashboard (160 px a 2x). */
    val avatarLarge = 80.dp

    // Home / 01 (nodos del frame 2007:3)
    /** Foto del saludo y campana: 40. */
    val avatarSmall = 40.dp
    /** Orbe del panel del coach. */
    val orbHome = 36.dp
    /** Anillo "Daily Goal": 64 con trazo de 5 (medido a 2x). */
    val ringSmall = 64.dp
    val ringStrokeSmall = 5.dp
    /** Barras de proteinas y carbohidratos: 6 (la lamina dibuja 4). */
    val progressBarThick = 6.dp
    /** Boton "Start voice session": 43 de alto. */
    val homeVoiceCta = 43.dp
    /** Iconos de 16: microfono, destello y calendario. */
    val iconXSmall = 16.dp

    // Acceso (Login - 01 y 02), medidos en docs/figma/Login Flow.png
    /** Circulo blanco de volver y selector de idioma: 40 y 30 de alto (nodos del Figma). */
    val accessBack = 40.dp
    val languagePill = 30.dp
    /** Chevron del idioma (14), y escudo y flecha de "Sign In" (16). */
    val accessChevron = 14.dp
    val accessIconSmall = 16.dp
    /** Logo: aro blanco de 72 (5 de ancho) e insignia "AI" de 28 x 20. */
    val accessLogo = 72.dp
    val accessLogoRing = 5.dp
    val accessBadgeWidth = 28.dp
    val accessBadgeHeight = 20.dp
    /** Resplandor del fondo ("Top Decorative Ambient Glow", circulo de 320 desenfocado): centro a
     *  64 dp del borde de arriba; el radio de 260 incluye el desenfoque. */
    val accessGlowY = 64.dp
    val accessGlowRadius = 260.dp
    /** Flecha de volver: en el frame mide 8 x 13 (el vector de la lamina es de 7 x 10). */
    val accessBackIconWidth = 10.dp
    val accessBackIconHeight = 14.dp
    /** Sombras: suave en el circulo de volver y el logo, lima debajo de "Sign In". */
    val accessShadow = 4.dp
    val accessShadowSoft = 2.dp
    val accessShadowLogo = 8.dp
    val accessShadowCta = 12.dp
    /** Ancho de la bajada: corta en "progress plan / in one place." como el frame. */
    val accessSubtitle = 276.dp
    /** SignUp: orbe de 56, campos de 52, boton principal y sociales de 48, tilde de 13. */
    val accessLogoSmall = 56.dp
    /** Insignia "AI" del orbe de SignUp: arranca en (33, 39) del orbe y sobresale. */
    val accessBadgeOffsetX = 33.dp
    val accessBadgeOffsetY = 39.dp
    val accessFieldOutlined = 52.dp
    val accessCtaCompact = 48.dp
    val socialButtonLarge = 48.dp
    val accessCheck = 13.dp
    /** Flecha de "Sign Up" y escudo de "Biometric sign-in": 14. */
    val accessIconTiny = 14.dp
    /** Campos de 55 de alto, boton principal de 56 y botones sociales de 46. */
    val accessField = 55.dp
    val accessCta = 56.dp
    val socialButton = 46.dp
}

/** Las formas de Material 3 apuntan a los mismos radios, asi los componentes de M3 coinciden. */
val VoraShapes = Shapes(
    extraSmall = RoundedCornerShape(VoraRadius.r8),
    small = RoundedCornerShape(VoraRadius.r12),
    medium = RoundedCornerShape(VoraRadius.r16),
    large = RoundedCornerShape(VoraRadius.r20),
    extraLarge = RoundedCornerShape(VoraRadius.r24),
)
