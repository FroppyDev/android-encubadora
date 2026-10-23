package com.fic.encubadorafic.data.auth

import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URI

class HttpAuthService(private val baseUrl: String, private val debug: Boolean) : AuthService {
    override fun register(name: String, email: String, password: String, confirmation: String) {
        request(
            "register", "POST", JSONObject().put("name", name).put("email", email)
                .put("password", password).put("password_confirmation", confirmation)
        )
    }

    override fun login(email: String, password: String, deviceName: String): LoginSession {
        val json = request(
            "login",
            "POST",
            JSONObject().put("email", email).put("password", password)
                .put("device_name", deviceName)
        )
        return parseResponse {
            LoginSession(
                parseUser(json.getJSONObject("data")),
                json.getString("token")
            )
        }
    }

    override fun user(token: String) =
        parseResponse { parseUser(request("user", "GET", token = token).getJSONObject("data")) }

    override fun updateProfile(token: String, name: String, bio: String?): AuthUser = parseResponse {
        parseUser(request("user", "PATCH", JSONObject().put("name", name)
            .put("bio", bio ?: JSONObject.NULL), token).getJSONObject("data"))
    }

    override fun categories(token: String, page: Int): CategoryPage = parseResponse {
        val json = request("categories?page=$page", "GET", token = token)
        val data = json.getJSONArray("data")
        CategoryPage((0 until data.length()).map { index ->
            val item = data.getJSONObject(index)
            // Legacy API has no active flag. Accept it until the backend adds is_active.
            val flag = when {
                item.has("is_active") -> item.opt("is_active")
                item.has("active") -> item.opt("active")
                else -> true
            }
            Category(item.getLong("id"), item.getString("name"),
                if (item.isNull("description")) null else item.getString("description"),
                flag == true || flag == 1 || flag == "1" || flag == "true")
        }, json.getInt("last_page"))
    }

    override fun uploadImage(token: String, field: String, bytes: ByteArray, mime: String): AuthUser {
        require(field == "avatar" || field == "cover")
        require(mime in listOf("image/jpeg", "image/png", "image/webp"))
        val boundary = "Android-${java.util.UUID.randomUUID()}"
        val extension = when (mime) { "image/png" -> "png"; "image/webp" -> "webp"; else -> "jpg" }
        val output = java.io.ByteArrayOutputStream()
        output.write(("--$boundary\r\nContent-Disposition: form-data; name=\"$field\"; filename=\"photo.$extension\"\r\n" +
            "Content-Type: $mime\r\n\r\n").toByteArray())
        output.write(bytes)
        output.write("\r\n--$boundary--\r\n".toByteArray())
        return parseResponse { parseUser(request("user/images", "POST", token = token,
            upload = output.toByteArray(), contentType = "multipart/form-data; boundary=$boundary").getJSONObject("data")) }
    }

    override fun technologies(token: String): List<com.fic.encubadorafic.model.ProjectOption> {
        val result = mutableListOf<com.fic.encubadorafic.model.ProjectOption>()
        var page = 1
        do {
            val json = request("technologies?page=$page", "GET", token = token)
            val items = json.getJSONArray("data")
            for (i in 0 until items.length()) {
                val item = items.getJSONObject(i)
                result.add(com.fic.encubadorafic.model.ProjectOption(item.getLong("id"), item.getString("name")))
            }
            val more = page < json.optInt("last_page", 1)
            page++
        } while (more)
        return result.distinctBy { it.id }
    }

    override fun resend(token: String) {
        request("email/verification-notification", "POST", JSONObject(), token)
    }

    override fun logout(token: String) {
        request("logout", "POST", JSONObject(), token)
    }

    private fun parseUser(json: JSONObject) = AuthUser(
        json.getLong("id"), json.getString("name"), json.getString("email"),
        json.has("email_verified_at") && !json.isNull("email_verified_at") && json.optString("email_verified_at")
            .isNotBlank(),
        if (json.isNull("bio")) null else json.getString("bio"),
        MediaUrls.resolve(json.optString("avatar_url").takeUnless { it == "null" }, baseUrl, debug),
        MediaUrls.resolve(json.optString("cover_image_url").takeUnless { it == "null" }, baseUrl, debug)
    )

    private fun endpoint(path: String): URI {
        val uri = runCatching { URI(baseUrl) }.getOrNull()
        if (uri == null || uri.host.isNullOrBlank() || uri.userInfo != null || uri.query != null || uri.fragment != null ||
            (uri.scheme != "https" && !(debug && uri.scheme == "http"))
        ) {
            throw AuthException(
                0,
                "Configura API_BASE_URL con la dirección del backend (HTTPS en producción)."
            )
        }
        return URI(baseUrl.trimEnd('/') + "/" + path)
    }

    fun request(
        path: String,
        method: String,
        body: JSONObject? = null,
        token: String? = null,
        upload: ByteArray? = null,
        contentType: String? = null,
    ): JSONObject {
        val connection = endpoint(path).toURL().openConnection() as HttpURLConnection
        try {
            connection.requestMethod = method
            connection.connectTimeout = 15_000
            connection.readTimeout = 15_000
            connection.instanceFollowRedirects =
                false // Never forward a token to a redirect target.
            connection.setRequestProperty("Accept", "application/json")
            token?.let { connection.setRequestProperty("Authorization", "Bearer $it") }
            if (upload != null) {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", requireNotNull(contentType))
                connection.setFixedLengthStreamingMode(upload.size)
                connection.outputStream.use { it.write(upload) }
            } else if (body != null) {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.outputStream.use {
                    it.write(
                        body.toString().toByteArray(Charsets.UTF_8)
                    )
                }
            }
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val text = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (code !in 200..299) throw error(code, text)
            if (code == 204) return JSONObject()
            return parseResponse { JSONObject(text) }
        } catch (e: SocketTimeoutException) {
            throw AuthException(
                0,
                "El servidor tardó demasiado. Comprueba tu conexión e intenta de nuevo."
            )
        } catch (e: IOException) {
            throw AuthException(
                0,
                "No fue posible conectar. Comprueba tu conexión y que el backend esté encendido."
            )
        } finally {
            connection.disconnect()
        }
    }

    private fun error(code: Int, text: String): AuthException {
        val json = runCatching { JSONObject(text) }.getOrNull()
        val errors = json?.optJSONObject("errors")
        val fields = mutableMapOf<String, String>()
        errors?.keys()?.forEach { key ->
            errors.optJSONArray(key)?.optString(0)?.takeIf { it.isNotBlank() }
                ?.let { fields[key] = it }
        }
        val message = when (code) {
            401 -> "Tu sesión expiró. Inicia sesión nuevamente."
            403 -> "No tienes permiso o tu correo aún no está verificado."
            422 -> json?.optString("message")?.takeIf { it.isNotBlank() }
                ?: "Revisa los datos ingresados."

            429 -> "Demasiados intentos. Espera un momento antes de volver a intentar."
            in 500..599 -> "El servidor no está disponible en este momento. Intenta más tarde."
            else -> "No se pudo completar la solicitud (HTTP $code)."
        }
        return AuthException(code, message, if (code == 422) fields else emptyMap())
    }

    private fun <T> parseResponse(block: () -> T): T = try {
        block()
    } catch (e: org.json.JSONException) {
        throw AuthException(
            502,
            "La respuesta del servidor no es válida. Verifica la dirección de la API."
        )
    }
}
