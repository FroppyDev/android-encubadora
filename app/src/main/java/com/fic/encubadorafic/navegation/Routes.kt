package com.fic.driver_app_ti.navegation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Routes: NavKey {
    @Serializable data object CreateProject: Routes()
    @Serializable data class ProjectDetail(val id: Long): Routes()
    @Serializable data object Recovery: Routes()
    @Serializable data object Profile: Routes()
    @Serializable data object Categories: Routes()

    @Serializable
    data object Search: Routes()

    @Serializable
    data object Main: Routes()

    @Serializable
    data object Login: Routes()

    @Serializable
    data object Register: Routes()

}