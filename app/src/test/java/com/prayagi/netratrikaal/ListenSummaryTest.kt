package com.prayagi.netratrikaal

import org.junit.Assert.assertEquals
import org.junit.Test

class ListenSummaryTest {
    @Test fun withPeriod() = assertEquals("Lagna Leo. Moon sign Aries. Current period Venus.", listenSummary("Leo", "Aries", "Venus"))
    @Test fun withoutPeriodSaysUnavailable() = assertEquals("Lagna Leo. Moon sign Aries. Current period is Unavailable.", listenSummary("Leo", "Aries", null))
}
