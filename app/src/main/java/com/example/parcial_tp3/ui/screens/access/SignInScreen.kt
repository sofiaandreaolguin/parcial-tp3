package com.example.parcial_tp3.ui.screens.access

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
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
 * Login - 01 — SignIn. Sin barra inferior. La flecha de arriba no tiene a donde volver (es la
 * primera pantalla) y lo que no tiene pantalla (idioma, Apple, Google, biometria, "Forgot your
 * password?") muestra el aviso de demostracion (specs/PANTALLAS.md, NAV03).
 *
 * El enlace "Sign up here" queda al pie: en pantallas altas se separa del formulario y en las
 * bajas todo hace scroll (UI03).
 */
@Composable
fun SignInScreen(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onDemo: () -> Unit,
) {
    val access = VoraTheme.colors.access
    VoraAccessScaffold(
        footer = {
            VoraAccessFooterLink(
                question = stringResource(R.string.sign_in_no_account),
                link = stringResource(R.string.sign_in_sign_up_link),
                onClick = onSignUp,
                modifier = Modifier.padding(top = VoraTheme.spacing.s40, bottom = VoraTheme.spacing.s20),
            )
        },
    ) {
        Spacer(Modifier.height(VoraTheme.spacing.s8))
        VoraAccessTopBar(language = stringResource(R.string.access_language), onLanguage = onDemo)
        // 42 hasta el aro del logo, medido en los nodos del Figma
        Spacer(Modifier.height(VoraTheme.spacing.s40 + VoraTheme.spacing.s2))
        VoraBrandHeader(subtitle = stringResource(R.string.sign_in_subtitle))
        Spacer(Modifier.height(VoraTheme.spacing.s24 + VoraTheme.spacing.s4))
        SignInForm(state, onEmailChange, onPasswordChange, onTogglePassword, onSignIn, onDemo)
        Spacer(Modifier.height(VoraTheme.spacing.s16))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s8, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_shield_check),
                contentDescription = null,
                tint = access.shield,
                modifier = Modifier.size(VoraTheme.sizes.accessIconSmall),
            )
            Text(
                text = stringResource(R.string.sign_in_encrypted),
                style = VoraTheme.text.footnote,
                color = access.text,
            )
        }
    }
}

/** La tarjeta blanca: email, contrasena, "Sign In →" y los tres botones sociales. */
@Composable
private fun SignInForm(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onSignIn: () -> Unit,
    onDemo: () -> Unit,
) {
    val access = VoraTheme.colors.access
    VoraAccessCard {
        VoraTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = stringResource(R.string.access_email_label),
            placeholder = stringResource(R.string.access_email_placeholder),
            leadingIcon = painterResource(R.drawable.ic_mail),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            variant = VoraTextFieldVariant.Access,
            error = state.emailError?.let { stringResource(it) },
        )
        Spacer(Modifier.height(VoraTheme.spacing.s16))
        VoraTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.access_password_label),
            leadingIcon = painterResource(R.drawable.ic_lock_open),
            placeholder = stringResource(R.string.access_password_placeholder),
            placeholderStyle = VoraTheme.text.accessPasswordDots,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSignIn() }),
            variant = VoraTextFieldVariant.Access,
            visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            error = state.passwordError?.let { stringResource(it) },
            labelAction = {
                Text(
                    text = stringResource(R.string.sign_in_forgot),
                    style = VoraTheme.text.accessLink,
                    color = access.accent,
                    modifier = Modifier.clickable(role = Role.Button, onClick = onDemo),
                )
            },
            trailing = {
                Icon(
                    painter = painterResource(R.drawable.ic_eye),
                    contentDescription = stringResource(
                        if (state.passwordVisible) R.string.access_hide_password else R.string.access_show_password,
                    ),
                    tint = if (state.passwordVisible) access.ink else access.hint,
                    // 20 dibujado; Compose extiende el toque a 48 (minimumTouchTargetSize)
                    modifier = Modifier
                        .size(VoraTheme.sizes.icon)
                        .clickable(role = Role.Button, onClick = onTogglePassword),
                )
            },
        )
        Spacer(Modifier.height(VoraTheme.spacing.s24))
        VoraAccessButton(text = stringResource(R.string.sign_in_action), onClick = onSignIn)
        Spacer(Modifier.height(VoraTheme.spacing.s20))
        VoraTextDivider(stringResource(R.string.sign_in_or_continue))
        Spacer(Modifier.height(VoraTheme.spacing.s20))
        Row(horizontalArrangement = Arrangement.spacedBy(VoraTheme.spacing.s12)) {
            VoraSocialButton(
                icon = painterResource(R.drawable.ic_apple),
                contentDescription = stringResource(R.string.access_apple),
                onClick = onDemo,
                modifier = Modifier.weight(1f),
            )
            VoraSocialButton(
                icon = painterResource(R.drawable.ic_google),
                contentDescription = stringResource(R.string.access_google),
                onClick = onDemo,
                modifier = Modifier.weight(1f),
                kind = VoraSocialButtonKind.Brand,
            )
            VoraSocialButton(
                icon = painterResource(R.drawable.ic_face_scan),
                contentDescription = stringResource(R.string.access_biometric),
                onClick = onDemo,
                modifier = Modifier.weight(1f),
                kind = VoraSocialButtonKind.Highlighted,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 851)
@Composable
private fun SignInScreenPreview() {
    Parcialtp3Theme {
        SignInScreen(
            state = AuthUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePassword = {},
            onSignIn = {},
            onSignUp = {},
            onDemo = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun SignInScreenErrorPreview() {
    Parcialtp3Theme {
        SignInScreen(
            state = AuthUiState(
                email = "alex",
                emailError = R.string.access_error_email,
                passwordError = R.string.access_error_password,
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePassword = {},
            onSignIn = {},
            onSignUp = {},
            onDemo = {},
        )
    }
}
