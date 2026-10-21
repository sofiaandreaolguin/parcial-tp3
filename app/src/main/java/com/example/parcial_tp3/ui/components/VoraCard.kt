package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/*
 * Tarjetas y paneles de la lamina 04: radio 12 en filas de comidas, 16 en tarjetas y
 * 20 en paneles destacados. Todas sin estado.
 */

/**
 * Tarjeta blanca con borde: contiene informacion y metricas (Home, Nutrition, Profile).
 *
 * @param padding 16 por defecto; 12 en las filas de comidas.
 * @param radius 16 por defecto; 12 en las filas de comidas (Nutrition / Dashboard).
 * @param onClick si no es null, la tarjeta entera se puede tocar.
 */
@Composable
fun VoraCard(
    modifier: Modifier = Modifier,
    padding: Dp = VoraTheme.spacing.s16,
    radius: Dp = VoraTheme.radius.r16,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(radius)
    Column(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(VoraTheme.sizes.border, MaterialTheme.colorScheme.outline, shape)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(padding),
        content = content,
    )
}

/**
 * Panel verde destacado ("READY FOR TODAY?" de Workout, "VORA knows your goals" de
 * Profile): radio 20, padding 20 y separacion de 16. Adentro, el texto y los iconos salen
 * blancos solos. Los botones de adentro van en tamano chico ([VoraButtonSize.Small]),
 * separados 8.
 */
@Composable
fun VoraPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(VoraTheme.radius.r20))
            .background(MaterialTheme.colorScheme.primary)
            .padding(VoraTheme.spacing.s20),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onPrimary) {
            content()
        }
    }
}

/**
 * Fila de Settings y de Profile: icono en un circulo, titulo, subtitulo opcional y flecha.
 *
 * @param icon el icono propio de la fila (cada pantalla trae el suyo del Figma).
 * @param trailing lo que va antes de la flecha, por ejemplo la etiqueta "Synced".
 */
@Composable
fun VoraListRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: Painter? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    VoraCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(VoraTheme.sizes.rowIconCircle)
                        .background(MaterialTheme.colorScheme.background, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painter = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(Modifier.size(VoraTheme.spacing.s12))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (subtitle != null) {
                    Spacer(Modifier.height(VoraTheme.spacing.s2))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (trailing != null) {
                Spacer(Modifier.size(VoraTheme.spacing.s8))
                trailing()
                Spacer(Modifier.size(VoraTheme.spacing.s8))
            }
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = VoraTheme.colors.textMuted,
            )
        }
    }
}

/**
 * Tarjeta de opcion con circulo de check ("Build strength", "Concise / Balanced").
 * Elegida: fondo menta translucido, borde menta de 2 y circulo verde con check.
 * Sin elegir: blanca, borde fino y circulo vacio.
 *
 * Sirve para eleccion unica o multiple: quien la usa decide que hace [onClick].
 *
 * @param role [Role.Checkbox] si se pueden elegir varias (Goals); [Role.RadioButton] si es
 *   una sola (Concise / Balanced / Detailed), asi TalkBack la anuncia bien.
 */
@Composable
fun VoraOptionCard(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    role: Role = Role.Checkbox,
) {
    val shape = RoundedCornerShape(VoraTheme.radius.r16)
    val borde = if (selected) {
        BorderStroke(VoraTheme.sizes.borderSelected, MaterialTheme.colorScheme.secondary)
    } else {
        BorderStroke(VoraTheme.sizes.border, MaterialTheme.colorScheme.outline)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) VoraTheme.colors.mintTranslucent else MaterialTheme.colorScheme.surface)
            .border(borde, shape)
            .selectable(selected = selected, onClick = onClick, role = role)
            .padding(horizontal = VoraTheme.spacing.s16, vertical = VoraTheme.spacing.s14),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (subtitle != null) {
                Spacer(Modifier.height(VoraTheme.spacing.s4))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.size(VoraTheme.spacing.s12))
        CheckCircle(selected)
    }
}

/** El circulo de la derecha de [VoraOptionCard]: verde con check, o vacio con borde. */
@Composable
private fun CheckCircle(selected: Boolean) {
    val size = VoraTheme.sizes.checkCircle
    if (selected) {
        Box(
            modifier = Modifier
                .size(size)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    } else {
        Box(
            Modifier
                .size(size)
                .border(VoraTheme.sizes.border, MaterialTheme.colorScheme.outline, CircleShape),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VoraCardPreview() {
    Parcialtp3Theme {
        val titulo = stringResource(R.string.pant_profile_03)
        val sub = stringResource(R.string.pant_profile_04)
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(VoraTheme.spacing.screen),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        ) {
            VoraPanel(Modifier.fillMaxWidth()) {
                Text(titulo, style = MaterialTheme.typography.titleLarge)
                Text(sub, style = MaterialTheme.typography.bodyMedium, color = VoraTheme.colors.onDarkMuted)
                VoraButton(sub, {}, style = VoraButtonStyle.Cta)
            }
            VoraCard(Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s12, radius = VoraTheme.radius.r12) {
                Text(titulo, style = MaterialTheme.typography.bodyLarge)
                Text(sub, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            VoraOptionCard(titulo, selected = true, onClick = {}, subtitle = sub)
            VoraOptionCard(titulo, selected = false, onClick = {})
            VoraListRow(titulo, {}, subtitle = sub, icon = painterResource(R.drawable.ic_tab_profile))
        }
    }
}
