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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
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
 * @param variant [VoraTextFieldVariant.Access] en Login: fondo verdoso, radio 16, 55 de alto
 *   y la etiqueta sin mayusculas.
 * @param labelAction lo que va a la derecha de la etiqueta ("Forgot your password?").
 * @param trailing lo que va al final del campo (el ojo de la contrasena).
 * @param placeholderStyle otro estilo para el placeholder (los puntos de la contrasena de Login).
 * @param error mensaje de strings.xml: pinta el borde en rojo y lo muestra abajo. El Figma no
 *   dibuja el error, pero un formulario que no entra tiene que decir por que.
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
    variant: VoraTextFieldVariant = VoraTextFieldVariant.Default,
    labelAction: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    error: String? = null,
    placeholderStyle: TextStyle? = null,
) {
    val colors = MaterialTheme.colorScheme
    val access = VoraTheme.colors.access
    val conBorde = variant == VoraTextFieldVariant.AccessOutlined
    val esAcceso = variant == VoraTextFieldVariant.Access || conBorde
    val textStyle = when (variant) {
        VoraTextFieldVariant.Access -> VoraTheme.text.accessBody.copy(color = access.ink)
        VoraTextFieldVariant.AccessOutlined -> VoraTheme.text.accessInputLarge.copy(color = access.ink)
        VoraTextFieldVariant.Default -> MaterialTheme.typography.bodyLarge.copy(color = colors.onSurface)
    }
    val hint = when (variant) {
        VoraTextFieldVariant.Access -> access.hint
        VoraTextFieldVariant.AccessOutlined -> access.outline
        VoraTextFieldVariant.Default -> VoraTheme.colors.textMuted
    }
    val icono = if (conBorde) access.ink else hint
    val fondo = if (variant == VoraTextFieldVariant.Access) access.field else colors.surface
    val borde: Color = when {
        error != null -> colors.error
        conBorde -> access.outline
        esAcceso -> access.border
        else -> colors.outline
    }
    val radio = if (variant == VoraTextFieldVariant.Access) VoraTheme.radius.r16 else VoraTheme.radius.r12
    val alto = if (conBorde) VoraTheme.sizes.accessFieldOutlined else VoraTheme.sizes.accessField
    Column(modifier) {
        if (label != null) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    style = if (conBorde) VoraTheme.text.accessLabelLarge else VoraTheme.text.fieldLabel,
                    color = if (esAcceso) access.ink else colors.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                labelAction?.invoke()
            }
            // Edit profile: 4 + los 3 del alto de linea = 8. Login - 01: 6. Login - 02: 4.
            Spacer(Modifier.height(if (esAcceso && !conBorde) VoraTheme.spacing.s6 else VoraTheme.spacing.s4))
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            // la etiqueta es un Text aparte: se la pasamos al campo para que TalkBack lo nombre
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    (label ?: placeholder)?.let { contentDescription = it }
                    if (error != null) error(error)
                },
            textStyle = textStyle,
            singleLine = singleLine,
            readOnly = readOnly,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            cursorBrush = SolidColor(colors.primary),
            decorationBox = { campo ->
                val shape = RoundedCornerShape(radio)
                Row(
                    modifier = Modifier
                        .then(if (esAcceso) Modifier.heightIn(min = alto) else Modifier)
                        .background(fondo, shape)
                        .border(VoraTheme.sizes.border, borde, shape)
                        .padding(horizontal = VoraTheme.spacing.s16, vertical = VoraTheme.spacing.s12),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10),
                ) {
                    if (leadingIcon != null) {
                        Icon(painter = leadingIcon, contentDescription = null, tint = icono)
                    }
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty() && placeholder != null) {
                            Text(text = placeholder, style = placeholderStyle ?: textStyle, color = hint)
                        }
                        campo()
                    }
                    trailing?.invoke()
                }
            },
        )
        if (error != null) {
            Spacer(Modifier.height(VoraTheme.spacing.s4))
            Text(text = error, style = VoraTheme.text.footnote, color = colors.error)
        }
    }
}

/** Las dos apariencias de campo que dibuja el Figma. */
enum class VoraTextFieldVariant {
    /** Lamina 04 y Edit profile: blanco, radio 12, etiqueta en mayusculas. */
    Default,
    /** Login - 01: fondo verdoso, radio 16, 55 de alto, etiqueta como oracion. */
    Access,
    /** Login - 02: blanco con borde gris, radio 12, 52 de alto, etiqueta de 16 e iconos verdes. */
    AccessOutlined,
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
