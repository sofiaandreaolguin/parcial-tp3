package com.example.parcial_tp3.ui.screens.nutrition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.FieldError
import com.example.parcial_tp3.domain.MealField
import com.example.parcial_tp3.domain.MealForm
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme
import java.time.LocalDate

/**
 * El pop-up de carga de "Log Manually" (03), "Type instead" (04) y la búsqueda (01 y 03). Como la
 * búsqueda, no es un destino del `NavHost`: se dibuja encima de la pantalla que lo abrió mientras
 * `manualEntry` no sea null. Lo escrito vive en el ViewModel, así que rotar no lo pierde (C97).
 *
 * @param onContinued navega a 07; se llama solo si el ViewModel abrió la confirmación.
 * @param onDarkScreen true sobre 04, que es oscura: la hoja es otra ventana y no hereda sus íconos claros.
 */
@Composable
fun ManualEntrySheetHost(
    grafo: NavBackStackEntry,
    onContinued: () -> Unit,
    viewModel: FoodCaptureViewModel = hiltViewModel(grafo),
    onDarkScreen: Boolean = false,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val entry = state.manualEntry ?: return
    ManualEntrySheet(
        entry = entry,
        onChange = viewModel::onManualEntryChange,
        onContinue = { if (viewModel.onContinueManualEntry()) onContinued() },
        onDismiss = viewModel::onDismissManualEntry,
        onDarkScreen = onDarkScreen,
    )
}

/** No está en el Figma: título, los campos del tema y dos botones (C99). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntrySheet(
    entry: ManualEntryState,
    onChange: (MealForm) -> Unit,
    onContinue: () -> Unit,
    onDismiss: () -> Unit,
    onDarkScreen: Boolean = false,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        // Sin esto la hoja sale lila: el tema no define surfaceContainerLow (ver FoodSearchSheet).
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        if (onDarkScreen) LightStatusIconsInSheet()
        ManualEntryContent(entry, onChange, onContinue, onDismiss)
    }
}

@Composable
private fun LightStatusIconsInSheet() {
    val view = LocalView.current
    DisposableEffect(view) {
        val window = generateSequence(view.parent) { it.parent }
            .filterIsInstance<DialogWindowProvider>().firstOrNull()?.window
        if (window != null) WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        onDispose {}
    }
}

@Composable
private fun ManualEntryContent(
    entry: ManualEntryState,
    onChange: (MealForm) -> Unit,
    onContinue: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = VoraTheme.spacing.s20, vertical = VoraTheme.spacing.s10),
        verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s20),
    ) {
        Text(stringResource(R.string.nutrition_manual_title), style = MaterialTheme.typography.titleMedium)
        MealFields(form = entry.form, errors = entry.errors, onFormChange = onChange)
        Column(verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
            VoraButton(
                text = stringResource(R.string.nutrition_manual_continue),
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
            )
            VoraButton(
                text = stringResource(R.string.nutrition_manual_cancel),
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
                style = VoraButtonStyle.Outlined,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun ManualEntryErrorsPreview() {
    Parcialtp3Theme {
        ManualEntryContent(
            entry = ManualEntryState(
                form = MealForm(date = LocalDate.of(2026, 10, 27), protein = "abc"),
                errors = mapOf(
                    MealField.NAME to FieldError.REQUIRED,
                    MealField.TYPE to FieldError.REQUIRED,
                    MealField.PROTEIN to FieldError.NOT_A_NUMBER,
                ),
            ),
            onChange = {},
            onContinue = {},
            onCancel = {},
        )
    }
}
