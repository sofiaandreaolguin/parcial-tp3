package com.example.parcial_tp3.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraOrb
import com.example.parcial_tp3.ui.components.VoraPanel
import com.example.parcial_tp3.ui.components.VoraSegmentedControl
import com.example.parcial_tp3.ui.components.VoraSegmentedStyle
import com.example.parcial_tp3.ui.components.VoraStat
import com.example.parcial_tp3.ui.components.VoraStatRow
import com.example.parcial_tp3.ui.components.VoraTopBarAction
import com.example.parcial_tp3.ui.components.VoraTopBarLarge
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/** Progress / 04 — Nutrition Progress (frame 1:2032). Se llega desde "Nutrition · View >" de 02. */
@Composable
fun NutritionProgressScreen(
    state: NutritionProgress,
    onBack: () -> Unit,
    onMore: () -> Unit,
    onPeriod: () -> Unit,
    onMacro: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        VoraTopBarLarge(
            title = stringResource(R.string.progress_nutrition_title),
            onBack = onBack,
            actions = {
                VoraTopBarAction(
                    icon = painterResource(R.drawable.ic_more_horizontal),
                    contentDescription = stringResource(R.string.progress_more),
                    onClick = onMore,
                )
            },
        )
        Column(
            // el frame deja 22 entre el circulo de volver y el selector; VoraTopBarLarge deja 12
            modifier = Modifier.padding(
                start = VoraTheme.spacing.screen,
                top = VoraTheme.spacing.s10,
                end = VoraTheme.spacing.screen,
                bottom = VoraTheme.spacing.s24,
            ),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
        ) {
            // fijo en Week: los otros períodos no tienen datos y avisan "modo demostración"
            VoraSegmentedControl(
                options = listOf(
                    stringResource(R.string.progress_period_week),
                    stringResource(R.string.progress_period_month),
                    stringResource(R.string.progress_period_3_months),
                ),
                selectedIndex = 0,
                onSelect = { if (it != 0) onPeriod() },
            )
            VoraStatRow(state.metrics.map { VoraStat(stringResource(it.label), stringResource(it.value)) })
            TargetCard(state, onMacro)
            InsightPanel(stringResource(state.insight))
            Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                ProgressSectionLabel(stringResource(R.string.progress_nutrition_habits))
                state.habits.chunked(2).forEach { fila ->
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
                    ) {
                        fila.forEach { habit ->
                            HabitCard(
                                label = stringResource(habit.label),
                                value = stringResource(habit.value),
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Macros (fijo en Protein), el objetivo diario y los 7 días con HIT o MISS. */
@Composable
private fun TargetCard(state: NutritionProgress, onMacro: () -> Unit) {
    val dias = stringArrayResource(R.array.workout_week_initials)
    VoraCard(modifier = Modifier.fillMaxWidth(), radius = VoraTheme.radius.r20) {
        // fijo en Protein, el que dibuja el Figma; los otros avisan "modo demostración"
        VoraSegmentedControl(
            options = listOf(
                stringResource(R.string.progress_nutrition_macro_calories),
                stringResource(R.string.progress_nutrition_macro_protein),
                stringResource(R.string.progress_nutrition_macro_carbs),
                stringResource(R.string.progress_nutrition_macro_fat),
            ),
            selectedIndex = 1,
            onSelect = { if (it != 1) onMacro() },
            style = VoraSegmentedStyle.Tabs,
        )
        // 12 de separacion mas 12 de aire arriba del objetivo (nodos 1:2068 y 1:2078)
        Spacer(Modifier.height(VoraTheme.spacing.s24))
        Text(
            text = stringResource(state.target),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(VoraTheme.spacing.s12))
        Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
            state.targetHit.forEachIndexed { i, hit ->
                DayTarget(dias.getOrElse(i) { "" }, hit, Modifier.weight(1f))
            }
        }
    }
}

/** Un día: la inicial en un círculo (menta si llegó al objetivo, gris si no) y HIT o MISS abajo. */
@Composable
private fun DayTarget(initial: String, hit: Boolean, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6),
    ) {
        Box(
            modifier = Modifier
                .size(VoraTheme.sizes.dayTarget)
                .background(if (hit) VoraTheme.colors.mintTranslucent else colors.background, CircleShape)
                .border(VoraTheme.sizes.border, if (hit) colors.secondary else colors.outline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initial,
                style = VoraTheme.text.fieldLabel,
                fontWeight = FontWeight.Bold,
                color = if (hit) colors.primary else VoraTheme.colors.textMuted,
            )
        }
        Text(
            text = stringResource(if (hit) R.string.progress_nutrition_hit else R.string.progress_nutrition_miss),
            style = MaterialTheme.typography.labelSmall,
            color = if (hit) colors.secondary else VoraTheme.colors.textMuted,
        )
    }
}

/** "Nutrition Analysis": el orbe y la etiqueta en menta, y la cita en blanco. */
@Composable
private fun InsightPanel(insight: String) {
    VoraPanel(modifier = Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s16) {
        // VoraPanel separa 16; el frame pide 10 (nodo 1:2109)
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
            ) {
                VoraOrb(size = VoraTheme.sizes.orbInsight, onDark = true)
                Text(
                    text = stringResource(R.string.progress_nutrition_analysis),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            Text(text = insight, style = VoraTheme.text.advice)
        }
    }
}

/** Una tarjeta de "FOOD HABITS & PATTERNS": etiqueta chica arriba y el dato abajo. */
@Composable
private fun HabitCard(label: String, value: String, modifier: Modifier = Modifier) {
    VoraCard(modifier = modifier.semantics(mergeDescendants = true) {}, padding = VoraTheme.spacing.s12) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(VoraTheme.spacing.s4))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1000)
@Composable
private fun NutritionProgressScreenPreview() {
    Parcialtp3Theme {
        NutritionProgressScreen(
            state = progressDemoState().nutrition,
            onBack = {},
            onMore = {},
            onPeriod = {},
            onMacro = {},
        )
    }
}
