package com.prayagi.netratrikaal

import com.prayagi.netratrikaal.update.AppUpdater
import com.prayagi.netratrikaal.update.DownloadMeter
import com.prayagi.netratrikaal.update.plainFailure
import java.io.File
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateCleanupTest {
    @Test fun onlyEcoHasUpdateDestination() = assertEquals("com.prayagi.netraeco",AppUpdater.ECO_PACKAGE)
    @Test fun noApkCapabilityRemains() { assertTrue(AppUpdater::class.java.declaredMethods.none { it.name in listOf("install","download","checkNow") }) }
    @Test fun percentAndFractionAreSafe() {
        assertEquals(50, DownloadMeter.percent(50, 100))
        assertEquals(null, DownloadMeter.percent(5, 0))
        assertEquals(0f, DownloadMeter.fraction(5, 0), 0f)
        assertEquals("Unavailable", DownloadMeter.eta(null))
    }
    @Test fun networkFailureIsPlain() {
        assertTrue(plainFailure(IOException("Unable to resolve host x"), "f").startsWith("No internet"))
        assertEquals("f", plainFailure(RuntimeException("x"), "f"))
    }
}
