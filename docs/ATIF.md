# ATIF v3.0 - applicable parts for Netra Trikaal

ATIF v3.0 (Absolute Truth & Intelligence Framework) is the owner's permanent master rule, issued 9 Nov 2026. Canonical full text: private governance copy (`netra-intruder`, `docs/ATIF-v3.0.md`). This file lists only the parts that apply to THIS app.

## Applicable parts and why

- Part 01, 02: astrological data must be computed and presented honestly.
- Part 04, 05: calculations come from named sources (Swiss Ephemeris, Lahiri); anything without a vetted rule shows Unavailable - an UNKNOWN is never filled with invention.
- Part 06: missing data means Unavailable; nothing is invented.
- Part 09: live ticker values are computed for the actual date/time and place shown.
- Part 14: on-device calculations do not mean the app never makes a network request; this limit stays stated.
- Part 15 (High-Stakes): readings follow Vedic principles, not scientific certainty; not medical, legal or financial advice - this notice stays with the content.
- Part 16: profiles and charts stay on the phone; location is optional with Prayagraj as the stated fallback.
- Part 17, 20: planned features (Match Making, Kundli PDF, etc.) stay under Future plans until released.

## Data this app shows, and its status

- Panchang/kundli/dasha/gochar: computed from Swiss Ephemeris (Moshier mode) with Lahiri ayanamsa - VERIFIED source named in-app.
- Daily rashifal: rule shown with the reading.
- Update notices: open the exact Trikaal page; no in-app APK download.

## Standing rule

No reading, date or rule is displayed without its named basis. Uncertainty shows as Unavailable, never as a plausible-looking value.

## Source labels (owner-approved 9 Nov 2026)

Every user-visible data claim carries a short source label. UI strings stay short; the label for each claim lives in this file's "Data this app shows" list and in WHATS_NEW/release notes (e.g. "Source: Android BatteryManager"). In-app visible source labels are planned and ship only after the owner's UI preview.

## Stale data: OLD marking (owner-approved 9 Nov 2026)

Any dated or versioned claim that stops matching reality is marked "OLD (as of <date>)" at the next check and corrected in the same or a following PR. Stale claims are never silently edited and never left unmarked.

## Weekly audit (owner-approved 9 Nov 2026)

A weekly audit records each material claim as VERIFIED or NOT VERIFIED in `docs/ATIF-AUDIT.md`, newest first. Failures and NOT VERIFIED items are listed first in every audit and every report.
