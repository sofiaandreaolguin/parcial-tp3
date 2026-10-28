package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.FieldError
import com.example.parcial_tp3.domain.IngredientField
import com.example.parcial_tp3.domain.IngredientForm
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraTextField
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * "+ Add Ingredient" de 07. No está en el Figma: es un `AlertDialog` de Material como el de
 * "Delete Meal", con los campos del tema (C99).
 */
@Composable
fun IngredientEntryDialog(
    entry: IngredientEntryState,
    onChange: (IngredientForm) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val form = entry.form
    val errors = entry.errors
    val numero = KeyboardOptions(keyboardType = KeyboardType.Number)
    AlertDialog(
        onDismissRequest = onDismiss,
        // El tema no define surfaceContainerHigh, el fondo por defecto del diálogo (ver FoodSearchSheet).
        containerColor = MaterialTheme.colorScheme.background,
        title = { Text(stringResource(R.string.nutrition_ingredient_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16)) {
                VoraTextField(
                    value = form.name,
                    onValueChange = { onChange(form.copy(name = it)) },
                    label = stringResource(R.string.nutrition_ingredient_field_name),
                    error = errors[IngredientField.NAME]?.let { stringResource(it.message()) },
                )
                VoraTextField(
                    value = form.grams,
                    onValueChange = { onChange(form.copy(grams = it)) },
                    label = stringResource(R.string.nutrition_ingredient_field_grams),
                    keyboardOptions = numero,
                    error = errors[IngredientField.GRAMS]
                        ?.let { stringResource(it.ingredientMessage(R.string.nutrition_ingredient_grams_range)) },
                )
                VoraTextField(
                    value = form.kcal,
                    onValueChange = { onChange(form.copy(kcal = it)) },
                    label = stringResource(R.string.nutrition_confirm_field_kcal),
                    keyboardOptions = numero,
                    error = errors[IngredientField.KCAL]
                        ?.let { stringResource(it.ingredientMessage(R.string.nutrition_ingredient_kcal_range)) },
                )
            }
        },
        confirmButton = {
            VoraButton(
                text = stringResource(R.string.nutrition_ingredient_add),
                onClick = onConfirm,
                size = VoraButtonSize.Medium,
            )
        },
        dismissButton = {
            VoraButton(
                text = stringResource(R.string.nutrition_ingredient_cancel),
                onClick = onDismiss,
                style = VoraButtonStyle.Outlined,
                size = VoraButtonSize.Medium,
            )
        },
    )
}

// validateIngredient usa NEGATIVE para "fuera de rango" (también el 0 en gramos): se muestra el rango.
@StringRes
private fun FieldError.ingredientMessage(@StringRes range: Int): Int =
    if (this == FieldError.NEGATIVE) range else message()

@Preview(showBackground = true)
@Composable
private fun IngredientEntryDialogPreview() {
    Parcialtp3Theme {
        IngredientEntryDialog(
            entry = IngredientEntryState(
                form = IngredientForm(name = "", grams = "0", kcal = "abc"),
                errors = mapOf(
                    IngredientField.NAME to FieldError.REQUIRED,
                    IngredientField.GRAMS to FieldError.NEGATIVE,
                    IngredientField.KCAL to FieldError.NOT_A_WHOLE_NUMBER,
                ),
            ),
            onChange = {},
            onConfirm = {},
            onDismiss = {},
        )
    }
}
