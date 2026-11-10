package com.prayagi.netratrikaal

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TrikaalCryptoTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun key(id: Int = 1): SecretKey =
        // Deterministic software AES keys for the pure-JVM crypto path (device uses Android Keystore).
        // Seeded SecureRandom is NOT deterministic on Android's Conscrypt provider, so derive bytes directly.
        SecretKeySpec(ByteArray(16) { (id * 31 + it).toByte() }, "AES")

    @Before
    fun clearPrefs() {
        context.getSharedPreferences("trikaal_local_profiles", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test fun roundtripPlainAscii() {
        assertEquals("Deepak|1985-04-12", TrikaalCrypto.open(key(), TrikaalCrypto.seal(key(), "Deepak|1985-04-12")))
    }

    @Test fun roundtripUnicodeAndEmpty() {
        val k = key()
        assertEquals("दीपक, प्रयागराज 25.4358°N", TrikaalCrypto.open(k, TrikaalCrypto.seal(k, "दीपक, प्रयागराज 25.4358°N")))
        assertEquals("", TrikaalCrypto.open(k, TrikaalCrypto.seal(k, "")))
    }

    @Test fun sameInputSealsDifferently() {
        val k = key()
        assertNotEquals(TrikaalCrypto.seal(k, "O+"), TrikaalCrypto.seal(k, "O+"))
    }

    @Test fun tamperedBlobIsRejected() {
        val k = key()
        val sealed = TrikaalCrypto.seal(k, "secret")
        val chars = sealed.toCharArray()
        chars[sealed.length - 2] = if (chars[sealed.length - 2] == 'A') 'B' else 'A'
        assertThrows(Exception::class.java) { TrikaalCrypto.open(k, String(chars)) }
    }

    @Test fun wrongKeyIsRejected() {
        val sealed = TrikaalCrypto.seal(key(1), "secret")
        assertThrows(Exception::class.java) { TrikaalCrypto.open(key(2), sealed) }
    }

    @Test fun legacyPlaintextMigratesOnFirstRead() {
        val sp = context.getSharedPreferences("trikaal_local_profiles", Context.MODE_PRIVATE)
        val legacy = JSONArray().put(org.json.JSONObject()
            .put("name","A").put("date","1990-01-01").put("time","06:30").put("zone","IST")
            .put("lat","25.4").put("lon","81.8").put("place","Prayagraj").put("offset","+5:30")).toString()
        sp.edit().putString("profiles", legacy).commit()

        val read = TrikaalProfiles.readFrom(sp, key())

        assertEquals(1, read.size)
        assertEquals("A", read[0].name)
        assertEquals("1990-01-01", read[0].date)
        assertTrue("sealed value written", sp.getString("profiles_enc", null) != null)
        assertNull("legacy plaintext removed", sp.getString("profiles", null))
        assertFalse("sealed value is not the plaintext", sp.getString("profiles_enc", "")!!.contains("Prayagraj"))
        // Second read comes from the sealed value
        assertEquals(read, TrikaalProfiles.readFrom(sp, key()))
    }

    @Test fun sealedRoundtripThroughStore() {
        val sp = context.getSharedPreferences("trikaal_local_profiles", Context.MODE_PRIVATE)
        val p = TrikaalProfile("B","1988-12-31","23:45","IST","28.6","77.2","Delhi","+5:30")
        assertTrue(TrikaalProfiles.writeTo(sp, listOf(p), key()))
        assertNull(sp.getString("profiles", null))
        assertEquals(listOf(p), TrikaalProfiles.readFrom(sp, key()))
    }

    @Test fun plaintextFallbackWhenNoKey() {
        val sp = context.getSharedPreferences("trikaal_local_profiles", Context.MODE_PRIVATE)
        val p = TrikaalProfile("C","2000-06-15","12:00","IST","26.8","80.9","Lucknow","+5:30")
        assertTrue(TrikaalProfiles.writeTo(sp, listOf(p), null))
        assertTrue("fallback writes legacy plaintext key", sp.getString("profiles", null) != null)
        assertEquals(listOf(p), TrikaalProfiles.readFrom(sp, null))
    }
}
