package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.FieldError
import com.example.parcial_tp3.domain.MealField
import com.example.parcial_tp3.domain.MealForm
import com.example.parcial_tp3.domain.model.MealType
import com.example.parcial_tp3.ui.components.VoraChip
import com.example.parcial_tp3.ui.components.VoraTextField
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Los campos del pop-up de carga: el Figma no los dibuja, así que son los del tema, uno abajo del
 * otro. Sin kcal: son la suma de los ingredientes que se agregan en 07.
 */
@Composable
internal fun MealFields(form: MealForm, errors: Map<MealField, FieldError>, onFormChange: (MealForm) -> Unit) {
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

@StringRes
internal fun FieldError.message(): Int = when (this) {
    FieldError.REQUIRED -> R.string.nutrition_confirm_error_required
    FieldError.NOT_A_WHOLE_NUMBER -> R.string.nutrition_confirm_error_whole
    FieldError.NOT_A_NUMBER -> R.string.nutrition_confirm_error_number
    FieldError.NEGATIVE -> R.string.nutrition_confirm_error_negative
}
