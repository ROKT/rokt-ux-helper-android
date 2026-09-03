# Rokt UX Helper Android

The Rokt UX Helper for Android enables partner applications to render tailored user experiences, improving the velocity of testing and relevancy for the customer. This library offers an easy way to perform rendering and provides event hooks for integration into backend systems.

| Environment | Build                                                                                                        | Coverage                                                                                                                                            |
| ----------- | ------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| main        | ![Build status](https://github.com/ROKT/rokt-ux-helper-android/actions/workflows/pull-request.yml/badge.svg) | [![codecov](https://codecov.io/gh/ROKT/rokt-ux-helper-android/graph/badge.svg?token=inV3Xb1tK9)](https://codecov.io/gh/ROKT/rokt-ux-helper-android) |

## Requirements

To consume `roktux` in an app:

- Android 6.0 (API level 23) and above
- A Jetpack Compose BOM compatible with the version you depend on (see [Jetpack Compose Compatibility](#jetpack-compose-compatibility))

To build this repository:

- The latest version of [Android Studio](https://developer.android.com/studio)
- Android Gradle Plugin 8.6.1
- Gradle 8.9
- JDK 17
- Kotlin 2.1.20

## Installation

The library is published to [Maven Central](https://central.sonatype.com/artifact/com.rokt/roktux) as `com.rokt:roktux`. Add it to your app-level `build.gradle.kts` dependencies, using the version listed there.

```kotlin
dependencies {
    implementation("com.rokt:roktux:<version>")
}
```

## Usage

Include `RoktUx.getIntegrationConfig(context).toJsonString()` in the Rokt experience request from your backend. Pass the experience response into `RoktLayout`. The `location` value must match a placement location in that response.

```kotlin
val integrationConfig = RoktUx.getIntegrationConfig(context).toJsonString()
// Send integrationConfig with your Rokt experience request, then:

RoktLayout(
    experienceResponse = experienceResponse,
    location = "Location1",
    onUxEvent = { event ->
        if (event is RoktUxEvent.OpenUrl) {
            // Open event.url, then notify the helper so offer progression can continue.
            event.onClose(event.id)
        }
    },
    onPlatformEvent = { events ->
        // Forward events.toJsonString() to Rokt through your backend.
    },
    roktUxConfig = RoktUxConfig.builder().build(),
)
```

`RoktUxConfig.builder()` defaults to `NetworkStrategy()` for images and `ColorMode.SYSTEM` for theming. For Views, use `RoktLayoutView` with `app:location` and `loadLayout(...)`.

The [Android integration guide](https://docs.rokt.com/server-to-server/android/) covers UX events, platform events, fonts, and color mode in more detail.

## Releases

- You can find a summary of changes in the [Changelog](CHANGELOG.md)
- If there are any migrations between versions you will find instructions in the [migration guide](MIGRATING.md)
- To learn about how to release the UX Helper modules, check out the [releasing guide](RELEASING.md)

> [!NOTE]
> `CHANGELOG.md` is generated automatically by the "Release – Draft" workflow from conventional commit PR titles. **Do not edit it in feature branches** — manual entries will be overwritten at release time. See [RELEASING.md](RELEASING.md) for details.

## Jetpack Compose Compatibility

As `roktux` uses Jetpack Compose, consuming projects should use Compose libraries with compatible versions:

| roktux             | Compose BOM |
| ------------------ | ----------- |
| 1.0.0 and later    | 2026.05.01  |
| 0.1.0 – 1.0.0-rc.1 | 2024.09.02  |

You can view the [BOM to library version mapping](https://developer.android.com/develop/ui/compose/bom/bom-mapping).

## Development and useful commands

When making changes to UX Helper you can utilise the [Demo app](demoapp/README.md) to validate your changes. It renders experience responses bundled in its assets, so it needs no credentials or network access to run.

Before submitting changes ensure that:

- Builds succeed with `./gradlew build`
- Tests pass with `./gradlew test`
- Lint checks pass with `./gradlew lint`

Additional checks are conducted using GitHub Actions which run on all pull requests and are required to pass before the changes are merged. You can find the [full pipeline details](.github/workflows/pull-request.yml).

To publish the UX Helper modules locally for use in other projects, run:

```bash
./gradlew publishMavenPublicationToMavenLocal -PVERSION=x.y.z
```

This publishes `roktux` (and the internal `testutils` artifact) to `~/.m2`. Signing is skipped unless `signingInMemoryKey` is set; see [`MavenCentralPublish.kt`](build-logic/convention/src/main/kotlin/com/rokt/roktux/MavenCentralPublish.kt).

## Modules

| Module      | Description                                                                                   |
| ----------- | --------------------------------------------------------------------------------------------- |
| `roktux`    | Published rendering library. [Testing notes](roktux/src/test/README.md)                       |
| `demoapp`   | Sample app for validating changes, rendering bundled responses. [Demo app](demoapp/README.md) |
| `testutils` | Internal test helpers. Not a partner dependency.                                              |

## License

Licensed under the [Rokt SDK Terms of Use 2.0](https://rokt.com/sdk-license-2-0/). See [LICENSE.md](LICENSE.md).
