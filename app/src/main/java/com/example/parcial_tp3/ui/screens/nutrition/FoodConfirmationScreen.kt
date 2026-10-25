package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.FieldError
import com.example.parcial_tp3.domain.MealField
import com.example.parcial_tp3.domain.MealForm
import com.example.parcial_tp3.domain.model.MealType
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraChip
import com.example.parcial_tp3.ui.components.VoraInfoMessage
import com.example.parcial_tp3.ui.components.VoraPanel
import com.example.parcial_tp3.ui.components.VoraStepper
import com.example.parcial_tp3.ui.components.VoraTextField
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme
import java.time.LocalDate

/**
 * Nutrition / 07 — Food Confirmation ("Review Meal").
 *
 * Con ingredientes (llega de 05 o 06) se ve como el frame: tarjetas, totales y botones. Sin ellos
 * (carga manual o edición) el frame no tiene dibujo: van los campos de RF03 con los mismos
 * componentes. −/+, "Remove", "+ Add Ingredient" y "Save without logging" llaman a [onDemo]: los
 * ingredientes no se guardan (spec carga-y-eliminacion-de-comidas).
 *
 * @param confirmation null mientras el ViewModel todavía no la abrió.
 * @param ingredients los del borrador; vacío muestra los campos.
 */
@Composable
fun FoodConfirmationScreen(
    confirmation: ConfirmationState?,
    ingredients: List<DraftIngredient>,
    onBack: () -> Unit,
    onFormChange: (MealForm) -> Unit,
    onSave: () -> Unit,
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
        when {
            confirmation == null -> Unit
            confirmation.notFound -> VoraInfoMessage(
                text = stringResource(R.string.nutrition_confirm_not_found),
                icon = painterResource(R.drawable.ic_info),
            )
            else -> {
                if (ingredients.isNotEmpty()) {
                    // El botón mide ~46 con su relleno y el texto 19: con 6 a cada lado, el texto queda a
                    // los ~29,5 de las tarjetas y del panel que mide el frame.
                    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6)) {
                        IngredientList(ingredients, onDemo)
                        VoraButton(
                            text = stringResource(R.string.nutrition_confirm_add_ingredient),
                            onClick = onDemo,
                            style = VoraButtonStyle.Text,
                            size = VoraButtonSize.Medium,
                        )
                        TotalsPanel(confirmation.form)
                    }
                } else {
                    MealFields(confirmation, onFormChange)
                }
                confirmation.saveError?.let {
                    VoraInfoMessage(text = stringResource(it), icon = painterResource(R.drawable.ic_info))
                }
                Actions(confirmation, onSave, onDemo)
            }
        }
    }
}

@Composable
private fun IngredientList(ingredients: List<DraftIngredient>, onDemo: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
        ingredients.forEach { IngredientCard(it, onDemo) }
    }
}

// Tarjeta de 85 en el frame: padding 12, nombre y kcal arriba, y a 10 el selector y "Remove".
@Composable
private fun IngredientCard(ingredient: DraftIngredient, onDemo: () -> Unit) {
    VoraCard(modifier = Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s12) {
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 14 y no 15: "Grilled Chicken Breast" mide 145 en el frame (14 Medium) y "280 kcal" 55 (14 SemiBold).
                Text(
                    text = stringResource(ingredient.name),
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
                    onClick = onDemo,
                    style = VoraButtonStyle.TextDestructive,
                    size = VoraButtonSize.Small,
                )
            }
        }
    }
}

// Panel con padding 16 (no los 20 de VoraPanel) y 12 entre el encabezado, el divisor y los macros.
@Composable
private fun TotalsPanel(form: MealForm) {
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
                    text = stringResource(R.string.nutrition_confirm_total_kcal, form.kcal),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            HorizontalDivider(thickness = VoraTheme.sizes.border, color = VoraTheme.colors.onDarkSubtle)
            // En el frame las tres columnas quedan con el mismo espacio entre ellas (CARBS en x 151 de 318).
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MacroTotal(R.string.nutrition_confirm_protein, form.protein)
                MacroTotal(R.string.nutrition_confirm_carbs, form.carbs)
                MacroTotal(R.string.nutrition_confirm_fat, form.fat)
            }
        }
    }
}

