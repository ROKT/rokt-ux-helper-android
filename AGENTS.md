# Rokt UX Helper Android

## Project Overview

The Rokt UX Helper for Android (`roktux`) is an open-source library that enables partner applications
to render tailored user experiences using Rokt's DCUI (Dynamic Component UI) schema. It provides
Jetpack Compose components for rendering layouts, handling user interactions (offer progression,
URL opening, dismissals), and reporting platform/UX events back to the host application. The library
is published to Maven Central as `com.rokt:roktux`.

**Owner:** sdk-engineering team (`@ROKT/sdk-engineering`)
**Cortex tag:** `android-ux-helper` (Service Tier 3)
**On-call:** OpsGenie schedule `Mobile Integrations_schedule`
**Current version:** 0.9.0

## Architecture

The project is a multi-module Gradle build with these modules:

```text
UxHelper (root)
├── roktux          — Core library (published to Maven Central as com.rokt:roktux)
│   ├── component/  — Compose UI components for DCUI layout rendering
│   ├── viewmodel/  — ViewModels (LayoutViewModel, DIComponentViewModel)
│   ├── event/      — UX and platform event handling
│   ├── modelmapper/ — Maps DCUI schema to UI models
│   ├── di/         — Dependency injection (manual, composable-scoped)
│   ├── imagehandler/ — Image loading strategies (network, custom)
│   └── logging/    — Configurable console logging
├── networkhelper   — Retrofit-based helper for calling the Rokt backend (not published)
├── testutils       — Shared test utilities (published as com.rokt.core:testutils)
├── demoapp         — Demo application showcasing integration
├── core            — Empty module (previously contained shared code, consolidated into roktux)
├── build-logic/    — Convention plugins for consistent build config
└── tools/          — Python/shell scripts for DCUI JSON manipulation
```

**Key data flow:**

1. Host app calls the Rokt backend (optionally via `networkhelper`) to get an experience response JSON.
2. Host app passes the JSON and a location string to the `RoktLayout` composable.
3. `roktux` parses the DCUI schema, maps it to UI models, and renders Compose components.
4. User interactions trigger events reported back via `onUxEvent` and `onPlatformEvent` callbacks.

## Tech Stack

| Component             | Version / Details                                                                                                |
| --------------------- | ---------------------------------------------------------------------------------------------------------------- |
| Language              | Kotlin 1.8.21                                                                                                    |
| JVM Target            | 17 (Temurin)                                                                                                     |
| Android Gradle Plugin | 8.6.1                                                                                                            |
| Compile SDK           | 35                                                                                                               |
| Min SDK               | 21 (Android 5.0)                                                                                                 |
| Jetpack Compose BOM   | 2024.09.02                                                                                                       |
| DCUI Schema           | 2.3.0                                                                                                            |
| Networking            | Retrofit 2.11.0, OkHttp 4.10.0                                                                                   |
| Serialization         | kotlinx-serialization-json 1.6.3                                                                                 |
| Image Loading         | Coil 2.3.0                                                                                                       |
| DI (demo app)         | Hilt 2.51.1                                                                                                      |
| Testing               | JUnit 4, MockK 1.13.16, Robolectric 4.14.1, Roborazzi 1.43.0                                                     |
| Code Coverage         | Kover 0.9.1, Codecov                                                                                             |
| Documentation         | Dokka 2.0.0                                                                                                      |
| Publishing            | vanniktech maven-publish 0.31.0 (Maven Central)                                                                  |
| Linting               | Trunk (ktlint 1.5.0, markdownlint, prettier, shellcheck, actionlint, yamllint, checkov, trufflehog, osv-scanner) |

## Development Guide

### Prerequisites

- Android Studio (latest stable)
- JDK 17
- Gradle 8.9+
- Android Gradle Plugin 8.1.2+

### Quick Start

