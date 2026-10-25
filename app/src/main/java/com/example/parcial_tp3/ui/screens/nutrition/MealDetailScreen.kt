package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.Meal
import com.example.parcial_tp3.domain.model.MealType
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraInfoMessage
import com.example.parcial_tp3.ui.components.VoraProgressRing
import com.example.parcial_tp3.ui.screens.workout.LoadingCard
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme
import java.time.LocalDate
import kotlin.math.roundToInt

/**
 * Nutrition / 08 — Meal Details. La barra inferior la pone el Scaffold de MainActivity y el scroll
 * cubre pantallas más bajas (UI03).
 *
 * Room guarda la comida con sus totales, sin ingredientes ni hora: "Ingredients" muestra una sola
 * tarjeta con la comida, y la hora y el consejo son los del frame, fijos (demostración). "Delete
 * Meal" no está en el Figma: es el control de RF04.
 */
@Composable
fun MealDetailScreen(
    state: MealDetailUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDeleteClick: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
) {
    val content = state as? MealDetailUiState.Content
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
        NutritionHeader(
            title = content?.meal?.type?.let { stringResource(it.labelRes()) }.orEmpty(),
            onBack = onBack,
            trailing = content?.let { stringResource(R.string.nutrition_detail_time_demo) },
        )
        when (state) {
            MealDetailUiState.Loading -> LoadingCard(stringResource(R.string.nutrition_loading))
            MealDetailUiState.NotFound -> InfoMessage(R.string.nutrition_confirm_not_found)
            is MealDetailUiState.Error -> InfoMessage(state.message)
            is MealDetailUiState.Content -> {
                TotalsCard(state.meal, state.kcalProgress)
                VoraInfoMessage(text = stringResource(R.string.nutrition_detail_advice))
                MealSection(state.meal)
                state.actionError?.let { InfoMessage(it) }
                Actions(onEdit, onDuplicate, onDeleteClick)
            }
        }
    }

    if (content?.confirmingDelete == true) {
        DeleteDialog(onConfirm = onConfirmDelete, onDismiss = onDismissDelete)
    }
}

@Composable
private fun InfoMessage(text: Int) {
    VoraInfoMessage(text = stringResource(text), icon = painterResource(R.drawable.ic_info))
}

// Tarjeta del frame: padding 20 y radio 20; "520 kcal" mide 127 dp de ancho (32 SemiBold).
@Composable
private fun TotalsCard(meal: Meal, progress: Float) {
    val ringDescription = stringResource(R.string.nutrition_detail_ring_desc, (progress * 100).roundToInt())
    VoraCard(
        modifier = Modifier.fillMaxWidth(),
        padding = VoraTheme.spacing.s20,
        radius = VoraTheme.radius.r20,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.semantics(mergeDescendants = true) {},
                    verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4),
                ) {
                    Text(
                        text = stringResource(R.string.nutrition_detail_total_label),
                        style = VoraTheme.text.fieldLabel,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(R.string.nutrition_kcal, meal.kcal),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                VoraProgressRing(
                    progress = progress,
                    size = VoraTheme.sizes.ringSmall,
                    strokeWidth = VoraTheme.sizes.ringStrokeSmall,
                    modifier = Modifier.semantics { contentDescription = ringDescription },
                )
            }
            HorizontalDivider(thickness = VoraTheme.sizes.border, color = MaterialTheme.colorScheme.outline)
            MacroTotalsRow(
                protein = meal.proteinG.roundToInt().toString(),
                carbs = meal.carbsG.roundToInt().toString(),
                fat = meal.fatG.roundToInt().toString(),
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                valueColor = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

// Una sola tarjeta: la comida guardada no tiene ingredientes. La línea gris de los gramos muestra la proteína.
@Composable
private fun MealSection(meal: Meal) {
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
        Text(
            text = stringResource(R.string.nutrition_detail_ingredients),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        VoraCard(
            modifier = Modifier
                .fillMaxWidth()
                .semantics(mergeDescendants = true) {},
            padding = VoraTheme.spacing.s12,
            radius = VoraTheme.radius.r12,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s2),
                ) {
                    Text(
                        text = meal.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(R.string.nutrition_detail_protein_line, meal.proteinG.roundToInt()),
                        style = MaterialTheme.typography.bodySmall,
                        color = VoraTheme.colors.textMuted,
                    )
                }
                Text(
                    text = stringResource(R.string.nutrition_kcal, meal.kcal),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun Actions(onEdit: () -> Unit, onDuplicate: () -> Unit, onDeleteClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
        DetailButton(R.string.nutrition_detail_edit, onEdit, VoraButtonStyle.Outlined)
        DetailButton(R.string.nutrition_detail_duplicate, onDuplicate, VoraButtonStyle.Outlined)
        DetailButton(R.string.nutrition_detail_delete, onDeleteClick, VoraButtonStyle.Destructive)
    }
}

// 43 de alto en el frame: el tamaño Medium (12 + 19 + 12).
@Composable
private fun DetailButton(text: Int, onClick: () -> Unit, style: VoraButtonStyle) {
    VoraButton(
        text = stringResource(text),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        style = style,
        size = VoraButtonSize.Medium,
    )
}

/** El Figma no dibuja la confirmación (RF04): es el diálogo de Material 3 con los textos del tema. */
@Composable
private fun DeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.nutrition_detail_delete_title)) },
        text = { Text(stringResource(R.string.nutrition_detail_delete_body)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.nutrition_detail_delete_confirm),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.nutrition_detail_delete_cancel)) }
        },
    )
}

@Composable
private fun previewMeal() = Meal(
    id = 1,
    name = stringResource(PhotoMealDraft.name),
    date = LocalDate.of(2026, 10, 24),
    type = MealType.LUNCH,
    kcal = PhotoMealDraft.kcal,
    proteinG = PhotoMealDraft.proteinG,
    carbsG = PhotoMealDraft.carbsG,
    fatG = PhotoMealDraft.fatG,
)

@Preview(showBackground = true, widthDp = 393, heightDp = 820)
@Composable
private fun MealDetailScreenPreview() {
    Parcialtp3Theme {
        MealDetailScreen(
            state = MealDetailUiState.Content(previewMeal()),
            onBack = {},
            onEdit = {},
            onDuplicate = {},
            onDeleteClick = {},
            onConfirmDelete = {},
            onDismissDelete = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 400)
@Composable
private fun MealDetailNotFoundPreview() {
    Parcialtp3Theme {
        MealDetailScreen(
            state = MealDetailUiState.NotFound,
            onBack = {},
            onEdit = {},
            onDuplicate = {},
            onDeleteClick = {},
            onConfirmDelete = {},
            onDismissDelete = {},
        )
    }
}
