package com.fic.driver_app_ti.navegation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Routes: NavKey {

    @Serializable
    data object Search: Routes()

    @Serializable
    data object Main: Routes()

    @Serializable
    data object Login: Routes()

    @Serializable
    data object Register: Routes()

}