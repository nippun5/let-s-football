# Let's Football

A Material 3 (Compose) Android app for browsing Premier League and Champions League
fixtures, with authentic club crests, built for **compileSdk / targetSdk 36 (Android 16)**.

## What's inside
- **100% Kotlin + Jetpack Compose**, Material 3 theming (dynamic color on Android 12+,
  a custom pitch-green palette as a fallback), edge-to-edge UI, and a splash screen.
- **Two leagues as tabs**: Premier League and UEFA Champions League. Fixtures are
  grouped by date with sticky-style section headers ("Today", "Tomorrow", full dates).
- **Real club crests**: each match card resolves and caches the actual badge image for
  both clubs from the API (not a placeholder), shown with graceful fallback iconography.
- **Pull-to-refresh**, loading, empty, and error states.
- **Data source**: [TheSportsDB](https://www.thesportsdb.com/api.php) — a genuinely free
  public API. The app ships using TheSportsDB's public test key (`"3"`), which needs
  **no signup at all** to run immediately.

## Project structure
```
app/src/main/java/com/letsfootball/app/
├── MainActivity.kt
├── data/
│   ├── model/Models.kt            # DTOs + clean UI model (FixtureUi)
│   ├── remote/                    # Retrofit service + client
│   └── repository/FootballRepository.kt
├── ui/
│   ├── theme/                     # Color.kt, Theme.kt, Type.kt
│   ├── components/                # MatchCard, headers, loading/empty/error states
│   └── screens/fixtures/          # FixturesScreen + FixturesViewModel
└── util/                          # DateUtils, UiState
```

## Running it
1. Open the project root folder in **Android Studio (Ladybug or newer recommended)**.
2. Let Gradle sync — it will download AGP 8.9.1, Gradle 8.11.1, Compose BOM 2024.12.01,
   Kotlin 2.0.21, Retrofit, OkHttp, Coil, etc. (needs an internet connection).
3. Run on a device/emulator with **API 26+** (the app targets 36).

No API key setup is required out of the box. If you outgrow the free test key's rate
limit, get your own free/paid key at thesportsdb.com/api.php and swap the constant in
`data/remote/RetrofitClient.kt`.

## Notes & ideas to extend
- Swap TheSportsDB for `football-data.org` if you want richer competition metadata —
  its free tier requires a personal API key (still free) but returns crests directly in
  the fixtures payload, avoiding the extra per-team badge lookup this app does.
- Add a "match details" bottom sheet, league table/standings tab, or favorite-teams
  filtering — the `League` enum and `FootballRepository` are structured to make adding
  a third competition a one-line change.
- The free API's data (especially live/very recent scores) can lag a few minutes;
  for near-real-time scores you'd want a paid tier or a livescore-specific API.
