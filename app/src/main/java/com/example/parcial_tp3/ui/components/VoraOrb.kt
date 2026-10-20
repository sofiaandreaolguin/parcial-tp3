package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/*
 * Proporciones del orbe medidas en el frame Voice / 01 Idle (140 de diametro): halo hasta
 * el borde, aro de degradado al 89 % del radio y de un 10 % de ancho, linea gris al 80 %
 * (unos 2 de ancho) y esfera al 77,5 %.
 */
private const val ARO = 0.89f
private const val ANCHO_ARO = 0.10f
private const val LINEA = 0.80f
private const val ESFERA = 0.775f

/**
 * El orbe de VORA de las pantallas de Voice: halo, aro con degradado lima, menta y verde,
 * y la esfera con degradado menta a verde. Es solo dibujo; el icono del centro (la onda de
 * voz, el reintento del error) lo pasa la pantalla en [content].
 *
 * Sin aro ([withRing] false) queda la esfera sola, como el avatar chico del coach en
 * Conversation o en las tarjetas de "VORA Insight".
 *
 * @param onDark true en las pantallas verdes (Listening, Processing, Speaking): el halo y la
 *   linea van en blanco translucido.
 */
@Composable
fun VoraOrb(
    modifier: Modifier = Modifier,
    size: Dp = VoraTheme.sizes.orb,
    withRing: Boolean = true,
    onDark: Boolean = false,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
    val aro = Brush.horizontalGradient(listOf(colors.tertiary, colors.secondary, colors.primary))
    val esfera = Brush.horizontalGradient(listOf(colors.secondary, colors.primary))
    // en fondo claro el halo es un disco suave y la linea es blanca; en fondo verde el halo
    // es un anillo fino y entre el aro y la esfera queda el verde del fondo
    val halo = if (onDark) VoraTheme.colors.onDarkSubtle else VoraTheme.colors.orbHalo
    val linea = if (onDark) colors.primary else colors.outline
    val trazoHalo = VoraTheme.sizes.borderSelected

    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val r = this.size.minDimension / 2
            if (withRing) {
                if (onDark) {
                    drawCircle(halo, radius = r - trazoHalo.toPx() / 2, style = Stroke(trazoHalo.toPx()))
                } else {
                    drawCircle(halo, radius = r)
                }
                drawCircle(aro, radius = r * ARO, style = Stroke(r * ANCHO_ARO))
                drawCircle(linea, radius = r * LINEA)
                drawCircle(esfera, radius = r * ESFERA)
            } else {
                drawCircle(esfera, radius = r)
            }
        }
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraOrbPreview() {
    Parcialtp3Theme {
        Row(
            Modifier.padding(VoraTheme.spacing.screen),
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VoraOrb()
            Box(Modifier.background(MaterialTheme.colorScheme.primary).padding(VoraTheme.spacing.s8)) {
                VoraOrb(onDark = true)
            }
            VoraOrb(size = VoraTheme.sizes.orbSmall, withRing = false)
        }
    }
}
