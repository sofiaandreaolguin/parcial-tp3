package com.example.parcial_tp3.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.Diet
import com.example.parcial_tp3.domain.model.Food
import com.example.parcial_tp3.domain.model.NutritionPriority
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraCard
import com.example.parcial_tp3.ui.components.VoraChip
import com.example.parcial_tp3.ui.components.VoraChipSelectedStyle
import com.example.parcial_tp3.ui.components.VoraOptionCard
import com.example.parcial_tp3.ui.components.VoraTextField
import com.example.parcial_tp3.ui.components.VoraTopBarLarge
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

@Composable
fun NutritionPreferencesScreen(
    state: NutritionPreferencesUiState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDemo: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        VoraTopBarLarge(title = stringResource(R.string.nutrition_title), onBack = onBack)
        Column(
            modifier = Modifier.padding(
                start = VoraTheme.spacing.screen,
                end = VoraTheme.spacing.screen,
                top = VoraTheme.spacing.s12,
                bottom = VoraTheme.spacing.s20,
            ),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12),
        ) {
            Text(
                text = stringResource(R.string.nutrition_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            ProfileSectionLabel(stringResource(R.string.nutrition_diet))
            ProfileChipRow {
                Diet.entries.forEach { diet ->
                    VoraChip(stringResource(diet.label()), selected = diet == state.diet, onClick = onDemo)
                }
            }
            ProfileSectionLabel(stringResource(R.string.nutrition_enjoy))
            ProfileChipRow {
                Food.entries.forEach { food ->
                    VoraChip(
                        text = stringResource(food.label()),
                        selected = food in state.foodsEnjoyed,
                        onClick = onDemo,
                        selectedStyle = VoraChipSelectedStyle.Outline,
                    )
                }
            }
            ProfileSectionLabel(stringResource(R.string.nutrition_avoid))
            VoraTextField(
                value = "",
                onValueChange = {},
                placeholder = stringResource(R.string.nutrition_add_ingredient),
                leadingIcon = painterResource(R.drawable.ic_plus),
                readOnly = true,
            )
            ProfileChipRow {
                state.foodsToAvoid.forEach { food ->
                    VoraChip(
                        text = food,
                        selected = false,
                        onClick = onDemo,
                        trailingIcon = painterResource(R.drawable.ic_remove),
                        trailingTint = MaterialTheme.colorScheme.error,
                    )
                }
            }
            ProfileSectionLabel(stringResource(R.string.nutrition_meals))
            VoraCard(modifier = Modifier.fillMaxWidth(), padding = VoraTheme.spacing.s12) {
                Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8)) {
                    ProfileInfoRow(stringResource(R.string.nutrition_meals_per_day), stringResource(R.string.nutrition_meals_per_day_value))
                    ProfileInfoRow(stringResource(R.string.nutrition_breakfast), stringResource(R.string.nutrition_breakfast_value))
                    ProfileInfoRow(stringResource(R.string.nutrition_dinner), stringResource(R.string.nutrition_dinner_value))
                }
            }
            ProfileSectionLabel(stringResource(R.string.nutrition_matters))
            NutritionPriority.entries.forEach { priority ->
                VoraOptionCard(
                    title = stringResource(priority.label()),
                    selected = priority in state.priorities,
                    onClick = onDemo,
                )
            }
            VoraButton(
                text = stringResource(R.string.profile_save_preferences),
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = VoraTheme.spacing.s8),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
private fun NutritionPreferencesScreenPreview() {
    Parcialtp3Theme {
        NutritionPreferencesScreen(
            state = NutritionPreferencesUiState(
                diet = Diet.EVERYTHING,
                foodsEnjoyed = Food.entries.toSet(),
                foodsToAvoid = listOf("Peanuts", "Shellfish", "Mushrooms"),
                priorities = NutritionPriority.entries.toSet(),
            ),
            onBack = {},
            onSave = {},
            onDemo = {},
        )
    }
}
