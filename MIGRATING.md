# Migration guide

## Coil 3 (`io.coil-kt.coil3`)

Rokt UX Helper now depends on **Coil 3** (`io.coil-kt.coil3`) instead of Coil 2 (`io.coil-kt`). Coil 3 uses the `coil3` package, so any app code that referenced Coil types from this library (for example `ImageLoaderStrategy` with a custom `coil.ImageLoader`) should switch imports and types to `coil3` (see the [Coil upgrading guide](https://coil-kt.github.io/coil/upgrading_to_coil3/)). Coil 2 and Coil 3 can coexist on the classpath if you need a gradual migration.

Building this repository from source uses **Kotlin 2.1** (Gradle `kotlin` plugin), which matches Coil 3’s Kotlin metadata and the Jetpack **Compose compiler Gradle plugin** (`org.jetbrains.kotlin.plugin.compose`).

No other migration steps are documented for prior releases.
