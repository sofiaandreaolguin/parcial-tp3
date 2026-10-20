package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Grafico de barras de Progress ("Training Volume" por sesion, "Weekly Steps Trend" por dia):
 * barras de 24 con las puntas redondeadas y la etiqueta del dia abajo. La altura de cada
 * barra es proporcional al maximo de [values].
 *
 * - Sin [highlighted], todas en verde (Training Volume).
 * - Con [highlighted], esas en verde y las demas en menta (Weekly Steps: los dias que
 *   pasaron la meta).
 * - Las puntas de arriba van redondeadas y la base es recta, apoyada en la linea de base.
 * - Un valor 0 se dibuja como un tramo gris chico, como el dia sin sesion de Workout
 *   Progress.
 *
 * @param labels una por barra ("M", "T", "W"...), de strings.xml.
 * @param description lo que lee TalkBack del grafico entero, por ejemplo el resumen
 *   "8,420 kg esta semana"; el dibujo solo no se puede leer.
 */
@Composable
fun VoraBarChart(
    values: List<Float>,
    labels: List<String>,
    description: String,
    modifier: Modifier = Modifier,
    highlighted: Set<Int>? = null,
    height: Dp = VoraTheme.sizes.chartHeight,
    barWidth: Dp = VoraTheme.sizes.barWidth,
) {
    val colors = MaterialTheme.colorScheme
    val maximo = values.maxOrNull()?.takeIf { it > 0f } ?: 1f
    val forma = RoundedCornerShape(topStart = VoraTheme.radius.r4, topEnd = VoraTheme.radius.r4)

    // una columna por barra (barra arriba, etiqueta abajo): asi quedan alineadas aunque falte
    // una etiqueta. TalkBack lee solo [description], no cada letra suelta.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = description },
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        values.forEachIndexed { i, valor ->
            val color: Color = when {
                valor <= 0f -> colors.outline
                highlighted == null || i in highlighted -> colors.primary
                else -> colors.secondary
            }
            val alto = if (valor <= 0f) VoraTheme.sizes.progressBar else height * (valor / maximo)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.height(height), contentAlignment = Alignment.BottomCenter) {
                    Box(
                        Modifier
                            .width(barWidth)
                            .height(alto)
                            .background(color, forma),
                    )
                }
                Spacer(Modifier.height(VoraTheme.spacing.s8))
                Text(
                    text = labels.getOrElse(i) { "" },
                    style = MaterialTheme.typography.bodySmall,
                    color = VoraTheme.colors.textMuted,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraBarChartPreview() {
    Parcialtp3Theme {
        val dia = stringResource(R.string.tab_home).take(1)
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(VoraTheme.spacing.screen),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s24),
        ) {
            VoraBarChart(listOf(0.7f, 0.8f, 0f, 0.9f, 1f), List(5) { dia }, description = dia)
            VoraBarChart(
                values = listOf(0.6f, 0.8f, 0.9f, 0.75f, 1f, 0.8f, 0.8f),
                labels = List(7) { dia },
                description = dia,
                highlighted = setOf(2, 4),
                barWidth = VoraTheme.sizes.barWidthNarrow,
            )
        }
    }
}
