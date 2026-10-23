package com.example.parcial_tp3.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * Paleta de VORA, tal cual la lamina "Sistema de diseno — VORA" del Figma (01 Colores).
 * Ninguna pantalla usa estos valores directo: se leen con MaterialTheme.colorScheme o,
 * los que Material 3 no tiene, con VoraTheme.colors.
 */

// Identidad y acentos
val VoraGreen = Color(0xFF123D35)      // acciones, paneles, navegacion activa
val VoraMint = Color(0xFF65D6B0)       // coach, voz, carbohidratos
val VoraLime = Color(0xFFC9E86A)       // CTA destacado, grasas

// Superficies y contenido
val VoraSurface = Color(0xFFFFFFFF)    // tarjetas y campos
val VoraBackground = Color(0xFFF7F8F5) // fondo de pantalla (si cambia, tambien en res/values/colors.xml)
val VoraBorder = Color(0xFFE4E9E6)     // separadores y bordes

val VoraTextPrimary = Color(0xFF17211F)
val VoraTextSecondary = Color(0xFF68736F)
val VoraTextMuted = Color(0xFF9AA39F)

// Senales y transparencia
val VoraError = Color(0xFFD95D5D)      // error y atencion ("NOT STARTED")
val VoraSuccess = Color(0xFF27AE60)
val VoraMintTranslucent = Color(0x1F65D6B0) // menta al 12 %: avisos y apoyo
val VoraMintBorder = Color(0x6665D6B0)      // menta al 40 %: borde de los avisos
val VoraMintBorderSoft = Color(0x4065D6B0)  // menta al 25 %: borde de "VORA RECOMMENDS" (Workout / 01)
val VoraPanelShadow = Color(0x0F123D35)     // verde al 6 %: sombra del panel de Workout / 01
val VoraOrbHalo = Color(0x739AA39F)         // texto tenue al 45 %: halo del orbe sobre fondo claro
val VoraOrbMutedCore = Color(0xFF4A5250)    // esfera del orbe apagado (Voice / 06 Error)
val VoraNoticeIcon = Color(0xFFFCDEDE)      // rosa del circulo del icono de un aviso (Voice / 06 Error)

// Sobre paneles verdes
val VoraOnDarkMuted = Color(0xB3FFFFFF)     // blanco al 70 %: datos secundarios
val VoraOnDarkBorder = Color(0x4DFFFFFF)    // blanco al 30 %: borde de "Details"
val VoraOnDarkSubtle = Color(0x1FFFFFFF)    // blanco al 12 %: fondo de "MODERATE" y de los - y +
val VoraOnDarkChip = Color(0x14FFFFFF)      // blanco al 8 %: fondo de "I'm tired today" (Workout / 02, nodo 1:396)
val VoraOnDarkPanel = Color(0x0DFFFFFF)     // blanco al 5 %: fondo de los paneles de Voice sobre el verde
val VoraOnDarkPanelBorder = Color(0x1AFFFFFF) // blanco al 10 %: borde de esos paneles

// Acceso (Login Flow). Los frames de Login no usan la lamina: medidos en
// docs/figma/Login Flow.png con scripts/color.py (22-oct).
val VoraAccessGlow = Color(0xFFE3F6ED)         // resplandor menta de arriba
val VoraAccessBackground = Color(0xFFF3F9F5)   // fondo de la pantalla
val VoraAccessInk = Color(0xFF0E382F)          // titulo, etiquetas, iconos e insignia AI
val VoraAccessAccent = Color(0xFF357E69)       // "ADAPTIVE INTELLIGENCE" y "Forgot your password?"
val VoraAccessText = Color(0xFF6B8780)         // subtitulos, "EN" y "Sign up here"
val VoraAccessHint = Color(0xFF9BAFA9)         // placeholder e iconos de los campos
val VoraAccessField = Color(0xFFF8FBF9)        // fondo de los campos
val VoraAccessBorder = Color(0xFFE2EDE7)       // borde de tarjeta, campos, botones sociales y separador
val VoraAccessCta = Color(0xFFCCE65E)          // "Sign In →": otro lima que el de la lamina
val VoraAccessBiometric = Color(0xFFECFDF5)    // fondo del boton de biometria
val VoraAccessBiometricBorder = Color(0xFFA7F3D0)
val VoraAccessBiometricIcon = Color(0xFF065F46)
val VoraAccessBadgeText = Color(0xFF6EE7B7)    // "AI" de la insignia
val VoraAccessOrbLight = Color(0xFF8DEABE)     // esfera del logo: brillo
val VoraAccessOrbMid = Color(0xFF3CD59A)
val VoraAccessOrbDark = Color(0xFF238B68)      // esfera del logo: borde inferior derecho
val VoraAccessOrbHighlight = Color(0x4DFFFFFF) // brillo del centro del logo: blanco al 30 %
val VoraAccessShield = Color(0xFF059669)       // escudo de "End-to-end encrypted..."

// Login - 02 (SignUp) tiene su propia variante, medida igual (frame 4002:2)
val VoraAccessOutline = Color(0xFF6B7280)      // borde y placeholder de los campos de SignUp
val VoraAccessTaglineDeep = Color(0xFF065F46)  // "ADAPTIVE INTELLIGENCE" de SignUp
val VoraAccessOrbLime = Color(0xFF98DF72)      // orbe de SignUp: arriba a la derecha
val VoraAccessOrbMint = Color(0xFF47D592)
val VoraAccessOrbDeep = Color(0xFF1A6A51)      // orbe de SignUp: abajo a la izquierda
