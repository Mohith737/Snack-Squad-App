# Snack Squad — Codex Agent Instructions

## Project Identity
- **Package**: com.example.snacksquad
- **Language**: Kotlin only — no Java files
- **Min SDK**: 24, Target SDK: 34, Compile SDK: 34
- **Build System**: Gradle with Kotlin DSL (build.gradle.kts)

## Architecture Rules (NEVER violate these)
- Pattern: MVVM with Repository layer
- Fragments observe ViewModels via StateFlow + repeatOnLifecycle(STARTED)
- NO LiveData — use StateFlow and SharedFlow only
- NO RxJava — use Kotlin Coroutines only
- NO Hilt or Dagger — use viewModels() delegate and manual instantiation
- ViewBinding only — NO findViewById except where binding is unavailable
- NO hardcoded strings in Kotlin files — use res/values/strings.xml

## Coding Standards
- All Kotlin files use 4-space indentation
- Class names: PascalCase. Functions and variables: camelCase
- Constants: UPPER_SNAKE_CASE in companion object
- Every ViewModel function must be unit-testable (no Android context dependencies)
- Null safety: prefer safe calls (?.) and elvis (?:) over !! assertions
- Use data classes for all model objects

## UI Patterns
- Material Design 3 components only (TextInputLayout, MaterialButton, CardView)
- All RecyclerView adapters extend ListAdapter<T, VH> with DiffUtil — NOT RecyclerView.Adapter
- Fragment binding pattern: private var _binding: FragmentXBinding? = null, null it in onDestroyView()
- No Toast for validation errors — use TextInputLayout.error instead

## Testing Rules
- Espresso tests live in androidTest/
- Unit tests live in test/
- Every test class has @RunWith(AndroidJUnit4::class)
- Use FragmentScenario for Fragment tests
- Use ActivityScenarioRule for Activity tests
- Tests must be deterministic — no Thread.sleep(), use IdlingResource

## File Structure
src/main/java/com/example/snacksquad/
├── data/
│   ├── model/          ← data classes only
│   └── repository/     ← repositories only
├── ui/
│   ├── home/           ← HomeFragment + HomeViewModel
│   ├── cart/           ← CartFragment + CartViewModel
│   ├── search/         ← SearchFragment + SearchViewModel
│   └── history/        ← HistoryFragment + HistoryViewModel
├── adapter/            ← RecyclerView adapters
└── util/               ← SecurityUtils, ImageUtils, etc.

## Dependencies — APPROVED LIST
Only add dependencies from this list. Ask before adding anything new.
- androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0
- androidx.lifecycle:lifecycle-runtime-ktx:2.7.0
- kotlinx-coroutines-android:1.7.3
- androidx.fragment:fragment-testing:1.6.2
- androidx.test.espresso:espresso-contrib:3.5.1
- androidx.core:core-splashscreen:1.0.1

## Git Behavior
- After completing each task, stage all changed files
- Commit message format: "feat: [what was done]" or "test: [what was tested]"
- Do NOT push automatically — leave push for the user

## What to NEVER do
- Never add Retrofit, OkHttp, or any network library (app is offline)
- Never add Glide, Picasso, or Coil (use optimized BitmapFactory instead)
- Never use GlobalScope for coroutines
- Never suppress lint warnings without a comment explaining why
- Never delete existing drawable resources