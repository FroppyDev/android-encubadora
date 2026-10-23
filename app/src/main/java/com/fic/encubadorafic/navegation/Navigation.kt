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
            }, onForgotPassword = {
                if (!state.busy) { auth.clearMessages(); backStack.add(Routes.Recovery) }
            }, state = state)
        }
        entry<Routes.Recovery> { RecoveryScreen(onBack = { backStack.removeLastOrNull() }) }
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
                        LaunchedEffect(Unit) { auth.loadCategories() }
                        MainScreen(onNavigateToSearch = { if (!state.busy) backStack.add(Routes.Search) },
                            categories = state.categories, categoriesLoading = state.busy,
                            categoriesError = state.error, onReloadCategories = auth::loadCategories,
                            onCreateProject = { if (!state.busy) backStack.add(Routes.CreateProject) },
                            onNavigateToProject = { id -> if (!state.busy) backStack.add(Routes.ProjectDetail(id)) },
                            onNavigateToProfile = { if (!state.busy) { auth.clearMessages(); backStack.add(Routes.Profile) } },
                            onNavigateToCategories = { if (!state.busy) { auth.clearMessages(); backStack.add(Routes.Categories) } },
                            userName = state.user?.name.orEmpty(), onLogout = { if (!state.busy) auth.logout() })
                    }
                    entry<Routes.Profile> {
                        LaunchedEffect(Unit) { auth.loadProfile() }
                        com.fic.encubadorafic.ui.perfil.PerfilScreen(state, auth::saveProfile, auth::loadProfile,
                            onBack = { auth.clearMessages(); backStack.removeLastOrNull() },
                            onUploadImage = auth::uploadImage,
                            projectsContent = { com.fic.encubadorafic.ui.perfil.OwnProjects { id -> backStack.add(Routes.ProjectDetail(id)) } })
                    }
                    entry<Routes.Categories> {
                        LaunchedEffect(Unit) { auth.loadCategories() }
                        com.fic.encubadorafic.ui.categories.CategoriesScreen(state, auth::loadCategories,
                            onBack = { auth.clearMessages(); backStack.removeLastOrNull() })
                    }
                    entry<Routes.CreateProject> {
                        LaunchedEffect(Unit) { auth.loadCategories() }
                        com.fic.encubadorafic.ui.projects.CreateProjectRoute(
                            technologyOptions = state.technologies,
                            categories = state.categories.map { com.fic.encubadorafic.model.ProjectOption(it.id, it.name) },
                            categoriesLoading = state.busy, categoriesError = state.error, onReloadCategories = auth::loadCategories,
                            authorId = requireNotNull(state.user).id, authorName = requireNotNull(state.user).name,
                            onBack = { backStack.removeLastOrNull() }, onCreated = { id ->
                                backStack.removeLastOrNull()
                                backStack.add(Routes.ProjectDetail(id))
                            })
                    }
                    entry<Routes.ProjectDetail> { route ->
                        com.fic.encubadorafic.ui.projects.ProjectDetailRoute(route.id, state.user?.id, categories = state.categories,
                            onBack = { backStack.removeLastOrNull() })
                    }
                    entry<Routes.Search> { SearchScren(onBack = { backStack.removeLastOrNull() }, onOpen = { backStack.add(Routes.ProjectDetail(it)) }, categories = state.categories) }
                })
        }
    }
}
