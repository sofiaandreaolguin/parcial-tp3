package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/*
 * Indicadores de la lamina 04: anillos para el avance global y barras para los
 * macronutrientes. El dato numerico siempre acompana al color: estos componentes dibujan
 * solo la forma, el numero lo pone la pantalla al lado o adentro.
 */

/**
 * Anillo de avance ("72%" de Today's Overview, "67%" de la lamina). Empieza arriba y gira
 * en sentido horario, con las puntas redondeadas.
 *
 * @param progress de 0 a 1; lo que este afuera se recorta.
 * @param size 72 por defecto; Today's Overview usa uno mas chico.
 * @param content lo que va en el centro, por ejemplo el porcentaje.
 */
@Composable
fun VoraProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = VoraTheme.sizes.ring,
    strokeWidth: Dp = VoraTheme.sizes.ringStroke,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val avance = progress.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .size(size)
            .semantics(mergeDescendants = true) { progressBarRangeInfo = ProgressBarRangeInfo(avance, 0f..1f) },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val trazo = strokeWidth.toPx()
            val lado = this.size.minDimension - trazo
            val origen = Offset(trazo / 2, trazo / 2)
            val area = Size(lado, lado)
            drawArc(trackColor, 0f, 360f, useCenter = false, topLeft = origen, size = area, style = Stroke(trazo))
            drawArc(
                color,
                startAngle = -90f,
                sweepAngle = 360f * avance,
                useCenter = false,
                topLeft = origen,
                size = area,
                style = Stroke(trazo, cap = StrokeCap.Round),
            )
        }
        content()
    }
}

/**
 * Barra de avance de 4 ("Protein 104g / 140g"). El color va por macro: verde para
 * proteinas, menta para carbohidratos y lima para grasas, como en el Figma.
 */
@Composable
fun VoraProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.outline,
    height: Dp = VoraTheme.sizes.progressBar,
) {
    val avance = progress.coerceIn(0f, 1f)
    val shape = RoundedCornerShape(VoraTheme.radius.pill)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(trackColor)
            .semantics { progressBarRangeInfo = ProgressBarRangeInfo(avance, 0f..1f) },
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(avance)
                .background(color, shape),
        )
    }
}

/**
 * Senal de estado de 8 ("Workout Progress  Not started 0%"): un punto de color al lado del
 * texto. El rojo indica "no empezado", no un error.
 */
@Composable
fun VoraStatusDot(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(VoraTheme.sizes.statusDot)
            .background(color, CircleShape),
    )
}

@Preview(showBackground = true)
@Composable
private fun VoraProgressPreview() {
    Parcialtp3Theme {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(VoraTheme.spacing.screen),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
            ) {
                VoraProgressRing(0.67f) {
                    Text(stringResource(R.string.tab_home), style = MaterialTheme.typography.labelMedium)
                }
                Column(Modifier.width(VoraTheme.sizes.orb)) {
                    VoraProgressBar(0.74f)
                    Box(Modifier.height(VoraTheme.spacing.s12))
                    VoraProgressBar(0.69f, color = MaterialTheme.colorScheme.secondary)
                    Box(Modifier.height(VoraTheme.spacing.s12))
                    VoraProgressBar(0.74f, color = MaterialTheme.colorScheme.tertiary)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                VoraStatusDot(MaterialTheme.colorScheme.error)
                Box(Modifier.width(VoraTheme.spacing.s8))
                Text(stringResource(R.string.tab_progress), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
