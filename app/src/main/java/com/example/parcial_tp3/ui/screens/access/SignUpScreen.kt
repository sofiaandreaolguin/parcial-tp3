package com.example.parcial_tp3.ui.screens.access

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.parcial_tp3.R
import com.example.parcial_tp3.ui.components.VoraAccessButton
import com.example.parcial_tp3.ui.components.VoraAccessCard
import com.example.parcial_tp3.ui.components.VoraAccessFooterLink
import com.example.parcial_tp3.ui.components.VoraAccessScaffold
import com.example.parcial_tp3.ui.components.VoraAccessTopBar
import com.example.parcial_tp3.ui.components.VoraBrandHeader
import com.example.parcial_tp3.ui.components.VoraSocialButton
import com.example.parcial_tp3.ui.components.VoraSocialButtonKind
import com.example.parcial_tp3.ui.components.VoraTextDivider
import com.example.parcial_tp3.ui.components.VoraTextField
import com.example.parcial_tp3.ui.components.VoraTextFieldVariant
import com.example.parcial_tp3.ui.theme.Parcialtp3Theme
import com.example.parcial_tp3.ui.theme.VoraTheme

/**
 * Login - 02 — SignUp (nodo 4002:2). Sin barra inferior. La flecha y "Sign In" vuelven a
 * SignIn; idioma, Apple, Google, biometria, "Password help?" y "Recover" muestran el aviso de
 * demostracion (specs/PANTALLAS.md, NAV03).
 *
 * El tilde de "8+ secure characters" se prende cuando la contrasena llega a 8: en el Figma esta
 * siempre prendido porque el frame es estatico.
 */
@Composable
fun SignUpScreen(
    state: AuthUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onSignUp: () -> Unit,
    onBack: () -> Unit,
    onSignIn: () -> Unit,
    onDemo: () -> Unit,
) {
    val access = VoraTheme.colors.access
    VoraAccessScaffold(
        footer = {
            VoraAccessFooterLink(
                question = stringResource(R.string.sign_up_have_account),
                link = stringResource(R.string.sign_up_sign_in_link),
                onClick = onSignIn,
                modifier = Modifier.padding(top = VoraTheme.spacing.s16, bottom = VoraTheme.spacing.s20),
            )
        },
    ) {
        Spacer(Modifier.height(VoraTheme.spacing.s8))
        VoraAccessTopBar(
            language = stringResource(R.string.access_language),
            onLanguage = onDemo,
            onBack = onBack,
        )
        Spacer(Modifier.height(VoraTheme.spacing.s20))
        VoraBrandHeader(compact = true)
        Spacer(Modifier.height(VoraTheme.spacing.s20))
        SignUpForm(state, onFullNameChange, onEmailChange, onPasswordChange, onTogglePassword, onSignUp, onDemo)
        Spacer(Modifier.height(VoraTheme.spacing.s16))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = VoraTheme.spacing.s4),
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s6, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_shield),
                contentDescription = null,
                tint = access.ink,
                modifier = Modifier.size(VoraTheme.sizes.accessIconTiny),
            )
            Text(
                text = stringResource(R.string.sign_up_biometric),
                style = VoraTheme.text.accessLabelLarge,
                color = access.ink,
            )
        }
        Spacer(Modifier.height(VoraTheme.spacing.s10))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s4, Alignment.CenterHorizontally),
        ) {
            Text(
                text = stringResource(R.string.sign_up_issues),
                style = VoraTheme.text.accessLabelLarge,
                color = access.ink,
            )
            Text(
                text = stringResource(R.string.sign_up_recover),
                style = VoraTheme.text.accessLabelLargeLink,
                color = access.ink,
                modifier = Modifier.clickable(role = Role.Button, onClick = onDemo),
            )
        }
    }
}

