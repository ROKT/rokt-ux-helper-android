# roktux Size Report

Measures how much `roktux` adds to an app's size by comparing a bare Compose app
against the same app with the dependency integrated. The number is indicative for
partners deciding to adopt the SDK.

## How it works

1. `measure_size.sh` builds two release apps from this standalone Gradle build:
    - **baseline** — a Compose activity showing a `Text`, with no Rokt dependency.
    - **with-helper** — the same app plus `roktux`, calling `RoktLayout` so R8 keeps
      the rendering pipeline. `roktux` comes from the checkout passed as `--root`
      through a composite build, so local source changes are measured directly.
2. Both apps are minified and resource-shrunk by R8. The script reports the APK size
   delta and the uncompressed DEX delta between them. Because the baseline already
   has Compose, the delta is `roktux` plus the dependencies it adds on top.

## Usage

```bash
./measure_size.sh                          # Human-readable output
./measure_size.sh --json                   # Single-line JSON (used by CI)
./measure_size.sh --json --root ../other   # Measure another checkout's roktux
python3 report.py base.json pr.json        # Render the PR comment
```

## CI integration

`.github/workflows/ci-size-report.yml` runs on pull requests. It measures the PR
branch and the target branch with the PR's tooling, renders the report into the job
summary, and posts (or updates) a sticky comment on the PR. Fork PRs get the job
summary only, since their token cannot comment.
