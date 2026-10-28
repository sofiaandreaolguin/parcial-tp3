package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.IngredientForm
import com.example.parcial_tp3.domain.MealForm
import com.example.parcial_tp3.domain.model.Ingredient
import com.example.parcial_tp3.domain.model.MealType
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraInfoMessage
import com.example.parcial_tp3.ui.components.VoraPanel
import com.example.parcial_tp3.ui.components.VoraStepper
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme
import java.time.LocalDate

/**
 * Nutrition / 07 — Food Confirmation ("Review Meal").
 *
 * Siempre como el frame: tarjetas, totales y botones. Nombre, tipo y macros se cargan antes, en el
 * pop-up de carga ([MealFields]). Las kcal del panel son la suma de las tarjetas. −/+ y "Save
 * without logging" llaman a [onDemo] (spec carga-y-eliminacion-de-comidas).
 *
 * @param confirmation null mientras el ViewModel todavía no la abrió.
 */
@Composable
fun FoodConfirmationScreen(
    confirmation: ConfirmationState?,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onAddIngredient: () -> Unit,
    onRemoveIngredient: (index: Int) -> Unit,
    onIngredientChange: (IngredientForm) -> Unit,
    onConfirmIngredient: () -> Unit,
    onDismissIngredient: () -> Unit,
    onDemo: () -> Unit,
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
        NutritionHeader(title = R.string.nutrition_confirm_title, onBack = onBack)
        if (confirmation != null) {
            // El botón mide ~46 con su relleno y el texto 19: con 6 a cada lado, el texto queda a
            // los ~29,5 de las tarjetas y del panel que mide el frame.
            Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6)) {
                IngredientList(confirmation.ingredients, onRemoveIngredient, onDemo)
                VoraButton(
                    text = stringResource(R.string.nutrition_confirm_add_ingredient),
                    onClick = onAddIngredient,
                    style = VoraButtonStyle.Text,
                    size = VoraButtonSize.Medium,
                )
                TotalsPanel(confirmation.totalKcal, confirmation.form)
            }
            confirmation.saveError?.let {
                VoraInfoMessage(text = stringResource(it), icon = painterResource(R.drawable.ic_info))
            }
            Actions(confirmation, onSave, onDemo)
            confirmation.ingredientEntry?.let {
                IngredientEntryDialog(
                    entry = it,
                    onChange = onIngredientChange,
                    onConfirm = onConfirmIngredient,
                    onDismiss = onDismissIngredient,
                )
            }
        }
    }
}

@Composable
private fun IngredientList(ingredients: List<Ingredient>, onRemove: (index: Int) -> Unit, onDemo: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
        ingredients.forEachIndexed { i, ingredient -> IngredientCard(ingredient, onRemove = { onRemove(i) }, onDemo) }
    }
}

// Tarjeta de 85 en el frame: padding 12, nombre y kcal arriba, y a 10 el selector y "Remove".
@Composable
private fun IngredientCard(ingredient: Ingredient, onRemove: () -> Unit, onDemo: () -> Unit) {
    VoraCard(modifier = Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s12) {
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 14 y no 15: "Grilled Chicken Breast" mide 145 en el frame (14 Medium) y "280 kcal" 55 (14 SemiBold).
                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(R.string.nutrition_kcal, ingredient.kcal),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                VoraStepper(
                    value = stringResource(R.string.nutrition_confirm_grams, ingredient.grams),
                    onDecrease = onDemo,
                    onIncrease = onDemo,
                    onDark = false,
                    valueStyle = MaterialTheme.typography.labelLarge,
                    square = true,
                )
                VoraButton(
                    text = stringResource(R.string.nutrition_confirm_remove),
                    onClick = onRemove,
                    style = VoraButtonStyle.TextDestructive,
                    size = VoraButtonSize.Small,
                )
            }
        }
    }
}

// Panel con padding 16 (no los 20 de VoraPanel) y 12 entre el encabezado, el divisor y los macros.
@Composable
private fun TotalsPanel(totalKcal: Int, form: MealForm) {
    VoraPanel(modifier = Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s16) {
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) {},
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.nutrition_confirm_totals),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text = stringResource(R.string.nutrition_confirm_total_kcal, totalKcal.toString()),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            HorizontalDivider(thickness = VoraTheme.sizes.border, color = VoraTheme.colors.onDarkSubtle)
            MacroTotalsRow(
                protein = form.protein,
                carbs = form.carbs,
                fat = form.fat,
                labelColor = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun Actions(confirmation: ConfirmationState, onSave: () -> Unit, onDemo: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
        VoraButton(
            text = stringResource(confirmation.saveLabel()),
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
        )
        VoraButton(
            text = stringResource(R.string.nutrition_confirm_save_without),
            onClick = onDemo,
            modifier = Modifier.fillMaxWidth(),
            style = VoraButtonStyle.Outlined,
        )
    }
}

/**
 * "Add to Lunch" con el tipo elegido. "Save Meal" queda para cuando no hay tipo: desde la app solo
 * pasa si Android cerró el proceso en 06 y el borrador volvió vacío.
 */
@StringRes
private fun ConfirmationState.saveLabel(): Int = when (form.type) {
    MealType.BREAKFAST -> R.string.nutrition_confirm_add_breakfast
    MealType.LUNCH -> R.string.nutrition_confirm_add_lunch
    MealType.SNACK -> R.string.nutrition_confirm_add_snack
    MealType.DINNER -> R.string.nutrition_confirm_add_dinner
    null -> R.string.nutrition_confirm_save_meal
}

private val previewDate = LocalDate.of(2026, 10, 24)

@Preview(showBackground = true, widthDp = 393, heightDp = 770)
@Composable
private fun FoodConfirmationDraftPreview() {
    Parcialtp3Theme {
        FoodConfirmationScreen(
            confirmation = ConfirmationState(
                form = MealForm(
                    type = PhotoMealDraft.type,
                    date = previewDate,
                    kcal = PhotoMealDraft.kcal.toString(),
                    protein = PhotoMealDraft.proteinG.toInt().toString(),
                    carbs = PhotoMealDraft.carbsG.toInt().toString(),
                    fat = PhotoMealDraft.fatG.toInt().toString(),
                ),
                ingredients = NutritionDemoData.photoIngredients(),
            ),
            onBack = {},
            onSave = {},
            onAddIngredient = {},
            onRemoveIngredient = {},
            onIngredientChange = {},
            onConfirmIngredient = {},
            onDismissIngredient = {},
            onDemo = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 900)
@Composable
private fun FoodConfirmationEmptyPreview() {
    Parcialtp3Theme {
        FoodConfirmationScreen(
            confirmation = ConfirmationState(
                form = MealForm(date = previewDate),
            ),
            onBack = {},
            onSave = {},
            onAddIngredient = {},
            onRemoveIngredient = {},
            onIngredientChange = {},
            onConfirmIngredient = {},
            onDismissIngredient = {},
            onDemo = {},
        )
    }
}
