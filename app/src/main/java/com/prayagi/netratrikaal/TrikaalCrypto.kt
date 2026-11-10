package com.prayagi.netratrikaal

import android.util.Base64
import android.util.Log
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * AES-GCM seal/open for on-phone birth profile data. Stored form: Base64( iv(12) | ciphertext+tag ).
 * Tampering or a wrong key makes [open] throw; callers decide the fallback. No data leaves the phone.
 */
internal object TrikaalCrypto {
    private const val IV_LEN = 12
    private const val TAG_BITS = 128
    private const val KEY_ALIAS = "trikaal_profiles_aes_v1"
    private const val TAG = "TrikaalCrypto"

    fun seal(key: SecretKey, plain: String): String {
        val iv = ByteArray(IV_LEN).also { SecureRandom().nextBytes(it) }
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_BITS, iv))
        return Base64.encodeToString(iv + c.doFinal(plain.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
    }

    fun open(key: SecretKey, blob: String): String {
        val raw = Base64.decode(blob, Base64.NO_WRAP)
        require(raw.size > IV_LEN) { "sealed value too short" }
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_BITS, raw.copyOfRange(0, IV_LEN)))
        return String(c.doFinal(raw.copyOfRange(IV_LEN, raw.size)), Charsets.UTF_8)
    }

    /** Device-only Android Keystore key, created on first use. Null when the keystore is unusable. */
    fun keystoreKey(): SecretKey? = try {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (ks.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey ?: run {
            val kg = KeyGenerator.getInstance("AES", "AndroidKeyStore")
            kg.init(
                android.security.keystore.KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or android.security.keystore.KeyProperties.PURPOSE_DECRYPT
                ).setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build()
            )
            kg.generateKey()
        }
    } catch (e: Exception) {
        Log.w(TAG, "keystore unavailable", e)
        null
    }
}
