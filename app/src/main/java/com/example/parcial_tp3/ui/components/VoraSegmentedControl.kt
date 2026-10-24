package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/** Las tres formas de control segmentado que dibuja el Figma. */
enum class VoraSegmentedStyle {
    /** Contenedor blanco en pildora con la opcion elegida verde adentro: "Week / Month / 3 Months". */
    Pill,
    /** Solapas grises separadas, radio 8, dentro de una tarjeta: "Calories / Protein / Carbs / Fat". */
    Tabs,
    /** Cajas blancas con borde, radio 12, separadas 8: "3 days / 4 days", "Supportive / Balanced / Direct". */
    Boxes,
}

/**
 * Control segmentado: una sola opcion elegida entre pocas, todas del mismo ancho. La elegida
 * va en verde con texto blanco. Sin estado: quien lo usa guarda cual esta elegida.
 *
 * @param options los textos, de strings.xml.
 */
@Composable
fun VoraSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    style: VoraSegmentedStyle = VoraSegmentedStyle.Pill,
) {
    val colors = MaterialTheme.colorScheme
    val spacing = VoraTheme.spacing
    val pill = RoundedCornerShape(VoraTheme.radius.pill)

    val contenedor = when (style) {
        VoraSegmentedStyle.Pill -> Modifier
            .background(colors.surface, pill)
            .border(VoraTheme.sizes.border, colors.outline, pill)
            .padding(spacing.s4)
        else -> Modifier
    }
    // en la pildora las opciones van pegadas; las solapas y las cajas van separadas
    val separacion = when (style) {
        VoraSegmentedStyle.Pill -> Arrangement.Start
        VoraSegmentedStyle.Tabs -> Arrangement.spacedBy(spacing.s4)
        VoraSegmentedStyle.Boxes -> Arrangement.spacedBy(spacing.s8)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(contenedor)
            .selectableGroup(),
        horizontalArrangement = separacion,
    ) {
        options.forEachIndexed { i, texto ->
            val elegida = i == selectedIndex
            val shape = when (style) {
                VoraSegmentedStyle.Pill -> pill
                VoraSegmentedStyle.Tabs -> RoundedCornerShape(VoraTheme.radius.r8)
                VoraSegmentedStyle.Boxes -> RoundedCornerShape(VoraTheme.radius.r12)
            }
            val fondo = when {
                elegida -> colors.primary
                style == VoraSegmentedStyle.Tabs -> colors.background
                style == VoraSegmentedStyle.Boxes -> colors.surface
                else -> Color.Transparent
            }
            val vertical = when (style) {
                VoraSegmentedStyle.Pill -> spacing.s8
                VoraSegmentedStyle.Tabs -> spacing.s6
                VoraSegmentedStyle.Boxes -> spacing.s10
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(shape)
                    .background(fondo)
                    .then(
                        if (style == VoraSegmentedStyle.Boxes && !elegida) {
                            Modifier.border(VoraTheme.sizes.border, colors.outline, shape)
                        } else {
                            Modifier
                        },
                    )
                    .selectable(
                        selected = elegida,
                        onClick = { onSelect(i) },
                        // Pill y Tabs cambian una vista; Boxes elige un valor de un formulario
                        role = if (style == VoraSegmentedStyle.Boxes) Role.RadioButton else Role.Tab,
                    )
                    .padding(vertical = vertical, horizontal = spacing.s8),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = texto,
                    // las solapas van en 11 SemiBold (Progress / 04, nodo 1:2070); a 14 no entra "Calories" en 360 dp
                    style = if (style == VoraSegmentedStyle.Tabs) VoraTheme.text.label else MaterialTheme.typography.labelLarge,
                    fontWeight = if (style == VoraSegmentedStyle.Tabs) null else FontWeight.Medium,
                    color = when {
                        elegida -> colors.onPrimary
                        style == VoraSegmentedStyle.Boxes -> colors.onSurface
                        else -> colors.onSurfaceVariant
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraSegmentedControlPreview() {
    Parcialtp3Theme {
        val opciones = listOf(
            stringResource(R.string.tab_home),
            stringResource(R.string.tab_workout),
            stringResource(R.string.tab_nutrition),
        )
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(VoraTheme.spacing.screen),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
        ) {
            VoraSegmentedControl(opciones, 0, {})
            VoraCard(Modifier.fillMaxWidth()) {
                VoraSegmentedControl(opciones, 1, {}, style = VoraSegmentedStyle.Tabs)
            }
            VoraSegmentedControl(opciones, 1, {}, style = VoraSegmentedStyle.Boxes)
        }
    }
}
