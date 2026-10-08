# NETRA TRIKAAL

Private-by-design Vedic astrology for Android: panchang, kundli, dasha and daily guidance. Part of the [Netra Eco](https://prayagi-store-and-services.github.io/netra-eco/) family by Prayagi Store and Services.

## Status: in development, not released

Trikaal is **not released yet**. It will launch once, in full, together with all of its planned features. Until then it is developed inside the Netra Eco app, so there is one single build to test and release. This repository is its public home and will hold the standalone code after launch. No separate download exists yet.

## Where the code is today

All Trikaal source lives in [netra-eco-app](https://github.com/prayagi-store-and-services/netra-eco-app):

- Screens and logic: [`app/src/main/java/com/prayagi/netraeco/`](https://github.com/prayagi-store-and-services/netra-eco-app/tree/main/app/src/main/java/com/prayagi/netraeco) (files starting with `Trikaal`)
- Calculation engine: [`astro-core/`](https://github.com/prayagi-store-and-services/netra-eco-app/tree/main/astro-core) (Swiss Ephemeris Java port, Lahiri sidereal, Moshier planets)
- Security policy: [SECURITY.md](https://github.com/prayagi-store-and-services/netra-eco-app/blob/main/SECURITY.md)

## Principles

- **On-device only.** Birth details and saved profiles stay on the phone. No account, no server.
- **Optional location.** It is used only if you say yes. Panchang falls back to Prayagraj.
- **Truthful data.** If something cannot be calculated, the app shows "Unavailable". Nothing is guessed or invented.
- **Cited sources.** Rules and data sources are named in the app.
- Readings follow Vedic principles, not scientific certainty. Not medical, legal or financial advice.

## Data credits

- City search uses GeoNames (geonames.org), CC BY 4.0.
- Swiss Ephemeris Java port by Thomas Mack; original authors Dieter Koch and Alois Treindl, Astrodienst AG (see `astro-core/NOTICE.md` in netra-eco-app).

## Licence

AGPL-3.0, see [LICENSE](LICENSE).
