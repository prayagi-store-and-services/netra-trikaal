package com.prayagi.netratrikaal.update

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppUpdaterRedirectTest {

    @Test fun `known release hosts are allowed over https`() {
        assertTrue(AppUpdater.isAllowedMetadataUrl("https://github.com/prayagi-store-and-services/netra-trikaal/releases/latest/download/latest.json"))
        assertTrue(AppUpdater.isAllowedMetadataUrl("https://release-assets.githubusercontent.com/github-production-release-asset/x"))
        assertTrue(AppUpdater.isAllowedMetadataUrl("https://objects.githubusercontent.com/x"))
    }

    @Test fun `lookalike and foreign hosts are refused`() {
        assertFalse(AppUpdater.isAllowedMetadataUrl("https://github.com.evil.example/latest.json"))
        assertFalse(AppUpdater.isAllowedMetadataUrl("https://evil-github.com/latest.json"))
        assertFalse(AppUpdater.isAllowedMetadataUrl("https://example.com/latest.json"))
        assertFalse(AppUpdater.isAllowedMetadataUrl("https://githubusercontent.com.evil.example/x"))
    }

    @Test fun `cleartext and non-http schemes are refused`() {
        assertFalse(AppUpdater.isAllowedMetadataUrl("http://github.com/latest.json"))
        assertFalse(AppUpdater.isAllowedMetadataUrl("ftp://github.com/latest.json"))
        assertFalse(AppUpdater.isAllowedMetadataUrl("not a url"))
    }
}
