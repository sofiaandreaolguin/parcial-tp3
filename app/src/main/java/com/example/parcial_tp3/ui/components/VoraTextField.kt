package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Campo de formulario de la lamina 04 ("Campos de formulario", Edit profile): etiqueta en
 * mayusculas arriba, separada 6; campo blanco con borde, radio 12 y padding 12/16; valor
 * en 15 Regular.
 *
 * Sin estado: el valor y el cambio vienen de afuera (el ViewModel de la pantalla). El Figma
 * no dibuja foco, error ni campo deshabilitado, asi que no se inventan.
 *
 * @param label de strings.xml, ya en mayusculas; null para el campo sin etiqueta
 *   ("Add an ingredient...").
 * @param leadingIcon por ejemplo el + de "Add an ingredient".
 * @param readOnly muestra el valor sin dejar editarlo y se ve igual (Edit profile de demostracion).
 */
@Composable
fun VoraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: Painter? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    readOnly: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.onSurface)
    Column(modifier) {
        if (label != null) {
            Text(text = label, style = VoraTheme.text.fieldLabel, color = colors.onSurfaceVariant)
            // 4 + los 2 que suma el alto de linea de la etiqueta = los 6 de la lamina
            Spacer(Modifier.height(VoraTheme.spacing.s4))
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            // la etiqueta es un Text aparte: se la pasamos al campo para que TalkBack lo nombre
            modifier = Modifier
                .fillMaxWidth()
                .semantics { (label ?: placeholder)?.let { contentDescription = it } },
            textStyle = textStyle,
            singleLine = singleLine,
            readOnly = readOnly,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            cursorBrush = SolidColor(colors.primary),
            decorationBox = { campo ->
                val shape = RoundedCornerShape(VoraTheme.radius.r12)
                Row(
                    modifier = Modifier
                        .background(colors.surface, shape)
                        .border(VoraTheme.sizes.border, colors.outline, shape)
                        .padding(horizontal = VoraTheme.spacing.s16, vertical = VoraTheme.spacing.s12),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10),
                ) {
                    if (leadingIcon != null) {
                        Icon(painter = leadingIcon, contentDescription = null, tint = VoraTheme.colors.textMuted)
                    }
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty() && placeholder != null) {
                            Text(text = placeholder, style = textStyle, color = VoraTheme.colors.textMuted)
                        }
                        campo()
                    }
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraTextFieldPreview() {
    Parcialtp3Theme {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(VoraTheme.spacing.screen),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
        ) {
            VoraTextField(
                value = stringResource(R.string.tab_profile),
                onValueChange = {},
                label = stringResource(R.string.tab_home).uppercase(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16)) {
                VoraTextField(stringResource(R.string.volver), {}, Modifier.weight(1f), label = stringResource(R.string.tab_progress).uppercase())
                VoraTextField(stringResource(R.string.cerrar), {}, Modifier.weight(1f), label = stringResource(R.string.tab_workout).uppercase())
            }
            VoraTextField(
                value = "",
                onValueChange = {},
                placeholder = stringResource(R.string.tab_nutrition),
                leadingIcon = painterResource(R.drawable.ic_plus),
            )
        }
    }
}
