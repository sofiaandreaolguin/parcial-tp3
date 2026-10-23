package com.example.parcial_tp3.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/*
 * Piezas de Login - 01 y 02, que comparten fondo, encabezado, logo, boton principal y
 * botones sociales. Los valores salen de docs/figma/Login Flow.png (medidos a 2x) y de
 * VoraTheme.colors.access: estos frames no usan los verdes de la lamina.
 */

/**
 * Fondo de Login: verde muy claro con un resplandor menta arriba, al centro (centro a 75 dp
 * del borde y radio de 260 dp, medidos en el frame).
 */
@Composable
fun VoraAccessBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val access = VoraTheme.colors.access
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(access.background)
            .drawWithCache {
                val resplandor = Brush.radialGradient(
                    colors = listOf(access.glow, access.background),
                    center = Offset(size.width / 2, VoraTheme.sizes.accessGlowY.toPx()),
                    radius = VoraTheme.sizes.accessGlowRadius.toPx(),
                )
                onDrawBehind { drawRect(resplandor) }
            },
        content = content,
    )
}

/**
 * Fila de arriba de Login: circulo blanco de volver y selector de idioma "EN ⌄".
 *
 * @param onBack null en SignIn: es la primera pantalla y la flecha no tiene a donde volver
 *   (specs/PANTALLAS.md), asi que se dibuja sin accion.
 * @param language el codigo que muestra la pildora, de strings.xml.
 */
@Composable
fun VoraAccessTopBar(
    language: String,
    onLanguage: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    val access = VoraTheme.colors.access
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(VoraTheme.sizes.accessBack)
                .shadow(VoraTheme.sizes.accessShadow, CircleShape, ambientColor = access.ink, spotColor = access.ink)
                .background(colors.surface, CircleShape)
                .then(
                    if (onBack != null) Modifier.clickable(role = Role.Button, onClick = onBack) else Modifier,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = if (onBack != null) stringResource(R.string.volver) else null,
                tint = access.ink,
                modifier = Modifier.size(VoraTheme.sizes.accessBackIconWidth, VoraTheme.sizes.accessBackIconHeight),
            )
        }
        Row(
            modifier = Modifier
                .height(VoraTheme.sizes.languagePill)
                .shadow(VoraTheme.sizes.accessShadowSoft, RoundedCornerShape(VoraTheme.radius.pill), ambientColor = access.ink, spotColor = access.ink)
                .background(colors.surface, RoundedCornerShape(VoraTheme.radius.pill))
                .clickable(role = Role.Button, onClick = onLanguage)
                .padding(horizontal = VoraTheme.spacing.s14),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6),
        ) {
            Text(text = language, style = VoraTheme.text.fieldLabel, color = access.text)
            Icon(
                painter = painterResource(R.drawable.ic_chevron_down),
                contentDescription = null,
                tint = access.hint,
                modifier = Modifier.size(VoraTheme.sizes.accessChevron),
            )
        }
    }
}

/**
 * Logo de VORA de Login: esfera menta con un brillo al centro, aro blanco y la insignia "AI"
 * abajo a la derecha. No es el orbe del asistente (VoraOrb): el de Login no tiene aro de
 * degradado ni halo.
 */
@Composable
fun VoraBrandLogo(
    modifier: Modifier = Modifier,
    size: Dp = VoraTheme.sizes.accessLogo,
) {
    val access = VoraTheme.colors.access
    val blanco = MaterialTheme.colorScheme.surface
    val aro = VoraTheme.sizes.accessLogoRing
    Box(modifier.size(size)) {
        Canvas(
            Modifier
                .fillMaxSize()
                .shadow(VoraTheme.sizes.accessShadowLogo, CircleShape, ambientColor = access.orbDark, spotColor = access.orbDark),
        ) {
            val r = this.size.minDimension / 2
            drawCircle(blanco, r)
            val esfera = r - aro.toPx()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(access.orbLight, access.orbMid, access.orbDark),
                    center = Offset(center.x - esfera * 0.15f, center.y - esfera * 0.2f),
                    radius = esfera * 1.3f,
                ),
                radius = esfera,
            )
            // el brillo del centro mide un tercio del diametro de la esfera
            drawCircle(access.orbHighlight, esfera / 3)
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(VoraTheme.sizes.accessBadgeWidth, VoraTheme.sizes.accessBadgeHeight)
                .background(blanco, RoundedCornerShape(VoraTheme.radius.pill))
                .padding(VoraTheme.sizes.border)
                .background(access.ink, RoundedCornerShape(VoraTheme.radius.pill)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = stringResource(R.string.access_badge_ai), style = VoraTheme.text.accessBadge, color = access.badgeText)
        }
    }
}

/**
 * Logo, "VORA Coach", "ADAPTIVE INTELLIGENCE" y, si hay, la bajada. SignIn lleva bajada y
 * SignUp no.
 */
