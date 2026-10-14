package com.fic.driver_app_ti.navegation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.fic.driver_app_ti.ui.theme.session.Login
import com.fic.driver_app_ti.ui.theme.session.Registro
import com.fic.encubadorafic.ui.main.MainScreen
import com.fic.encubadorafic.ui.search.SearchScren
import com.fic.encubadorafic.ui.sessions.*

@Composable
fun Navigator(auth: AuthViewModel = viewModel()) {
    val state by auth.state.collectAsState()
    when (state.phase) {
        AuthPhase.Restoring -> RestoreSessionScreen(state, auth::restore, auth::logout)
        AuthPhase.Guest -> GuestNavigation(auth, state)
        AuthPhase.SignedIn -> {
            if (state.user?.verified != true) VerificationScreen(state, auth::refreshVerification, auth::resend, auth::logout)
            else key(state.user?.id) { SignedInNavigation(auth, state) }
        }
    }
    // Prevent navigating away while credentials or token revocation are in flight.
    BackHandler(enabled = state.busy) { }
}

@Composable
private fun GuestNavigation(auth: AuthViewModel, state: AuthUiState) {
    val backStack = rememberNavBackStack(Routes.Login)
    LaunchedEffect(state.registered) {
        if (state.registered) { backStack.clear(); backStack.add(Routes.Login) }
    }
    NavDisplay(backStack = backStack, onBack = {
        if (!state.busy && backStack.size > 1) { auth.clearMessages(); backStack.removeLastOrNull() }
    }, entryProvider = entryProvider {
        entry<Routes.Login> {
            Login(onLogin = auth::login, onGoToRegister = {
                if (!state.busy) { auth.clearMessages(); backStack.add(Routes.Register) }
            }, state = state)
        }
        entry<Routes.Register> {
            Registro(onRegister = auth::register, onBackToLogin = {
                if (!state.busy) { auth.clearMessages(); backStack.clear(); backStack.add(Routes.Login) }
            }, state = state)
        }
    })
}

@Composable
private fun SignedInNavigation(auth: AuthViewModel, state: AuthUiState) {
    val backStack = rememberNavBackStack(Routes.Main)
    Column(Modifier.fillMaxSize()) {
        if (state.busy || state.error != null) Box(Modifier.fillMaxWidth().statusBarsPadding().padding(12.dp)) { AuthFeedback(state) }
        Box(Modifier.weight(1f)) {
            NavDisplay(backStack = backStack, onBack = { if (!state.busy && backStack.size > 1) backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<Routes.Main> {
                        MainScreen(onNavigateToSearch = { if (!state.busy) backStack.add(Routes.Search) },
                            userName = state.user?.name.orEmpty(), onLogout = { if (!state.busy) auth.logout() })
                    }
                    entry<Routes.Search> { SearchScren() }
                })
        }
    }
}
