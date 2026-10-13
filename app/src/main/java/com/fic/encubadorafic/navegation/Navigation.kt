package com.fic.driver_app_ti.navegation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.fic.driver_app_ti.ui.theme.session.Login
import com.fic.driver_app_ti.ui.theme.session.Registro
import com.fic.encubadorafic.ui.main.MainScreen
import com.fic.encubadorafic.ui.search.SearchScren

@Composable
fun Navigator() {
    val backStack = rememberNavBackStack(Routes.Login)

    NavDisplay(
        backStack = backStack,
        onBack = {
            backStack.back()
        },
        entryProvider = entryProvider {
            entry<Routes.Login> {
                Login(onLogin = { usuario, password ->
                    backStack.navigateTo(Routes.Main)
                }, onGoToRegister = {
                    backStack.navigateTo(Routes.Register)
                })
            }

            entry<Routes.Main> {
                MainScreen(
                    onNavigateToSearch = {
                        backStack.navigateTo(Routes.Search)
                    }
                )
            }

            entry<Routes.Search> {
                SearchScren()
            }

            entry<Routes.Register> {
                Registro(
                    onRegister = { nombre, usuario, password ->

                    },
                    onBackToLogin = {

                    }
                )
        }
    })
}
