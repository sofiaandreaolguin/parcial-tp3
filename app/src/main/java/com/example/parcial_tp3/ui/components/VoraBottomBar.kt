package com.example.parcial_tp3.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.navigation.VoraTab
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * La barra inferior de 5 destinos, segun la lamina "04 Elementos de interfaz / Navegacion":
 * fondo blanco con borde superior de 1, iconos de 20 con trazo de 2, la activa en verde
 * SemiBold y las demas en el texto tenue con peso Medium. Sin el ovalo de seleccion de
 * Material 3, que el Figma no tiene.
 *
 * SI CAMBIAS ESTA FIRMA SE ROMPE MainActivity: avisá antes de tocarla.
 * Es sin estado a proposito: recibe cual esta activa y avisa cual tocaron. No navega
 * ella: de eso se encarga MainActivity.
 *
 * La barra se dibuja UNA sola vez, en el Scaffold. Ninguna pantalla la copia.
 */
@Composable
fun VoraBottomBar(
    selected: VoraTab?,
    onTab: (VoraTab) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        HorizontalDivider(
            thickness = VoraTheme.sizes.border,
            color = MaterialTheme.colorScheme.outline,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // 12 a los costados: en el Figma Home y Profile no van pegadas al borde.
                // El vertical va en cada pestana, asi tocar arriba o abajo tambien cuenta.
                .padding(horizontal = VoraTheme.spacing.s12)
                .selectableGroup(),
        ) {
            VoraTab.entries.forEach { tab ->
                TabItem(
                    tab = tab,
                    activa = tab == selected,
                    onClick = { onTab(tab) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    tab: VoraTab,
    activa: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = if (activa) MaterialTheme.colorScheme.primary else VoraTheme.colors.textMuted
    Column(
        modifier = modifier
            .selectable(selected = activa, onClick = onClick, role = Role.Tab)
            .padding(vertical = VoraTheme.spacing.s10),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Cada icono a su tamano del Figma (17 a 19), centrado en la caja de 20: asi las
        // etiquetas quedan alineadas y ningun icono se estira.
        Box(Modifier.size(VoraTheme.sizes.icon), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(tab.icono()),
                contentDescription = null, // la etiqueta de abajo ya lo nombra
                tint = color,
            )
        }
        Spacer(Modifier.height(VoraTheme.spacing.s4))
        Text(
            text = stringResource(tab.etiqueta),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (activa) FontWeight.SemiBold else FontWeight.Medium,
            color = color,
            // en espanol ("Entrenamiento") o con letra grande no puede saltar a dos lineas
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Los iconos del Figma, exportados como SVG y pasados a vector drawable. */
@DrawableRes
private fun VoraTab.icono(): Int = when (this) {
    VoraTab.HOME -> R.drawable.ic_tab_home
    VoraTab.WORKOUT -> R.drawable.ic_tab_workout
    VoraTab.NUTRITION -> R.drawable.ic_tab_nutrition
    VoraTab.PROGRESS -> R.drawable.ic_tab_progress
    VoraTab.PROFILE -> R.drawable.ic_tab_profile
}

@Preview(showBackground = true)
@Composable
private fun VoraBottomBarPreview() {
    Parcialtp3Theme {
        VoraBottomBar(selected = VoraTab.WORKOUT, onTab = {})
    }
}
