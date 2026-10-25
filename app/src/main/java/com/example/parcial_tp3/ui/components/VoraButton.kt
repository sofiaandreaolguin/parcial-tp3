package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Las apariencias de boton que existen en el Figma (lamina 04, "Botones y acciones").
 * No se agregan variantes que no esten dibujadas.
 */
enum class VoraButtonStyle {
    /** Verde oscuro con texto blanco: "Start workout", "Save changes". */
    Primary,
    /** Lima con texto verde, dentro de paneles oscuros: "Start Workout". */
    Cta,
    /** Menta con texto verde: "Start Workout" de Pre Workout. */
    Mint,
    /** Borde blanco translucido y texto blanco, sobre paneles oscuros: "Details". */
    OutlinedOnDark,
    /** Blanco al 12 % sin borde y texto blanco, sobre paneles oscuros: "Start my workout" de Home. */
    SubtleOnDark,
    /** Fondo blanco con borde y texto oscuro: "Start my workout", "Edit workout". */
    Outlined,
    /** Fondo claro de pantalla, sin borde, texto verde, sobre tarjetas blancas: "Edit profile". */
    Soft,
    /** Borde y texto rojos: "Log out". */
    Destructive,
    /** Solo texto verde SemiBold: "View History". */
    Text,
    /** Texto tenue subrayado: "Type instead". */
    TextMuted,
    /** Texto subrayado sobre fondo verde: "Type instead" de Nutrition / 04. */
    TextOnDark,
    /** Solo texto rojo, sin borde: "Remove" de Nutrition / 07. */
    TextDestructive,
}

/**
 * Chico: 13 SemiBold con padding 8/16 (Home). Mediano: 14 SemiBold con padding 12/16 (panel
 * de Workout / 01). Grande: 15 Bold con padding 14/24 (Edit profile).
 */
enum class VoraButtonSize {
    Small,
    Medium,
    Large,
    /** 13 Medium en 16 con padding 8/12: las acciones del panel de Home (32 de alto). */
    Compact,
}

/**
 * Boton de VORA, siempre en forma de pildora. Sin estado: recibe el texto y avisa el toque.
 *
 * Para que ocupe todo el ancho ("Save preferences"), pasar `Modifier.fillMaxWidth()`.
 *
 * @param text de strings.xml.
 * @param icon icono de 16 antes del texto, separado 8 ("Start voice session").
 * @param alignStart el contenido va pegado a la izquierda en vez de centrado (Home / 01).
 */
@Composable
fun VoraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: VoraButtonStyle = VoraButtonStyle.Primary,
    size: VoraButtonSize = VoraButtonSize.Large,
    icon: Painter? = null,
    alignStart: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val extra = VoraTheme.colors
    val shape = RoundedCornerShape(VoraTheme.radius.pill)

    val (fondo, texto, borde) = when (style) {
        VoraButtonStyle.Primary -> Triple(colors.primary, colors.onPrimary, null)
        VoraButtonStyle.Cta -> Triple(extra.cta, extra.onAccent, null)
        VoraButtonStyle.Mint -> Triple(colors.secondary, extra.onAccent, null)
        VoraButtonStyle.OutlinedOnDark ->
            Triple(Color.Transparent, colors.onPrimary, extra.onDarkBorder)
        VoraButtonStyle.SubtleOnDark -> Triple(extra.onDarkSubtle, colors.onPrimary, null)
        VoraButtonStyle.Outlined -> Triple(colors.surface, colors.onSurface, colors.outline)
        VoraButtonStyle.Soft -> Triple(colors.background, colors.primary, null)
        VoraButtonStyle.Destructive -> Triple(Color.Transparent, colors.error, colors.error)
        VoraButtonStyle.Text -> Triple(Color.Transparent, colors.primary, null)
        VoraButtonStyle.TextMuted -> Triple(Color.Transparent, colors.onSurfaceVariant, null)
        VoraButtonStyle.TextOnDark -> Triple(Color.Transparent, extra.onDarkMuted, null)
        VoraButtonStyle.TextDestructive -> Triple(Color.Transparent, colors.error, null)
    }
    val subrayado = style == VoraButtonStyle.TextMuted || style == VoraButtonStyle.TextOnDark
    val esTexto = style == VoraButtonStyle.Text || style == VoraButtonStyle.TextDestructive || subrayado

    val textStyle: TextStyle = when (size) {
        VoraButtonSize.Small -> MaterialTheme.typography.labelMedium
        VoraButtonSize.Medium -> MaterialTheme.typography.labelLarge
        VoraButtonSize.Large -> MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        VoraButtonSize.Compact -> VoraTheme.text.buttonCompact
    }.let { if (subrayado) it.copy(textDecoration = TextDecoration.Underline) else it }

    val (vertical, horizontal) = when (size) {
        VoraButtonSize.Small -> VoraTheme.spacing.s8 to VoraTheme.spacing.s16
        VoraButtonSize.Medium -> VoraTheme.spacing.s12 to VoraTheme.spacing.s16
        VoraButtonSize.Large -> VoraTheme.spacing.s14 to VoraTheme.spacing.s24
        VoraButtonSize.Compact -> VoraTheme.spacing.s8 to VoraTheme.spacing.s12
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(fondo)
            .then(if (borde != null) Modifier.border(BorderStroke(VoraTheme.sizes.border, borde), shape) else Modifier)
            .clickable(role = Role.Button, onClick = onClick)
            // los de solo texto no llevan relleno lateral: se alinean con el texto de al lado
            .then(
                if (esTexto) Modifier.padding(vertical = vertical)
                else Modifier.padding(vertical = vertical, horizontal = horizontal),
            ),
        contentAlignment = if (alignStart) Alignment.CenterStart else Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(painter = icon, contentDescription = null, tint = texto, modifier = Modifier.size(VoraTheme.sizes.iconXSmall))
                Spacer(Modifier.width(VoraTheme.spacing.s8))
            }
            // nunca en dos lineas: si no entra, quien lo usa le da mas ancho o lo pasa abajo
            Text(
                text = text,
                style = textStyle,
                color = texto,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraButtonPreview() {
    Parcialtp3Theme {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(VoraTheme.spacing.screen),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        ) {
            val texto = stringResource(R.string.entrar_demo)
            Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
                VoraButton(texto, {}, size = VoraButtonSize.Small)
                VoraButton(texto, {}, style = VoraButtonStyle.Outlined, size = VoraButtonSize.Small)
            }
            VoraButton(texto, {}, Modifier.fillMaxWidth())
            Row(
                Modifier
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(VoraTheme.radius.r20))
                    .padding(VoraTheme.spacing.s20),
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
            ) {
                VoraButton(texto, {}, style = VoraButtonStyle.Cta)
                VoraButton(texto, {}, style = VoraButtonStyle.OutlinedOnDark)
                VoraButton(texto, {}, style = VoraButtonStyle.TextOnDark, size = VoraButtonSize.Small)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s24)) {
                VoraButton(texto, {}, style = VoraButtonStyle.Text, size = VoraButtonSize.Small)
                VoraButton(texto, {}, style = VoraButtonStyle.TextMuted, size = VoraButtonSize.Small)
                VoraButton(texto, {}, style = VoraButtonStyle.TextDestructive, size = VoraButtonSize.Small)
            }
            VoraButton(texto, {}, Modifier.fillMaxWidth(), style = VoraButtonStyle.Mint)
            VoraButton(texto, {}, Modifier.fillMaxWidth(), style = VoraButtonStyle.Destructive)
        }
    }
}