1. Clone the repo and open in Android Studio.
2. Create `local.properties` in the root with demo app config (see [demoapp/README.md](demoapp/README.md)):

    ```properties
    BASE_URL=<your_base_url>
    VIEW_NAME=<your_view_name>
    ROKT_TAG_ID=<your_tag_id>
    ROKT_PUB_ID=<your_pub_id>
    ROKT_SECRET=<your_secret>
    ROKT_CLIENT_UNIQUE_ID=<your_client_unique_id>
    ```

3. Sync Gradle and run the `demoapp` configuration.

### Common Tasks

- **Build:** `./gradlew build`
- **Run unit tests:** `./gradlew test`
- **Run lint:** `./gradlew lint`
- **Run all checks (trunk):** `trunk check`
- **Format code (trunk):** `trunk fmt`
- **Publish locally:** `./gradlew publishMavenPublicationToMavenLocal -PVERSION=x.y.z`
  (Comment out `signAllPublications()` in `MavenCentralPublish.kt` if you don't want signing)

## Build, Test & Lint Commands

| Command                                                                                              | Description                        |
| ---------------------------------------------------------------------------------------------------- | ---------------------------------- |
| `./gradlew build`                                                                                    | Full build (compile + test + lint) |
| `./gradlew test`                                                                                     | Run unit tests                     |
| `./gradlew lint`                                                                                     | Run Android lint                   |
| `./gradlew koverXmlReport`                                                                           | Generate code coverage XML report  |
| `./gradlew assembleDebug`                                                                            | Build debug APK and AAR artifacts  |
| `./gradlew verifyRoborazziRelease -PenableSnapshotTests --tests "com.rokt.roktux.snapshot.*"`        | Run snapshot tests                 |
| `./gradlew publishMavenPublicationToMavenLocal -PVERSION=x.y.z`                                      | Publish to local Maven             |
| `./gradlew publishMavenPublicationToMavenCentralRepository -PVERSION=x.y.z --no-configuration-cache` | Publish to Maven Central           |
| `trunk check`                                                                                        | Run all Trunk linters              |
| `trunk fmt`                                                                                          | Auto-format with Trunk             |

## CI/CD Pipeline

CI runs via **GitHub Actions** (not Buildkite, despite the Cortex entry referencing a Buildkite slug).

### Pull Request Workflow (`.github/workflows/pull-request.yml`)

Triggered on push to `main`, `workstation**`, `release**`, and all PRs:

| Job              | What it does                                         |
| ---------------- | ---------------------------------------------------- |
| `trunk-check`    | Runs Trunk linters (`check-mode: all`)               |
| `lint`           | `./gradlew lint`                                     |
| `unit-test`      | `./gradlew test` + Kover coverage → Codecov          |
| `assemble-debug` | `./gradlew assembleDebug`, uploads APK/AAR artifacts |
| `snapshot-test`  | Roborazzi snapshot verification                      |
| `pr-notify`      | Google Chat notification for non-draft PRs           |

### Release from Main (`.github/workflows/release-from-main.yml`)

Triggered on push to `main`:

- Publishes a SNAPSHOT version to Maven Central on every commit.
- If the `VERSION` file changed, also publishes a full release, creates a GitHub Release with AAR/POM artifacts, and opens a PR in `ROKT/sdk-android-source` to bump the UX Helper version.

### Draft Release (`.github/workflows/draft-release-publish.yml`)

Manual workflow dispatch that bumps version (major/minor/patch), updates CHANGELOG, builds, and creates a release PR targeting `main`.

### Trunk Upgrade (`.github/workflows/trunk-upgrade.yml`)

Monthly scheduled workflow to auto-upgrade Trunk linter versions.

## Environment Variables

| Variable                                        | Description                        | Used in                              |
| ----------------------------------------------- | ---------------------------------- | ------------------------------------ |
| `BASE_URL`                                      | Rokt backend base URL              | `local.properties` → `networkhelper` |
| `VIEW_NAME`                                     | View name for demo app experiences | `local.properties` → `demoapp`       |
| `ROKT_TAG_ID`                                   | Rokt tag identifier                | `local.properties` → `demoapp`       |
| `ROKT_PUB_ID`                                   | Rokt publisher ID                  | `local.properties` → `networkhelper` |
| `ROKT_SECRET`                                   | Rokt API secret                    | `local.properties` → `networkhelper` |
| `ROKT_CLIENT_UNIQUE_ID`                         | Client unique ID                   | `local.properties` → `networkhelper` |
| `ORG_GRADLE_PROJECT_signingInMemoryKey`         | Maven signing key (CI only)        | Release workflows                    |
| `ORG_GRADLE_PROJECT_signingInMemoryKeyPassword` | Maven signing password (CI only)   | Release workflows                    |
| `ORG_GRADLE_PROJECT_mavenCentralUsername`       | Sonatype username (CI only)        | Release workflows                    |
| `ORG_GRADLE_PROJECT_mavenCentralPassword`       | Sonatype password (CI only)        | Release workflows                    |

## Project Structure

| Path                                         | Description                                                    |
| -------------------------------------------- | -------------------------------------------------------------- |
| `roktux/`                                    | Core library — DCUI rendering, event handling, view models     |
| `roktux/src/main/java/com/rokt/roktux/`      | Public API: `RoktLayout`, `RoktUx`, `RoktUxConfig`             |
| `roktux/src/main/java/com/rokt/modelmapper/` | DCUI schema → UI model mapping                                 |
| `roktux/src/main/java/com/rokt/core/`        | Composable-scoped ViewModel utilities                          |
| `roktux/src/test/`                           | Unit tests + Roborazzi snapshot tests                          |
| `roktux/src/test/snapshots/`                 | Snapshot baseline images                                       |
| `networkhelper/`                             | Retrofit client for Rokt backend API                           |
| `testutils/`                                 | Shared test utilities (MockK, Robolectric, Compose test)       |
| `demoapp/`                                   | Demo app with tutorials, QR scanner, and custom layout builder |
| `core/`                                      | Empty module (code consolidated into `roktux`)                 |
| `build-logic/convention/`                    | Custom Gradle convention plugins                               |
| `tools/`                                     | Python/shell scripts for DCUI JSON formatting                  |
| `gradle/libs.versions.toml`                  | Version catalog                                                |
| `.github/workflows/`                         | CI/CD pipeline definitions                                     |
| `.cortex/`                                   | Service catalog metadata                                       |
| `.trunk/trunk.yaml`                          | Trunk linter configuration                                     |

## Code Style

- Kotlin code style: IntelliJ IDEA (`ktlint_code_style = intellij_idea`)
- Max line length: 120 (disabled in test files)
- Trailing commas enabled
- Indent: 4 spaces (2 for JSON/YAML/HTML/Terraform)
- Linting enforced via Trunk (ktlint 1.5.0 + compose lint checks)

## Snapshot Testing

Roborazzi is used for snapshot tests. See [roktux/src/test/README.md](roktux/src/test/README.md):

- Annotate snapshot test classes with `@Category(SnapshotTest::class)`.
- Run with: `./gradlew verifyRoborazziRelease -PenableSnapshotTests --tests "com.rokt.roktux.snapshot.*"`
- Baseline images live in `roktux/src/test/snapshots/images/`.
- New/updated baselines must be generated via CI and committed.

## Releasing

See [RELEASING.md](RELEASING.md) for the full release process:

- **Pre-release/SNAPSHOT:** Every commit to `main` publishes a `{next_minor}-SNAPSHOT` to Maven Central.
- **Major/Minor release:** Trigger the "Create draft release from main" workflow, merge the generated PR.
- **Hotfix/Patch:** Cherry-pick fixes to a `release/X.Y.x` branch and merge.

## Maintaining This Document

When making changes to this repository that affect the information documented here
(build commands, dependencies, architecture, deployment configuration, etc.),
please update this document to keep it accurate. This file is the primary reference
for AI coding assistants working in this codebase.
