# Rokt UX Helper Andriod

The Rokt UX Helper for Android enables partner applications to render tailored user experiences, improving the velocity of testing and relevancy for the customer. This library offers an easy way to perform rendering and provides event hooks for integration into backend systems.

## Resident Experts

- Thomson Thomas - <thomson.thomas@rokt.com>
- Sahil Suri - <sahil.suri@rokt.com>
- Lewis Krishnamurti - <lewis.raj.krishnamurti@rokt.com>

| Environment | Build                                                                                                        | Coverage                                                                                                                                            |
| ----------- | ------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| main        | ![Build status](https://github.com/ROKT/rokt-ux-helper-android/actions/workflows/pull-request.yml/badge.svg) | [![codecov](https://codecov.io/gh/ROKT/rokt-ux-helper-android/graph/badge.svg?token=inV3Xb1tK9)](https://codecov.io/gh/ROKT/rokt-ux-helper-android) |

## Requirements

- The latest version of [Android Studio](https://developer.android.com/studio).
- Android 5.0 (API level 21) and above
- Android Gradle Plugin 8.1.2
- Gradle 8.9+
- JDK 17

## Installation

The library is published to [Maven Central](https://central.sonatype.com/artifact/com.rokt/roktux).
Add `roktux` to your app-level `build.gradle.kts` dependencies.

```kotlin
dependencies {
    implementation("com.rokt:roktux:0.1.0")
}
```

## Releases

- You can find a summary of changes in the [Changelog](CHANGELOG.md)
- If there are any migrations between versions you will find instructions in the [migration guide](MIGRATING.md)
- To learn about how to release the UX Helper modules, check out the releasing guide [here](RELEASING.md)

## Jetpack Compose Compatibility

As `roktux` uses Jetpack Compose, consuming projects should use Compose libraries with compatible versions:

| roktux          | Compose BOM |
| --------------- | ----------- |
| 0.1.0 - current | 2024.09.02  |

You can view the BOM to library version mapping [here](https://developer.android.com/develop/ui/compose/bom/bom-mapping).

## Development and useful commands

When making changes to UX Helper you can utilise the [Demo app](demoapp/README.md) to validate your changes.

Before submitting changes ensure that:

- Builds succeed with `./gradlew build`
- Tests pass with `./gradlew test`
- Lint checks pass with `./gradlew lint`

Additional checks are conducted using GitHub Actions which run on all pull requests and are required to pass before the changes are merged. You can find the details of the full pipeline [here](.github/workflows/pull-request.yml).

To publish the UX Helper modules locally for use in other projects, run

- `./gradlew publishMavenPublicationToMavenLocal -PVERSION=x.y.z`

You should comment out the `signAllPublications()` line in `MavenCentralPublish.kt` if you do not wish to sign the modules.

## Modules

| Module | Docs                                 |
| ------ | ------------------------------------ |
| roktux | [Testing](roktux/src/test/README.md) |

## FAQ

### Documentation

For detailed documentation, check the [Android integration guide](https://docs.rokt.com/server-to-server/android/).
