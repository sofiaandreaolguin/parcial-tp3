package com.example.parcial_tp3.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Interruptor de Voice & AI Settings ("Speak responses aloud"): pista menta de 44 x 24 con
 * la perilla blanca de 20 a la derecha cuando esta prendido; pista gris de borde con la
 * perilla a la izquierda cuando esta apagado. No es el `Switch` de Material 3 porque ese
 * mide 52 x 32 y achica la perilla apagada, y el Figma no.
 *
 * Uso recomendado (como pide Material para que TalkBack lo nombre): la fila entera con su
 * texto es la que se toca, y el interruptor queda solo como dibujo:
 * ```
 * Row(Modifier.toggleable(value = on, role = Role.Switch, onValueChange = onChange)) {
 *     Text(stringResource(R.string.speak_aloud)); VoraToggle(checked = on, onCheckedChange = null)
 * }
 * ```
 *
 * @param onCheckedChange null cuando la que se toca es la fila (ver arriba).
 */
@Composable
fun VoraToggle(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val sizes = VoraTheme.sizes
    val recorrido = sizes.toggleWidth - sizes.toggleThumb - VoraTheme.spacing.s4
    val desplazamiento by animateDpAsState(if (checked) recorrido else 0.dp, label = "perilla")
    Box(
        modifier = modifier
            .size(sizes.toggleWidth, sizes.toggleHeight)
            .clip(CircleShape)
            .background(if (checked) colors.secondary else colors.outline)
            .then(
                if (onCheckedChange != null) {
                    Modifier.toggleable(value = checked, onValueChange = onCheckedChange, role = Role.Switch)
                } else {
                    Modifier
                },
            )
            .padding(VoraTheme.spacing.s2),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .offset(x = desplazamiento)
                .size(sizes.toggleThumb)
                .background(colors.surface, CircleShape),
        )
    }
}

/**
 * Selector con - y + ("45 kg" de Active Workout): dos botones redondos de 36 a los costados
 * del valor. Sin estado: el valor llega armado ("45 kg") y avisa cada toque.
 *
 * @param onDark true sobre paneles verdes (botones blancos translucidos y texto blanco);
 *   false sobre fondo claro.
 */
@Composable
fun VoraStepper(
    value: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    onDark: Boolean = true,
) {
    val colors = MaterialTheme.colorScheme
    val texto = if (onDark) colors.onPrimary else colors.onSurface
    val fondoBoton = if (onDark) VoraTheme.colors.onDarkSubtle else colors.background
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        // 2 + los 6 que cada area tactil de 48 suma alrededor del circulo de 36 = los 8 del Figma
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2),
    ) {
        StepperButton(R.drawable.ic_minus, stringResource(R.string.disminuir), onDecrease, fondoBoton, texto)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = texto)
        StepperButton(R.drawable.ic_plus, stringResource(R.string.aumentar), onIncrease, fondoBoton, texto)
    }
}

/** Un boton redondo del selector: circulo de 36 dibujado, area tactil de 48. */
@Composable
private fun StepperButton(
    @DrawableRes icon: Int,
    description: String,
    onClick: () -> Unit,
    fondo: Color,
    tinte: Color,
) {
    Box(
        modifier = Modifier
            .size(VoraTheme.sizes.touchTarget)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(VoraTheme.sizes.stepperButton)
                .background(fondo, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painter = painterResource(icon), contentDescription = description, tint = tinte)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraControlsPreview() {
    Parcialtp3Theme {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(VoraTheme.spacing.screen),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
                VoraToggle(checked = true, onCheckedChange = {})
                VoraToggle(checked = false, onCheckedChange = {})
            }
            VoraPanel {
                VoraStepper(stringResource(R.string.tab_progress), {}, {})
            }
            VoraStepper(stringResource(R.string.tab_progress), {}, {}, onDark = false)
        }
    }
}
