package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * El encabezado comun de las pantallas que se abren encima (las que no llevan barra):
 * volver a la izquierda, titulo chico centrado y, a la derecha, cerrar o acciones propias
 * (por ejemplo la ayuda de Exercise Guide). Asi se ve en Workout / 03, 04 y 06.
 *
 * El titulo grande de cada pantalla ("Upper Body Strength") no va aca: es contenido de
 * la pantalla.
 *
 * Fondo transparente: se ve el fondo de la pantalla, sea el claro o el verde de Voice.
 * Para fondos oscuros, pasar [contentColor]. Los iconos van a su tamano del Figma
 * (flecha 7 x 10, cruz 11 x 11), centrados en el area tactil de 48.
 *
 * @param title el titulo chico centrado, de strings.xml; null si el frame no lo tiene.
 * @param backInCircle true cuando el frame dibuja la flecha dentro de un circulo de 32 con
 *   borde, como los seis de Voice: blanco con borde gris sobre fondo claro, y blanco al 8 %
 *   con borde al 12 % sobre el verde.
 * @param titleAtStart true cuando el frame lo pone pegado a la flecha en vez de centrado.
 *   Asi se ve en los seis frames de Voice; en Workout / 03, 04 y 06 va centrado.
 * @param titleBadge lo que va inmediatamente despues del titulo: el punto verde de
 *   Voice / 05 Conversation.
 * @param onBack si no es null, dibuja la flecha de volver.
 * @param onClose si no es null, dibuja la cruz de cerrar a la derecha.
 * @param actions otros iconos a la derecha, antes de cerrar.
 */
@Composable
fun VoraTopBar(
    title: String?,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    backInCircle: Boolean = false,
    titleAtStart: Boolean = false,
    titleBadge: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            // Sin statusBarsPadding: el Scaffold de MainActivity ya deja el lugar.
            // Sin padding lateral: la flecha (7) centrada en el boton de 48 cae a unos 20
            // del borde, que es el margen de pantalla del Figma.
            .padding(vertical = VoraTheme.spacing.s4),
        contentAlignment = Alignment.Center,
    ) {
        if (title != null && !titleAtStart) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                // deja lugar a los botones de los costados
                modifier = Modifier.padding(horizontal = VoraTheme.sizes.touchTarget),
            )
        }
        Row(
            // el frame deja el circulo a 20 del borde; el area tactil de 48 alrededor de
            // uno de 32 ya aporta 8, asi que faltan 12
            modifier = Modifier
                .align(Alignment.CenterStart)
                .then(
                    if (backInCircle) Modifier.padding(start = VoraTheme.spacing.s12) else Modifier,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    if (backInCircle) {
                        val sobreVerde = contentColor == MaterialTheme.colorScheme.onPrimary
                        val forma = RoundedCornerShape(VoraTheme.radius.r16)
                        Box(
                            Modifier
                                .size(VoraTheme.sizes.backCircle)
                                .background(
                                    if (sobreVerde) VoraTheme.colors.onDarkPanel else MaterialTheme.colorScheme.surface,
                                    forma,
                                )
                                .border(
                                    VoraTheme.sizes.border,
                                    if (sobreVerde) VoraTheme.colors.onDarkPanelBorder else MaterialTheme.colorScheme.outline,
                                    forma,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = stringResource(R.string.volver),
                                tint = contentColor,
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = stringResource(R.string.volver),
                            tint = contentColor,
                        )
                    }
                }
            }
            if (title != null && titleAtStart) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (titleBadge != null) {
                    Spacer(Modifier.width(VoraTheme.spacing.s10))
                    titleBadge()
                }
            }
        }
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            actions()
            if (onClose != null) {
                IconButton(onClick = onClose) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.cerrar),
                        tint = contentColor,
                    )
                }
            }
        }
    }
}

/**
 * El encabezado grande de Profile, Settings y Nutrition Preferences: la flecha de volver
 * dentro de un circulo blanco y el titulo de pantalla (20 Bold) a su lado, alineado a la
 * izquierda, con el margen de pantalla de 20.
 *
 * @param title el titulo de la pantalla, de strings.xml ("Your goals", "Settings").
 * @param actions iconos a la derecha: el "···" de Progress / 03, 04 y 05.
 */
@Composable
fun VoraTopBarLarge(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = VoraTheme.spacing.screen, vertical = VoraTheme.spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s14),
    ) {
        VoraTopBarAction(
            icon = painterResource(R.drawable.ic_back),
            contentDescription = stringResource(R.string.volver),
            onClick = onBack,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .semantics { heading() },
        )
        actions()
    }
}

/**
 * Icono dentro de un circulo blanco con borde, el boton de [VoraTopBarLarge]: la flecha de
 * volver y el "···" de Progress / 03, 04 y 05.
 *
 * @param contentDescription de strings.xml, para TalkBack.
 */
@Composable
fun VoraTopBarAction(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(VoraTheme.sizes.backCircle)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(VoraTheme.sizes.border, MaterialTheme.colorScheme.outline, CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(painter = icon, contentDescription = contentDescription, tint = MaterialTheme.colorScheme.onSurface)
    }
}

@Preview
@Composable
private fun VoraTopBarPreview() {
    Parcialtp3Theme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                VoraTopBar(title = stringResource(R.string.pant_workout_03), onBack = {}, onClose = {})
                VoraTopBarLarge(title = stringResource(R.string.tab_profile), onBack = {})
            }
        }
    }
}
