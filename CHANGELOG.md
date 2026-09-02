<!-- markdownlint-disable MD024 -->

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- Update Jetpack Compose BOM to 2026.05.01.

## [2.0.1] - 2026-09-02

### Added

- Surface sessionId and separate no-offers from render f… ([#315](https://github.com/ROKT/rokt-ux-helper-android/pull/315))

### Fixed

- Stop animating sheet height on offer change ([#322](https://github.com/ROKT/rokt-ux-helper-android/pull/322))

### Changed

- Scope Trunk Check to the PR diff instead of the whole repo ([#320](https://github.com/ROKT/rokt-ux-helper-android/pull/320))
- Correct README against the library and drop resident experts ([#321](https://github.com/ROKT/rokt-ux-helper-android/pull/321))
- Upgrade trunk ([#319](https://github.com/ROKT/rokt-ux-helper-android/pull/319))
- Bump trunk-io/trunk-action from 1.3.1 to 2.0.0 ([#317](https://github.com/ROKT/rokt-ux-helper-android/pull/317))
- Trim AGENTS.md to the non-derivable core ([#318](https://github.com/ROKT/rokt-ux-helper-android/pull/318))

## [2.0.0] - 2026-08-11

### Breaking Changes

- Make SelectResponse the canonical experience response ([#313](https://github.com/ROKT/rokt-ux-helper-android/pull/313))

### Added

- Include click destination URL metadata ([#308](https://github.com/ROKT/rokt-ux-helper-android/pull/308))
- Populate interactionType on user_interaction objectData ([#304](https://github.com/ROKT/rokt-ux-helper-android/pull/304))

### Fixed

- Drop fat eventData from legacy InstantPurchase initiated ([#312](https://github.com/ROKT/rokt-ux-helper-android/pull/312))
- Include pageInstanceGuid on ForwardPayment initiated ([#311](https://github.com/ROKT/rokt-ux-helper-android/pull/311))
- Include pageInstanceGuid on user interaction signals ([#310](https://github.com/ROKT/rokt-ux-helper-android/pull/310))

### Changed

- Bump gradle/actions/setup-gradle from 6.2.0 to 6.3.0 ([#314](https://github.com/ROKT/rokt-ux-helper-android/pull/314))
- Upgrade trunk ([#309](https://github.com/ROKT/rokt-ux-helper-android/pull/309))
- Upgrade trunk ([#269](https://github.com/ROKT/rokt-ux-helper-android/pull/269))
- Skip Dependabot-incompatible steps on Dependabot PRs ([#307](https://github.com/ROKT/rokt-ux-helper-android/pull/307))
- Cache Gradle User Home in the snapshot-test job ([#306](https://github.com/ROKT/rokt-ux-helper-android/pull/306))
- Bump actions/checkout from 6.0.3 to 7.0.1 ([#303](https://github.com/ROKT/rokt-ux-helper-android/pull/303))
- Update Roborazzi screenshot testing ([#305](https://github.com/ROKT/rokt-ux-helper-android/pull/305))

## [1.0.0] - 2026-07-20

### Changed

- Support maintenance branch patch releases ([#292](https://github.com/ROKT/rokt-ux-helper-android/pull/292))
- Bump gradle/actions from 6.1.0 to 6.2.0 ([#286](https://github.com/ROKT/rokt-ux-helper-android/pull/286))
- Bump codecov/codecov-action from 6.0.1 to 7.0.0 ([#278](https://github.com/ROKT/rokt-ux-helper-android/pull/278))
- Bump actions/checkout from 6.0.2 to 6.0.3 ([#270](https://github.com/ROKT/rokt-ux-helper-android/pull/270))
- Add public-repo confidentiality guidance ([#268](https://github.com/ROKT/rokt-ux-helper-android/pull/268))
- Bump actions/create-github-app-token from 3.1.1 to 3.2.0 ([#266](https://github.com/ROKT/rokt-ux-helper-android/pull/266))
- Add AGENTS.md and clarify CHANGELOG is auto-generated ([#255](https://github.com/ROKT/rokt-ux-helper-android/pull/255))
- Stabilize Roborazzi host rendering ([#263](https://github.com/ROKT/rokt-ux-helper-android/pull/263))
- Add embedded and bottom sheet coverage ([#261](https://github.com/ROKT/rokt-ux-helper-android/pull/261))
- Snapshot coverage for custom font registration ([#260](https://github.com/ROKT/rokt-ux-helper-android/pull/260))
- Increase snapshot testing of basic components ([#258](https://github.com/ROKT/rokt-ux-helper-android/pull/258))
- Skip source-dependent jobs when only non-source files change ([#259](https://github.com/ROKT/rokt-ux-helper-android/pull/259))
- Support pre-release qualifiers and non-main branch dispatch ([#256](https://github.com/ROKT/rokt-ux-helper-android/pull/256))
- Bump trunk-io/trunk-action from 1.2.4 to 1.3.1 ([#246](https://github.com/ROKT/rokt-ux-helper-android/pull/246))
- Bump codecov/codecov-action from 6.0.0 to 6.0.1 ([#247](https://github.com/ROKT/rokt-ux-helper-android/pull/247))

## [1.0.0-rc.1] - 2026-05-21

### Changed

- Upgrade Kotlin to 2.1.20 and Compose BOM to 2026.05.01 ([#253](https://github.com/ROKT/rokt-ux-helper-android/pull/253))
- Support pre-release qualifiers for workstation releases ([#257](https://github.com/ROKT/rokt-ux-helper-android/pull/257))
- Bump trunk-io/trunk-action from 1.2.4 to 1.3.1 ([#246](https://github.com/ROKT/rokt-ux-helper-android/pull/246))
- Bump codecov/codecov-action from 6.0.0 to 6.0.1 ([#247](https://github.com/ROKT/rokt-ux-helper-android/pull/247))

## [0.9.3] - 2026-05-13

### Fixed

- Handle bottom sheet properties constructor changes ([#241](https://github.com/ROKT/rokt-ux-helper-android/pull/241))
- Harden GitHub Actions workflows and add zizmor CI check ([#233](https://github.com/ROKT/rokt-ux-helper-android/pull/233))
- Pin reusable workflow references to commit SHAs ([#231](https://github.com/ROKT/rokt-ux-helper-android/pull/231))

### Changed

- Bump peter-evans/create-pull-request from 8.1.0 to 8.1.1 ([#240](https://github.com/ROKT/rokt-ux-helper-android/pull/240))
- Bump tj-actions/changed-files from 47.0.5 to 47.0.6 ([#237](https://github.com/ROKT/rokt-ux-helper-android/pull/237))
- Bump codecov/codecov-action from 5.5.3 to 6.0.0 ([#236](https://github.com/ROKT/rokt-ux-helper-android/pull/236))
- Upgrade trunk ([#232](https://github.com/ROKT/rokt-ux-helper-android/pull/232))
- Bump actions/create-github-app-token from 3.0.0 to 3.1.1 ([#235](https://github.com/ROKT/rokt-ux-helper-android/pull/235))
- Bump actions/upload-artifact from 7.0.0 to 7.0.1 ([#234](https://github.com/ROKT/rokt-ux-helper-android/pull/234))
- Bump codecov/codecov-action from 5.5.2 to 5.5.3 ([#230](https://github.com/ROKT/rokt-ux-helper-android/pull/230))
- Bump gradle/actions from 5.0.2 to 6.0.0 ([#229](https://github.com/ROKT/rokt-ux-helper-android/pull/229))
- Bump ncipollo/release-action from 1.20.0 to 1.21.0 ([#228](https://github.com/ROKT/rokt-ux-helper-android/pull/228))
- Bump actions/create-github-app-token from 2.2.1 to 3.0.0 ([#227](https://github.com/ROKT/rokt-ux-helper-android/pull/227))
- Centralise version/signing and add POM validation ([#222](https://github.com/ROKT/rokt-ux-helper-android/pull/222))

## [0.9.2] - 2026-03-10

### Fixed

- App crash when compose version material3 1.4.0 is used ([#225](https://github.com/ROKT/rokt-ux-helper-android/pull/225))

### Changed

- Bump ffurrer2/extract-release-notes from 3.0.0 to 3.1.0 ([#223](https://github.com/ROKT/rokt-ux-helper-android/pull/223))
- Bump gradle/actions from 5.0.1 to 5.0.2 ([#224](https://github.com/ROKT/rokt-ux-helper-android/pull/224))

## [0.9.1] - 2026-03-06

### Fixed

- Image width and scale behaviour in DataImageCarouselComponent ([#219](https://github.com/ROKT/rokt-ux-helper-android/pull/219))

### Changed

- Align draft release changelog generation ([#220](https://github.com/ROKT/rokt-ux-helper-android/pull/220))
- Remove update-android-sdk PR creation on release ([#218](https://github.com/ROKT/rokt-ux-helper-android/pull/218))
- Bump tj-actions/changed-files from 47.0.2 to 47.0.4 ([#213](https://github.com/ROKT/rokt-ux-helper-android/pull/213))
- Upgrade trunk to 1.25.0 ([#215](https://github.com/ROKT/rokt-ux-helper-android/pull/215))
- Workflow naming alignment ([#214](https://github.com/ROKT/rokt-ux-helper-android/pull/214))
- Bump actions/upload-artifact from 6.0.0 to 7.0.0 ([#216](https://github.com/ROKT/rokt-ux-helper-android/pull/216))
- Add workflow to update trunk and configure Gradle build environment properly ([#211](https://github.com/ROKT/rokt-ux-helper-android/pull/211))

## [0.9.0] - 2026-02-12

### Added

- Add configurable console logging with `RoktUx.setLogLevel()`

## [0.8.4] - 2026-01-27

### Changed

- Consolidated `modelmapper` and `core` modules into `roktux`

## [0.8.3] - 2026-01-12

### Fixed

- DataImageCarousel scaling issue for smaller images.

## [0.8.2] - 2025-12-18

### Fixed

- Fixed ViewModelStoreOwner crash when RoktLayout is used in RecyclerView.

## [0.8.1] - 2025-12-01

### Changed

- Replaced desugaring configuration with native Kotlin APIs.

## [0.8.0] - 2025-11-20

### Added

- Updated to the DCUI schema version `2.3.0`.
- Added support for the new transition node as per updated DCUI schema.

### Fixed

- Updated image scaling logic match iOS and Web behaviour.
- Fix Hero image DataImage component dimension

## [0.7.1] - 2025-09-16

### Fixed

- Fixed CarouselDistribution pages filling screen when using fit-height
- Fix `RoktLayout` recomposition when ViewModel is changed

## [0.7.0] - 2025-08-18

### Added

- Hide the Creative Response Component when the actionType is external
- Fallback `imageKey` support in `DataImage` and `DataImageCarousel` nodes

### Fixed

- Remove incorrectly applied vertical padding on the CarouselDistribution
- Fixed CarouselDistribution pages filling screen when using fit-height

## [0.6.0] - 2025-05-28

### Added

- Enhanced offer viewed signals
- Support new response action type `ExternalPaymentTrigger`

### Fixed

- Fixed html links not opening when textTransformation is set to upper case
- Component stretch behaviour not being applied

## [0.5.0] - 2025-04-02

### Added

- Support for the `DataImageCarousel` node
- `Passthrough` support in `LinkOpenTarget`
- `layoutId` to the `OpenUrl` event
- Support for the `CatalogStackedCollection` and `CatalogResponseButton` nodes

### Fixed

- Fix border radius clipping issue
- Image component accessibility issue when alt value is empty
- Image component not showing when device is changed from dark mode to light mode

## [0.4.0] - 2025-02-27

### Added

- Edge to Edge Display Support configuration

### Fixed

- BottomSheet border radius value is not applied correctly
- Button pressed state not being applied
- Fix text color in dark mode for `BasicText` `Icons` and `ProgressIndicator` nodes

## [0.3.0] - 2025-02-05

### Added

- View state caching

### Changed

- Package name of test utils changed from com.core.testutils to com.rokt.core.testutils

### Fixed

- Handle dismissed state when loaded from cache
- Font diagnostics not being sent
- Conflict with resource names. Added resource prefix
- Fix signal issues
- Bottom sheet rounded corner styling
- Exit animation not firing prior to closure-related UX events

## [0.2.0] - 2024-12-17

### Added

- Accessibility readouts

### Fixed

- Events not being sent after job is cancelled
- Fixed negative action not proceeding to next offer

## [0.1.0] - 2024-10-30

### Added

- Initial implementation of UX Helper

[unreleased]: https://github.com/ROKT/rokt-ux-helper-android/compare/2.0.1...HEAD
[2.0.1]: https://github.com/ROKT/rokt-ux-helper-android/compare/2.0.0...2.0.1
[2.0.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/1.0.0...2.0.0
[1.0.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.9.3...1.0.0
[1.0.0-rc.1]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.9.3...1.0.0-rc.1
[0.9.3]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.9.2...0.9.3
[0.9.2]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.9.1...0.9.2
[0.9.1]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.9.0...0.9.1
[0.9.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.8.4...0.9.0
[0.8.4]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.8.3...0.8.4
[0.8.3]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.8.2...0.8.3
[0.8.2]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.8.1...0.8.2
[0.8.1]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.8.0...0.8.1
[0.8.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.7.1...0.8.0
[0.7.1]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.7.0...0.7.1
[0.7.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.6.0...0.7.0
[0.6.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.5.0...0.6.0
[0.5.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.4.0...0.5.0
[0.4.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.3.0...0.4.0
[0.3.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/f3489d36b16268fe284acf868f3c147b96c0adb7...0.3.0
