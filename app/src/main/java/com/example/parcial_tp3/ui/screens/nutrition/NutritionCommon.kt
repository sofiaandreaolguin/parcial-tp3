package com.example.parcial_tp3.ui.screens.nutrition

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.MealType
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraInfoMessage
import com.example.parcial_tp3.ui.screens.workout.LoadingCard
import com.example.parcial_tp3.ui.theme.VoraTheme
import java.time.LocalDate

// Lo que comparten las pantallas de Nutrition.

@Composable
internal fun MealType.label(): String = stringResource(
    when (this) {
        MealType.BREAKFAST -> R.string.nutrition_dash_breakfast
        MealType.LUNCH -> R.string.nutrition_dash_lunch
        MealType.SNACK -> R.string.nutrition_dash_snack
        MealType.DINNER -> R.string.nutrition_dash_dinner
    },
)

/** Cargando o error con reintento, como Workout / 01. Content lo dibuja cada pantalla. */
@Composable
internal fun NutritionStatus(state: NutritionUiState, onRetry: () -> Unit) {
    when (state) {
        NutritionUiState.Loading -> LoadingCard(stringResource(R.string.nutrition_loading))
        is NutritionUiState.Error -> Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
            VoraInfoMessage(text = stringResource(state.message), icon = painterResource(R.drawable.ic_info))
            VoraButton(
                text = stringResource(R.string.nutrition_retry),
                onClick = onRetry,
                style = VoraButtonStyle.Outlined,
                size = VoraButtonSize.Small,
            )
        }
        is NutritionUiState.Content -> Unit
    }
}

/** El calendario de 01 (C52). Elegir una fecha y confirmar llama a [onConfirm]. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NutritionDatePickerDialog(
    selected: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = selected.toPickerMillis())
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = pickerState.selectedDateMillis
                    if (millis == null) onDismiss() else onConfirm(selectedMillisToLocalDate(millis))
                },
            ) { Text(stringResource(R.string.nutrition_date_confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.nutrition_date_cancel)) }
        },
    ) {
        DatePicker(state = pickerState)
    }
}

/**
 * PROTEIN, CARBS y FAT con sus gramos, en tres columnas con el mismo espacio entre ellas (en el panel
 * de 07, CARBS queda en x 151 de 318; en la tarjeta de 08, en x 146 de 310). Los gramos llegan como
 * texto: 07 muestra lo que dice el formulario.
 *
 * @param valueColor sin especificar toma el color del contenedor (blanco dentro de VoraPanel).
 */
@Composable
internal fun MacroTotalsRow(
    protein: String,
    carbs: String,
    fat: String,
    labelColor: Color,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Unspecified,
    valueStyle: TextStyle = MaterialTheme.typography.titleSmall,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        MacroTotal(R.string.nutrition_confirm_protein, protein, labelColor, valueColor, valueStyle)
        MacroTotal(R.string.nutrition_confirm_carbs, carbs, labelColor, valueColor, valueStyle)
        MacroTotal(R.string.nutrition_confirm_fat, fat, labelColor, valueColor, valueStyle)
    }
}

@Composable
private fun MacroTotal(@StringRes label: Int, grams: String, labelColor: Color, valueColor: Color, valueStyle: TextStyle) {
    Column(Modifier.semantics(mergeDescendants = true) {}) {
        Text(text = stringResource(label), style = MaterialTheme.typography.bodySmall, color = labelColor)
        Text(text = stringResource(R.string.nutrition_confirm_macro_value, grams), style = valueStyle, color = valueColor)
    }
}
