package com.prayagi.netratrikaal
import org.junit.Test
import org.junit.Assert.*
class PreDownloadTest { @Test fun retiredUpdaterHasNoInstaller() { assertFalse(com.prayagi.netratrikaal.update.AppUpdater::class.java.declaredMethods.any{it.name=="download"||it.name=="install"}) } }
