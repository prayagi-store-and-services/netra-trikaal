package com.prayagi.netratrikaal

import android.Manifest
import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.SystemClock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class)
class CurrentPlaceFreshnessTest {
    private lateinit var context: Application
    private lateinit var manager: LocationManager

    @Before fun setup() {
        context = RuntimeEnvironment.getApplication()
        shadowOf(context).grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
        manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        shadowOf(manager).setLocationEnabled(true)
        shadowOf(manager).setProviderEnabled(LocationManager.NETWORK_PROVIDER, true)
        ShadowSystemClock.advanceBy(Duration.ofHours(2))
    }

    private fun seed(ageMinutes: Long) {
        val fix = Location(LocationManager.NETWORK_PROVIDER).apply {
            latitude = 25.4358
            longitude = 81.8463
            accuracy = 100f
            time = System.currentTimeMillis() - ageMinutes * 60_000L
            elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos() - ageMinutes * 60_000_000_000L
        }
        @Suppress("DEPRECATION")
        shadowOf(manager).setLastKnownLocation(LocationManager.NETWORK_PROVIDER, fix)
    }

    @Test fun hourOldFixIsNotCurrentLocation() {
        seed(60)
        assertNull("An hour-old cached fix must trigger the fresh-fix/fallback path", TrikaalPlaces.lastKnown(context))
    }

    @Test fun recentFixRemainsUsable() {
        seed(1)
        assertEquals(25.4358 to 81.8463, TrikaalPlaces.lastKnown(context))
    }

    @Test fun deniedPermissionDoesNotReadCachedFix() {
        seed(1)
        shadowOf(context).denyPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
        assertNull(TrikaalPlaces.lastKnown(context))
    }
}
