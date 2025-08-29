<!-- markdownlint-disable MD024 -->

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed

- Fixed CarouselDistribution pages filling screen when using fit-height

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

[unreleased]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.7.0...HEAD
[0.7.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.6.0...0.7.0
[0.6.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.5.0...0.6.0
[0.5.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.4.0...0.5.0
[0.4.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.3.0...0.4.0
[0.3.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/f3489d36b16268fe284acf868f3c147b96c0adb7...0.3.0
