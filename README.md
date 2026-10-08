# CourseApplication

Android app (Kotlin, Jetpack Compose, MVVM + Clean Architecture): login, course dashboard, course details with lesson completion, and offline support.

**Run:** open the project in Android Studio (Koala or newer, JDK 17), let Gradle sync, run the `app` configuration. Demo login: `test@example.com` / `Password1`.
**Tests:** `./gradlew testDebugUnitTest`

## 1. Architecture
Three layers, dependencies pointing inward: `presentation` (Compose screens + ViewModels exposing a single `StateFlow` of UI state) → `domain` (models, use cases, repository interfaces, validation, progress calculation; plain Kotlin) → `data` (Room, mock API, repository implementations). I chose it because the screens only talk to use cases, so the mock API can be replaced by Retrofit without touching UI or domain code, and the business rules (progress, validation) are testable on the JVM without Android. Dependencies are wired by a small hand-written `AppContainer`; at this size a DI framework adds more than it saves, and moving to Hilt would only change the `di` package.

## 2. Offline support
Room is the single source of truth. The dashboard and details screens only observe Room (`Flow`). On open, the repository fetches courses and lessons from the API and writes them in one transaction. If the call fails (e.g. no connection), the UI keeps showing the saved data with a "couldn't refresh" banner; if nothing is saved yet, it shows the error state with Retry. Completing a lesson is a local write, and course progress is derived from completed lessons in the query, so both screens update immediately and offline. Refreshes upsert courses and insert only new lessons, so progress made locally is not overwritten.

## 3. Security
Tokens would never go in plain `SharedPreferences`, Room or logs. I would keep the short-lived access token in memory and persist the refresh token encrypted with a key held in the Android Keystore (Jetpack Security / Tink-backed DataStore), clear it on logout or a 401 refresh failure, disable `allowBackup`, use HTTPS only (optionally with certificate pinning), and add biometric re-authentication for sensitive actions if needed.

## 4. Scale (1M users, hundreds of courses)
1. Paginate the course list (Paging 3 with a RemoteMediator) and load lessons per course on demand instead of all at once.
2. Sync completion to the server through an offline queue (WorkManager, idempotent requests, conflict rules) so progress is shared across devices.
3. Use a real network stack (Retrofit/OkHttp) with HTTP caching/ETags, retries with backoff, and search backed by Room FTS.
4. Modularize by feature and move to Hilt for faster builds and clearer ownership as the team grows.
5. Add observability: crash reporting, performance traces, analytics and remote config/feature flags, plus a CDN for images and media.

## 5. Second platform (iOS)
Same layering in Swift: SwiftUI views bound to `@Observable` view models (MVVM), use cases/repository protocols in a domain module, `URLSession` with async/await for the API, and SwiftData (or Core Data) as the local store, observed by the view models just as Room is via Flow. Navigation with `NavigationStack`; the token goes in the Keychain; `XCTest` for the progress and view model tests. The domain rules stay identical, so the logic ports one-to-one.
