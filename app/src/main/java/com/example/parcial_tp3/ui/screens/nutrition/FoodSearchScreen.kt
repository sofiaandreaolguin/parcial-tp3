package com.example.parcial_tp3.ui.screens.nutrition

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.FoodItem
import com.example.parcial_tp3.domain.model.Nutrients
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraInfoMessage
import com.example.parcial_tp3.ui.components.VoraTextField
import com.example.parcial_tp3.ui.screens.workout.LoadingCard
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * La busqueda de alimentos (EXT02) como **hoja inferior**, no como pantalla.
 *
 * No es un destino del `NavHost`: se abre encima de la pantalla que la llamo, que se sigue viendo
 * atras. Por eso el inventario sigue teniendo 37 pantallas y 33 rutas. La decision y el porque,
 * en `docs/decisiones/010-buscador-como-hoja.md`.
 *
 * Es el primer `ModalBottomSheet` del proyecto; el unico otro elemento flotante es el calendario
 * de `NutritionDatePickerDialog`, que es el de Material.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchSheet(
    estado: FoodSearchUiState,
    onQueryChange: (String) -> Unit,
    onFoodSelected: (FoodItem) -> Unit,
    onGramsChange: (String) -> Unit,
    onBackFromPortion: () -> Unit,
    onConfirm: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        // El tema no define surfaceContainerLow, que es lo que ModalBottomSheet usa por defecto,
        // asi que Material caia a su paleta base y la hoja salia lila. Se le pasa el fondo del
        // tema para que se vea como el resto de la app (UI01).
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        FoodSearchScreen(
            estado = estado,
            onQueryChange = onQueryChange,
            onFoodSelected = onFoodSelected,
            onGramsChange = onGramsChange,
            onBackFromPortion = onBackFromPortion,
            onConfirm = onConfirm,
            onRetry = onRetry,
        )
    }
}

/**
 * El contenido de la hoja, sin estado. Dos momentos: la lista de resultados y, con un alimento
 * elegido, el panel de la porcion. Es un cambio de estado, no navegacion.
 */
@Composable
fun FoodSearchScreen(
    estado: FoodSearchUiState,
    onQueryChange: (String) -> Unit,
    onFoodSelected: (FoodItem) -> Unit,
    onGramsChange: (String) -> Unit,
    onBackFromPortion: () -> Unit,
    onConfirm: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Con un alimento elegido, el atras vuelve a los resultados en vez de cerrar la hoja.
    BackHandler(enabled = estado.seleccionado != null, onBack = onBackFromPortion)

    Column(
        modifier = modifier
            .fillMaxWidth()
            // Altura fija: dejandola crecer con el contenido, la lista colapsaba y se veian
            // dos alimentos de doce.
            .fillMaxHeight(0.82f)
            .navigationBarsPadding()
            .padding(horizontal = VoraTheme.spacing.s20, vertical = VoraTheme.spacing.s10),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
    ) {
        Encabezado(
            titulo = estado.seleccionado?.name ?: stringResource(R.string.food_search_title),
            onVolver = onBackFromPortion.takeIf { estado.seleccionado != null },
        )

        if (estado.seleccionado != null) {
            PanelDePorcion(estado, onGramsChange, onConfirm)
            return@Column
        }

        VoraTextField(
            value = estado.query,
            onValueChange = onQueryChange,
            placeholder = stringResource(R.string.food_search_hint),
            leadingIcon = painterResource(R.drawable.ic_search),
            modifier = Modifier.fillMaxWidth(),
        )

        when {
            estado.cargando -> LoadingCard(stringResource(R.string.food_search_loading))
            estado.error != null -> Column(
                verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10),
            ) {
                VoraInfoMessage(
                    text = stringResource(mensajeDe(estado.error)),
                    icon = painterResource(R.drawable.ic_info),
                )
                VoraButton(
                    text = stringResource(R.string.food_search_retry),
                    onClick = onRetry,
                    style = VoraButtonStyle.Outlined,
                    size = VoraButtonSize.Small,
                )
            }
            estado.vacio -> VoraInfoMessage(
                text = stringResource(R.string.food_search_empty),
                icon = painterResource(R.drawable.ic_info),
            )
            else -> LazyColumn(modifier = Modifier.weight(1f)) {
                items(estado.items, key = { it.id }) { alimento ->
                    FilaDeAlimento(alimento) { onFoodSelected(alimento) }
                }
            }
        }
    }
}

@Composable
private fun Encabezado(titulo: String, onVolver: (() -> Unit)?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (onVolver != null) {
            IconButton(onClick = onVolver) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = stringResource(R.string.food_search_back_to_results),
                )
            }
        }
        Text(titulo, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun FilaDeAlimento(alimento: FoodItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = VoraTheme.spacing.s12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(alimento.name, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = stringResource(R.string.food_search_per_100g, alimento.per100g.kcal),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** El segundo momento: cuantos gramos se comieron y como quedan los nutrientes. */
@Composable
private fun PanelDePorcion(
    estado: FoodSearchUiState,
    onGramsChange: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
    ) {
        VoraTextField(
            value = estado.gramos,
            onValueChange = onGramsChange,
            label = stringResource(R.string.food_search_grams_label),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )

        estado.gramosError?.let {
            VoraInfoMessage(
                text = stringResource(mensajeDeGramos(it)),
                icon = painterResource(R.drawable.ic_info),
            )
        }

        estado.porcion?.let { porcion ->
            Text(
                text = stringResource(R.string.nutrition_kcal, porcion.kcal),
                style = MaterialTheme.typography.headlineSmall,
            )
            MacroTotalsRow(
                protein = porcion.proteinG.toFormText(),
                carbs = porcion.carbsG.toFormText(),
                fat = porcion.fatG.toFormText(),
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.weight(1f))

        VoraButton(
            text = stringResource(R.string.food_search_confirm),
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, widthDp = 394)
@Composable
private fun FoodSearchResultadosPreview() {
    Parcialtp3Theme {
        FoodSearchScreen(
            estado = FoodSearchUiState(
                items = listOf(
                    FoodItem("f05", "Pollo cocido", Nutrients(165, 31.0, 0.0, 3.6)),
                    FoodItem("f01", "Avena", Nutrients(389, 16.9, 66.3, 6.9)),
                ),
            ),
            onQueryChange = {}, onFoodSelected = {}, onGramsChange = {},
            onBackFromPortion = {}, onConfirm = {}, onRetry = {},
        )
    }
}
