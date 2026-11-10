package com.prayagi.netratrikaal

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.ZoneId

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TrikaalPlaceZoneTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test fun `fix near Prayagraj resolves to Kolkata zone, not the phone zone`() {
        assertEquals(ZoneId.of("Asia/Kolkata"), TrikaalCities.nearestZone(context, 25.44, 81.85))
    }

    @Test fun `fix near Shanghai resolves to Shanghai zone`() {
        assertEquals(ZoneId.of("Asia/Shanghai"), TrikaalCities.nearestZone(context, 31.22, 121.46))
    }

    @Test fun `fix near New Delhi stays Kolkata`() {
        assertEquals(ZoneId.of("Asia/Kolkata"), TrikaalCities.nearestZone(context, 28.61, 77.21))
    }

    @Test fun `mid-ocean fix has no honest zone and falls back`() {
        // Middle of the South Pacific: no city within the threshold.
        assertNull(TrikaalCities.nearestZone(context, -30.0, -140.0))
    }
}
