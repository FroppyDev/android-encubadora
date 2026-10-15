package com.fic.encubadorafic.data.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class KeystoreTokenStore(context: Context, private val apiUrl: String, private val namespace: String = "auth_session") : TokenStore {
    private val preferences = context.getSharedPreferences(namespace, Context.MODE_PRIVATE)
    private val alias = "encubadorafic.$namespace.token.v1"

    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(alias, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        }.generateKey()
    }

    @Synchronized override fun write(token: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.ENCRYPT_MODE, key()); updateAAD(apiUrl.toByteArray(Charsets.UTF_8)) }
        val encrypted = cipher.doFinal(token.toByteArray(Charsets.UTF_8))
        check(preferences.edit().putString("iv", encode(cipher.iv)).putString("token", encode(encrypted)).commit())
    }

    @Synchronized override fun read(): String? {
        val encrypted = preferences.getString("token", null) ?: return null
        return try {
            val iv = Base64.decode(requireNotNull(preferences.getString("iv", null)), Base64.NO_WRAP)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
                init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, iv))
                updateAAD(apiUrl.toByteArray(Charsets.UTF_8))
            }
            String(cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP)), Charsets.UTF_8)
        } catch (e: Exception) {
            clear() // Changed server, restored backup or invalidated key: require fresh login.
            null
        }
    }

    @Synchronized override fun clear() { check(preferences.edit().clear().commit()) }
    private fun encode(value: ByteArray) = Base64.encodeToString(value, Base64.NO_WRAP)
}
