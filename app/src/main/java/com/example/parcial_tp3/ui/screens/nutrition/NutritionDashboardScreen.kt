package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraInfoMessage
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraPanel
import com.example.parcial_tp3.ui.components.VoraProgressBar
import com.example.parcial_tp3.ui.components.VoraProgressRing
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Nutrition / 01 — Dashboard (nodo 1:730 del Figma). Con barra inferior: la pone el Scaffold de
 * MainActivity. Más alta que 850 dp, así que va con scroll (UI03). Cada control va a donde dice
 * la tabla NAV03 de specs/PANTALLAS.md.
 */
@Composable
fun NutritionDashboardScreen(
    state: NutritionUiState,
    onCalendar: () -> Unit,
    onHistory: () -> Unit,
    onMeal: (mealId: Long) -> Unit,
    onAddDinner: () -> Unit,
    onVoice: () -> Unit,
    onPhoto: () -> Unit,
    onSearch: () -> Unit,
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
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s20),
    ) {
        Header(stringResource(state.date), onCalendar)
        CaloriesCard(state)
        VoraInfoMessage(text = stringResource(state.advice))
        MealsSection(state, onHistory, onMeal, onAddDinner)
        VoraPanelCard(onVoice, onPhoto, onSearch)
    }
}

@Composable
private fun Header(date: String, onCalendar: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2)) {
            Text(
                text = date,
                style = VoraTheme.text.label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.tab_nutrition),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )
        }
        CalendarButton(onCalendar)
    }
}

// El frame lo dibuja blanco con borde (nodo 1:745); VoraCircleButton no tiene ese tono.
@Composable
private fun CalendarButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(VoraTheme.sizes.circleButton)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(VoraTheme.sizes.border, MaterialTheme.colorScheme.outline, CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_calendar),
            contentDescription = stringResource(R.string.nutrition_dash_open_calendar),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(VoraTheme.sizes.icon),
        )
    }
}

@Composable
private fun CaloriesCard(state: NutritionUiState) {
    VoraCard(modifier = Modifier.fillMaxWidth(), radius = VoraTheme.radius.r20) {
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4),
                ) {
                    Text(
                        text = stringResource(R.string.nutrition_dash_calories_label),
                        style = VoraTheme.text.fieldLabel,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(state.caloriesConsumed),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = stringResource(state.caloriesGoal),
                        style = VoraTheme.text.footnote,
                        color = VoraTheme.colors.textMuted,
                    )
                }
                VoraProgressRing(progress = state.caloriesProgress) {
                    Text(
                        text = stringResource(state.caloriesPercent),
                        style = VoraTheme.text.homeValue,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
                state.macros.forEach { macro -> MacroColumn(macro, Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun MacroColumn(macro: MacroSummary, modifier: Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6)) {
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2)) {
            Text(
                text = stringResource(macro.label),
                style = VoraTheme.text.fieldLabel,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = stringResource(macro.consumed),
                    style = VoraTheme.text.panelLabel,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = stringResource(macro.goal),
                    style = MaterialTheme.typography.labelSmall,
                    color = VoraTheme.colors.textMuted,
                )
            }
        }
        VoraProgressBar(progress = macro.progress, color = macro.kind.color())
    }
}

@Composable
private fun MacroKind.color(): Color = when (this) {
    MacroKind.PROTEIN -> MaterialTheme.colorScheme.primary
    MacroKind.CARBS -> MaterialTheme.colorScheme.secondary
    MacroKind.FAT -> VoraTheme.colors.cta
}

@Composable
private fun MealsSection(
    state: NutritionUiState,
    onHistory: () -> Unit,
    onMeal: (Long) -> Unit,
    onAddDinner: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.nutrition_dash_meals),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )
            VoraButton(
                text = stringResource(R.string.nutrition_dash_history),
                onClick = onHistory,
                style = VoraButtonStyle.Text,
                size = VoraButtonSize.Small,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
            state.meals.forEach { meal -> MealRow(meal, onClick = { onMeal(meal.id) }) }
            PendingMealRow(state.pendingMeal, onAddDinner)
        }
    }
}

@Composable
private fun MealRow(meal: MealSummary, onClick: () -> Unit) {
    VoraCard(
        modifier = Modifier.fillMaxWidth(),
        padding = VoraTheme.spacing.s12,
        radius = VoraTheme.radius.r12,
        onClick = onClick,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2)) {
                Text(
                    text = stringResource(meal.name),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = stringResource(meal.items),
                    style = VoraTheme.text.footnote,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = stringResource(meal.kcal),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
private fun PendingMealRow(meal: PendingMealSummary, onAdd: () -> Unit) {
    VoraCard(
        modifier = Modifier.fillMaxWidth(),
        padding = VoraTheme.spacing.s12,
        radius = VoraTheme.radius.r12,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2)) {
                Text(
                    text = stringResource(meal.name),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = stringResource(meal.hint),
                    style = VoraTheme.text.footnote,
                    color = VoraTheme.colors.textMuted,
                )
            }
            VoraButton(
                text = stringResource(R.string.nutrition_dash_add),
                onClick = onAdd,
                size = VoraButtonSize.Compact,
            )
        }
    }
}

@Composable
private fun VoraPanelCard(onVoice: () -> Unit, onPhoto: () -> Unit, onSearch: () -> Unit) {
    VoraPanel(Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s16) {
        // Un solo hijo: el panel separa 16 y el frame separa 12 (nodo 1:808).
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
            ) {
                VoraOrb(size = VoraTheme.sizes.orbSmall, onDark = true)
                Text(
                    text = stringResource(R.string.nutrition_dash_vora_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            Text(
                text = stringResource(R.string.nutrition_dash_vora_body),
                style = MaterialTheme.typography.bodyMedium,
                color = VoraTheme.colors.onDarkMuted,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                VoraButton(
                    text = stringResource(R.string.nutrition_dash_voice),
                    onClick = onVoice,
                    modifier = Modifier.weight(VOICE_WEIGHT),
                    style = VoraButtonStyle.Cta,
                    size = VoraButtonSize.Compact,
                    icon = painterResource(R.drawable.ic_mic),
                )
                VoraButton(
                    text = stringResource(R.string.nutrition_dash_photo),
                    onClick = onPhoto,
                    modifier = Modifier.weight(PHOTO_WEIGHT),
                    style = VoraButtonStyle.SubtleOnDark,
                    size = VoraButtonSize.Compact,
                    icon = painterResource(R.drawable.ic_camera),
                )
                VoraButton(
                    text = stringResource(R.string.nutrition_dash_search),
                    onClick = onSearch,
                    modifier = Modifier.weight(SEARCH_WEIGHT),
                    style = VoraButtonStyle.SubtleOnDark,
                    size = VoraButtonSize.Compact,
                    icon = painterResource(R.drawable.ic_search),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1013)
@Composable
private fun NutritionDashboardScreenPreview() {
    Parcialtp3Theme {
        NutritionDashboardScreen(
            state = nutritionDemoState(),
            onCalendar = {},
            onHistory = {},
            onMeal = {},
            onAddDinner = {},
            onVoice = {},
            onPhoto = {},
            onSearch = {},
        )
    }
}

// Los tres botones del panel miden lo mismo en el Figma, pero con el padding de Compact "Search" se
// corta a 360 dp. Se reparte el ancho según el largo de cada texto; la suma sigue siendo 3.
private const val VOICE_WEIGHT = 0.93f
private const val PHOTO_WEIGHT = 0.98f
private const val SEARCH_WEIGHT = 1.09f
