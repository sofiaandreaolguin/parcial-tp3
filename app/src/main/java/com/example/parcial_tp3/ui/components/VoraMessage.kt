package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
 * Destello y etiqueta en menta arriba, el consejo abajo. Radio 16 y padding 16.
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
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_sparkle),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
            )
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        }
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary)
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
        }
    }
}