@Composable
private fun MacroTotal(@StringRes label: Int, grams: String) {
    Column(Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = stringResource(R.string.nutrition_confirm_macro_value, grams),
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

/** Carga manual y edición: el frame no los dibuja, así que son los campos del tema, uno abajo del otro. */
@Composable
private fun MealFields(confirmation: ConfirmationState, onFormChange: (MealForm) -> Unit) {
    val form = confirmation.form
    val errors = confirmation.errors
    val numero = KeyboardOptions(keyboardType = KeyboardType.Number)
    val decimal = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16)) {
        VoraTextField(
            value = form.name,
            onValueChange = { onFormChange(form.copy(name = it)) },
            label = stringResource(R.string.nutrition_confirm_field_name),
            error = errors[MealField.NAME]?.let { stringResource(it.message()) },
        )
        MealTypeChips(
            selected = form.type,
            error = errors[MealField.TYPE]?.let { stringResource(R.string.nutrition_confirm_error_type) },
            onSelect = { onFormChange(form.copy(type = it)) },
        )
        VoraTextField(
            value = form.kcal,
            onValueChange = { onFormChange(form.copy(kcal = it)) },
            label = stringResource(R.string.nutrition_confirm_field_kcal),
            keyboardOptions = numero,
            error = errors[MealField.KCAL]?.let { stringResource(it.message()) },
        )
        VoraTextField(
            value = form.protein,
            onValueChange = { onFormChange(form.copy(protein = it)) },
            label = stringResource(R.string.nutrition_confirm_field_protein),
            keyboardOptions = decimal,
            error = errors[MealField.PROTEIN]?.let { stringResource(it.message()) },
        )
        VoraTextField(
            value = form.carbs,
            onValueChange = { onFormChange(form.copy(carbs = it)) },
            label = stringResource(R.string.nutrition_confirm_field_carbs),
            keyboardOptions = decimal,
            error = errors[MealField.CARBS]?.let { stringResource(it.message()) },
        )
        VoraTextField(
            value = form.fat,
            onValueChange = { onFormChange(form.copy(fat = it)) },
            label = stringResource(R.string.nutrition_confirm_field_fat),
            keyboardOptions = decimal,
            error = errors[MealField.FAT]?.let { stringResource(it.message()) },
        )
    }
}

@Composable
private fun MealTypeChips(selected: MealType?, error: String?, onSelect: (MealType) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4)) {
        Text(
            text = stringResource(R.string.nutrition_confirm_field_type),
            style = VoraTheme.text.fieldLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8),
        ) {
            MealType.entries.forEach { type ->
                VoraChip(text = type.label(), selected = type == selected, onClick = { onSelect(type) })
            }
        }
        if (error != null) {
            Text(text = error, style = VoraTheme.text.footnote, color = MaterialTheme.colorScheme.error)
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

/** "Add to Lunch" cuando se sabe el tipo de una comida nueva; "Save Meal" sin tipo o al editar. */
@StringRes
private fun ConfirmationState.saveLabel(): Int = when {
    mealId != NEW_MEAL_ID -> R.string.nutrition_confirm_save_meal
    else -> when (form.type) {
        MealType.BREAKFAST -> R.string.nutrition_confirm_add_breakfast
        MealType.LUNCH -> R.string.nutrition_confirm_add_lunch
        MealType.SNACK -> R.string.nutrition_confirm_add_snack
        MealType.DINNER -> R.string.nutrition_confirm_add_dinner
        null -> R.string.nutrition_confirm_save_meal
    }
}

@StringRes
private fun FieldError.message(): Int = when (this) {
    FieldError.REQUIRED -> R.string.nutrition_confirm_error_required
    FieldError.NOT_A_WHOLE_NUMBER -> R.string.nutrition_confirm_error_whole
    FieldError.NOT_A_NUMBER -> R.string.nutrition_confirm_error_number
    FieldError.NEGATIVE -> R.string.nutrition_confirm_error_negative
}

private val previewDate = LocalDate.of(2026, 10, 24)

@Preview(showBackground = true, widthDp = 393, heightDp = 770)
@Composable
private fun FoodConfirmationDraftPreview() {
    Parcialtp3Theme {
        FoodConfirmationScreen(
            confirmation = ConfirmationState(
                mealId = NEW_MEAL_ID,
                form = MealForm(
                    type = PhotoMealDraft.type,
                    date = previewDate,
                    kcal = PhotoMealDraft.kcal.toString(),
                    protein = PhotoMealDraft.proteinG.toInt().toString(),
                    carbs = PhotoMealDraft.carbsG.toInt().toString(),
                    fat = PhotoMealDraft.fatG.toInt().toString(),
                ),
            ),
            ingredients = PhotoMealDraft.ingredients,
            onBack = {},
            onFormChange = {},
            onSave = {},
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
                mealId = NEW_MEAL_ID,
                form = MealForm(date = previewDate),
                errors = mapOf(MealField.NAME to FieldError.REQUIRED, MealField.TYPE to FieldError.REQUIRED),
            ),
            ingredients = emptyList(),
            onBack = {},
            onFormChange = {},
            onSave = {},
            onDemo = {},
        )
    }
}
