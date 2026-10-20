package com.example.parcial_tp3.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Los colores de la lamina repartidos en los roles de Material 3. Asi los componentes de
 * M3 (Switch, TextField, NavigationBar...) salen con los colores de VORA sin tocarlos.
 */
private val VoraColorScheme = lightColorScheme(
    primary = VoraGreen,
    onPrimary = VoraSurface,
    secondary = VoraMint,
    onSecondary = VoraGreen,
    tertiary = VoraLime,
    onTertiary = VoraGreen,
    background = VoraBackground,
    onBackground = VoraTextPrimary,
    surface = VoraSurface,
    onSurface = VoraTextPrimary,
    surfaceVariant = VoraBackground,
    onSurfaceVariant = VoraTextSecondary,
    outline = VoraBorder,
    outlineVariant = VoraBorder,
    error = VoraError,
    onError = VoraSurface,
)

/**
 * Los colores de VORA que Material 3 no tiene como rol propio. Se leen con
 * `VoraTheme.colors`. Sobre lima o menta, el texto va en verde (`onAccent`).
 */
@Immutable
data class VoraExtraColors(
    val cta: Color = VoraLime,
    val onAccent: Color = VoraGreen,
    val mintTranslucent: Color = VoraMintTranslucent,
    val mintBorder: Color = VoraMintBorder,
    val onDarkMuted: Color = VoraOnDarkMuted,
    val onDarkBorder: Color = VoraOnDarkBorder,
    val success: Color = VoraSuccess,
    val textMuted: Color = VoraTextMuted,
)

private val LocalVoraColors = staticCompositionLocalOf { VoraExtraColors() }

/**
 * Tema de VORA. Solo modo claro y sin color dinamico: el Figma no tiene modo oscuro y
 * la fidelidad (UI01) pide los mismos colores en cualquier celular.
 *
 * Mantiene el nombre de la plantilla para no tocar MainActivity (zona de Navegacion).
 */
@Composable
fun Parcialtp3Theme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalVoraColors provides VoraExtraColors()) {
        MaterialTheme(
            colorScheme = VoraColorScheme,
            typography = VoraTypography,
            shapes = VoraShapes,
            content = content,
        )
    }
}

/**
 * Acceso a lo propio de VORA: `VoraTheme.colors`, `VoraTheme.spacing`, `VoraTheme.radius`
 * y `VoraTheme.sizes`.
 */
object VoraTheme {
    val colors: VoraExtraColors
        @Composable @ReadOnlyComposable
        get() = LocalVoraColors.current

    val spacing = VoraSpacing
    val radius = VoraRadius
    val sizes = VoraSizes
}
