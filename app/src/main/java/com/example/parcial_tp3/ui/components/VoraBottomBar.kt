package com.example.parcial_tp3.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.parcial_tp3.ui.navigation.VoraTab

/**
 * PROVISORIA. La definitiva la hace Tema y componentes (tarea VC-d), con los colores,
 * la tipografia y los iconos del Figma.
 *
 * SI CAMBIAS ESTA FIRMA SE ROMPE MainActivity: avisá antes de tocarla.
 * Es sin estado a proposito: recibe cual esta activa y avisa cual tocaron. No navega
 * ella: de eso se encarga MainActivity.
 *
 * Los iconos del Figma son dibujos propios, no de Material, asi que aca va un punto de
 * relleno. Tema y componentes lo reemplaza por el drawable que corresponda.
 *
 * La barra se dibuja UNA sola vez, en el Scaffold. Ninguna pantalla la copia.
 */
@Composable
fun VoraBottomBar(
    selected: VoraTab?,
    onTab: (VoraTab) -> Unit,
) {
    NavigationBar {
        VoraTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = tab == selected,
                onClick = { onTab(tab) },
                icon = { Text("•") },
                label = { Text(stringResource(tab.etiqueta)) },
            )
        }
    }
}
