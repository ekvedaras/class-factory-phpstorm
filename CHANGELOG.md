<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# class-factory-phpstorm Changelog

## [Unreleased]

## [1.2.0]
### Added
- Detect missing class constructor property definitions.

## [1.1.4]
### Fixed
- Complete properties in returned array keys of directly passed closure.
- Detect unknown properties in return array keys when closure is passed directly to class factory method instead of
  as an array hash value.
- Resolve references in returned array keys of directly passed closure.

## [1.1.3]
### Fixed
- Do not crash when closure has no parameters (#5)

## [1.1.2]
### Fixed
- Complete attributes array keys when closure is passed directly to class factory method instead of as an array hash
  value.
- Complete attributes array value when closure is passed directly to class factory method instead of as an array hash
  value.
- Resolve references for attributes array keys when closure is passed directly to class factory method instead of as an
  array hash value.
- Detect unknown properties in attributes array keys when closure is passed directly to class factory method instead of
  as an array hash value.

## [1.1.1]
### Fixed
- Potential fix for `Indexing process should not rely on non-indexed file data` (#2)

## [1.1.0]
### Added
- Complete properties for attributes array keys in closure states.
- Resolve references for attributes array keys in closure states.
- Detect unknown properties in state and make methods both inside and outside factory.
- Detect unknown properties in attributes array keys in closure states.
- Resolve attributes array value types from factory definition.

## [1.0.0]
### Added
- Initial release with autocompletion, reference resolving and property not found inspection.
- Initial scaffold created
  from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)

### Fixed
- `NullPointerException` cast (#1)