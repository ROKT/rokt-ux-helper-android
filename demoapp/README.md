# Rokt UX Helper Sample App (Android)

Renders Rokt experiences with `roktux`, using experience responses bundled in `src/main/assets/`.

The app makes **no calls to the Rokt API**, so it needs no account, no credentials and no
configuration — clone, open, run. Only the offers' own images and links reach the network.
This mirrors the [iOS Example app](https://github.com/ROKT/rokt-ux-helper-ios/tree/main/Example).

```kotlin
dependencies {
    implementation(projects.uxHelper.roktux)
}
```

## Running

Open the repository in [Android Studio](https://developer.android.com/studio) and run the
`demoapp` configuration, or:

```bash
./gradlew :demoapp:installDebug
```

## What the home screen launches

| Button                 | Entry point             | Bundled response              |
| ---------------------- | ----------------------- | ----------------------------- |
| Compose                | `RoktLayout` composable | `experience.json`             |
| Compose — overlay      | `RoktLayout` composable | `experience-overlay.json`     |
| Compose — bottom sheet | `RoktLayout` composable | `experience-bottomsheet.json` |
| View system            | `RoktLayoutView`        | `experience.json`             |

`RoktLayout` renders whichever container the response asks for — overlay or bottom sheet — so the
host composes it in place rather than wrapping it in a dialog of its own.

## Compose: `RoktLayout`

Key parameters:

1. `experienceResponse` — the layout's JSON string.
2. `location` — the element RoktUX targets, matching `target_element_selector` in the response.
   Overlay and bottom-sheet layouts carry their own container and render regardless.
3. `roktUxConfig` — colour mode, fonts and image handling. Required; use `RoktUxConfig.builder().build()` for defaults.
4. `onUxEvent` — user interaction callbacks (opening links, layout completion and failure).
5. `onPlatformEvent` — integration data a production app forwards via its backend.

See the [Android integration guide](https://docs.rokt.com/server-to-server/android).

```kotlin
RoktLayout(
    experienceResponse = experienceResponse,
    location = "RoktTest", // "target_element_selector" in the experience JSON
    roktUxConfig = RoktUxConfig.builder().build(),
    onUxEvent = { event -> /* handle UX events here */ },
    onPlatformEvent = { /* send these platform events to the Rokt API */ },
)
```

`SampleScreen.kt` and `SampleEvents.kt` hold this wiring.

## View system: `RoktLayoutView`

Same parameters, added to a view hierarchy instead — see `SampleLayoutViewActivity.kt`.

```kotlin
val layoutView = RoktLayoutView(this, location = "RoktTest")
setContentView(layoutView)

layoutView.loadLayout(
    experienceResponse = experienceResponse,
    roktUxConfig = RoktUxConfig.builder().build(),
    onUxEvent = { event -> /* handle UX events here */ },
    onPlatformEvent = { /* send these platform events to the Rokt API */ },
)
```

## Bundled experience responses

Copied from the iOS Example app's fixtures, so both samples render the same layouts.

| File                          | Outer layout | `target_element_selector` |
| ----------------------------- | ------------ | ------------------------- |
| `experience.json`             | Overlay      | `RoktTest`                |
| `experience-overlay.json`     | Overlay      | `""`                      |
| `experience-bottomsheet.json` | BottomSheet  | `""`                      |

Each was converted from a captured placement payload into a server-to-server shaped response, with
the session tokens replaced by placeholders. Creative imagery does not load — the image URLs point
at assets that no longer resolve — so the fixtures exercise layout and text, not images.

To render a different layout, drop its JSON into `src/main/assets/`, add it to `ExperienceAssets`
in `SampleScreen.kt`, and give it an entry in `SampleDestination` in `HomeScreen.kt`.
