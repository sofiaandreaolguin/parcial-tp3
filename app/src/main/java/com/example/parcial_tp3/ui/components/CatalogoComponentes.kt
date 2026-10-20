package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Catalogo de los componentes comunes, para compararlo con la lamina 04 "Elementos de
 * interfaz" del Figma. Es solo una preview: no es una ruta ni se ve en la app.
 *
 * Los textos son de relleno (claves que ya existen en strings.xml): lo que se compara es
 * la forma, el color y la medida, no el copy.
 */
@Composable
internal fun CatalogoComponentes() {
    val corto = stringResource(R.string.tab_workout)
    val largo = stringResource(R.string.pant_workout_03)
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
    ) {
        VoraTopBarLarge(title = stringResource(R.string.tab_profile), onBack = {})
        Column(
            Modifier.padding(horizontal = VoraTheme.spacing.screen),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
        ) {
            // Botones y acciones
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
            ) {
                VoraButton(corto, {}, size = VoraButtonSize.Small)
                VoraButton(corto, {}, style = VoraButtonStyle.Outlined, size = VoraButtonSize.Small)
                VoraButton(corto, {}, style = VoraButtonStyle.Text, size = VoraButtonSize.Small)
            }
            // Soft va sobre una tarjeta blanca, como "Edit profile" en Profile / 01
            VoraCard(Modifier.fillMaxWidth()) {
                VoraButton(corto, {}, style = VoraButtonStyle.Soft, size = VoraButtonSize.Small)
            }
            VoraButton(largo, {}, Modifier.fillMaxWidth())

            // Tarjetas y paneles
            VoraPanel(Modifier.fillMaxWidth()) {
                VoraTag(corto.uppercase(), tone = VoraTagTone.OnDark)
                Text(largo, style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                    VoraButton(corto, {}, style = VoraButtonStyle.Cta, size = VoraButtonSize.Small)
                    VoraButton(corto, {}, style = VoraButtonStyle.OutlinedOnDark, size = VoraButtonSize.Small)
                }
            }
            VoraCard(Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s12, radius = VoraTheme.radius.r12) {
                Text(corto, style = MaterialTheme.typography.bodyLarge)
                Text(largo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Campos
            VoraTextField(value = largo, onValueChange = {}, label = corto.uppercase())

            // Chips y etiquetas
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
            ) {
                VoraChip(corto, selected = true, onClick = {})
                VoraChip(corto, selected = false, onClick = {})
                VoraChip(corto, selected = true, onClick = {}, selectedStyle = VoraChipSelectedStyle.Dark)
                VoraChip(corto, selected = true, onClick = {}, selectedStyle = VoraChipSelectedStyle.Outline)
                VoraTag(corto.uppercase())
            }

            // Opciones y filas
            VoraOptionCard(corto, selected = true, onClick = {}, subtitle = largo)
            VoraOptionCard(corto, selected = false, onClick = {})
            VoraListRow(corto, {}, subtitle = largo, icon = painterResource(R.drawable.ic_tab_profile))

            // Mensajes y coaching
            VoraInfoMessage(largo, icon = painterResource(R.drawable.ic_info))
            VoraAdvice(corto.uppercase(), largo)
        }
    }
}

@Preview(showBackground = true, widthDp = 394, heightDp = 1400)
@Composable
private fun CatalogoComponentesPreview() {
    Parcialtp3Theme { CatalogoComponentes() }
}
