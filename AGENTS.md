# Agent Guide

Instructions for AI coding agents working in this repository. Versions, SDK levels and the module inventory are deliberately **not** repeated here — read the Gradle files, which cannot go stale. See also [README.md](./README.md), [RELEASING.md](./RELEASING.md) and [roktux/src/test/README.md](./roktux/src/test/README.md).

`roktux` is an Android library that renders Rokt experiences inside partner apps using Jetpack Compose, published to Maven Central.

## Confidentiality — this is a public repository

This repository is public, and everything committed to it is permanent and world-readable: code, comments, commit messages, branch names, PR titles/descriptions (PR titles also become public release-note entries), and test names. Never include internal-only information:

- Partner, client, customer, or advertiser names or identifiers — or any detail that could identify one (account/tenant/campaign IDs, deal terms, integration specifics). Keep examples generic and anonymized.
- Internal service/system names, internal contract/class names, or their field layouts.
- Backend or infrastructure implementation details: serializer libraries and versions, server-side validation/deserialization behavior, datastore/infra specifics, or anything describing how a payload is checked server-side.
- Links to private repos, internal tickets/PRs, or internal dashboards.

Describe client-side behavior only — what the SDK sends and receives and why, in partner-facing terms. When a change is driven by a server contract, refer to it generically (e.g. "to match the server contract") without naming internal types, versions, or server behavior. Keep internal rationale in non-public channels.

## Where the build is actually configured

The module `build.gradle.kts` files are thin — most Android configuration lives in convention plugins under `build-logic/convention/`, so grepping a module for `targetSdk` finds nothing:

- `build-logic/convention/src/main/kotlin/com/rokt/roktux/KotlinAndroid.kt` sets `compileSdk`, `minSdk`, the Java source/target level, the Kotlin `freeCompilerArgs` opt-ins and the Lint baseline wiring.
- `build-logic/convention/src/main/kotlin/AndroidLibraryConventionPlugin.kt` sets `targetSdk`, `resourcePrefix`, version name and code, and adds an implicit `testImplementation` dependency on `:testutils` to every Android library module.
- Versions are pinned in `gradle/libs.versions.toml`, Gradle in `gradle/wrapper/gradle-wrapper.properties`, the CI JDK in `.github/actions/setup-java/action.yml`.

## Commands

- Full build: `./gradlew build`
- Unit tests (Robolectric, on the JVM): `./gradlew test`
- Android Lint: `./gradlew lint`
- Kotlin formatting and the non-Gradle linters: `trunk check` — see traps 4 and 5
- Snapshot image verification: follow [roktux/src/test/README.md](./roktux/src/test/README.md). It is **not** part of `./gradlew test` — see trap 1.

### Verification traps

1. **`./gradlew test` runs the snapshot tests but compares no images.** The `@Category(SnapshotTest::class)` classes under `roktux/src/test/java/com/rokt/roktux/snapshot/` execute and pass under a plain `./gradlew test`, but Roborazzi stays inert: `:roktux:finalizeTestRoborazziDebug` is SKIPPED and no image is written or compared. A pixel regression is invisible to `./gradlew test`. Only a Roborazzi task compares — the `snapshot-test` CI job runs `verifyRoborazziRelease -PenableSnapshotTests`.
2. **Golden images are read from `roktux/build/outputs/roborazzi/`, not from where they are committed.** CI first moves `roktux/src/test/snapshots/images/*` into that build directory — the "Move snapshots" step in `.github/workflows/pull-request.yml`. Verifying locally without doing the same move leaves nothing to compare against. That move is `|| true` guarded, so it cannot fail the job.
3. **A green `./gradlew test` covers less than it looks.** `networkhelper` and `demoapp` have no test sources, so their test tasks report `NO-SOURCE` and pass; only `roktux` and `testutils` produce results. And there are no instrumented tests at all — several modules declare a `testInstrumentationRunner` and `roktux` declares `androidTestImplementation` dependencies, but no `src/androidTest` source set exists anywhere and no CI job starts an emulator. Every test is a Robolectric JVM test, so "tests pass" never means device-verified.
4. **ktlint is not a Gradle task.** Kotlin formatting is enforced only by Trunk (`.trunk/trunk.yaml`); `./gradlew lint` is Android Lint and will never flag a formatting violation. Run `trunk check` before pushing. CI runs it with `check-mode: all`, so it inspects every file in the repository, not only your diff.
5. **Android Lint runs against a baseline.** Every module has a `lint-baseline.xml`, and `roktux`'s is not empty, so a clean `./gradlew lint` does not mean the module is lint-clean. Deleting or regenerating a baseline changes what the task reports.
6. **The build cache and the configuration cache are both enabled** (`gradle.properties`). A green run can be a replayed one. When a result looks impossible, re-run with `--rerun-tasks` and/or `--no-configuration-cache` before believing it.

## What CI enforces, and what it does not

`.github/workflows/pull-request.yml` is the only PR workflow that gates merges; `trunk-check`, `lint`, `unit-test`, and `assemble-debug` are the required status checks on `main`. Beyond those:

1. **`snapshot-test` is not a required check.** A snapshot regression shows a red job and still merges. Read that job's result yourself.
2. **A PR touching no file in the `detect-source-changes` allow-list compiles nothing.** `lint`, `unit-test`, `assemble-debug`, and `snapshot-test` are all conditional on that job, and a skipped check satisfies a required check — so such a PR reports success having run only `trunk-check`. The allow-list is source and config extensions plus two test-data paths; every `.md` file, and every workflow other than `pull-request.yml`, falls outside it. If you change behavior, make sure something in the allow-list is in the diff.
3. **Coverage is reported, not enforced.** `koverXmlReport` uploads to Codecov; no `koverVerify` task or threshold rule exists.

## Pull requests

- Base branch is `main`. Branch names and PR titles both use Conventional Commits (`feat(catalog-dropdown): …`, `fix(bottom-sheet): …`). The `Release – Draft` workflow parses the PR title to build the changelog, so the type and scope matter.
- One approving review from `@ROKT/sdk-engineering` is required — CODEOWNERS covers the whole repository — and pushing to the branch dismisses existing approvals, so get the branch finished before requesting review. The branch does **not** have to be up to date with `main` to merge.
- Update [MIGRATING.md](./MIGRATING.md) when you rename or remove a public symbol. Nothing enforces this: the build has no API dump and no binary-compatibility check.
- Validate UI changes in the demo app — see [demoapp/README.md](./demoapp/README.md).

## Files not to edit

- `CHANGELOG.md` — generated by the `Release – Draft` workflow from Conventional Commit PR titles. Manual edits on a feature branch are overwritten at release time. Write a good PR title instead; that becomes the changelog entry.
- `VERSION` — bumped by the same workflow. Do not hand-write release commits; see [RELEASING.md](./RELEASING.md).
- `lint-baseline.xml` and `roktux/src/test/snapshots/images/**` — regenerate these through their documented flows, never by hand-editing.
- `local.properties` — gitignored, holds demo-app credentials, never commit it. Note that the root `build.gradle.kts` supplies a placeholder default for every value it reads, so a missing or incomplete `local.properties` yields a demo app that builds happily and points at a fake base URL instead of failing.
