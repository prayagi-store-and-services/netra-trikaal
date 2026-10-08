# NETRA TRIKAAL

Private-by-design Vedic astrology for Android: panchang, kundli, dasha, gochar and daily guidance. Part of the [Netra Eco](https://prayagi-store-and-services.github.io/netra-eco/) family by Prayagi Store and Services.

## Status: first release in preparation

Trikaal is a separate app (applicationId `com.prayagi.netratrikaal`). Nothing is released yet. Each release is a normal GitHub release with a direct APK link, size and SHA-256, published only when CI is green. Milestone: [Trikaal launch](https://github.com/prayagi-store-and-services/netra-trikaal/milestone/1).

## What works today

- Live ticker: tithi, Rahu Kaal, muhurat and planets (Lahiri, Swiss Ephemeris Moshier mode).
- Birth form: name, date (calendar), time (AM/PM), city search, with the place and state shown as of the birth date.
- Kundli (North or South style), planet positions, Vimshottari dasha, gochar and daily rashifal with the rule shown.
- Up to 5 profiles with a default, stored only on the phone.
- Optional location with a clear yes or no; Prayagraj is used if declined.

Planned features (Match Making, Kundli PDF, tithi food guide, face and palm reading) are listed in the app under Future plans and come in later updates. Anything without a vetted rule shows "Unavailable".

## Principles

- On-device only: no account, no server, no INTERNET permission.
- Truthful data: missing means "Unavailable". Nothing is invented. Sources are cited.
- Readings follow Vedic principles, not scientific certainty. Not medical, legal or financial advice.

## Build

`gradle :app:assembleDebug :app:testDebugUnitTest` (JDK 21, Gradle 9.3.1). CI also generates a CI-only debug key.

## Credits

- City search: GeoNames (geonames.org), CC BY 4.0.
- `astro-core` is a Swiss Ephemeris Java port by Thomas Mack; original authors Dieter Koch and Alois Treindl, Astrodienst AG. See `astro-core/NOTICE.md`.

## Licence

AGPL-3.0, see [LICENSE](LICENSE).
