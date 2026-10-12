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
    @Test fun removesEveryDownloadedInstaller() {
        val dir = java.nio.file.Files.createTempDirectory("updates").toFile()
        File(dir, "a.apk").writeText("a")
        File(dir, "b.apk").writeText("b")
        assertEquals(2, AppUpdater.cleanDir(dir))
        assertTrue(dir.listFiles()!!.isEmpty())
        dir.delete()
    }
    @Test fun missingFolderIsFine() {
        assertEquals(0, AppUpdater.cleanDir(File("/nonexistent-folder-for-test")))
        assertEquals(0, AppUpdater.cleanDir(null))
    }
    @Test fun repoIsTrikaal() = assertEquals("prayagi-store-and-services/netra-trikaal", AppUpdater.REPO)
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
