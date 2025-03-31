<!-- markdownlint-disable MD024 -->

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Support for the `DataImageCarousel` node
- `Passthrough` support in `LinkOpenTarget`
- `layoutId` to the `OpenUrl` event
- Support for the `CatalogStackedCollection` and `CatalogResponseButton` nodes

### Fixed

- Fix border radius clipping issue
- Image component accessibility issue when alt value is empty

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

[unreleased]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.4.0...HEAD
[0.4.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/0.3.0...0.4.0
[0.3.0]: https://github.com/ROKT/rokt-ux-helper-android/compare/f3489d36b16268fe284acf868f3c147b96c0adb7...0.3.0
