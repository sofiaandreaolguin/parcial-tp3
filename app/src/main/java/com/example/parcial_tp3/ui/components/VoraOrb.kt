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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/*
 * Proporciones del orbe medidas en el frame Voice / 01 Idle (140 de diametro): halo hasta
 * el borde, aro de degradado al 89 % del radio y de un 10 % de ancho, linea gris al 80 %
 * (unos 2 de ancho) y esfera al 77,5 %.
 *
 * Son las mismas en los cinco tamanos que usa el asistente (170 en Listening, 150 en
 * Processing, 140 en Idle y Speaking, 120 en Error y 28 en los avatares), asi que alcanza
 * con pasarle [size].
 */
private const val ARO = 0.89f
private const val ANCHO_ARO = 0.10f
private const val LINEA = 0.80f
private const val ESFERA = 0.775f

/** El anillo de onda de Listening y Speaking: 115 % del diametro del orbe. */
private const val ONDA = 1.15f

/** La paleta del orbe. El apagado es el mismo dibujo con los grises del frame del error. */
enum class VoraOrbTone { Brand, Muted }

/**
 * El orbe de VORA de las pantallas de Voice: halo, aro con degradado lima, menta y verde,
 * y la esfera con degradado menta a verde. Es solo dibujo; el icono del centro (la onda de
 * voz, el reintento del error) lo pasa la pantalla en [content].
 *
 * Sin aro ([withRing] false) queda la esfera sola. **El avatar chico de Conversation y el
 * del panel de VORA no son ese caso:** en el Figma llevan las tres capas, asi que van con
 * el aro puesto y solo cambian de tamano.
 *
 * @param onDark true en las pantallas verdes (Listening, Processing, Speaking): el halo y la
 *   linea van en blanco translucido.
 * @param tone [VoraOrbTone.Muted] en Voice / 06 Error: el degradado va de gris a gris
 *   oscuro, leido del SVG del frame.
 * @param withRipple el anillo exterior de Listening y Speaking. Agranda el componente un
 *   15 %, porque se dibuja por fuera del orbe.
 */
@Composable
fun VoraOrb(
    modifier: Modifier = Modifier,
    size: Dp = VoraTheme.sizes.orb,
    withRing: Boolean = true,
    onDark: Boolean = false,
    tone: VoraOrbTone = VoraOrbTone.Brand,
    withRipple: Boolean = false,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
    val extra = VoraTheme.colors
    val apagado = tone == VoraOrbTone.Muted
    val aro = if (apagado) {
        Brush.horizontalGradient(listOf(colors.outlineVariant, extra.textMuted, colors.primary))
    } else {
        Brush.horizontalGradient(listOf(colors.tertiary, colors.secondary, colors.primary))
    }
    val esfera = if (apagado) {
        Brush.horizontalGradient(listOf(extra.textMuted, extra.orbMutedCore))
    } else {
        Brush.horizontalGradient(listOf(colors.secondary, colors.primary))
    }
    // en fondo claro el halo es un disco suave y la linea es blanca; en fondo verde el halo
    // es un anillo fino y entre el aro y la esfera queda el verde del fondo. El del orbe
    // apagado es un degradado radial casi transparente, como lo exporta el frame.
    val halo = when {
        onDark -> SolidColor(extra.onDarkSubtle)
        apagado -> Brush.radialGradient(listOf(extra.textMuted, colors.primary))
        else -> SolidColor(extra.orbHalo)
    }
    val opacidadHalo = if (apagado && !onDark) 0.1f else 1f
    val linea = if (onDark) colors.primary else colors.outline
    val trazoHalo = VoraTheme.sizes.borderSelected
    val lienzo = if (withRipple) size * ONDA else size

    Box(modifier.size(lienzo), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val medio = center
            val r = size.toPx() / 2
            if (withRipple) {
                drawCircle(
                    brush = halo,
                    radius = r * ONDA - trazoHalo.toPx() / 2,
                    center = medio,
                    alpha = opacidadHalo,
                    style = Stroke(trazoHalo.toPx()),
                )
            }
            if (withRing) {
                if (onDark) {
                    drawCircle(halo, r - trazoHalo.toPx() / 2, medio, opacidadHalo, Stroke(trazoHalo.toPx()))
                } else {
                    drawCircle(brush = halo, radius = r, center = medio, alpha = opacidadHalo)
                }
                drawCircle(brush = aro, radius = r * ARO, center = medio, style = Stroke(r * ANCHO_ARO))
                drawCircle(color = linea, radius = r * LINEA, center = medio)
                drawCircle(brush = esfera, radius = r * ESFERA, center = medio)
            } else {
                drawCircle(brush = esfera, radius = r, center = medio)
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
                VoraOrb(onDark = true, withRipple = true)
            }
            VoraOrb(size = VoraTheme.sizes.orbMuted, tone = VoraOrbTone.Muted)
            VoraOrb(size = VoraTheme.sizes.orbSmall)
        }
    }
}
