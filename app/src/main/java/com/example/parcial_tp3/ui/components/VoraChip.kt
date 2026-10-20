package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/** Como se ve un [VoraChip] elegido. Todos los chips sin elegir son blancos con borde. */
enum class VoraChipSelectedStyle {
    /** Menta con texto verde: "Everything", "Strength". */
    Mint,
    /** Verde oscuro con texto blanco: "4 days". */
    Dark,
    /** Sigue blanco, con borde menta y check: "Chicken ✓". */
    Outline,
}

/**
 * Chip que se elige y se desmarca (Goals & Preferences, Nutrition Preferences, Food
 * Confirmation). Sin estado: quien lo usa guarda cuales estan elegidos.
 *
 * @param trailingIcon por ejemplo el check de los alimentos elegidos o la cruz roja de
 *   "Foods to avoid". Con [VoraChipSelectedStyle.Outline] elegido, va el check solo.
 * @param trailingTint color del icono; por defecto el del texto.
 */
@Composable
fun VoraChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedStyle: VoraChipSelectedStyle = VoraChipSelectedStyle.Mint,
    trailingIcon: Painter? = null,
    trailingTint: Color? = null,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(VoraTheme.radius.pill)
    val (fondo, texto, borde) = when {
        !selected -> Triple(colors.surface, colors.onSurface, BorderStroke(VoraTheme.sizes.border, colors.outline))
        selectedStyle == VoraChipSelectedStyle.Mint -> Triple(colors.secondary, VoraTheme.colors.onAccent, null)
        selectedStyle == VoraChipSelectedStyle.Dark -> Triple(colors.primary, colors.onPrimary, null)
        else -> Triple(colors.surface, colors.onSurface, BorderStroke(VoraTheme.sizes.border, colors.secondary))
    }
    val icono = trailingIcon
        ?: if (selected && selectedStyle == VoraChipSelectedStyle.Outline) painterResource(R.drawable.ic_check) else null

    Row(
        modifier = modifier
            .clip(shape)
            .background(fondo)
            .then(if (borde != null) Modifier.border(borde, shape) else Modifier)
            .selectable(selected = selected, onClick = onClick, role = Role.Checkbox)
            .padding(horizontal = VoraTheme.spacing.s14, vertical = VoraTheme.spacing.s6),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium, color = texto)
        if (icono != null) {
            Icon(
                painter = icono,
                contentDescription = null,
                tint = trailingTint ?: if (selectedStyle == VoraChipSelectedStyle.Outline) colors.primary else texto,
            )
        }
    }
}

/** Tono de una [VoraTag]. */
enum class VoraTagTone {
    /** Menta translucida con texto verde: "ONLINE", "READY". */
    Mint,
    /** Sobre un panel verde: blanco translucido con texto blanco ("MODERATE"). */
    OnDark,
}

/**
 * Etiqueta compacta de disponibilidad o estado (lamina 04, "Indicadores"): pildora con
 * texto de 10 SemiBold en mayusculas. No se toca.
 *
 * @param text de strings.xml, ya en mayusculas.
 */
@Composable
fun VoraTag(
    text: String,
    modifier: Modifier = Modifier,
    tone: VoraTagTone = VoraTagTone.Mint,
) {
    val (fondo, texto) = when (tone) {
        VoraTagTone.Mint -> VoraTheme.colors.mintTranslucent to MaterialTheme.colorScheme.primary
        VoraTagTone.OnDark -> VoraTheme.colors.onDarkSubtle to MaterialTheme.colorScheme.onPrimary
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = texto,
        modifier = modifier
            .background(fondo, RoundedCornerShape(VoraTheme.radius.pill))
            .padding(horizontal = VoraTheme.spacing.s10, vertical = VoraTheme.spacing.s4),
    )
}

@Preview(showBackground = true)
@Composable
private fun VoraChipPreview() {
    Parcialtp3Theme {
        val a = stringResource(R.string.tab_workout)
        val b = stringResource(R.string.tab_nutrition)
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(VoraTheme.spacing.screen),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        ) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                VoraChip(a, selected = true, onClick = {})
                VoraChip(b, selected = false, onClick = {})
                VoraChip(a, selected = true, onClick = {}, selectedStyle = VoraChipSelectedStyle.Dark)
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                VoraChip(a, selected = true, onClick = {}, selectedStyle = VoraChipSelectedStyle.Outline)
                VoraChip(
                    b,
                    selected = false,
                    onClick = {},
                    trailingIcon = painterResource(R.drawable.ic_remove),
                    trailingTint = MaterialTheme.colorScheme.error,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                VoraTag(a.uppercase())
                VoraPanel { VoraTag(b.uppercase(), tone = VoraTagTone.OnDark) }
            }
        }
    }
}
