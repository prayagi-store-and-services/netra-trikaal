# Trikaal baseline and implementation plan

4 November 2026, source checked14:52IST. This is an audit/plan, not a release.

## Authority and order

The owner's28October master command requires baseline evidence before complex new
modules, preserves the existing app/data/core tabs, requires independent calculation
fixtures, and prohibits fabricated values. The28October21:16-21:17rolling-release
instruction supersedes the older13October all-at-once constraint. Ready independent
parts may release after tests, privacy review and applicable visual approval. This
does not make an incomplete module complete or authorize paid services.

## Baseline

- Starting/current main: `d6893061ebedf0f9312f544eb0e37a6cf19379d6`.
- Worktree clean before this documentation branch; no production source changed.
- Existing Android build CI success; retrieved original XML:69app tests,0failures,
  0errors,0skipped. This is that commit's CI, not a new local Android build.
- Fresh local astro-core compile: Java11-compatible ECJ plus Kotlin2.2.10;16JUnit
  tests passed,0failures. Suites:AstroCoreTest,PanchangTest,RashifalTest,
  NumericalReferenceTest. No repository test weakened or removed.
- Android SDK/Gradle are not installed locally; no fresh local APK size, physical
  device timing, battery, memory or thermal result is claimed.
- Existing native screenshot workflow exists, but no fresh screenshots have been
  produced or approved for this plan.

CI evidence:
https://github.com/prayagi-store-and-services/netra-trikaal/actions/runs/40727624058/job/122955039184

## Feature matrix

| Area | Source and current behavior | Status | Next acceptance |
| --- | --- | --- | --- |
| Four core tabs | TrikaalTabs.kt,TrikaalNav.kt:Kundli,Horoscope,Panchang,Settings | Implemented, insufficient expanded acceptance | Fresh install, empty state,320/360dp,Hindi,font scaling,back navigation |
| Birth profiles | TrikaalBirthForm.kt,TrikaalProfiles.kt:calendar,AM/PM,offline birth-city coordinates/timezone,up to5profiles | Implemented, insufficient workflow evidence | Switch/edit/delete profiles; no stale chart; missing/approximate/exact time states |
| Panchang/planets | astro-core/Panchang.kt,AstroCore.kt; local Swiss Ephemeris Moshier/Lahiri | Existing tests pass; expanded independent validation open | Multi-year/place boundaries,all weekdays,Adhik Maas,tradition/tolerance/source documented |
| Dasha/D9 | AstroCore.kt,KundliTopics.kt | Implemented, limited fixtures | Independent nested-period and divisional-boundary fixtures |
| Current place | TrikaalPlaces.kt:optional coarse fix,Prayagraj fallback | Partial; source risks found | Reproduce stale-fix/timezone cases;manual saved place independent of birthplace |
| Personal Muhurat | General daily Rahu/Abhijit only | Dedicated activity finder not implemented | Activity/date range/location/rule/exclusions/no-result state;never override legal deadlines |
| Vastu | No dedicated module found in audited source | Not implemented | North input,manual grid,no-demolition/free filters,source and safety caveats |
| Marriage/business/friendship Milan | Planned text,not working modules | Not implemented | Separate rules and tests;consent;no reused marriage score for business/friendship |
| PDF/history | Future-plan composable,not export | Not implemented | Hindi pagination,branding,local export/reopen/delete,versioned rules |
| Palm/face | Planned text,not analysis | Feasibility dependency | Consent,quality checks,on-device feasibility,no sensitive-trait inference;no educational substitute claimed as feature |
| Medicine | No dedicated module found | Data/license/safety dependency | Verified maintained official sources,revision,Unavailable fallback,no astrology-based medical advice |
| Redesign matching | Existing starry navy/gold screens,heldPR28 prep | Partial; original image comparison open | Recover9Octmockup,compare actual pixels,preview approval,no fake example data |
| Updates | Metadata notices/Eco route | Existing CI-tested source | Regression permission/network/offline/route cases;not a download/install subsystem |

## Reproductions before fixes

1. Current-place load uses `ZoneId.systemDefault()`, not a persisted place timezone.
   Build a regression fixture with selected coordinates and a different device zone;
   compare the intended place/timezone before selecting a fix. Do not assume GPS
   coordinates prove timezone. Manual city selection can reuse the bundled city DB.
2. `lastKnown` picks latest provider result but does not inspect its age. Reproduce
   an old fix and verify a stale reading cannot be presented as current. Keep GPS
   denial/offline fallbacks independent of the rest of the app.
3. Header/ticker layout: source places the ticker outside the scrolling content and
   aClosecontrol in the fixed header. Compare with the current standards and owner
   mockup before changing UI. Do not silently choose between conflicting layouts.

## Next reviewable milestones, not release dates

- Next work session: test-only baseline PR with explicit `:astro-core:test` in the
  acceptance command and preservation of both app/core XML. Expand the matrix into
  executable regression cases. Keep all current checks.
- Then: reproduce current-place freshness/timezone bugs and submit the smallest
  verified fixPR, with migration/fallback tests. Target a reviewable PR within one
  working day after reproduction, not a guaranteed release.
- In parallel: recover the original9October mockup image; capture existing native
  screens with synthetic fixtures. Produce the next preview only after side-by-side
  comparison. No pixel-matching claim until the image is recovered.
- New P1/P2modules follow the owner's phase order after prerequisite acceptance;
  independent ready fixes need not wait for the full feature list.

No new full-redesign release date is defensible yet. The missed24October estimate
remains a miss. Do not replace it with an invented date or call this plan delivery.

## Test route and release gates

Test account/disposable emulator only; do not use the owner's phone/accounts.
Physical low-end/modern-phone performance is NOT RUN under that route. Separate
PASS/FAIL/NOT RUN, preserve source/ref/tolerance for independent fixtures, inspect
native screenshots, require green final-headCI and privacy gate. Major UI changes
need approved preview. Free-only, no new server/secret/settings/signing change.
