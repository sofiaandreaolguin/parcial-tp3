package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.MealType
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Nutrition / 02 — Today's Meals. Con barra inferior: la pone el Scaffold de
 * MainActivity. Con tres comidas más la cena no entra en 818 dp, así que va con scroll (UI03).
 * Cada control va a donde dice la tabla NAV03 de specs/PANTALLAS.md.
 *
 * Las comidas de Room de la fecha elegida en 01, agrupadas por tipo. Sin ingredientes guardados,
 * cada grupo lista sus comidas con nombre y kcal, y tocar una abre su detalle (08).
 */
@Composable
fun TodaysMealsScreen(
    state: NutritionUiState,
    onRetry: () -> Unit,
    onMeal: (mealId: Long) -> Unit,
    onAddMeal: () -> Unit,
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
        val content = state as? NutritionUiState.Content
        Header(total = content?.let { stringResource(R.string.nutrition_kcal, it.kcal) })
        if (content == null) {
            NutritionStatus(state, onRetry)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16)) {
                // En el orden del día, no en el de carga.
                MealType.entries.forEach { type ->
                    val meals = content.meals.filter { it.type == type }
                    if (meals.isNotEmpty()) MealGroupCard(type, meals, onMeal)
                }
                content.pending?.let { NotLoggedCard(it, onAddMeal) }
            }
        }
    }
}

@Composable
private fun Header(total: String?) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2)) {
            Text(
                text = stringResource(R.string.nutrition_meals_label),
                style = VoraTheme.text.label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.nutrition_dash_meals),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )
        }

        if (total != null) {
            Text(
                text = total,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.outline, RoundedCornerShape(VoraTheme.radius.pill))
                    .padding(horizontal = VoraTheme.spacing.s12, vertical = VoraTheme.spacing.s6),
            )
        }
    }
}

// La tarjeta del frame (encabezado con el tipo y las kcal, divisor y una línea por ingrediente);
// las líneas pasan a ser las comidas de ese tipo.
@Composable
private fun MealGroupCard(type: MealType, meals: List<MealRow>, onMeal: (Long) -> Unit) {
    VoraCard(modifier = Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s12) {
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = type.label(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = stringResource(R.string.nutrition_kcal, meals.sumOf { it.kcal }),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6)) {
                meals.forEach { meal -> MealLine(meal, onClick = { onMeal(meal.id) }) }
            }
        }
    }
}

@Composable
private fun MealLine(meal: MealRow, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = VoraTheme.spacing.s4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = meal.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(R.string.nutrition_kcal, meal.kcal),
            style = VoraTheme.text.fieldLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// Estado vacío de una comida: el frame lo dibuja sin relleno y con borde discontinuo (nodo 1:925),
// a diferencia de las registradas, que son VoraCard. Ningún componente común lo cubre todavía.
@Composable
private fun NotLoggedCard(type: MealType, onAdd: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .dashedBorder(MaterialTheme.colorScheme.outline, VoraTheme.radius.r16, VoraTheme.sizes.border)
            .padding(VoraTheme.spacing.s16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10),
    ) {
        Text(
            text = stringResource(R.string.nutrition_meals_type_empty, type.label()),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        VoraButton(
            text = stringResource(R.string.nutrition_meals_add_type, type.label()),
            onClick = onAdd,
            size = VoraButtonSize.Small,
        )
    }
}

// Se dibuja adentro del borde: un trazo centrado en el límite quedaría cortado a la mitad.
private fun Modifier.dashedBorder(color: Color, radius: Dp, width: Dp): Modifier = drawBehind {
    val stroke = width.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(stroke / 2, stroke / 2),
        size = Size(size.width - stroke, size.height - stroke),
        cornerRadius = CornerRadius(radius.toPx()),
        style = Stroke(
            width = stroke,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(DASH.toPx(), DASH.toPx())),
        ),
    )
}

// Medido sobre el nodo 1:925 a 1x: 4 px de borde y 4 px de hueco.
private val DASH = 4.dp

@Preview(showBackground = true, widthDp = 390, heightDp = 818)
@Composable
private fun TodaysMealsScreenPreview() {
    Parcialtp3Theme {
        TodaysMealsScreen(state = NutritionDemoData.previewState(), onRetry = {}, onMeal = {}, onAddMeal = {})
    }
}