/** La tarjeta: nombre, email, contrasena, la nota de seguridad, "Sign Up →" y los sociales. */
@Composable
private fun SignUpForm(
    state: AuthUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onSignUp: () -> Unit,
    onDemo: () -> Unit,
) {
    val access = VoraTheme.colors.access
    VoraAccessCard {
        VoraTextField(
            value = state.fullName,
            onValueChange = onFullNameChange,
            label = stringResource(R.string.sign_up_name_label),
            placeholder = stringResource(R.string.sign_up_name_placeholder),
            leadingIcon = painterResource(R.drawable.ic_person),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
            variant = VoraTextFieldVariant.AccessOutlined,
            error = state.nameError?.let { stringResource(it) },
        )
        Spacer(Modifier.height(VoraTheme.spacing.s14))
        VoraTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = stringResource(R.string.access_email_label),
            placeholder = stringResource(R.string.access_email_placeholder),
            leadingIcon = painterResource(R.drawable.ic_mail),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            variant = VoraTextFieldVariant.AccessOutlined,
            error = state.emailError?.let { stringResource(it) },
        )
        Spacer(Modifier.height(VoraTheme.spacing.s14))
        VoraTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.access_password_label),
            placeholder = stringResource(R.string.sign_up_password_placeholder),
            leadingIcon = painterResource(R.drawable.ic_lock),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSignUp() }),
            variant = VoraTextFieldVariant.AccessOutlined,
            visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            error = state.passwordError?.let { stringResource(it) },
            trailing = {
                Icon(
                    painter = painterResource(R.drawable.ic_eye),
                    contentDescription = stringResource(
                        if (state.passwordVisible) R.string.access_hide_password else R.string.access_show_password,
                    ),
                    tint = access.ink,
                    // 20 dibujado; Compose extiende el toque a 48 (minimumTouchTargetSize)
                    modifier = Modifier
                        .size(VoraTheme.sizes.icon)
                        .clickable(role = Role.Button, onClick = onTogglePassword),
                )
            },
        )
        Spacer(Modifier.height(VoraTheme.spacing.s14))
        SecureNote(passwordLongEnough = state.passwordLongEnough, onHelp = onDemo)
        Spacer(Modifier.height(VoraTheme.spacing.s14))
        state.formError?.let {
            Text(text = stringResource(it), style = VoraTheme.text.footnote, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(VoraTheme.spacing.s14))
        }
        VoraAccessButton(
            text = stringResource(R.string.sign_up_action),
            onClick = onSignUp,
            compact = true,
            isLoading = state.isLoading,
        )
        Spacer(Modifier.height(VoraTheme.spacing.s14))
        VoraTextDivider(stringResource(R.string.sign_up_or), lines = false)
        Spacer(Modifier.height(VoraTheme.spacing.s14))
        Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s10)) {
            val alto = VoraTheme.sizes.socialButtonLarge
            VoraSocialButton(
                icon = painterResource(R.drawable.ic_apple),
                contentDescription = stringResource(R.string.access_apple),
                onClick = onDemo,
                modifier = Modifier.weight(1f),
                height = alto,
            )
            VoraSocialButton(
                icon = painterResource(R.drawable.ic_google),
                contentDescription = stringResource(R.string.access_google),
                onClick = onDemo,
                modifier = Modifier.weight(1f),
                kind = VoraSocialButtonKind.Brand,
                height = alto,
            )
            VoraSocialButton(
                icon = painterResource(R.drawable.ic_face_scan),
                contentDescription = stringResource(R.string.access_biometric),
                onClick = onDemo,
                modifier = Modifier.weight(1f),
                kind = VoraSocialButtonKind.Highlighted,
                height = alto,
            )
        }
    }
}

/** "✓ 8+ secure characters" a la izquierda y "Password help?" subrayado a la derecha. */
@Composable
private fun SecureNote(
    passwordLongEnough: Boolean,
    onHelp: () -> Unit,
) {
    val access = VoraTheme.colors.access
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = VoraTheme.spacing.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(VoraTheme.sizes.accessCheck)
                .background(if (passwordLongEnough) access.ink else access.outline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(VoraTheme.spacing.s2),
            )
        }
        Spacer(Modifier.size(VoraTheme.spacing.s6))
        Text(
            text = stringResource(R.string.sign_up_secure),
            style = VoraTheme.text.accessNote,
            color = access.ink,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(R.string.sign_up_password_help),
            style = VoraTheme.text.accessNoteLink,
            color = access.ink,
            modifier = Modifier.clickable(role = Role.Button, onClick = onHelp),
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 900)
@Composable
private fun SignUpScreenPreview() {
    Parcialtp3Theme {
        SignUpScreen(
            state = AuthUiState(),
            onFullNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePassword = {},
            onSignUp = {},
            onBack = {},
            onSignIn = {},
            onDemo = {},
        )
    }
}
