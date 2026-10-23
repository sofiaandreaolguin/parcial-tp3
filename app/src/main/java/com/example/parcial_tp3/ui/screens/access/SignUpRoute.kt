package com.example.parcial_tp3.ui.screens.access

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.example.parcial_tp3.ui.components.rememberDemoToast

/**
 * @param accessGraph la entrada del grafo de acceso: el mismo AuthViewModel que SignIn, asi lo
 *   escrito en una pantalla sigue en la otra.
 * @param onSignedIn navegar a Home sacando el acceso de la pila (NAV02): lo resuelve el grafo.
 * @param onBack volver a SignIn (la flecha y "Sign In").
 */
@Composable
fun SignUpRoute(
    accessGraph: NavBackStackEntry,
    onSignedIn: () -> Unit,
    onBack: () -> Unit,
) {
    val viewModel: AuthViewModel = hiltViewModel(accessGraph)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.signedIn) {
        if (state.signedIn) {
            viewModel.onNavigated()
            onSignedIn()
        }
    }
    val volver = {
        viewModel.onSwitchScreen()
        onBack()
    }
    // el atras del sistema hace lo mismo que la flecha: vuelve a SignIn sin errores viejos
    BackHandler(onBack = volver)
    SignUpScreen(
        state = state,
        onFullNameChange = viewModel::onFullNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePassword = viewModel::onTogglePasswordVisible,
        onSignUp = viewModel::signUp,
        onBack = volver,
        onSignIn = volver,
        onDemo = rememberDemoToast(),
    )
}
