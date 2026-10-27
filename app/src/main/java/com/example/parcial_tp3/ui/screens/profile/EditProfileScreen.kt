package com.example.parcial_tp3.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import com.example.parcial_tp3.ui.screens.nutrition.NutritionDatePickerDialog
import com.example.parcial_tp3.ui.screens.nutrition.toPickerMillis
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.domain.model.Gender
import com.example.parcial_tp3.ui.components.VoraAvatar
import com.example.parcial_tp3.ui.components.VoraButton
import com.example.parcial_tp3.ui.components.VoraButtonSize
import com.example.parcial_tp3.ui.components.VoraButtonStyle
import com.example.parcial_tp3.ui.components.VoraInfoMessage
import com.example.parcial_tp3.ui.components.VoraTextField
import com.example.parcial_tp3.ui.components.VoraTopBarLarge
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EditProfileScreen(
    state: EditProfileUiState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDemo: () -> Unit,
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onHeightChange: (Int) -> Unit = {},
    onWeightChange: (Int) -> Unit = {},
    onBirthDateChange: (LocalDate) -> Unit = {},
    onGenderChange: (Gender) -> Unit = {},
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showGenderMenu by rememberSaveable { mutableStateOf(false) }
    val locale = LocalConfiguration.current.locales[0]
    val pattern = stringResource(R.string.edit_birth_date_pattern)
    val formatter = remember(pattern, locale) { DateTimeFormatter.ofPattern(pattern, locale) }
    val birthDate = state.birthDate.format(formatter)
    if (showDatePicker) {
        NutritionDatePickerDialog(
            selected = state.birthDate,
            onConfirm = {
                onBirthDateChange(it)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            selectableDates = NoFutureDates,
            confirmText = stringResource(R.string.edit_date_confirm),
            cancelText = stringResource(R.string.edit_date_cancel),
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        VoraTopBarLarge(title = stringResource(R.string.profile_edit), onBack = onBack)
        Column(
            modifier = Modifier.padding(
                start = VoraTheme.spacing.screen,
                end = VoraTheme.spacing.screen,
                bottom = VoraTheme.spacing.s20,
            ),
            verticalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s16),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                VoraAvatar(
                    modifier = Modifier.padding(top = VoraTheme.spacing.s12),
                    size = VoraTheme.sizes.avatarLarge,
                )
                VoraButton(
                    text = stringResource(R.string.edit_change_photo),
                    onClick = onDemo,
                    style = VoraButtonStyle.Text,
                    size = VoraButtonSize.Small,
                )
            }
            VoraTextField(
                value = state.firstName,
                onValueChange = onFirstNameChange,
                label = stringResource(R.string.edit_first_name),
                error = state.firstNameError?.let { stringResource(it) },
            )
            VoraTextField(
                value = state.lastName,
                onValueChange = onLastNameChange,
                label = stringResource(R.string.edit_last_name),
            )
            PickerField(
                label = stringResource(R.string.edit_birth_date),
                value = birthDate,
                onClick = { showDatePicker = true },
                error = state.birthDateError?.let { stringResource(it) },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
                NumberField(
                    label = stringResource(R.string.edit_height),
                    unit = stringResource(R.string.edit_unit_cm),
                    value = state.heightCm,
                    onValueChange = onHeightChange,
                    error = state.heightError,
                    modifier = Modifier.weight(1f),
                )
                NumberField(
                    label = stringResource(R.string.edit_weight),
                    unit = stringResource(R.string.edit_unit_kg),
                    value = state.weightKg,
                    onValueChange = onWeightChange,
                    error = state.weightError,
                    modifier = Modifier.weight(1f),
                )
            }
            var fieldWidth by remember { mutableStateOf(0.dp) }
            val density = LocalDensity.current
            Box(Modifier.onSizeChanged { fieldWidth = with(density) { it.width.toDp() } }) {
                PickerField(
                    label = stringResource(R.string.edit_gender),
                    value = stringResource(state.gender.label()),
                    onClick = { showGenderMenu = true },
                )
                DropdownMenu(
                    expanded = showGenderMenu,
                    onDismissRequest = { showGenderMenu = false },
                    modifier = Modifier.width(fieldWidth),
                    shape = RoundedCornerShape(VoraTheme.radius.r12),
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    Gender.entries.forEach { gender ->
                        DropdownMenuItem(
                            text = { Text(stringResource(gender.label())) },
                            onClick = {
                                onGenderChange(gender)
                                showGenderMenu = false
                            },
                        )
                    }
                }
            }
            VoraInfoMessage(
                text = stringResource(R.string.edit_info),
                icon = painterResource(R.drawable.ic_info),
            )
            state.saveError?.let {
                Text(text = stringResource(it), style = VoraTheme.text.footnote, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(VoraTheme.spacing.s8))
            VoraButton(
                text = stringResource(R.string.edit_save),
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(R.string.edit_footer),
                style = VoraTheme.text.footnote,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private object NoFutureDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= LocalDate.now().toPickerMillis()
    override fun isSelectableYear(year: Int) = year <= LocalDate.now().year
}

/** Entero de 0 en adelante: vacio es 0, que la validacion rechaza (C21). */
@Composable
private fun NumberField(
    label: String,
    unit: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    error: Int?,
    modifier: Modifier = Modifier,
) {
    VoraTextField(
        value = if (value > 0) value.toString() else "",
        onValueChange = { text -> onValueChange(text.filter(Char::isDigit).take(3).toIntOrNull() ?: 0) },
        modifier = modifier,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = remember(unit) { UnitSuffix(unit) },
        error = error?.let { stringResource(it) },
    )
}

// El Figma muestra "178 cm" dentro del campo; la unidad se dibuja pero no forma parte del valor.
private class UnitSuffix(private val unit: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        if (text.isEmpty()) return TransformedText(text, OffsetMapping.Identity)
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = offset
            override fun transformedToOriginal(offset: Int) = offset.coerceAtMost(text.length)
        }
        return TransformedText(text + AnnotatedString(" $unit"), mapping)
    }
}

/** Se ve como [VoraTextField]; el toque abre un selector en vez del teclado. */
@Composable
private fun PickerField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
) {
    Box(modifier) {
        VoraTextField(value = value, onValueChange = {}, label = label, readOnly = true, error = error)
        // el campo de texto se come el toque: una capa encima lo recibe. Cubre solo el campo (la etiqueta mide 18)
        Box(
            Modifier
                .matchParentSize()
                .padding(top = VoraTheme.spacing.s20)
                .clickable(role = Role.DropdownList, onClick = onClick),
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun EditProfileScreenPreview() {
    Parcialtp3Theme {
        EditProfileScreen(
            state = EditProfileUiState(
                firstName = "Alex",
                lastName = "Morgan",
                birthDate = LocalDate.of(1995, 3, 14),
                heightCm = 178,
                weightKg = 76,
                gender = Gender.PREFER_NOT_TO_SAY,
            ),
            onBack = {},
            onSave = {},
            onDemo = {},
        )
    }
}
