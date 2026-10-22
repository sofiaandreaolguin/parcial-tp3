package com.example.parcial_tp3.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

/**
 * La etiqueta chica que encabeza una seccion: "SUGGESTED QUESTIONS" y "LAST CONVERSATION"
 * en Voice / 01, "LIVE TRANSCRIPT" en 02 y "TODAY AT 12:40 PM" en 05. Alto de linea 15 en
 * los frames, que es el de labelSmall.
 *
 * El texto ya viene en mayusculas de strings.xml, como el resto de las etiquetas del
 * proyecto (tabla de ui/theme/Type.kt).
 */
@Composable
fun VoraSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = MaterialTheme.typography.labelSmall,
        textAlign = textAlign,
    )
}
