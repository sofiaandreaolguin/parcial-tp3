package com.example.parcial_tp3.ui.screens.access

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.example.parcial_tp3.ui.components.rememberDemoToast

/**
 * @param accessGraph la entrada del grafo de acceso: el AuthViewModel es de ese grafo y no de
 *   esta pantalla, asi SignIn y SignUp comparten el formulario. Sin valor por omision a
 *   proposito: con `hiltViewModel()` a secas cada pantalla tendria el suyo.
 * @param onSignedIn navegar a Home sacando el acceso de la pila (NAV02): lo resuelve el grafo.
 */
@Composable
fun SignInRoute(
    accessGraph: NavBackStackEntry,
    onSignedIn: () -> Unit,
    onSignUp: () -> Unit,
) {
    val viewModel: AuthViewModel = hiltViewModel(accessGraph)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.signedIn) {
        if (state.signedIn) {
            viewModel.onNavigated()
            onSignedIn()
        }
    }
    SignInScreen(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePassword = viewModel::onTogglePasswordVisible,
        onSignIn = viewModel::signIn,
        onSignUp = {
            viewModel.onSwitchScreen()
            onSignUp()
        },
        onDemo = rememberDemoToast(),
    )
}
