package com.prayagi.netratrikaal

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OwnRashiTest {
    private fun p(name: String, date: String = "1990-05-01", time: String = "10:30", lat: String = "25.43", lon: String = "81.84") =
        TrikaalProfile(name, date, time, "Asia/Kolkata", lat, lon, "Prayagraj", "")

    @Test fun defaultProfileWins() {
        val a = p("A"); val b = p("B")
        assertEquals(b, OwnRashi.choose(listOf(a, b), TrikaalProfiles.key(b)))
    }
    @Test fun onlyProfileIsUsedWithoutDefault() { val a = p("A"); assertEquals(a, OwnRashi.choose(listOf(a), "")) }
    @Test fun severalProfilesWithoutDefaultPickNothing() = assertNull(OwnRashi.choose(listOf(p("A"), p("B")), ""))
    @Test fun noProfilesPickNothing() = assertNull(OwnRashi.choose(emptyList(), ""))
    @Test fun completeProfileHasNothingMissing() = assertNull(OwnRashi.missing(p("A")))
    @Test fun missingPartsAreNamed() {
        assertEquals("date of birth", OwnRashi.missing(p("A", date = "")))
        assertEquals("birth time", OwnRashi.missing(p("A", time = "")))
        assertEquals("birth place", OwnRashi.missing(p("A", lat = "")))
        assertEquals("birth place", OwnRashi.missing(p("A", lon = "x")))
    }
    @Test fun noProfileIsUnavailableNotAPicker() {
        val s = OwnRashi.compute(emptyList(), "")
        assertTrue(s is OwnRashi.State.Unavailable && s.reason.startsWith("Unavailable"))
    }
    @Test fun incompleteProfileIsUnavailable() {
        val s = OwnRashi.compute(listOf(p("A", time = "")), "")
        assertTrue(s is OwnRashi.State.Unavailable && s.reason.contains("birth time"))
    }
    @Test fun completeProfileGivesExactlyOneRashiInRange() {
        val s = OwnRashi.compute(listOf(p("A")), "")
        assertTrue(s is OwnRashi.State.Ready && s.rashi in 0..11)
    }
}
