# NETRA TRIKAAL

Private-by-design Vedic astrology for Android: panchang, kundli, dasha, gochar and daily guidance. Part of the [Netra Eco](https://prayagi-store-and-services.github.io/netra-eco/) family by Prayagi Store and Services.

## Status: released Android app

Trikaal is a separate app (applicationId `com.prayagi.netratrikaal`). Current public release: [v1.0.20](https://github.com/prayagi-store-and-services/netra-trikaal/releases/tag/v1.0.20). Updates are offered through Netra Ecosystem. Signed releases require green checks; this is not a physical-device validation claim.

## What works today

- Live ticker: tithi, Rahu Kaal, muhurat and planets (Lahiri, Swiss Ephemeris Moshier mode).
- Birth form: name, date (calendar), time (AM/PM), city search, with the place and state shown as of the birth date.
- Kundli (North or South style), planet positions, Vimshottari dasha, gochar and daily rashifal with the rule shown.
- Up to 5 profiles with a default, stored only on the phone.
- Optional location with a clear yes or no; Prayagraj is used if declined.

Planned features (Match Making, Kundli PDF, tithi food guide, face and palm reading) are listed in the app under Future plans and come in later updates. Anything without a vetted rule shows "Unavailable".

## Principles

- Core chart calculations and saved profiles stay on the phone. INTERNET also supports version-metadata checks. In v1.0.20 the app no longer downloads or installs APKs: a newer-version notice opens the exact Trikaal update page in compatible Netra Ecosystem or on the Eco website. Do not interpret on-device calculations as a claim that the app never makes a network request.
- Truthful data: missing means "Unavailable". Nothing is invented. Sources are cited.
- Readings follow Vedic principles, not scientific certainty. Not medical, legal or financial advice.

## Build

`gradle :app:assembleDebug :app:testDebugUnitTest` (JDK 21, Gradle 9.3.1). CI also generates a CI-only debug key.

## Credits

- City search: GeoNames (geonames.org), CC BY 4.0.
- `astro-core` is a Swiss Ephemeris Java port by Thomas Mack; original authors Dieter Koch and Alois Treindl, Astrodienst AG. See `astro-core/NOTICE.md`.

## Licence

AGPL-3.0, see [LICENSE](LICENSE).

## Description maintenance

Feature and architecture changes must update the app/store/site/Eco description, README and release notes against shipped behavior. Planned features remain labelled planned.
