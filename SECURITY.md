# Security notes - Netra Trikal Drishti

Netra Trikal Drishti is an on-device Vedic astrology app by Prayagi Team. It has its own repository and its own releases. It began inside Netra Eco and moved out on 8 October 2026.

## Data and permissions (1.0.0)

- All calculation runs on the phone with the Swiss Ephemeris (Moshier mode, AGPL, source in `astro-core`).
- Birth name, date, time, place and up to 5 saved profiles stay in the app's private storage. Nothing is sent anywhere.
- One optional permission: approximate location (ACCESS_COARSE_LOCATION), asked with a clear yes or no. It is used on the phone to work out Rahu Kaal, muhurat and tithi for your place and is never sent. If declined, Prayagraj is used.
- The app declares no INTERNET permission, so it cannot make network calls. No account. No analytics.
- City search uses an offline GeoNames file (CC BY 4.0) shipped in the app.
- Values with no vetted rule show "Unavailable". Results are not checked against professional software for every case, and the app says so.

## Reporting a vulnerability

Open a private security advisory on this repository (Security tab), or an issue without personal data.

## Release checks

Releases are built by the Signed Release workflow only after unit tests pass. The APK signature is verified before publishing, and each release lists its size and SHA-256.
