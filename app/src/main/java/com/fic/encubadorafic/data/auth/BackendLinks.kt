package com.fic.encubadorafic.data.auth

import java.net.URI

object BackendLinks {
    fun passwordRecovery(baseUrl: String, debug: Boolean): String {
        val uri = runCatching { URI(baseUrl) }.getOrNull()
        require(uri != null && !uri.host.isNullOrBlank() && uri.userInfo == null &&
            uri.query == null && uri.fragment == null &&
            (uri.scheme == "https" || (debug && uri.scheme == "http" &&
                uri.host in setOf("localhost", "127.0.0.1", "10.0.2.2")))) {
            "No se ha configurado una dirección segura para recuperar tu contraseña."
        }
        val path = uri.path.trimEnd('/')
        require(path.endsWith("/api/v1")) { "La dirección de la API no es válida." }
        return URI(uri.scheme, null, uri.host, uri.port,
            path.removeSuffix("/api/v1") + "/forgot-password", null, null).toString()
    }
}
