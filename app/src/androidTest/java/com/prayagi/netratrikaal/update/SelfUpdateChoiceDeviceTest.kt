package com.prayagi.netratrikaal.update
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.core.app.ActivityScenario
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.io.File
@RunWith(AndroidJUnit4::class)
class SelfUpdateChoiceDeviceTest {
 @Test(timeout = 60_000) fun legacyNotificationIntentCannotOfferDownloadOrInstall() {
  val ins=InstrumentationRegistry.getInstrumentation();val c=ins.targetContext
  val dir=File(c.cacheDir,"updates").apply { mkdirs() };File(dir,"legacy.apk").writeText("old cache")
  // The continuously scrolling ticker prevents Instrumentation.startActivitySync from becoming idle.
  // Launch through Android's ordinary shell route, then verify the actual visible app explicitly.
  android.os.ParcelFileDescriptor.AutoCloseInputStream(ins.uiAutomation.executeShellCommand(
   "am start -W -n com.prayagi.netratrikaal/.MainActivity --ez netra_auto_update true")).use { it.readBytes() }
  try {
   val visibleDeadline=System.currentTimeMillis()+15000
   while(ins.uiAutomation.rootInActiveWindow?.packageName?.toString()!=c.packageName && System.currentTimeMillis()<visibleDeadline) Thread.sleep(100)
   assertEquals("Actual Trikaal window must be visible",c.packageName,ins.uiAutomation.rootInActiveWindow?.packageName?.toString())
   val deadline=System.currentTimeMillis()+10000
   while(dir.exists() && System.currentTimeMillis()<deadline)Thread.sleep(100)
   assertFalse("Legacy APK cache removed",dir.exists())
   fun text(n:android.view.accessibility.AccessibilityNodeInfo?):List<String> = if(n==null)emptyList() else listOfNotNull(n.text?.toString())+(0 until n.childCount).flatMap { text(n.getChild(it)) }
   val labels=text(ins.uiAutomation.rootInActiveWindow)
   assertFalse(labels.any { it=="Download" || it=="Install" || it.startsWith("Download KBC update") || it.startsWith("Download SensorGuard update") })
   android.os.ParcelFileDescriptor.AutoCloseInputStream(ins.uiAutomation.executeShellCommand("screencap -p /data/local/tmp/centralized-no-updater.png")).use { it.readBytes() }
  } finally {
   android.os.ParcelFileDescriptor.AutoCloseInputStream(ins.uiAutomation.executeShellCommand("input keyevent KEYCODE_HOME")).use { it.readBytes() }
  }
 }
 @Test(timeout = 60_000) fun ecoAbsentDoesNotFallBackToOwnDownload() {
  val c=InstrumentationRegistry.getInstrumentation().targetContext
  if(c.packageManager.getLaunchIntentForPackage(AppUpdater.ECO_PACKAGE)==null) assertFalse(AppUpdater.openEco(c))
  assertFalse(AppUpdater::class.java.declaredMethods.any { it.name=="download" || it.name=="install" })
 }
}
