# Agent Guide

Instructions for AI coding agents (Claude Code, Codex, etc.) working in this repository. Human contributors should still read [README.md](./README.md) and [RELEASING.md](./RELEASING.md).

## Project overview

`roktux` is an Android library that renders Rokt experiences inside partner apps using Jetpack Compose. It is published to [Maven Central](https://central.sonatype.com/artifact/com.rokt/roktux). The library targets **Android 5.0 (API 21)+**, AGP 8.1.2, Gradle 8.9+, JDK 17. See [README.md](./README.md) for installation and the Jetpack Compose compatibility matrix.

## Repository layout

- `roktux/` — main library module (the artifact published to Maven Central).
- `networkhelper/` — supporting module.
- `testutils/` — shared test utilities.
- `demoapp/` — demo app used to validate changes locally (see `demoapp/README.md`).
- `build-logic/` — convention plugins for Gradle builds.
- `.github/workflows/` — CI (pull request checks) and release automation.
- `tools/` — local tooling and scripts.

## Working on a feature branch

1. **Branch naming and commits** — use conventional commit style for both branch names and PR titles (e.g. `feat(catalog-dropdown): use icon font markers`, `fix(bottom-sheet): preserve compose 1.4.0 compatibility`). The release-draft workflow parses PR titles to generate the changelog, so accurate type/scope matters.
2. **Local verification** — before opening a PR, run:
    - `./gradlew build`
    - `./gradlew test`
    - `./gradlew lint`
      The same checks run in CI (`.github/workflows/pull-request.yml`) and must pass before merge.
3. **Demo app** — use the demo app (`demoapp/`) to validate UI changes against real layouts. See `demoapp/README.md`.
4. **Public API changes** — update [MIGRATING.md](./MIGRATING.md) when you rename or remove public symbols.
5. **PR template** — fill in the sections in `.github/pull_request_template.md` (background, what changed, screenshots if UI, checklist).

## CHANGELOG.md is auto-generated — do not edit it

`CHANGELOG.md` is produced automatically by the [`Release – Draft`](https://github.com/ROKT/rokt-ux-helper-android/actions/workflows/release-draft.yml) GitHub Actions workflow, which calls `ROKT/rokt-workflows/actions/generate-changelog` and builds entries from the git history (conventional commit PR titles).

**Do not modify `CHANGELOG.md` on feature branches.** Any manual edits will be overwritten when the next release PR is drafted. If you want your change to appear in the release notes:

- Write a clear, conventional commit-style **PR title** (e.g. `feat(catalog-dropdown): use icon font markers`).
- That title becomes the changelog entry at release time — no further action needed.

See [RELEASING.md](./RELEASING.md) for the full release flow.

## Files you generally should not touch

- `CHANGELOG.md` — auto-generated (see above).
- `VERSION` — bumped by the `Release – Draft` workflow.
- `local.properties` — local-only Gradle config; never commit.

## Verification before opening a PR

- `./gradlew build` succeeds.
- `./gradlew test` passes.
- `./gradlew lint` is clean.
- Confirm the demo app in `demoapp/` still builds and your change renders as expected.

## Releases

Releases are driven by the `Release – Draft` → merge → `Release – Publish` workflow chain, plus snapshot builds on every commit to `main`. Agents should not create release commits, edit `CHANGELOG.md`, or bump `VERSION` directly. See [RELEASING.md](./RELEASING.md).
