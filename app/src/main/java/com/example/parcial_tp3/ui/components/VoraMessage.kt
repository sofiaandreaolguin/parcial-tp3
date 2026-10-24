package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/*
 * Mensajes y coaching de la lamina 04. Las muestras mantienen el copy del Figma: no se
 * inventan mensajes de validacion.
 */

/**
 * Recomendacion breve o aviso: menta translucida, borde menta, texto verde, con icono.
 * Radio 12 y padding 12 ("Used to personalise your recommendations.").
 *
 * @param icon por defecto el destello de VORA; para un aviso informativo, `ic_info`.
 */
@Composable
fun VoraInfoMessage(
    text: String,
    modifier: Modifier = Modifier,
    icon: Painter = painterResource(R.drawable.ic_sparkle),
) {
    val shape = RoundedCornerShape(VoraTheme.radius.r12)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(VoraTheme.colors.mintTranslucent, shape)
            .border(VoraTheme.sizes.border, VoraTheme.colors.mintBorder, shape)
            .padding(VoraTheme.spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10),
    ) {
        Icon(painter = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
    }
}

/**
 * Consejo destacado de VORA: invierte la superficie, panel verde con contenido blanco.
 * Destello de 16 y etiqueta de 13 en menta arriba, el consejo (14 en 1,4) abajo, separados 12.
 * Radio 16 y padding 16. Medidas de Progress / 01 (nodo 1:1726).
 *
 * @param label de strings.xml, ya en mayusculas ("VORA ADVICE", "VORA INSIGHT").
 */
@Composable
fun VoraAdvice(
    label: String,
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(VoraTheme.radius.r16))
            .padding(VoraTheme.spacing.s16),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_sparkle),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(VoraTheme.sizes.iconXSmall),
            )
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
        }
        Text(text = text, style = VoraTheme.text.advice, color = MaterialTheme.colorScheme.onPrimary)
    }
}

/**
 * Nota del coach: orbe, etiqueta y texto en verde sobre menta translucida. Aparece en
 * Workout / 01 ("VORA RECOMMENDS"), 03 ("VORA COACH NOTE") y 07 ("VORA INSIGHT"); el
 * texto es 13 con alto de linea 1,4 en las tres.
 *
 * La de 01 es la destacada ([featured]): orbe de 20, etiqueta de 13, separacion 12 y borde
 * menta al 25 %. Las de 03 y 07 van sin borde, con orbe de 16, etiqueta de 12 y separacion 8.
 *
 * @param label de strings.xml, ya en mayusculas.
 * @param padding y [radius] 16 por defecto; 12 en Workout / 03.
 */
@Composable
fun VoraCoachNote(
    label: String,
    text: String,
    modifier: Modifier = Modifier,
    featured: Boolean = false,
    padding: Dp = VoraTheme.spacing.s16,
    radius: Dp = VoraTheme.radius.r16,
) {
    val shape = RoundedCornerShape(radius)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(VoraTheme.colors.mintTranslucent, shape)
            .then(
                if (featured) Modifier.border(VoraTheme.sizes.border, VoraTheme.colors.mintBorderSoft, shape)
                else Modifier,
            )
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(if (featured) VoraTheme.spacing.s12 else VoraTheme.spacing.s8),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (featured) VoraTheme.spacing.s8 else VoraTheme.spacing.s6),
        ) {
            VoraOrb(size = if (featured) VoraTheme.sizes.orbNoteLarge else VoraTheme.sizes.orbNote)
            Text(
                text = label,
                style = if (featured) MaterialTheme.typography.labelMedium else VoraTheme.text.fieldLabel,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraMessagePreview() {
    Parcialtp3Theme {
        val texto = stringResource(R.string.pant_voice)
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(VoraTheme.spacing.screen),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        ) {
            VoraInfoMessage(texto, icon = painterResource(R.drawable.ic_info))
            VoraInfoMessage(texto)
            VoraAdvice(stringResource(R.string.tab_progress).uppercase(), texto)
            VoraCoachNote(stringResource(R.string.tab_workout).uppercase(), texto, featured = true)
            VoraCoachNote(stringResource(R.string.tab_workout).uppercase(), texto)
        }
    }
}