@Composable
fun VoraBrandHeader(
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    val access = VoraTheme.colors.access
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        VoraBrandLogo()
        Spacer(Modifier.height(VoraTheme.spacing.s14))
        Text(text = stringResource(R.string.access_brand), style = VoraTheme.text.accessTitle, color = access.ink)
        Spacer(Modifier.height(VoraTheme.spacing.s4))
        Text(text = stringResource(R.string.access_tagline), style = VoraTheme.text.accessTagline, color = access.accent)
        if (subtitle != null) {
            Spacer(Modifier.height(VoraTheme.spacing.s4))
            Text(
                text = subtitle,
                style = VoraTheme.text.accessBody,
                color = access.text,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = VoraTheme.sizes.accessSubtitle),
            )
        }
    }
}

/** Tarjeta blanca del formulario: radio 24, borde y padding 24. */
@Composable
fun VoraAccessCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(VoraTheme.radius.r24)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(VoraTheme.sizes.border, VoraTheme.colors.access.border, shape)
            .padding(VoraTheme.spacing.s24),
        content = content,
    )
}

/**
 * Boton principal de Login ("Sign In →", "Sign Up →"): lima, radio 16, 56 de alto, con una
 * sombra lima y la flecha despues del texto. No es [VoraButton]: ese es siempre una pildora.
 */
@Composable
fun VoraAccessButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val access = VoraTheme.colors.access
    val shape = RoundedCornerShape(VoraTheme.radius.r16)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(VoraTheme.sizes.accessCta)
            .shadow(VoraTheme.sizes.accessShadowCta, shape, ambientColor = access.cta, spotColor = access.cta)
            .clip(shape)
            .background(access.cta)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = VoraTheme.text.accessCta, color = access.ink)
        Spacer(Modifier.width(VoraTheme.spacing.s8))
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = access.ink,
            modifier = Modifier.size(VoraTheme.sizes.accessIconSmall),
        )
    }
}

/** "Or continue with" entre dos lineas finas. */
@Composable
fun VoraTextDivider(
    text: String,
    modifier: Modifier = Modifier,
) {
    val access = VoraTheme.colors.access
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(Modifier.weight(1f), thickness = VoraTheme.sizes.border, color = access.border)
        Text(
            text = text,
            style = VoraTheme.text.footnote,
            color = access.text,
            modifier = Modifier.padding(horizontal = VoraTheme.spacing.s12),
        )
        HorizontalDivider(Modifier.weight(1f), thickness = VoraTheme.sizes.border, color = access.border)
    }
}

/** De que tipo es un boton social: el icono va pintado, a color o en el boton menta. */
enum class VoraSocialButtonKind {
    /** Apple: icono relleno en el verde de Login. */
    Tinted,
    /** Google: el logo con sus cuatro colores, sin tinte. */
    Brand,
    /** Biometria: fondo y borde menta, icono verde oscuro. */
    Highlighted,
}

/** Uno de los tres botones de "Or continue with". Ocupa el ancho que le den (van con weight). */
@Composable
fun VoraSocialButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    kind: VoraSocialButtonKind = VoraSocialButtonKind.Tinted,
) {
    val access = VoraTheme.colors.access
    val shape = RoundedCornerShape(VoraTheme.radius.r16)
    val (fondo, borde) = if (kind == VoraSocialButtonKind.Highlighted) {
        access.biometric to access.biometricBorder
    } else {
        MaterialTheme.colorScheme.surface to access.border
    }
    Box(
        modifier = modifier
            .height(VoraTheme.sizes.socialButton)
            .clip(shape)
            .background(fondo)
            .border(VoraTheme.sizes.border, borde, shape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        val tamano = Modifier.size(VoraTheme.sizes.icon)
        when (kind) {
            VoraSocialButtonKind.Brand -> Image(icon, contentDescription, tamano)
            VoraSocialButtonKind.Tinted -> Icon(icon, contentDescription, tamano, tint = access.ink)
            VoraSocialButtonKind.Highlighted -> Icon(icon, contentDescription, tamano, tint = access.biometricIcon)
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun VoraAccessPreview() {
    Parcialtp3Theme {
        VoraAccessBackground {
            Column(
                Modifier.padding(VoraTheme.spacing.s24),
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s20),
            ) {
                VoraAccessTopBar(language = stringResource(R.string.access_language), onLanguage = {})
                VoraBrandHeader(subtitle = stringResource(R.string.sign_in_subtitle))
                VoraAccessCard {
                    VoraAccessButton(text = stringResource(R.string.sign_in_action), onClick = {})
                    Spacer(Modifier.height(VoraTheme.spacing.s20))
                    VoraTextDivider(stringResource(R.string.sign_in_or_continue))
                    Spacer(Modifier.height(VoraTheme.spacing.s20))
                    Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
                        VoraSocialButton(painterResource(R.drawable.ic_apple), "", {}, Modifier.weight(1f))
                        VoraSocialButton(painterResource(R.drawable.ic_google), "", {}, Modifier.weight(1f), VoraSocialButtonKind.Brand)
                        VoraSocialButton(painterResource(R.drawable.ic_face_scan), "", {}, Modifier.weight(1f), VoraSocialButtonKind.Highlighted)
                    }
                }
            }
        }
    }
}
