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
val VoraOrbHalo = Color(0x739AA39F)         // texto tenue al 45 %: halo del orbe sobre fondo claro

// Sobre paneles verdes
val VoraOnDarkMuted = Color(0xB3FFFFFF)     // blanco al 70 %: datos secundarios
val VoraOnDarkBorder = Color(0x4DFFFFFF)    // blanco al 30 %: borde de "Details"
val VoraOnDarkSubtle = Color(0x1FFFFFFF)    // blanco al 12 %: fondo de "MODERATE" y de los - y +
