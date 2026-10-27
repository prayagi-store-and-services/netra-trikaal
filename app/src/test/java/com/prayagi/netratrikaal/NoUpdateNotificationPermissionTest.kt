package com.prayagi.netratrikaal
import android.app.Application
import android.Manifest
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[34],application=Application::class)
class NoUpdateNotificationPermissionTest {
 @Test fun repeatedLaunchNeverRequestsUpdaterNotificationPermission(){
  androidx.work.WorkManager.initialize(org.robolectric.RuntimeEnvironment.getApplication(),androidx.work.Configuration.Builder().build())
  repeat(2){
   val c=Robolectric.buildActivity(MainActivity::class.java).create()
   val req=Shadows.shadowOf(c.get()).lastRequestedPermission
   assertTrue(req==null || !req.requestedPermissions.contains(Manifest.permission.POST_NOTIFICATIONS))
   c.destroy()
  }
 }
}
