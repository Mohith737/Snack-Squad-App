<p align="center">
  <img src="res/drawable/logo.png" alt="Snack Squad logo" width="120" />
</p>

# Snack Squad

[![Android CI](https://github.com/OWNER/REPO/actions/workflows/android_ci.yml/badge.svg?branch=main)](https://github.com/OWNER/REPO/actions/workflows/android_ci.yml)

A Kotlin Android food ordering app built with MVVM architecture, Espresso test coverage, and GitHub Actions CI/CD.

## Tech Stack

| Area | Choice |
| --- | --- |
| Language | Kotlin |
| Architecture | MVVM + Repository |
| UI | ViewBinding, Material 3, RecyclerView |
| Async | Kotlin Coroutines + StateFlow |
| Database | SQLite via Room-ready `DBHelper` |
| Testing | JUnit4, Espresso, FragmentScenario |
| CI/CD | GitHub Actions |
| Min SDK | 24 / Android 7.0 |

## Architecture

Snack Squad uses a four-layer structure designed for clarity and testability. The UI layer contains Activities and Fragments responsible only for rendering state, handling user input, and observing lifecycle-aware flows. Business state is pushed into ViewModels, which expose `StateFlow` streams rather than holding Android view references.

The ViewModel layer coordinates screen logic and transforms repository output into observable UI state. Repositories act as the single source of truth for app data, whether that data comes from in-memory cart state, persisted order history, or local authentication storage. The data layer is currently backed by SQLite through `DBHelper`, with the repository boundary intentionally kept clean enough for a future Room migration.

```text
+-------------------+
| Activities /      |
| Fragments         |
+-------------------+
          |
          v
+-------------------+
| ViewModels        |
| StateFlow-driven  |
+-------------------+
          |
          v
+-------------------+
| Repositories      |
| Single source     |
| of truth          |
+-------------------+
          |
          v
+-------------------+
| DBHelper / Local  |
| persistence       |
+-------------------+
```

## Features

- Image slider banner on the home screen for featured content.
- Popular items feed rendered through RecyclerView adapters with stable IDs.
- Cart with quantity management, delete actions, and place-order flow.
- Real-time search with debounced query handling.
- Order history persisted locally with `SharedPreferences` using JSON serialization.
- Secure login and signup with SHA-256 hashed passwords and parameterized SQL queries.
- Input validation surfaced through Material `TextInputLayout` inline errors.

## Test Coverage

| Test Class | Coverage | Test Cases |
| --- | --- | ---: |
| `LoginActivityTest` | Empty credentials, invalid input, valid login, failed login, navigation gating | 5 |
| `SignUpActivityTest` | Password confirmation, duplicate users, successful signup navigation | 3 |
| `CartFragmentTest` | Default cart state, quantity controls, delete flow, place-order clearing behavior | 5 |
| `HomeFragmentTest` | Popular feed visibility and banner slider rendering | 2 |
| `SearchFragmentTest` | Exact-match filtering, clear-search recovery, empty-state handling | 3 |

## Setup & Run

1. Clone the repository.
   ```bash
   git clone <your-repo-url>
   cd Snack-Squad-App
   ```
2. Open the project in Android Studio Hedgehog or newer.
3. Let Gradle sync and resolve the Android SDK components.
4. Run the app on an emulator or device running API 24 or higher.
5. Run the Espresso suite:
   ```bash
   ./gradlew connectedDebugAndroidTest
   ```

## CI/CD

The GitHub Actions pipeline is defined to enforce a clear engineering gate before artifacts are produced.

- `lint`: runs Android lint and uploads the generated report for review.
- `unit-tests`: executes the local JVM test suite after lint passes.
- `instrumented-tests`: runs the Espresso suite on an Android emulator (API 34) as the UI quality gate.
- `build`: assembles the debug APK only after all validation jobs succeed and uploads it as a build artifact.

The release workflow is tag-driven (`v*.*.*`) and builds a release APK before publishing a GitHub Release artifact.

## Architecture Decisions

MVVM was chosen to keep rendering code separate from business logic. In a small portfolio project this matters because the architecture is visible quickly: Fragments stay focused on binding UI, while ViewModels own state transitions and user actions. That separation makes the code easier to review and extend.

`StateFlow` was chosen over `LiveData` because it fits structured concurrency and coroutine-based state pipelines more naturally. It also keeps the ViewModel API closer to plain Kotlin, which reduces Android framework coupling and makes flow-based transformations like debounced search straightforward.

Manual dependency wiring was chosen over Hilt because this project is meant to demonstrate understanding of the dependency boundaries themselves, not just framework usage. For a portfolio app, explicit factories and repository providers keep the dependency graph small, readable, and easy to reason about during review.
