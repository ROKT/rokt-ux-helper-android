# Testing in roktux

## Snapshot Testing

We use [Roborazzi](https://github.com/takahirom/roborazzi) for snapshot testing.

### Test Annotations

To separate snapshot tests from regular unit tests, you can use the `@Category(SnapshotTest::class)` annotation for each test class. When running a test command, include `-PenableSnapshotTests` to also make the snapshot tests run. If you are not using a Roborazzi task (e.g. running `./gradlew test` instead of `./gradlew verifyRoborazziRelease`) you will also need to include the Roborazzi properties `-Proborazzi.test.record=true`, `-Proborazzi.test.compare=true`, and `-Proborazzi.test.verify=true` depending on which task you want to execute.

### Updating Baselines

To ensure consistency of the environment used for snapshot testing, baseline images should be created via CI.

#### New Tests

When you create a new snapshot test, the test step in CI will initially fail. You can download the image from the uploaded artifact and commit it to the [snapshots directory](./snapshots). When the tests re-run it will now pass.

#### Existing Tests

Follow the same process as with a new test, however ensure that the difference is expected before updating the baseline and committing it. This change should be justified for reviewers of the PR.
