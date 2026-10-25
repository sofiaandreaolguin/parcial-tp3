package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraPanel
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Nutrition / 03 — Add Food. barra inferior la pone el Scaffold de MainActivity.
 *  El scroll cubre pantallas más bajas (UI03).
 * Cada control va a donde dice la tabla NAV03 de specs/PANTALLAS.md.
 */
@Composable
fun AddFoodScreen(
    onBack: () -> Unit,
    onVoice: () -> Unit,
    onPhoto: () -> Unit,
    onSearch: () -> Unit,
    onManual: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(
                start = VoraTheme.spacing.screen,
                end = VoraTheme.spacing.screen,
                top = VoraTheme.spacing.s12,
                bottom = VoraTheme.spacing.s24,
            ),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s24),
    ) {
        NutritionHeader(title = R.string.nutrition_add_title, onBack = onBack)
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16)) {
            VoicePanel(onVoice)
            OptionCard(
                icon = R.drawable.ic_camera,
                iconSize = VoraTheme.sizes.iconSmall,
                title = R.string.nutrition_add_photo,
                hint = R.string.nutrition_add_photo_hint,
                circleColor = VoraTheme.colors.mintTranslucent,
                onClick = onPhoto,
            )
            OptionCard(
                icon = R.drawable.ic_search,
                iconSize = VoraTheme.sizes.iconSmall,
                title = R.string.nutrition_add_search,
                hint = R.string.nutrition_add_search_hint,
                onClick = onSearch,
            )
            OptionCard(
                icon = R.drawable.ic_pen,
                iconSize = VoraTheme.sizes.icon,
                title = R.string.nutrition_add_manual,
                hint = R.string.nutrition_add_manual_hint,
                onClick = onManual,
            )
        }
    }
}

@Composable
private fun VoicePanel(onVoice: () -> Unit) {
    VoraPanel(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.width(QUOTE_WIDTH),
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4),
            ) {
                Text(
                    text = stringResource(R.string.nutrition_add_vora_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(R.string.nutrition_add_vora_quote),
                    style = MaterialTheme.typography.bodyMedium,
                    color = VoraTheme.colors.onDarkMuted,
                )
            }
            // El frame dibuja el orbe de 44 (nodo 1:975); el tema todavía no tiene ese tamaño.
            VoraOrb(size = VoraTheme.sizes.orbHome, onDark = true)
        }
        VoraButton(
            text = stringResource(R.string.nutrition_add_voice),
            onClick = onVoice,
            modifier = Modifier.fillMaxWidth(),
            style = VoraButtonStyle.Cta,
            size = VoraButtonSize.Medium,
            icon = painterResource(R.drawable.ic_mic),
        )
    }
}

@Composable
private fun OptionCard(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes hint: Int,
    iconSize: Dp,
    onClick: () -> Unit,
    circleColor: Color = MaterialTheme.colorScheme.background,
) {
    VoraCard(modifier = Modifier.fillMaxWidth(), radius = VoraTheme.radius.r20, onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
        ) {
            // Círculo de 48 del nodo 1:985; coincide con touchTarget, que no es su significado.
            Box(
                modifier = Modifier
                    .size(VoraTheme.sizes.touchTarget)
                    .background(circleColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2),
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(hint),
                    style = VoraTheme.text.footnote,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier.size(VoraTheme.sizes.icon),
                tint = VoraTheme.colors.textMuted,
            )
        }
    }
}

// Ancho del texto del panel (nodo 1:972); el tema no tiene ese tamaño.
private val QUOTE_WIDTH = 220.dp

@Preview(showBackground = true, widthDp = 396, heightDp = 770)
@Composable
private fun AddFoodScreenPreview() {
    Parcialtp3Theme {
        AddFoodScreen(onBack = {}, onVoice = {}, onPhoto = {}, onSearch = {}, onManual = {})
    }
}
